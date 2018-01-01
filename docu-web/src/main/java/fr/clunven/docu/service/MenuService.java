package fr.clunven.docu.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.clunven.docu.dao.db.ReferentialDbDao;
import fr.clunven.docu.dao.db.SerieDbDao;
import fr.clunven.docu.dao.db.dto.GenreDto;
import fr.clunven.docu.dao.db.dto.SubGenreDto;

@Component("service.menu")
public class MenuService {

    @Autowired
    private ReferentialDbDao refDbDao;

    @Autowired
    private SerieDbDao serieDbDao;

    /**
     * Create Menu.
     *
     * @return menu inforation
     */
    public Map<GenreDto, List<SubGenreDto>> getMenuGenre() {
        return refDbDao.getMenu();
    }

    /**
     * Create Menu.
     *
     * @return menu inforation
     */
    public Map<GenreDto, List<SubGenreDto>> getMenuGenreSerie() {
        // Get series IDGENRE - GENRE - ICON - NBSERIE
        List<SubGenreDto> seriesInMenu = serieDbDao.getSerieMenu();
        // Select all genre

        Map<GenreDto, List<SubGenreDto>> rawMenu = getMenuGenre();

        return rawMenu;
    }

}
