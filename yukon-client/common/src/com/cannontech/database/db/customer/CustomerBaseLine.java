package com.cannontech.database.db.customer;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class CustomerBaseLine extends com.cannontech.database.db.DBPersistent 
{
	private Integer customerID = null;
	private Integer daysUsed = null;
	private Integer percentWindow = null;
	private Integer calcDays = null;
	private String excludedWeekDays = null;
	private Integer holidaysUsed = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"DaysUsed", "PercentWindow", "CalcDays", 
		"ExcludedWeekDays" , "HolidaysUsed"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "CustomerID" };
	
	public static final String TABLE_NAME = "CustomerBaseLine";
/**
 * CustomerBaseLine constructor comment.
 */
public CustomerBaseLine() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getCustomerID(), getPercentWindow(),
		getDaysUsed(),
		getCalcDays(), getExcludedWeekDays(),
		getHolidaysUsed()
	};

	//if any of the values are null, just delete this obj from the DB
	if( !isValidValues(addValues) )
		return;
	
	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getCustomerID() );
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 1:02:50 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCalcDays() {
	return calcDays;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 1:02:50 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCustomerID() {
	return customerID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 4:12:59 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDaysUsed() {
	return daysUsed;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 1:02:50 PM)
 * @return java.lang.String
 */
public java.lang.String getExcludedWeekDays() {
	return excludedWeekDays;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 1:02:50 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getHolidaysUsed() {
	return holidaysUsed;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 1:02:50 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPercentWindow() {
	return percentWindow;
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 10:30:24 AM)
 * @return boolean
 */
private boolean isValidValues( Object[] values ) 
{
	if( values == null )
		return false;

	for( int i = 0; i < values.length; i++ )
		if( values[i] == null )
			return false;


	return true;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getCustomerID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setDaysUsed( (Integer) results[0] );
		setPercentWindow( (Integer) results[1] );
		setCalcDays( (Integer) results[2] );
		setExcludedWeekDays( (String) results[3] );
		setHolidaysUsed( (Integer)results[4] );
	}
	//do not throw the ERROR here if we dont get back any columns!!!!

}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 1:02:50 PM)
 * @param newCalcDays java.lang.Integer
 */
public void setCalcDays(java.lang.Integer newCalcDays) {
	calcDays = newCalcDays;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 1:02:50 PM)
 * @param newCustomerID java.lang.Integer
 */
public void setCustomerID(java.lang.Integer newCustomerID) {
	customerID = newCustomerID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 4:12:59 PM)
 * @param newDaysUsed java.lang.Integer
 */
public void setDaysUsed(java.lang.Integer newDaysUsed) {
	daysUsed = newDaysUsed;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 1:02:50 PM)
 * @param newExcludedWeekDays java.lang.String
 */
public void setExcludedWeekDays(java.lang.String newExcludedWeekDays) {
	excludedWeekDays = newExcludedWeekDays;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 1:02:50 PM)
 * @param newHolidaysUsed java.lang.Integer
 */
public void setHolidaysUsed(java.lang.Integer newHolidaysUsed) {
	holidaysUsed = newHolidaysUsed;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 1:02:50 PM)
 * @param newPercentWindow java.lang.Integer
 */
public void setPercentWindow(java.lang.Integer newPercentWindow) {
	percentWindow = newPercentWindow;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = { getDaysUsed(),
			getPercentWindow(), getCalcDays(), 
			getExcludedWeekDays(), getHolidaysUsed()	};

	if( !isValidValues(setValues) )
	{
		//if any of the values are null, just delete this obj from the DB
		delete();
		return;
	}

	
	Object[] constraintValues =  { getCustomerID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length > 0 )
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	else
		add();

}
}
