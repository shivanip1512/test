package com.cannontech.database.db.starscustomer;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class CustomerLoadType extends com.cannontech.database.db.DBPersistent 
{
	private Integer typeID = null;
	private String description = null;
	private String category = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"Description", "Category"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "TypeID" };
	
	public static final String TABLE_NAME = "CustomerLoadType";
/**
 * CustomerWebSettings constructor comment.
 */
public CustomerLoadType() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getTypeID(), getDescription(), getCategory()
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
 * Creation date: (3/22/2002 4:43:09 PM)
 * @return java.lang.String
 */
public java.lang.String getCategory() {
	return category;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:48:03 PM)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return description;
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
	Object[] constraintValues =  { getTypeID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setDescription( (String) results[0] );
		setCategory( (String) results[1] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
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
 * Creation date: (3/22/2002 4:48:03 PM)
 * @param newDescription java.lang.String
 */
public void setDescription(java.lang.String newDescription) {
	description = newDescription;
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
		getDescription(), getCategory()
	};

	Object[] constraintValues =  { getTypeID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
