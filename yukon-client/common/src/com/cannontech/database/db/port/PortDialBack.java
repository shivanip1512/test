package com.cannontech.database.db.port;

/**
 * This type was created in VisualAge.
 */
 import com.cannontech.database.db.DBPersistent;
 
public class PortDialBack extends DBPersistent 
{
	private Integer portID = null;
	private String modemType = null;
	private String initializationString = "ATH0";
		
	
   public static final String SETTER_COLUMNS[] = 
   { 
      "ModemType", "InitializationString"
   };

   public static final String CONSTRAINT_COLUMNS[] = { "PortID" };

   private static final String TABLE_NAME = "PortDialBack";
   
   
   	
/**
 * PortDialupModem constructor comment.
 */
public PortDialBack() 
{
	super();
}

/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	
	Object values[] = { getPortID(), getModemType(), getInitializationString() };
	add( TABLE_NAME, values );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	
	delete( TABLE_NAME, "PortID", getPortID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInitializationString() {
	return initializationString;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getModemType() {
	return modemType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPortID() {
	return portID;
}

/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	
	Object constraintColumnValues[] = { getPortID() };	
	Object result[] =  
		retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintColumnValues );
	
	if( result.length == SETTER_COLUMNS.length )
	{
		setModemType( (String) result[0] );
		setInitializationString( (String) result[1] );
	}

}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setInitializationString(String newValue) {
	this.initializationString = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setModemType(String newValue) {
	this.modemType = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPortID(Integer newValue) {
	this.portID = newValue;
}

/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setColumnValues[]= { getModemType(), getInitializationString() };
	Object constraintColumnValues[] = { getPortID() };
	
	update( TABLE_NAME, SETTER_COLUMNS, setColumnValues, CONSTRAINT_COLUMNS, constraintColumnValues);
}

}
