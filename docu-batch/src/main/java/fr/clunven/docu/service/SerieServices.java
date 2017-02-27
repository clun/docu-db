package fr.clunven.docu.service;

import static java.util.Arrays.stream;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.ff4j.FF4j;
import org.ff4j.core.Feature;
import org.ff4j.spring.autowire.FF4JFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.clun.movie.MovieMetadataParser;

import fr.clunven.docu.dao.FileSystemDao;
import fr.clunven.docu.dao.ValidationDao;
import fr.clunven.docu.dao.db.EpisodeDbDao;
import fr.clunven.docu.dao.db.SerieDbDao;
import fr.clunven.docu.dao.db.dto.GenreDto;
import fr.clunven.docu.dao.db.dto.SerieDto;
import fr.clunven.docu.domain.ComparaisonFsDb;
import fr.clunven.docu.domain.Episode;
import fr.clunven.docu.domain.SmartFileName;
import fr.clunven.docu.domain.SmartSerieName;
import fr.clunven.docu.utils.BatchUtils;

@Service("docu.batch.service.serie")
public class SerieServices {
    
    /** logger for this class. */
    private Logger logger = LoggerFactory.getLogger(SerieServices.class);
    
    @Autowired
    private FF4j ff4j;
    
    @FF4JFeature("simulation")
    private Feature simulation;
    
    @Autowired
    private SerieDbDao serieDbDao;
    
    @Autowired
    private EpisodeDbDao episodeDbDao;
    
    @Autowired
    private ValidationDao validationService;
    
    @Autowired
    private GenreServices genreService;
    
    @Autowired
    private FileSystemDao fsDao;
    
    /**
     * Analyse le répertoire
     * @param currentGenre
     * @param currentFolder
     */
    public void analyseRepertoire(GenreDto currentGenre, File currentFolder) {
        
        Set < String > seriesDB = serieDbDao.getSerieNamesByGenre(currentGenre.getId());
        
        Map < String, SmartSerieName > seriesFS = listeSeries(currentFolder);     
        ComparaisonFsDb fsdb1 = validationService.compareFSandDb(seriesFS.keySet(), seriesDB);
        if (!fsdb1.getNotOnFileSystem().isEmpty()) {
            logger.error("Erreur: Documentaires uniquement en BDD " + fsdb1.getNotOnFileSystem());
            System.exit(-9);
        }
        
        // On crée les series qui ne sont pas encore en base
        fsdb1.getNotInDatabase().stream().forEach(s-> createSerie(seriesFS.get(s)));
        
        // On analyse toutes les séries (création et update des episodes)
        seriesFS.values().stream().forEach(ssn -> analyseSerie(ssn));
    }
    
    /**
     * Liste tous les dossiers qui sont des séries. [SERIE]
     * 
     * @param parent
     *      liste des séries
     * @return
     *      liste des sériess
     */
    private Map < String, SmartSerieName > listeSeries(File parent) {
        return stream(parent.listFiles())
                            .filter(isSerieFolder())
                            .map(SmartSerieName::new)
                            .collect(Collectors.toMap(SmartSerieName::getTitre, Function.identity()));
    } 
    
    
    public Predicate< File > isSerieFolder() {
        return f -> f != null && f.isDirectory() && f.getName().startsWith("[SERIE] - ");
    }
    
    /**
     * Permet de créer les series si nécessaires et détecte les séries en BDD et pas sur FS
     *
     * @param rootFolder
     *      repertoire de travail
     */
    public void createSerie(SmartSerieName ssn) {
      if (serieDbDao.exist(ssn.getTitre())) {
          throw new IllegalArgumentException("Une serie existe deja avec le titre " + ssn.getTitre());
      }
      if (!ff4j.check(simulation.getUid())) {
          int genreId = genreService.getGenreIdFromFolderName(ssn.getFolder().getParentFile());
          logger.info("Création de : '" + ssn.getTitre() + "' (" + ssn.getFolder().getName() + ")");
          serieDbDao.createSerie(ssn.getTitre(), genreId, ssn.getAnneeStart(),ssn.getAnneeEnd(), ssn.isVo());
      } else {
          logger.info("[SIMU] - Création de : '" + ssn.getTitre() + "' (" + ssn.getFolder().getName() + ")");
      }
    }
    
    /**
     * Nous sommes sur un répertoire de série.
     *
     * @param folder
     */
    public void analyseSerie(SmartSerieName ssn) {  
        
        logger.info("Serie [" + ssn.getTitre() + "]");
        SerieDto dto = serieDbDao.getSerieById(serieDbDao.getSerieIdByName(ssn.getTitre()));
        
        // Recherche le pattern "NN - TITRE", x dossier ou fichier
        File[] episodes = ssn.getFolder().listFiles(file -> new SmartFileName(file.getName()).startByNumber());
       
        // Ce ne sont PAS des épisodes, alors sans doute des Saisons 
        if (episodes == null || episodes.length == 0) {
            // Liste des saisons du coup
            File[] saisons = ssn.getFolder().listFiles(file -> file.getName().startsWith("Saison"));
            // Ni des episodes, ni des saisons ==> Erreur ICI
            if (saisons == null || saisons.length == 0) {
               logger.error("ERREUR dans le répertoire " + ssn.getFolder().getAbsolutePath() + " no Saison et pas de 01 XXX");
                System.exit(-5);
            }
            // Boucle sur les saisons
            for (File saisonX : saisons) {
                int annee = 1900;
                int nbSaison = 0;
                
                // Recherche du numéro de saison X et de la date avec le pattern Saison X (date)
                String tmps = saisonX.getName().replaceAll("Saison", "");
                if (tmps.lastIndexOf("(") != -1) {
                    // Le format est Saison AA (YYYY)
                    nbSaison = Integer.parseInt(tmps.substring(0, tmps.lastIndexOf("(")).trim());
                    annee = Integer.parseInt(tmps.substring(tmps.lastIndexOf("(") + 1, tmps.length() - 1));
                    logger.debug("Titre=" + dto.getTitre() + 
                            "(" + dto.getId() + ") Saison=" + nbSaison + " annee=" + annee);
                } else {
                    // Le format est Saison AA, pas de date
                    nbSaison = Integer.parseInt(tmps.trim());
                    // Logger pour traitements
                    logger.warn("Cannot find annee of saison " + saisonX.getAbsolutePath());
                }

                // On donne un répertoire de Saison contenant forcément des épisodes
                analyseSaison(saisonX, dto.getId(), nbSaison, annee);
            }
         
        } else {
            // L'annee est portée par le répertoire parent (ou chaque episode)
            String annee = ssn.getFolder().getName().substring(
                    ssn.getFolder().getName().lastIndexOf("(")+1, ssn.getFolder().getName().length() - 1);
          
            // Si pas d'annee trouve on met 1900 comme valeur par defaut
            // Dans le répertoire de la serie on a trouve des épisodes (pas de saison)
            analyseSaison(ssn.getFolder(), 
                   dto.getId(), 1, BatchUtils.isNumeric(annee) ? Integer.parseInt(annee) : 1900);
        }
    }
    
    /**
     * Analayse d'une SAISON avec des episodes attendus
     * @param folderSaison
     *      le répertoire de la saison (peut etre la serie si unique)
     * @param serie
     *      l'identifiant de la série
     * @param nbSaison
     *      numero de la saison
     * @param annee
     *      annee pour cette saison
     */
    private void analyseSaison(File folderSaison, int serie, int nbSaison, int annee) {
        SerieDto dto = serieDbDao.getSerieById(serie);
        
        // Recherche le pattern "NN - EPISODE NAME"
        File[] episodes = folderSaison.listFiles(file -> new SmartFileName(file.getName()).startByNumber());
        
        // On s'attend forcément a avoir des episodes ici alors erreur au besoin
        if (episodes == null || episodes.length == 0) {
            logger.error("ERREUR dans le répertoire " + folderSaison.getAbsolutePath() + " Pas episodes");
            System.exit(-4);
        }
        
        // On vérifie si l'on travaille avec des répertoires
        if (!episodes[0].isDirectory()) {
            
            // On liste les fichiers qui ne sont pas des sous titres
            for (File episode : folderSaison.listFiles(f -> f.isFile() && 
                    !f.getName().endsWith("srt") && 
                    !f.getName().endsWith("txt"))) {
                    
              // Lecture du fichier VIDEO pour initialiser un episode
              Episode ep = new Episode(MovieMetadataParser.getInstance().parseFile(episode.getAbsolutePath()));
                    
              // Information complémentaires sur le fichier
              SmartFileName smf = new SmartFileName(episode.getName());
              ep.setEpisode(smf.getNumber());
              ep.setTitre(smf.getTitre());
              ep.setVo(smf.isVo());
              ep.setExtension(smf.getExtension());
              ep.setAnnee(smf.containsAnnee() ? smf.getAnnee() : annee);
              ep.setSaison(nbSaison);
              ep.setSerie(serie);
              analyseEpisode(ep, dto);
            }
        } else {
           
            // liste des répertoires respectant le format "NN - Titre"
            for (File episode : folderSaison.listFiles(f -> f.isDirectory() && 
                    new SmartFileName(f.getName()).startByNumber())) {
                
                SmartFileName smfr =  new SmartFileName(episode.getName());
                
                // Initialisation de l'enregistrement
                Episode ep = new Episode();
                ep.setEpisode(smfr.getNumber());
                ep.setTitre(smfr.getTitre());
                ep.setVo(smfr.isVo());
                ep.setAnnee(smfr.containsAnnee() ? smfr.getAnnee() : annee);
                ep.setSaison(nbSaison);
                ep.setSerie(serie);
                stream(episode.listFiles())
                              .filter(fsDao.isMovieFilesValid())
                              .map(f-> MovieMetadataParser.getInstance().parseFile(f.getAbsolutePath()))
                              .forEach(ep::updateMovieMetaData);
                analyseEpisode(ep, dto);
            }
        }
    }
    
    private void analyseEpisode(Episode ep, SerieDto serie) {
        logger.info(ep.getSaison() + "x" + ep.getEpisode() + " - " + ep.getTitre());
        // Episode existe Déjà
        if (episodeDbDao.existID(ep.getSerie(), ep.getSaison(), ep.getEpisode())) {
            // Comparaison des titres
            Episode old = episodeDbDao.getEpisodeById(ep.getSerie(), ep.getSaison(), ep.getEpisode());
            if (!old.getTitre().equalsIgnoreCase(ep.getTitre())) {
                logger.error("Titre episode désynchronisés ");
                logger.error("serie=" + serie.getTitre());
                logger.error("db=" + old.getTitre());
                logger.error("fs=" + ep.getTitre());
                logger.error("SELECT * FROM t_episode WHERE (SERIE = " + ep.getSerie() + ") and "
                        + "(SAISON = "+ep.getSaison()+") AND "
                        + "(EPISODE = "+ep.getEpisode()+")");
                System.exit(-7);
            // Les épisodes ont le même nom
            } else if (old.getFormat() == null || old.getFormat().isEmpty()) {
                logger.info("-> update :" + ep.toString());;
                if (!ff4j.check(simulation.getUid())) {
                    episodeDbDao.updateEpisodeFileInformation(ep);
                }
            }
        } else {
            logger.info("-> Create " + ep);
            if (!ff4j.check(simulation.getUid())) {
                episodeDbDao.createEpisode(ep, serie.getImage());
            }
        }
    }

}
