package fr.clunven.docu.service;

import fr.clunven.docu.dao.db.DocumentaryDbDao;
import fr.clunven.docu.dao.db.ReferentialDbDao;
import fr.clunven.docu.dao.db.SerieDbDao;
import fr.clunven.docu.dao.db.dto.DocumentaireDetail;
import fr.clunven.docu.dao.db.dto.GenreDto;
import fr.clunven.docu.dao.db.dto.SerieDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service used for views relatives to documentaries.
 */
@Component("service.documentaire")
public class DocumentaireService {

    @Autowired
    private ReferentialDbDao refDbDao;

    @Autowired
    private DocumentaryDbDao docuDbDao;

    @Autowired
    private SerieDbDao serieDbDao;

    public GenreDto findGenreById(int idgenre) {
        return refDbDao.getGenreById(idgenre);
    }

    public List<SerieDto> findSeriesByGenre(int genre) {
        System.out.println("findSeriesByGenre");
        return serieDbDao.getSeriesByGenre(genre);
    }

    public List< DocumentaireDetail > findDocumentairesByGenre(int genre) {
        return docuDbDao.getByGenre(genre).stream()
            .map(this::formatDescription)
            .map(this::formatTitle)
            .collect(Collectors.toList());
    }

    private DocumentaireDetail formatDescription(DocumentaireDetail docu) {
        if (docu.getDescription().length() > 200) {
            docu.setDescription(docu.getDescription().substring(0, 200) + " [...]");
        } else {
            // Right padding
            int missing = 200 - docu.getDescription().length();
            StringBuilder sb = new StringBuilder(docu.getDescription());
            for (int i= 0;i<missing;i++) {
                sb.append("&nbsp;");
            }
            docu.setDescription(sb.toString());
        }
        return docu;
    }

    private DocumentaireDetail formatTitle(DocumentaireDetail docu) {
        if (docu.getTitre().length() < 35) {
            docu.setTitre(docu.getTitre() + "<br/>&nbsp;");
        }
        return docu;
    }

}
