package com.cannontech.database.db.point.fdr;

/**
 * This type was created in VisualAge.
 */
import java.sql.*;

import com.cannontech.database.db.*;

public class FDRTranslation extends com.cannontech.database.db.DBPersistent {
	private Integer pointID = null;
	private String directionType = null;
	private String interfaceType = null;
	private String destination = "(not used)"; //not used as of 5-31-2001
	private String translation = null;

	public static final String TABLE_NAME = "FDRTranslation";
/**
 * Point constructor comment.
 */
public FDRTranslation() 
{
	super();
}
/**
 * Point constructor comment.
 */
public FDRTranslation(Integer pointID) 
{
	super();
	setPointID( pointID );
}
/**
 * Point constructor comment.
 */
public FDRTranslation( Integer pointID, String directionType, String interfaceType, String destination, String translation ) 
{
	super();
	setPointID( pointID );
	setDirectionType( directionType );
	setInterfaceType( interfaceType );
	setDestination( destination );
	setTranslation( translation );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object addValues[]= { getPointID(), getDirectionType(), getInterfaceType(), getDestination(), getTranslation() };

	add( this.TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	delete( TABLE_NAME, "POINTID", getPointID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getDestination() {
	return destination;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getDirectionType() {
	return directionType;
}
/**
 * This method was created in VisualAge.
 */
public static java.util.Vector getFDRTranslations(Integer pointID) {
	
	return getFDRTranslations(pointID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public static java.util.Vector getFDRTranslations(Integer pointID, String databaseAlias) 
{
	java.util.Vector returnVector = new java.util.Vector(10);
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT DirectionType, InterfaceType, Destination, " + 
				 "Translation FROM " + TABLE_NAME + " WHERE PointID= ?";

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);

		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, pointID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				returnVector.addElement( new FDRTranslation( 
							pointID, 
							rset.getString("DirectionType"), 
							rset.getString("InterfaceType"), 
							rset.getString("Destination"), 
							rset.getString("Translation") ) );
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
			if( pstmt != null ) pstmt.close();
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			e2.printStackTrace();//something is up
		}	
	}
	
	return returnVector;
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 1:26:25 PM)
 * @return java.lang.String
 */
public java.lang.String getInterfaceType() {
	return interfaceType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPointID() {
	return pointID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getTranslation() {
	return translation;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	String selectColumns[] = { "DIRECTIONTYPE", "INTERFACETYPE", "DESTINATION", "TRANSLATION" };

	String constraintColumns[] = { "POINTID" };
	Object constraintValues[] = { getPointID() };

	Object results[] = retrieve( selectColumns, TABLE_NAME, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setDirectionType( (String) results[0] );
		setInterfaceType( (String) results[1]);
		setDestination( (String) results[2]);
		setTranslation( (String) results[3] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
		
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setDestination(String newValue) {
	this.destination = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setDirectionType(String newValue) {
	this.directionType = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 1:26:25 PM)
 * @param newInterfaceType java.lang.String
 */
public void setInterfaceType(java.lang.String newInterfaceType) {
	interfaceType = newInterfaceType;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPointID(Integer newValue) {
	this.pointID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setTranslation(String newValue) {
	this.translation = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	String setColumns[] = { "DIRECTIONTYPE", "INTERFACETYPE", "DESTINATION", "TRANSLATION" };
	Object setValues[]= { getDirectionType(), getInterfaceType(), getDestination(),getTranslation() };

	String constraintColumns[] = { "POINTID" };
	Object constraintValues[] = { getPointID() };

	update( TABLE_NAME, setColumns, setValues, constraintColumns, constraintValues );
}
}
