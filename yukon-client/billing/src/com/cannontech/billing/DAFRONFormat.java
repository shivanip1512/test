package com.cannontech.billing;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class DAFRONFormat extends FileFormatBase 
{
	java.sql.Connection dbConnection = null;
	java.sql.PreparedStatement prepStatement = null;

	public static final String HEADER =
		"H    Meter    Kwh   Time   Date   Peak   PeakT  PeakD  Stat Sig Freq Phase\r\n";

	public static final java.text.SimpleDateFormat DATE_FORMATTER = 
				new java.text.SimpleDateFormat("yy/MM/dd");
				
	public static final java.text.SimpleDateFormat TIME_FORMATTER = 
				new java.text.SimpleDateFormat("HH:mm");

	public static final java.text.DecimalFormat NUMBER_FORMATTER = 
		new java.text.DecimalFormat();

	static
	{
		NUMBER_FORMATTER.applyPattern("#####");
	}

/**
 * Default DAFRON constructor
 */
public DAFRONFormat() 
{
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2001 3:22:12 PM)
 */
private void removeDuplicateMeters() 
{
	//we must remove items from the end of our Vector
	int itemPos = getRecordVector().size() - 1;
	int j = itemPos - 1;
	
	for( int i = itemPos; i >= 0; i--, j-- )
	{
		//be sure we are not at the last element in our Vector
		if( j >=0 )
		{
			if( getRecordVector().get(i).equals(getRecordVector().get(j)) )
			{
				if( ((com.cannontech.billing.record.DAFRONRecord)getRecordVector().get(i)).getReadingKWH().doubleValue() >=
					    ((com.cannontech.billing.record.DAFRONRecord)getRecordVector().get(j)).getReadingKWH().doubleValue() )
					 getRecordVector().remove(j);
				else
					getRecordVector().remove(i);
			}
		}
		
	}
	
}
/**
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
public boolean retrieveBillingData(java.util.Vector collectionGroups, String dbAlias)
{	
	long timer = System.currentTimeMillis();
	
	if( dbAlias == null)
		dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();

	if( collectionGroups.isEmpty() )
		return false;

	StringBuffer sql = new StringBuffer("SELECT DMG.METERNUMBER, PT.POINTID, RPH.TIMESTAMP, RPH.VALUE, DMG.DEVICEID, PT.POINTOFFSET " +
				" FROM POINT PT, RAWPOINTHISTORY RPH, DEVICEMETERGROUP DMG " +
				" WHERE PT.PAOBJECTID = DMG.DEVICEID " +
				" AND RPH.TIMESTAMP < ? " + 
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

				sql.append("))  OR  (POINTTYPE = 'PulseAccumulator'  AND POINTOFFSET IN (" + validAccPtOffsets[0]);
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

			System.out.println(" * Start looping through return resultset");
			
			int recCount = 0;
			
			if( !isAppending() )
			{
				getRecordVector().add( new com.cannontech.billing.record.StringRecord(HEADER) );
				recCount ++;
			}

			
			int currentPointID = 0;
			int lastPointID = 0;
			int lastDeviceID = 0;
			int currentDeviceID = 0;
				
			while (rset.next())
			{
				currentPointID = rset.getInt(2);
				if( currentPointID != lastPointID )	//just getting max time for each point
				{
					lastPointID = currentPointID;

					String meterNumber = rset.getString(1);
					java.sql.Timestamp ts = rset.getTimestamp(3);
					double reading = rset.getDouble(4);
					currentDeviceID = rset.getInt(5);
					int ptOffset = rset.getInt(6);
					Date tsDate = new Date(ts.getTime());
					
					inValidTimestamp:
					if( currentDeviceID == lastDeviceID)
					{
						if (ptOffset == 1 || isKWH(ptOffset))
						{
							if( tsDate.compareTo((Object)getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;
								
							//** Get the last record and add to it the other pointOffsets' values. **//
							com.cannontech.billing.record.DAFRONRecord lastRecord =
								(com.cannontech.billing.record.DAFRONRecord)getRecordVector().get(recCount -1);

							lastRecord.setReadingKWH(reading);
							lastRecord.setTime(ts);
							lastRecord.setDate(ts);
						}
						else if (isKW(ptOffset))
						{
							if( tsDate.compareTo((Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;
								
							//** Get the last record and add to it the other pointOffsets' values. **//
							com.cannontech.billing.record.DAFRONRecord lastRecord =
								(com.cannontech.billing.record.DAFRONRecord)getRecordVector().get(recCount -1);

							lastRecord.setPeakReadingKW(reading);
							lastRecord.setPeakTimeKW(ts);
							lastRecord.setPeakDateKW(ts);
						}												
					}
					else
					{
						com.cannontech.billing.record.DAFRONRecord dafronRec = 
							new com.cannontech.billing.record.DAFRONRecord(meterNumber);
						if (ptOffset == 1 || isKWH(ptOffset))
						{
							if( tsDate.compareTo((Object)getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;
							
							dafronRec.setReadingKWH(reading);
							dafronRec.setTime(ts);
							dafronRec.setDate(ts);

						}
						else if (isKW(ptOffset))
						{
							if( tsDate.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;

							dafronRec.setPeakReadingKW(reading);
							dafronRec.setPeakTimeKW(ts);
							dafronRec.setPeakDateKW(ts);
						}
						lastDeviceID = currentDeviceID;
						getRecordVector().addElement(dafronRec);
						recCount++;
					}
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
			if( rset != null ) rset.close();
			if( pstmt != null ) pstmt.close();
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			e2.printStackTrace();//sometin is up
		}	
	}
	System.out.println(" @DAFRON Data Collection : Took " + (System.currentTimeMillis() - timer));
	return true;
}
}
