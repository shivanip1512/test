package com.cannontech.billing;

import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.cannontech.billing.record.BillingRecordBase;
import com.cannontech.billing.record.StringRecord;
import com.cannontech.billing.record.WLT_40HeaderRecord0000;
import com.cannontech.billing.record.WLT_40HeaderRecord0001;
import com.cannontech.billing.record.WLT_40HeaderRecord0002;
import com.cannontech.billing.record.WLT_40PulseDataRecord;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.point.RawPointHistory;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class WLT_40Format extends FileFormatBase 
{
	java.sql.Connection dbConnection = null;
	java.sql.PreparedStatement prepStatement = null;

	public static final String FILE_TERMINATION_RECORD =
		"999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999";
/**
 * Default WLT_40 constructor
 */
public WLT_40Format() 
{
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public void parseAndCalculatePulses(Double multiplier, Integer demandInterval, Vector<RawPointHistory> rawPointHistoryVector, 
		Vector<String> returnPulseVector, Vector<Integer> pulseTotalVector, Vector<Double> pulseMultiplierVector, 
		Vector<Integer> missingIntervalsVector, Vector<Integer> falseIntervalsVector)
{
	int falseIntervals = 0;
	int missingIntervals = 0;
	if( rawPointHistoryVector != null && rawPointHistoryVector.size() != 0 )
	{
		int intervalPulses = 0;
		int tempPulseTotal = 0;
		char quality = '1';
		java.util.GregorianCalendar expectedRPHTimestamp = null;
		java.util.GregorianCalendar lastRPHTimestamp = null;
		if( rawPointHistoryVector.size() > 0 )
		{
			com.cannontech.clientutils.CTILogger.info( "Number of Raw Point History entries " + Integer.toString(rawPointHistoryVector.size()) );

			expectedRPHTimestamp = new java.util.GregorianCalendar();
			expectedRPHTimestamp.setTime(getBillingFileDefaults().getDemandStartDate());
		}
		String tempDataStatusZoneString = null;
		for(int i=0;i<rawPointHistoryVector.size();i++)
		{
			RawPointHistory currentRPH = rawPointHistoryVector.get(i);
			if( i == 0 )
			{
				java.text.SimpleDateFormat timestampFormatter = new java.text.SimpleDateFormat("MM/dd/yy/HH:mm:ss:SSSS");
				CTILogger.info("First raw point entry date: " + timestampFormatter.format(currentRPH.getTimeStamp().getTime()) );
			}
			else if( i == (rawPointHistoryVector.size()-1) )
			{
				java.text.SimpleDateFormat timestampFormatter = new java.text.SimpleDateFormat("MM/dd/yy/HH:mm:ss:SSSS");
				CTILogger.info("Last raw point entry date: " + timestampFormatter.format(currentRPH.getTimeStamp().getTime()) );
			}

			if( expectedRPHTimestamp.getTime().getTime() < currentRPH.getTimeStamp().getTime().getTime() )
			{//special case: missing raw point entry must plug
				tempDataStatusZoneString = "";
				for(int j=0;j<(5-Integer.toString(intervalPulses).length());j++)
					tempDataStatusZoneString = tempDataStatusZoneString.concat("0");
				quality = '2';
				tempDataStatusZoneString = tempDataStatusZoneString.concat( String.valueOf(quality) );
				java.text.SimpleDateFormat timestampFormatter = new java.text.SimpleDateFormat("MM/dd/yy/HH:mm:ss:SSSS");
				while( expectedRPHTimestamp.getTime().getTime() < currentRPH.getTimeStamp().getTime().getTime() )
				{
					CTILogger.info("Missing raw point entry for date: " + timestampFormatter.format(expectedRPHTimestamp.getTime()) );
					//com.cannontech.clientutils.CTILogger.info("Current Raw Point History Timestamp: " + timestampFormatter.format(currentRPH.getTimeStamp().getTime()) );
					//com.cannontech.clientutils.CTILogger.info("Expected Raw Point History Timestamp: " + timestampFormatter.format(expectedRPHTimestamp.getTime()) );
					expectedRPHTimestamp.add(java.util.GregorianCalendar.SECOND,demandInterval.intValue());
					returnPulseVector.addElement( tempDataStatusZoneString );
					missingIntervals++;
				}
				//com.cannontech.clientutils.CTILogger.info("Current Raw Point History Timestamp: " + timestampFormatter.format(currentRPH.getTimeStamp().getTime()) );
				//com.cannontech.clientutils.CTILogger.info("Expected Raw Point History Timestamp: " + timestampFormatter.format(expectedRPHTimestamp.getTime()) );
			}

			if( lastRPHTimestamp == null ||
					currentRPH.getTimeStamp().getTime().getTime() != lastRPHTimestamp.getTime().getTime() )
			{
				if( currentRPH.getQuality().intValue() == PointQuality.Normal.getQuality() ||
						currentRPH.getQuality().intValue() == PointQuality.PartialInterval.getQuality() ||
						currentRPH.getQuality().intValue() == PointQuality.Powerfail.getQuality())
				{
					intervalPulses = (int)(currentRPH.getValue().doubleValue()/(multiplier.doubleValue()*(3600/demandInterval.intValue())));
					if( intervalPulses <= 9999 )
					{
						tempPulseTotal += intervalPulses;
					}
				}

				if( currentRPH.getQuality().intValue() == PointQuality.Normal.getQuality()) {
					if( intervalPulses <= 9999 ) {
						quality = '0';
					}
					else {
						quality = '1';
						falseIntervals++;
					}
				} else if (currentRPH.getQuality().intValue() == PointQuality.DeviceFiller.getQuality()) {
					quality = '9';
					falseIntervals++;
				} else if( currentRPH.getQuality().intValue() == PointQuality.Powerfail.getQuality()) {
					quality = '4';
					falseIntervals++;
				} else {
					quality = '1';
					falseIntervals++;
				}

				tempDataStatusZoneString = "";
				for(int j=0;j<(4-Integer.toString(intervalPulses).length());j++)
					tempDataStatusZoneString = tempDataStatusZoneString.concat("0");
				tempDataStatusZoneString = tempDataStatusZoneString.concat(Integer.toString(intervalPulses));
				tempDataStatusZoneString = tempDataStatusZoneString.concat( String.valueOf(quality) );
				returnPulseVector.addElement( tempDataStatusZoneString );
				intervalPulses = 0;
				lastRPHTimestamp = new java.util.GregorianCalendar();
				lastRPHTimestamp.setTime(currentRPH.getTimeStamp().getTime());
				expectedRPHTimestamp = currentRPH.getTimeStamp();
				expectedRPHTimestamp.add(java.util.GregorianCalendar.SECOND,demandInterval.intValue());
			}
			else
			{
				java.text.SimpleDateFormat timestampFormatter = new java.text.SimpleDateFormat("MM/dd/yy/HH:mm:ss:SSSS");
				CTILogger.info( "Duplicate timestamp: " + timestampFormatter.format( currentRPH.getTimeStamp().getTime()) + ", using first entry throwing other entries with the same timestamp." );
			}
		}
		pulseTotalVector.addElement(new Integer(tempPulseTotal));
		pulseMultiplierVector.addElement(multiplier);

		java.util.GregorianCalendar stopRPHTimestamp = new java.util.GregorianCalendar();
		stopRPHTimestamp.setTime(getBillingFileDefaults().getEndDate());
		if( expectedRPHTimestamp.getTime().getTime() < stopRPHTimestamp.getTime().getTime() )
		{
			tempDataStatusZoneString = "";
			for(int j=0;j<(5-Integer.toString(intervalPulses).length());j++)
				tempDataStatusZoneString = tempDataStatusZoneString.concat("0");
			quality = '2';
			tempDataStatusZoneString = tempDataStatusZoneString.concat( String.valueOf(quality) );
			java.text.SimpleDateFormat timestampFormatter = new java.text.SimpleDateFormat("MM/dd/yy/HH:mm:ss:SSSS");
			while( expectedRPHTimestamp.getTime().getTime() < stopRPHTimestamp.getTime().getTime() )
			{
				CTILogger.info("Missing raw point entry for date: " + timestampFormatter.format( expectedRPHTimestamp.getTime()) );
				//com.cannontech.clientutils.CTILogger.info("Current Raw Point History Timestamp: " + timestampFormatter.format(currentRPH.getTimeStamp().getTime()) );
				//com.cannontech.clientutils.CTILogger.info("Expected Raw Point History Timestamp: " + timestampFormatter.format(expectedRPHTimestamp.getTime()) );
				expectedRPHTimestamp.add(java.util.GregorianCalendar.SECOND,demandInterval.intValue());
				returnPulseVector.addElement( tempDataStatusZoneString );
				missingIntervals++;
			}
			//com.cannontech.clientutils.CTILogger.info("Stop Raw Point History Timestamp: " + timestampFormatter.format(stopRPHTimestamp.getTime()) );
			//com.cannontech.clientutils.CTILogger.info("Expected Raw Point History Timestamp: " + timestampFormatter.format(expectedRPHTimestamp.getTime()) );
		}
	}

	falseIntervalsVector.addElement(new Integer(falseIntervals));
	missingIntervalsVector.addElement(new Integer(missingIntervals));
}
/**
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
public boolean retrieveBillingData(java.util.Vector collectionGroups)
{
	//If we do not have any collectiongroups, we will get every single point value!!
	StringBuffer collectionGroupString = new StringBuffer();
	for(int i=0;i<collectionGroups.size();i++)
	{
		if( i == 0 )
			collectionGroupString.append(" and (COLLECTIONGROUP='" + collectionGroups.get(i) + "'");
		else
			collectionGroupString.append(" or COLLECTIONGROUP='" + collectionGroups.get(i) + "'");
	}

	//be sure we have at least 1 collection group
	if( collectionGroupString.length() > 0 )
		collectionGroupString.append(")");


	java.io.RandomAccessFile file = null;
	Vector<String> substationPointGroupVector = new Vector<String>( 40 );
	java.io.File checkFile = null;
	if( StringUtils.isNotBlank(getBillingFileDefaults().getInputFileDir())) {
		checkFile = new java.io.File( getBillingFileDefaults().getInputFileDir() );
	} else {
		checkFile = new java.io.File( "../config/mv90.dat" );
	}
	
	try
	{
		// open file		
		if( checkFile.exists() )
		{
			file = new java.io.RandomAccessFile( checkFile, "r" );

			long filePointer = 0;
			long length = file.length();

			while ( filePointer < length )  // loop until the end of the file
			{
				String line = file.readLine();  // read a line in

				substationPointGroupVector.addElement( line );

				// set our pointer to the new position in the file
				filePointer = file.getFilePointer();
			}
		}
		else
		{
			return false;
		}

		// Close file
		file.close();						
	}
	catch(java.io.IOException ex) {
		CTILogger.error(ex);
		return false;
	}
	finally
	{
		try {
			if( checkFile.exists() )
				file.close();
		}
		catch( java.io.IOException ex ) {
			CTILogger.error(ex);
		}
	}

	// make sure we received all lines from the parameters file
	if( substationPointGroupVector.size() == 0 )
	{
		return false;
	}
	else
	{
		for(int i=0;i<substationPointGroupVector.size();i++)
		{
			String substationId = null;
			Vector<Integer> channelPointIdVector = new Vector<Integer>();

			java.util.StringTokenizer stringTokenizer = new java.util.StringTokenizer(substationPointGroupVector.get(i),",");
			int pass = 0;
			while( stringTokenizer.hasMoreTokens() )
			{
				String tokenString = stringTokenizer.nextToken();
				com.cannontech.clientutils.CTILogger.info(tokenString);
				if( pass == 0 )
				{
					substationId = tokenString;
					pass++;
				}
				else
				{
					channelPointIdVector.addElement(new Integer(tokenString));
				}
			}

			dbConnection = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			java.sql.ResultSet rset = null;
			
			try
			{
				java.sql.PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT DISTINCT RPH.POINTID, RPH.TIMESTAMP, RPH.QUALITY, RPH.VALUE, P.POINTTYPE FROM POINT P, RAWPOINTHISTORY RPH WHERE P.POINTID = ? AND P.POINTID = RPH.POINTID AND TIMESTAMP >= ? AND TIMESTAMP < ? ORDER BY TIMESTAMP");
				Vector<RawPointHistory> tempRawPointHistoryVector = null;
				Vector<Vector<RawPointHistory>> rawPointHistoryVectorOfVectors = new Vector<Vector<RawPointHistory>>();
				String pointType = null;
				try
				{
					for(int j=0;j<channelPointIdVector.size();j++)
					{
						if( channelPointIdVector.get(j).intValue() > 0 )
						{
							preparedStatement.setInt(1,channelPointIdVector.get(j).intValue());
							preparedStatement.setTimestamp(2,new java.sql.Timestamp(getBillingFileDefaults().getDemandStartDate().getTime()));
							preparedStatement.setTimestamp(3,new java.sql.Timestamp(getBillingFileDefaults().getEndDate().getTime()));
							rset = preparedStatement.executeQuery();
							tempRawPointHistoryVector = new Vector<RawPointHistory>();

							while (rset.next())
							{
								Long changeID = new Long(0);
								Integer pointID = new Integer(rset.getInt(1));
								java.util.GregorianCalendar timestamp = new java.util.GregorianCalendar();
								timestamp.setTime(rset.getTimestamp(2));
								Integer quality = new Integer(rset.getInt(3));
								Double value = new Double(rset.getDouble(4));
								if( pointType == null )
								{
									pointType = new String(rset.getString(5));
								}

								com.cannontech.database.db.point.RawPointHistory rph = new com.cannontech.database.db.point.RawPointHistory(changeID, pointID, timestamp, quality, value);

								tempRawPointHistoryVector.add(rph);
							}

							if( tempRawPointHistoryVector != null && tempRawPointHistoryVector.size() > 0 )
							{
								rawPointHistoryVectorOfVectors.add(tempRawPointHistoryVector);
							}
							else
							{
								rawPointHistoryVectorOfVectors.add(new Vector<RawPointHistory>(0));//add vector with size == 0
							}

							tempRawPointHistoryVector = null;
						}
					}
				}
				catch( java.sql.SQLException e ) {
					CTILogger.error(e);
				}
				finally
				{
					try {
						if( preparedStatement != null )
							preparedStatement.close();
					}
					catch( java.sql.SQLException e ) {
						CTILogger.error(e);
					}
				}
				prepStatement = null;

				Vector<String> tempPulseVector = null;
				Vector<Vector <String>> pulseVectorOfVectors = new Vector<Vector <String>>();
				if( pointType != null && PointTypes.getType(pointType) == PointTypes.DEMAND_ACCUMULATOR_POINT ) {
					preparedStatement = dbConnection.prepareStatement("SELECT PA.MULTIPLIER, DLP.LOADPROFILEDEMANDRATE, PU.UOMID FROM POINTACCUMULATOR PA, DEVICELOADPROFILE DLP, POINTUNIT PU, POINT P WHERE PA.POINTID = ? AND PA.POINTID = P.POINTID AND PU.POINTID = P.POINTID AND DLP.DEVICEID = P.PAOBJECTID");
				}
				else {
					preparedStatement = dbConnection.prepareStatement("SELECT PA.MULTIPLIER, DLP.LOADPROFILEDEMANDRATE, PU.UOMID FROM POINTANALOG PA, DEVICELOADPROFILE DLP, POINTUNIT PU, POINT P WHERE PA.POINTID = ? AND PA.POINTID = P.POINTID AND PU.POINTID = P.POINTID AND DLP.DEVICEID = P.PAOBJECTID");
				}
				pointType = null;

				WLT_40HeaderRecord0000 header0000 = new WLT_40HeaderRecord0000();
				WLT_40HeaderRecord0001 header0001 = new WLT_40HeaderRecord0001();
				WLT_40HeaderRecord0002 header0002 = new WLT_40HeaderRecord0002();
				header0000.setIdentNumber(substationId);
				header0001.setIdentNumber(substationId);
				header0002.setIdentNumber(substationId);

				Integer savedDemandInterval = null;
				Vector<Integer> missingIntervalsVector = new Vector<Integer>(channelPointIdVector.size());
				Vector<Integer> falseIntervalsVector = new Vector<Integer>(channelPointIdVector.size());
				Vector<Integer> pulseTotalVector = new Vector<Integer>(channelPointIdVector.size());
				Vector<Double> pulseMultiplierVector = new Vector<Double>(channelPointIdVector.size());
				Vector<Integer> unitOfMeasureIdVector = new Vector<Integer>(channelPointIdVector.size());

				java.util.GregorianCalendar tempGreg1 = new java.util.GregorianCalendar();
				tempGreg1.setTime(getBillingFileDefaults().getDemandStartDate());
				java.util.GregorianCalendar tempGreg2 = new java.util.GregorianCalendar();
				tempGreg2.setTime(getBillingFileDefaults().getEndDate());
				long milliseconds = getBillingFileDefaults().getEndDate().getTime() - getBillingFileDefaults().getDemandStartDate().getTime();
				int predictedIntervals = 0;

				try
				{
					for(int j=0;j<channelPointIdVector.size();j++)
					{
						if( channelPointIdVector.get(j).intValue() > 0 )
						{
							preparedStatement.setInt(1,channelPointIdVector.get(j).intValue());
							rset = preparedStatement.executeQuery();

							if( rset.next() )
							{
								Double multiplier = new Double(rset.getDouble(1));
								Integer loadProfileDemandRate = new Integer(rset.getInt(2));
								Integer unitOfMeasureId = new Integer(rset.getInt(3));
								unitOfMeasureIdVector.addElement(unitOfMeasureId);
								tempPulseVector = new Vector<String>((rawPointHistoryVectorOfVectors.get(j)).size());
								
								if( savedDemandInterval == null ) {
									savedDemandInterval = new Integer(loadProfileDemandRate.intValue());
								}
								predictedIntervals = (int)(milliseconds/1000/savedDemandInterval.intValue());
								parseAndCalculatePulses(multiplier, loadProfileDemandRate, rawPointHistoryVectorOfVectors.get(j), tempPulseVector, pulseTotalVector, pulseMultiplierVector, missingIntervalsVector, falseIntervalsVector);
							}

							if( tempPulseVector != null && tempPulseVector.size() > 0 )
							{
								pulseVectorOfVectors.add(tempPulseVector);
							}
							else
							{
								pulseVectorOfVectors.add(new Vector<String>(0));//add vector with size == 0
							}

							if( ((java.util.Vector)pulseVectorOfVectors.get(j)).size() != predictedIntervals )
							{
								CTILogger.info("Why isn't there the corrent number of pulses?");
								CTILogger.info( "Expected number " + Integer.toString(predictedIntervals) );
								CTILogger.info( "Actual number " + Integer.toString(((java.util.Vector)pulseVectorOfVectors.get(j)).size()) );
								if( ((java.util.Vector)pulseVectorOfVectors.get(j)).size() < predictedIntervals )
								{
									CTILogger.info("Not enough pulses!!");
									while( ((java.util.Vector)pulseVectorOfVectors.get(j)).size() < predictedIntervals )
									{
										(pulseVectorOfVectors.get(j)).addElement("00002");
									}
								}
								else { //if( ((java.util.Vector)pulseVectorOfVectors.get(j)).size() > predictedIntervals )
									CTILogger.info("Too many pulses!!");
								}
							}

							tempPulseVector = null;
						}
					}
				}
				catch( java.sql.SQLException e ) {
					CTILogger.error(e);
				}
				finally
				{
					try {
						if( preparedStatement != null )
							preparedStatement.close();
					}
					catch( java.sql.SQLException e ) {
						CTILogger.error(e);
					}
				}
				prepStatement = null;

				int missingIntervals = 0;
				for(int k=0;k<missingIntervalsVector.size();k++)
				{
					if( missingIntervalsVector.get(k).intValue() > missingIntervals )
					{//we take just the highest missing intervals number because if we will have 3 times to many missing intervals if we count all 3 channels
						missingIntervals = missingIntervalsVector.get(k).intValue();
					}
				}
				int falseIntervals = 0;
				for(int k=0;k<falseIntervalsVector.size();k++)
					falseIntervals += falseIntervalsVector.get(k).intValue();

				header0000.setStartTime(tempGreg1);
				header0000.setStopTime(tempGreg2);
				header0000.setTotalIntervals(new Integer(predictedIntervals-missingIntervals));
				header0000.setPredictedIntervals(new Integer(predictedIntervals));
				header0000.setMissingIntervals(new Integer(missingIntervals));
				header0000.setFalseIntervals(new Integer(falseIntervals));
				//header0000.setStartMeterReadingVector();
				//header0000.setEndMeterReadingVector();
				//header0000.setMeterMultiplierVector();

				header0001.setPulseTotalVector(pulseTotalVector);
				header0001.setPulseMultiplierVector(pulseMultiplierVector);
				Vector<Integer> unitOfMeasureCodeVector = new Vector<Integer>();
				for(int k=0;k<unitOfMeasureIdVector.size();k++)
				{
					String unitMeasureName = DaoFactory.getUnitMeasureDao().getLiteUnitMeasure(unitOfMeasureIdVector.get(k).intValue()).getUnitMeasureName();
					if( unitMeasureName.equalsIgnoreCase("kWh") )
					{
						unitOfMeasureCodeVector.addElement(new Integer(1));
					}
					else if( unitMeasureName.equalsIgnoreCase("kW") )
					{
						unitOfMeasureCodeVector.addElement(new Integer(2));
					}
					else if( unitMeasureName.equalsIgnoreCase("kVArh") )
					{
						unitOfMeasureCodeVector.addElement(new Integer(3));
					}
					else if( unitMeasureName.equalsIgnoreCase("kVAr") )
					{
						unitOfMeasureCodeVector.addElement(new Integer(4));
					}
					else if( unitMeasureName.equalsIgnoreCase("TEMP-F") )
					{
						unitOfMeasureCodeVector.addElement(new Integer(5));
					}
					else if( unitMeasureName.equalsIgnoreCase("kQ") )
					{
						unitOfMeasureCodeVector.addElement(new Integer(6));
					}
					else
					{
						unitOfMeasureCodeVector.addElement(new Integer(0));
					}
				}
				if( unitOfMeasureCodeVector.size() > 0 )
				{
					header0001.setUnitOfMeasureCodeVector(unitOfMeasureCodeVector);
				}
				if( savedDemandInterval == null )
				{
					savedDemandInterval = new Integer(3600);
				}
				header0001.setOriginalRecordedIntervalsPerHour(new Integer(3600/savedDemandInterval.intValue()));
				header0001.setDaysPerRecording( new Integer( (int)(milliseconds/86400000) ) );
				header0001.setSegmentedIntervalsPerHour( new Integer( 3600/savedDemandInterval.intValue() ) );
				header0001.setRequestedOutputIntervalsPerHour( new Integer( 3600/savedDemandInterval.intValue() ) );
				java.util.GregorianCalendar tempGreg3 = new java.util.GregorianCalendar();
				tempGreg3.setTime(getBillingFileDefaults().getDemandStartDate());
				java.util.GregorianCalendar tempGreg4 = new java.util.GregorianCalendar();
				tempGreg4.setTime(getBillingFileDefaults().getEndDate());
				int tempDstOffset = tempGreg3.get(java.util.Calendar.DST_OFFSET);
				while( tempGreg3.getTime().getTime() < tempGreg4.getTime().getTime() )
				{
					if( tempDstOffset != tempGreg3.get(java.util.Calendar.DST_OFFSET) )
					{
						// DST starts at 2 AM
						if( tempGreg3.get(java.util.Calendar.MONTH) == java.util.Calendar.OCTOBER )
						{
							header0001.setDstType("F");
							header0001.setDstChange(new java.util.GregorianCalendar(tempGreg3.get(java.util.Calendar.YEAR), tempGreg3.get(java.util.Calendar.MONTH), tempGreg3.get(java.util.Calendar.DAY_OF_MONTH)-1, 1, 0));
						}
						else// if( tempGreg3.get(java.util.Calendar.MONTH) == java.util.Calendar.APRIL )
						{
							header0001.setDstType("S");
							header0001.setDstChange(new java.util.GregorianCalendar(tempGreg3.get(java.util.Calendar.YEAR), tempGreg3.get(java.util.Calendar.MONTH), tempGreg3.get(java.util.Calendar.DAY_OF_MONTH)-1, 3, 0));
						}
						java.text.SimpleDateFormat timestampFormatter = new java.text.SimpleDateFormat("MM/dd/yy/HH:mm");
						com.cannontech.clientutils.CTILogger.info("Day Light Savings Time Change at: " + timestampFormatter.format(header0001.getDstChange().getTime()) + " type: " + header0001.getDstType() );
						break;
					}
					else
					{
						tempGreg3.add(java.util.Calendar.DAY_OF_MONTH, 1);
					}
				}

				//header0002.set();

				getRecordVector().add( header0000 );
				getRecordVector().add( header0001 );
				getRecordVector().add( header0002 );

				int currentDataRecordSortCode = 100;
				Vector<String> tempDataStatusZonesVector = null;
				WLT_40PulseDataRecord record =  new WLT_40PulseDataRecord();
				record.setIdentNumber(substationId);
				record.setSortCode(new Integer(currentDataRecordSortCode));
				currentDataRecordSortCode++;
				tempDataStatusZonesVector = new Vector<String>(24);
				for(int x=0;x<((java.util.Vector)pulseVectorOfVectors.get(0)).size();x++)
				{
					for(int y=0;y<pulseVectorOfVectors.size();y++)
					{
						if(tempDataStatusZonesVector.size()<24)
						{
							if( x < ((java.util.Vector)pulseVectorOfVectors.get(y)).size() ) {
								tempDataStatusZonesVector.addElement((pulseVectorOfVectors.get(y)).get(x));
							}
							else {
								tempDataStatusZonesVector.addElement("00002");
							}
						}
						else
						{
							if( tempDataStatusZonesVector != null && tempDataStatusZonesVector.size() > 0 ) {
								record.setDataStatusZonesVector(tempDataStatusZonesVector);
							}

							getRecordVector().add( record );
							record = new com.cannontech.billing.record.WLT_40PulseDataRecord();
							record.setIdentNumber(substationId);
							record.setSortCode(new Integer(currentDataRecordSortCode));
							currentDataRecordSortCode++;
							tempDataStatusZonesVector = new Vector<String>(24);
							if( x < ((java.util.Vector)pulseVectorOfVectors.get(y)).size() )
							{
								tempDataStatusZonesVector.addElement((pulseVectorOfVectors.get(y)).get(x));
							}
							else
							{
								tempDataStatusZonesVector.addElement("00002");
							}
						}
					}
				}

				getRecordVector().add( new StringRecord(FILE_TERMINATION_RECORD) );
			}
			catch(Exception e) {		
				CTILogger.error(e);
				return false;
			}
			finally
			{
				try {
					if( dbConnection != null ) dbConnection.close();
					if( prepStatement != null ) prepStatement.close();
					if( rset != null ) rset.close();
				}
				catch( java.sql.SQLException ex ) {
					//this is most likey caused by the rset already being closed,
					// happens when there is zero rows returned
					CTILogger.error(ex);
					return true;
				}
			}
		}
 	}

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/2001 12:09:57 PM)
 */
public void writeToFile() throws java.io.IOException
{
	StringBuffer tempBuffer = null;
	String outputFileName = null;
	Vector<BillingRecordBase> records = getRecordVector();
	
	for(int i=0;i<records.size();i++)
	{
		String recordString = records.get(i).dataToString();
		if( recordString != FILE_TERMINATION_RECORD )
		{
			if( tempBuffer != null ) {
				tempBuffer.append(recordString);
			}
			else {
				outputFileName = ((WLT_40HeaderRecord0000)records.get(i)).getIdentNumber() + ".40A";
				tempBuffer = new StringBuffer();
				tempBuffer.append(recordString);
			}
		}
		else
		{
			tempBuffer.append(recordString);
			if( outputFileName != null )
			{
				java.io.File outFile = new java.io.File(CtiUtilities.getExportDirPath() );
				outFile.mkdirs();
				
				java.io.FileWriter outputFileWriter = new java.io.FileWriter( outFile + outputFileName);
				outputFileWriter.write( tempBuffer.toString() );
				outputFileWriter.flush();
				outputFileWriter.close();
				outputFileName = null;
			}
			else {
				CTILogger.info("Output File Name == null!!!!!!!");
			}

			tempBuffer = null;
		}
	}
}

/**
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
@Override
public boolean retrieveBillingData()
{
	return false;
}
}
