package com.cannontech.database.db.version;

/**
 * This type was created in VisualAge.
 */
public class CTIDatabase extends com.cannontech.database.db.DBPersistent 
{
	private String version = null;
	private String ctiEmployeeName = null;
	private java.util.Date dateApplied = null;
	private String notes = null;
	private Integer build = null;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"CTIEmployeeName", "DateApplied", "Notes", "Build"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "version" };

	public static final String TABLE_NAME = "CTIDatabase";
/**
 * State constructor comment.
 */
public CTIDatabase() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object setValues[] = { getVersion(), getCtiEmployeeName(), 
		getDateApplied(), getNotes(), getBuild() };

	add( TABLE_NAME, setValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	Object constraintValues[] = { getVersion() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 9:26:29 AM)
 * @return java.lang.String
 */
public java.lang.String getCtiEmployeeName() {
	return ctiEmployeeName;
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 9:26:29 AM)
 * @return java.util.Date
 */
public java.util.Date getDateApplied() {
	return dateApplied;
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 9:26:29 AM)
 * @return java.lang.String
 */
public java.lang.String getNotes() {
	return notes;
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 9:26:29 AM)
 * @return java.lang.String
 */
public java.lang.String getVersion() {
	return version;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getVersion() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setCtiEmployeeName( (String) results[0] );
		setDateApplied( (java.util.Date) results[1] );
		setNotes( (String) results[2] );
	}
	else
		throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 9:26:29 AM)
 * @param newCtiEmployeeName java.lang.String
 */
public void setCtiEmployeeName(java.lang.String newCtiEmployeeName) {
	ctiEmployeeName = newCtiEmployeeName;
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 9:26:29 AM)
 * @param newDateApplied java.util.Date
 */
public void setDateApplied(java.util.Date newDateApplied) {
	dateApplied = newDateApplied;
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 9:26:29 AM)
 * @param newNotes java.lang.String
 */
public void setNotes(java.lang.String newNotes) {
	notes = newNotes;
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 9:26:29 AM)
 * @param newVersion java.lang.String
 */
public void setVersion(java.lang.String newVersion) {
	version = newVersion;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getCtiEmployeeName(), 
		getDateApplied(), getNotes(), getBuild() };
	
	Object constraintValues[] = { getVersion() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
	/**
	 * @return
	 */
	public Integer getBuild()
	{
		return build;
	}

	/**
	 * @param integer
	 */
	public void setBuild(Integer integer)
	{
		build = integer;
	}

}
