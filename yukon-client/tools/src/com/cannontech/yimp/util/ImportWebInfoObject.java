/*
 * Created on Feb 7, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yimp.util;
import java.util.Vector;
import java.sql.Connection;
import java.util.Date;
import java.util.GregorianCalendar;
import com.cannontech.database.PoolManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.importer.ImportFail;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ImportWebInfoObject 
{
	private Vector failures;
	
	public final String REFRESH_RATE = "10";
	
	public final String COLUMN_NAMES[] = 
	{ 
		"NAME", "REASONS FOR FAILURE", "TIME"
	};
		
	public ImportWebInfoObject()
	{
		super();
		
		//getFailures();
	}
	
	public synchronized Vector getFailures()
	{
		Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
		failures = ImportFuncs.getAllFailed(conn);
			
		try
		{
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();	
		}
	
		return failures;
	}
	
	public ImportFail getFailureAt(int index)
	{
		if(failures == null)
			failures = getFailures();
		
		return (ImportFail)getFailures().elementAt(index);
	}
	
	public String getFailName(int index)
	{
		return ((ImportFail)getFailureAt(index)).getName();
	}
	
	public String getErrorString(int index)
	{
		return ((ImportFail)getFailureAt(index)).getErrorMsg();
	}
	
	public String getFailureTime(int index)
	{
		return ((ImportFail)getFailureAt(index)).getDateTime().toString();
	}
	
	public synchronized String getLastImportTime()
	{
		String lastImportTime = new String("------------");
		
		Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
		
		java.sql.PreparedStatement preparedStatement = null;
		java.sql.ResultSet rset = null;
		
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");
		
		try
		{
			String statement = ("SELECT LASTIMPORTTIME FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'");

			preparedStatement = conn.prepareStatement( statement );
			rset = preparedStatement.executeQuery();

			while (rset.next() && rset != null)
			{
				lastImportTime = rset.getString(1);
			}
			
			if( conn != null )
				conn.close();
			
		}
		
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		
		return lastImportTime;
	}
	
	public synchronized String getNextImportTime()
	{
		String nextImportTime = new String("------------");
		
		Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
		
		java.sql.PreparedStatement preparedStatement = null;
		java.sql.ResultSet rset = null;
		
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");
		
		try
		{
			String statement = ("SELECT NEXTIMPORTTIME FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'");

			preparedStatement = conn.prepareStatement( statement );
			rset = preparedStatement.executeQuery();

			while (rset.next() && rset != null)
			{
				nextImportTime = rset.getString(1);
			}
			
			if( conn != null )
				conn.close();
			
		}
		
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		
		return nextImportTime;
	}
	
	public synchronized String getTotalSuccesses()
	{
		String totalSuccesses = new String("--");
		
		Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
		
		java.sql.PreparedStatement preparedStatement = null;
		java.sql.ResultSet rset = null;
		
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");
		
		try
		{
			String statement = ("SELECT TOTALSUCCESSES FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'");

			preparedStatement = conn.prepareStatement( statement );
			rset = preparedStatement.executeQuery();

			while (rset.next() && rset != null)
			{
				totalSuccesses = rset.getString(1);
			}
			
			if( conn != null )
				conn.close();
			
		}
		
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		
		return totalSuccesses;
	}
	
	public synchronized String getTotalAttempts()
	{
		String totalAttempts = new String("--");
		
		Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				
		java.sql.PreparedStatement preparedStatement = null;
		java.sql.ResultSet rset = null;
		
		if( conn == null )
			throw new IllegalArgumentException("Database connection should not be (null)");
		
		try
		{
			String statement = ("SELECT TOTALATTEMPTS FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'");

			preparedStatement = conn.prepareStatement( statement );
			rset = preparedStatement.executeQuery();

			while (rset.next() && rset != null)
			{
				totalAttempts = rset.getString(1);
			}
			
			if( conn != null )
				conn.close();
			
		}
		
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		
		return totalAttempts;
	}
	
	
	
}
