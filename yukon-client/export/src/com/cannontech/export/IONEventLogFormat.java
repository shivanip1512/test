package com.cannontech.export;

public class IONEventLogFormat extends ExportFormatBase
{
	private final char[] ignoreChars = new char[]{',', ' '};
		
	private final int VALID_PRIORITY = 51;
	private final int VALID_POINTOFFSET = 2600;

	private final String EXPORT_FILENAME= "ioneventlog.csv";	//"c2000_control.csv";
	public final String LASTID_FILENAME = "\\IONELID.DAT";	//ION Event Log ID (last read).dat
	
	private long runTimeIntervalInMillis = 1800000;	//30mins

	//VALID EFFECT HANDLES MUST BE MAPPED TOGETHER WITH INT AND STRING VALUES.
	private int[] validEffectHandle = new int[]
	{
		26817,
		26818,
		26819,
		26821,
		24577,
		24578,
		24615,
		24616		
	};
	private String[] validEffectHandleString = new String[]
	{
		"Start Activate",
		"Start Test",
		"Deactivate",
		"Start Shop Test",
		"Notify Status",
		"Control Status",
		"Notify Relay",
		"Control Relay"
	};
	
	//VALID CAUSE HANDLES MUST BE MAPPED TOGETHER WITH INT AND STRING VALUES.
	private int[] validCauseHandle = new int[]
	{
		0,
		25017,
		25018,
		3072,
		3073
	};
	private String[] validCauseHandleString = new String[]
	{
		"",
		"Notify Output",
		"Control Output",
		"Comm 1",
		"Front Panel"
	};

	
	//Innerclass for Yukon.SystemLog.Description Column
	private class IONDescription
	{
		private Integer ion_pri = null;
		private String ion_cause = null;
		private String ion_effect = null;
		private String ion_nlog = null;
	}
	
	//Innerclass for Yukon.SystemLog.Action Column	
	private class IONAction
	{
		private String ion_cause_handle = null;
		private String ion_effect_handle = null;
		//private int state = -1;		//NOT CURRENTLY USED   02/17/03 SN
		private Integer record = null;
	}

	/**
	 * Constructor for IONEventLogFormat.
	 */
	public IONEventLogFormat()
	{
		super();
		super.setRunTimeIntervalInMillis(runTimeIntervalInMillis);
		super.setExportFileName(EXPORT_FILENAME);
		super.setLastIDFileName(LASTID_FILENAME);
		super.setIsAppend(true);
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
				else if(keys[i].equalsIgnoreCase("APPEND"))
				{
					setIsAppend(Boolean.valueOf(values[i].trim()).booleanValue());
				}
			}
		}
		else
		{
			// MODIFY THE LOG EVENT HERE!!!
			logEvent("Usage:  format=<formatID> dir=<exportfileDirectory> file=<exportFileName> int=<RunTimeIntervalInMinutes>", com.cannontech.common.util.LogWriter.INFO);
			logEvent("Ex.	  format=2 dir=c:/yukon/client/export/ file=export.csv int=30", com.cannontech.common.util.LogWriter.INFO);
			logEvent("** All parameters will be defaulted to the above if not specified", com.cannontech.common.util.LogWriter.INFO);
		}
		
	}		
	
	/**
	 * @see com.cannontech.export.ExportFormatBase#buildKeysAndValues()
	 */
	public String[][] buildKeysAndValues()
	{
		String[] keys = new String[5];
		String[] values = new String[5];
		
		int i = 0; 
		keys[i] = "FORMAT";
		values[i++] = String.valueOf(ExportFormatTypes.IONEVENTLOG_FORMAT);
		
		keys[i] = "DIR";
		values[i++] = getExportDirectory();

		keys[i] = "FILE";
		values[i++] = getExportFileName();
		
		long millisPerMinute = 60L * 1000L;	//60 seconds * 1000millis
		keys[i] = "INT";
		values[i++] = String.valueOf(getRunTimeIntervalInMillis()/millisPerMinute);
		
		keys[i] = "APPEND";
		values[i++] = String.valueOf(isAppend());
		
		return new String[][]{keys, values};
	}
	
	/**
	 * @see com.cannontech.export.ExportFormatBase#retrieveExportData()
	 */
	public void retrieveData()
	{
		long timer = System.currentTimeMillis();
	
		StringBuffer sql = new StringBuffer("SELECT LOGID, SL.POINTID, DATETIME, ACTION, SL.DESCRIPTION, USERNAME, PAO.PAONAME, DA.SLAVEADDRESS, DMG.BILLINGGROUP ");
		sql.append(" FROM SYSTEMLOG SL, POINT P, YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, DEVICEADDRESS DA ");
		sql.append(" WHERE P.POINTOFFSET = "+ VALID_POINTOFFSET);
		sql.append(" AND SL.POINTID = P.POINTID ");
		sql.append(" AND P.PAOBJECTID = PAO.PAOBJECTID ");
		sql.append(" AND PAO.PAOBJECTID = DMG.DEVICEID ");
		sql.append(" AND PAO.PAOBJECTID = DA.DEVICEID ");		
		sql.append(" AND LOGID > " + getLastID());
		sql.append(" ORDER BY LOGID");
					
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		logEvent("ION Event Log for Max Log ID = " + getLastID(), com.cannontech.common.util.LogWriter.INFO);
		
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
//					Integer record = ionAction.record;
					String causeIon = ionAction.ion_cause_handle;
					String effectIon = ionAction.ion_effect_handle;
					
					String desc = rset.getString(5);
					IONDescription ionDesc = getIONDescription(desc);
					//Parse description here!
					Integer priority = ionDesc.ion_pri;
					String causeValue = ionDesc.ion_cause;
					String effectValue = ionDesc.ion_effect;
					String nLog = ionDesc.ion_nlog;

					if (isValid(ionDesc, ionAction) )
					{
						int logid = rset.getInt(1);
						lastLogID = logid;
						
						int pointid = rset.getInt(2);
						
						java.sql.Timestamp timestamp = rset.getTimestamp(3);
						java.util.Date tsDate = new java.util.Date();
						tsDate.setTime(timestamp.getTime());
						
						String user = rset.getString(6);
						String paoName = rset.getString(7);
//						String meterNum = rset.getString(8);
						int slaveAdd = rset.getInt(8);
						String billGroup = rset.getString(9);
//						String node = billGroup + "." + paoName + "_" + meterNum;
						String node = billGroup + "." + paoName + "_" + String.valueOf(slaveAdd);
						
						com.cannontech.export.record.IONEventLogRecord ionRecord =
							new com.cannontech.export.record.IONEventLogRecord(node, nLog, tsDate, priority,new Integer(logid), null, causeIon, causeValue, effectIon, effectValue);
	
						getRecordVector().addElement(ionRecord);
					}
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
	 * Return true when desc parameters pass the validity checks.
	 * @param desc innerclass.IONDescription
	 * @return boolean
	 */
	private boolean isValid(IONDescription desc, IONAction action)
	{
		//ONLY continue on if we pass one of the checks:
		//effect like 'Control%' OR effect like 'Notify%' OR pri == 51
		
		if(desc != null && action!= null)
		{
			if (desc.ion_pri != null && desc.ion_pri.compareTo(new Integer(VALID_PRIORITY)) == 0)
				return true;
			else if (desc.ion_effect != null &&
					(action.ion_effect_handle.toLowerCase().indexOf("control status") >= 0 ||
						action.ion_effect_handle.toLowerCase().indexOf("notify status") >= 0))
				return true;
		}
		return false;
	}
	
	/**
	 * Return a vector of keys and values from valueString.
	 * Used for IONDescription and IONAction column data strings.
	 * @param valueString java.lang.String
	 * @return Vector
	 */
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
	 * @return IONDescription
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
			
			if( value.length() > 0)
			{	
				if( key.equalsIgnoreCase("pri"))
					ion_desc.ion_pri = Integer.valueOf(value.trim());
				else if( key.equalsIgnoreCase("cause"))
					ion_desc.ion_cause = value.toString();
				else if( key.equalsIgnoreCase("effect"))
					ion_desc.ion_effect = value.toString();
				else if( key.equalsIgnoreCase("nlog"))
					ion_desc.ion_nlog = value.toString();
			}
		}

		return ion_desc;
	}
	
	/**
	 * Sets the ionAction(s).
	 * @param actionString the string from SYSTEM LOG COLUMN
	 * @return IONAction
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
				
			if (value.length() > 0)
			{
				if( key.toLowerCase().startsWith("rec"))
					ion_action.record = Integer.valueOf(value.trim());
				else if( key.equalsIgnoreCase("c_h"))
					ion_action.ion_cause_handle = getValidCauseHandle(Integer.valueOf(value.trim()).intValue());
				else if( key.equalsIgnoreCase("e_h"))
					ion_action.ion_effect_handle = getValidEffectHandle(Integer.valueOf(value.trim()).intValue());
			}
		}

		return ion_action;
	}
	
	/**
	 * Return the String value of the int value effectHandle.
	 * The database only stores the int value but the record needs the string value.
	 * @param effectHandle
	 * @return String
	 */
	private String getValidEffectHandle(int effectHandle)
	{
		for(int i = 0; i < validEffectHandle.length; i++)
		{
			if( effectHandle == validEffectHandle[i])
				return validEffectHandleString[i];
		}
		return String.valueOf(effectHandle);
	}
	
	/**
	 * Return the String value of the int value causeHandle.
	 * The database only stores the int value but the record needs the string value.
	 * @param causeHandle
	 * @return String
	 */
	private String getValidCauseHandle(int causeHandle)
	{
		for(int i = 0; i < validCauseHandle.length; i++)
		{
			if( causeHandle == validCauseHandle[i])
				return validCauseHandleString[i];
		}
		return String.valueOf(causeHandle);	//default value back...I guess.
	}
}
