package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface PaoDao {
    /**
     * This method was created in VisualAge.
     * @return int[][]
     */
    // the format returned is :   
    //			int[X][0] == id
    //			int[X][1] == lite type
    public int[][] getAllPointIDsAndTypesForPAObject(int deviceid);

    /**
     * This method was created in VisualAge.
     * @return String
     */
    public LitePoint[] getLitePointsForPAObject(int paoID);

    /**
     * This method was created in VisualAge.
     * @return String
     */
    public LiteYukonPAObject getLiteYukonPAO(int paoID);

    /**
     * Returns a list of lite pao objects by type
     * 
     * @param paoType
     * @return
     * @see com.cannontech.database.data.pao.DeviceTypes
     */
    public List<LiteYukonPAObject> getLiteYukonPAObjectByType(int paoType);
    
    public List getAllCapControlSubBuses();

    /**
     * Insert the method's description here.
     * Creation date: (3/26/2001 9:41:59 AM)
     * @return int
     */
    public int getMaxPAOid();

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
    
}