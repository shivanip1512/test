package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.pao.PaoCategory;
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
import com.cannontech.common.util.CtiUtilities;
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
import com.cannontech.database.YukonRowMapperAdapter;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.point.CapBankMonitorPointParams;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.capcontrol.CCMonitorBankList;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.enums.Phase;
import com.cannontech.yukon.IDatabaseCache;
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
    
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    @Autowired private YukonJdbcTemplate template;
    
    private static final Logger log = YukonLogManager.getLogger(PointDaoImpl.class);
    private JdbcOperations jdbcOps;
    private ChunkingSqlTemplate chunkingTemplate;
    private ChunkingMappedSqlTemplate chunkingMappedSqlTemplate;
    
    @PostConstruct
    public void init() {
        chunkingTemplate = new ChunkingSqlTemplate(template);
        chunkingMappedSqlTemplate = new ChunkingMappedSqlTemplate(template);
        jdbcOps = template.getJdbcOperations();
    }
    
    private final YukonRowMapper<LitePoint> litePointRowMapper = new YukonRowMapper<LitePoint>() {
        @Override
        public LitePoint mapRow(YukonResultSet rs) throws SQLException {
            return createLitePoint(rs);
        }
    };
    
    private static final SqlFragmentGenerator<Integer> paoPointFragmentGenerator = new SqlFragmentGenerator<Integer>() {
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
            SqlStatementBuilder sql = new SqlStatementBuilder(litePointSql);
            sql.append("WHERE PaobjectId").eq(pao.getPaoIdentifier().getPaoId());
            sql.append(  "AND UPPER(PointName)").eq(pointName.toUpperCase());
            
            return template.queryForObject(sql, litePointRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    

    @Override
    public LitePoint findPointByNameAndType(YukonPao pao, String pointName, PointType pointType) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder(litePointSql);
            sql.append("WHERE PaobjectId").eq(pao.getPaoIdentifier().getPaoId());
            sql.append(  "AND UPPER(PointName)").eq(pointName.toUpperCase());
            sql.append(  "AND PointType").eq(pointType);
            
            return template.queryForObject(sql, litePointRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public LitePoint getLitePoint(int pointId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder(litePointSql);
            sql.append("WHERE P.POINTID").eq(pointId);
            
            return template.queryForObject(sql, litePointRowMapper);
            
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
        
        PaoPointIdentifier result = template.queryForObject(sql, new YukonRowMapper<PaoPointIdentifier>() {
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
                sql.append(litePointSql);
                sql.append("where p.pointid").in(subList);
                return sql;
            }
        };
        
        return chunkingTemplate.query(sqlGenerator, pointIds, litePointRowMapper);
    }

    @Override
    public Map<LitePoint, PaoPointIdentifier> getLitePointsForPaoPointIdentifiers(Iterable<PaoPointIdentifier> paoPointIdentifiers) {
        
        Map<LitePoint, PaoPointIdentifier> retVal = Maps.newHashMap();

        ImmutableMultimap<PointIdentifier, PaoIdentifier> paoPointIdentifiersMap =
            PaoUtils.mapPaoPointIdentifiers(paoPointIdentifiers);
        for (final PointIdentifier pointIdentifier : paoPointIdentifiersMap.keySet()) {
            ImmutableCollection<PaoIdentifier> paoIdentifiers =
                paoPointIdentifiersMap.get(pointIdentifier);

            SqlFragmentGenerator<Integer> sqlGenerator =
                new SqlFragmentGenerator<Integer>() {
                    @Override
                    public SqlFragmentSource generate(List<Integer> subList) {
                        SqlStatementBuilder sql = new SqlStatementBuilder();
                        sql.append(litePaoPointSql);
                        sql.append("WHERE P.PointType").eq_k(pointIdentifier.getPointType());
                        sql.append(  "AND P.PointOffset").eq_k(pointIdentifier.getOffset());
                        sql.append(  "AND P.PaobjectId").in(subList);
                        return sql;
                    }
                };

            final Function<PaoIdentifier, Integer> paoIdFunction = PaoUtils.getPaoIdFunction();
            Map<PaoIdentifier, LitePoint> litePointsForPoint =
                chunkingMappedSqlTemplate.mappedQuery(sqlGenerator, paoIdentifiers, new PointIdToLitePointRowMapper(), paoIdFunction);

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
        for(int i = 0; i < count; i++) {
            ids[i] = getNextPointId();
        }
        return ids;
    }

    @Override
    public List<LitePoint> getLitePointsBy(Integer[] pointTypes, 
            Integer[] uomIDs, 
            Integer[] paoTypes, 
            Integer[] paoCategories, 
            Integer[] paoClasses) {
        
        StringBuilder sql = new StringBuilder(litePaoPointSql);
    
        pointTypes = CtiUtilities.ensureNotNull(pointTypes);
        uomIDs = CtiUtilities.ensureNotNull(uomIDs);
        paoTypes = CtiUtilities.ensureNotNull(paoTypes);
        paoCategories = CtiUtilities.ensureNotNull(paoCategories);
        paoClasses = CtiUtilities.ensureNotNull(paoClasses);
        
        String[] pointTypesStr = PointTypes.convertPointTypes(pointTypes);
        String[] paoTypesStr = PaoType.convertPaoTypes(paoTypes);
        String[] paoCategoriesStr = PaoCategory.convertPaoCategories(paoCategories);
        String[] paoClassesStr = DeviceClasses.convertPaoClasses(paoClasses);
        
        SqlUtil.buildInClause("where", "p", "pointtype", pointTypesStr, sql);
        SqlUtil.buildInClause("and", "pu", "uomid", uomIDs, sql);
        SqlUtil.buildInClause("and", "ypo", "type", paoTypesStr, sql);
        SqlUtil.buildInClause("and", "ypo", "cateogry", paoCategoriesStr, sql);
        SqlUtil.buildInClause("and", "ypo", "paoclass", paoClassesStr, sql);
        
        List<LitePoint> points = jdbcOps.query(sql.toString(), new YukonRowMapperAdapter<LitePoint>(litePointRowMapper));
        
        return points;
    }
    
    @Override
    public List<LitePoint> getLitePointsByNumStates(int numberOfStates) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder(litePointSql);
        sql.append("where P.StateGroupId in (");
        sql.append("select StateGroupId from State");
        sql.append("group by StateGroupId having count(RawState)").eq(numberOfStates);
        sql.append(")");
        
        List<LitePoint> points = template.query(sql, litePointRowMapper);
        
        return points;
    }

    @Override
    public List<LitePoint> getLitePointsByPaObjectId(int paobjectId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder(litePointSql);
        sql.append("where PAObjectId").eq(paobjectId);
        List<LitePoint> points = template.query(sql, litePointRowMapper);
        
        return points;
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
    public String getPointName(int pointId) {
        return getLitePoint(pointId).getPointName();
    }

    @Override
    public LitePointLimit getPointLimit(int pointId) {
        
        synchronized (databaseCache) {
            Iterator<LitePointLimit> iter = databaseCache.getAllPointLimits().iterator();
            while (iter.hasNext()) {
                LitePointLimit lpl = iter.next();
                if (lpl.getPointID() == pointId) {
                    return lpl;
                }
            }
        }    
        
        throw new NotFoundException("PointLimit for point with id " + pointId + "cannot be found.");
    }

    @Override
    public LitePointUnit getPointUnit(int pointId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select PointId, UomId, DecimalPlaces");
        sql.append("from PointUnit");
        sql.append("where PointId").eq(pointId);
        
        try {  
            LitePointUnit lpu = template.queryForObject(sql, new YukonRowMapper<LitePointUnit>() {
                @Override
                public LitePointUnit mapRow(YukonResultSet rs) throws SQLException {
                    
                    int pointID = rs.getInt("PointId");
                    int uomID = rs.getInt("UomId");
                    int decimalPlaces = rs.getInt("DecimalPlaces");

                    LitePointUnit lpu = new LitePointUnit(pointID, uomID, decimalPlaces);
                    return lpu;
                };
            });
            return lpu;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Pointunit for point with id " + pointId + " cannot be found.");
        }
    }
    
    @Override
    public LiteStateGroup getStateGroup(int stateGroupId) {
        return databaseCache.getAllStateGroupMap().get(stateGroupId);
    }
    
    @Override
    public LitePoint getLitePoint(PaoPointIdentifier paoPointIdentifier) {
        
        int paoId = paoPointIdentifier.getPaoIdentifier().getPaoId();
        int offset = paoPointIdentifier.getPointIdentifier().getOffset();
        PointType pointType = paoPointIdentifier.getPointIdentifier().getPointType();
        
        return getLitePointIdByDeviceId_Offset_PointType(paoId, offset, pointType.getPointTypeId());
    }

    /**
     * Build a PointInfo object from the result set reuse items from the alreadyCreated map if
     * possible.
     */
    private static PointInfo createPointInfo(Map<PointInfo, 
            PointInfo> alreadyCreated,
            YukonResultSet rs) throws SQLException {
        
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
        
        private Map<PointInfo, PointInfo> alreadyCreated = Maps.newHashMap();

        @Override
        public Map.Entry<Integer, PointInfo> mapRow(YukonResultSet rs) throws SQLException {
            
            int paoId = rs.getInt("PaobjectId");
            PointInfo pointInfo = createPointInfo(alreadyCreated, rs);
            
            return Maps.immutableEntry(paoId, pointInfo);
        }
    }
    
    private static class PointIdToPointInfoRowMapper implements YukonRowMapper<Map.Entry<Integer, PointInfo>> {
        
        private Map<PointInfo, PointInfo> alreadyCreated = Maps.newHashMap();
        
        @Override
        public Map.Entry<Integer, PointInfo> mapRow(YukonResultSet rs) throws SQLException {
            
            int pointId = rs.getInt("PointId");
            PointInfo pointInfo = createPointInfo(alreadyCreated, rs);
            
            return Maps.immutableEntry(pointId, pointInfo);
        }
    }
    
    private class PointIdToLitePointRowMapper implements YukonRowMapper<Map.Entry<Integer, LitePoint>> {
        
        private Map<LitePoint, LitePoint> alreadyCreated = Maps.newHashMap();

        @Override
        public Map.Entry<Integer, LitePoint> mapRow(YukonResultSet rs) throws SQLException {
            
            int paoId = rs.getInt("PaobjectId");
            LitePoint litePoint = createLitePoint(alreadyCreated, rs);
            
            return Maps.immutableEntry(paoId, litePoint);
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
                        sql.append(  "P.PointOffset, P.StateGroupId, UM.UomName");
                        sql.append("FROM Point P");
                        sql.append(  "LEFT JOIN PointUnit PU ON P.PointId = PU.PointId");
                        sql.append(  "LEFT JOIN UnitMeasure UM ON PU.UomId = UM.UomId");
                        sql.append("WHERE P.PointType").eq_k(pointIdentifier.getPointType());
                        sql.append(  "AND P.PointOffset").eq_k(pointIdentifier.getOffset());
                        sql.append(  "AND P.PaobjectId").in(subList);
                        
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
                sql.append(  "P.PointOffset, P.StateGroupId, UM.UomName");
                sql.append("FROM Point P");
                sql.append(  "LEFT JOIN PointUnit PU ON P.PointId = PU.PointId");
                sql.append(  "LEFT JOIN UnitMeasure UM ON PU.UomId = UM.UomId");
                sql.append("WHERE P.PointId").in(subList);
                
                return sql;
            }
        };
        
        Function<Integer, Integer> identityFunc = Functions.identity();
        return chunkingMappedSqlTemplate.mappedQuery(sqlGenerator, pointIds, new PointIdToPointInfoRowMapper(), identityFunc);
    }

    @Override
    public Map<PaoIdentifier, PointInfo> getPointInfoByPointName(Iterable<PaoIdentifier> paoIdentifiers,
                                                                 final String pointName) {
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> integerPaoIds) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT P.PointId, P.PointName, P.PointType, P.PaobjectId,");
                sql.append(  "P.PointOffset, P.StateGroupId, UM.UomName");
                sql.append("FROM Point P");
                sql.append(  "LEFT JOIN PointUnit PU ON P.PointId = PU.PointId");
                sql.append(  "LEFT JOIN UnitMeasure UM ON PU.UomId = UM.UomId");
                sql.append("WHERE P.PointName").eq(pointName);
                sql.append(  "AND P.PaobjectId").in(integerPaoIds);
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
                    sql.append(  "P.PointOffset, P.StateGroupId, UM.UomName");
                    sql.append("FROM Point P");
                    sql.append(  "LEFT JOIN PointUnit PU ON P.PointId = PU.PointId");
                    sql.append(  "LEFT JOIN UnitMeasure UM ON PU.UomId = UM.UomId");
                    sql.append("WHERE p.PointOffset").eq_k(pointIdentifier.getOffset());
                    sql.append(  "AND p.PointType").eq_k(pointIdentifier.getPointType());
                    sql.append(  "AND P.PaobjectId").in(integerPaoIds);
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
                sql.append(  "P.PointOffset, P.StateGroupId, UM.UomName");
                sql.append("FROM Point P");
                sql.append(  "LEFT JOIN PointUnit PU ON P.PointId = PU.PointId");
                sql.append(  "LEFT JOIN UnitMeasure UM ON PU.UomId = UM.UomId");
                sql.append("WHERE P.PointType").eq_k(pointIdentifier.getPointType());
                sql.append(  "AND P.PointOffset").eq_k(pointIdentifier.getOffset());
                sql.append(  "AND P.PaobjectId").in(integerPaoIds);
                
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
            int pointId = template.queryForInt(sql);
            return pointId;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("unable to find pointId for " + paoPointIdentifier, e);
        }
    }
    
    @Override
    public Map<Integer, Integer> getPointIdsForPaosAndAttribute(Attribute attribute, Iterable<Integer> paoIds) {
        
        final SqlFragmentSource attributeLookupSql = getAttributeLookupSql(attribute);
        
        SqlFragmentGenerator<Integer> sqlFragmentGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.appendFragment(attributeLookupSql);
                sql.append("AND YPO.paObjectId").in(subList);
                return sql;
            }
        };
        
        List<int[]> pointPaoPairs = chunkingTemplate.query(sqlFragmentGenerator, paoIds, new YukonRowMapper<int[]>() {
            public int[] mapRow(YukonResultSet rs) throws SQLException {
                
                int paObjectId = rs.getInt("PaObjectId");
                int pointId = rs.getInt("PointId");
                
                return new int[] {pointId, paObjectId};
            }
        });
        Map<Integer, Integer> pointsToPao = Maps.newHashMap();
        for (int[] pair : pointPaoPairs) {
            pointsToPao.put(pair[0], pair[1]);
        }
        return pointsToPao;
    }
    
    @Override
    public Multimap<Integer, Integer> getPaoPointMultimap(Iterable<Integer> paoIds) {
        List<int[]> paoPointPairs = chunkingTemplate.query(paoPointFragmentGenerator, paoIds, new YukonRowMapper<int[]>() {
            public int[] mapRow(YukonResultSet rs) throws SQLException {
                int paObjectId = rs.getInt("PaObjectId");
                int pointId = rs.getInt("PointId");
                return new int[] {paObjectId, pointId};
            }
        });
        Multimap<Integer, Integer> paoToPoints = ArrayListMultimap.create();
        for (int[] pair : paoPointPairs) {
            paoToPoints.put(pair[0], pair[1]);
        }
        return paoToPoints;
    }
    
    @Override
    public Map<Integer, Integer> getPointIdsForPaos(Iterable<Integer> paoIds) {
        List<int[]> pointPaoPairs = chunkingTemplate.query(paoPointFragmentGenerator, paoIds, new YukonRowMapper<int[]>() {
            public int[] mapRow(YukonResultSet rs) throws SQLException {
                int paObjectId = rs.getInt("PaObjectId");
                int pointId = rs.getInt("PointId");
                return new int[] {pointId, paObjectId};
            }
        });
        Map<Integer, Integer> pointsToPao = Maps.newHashMap();
        for (int[] pair : pointPaoPairs) {
            pointsToPao.put(pair[0], pair[1]);
        }
        return pointsToPao;
    }
    
    @Override
    public List<PointBase> getPointsForPao(int paoId) {
        
        List<LitePoint> litePoints = getLitePointsByPaObjectId(paoId);
        List<PointBase> points = new ArrayList<PointBase>(litePoints.size());
        
        for (LitePoint litePoint: litePoints) {
            
            PointBase pointBase = (PointBase)LiteFactory.createDBPersistent(litePoint);
            
            try {
                dbPersistentDao.retrieveDBPersistent(pointBase);
                points.add(pointBase);
            } catch (PersistenceException e) {
                throw new DeviceCreationException("Could not retrieve points for new device.", e);
            }
        }

        return points;
    }

    @Override
    public int getPointIDByDeviceID_Offset_PointType(int deviceId, int pointOffset, int pointType) {
        try {
            LitePoint point = getLitePointIdByDeviceId_Offset_PointType(deviceId, pointOffset, pointType);
            return point.getPointID();
        } catch (NotFoundException e) {
            return PointTypes.SYS_PID_SYSTEM; //not found
        }
    }

    @Override
    public LitePoint getLitePointIdByDeviceId_Offset_PointType(int paobjectId, int pointOffset, int pointType) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder(litePointSql);
        sql.append("where PAObjectId").eq(paobjectId);
        sql.append("and PointOffset").eq(pointOffset);
        sql.append("and PointType").eq(PointTypes.getType(pointType));
        try {
            return template.queryForObject(sql, litePointRowMapper);
        } catch (Exception e) {
            throw new NotFoundException("Unable to find point for deviceId=" + paobjectId + ", pointOffset=" + pointOffset + ", pointType=" + pointType);
        }
    }
    
    @Override
    public List<LitePoint> getLitePointIdByDeviceId_PointType(int paobjectId, int pointType) throws NotFoundException {
        
        SqlStatementBuilder sql = new SqlStatementBuilder(litePointSql);
        sql.append("where PAObjectId").eq(paobjectId);
        sql.append("and PointType").eq(PointTypes.getType(pointType));
        try {
            List<LitePoint> pointList = template.query(sql, litePointRowMapper);  
            return pointList;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unable to find point for deviceId=" + paobjectId + ", pointType=" + pointType);
        }
    }
    
    @Override
    public List<LiteRawPointHistory> getPointData(int pointId, Date startDate, Date stopDate) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ChangeId, PointId, Timestamp, Quality, Value");
        sql.append("from RawPointHistory");
        sql.append("where PointId").eq(pointId);
        
        if (startDate != null) {
            sql.append("and Timestamp").gt(startDate);
        }
        
        if (stopDate != null) {
            sql.append("and Timestamp").lte(stopDate);
        }
        sql.append("order by Timestamp");
        
        try {
            log.info("Retrieve PointDate for ID: " + pointId + 
                           "  - START DATE > " + (startDate != null ? startDate:"---") +
                           "  -  STOP DATE <= " + (stopDate!= null ? stopDate:"---") );
            List<LiteRawPointHistory> history = template.query(sql, new YukonRowMapper<LiteRawPointHistory>() {
                @Override
                public LiteRawPointHistory mapRow(YukonResultSet rs) throws SQLException {
                    long changeID = rs.getLong("ChangeId");
                    int pointID = rs.getInt("PointId");
                    Instant timestamp = rs.getInstant("Timestamp");
                    int quality = rs.getInt("Quality");
                    double value = rs.getDouble("Value");
                    
                    LiteRawPointHistory lrph = new LiteRawPointHistory( changeID, pointID, timestamp.getMillis(), quality, value);
                    return lrph;
                }
                
            });
            
            return history;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No pointdata retrieved for pointID: " + pointId);
        }
    }

    @Override
    public List<CapBankMonitorPointParams> getCapBankMonitorPoints(CapBank capBank) {
        
        List<CapBankMonitorPointParams> monitorPointList = new ArrayList<CapBankMonitorPointParams>();
        
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
    public List<LitePoint> searchByName(final String name, final String paoClass) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder(litePaoPointSql);
        sql.append("where YPO.PAOClass").eq(paoClass);
        sql.append("and upper(PointName) like").appendArgument("%" + name.toUpperCase() + "%");
        
        List<LitePoint> pointList = template.query(sql, litePointRowMapper);
        return pointList;
    }
    
    @Override
    public boolean deviceHasPoint(int deviceId, int pointOffset, int pointType) {
        int pointId = getPointIDByDeviceID_Offset_PointType(deviceId, pointOffset, pointType);
        return pointId != 0;
    }
    
    @Override
    public SqlFragmentSource getAttributeLookupSql(Attribute attribute) {
        
        Map<PaoType, Map<Attribute, AttributeDefinition>> definitionMap = paoDefinitionDao.getPaoAttributeAttrDefinitionMap();

        SetMultimap<PointIdentifier, PaoType> typesByPointIdentifier = HashMultimap.create();
        for (Entry<PaoType, Map<Attribute, AttributeDefinition>> entry : definitionMap.entrySet()) {
            AttributeDefinition attributeDefinition = entry.getValue().get(attribute);
            
            if (attributeDefinition == null) continue;
            
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
     * Add a version which limits the results.  Used {@link RawPointHistoryDaoImpl} as multi-DB-engine example,
     * however had to add TOP to sqlb for it to be valid within MSSQL2008.
     * 
     * @category    YUK-11992
     * @since       5.6.4
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
        SqlBuilder sqla = builder.buildFor(DatabaseVendor.MS2000);
        sqla.append("SELECT TOP " + limitToRowCount + " YPO.paObjectid, P.pointId");
        sqla.append("FROM YukonPAObject YPO");
        sqla.append("JOIN Point P ON YPO.paObjectId = P.paObjectId");
        sqla.append(orderByClause);

        SqlBuilder sqlb = builder.buildOther();
        sqlb.append("select TOP " + limitToRowCount + " * from (");
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

        return builder;
    }
    
    private LitePoint createLitePoint(Map<LitePoint, 
            LitePoint> alreadyCreated, 
            YukonResultSet rset) throws SQLException {
        
        LitePoint litePoint = createLitePoint(rset);
        LitePoint retVal = alreadyCreated.get(litePoint);
        if (retVal == null) {
            alreadyCreated.put(litePoint, litePoint);
            retVal = litePoint;
        }
        return retVal;
    }

    @Override
    public LitePoint createLitePoint(YukonResultSet rs) throws SQLException {
        
        int pointId = rs.getInt("PointId");
        String pointName = rs.getString("PointName").trim();
        PointType pointType = rs.getEnum("PointType", PointType.class);
        int paobjectId = rs.getInt("PaobjectId");
        int pointOffset = rs.getInt("PointOffset");
        int stateGroupId = rs.getInt("StateGroupId");
        String formula = rs.getString("Formula");
        int uomId = rs.getInt("uomId");

        if (rs.wasNull()) { // if uomid is null, set it to an INVALID int
            uomId = UnitOfMeasure.INVALID.getId();
        }

        // process all the bit mask tags here
        long tags = LitePoint.POINT_UOFM_GRAPH;
        if ("usage".equalsIgnoreCase(formula)) {
            tags = LitePoint.POINT_UOFM_USAGE;
        }

        LitePoint lp = new LitePoint(pointId, pointName, 
                pointType.getPointTypeId(), 
                paobjectId,
                pointOffset, 
                stateGroupId, 
                tags, 
                uomId);

        if (pointType == PointType.Analog) {
            lp.setMultiplier(rs.getNullableDouble("analogMultiplier"));
            lp.setDataOffset(rs.getNullableDouble("analogOffset"));
        } else if (pointType == PointType.PulseAccumulator || pointType == PointType.DemandAccumulator) {
            lp.setMultiplier(rs.getNullableDouble("accumulatorMultiplier"));
            lp.setDataOffset(rs.getNullableDouble("accumulatorOffset"));
        }

        return lp;
    }

}