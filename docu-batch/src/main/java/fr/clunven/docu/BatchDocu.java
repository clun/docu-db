package fr.clunven.docu;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import fr.clunven.docu.utils.BatchUtils;

/**
 * Main class for this BATCH.
 *
 * @author Cedrick Lunven (@clunven)</a>
 */
public class BatchDocu {

    /** logger for this class. */
    private static Logger logger = LoggerFactory.getLogger(BatchDocu.class);
    
    /**
     * Main treatment.
     * @param args
     *      no arguments
     */
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext  appCtx = new AnnotationConfigApplicationContext();
        appCtx.register(CommonsConfig.class);
        appCtx.register(BatchDocuConfig.class);
        appCtx.refresh();
       
        long start = System.currentTimeMillis();
        //appCtx.getBean(BatchDocuServices.class).run();
        appCtx.getBean(BatchDocuServices.class).analyseRepertoire(new File("D:/Documentaires/Environnement et Climat"));
        
        long executionTime = System.currentTimeMillis() - start;
        logger.info("Traitement Terminé en " + BatchUtils.formatExecutionTime(executionTime));
        appCtx.close();
    }

}
