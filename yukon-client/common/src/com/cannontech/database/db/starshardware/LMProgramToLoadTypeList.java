package com.cannontech.database.db.starshardware;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class LMProgramToLoadTypeList extends com.cannontech.database.db.DBPersistent 
{
	private Integer typeID = null;
	private Integer programID = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"ProgramID"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "TypeID" };
	
	public static final String TABLE_NAME = "LMProgramToLoadTypeList";
/**
 * CustomerWebSettings constructor comment.
 */
public LMProgramToLoadTypeList() {
	super();
}
/**
 * CustomerWebSettings constructor comment.
 */
public LMProgramToLoadTypeList( Integer typeID_, Integer programID_ ) 
{
	super();

	setTypeID( typeID_ );
	setProgramID( programID_ );
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getTypeID(), getProgramID()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getTypeID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 12:11:18 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getProgramID() {
	return programID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 12:10:14 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getTypeID() {
	return typeID;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getTypeID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setProgramID( (Integer) results[0] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 12:11:18 PM)
 * @param newProgramID java.lang.Integer
 */
public void setProgramID(java.lang.Integer newProgramID) {
	programID = newProgramID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 12:10:14 PM)
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
		getProgramID()
	};

	Object[] constraintValues =  { getTypeID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
