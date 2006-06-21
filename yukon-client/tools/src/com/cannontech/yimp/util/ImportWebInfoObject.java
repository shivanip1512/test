/*
 * Created on Feb 7, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yimp.util;
import java.util.Vector;

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
	
	public final String REFRESH_RATE = "45";
	
	public final String COLUMN_NAMES[] = 
	{ 
		"NAME", "REASONS FOR FAILURE", "TIME"
	};
		
	public ImportWebInfoObject()
	{
		super();
		
		//getFailures();
	}
	
	public Vector getFailures()
	{
		return ImportFuncs.getAllFailed();
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
	
	public String getLastImportTime()
	{
		String lastImportTime = new String("------------");
		
		com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement("SELECT LASTIMPORTTIME FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'",
													"yukon" );

		try
		{
			stmt.execute();

			if( stmt.getRowCount() > 0 )
			{
				lastImportTime = stmt.getRow(0)[0].toString();
			}
		}
		
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return lastImportTime;
	}
	
	public String getNextImportTime()
	{
		String nextImportTime = new String("------------");
		
		com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement("SELECT NEXTIMPORTTIME FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'",
													"yukon" );

		try
		{
			stmt.execute();
			
			if( stmt.getRowCount() > 0 )
			{
				nextImportTime = stmt.getRow(0)[0].toString();
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return nextImportTime;
	}
	
	public String getTotalSuccesses()
	{
		String totalSuccesses = new String("--");
		
		com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement("SELECT TOTALSUCCESSES FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'",
													"yukon" );
		try
		{
			stmt.execute();
				
			if( stmt.getRowCount() > 0 )
			{
				totalSuccesses = stmt.getRow(0)[0].toString();
			}
		}
		
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return totalSuccesses;
	}
	
	public String getTotalAttempts()
	{
		String totalAttempts = new String("--");
		
		com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement("SELECT TOTALATTEMPTS FROM DYNAMICIMPORTSTATUS WHERE ENTRY = 'SYSTEMVALUE'",
													"yukon" );
		
		try
		{
			stmt.execute();
			
			if( stmt.getRowCount() > 0 )
			{
				totalAttempts = stmt.getRow(0)[0].toString();
			}
		}
		
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return totalAttempts;
	}
	
	
	
}
