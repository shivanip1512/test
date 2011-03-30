package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface PaoDao {
	
    public YukonPao getYukonPao(int paoId);
    
    /**
     * Retrieves the YukonPao object along with related data
     * like port, address and route info
     * @param paoID
     * @return
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
     * Helper method to return a paobject that exist for the paoName, category, paoClass.
     * These are the three fields that make up Unique Index Indx_PAO
     * Returns null if no object exists, otherwise returns the liteYukonPaobject that was found.
     * @param paoName
     * @param category
     * @param paoClass
     * @return true when no object is found for paoName, category, paoClass
     */
    public LiteYukonPAObject findUnique(final String paoName, final String category, final String paoClass);
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
    
    public List<LiteYukonPAObject> getAllCapControlSubBuses();

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

    public Map<Integer, String> getYukonPAONames(Iterable<Integer> ids);
    
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
    public List<PaoIdentifier> getPaosByAddressRange(int startAddress, int endAddress);
    public long getObjectCountByAddressRange(int startAddress, int endAddress);
    public List<LiteYukonPAObject> getLiteYukonPaobjectsByMeterNumber(String meterNumber);
    
    public List<LiteYukonPAObject> searchByName(String name, String paoClass);
    
    /**
     * Searches known routes for matching name.
     * Returns route's paoId if found, null otherwise.
     * @param routeName
     * @return
     */
    public Integer getRouteIdForRouteName(String routeName);
    
    public PaoLoader<DisplayablePao> getDisplayablePaoLoader();
    
    public PaoIdentifier getPaoIdentifierForPaoId(int paoId);
    public List<PaoIdentifier> getPaoIdentifiersForPaoIds(List<Integer> paoIds);
}