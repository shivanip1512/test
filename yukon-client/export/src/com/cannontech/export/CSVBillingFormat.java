package com.cannontech.export;

/**
 * Insert the type's description here.
 * Creation date: (2/29/00 10:16:47 AM)
 * @author: 
 */
public class CSVBillingFormat extends ExportFormatBase
{
	private java.util.Vector billingVector = null;
	private java.util.Hashtable customerHashtable = null;
	private Double [] baselineValues = null;
	private java.util.GregorianCalendar runDate = null;
	private String filePrefix = "OfferBill";
	private String fileExtension = ".csv";

	private java.text.SimpleDateFormat COMMAND_LINE_FORMAT = new java.text.SimpleDateFormat("MM/dd/yyyy");
	private java.text.SimpleDateFormat FILENAME_FORMAT = new java.text.SimpleDateFormat("-MM-dd-yyyy");
	
	private class EnergyNumbers
	{
		private String energyDebtor = "DebtorNumber";
		private String energyPremise = "PremiseNumber";
	}


/**
 * CommaDeleimited constructor comment.
 */
public CSVBillingFormat() {
	super();
}
public String appendBatchFileParms(String batchString)
{
	batchString += "com.cannontech.export.ExportFormatBase ";

	batchString += "FORMAT=" + ExportFormatTypes.CSVBILLING_FORMAT+" ";
	
	batchString += "FILE="+ getDirectory() + " " ;

	batchString += "START=" + COMMAND_LINE_FORMAT.format(getExportProperties().getMinTimestamp().getTime()) + " ";
	
	batchString += "STOP=" + COMMAND_LINE_FORMAT.format(getExportProperties().getMaxTimestamp().getTime()) + " ";
	
	batchString += "DELIMITER=\"" + getExportProperties().getDelimiter() +"\" ";
	
	return batchString;
}

/**
 * Insert the method's description here.
 * Creation date: (3/18/2002 4:13:26 PM)
 * @return java.lang.String
 */
public String getFileName()
{
	String name = getDirectory();
	name += filePrefix;
	name += FILENAME_FORMAT.format(getExportProperties().getMaxTimestamp().getTime());
	name += fileExtension;

	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (3/18/2002 3:36:13 PM)
 * @param args java.lang.String[]
 */
public void parseCommandLineArgs(String[] args)
{
	if( args.length > 0 )
	{
		boolean gotStart = false;
		boolean gotStop = false;
		
		for ( int i = 0; i < args.length; i++)
		{
			String argString = ((String)args[i]).toUpperCase();
			
			if( argString.startsWith("FILE"))
			{
				int startIndex = argString.indexOf("=") + 1;
				String subString = argString.substring(startIndex);

				setDirectory(subString);
				java.io.File file = new java.io.File( getDirectory() );
				file.mkdirs();
			}
			else if( argString.startsWith("DELIMITER"))
			{
				int startIndex = argString.indexOf("=") + 1;
				String subString = argString.substring(startIndex);
				for (int j = 0; j < subString.length(); j++)
				{
					char tempChar = subString.charAt(j);
					if (tempChar != '"')
						getExportProperties().setDelimiter(new Character(tempChar));
				}
			}
			else if( argString.startsWith("START"))
			{
				int startIndex = argString.indexOf("=") + 1;
				String subString = argString.substring(startIndex);
				java.util.Date date = null;
				java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
				try 
				{
					date = COMMAND_LINE_FORMAT.parse(subString);
				}
				catch (java.text.ParseException pe)
				{
					date = new java.util.Date();
					long time = date.getTime()  - 84600000;
					date.setTime(time);
					cal.setTime( date );
					cal.set(java.util.GregorianCalendar.HOUR_OF_DAY, 0);
					cal.set(java.util.GregorianCalendar.MINUTE, 0);
					cal.set(java.util.GregorianCalendar.SECOND, 0);
				}
				
				cal.setTime(date);
				
				getExportProperties().setMinTimestamp(cal);
				gotStart = true;
			}
			else if( argString.startsWith("STOP"))
			{
				int startIndex = argString.indexOf("=") + 1;
				String subString = argString.substring(startIndex);
				java.util.Date date = null;
				java.util.GregorianCalendar cal = new java.util.GregorianCalendar();

				try 
				{
					date = COMMAND_LINE_FORMAT.parse(subString);
				}
				catch (java.text.ParseException pe)
				{
					date = new java.util.Date();
					cal.setTime( date );
					cal.set(java.util.GregorianCalendar.HOUR_OF_DAY, 0);
					cal.set(java.util.GregorianCalendar.MINUTE, 0);
					cal.set(java.util.GregorianCalendar.SECOND, 0);
				}
				cal.setTime(date);
				
				getExportProperties().setMaxTimestamp(cal);
				gotStop = true;
			}
		}
		
		if(gotStop)	
		{
			if( !gotStart)	//Have stop but not start
			{
				java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
				java.util.Date date = getExportProperties().getMaxTimestamp().getTime();
				date.setTime(getExportProperties().getMaxTimestamp().getTime().getTime() - 86400000);
				cal.setTime(date);
				getExportProperties().setMinTimestamp(cal);
			}
			//else I'm good to go...already have both.
		}
		else
		{
			if( gotStart) //Have start but not stop
			{
				java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
				java.util.Date date = getExportProperties().getMinTimestamp().getTime();
				date.setTime(getExportProperties().getMinTimestamp().getTime().getTime() + 86400000);
				cal.setTime(date);
				getExportProperties().setMaxTimestamp(cal);
			}
		}
	}
	else
	{
		com.cannontech.clientutils.CTILogger.info("Usage:  CSVBilling FILE=FileDirectory START=mmddyyyy STOP=mmddyyyy");
		com.cannontech.clientutils.CTILogger.info("Ex.		CSVBilling FILE=c:/yukon/export/ START=02/28/2002 STOP=03/01/2002");
		com.cannontech.clientutils.CTILogger.info("Parameters not specifed will be defaulted to c:/yukon/export and \"yesterday\" run date.");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2002 5:07:44 PM)
 * @return java.lang.String[]
 */
public void retrieveBaselineData(int baselinePointID)
{
	long timer = System.currentTimeMillis();
	int rowCount = 0;
		
	StringBuffer sql = new StringBuffer	("SELECT RPH.VALUE, RPH.TIMESTAMP ");
	sql.append("FROM RAWPOINTHISTORY RPH");

	sql.append(" WHERE RPH.TIMESTAMP > ? AND RPH.TIMESTAMP <= ?");
	sql.append(" AND RPH.POINTID = " + baselinePointID);
	sql.append(" ORDER BY RPH.TIMESTAMP");
	
	java.sql.Connection conn = null;
	java.sql.PreparedStatement stmt = null;
	java.sql.ResultSet rset = null;

//	System.out.println("retrieveBaselineData: Min = " + getMinTimestamp().getTime() + " Max = " + getMaxTimestamp().getTime());
	//Initialize baselineValues for 24 vals and value = 0;
	baselineValues = new Double[24];
	for( int i = 0; i < baselineValues.length; i++)
	{
		baselineValues[i] = new Double(0);
	}

	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");

		if( conn == null )
		{
			com.cannontech.clientutils.CTILogger.info(getClass() + ":  Error getting database connection.");
			return;
		}
		else
		{
			stmt = conn.prepareStatement(sql.toString());
			stmt.setTimestamp(1, new java.sql.Timestamp(getExportProperties().getMinTimestamp().getTime().getTime()));
			stmt.setTimestamp(2, new java.sql.Timestamp(getExportProperties().getMaxTimestamp().getTime().getTime()));
			
			rset = stmt.executeQuery();
			while( rset.next())
			{
				Double value = new Double(rset.getDouble(1));
				java.util.GregorianCalendar offerDate = new java.util.GregorianCalendar();
				offerDate.setTime(rset.getTimestamp(2));

				int hour = offerDate.get(java.util.GregorianCalendar.HOUR_OF_DAY);
				baselineValues[hour] = value;
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
			if( stmt != null )
				stmt.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
	}
	logEvent("...BASELINE DATA RETRIEVED: Took " + (System.currentTimeMillis() - timer) + 
					" millis.", com.cannontech.common.util.LogWriter.INFO);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2002 5:07:44 PM)
 * @return java.lang.String[]
 */
public void retrieveBillingData(int keyId, com.cannontech.export.record.CSVBillingCustomerRecord csvBillingCust)
{
	long timer = System.currentTimeMillis();
	int rowCount = 0;
		
	StringBuffer sql = new StringBuffer	("SELECT LMEEHO.OFFERID, LMEEHO.REVISIONNUMBER");
	sql.append(", LMEEHO.PRICE, LMEEHC.AMOUNTCOMMITTED, RPH.TIMESTAMP, RPH.VALUE, RPH.POINTID, LMEEHO.HOUR ");
	sql.append("FROM LMENERGYEXCHANGEHOURLYCUSTOMER LMEEHC, ");
	sql.append("LMENERGYEXCHANGEPROGRAMOFFER LMEEPO, ");
	sql.append("LMENERGYEXCHANGEHOURLYOFFER LMEEHO, ");
	sql.append("RAWPOINTHISTORY RPH");

	sql.append(" WHERE LMEEHC.CUSTOMERID = " + keyId);
	sql.append(" AND LMEEPO.OFFERID = LMEEHO.OFFERID");
	sql.append(" AND LMEEPO.OFFERID = LMEEHC.OFFERID");
	sql.append(" AND LMEEHO.REVISIONNUMBER = LMEEHC.REVISIONNUMBER");
	sql.append(" AND LMEEHO.HOUR = LMEEHC.HOUR");
	sql.append(" AND LMEEHC.AMOUNTCOMMITTED > 0");
	sql.append(" AND LMEEHO.REVISIONNUMBER = (SELECT MAX(REVISIONNUMBER) FROM LMENERGYEXCHANGEHOURLYOFFER");
	sql.append(" WHERE LMEEHO.OFFERID = OFFERID");
	sql.append(" AND LMEEHO.HOUR = HOUR)");
	sql.append(" AND RPH.POINTID = " + csvBillingCust.getCurtailPointId());
	sql.append(" AND RPH.TIMESTAMP > ? AND RPH.TIMESTAMP <= ?");
	sql.append(" ORDER BY RPH.TIMESTAMP");
	
	java.sql.Connection conn = null;
	java.sql.PreparedStatement stmt = null;
	java.sql.ResultSet rset = null;
//	System.out.println("retrieveBillingData: Min = " + getMinTimestamp().getTime() + " Max = " + getMaxTimestamp().getTime());
	retrieveBaselineData(csvBillingCust.getBaselinePointId().intValue());


	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");

		if( conn == null )
		{
			com.cannontech.clientutils.CTILogger.info(getClass() + ":  Error getting database connection.");
			return;
		}
		else
		{
			stmt = conn.prepareStatement(sql.toString());
			stmt.setTimestamp(1, new java.sql.Timestamp(getExportProperties().getMinTimestamp().getTime().getTime()));
			stmt.setTimestamp(2, new java.sql.Timestamp(getExportProperties().getMaxTimestamp().getTime().getTime()));

			rset = stmt.executeQuery();
			while( rset.next())
			{
				//LMEEPO.OFFERID ||'-'||LMEEPO.REVISIONNUMBER")
				//LMEEHO.PRICE, LMEEHC.AMOUNTCOMMITTED, RPH.TIMESTAMP, RPH.VALUE, RPH.POINTID
				String offerID = rset.getString(1) + "-" + rset.getString(2);
				Double price = new Double(rset.getDouble(3));
				Double amtCommit = new Double(rset.getDouble(4));
				java.util.GregorianCalendar offerDate = new java.util.GregorianCalendar();
				offerDate.setTime(rset.getTimestamp(5));
				Double value = new Double(rset.getDouble(6));
				int ptId = rset.getInt(7);
				int hour = rset.getInt(8);

				boolean addRec = false;
				if( offerDate.get(java.util.GregorianCalendar.HOUR_OF_DAY) == hour)
				{
					if( offerDate.get(java.util.GregorianCalendar.MINUTE) > 0)
					{
						addRec = true;
					}
				}
				else if( offerDate.get(java.util.GregorianCalendar.HOUR_OF_DAY) == hour + 1)
				{
					if( offerDate.get(java.util.GregorianCalendar.MINUTE) == 0)
					{
						addRec = true;
					}
				}
				else if ( offerDate.get(java.util.GregorianCalendar.HOUR_OF_DAY) == hour - 23)	//00:00
				{
					if (offerDate.get(java.util.GregorianCalendar.MINUTE) == 0)
					{
						addRec = true;
					}
				}

				if( addRec)
				{
					rowCount++;
					com.cannontech.export.record.CSVBillingRecord csvBillingRec = new com.cannontech.export.record.CSVBillingRecord();
					csvBillingRec.setCustomerName(csvBillingCust.getCustomerName());
					csvBillingRec.setEnergyDebtor(csvBillingCust.getEnergyDebtor());
					csvBillingRec.setEnergyPremise(csvBillingCust.getEnergyPremise());
					csvBillingRec.setCurtailOffer(offerID);
					csvBillingRec.setMeterLocation(csvBillingCust.getMeterLocation());
					csvBillingRec.setCurtailDate(offerDate);
					csvBillingRec.setCurtailPeriodInterval(offerDate);
					csvBillingRec.setCurtailRate(price);
					csvBillingRec.setHDL((Double)baselineValues[hour]);
					csvBillingRec.setSCL(amtCommit);
					csvBillingRec.setADL(value);

					csvBillingRec.setDelimiter(getExportProperties().getDelimiter());
					
					String dataString = csvBillingRec.dataToString();
					getRecordVector().add(dataString);
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
			if( stmt != null )
				stmt.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
	}

	logEvent("SYSTEMLOG DATA COLLECTION: Took " + (System.currentTimeMillis() - timer) + 	" millis.", com.cannontech.common.util.LogWriter.INFO);
	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2002 5:07:44 PM)
 * @return java.lang.String[]
 */
public void retrieveCustomerData()
{
	long timer = System.currentTimeMillis();
	int rowCount = 0;
		
	StringBuffer sql = new StringBuffer	("SELECT PAO.PAONAME, PAO.PAOBJECTID");
	sql.append(", CC.POINTID, CC.COMPONENTPOINTID, DMG.METERNUMBER ");
	sql.append("FROM YUKONPAOBJECT PAO, ");
	sql.append("CUSTOMERBASELINEPOINT CBP, ");
	sql.append("CALCCOMPONENT CC, ");
	sql.append("POINT PT, ");
	sql.append("DEVICEMETERGROUP DMG ");

	sql.append(" WHERE PAO.PAOBJECTID = CBP.CUSTOMERID");
	sql.append(" AND CBP.POINTID = CC.POINTID");
	sql.append(" AND CC.POINTID = PT.POINTID");
	sql.append(" AND PT.PAOBJECTID = DMG.DEVICEID");

	java.sql.Connection conn = null;
	java.sql.PreparedStatement stmt = null;
	java.sql.ResultSet rset = null;

	java.util.Hashtable energyNumbersHashTable = retrieveEnergyNumbers("yukon");
	customerHashtable = new java.util.Hashtable(10);
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");

		if( conn == null )
		{
			com.cannontech.clientutils.CTILogger.info(getClass() + ":  Error getting database connection.");
			return;
		}
		else
		{
			stmt = conn.prepareStatement(sql.toString());
			rset = stmt.executeQuery();
			while( rset.next())
			{
				//SELECT PAO.PAONAME, PAO.PAOBJECTID, CC.POINTID, CC.COMPONENTPOINTID, DMG.METERNUMBER, CEN.ENERGYDEBTOR, CEN.ENERGYPREMISE  
				String paoName = rset.getString(1);
				Integer paoId = new Integer(rset.getInt(2));
				Integer baselinePtId = new Integer(rset.getInt(3));
				Integer curatailPtId = new Integer(rset.getInt(4));
				String meterLoc = rset.getString(5);
				
				EnergyNumbers nums = (EnergyNumbers)energyNumbersHashTable.get(paoName);
								
				String energyDebtor = nums.energyDebtor;
				String energyPremise = nums.energyPremise;

				com.cannontech.export.record.CSVBillingCustomerRecord csvBillingCust = new com.cannontech.export.record.CSVBillingCustomerRecord(
					paoName, meterLoc, energyDebtor, energyPremise, baselinePtId, curatailPtId);

				customerHashtable.put(paoId, csvBillingCust);
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
			if( stmt != null )
				stmt.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
	}
	logEvent("...CUSOTMER DATA RETRIEVED: Took " + (System.currentTimeMillis() - timer) + 
					" millis.", com.cannontech.common.util.LogWriter.INFO);
	return;
}
	/**
// This method creates a hash table of MeterNumbers (keys) and AccountNumbers(values).
 */
public java.util.Hashtable retrieveEnergyNumbers(String dbAlias)
{
	java.util.Vector linesInFile = new java.util.Vector();
	java.util.Hashtable energyNumberHashTable = null;
	
	if (dbAlias == null)
		dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
		
	try
	{
		java.io.FileReader energyNumbersFileReader = new java.io.FileReader(getExportProperties().getEnergyFileName());
		java.io.BufferedReader readBuffer = new java.io.BufferedReader(energyNumbersFileReader);

		try
		{
			String tempLineString = readBuffer.readLine();
						
			while(tempLineString != null)
			{
				linesInFile.add(new String(tempLineString));
				tempLineString = readBuffer.readLine();	
			}
		}
		catch(java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	catch(java.io.FileNotFoundException fnfe)
	{
		//fnfe.printStackTrace();
		com.cannontech.clientutils.CTILogger.info("***********************************************************************************************");
		com.cannontech.clientutils.CTILogger.info("Cannot find " + getExportProperties().getEnergyFileName().toString() + ", aborting.");
		com.cannontech.clientutils.CTILogger.info("***********************************************************************************************");
		return null;	//with null return, meternumbers will be used in place of accountnumbers
	}

	if(linesInFile != null)
	{	
		java.util.Collections.sort(linesInFile);
		int hashCapacity = (linesInFile.size() + 1);
		energyNumberHashTable = new java.util.Hashtable(hashCapacity);

		for (int i = 0; i < linesInFile.size(); i++)
		{
			String line = (String)linesInFile.get(i);
			int commaIndex = line.indexOf(",");

			java.util.StringTokenizer t = new java.util.StringTokenizer( line, (new Character('|')).toString(), false );
			EnergyNumbers nums = new EnergyNumbers();
			String custName = "";
			if( t.countTokens() != 3 )
				return null;	//bad line should we be more wrathfull?

			custName = t.nextToken().trim();
			nums.energyDebtor = t.nextToken().trim();
			nums.energyPremise = t.nextToken().trim();
							
//			String keyMeterNumber = line.substring(0, commaIndex);
//			String valueAccountNumber = line.substring(commaIndex + 1);
			energyNumberHashTable.put(custName, nums);
		}
	}
	return energyNumberHashTable;
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2002 5:07:44 PM)
 * @return java.lang.String[]
 */
public void retrieveExportData()
{
	if( getExportProperties().isShowColumnHeadings())
	{
		//Add a title record
		getRecordVector().add("Yukon Curtailment Settlement for " +
			getExportProperties().getMinTimestamp().getTime() + " - " + 
			getExportProperties().getMaxTimestamp().getTime() + "\r\n");
		
		//Add a column headings record
		getRecordVector().add(com.cannontech.export.record.CSVBillingRecord.getColumnHeadingsString());
		
	}

	logEvent("...Retrieving data for Date > " + getExportProperties().getMinTimestamp().getTime() +
		" AND <= " + getExportProperties().getMaxTimestamp().getTime(), com.cannontech.common.util.LogWriter.INFO);
	com.cannontech.clientutils.CTILogger.info("...Retrieving data for Date > " + getExportProperties().getMinTimestamp().getTime() +
		" AND <= " + getExportProperties().getMaxTimestamp().getTime());
		
	//Get a hashtable of records of all curtailment customers
	retrieveCustomerData();

	//Loop through all customers and get all curtailment data for the timeframe.
	java.util.Set keyset = customerHashtable.keySet();
	java.util.Iterator iter = keyset.iterator();
	while(iter.hasNext())
	{
		Integer keyid = (Integer)iter.next();
		com.cannontech.export.record.CSVBillingCustomerRecord custRec = (com.cannontech.export.record.CSVBillingCustomerRecord)customerHashtable.get(keyid);
		if( custRec != null)
		{
			retrieveBillingData(keyid.intValue(), custRec);
		}
	}

	//Set timestamps for next day.
	getExportProperties().setMinTimestamp(getExportProperties().getMaxTimestamp());
	
	java.util.GregorianCalendar newCal = new java.util.GregorianCalendar();	
	newCal.setTime(new java.util.Date(getExportProperties().getMaxTimestamp().getTime().getTime() + 86400000));
	getExportProperties().setMaxTimestamp(newCal);
	com.cannontech.clientutils.CTILogger.info(" * Next TimePeriod: " + getExportProperties().getMinTimestamp().getTime() + " - " + getExportProperties().getMaxTimestamp().getTime());
	//writeToFile();
}
}