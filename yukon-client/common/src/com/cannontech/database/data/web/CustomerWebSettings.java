package com.cannontech.database.data.web;

/**
 * Creation date: (3/27/2001 1:20:37 PM)
 * @author: Aaron Lauinger 
 */
public class CustomerWebSettings extends com.cannontech.database.db.DBPersistent {
	public static final String tableName = "CustomerWebSettings";

	private long id;
	private String databaseAlias = "";
	private String logo = "";
	private String homeURL = "";
/**
 * CustomerWebSettings constructor comment.
 */
public CustomerWebSettings() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
		{ 
			new Long(getId()), 
			getDatabaseAlias(), 
			getLogo(), 
			getHomeURL() 
		};
		
	super.add( tableName, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	super.delete( tableName, "CustomerID", new Long(getId()) );
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 1:22:57 PM)
 * @return java.lang.String
 */
public java.lang.String getDatabaseAlias() {
	return databaseAlias;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 1:22:57 PM)
 * @return java.lang.String
 */
public java.lang.String getHomeURL() {
	return homeURL;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 1:22:57 PM)
 * @return long
 */
public long getId() {
	return id;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 1:22:57 PM)
 * @return java.lang.String
 */
public java.lang.String getLogo() {
	return logo;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	String[] selectColumns = { "DatabaseAlias", "Logo", "HomeURL" };

	String[] constraintColumns = { "CustomerID" };
	Object[] constraintValues =  { new Long(getId()) };

	Object[] results = super.retrieve(selectColumns, tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setDatabaseAlias( (String) results[0] );
		setLogo( (String) results[1] );
		setHomeURL( (String) results[2] );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 1:22:57 PM)
 * @param newDatabaseAlias java.lang.String
 */
public void setDatabaseAlias(java.lang.String newDatabaseAlias) {
	databaseAlias = newDatabaseAlias;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 1:22:57 PM)
 * @param newHomeURL java.lang.String
 */
public void setHomeURL(java.lang.String newHomeURL) {
	homeURL = newHomeURL;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 1:22:57 PM)
 * @param newId long
 */
public void setId(long newId) {
	id = newId;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 1:22:57 PM)
 * @param newLogo java.lang.String
 */
public void setLogo(java.lang.String newLogo) {
	logo = newLogo;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	String[] setColumns	= { "DatabaseAlias", "Logo", "HomeURL" };
	Object[] setValues = { getDatabaseAlias(), getLogo(), getHomeURL() };

	String[] constraintColumns = { "CustomerID" };
	Object[] constraintValues =  { new Long(getId()) };

	update( tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}
