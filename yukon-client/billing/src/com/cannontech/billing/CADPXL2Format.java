package com.cannontech.billing;

import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class CADPXL2Format extends FileFormatBase
{
	
/**
 * Default CADPXL2 constructor
 */
public CADPXL2Format()
{
	super();
	//setOutputFile("Bx65hh999.dat");
	//inputFileName = ".\\ASCII\\BX65HH98.DAT";  // CADPXL2 formats file name
}

/**
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
public boolean retrieveBillingData(String dbAlias)
{
	java.text.SimpleDateFormat TEST_FORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	long timer = System.currentTimeMillis();
	
	if( dbAlias == null )
		dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();

	java.util.Hashtable accountNumbersHashTable = retrieveAccountNumbers(dbAlias);
	java.util.Hashtable countMetersPerAccount = null;
	
	if( accountNumbersHashTable != null)
		countMetersPerAccount = new java.util.Hashtable(accountNumbersHashTable.size());
	else
		countMetersPerAccount = new java.util.Hashtable();	//aren't able to set the init capacity here.


	String [] SELECT_COLUMNS =
	{
		SQLStringBuilder.DMG_METERNUMBER,
		SQLStringBuilder.PT_POINTID,
		SQLStringBuilder.PT_POINTOFFSET,
		SQLStringBuilder.RPH_TIMESTAMP,
		SQLStringBuilder.RPH_VALUE,
		SQLStringBuilder.DMG_DEVICEID,
		SQLStringBuilder.PAO_PAONAME
	};

	String [] FROM_TABLES =
	{
		com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME,
		com.cannontech.database.db.point.RawPointHistory.TABLE_NAME,
		com.cannontech.database.db.point.Point.TABLE_NAME,
		com.cannontech.database.db.device.DeviceMeterGroup.TABLE_NAME
	};

	SQLStringBuilder builder = new SQLStringBuilder();
	String sql = new String((builder.buildSQLStatement(SELECT_COLUMNS, FROM_TABLES, getBillingDefaults(), validAnalogPtOffsets, validAccPtOffsets)).toString());
		sql += " ORDER BY " 
			+ SQLStringBuilder.DMG_METERNUMBER + ", "
			+ SQLStringBuilder.DMG_DEVICEID + ", "
			+ SQLStringBuilder.PT_POINTOFFSET + ", " 
			+ SQLStringBuilder.RPH_TIMESTAMP + " DESC ";
		
/*		
	StringBuffer sql = new StringBuffer("SELECT DMG.METERNUMBER, PT.POINTID, PT.POINTOFFSET, RPH.TIMESTAMP, RPH.VALUE, DMG.DEVICEID, PAO.PAONAME " +
				" FROM POINT PT, RAWPOINTHISTORY RPH, DEVICEMETERGROUP DMG, YUKONPAOBJECT PAO " +
				" WHERE PT.PAOBJECTID = DMG.DEVICEID " +
				" AND PT.PAOBJECTID = PAO.PAOBJECTID " +
				" AND PAO.PAOBJECTID = DMG.DEVICEID " +
				" AND RPH.TIMESTAMP <= ? " +
				" AND DMG.COLLECTIONGROUP IN ('" + collectionGroups.get(0) + "'");
				for (int i = 1; i < collectionGroups.size(); i++)
				{
					sql.append( ", '" + collectionGroups.get(i) + "'");
				}

				sql.append(") AND PT.POINTID = RPH.POINTID " +
				" AND ( (POINTTYPE = 'Analog' AND POINTOFFSET IN (" + validAnalogPtOffsets[0]);
				for (int i = 1; i < validAnalogPtOffsets.length; i++)
				{
					sql.append( ", " + validAnalogPtOffsets[i]);
				}

				sql.append("))  OR  (POINTTYPE = 'PulseAccumulator' AND POINTOFFSET IN (" + validAccPtOffsets[0]);
				for (int i = 1; i < validAccPtOffsets.length; i++)
				{
					sql.append(", " + validAccPtOffsets[i]);
				}
				sql.append( ") )) ORDER BY DMG.METERNUMBER, DMG.DEVICEID, PT.POINTOFFSET, RPH.TIMESTAMP DESC");
*/

	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

		if( conn == null )
		{
			System.out.println(getClass() + ":  Error getting database connection.");
			return false;
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setTimestamp(1, new java.sql.Timestamp(getBillingDefaults().getEndDate().getTime()));
			rset = pstmt.executeQuery();
			
			int deviceID = -1;
			int lastDeviceID = -1;
			
			String meterNumber = new String();
			String paoName = new String();
			Integer meterPositionNumber = new Integer(0);
			String accountNumber = new String();
			int registerNumCount = 1;
		
			double value = 0;
			
			int pointID = 0;
			int ptOffset = 0;
			int lastPointID = 0;
			int recCount = 0;

			int rsetCount = 0;
			System.out.println(" Start looping through return resultset");

			while (rset.next())
			{
				rsetCount++;
				pointID = rset.getInt(2);
				
				double multiplier = 1;
				if( getBillingDefaults().getRemoveMultiplier())
				{
					multiplier = ((Double)getPointIDMultiplierHashTable().get(new Integer(pointID))).doubleValue();
				}
				
				if( pointID != lastPointID )	//just getting max time for each point
				{
					lastPointID = pointID;
					meterNumber = rset.getString(1);
					ptOffset = rset.getInt(3);
					java.sql.Timestamp ts = rset.getTimestamp(4);
					value = rset.getDouble(5) / multiplier;
					
					deviceID = rset.getInt(6);
					paoName = rset.getString(7);
					Date tsDate = new Date(ts.getTime());
					
					java.util.Vector registerNumberVector = new java.util.Vector();
					java.util.Vector kwValueVector = new java.util.Vector();
					java.util.Vector kwhValueVector = new java.util.Vector();
					java.util.Vector kvarValueVector = new java.util.Vector();

					// Our break label so we can exit the if-else checks
					inValidTimestamp:
					if( deviceID == lastDeviceID)
					{
						if ( ptOffset == 1 || isKWH(ptOffset) )
						{
							if( tsDate.compareTo( (Object)getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;
	
							//** Get the last record and add to it the other pointOffsets' values. **//								
							com.cannontech.billing.record.CADPXL2Record lastRecord =
							(com.cannontech.billing.record.CADPXL2Record)getRecordVector().get(recCount -1);

							lastRecord.getKwhReadingVector().add( new Double(value));
						}
						else if ( isKW(ptOffset) )
						{
							if( tsDate.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;
								
							//** Get the last record and add to it the other pointOffsets' values. **//
							com.cannontech.billing.record.CADPXL2Record lastRecord =
								(com.cannontech.billing.record.CADPXL2Record)getRecordVector().get(recCount -1);
							lastRecord.getKwReadingVector().add (new Double(value));
							
							/* registerNumCount needs to be incremented with every register loop through.
							   Since the ptOffsets are sorted in ascending order, this will be the first
								   type of ptOffset to get added for each register, assuming they all exist.*/
							if (ptOffset > 2)
								lastRecord.getRegisterNumberVector().add(new Integer(++registerNumCount));
						}
						else if ( isKVAR(ptOffset) )
						{
							if( tsDate.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;
								
							//** Get the last record and add to it the other pointOffsets' values. **//
							com.cannontech.billing.record.CADPXL2Record lastRecord =
								(com.cannontech.billing.record.CADPXL2Record)getRecordVector().get(recCount -1);
						
							lastRecord.getKvarReadingVector().add(new Double(value));
						}
					}
					
					else	//**  HAVE NEW POINT AND METERNUMBER **//
					{
						registerNumCount = 1;

						//*****************************************************************************************
						/* Add the value to the correct unitOfMeasure Vector according to the point offset 
						*/
						if (ptOffset == 1 || isKWH(ptOffset))
						{
							if( tsDate.compareTo( (Object)getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;
								
							kwhValueVector.add(new Double(value));
						}
						else if (isKW(ptOffset))
						{
							if (tsDate.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;

							kwValueVector.add (new Double(value));
						}

						else if (isKVAR(ptOffset))
						{
							if (tsDate.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;

							kvarValueVector.add(new Double(value));
						}
						
						//*****************************************************************************************
						/* Get the accountNumber from the hash table for the current meterNumber.
						   If the accountNumber is NOT found in the hash table then the paoName  is
						   		used as the accountNumber also
						*/
						Object tempActNum = null;
						
						if( accountNumbersHashTable != null)
							tempActNum = accountNumbersHashTable.get(meterNumber);
							
						if( tempActNum != null)
							accountNumber = (String)tempActNum;
						else
							accountNumber = (String)paoName;


						//*****************************************************************************************
						/*  countMetersPerAccount is a vector of accountnumbers(keys) and the count(values)
								of meters that have been associated with the account number.
							If an account number is NOT found in the vector then it is added with a count = 1
							Else if the account number is found, the value is retrieved, incremented, and re-put
								into the vector.	
						*/
						if( countMetersPerAccount.get(accountNumber) == null)
						{
							meterPositionNumber = new Integer(1);
							countMetersPerAccount.put(accountNumber, meterPositionNumber);
						}
						else
						{
							meterPositionNumber = new Integer(meterPositionNumber.intValue() + 1);
							countMetersPerAccount.put(accountNumber, new Integer(meterPositionNumber.intValue() + 1));
						}
						//*****************************************************************************************

						
						registerNumberVector.add(new Integer(registerNumCount));
														
						com.cannontech.billing.record.CADPXL2Record cadpxl2Rec =
							new com.cannontech.billing.record.CADPXL2Record(accountNumber, 
																	meterNumber, 
																	meterPositionNumber,
																	ts,
																	registerNumberVector,
																	kwhValueVector, 
																	kwValueVector, 
																	kvarValueVector);
						getRecordVector().addElement(cadpxl2Rec);

						lastDeviceID = deviceID;
						recCount++;
					}
				}
			}//end while
			System.out.println(" Records counted = " +recCount + " ||  ResultSet Size = " + rsetCount);
		}//end else
	}//end try 
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
	System.out.println(" @CADPXL2 Data Collection : Took " + (System.currentTimeMillis() - timer));
	
	return true;
}
}
