package com.cannontech.export;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class IONEventLogFormat extends ExportFormatBase
{
	private final char[] ignoreChars = new char[]{',', ' '};
	
	private final int VALID_PRIORITY = 51;
	private final int VALID_POINTID = 2600;
	private String fileName = "ioneventlog.csv";	//"c2000_control.csv";
	
	public static final String LASTLOGID_FILENAME = "\\IONELID.DAT";	//ION Event Log ID (last read).dat
	public static final String DIRECTORY = com.cannontech.common.util.CtiUtilities.getConfigDirPath();
	private long runTimeInterval = 1800000;	//30mins

	private class IONDescription
	{
		private Integer ion_pri = null;
		private String ion_cause = null;
		private String ion_effect = null;
		private String ion_nlog = null;
	}
	private class IONAction
	{
		private String ion_cause_handle = null;
		private String ion_effect_handle = null;
//		private int state = -1;		//NOT CURRENTLY USED   02/17/03 SN
		private Integer record = null;
	}

	/**
	 * Constructor for IONEventLogFormat.
	 */
	public IONEventLogFormat()
	{
		super();
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		int nowHour = cal.get(java.util.GregorianCalendar.HOUR_OF_DAY);
		System.out.println("NOW HOUR = " + nowHour);
		super.getExportProperties().setRunTimeHour(nowHour);
		super.setRunTimeInterval(runTimeInterval);
	}

	/**
	 * @see com.cannontech.export.ExportFormatBase#appendBatchFileParms(String)
	 */
	public String appendBatchFileParms(String batchString)
	{
		batchString += "com.cannontech.export.ExportFormatBase ";
	
		batchString += "FORMAT=" + ExportFormatTypes.IONEVENTLOG_FORMAT + " ";
		
		batchString += "FILE="+ getDirectory() + " " ;
	
		return batchString;
	}

	/**
	 * @see com.cannontech.export.ExportFormatBase#getFileName()
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * @see com.cannontech.export.ExportFormatBase#parseCommandLineArgs(String[])
	 */
	public void parseCommandLineArgs(String[] args)
	{		
		if( args.length > 0 )
		{
			for ( int i = 0; i < args.length; i++)
			{
				String argSubString = (String)args[i].substring(2);
				String argUpper = (String)args[i].toUpperCase();
				
				if( argUpper.startsWith("FILE"))
				{
					int startIndex = argUpper.indexOf("=") + 1;
					String subString = argUpper.substring(startIndex);
	
					setDirectory(subString);
					java.io.File file = new java.io.File( getDirectory() );
					file.mkdirs();
				}
				else if( args[i].startsWith("-f") || args[i].startsWith("-F"))
				{
					setDirectory( argSubString );
					java.io.File file = new java.io.File( getDirectory() );
					file.mkdirs();
				}
			}
		}
		else
		{
			logEvent("Usage:  format=<formatID> -fFileDirectory", com.cannontech.common.util.LogWriter.INFO);
			logEvent("Ex.		format=2 -fc:/yukon/client/export/", com.cannontech.common.util.LogWriter.INFO);
			logEvent("** All parameters will be defaulted to the above if not specified", com.cannontech.common.util.LogWriter.INFO);
		}
		
	}

		/**
		 * @see com.cannontech.export.ExportFormatBase#retrieveExportData()
		 */
		public void retrieveExportData()
		{
			long timer = System.currentTimeMillis();
		
			StringBuffer sql = new StringBuffer("SELECT LOGID, SL.POINTID, DATETIME, ACTION, SL.DESCRIPTION, USERNAME, PAO.PAONAME ");
			sql.append(" FROM SYSTEMLOG SL, POINT P, YUKONPAOBJECT PAO ");
			sql.append(" WHERE SL.POINTID = "+ VALID_POINTID);
			sql.append(" AND SL.POINTID = P.POINTID ");
			sql.append(" AND P.PAOBJECTID = PAO.PAOBJECTID ");
			sql.append(" AND LOGID > " + getLastEventLogID());
			sql.append(" ORDER BY LOGID");
						
			java.sql.Connection conn = null;
			java.sql.PreparedStatement pstmt = null;
			java.sql.ResultSet rset = null;
		
			logEvent("ION Event Log for Max Log ID = " + getLastEventLogID(), com.cannontech.common.util.LogWriter.INFO);
			
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
						String action = rset.getString(4);	
						//Parse action here!
						IONAction ionAction = getIONAction(action);
						Integer record = ionAction.record;
						String causeIon = ionAction.ion_cause_handle;
						String effectIon = ionAction.ion_effect_handle;					
						
						String desc = rset.getString(5);
						IONDescription ionDesc = getIONDescription(desc);
						//Parse description here!
						Integer priority = ionDesc.ion_pri;
						String causeValue = ionDesc.ion_cause;
						String effectValue = ionDesc.ion_effect;
						String nLog = ionDesc.ion_nlog;
	
						if (isValid(ionDesc) )
						{
							//ONLY continue on if we pass one of the checks:
							//effect like 'Control%' 
							//effect_ion like 'Notify%'
							//priority  == 51
							
							int logid = rset.getInt(1);
							lastLogID = logid;
							
							int pointid = rset.getInt(2);
							
							java.sql.Timestamp timestamp = rset.getTimestamp(3);
							java.util.Date tsDate = new java.util.Date();
							tsDate.setTime(timestamp.getTime());
							
							String user = rset.getString(6);
							String node = rset.getString(7);
							
							com.cannontech.export.record.IONEventLogRecord ionRecord =
								new com.cannontech.export.record.IONEventLogRecord(node, nLog, tsDate, priority, record, null, causeIon, causeValue, effectIon, effectValue);
		
							getRecordVector().addElement(ionRecord.dataToString());
						}
					}
					
					if( lastLogID > 0)	//only write to file if we have actually collected new data.
						writeLastLogIDToFile(lastLogID);
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

	private int getLastEventLogID()
	{
		int lastLogID = -1;
		java.io.RandomAccessFile raFile = null;
		java.io.File inFile = new java.io.File( DIRECTORY + LASTLOGID_FILENAME);	
		try
		{
			// open file		
			if( inFile.exists() )
			{
				raFile = new java.io.RandomAccessFile( inFile, "r" );

				//Believe we should only have one line to read!		
				String line = raFile.readLine();  // read a line in
				lastLogID = Integer.valueOf(line).intValue();

				// Close file
				raFile.close();						
			}
		}
		catch(java.io.IOException ex)
		{
			System.out.print("IOException in getLastEventLogID()");
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				//Try to close the file, again.
				if( inFile.exists() )
					raFile.close();
			}
			catch( java.io.IOException ex )
			{}		
		}
		return lastLogID;
	}	
	
	private void writeLastLogIDToFile(int maxLogID)
	{
		try
		{
			java.io.File file = new java.io.File( DIRECTORY );
			file.mkdirs();
			
			java.io.FileWriter writer = new java.io.FileWriter( file.getPath() + LASTLOGID_FILENAME);

			// write the max log id recorded
	 		writer.write( String.valueOf( maxLogID ) + "\r\n");
	
			writer.close();
		}
		catch ( java.io.IOException e )
		{
			System.out.print(" IOException in writeLastLogIDToFile()");
			e.printStackTrace();
		}
	}	
	
	private boolean isValid(IONDescription desc)
	{
		if (desc.ion_pri.compareTo(new Integer(VALID_PRIORITY)) != 0)
			return false;
		else if (! desc.ion_effect.toLowerCase().startsWith("control"))
			return false;
		else if (! desc.ion_cause.toLowerCase().startsWith("notify"))
			return false;
			
		return true;
	}
	
	private java.util.Vector getKeysAndValuesVector(String valueString)
	{
		java.util.Vector keysAndValues = new java.util.Vector(4);
		String tempString = new String(valueString);
		int endIndex = -1;

		exit:
		while (true)
		{
			//get endIndex
			endIndex = tempString.indexOf(',');
			if( endIndex < 0)
				endIndex = tempString.length();	//grab the last index possible.

			keysAndValues.add(tempString.substring(0, endIndex));
			
			boolean checkChar = true;
			
			while(checkChar)
			{
				checkChar = false;
				for(int i = 0; i < ignoreChars.length; i++)
				{
					if( tempString.length() == endIndex)
						break exit;
					
					if( tempString.charAt(endIndex) == ignoreChars[i])
					{
						endIndex++;
						checkChar = true;
					}
				}
			}
			tempString= tempString.substring(endIndex);	//remove the String already added to the vector.			
		}
		return keysAndValues;
	}		
		
	/**
	 * Sets the ionDescription(s).
	 * @param descString The string from SYSTEM LOG COLUMN
	 */
	private IONDescription getIONDescription(String descString)
	{
		java.util.Vector descVector = getKeysAndValuesVector(descString);

		IONDescription ion_desc = new IONDescription();
		for( int i = 0; i < descVector.size(); i++)
		{
			String keyAndValue = (String)descVector.get(i);
			int separatorIndex = keyAndValue.indexOf('=');
			if( separatorIndex < 0)
				break;	//not sure what our other options are at this point.
			
			String key = keyAndValue.substring(0, separatorIndex);
			String value = keyAndValue.substring(separatorIndex + 1, keyAndValue.length());
				
			if( key.equalsIgnoreCase("ion_pri"))
				ion_desc.ion_pri = Integer.valueOf(value.trim());
			else if( key.equalsIgnoreCase("ion_cause"))
				ion_desc.ion_cause = value.toString();
			else if( key.equalsIgnoreCase("ion_effect"))
				ion_desc.ion_effect = value.toString();
			else if( key.equalsIgnoreCase("ion_nlog"))
				ion_desc.ion_nlog = value.toString();
		}

		return ion_desc;
	}
	
	/**
	 * Sets the ionAction(s).
	 * @param actionString the string from SYSTEM LOG COLUMN
	 */
	private IONAction getIONAction(String actionString)
	{
		java.util.Vector actionVector = getKeysAndValuesVector(actionString);

		IONAction ion_action = new IONAction();
		for( int i = 0; i < actionVector.size(); i++)
		{
			String keyAndValue = (String)actionVector.get(i);
			int separatorIndex = keyAndValue.indexOf('=');
			if( separatorIndex < 0)
				break;	//not sure what our other options are at this point.

			
			String key = keyAndValue.substring(0, separatorIndex);
			String value = keyAndValue.substring(separatorIndex + 1, keyAndValue.length());
				
			if( key.equalsIgnoreCase("record"))
				ion_action.record = Integer.valueOf(value.trim());
			else if( key.equalsIgnoreCase("ion_cause_handle"))
				ion_action.ion_cause_handle = value.toString();
			else if( key.equalsIgnoreCase("ion_effect_handle"))
				ion_action.ion_effect_handle = value.toString();
		}

		return ion_action;
	}


	

}
