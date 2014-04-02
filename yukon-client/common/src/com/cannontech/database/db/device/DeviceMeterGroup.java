package com.cannontech.database.db.device;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.db.pao.YukonPAObject;

/**
 * This type was created in VisualAge.
 */
public class DeviceMeterGroup extends com.cannontech.database.db.DBPersistent 
{
    public static final String DISCONNECTED_GROUP_PREFIX = "@_DISC_";
    public static final String USAGE_MONITORING_GROUP_PREFIX = "@_UM_";
    public static final String INVENTORY_GROUP_PREFIX = "@_INV_";

    public static final String REMOVED_METER_NUMBER_SUFFIX = "_REM";
    
	private Integer deviceID = null;
	private String meterNumber = com.cannontech.common.util.CtiUtilities.STRING_DEFAULT;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"MeterNumber",
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
	public static final String TABLE_NAME = "DeviceMeterGroup";

	/**
	 * DeviceMeterGroup constructor comment.
	 */
	public DeviceMeterGroup() 
	{
		super();
	}

	/**
	 * add method comment.
	 */
	@Override
    public void add() throws java.sql.SQLException 
	{
		Object addValues[] = { getDeviceID(), getMeterNumber() };
	
		add( TABLE_NAME, addValues );
	}
	
	/**
	 * delete method comment.
	 */
	@Override
    public void delete() throws java.sql.SQLException {
	
		Object values [] = {getDeviceID()};
		
		delete( DeviceMeterGroup.TABLE_NAME, CONSTRAINT_COLUMNS, values);
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
	public String getMeterNumber() {
		return meterNumber;
	}

    /**
	 * retrieve method comment.
	 */
	@Override
    public void retrieve() throws java.sql.SQLException 
	{
	
		Object constraintValues[] = { getDeviceID() };
	
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setMeterNumber( (String) results[0] );
		}
	
	}

	public void setDeviceID(Integer newValue) {
		this.deviceID = newValue;
	}

    public void setMeterNumber(String newValue) {
		this.meterNumber = newValue;
	}

    @Override
    public void update() throws java.sql.SQLException 
	{
		Object setValues[] = { getMeterNumber() };
	
		Object constraintValues[] = { getDeviceID() };
	
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
    
    /**
     * Method to get a list of device names for devices that have a given meternumber
     * @param meterNumber - Meternumber to search for
     * @param excludedDeviceId - Id of device to exclude when searching or null if no exclusions
     * @return A list of device names or an empty list if none found
     */
    public static List<String> checkMeterNumber(String meterNumber, Integer excludedDeviceId){
        
        List<String> deviceNameList = new ArrayList<String>();

        if(StringUtils.isNotBlank(meterNumber) && !StringUtils.equalsIgnoreCase(meterNumber, CtiUtilities.STRING_DEFAULT)) {

            String exclude = "";
            Object[] parameters = new Object[] { meterNumber };
            if (excludedDeviceId != null) {
                exclude = " AND ypo.paobjectid <> ?";
                parameters = new Object[] { meterNumber, excludedDeviceId };
            }
    
            JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
            String sql = "SELECT ypo.paoname FROM " + TABLE_NAME + " dmg, " + YukonPAObject.TABLE_NAME
                    + " ypo WHERE ypo.paobjectid=dmg.deviceid AND dmg.meternumber = ?" + exclude;
    
            SqlRowSet rowSet = jdbcOps.query(sql,
                                                         parameters,
                                                         new SqlRowSetResultSetExtractor());
            while (rowSet.next()) {
                deviceNameList.add(rowSet.getString(1));
            }
        }        
        return deviceNameList;
    }

}
