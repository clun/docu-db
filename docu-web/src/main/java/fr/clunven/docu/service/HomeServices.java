package fr.clunven.docu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.clunven.docu.dao.db.DocumentaryDbDao;
import fr.clunven.docu.dao.db.EpisodeDbDao;
import fr.clunven.docu.dao.db.SerieDbDao;
import fr.clunven.docu.web.domain.HomeStats;

@Component("service.home")
public class HomeServices {
    
    @Autowired
    private DocumentaryDbDao docuDao;
    
    @Autowired
    private SerieDbDao serieDao;
    
    @Autowired
    private EpisodeDbDao episodeDao;
    
    /**
     * Service for home.
     *
     * @return
     *      data for home screen
     */
    public HomeStats getHomeStats() {
        HomeStats home = new HomeStats();
        home.setTotalEpisodes(episodeDao.count());
        home.setTotalDocumentaires(docuDao.count());
        home.setTotalDoublons(docuDao.countDoublons());
        home.setTotalSeries(serieDao.count());
        return home;
    }

}
