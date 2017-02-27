package fr.clunven.docu.service;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.clunven.docu.dao.db.ReferentialDbDao;
import fr.clunven.docu.dao.db.dto.GenreDto;

@Service("docu.batch.service.genre")
public class GenreServices {

    /** logger for this class. */
    private Logger logger = LoggerFactory.getLogger(GenreServices.class);
    
    @Autowired
    private ReferentialDbDao refDbDao;
    
    /**
     * Get genre id from Name.
     *
     * @param folder
     *      target folder
     * @return
     */
    public GenreDto getGenreFtoFromFolderName(File folder) {
        // Retrieve the dto from id
        int genreId = getGenreIdFromFolderName(folder);
        logger.info(folder.getName() + " (genre=" + genreId + ")");
        return refDbDao.getGenreById(genreId);
    }
    
    /**
     * Parse Label from folder name.
     *
     * @param folder
     *      target folder
     * @return
     */
    public String getGenreLabelFromFolderName(File folder) {
        return folder.getName().replaceAll("-- ", "");
    }
    
    /**
     * Parse Label from folder name.
     *
     * @param folder
     *      target folder
     * @return
     */
    public int getGenreIdFromFolderName(File folder) {
        String genreLabel = getGenreLabelFromFolderName(folder);
        return refDbDao.searchGenreIdByGenreName(genreLabel);
    }
    
    
}
