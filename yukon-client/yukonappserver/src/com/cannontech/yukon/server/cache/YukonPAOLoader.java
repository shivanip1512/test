package com.cannontech.yukon.server.cache;

import java.math.BigDecimal;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceRoutes;
import com.cannontech.database.db.pao.YukonPAObject;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class YukonPAOLoader implements Runnable 
{
	//Map<Integer(paoID), LiteYukonPAObject>
	private Map allPAOsMap = null;

	private java.util.ArrayList allPAObjects = null;
	private String databaseAlias = null;
	
	
	/**
	 * YukonPAOLoader constructor comment.
	 */
	public YukonPAOLoader(java.util.ArrayList pAObjectArray, Map paoMap_, String alias) 
	{
		super();
		this.allPAObjects = pAObjectArray;
		this.allPAOsMap = paoMap_;
		this.databaseAlias = alias;
	}

	/**
	 * run method comment.
	 */
	public void run() 
	{
		//temp code
		java.util.Date timerStart = null;
		java.util.Date timerStop = null;
		//temp code
		
		//temp code
		timerStart = new java.util.Date();
		//temp code
		String sqlString = 
				"SELECT y.PAObjectID, y.Category, y.PAOName, " +
				"y.Type, y.PAOClass, y.Description, d.PORTID, dcs.ADDRESS " +
				"FROM " + YukonPAObject.TABLE_NAME+ " y left outer join " + DeviceDirectCommSettings.TABLE_NAME + " d " +
				"on y.paobjectid = d.deviceid " +
				"left outer join " + DeviceCarrierSettings.TABLE_NAME + " DCS ON Y.PAOBJECTID = DCS.DEVICEID " +				
				//"WHERE y.PAObjectID > 0 " +
				"ORDER BY y.Category, y.PAOClass, y.PAOName";
	
	/*"SELECT PAObjectID, Category, PAOName, " +
				"Type, PAOClass, Description " +
				"FROM YukonPAObject WHERE PAObjectID > 0 ORDER BY Category, PAOClass, PAOName";
	*/
	
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( this.databaseAlias );
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);
	
			while (rset.next())
			{
				int paoID = rset.getInt(1);
				String paoCategory = rset.getString(2).trim();
				String paoName = rset.getString(3).trim();
				String paoType = rset.getString(4).trim();
				String paoClass = rset.getString(5).trim();
				String paoDescription = rset.getString(6).trim();
				
				//this column may be null!!
				BigDecimal portID = (BigDecimal)rset.getObject(7);
				//this column may be null!!
				BigDecimal address = (BigDecimal)rset.getObject(8);

				LiteYukonPAObject pao = new LiteYukonPAObject(
							paoID, 
							paoName, 
							PAOGroups.getCategory(paoCategory),
							PAOGroups.getPAOType(paoCategory, paoType),
							PAOGroups.getPAOClass(paoCategory, paoClass),
							paoDescription );
	
				if( portID != null )
					pao.setPortID( portID.intValue() );
	
				if( address != null )
					pao.setAddress( address.intValue() );

				allPAObjects.add( pao );
				allPAOsMap.put( new Integer(paoID), pao );
			}
			
			
			/** Load the routeID - the outer join just wasn't getting us the right information*/
			if( rset != null )
				rset.close();
	
			sqlString = "SELECT DEVICEID,ROUTEID FROM " + DeviceRoutes.TABLE_NAME+" WHERE DEVICEID > 0 ORDER BY DEVICEID";
			rset = stmt.executeQuery(sqlString);
	
			while (rset.next())
			{
				int deviceID = rset.getInt(1);
				int routeID = rset.getInt(2);
	
				for(int i = 0; i < allPAObjects.size(); i++)
				{
					if ( ((LiteYukonPAObject)allPAObjects.get(i)).getYukonID() == deviceID )
					{
						((LiteYukonPAObject)allPAObjects.get(i)).setRouteID(routeID);
						break;
					}
				}
			}
	
		}
		catch( java.sql.SQLException e )
		{
	      CTILogger.error(" DB : YukonPAOLoader query did not work, trying Query with a non SQL-92 query");
	      //try using a nonw SQL-92 method, will be slower
	      //  Oracle 8.1.X and less will use this
	 		executeNonSQL92Query();
		}
		finally
		{
			try
			{
				if( rset != null )
					rset.close();
				if( stmt != null )
					stmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				CTILogger.error( e.getMessage(), e );
			}
	//temp code
	timerStop = new java.util.Date();
	
	CTILogger.info((timerStop.getTime() - timerStart.getTime())*.001 + 
	      " Secs for YukonPAObjectLoader (" + allPAObjects.size() + " loaded)" );
	
	//temp code
		}
	}
	
	private void executeNonSQL92Query()
	{
		String sqlString = 
				"SELECT PAObjectID, Category, PAOName, " +
				"Type, PAOClass, Description " +
				"FROM " + YukonPAObject.TABLE_NAME +
				//" WHERE PAObjectID > 0 " + 
				" ORDER BY Category, PAOClass, PAOName";
	
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( this.databaseAlias );
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);
	
			while (rset.next())
			{
				int paoID = rset.getInt(1);
				String paoCategory = rset.getString(2).trim();
				String paoName = rset.getString(3).trim();
				String paoType = rset.getString(4).trim();
				String paoClass = rset.getString(5).trim();
				String paoDescription = rset.getString(6).trim();
	
				LiteYukonPAObject pao = new LiteYukonPAObject(
							paoID, 
							paoName, 
							PAOGroups.getCategory(paoCategory),
							PAOGroups.getPAOType(paoCategory, paoType),
							PAOGroups.getPAOClass(paoCategory, paoClass),
							paoDescription );
	
				allPAObjects.add( pao );
				allPAOsMap.put( new Integer(paoID), pao );
			}
	
			/** Load the PortID*/
			if( rset != null )
				rset.close();
	
			sqlString = "SELECT DEVICEID,PORTID FROM " + DeviceDirectCommSettings.TABLE_NAME+" WHERE DEVICEID > 0 ORDER BY DEVICEID";
			rset = stmt.executeQuery(sqlString);
	
			while (rset.next())
			{
				int deviceID = rset.getInt(1);
				int portID = rset.getInt(2);
	
				for(int i = 0; i < allPAObjects.size(); i++)
				{
					if ( ((LiteYukonPAObject)allPAObjects.get(i)).getYukonID() == deviceID )
					{
						((LiteYukonPAObject)allPAObjects.get(i)).setPortID(portID);
						break;
					}
				}
			}
			/** Load the address */
			if( rset != null )
				rset.close();
	
			sqlString = "SELECT DEVICEID,ADDRESS FROM " + DeviceCarrierSettings.TABLE_NAME+" WHERE DEVICEID > 0 ORDER BY DEVICEID";
			rset = stmt.executeQuery(sqlString);
	
			while (rset.next())
			{
				int deviceID = rset.getInt(1);
				int address = rset.getInt(2);
	
				for(int i = 0; i < allPAObjects.size(); i++)
				{
					if ( ((LiteYukonPAObject)allPAObjects.get(i)).getYukonID() == deviceID )
					{
						((LiteYukonPAObject)allPAObjects.get(i)).setAddress(address);
						break;
					}
				}
			}
			/** Load the routeID*/
			if( rset != null )
				rset.close();
	
			sqlString = "SELECT DEVICEID,ROUTEID FROM " + DeviceRoutes.TABLE_NAME+" WHERE DEVICEID > 0 ORDER BY DEVICEID";
			rset = stmt.executeQuery(sqlString);
	
			while (rset.next())
			{
				int deviceID = rset.getInt(1);
				int routeID = rset.getInt(2);
	
				for(int i = 0; i < allPAObjects.size(); i++)
				{
					if ( ((LiteYukonPAObject)allPAObjects.get(i)).getYukonID() == deviceID )
					{
						((LiteYukonPAObject)allPAObjects.get(i)).setRouteID(routeID);
						break;
					}
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
				if( stmt != null )
					stmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				CTILogger.error( e.getMessage(), e );
			}
		}
	}
}