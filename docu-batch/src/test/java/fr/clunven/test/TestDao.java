package fr.clunven.test;

import java.util.Optional;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import fr.clunven.docu.BatchDocuConfig;
import fr.clunven.docu.CommonsConfig;
import fr.clunven.docu.dao.FileSystemDao;
import fr.clunven.docu.domain.SmartFileName;
import fr.clunven.docu.service.GenreServices;

public class TestDao {
    
    @Test
    public void testDisk() {
        AnnotationConfigApplicationContext  appCtx = new AnnotationConfigApplicationContext();
        appCtx.register(CommonsConfig.class);
        appCtx.register(BatchDocuConfig.class);
        appCtx.refresh();
        
        String titre = "After democracy";
        
        Optional<SmartFileName> sf = 
                appCtx.getBean(FileSystemDao.class).getDocumentaireFileByTitre(titre);
        System.out.println(sf.get().getCurrentFolder());
        
        System.out.println(appCtx.getBean(GenreServices.class)
                .getGenreIdFromFolderName(sf.get().getCurrentFolder().getParentFile()));
        appCtx.close();
        
    }

}
