package com.cannontech.database.db.starscustomer;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class CustomerLoadInformation extends com.cannontech.database.db.DBPersistent 
{
	private Integer loadID = null;
	private Integer accountID = null;
	private Integer typeID = null;
	private Integer programID = null;
	private String notes = null;
	private String category = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"AccountID", "TypeID", "ProgramID", "Notes", "Category"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "LoadID" };
	
	public static final String TABLE_NAME = "CustomerLoadInformation";
/**
 * CustomerWebSettings constructor comment.
 */
public CustomerLoadInformation() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getLoadID(), getAccountID(), getTypeID(),
		getProgramID(), getNotes(), getCategory()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getLoadID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:43:09 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAccountID() {
	return accountID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:43:09 PM)
 * @return java.lang.String
 */
public java.lang.String getCategory() {
	return category;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:43:09 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLoadID() {
	return loadID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:43:09 PM)
 * @return java.lang.String
 */
public java.lang.String getNotes() {
	return notes;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:43:09 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getProgramID() {
	return programID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:43:09 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getTypeID() {
	return typeID;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getLoadID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setAccountID( (Integer)results[0] );
		setTypeID( (Integer)results[1] );
		setProgramID( (Integer)results[2] );		
		setNotes( (String) results[3] );
		setCategory( (String) results[4] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:43:09 PM)
 * @param newAccountID java.lang.Integer
 */
public void setAccountID(java.lang.Integer newAccountID) {
	accountID = newAccountID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:43:09 PM)
 * @param newCategory java.lang.String
 */
public void setCategory(java.lang.String newCategory) {
	category = newCategory;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:43:09 PM)
 * @param newLoadID java.lang.Integer
 */
public void setLoadID(java.lang.Integer newLoadID) {
	loadID = newLoadID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:43:09 PM)
 * @param newNotes java.lang.String
 */
public void setNotes(java.lang.String newNotes) {
	notes = newNotes;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:43:09 PM)
 * @param newProgramID java.lang.Integer
 */
public void setProgramID(java.lang.Integer newProgramID) {
	programID = newProgramID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:43:09 PM)
 * @param newTypeID java.lang.Integer
 */
public void setTypeID(java.lang.Integer newTypeID) {
	typeID = newTypeID;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getAccountID(), getTypeID(),
		getProgramID(), getNotes(), getCategory()
	};

	Object[] constraintValues =  { getLoadID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
