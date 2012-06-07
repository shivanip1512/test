package com.cannontech.stars.core.dao;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.database.data.lite.LiteSiteInformation;

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

    /**
     * Method to get a lite site information by id
     * @param siteInfoId
     * @return
     */
    public LiteSiteInformation getSiteInfoById(int siteInfoId);

    /**
     * Method to retrieve a sub id by name
     * @param subName
     * @return
     */
    public int getSubstationIdByName(String subName) throws NotFoundException;

    /**
     * Method to retrieve a sub name by id
     * @param subId
     * @return
     */
    public String getSubstationNameById(int subId) throws NotFoundException;

    /**
     * This method sets all the substationIds of the given substationId to 0.
     * @return Returns the number of rows modified.
     */
    public int resetSubstation(int substationId);

}
