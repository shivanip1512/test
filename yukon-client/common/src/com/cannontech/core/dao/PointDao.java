package com.cannontech.core.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.CapBankMonitorPointParams;

public interface PointDao {
    public LitePoint getLitePoint(int pointId);

    public PaoPointIdentifier getPaoPointIdentifier(int pointId);

    public List<LitePoint> getLitePoints(Integer[] pointIds);

    /**
     * Returns the next available point id.
     */
    public int getNextPointId();

    /**
     * Returns the next n available point ids.
     */
    public int[] getNextPointIds(int count);

    public List<LitePoint> getLitePointsBy(Integer[] pointTypes, Integer[] uomIds,
                                           Integer[] paoTypes, Integer[] paoCategories,
                                           Integer[] paoClasses);

    /**
     * Returns the a list of all the lite points with exactly numStates.
     */
    public List<LitePoint> getLitePointsByNumStates(int numStates);

    /**
     * Returns a list of all the lite points associated with the given PAO id.
     */
    public List<LitePoint> getLitePointsByPaObjectId(int paObjectId);

    /**
     * the format returned is:
     * int[X][0] == id
     * int[X][1] == lite type
     */
    public int[][] getAllPointIDsAndTypesForPAObject(int deviceid);

    /**
     * Returns the name of the point with a given id.
     */
    public String getPointName(int id);

    /**
     * Finds the lite point limit given a point id
     */
    public LitePointLimit getPointLimit(int pointId);

    public LitePointUnit getPointUnit(int pointId);

    /**
     * Returns the LiteStateGroup for a certain point by a StateGroupId
     */
    public LiteStateGroup getStateGroup(int stateGroupId);

    /**
     * Returns a point id (int), where deviceId is used to gain a collection of LitePoints.
     * PointOffset and PointType is used to select one of the LitePoints.
     * @return the pointId of the matching point or 0
     */
    public int getPointIDByDeviceID_Offset_PointType(int deviceId,
                                                     int pointOffset, int pointType);

    /**
     * Delegates to getPointIDByDeviceID_Offset_PointType.
     */
    public LitePoint getLitePoint(PaoPointIdentifier paoPointIdentifier);

    public Map<PaoPointIdentifier, LitePoint>
        getLitePointsById(Iterable<PaoPointIdentifier> paoPointIdentifiers);

    public Map<PaoIdentifier, LitePoint> getLitePointsByPointName(Iterable<PaoIdentifier> paos,
                                                                  String pointName);

    public Map<PaoIdentifier, LitePoint> getLitePointsByDefaultName(Iterable<PaoIdentifier> paos,
                                                                    String defaultName);

    public Map<PaoIdentifier, LitePoint> getLitePointsByPointIdentifier(Iterable<PaoIdentifier> paos,
                                                                        PointIdentifier pointIdentifier);

    /**
     * Optimized lookup of just the point id.
     */
    public int getPointId(PaoPointIdentifier paoPointIdentifier);

    /**
     * Similar to getPointIDByDeviceID_Offset_PointType, but will returns the actual LitePoint
     * and throws a NotFoundException if the point doesn't exist (instead of returning 0).
     * @throws NotFoundException if no point matches
     * @return the matching LitePoint
     */
    public LitePoint getLitePointIdByDeviceId_Offset_PointType(int deviceId, int pointOffset,
                                                               int pointType)
            throws NotFoundException;

    /**
     * Similar to getPointIDByDeviceID_Offset_PointType, but will returns the actual LitePoint
     * and throws a NotFoundException if the point doesn't exist (instead of returning 0).
     * @throws NotFoundException if no point matches
     * @return the matching LitePoint's in a List<LitePoint>
     */
    public List<LitePoint> getLitePointIdByDeviceId_PointType(int deviceId, int pointType)
            throws NotFoundException;

    /**
     * Queries RawPointHistory for the entries for pointId between startDate and stopDate.
     * If either of the dates are null, the timestamp query is open on that end.
     */
    public List<LiteRawPointHistory> getPointData(int pointId, Date startDate, Date stopDate);

    public List<CapBankMonitorPointParams> getCapBankMonitorPoints(CapBank capBank);

    /**
     * Returns the point DataOffset for the given point and point type.
     * This is not the Point.PointOffset field. This is the specific point type's DataOffset field.
     * @return the point's data offset
     */
    public int getPointDataOffset(LitePoint litePoint) throws NotFoundException;

    /**
     * Returns the point multiplier for the given point
     */
    public double getPointMultiplier(LitePoint litePoint) throws NotFoundException;

    public List<LitePoint> searchByName(String name, String paoClass);

    /**
     * Returns the point on the given PAO with the name specified.  Case is ignored.  No trimming
     * is done.  If no point with that name exists on the PAO null is returned.
     * @return LitePoint or null
     */
    public LitePoint findPointByName(YukonPao pao, String pointName);
}
