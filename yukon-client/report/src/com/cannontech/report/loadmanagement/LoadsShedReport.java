package com.cannontech.report.loadmanagement;

/**
 * Insert the type's description here.
 * Creation date: (3/21/2002 4:04:12 PM)
 * @author: 
 */
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.report.ReportBase;
import com.cannontech.report.ReportTypes;
public class LoadsShedReport extends ReportBase
{
	
	class TempControlAreaIdNameObject
	{
		// holds just the pao id and name of the control areas
		private Integer controlAreaId = null;
		private String controlAreaName = null;

		public Integer getControlAreaId()
		{
			return controlAreaId;
		}
		public String getControlAreaName()
		{
			return controlAreaName;
		}
		public void setControlAreaId(Integer paoId)
		{
			controlAreaId = paoId;
		}
		public void setControlAreaName(String name)
		{
			controlAreaName = name;
		}
	}	
/**
 * LoadsShedReport constructor comment.
 */
public LoadsShedReport()
{
	super();
}
public Vector getOutputStringsVector()
{
	if (outputStringsVector == null)
	{
	    outputStringsVector = new Vector();
		String previousControlAreaName = "(null)";
		String previousGroupName = "(null)";
		int linesOnCurrentPage = 0;
		
		//Get some lines anyway!!
		if( getRecordVector().isEmpty())
		{
		    LoadsShedRecord emptyRecord = new LoadsShedRecord();
		    if( linesOnCurrentPage == 0 )	//get page header
			{
				for(int j = 0; j < emptyRecord.getControlAreaHeaderVector().size(); j++)
				{
				    outputStringsVector.add((String)emptyRecord.getControlAreaHeaderVector().get(j) );
				    linesOnCurrentPage++;
				}
				outputStringsVector.add("\r\n");	//want a blank line
				outputStringsVector.add("Group: ");// + currentRecord.getPaoName());
				outputStringsVector.add("\r\n");
				for(int j = 0; j < emptyRecord.getGroupHeaderVector().size();j++)
				{
				    outputStringsVector.add( (String)emptyRecord.getGroupHeaderVector().get(j) );
					linesOnCurrentPage++;
				}
				outputStringsVector.add("\r\n");
				outputStringsVector.add(" *** NO DATA TO REPORT *** ");
				linesOnCurrentPage += 5;
			}
		}
		else
		{
			for(int i = 0; i < getRecordVector().size(); i++)
			{
				LoadsShedRecord currentRecord = (LoadsShedRecord)getRecordVector().get(i);
				String currentControlAreaName = currentRecord.getControlAreaName();
				String currentGroupName = currentRecord.getPaoName();
		
				if( linesOnCurrentPage >= MAX_LINES_PER_PAGE || !currentControlAreaName.equalsIgnoreCase(previousControlAreaName) )
				{
					if( !previousControlAreaName.equalsIgnoreCase("(null)") )
					{
					    outputStringsVector.add("\f");
					}
					previousControlAreaName = currentControlAreaName;
					linesOnCurrentPage = 0;
				}
		
				if( linesOnCurrentPage == 0 )	//get page header
				{
					previousGroupName = currentGroupName;
					for(int j = 0; j < currentRecord.getControlAreaHeaderVector().size(); j++)
					{
					    outputStringsVector.add((String)currentRecord.getControlAreaHeaderVector().get(j) );
					    linesOnCurrentPage++;
					}
					outputStringsVector.add("\r\n");	//want a blank line
					outputStringsVector.add("Group: " + currentRecord.getPaoName());
					outputStringsVector.add("\r\n");
					for(int j=0;j<currentRecord.getGroupHeaderVector().size();j++)
					{
					    outputStringsVector.add( (String)currentRecord.getGroupHeaderVector().get(j) );
						linesOnCurrentPage++;
					}
					outputStringsVector.add("\r\n");
					linesOnCurrentPage += 4;
				}
				else if( !currentGroupName.equalsIgnoreCase(previousGroupName) )
				{
					previousGroupName = currentGroupName;
					outputStringsVector.add("\r\n");
					outputStringsVector.add("Group: " + currentRecord.getPaoName());
					outputStringsVector.add("\r\n");
					for(int j = 0; j < currentRecord.getGroupHeaderVector().size(); j++)
					{
					    outputStringsVector.add( (String)currentRecord.getGroupHeaderVector().get(j) );
						linesOnCurrentPage++;
					}
					outputStringsVector.add("\r\n");
					linesOnCurrentPage += 4;
				}
		
				String dataString = currentRecord.dataToString();
				if( dataString != null)
				{
				    outputStringsVector.add(dataString);
					linesOnCurrentPage++;
				}
			}
		}
	}
	return outputStringsVector;
}

/**
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
public boolean retrieveReportData(String dbAlias)
{
	boolean returnBoolean = false;

	//Dump the old printLines vector
	outputStringsVector = null;

	if( dbAlias == null )
	{
		dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
	}

	java.sql.Connection conn = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
		if( conn == null )
		{
			com.cannontech.clientutils.CTILogger.info(getClass() + ":  Error getting database connection.");
			return false;
		}

		java.util.Vector controlAreaVector = new java.util.Vector();
		{
			StringBuffer sqlString = new StringBuffer("select paobjectid, paoname from yukonpaobject, lmcontrolarea where paobjectid = deviceid order by paoname");

			java.sql.PreparedStatement pstmt = conn.prepareStatement(sqlString.toString());
			java.sql.ResultSet rset = null;
			rset = pstmt.executeQuery();
			if( rset != null )
			{
				while(rset.next())
				{
					TempControlAreaIdNameObject tempObj = new TempControlAreaIdNameObject();
					tempObj.setControlAreaId(new Integer(rset.getInt(1)));
					tempObj.setControlAreaName(rset.getString(2));
					controlAreaVector.addElement(tempObj);
				}
			}
		}
		
		for(int i=0;i<controlAreaVector.size();i++)
		{
			StringBuffer sqlString2 = new StringBuffer("select pao.paobjectid, startdatetime, paoname, soe_tag, controltype, currentdailytime, " +
				" currentmonthlytime, currentseasonaltime, currentannualtime, activerestore, reductionvalue, stopdatetime " +
				" from yukonpaobject pao, lmcontrolhistory ch " +
				" where pao.paobjectid = ch.paobjectid " +
				" and (ch.activerestore = 'M' or ch.activerestore = 'T' or ch.activerestore = 'O') " +
				" and STARTDATETIME > ? and STARTDATETIME <= ? " +
				" and (pao.paobjectid in (select distinct lmgroupdeviceid " +
				" from lmcontrolareaprogram lmcap, lmprogramdirectgroup lmpdg " +
				" where lmcap.lmprogramdeviceid = lmpdg.deviceid " +
				" and lmcap.deviceid = ?) or " +
				" pao.paobjectid in (select childID from lmcontrolareaprogram lmcap, lmprogramdirectgroup lmpdg, genericmacro gm " +
				" where lmcap.lmprogramdeviceid = lmpdg.deviceid " +
				" and lmpdg.lmgroupdeviceid = gm.ownerid " +
				" and gm.macrotype = '" + MacroTypes.GROUP + "' " +				
				" and lmcap.deviceid = ?) ) " +
				" order by paoname, startdatetime, stopdatetime desc");
				
				//Deprecated query with the loss of the lmgroupmacroexpander_view
//				"(select paobjectid from lmgroupmacroexpander_view " +
//				"where (childid IS NULL AND paobjectid = lmgroupdeviceid OR " +
//				"NOT childid IS NULL AND paobjectid = childid) and " +
//				"deviceid in (select lmprogramdeviceid from lmcontrolareaprogram " +
//				"where deviceid = ?)) " +
				

			java.sql.PreparedStatement pstmt2 = conn.prepareStatement(sqlString2.toString());
			pstmt2.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTimeInMillis()));			
			pstmt2.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTimeInMillis()));
			pstmt2.setInt(3,((TempControlAreaIdNameObject)controlAreaVector.get(i)).getControlAreaId().intValue());
			pstmt2.setInt(4,((TempControlAreaIdNameObject)controlAreaVector.get(i)).getControlAreaId().intValue());
			java.sql.ResultSet rset2 = null;

			{
				rset2 = pstmt2.executeQuery();
				if( rset2 != null )
				{
					while(rset2.next())
					{
						Integer groupId = new Integer(rset2.getInt(1));
						java.util.GregorianCalendar startTime = new java.util.GregorianCalendar();
						startTime.setTime( new java.util.Date((rset2.getTimestamp(2)).getTime()) );
						String controlAreaName = ((TempControlAreaIdNameObject)controlAreaVector.get(i)).getControlAreaName();
						String groupName = rset2.getString(3);
						Integer soeTag = new Integer(rset2.getInt(4));
						String controlType = rset2.getString(5);
						Integer currentDailyTime = new Integer(rset2.getInt(6));
						Integer currentMonthlyTime = new Integer(rset2.getInt(7));
						Integer currentSeasonalTime = new Integer(rset2.getInt(8));
						Integer currentAnnualTime = new Integer(rset2.getInt(9));
						String activeRestore = rset2.getString(10);
						Double reductionValue = new Double(rset2.getDouble(11));
						GregorianCalendar stopTime = new GregorianCalendar();
						stopTime.setTime( new java.util.Date((rset2.getTimestamp(12)).getTime()) );

						LoadsShedRecord loadsShedRec = new LoadsShedRecord(controlAreaName, groupId,
										groupName, startTime, soeTag, controlType,
										currentDailyTime, currentMonthlyTime, currentSeasonalTime,
										currentAnnualTime, activeRestore, reductionValue, stopTime);
						getRecordVector().addElement(loadsShedRec);
					}
					returnBoolean = true;
				}
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
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			e2.printStackTrace();//sometin is up
		}	
	}

	return returnBoolean;
}


/**
 * Returns true if the line is a line from Group1Header
 * @param line
 * @return
 */
public boolean isGroup1HeaderLine(String line)
{
	if( line.startsWith("Group:") )	//a keyword for the Group name info
	{
	    groupLineCount = 1;
	    return true;
	}
	else if( groupLineCount < 4  && groupLineCount > 0)
	{
	    groupLineCount++;
	    return true;
	}
	groupLineCount = 0;	//reset the group line count, until the next grouping is found
    return false;
}

public boolean isPageHeaderLine(String line)
{
    if( line.startsWith("Printed on:"))//a keyword from the Page header info
    {
        pageLineCount = 1;
        return true;
    }
    else if( pageLineCount < 2 && pageLineCount > 0)
    {
        pageLineCount++;
        return true;
    }
    pageLineCount = 0;
    return false;
}

public String getReportName()
{
    return ReportTypes.REPORT_LOADS_SHED;
}
}


