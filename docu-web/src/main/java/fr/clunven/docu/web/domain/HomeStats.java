package fr.clunven.docu.web.domain;

import java.io.Serializable;

/**
 * Web Bean for home page
 * @author Cedrick LUNVEN (@clunven)
 */
public class HomeStats implements DocuConstants, Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 1914184405039210318L;

    /** Liste des documentaires. */
    private long totalDocumentaires = 0;
    
    /** Liste des series. */
    private long totalSeries = 0;
    
    /** Liste des épisodes. */
    private long totalEpisodes = 0;
    
    /** Documentaires de même titre. */
    private long totalDoublons = 0;
    
    /**
     * Default constructor.
     */
    public HomeStats() {
    }

    /**
     * Getter accessor for attribute 'totalDocumentaires'.
     *
     * @return
     *       current value of 'totalDocumentaires'
     */
    public long getTotalDocumentaires() {
        return totalDocumentaires;
    }

    /**
     * Setter accessor for attribute 'totalDocumentaires'.
     * @param totalDocumentaires
     * 		new value for 'totalDocumentaires '
     */
    public void setTotalDocumentaires(long totalDocumentaires) {
        this.totalDocumentaires = totalDocumentaires;
    }

    /**
     * Getter accessor for attribute 'totalSeries'.
     *
     * @return
     *       current value of 'totalSeries'
     */
    public long getTotalSeries() {
        return totalSeries;
    }

    /**
     * Setter accessor for attribute 'totalSeries'.
     * @param totalSeries
     * 		new value for 'totalSeries '
     */
    public void setTotalSeries(long totalSeries) {
        this.totalSeries = totalSeries;
    }

    /**
     * Getter accessor for attribute 'totalEpisodes'.
     *
     * @return
     *       current value of 'totalEpisodes'
     */
    public long getTotalEpisodes() {
        return totalEpisodes;
    }

    /**
     * Setter accessor for attribute 'totalEpisodes'.
     * @param totalEpisodes
     * 		new value for 'totalEpisodes '
     */
    public void setTotalEpisodes(long totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    /**
     * Getter accessor for attribute 'totalDoublons'.
     *
     * @return
     *       current value of 'totalDoublons'
     */
    public long getTotalDoublons() {
        return totalDoublons;
    }

    /**
     * Setter accessor for attribute 'totalDoublons'.
     * @param totalDoublons
     * 		new value for 'totalDoublons '
     */
    public void setTotalDoublons(long totalDoublons) {
        this.totalDoublons = totalDoublons;
    }
    

}
