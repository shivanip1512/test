package com.cannontech.export;

import com.cannontech.export.record.DynamicLMProgramDirectRecord;
import com.cannontech.export.record.DynamicLMProgramRecord;
import com.cannontech.export.record.LMControlHistoryRecord;
/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LMControlHistoryImportFormat extends ExportFormatBase
{
	private final String EXPORT_FILENAME = "lmctrlhistEx.csv";
	private final String LASTID_FILENAME = "\\LMCHID.DAT";	//LM Control History ID (last read).dat
	private final String IMPORT_FILENAME = "lmctrlhist.csv";	//the file you are importing (which is the one you just exported...hopefully!)

	/**
	 * Constructor for LMControlHistoryImportFormat.
	 */
	public LMControlHistoryImportFormat()
	{
		super();
		setExportFileName(EXPORT_FILENAME);
		setLastIDFileName(LASTID_FILENAME);
		setImportFileName(IMPORT_FILENAME);
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
					setImportDirectory(values[i].toString());
					java.io.File file = new java.io.File( getImportDirectory() );
					file.mkdirs();
				}
				else if(keys[i].equalsIgnoreCase("FILE"))
				{
					setImportFileName(values[i].toString());
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
			logEvent("Usage:  format=<formatID> dir=<importFileDirectory> file=<importFileName> int=<RunTimeIntervalInMinutes>", com.cannontech.common.util.LogWriter.INFO);
			logEvent("Ex.	  format=4 dir=c:/yukon/client/export/ file=import.csv int=30", com.cannontech.common.util.LogWriter.INFO);
			logEvent("** All parameters will be defaulted to the above if not specified", com.cannontech.common.util.LogWriter.INFO);
		}
		
	
	}

	/**
	 * @see com.cannontech.export.ExportFormatBase#buildKeysAndValues()
	 */
	public String[][] buildKeysAndValues()
	{
		String[] keys = new String[6];
		String[] values = new String[6];
		
		int i = 0; 
		keys[i] = "FORMAT";
		values[i++] = String.valueOf(ExportFormatTypes.LMCTRLHIST_IMPORT_FORMAT);
		
		keys[i] = "DIR";
		values[i++] = getImportDirectory();

		keys[i] = "FILE";
		values[i++] = getImportFileName();
	
		long millisPerMinute = 60L * 1000L;	//60 seconds * 1000millis
		keys[i] = "INT";
		values[i++] = String.valueOf(getRunTimeIntervalInMillis()/millisPerMinute);
		
		return new String[][]{keys, values};
	}
	private void parseImportData()
	{
		for(int i = 0; i < getImportStringVector().size(); i++)
		{
			String line = (String)getImportStringVector().get(i);
			String recordType = line.substring(0, line.indexOf(','));
			
			com.cannontech.export.record.ImportRecordBase record = null;
			if( recordType.equalsIgnoreCase("LMCH"))
			{
				record = new LMControlHistoryRecord();
			}
			else if( recordType.equalsIgnoreCase("DLMP"))
			{
				record = new DynamicLMProgramRecord();
			}
			else if( recordType.equalsIgnoreCase("DLMPD"))
			{
				record = new DynamicLMProgramDirectRecord();
			}
			record.parse(line);
			getRecordVector().addElement(record);
		}
	}

	public void updateDatabase()
	{
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		
		try
		{		
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
			conn.setAutoCommit(false);
			
			int count = 0;
			String lastRecord = "";
			for(int i = 0; i < getRecordVector().size(); i++ )
			{
				com.cannontech.export.record.ImportRecordBase record = (com.cannontech.export.record.ImportRecordBase)getRecordVector().get(i);
				if( record == null)
					continue;
				
				if( lastRecord != record.getRecord())
				{
					lastRecord = record.getRecord();
					pstmt = conn.prepareStatement(record.getSqlString());
				}

				record.prepareStatement(pstmt);
				try
				{
					pstmt.execute();
				}
				catch( java.sql.SQLException sqle)
				{
					System.out.println(sqle.getMessage());
					count--;
				}
				count++;
			}

			conn.commit();
			com.cannontech.clientutils.CTILogger.info("Of " + getRecordVector().size()+ " records read, there were " + count + " database transactions");
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			}
			catch(java.sql.SQLException e2)
			{
				e2.printStackTrace();
			}
		}
	}
	
	public void retrieveData()
	{
		java.io.RandomAccessFile raFile = null;
		java.io.File inFile = new java.io.File( getImportDirectory() + getImportFileName());
		
		try
		{
			// open file		
			if( inFile.exists() )
			{
				raFile = new java.io.RandomAccessFile( inFile, "r" );
						
				long readLinePointer = 0;
				long fileLength = raFile.length();
	
				while ( readLinePointer < fileLength )  // loop until the end of the file
				{
					getImportStringVector().addElement(raFile.readLine());  // read a line in
					readLinePointer = raFile.getFilePointer();
				}
			}
			else
				return;
	
			// Close file
			raFile.close();						
		}
		catch( java.io.FileNotFoundException fnfe)
		{
			com.cannontech.clientutils.CTILogger.info("*** File Not Found Exception: " + getClass().toString());
		}
		catch( java.io.IOException ioe )
		{
			com.cannontech.clientutils.CTILogger.error( ioe.getMessage(), ioe );
		}
		finally
		{
			try
			{
				if( raFile != null)
					raFile.close();
			}
			catch (java.io.IOException e)
			{
				e.printStackTrace();		
			}
		}	
		
		parseImportData();
	}
}
