/*
 * Created on Oct 20, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.tools.custom;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MeterNumberUpdate
{
	//Contains values of Pairs(<String(accountNumber), String(meterNumber)>)
	private Vector accountNumMeterNumPair = null;
	
	//Contains values of Pairs(<String(accountNumber), String(meterNumber)>)...of not found accountnumbers
	private Vector missedAccountNumMeterNumPair = null;
	
	//Contains values of <paoName, LiteYukonPaobject> where paoName represents the accountnumber
	private Map accountNumMap = null;

	//Vector of String (SQL Update Statements) for exact matches
	private Vector sqlUpdates = null;
	
	//Vector of String (SQL Update Statements) for partial matches
	private Vector sqlUpdatesPartial = null;
	
	private String acctNumMeterNumFileName = "";
	private String sqlFileName = "";
	private String sqlPartialFileName = "";
	private String missedAccountNumMeterNumFileName = "";
		
	public static void main(String[] args)
	{
		MeterNumberUpdate updater = new MeterNumberUpdate();
		if( args.length > 0 )
		{
			for (int i = 0; i < args.length; i++)
			{
				// Check the delimiter of '=', if not found check ':'
				int startIndex = args[i].indexOf(':');
				String key = args[i].substring(0, startIndex).toLowerCase();
				startIndex += 1;
				String value = args[i].substring(startIndex);
				
				if( key.startsWith("to"))
				{
					updater.setSQLFileName(value);
				}
				else if( key.startsWith("from"))
				{
					updater.setAcctNumMeterNumFileName(value);
				}
			}
		}
	
		if(updater.getSQLFileName() =="" || updater.getAcctNumMeterNumFileName() =="")
		{
			CTILogger.info("EXITTING..No file names provided on startup.");
			CTILogger.info("MeterNumberUpdate.bat from:c:/yukon/client/log/acctNumMetNum.txt to:c:/yukon/client/export/acctNumMetNumUpdates.txt");
			
			System.exit(0);
		}
		updater.retrieveFromFile();
		updater.updateMeterNumbers();
		updater.writeVectorToFile(updater.getSQLFileName(), updater.getSqlUpdates());
		updater.writeVectorToFile(updater.sqlPartialFileName, updater.getSqlUpdatesPartial());
		updater.writeVectorToFile(updater.getMissedAccountNumMeterNumFileName(), updater.getMissedAccountNumMeterNumPair());

		System.exit(0);
	}
	/**
	 * 
	 */
	public void retrieveFromFile()
	{
		CTILogger.info("Retrieving AccountNumber and MeterNumber Pairs from file: " + getAcctNumMeterNumFileName());
		File file = new File(getAcctNumMeterNumFileName());	
		RandomAccessFile raFile = null;
		try
		{
			// open file		
			if( file.exists() )
			{
				raFile = new RandomAccessFile( file, "r" );
				
				long readLinePointer = 0;
				long fileLength = raFile.length();
				
				while ( readLinePointer < fileLength )  // loop until the end of the file
				{
					String line = raFile.readLine();  // read a line in
					int delimIndex = line.indexOf(',');
					if(delimIndex > 0)
					{
						String accountNum = line.substring(0, delimIndex).trim();
						String meterNum = line.substring(delimIndex+1).trim();
						Pair pair = new Pair(accountNum, meterNum);
						getAccountNumMeterNumPair().add(pair);
					}
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
			CTILogger.info("*** File Not Found Exception:");
			CTILogger.info("  " + fnfe.getMessage());
			CTILogger.error( fnfe.getMessage(), fnfe );
		}
		catch( java.io.IOException ioe )
		{
			CTILogger.error( ioe.getMessage(), ioe );
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
	}
	
	public void updateMeterNumbers()
	{
		for (int i = 0; i < getAccountNumMeterNumPair().size(); i++)
		{
			Pair pair = (Pair)getAccountNumMeterNumPair().get(i);
			if( getAccountNumMap().get(pair.getFirst()) != null)
			{
				String sql = "UPDATE DEVICEMETERGROUP SET METERNUMBER = \'" + pair.getSecond().toString() + "\' " + 
								"FROM YUKONPAOBJECT PAO WHERE DEVICEID = PAO.PAOBJECTID "+
								"AND PAO.PAONAME = \'"+pair.getFirst().toString() + "\' ";
				getSqlUpdates().add(sql);
			}
			else
			{
				getMissedAccountNumMeterNumPair().addElement(pair);

				String acctNum = pair.getFirst().toString();
				int lastIndexNoRotation = acctNum.length()-1;
				String acctNumNoRotation = acctNum.substring(0,lastIndexNoRotation);
				
				String sql = "UPDATE DEVICEMETERGROUP SET METERNUMBER = \'" + pair.getSecond().toString() + "\' " + 
								"FROM YUKONPAOBJECT PAO WHERE DEVICEID = PAO.PAOBJECTID "+
								"AND PAO.PAONAME like \'"+ acctNumNoRotation + "%\' ";
				getSqlUpdatesPartial().add(sql);
			}
		}
	}
	
	public void loadAccountNumberMap()
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		List paos = cache.getAllYukonPAObjects();
		
		for (int i = 0; i < paos.size(); i++)
		{
			LiteYukonPAObject lPao = (LiteYukonPAObject)paos.get(i);
			
			getAccountNumMap().put(lPao.getPaoName(), lPao);
		}
	}
	
	public Map getAccountNumMap()
	{
		if( accountNumMap == null)
		{
			accountNumMap = new HashMap();
			loadAccountNumberMap();
		}
		return accountNumMap;
	}
	
	public void writeVectorToFile(String fileName, Vector writeLines )
	{
		CTILogger.info("Writing "+ writeLines.size() + " lines to file: " + fileName);
		try
		{
			int index = fileName.lastIndexOf('/');
			if( index < 0)
				index = fileName.lastIndexOf('\\');
			String path = fileName.substring(0, index);
		
			java.io.File file = new java.io.File(path);
			file.mkdirs();

			java.io.FileWriter writer = new java.io.FileWriter( fileName);
			String endline = "\r\n";

			if( writeLines!= null)
			{
				for (int i = 0; i < writeLines.size(); i++)
				{
					Object obj = writeLines.get(i);
					if( obj instanceof String)
						writer.write(obj.toString() + endline);
					else if( obj instanceof Pair)
					{
						writer.write( ((Pair)obj).getFirst().toString() + ","+ ((Pair)obj).getSecond().toString()+endline);
					}
				}
			}
		
			writer.close();
		}
		catch ( java.io.IOException e )
		{
			System.out.print(" IOException in writeDatFile");
			e.printStackTrace();
		}
	}
	/**
	 * @return
	 */
	public Vector getAccountNumMeterNumPair()
	{
		if( accountNumMeterNumPair == null)
			accountNumMeterNumPair = new Vector();

		return accountNumMeterNumPair;
	}

	/**
	 * @return
	 */
	public Vector getMissedAccountNumMeterNumPair()
	{
		if( missedAccountNumMeterNumPair == null)
			missedAccountNumMeterNumPair = new Vector();

		return missedAccountNumMeterNumPair;
	}

	/**
	 * @return
	 */
	public String getAcctNumMeterNumFileName()
	{
		return acctNumMeterNumFileName;
	}

	public String getMissedAccountNumMeterNumFileName()
	{
		return missedAccountNumMeterNumFileName;
	}

	/**
	 * @return
	 */
	public Vector getSqlUpdates()
	{
		if( sqlUpdates == null)
			sqlUpdates = new Vector();

		return sqlUpdates;
	}

	/**
	 * @return
	 */
	public Vector getSqlUpdatesPartial()
	{
		if( sqlUpdatesPartial == null)
			sqlUpdatesPartial = new Vector();

		return sqlUpdatesPartial;
	}

	/**
	 * @return
	 */
	public String getSQLFileName()
	{
		return sqlFileName;
	}

	/**
	 * @return
	 */
	public String getSQLPartialFileName()
	{
		return sqlPartialFileName;
	}
	
	/**
	 * @param string
	 */
	public void setAcctNumMeterNumFileName(String string)
	{
		acctNumMeterNumFileName = string;
	}

	/**
	 * @param vector
	 */
	public void setSqlUpdates(Vector vector)
	{
		sqlUpdates = vector;
	}

	/**
	 * @param vector
	 */
	public void setSqlUpdatesPartial(Vector vector)
	{
		sqlUpdatesPartial = vector;
	}

	/**
	 * @param string
	 */
	public void setSQLFileName(String string)
	{
		sqlFileName = string;
		CTILogger.info("Setting sqlFileName: "+ sqlFileName);
		
		int dot = string.lastIndexOf('.');
		String ext = string.substring(dot);
		
		sqlPartialFileName = string.substring(0, dot-1)+"_Partial" + ext;
		CTILogger.info("Setting sqlPartialFileName: "+ sqlPartialFileName);

		missedAccountNumMeterNumFileName = string.substring(0, dot-1)+"_MissedPairs" + ext;
		CTILogger.info("Setting missedAccountNumMeterNumFileName: "+ missedAccountNumMeterNumFileName);

	}
}
