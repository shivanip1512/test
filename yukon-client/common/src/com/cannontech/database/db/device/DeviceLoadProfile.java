package com.cannontech.database.db.device;

/**
 * This type was created by Cannon Technologies Inc.
 */
public class DeviceLoadProfile extends com.cannontech.database.db.DBPersistent
{
	private Integer deviceID = null;
	private Integer lastIntervalDemandRate = new Integer(300);
	private Integer loadProfileDemandRate = new Integer(300);
	private String loadProfileCollection = "NNNN";
	private Integer voltageDmdInterval = new Integer(60);
	private Integer voltageDmdRate = new Integer(300);


	public static final String SETTER_COLUMNS[] = 
	{ 
		"LastIntervalDemandRate", "LoadProfileDemandRate",
		"LoadProfileCollection", "VoltageDmdInterval", "VoltageDmdRate"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
   
	public static final String TABLE_NAME = "DeviceLoadProfile";
	

/**
 * DeviceLoadProfile constructor comment.
 */
public DeviceLoadProfile() {
	super();
}

/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] =
	{
		getDeviceID(), getLastIntervalDemandRate(), getLoadProfileDemandRate(),
		getLoadProfileCollection(), getVoltageDmdInterval(), getVoltageDmdRate()
	};

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getLastIntervalDemandRate() {
	return lastIntervalDemandRate;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public String getLoadProfileCollection() {
	return loadProfileCollection;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getLoadProfileDemandRate() {
	return loadProfileDemandRate;
}

/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException
{
	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setLastIntervalDemandRate( (Integer) results[0] );
		setLoadProfileDemandRate( (Integer) results[1] );
		setLoadProfileCollection( (String) results[2] );
		setVoltageDmdInterval( (Integer) results[3] );
		setVoltageDmdRate( (Integer) results[4] );
	}
}

/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setLastIntervalDemandRate(Integer newValue) {
	this.lastIntervalDemandRate = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setLoadProfileCollection(String newValue) {
	this.loadProfileCollection = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setLoadProfileDemandRate(Integer newValue) {
	this.loadProfileDemandRate = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException
{
	Object setValues[] =
	{ 
		getLastIntervalDemandRate(), getLoadProfileDemandRate(),
		getLoadProfileCollection(), getVoltageDmdInterval(), getVoltageDmdRate()
	};

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
	/**
	 * @return
	 */
	public Integer getVoltageDmdInterval()
	{
		return voltageDmdInterval;
	}

	/**
	 * @return
	 */
	public Integer getVoltageDmdRate()
	{
		return voltageDmdRate;
	}

	/**
	 * @param integer
	 */
	public void setVoltageDmdInterval(Integer integer)
	{
		voltageDmdInterval = integer;
	}

	/**
	 * @param integer
	 */
	public void setVoltageDmdRate(Integer integer)
	{
		voltageDmdRate = integer;
	}

}
