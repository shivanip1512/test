package com.cannontech.export;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.calchist.Baseline;
import com.cannontech.calchist.HoursAndValues;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.point.PointOffsets;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.export.record.CSVBillingCustomerRecord;
import com.cannontech.export.record.CSVBillingRecord;
import com.cannontech.export.record.StringRecord;
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
	 * CSVBillingFormat constructor comment.
	 */
	public CSVBillingFormat() {
		super();
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
			boolean gotStart = false;
			boolean gotStop = false;
	
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
				else if( keys[i].equalsIgnoreCase("DELIMITER"))
				{
					getExportProperties().setDelimiter(new Character(values[i].charAt(0)));
				}
				else if( keys[i].equalsIgnoreCase("HEADINGS"))
				{
					getExportProperties().setShowColumnHeadings(Boolean.valueOf(values[i]).booleanValue());
				}
				else if( keys[i].equalsIgnoreCase("START"))
				{
					java.util.Date date = null;
					java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
					try
					{
						date = COMMAND_LINE_FORMAT.parse(values[i]);
					}
					catch( java.text.ParseException pe)
					{
						date = new java.util.Date();
						long time = date.getTime() - 84600000;
						date.setTime(time);
						cal.setTime(date);
						cal.set(java.util.GregorianCalendar.HOUR_OF_DAY, 0);
						cal.set(java.util.GregorianCalendar.MINUTE, 0);
						cal.set(java.util.GregorianCalendar.SECOND, 0);
					}
					cal.setTime(date);
				
					getExportProperties().setMinTimestamp(cal);
					gotStart = true;
				}
				else if( keys[i].equalsIgnoreCase("STOP"))
				{
					java.util.Date date = null;
					java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	
					try 
					{
						date = COMMAND_LINE_FORMAT.parse(values[i]);
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
			// MODIFY THE LOG EVENT HERE!!!
			logEvent("Usage:  format=<formatID> dir=<exportfileDirectory> energyfile=<energyDataFile> delimiter=<delChar> headings=<boolean> start=<mm/dd/yyyy> stop=<mm/dd/yyyy>", com.cannontech.common.util.LogWriter.INFO);
			logEvent("Ex.	  format=0 dir=c:/yukon/client/export/ energyfile=c:/yukon/client/config/nums.txt delimiter=',' headings=false start=mm/dd/yyyy stop=mm/dd/yyyy", com.cannontech.common.util.LogWriter.INFO);
			logEvent("** All parameters will be defaulted to the above if not specified", com.cannontech.common.util.LogWriter.INFO);
		}
		
	}		
	
	/**
	 * @see com.cannontech.export.ExportFormatBase#buildKeysAndValues()
	 */
	public String[][] buildKeysAndValues()
	{
		String[] keys = new String[7];
		String[] values = new String[7];
		
		int i = 0; 
		keys[i] = "FORMAT";
		values[i++] = String.valueOf(ExportFormatTypes.CSVBILLING_FORMAT);
		
		keys[i] = "DIR";
		values[i++] = getExportDirectory();
	
		keys[i] = "START";
		values[i++] = COMMAND_LINE_FORMAT.format(getExportProperties().getMinTimestamp().getTime());
	
		keys[i] = "STOP";
		values[i++] = COMMAND_LINE_FORMAT.format(getExportProperties().getMaxTimestamp().getTime());
	
		keys[i] = "DELIMITER";
		values[i++] = getExportProperties().getDelimiter().toString();
	
		keys[i] = "HEADINGS";
		values[i++] = String.valueOf(getExportProperties().isShowColumnHeadings());
		
		return new String[][]{keys, values};
	}
	
	/**
	 * Method retrieveBaselineData.
	 * @param baselinePointID
	 */
	public void retrieveBaselineData(int baselinePointID, GregorianCalendar curtailDate)
	{
		GregorianCalendar cal = (GregorianCalendar)curtailDate.clone();
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
	
		//Initialize baselineValues for 24 vals and value = 0;
		baselineValues = new Double[24];
		for( int i = 0; i < baselineValues.length; i++)
		{
			baselineValues[i] = new Double(0);
		}
	
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				logEvent(getClass() + ":  Error getting database connection.", com.cannontech.common.util.LogWriter.INFO);
				return;
			}
			else
			{
				stmt = conn.prepareStatement(sql.toString());
				stmt.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
				cal.add(Calendar.DATE, 1);
				stmt.setTimestamp(2, new java.sql.Timestamp(cal.getTime().getTime()));
				
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
			SqlUtils.close(rset, stmt, conn );
		}
		logEvent("...BASELINE DATA RETRIEVED: Took " + (System.currentTimeMillis() - timer) + " millis.", com.cannontech.common.util.LogWriter.INFO);
		return;
	}
	/**
	 * Method retrieveBillingData.
	 * @param keyId
	 * @param csvBillingCust
	 */
	public Vector retrieveCurtailHistory(CSVBillingCustomerRecord csvBillingCust)
	{
		//contains RecordBase values.
		Vector curtailHistoryVector = new Vector();
		
		StringBuffer sql = new StringBuffer	("SELECT LMEEHO.OFFERID, LMEEHO.REVISIONNUMBER");
		sql.append(", LMEEHO.PRICE, LMEEHC.AMOUNTCOMMITTED, LMEEHO.HOUR, LMEEPO.OFFERDATE ");
		sql.append("FROM LMENERGYEXCHANGEHOURLYCUSTOMER LMEEHC, ");
		sql.append("LMENERGYEXCHANGEPROGRAMOFFER LMEEPO, ");
		sql.append("LMENERGYEXCHANGEHOURLYOFFER LMEEHO ");
		sql.append(" WHERE LMEEHC.CUSTOMERID = " + csvBillingCust.getCustomerID().intValue());
		sql.append(" AND LMEEPO.OFFERID = LMEEHO.OFFERID");
		sql.append(" AND LMEEPO.OFFERID = LMEEHC.OFFERID");
		sql.append(" AND LMEEHO.REVISIONNUMBER = LMEEHC.REVISIONNUMBER");
		sql.append(" AND LMEEHO.HOUR = LMEEHC.HOUR");
		sql.append(" AND LMEEHC.AMOUNTCOMMITTED > 0");
		sql.append(" AND LMEEPO.OFFERDATE > ? AND LMEEPO.OFFERDATE <= ?");
		sql.append(" ORDER BY LMEEPO.OFFERID, LMEEPO.OFFERDATE, LMEEHO.HOUR ");
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				logEvent(getClass() + ":  Error getting database connection.", com.cannontech.common.util.LogWriter.INFO);
				return null;
			}
			else
			{
				stmt = conn.prepareStatement(sql.toString());
				// Curtail Dates are stored with a 00:00 timestamp but we still want that actual date 
				// so we have to subtract one day from each timestamp to get an accurate date for offer info queries.
				GregorianCalendar tempCal = (GregorianCalendar)getExportProperties().getMinTimestamp().clone();
				tempCal.add(Calendar.DATE, -1);
				stmt.setTimestamp(1, new java.sql.Timestamp(tempCal.getTime().getTime()));
				
				tempCal = (GregorianCalendar)getExportProperties().getMaxTimestamp().clone();
				tempCal.add(Calendar.DATE, -1);
				stmt.setTimestamp(2, new java.sql.Timestamp(tempCal.getTime().getTime()));
				rset = stmt.executeQuery();
				while( rset.next())
				{
					//1LMEEHO.OFFERID, 2LMEEHO.REVISIONNUMBER, 3LMEEHO.PRICE, 
					// 4LMEEHC.AMOUNTCOMMITTED, 5LMEEHO.HOUR, 6LMEEPO.OFFERDATE
					String offerID = rset.getString(1) + " - " + rset.getString(2);
					Double price = new Double(rset.getDouble(3));
					Double amtCommit = new Double(rset.getDouble(4));
					int hour = rset.getInt(5);
					GregorianCalendar offerDate = new GregorianCalendar();
					offerDate.setTime(rset.getTimestamp(6));

					GregorianCalendar curtailDate = (GregorianCalendar)offerDate.clone();
					curtailDate.set(Calendar.HOUR_OF_DAY, hour);
					
					CSVBillingRecord csvBillingRec = new CSVBillingRecord();
					csvBillingRec.setCurtailOffer(offerID);
					csvBillingRec.setCurtailDate(curtailDate);
					csvBillingRec.setCurtailRate(price);

					csvBillingRec.setCLR(amtCommit);
					csvBillingRec.setPDL(csvBillingCust.getPDL());
					csvBillingRec.setDelimiter(getExportProperties().getDelimiter());
		
					curtailHistoryVector.add(csvBillingRec);
				}
			}
		}
				
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
			SqlUtils.close(rset, stmt, conn );
		}
		return curtailHistoryVector;
	}



	/**
	 * Method retrieveBillingData.
	 * @param keyId
	 * @param csvBillingCust
	 */
	public void retrieveBillingData(Vector recordVector, CSVBillingCustomerRecord csvBillingCust)
	{
		GregorianCalendar prevCurtailDate = new GregorianCalendar();
		HoursAndValues hoursAndValues = null;
		for (int i = 0; i < recordVector.size(); i++)
		{
			if( recordVector.get(i) instanceof CSVBillingRecord)
			{
				CSVBillingRecord record = (CSVBillingRecord)recordVector.get(i);
				GregorianCalendar curtailDate = (GregorianCalendar)record.getCurtailDate().clone();
				curtailDate.set(Calendar.HOUR_OF_DAY, 0);
				curtailDate.set(Calendar.MINUTE, 0);
				curtailDate.set(Calendar.SECOND, 0);

				if( curtailDate.getTime().compareTo(prevCurtailDate.getTime()) != 0)
				{
					//Only collect baseline data for curtailed dates.
					retrieveBaselineData(csvBillingCust.getBaselinePointId().intValue(), curtailDate);					
					prevCurtailDate = (GregorianCalendar)curtailDate.clone();
					Vector validTimestamps = new Vector(1);
					validTimestamps.add(curtailDate.getTime());
					hoursAndValues = Baseline.retrieveData(csvBillingCust.getCurtailPointId(), validTimestamps);
				}
				int hourOfDay = record.getCurtailDate().get(Calendar.HOUR_OF_DAY);
				if( hoursAndValues != null)
				{
					Double value = hoursAndValues.getValue(hourOfDay);
					if( value != null)
						record.setADL(value);
				
					//Change hour from 0-23 to 1-24 (the new 0 is midnight as in the latest reading, not the earliest)
					hourOfDay++;
					if (hourOfDay == 24)
						hourOfDay = 0;
					record.setRLP((Double)baselineValues[hourOfDay]);
				}				
			}
		}
		return;
	}

	/**
	 * Method retrieveCustomerData.
	 */
	public void retrieveCustomerData()
	{
		long timer = System.currentTimeMillis();
		int rowCount = 0;
			
		StringBuffer sql = new StringBuffer	("SELECT CCB.COMPANYNAME, CCB.CUSTOMERID, CC.POINTID, CC.COMPONENTPOINTID, DMG.METERNUMBER, CCB.CUSTOMERDEMANDLEVEL, DMG.DEVICEID, PAO.PAONAME ");
		sql.append(" FROM CICUSTOMERBASE CCB, ");
		sql.append(" DEVICECUSTOMERLIST DCL, ");
		sql.append(" CALCCOMPONENT CC, ");
		sql.append(" POINT PT, ");
		sql.append(" YUKONPAOBJECT PAO, ");
		sql.append(" DEVICEMETERGROUP DMG ");
		sql.append(" WHERE CCB.CUSTOMERID = DCL.CUSTOMERID ");
		sql.append(" AND DCL.DEVICEID = DMG.DEVICEID ");
		sql.append(" AND PAO.PAOBJECTID = PT.PAOBJECTID ");
		sql.append(" AND PAO.PAOBJECTID = DMG.DEVICEID ");
		sql.append(" AND CC.POINTID = PT.POINTID ");
		sql.append(" AND PT.POINTOFFSET = " + PointOffsets.PT_OFFSET_BILLING_BASELINE);
		sql.append(" AND FUNCTIONNAME = '" + CalcComponentTypes.BASELINE_FUNCTION+"'");
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;
	
		customerHashtable = new java.util.Hashtable(10);
		
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				logEvent(getClass() + ":  Error getting database connection.", com.cannontech.common.util.LogWriter.INFO);
				return;
			}
			else
			{
				stmt = conn.prepareStatement(sql.toString());
				rset = stmt.executeQuery();
				while( rset.next())
				{
					//CCB.COMPANYNAME, CCB.CUSTOMERID, CC.POINTID, CC.COMPONENTPOINTID, DMG.METERNUMBER, CCB.CUSTOMERDEMANDLEVEL, DMG.DEVICEID 
					String custName = rset.getString(1);
					Integer custID = new Integer(rset.getInt(2));
					Integer baselinePtId = new Integer(rset.getInt(3));
					Integer curtailPtId = new Integer(rset.getInt(4));
					String meterLoc = rset.getString(5);
					Double pdl = new Double(rset.getDouble(6));
					Integer paoID = new Integer(rset.getInt(7));
					String paoName = new String(rset.getString(8));
					
					CSVBillingCustomerRecord csvBillingCust = new CSVBillingCustomerRecord(
						custName, custID, meterLoc, paoName, baselinePtId, curtailPtId, pdl);
	
					csvBillingCust.setDelimiter(getExportProperties().getDelimiter());	
					customerHashtable.put(paoID, csvBillingCust);
				}
			}
		}
				
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
			SqlUtils.close(rset, stmt, conn );
		}
		return;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/8/2002 5:07:44 PM)
	 * @return java.lang.String[]
	 */
	public void retrieveData()
	{
		String name = new String();
		name += filePrefix;
		name += FILENAME_FORMAT.format(getExportProperties().getMaxTimestamp().getTime());
		name += fileExtension;
		setExportFileName(name)	;
		
		long timer = System.currentTimeMillis();
		if( getExportProperties().isShowColumnHeadings())
		{
			StringRecord stringRec = new StringRecord("Yukon Curtailment Settlement for " +
				getExportProperties().getMinTimestamp().getTime() + " - " + 
				getExportProperties().getMaxTimestamp().getTime() + "\r\n");
				
			//Add a title record
			getRecordVector().add(stringRec);
		}
	
		logEvent("...Retrieving data for Date > " + getExportProperties().getMinTimestamp().getTime() +
			" AND <= " + getExportProperties().getMaxTimestamp().getTime(), com.cannontech.common.util.LogWriter.INFO);
			
		//Get a hashtable of records of all curtailment customers
		retrieveCustomerData();
	
		if(customerHashtable == null)
			return;
		//Loop through all customers and get all curtailment data for the timeframe.
		java.util.Set keyset = customerHashtable.keySet();
		java.util.Iterator iter = keyset.iterator();
		while(iter.hasNext())
		{
			Vector tempRecordVector = null;
			Integer keyid = (Integer)iter.next();
			com.cannontech.export.record.CSVBillingCustomerRecord custRec = (com.cannontech.export.record.CSVBillingCustomerRecord)customerHashtable.get(keyid);
			if( custRec != null)
			{
				getRecordVector().add(custRec);
				if( getExportProperties().isShowColumnHeadings())
				{
					StringRecord stringRec = new StringRecord(CSVBillingRecord.getColumnHeadingsString()); 
					//Add a column headings record
					getRecordVector().add(stringRec);
				}				
				tempRecordVector = retrieveCurtailHistory(custRec);
				retrieveBillingData(tempRecordVector, custRec);
				getRecordVector().addAll(tempRecordVector);
			}
		}
	
		//Set timestamps for next day.
		getExportProperties().setMinTimestamp(getExportProperties().getMaxTimestamp());
		
		java.util.GregorianCalendar newCal = new java.util.GregorianCalendar();	
		newCal.setTime(new java.util.Date(getExportProperties().getMaxTimestamp().getTime().getTime() + 86400000));
		getExportProperties().setMaxTimestamp(newCal);
		logEvent(" * Next TimePeriod: " + getExportProperties().getMinTimestamp().getTime() + " - " + getExportProperties().getMaxTimestamp().getTime(), com.cannontech.common.util.LogWriter.INFO);
		logEvent("@" + this.toString() +" Data Collection : Took " + (System.currentTimeMillis() - timer) + " millis", com.cannontech.common.util.LogWriter.INFO);	
	}
}