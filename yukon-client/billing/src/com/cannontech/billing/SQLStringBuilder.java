package com.cannontech.billing;

/**
 * Insert the type's description here.
 * Creation date: (7/10/2002 11:15:09 AM)
 * @author: 
 */
public class SQLStringBuilder 
{
	public static final String PAO_PAONAME = "YUKONPAOBJECT.PAONAME";

	public static final String RPH_VALUE = "RAWPOINTHISTORY.VALUE";
	public static final String RPH_TIMESTAMP = "RAWPOINTHISTORY.TIMESTAMP";

	public static final String PT_POINTID = "POINT.POINTID";
	public static final String PT_POINTNAME = "POINT.POINTNAME";
	public static final String PT_POINTOFFSET = "POINT.POINTOFFSET";
	
	public static final String UM_UOMNAME = "UNITMEASURE.UOMNAME";

	public static final String DMG_DEVICEID = "DEVICEMETERGROUP.DEVICEID";
	public static final String DMG_METERNUMBER = "DEVICEMETERGROUP.METERNUMBER";
	// COLUMNS THAT MAY BE USED AS BILLING GROUPINGS.
	//public static final String DMG_COLLECTIONGROUP = "DEVICEMETERGROUP.COLLECTIONGROUP";
	//public static final String DMG_TESTCOLLECTIONGROUP = "DEVICEMETERGROUP.TESTCOLLECTIONGROUP";
	//public static final String DMG_BILLINGGROUP = "DEVICEMETERGROUP.BILLINGGROUP";
	

	// true when table is used in sql string.
	boolean deviceMeterGroup_from = false;
	boolean yukonPAObjectTable_from = false;
	boolean rawPointHistoryTable_from = false;
	boolean point_from = false;
	boolean device_from = false;
	boolean pointUnit_from = false;
	boolean unitMeasure_from = false;
public SQLStringBuilder()
{
}
private String buildFromClause( String [] tables )
{
	String fromString = " FROM " + tables[0];
	for ( int i = 1; i < tables.length; i++)
	{
		fromString += ", " + tables[i];
	}


	// set the boolean values of the tables that exist.	
	for ( int i = 0; i < tables.length; i++)
	{
		//setBooleanTables(tables[i]);
		if( tables[i].equalsIgnoreCase(com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME))
		{
			yukonPAObjectTable_from = true;
		}
		else if( tables[i].equalsIgnoreCase(com.cannontech.database.db.device.DeviceMeterGroup.TABLE_NAME))
		{
			deviceMeterGroup_from = true;
		}
		else if( tables[i].equalsIgnoreCase(com.cannontech.database.db.point.Point.TABLE_NAME))
		{
			point_from = true;
		}
		else if (tables[i].equalsIgnoreCase(com.cannontech.database.db.point.RawPointHistory.TABLE_NAME))
		{
			rawPointHistoryTable_from = true;
		}
		else if( tables[i].equalsIgnoreCase(com.cannontech.database.db.point.PointUnit.TABLE_NAME))
		{
			pointUnit_from = true;
		}
		else if( tables[i].equalsIgnoreCase(com.cannontech.database.db.point.UnitMeasure.TABLE_NAME))
		{
			unitMeasure_from = true;
		}
	}
	return fromString;
}
private String buildSelectClause( String [] columns)
{
	String selectString = "SELECT " + columns[0];
	for ( int i = 1; i < columns.length; i++)
	{
		selectString += ", " + columns[i];
	}
	
	return selectString;
}
public StringBuffer buildSQLStatement(String[] columns, String [] tables, com.cannontech.billing.mainprograms.BillingFileDefaults billingDefaults, int [] analogOffsets, int [] accOffsets)
{
	if(columns == null || tables == null)
	{
		return null;
	}

	
	//setTableBooleans(tables
	StringBuffer sqlBuffer = new StringBuffer(buildSelectClause(columns));
	
	sqlBuffer.append( buildFromClause(tables));
	sqlBuffer.append( buildWhereClause( billingDefaults.getBillGroup(), billingDefaults.getBillGroupColumn(), analogOffsets, accOffsets));


	System.out.println(" SQL Statement: " + sqlBuffer.toString());
	return sqlBuffer;
}
private String buildWhereClause(java.util.Vector groupVector, String groupingColumn, int [] analogOffsets, int [] accOffsets)
{
	java.util.Vector whereClauses = new java.util.Vector();
	
	//String whereString = " WHERE ";
	if( yukonPAObjectTable_from && deviceMeterGroup_from)
	{
		whereClauses.add(new String(" YUKONPAOBJECT.PAOBJECTID = DEVICEMETERGROUP.DEVICEID "));
	}
	if ( yukonPAObjectTable_from && point_from)
	{
		whereClauses.add(new String(" YUKONPAOBJECT.PAOBJECTID = POINT.PAOBJECTID "));
	}
	if( rawPointHistoryTable_from)
	{
		whereClauses.add(new String(" RAWPOINTHISTORY.TIMESTAMP <= ? "));	//END BILLING DATE
	}

	if( rawPointHistoryTable_from && point_from)
	{
		whereClauses.add(new String(" RAWPOINTHISTORY.POINTID = POINT.POINTID "));
	}
	
	if( deviceMeterGroup_from)
	{
		String inCollectionGroup = new String(groupingColumn + " IN ('" + groupVector.get(0) + "'");
		for (int i = 1; i < groupVector.size(); i++)
		{
			inCollectionGroup += ", '" + groupVector.get(i) + "'";
		}
		inCollectionGroup += ")";
			
		whereClauses.add(inCollectionGroup);
	}
	if( point_from )
	{
		if( pointUnit_from && unitMeasure_from)
		{
			whereClauses.add(new String(" POINT.POINTID = POINTUNIT.POINTID "));
			whereClauses.add(new String(" POINTUNIT.UOMID = UNITMEASURE.UOMID"));
		}
		
		if( deviceMeterGroup_from)
		{
			whereClauses.add(new String(" POINT.PAOBJECTID = DEVICEMETERGROUP.DEVICEID"));
		}
		// select valid pointtypes with appropriate pointoffsets.
		String pointTypeString = new String(" ((POINTTYPE = 'Analog' AND POINTOFFSET IN (" + analogOffsets[0]);
		for (int i = 1; i < analogOffsets.length; i++)
		{
			pointTypeString += ", " + analogOffsets[i];
		}
		
		pointTypeString += ")) OR (POINTTYPE = 'PulseAccumulator' AND POINTOFFSET IN (" + accOffsets[0];
		for (int i = 1; i < accOffsets.length; i++)
		{
			pointTypeString += ", " + accOffsets[i];
		}
		pointTypeString += " ))) ";

		whereClauses.add(pointTypeString);
	}


	if( !whereClauses.isEmpty())
	{
		String whereString = new String(" WHERE " + whereClauses.get(0));
		for (int i = 1; i < whereClauses.size(); i++)
		{
			whereString += " AND " + whereClauses.get(i);
		}
		return whereString;
	}
	else
	{
		return null;
	}
}
}
