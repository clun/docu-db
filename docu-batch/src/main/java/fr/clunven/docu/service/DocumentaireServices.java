package fr.clunven.docu.service;

import static java.util.Arrays.stream;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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
import fr.clunven.docu.dao.db.DocumentaryDbDao;
import fr.clunven.docu.dao.db.dto.DocumentaireListElementDto;
import fr.clunven.docu.dao.db.dto.GenreDto;
import fr.clunven.docu.domain.ComparaisonFsDb;
import fr.clunven.docu.domain.Documentaire;
import fr.clunven.docu.domain.SmartFileName;


@Service("docu.batch.service.docus")
public class DocumentaireServices {
    
    /** logger for this class. */
    private Logger logger = LoggerFactory.getLogger(DocumentaireServices.class);
    
    @Autowired
    private FF4j ff4j;
    
    @Autowired
    private ValidationDao validationService;
    
    @Autowired
    private DocumentaryDbDao docuDbDao;
    
    @Autowired
    private FileSystemDao fileSystemDao;
    
    @Autowired
    private GenreServices genreService;
    
    @FF4JFeature("simulation")
    private Feature simulation;
    
    @FF4JFeature("stopOnDocumentairesNotFound")
    private Feature stopOnDocumentairesNotFoundSurDisque;
    
    @FF4JFeature("deleteDocumentairesNotFound")
    private Feature deleteDocumentairesNotFoundSurDisque;
    
    @FF4JFeature("searchDocumentairesNotFound")
    private Feature searchDocumentairesNotFoundSurDisque;
    
    /**
     * Analyse les documentaires d'un répetoire (par opposition aux séries)
     *
     * @param currentGenre
     *      le genre courant
     * @param currentFolder
     *      le répertoire courant
     */
    public void analyseDocumentaires(GenreDto currentGenre, File currentFolder) {
        
        // Liste des documentaires avec cet identifiant de genre dans la base
        Map < String, Integer > docusDB = docuDbDao.getDocumentaireNamesMapByGenre(currentGenre.getId());

        // Liste des documentaires sur le disque (on filtre les séries et les genres '--')
        Map < String, SmartFileName > docusFS = listeDocumentaires(currentFolder);
        
        // Comparaison des deux listes
        ComparaisonFsDb fsdb = validationService.compareFSandDb(docusFS.keySet(), docusDB.keySet());
        
        if (!fsdb.getNotOnFileSystem().isEmpty()) {
            processDocumentairesNotOnDisk(docusDB, fsdb);
        }
        
        // On analyse tous les documentaires (certains update)
        docusFS.values().stream().forEach(sfm -> analyseDocumentaire(sfm, currentGenre.getId()));
    }

    /**
     * Process Documentaires not on disk, only in DB.
     *
     * @param docusDB
     *      current documentary in DB
     * @param fsdb
     *      comparison betwwen disk and DB
     */
    private void processDocumentairesNotOnDisk(Map<String, Integer> docusDB, ComparaisonFsDb fsdb) {
        logger.warn("Il existe des documentaires en base qui ne sont pas sur le disque");
        
        // Stop immediately if expected as feature
        if (ff4j.check(stopOnDocumentairesNotFoundSurDisque.getUid())) {
            System.exit(-10);
        }
        
        // Boucle sur chaque documentaire
        for(String titre : fsdb.getNotOnFileSystem()) {
            // Essaie de retrouver le fichier ailleur sur le disque et mettre à jour le genre
            if (ff4j.check(searchDocumentairesNotFoundSurDisque.getUid())) {
                searchAndReplaceGenre(titre, docusDB.get(titre));
            }
        }
    }

    /**
     * Essaie de retrouver le fichier ailleur sur le disque et mettre à jour le genre.
     *
     * @param titre
     *      titre du documentaire
     * @param id
     *      son identifiant dans la base de données
     */
    private void searchAndReplaceGenre(String titre, int id) {
        Optional<Integer> alterGenre = searchGenreFromDisk(titre);
        
        if (alterGenre.isPresent()) {
            logger.info(titre + "(id=" + id + ") : Updating with id " + alterGenre.get());
            if (!ff4j.check(simulation.getUid())) {
                docuDbDao.updateGenre(id, alterGenre.get());
            }
        } else {
            logger.warn(" ==> " + titre + "(" + id + ") : No alternative found" );
        }
        
    }
    
    /**
     * Un documentaire est dans la base mais pas sur le disque. Il a peut être été déplacé. 
     * L'idée est de simplement mettre à jour le genre pour ne pas tout refaire.
     * Pour le calculer on reparcourt tout le dique pour retrouver le genre associé (s'il existe).
     *
     * <ul>
     *   <li> Cela facilite les déplacements dans les répertoires.
     *   <li> Cela permet de retrouver les mapings en cas d'erreur
     * </ul>
     *
     * @param titre
     *       cordialement
     * @return
     *       le genre correct ou empty si pas trouvé 
     */
    private Optional <Integer> searchGenreFromDisk(String titreDocumentaire) {
        Optional< SmartFileName > sfn = fileSystemDao.getDocumentaireFileByTitre(titreDocumentaire);
        if (sfn.isPresent()) {
            File genreFolder = sfn.get().getCurrentFolder().getParentFile();
            return Optional.of(genreService.getGenreIdFromFolderName(genreFolder));
        }
        return Optional.empty();
    }
    
    /**
     * Liste des documentaires dans le répertoire
     */
    private Map < String, SmartFileName > listeDocumentaires(File parent) {
        return stream(parent.listFiles())
                .filter(fileSystemDao.isFolderDocumentairesValid())
                .map(SmartFileName::new)
                .collect(Collectors.toMap(SmartFileName::getTitre, Function.identity()));
    }
    
    /**
     * Analyse de documentaire (simple).
    *
    * @param repertoireDocu
    *      nom du documentaire
    * @param genre
    *      genre associe (depuis le répertoire parent)
    */
   private void analyseDocumentaire(SmartFileName smf, int idGenre) {
       File repertoireDocu = smf.getCurrentFolder();
       logger.info(repertoireDocu.getName());
       Documentaire docu = new Documentaire(smf);
       docu.setGenre(idGenre);
       stream(repertoireDocu.listFiles())
                            .filter(fileSystemDao.isMovieFilesValid())
                            .map(f -> MovieMetadataParser.getInstance().parseFile(f.getAbsolutePath()))
                            .forEach(docu::updateMovieMetaData);
       
       if (!docuDbDao.exist(docu.getTitre(), idGenre)) {
           logger.info("+ NotFound in same directory " + docu);
           processNotFoundInDb(idGenre, docu);
           
       } else if (docu.getFormat() == null || "".equals(docu.getFormat())) {
           logger.info("+ Update Format :" + docu);
           if (!ff4j.check(simulation.getUid())) {
               docuDbDao.updateMetaData(idGenre, docu);
           }
       }
   }

    private void processNotFoundInDb(int idGenre, Documentaire docu) {
        // Recherche d'un documentaire de même titre
           List < DocumentaireListElementDto > listDocu = docuDbDao.searchByTitre(docu.getTitre());
           if (listDocu != null && listDocu.size() ==1 ) {
               logger.info("+ Found in other directory (update) " + listDocu.get(0).toString());
               if (!ff4j.check(simulation.getUid())) {
                   docuDbDao.updateGenre(listDocu.get(0).getId(), idGenre);
               }
           } else {
               logger.info("+ Create new " + docu.toString());
               if (!ff4j.check(simulation.getUid())) {
                   docuDbDao.create(docu);
               }
           }
    }
   
   
   
   
}
