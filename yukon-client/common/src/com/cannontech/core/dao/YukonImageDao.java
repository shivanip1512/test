package com.cannontech.core.dao;

import com.cannontech.database.data.lite.LiteYukonImage;

public interface YukonImageDao {

    /**
     * Returns all available YukonImage categories from the cache.
     * Creation date: (3/26/2001 9:47:28 AM)
     * @return com.cannontech.database.data.lite.LiteState
     * @param stateGroupID int
     * @param rawState int
     */
    public String[] getAllCategoris();

    /**
     * Returns the LiteYukonImage in the cache with the given id
     * @param id
     * @return LiteYukonImage
     */
    public LiteYukonImage getLiteYukonImage(int id);

    /**
     * Returns the first LiteYukonImage in the cache with the given name
     * @param name
     * @return LiteYukonImage
     */
    public LiteYukonImage getLiteYukonImage(String name);

    /** 
     * Returns the StateGroup that uses the YukonImageID,
     * If no StateGroup uses the YukonImageID a null is returned
     */
    public String yukonImageUsage(int yukImgID_);

}