package com.cannontech.database.db.device;

import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.device.Repeater900;
import com.cannontech.database.db.pao.YukonPAObject;

/**
 * This type was created in VisualAge.
 */
public class DeviceCarrierSettings extends com.cannontech.database.db.DBPersistent {
	private Integer deviceID = null;
	private Integer address = null;
	
	public static final String TABLE_NAME = "DeviceCarrierSettings";

	public static final String SETTER_COLUMNS[] = { "Address" };
	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
		
/**
 * DeviceCarrierSettings constructor comment.
 */
public DeviceCarrierSettings() {
	super();
	initialize( null, null );
}
/**
 * DeviceCarrierSettings constructor comment.
 */
public DeviceCarrierSettings(Integer deviceID) {
	super();
	initialize( deviceID, null );
}
/**
 * DeviceCarrierSettings constructor comment.
 */
public DeviceCarrierSettings(Integer deviceID, Integer address ) {
	super();
	initialize( deviceID, address);
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] = { getDeviceID(), getAddress() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	Object values [] = {getDeviceID()};
	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getAddress() {
	return address;
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
 * @param deviceID java.lang.Integer
 * @param address java.lang.Integer
 * @param demandInterval Intger
 */
public void initialize( Integer deviceID, Integer address ) {

	setDeviceID( deviceID );
	setAddress( address );
}

    /**
     * Method to get a list of names of paobjects that have the given address
     * @param address - Address in question
     * @param excludedPAOId - Id of pao to ignore (if any)
     * @return Array of pao names
     */
    @SuppressWarnings("unchecked")
    public static String[] isAddressUnique(int address, Integer excludedPAOId) {

        JdbcOperations ops = JdbcTemplateHelper.getYukonTemplate();
        
        // Special case for repeater 900 and Repeater 921 - has an address offset
        int address900 = address + Repeater900.ADDRESS_OFFSET;

        String sql = "select y.paoname from " + YukonPAObject.TABLE_NAME + " y, " + TABLE_NAME
                + " d where y.paobjectid=d.deviceid and (d.address=? or d.address=?)"
                + (excludedPAOId != null ? " and y.paobjectid <> " + excludedPAOId : "");

        List<String> devices = ops.queryForList(sql, new Object[] { address, address900 }, String.class);

        return devices.toArray(new String[] {});

    }
    
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setAddress( (Integer) results[0] );
	}
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setAddress(Integer newValue) {
	this.address = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	Object setValues[] = { getAddress() };
	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );	
}
}
