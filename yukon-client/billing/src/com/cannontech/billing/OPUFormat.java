package com.cannontech.billing;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 3:56:10 PM)
 * @author: 
 */
public class OPUFormat extends FileFormatBase
{

/**
 * CTICSVFormat constructor comment.
 */
public OPUFormat() {
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
	String [] SELECT_COLUMNS =
	{
		SQLStringBuilder.PAO_PAONAME,
		SQLStringBuilder.RPH_VALUE,
		SQLStringBuilder.RPH_TIMESTAMP,
		SQLStringBuilder.PT_POINTID,
		SQLStringBuilder.PT_POINTNAME
	};

	String [] FROM_TABLES =
	{
		com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME,
		com.cannontech.database.db.point.RawPointHistory.TABLE_NAME,
		com.cannontech.database.db.point.Point.TABLE_NAME,
		com.cannontech.database.db.device.DeviceMeterGroup.TABLE_NAME
	};
	
	long timer = System.currentTimeMillis();

	if( dbAlias == null )
		dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
/*
	String sql = "SELECT PAO.PAONAME, RPH.VALUE, RPH.TIMESTAMP, PT.POINTID, PT.POINTNAME " +
				" FROM YUKONPAOBJECT PAO, RAWPOINTHISTORY RPH, POINT PT, DEVICEMETERGROUP DMG " +
				" WHERE PAO.PAOBJECTID = DMG.DEVICEID " +
				" AND PAO.PAOBJECTID = PT.PAOBJECTID " +
				" AND RPH.TIMESTAMP < ? " + 
				" AND DMG.COLLECTIONGROUP IN ('" + collectionGroups.get(0) + "'";
				for (int i = 1; i < collectionGroups.size(); i++)
				{
					sql += ", '" + collectionGroups.get(i) + "'";
				}

				sql += ") AND PT.POINTID = RPH.POINTID " +
				" AND ( (POINTTYPE = 'Analog' AND POINTOFFSET IN (" + kwhAnalogOffsets[0];
				for (int i = 1; i < kwhAnalogOffsets.length; i++)
				{
					sql += ", " + kwhAnalogOffsets[i];
				}

				sql += "))  OR  (POINTTYPE = 'PulseAccumulator' AND POINTOFFSET IN (" + validAccPtOffsets[0];
				for (int i = 1; i < validAccPtOffsets.length; i++)
				{
					sql += ", " + validAccPtOffsets[i];
				}
				sql += ") )) ORDER BY PAO.PAONAME, PT.POINTID, RPH.TIMESTAMP DESC";
	com.cannontech.clientutils.CTILogger.info((" Local SQL: " + sql);
*/
	SQLStringBuilder builder = new SQLStringBuilder();
	String sql = new String((builder.buildSQLStatement(SELECT_COLUMNS, FROM_TABLES, getBillingDefaults(), kwAnalogOffsets, validAccPtOffsets)).toString());
		sql += " ORDER BY " 
			+ SQLStringBuilder.PAO_PAONAME + ", " 
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
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setTimestamp(1, new java.sql.Timestamp(getBillingDefaults().getEndDate().getTime()));
			rset = pstmt.executeQuery();
			com.cannontech.clientutils.CTILogger.info(" *Start looping through return resultset");

			int currentPointID = 0;
			int lastPointID = 0;
			while (rset.next())
			{
				currentPointID = rset.getInt(4);
				
				double multiplier = 1;
				if( getBillingDefaults().getRemoveMultiplier())
				{
					multiplier = ((Double)getPointIDMultiplierHashTable().get(new Integer(currentPointID))).doubleValue();
				}
				
				if( currentPointID != lastPointID )	//just getting max time for each point
				{
					lastPointID = currentPointID;

					String name = rset.getString(1);
					double reading = rset.getDouble(2) / multiplier;
					java.sql.Timestamp ts = rset.getTimestamp(3);
					java.util.Date tsDate = new java.util.Date(ts.getTime());
					String ptName = rset.getString(5);

					if( tsDate.compareTo((Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
						break;
						
					com.cannontech.billing.record.OPURecord opuRec=
						new com.cannontech.billing.record.OPURecord(name, ptName, reading, ts, "N");
					getRecordVector().addElement(opuRec);

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
	com.cannontech.clientutils.CTILogger.info(" @OPU Data Collection : Took " + (System.currentTimeMillis() - timer));
	return true;
}
}
