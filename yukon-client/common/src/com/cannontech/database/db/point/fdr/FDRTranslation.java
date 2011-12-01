package com.cannontech.database.db.point.fdr;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.fdr.FDRInterface;

/**
 * This type was created in VisualAge.
 */
@Deprecated
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
 * Create a dummy FDR translation object from some reasonable default values.
 * Attempts to create a DSM2IMPORT interface.
 */
public static final FDRTranslation createTranslation( Integer pointID ) {
	
	FDRInterface[] interfaces =
		com.cannontech.database.db.point.fdr.FDRInterface.getALLFDRInterfaces();	
	if( interfaces.length > 0 ) {
		return new FDRTranslation(
			pointID,
			"Receive",
			"DSM2IMPORT",
			"DSM2IMPORT",
			"Point:1;POINTTYPE:Analog;");
	}
	else
		return new FDRTranslation(pointID);
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
	setTranslation( translation );
	setDestination( destination );

}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object addValues[]= { getPointID(), getDirectionType(), getInterfaceType(), getDestination(), getTranslation() };

	add( TABLE_NAME, addValues );
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
 * Finds the value of an entry with the translation in the following form (examples):
 *   Category:PSEUDO;Remote:14;Point:66;POINTTYPE:Analog;
 *   Point:234;Interval (sec):1;POINTTYPE:Analog;
 * 
 * First, the label is searched for, then the value on the right hand side of the : is
 * returned. The label search is case sensitive. Returns null if the value is not
 * found.
 */
public static String getTranslationValue( String translation, String label ) {
	
	if( translation == null || label == null ) return null;
	
	String[] entries = translation.split( ";" );
	
	for(String entry : entries){
	    //found the label, return the value
	    if(entry.startsWith(label)){
	        int valueBegin = entry.indexOf(":") + 1;
	        return entry.substring(valueBegin);
	    }
	}
	
	return null;
}

/**
 * Returns the new translation string with the passed in label value set.
 */
public static String setTranslationValue( String translation, String newVal, String label ) {
	
	if( translation == null || label == null || newVal == null ) return translation;
	
	StringBuffer buffer = new StringBuffer(translation);
	int startIndx = translation.indexOf( label + ":" );	
	int endIndx = startIndx + translation.substring(startIndx, translation.length()).indexOf(";");
	
	buffer.replace( startIndx, endIndx,
		label + ":" + newVal );
	
	return buffer.toString();
}

/**
 * This method was created in VisualAge.
 */
public static java.util.Vector<FDRTranslation> getFDRTranslations(Integer pointID) {
	
	return getFDRTranslations(pointID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public static java.util.Vector<FDRTranslation> getFDRTranslations(Integer pointID, String databaseAlias) 
{
	java.util.Vector<FDRTranslation> returnVector = new java.util.Vector<FDRTranslation>(10);
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
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, pstmt, conn );
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
		setDestination( (String) results[2] );
		setTranslation( (String) results[3] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
		
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setDestination(String destination) {
		this.destination = destination;
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

static public String getDestinationField( String trans ) 
{
	java.util.StringTokenizer tokenizer = new java.util.StringTokenizer( trans, ";" );
	while( tokenizer.hasMoreTokens() )
	{
		String token = tokenizer.nextElement().toString();
		if( token.toLowerCase().indexOf("estination") >= 0 )
			return token.substring( token.indexOf(":")+1, token.length() );
	}

	return "(not found)";
}
}
