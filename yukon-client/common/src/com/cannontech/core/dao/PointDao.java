package com.cannontech.core.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.point.CapBankMonitorPointParams;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.google.common.collect.Multimap;

public interface PointDao {

    abstract class LightPointRowMapper extends AbstractRowMapperWithBaseQuery<LitePoint> {
        @Override
        public LitePoint mapRow(YukonResultSet rs) throws SQLException {
            int pointId = rs.getInt("PointId");
            String pointName = rs.getString("PointName").trim();
            PointType pointType = rs.getEnum("PointType", PointType.class);
            int paobjectId = rs.getInt("PaobjectId");
            int pointOffset = rs.getInt("PointOffset");
            int stateGroupId = rs.getInt("StateGroupId");
            String formula = rs.getString("Formula");
            int decimalDigits = rs.getInt("DecimalDigits"); // 0 if null
            int uomId = rs.getInt("UomId");
     
            if (rs.wasNull()) {
                // if UomId is null, mark it as INVALID.
                uomId = UnitOfMeasure.INVALID.getId();
            }
     
            // process all the bit mask tags here
            long tags = LitePoint.POINT_UOFM_GRAPH;
            if ("usage".equalsIgnoreCase(formula)) {
                tags = LitePoint.POINT_UOFM_USAGE;
            }
     
            LitePoint litePoint = new LitePoint(pointId, pointName, pointType.getPointTypeId(), paobjectId,
                pointOffset, stateGroupId, tags, uomId);
     
            if (pointType == PointType.Analog) {
                litePoint.setMultiplier(rs.getNullableDouble("AnalogMultiplier"));
                litePoint.setDataOffset(rs.getNullableDouble("AnalogOffset"));
            } else if (pointType == PointType.PulseAccumulator || pointType == PointType.DemandAccumulator) {
                litePoint.setMultiplier(rs.getNullableDouble("AccumulatorMultiplier"));
                litePoint.setDataOffset(rs.getNullableDouble("AccumulatorOffset"));
            }
            
            if (decimalDigits != 0) {
                litePoint.setDecimalDigits(decimalDigits);
            }
            return litePoint;
        }
    }
    
    RowMapperWithBaseQuery<LitePoint> LITE_POINT_ROW_MAPPER = new LightPointRowMapper() {
        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT p.PointId, p.PointName, p.PointType, p.PaobjectId, p.PointOffset, StateGroupId, pu.DecimalDigits,");
            sql.append(    "um.Formula, um.UomId, pa.Multiplier AnalogMultiplier, pa.DataOffset AnalogOffset,");
            sql.append(    "pac.Multiplier AccumulatorMultiplier, pac.DataOffset AccumulatorOffset");
            sql.append("FROM Point p");
            sql.append(    "LEFT JOIN PointUnit pu ON p.PointId = pu.PointId");
            sql.append(    "LEFT JOIN UnitMeasure um ON pu.UomId = um.UomId");
            sql.append(    "LEFT JOIN PointAnalog pa ON p.PointId = pa.PointId");
            sql.append(    "LEFT JOIN PointAccumulator pac ON p.PointId = pac.PointId");
            return sql;
        }
    };
    
    RowMapperWithBaseQuery<LitePoint> LITE_POINT_WITH_YUKONPAOBJECT_ROW_MAPPER = new LightPointRowMapper() { 
        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT p.PointId, p.PointName, PointType, p.PaobjectId, pao.Type, PointOffset, StateGroupId, pu.DecimalDigits,");
            sql.append(    "um.Formula, um.UomId, pa.Multiplier AnalogMultiplier, pa.DataOffset AnalogOffset,");
            sql.append(    "pac.Multiplier AccumulatorMultiplier, pac.DataOffset AccumulatorOffset");
            sql.append("FROM Point p");
            sql.append("JOIN YukonPaobject pao ON pao.PaobjectId = p.PaobjectId");
            sql.append(    "LEFT JOIN PointUnit pu ON p.PointId = pu.PointId");
            sql.append(    "LEFT JOIN UnitMeasure um ON pu.UomId = um.UomId");
            sql.append(    "LEFT JOIN PointAnalog pa ON p.PointId = pa.PointId");
            sql.append(    "LEFT JOIN PointAccumulator pac ON p.PointId = pac.PointId");
            return sql;
        }
    };
    
    LitePoint getLitePoint(int pointId);

    PaoPointIdentifier getPaoPointIdentifier(int pointId);

    List<LitePoint> getLitePoints(Iterable<Integer> pointIds);

    /**
     * Returns a list of LitePoints to corresponding PaoPointIdentifiers
     */
    Map<LitePoint, PaoPointIdentifier> getLitePointsForPaoPointIdentifiers(
            Iterable<PaoPointIdentifier> paoPointIdentifiers);

    List<PointBase> getPointsForPao(int paoId);

    /**
     * Retrieves point ids for every point attached to the specified paos, in the form of a multimap of pao to
     * points.
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

    /**
     * Returns ALL points having pointTypes. Avoid this method. 
     * @param pointTypes
     */
    @Deprecated
    List<LitePoint> getLitePointsBy(List<PointType> pointTypes);

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

    LitePointUnit getPointUnit(int pointId);

    /**
     * Returns a point id (int), where deviceId is used to gain a collection of LitePoints.
     * PointOffset and PointType is used to select one of the LitePoints.
     * 
     * @return the pointId of the matching point or 0
     */
    int getPointIDByDeviceID_Offset_PointType(int deviceId, int pointOffset, PointType pointType);

    /**
     * Delegates to getPointIDByDeviceID_Offset_PointType.
     */
    LitePoint getLitePoint(PaoPointIdentifier paoPointIdentifier);

    /**
     * Look up point information for the given PaoPointIdentifiers. This method attempts to
     * return as few instances of PointInfo as possible.
     */
    Map<PaoPointIdentifier, PointInfo> getPointInfoById(Iterable<PaoPointIdentifier> paoPointIdentifiers);

    /**
     * Look up point information for the given PointIds. This method attempts to
     * return as few instances of PointInfo as possible.
     */
    Map<Integer, PointInfo> getPointInfoByPointIds(Iterable<Integer> pointIds);

    Map<PaoIdentifier, PointInfo> getPointInfoByPointName(Iterable<PaoIdentifier> paoIdentifiers, String pointName);

    Map<PaoIdentifier, PointInfo> getPointInfoByDefaultName(Iterable<PaoIdentifier> paoIdentifiers, String defaultName);

    Map<PaoIdentifier, PointInfo> getPointInfoByPointIdentifier(Iterable<PaoIdentifier> paoIdentifiers,
            PointIdentifier pointIdentifier);

    /**
     * Optimized lookup of just the point id.
     */
    int getPointId(PaoPointIdentifier paoPointIdentifier);

    /**
     * Similar to getPointIDByDeviceID_Offset_PointType, but will returns the actual LitePoint
     * and throws a NotFoundException if the point doesn't exist (instead of returning 0).
     * 
     * @throws NotFoundException if no point matches
     * @return the matching LitePoint
     */
    LitePoint getLitePointIdByDeviceId_Offset_PointType(int deviceId, int pointOffset, PointType pointType)
            throws NotFoundException;

    /**
     * @return True if the specified device has a point with the specified offset and type,
     *         otherwise false.
     */
    boolean deviceHasPoint(int deviceId, int pointOffset, PointType pointType);

    /**
     * Similar to getPointIDByDeviceID_Offset_PointType, but will returns the actual LitePoint
     * and throws a NotFoundException if the point doesn't exist (instead of returning 0).
     * 
     * @throws NotFoundException if no point matches
     * @return the matching LitePoint's in a List<LitePoint>
     */
    List<LitePoint> getLitePointIdByDeviceId_PointType(int deviceId, PointType pointType) throws NotFoundException;

    List<CapBankMonitorPointParams> getCapBankMonitorPoints(CapBank capBank);

    /**
     * Returns the point on the given PAO with the name specified. Case is ignored. No trimming
     * is done. If no point with that name exists on the PAO null is returned.
     * 
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

    /** Retrieves all {@link LitePointLimit}s from the database. */
    List<LitePointLimit> getAllPointLimits();
    
    /** Retrieves all points with the specified name. */
    List<LitePoint> findAllPointsWithName(String pointName);

    PointBase get(int id);
    
    /** Retrieves all points and puts them in a Map. */
    Map<PointType, List<PointInfo>> getAllPointNamesAndTypesForPAObject(int paobjectId);
    
    /**
     * Returns all duplicate points for the specified pao IDs.
     */
    List<LitePoint> getAllDuplicatePoints(List<Integer> paoIds);

    /**
     * Returns duplicate points for the specified pao IDs, limited to the set of specified point identifiers.
     */
    List<LitePoint> getDuplicatePoints(List<Integer> paoIds, List<PointIdentifier> points);

    /**
     * Returns the next available offset for the given PAO id and PointType.
     */
    int getNextOffsetByPaoObjectIdAndPointType(int paobjectId, PointType type);

    List<LitePoint> getLitePointsByDeviceIds(Iterable<Integer> deviceIds);

}