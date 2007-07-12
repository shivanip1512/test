package com.cannontech.database.db.macro;

/**
 * This type was created in VisualAge.
 */
import java.sql.SQLException;

import com.cannontech.database.SqlUtils;

public class GenericMacro extends com.cannontech.database.db.DBPersistent {
	
	private Integer ownerID = null;
	private Integer childID = null;
	private Integer childOrder = null;
	private String macroType = null;

	public static final String TABLE_NAME = "GenericMacro";
/**
 * This method was created in VisualAge.
 */
public GenericMacro() {
	super();

	initialize( null,  null, null, null );
}
/**
 * This method was created in VisualAge.
 */
public GenericMacro(Integer routeID, Integer routeOrder) {
	super();

	initialize( ownerID, childID, null, null );
}
/**
 * This method was created in VisualAge.
 */
public GenericMacro(Integer ownerID,  Integer childID, Integer childOrder) {
	super();
	initialize( ownerID,  childID, childOrder, null );
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2001 4:12:34 PM)
 * @param ownerID java.lang.Integer
 * @param childID java.lang.Integer
 * @param childOrder java.lang.Integer
 * @param macroType java.lang.String
 */
public GenericMacro(Integer ownerID, Integer childID, Integer childOrder, String macroType)
{
	super();
	initialize(ownerID, childID, childOrder, macroType);
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	
	Object addValues[] = { getOwnerID(), getChildID(), getChildOrder(), getMacroType() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	String constraintColumns[] = { "ownerID", "childOrder" };
	Object constraintValues[] = { getOwnerID(), getChildOrder() };

	delete( TABLE_NAME, constraintColumns, constraintValues );
}
/**
 * This method was deletes any rows from the MacroRoute table where RouteID = macroRouteID
 * @param macroRouteID java.lang.Integer
 */
public final static boolean deleteAllGenericMacros(Integer ownerID, String macroType, java.sql.Connection conn) throws SQLException 
{
	java.sql.PreparedStatement pstmt = null;

	String sql = "DELETE FROM " + TABLE_NAME + " WHERE OwnerID=" + 
					ownerID + " and MacroType='" + macroType + "'";

	try
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("Unable to update database since our connect = (null)");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.executeUpdate();
		}
	}
	catch (java.sql.SQLException e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}
	finally
	{
		try
		{
			if (pstmt != null)
				pstmt.close();
		}
		catch (java.sql.SQLException e2)
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 ); //something is up
		}
	}

	return true;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getChildID() {
	return childID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getChildOrder() {
	return childOrder;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.route.MacroRoute[]
 * @param routeID java.lang.Integer
 */
public static GenericMacro[] getGenericMacros(Integer ownerID, String macroType, java.sql.Connection conn) throws SQLException 
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT childID,childOrder,macroType FROM " + TABLE_NAME + 
					" WHERE ownerID= ? and MacroType = ? ORDER BY childOrder";

	try
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("Unable to access database since the connect = (null).");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(1, ownerID.intValue());
			pstmt.setString(2, macroType );

			rset = pstmt.executeQuery();

			while (rset.next())
			{
				tmpList.add(new GenericMacro(
						ownerID, 
						new Integer(rset.getInt("ChildID")), 
						new Integer(rset.getInt("childOrder")), 
						macroType) );
			}

		}
	}
	catch (java.sql.SQLException e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, pstmt);
	}

	GenericMacro retVal[] = new GenericMacro[tmpList.size()];
	tmpList.toArray(retVal);

	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2001 4:10:28 PM)
 * @return java.lang.String
 */
public String getMacroType() {
	return macroType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getOwnerID() {
	return ownerID;
}
/**
 * This method was created in VisualAge.
 * @param name java.lang.String
 * @param routeName java.lang.String
 * @param routeOrder java.lang.Integer
 */
public void initialize( Integer ownerID, Integer childID, Integer childOrder, String macroType )
{
	setOwnerID( ownerID );
	setChildID( childID) ;
	setChildOrder( childOrder );
	setMacroType( macroType);
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "ChildID" };

	String constraintColumns[] = { "ownerID", "childOrder"};
	Object constraintValues[] = { getOwnerID(), getChildOrder() };

	Object results[] = retrieve(selectColumns, TABLE_NAME, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setChildID( (Integer) results[0] );
	}
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setChildID(Integer newValue) {
	this.childID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setChildOrder(Integer newValue) {
	this.childOrder = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2001 4:01:38 PM)
 */
public void setMacroType(String type) {
	macroType = type;
	
	}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setOwnerID(Integer newValue) {
	this.ownerID = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "childID" };
	Object setValues[] = { getChildID() };

	String constraintColumns[] = { "ownerID", "childOrder" };
	Object constraintValues[] = { getOwnerID(), getChildOrder() };

	update( TABLE_NAME, setColumns, setValues, constraintColumns, constraintValues );
}
}
