package com.cannontech.database.db.device;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.database.JdbcTemplateHelper;

/**
 * This type was created in VisualAge.
 */
 
public class DeviceScanRate extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private String scanType = TYPE_GENERAL;
	private Integer intervalRate = new Integer(300);
	private Integer scanGroup = new Integer(0);
	private Integer alternateRate = new Integer(300);

	public static final String TYPE_GENERAL = "General";
	public static final String TYPE_STATUS = "Status";
	public static final String TYPE_EXCEPTION = "Exception";
	public static final String TYPE_ACCUMULATOR = "Accumulator";
	public static final String TYPE_INTEGRITY = "Integrity";
	
	public static final String TABLE_NAME = "DeviceScanRate";

/**
 * DeviceScanRate constructor comment.
 */
public DeviceScanRate() {
	super();
}

/**
 * DeviceScanRate constructor comment.
 */
public DeviceScanRate( Integer devID, String scanTypeStr ) {
	super();
	setDeviceID( devID );
	setScanType( scanTypeStr );
}

/**
 * DeviceScantRateTable constructor comment.
 */
public DeviceScanRate(Integer deviceID, String scanType, Integer intervalRate, Integer scanGroup, Integer altRate ) 
{
	super();
	initialize( deviceID , scanType, intervalRate, scanGroup, altRate );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), 
		getScanType(), getIntervalRate(), getScanGroup(), getAlternateRate() };

	add( this.TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	String constraintColumns[] = { "DeviceID", "ScanType", "ScanGroup", "AlternateRate" };
	Object constraintValues[] = { getDeviceID(), getScanType(), getScanGroup(), getAlternateRate() };
	
	delete( this.TABLE_NAME, constraintColumns, constraintValues  );
}

/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteDeviceScanRates(Integer deviceID, java.sql.Connection conn )
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement(
               "DELETE FROM " + TABLE_NAME+ " WHERE DeviceID=" + deviceID,
					conn );
	try
	{
		stmt.execute();
	}
	catch(Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 11:59:46 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAlternateRate() {
	return alternateRate;
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
 * @return com.cannontech.database.db.device.DeviceScanRate[]
 * @param deviceID java.lang.Integer
 */
public static DeviceScanRate[] getDeviceScanRates(Integer deviceID, java.sql.Connection conn ) throws java.sql.SQLException
{
	com.cannontech.database.SqlStatement stmt = 
		new com.cannontech.database.SqlStatement(
            "SELECT IntervalRate, ScanType, ScanGroup, AlternateRate FROM " + TABLE_NAME + " WHERE DeviceID=" + deviceID,
				conn );
													
	try
	{
		stmt.execute();
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	
	DeviceScanRate retVal[] = new DeviceScanRate[stmt.getRowCount()];

	for( int i = 0; i < retVal.length; i++ )
	{
		Integer intervalRate = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue() );
		String scanType = (String) stmt.getRow(i)[1];
		Integer scanGroup = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[2]).intValue() );
		Integer altRate = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[3]).intValue() );
		
		retVal[i] = new DeviceScanRate( deviceID, scanType, intervalRate, scanGroup, altRate );
	}
	
	return retVal;

}

public static List getAllDeviceIDs () {
    String sqlStmt = "SELECT DEVICEID FROM " + TABLE_NAME;   
    JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();   
    List retList = yukonTemplate.query(sqlStmt, new RowMapper () {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Integer deviceID = null;
				deviceID = new Integer ( rs.getInt(1) );
				return deviceID;
			}
    });
    return retList;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public Integer getIntervalRate() {
	return intervalRate;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getScanGroup() {
	return scanGroup;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getScanType() {
	return scanType;
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 * @param scanType java.lang.String
 * @param intervalRate java.lang.String
 */
private void initialize( Integer deviceID, String scanType, Integer intervalRate, Integer scanGroup, Integer altRate ) 
{
	setDeviceID( deviceID );
	setScanType( scanType );
	setIntervalRate( intervalRate);
	setScanGroup( scanGroup );
	setAlternateRate( altRate );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	String selectColumns[] = {  "ScanType", "IntervalRate", "ScanGroup", "AlternateRate" };

	String constraintColumns[] = { "DeviceID", "ScanType" };
	Object constraintValues[] = { getDeviceID(), getScanType()};

	Object results[] = retrieve( selectColumns, this.TABLE_NAME, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setScanType( (String) results[0] ) ;
		setIntervalRate( (Integer) results[1]  );
		setScanGroup( (Integer) results[2]  );
		setAlternateRate( (Integer) results[3]  );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 11:59:46 AM)
 * @param newAlternateRate java.lang.Integer
 */
public void setAlternateRate(java.lang.Integer newAlternateRate) {
	alternateRate = newAlternateRate;
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
 * @param newValue java.lang.String
 */
public void setIntervalRate(Integer newValue) {
	this.intervalRate = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setScanGroup(Integer newValue) {
	this.scanGroup = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setScanType(String newValue) {
	this.scanType = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "ScanType", "IntervalRate", "ScanGroup", "AlternateRate" };
	Object setValues[] = { getScanType(), getIntervalRate(), getScanGroup(), getAlternateRate() };

	String constraintColumns[] = { "DeviceID", "ScanType" };
	Object constraintValues[] = { getDeviceID(), getScanType() };

	update( this.TABLE_NAME, setColumns, setValues, constraintColumns, constraintValues );
	
}
}
