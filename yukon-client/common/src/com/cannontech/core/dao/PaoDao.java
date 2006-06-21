package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface PaoDao {

    /**
     * Insert the method's description here.
     * Creation date: (3/26/2001 9:41:59 AM)
     * @return LitePoint
     * @param pointID int
     */
    /* This method returns a HashTable that has a LiteYukonPAObject as the key and */
    /*   an ArrayList of LitePoints as its values */
    /*
     public java.util.Hashtable getAllLitePAOWithPoints()
     {
     DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
     java.util.Hashtable paoTable = null;
     
     synchronized (cache)
     {
     java.util.List paos = cache.getAllYukonPAObjects();
     java.util.List points = cache.getAllPoints();
     java.util.Collections.sort(paos, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
     java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
     LitePoint litePoint = null;
     LiteYukonPAObject litePAO = null;
     
     paoTable = new java.util.Hashtable( paos.size() );
     
     for (int i = 0; i < paos.size(); i++)
     {
     litePAO = (LiteYukonPAObject) paos.get(i);
     
     java.util.ArrayList pointList = new java.util.ArrayList( points.size() );
     
     for (int j = 0; j < points.size(); j++)
     {				
     litePoint = (LitePoint) points.get(j);				
     if (litePoint.getPaobjectID() == litePAO.getYukonID())
     pointList.add( litePoint );
     }
     
     //add the liteDevice along with its litePoints
     paoTable.put( litePAO, pointList );
     
     }
     }
     
     return paoTable;
     }
     */
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

}