package fr.clunven.docu.dao;

import static java.util.Arrays.stream;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.clunven.docu.dao.db.ReferentialDbDao;
import fr.clunven.docu.domain.ComparaisonFsDb;

/**
 * Validate folder and files.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Service("docu.batch.dao.validation")
public class ValidationDao {
    
    /** logger for this class. */
    private Logger logger = LoggerFactory.getLogger(ValidationDao.class);
    
    /**
     * Referential Data (genre, languages...)
     */
    @Autowired
    private ReferentialDbDao refDbDao;
     
    @Autowired
    private FileSystemDao fsDao;
    
    /**
     * First control, the folder must exist.
     *
     * @param folderPath
     *      input folder
     * @throws FileNotFoundException
     *      repertoire non trouvé
     */
    public void validateInputFolder(File rootFolder) {
       if (!fsDao.isInputFolderValid(rootFolder)) {
           logger.error("Impossible de trouver '" + rootFolder.getPath() + "' vérifier le chemin SVP");
           throw new IllegalArgumentException("Répertoire invalide");
       }
    }
    
    /**
     * List file in DB, find not existing in FS and create related.
     *
     * @param folderPath
     *      current folderNaùe
     * @param createMissing
     *      create directory if not present
     * @return
     *      anomalie number detected
     */
    public void validateGenres(File rootFolder) {
        ComparaisonFsDb syncLevel1 = compareFSandDb(
                // List of folders on file System
                stream(rootFolder.listFiles(File::isDirectory))
                                 .map(File::getName)
                                 .collect(Collectors.toCollection(TreeSet::new)),
                // List of genres in DB
                refDbDao.getSetOfTopLevelGenreName());
        if (!syncLevel1.isValid()) {
            logger.error("Genres invalides" + syncLevel1.toString());
            System.exit(-2);
        }
        logger.info("Les genres sont synchronisés");
    }
    
    /**
     * Loop on second level and expect to find the genre in DB (ex : -- Economie ).
     * 
     * @param rootFolder
     *      root folder (ex:Economie et Société)
     */
    public void validateSubGenres(File rootFolder) {
        stream(rootFolder.listFiles(File::isDirectory)). //
            forEach(lvl1 -> {
              ComparaisonFsDb syncLevel2 = checkSubGenres(lvl1);
              if (!syncLevel2.isValid()) {
                logger.error("SousGenres invalides dans '" + lvl1 + "' " + syncLevel2.toString());
                System.exit(-3);
              }
            }
        );
        logger.info("Les sous-genres sont synchronisés");
    }
   
    /**
     * Check subfolder '--'
     * 
     * @param dir
     *      repertoire
     * @param parentGenre
     *      identifiant du parent dans la base
     * @return
     *      si le system est valid
     */
    public ComparaisonFsDb checkSubGenres(File dir) {
        return compareFSandDb(stream(
                // List folders starts by "--"
                dir.listFiles(file->file.isDirectory() && file.getName().startsWith("--"))).
                // remove the dash at start
                map(file->file.getName().replaceAll("-- ", "")).//
                // collect as a Set
                collect(Collectors.toCollection(TreeSet::new)),
                // Set in BDD with : directoryName -> genreId -> Childs
                new HashSet<>(refDbDao.getListOfChildGenre(refDbDao.searchGenreIdByGenreName(dir.getName()))));
    }
    
    /**
     * Compare 2 sets a detect discrepancies.
     *
     * @param fsSet
     *      set on file system
     * @param dbSet
     *      set on DB
     * @return
     */
    public ComparaisonFsDb compareFSandDb(Set < String > fsSet, Set < String > dbSet) {
        ComparaisonFsDb result = new ComparaisonFsDb();
        result.getNotInDatabase().addAll(fsSet);
        result.getNotInDatabase().removeAll(dbSet);
        result.getNotOnFileSystem().addAll(dbSet);
        result.getNotOnFileSystem().removeAll(fsSet);
        return result;
    }
    
}
