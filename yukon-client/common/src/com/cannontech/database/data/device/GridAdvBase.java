package com.cannontech.database.data.device;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.device.Device;
import com.cannontech.database.db.device.DeviceDirectCommSettings;

public class GridAdvBase extends DeviceBase {
    private static final Logger log = YukonLogManager.getLogger(GridAdvBase.class);
    private DeviceDirectCommSettings deviceDirectCommSettings = null;
    
    @Override
    public void add() throws SQLException {
        super.add();
        getDeviceDirectCommSettings().add();
    }

    @Override
    public void addPartial() throws SQLException {
        super.addPartial();
        getDeviceDirectCommSettings().add();
    }

    @Override
    public boolean allowRebroadcast() {
        return super.allowRebroadcast();
    }

    @Override
    public void delete() throws SQLException {
        getDeviceDirectCommSettings().delete();
        super.delete();
    }

    @Override
    public void deletePartial() throws SQLException {
        super.deletePartial();
        getDeviceDirectCommSettings().deletePartial();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public Device getDevice() {
        return super.getDevice();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getDeviceDirectCommSettings().retrieve();
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getDeviceDirectCommSettings().setDbConnection(conn);
    }

    @Override
    public void setDevice(Device newValue) {
        super.setDevice(newValue);
    }

    @Override
    public void setDeviceClass(String devClass) {
        super.setDeviceClass(devClass);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceDirectCommSettings().setDeviceID( deviceID);
    }
    
    public void setDefaultPort()
    {
        try{
            getDeviceDirectCommSettings().setDefaultPortID();
        }
        catch( java.sql.SQLException j )
        {            
            log.info("SQL Exception in setting default port: " + j);
            
        }
        catch( Exception e )
        {
            log.debug("Exception in Setting Default port:" + e);
        }        
    }
    @Override
    public void setDeviceType(String devType) {
        super.setDeviceType(devType);
    }

    @Override
    public void setDisableFlag(Character ch) {
        super.setDisableFlag(ch);
    }

    @Override
    public void setPAOName(String name) {
        super.setPAOName(name);
    }

    @Override
    public void update() throws SQLException {
        getDeviceDirectCommSettings().update();
        super.update();
    }

    public DeviceDirectCommSettings getDeviceDirectCommSettings() {
        if ( deviceDirectCommSettings == null )
        {
            deviceDirectCommSettings = new DeviceDirectCommSettings();
        }
        return deviceDirectCommSettings;
    }

    public void setDeviceDirectCommSettings(
            DeviceDirectCommSettings deviceDirectCommSettings) {
        this.deviceDirectCommSettings = deviceDirectCommSettings;
    }
    
    

}
