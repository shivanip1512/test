package com.cannontech.report.loadmanagement;

/**
 * Insert the type's description here.
 * Creation date: (3/21/2002 4:04:12 PM)
 * @author: 
 */
import com.cannontech.report.ReportBase;
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
/**
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
public com.klg.jclass.page.JCFlow getFlow(com.klg.jclass.page.JCDocument doc)
{
	com.klg.jclass.page.JCFlow returnFlow = new com.klg.jclass.page.JCFlow(doc);

	java.util.Vector records = getRecordVector();

	java.awt.Font controlAreaHeaderFont = new java.awt.Font( "Monospaced", java.awt.Font.BOLD, 12 );
	java.awt.Font groupHeaderFont = new java.awt.Font( "Monospaced", java.awt.Font.BOLD, 8 );
	java.awt.Font normalFont = new java.awt.Font( "Monospaced", java.awt.Font.PLAIN, 8 );

	com.klg.jclass.page.JCTextStyle currentTextStyle = new com.klg.jclass.page.JCTextStyle("Current");
	currentTextStyle.setParagraphSpacing(1);
	returnFlow.setCurrentTextStyle( currentTextStyle );

	String previousControlAreaName = "(null)";
	String previousGroupName = "(null)";
	int linesOnCurrentPage = 0;
	for(int i=0;i<records.size();i++)
	{
		LoadsShedRecord currentRecord = (LoadsShedRecord)records.get(i);
		String currentControlAreaName = currentRecord.getControlAreaName();
		String currentGroupName = currentRecord.getPaoName();

		if( linesOnCurrentPage >= 53 || !currentControlAreaName.equalsIgnoreCase(previousControlAreaName) )
		{
			if( !previousControlAreaName.equalsIgnoreCase("(null)") )
			{
				returnFlow.newPage();
			}
			previousControlAreaName = currentControlAreaName;
			linesOnCurrentPage = 0;
		}

		if( linesOnCurrentPage == 0 )
		{
			previousGroupName = currentGroupName;
			currentTextStyle.setFont(controlAreaHeaderFont);
			for(int j=0;j<currentRecord.getControlAreaHeaderVector().size();j++)
			{
				returnFlow.print( (String)currentRecord.getControlAreaHeaderVector().get(j) );
				returnFlow.newLine();
				returnFlow.newLine();
			}
			currentTextStyle.setFont(groupHeaderFont);
			returnFlow.print("Group: " + currentRecord.getPaoName());
			returnFlow.newLine();
			returnFlow.newLine();
			for(int j=0;j<currentRecord.getGroupHeaderVector().size();j++)
			{
				returnFlow.print( (String)currentRecord.getGroupHeaderVector().get(j) );
				returnFlow.newLine();
			}
			returnFlow.newLine();
			linesOnCurrentPage += 7;
		}
		else if( !currentGroupName.equalsIgnoreCase(previousGroupName) )
		{
			previousGroupName = currentGroupName;
			returnFlow.newLine();
			currentTextStyle.setFont(groupHeaderFont);
			returnFlow.print("Group: " + currentRecord.getPaoName());
			returnFlow.newLine();
			returnFlow.newLine();
			for(int j=0;j<currentRecord.getGroupHeaderVector().size();j++)
			{
				returnFlow.print( (String)currentRecord.getGroupHeaderVector().get(j) );
				returnFlow.newLine();
			}
			returnFlow.newLine();
			linesOnCurrentPage += 7;
		}

		String dataString = currentRecord.dataToString();
		if( dataString != null)
		{
			currentTextStyle.setFont(normalFont);
			returnFlow.print(dataString);
			returnFlow.newLine();
			linesOnCurrentPage++;
		}
	}

	return returnFlow;
}
/**
 * Insert the method's description here.
 * Creation date: (11/29/00)
 */
public StringBuffer getOutputAsStringBuffer()
{
	StringBuffer returnBuffer = new StringBuffer();

	java.util.Vector records = getRecordVector();

	String previousControlAreaName = "(null)";
	String previousGroupName = "(null)";
	int linesOnCurrentPage = 0;
	for(int i=0;i<records.size();i++)
	{
		LoadsShedRecord currentRecord = (LoadsShedRecord)records.get(i);
		String currentControlAreaName = currentRecord.getControlAreaName();
		String currentGroupName = currentRecord.getPaoName();

		if( linesOnCurrentPage >= 55 || !currentControlAreaName.equalsIgnoreCase(previousControlAreaName) )
		{
			if( !previousControlAreaName.equalsIgnoreCase("(null)") )
			{
				returnBuffer.append( (char)12 );//form feed
			}
			previousControlAreaName = currentControlAreaName;
			linesOnCurrentPage = 0;
		}

		if( linesOnCurrentPage == 0 )
		{
			previousGroupName = currentGroupName;
			for(int j=0;j<currentRecord.getControlAreaHeaderVector().size();j++)
			{
				returnBuffer.append( (String)currentRecord.getControlAreaHeaderVector().get(j) );
				returnBuffer.append("\r\n");
				returnBuffer.append("\r\n");
			}
			for(int j=0;j<currentRecord.getGroupHeaderVector().size();j++)
			{
				returnBuffer.append( (String)currentRecord.getGroupHeaderVector().get(j) );
				returnBuffer.append("\r\n");
			}
			returnBuffer.append("\r\n");
			linesOnCurrentPage += 5;
		}
		else if( !currentGroupName.equalsIgnoreCase(previousGroupName) )
		{
			previousGroupName = currentGroupName;
			returnBuffer.append("\r\n");
			for(int j=0;j<currentRecord.getGroupHeaderVector().size();j++)
			{
				returnBuffer.append( (String)currentRecord.getGroupHeaderVector().get(j) );
				returnBuffer.append("\r\n");
			}
			returnBuffer.append("\r\n");
			linesOnCurrentPage += 5;
		}

		String dataString = currentRecord.dataToString();
		if( dataString != null)
		{
			returnBuffer.append(dataString);
			returnBuffer.append("\r\n");
			linesOnCurrentPage++;
		}
	}

	return returnBuffer;
}
/**
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
public boolean retrieveReportData(String dbAlias)
{
	boolean returnBoolean = false;

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
			System.out.println(getClass() + ":  Error getting database connection.");
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
						"currentmonthlytime, currentseasonaltime, currentannualtime, activerestore, reductionvalue, stopdatetime " +
						"from yukonpaobject pao, lmcontrolhistory ch " +
						"where pao.paobjectid = ch.paobjectid and " +
						"(ch.activerestore = 'M' or ch.activerestore = 'T' or ch.activerestore = 'O') and " +
						"STARTDATETIME > ? and STARTDATETIME <= ? and " +
						"pao.paobjectid in (select paobjectid from lmgroupmacroexpander_view " +
						"where (childid IS NULL AND paobjectid = lmgroupdeviceid OR " +
						"NOT childid IS NULL AND paobjectid = childid) and " +
						"deviceid in (select lmprogramdeviceid from lmcontrolareaprogram " +
						"where deviceid = ?)) " +
						"order by paoname, startdatetime, stopdatetime desc");

			java.sql.PreparedStatement pstmt2 = conn.prepareStatement(sqlString2.toString());
			pstmt2.setObject(1,new java.sql.Date(getStartDate().getTime().getTime()));
			pstmt2.setObject(2,new java.sql.Date(getStopDate().getTime().getTime()));
			pstmt2.setInt(3,((TempControlAreaIdNameObject)controlAreaVector.get(i)).getControlAreaId().intValue());
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
						java.util.GregorianCalendar stopTime = new java.util.GregorianCalendar();
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
}
