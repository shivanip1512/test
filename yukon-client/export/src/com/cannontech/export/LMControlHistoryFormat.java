package com.cannontech.export;

import com.cannontech.export.record.LMControlHistoryRecord;
/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LMControlHistoryFormat extends ExportFormatBase
{
	public final String EXPORT_FILENAME = "lmctrlhist.csv";
	public final String LASTID_FILENAME = "\\LMCHID.DAT";	//LM Control History ID (last read).dat

	/**
	 * Constructor for LMControlHistoryFormat.
	 */
	public LMControlHistoryFormat()
	{
		super();
		setExportFileName(EXPORT_FILENAME);
		setLastIDFileName(LASTID_FILENAME);
	}

	/**
	 * @see com.cannontech.export.ExportFormatBase#parseDatFile()
	 */
	public void parseDatFile()
	{

		com.cannontech.common.util.KeysAndValuesFile kavFile = new com.cannontech.common.util.KeysAndValuesFile(com.cannontech.common.util.CtiUtilities.getConfigDirPath() + getDatFileName());
		
		com.cannontech.common.util.KeysAndValues keysAndValues = kavFile.getKeysAndValues();
		
		if( keysAndValues != null )
		{
			String keys[] = keysAndValues.getKeys();
			String values[] = keysAndValues.getValues();
			for (int i = 0; i < keys.length; i++)
			{
				if(keys[i].equalsIgnoreCase("DIR"))
				{
					setExportDirectory(values[i].toString());
					java.io.File file = new java.io.File( getExportDirectory() );
					file.mkdirs();
				}
				else if(keys[i].equalsIgnoreCase("FILE"))
				{
					setExportFileName(values[i].toString());
				}
				else if(keys[i].equalsIgnoreCase("INT"))
				{
					//INT parameter is in MINUTES but we need millis					
					long minuteInterval = Long.valueOf(values[i].trim()).longValue();
					long millisPerMinute  = 60L * 1000L;	//60 seconds * 1000 millis
					setRunTimeIntervalInMillis( minuteInterval * millisPerMinute);
				}
			}
		}
		else
		{
			// MODIFY THE LOG EVENT HERE!!!
			logEvent("Usage:  format=<formatID> dir=<exportfileDirectory> file=<exportFileName> int=<RunTimeIntervalInMinutes>", com.cannontech.common.util.LogWriter.INFO);
			logEvent("Ex.	  format=3 dir=c:/yukon/client/export/ file=export.csv int=30", com.cannontech.common.util.LogWriter.INFO);
			logEvent("** All parameters will be defaulted to the above if not specified", com.cannontech.common.util.LogWriter.INFO);
		}
		
	
	}

	/**
	 * @see com.cannontech.export.ExportFormatBase#retrieveExportData()
	 */
	public void retrieveData()
	{
		retrieveDynamicExportData();
		long timer = System.currentTimeMillis();
	
		StringBuffer sql = new StringBuffer("SELECT LMCTRLHISTID, PAOBJECTID, STARTDATETIME, SOE_TAG, CONTROLDURATION, CONTROLTYPE, ");
		sql.append(" CURRENTDAILYTIME, CURRENTMONTHLYTIME, CURRENTSEASONALTIME, CURRENTANNUALTIME, ACTIVERESTORE, REDUCTIONVALUE, STOPDATETIME ");
		sql.append(" FROM LMCONTROLHISTORY LMCH ");
		sql.append(" WHERE LMCTRLHISTID > " + getLastID());
		sql.append(" ORDER BY LMCTRLHISTID");
					
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		logEvent("LMControlHistory Log for Max LMCTRLHISTID = " + getLastID(), com.cannontech.common.util.LogWriter.INFO);
		
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
			if( conn == null )
			{
				logEvent(getClass() + ":  Error getting database connection.", com.cannontech.common.util.LogWriter.ERROR);
				return;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				rset = pstmt.executeQuery();
	
				logEvent(" *Start looping through return resultset", com.cannontech.common.util.LogWriter.INFO);
	
				int lastLogID = -1;
				while (rset.next())
				{
					int lmctrlhistid = rset.getInt(1);
					lastLogID = lmctrlhistid;
					int paobjectid = rset.getInt(2);
					java.sql.Timestamp startdatetime = rset.getTimestamp(3);
					int soe_tag = rset.getInt(4);
					int controlduration = rset.getInt(5);
					String controltype = rset.getString(6);
					int currentdailytime = rset.getInt(7);
					int currentmonthlytime = rset.getInt(8);
					int currentseasonaltime = rset.getInt(9);
					int currentannualtime = rset.getInt(10);
					String activerestore = rset.getString(11);
					double reductionvalue = rset.getDouble(12);
					java.sql.Timestamp stopdatetime = rset.getTimestamp(13);
					
					com.cannontech.database.db.pao.LMControlHistory lmctrlhist = new  com.cannontech.database.db.pao.LMControlHistory(new Integer(lmctrlhistid));
					lmctrlhist.setPaObjectID(new Integer(paobjectid));
					lmctrlhist.setStartDateTime(new java.util.Date(startdatetime.getTime()));
					lmctrlhist.setSoeTag(new Integer(soe_tag));
					lmctrlhist.setControlDuration(new Integer(controlduration));
					lmctrlhist.setControlType(controltype);
					lmctrlhist.setCurrentDailyTime(new Integer(currentdailytime));
					lmctrlhist.setCurrentMonthlyTime(new Integer(currentmonthlytime));
					lmctrlhist.setCurrentSeasonalTime(new Integer(currentseasonaltime));
					lmctrlhist.setCurrentAnnualTime(new Integer(currentannualtime));
					lmctrlhist.setActiveRestore(activerestore);
					lmctrlhist.setReductionValue(new Double(reductionvalue));
					lmctrlhist.setStopDateTime(new java.util.Date(stopdatetime.getTime()));
					
					LMControlHistoryRecord lmctrlhistRecord = new LMControlHistoryRecord(lmctrlhist);
	
					getRecordVector().addElement(lmctrlhistRecord);
				}
				
				if( lastLogID > 0)	//only write to file if we have actually collected new data.
					writeLastIDToFile(lastLogID);
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
				if( rset != null ) rset.close();
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				e2.printStackTrace();//sometin is up
			}	
		}
		logEvent("@" + this.toString() +" Data Collection : Took " + (System.currentTimeMillis() - timer) + " millis", com.cannontech.common.util.LogWriter.INFO);
	}

	/**
	 * @see com.cannontech.export.ExportFormatBase#buildKeysAndValues()
	 */
	public String[][] buildKeysAndValues()
	{
		String[] keys = new String[4];
		String[] values = new String[4];
		
		int i = 0; 
		keys[i] = "FORMAT";
		values[i++] = String.valueOf(ExportFormatTypes.LMCTRLHIST_EXPORT_FORMAT);
		
		keys[i] = "DIR";
		values[i++] = getExportDirectory();

		keys[i] = "FILE";
		values[i++] = getExportFileName();
		
		long millisPerMinute = 60L * 1000L;	//60 seconds * 1000millis
		keys[i] = "INT";
		values[i++] = String.valueOf(getRunTimeIntervalInMillis()/millisPerMinute);
		
		return new String[][]{keys, values};
	}
	/**
	 * @see com.cannontech.export.ExportFormatBase#retrieveExportData()
	 */
	public void retrieveDynamicExportData()
	{
		long timer = System.currentTimeMillis();
	
		StringBuffer sql = new StringBuffer("SELECT DLMP.DEVICEID, PROGRAMSTATE, STARTTIME, STOPTIME ");
		sql.append(" FROM DYNAMICLMPROGRAM DLMP, DYNAMICLMPROGRAMDIRECT DLMPD ");
		sql.append(" WHERE DLMP.DEVICEID = DLMPD.DEVICEID ");
					
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
			if( conn == null )
			{
				logEvent(getClass() + ":  Error getting database connection.", com.cannontech.common.util.LogWriter.ERROR);
				return;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				rset = pstmt.executeQuery();
	
				logEvent(" *Start looping through return resultset", com.cannontech.common.util.LogWriter.INFO);
	
				while (rset.next())
				{
					int deviceid = rset.getInt(1);
					int programstate = rset.getInt(2);
					java.sql.Timestamp starttime = rset.getTimestamp(3);
					java.util.Date start = new java.util.Date(starttime.getTime());
					java.sql.Timestamp stoptime = rset.getTimestamp(4);
					java.util.Date stop = new java.util.Date(stoptime.getTime());
					
					com.cannontech.export.record.DynamicLMProgramRecord dynamicLMPRecord =
							new com.cannontech.export.record.DynamicLMProgramRecord(deviceid, programstate);
	
					getRecordVector().addElement(dynamicLMPRecord);

					com.cannontech.export.record.DynamicLMProgramDirectRecord dynamicLMPDRecord =
							new com.cannontech.export.record.DynamicLMProgramDirectRecord(deviceid, start, stop);
	
					getRecordVector().addElement(dynamicLMPDRecord);
					
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
				if( rset != null ) rset.close();
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				e2.printStackTrace();//sometin is up
			}	
		}
		logEvent("@" + this.toString() +" Data Collection : Took " + (System.currentTimeMillis() - timer) + " millis", com.cannontech.common.util.LogWriter.INFO);
	}
	
}
