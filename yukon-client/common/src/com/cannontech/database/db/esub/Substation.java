package com.cannontech.database.db.esub;

/**
 * Insert the type's description here.
 * Creation date: (12/19/2000 9:37:26 AM)
 * @author: 
 */
public class Substation extends com.cannontech.database.db.DBPersistent {

	private static final String tableName = "Substation";

	private int id;
	private String name;
/**
 * Substation constructor comment.
 */
public Substation() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] vals = { new Integer(getID()), getName() };
	add( tableName, vals );
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{
	delete( tableName, "SubstationID", new Integer(getID()) );
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:38:02 AM)
 * @return int
 */
public int getID() {
	return id;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:38:02 AM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	String[] selectColumns = { "Name" };
	String[] constraintColumns = { "SubstationID" };
	Object[] constraintValues = { new Integer(getID()) };
	
	Object[] result = retrieve( selectColumns, tableName, constraintColumns, constraintValues );

	if( result.length == selectColumns.length )
	{
		setName( (String) result[0] );
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:38:02 AM)
 * @param newID int
 */
public void setID(int newID) {
	id = newID;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:38:02 AM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	String[] setColumns = { "Name" };
	Object[] setValues = { getName() };
	String[] constraintColumns = { "SubstationID" };
	Object[] constraintValues = { new Integer(getID()) };
	
	update( tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}
