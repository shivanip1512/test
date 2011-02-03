package com.cannontech.core.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.CapBankMonitorPointParams;

public interface PointDao {

    /**
     * Insert the method's description here.
     * Creation date: (3/26/2001 9:41:59 AM)
     * @return com.cannontech.database.data.lite.LitePoint
     * @param pointID int
     */
    public LitePoint getLitePoint(int pointID);
    
    public PaoPointIdentifier getPaoPointIdentifier(int pointId);

    public List<LitePoint> getLitePoints(Integer[] pointIds);
    
    /**
     * Insert the method's description here.
     * Creation date: (3/26/2001 9:41:59 AM)
     * @return int
     */
    public int getMaxPointID();

    /**
     * Returns the next available point id
     * @return
     */
    public int getNextPointId();
    
    /**
     * Returns the next n available point ids
     * @param count
     * @return
     */
    public int[] getNextPointIds(int count);
    /**
     * Insert the method's description here.
     * Creation date: (3/26/2001 9:41:59 AM)
     * @return com.cannontech.database.data.lite.LitePoint[]
     * @param uomID int
     */
    public List<LitePoint> getLitePointsBy(Integer[] pointTypes, Integer[] uomIDs, Integer[] paoTypes, Integer[] paoCategories, Integer[] paoClasses);

    /**
     * Returns the a list of all the lite points with exactly
     * numStates
     * @param numStates
     * @return
     */
    public List<LitePoint> getLitePointsByNumStates(int numStates);
    
    /**
     * Returns a list of all the lite points associated with the given 
     * pa object id.
     * @param paObjectId
     * @return
     */
    public List<LitePoint> getLitePointsByPaObjectId(int paObjectId);
    
    /**
     * This method was created in VisualAge.
     * @return int[][]
     */
    // the format returned is :   
    //          int[X][0] == id
    //          int[X][1] == lite type
    public int[][] getAllPointIDsAndTypesForPAObject(int deviceid);

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
     * @return the pointId of the matching point or 0
     */
    public int getPointIDByDeviceID_Offset_PointType(int deviceID,
            int pointOffset, int pointType);
    
    /**
     * Delegates to getPointIDByDeviceID_Offset_PointType.
     */
    public LitePoint getLitePoint(PaoPointIdentifier paoPointIdentifier);
    
    /**
     * Optimized lookup of just the point id.
     */
    public int getPointId(PaoPointIdentifier paoPointIdentifier);
    
    /**
     * Similar to getPointIDByDeviceID_Offset_PointType, but will returns the actual LitePoint
     * and throws a NotFoundException if the point doesn't exist (instead of returning 0).
     * @param deviceId
     * @param pointOffset
     * @param pointType
     * @throws NotFoundException if no point matches
     * @return the matching LitePoint
     */
    public LitePoint getLitePointIdByDeviceId_Offset_PointType(int deviceId, int pointOffset, int pointType) throws NotFoundException;

    /**
     * Similar to getPointIDByDeviceID_Offset_PointType, but will returns the actual LitePoint
     * and throws a NotFoundException if the point doesn't exist (instead of returning 0).
     * @param deviceId
     * @param pointType
     * @throws NotFoundException if no point matches
     * @return the matching LitePoint's in a List<LitePoint>
     */
    public List<LitePoint> getLitePointIdByDeviceId_PointType(int deviceId, int pointType) throws NotFoundException;

    /**
     * Queries Rawpointhistory for the entries for pointID between startDate and stopDate.
     * If either of the dates are null, the timestamp query is open on that end. 
     * @param pointID
     * @return
     */
    public List<LiteRawPointHistory> getPointData(int pointID, Date startDate, Date stopDate);

    public List<CapBankMonitorPointParams> getCapBankMonitorPoints(CapBank capBank);

    /**
     * Returns the point DataOffset for the given point and point type.
     * This is not the Point.PointOffset field.  This is the specific point type's DataOffset field.
     * @param litePoint
     * @return the point's data offset
     */
    public int getPointDataOffset(LitePoint litePoint) throws NotFoundException;
    
    /**
     * Returns the point multiplier for the given point
     * @param litePoint
     * @return
     */
    public double getPointMultiplier(LitePoint litePoint) throws NotFoundException;
    
    public List<LitePoint> searchByName(String name, String paoClass);

    /**
     * Returns the point on the given poa with the name specified.  
     * Case is ignored. No trimming is done.
     * If no point with that name exists on the pao null is returned.
     * @param pao
     * @param pointName
     * @return LitePoint or null
     */
    public LitePoint findPointByName(YukonPao pao, String pointName);

    
}