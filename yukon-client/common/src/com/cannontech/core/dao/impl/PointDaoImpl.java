package com.cannontech.core.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.CapBankMonitorPointParams;
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

    
    private static final RowMapper litePointRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createLitePoint(rs);
        };
    };
    
    private static final RowMapper litePointUnitRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createLitePointUnit(rs);
        };
    };
    
    private IDatabaseCache databaseCache;
    private JdbcOperations jdbcOps;
    private NextValueHelper nextValueHelper;
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getLitePoint(int)
     */
    public LitePoint getLitePoint(int pointID) {
        String sql = litePointSql + " WHERE P.POINTID = ?";
        LitePoint p = (LitePoint) jdbcOps.queryForObject(sql, new Object[] { pointID }, PointDaoImpl.litePointRowMapper);
        return p;
    }
    
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
    
    
    public List<LitePoint> getLitePointsByNumStates(int numberOfStates) {
        String sql = litePointSql + 
            " WHERE P.STATEGROUPID IN (select stategroupid from state group by stategroupid having count(rawstate)=?) ";
        
        List<LitePoint> points = 
            jdbcOps.query(sql, new Object[] { numberOfStates }, PointDaoImpl.litePointRowMapper);
        return points;
    }

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
            idAndTypes[i][0] = ((LitePoint)points.get(i)).getPointID();
            idAndTypes[i][1] = ((LitePoint)points.get(i)).getPointType();
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
			Iterator iter = databaseCache.getAllPointLimits().iterator();
			while(iter.hasNext()) {
				LitePointLimit lpl = (LitePointLimit) iter.next();
				if( lpl.getPointID() == pointID ) {
					return lpl;
				}
			}
		}	
		
		return null;
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getPointUnit(int)
     */
	public LitePointUnit getPointUnit(int pointID) {
        String sql = 
            "SELECT POINTID,UOMID,DECIMALPLACES FROM POINTUNIT WHERE POINTID=?";
        LitePointUnit lpu = (LitePointUnit) 
            jdbcOps.queryForObject(sql, new Object[] { pointID }, litePointUnitRowMapper);
        return lpu;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getStateGroup(int)
     */
	public LiteStateGroup getStateGroup( int stateGroupID ) {
		return (LiteStateGroup)
			databaseCache.getAllStateGroupMap().get( new Integer(stateGroupID) );
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getPointIDByDeviceID_Offset_PointType(int, int, int)
     */
	public int getPointIDByDeviceID_Offset_PointType(int deviceID, int pointOffset, int pointType) {
        List<LitePoint> litePoints = getLitePointsByPaObjectId(deviceID);
		for (LitePoint lp : litePoints) {
			if( lp.getPointOffset() == pointOffset && pointType == lp.getPointType())
				return lp.getPointID();
		}
	
		return PointTypes.SYS_PID_SYSTEM; //not found
	}
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#retrieveCICustomerPointData(int)
     */
	public Double retrieveCICustomerPointData(int pointID)
	{
		Double data = new Double(0);
		String sqlString = "SELECT TIMESTAMP, VALUE FROM " + RawPointHistory.TABLE_NAME +
			" WHERE POINTID = " + pointID + 
			" ORDER BY TIMESTAMP DESC ";

		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);

			while (rset.next())
			{
				Timestamp ts = rset.getTimestamp(1);
				double value = rset.getDouble(2);

				data = new Double(value);
				break;
			}
		}
		catch (java.sql.SQLException e)
		{
			CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if (rset != null)
					rset.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			}
			catch (java.sql.SQLException e)
			{
				CTILogger.error( e.getMessage(), e );
			}
		}
		return data;
	}
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getCapBankMonitorPoints(com.cannontech.database.data.capcontrol.CapBank)
     */
	public List getCapBankMonitorPoints(CapBank capBank) {
		List monitorPointList = new ArrayList();
		for (Iterator iter = capBank.getCcMonitorBankList().iterator(); iter.hasNext();) {
			CCMonitorBankList point = (CCMonitorBankList) iter.next();			
			
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
    
    public int getPointDataOffset(int pointId) {
        String sql = "select dataoffset from pointanalog where pointid=?";
        return (Integer)jdbcOps.queryForObject(sql, new Object[] {pointId}, Integer.class);
    }
    
    public double getPointMultiplier(int pointId) {
        String sql = "select multiplier from pointanalog where pointid=?";
        return (Double)jdbcOps.queryForObject(sql, new Object[] {pointId}, Double.class);
    }
    
	public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
    
    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
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
    

}
