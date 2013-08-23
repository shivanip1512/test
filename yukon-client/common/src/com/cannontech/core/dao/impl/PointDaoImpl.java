package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.clientutils.CTILogger;
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
import com.cannontech.common.util.Pair;
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
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.enums.Phase;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

/**
 * Implementation of PointDao
 * Creation date: (7/01/2006 9:40:33 AM)
 * @author: alauinger
 */
public class PointDaoImpl implements PointDao {
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    private JdbcOperations jdbcOps;
    private ChunkingSqlTemplate chunkingSqlTemplate;
    private ChunkingMappedSqlTemplate chunkingMappedSqlTemplate;
    
    @PostConstruct
    public void init() {
        chunkingSqlTemplate = new ChunkingSqlTemplate(yukonJdbcTemplate);
        chunkingMappedSqlTemplate = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
        jdbcOps = yukonJdbcTemplate.getJdbcOperations();
    }
    
    private static final String litePointSql = 
        "SELECT P.POINTID, POINTNAME, POINTTYPE, P.PAOBJECTID, POINTOFFSET, STATEGROUPID, UM.FORMULA, UM.UOMID" +
        " FROM " +         
        "( POINT P LEFT OUTER JOIN POINTUNIT PU ON P.POINTID = PU.POINTID " +
        "LEFT OUTER JOIN UNITMEASURE UM ON PU.UOMID = UM.UOMID ) ";

    private static final String litePointPaoSql = 
        "SELECT P.POINTID, POINTNAME, POINTTYPE, YPO.PAOBJECTID, POINTOFFSET, STATEGROUPID, UM.FORMULA, UM.UOMID" +
        " FROM " +    
        "( POINT P LEFT OUTER JOIN POINTUNIT PU ON P.POINTID = PU.POINTID " +
        "LEFT OUTER JOIN UNITMEASURE UM ON PU.UOMID = UM.UOMID " +
        "LEFT OUTER JOIN YUKONPAOBJECT YPO ON P.PAOBJECTID=YPO.PAOBJECTID ) ";
    
    private static final YukonRowMapper<LitePoint> litePointYukonRowMapper =
        new YukonRowMapper<LitePoint>() {
            @Override
            public LitePoint mapRow(YukonResultSet rs) throws SQLException {
                return createLitePoint(rs);
            }
        };
    private static final ParameterizedRowMapper<LitePoint> litePointRowMapper =
        new YukonRowMapperAdapter<LitePoint>(litePointYukonRowMapper);

    private static final RowMapper<LitePointUnit> litePointUnitRowMapper = new RowMapper<LitePointUnit>() {
        @Override
        public LitePointUnit mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createLitePointUnit(rs);
        };
    };

    private static final RowMapper<LiteRawPointHistory> litePointHistoryRowMapper = new RowMapper<LiteRawPointHistory>() {
        @Override
        public LiteRawPointHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createLitePointHistory(rs);
        };
    };
    
    @Override
    public LitePoint findPointByName(YukonPao pao, String pointName) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder(litePointSql);
            sql.append("WHERE PaobjectId").eq(pao.getPaoIdentifier().getPaoId());
            sql.append(  "AND UPPER(PointName)").eq(pointName.toUpperCase());
            
            return yukonJdbcTemplate.queryForObject(sql, litePointRowMapper);
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
            
            return yukonJdbcTemplate.queryForObject(sql, litePointRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getLitePoint(int)
     */
    @Override
    public LitePoint getLitePoint(int pointID) {
        try {
            String sql = litePointSql + " WHERE P.POINTID = ?";
            LitePoint p = jdbcOps.queryForObject(sql, new Object[] { pointID }, PointDaoImpl.litePointRowMapper);
            
            return p;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A point with id " + pointID + " cannot be found.");
        }
    }

    @Override
    public PaoPointIdentifier getPaoPointIdentifier(int pointId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select PointOffset, PointType, p.PAObjectId, pao.Type ");
        sql.append("from Point p");
        sql.append("join YukonPAObject pao on p.PAObjectId = pao.PAObjectId");
        sql.append("where p.PointId = ").appendArgument(pointId);
        
        PaoPointIdentifier result = yukonJdbcTemplate.queryForObject(sql, new ParameterizedRowMapper<PaoPointIdentifier>() {
            @Override
            public PaoPointIdentifier mapRow(ResultSet rs, int rowNum) throws SQLException {
                PaoType paoType = PaoType.getForDbString(rs.getString("Type"));
                int paoId = rs.getInt("PAObjectId");
                PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, paoType);
                
                String pointType = rs.getString("PointType");
                int pointOffset = rs.getInt("PointOffset");
                PointIdentifier pointIdentifier = new PointIdentifier(PointType.getForString(pointType), pointOffset);
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
        return chunkingSqlTemplate.query(sqlGenerator, pointIds, litePointRowMapper);
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
                        sql.append(litePointPaoSql);
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

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getLitePointsByUOMID(int[], int[])
     */
    @Override
    public List<LitePoint> getLitePointsBy(Integer[] pointTypes, Integer[] uomIDs, Integer[] paoTypes, Integer[] paoCategories, Integer[] paoClasses) {
        StringBuilder sql = new StringBuilder(litePointPaoSql);
    
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
        
        List<LitePoint> points = 
           jdbcOps.query(sql.toString(), PointDaoImpl.litePointRowMapper);
       return points;
    }
    
    @Override
    public List<LitePoint> getLitePointsByNumStates(int numberOfStates) {
        String sql = litePointSql + 
            " WHERE P.STATEGROUPID IN (select stategroupid from state group by stategroupid having count(rawstate)=?) ";
        
        List<LitePoint> points = 
            jdbcOps.query(sql, new Object[] { numberOfStates }, PointDaoImpl.litePointRowMapper);
        return points;
    }

    @Override
    public List<LitePoint> getLitePointsByPaObjectId(int paObjectId) {
        String sql = litePointSql +
        " WHERE PaObjectId = ? ";
        List<LitePoint> points = 
            jdbcOps.query(sql, new Object[] { paObjectId }, PointDaoImpl.litePointRowMapper);
        return points;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.PaoDao#getAllPointIDsAndTypesForPAObject(int)
     */
    // the format returned is :   
    //          int[X][0] == id
    //          int[X][1] == lite type
    @Override
    public int[][] getAllPointIDsAndTypesForPAObject( int deviceid ) {        
        List<LitePoint> points = getLitePointsByPaObjectId(deviceid);
        int[][] idAndTypes = new int[points.size()][2];
        for(int i = 0; i < points.size(); i++) {
            idAndTypes[i][0] = points.get(i).getPointID();
            idAndTypes[i][1] = points.get(i).getPointType();
        }
        return idAndTypes;
    }
    
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getPointName(int)
     */
	@Override
    public String getPointName(int pointId) {
	    LitePoint p = getLitePoint(pointId);
        return p.getPointName();
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getPointLimit(int)
     */
	@Override
    public LitePointLimit getPointLimit(int pointID) {
		synchronized(databaseCache) {
			Iterator<LitePointLimit> iter = databaseCache.getAllPointLimits().iterator();
			while(iter.hasNext()) {
				LitePointLimit lpl = iter.next();
				if( lpl.getPointID() == pointID ) {
					return lpl;
				}
			}
		}	
		
    
        throw new NotFoundException("PointLimit for point with id " + pointID + "cannot be found.");

	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getPointUnit(int)
     */
	@Override
    public LitePointUnit getPointUnit(int pointID) {
        String sql = 
            "SELECT POINTID,UOMID,DECIMALPLACES FROM POINTUNIT WHERE POINTID=?";
      try {  
        LitePointUnit lpu = jdbcOps.queryForObject(sql, new Object[] { pointID }, litePointUnitRowMapper);
        return lpu;
     } catch (IncorrectResultSizeDataAccessException e) {
         throw new NotFoundException("Pointunit for point with id " + pointID + " cannot be found.");
     }
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getStateGroup(int)
     */
	@Override
    public LiteStateGroup getStateGroup( int stateGroupID ) {
		return databaseCache.getAllStateGroupMap().get( new Integer(stateGroupID) );
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
    private static PointInfo createPointInfo(Map<PointInfo, PointInfo> alreadyCreated,
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
        public Map.Entry<Integer, PointInfo> mapRow(YukonResultSet rs)
                throws SQLException {
            int paoId = rs.getInt("PaobjectId");
            PointInfo pointInfo = createPointInfo(alreadyCreated, rs);
            return Maps.immutableEntry(paoId, pointInfo);
        }
    }
    
    private static class PointIdToPointInfoRowMapper implements YukonRowMapper<Map.Entry<Integer, PointInfo>> {
        private Map<PointInfo, PointInfo> alreadyCreated = Maps.newHashMap();
        
        @Override
        public Map.Entry<Integer, PointInfo> mapRow(YukonResultSet rs)
                throws SQLException {
            int pointId = rs.getInt("PointId");
            PointInfo pointInfo = createPointInfo(alreadyCreated, rs);
            return Maps.immutableEntry(pointId, pointInfo);
        }
    }
    
    private static class PointIdToLitePointRowMapper implements YukonRowMapper<Map.Entry<Integer, LitePoint>> {
        private Map<LitePoint, LitePoint> alreadyCreated = Maps.newHashMap();

        @Override
        public Map.Entry<Integer, LitePoint> mapRow(YukonResultSet rs)
                throws SQLException {
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
            ImmutableCollection<PaoIdentifier> paoIdentifiers =
                paoPointIdentifiersMap.get(pointIdentifier);

            SqlFragmentGenerator<Integer> sqlGenerator =
                new SqlFragmentGenerator<Integer>() {
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
        SqlFragmentGenerator<Integer> sqlGenerator =
                new SqlFragmentGenerator<Integer>() {
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
	        int pointId = yukonJdbcTemplate.queryForInt(sql);
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
        
        List<Pair<Integer, Integer>> pointPaoPairs = chunkingSqlTemplate.query(sqlFragmentGenerator, paoIds, new YukonRowMapper<Pair<Integer, Integer>>() {
            public Pair<Integer, Integer> mapRow(YukonResultSet rs) throws SQLException {
                int paObjectId = rs.getInt("PaObjectId");
                int pointId = rs.getInt("PointId");
                return new Pair<Integer, Integer>(pointId, paObjectId);
            }
        });
        Map<Integer, Integer> pointsToPao = Maps.newHashMap();
        for(Pair<Integer, Integer> pair : pointPaoPairs) {
            pointsToPao.put(pair.getFirst(), pair.getSecond());
        }
        return pointsToPao;
    }
    
    @Override
    public Map<Integer, Integer> getPointIdsForPaos(Iterable<Integer> paoIds) {
        SqlFragmentGenerator<Integer> sqlFragmentGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT PaObjectId, PointId");
                sql.append("FROM Point");
                sql.append("WHERE PaObjectId").in(subList);
                return sql;
            }
        };
        
        YukonRowMapper<Map.Entry<Integer, Integer>> pointToPaoRowMapper = new YukonRowMapper<Map.Entry<Integer, Integer>>() {
            public Map.Entry<Integer, Integer> mapRow(YukonResultSet rs) throws SQLException {
                int paObjectId = rs.getInt("PaObjectId");
                int pointId = rs.getInt("PointId");
                return Maps.immutableEntry(pointId, paObjectId);
            }
        };
        Function<Integer, Integer> identityFunction = Functions.identity();
        Map<Integer, Integer> pointsToPao = chunkingMappedSqlTemplate.mappedQuery(sqlFragmentGenerator, paoIds, pointToPaoRowMapper, identityFunction);
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
            }
            catch (PersistenceException e) {
                throw new DeviceCreationException("Could not retrieve points for new device.", e);
            }
        }

        return points;
    }

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getPointIDByDeviceID_Offset_PointType(int, int, int)
     */
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
    public LitePoint getLitePointIdByDeviceId_Offset_PointType(int deviceId, int pointOffset, int pointType) {
	    final String sqlString = litePointSql + " WHERE PaObjectId = ? AND POINTOFFSET = ? AND POINTTYPE = ?";
        try {
            return jdbcOps.queryForObject(sqlString,
                                          new Object[]{deviceId, pointOffset, PointTypes.getType(pointType)},
                                          PointDaoImpl.litePointRowMapper);   
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unable to find point for deviceId=" + deviceId + ", pointOffset=" + pointOffset + ", pointType=" + pointType);
        }
	}
    
    @Override
    public List<LitePoint> getLitePointIdByDeviceId_PointType(int deviceId, int pointType) throws NotFoundException {
        final String sqlString = litePointSql + " WHERE PaObjectId = ? AND POINTTYPE = ?";
        try {
            List<LitePoint> pointList = jdbcOps.query(sqlString,
                                                      new Object[]{deviceId, PointTypes.getType(pointType)},
                                                      PointDaoImpl.litePointRowMapper);  
            return pointList;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unable to find point for deviceId=" + deviceId + ", pointType=" + pointType);
        }
    }
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#retrievePointData(int)
     */
    @Override
    public List<LiteRawPointHistory> getPointData(int pointID, Date startDate, Date stopDate)
	{
        try {
            int numArgs = 1;
            String sqlString = "SELECT CHANGEID, POINTID, TIMESTAMP, QUALITY, VALUE " + 
                               " FROM " + RawPointHistory.TABLE_NAME +
                               " WHERE POINTID = ? ";
                               if (startDate != null){
                                   sqlString += " AND TIMESTAMP > ? ";
                                   numArgs++;
                               }
                               if (stopDate != null){
                                   sqlString += " AND TIMESTAMP <= ? ";
                                   numArgs++;
                               }
                               sqlString += " ORDER BY TIMESTAMP";
            int index = 0;                   
            Object[] args = new Object[numArgs];
            args[index++] = pointID;
            if( startDate != null)
                args[index++] = startDate;
            if( stopDate != null)
                args[index++] = stopDate;
            CTILogger.info("Retrieve PointDate for ID: " + pointID + 
                           "  - START DATE > " + (startDate != null ? startDate:"---") +
                           "  -  STOP DATE <= " + (stopDate!= null ? stopDate:"---") );
            List<LiteRawPointHistory> lrphList = jdbcOps.query(sqlString, args, litePointHistoryRowMapper);
            return lrphList;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No pointdata retrieved for pointID: " + pointID);
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
        String sql = litePointPaoSql + " WHERE YPO.PAOClass = ? AND UPPER(POINTNAME) LIKE ?";
        List<LitePoint> pointList = this.jdbcOps.query(
                                                       sql,
                                                       new Object[]{paoClass, "%" + name.toUpperCase() + "%"},
                                                       litePointRowMapper);
        return pointList;
    }
    
    @Override
    public int getPointDataOffset(LitePoint litePoint) {
        int pointId = litePoint.getPointID();
        int pointType = litePoint.getPointType();

        if (pointType == PointTypes.ANALOG_POINT) {
            return getAnalogPointDataOffset(pointId);
        } else if (pointType == PointTypes.PULSE_ACCUMULATOR_POINT || pointType == PointTypes.DEMAND_ACCUMULATOR_POINT) {
            return getAccumulatorPointDataOffset(pointId);
        } else {
            throw new NotFoundException( "Point DataOffset not found for Id: " + pointId + " and Type " + pointType + ".");
        }
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

            if (attributeDefinition == null)
                continue;

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
    
    private int getAccumulatorPointDataOffset(int pointId) {
        try {
            String sql = "select dataoffset from pointaccumulator where pointid=?";
            return jdbcOps.queryForInt(sql, new Object[] {pointId});
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException( "Accumulator Point DataOffset not found for Id: " + pointId + ".");
        }
    }
    
    private int getAnalogPointDataOffset(int pointId) {
        try {
            String sql = "select dataoffset from pointanalog where pointid=?";
            return jdbcOps.queryForInt(sql, new Object[] {pointId});
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException( "Analog Point DataOffset not found for Id: " + pointId + ".");
        }
    }
   
    
    @Override
    public double getPointMultiplier(LitePoint litePoint) {
        int pointId = litePoint.getPointID();
        int pointType = litePoint.getPointType();

        if (pointType == PointTypes.ANALOG_POINT) {
            return getAnalogPointMultiplier(pointId);
        } else if (pointType == PointTypes.PULSE_ACCUMULATOR_POINT || pointType == PointTypes.DEMAND_ACCUMULATOR_POINT) {
            return getAccumulatorPointMultiplier(pointId);
        } else {
            throw new NotFoundException( "Point DataOffset not found for Id: " + pointId + " and Type " + pointType + ".");
        }
    }
    
    private double getAccumulatorPointMultiplier(int pointId) {
        try {
            String sql = "select multiplier from pointaccumulator where pointid=?";
            return jdbcOps.queryForObject(sql, new Object[] {pointId}, Double.class);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException( "Accumulator Point DataOffset not found for Id: " + pointId + ".");
        }
    }
    
    private double getAnalogPointMultiplier(int pointId) {
        try {
            String sql = "select multiplier from pointanalog where pointid=?";
            return jdbcOps.queryForObject(sql, new Object[] {pointId}, Double.class);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException( "Analog Point DataOffset not found for Id: " + pointId + ".");
        }
    }

    private static LitePoint createLitePoint(Map<LitePoint, LitePoint> alreadyCreated, YukonResultSet rset) throws SQLException {
        LitePoint litePoint = createLitePoint(rset);
        LitePoint retVal = alreadyCreated.get(litePoint);
        if (retVal == null) {
            alreadyCreated.put(litePoint, litePoint);
            retVal = litePoint;
        }
        return retVal;
    }

    private static LitePoint createLitePoint(YukonResultSet rset) throws SQLException {
        int pointId = rset.getInt("PointId");
        String pointName = rset.getString("PointName").trim();
        PointType pointType = rset.getEnum("PointType", PointType.class);
        int paobjectId = rset.getInt("PaobjectId");
        int pointOffset = rset.getInt("PointOffset");
        int stateGroupId = rset.getInt("StateGroupId");
        String formula = rset.getString("Formula");
        int uofmId = rset.getInt("uomId");

        if (rset.wasNull()) { // if uomid is null, set it to an INVALID int
            uofmId = UnitOfMeasure.INVALID.getId();
        }

        // process all the bit mask tags here
        long tags = LitePoint.POINT_UOFM_GRAPH;
        if ("usage".equalsIgnoreCase(formula)) {
            tags = LitePoint.POINT_UOFM_USAGE;
        }

        LitePoint lp = new LitePoint(pointId, pointName, pointType.getPointTypeId(), paobjectId,
                                     pointOffset, stateGroupId, tags, uofmId);
        return lp;
    }

    private static LitePointUnit createLitePointUnit(ResultSet rset) throws SQLException {
        int pointID = rset.getInt(1);
        int uomID = rset.getInt(2);
        int decimalPlaces = rset.getInt(3);

        com.cannontech.database.data.lite.LitePointUnit lpu =
            new com.cannontech.database.data.lite.LitePointUnit( pointID, uomID, decimalPlaces);
        return lpu;
    }

    private static LiteRawPointHistory createLitePointHistory(ResultSet rset) throws SQLException {
        long changeID = rset.getLong(1);
        int pointID = rset.getInt(2);
        Timestamp ts = rset.getTimestamp(3);
        int quality = rset.getInt(4);
        double value = rset.getDouble(5);
        
        LiteRawPointHistory lrph =
            new LiteRawPointHistory( changeID, pointID, ts.getTime(), quality, value);
        return lrph;
    }
}
