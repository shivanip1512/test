package com.cannontech.database.db.customer;

/**
 * Creation date: (3/27/2001 1:20:37 PM)
 * @author: Aaron Lauinger 
 */
public class CustomerWebSettings extends com.cannontech.database.db.DBPersistent 
{
	private long id;
	private String databaseAlias = null;
	private String logo = null;
	private String homeURL = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"DatabaseAlias", "Logo", "HomeURL" 
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "CustomerID" };
	
	public static final String TABLE_NAME = "CustomerWebSettings";
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
		
	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], new Long(getId()) );
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
	Object[] constraintValues =  { new Long(getId()) };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
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
	Object[] setValues = { getDatabaseAlias(), getLogo(), getHomeURL() };
	Object[] constraintValues =  { new Long(getId()) };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length > 0 )
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	else
		add();

}
}
