package com.cannontech.billing;

import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 3:56:10 PM)
 * @author: 
 */
public class CTICSVFormat extends FileFormatBase
{

/**
 * CTICSVFormat constructor comment.
 */
public CTICSVFormat() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 1:41:30 PM)
 * @return boolean
 * @param collectionGroups java.util.Vector
 * @param dbAlias java.lang.String
 */
public boolean retrieveBillingData(java.util.Vector collectionGroups, String dbAlias)
{
	long timer = System.currentTimeMillis();

	if( dbAlias == null )
		dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();

	if (collectionGroups.isEmpty())
		return false;

	String sql = "SELECT PAO.PAONAME, RPH.VALUE, RPH.TIMESTAMP, PT.POINTID, UM.UOMNAME, PT.POINTOFFSET " +
				" FROM YUKONPAOBJECT PAO, RAWPOINTHISTORY RPH, POINT PT, DEVICEMETERGROUP DMG, " +
				" POINTUNIT PU, UNITMEASURE UM " + 
				" WHERE PAO.PAOBJECTID = DMG.DEVICEID " +
				" AND PAO.PAOBJECTID = PT.PAOBJECTID " +
				" AND RPH.TIMESTAMP <= ? " +		//endDate timeStamp
				" AND DMG.COLLECTIONGROUP IN ('" + collectionGroups.get(0) + "'";
				for (int i = 1; i < collectionGroups.size(); i++)
				{
					sql += ", '" + collectionGroups.get(i) + "'";
				}

				sql += ") AND PT.POINTID = RPH.POINTID " +
				" AND PT.POINTID = PU.POINTID " +
				" AND PU.UOMID = UM.UOMID " + 
				" AND ( (POINTTYPE = 'Analog' AND POINTOFFSET IN (" + validAnalogPtOffsets[0];
				for (int i = 1; i < validAnalogPtOffsets.length; i++)
				{
					sql += ", " + validAnalogPtOffsets[i];
				}

				sql += "))  OR  (POINTTYPE = 'PulseAccumulator' AND POINTOFFSET IN (" + validAccPtOffsets[0];
				for (int i = 1; i < validAccPtOffsets.length; i++)
				{
					sql += ", " + validAccPtOffsets[i];
				}
				sql += ") )) ORDER BY PAO.PAONAME, PT.POINTOFFSET, PT.POINTID, RPH.TIMESTAMP DESC";

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
			pstmt = conn.prepareStatement(sql);
			pstmt.setTimestamp(1,new java.sql.Timestamp(getBillingDefaults().getEndDate().getTime()));
			rset = pstmt.executeQuery();

			System.out.println(" *Start looping through return resultset");

			int currentPointID = 0;
			int lastPointID = 0;
			while (rset.next())
			{
				currentPointID = rset.getInt(4);

				// Our break label so we can exit the if-else checks
				inValidTimestamp:
				if( currentPointID != lastPointID )	//just getting max time for each point
				{
					java.sql.Timestamp ts = rset.getTimestamp(3);
					int ptOffset = rset.getInt(6);
					Date tsDate = new Date(ts.getTime());
										
					if( isKWH(ptOffset) || ptOffset == 1) 
					{
						if( tsDate.compareTo( (Object)getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
							break inValidTimestamp;
					}
					else if( isKW(ptOffset) || isKVAR(ptOffset))
					{
						if( tsDate.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
							break inValidTimestamp;
					}
					
					lastPointID = currentPointID;

					String paoName = rset.getString(1);
					double reading = rset.getDouble(2);
					String unitMeasure = rset.getString(5);
					
					com.cannontech.billing.record.CTICSVRecord csvRec = 
						new com.cannontech.billing.record.CTICSVRecord(paoName, reading, unitMeasure, ts);
					getRecordVector().addElement(csvRec);

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
	System.out.println(" @CTI_CSV Data Collection : Took " + (System.currentTimeMillis() - timer));
	return true;
}
}
