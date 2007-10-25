package com.cannontech.cbc.web;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.SystemLogData;
import com.cannontech.database.db.capcontrol.CCEventLog;
import com.cannontech.database.db.capcontrol.RecentControls;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.roles.capcontrol.CBCSettingsRole;
import com.cannontech.util.ServletUtil;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;

/**
 * Generates a URL for a graph with the given cache and item ID
 * 
 * @author ryan
 */
public class CBCWebUtils implements CBCParamValues
{
	private static final String GRAPH_30_DAY_URL
		= "/servlet/GraphGenerator?action=EncodeGraph";

    public static  final String ONE_LINE_DIR = "/capcontrol/oneline";
	/**
	 * Creates a URL that will generate a graph  for the give FEEDER
	 *  or SUBUBUS id
	 * 
	 */
	public static synchronized String genGraphURL( int theId, CapControlCache theCache, String period, String type )
	{
		if( theCache == null )
			return null;
		
		String retURL = GRAPH_30_DAY_URL;
		if( period == null )
			retURL += "&period=" + ServletUtil.PREVTHIRTYDAYS;
		else
			retURL += "&period=" + period;
		
		String res = null;
		if( theCache.isSubBus(theId) )
		{
			res = _createSubBusGraphURL( theCache.getSubBus(new Integer(theId)), type );
			retURL = (res == null ? null : retURL + res);
		}
		else if( theCache.isFeeder(theId) )
		{
			res = _createFeederGraphURL( theCache.getFeeder(new Integer(theId)), type );
			retURL = (res == null ? null : retURL + res);
		}


		return retURL;
	}

	public static synchronized String genGraphURL( int theId, CapControlCache theCache, String type )
	{
		return genGraphURL( theId, theCache, ServletUtil.PREVTHIRTYDAYS, type );
	}

	/**
	 * Creates a URL for the given SubBus's points
	 * 
	 */
	private static synchronized String _createSubBusGraphURL( SubBus subBus, String type )
	{
		String temp = "";
		
		if( TYPE_PF.equals(type) )
		{
			temp += _getPointStr( subBus.getPowerFactorPointId() );		
			temp += _getPointStr( subBus.getEstimatedPowerFactorPointId() );
		}
		else
		{
			temp += _getPointStr( subBus.getCurrentVarLoadPointID() );		
			temp += _getPointStr( subBus.getCurrentWattLoadPointID() );
			temp += _getPointStr( subBus.getEstimatedVarLoadPointID() );
		}

		if( temp.length() > 0 )
			return "&pointid=" + temp.substring(1);
		else
			return null;
	}

	/**
	 * Creates a URL for the given feeders points
	 * 
	 */
	private static synchronized String _createFeederGraphURL( Feeder feeder, String type )
	{
		String temp = "";
		if( TYPE_PF.equals(type) )
		{
			temp += _getPointStr( feeder.getPowerFactorPointID() );		
			temp += _getPointStr( feeder.getEstimatedPowerFactorPointID() );
		}
		else
		{		
			temp += _getPointStr( feeder.getCurrentVarLoadPointID() );		
			temp += _getPointStr( feeder.getCurrentWattLoadPointID() );
			temp += _getPointStr( feeder.getEstimatedVarLoadPointID() );
		}
		
		if( temp.length() > 0 )
			return "&pointid=" + temp.substring(1);
		else
			return null;
	}

	/**
	 * Decides if a given PointID is valid or not.
	 * 
	 */
	private static boolean _isPointIDValid( Integer ptID )
	{
		return ptID != null && ptID.intValue() != CtiUtilities.NONE_ZERO_ID;
	}
	
	/**
	 * Returns the ptID as a string with a preceding comma
	 * 
	 */
	private static String _getPointStr( Integer ptID )
	{
		if( _isPointIDValid(ptID) )
			return ","+ptID;
		else
			return "";
	}

	/**
	 * A quick access method to find out if the user in the given session
	 * has the CBC controls property set to true.
	 * 
	 */
    //TODO: There shouldn't be a dependency on httpsession here!
	public static synchronized boolean hasControlRights( HttpSession session )
	{
		LiteYukonUser yukUser = (LiteYukonUser)ServletUtil.getYukonUser(session);
		
		LiteYukonRoleProperty liteProp =
			DaoFactory.getAuthDao().getRoleProperty(CBCSettingsRole.ALLOW_CONTROLS);

		if( yukUser != null && liteProp != null )
		{
			String val = DaoFactory.getAuthDao().getRolePropertyValue(
				yukUser,
				liteProp.getRolePropertyID() );
			
			if( Boolean.TRUE.toString().equalsIgnoreCase(val) )
				return true;
		}

		return false;
	}



	/**
	 * Returns SystemLog rows from the database 
	 * 
	 */	
	private static synchronized SystemLogData[] _getRecentEntries( int pointid, int prevDays )
	{
		//get all the columns from SystemLog	
		String sql = "select PointID, DateTime, SOE_Tag, " +
			"Type, Priority, Action, Description, UserName from " +
			SystemLog.TABLE_NAME +              
			" where pointid = " + pointid +
			" and datetime >= ? " + 
			" order by datetime desc";
		
		ArrayList tmpList = new ArrayList(64);
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		long startTS = ServletUtil.getDate(prevDays).getTime();
	
		try
		{		
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			if( conn == null )
			{
				throw new IllegalStateException("Error getting database connection.");
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( startTS ));

				rset = pstmt.executeQuery();

				while( rset.next() )
				{
					SystemLog sLog = new SystemLog();
					sLog.setPointID( new Integer(rset.getInt(1)) );
					java.sql.Timestamp ts = rset.getTimestamp(2);
					sLog.setDateTime( new Date(ts.getTime()) );
					sLog.setSoe_tag( new Integer(rset.getInt(3)) );
					sLog.setType( new Integer(rset.getInt(4)) );
					sLog.setPriority( new Integer(rset.getInt(5)) );
					sLog.setAction( rset.getString(6) );
					sLog.setDescription( rset.getString(7) );
					sLog.setUserName( rset.getString(8) );

					tmpList.add( sLog );
				}
			}		
		}
		catch( java.sql.SQLException e )
		{
			CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			SqlUtils.close(rset, pstmt, conn );
		}

		return (SystemLogData[])tmpList.toArray( new SystemLogData[tmpList.size()] );
	}

	/**
	 * Returns events that have occured on the the given PaoID. The events
     * will be retrieved from the SystemLog table
	 */
	public static synchronized SystemLogData[] getRecentControls( int theId, CapControlCache theCache, int prevDayCount )
	{
		SystemLogData[] retLog = new SystemLogData[0];
		if( theCache == null )
			return retLog;
		
		//we need the negative value for number of days
		if( prevDayCount >= 0 )
			prevDayCount *= -1;
		

		if( theCache.isSubBus(theId) )
		{			
			//SubBus subBus = theCache.getSubBus(new Integer(theId));			
			//just show capcontrol log entries, nothing currently available to
			//  SubBus systemlog entries			
			retLog = _getRecentEntries( PointTypes.SYS_PID_CAPCONTROL, prevDayCount );
		}
		else if( theCache.isFeeder(theId) )
		{
			//Feeder feeder = theCache.getFeeder(new Integer(theId));
			//just show capcontrol log entries, nothing currently available to
			//  Feeder systemlog entries			
			retLog = _getRecentEntries( PointTypes.SYS_PID_CAPCONTROL, prevDayCount );
		}
		else if( theCache.isCapBank(theId) )
		{
			CapBankDevice capBank = theCache.getCapBankDevice(new Integer(theId));
			retLog = _getRecentEntries( capBank.getStatusPointID().intValue(), prevDayCount );
		}


		return retLog;
	}
	
	public static List getCCEventsForPAO (Long _paoId_, String type, CapControlCache theCache, int prevDaysCount) {
	    String sqlStmt ="SELECT * FROM " + CCEventLog.TABLE_NAME + " WHERE"; 
	    List<RecentControls> ccEvents = new ArrayList<RecentControls>();
	    long startTS = ServletUtil.getDate(- prevDaysCount).getTime();
	    java.sql.Timestamp timestamp = new java.sql.Timestamp( startTS );
			    
	    if (type.equalsIgnoreCase("CCFEEDER") || type.equalsIgnoreCase("CCSUBBUS")) {
		    if (type.equalsIgnoreCase("CCFEEDER")){
		    	sqlStmt += " FeederId = ?";
		    }	    	
		    else if (type.equalsIgnoreCase("CCSUBBUS")){
		    	sqlStmt += " SubId = ?";
		    }
		    sqlStmt += " AND DateTime >= ? ";
		    
		    sqlStmt += " ORDER BY " + CCEventLog.COLUMNS [CCEventLog.COL_DATETIME] + " DESC";
		    JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
		    
		    ccEvents = yukonTemplate.query(sqlStmt, new Object[] {_paoId_, timestamp}, new int[] {Types.BIGINT, Types.TIMESTAMP},
		    		new RowMapper() {
		    			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		    				CCEventLog row = new CCEventLog(); 
		    				row.setLogId (new Long ( rs.getBigDecimal(1).longValue() ));
		    				row.setPointId(new Long ( rs.getBigDecimal(2).longValue() ));
		    				row.setDateTime((( rs.getTimestamp(3))));
		    				row.setSubId(new Long ( rs.getBigDecimal(4).longValue() ));
		    				row.setFeederId(new Long ( rs.getBigDecimal(5).longValue() ));
		    				row.setEventType(new Integer ( rs.getBigDecimal(6).intValue() ));
		    				row.setSeqId(new Long ( rs.getBigDecimal(7).longValue() ));
		    				row.setValue(new Long ( rs.getBigDecimal(8).longValue() ));
		    				row.setText(( rs.getString(9)));
		    				row.setUserName(( rs.getString(10)));
		    				return row;        				
		    			}
		    		});
	    }
	    else if (type.equalsIgnoreCase("CAP BANK")){
	    	sqlStmt = "SELECT * FROM " + SystemLog.TABLE_NAME + " WHERE PointId = ? ";
	    	sqlStmt += " AND DateTime >= ?";
	    	sqlStmt += " ORDER BY " + SystemLog.COLUMNS[SystemLog.COL_DATETIME]+ " DESC";
	    	Integer statusPtId = getStatusPointFromPaoId(_paoId_, theCache);  		
			if (statusPtId != null && statusPtId.intValue() >= 0) {
				JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();  
			
				ccEvents = yukonTemplate.query(sqlStmt, new Object[] {statusPtId, timestamp}, new int[] {Types.INTEGER, Types.TIMESTAMP},
			    		new RowMapper() {
			    			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			    				SystemLog row = new SystemLog();
			    				row.setLogID(new Integer (rs.getBigDecimal(1).intValue()));
			    				row.setPointID(new Integer (rs.getBigDecimal(2).intValue()));
			    				row.setDateTime((( rs.getTimestamp(3))));			    				
			    				row.setSoe_tag(new Integer(rs.getBigDecimal(4).intValue()));
			    				row.setType(new Integer (rs.getBigDecimal(5).intValue()));
			    				row.setPriority(new Integer(rs.getBigDecimal(6).intValue()));
			    				row.setAction((rs.getString(7)));
			    				row.setDescription(rs.getString(8));
			    				row.setUserName(rs.getString(9));
			    				rs.getBigDecimal(10).intValue();
			    				return row;        				
			    			}
			    		});
			}
					
	    	}
	    	
	    
	    return ccEvents;  	
	}

	/**
	 * @param _paoId_
	 * @param theCache
	 * @return TODO
	 */
	private static synchronized Integer getStatusPointFromPaoId(Long _paoId_, CapControlCache theCache) {
		Integer statusPtId = null;
		StreamableCapObject obj = theCache.getCapControlPAO(new Integer (_paoId_.intValue() ));
			if (obj instanceof CapBankDevice) {
				CapBankDevice capBank = (CapBankDevice) obj;
				statusPtId = capBank.getStatusPointID();
			}
		
		return statusPtId;
	}
}