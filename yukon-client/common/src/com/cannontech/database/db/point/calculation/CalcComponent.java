package com.cannontech.database.db.point.calculation;

import java.util.Vector;

import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */

public class CalcComponent extends com.cannontech.database.db.DBPersistent 
{
	private Integer pointID = null;
	private Integer componentOrder = null;
	private String componentType = null;
	private Integer componentPointID = null;
	private String operation = null;
	private Double constant = null;
	private String functionName = null;
	
	public static final String COLUMNS[] = { 
			"POINTID", 
			"COMPONENTORDER",
			"COMPONENTTYPE", 
			"COMPONENTPOINTID", 
			"OPERATION", 
			"CONSTANT", 
			"FUNCTIONNAME" };


	public static final String SELECT_COLUMNS[] = { COLUMNS[1], COLUMNS[2], COLUMNS[3], COLUMNS[4], COLUMNS[5], COLUMNS[6] };

	public static final String TABLENAME = "CalcComponent";
/**
 * Point constructor comment.
 */
public CalcComponent() {
	super();
	initialize( null, null, null, null, null, null, null );
}
/**
 * Point constructor comment.
 */
public CalcComponent(Integer pointID) {
	super();
	initialize(pointID, null, null, null, null, null, null );
}
/**
 * Point constructor comment.
 */
public CalcComponent( Integer pointID, Integer componentOrder, String componentType, Integer componentPointID, String operation, Double constant, String functionName ) {
	super();
	initialize( pointID, componentOrder, componentType, componentPointID, operation, constant, functionName );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object addValues[]= { getPointID(), getComponentOrder(), getComponentType(), getComponentPointID(), getOperation(), getConstant(), getFunctionName() };

	add( this.TABLENAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	delete( TABLENAME, COLUMNS[0], getPointID() );
}

/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param pointID java.lang.Integer
 */
public static boolean deleteCalcComponents(Integer pointID, java.sql.Connection conn )
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement(
            "DELETE FROM " + TABLENAME + " WHERE " + COLUMNS[0] + "=" + pointID,
				conn );
	try
	{
		stmt.execute();
	}
	catch(Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}

	return true;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public static Vector<CalcComponent> getCalcComponents(Integer pointID){

	return getCalcComponents( pointID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceScanRate[]
 * @param deviceID java.lang.Integer
 */
public static Vector<CalcComponent> getCalcComponents(Integer pointID, String databaseAlias)
{
	Vector<CalcComponent> returnVector = null;
	Integer componentOrder = null;
	String componentType = null;
	Integer componentPointID = null;
	String operation = null;
	Double constant = null;
	String functionName = null;

	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT " + COLUMNS[1] +"," + COLUMNS[2] + "," + COLUMNS[3] +
		"," + COLUMNS[4] + "," + COLUMNS[5] + "," + COLUMNS[6] + " FROM CalcComponent" +
		" WHERE " + COLUMNS[0] + " = ? ORDER BY " + COLUMNS[1];

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
			returnVector = new Vector<CalcComponent>(5);
	
			while( rset.next() )
			{
				componentOrder = new Integer( rset.getInt(COLUMNS[1]) );
				componentType = rset.getString(COLUMNS[2]);
				componentPointID = new Integer( rset.getInt(COLUMNS[3]) );
				operation = rset.getString(COLUMNS[4]);
				constant = new Double( rset.getDouble(COLUMNS[5]) );
				functionName = rset.getString(COLUMNS[6]);
				
				returnVector.addElement( new CalcComponent( pointID, componentOrder, componentType, componentPointID, operation, constant, functionName ) );				
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
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getComponentOrder() {
	return componentOrder;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getComponentPointID() {
	return componentPointID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getComponentType() {
	return componentType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Double
 */
public Double getConstant() {
	return constant;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getFunctionName() {
	return functionName;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getOperation() {
	return operation;
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
 */
public void initialize( Integer pointID, Integer componentOrder, String componentType, Integer componentPointID, String operation, Double constant, String functionName ) {

	setPointID( pointID ) ;
	setComponentOrder( componentOrder );
	setComponentType( componentType );
	setComponentPointID( componentPointID );
	setOperation( operation );
	setConstant( constant );
	setFunctionName( functionName );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	String constraintColumns[] = { COLUMNS[0] };
	Object constraintValues[] = { getPointID() };

	Object results[] = retrieve( SELECT_COLUMNS, TABLENAME, constraintColumns, constraintValues );

	if( results.length == SELECT_COLUMNS.length )
	{
		setComponentOrder( (Integer) results[0] );
		setComponentType( (String) results[1]);
		setComponentPointID( (Integer) results[2]);
		setOperation( (String) results[3]);
		setConstant( (Double) results[4]);
		setFunctionName( (String) results[5]);
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
		
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setComponentOrder(Integer newValue) {
	this.componentOrder = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setComponentPointID(Integer newValue) {
	this.componentPointID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setComponentType(String newValue) {
	this.componentType = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Double
 */
public void setConstant(Double newValue) {
	this.constant = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setFunctionName(String newValue) {
	this.functionName = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setOperation(String newValue) {
	this.operation = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPointID(Integer newValue) {
	this.pointID = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[]= { getComponentOrder(), getComponentType(), getComponentPointID(), getOperation(), getConstant(), getFunctionName() };

	String constraintColumns[] = { COLUMNS[0] };
	Object constraintValues[] = { getPointID() };

	update( TABLENAME, SELECT_COLUMNS, setValues, constraintColumns, constraintValues );
}
}
