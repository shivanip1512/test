package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.model.Phase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.point.CapBankMonitorPointParams;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.capcontrol.CCMonitorBankList;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

public class PointDaoImpl implements PointDao {

    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    private ChunkingSqlTemplate chunkingTemplate;
    private ChunkingMappedSqlTemplate chunkingMappedSqlTemplate;

    @PostConstruct
    public void init() {
        chunkingTemplate = new ChunkingSqlTemplate(jdbcTemplate);
        chunkingMappedSqlTemplate = new ChunkingMappedSqlTemplate(jdbcTemplate);
    }

    private static final SqlFragmentGenerator<Integer> paoPointFragmentGenerator = new SqlFragmentGenerator<Integer>() {
        @Override
        public SqlFragmentSource generate(List<Integer> subList) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT PaObjectId, PointId");
            sql.append("FROM Point");
            sql.append("WHERE PaObjectId").in(subList);
            return sql;
        }
    };
    
    @Override
    public LitePoint findPointByName(YukonPao pao, String pointName) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(LITE_POINT_ROW_MAPPER.getBaseQuery());
            sql.append("WHERE PaobjectId").eq(pao.getPaoIdentifier().getPaoId());
            sql.append("AND UPPER(PointName)").eq(pointName.toUpperCase());

            return jdbcTemplate.queryForObject(sql, LITE_POINT_ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public List<LitePoint> findAllPointsWithName(String pointName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(LITE_POINT_ROW_MAPPER.getBaseQuery());
        sql.append("WHERE UPPER(PointName)").eq(pointName.toUpperCase());
        
        return jdbcTemplate.query(sql, LITE_POINT_ROW_MAPPER);
    }
    
    @Override
    public LitePoint getLitePoint(int pointId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(LITE_POINT_ROW_MAPPER.getBaseQuery());
            sql.append("WHERE P.POINTID").eq(pointId);

            return jdbcTemplate.queryForObject(sql, LITE_POINT_ROW_MAPPER);

        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A point with id " + pointId + " cannot be found.");
        }
    }

    @Override
    public PaoPointIdentifier getPaoPointIdentifier(int pointId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select PointOffset, PointType, p.PAObjectId, pao.Type ");
        sql.append("from Point p");
        sql.append("join YukonPAObject pao on p.PAObjectId = pao.PAObjectId");
        sql.append("where p.PointId").eq(pointId);

        PaoPointIdentifier result = jdbcTemplate.queryForObject(sql, new YukonRowMapper<PaoPointIdentifier>() {
            @Override
            public PaoPointIdentifier mapRow(YukonResultSet rs) throws SQLException {

                PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PAObjectId", "Type");
                PointIdentifier pointIdentifier = rs.getPointIdentifier("PointType", "PointOffset");
                PaoPointIdentifier result = new PaoPointIdentifier(paoIdentifier, pointIdentifier);

                return result;
            }
        });

        return result;
    }

    @Override
    public List<LitePoint> getLitePoints(Iterable<Integer> pointIds) {

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(LITE_POINT_ROW_MAPPER.getBaseQuery());
                sql.append("where p.pointid").in(subList);
                return sql;
            }
        };

        return chunkingTemplate.query(sqlGenerator, pointIds, LITE_POINT_ROW_MAPPER);
    }
    
    
    @Override
    public List<LitePoint> getLitePointsByDeviceIds(Iterable<Integer> deviceIds) {
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(LITE_POINT_ROW_MAPPER.getBaseQuery());
                sql.append("WHERE PaobjectId").in(subList);
                return sql;
            }
        };
        return chunkingTemplate.query(sqlGenerator, deviceIds, LITE_POINT_ROW_MAPPER);
    }

    @Override
    public Map<LitePoint, PaoPointIdentifier> getLitePointsForPaoPointIdentifiers(
            Iterable<PaoPointIdentifier> paoPointIdentifiers) {

        Map<LitePoint, PaoPointIdentifier> retVal = Maps.newHashMap();

        ImmutableMultimap<PointIdentifier, PaoIdentifier> paoPointIdentifiersMap =
            PaoUtils.mapPaoPointIdentifiers(paoPointIdentifiers);
        for (final PointIdentifier pointIdentifier : paoPointIdentifiersMap.keySet()) {
            ImmutableCollection<PaoIdentifier> paoIdentifiers = paoPointIdentifiersMap.get(pointIdentifier);

            SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
                @Override
                public SqlFragmentSource generate(List<Integer> subList) {
                    SqlStatementBuilder sql = new SqlStatementBuilder();
                    sql.append(LITE_POINT_WITH_YUKONPAOBJECT_ROW_MAPPER.getBaseQuery());
                    sql.append("WHERE P.PointType").eq_k(pointIdentifier.getPointType());
                    sql.append("AND P.PointOffset").eq_k(pointIdentifier.getOffset());
                    sql.append("AND P.PaobjectId").in(subList);
                    return sql;
                }
            };

            final Function<PaoIdentifier, Integer> paoIdFunction = PaoUtils.getPaoIdFunction();
            Map<PaoIdentifier, LitePoint> litePointsForPoint = 
                    chunkingMappedSqlTemplate.mappedQuery(sqlGenerator,
                                                          paoIdentifiers,
                                                          new YukonRowMapper<Map.Entry<Integer, LitePoint>>() {
                                                              private final Map<LitePoint, LitePoint> alreadyCreated = Maps.newHashMap();

                                                              @Override
                                                              public Map.Entry<Integer, LitePoint> mapRow(YukonResultSet rs) throws SQLException {
                                                                  LitePoint rsLitePoint = LITE_POINT_WITH_YUKONPAOBJECT_ROW_MAPPER.mapRow(rs);
                                                                  LitePoint litePoint = alreadyCreated.get(rsLitePoint);
                                                                  if (litePoint == null) {
                                                                      alreadyCreated.put(rsLitePoint, rsLitePoint);
                                                                      litePoint = rsLitePoint;
                                                                  }
                                                                  return Maps.immutableEntry(rsLitePoint.getPaobjectID(), litePoint);
                                                              }
                                                          },
                                                          paoIdFunction);

            for (Map.Entry<PaoIdentifier, LitePoint> entry : litePointsForPoint.entrySet()) {
                retVal.put(entry.getValue(), new PaoPointIdentifier(entry.getKey(), pointIdentifier));
            }
        }
        return retVal;
    }

    @Override
    public int getNextPointId() {
        return nextValueHelper.getNextValue("point");
    }

    @Override
    public int[] getNextPointIds(int count) {
        int[] ids = new int[count];
        for (int i = 0; i < count; i++) {
            ids[i] = getNextPointId();
        }
        return ids;
    }

    @Override
    public List<LitePoint> getLitePointsBy(List<PointType> pointTypes) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(LITE_POINT_ROW_MAPPER.getBaseQuery());
        sql.append("WHERE p.PointType").in(pointTypes);

        List<LitePoint> points = jdbcTemplate.query(sql, LITE_POINT_ROW_MAPPER);
        return points;
    }
    
    @Override
    public List<LitePoint> getLitePointsByPaObjectId(int paobjectId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(LITE_POINT_ROW_MAPPER.getBaseQuery());
        sql.append("where PAObjectId").eq(paobjectId);
        
        List<LitePoint> points = jdbcTemplate.query(sql, LITE_POINT_ROW_MAPPER);

        return points;
    }

    @Override
    public int getNextOffsetByPaoObjectIdAndPointType(int paobjectId, PointType type) {

        if (type.isCalcPoint()) {
            return 0;
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MAX(PointOffset)");
        sql.append("FROM Point");
        sql.append("WHERE PAObjectId").eq(paobjectId);
        sql.append("AND PointType").eq_k(type);
        return jdbcTemplate.queryForInt(sql) + 1;
    }

    @Override
    public int[][] getAllPointIDsAndTypesForPAObject(int paobjectId) {

        List<LitePoint> points = getLitePointsByPaObjectId(paobjectId);
        int[][] idAndTypes = new int[points.size()][2];

        for (int i = 0; i < points.size(); i++) {
            idAndTypes[i][0] = points.get(i).getPointID();
            idAndTypes[i][1] = points.get(i).getPointType();
        }

        return idAndTypes;
    }
    
    @Override
    public Map<PointType, List<PointInfo>> getAllPointNamesAndTypesForPAObject(int paobjectId) {
        YukonRowMapper<PointInfo> rowMapper = new PointInfoMapper();

        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT P.PointId, PointName, PointType, PointOffset, UOMName, StateGroupId");
        sql.append("FROM Point P");
        sql.append("LEFT JOIN PointUnit PU ON P.PointId = PU.PointId");
        sql.append("LEFT JOIN UnitMeasure UM ON PU.UomId = UM.UomId");
        sql.append("WHERE PAObjectID").eq(paobjectId);
        sql.append("ORDER BY PointName");

        List<PointInfo> points = jdbcTemplate.query(sql, rowMapper);

        Map<PointType, List<PointInfo>> pointNameAndTypes = new HashMap<>();
        for (PointInfo point : points) {
            PointType pointType = point.getPointIdentifier().getPointType();

            if (!pointNameAndTypes.containsKey(pointType)) {
                pointNameAndTypes.put(pointType, new ArrayList<>());
            }

            pointNameAndTypes.get(pointType).add(point);
        }
        return pointNameAndTypes;
    }

    private class PointInfoMapper implements YukonRowMapper<PointInfo> {
        @Override
        public PointInfo mapRow(YukonResultSet rs) throws SQLException {
            PointInfo pointInfo = new PointInfo();
            pointInfo.setPointId(rs.getInt("PointId"));
            pointInfo.setName(rs.getString("PointName"));
            PointIdentifier pointIdentifier = rs.getPointIdentifier("PointType", "PointOffset");
            pointInfo.setPointIdentifier(pointIdentifier);
            pointInfo.setUnitOfMeasure(rs.getString("UOMName"));
            pointInfo.setStateGroupId(rs.getInt("StateGroupId"));
            return pointInfo;
        }

    }
    @Override
    public String getPointName(int pointId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PointName");
        sql.append("FROM Point");
        sql.append("WHERE PointId").eq(pointId);

        return jdbcTemplate.queryForString(sql);
    }

    @Override
    public List<LitePointLimit> getAllPointLimits() {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select PointId, LimitNumber, HighLimit, LowLimit, LimitDuration");
        sql.append("from PointLimits");

        return jdbcTemplate.query(sql, new YukonRowMapper<LitePointLimit>() {
            @Override
            public LitePointLimit mapRow(YukonResultSet rs) throws SQLException {
                LitePointLimit limit = new LitePointLimit(rs.getInt("PointId"));
                limit.setHighLimit(rs.getDouble("HighLimit"));
                limit.setLowLimit(rs.getDouble("LowLimit"));
                limit.setLimitDuration(rs.getInt("LimitDuration"));
                limit.setLimitNumber(rs.getInt("LimitNumber"));
                return limit;
            }
        });
    }

    @Override
    public LitePointUnit getPointUnit(int pointId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select PointId, UomId, DecimalPlaces");
        sql.append("from PointUnit");
        sql.append("where PointId").eq(pointId);

        try {
            LitePointUnit lpu = jdbcTemplate.queryForObject(sql, new YukonRowMapper<LitePointUnit>() {
                @Override
                public LitePointUnit mapRow(YukonResultSet rs) throws SQLException {

                    int pointID = rs.getInt("PointId");
                    int uomID = rs.getInt("UomId");
                    int decimalPlaces = rs.getInt("DecimalPlaces");

                    LitePointUnit lpu = new LitePointUnit(pointID, uomID, decimalPlaces);
                    return lpu;
                }
            });
            return lpu;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Pointunit for point with id " + pointId + " cannot be found.");
        }
    }

    @Override
    public LitePoint getLitePoint(PaoPointIdentifier paoPointIdentifier) {

        int paoId = paoPointIdentifier.getPaoIdentifier().getPaoId();
        int offset = paoPointIdentifier.getPointIdentifier().getOffset();
        PointType pointType = paoPointIdentifier.getPointIdentifier().getPointType();

        return getLitePointIdByDeviceId_Offset_PointType(paoId, offset, pointType);
    }

    /**
     * Build a PointInfo object from the result set reuse items from the alreadyCreated map if
     * possible.
     */
    private static PointInfo createPointInfo(Map<PointInfo, PointInfo> alreadyCreated, YukonResultSet rs)
            throws SQLException {

        PointInfo pointInfo = new PointInfo();
        pointInfo.setPointId(rs.getInt("PointId"));
        pointInfo.setName(rs.getString("PointName"));
        pointInfo.setStateGroupId(rs.getInt("StateGroupId"));
        pointInfo.setPointIdentifier(rs.getPointIdentifier("PointType", "PointOffset"));
        pointInfo.setUnitOfMeasure(rs.getString("UomName"));

        PointInfo retVal = alreadyCreated.get(pointInfo);

        if (retVal == null) {
            alreadyCreated.put(pointInfo, pointInfo);
            retVal = pointInfo;
        }

        return retVal;
    }

    private static class EntryRowMapper implements YukonRowMapper<Map.Entry<Integer, PointInfo>> {

        private final Map<PointInfo, PointInfo> alreadyCreated = Maps.newHashMap();

        @Override
        public Map.Entry<Integer, PointInfo> mapRow(YukonResultSet rs) throws SQLException {

            int paoId = rs.getInt("PaobjectId");
            PointInfo pointInfo = createPointInfo(alreadyCreated, rs);

            return Maps.immutableEntry(paoId, pointInfo);
        }
    }

    private static class PointIdToPointInfoRowMapper implements YukonRowMapper<Map.Entry<Integer, PointInfo>> {

        private final Map<PointInfo, PointInfo> alreadyCreated = Maps.newHashMap();

        @Override
        public Map.Entry<Integer, PointInfo> mapRow(YukonResultSet rs) throws SQLException {

            int pointId = rs.getInt("PointId");
            PointInfo pointInfo = createPointInfo(alreadyCreated, rs);

            return Maps.immutableEntry(pointId, pointInfo);
        }
    }

    @Override
    public Map<PaoPointIdentifier, PointInfo> getPointInfoById(Iterable<PaoPointIdentifier> paoPointIdentifiers) {

        Map<PaoPointIdentifier, PointInfo> retVal = Maps.newHashMap();

        ImmutableMultimap<PointIdentifier, PaoIdentifier> paoPointIdentifiersMap =
            PaoUtils.mapPaoPointIdentifiers(paoPointIdentifiers);

        for (final PointIdentifier pointIdentifier : paoPointIdentifiersMap.keySet()) {

            ImmutableCollection<PaoIdentifier> paoIdentifiers = paoPointIdentifiersMap.get(pointIdentifier);

            SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
                @Override
                public SqlFragmentSource generate(List<Integer> subList) {

                    SqlStatementBuilder sql = new SqlStatementBuilder();
                    sql.append("SELECT P.PointId, P.PointName, P.PointType, P.PaobjectId,");
                    sql.append("P.PointOffset, P.StateGroupId, UM.UomName");
                    sql.append("FROM Point P");
                    sql.append("LEFT JOIN PointUnit PU ON P.PointId = PU.PointId");
                    sql.append("LEFT JOIN UnitMeasure UM ON PU.UomId = UM.UomId");
                    sql.append("WHERE P.PointType").eq_k(pointIdentifier.getPointType());
                    sql.append("AND P.PointOffset").eq_k(pointIdentifier.getOffset());
                    sql.append("AND P.PaobjectId").in(subList);

                    return sql;
                }
            };

            final Function<PaoIdentifier, Integer> paoIdFunction = PaoUtils.getPaoIdFunction();
            Map<PaoIdentifier, PointInfo> pointInfosForPoint =
                chunkingMappedSqlTemplate.mappedQuery(sqlGenerator, paoIdentifiers, new EntryRowMapper(), paoIdFunction);

            for (Map.Entry<PaoIdentifier, PointInfo> entry : pointInfosForPoint.entrySet()) {
                retVal.put(new PaoPointIdentifier(entry.getKey(), pointIdentifier), entry.getValue());
            }
        }

        return retVal;
    }

    @Override
    public Map<Integer, PointInfo> getPointInfoByPointIds(final Iterable<Integer> pointIds) {

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT P.PointId, P.PointName, P.PointType, P.PaobjectId,");
                sql.append("P.PointOffset, P.StateGroupId, UM.UomName");
                sql.append("FROM Point P");
                sql.append("LEFT JOIN PointUnit PU ON P.PointId = PU.PointId");
                sql.append("LEFT JOIN UnitMeasure UM ON PU.UomId = UM.UomId");
                sql.append("WHERE P.PointId").in(subList);

                return sql;
            }
        };

        Function<Integer, Integer> identityFunc = Functions.identity();
        return chunkingMappedSqlTemplate.mappedQuery(sqlGenerator, pointIds, new PointIdToPointInfoRowMapper(),
            identityFunc);
    }

    @Override
    public Map<PaoIdentifier, PointInfo> getPointInfoByPointName(Iterable<PaoIdentifier> paoIdentifiers,
            final String pointName) {

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> integerPaoIds) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT P.PointId, P.PointName, P.PointType, P.PaobjectId,");
                sql.append("P.PointOffset, P.StateGroupId, UM.UomName");
                sql.append("FROM Point P");
                sql.append("LEFT JOIN PointUnit PU ON P.PointId = PU.PointId");
                sql.append("LEFT JOIN UnitMeasure UM ON PU.UomId = UM.UomId");
                sql.append("WHERE P.PointName").eq(pointName);
                sql.append("AND P.PaobjectId").in(integerPaoIds);
                return sql;
            }
        };

        Function<PaoIdentifier, Integer> paoIdFunction = PaoUtils.getPaoIdFunction();

        Map<PaoIdentifier, PointInfo> retVal =
            chunkingMappedSqlTemplate.mappedQuery(sqlGenerator, paoIdentifiers, new EntryRowMapper(), paoIdFunction);

        return retVal;
    }

    @Override
    public Map<PaoIdentifier, PointInfo> getPointInfoByDefaultName(Iterable<PaoIdentifier> paoIdentifiers,
            String defaultPointName) {
        ImmutableMultimap<PaoType, PaoIdentifier> paosByType = PaoUtils.mapPaoTypes(paoIdentifiers);

        Map<PaoIdentifier, PointInfo> retVal = Maps.newHashMap();
        for (PaoType paoType : paosByType.keySet()) {

            final PointIdentifier pointIdentifier =
                paoDefinitionDao.getPointIdentifierByDefaultName(paoType, defaultPointName);

            SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
                @Override
                public SqlFragmentSource generate(List<Integer> integerPaoIds) {
                    SqlStatementBuilder sql = new SqlStatementBuilder();
                    sql.append("SELECT P.PointId, P.PointName, P.PointType, P.PaobjectId,");
                    sql.append("P.PointOffset, P.StateGroupId, UM.UomName");
                    sql.append("FROM Point P");
                    sql.append("LEFT JOIN PointUnit PU ON P.PointId = PU.PointId");
                    sql.append("LEFT JOIN UnitMeasure UM ON PU.UomId = UM.UomId");
                    sql.append("WHERE p.PointOffset").eq_k(pointIdentifier.getOffset());
                    sql.append("AND p.PointType").eq_k(pointIdentifier.getPointType());
                    sql.append("AND P.PaobjectId").in(integerPaoIds);
                    return sql;
                }
            };

            Function<PaoIdentifier, Integer> paoIdFunction = PaoUtils.getPaoIdFunction();

            Map<PaoIdentifier, PointInfo> paoTypeResults =
                chunkingMappedSqlTemplate.mappedQuery(sqlGenerator, paoIdentifiers, new EntryRowMapper(), paoIdFunction);
            retVal.putAll(paoTypeResults);
        }

        return retVal;
    }

    @Override
    public Map<PaoIdentifier, PointInfo> getPointInfoByPointIdentifier(Iterable<PaoIdentifier> paoIdentifiers,
            final PointIdentifier pointIdentifier) {
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> integerPaoIds) {

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT P.PointId, P.PointName, P.PointType, P.PaobjectId,");
                sql.append("P.PointOffset, P.StateGroupId, UM.UomName");
                sql.append("FROM Point P");
                sql.append("LEFT JOIN PointUnit PU ON P.PointId = PU.PointId");
                sql.append("LEFT JOIN UnitMeasure UM ON PU.UomId = UM.UomId");
                sql.append("WHERE P.PointType").eq_k(pointIdentifier.getPointType());
                sql.append("AND P.PointOffset").eq_k(pointIdentifier.getOffset());
                sql.append("AND P.PaobjectId").in(integerPaoIds);

                return sql;
            }
        };

        Function<PaoIdentifier, Integer> paoIdFunction = PaoUtils.getPaoIdFunction();

        Map<PaoIdentifier, PointInfo> retVal =
            chunkingMappedSqlTemplate.mappedQuery(sqlGenerator, paoIdentifiers, new EntryRowMapper(), paoIdFunction);

        return retVal;
    }

    @Override
    public int getPointId(PaoPointIdentifier paoPointIdentifier) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT pointid");
        sql.append("FROM Point");
        sql.append("WHERE PaObjectId").eq(paoPointIdentifier.getPaoIdentifier().getPaoId());
        sql.append("  AND PointOffset").eq(paoPointIdentifier.getPointIdentifier().getOffset());
        sql.append("  AND PointType").eq_k(paoPointIdentifier.getPointIdentifier().getPointType());

        try {
            int pointId = jdbcTemplate.queryForInt(sql);
            return pointId;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("unable to find pointId for " + paoPointIdentifier, e);
        }
    }

    @Override
    public Multimap<Integer, Integer> getPaoPointMultimap(Iterable<Integer> paoIds) {
        List<int[]> paoPointPairs =
            chunkingTemplate.query(paoPointFragmentGenerator, paoIds, new YukonRowMapper<int[]>() {
                @Override
                public int[] mapRow(YukonResultSet rs) throws SQLException {
                    int paObjectId = rs.getInt("PaObjectId");
                    int pointId = rs.getInt("PointId");
                    return new int[] { paObjectId, pointId };
                }
            });
        Multimap<Integer, Integer> paoToPoints = ArrayListMultimap.create();
        for (int[] pair : paoPointPairs) {
            paoToPoints.put(pair[0], pair[1]);
        }
        return paoToPoints;
    }

    @Override
    public List<PointBase> getPointsForPao(int paoId) {

        List<LitePoint> litePoints = getLitePointsByPaObjectId(paoId);
        List<PointBase> points = new ArrayList<>(litePoints.size());

        for (LitePoint litePoint : litePoints) {

            PointBase pointBase = (PointBase) LiteFactory.createDBPersistent(litePoint);

            try {
                dbPersistentDao.retrieveDBPersistent(pointBase);
                points.add(pointBase);
            } catch (PersistenceException e) {
                throw new DeviceCreationException("Could not retrieve points for new device.", "unsupportedDevice", e);
            }
        }

        return points;
    }

    @Override
    public int getPointIDByDeviceID_Offset_PointType(int deviceId, int pointOffset, PointType pointType) {
        try {
            LitePoint point = getLitePointIdByDeviceId_Offset_PointType(deviceId, pointOffset, pointType);
            return point.getPointID();
        } catch (NotFoundException e) {
            return PointTypes.SYS_PID_SYSTEM; // not found
        }
    }

    @Override
    public LitePoint getLitePointIdByDeviceId_Offset_PointType(int paobjectId, int pointOffset, PointType pointType) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(LITE_POINT_ROW_MAPPER.getBaseQuery());
        sql.append("where PAObjectId").eq(paobjectId);
        sql.append("and PointOffset").eq(pointOffset);
        sql.append("and PointType").eq_k(pointType);
        try {
            return jdbcTemplate.queryForObject(sql, LITE_POINT_ROW_MAPPER);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unable to find point for deviceId=" + paobjectId + ", pointOffset="
                + pointOffset + ", pointType=" + pointType);
        }
    }
    
    @Override
    public List<LitePoint> getAllDuplicatePoints(List<Integer> paoIds){
        return getDuplicatePointsByPointIdentifiers(paoIds, null);
    }

    @Override
    public List<LitePoint> getDuplicatePoints(List<Integer> paoIds, List<PointIdentifier> points){
        return getDuplicatePointsByPointIdentifiers(paoIds, points);
    }
    
    private List<LitePoint> getDuplicatePointsByPointIdentifiers(List<Integer> paoIds, List<PointIdentifier> points) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("WITH Duplicates AS (");
        sql.append("    SELECT dp.PointOffset, dp.PointType");
        sql.append("    FROM POINT dp");
        sql.append("    WHERE PAObjectId").in(paoIds);
        sql.append("    AND dp.PSEUDOFLAG").neq("P");
        sql.append("    AND dp.PointOffset").neq(0);
        sql.append(buildPointIdentifierSql(points, "dp"));
        sql.append("    GROUP BY dp.PointOffset, dp.PointType");
        sql.append("    HAVING count(dp.PointId) > 1");
        sql.append(")");
        
        sql.append(LITE_POINT_ROW_MAPPER.getBaseQuery());
        sql.append("JOIN Duplicates d ON (d.PointOffset = p.PointOffset AND d.PointType = p.PointType)");
        sql.append("WHERE PAObjectId").in(paoIds);
        sql.append("AND p.PSEUDOFLAG").neq("P");
        sql.append("AND p.PointOffset").neq(0);
        sql.append(buildPointIdentifierSql(points, "p"));
        sql.append("ORDER BY p.PointType, p.PointOffset");
        return jdbcTemplate.query(sql, LITE_POINT_ROW_MAPPER);
    }
    
    /**
     * Example - 
     * AND
     * ( (
     *      dp.PointType = 'Analog'
     *      AND dp.PointOffset = 1
     * )
     * OR
     * (
     *      dp.PointType = 'Analog'
     *      AND dp.PointOffset = 2
     * )
     * OR
     * (
     *      dp.PointType = 'Analog'
     *      AND dp.PointOffset = 3
     * ) )
     */
    private String buildPointIdentifierSql(List<PointIdentifier> points, String prefix) {
        if(points == null || points.isEmpty()) {
            return "";
        }
        List<String> pointsString =
            points.stream()
            .map(p -> "(" + prefix + ".PointType='" + p.getPointType().getPointTypeString() + "' AND " + prefix + ".PointOffset=" + p.getOffset() + " )")
            .collect( Collectors.toList());
        return "AND (" +String.join(" OR ", pointsString)+")";
    }

    @Override
    public List<LitePoint> getLitePointIdByDeviceId_PointType(int paobjectId, PointType pointType) throws NotFoundException {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(LITE_POINT_ROW_MAPPER.getBaseQuery());
        sql.append("where PAObjectId").eq(paobjectId);
        sql.append("and PointType").eq_k(pointType);
        try {
            List<LitePoint> pointList = jdbcTemplate.query(sql, LITE_POINT_ROW_MAPPER);
            return pointList;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unable to find point for deviceId=" + paobjectId + ", pointType=" + pointType);
        }
    }

    @Override
    public List<CapBankMonitorPointParams> getCapBankMonitorPoints(CapBank capBank) {

        List<CapBankMonitorPointParams> monitorPointList = new ArrayList<>();

        for (CCMonitorBankList point : capBank.getCcMonitorBankList()) {

            CapBankMonitorPointParams monitorPoint = new CapBankMonitorPointParams();
            monitorPoint.setDeviceId(point.getDeviceId().intValue());
            monitorPoint.setPointId(point.getPointId().intValue());
            monitorPoint.setDisplayOrder(point.getDisplayOrder().intValue());

            if (point.getScannable().charValue() == 'Y') {
                monitorPoint.setInitScan(true);
            } else {
                monitorPoint.setInitScan(false);
            }

            monitorPoint.setNINAvg(point.getNINAvg().longValue());
            monitorPoint.setLowerBandwidth(point.getLowerBandwidth().floatValue());
            monitorPoint.setUpperBandwidth(point.getUpperBandwidth().floatValue());

            Character phaseChar = point.getPhase();
            if (phaseChar != null) {
                monitorPoint.setPhase(String.valueOf(phaseChar));
            } else {
                monitorPoint.setPhase(Phase.A.name());
            }

            LitePoint p = getLitePoint(point.getPointId().intValue());
            monitorPoint.setPointName(p.getPointName());

            monitorPoint.setOverrideFdrLimits(point.isOverrideStrategySettings());
            monitorPointList.add(monitorPoint);
        }

        return monitorPointList;
    }

    @Override
    public boolean deviceHasPoint(int deviceId, int pointOffset, PointType pointType) {
        int pointId = getPointIDByDeviceID_Offset_PointType(deviceId, pointOffset, pointType);
        return pointId != 0;
    }

    @Override
    public SqlFragmentSource getAttributeLookupSql(Attribute attribute) {

        Map<PaoType, Map<Attribute, AttributeDefinition>> definitionMap =
            paoDefinitionDao.getPaoAttributeAttrDefinitionMap();

        SetMultimap<PointIdentifier, PaoType> typesByPointIdentifier = HashMultimap.create();
        for (Entry<PaoType, Map<Attribute, AttributeDefinition>> entry : definitionMap.entrySet()) {
            AttributeDefinition attributeDefinition = entry.getValue().get(attribute);

            if (attributeDefinition == null) {
                continue;
            }

            PointIdentifier pointIdentifier = attributeDefinition.getPointTemplate().getPointIdentifier();
            typesByPointIdentifier.put(pointIdentifier, entry.getKey());
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YPO.paObjectid, P.pointId");
        sql.append("FROM YukonPAObject YPO");
        sql.append("JOIN Point P ON YPO.paObjectId = P.paObjectId");

        SqlFragmentCollection orCollection = SqlFragmentCollection.newOrCollection();
        for (Entry<PointIdentifier, Collection<PaoType>> entry : typesByPointIdentifier.asMap().entrySet()) {
            SqlStatementBuilder clause1 = new SqlStatementBuilder();
            clause1.append("(");
            clause1.append("YPO.Type").in(entry.getValue());
            clause1.append("AND P.pointType").eq(entry.getKey().getPointType());
            clause1.append("AND P.pointOffset").eq_k(entry.getKey().getOffset());
            clause1.append(")");
            orCollection.add(clause1);
        }

        if (!orCollection.isEmpty()) {
            sql.append("WHERE").appendFragment(orCollection);
        }

        return sql;
    }

    /**
     * Add a version which limits the results. Used {@link RawPointHistoryDaoImpl} as multi-DB-engine example,
     * however had to add TOP to sqlb for it to be valid within MSSQL2008.
     *
     * @category YUK-11992
     * @since 5.6.4
     */
    @Override
    public SqlFragmentSource getAttributeLookupSqlLimit(Attribute attribute, int limitToRowCount) {

        Map<PaoType, Map<Attribute, AttributeDefinition>> definitionMap =
            paoDefinitionDao.getPaoAttributeAttrDefinitionMap();

        SetMultimap<PointIdentifier, PaoType> typesByPointIdentifier = HashMultimap.create();
        for (Entry<PaoType, Map<Attribute, AttributeDefinition>> entry : definitionMap.entrySet()) {
            AttributeDefinition attributeDefinition = entry.getValue().get(attribute);

            if (attributeDefinition == null) {
                continue;
            }

            PointIdentifier pointIdentifier = attributeDefinition.getPointTemplate().getPointIdentifier();
            typesByPointIdentifier.put(pointIdentifier, entry.getKey());
        }

        String orderByClause = "ORDER BY YPO.paObjectid ASC, P.pointId ASC";

        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
        SqlBuilder sqla = builder.buildFor(DatabaseVendor.getMsDatabases());
        sqla.append("SELECT TOP " + limitToRowCount + " YPO.paObjectid, P.pointId");
        sqla.append("FROM YukonPAObject YPO");
        sqla.append("JOIN Point P ON YPO.paObjectId = P.paObjectId");

        SqlBuilder sqlb = builder.buildOther();
        sqlb.append("SELECT * FROM (");
        sqlb.append("SELECT YPO.paObjectid, P.pointId, ROW_NUMBER() over (");
        sqlb.append(orderByClause);
        sqlb.append(") rn");
        sqlb.append("FROM YukonPAObject YPO");
        sqlb.append("JOIN Point P ON YPO.paObjectId = P.paObjectId");

        SqlFragmentCollection orCollection = SqlFragmentCollection.newOrCollection();
        for (Entry<PointIdentifier, Collection<PaoType>> entry : typesByPointIdentifier.asMap().entrySet()) {
            SqlStatementBuilder clause1 = new SqlStatementBuilder();
            clause1.append("(");
            clause1.append("YPO.Type").in(entry.getValue());
            clause1.append("AND P.pointType").eq_k(entry.getKey().getPointType());
            clause1.append("AND P.pointOffset").eq_k(entry.getKey().getOffset());
            clause1.append(")");
            orCollection.add(clause1);
        }

        if (!orCollection.isEmpty()) {
            sqla.append("WHERE").appendFragment(orCollection);
            sqlb.append("WHERE").appendFragment(orCollection);
        }
        sqlb.append(") numberedRows");
        sqlb.append("WHERE numberedRows.rn").lte(limitToRowCount);
        sqlb.append("ORDER BY numberedRows.rn");
        sqla.append(orderByClause);

        return builder;
    }
    
    @Override 
    public PointBase get(int id) {
        
        LitePoint litePoint = getLitePoint(id);
        PointBase pointBase = (PointBase) LiteFactory.createDBPersistent(litePoint);
        pointBase = (PointBase) dbPersistentDao.retrieveDBPersistent(pointBase);
        return pointBase;
    }
}