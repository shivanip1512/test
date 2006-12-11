/*
 * Created on Feb 7, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yimp.util;
import java.sql.Connection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.cannontech.database.PoolManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.importer.ImportFail;
import com.cannontech.database.db.importer.ImportPendingComm;
import com.cannontech.database.SqlStatement;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ImportWebInfoObject 
{
	private List failures;
    private List pendingComms;
    private List failedComms;
	
	public final String REFRESH_RATE = "45";
	
	public final String FAILURE_COLUMN_NAMES[] = 
	{ 
		"NAME", "REASONS FOR FAILURE", "TIME"
	};
    
    public final String COMM_FAILURE_COLUMN_NAMES[] = 
    { 
        "NAME", "ROUTE", "SUBSTATION", "ERROR", "TIME"
    };
    
    public final String COMM_PENDING_COLUMN_NAMES[] = 
    { 
        "NAME", "ROUTE", "SUBSTATION"
    };
		
	public ImportWebInfoObject()
	{
		super();
		
		//getFailures();
	}
	
	public List getFailures()
	{
		return ImportFuncs.getAllDataFailures();
	}
	
	public ImportFail getFailureAt(int index)
	{
		if(failures == null)
			failures = getFailures();
		
		return (ImportFail)getFailures().get(index);
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
	
    public List getPendingComms() {
        return ImportFuncs.getAllPending();
    }
    
    public ImportPendingComm getPendingCommAt(int index) {
        if(pendingComms == null)
            pendingComms = getPendingComms();
        
        return (ImportPendingComm)getPendingComms().get(index);
    }
    
    public String getPendingName(int index) {
        return ((ImportPendingComm)getPendingCommAt(index)).getName();
    }
    
    public String getPendingRouteName(int index) {
        return ((ImportPendingComm)getPendingCommAt(index)).getRouteName();
    }
    
    public String getPendingSubstationName(int index) {
        return ((ImportPendingComm)getPendingCommAt(index)).getSubstationName();
    }
    
    public List getFailedComms() {
        return ImportFuncs.getAllCommunicationFailures();
    }
    
    public ImportFail getCommFailureAt(int index) {
        if(failedComms == null)
            failedComms = getFailedComms();
        
        return (ImportFail)getFailedComms().get(index);
    }
    
    public String getCommFailureName(int index) {
        return ((ImportFail)getCommFailureAt(index)).getName();
    }
    
    public String getCommFailureErrorString(int index) {
        return ((ImportFail)getCommFailureAt(index)).getErrorMsg();
    }
    
    public String getCommFailureTime(int index) {
        return ((ImportFail)getCommFailureAt(index)).getDateTime().toString();
    }
    
    public String getCommFailureRouteName(int index) {
        return ((ImportFail)getCommFailureAt(index)).getRouteName();
    }
    
    public String getCommFailureSubstationName(int index) {
        return ((ImportFail)getCommFailureAt(index)).getSubstationName();
    }
	
}
