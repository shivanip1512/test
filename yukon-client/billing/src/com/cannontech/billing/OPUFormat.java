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
public boolean retrieveBillingData(java.util.Vector collectionGroups, String dbAlias)
{
	long timer = System.currentTimeMillis();

	if( dbAlias == null )
		dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();

	if (collectionGroups.isEmpty())
		return false;

	String sql = "SELECT PAO.PAONAME, RPH.VALUE, RPH.TIMESTAMP, PT.POINTID, PT.POINTNAME " +
				" FROM YUKONPAOBJECT PAO, RAWPOINTHISTORY RPH, POINT PT, DEVICEMETERGROUP DMG " +
				" WHERE PAO.PAOBJECTID = DMG.DEVICEID " +
				" AND PAO.PAOBJECTID = PT.PAOBJECTID " +
				" AND DMG.COLLECTIONGROUP IN ('" + collectionGroups.get(0) + "'";
				for (int i = 1; i < collectionGroups.size(); i++)
				{
					sql += ", '" + collectionGroups.get(i) + "'";
				}

				sql += ") AND PT.POINTID = RPH.POINTID " +
				" AND ( (POINTTYPE = 'Analog' AND POINTOFFSET IN (" + kwhAnalogOffsets[0];
				for (int i = 0; i < kwhAnalogOffsets.length; i++)
				{
					sql += ", " + kwhAnalogOffsets[i];
				}

				sql += "))  OR  ((POINTTYPE = 'PulseAccumulator' OR POINTTYPE = 'DemandAccumulator') AND POINTOFFSET IN (" + validAccPtOffsets[0];
				for (int i = 0; i < validAccPtOffsets.length; i++)
				{
					sql += ", " + validAccPtOffsets[i];
				}
				sql += ") )) ORDER BY PAO.PAONAME, PT.POINTID, RPH.TIMESTAMP DESC";

	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
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
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql.toString());

			System.out.println(" *Start looping through return resultset");

			int currentPointID = 0;
			int lastPointID = 0;
			while (rset.next())
			{
				currentPointID = rset.getInt(4);
				if( currentPointID != lastPointID )	//just getting max time for each point
				{
					lastPointID = currentPointID;

					String name = rset.getString(1);
					double reading = rset.getDouble(2);
					java.sql.Timestamp ts = rset.getTimestamp(3);
					String ptName = rset.getString(5);
					
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
			if( stmt != null ) stmt.close();
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
