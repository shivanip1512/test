package com.cannontech.billing;

/**
 * Insert the type's description here.
 * Creation date: (6/28/00 11:55:04 AM)
 * @author: 
 */
public class CADPFormat extends FileFormatBase
{
/**
 * CADPFormat constructor comment.
 */
public CADPFormat()
{
	super();
}
/**
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
public boolean retrieveBillingData(java.util.Vector collectionGroups, String dbAlias)
{
	long timer = System.currentTimeMillis();
	
	if( dbAlias == null )
		dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();

	if (collectionGroups.isEmpty())
		return false;

	java.util.Hashtable accountNumbersHashTable = retrieveAccountNumbers(dbAlias);
	java.util.Hashtable countMetersPerAccount = null;
	
	if( accountNumbersHashTable != null)
		countMetersPerAccount = new java.util.Hashtable(accountNumbersHashTable.size());
	else
		countMetersPerAccount = new java.util.Hashtable();	//aren't able to set the init capacity here.

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
			int pageNumber = 0;	//individual row counter
			double value = 0;
			
			int pointID = 0;
			int ptOffset = 0;
			int lastPointID = 0;
			int recCount = 0;

			int rsetCount = 0;
			System.out.println(" Start looping through return resultset");

			int vectorRecordCount = 0;

			boolean hadKW = true;
			boolean hadKWH = true;
			boolean hadKVAR = true;

			java.util.Vector acctNumberVector = new java.util.Vector(8);
			java.util.Vector kwValueVector = new java.util.Vector(8);
			java.util.Vector kwhValueVector = new java.util.Vector(8);
			java.util.Vector kvarValueVector = new java.util.Vector(8);

			for (int i = 0; i < 8; i++)
			{
				acctNumberVector.add(new String(""));
				kwValueVector.add(new Double(0));
				kwhValueVector.add(new Double(0));
				kvarValueVector.add(new Double(0));
			}

			
			//DMG.METERNUMBER, PT.POINTID, PT.POINTOFFSET, RPH.TIMESTAMP, RPH.VALUE, DMG.DEVICEID, PAO.PAONAME
			while (rset.next())
			{
				rsetCount++;
				pointID = rset.getInt(2);
				if( pointID != lastPointID )	//just getting max time for each point
				{
					lastPointID = pointID;
					meterNumber = rset.getString(1);
					ptOffset = rset.getInt(3);
					java.sql.Timestamp ts = rset.getTimestamp(4);
					value = rset.getDouble(5);
					deviceID = rset.getInt(6);
					paoName = rset.getString(7);

					// Our break label so we can exit the if-else checks
					inValidTimestamp:
					if( deviceID == lastDeviceID)
					{
						if ( ptOffset == 1 || isKWH(ptOffset) )
						{
							if( ts.compareTo( (Object)getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;

							kwhValueVector.set(vectorRecordCount -1, new Double(value));
							hadKWH = true;
						}
						else if ( isKW(ptOffset) )
						{
							if( ts.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;
								
							kwValueVector.set(vectorRecordCount - 1, new Double(value));
							hadKW = true;
						}
						else if ( isKVAR(ptOffset) )
						{
							if( ts.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;

							kvarValueVector.set(vectorRecordCount -1, new Double(value));
							hadKVAR = true;
						}
					}
					
					else	//**  HAVE NEW POINT AND METERNUMBER **//
					{
						hadKWH = false;
						hadKW = false;
						hadKVAR = false;
						
						lastDeviceID = deviceID;

						//*****************************************************************************************
						/* Add the value to the correct unitOfMeasure Vector according to the point offset 
						*/
						if (ptOffset == 1 || isKWH(ptOffset))
						{
							if( ts.compareTo( (Object)getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;
								
							kwhValueVector.set(vectorRecordCount, new Double(value));
							hadKWH = true;
						}
						else if (isKW(ptOffset))
						{
							if (ts.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;

							kwValueVector.set(vectorRecordCount, new Double(value));
							hadKW = true;
						}

						else if (isKVAR(ptOffset))
						{
							if (ts.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;

							kvarValueVector.set(vectorRecordCount, new Double(value));
							hadKVAR = true;
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
							acctNumberVector.set(vectorRecordCount, (String)tempActNum);
						else
							acctNumberVector.set(vectorRecordCount, (String)paoName);


						//*****************************************************************************************
						vectorRecordCount ++;
						if( vectorRecordCount == 8)
						{
							com.cannontech.billing.record.CADPRecord cadpRec =
								new com.cannontech.billing.record.CADPRecord(acctNumberVector,
																	kwhValueVector, 
																	kwValueVector, 
																	kvarValueVector);
							getRecordVector().addElement(cadpRec);

							//inti vectors
							acctNumberVector = new java.util.Vector(8);
							kwValueVector = new java.util.Vector(8);
							kwhValueVector = new java.util.Vector(8);
							kvarValueVector = new java.util.Vector(8);

							for (int i = 0; i < 8; i++)
							{
								acctNumberVector.add(new String(""));
								kwValueVector.add(new Double(0));
								kwhValueVector.add(new Double(0));
								kvarValueVector.add(new Double(0));
							}
							vectorRecordCount = 0;
						}
						
						recCount++;
					}
				}
			}//end while
			if( vectorRecordCount > 0)
			{
				com.cannontech.billing.record.CADPRecord cadpRec =
					new com.cannontech.billing.record.CADPRecord(acctNumberVector,
														kwhValueVector, 
														kwValueVector, 
														kvarValueVector);
				getRecordVector().addElement(cadpRec);
			}
			
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
	System.out.println(" @CADP Data Collection : Took " + (System.currentTimeMillis() - timer));
	
	return true;
}
}
