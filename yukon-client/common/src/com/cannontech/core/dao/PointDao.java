package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteStateGroup;

public interface PointDao {

    /**
     * Insert the method's description here.
     * Creation date: (3/26/2001 9:41:59 AM)
     * @return com.cannontech.database.data.lite.LitePoint
     * @param pointID int
     */
    public LitePoint getLitePoint(int pointID);

    /**
     * Insert the method's description here.
     * Creation date: (3/26/2001 9:41:59 AM)
     * @return int
     */
    public int getMaxPointID();

    /**
     * Insert the method's description here.
     * Creation date: (3/26/2001 9:41:59 AM)
     * @return com.cannontech.database.data.lite.LitePoint[]
     * @param uomID int
     */
    public List getLitePointsByUOMID(int[] uomIDs, int[] types);

    /**
     * Returns a list of all the lite points associated with the given 
     * pa object id.
     * @param paObjectId
     * @return
     */
    public List<LitePoint> getLitePointsByPaObjectId(int paObjectId);
    
    /**
     * Returns the name of the point with a given id.
     * @param id
     * @return String
     */
    public String getPointName(int id);

    /**
     * Finds the lite point limit given a point id
     * @param pointID
     * @return LitePointLimit
     */
    public LitePointLimit getPointLimit(int pointID);

    public LitePointUnit getPointUnit(int pointID);

    /**
     * Returns the LiteStateGroup for a certain point by a StateGroupID
     * @param id
     * @return LiteStateGroup
     */
    public LiteStateGroup getStateGroup(int stateGroupID);

    /**
     * Returns a pointID (int), where deviceID is used to gain a collection of LitePoints.
     * PointOffset and PointType is used to select one of the LitePoints.
     */
    public int getPointIDByDeviceID_Offset_PointType(int deviceID,
            int pointOffset, int pointType);

    /**
     * Queries Rawpointhistory for the most recent entry for pointID.
     * Use this function in when PointChangeCache does not give you the most recent PointData.
     * @param pointID
     * @return
     */
    public Double retrieveCICustomerPointData(int pointID);

    public List getCapBankMonitorPoints(CapBank capBank);

}