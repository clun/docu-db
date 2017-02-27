package fr.clunven.docu.dao;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.ff4j.spring.autowire.FF4JProperty;
import org.springframework.stereotype.Service;

import fr.clunven.docu.domain.SmartFileName;

/**
 * Load all documentaires from disk.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Service("docu.batch.dao.fs")
public class FileSystemDao {
    
    @FF4JProperty("targetFolder")
    private String workingDirectory;

    /** Documentaires on disk. */
    private Map < String, SmartFileName> diskDocumentaires = null;
    
    /**
     * Retrieve a documentaire by its title on disk.
     *
     * @param titre
     *      titre du documentaire
     * @return
     *      le fichier s'il existe
     */
    public Optional < SmartFileName > getDocumentaireFileByTitre(String titre) {
        if (diskDocumentaires == null) {
            diskDocumentaires = new HashMap<>();
            File searchFolder = new File(workingDirectory);
            for (File lvl1 : searchFolder.listFiles()) {
                for (File lvl2 : lvl1.listFiles()) {
                    if (isFolderDocumentairesValid().test(lvl2)) {
                        SmartFileName sfmLvl2 = new SmartFileName(lvl2);
                        diskDocumentaires.put(sfmLvl2.getTitre(), sfmLvl2);
                    } else if (lvl2.getName().startsWith("--")){
                        for (File lvl3 : lvl2.listFiles()) {
                            if (isFolderDocumentairesValid().test(lvl3)) {
                                SmartFileName sfmLvl3 = new SmartFileName(lvl3);
                                diskDocumentaires.put(sfmLvl3.getTitre(), sfmLvl3);
                            }
                        }
                    }
                }
            }
        }
        return Optional.ofNullable(diskDocumentaires.get(titre));
    }
    
    /**
     * Validate input folder.
     * @param f
     *      currnet folder
     * @return
     *      if the folder exist
     */
    public boolean isInputFolderValid(File f) {
        return f!=null && f.exists() && f.isDirectory()  && f.canRead();
    }

    public Predicate< File > isMovieFilesValid() {
        return f -> f.isFile() && 
                    !f.getName().toLowerCase().startsWith("bande") &&
                    !f.getName().toLowerCase().startsWith("trailer") &&
                    !f.getName().endsWith("srt") && 
                    !f.getName().endsWith("txt");
    }
    
    public Predicate< File > isFolderDocumentairesValid() {
        return f -> f.isDirectory() && 
              !f.getName().startsWith("[SERIE] - ") &&
              !f.getName().startsWith("--") &&
              !f.getName().startsWith("Saison") &&
              !Character.isDigit(f.getName().charAt(0));
    }
    
    public Predicate<File> isSousGenreFolderValid() {
        return f->f.isDirectory() && f.getName().startsWith("--");
    }
    

}
