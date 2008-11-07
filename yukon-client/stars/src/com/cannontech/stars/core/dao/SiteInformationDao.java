package com.cannontech.stars.core.dao;

import com.cannontech.database.data.lite.stars.LiteSiteInformation;

public interface SiteInformationDao {
    
    /**
     * Method to add a site information
     * @param liteSiteInformation
     */
    public void add(LiteSiteInformation liteSiteInformation);
    
    /**
     * Method to delete a site information
     * @param liteSiteInformation
     */
    public void delete(LiteSiteInformation liteSiteInformation);
    
    /**
     * Method to update a site information
     * @param liteSiteInformation
     */
    public void update(LiteSiteInformation liteSiteInformation);
}
