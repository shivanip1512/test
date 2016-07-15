package com.cannontech.database.db.device;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.database.db.DBPersistent;

public class RfnAddress extends DBPersistent  {
        
    private Integer deviceID = null;
    private String serialNumber;
    private String manufacturer;
    private String model;

    public static final String SETTER_COLUMNS[] = {"SerialNumber", "Manufacturer", "Model"};
    public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
    public static final String TABLE_NAME = "RFNAddress";
    
    public RfnAddress() {
        super();
    }

    public void add() throws SQLException {   
        Object addValues[] = { getDeviceID(), getSerialNumber(), getManufacturer(), getModel() };
        add( TABLE_NAME, addValues );
    }

    public void delete() throws SQLException {
        delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID() );
    }

    public Integer getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(Integer newValue) {
        this.deviceID = newValue;
    }

    public void retrieve() throws SQLException {
        Object constraintValues[] = { getDeviceID() };
        Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if( results.length == SETTER_COLUMNS.length ) {
            setSerialNumber((String) results[0]);
            setManufacturer((String) results[1]);
            setModel((String) results[2]);
        }

    }
    
    public void update() throws SQLException {
        Object setValues[] = { getSerialNumber(), getManufacturer(), getModel() };
        Object constraintValues[] = { getDeviceID() };
        
        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        setManufacturer(rfnIdentifier.getSensorManufacturer());
        setModel(rfnIdentifier.getSensorModel());
        setSerialNumber(rfnIdentifier.getSensorSerialNumber());
    }
    
    public boolean isBlank() {
        return StringUtils.isBlank(manufacturer) 
            && StringUtils.isBlank(model)
            && StringUtils.isBlank(serialNumber);
    }
}