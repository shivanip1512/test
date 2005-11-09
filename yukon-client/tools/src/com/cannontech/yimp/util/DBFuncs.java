/*
 * Created on Feb 4, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yimp.util;

import java.util.Vector;
import java.sql.Connection; 
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.device.MCT400SeriesBase;
import com.cannontech.database.data.device.MCT410CL;
import com.cannontech.database.data.device.MCT410IL;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.PoolManager;
import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DBFuncs 
{
	/*
	 * Grab a Route using a PAOName, returns one with an ID of -12 if unsuccessful
	 * This should be changed to use cache at a later time
	 */
	public static Integer getRouteFromName(String name)
	{
		Integer routeID = new Integer(-12);
		
		SqlStatement stmt = new SqlStatement("SELECT PAOBJECTID FROM YUKONPAOBJECT WHERE PAONAME = '" 
			+ name + "' AND PAOCLASS = 'ROUTE'", "yukon");
		
		try
		{
			stmt.execute();
				
			if( stmt.getRowCount() > 0 )
			{
				routeID = new Integer( ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue());	
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return routeID;
	}
	
	/*
	 * Grab an MCT410 using a PAOName, returns one with an ID of -12 if unsuccessful
	 * This should be changed to use cache at a later time
	 */
	public static MCT400SeriesBase get410FromTemplateName(String name)
	{
		MCT400SeriesBase template410 = new MCT400SeriesBase();
		template410.setDeviceID(new Integer(-12));
		Integer id = new Integer(1);
        String type = new String();

		SqlStatement stmt = new SqlStatement("SELECT PAOBJECTID,TYPE FROM YUKONPAOBJECT WHERE PAONAME = '" 
			+ name + "' AND (TYPE = 'MCT-410IL' OR TYPE = 'MCT-410CL')", "yukon");
        
		try
		{
			stmt.execute();
				
			if( stmt.getRowCount() > 0 )
			{
				id = new Integer( ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue());	
				type = new String(stmt.getRow(0)[1].toString());
                
                if(type.length() > 0)
                {
                    if(type.compareTo(DeviceTypes.STRING_MCT_410CL[0]) == 0)
                    {
                        template410 = new MCT410CL();
                        template410.setDeviceID(id);
                        template410 = (MCT410CL) Transaction.createTransaction(Transaction.RETRIEVE, template410).execute();
                    }
                    else
                    {
                        template410 = new MCT410IL();
                        template410.setDeviceID(id);
                        template410 = (MCT410IL) Transaction.createTransaction(Transaction.RETRIEVE, template410).execute();
                    }
                }
			}
		}
		catch( Exception e )
		{
			template410.setDeviceID(new Integer(-12));
			e.printStackTrace();
		}
	
		return template410;
	}
	
	public static boolean IsDuplicateName(String name)
	{
		SqlStatement stmt = new SqlStatement("SELECT PAOBJECTID FROM YUKONPAOBJECT WHERE PAONAME = '" 
			+ name + "' AND (TYPE = 'MCT-410IL' OR TYPE = 'MCT-410CL')", "yukon");
		
		try
		{
			stmt.execute();

			if( stmt.getRowCount() > 0 )
			{
				return ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue() != 0;	
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return false;
	}
	/**
	 * returns the paobjectid of the device with address.  Else null
	 * @param address
	 * @return
	 */
	public static Integer getDeviceIDByAddress(String address)
	{
		Integer returnDeviceID = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		Connection conn = null;
		
		try
		{
			String sql = "SELECT DEVICEID FROM " + DeviceCarrierSettings.TABLE_NAME + " WHERE ADDRESS = '" + address + "'";
		    
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			if( conn == null )
			{
				CTILogger.error("Error getting database connection.");
				return null;	//this might not be the best thing to return, its assuming the address is NOT already in t
			}
			else
			{
	
				pstmt= conn.prepareStatement( sql);
				rset = pstmt.executeQuery();
	
				if(rset != null && rset.next() )
				{
					returnDeviceID = new Integer(rset.getInt(1));
				}
			}
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( rset != null)
					rset.close();
				if( pstmt != null )
					pstmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
		}
		
		return returnDeviceID;
	}
	public static int[] getNextPAObjectID(int numberOfImportEntries)
	{
		int retVal = 0;
		int[] ids = new int[numberOfImportEntries];
		
		SqlStatement stmt = new SqlStatement("select max(paobjectid) AS maxid from yukonpaobject", "yukon");
		
		try
		{		
			stmt.execute();							

			// Just one please
			if( stmt.getRowCount() > 0 )
			{
				retVal = ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue();	
			}
		}
		catch(Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		
		for(int j = 0; j < numberOfImportEntries; j++)
		{
			ids[j] = retVal + j;
		}
		
		com.cannontech.clientutils.CTILogger.info("----- getNextYukonPAObjectIDs(yukonPAObjectsCnt) returning with " + numberOfImportEntries + " ids!");
		
		return ids;
	}
	
	public static Vector getPointsForPAO( Integer paoID )
	{
		LitePoint[] points = PAOFuncs.getLitePointsForPAObject( paoID.intValue() );
		
		Vector daPoints = new Vector(points.length);
		
		PointBase pointBase = null;
	
		for (int i = 0; i < points.length; i++)
		{
			pointBase = (PointBase) LiteFactory.createDBPersistent(points[i]);
			
			try
			{
				com.cannontech.database.Transaction t =
					com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, pointBase);
				t.execute();
				daPoints.addElement(pointBase);
			}
			catch (com.cannontech.database.TransactionException e)
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
		}
			
		return daPoints;
	
	}
	
	public final static int getNextPointID()
	{
		return Point.getNextPointID();
	}
	
	public static boolean writeLastImportTime(Date lastImport)
	{
		SqlStatement stmt = new SqlStatement("UPDATE DYNAMICIMPORTSTATUS SET LASTIMPORTTIME = '" + lastImport.toString() + "' WHERE ENTRY = 'SYSTEMVALUE'", "yukon");
		
		try
		{
			stmt.execute();
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			return false;
		}

		return true;
	}
	
	public static boolean writeNextImportTime(Date nextImport, boolean currentlyRunning)
	{
		String next;
		
		if(currentlyRunning)
			next = "CURRENTLY RUNNING..";
		else
			next = nextImport.toString();

		SqlStatement stmt = new SqlStatement("UPDATE DYNAMICIMPORTSTATUS SET NEXTIMPORTTIME = '" + next + "' WHERE ENTRY = 'SYSTEMVALUE'", "yukon");
				
		try
		{
			stmt.execute();
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			return false;
		}

		return true;
	}
	
	public static boolean writeTotalSuccess(int success)
	{
		SqlStatement stmt = new SqlStatement("UPDATE DYNAMICIMPORTSTATUS SET TOTALSUCCESSES = " + success + " WHERE ENTRY = 'SYSTEMVALUE'", "yukon");

		try
		{
			stmt.execute();
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			return false;
		}

		return true;
	}
	
	public static boolean writeTotalAttempted(int attempts)
	{
		SqlStatement stmt = new SqlStatement("UPDATE DYNAMICIMPORTSTATUS SET TOTALATTEMPTS = " + attempts + " WHERE ENTRY = 'SYSTEMVALUE'", "yukon");

		try
		{
			stmt.execute();
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			return false;
		}

		return true;
	}
	
	public static void alreadyForcedImport()
	{
		SqlStatement stmt = new SqlStatement("UPDATE DYNAMICIMPORTSTATUS SET FORCEIMPORT = 'N' WHERE ENTRY = 'SYSTEMVALUE'", "yukon");
	
		try
		{
			stmt.execute();
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}
	
	public static boolean forceImport()
	{
		SqlStatement stmt = new SqlStatement("UPDATE DYNAMICIMPORTSTATUS SET FORCEIMPORT = 'Y' WHERE ENTRY = 'SYSTEMVALUE'", "yukon");
			
		try
		{
			stmt.execute();
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			return false;
		}

		return true;
	}
	
	public static boolean isForcedImport()
	{
		boolean isForced = false;
		SqlStatement stmt = new SqlStatement("SELECT FORCEIMPORT FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'", "yukon");
		
		try
		{
			stmt.execute();

			if( stmt.getRowCount() > 0 )
				isForced = stmt.getRow(0)[0].toString().compareTo("Y") == 0;
		}
		
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return isForced;
	}
	
	/*
	 * If the servers receive a device or point DBChangeMsg with an id of zero, then
	 * they do a full reload of the database.
	 * This is a poor man's RELOAD_ALL DBChangeMsg.
	 */
	public static void generateBulkDBChangeMsg(int dbField, String objCategory, String objType, ClientConnection dispatchConnection  ) 
	{
		DBChangeMsg chumpChange = new com.cannontech.message.dispatch.message.DBChangeMsg(
			0,
			dbField,
			objCategory,
			objType,
			DBChangeMsg.CHANGE_TYPE_ADD );
		
		dispatchConnection.write(chumpChange);
				
	}
	
	
	
	
}