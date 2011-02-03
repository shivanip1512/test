package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.CapBankMonitorPointParams;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.capcontrol.CCMonitorBankList;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Implementation of PointDao
 * Creation date: (7/01/2006 9:40:33 AM)
 * @author: alauinger
 */
public final class PointDaoImpl implements PointDao {

    private static final String litePointSql = 
        "SELECT P.POINTID, POINTNAME, POINTTYPE, PAOBJECTID, POINTOFFSET, STATEGROUPID, UM.FORMULA, UM.UOMID" +
        " FROM " +         
        "( POINT P LEFT OUTER JOIN POINTUNIT PU ON P.POINTID = PU.POINTID " +
        "LEFT OUTER JOIN UNITMEASURE UM ON PU.UOMID = UM.UOMID ) ";
    
    private static final String litePointPaoSql = 
        "SELECT P.POINTID, POINTNAME, POINTTYPE, YPO.PAOBJECTID, POINTOFFSET, STATEGROUPID, UM.FORMULA, UM.UOMID" +
        " FROM " +    
        "( POINT P LEFT OUTER JOIN POINTUNIT PU ON P.POINTID = PU.POINTID " +
        "LEFT OUTER JOIN UNITMEASURE UM ON PU.UOMID = UM.UOMID " +
        "LEFT OUTER JOIN YUKONPAOBJECT YPO ON P.PAOBJECTID=YPO.PAOBJECTID ) ";

    
    private static final ParameterizedRowMapper<LitePoint> litePointRowMapper = new ParameterizedRowMapper<LitePoint>() {
        public LitePoint mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createLitePoint(rs);
        };
    };
    
    private static final RowMapper litePointUnitRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createLitePointUnit(rs);
        };
    };

    private static final RowMapper litePointHistoryRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createLitePointHistory(rs);
        };
    };

    private IDatabaseCache databaseCache;
    private YukonJdbcOperations simpleJdbcTemplate;
    private JdbcOperations jdbcOps;
    private NextValueHelper nextValueHelper;
    
    @Override
    public LitePoint findPointByName(YukonPao pao, String pointName) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder(litePointSql);
            sql.append("WHERE PaobjectId").eq(pao.getPaoIdentifier().getPaoId());
            sql.append(  "AND UPPER(PointName)").eq(pointName.toUpperCase());
            
            return simpleJdbcTemplate.queryForObject(sql, litePointRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getLitePoint(int)
     */
    public LitePoint getLitePoint(int pointID) {
        try {
            String sql = litePointSql + " WHERE P.POINTID = ?";
            LitePoint p = (LitePoint) jdbcOps.queryForObject(sql, new Object[] { pointID }, PointDaoImpl.litePointRowMapper);
            
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
        
        PaoPointIdentifier result = simpleJdbcTemplate.queryForObject(sql, new ParameterizedRowMapper<PaoPointIdentifier>() {
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
    
    @SuppressWarnings("unchecked")
    public List<LitePoint> getLitePoints(Integer[] pointIds) {
        StringBuilder sql = new StringBuilder(litePointSql);
        SqlUtil.buildInClause("where", "p", "pointid", pointIds, sql);
        List<LitePoint> points = 
            jdbcOps.query(sql.toString(), PointDaoImpl.litePointRowMapper);
        return points;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getMaxPointID()
     */
    public int getMaxPointID() {
        return jdbcOps.queryForInt("select max(pointid) from point");
    }
    
    public int getNextPointId() {
        return nextValueHelper.getNextValue("point");
    }
    
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
    @SuppressWarnings("unchecked")
    public List<LitePoint> getLitePointsBy(Integer[] pointTypes, Integer[] uomIDs, Integer[] paoTypes, Integer[] paoCategories, Integer[] paoClasses) {
        StringBuilder sql = new StringBuilder(litePointPaoSql);
    
        pointTypes = CtiUtilities.ensureNotNull(pointTypes);
        uomIDs = CtiUtilities.ensureNotNull(uomIDs);
        paoTypes = CtiUtilities.ensureNotNull(paoTypes);
        paoCategories = CtiUtilities.ensureNotNull(paoCategories);
        paoClasses = CtiUtilities.ensureNotNull(paoClasses);
        
        String[] pointTypesStr = PointTypes.convertPointTypes(pointTypes);
        String[] paoTypesStr = PAOGroups.convertPaoTypes(paoTypes);
        String[] paoCategoriesStr = PAOGroups.convertPaoCategories(paoCategories);
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
    
    
    @SuppressWarnings("unchecked")
    public List<LitePoint> getLitePointsByNumStates(int numberOfStates) {
        String sql = litePointSql + 
            " WHERE P.STATEGROUPID IN (select stategroupid from state group by stategroupid having count(rawstate)=?) ";
        
        List<LitePoint> points = 
            jdbcOps.query(sql, new Object[] { numberOfStates }, PointDaoImpl.litePointRowMapper);
        return points;
    }

    @SuppressWarnings("unchecked")
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
	public String getPointName(int pointId) {
	    LitePoint p = getLitePoint(pointId);
        return p.getPointName();
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getPointLimit(int)
     */
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
	public LitePointUnit getPointUnit(int pointID) {
        String sql = 
            "SELECT POINTID,UOMID,DECIMALPLACES FROM POINTUNIT WHERE POINTID=?";
      try {  
        LitePointUnit lpu = (LitePointUnit) 
            jdbcOps.queryForObject(sql, new Object[] { pointID }, litePointUnitRowMapper);
        return lpu;
     } catch (IncorrectResultSizeDataAccessException e) {
         throw new NotFoundException("Pointunit for point with id " + pointID + " cannot be found.");
     }
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getStateGroup(int)
     */
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
	
	@Override
	public int getPointId(PaoPointIdentifier paoPointIdentifier) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append("SELECT pointid");
	    sql.append("FROM Point");
	    sql.append("WHERE PaObjectId").eq(paoPointIdentifier.getPaoIdentifier().getPaoId());
	    sql.append("  AND PointOffset").eq(paoPointIdentifier.getPointIdentifier().getOffset());
	    sql.append("  AND PointType").eq_k(paoPointIdentifier.getPointIdentifier().getPointType());

	    try {
	        int pointId = simpleJdbcTemplate.queryForInt(sql);
	        return pointId;
	    } catch (IncorrectResultSizeDataAccessException e) {
	        throw new NotFoundException("unable to find pointId for " + paoPointIdentifier, e);
	    }
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getPointIDByDeviceID_Offset_PointType(int, int, int)
     */
	public int getPointIDByDeviceID_Offset_PointType(int deviceId, int pointOffset, int pointType) {
        try {
            LitePoint point = getLitePointIdByDeviceId_Offset_PointType(deviceId, pointOffset, pointType);
            return point.getPointID();
        } catch (NotFoundException e) {
            return PointTypes.SYS_PID_SYSTEM; //not found
        }
	}

    public LitePoint getLitePointIdByDeviceId_Offset_PointType(int deviceId, int pointOffset, int pointType) {
	    final String sqlString = litePointSql + " WHERE PaObjectId = ? AND POINTOFFSET = ? AND POINTTYPE = ?";
        try {
            return (LitePoint) jdbcOps.queryForObject(sqlString,
                                                      new Object[]{deviceId, pointOffset, PointTypes.getType(pointType)},
                                                      PointDaoImpl.litePointRowMapper);   
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unable to find point for deviceId=" + deviceId + ", pointOffset=" + pointOffset + ", pointType=" + pointType);
        }
	}
    
    @SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
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

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getCapBankMonitorPoints(com.cannontech.database.data.capcontrol.CapBank)
     */
	public List<CapBankMonitorPointParams> getCapBankMonitorPoints(CapBank capBank) {
		List<CapBankMonitorPointParams> monitorPointList = new ArrayList<CapBankMonitorPointParams>();
		for (Iterator<CCMonitorBankList> iter = capBank.getCcMonitorBankList().iterator(); iter.hasNext();) {
			CCMonitorBankList point = iter.next();			
			
			CapBankMonitorPointParams monitorPoint = new CapBankMonitorPointParams();
			monitorPoint.setCapBankId(point.getCapBankId().intValue());
			monitorPoint.setPointId(point.getPointId().intValue());
			monitorPoint.setDisplayOrder(point.getDisplayOrder().intValue());
			if (point.getScannable().charValue() == 'Y') 
				monitorPoint.setInitScan(true);
			else
				monitorPoint.setInitScan(false);
			monitorPoint.setNINAvg(point.getNINAvg().longValue());
			monitorPoint.setLowerBandwidth(point.getLowerBandwidth().floatValue());
			monitorPoint.setUpperBandwidth(point.getUpperBandwidth().floatValue());			
			LitePoint p = getLitePoint(point.getPointId().intValue());
			monitorPoint.setPointName(p.getPointName());
			monitorPoint.setOverrideFdrLimits(false);
			monitorPointList.add(monitorPoint);
		}
			return monitorPointList;
	}
    
    @SuppressWarnings("unchecked")
    public List<LitePoint> searchByName(final String name, final String paoClass) {
        String sql = litePointPaoSql + " WHERE YPO.PAOClass = ? AND UPPER(POINTNAME) LIKE ?";
        List<LitePoint> pointList = this.jdbcOps.query(
                                                       sql,
                                                       new Object[]{paoClass, "%" + name.toUpperCase() + "%"},
                                                       litePointRowMapper);
        return pointList;
    }
    
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
            return (Double)jdbcOps.queryForObject(sql, new Object[] {pointId}, Double.class);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException( "Accumulator Point DataOffset not found for Id: " + pointId + ".");
        }
    }
    
    private double getAnalogPointMultiplier(int pointId) {
        try {
            String sql = "select multiplier from pointanalog where pointid=?";
            return (Double)jdbcOps.queryForObject(sql, new Object[] {pointId}, Double.class);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException( "Analog Point DataOffset not found for Id: " + pointId + ".");
        }
    }
    
	public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
    
    public void setSimpleJdbcTemplate(YukonJdbcOperations simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
        this.jdbcOps = this.simpleJdbcTemplate.getJdbcOperations();
    }
    
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    private static LitePoint createLitePoint(ResultSet rset) throws SQLException {
        int pointID = rset.getInt(1);
        String pointName = rset.getString(2).trim();
        String pointType = rset.getString(3).trim();
        int paobjectID = rset.getInt(4);
        int pointOffset = rset.getInt(5);
        int stateGroupID = rset.getInt(6);
        String formula = rset.getString(7);
        int uofmID = rset.getInt(8);
        
        if( rset.wasNull() )  {//if uomid is null, set it to an INVALID int
            uofmID = PointUnits.UOMID_INVALID;
        }
        
     //process all the bit mask tags here
        long tags = LitePoint.POINT_UOFM_GRAPH;
        if( "usage".equalsIgnoreCase(formula) ) {
            tags = LitePoint.POINT_UOFM_USAGE;
        }
          
        LitePoint lp =
            new LitePoint( pointID, pointName, PointTypes.getType(pointType),
                           paobjectID, pointOffset, stateGroupID, tags, uofmID );
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
        int changeID = rset.getInt(1);
        int pointID = rset.getInt(2);
        Timestamp ts = rset.getTimestamp(3);
        int quality = rset.getInt(4);
        double value = rset.getDouble(5);
        
        LiteRawPointHistory lrph =
            new LiteRawPointHistory( changeID, pointID, ts.getTime(), quality, value);
        return lrph;
    }

}
