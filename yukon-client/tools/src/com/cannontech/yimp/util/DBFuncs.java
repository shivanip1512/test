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
import com.cannontech.database.data.device.MCT410IL;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.PoolManager;
import java.util.Date;
import com.cannontech.common.util.CtiUtilities;

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
	public static Integer getRouteFromName(String name, Connection conn)
	{
		Integer routeID = new Integer(-12);
		java.sql.PreparedStatement preparedStatement = null;
		java.sql.ResultSet rset = null;
		
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");
		
		try
		{
			String statement = ("SELECT PAOBJECTID FROM YUKONPAOBJECT WHERE PAONAME = '" 
							+ name + "' AND PAOCLASS = 'ROUTE'");

			preparedStatement = conn.prepareStatement( statement );
			rset = preparedStatement.executeQuery();

			if(rset != null && rset.next() )
			{
				routeID = new Integer(rset.getInt(1));
			}
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		
		return routeID;
	}
	
	/*
	 * Grab an MCT410 using a PAOName, returns one with an ID of -12 if unsuccessful
	 * This should be changed to use cache at a later time
	 */
	public static MCT410IL get410FromTemplateName(String name, Connection conn)
	{
		MCT410IL template410 = new MCT410IL();
		template410.setDeviceID(new Integer(-12));
		Integer id = new Integer(1);
		java.sql.PreparedStatement preparedStatement = null;
		java.sql.ResultSet rset = null;
	
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");
	
		try
		{
			String statement = ("SELECT PAOBJECTID FROM YUKONPAOBJECT WHERE PAONAME = '" 
							+ name + "' AND TYPE = 'MCT-410IL'");
			preparedStatement = conn.prepareStatement( statement );
			rset = preparedStatement.executeQuery();
			if(rset != null && rset.next() )
			{
				id = new Integer(rset.getInt(1));
			
				template410.setDeviceID(id);
				template410.setDbConnection(conn);
				template410.retrieve();
			}
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
	
		return template410;
	}
	
	public static int[] getNextPAObjectID(int numberOfImportEntries)
	{
		int[] ids = YukonPAObject.getNextYukonPAObjectIDs( numberOfImportEntries );
		
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
		Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
	
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");

		try
		{
			java.sql.Statement stat = conn.createStatement();

			stat.execute("UPDATE DYNAMICIMPORTSTATUS SET LASTIMPORTTIME = '" + lastImport.toString() + "' WHERE ENTRY = 'SYSTEMVALUE'");
		
			if (stat != null)
				stat.close();
				
			conn.close();
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			return false;
		}

		return true;
	}
	
	public static boolean writeNextImportTime(Date nextImport)
	{
		Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
	
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");

		try
		{
			java.sql.Statement stat = conn.createStatement();

			stat.execute("UPDATE DYNAMICIMPORTSTATUS SET NEXTIMPORTTIME = '" + nextImport.toString() + "' WHERE ENTRY = 'SYSTEMVALUE'");
		
			if (stat != null)
				stat.close();
				
			conn.close();
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
		Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
	
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");

		try
		{
			java.sql.Statement stat = conn.createStatement();

			stat.execute("UPDATE DYNAMICIMPORTSTATUS SET TOTALSUCCESSES = " + success + " WHERE ENTRY = 'SYSTEMVALUE'");
		
			if (stat != null)
				stat.close();
				
			conn.close();
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
		Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
	
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");

		try
		{
			java.sql.Statement stat = conn.createStatement();

			stat.execute("UPDATE DYNAMICIMPORTSTATUS SET TOTALATTEMPTS = " + attempts + " WHERE ENTRY = 'SYSTEMVALUE'");
		
			if (stat != null)
				stat.close();
				
			conn.close();
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
		Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
	
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");

		try
		{
			java.sql.Statement stat = conn.createStatement();

			stat.execute("UPDATE DYNAMICIMPORTSTATUS SET FORCEIMPORT = 'N' WHERE ENTRY = 'SYSTEMVALUE'");
		
			if (stat != null)
				stat.close();
				
			conn.close();
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}
	
	public static boolean isForcedImport()
	{
		boolean isForced = false;
		
		
		Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
		
		java.sql.PreparedStatement preparedStatement = null;
		java.sql.ResultSet rset = null;
		
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");
		
		try
		{
			String statement = ("SELECT FORCEIMPORT FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'");

			preparedStatement = conn.prepareStatement( statement );
			rset = preparedStatement.executeQuery();

			while (rset.next() && rset != null)
			{
				isForced = rset.getString(1).compareTo("Y") == 0;
			}
			
			conn.close();
			
		}
		
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		
		return isForced;
	}
	
	
	
	
}