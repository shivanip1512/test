package com.cannontech.database.data.lite;

/*
 */
public class LitePointUnit extends LiteBase
{
	private int uomID = 0;
	private int decimalPlaces = 0;

/**
 * LitePointUnit
 */
public LitePointUnit( int pntID ) 
{
	super();
	setLiteID(pntID);
	setLiteType(LiteTypes.POINT_UNIT);
}
/**
 * LitePointUnit
 */
public LitePointUnit( int pntID, int uomid, int decimals) 
{
	super();
	
	setLiteID(pntID);	
	uomID = uomid;
	decimalPlaces = decimals;
	setLiteType(LiteTypes.POINT_UNIT);
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 1:56:09 PM)
 * @return int
 */
public int getUomID() {
	return uomID;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public int getPointID() {
	return getLiteID();
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public int getDecimalPlaces() {
	return decimalPlaces;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
 	com.cannontech.database.SqlStatement stmt =
 		new com.cannontech.database.SqlStatement("SELECT UOMID, DECIMALPLACES FROM PointUnit WHERE PointID = " + Integer.toString(getPointID()), databaseAlias);

 	try
 	{
 		stmt.execute();
		setUomID( ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue() );
		setDecimalPlaces( ((java.math.BigDecimal) stmt.getRow(0)[1]).intValue() );
 	}
 	catch( Exception e )
 	{
 		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
 	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 1:56:09 PM)
 * @param newPaobjectID int
 */
public void setUomID(int newUomID) {
	uomID = newUomID;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setPointID(int newValue) 
{
	setLiteID(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setDecimalPlaces(int newDecimals) {
	this.decimalPlaces = newDecimals;
}
}
