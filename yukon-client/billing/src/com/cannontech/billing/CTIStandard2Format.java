package com.cannontech.billing;

import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 3:56:10 PM)
 * @author: 
 */
public class CTIStandard2Format extends FileFormatBase
{

/**
 * CTICSVFormat constructor comment.
 */
public CTIStandard2Format() {
	super();
}

/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 1:41:30 PM)
 * @return boolean
 * @param collectionGroups java.util.Vector
 * @param dbAlias java.lang.String
 */
public boolean retrieveBillingData(String dbAlias)
{
	long timer = System.currentTimeMillis();

	if( dbAlias == null )
		dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();

	String [] SELECT_COLUMNS =
	{
		SQLStringBuilder.DCS_ADDRESS,
		SQLStringBuilder.PT_POINTNAME,
		SQLStringBuilder.PT_POINTID,
		SQLStringBuilder.RPH_VALUE,
		SQLStringBuilder.RPH_TIMESTAMP
	};

	String [] FROM_TABLES =
	{
		com.cannontech.database.db.point.RawPointHistory.TABLE_NAME,
		com.cannontech.database.db.point.Point.TABLE_NAME,
		com.cannontech.database.db.device.DeviceCarrierSettings.TABLE_NAME
	};

	SQLStringBuilder builder = new SQLStringBuilder();
	String sql = new String((builder.buildSQLStatement(SELECT_COLUMNS, FROM_TABLES, getBillingDefaults(), null, validAccPtOffsets)).toString());
		sql += " ORDER BY " 
		+ SQLStringBuilder.DCS_ADDRESS + ", "
		+ SQLStringBuilder.PT_POINTID + ", " 
		+ SQLStringBuilder.RPH_TIMESTAMP + " DESC ";

	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
		if( conn == null )
		{
			com.cannontech.clientutils.CTILogger.info(getClass() + ":  Error getting database connection.");
			return false;
		}
		else
		{
			pstmt = conn.prepareStatement(sql);
			pstmt.setTimestamp(1, new java.sql.Timestamp(getBillingDefaults().getEarliestStartDate().getTime()));
			rset = pstmt.executeQuery();

			com.cannontech.clientutils.CTILogger.info(" *Start looping through return resultset");

			int currentPointID = 0;
			int lastPointID = 0;
			while (rset.next())
			{
				java.sql.Timestamp ts = rset.getTimestamp(5);
				Date tsDate = new Date(ts.getTime());
				if( tsDate.compareTo( (Object)getBillingDefaults().getEndDate()) <= 0) //ts <= maxtime, CONTINUE ON!
				{
					currentPointID = rset.getInt(3);
					double multiplier = 1;
					if( getBillingDefaults().isRemoveMultiplier())
					{
						multiplier = ((Double)getPointIDMultiplierHashTable().get(new Integer(currentPointID))).doubleValue();
					}
	
					// Our break label so we can exit the if-else checks
					inValidTimestamp:
					if( currentPointID != lastPointID )	//just getting max time for each point
					{
						if( tsDate.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
							break inValidTimestamp;

						lastPointID = currentPointID;
	
						String address = rset.getString(1);
						String pointName = rset.getString(2);
						double reading = rset.getDouble(4) / multiplier;
						
						com.cannontech.billing.record.CTIStandard2Record cti2Rec = 
							new com.cannontech.billing.record.CTIStandard2Record(address, pointName, reading, ts);
						getRecordVector().addElement(cti2Rec);
	
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
	com.cannontech.clientutils.CTILogger.info("@" +this.toString() +" Data Collection : Took " + (System.currentTimeMillis() - timer));
	return true;
}
}
