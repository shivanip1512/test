package com.cannontech.database.db.starscustomer;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class CustomerServiceInformation extends com.cannontech.database.db.DBPersistent 
{
	private Integer serviceID = null;
	private Integer substationID = null;
	private String feeder = null;
	private String pole = null;
	private String transformerSize = null;
	private String serviceVoltage = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"SubstationID", "Feeder", "Pole", "TransformerSize",
		"ServiceVoltage"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ServiceID" };
	
	public static final String TABLE_NAME = "CustomerServiceInformation";
/**
 * CustomerWebSettings constructor comment.
 */
public CustomerServiceInformation() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getServiceID(), getSubstationID(), getFeeder(),
		getPole(), getTransformerSize(), getServiceVoltage()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getServiceID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:08:13 PM)
 * @return java.lang.String
 */
public java.lang.String getFeeder() {
	return feeder;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:08:13 PM)
 * @return java.lang.String
 */
public java.lang.String getPole() {
	return pole;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:08:13 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getServiceID() {
	return serviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:08:13 PM)
 * @return java.lang.String
 */
public java.lang.String getServiceVoltage() {
	return serviceVoltage;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:08:13 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSubstationID() {
	return substationID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:08:13 PM)
 * @return java.lang.String
 */
public java.lang.String getTransformerSize() {
	return transformerSize;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getServiceID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setSubstationID( (Integer) results[0] );
		setFeeder( (String) results[1] );
		setPole( (String) results[2] );
		setTransformerSize( (String) results[3] );
		setServiceVoltage( (String) results[4] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:08:13 PM)
 * @param newFeeder java.lang.String
 */
public void setFeeder(java.lang.String newFeeder) {
	feeder = newFeeder;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:08:13 PM)
 * @param newPole java.lang.String
 */
public void setPole(java.lang.String newPole) {
	pole = newPole;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:08:13 PM)
 * @param newServiceID java.lang.Integer
 */
public void setServiceID(java.lang.Integer newServiceID) {
	serviceID = newServiceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:08:13 PM)
 * @param newServiceVoltage java.lang.String
 */
public void setServiceVoltage(java.lang.String newServiceVoltage) {
	serviceVoltage = newServiceVoltage;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:08:13 PM)
 * @param newSubstationID java.lang.Integer
 */
public void setSubstationID(java.lang.Integer newSubstationID) {
	substationID = newSubstationID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:08:13 PM)
 * @param newTransformerSize java.lang.String
 */
public void setTransformerSize(java.lang.String newTransformerSize) {
	transformerSize = newTransformerSize;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getSubstationID(), getFeeder(),
		getPole(), getTransformerSize(), getServiceVoltage()
	};

	Object[] constraintValues =  { getServiceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
