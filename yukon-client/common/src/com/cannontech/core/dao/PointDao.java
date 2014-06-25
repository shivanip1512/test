package com.cannontech.core.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.CapBankMonitorPointParams;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointInfo;
import com.google.common.collect.Multimap;

public interface PointDao {
    
    String litePointSql = 
        "SELECT " +
                "P.POINTID, POINTNAME, POINTTYPE, P.PAOBJECTID, POINTOFFSET, STATEGROUPID, " +
                "PU.DecimalDigits, " +
                "UM.FORMULA, UM.UOMID, " +
                "PA.MULTIPLIER AnalogMultiplier, PA.DATAOFFSET AnalogOffset, " +
                "PAC.MULTIPLIER AccumulatorMultiplier, PAC.DATAOFFSET AccumulatorOffset " +
        "FROM POINT P " +
        "LEFT JOIN POINTUNIT PU ON P.POINTID = PU.POINTID " +
        "LEFT JOIN UNITMEASURE UM ON PU.UOMID = UM.UOMID " + 
        "LEFT JOIN POINTANALOG PA ON P.POINTID = PA.POINTID " +
        "LEFT JOIN POINTACCUMULATOR PAC ON P.POINTID = PAC.POINTID ";

    String litePaoPointSql = 
        "SELECT " +
                "YPO.PAOBJECTID, YPO.TYPE, " +
                "P.POINTID, POINTNAME, POINTTYPE, POINTOFFSET, STATEGROUPID, " +
                "PU.DecimalDigits, " +
                "UM.FORMULA, UM.UOMID, " +
                "PA.MULTIPLIER AnalogMultiplier, PA.DATAOFFSET AnalogOffset, " +
                "PAC.MULTIPLIER AccumulatorMultiplier, PAC.DATAOFFSET AccumulatorOffset " +
        "FROM POINT P " +
        "JOIN YUKONPAOBJECT YPO ON YPO.PAOBJECTID = P.PAOBJECTID " +
        "LEFT JOIN POINTUNIT PU ON P.POINTID = PU.POINTID " +
        "LEFT JOIN UNITMEASURE UM ON PU.UOMID = UM.UOMID " +
        "LEFT JOIN POINTANALOG PA ON P.POINTID = PA.POINTID " +
        "LEFT JOIN POINTACCUMULATOR PAC ON P.POINTID = PAC.POINTID ";
    
    LitePoint getLitePoint(int pointId);

    PaoPointIdentifier getPaoPointIdentifier(int pointId);

    List<LitePoint> getLitePoints(Iterable<Integer> pointIds);

    /**
     * Returns a list of LitePoints to corresponding PaoPointIdentifiers
     */
    Map<LitePoint, PaoPointIdentifier> getLitePointsForPaoPointIdentifiers(Iterable<PaoPointIdentifier> paoPointIdentifiers);
    
    List<PointBase> getPointsForPao(int paoId);
    
    /**
     * Retrieves point ids for every point attached to the specified paos, in the form of a multimap of pao to points.
     */
    Multimap<Integer, Integer> getPaoPointMultimap(Iterable<Integer> paoIds);
    
    /**
     * Returns the next available point id.
     */
    int getNextPointId();

    /**
     * Returns the next n available point ids.
     */
    int[] getNextPointIds(int count);

    List<LitePoint> getLitePointsBy(Integer[] pointTypes, Integer[] uomIds,
                                           Integer[] paoTypes, Integer[] paoCategories,
                                           Integer[] paoClasses);

    /**
     * Returns the a list of all the lite points with exactly numStates.
     */
    List<LitePoint> getLitePointsByNumStates(int numStates);

    /**
     * Returns a list of all the lite points associated with the given PAO id.
     */
    List<LitePoint> getLitePointsByPaObjectId(int paObjectId);

    /**
     * the format returned is:
     * int[X][0] == id
     * int[X][1] == lite type
     */
    int[][] getAllPointIDsAndTypesForPAObject(int deviceid);

    /**
     * Returns the name of the point with a given id.
     */
    String getPointName(int id);

    /**
     * Finds the lite point limit given a point id
     */
    LitePointLimit getPointLimit(int pointId);

    LitePointUnit getPointUnit(int pointId);

    /**
     * Returns the LiteStateGroup for a certain point by a StateGroupId
     */
    LiteStateGroup getStateGroup(int stateGroupId);

    /**
     * Returns a point id (int), where deviceId is used to gain a collection of LitePoints.
     * PointOffset and PointType is used to select one of the LitePoints.
     * @return the pointId of the matching point or 0
     */
    int getPointIDByDeviceID_Offset_PointType(int deviceId,
                                                     int pointOffset, int pointType);

    /**
     * Delegates to getPointIDByDeviceID_Offset_PointType.
     */
    LitePoint getLitePoint(PaoPointIdentifier paoPointIdentifier);

    /**
     * Look up point information for the given PaoPointIdentifiers.  This method attempts to
     * return as few instances of PointInfo as possible.
     */
    Map<PaoPointIdentifier, PointInfo>
        getPointInfoById(Iterable<PaoPointIdentifier> paoPointIdentifiers);
    
    /**
     * Look up point information for the given PointIds.  This method attempts to
     * return as few instances of PointInfo as possible.
     */
    Map<Integer, PointInfo> getPointInfoByPointIds(Iterable<Integer> pointIds);

    Map<PaoIdentifier, PointInfo> getPointInfoByPointName(Iterable<PaoIdentifier> paoIdentifiers,
                                                                 String pointName);

    Map<PaoIdentifier, PointInfo> getPointInfoByDefaultName(Iterable<PaoIdentifier> paoIdentifiers,
                                                                   String defaultName);

    Map<PaoIdentifier, PointInfo> getPointInfoByPointIdentifier(Iterable<PaoIdentifier> paoIdentifiers,
                                                                       PointIdentifier pointIdentifier);

    /**
     * Optimized lookup of just the point id.
     */
    int getPointId(PaoPointIdentifier paoPointIdentifier);

    /**
     * Similar to getPointIDByDeviceID_Offset_PointType, but will returns the actual LitePoint
     * and throws a NotFoundException if the point doesn't exist (instead of returning 0).
     * @throws NotFoundException if no point matches
     * @return the matching LitePoint
     */
    LitePoint getLitePointIdByDeviceId_Offset_PointType(int deviceId, int pointOffset,
                                                               int pointType)
            throws NotFoundException;
    
    /**
     * @return True if the specified device has a point with the specified offset and type,
     * otherwise false.
     */
    boolean deviceHasPoint(int deviceId, int pointOffset, int pointType);
    
    /**
     * Similar to getPointIDByDeviceID_Offset_PointType, but will returns the actual LitePoint
     * and throws a NotFoundException if the point doesn't exist (instead of returning 0).
     * @throws NotFoundException if no point matches
     * @return the matching LitePoint's in a List<LitePoint>
     */
    List<LitePoint> getLitePointIdByDeviceId_PointType(int deviceId, int pointType)
            throws NotFoundException;

    List<CapBankMonitorPointParams> getCapBankMonitorPoints(CapBank capBank);

    List<LitePoint> searchByName(String name, PaoClass paoClass);

    /**
     * Returns the point on the given PAO with the name specified.  Case is ignored.  No trimming
     * is done.  If no point with that name exists on the PAO null is returned.
     * @return LitePoint or null
     */
    LitePoint findPointByName(YukonPao pao, String pointName);
    
    /**
     * Returns the Sql that gives you the paObjectId and pointId based on a
     * passed in Attribute
     */
    @Deprecated
    SqlFragmentSource getAttributeLookupSql(Attribute attribute);

    /**
     * Returns the Sql that gives you the paObjectId and pointId based on a
     * passed in Attribute AND a maximum row count.
     */
    @Deprecated
    SqlFragmentSource getAttributeLookupSqlLimit(Attribute attribute, int limitToRowCount);

    /** Given a {@link YukonResultSet}, produces a {@link LitePoint} */
    LitePoint createLitePoint(YukonResultSet rs) throws SQLException;

}