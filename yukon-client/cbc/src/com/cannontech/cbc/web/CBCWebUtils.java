package com.cannontech.cbc.web;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.SystemLogData;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.roles.capcontrol.CBCSettingsRole;
import com.cannontech.util.ServletUtil;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
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
	public static synchronized boolean hasControlRights( HttpSession session )
	{
		LiteYukonUser yukUser = (LiteYukonUser)ServletUtil.getYukonUser(session);
		
		LiteYukonRoleProperty liteProp =
			AuthFuncs.getRoleProperty(CBCSettingsRole.ALLOW_CONTROLS);

		if( yukUser != null && liteProp != null )
		{
			String val = AuthFuncs.getRolePropertyValue(
				yukUser,
				liteProp.getRolePropertyID(),
				liteProp.getDefaultValue() );
			
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
			try
			{
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			} 
			catch( SQLException e2 )
			{
				CTILogger.error( e2.getMessage(), e2 );
			}	
		}

		return (SystemLogData[])tmpList.toArray( new SystemLogData[tmpList.size()] );
	}

	/**
	 * Creates a URL that will generate a graph  for the give FEEDER
	 *  or SUBUBUS id
	 * 
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

}