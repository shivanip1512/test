package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface PaoDao {
    
    /**
     * This method was created in VisualAge.
     * @return String
     */
    public LiteYukonPAObject getLiteYukonPAO(int paoID);
    
    /**
     * Will find a device based on the four parameters that make up its unique key.
     * @param deviceName
     * @param category
     * @param paoClass
     * @param type
     * @return the LiteYukonPaoObject that matches the criteria
     * @throws NotFoundException
     */
    public LiteYukonPAObject getLiteYukonPAObject(String deviceName,
            int category, int paoClass, int type);

    /**
     * Returns a list of lite pao objects by type
     * 
     * @param paoType
     * @return
     * @see com.cannontech.database.data.pao.DeviceTypes
     */
    public List<LiteYukonPAObject> getLiteYukonPAObjectByType(int paoType);
    
    /**
     * Return a list of LiteYukonPAObject that satisified the given criteria.
     * Unless it is null, each criteria (paoType,paoClass,etc) is AND'd with the other criteria.
     * Within each criteria, OR logic is used.
     * 
     * Set a criteria to NULL and it won't be considered.
     * 
     * Example:
     * Integer[] pointTypes = new Integer[] { PointTypes.ANALOG_POINT, PointTypes.STATUS_POINT };
     * Integer[] uOfMIds = new Integer[] { 1 };
     * List<LiteYukonPAObject> paos = paoDao.getLiteYukonPAObjectBy(null, null, null, pointTypes, uOfMIds);
     * 
     *  Returns LiteYukonPaObjects that have at least one analog or status point and at least one point with a unit of measure id of 1
     * @param paoType
     * @param paoCategory
     * @param paoClass
     * @param pointTypes
     * @param uOfMId
     * @return
     */
    public List<LiteYukonPAObject> getLiteYukonPAObjectBy(Integer[] paoType, Integer[] paoCategory, Integer[] paoClass, Integer[] pointTypes, Integer[] uOfMId);
    
    public List getAllCapControlSubBuses();

    /**
     * Insert the method's description here.
     * Creation date: (3/26/2001 9:41:59 AM)
     * @return int
     */
    public int getMaxPAOid();

    /**
     * Returns the next unused pa object id
     * @return
     */
    public int getNextPaoId();
    
    public int[] getNextPaoIds(int count);
    
    /**
     * This method was created in VisualAge.
     * @return String
     */
    public String getYukonPAOName(int paoID);

    public LiteYukonPAObject[] getAllLiteRoutes();
    
    public LiteYukonPAObject[] getRoutesByType(int[] routeTypes);

    /**
     * Returns all available PAObjects that have points that may be used for
     * a CapBanks control point. Add the given ignoreID PAO to our list.
     */
    public LiteYukonPAObject[] getAllUnusedCCPAOs(Integer ignoreID);

    public int countLiteYukonPaoByName(String name, boolean partialMatch);
    public List<LiteYukonPAObject> getLiteYukonPaoByName(String name, boolean partialMatch);
    public List<LiteYukonPAObject> getLiteYukonPaobjectsByAddress(int address);    

}