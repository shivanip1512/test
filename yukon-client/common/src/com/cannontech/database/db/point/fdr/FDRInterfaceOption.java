package com.cannontech.database.db.point.fdr;

/**
 * This type was created in VisualAge.
 */
@Deprecated
public class FDRInterfaceOption extends com.cannontech.database.db.DBPersistent 
{
	private Integer interfaceID = null;
	private String  optionLabel = null;
	private Integer ordering = null;
	private String optionType = null;
	private String optionValues = null;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"OptionLabel", "Ordering", "OptionType", "OptionValues" 
	};

	public static final String CONSTRAINT_COLUMNS[] = { "InterfaceID" };

	public static final String OPTION_TEXT = "Text";
	public static final String OPTION_COMBO = "Combo";
	public static final String OPTION_QUERY = "Query";
	
	public static final String TABLE_NAME = "FDRInterfaceOption";
/**
 * Point constructor comment.
 */
public FDRInterfaceOption() 
{
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[]= { getInterfaceID(), getOptionLabel(), getOrdering(),
				getOptionType(), getOptionValues() };

	add( this.TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, "INTERFACEID", getInterfaceID() );
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:08:18 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getInterfaceID() {
	return interfaceID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:16:08 PM)
 * @return java.lang.String
 */
public java.lang.String getOptionLabel() {
	return optionLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:16:08 PM)
 * @return java.lang.String
 */
public java.lang.String getOptionType() {
	return optionType;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:16:08 PM)
 * @return java.lang.String
 */
public java.lang.String getOptionValues() {
	return optionValues;
}
/**
 * Insert the method's description here.
 * Creation date: (6/7/2001 10:59:33 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getOrdering() {
	return ordering;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getInterfaceID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setOptionLabel( (String) results[0] );
		setOrdering( (Integer) results[1] );
		setOptionType( (String) results[2]);
		setOptionValues( (String) results[3]);
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
		
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:08:18 PM)
 * @param newInterfaceID java.lang.Integer
 */
public void setInterfaceID(java.lang.Integer newInterfaceID) {
	interfaceID = newInterfaceID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:16:08 PM)
 * @param newOptionLabel java.lang.String
 */
public void setOptionLabel(java.lang.String newOptionLabel) {
	optionLabel = newOptionLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:16:08 PM)
 * @param newOptionType java.lang.String
 */
public void setOptionType(java.lang.String newOptionType) {
	optionType = newOptionType;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:16:08 PM)
 * @param newOptionValues java.lang.String
 */
public void setOptionValues(java.lang.String newOptionValues) {
	optionValues = newOptionValues;
}
/**
 * Insert the method's description here.
 * Creation date: (6/7/2001 10:59:33 AM)
 * @param newOrdering java.lang.Integer
 */
public void setOrdering(java.lang.Integer newOrdering) {
	ordering = newOrdering;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[]= { getOptionLabel(), getOrdering(),
				getOptionType(), getOptionValues() };

	Object constraintValues[] = { getInterfaceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

/**
 * Returns all the possible option values by parsing the OptionValues String.
 * Assumes the delimiter is a comma.
 */
public String[] getAllOptionValues() {

	if( getOptionValues() == null )
		return new String[0];

	return getOptionValues().split( "," );
}

}