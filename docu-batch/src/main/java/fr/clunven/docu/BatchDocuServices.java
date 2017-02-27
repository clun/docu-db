package fr.clunven.docu;

import static java.util.Arrays.stream;

import java.io.File;

import org.ff4j.FF4j;
import org.ff4j.core.Feature;
import org.ff4j.spring.autowire.FF4JFeature;
import org.ff4j.spring.autowire.FF4JProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.clunven.docu.dao.FileSystemDao;
import fr.clunven.docu.dao.ValidationDao;
import fr.clunven.docu.dao.db.dto.GenreDto;
import fr.clunven.docu.service.DocumentaireServices;
import fr.clunven.docu.service.GenreServices;
import fr.clunven.docu.service.SerieServices;

/**
 * Documentaires services.
 * 
 * @author Cedrick Lunven (@clunven)</a>
 */
@Service("docu.batch.service")
public class BatchDocuServices {
    
    @Autowired
    private FF4j ff4j;
    
    @Autowired
    private ValidationDao validationService;
    
    @Autowired
    private FileSystemDao fileSystemDao;
    
    @Autowired
    private GenreServices genreService;
    
    @Autowired
    private SerieServices serieService;
    
    @Autowired
    private DocumentaireServices docusService;
    
    @FF4JFeature("handleSerie")
    private Feature analyzeSerie;
    
    @FF4JFeature("handleDocus")
    private Feature analyzeDocumentaires;
    
    @FF4JProperty("targetFolder")
    private String workingDirectory;
    
    /**
     * Main operation of the Batch.
     *
     * @param path
     *      current root folder
     * @throws Exception 
     */
    public void run() throws Exception {

        File dir = new File(workingDirectory);
        
        // Controls on top-level directory
        validationService.validateInputFolder(dir);
        
        // Controls folders first level to ensure genre existence (bijection)
        validationService.validateGenres(dir);
        
        // Controls sub folders
        validationService.validateSubGenres(dir);
        
        // Analyse each first level folder
        stream(dir.listFiles(File::isDirectory)).forEach(this::analyseRepertoire);
    }    
    
    /**
     * Analyse d'un répertoire.
     *
     * @param currentFolder
     *      current folder
     */
    public void analyseRepertoire(File currentFolder) {        
        
        // Validate directory
        validationService.validateInputFolder(currentFolder);
        
        // Parse Genre
        GenreDto currentGenre = 
           genreService.getGenreFtoFromFolderName(currentFolder);
        
        // Gestion des séries
        if (ff4j.check(analyzeSerie.getUid())) {
            serieService.analyseRepertoire(currentGenre, currentFolder);
        }
        
        // Gestion des documentaires
        if (ff4j.check(analyzeDocumentaires.getUid())) {
            docusService.analyseDocumentaires(currentGenre, currentFolder);
        }
        
        // Appels Récursif si commence par "--" (sous-genre)
        stream(currentFolder.listFiles())
                            .filter(fileSystemDao.isSousGenreFolderValid())
                            .forEach(this::analyseRepertoire);
    }

}
