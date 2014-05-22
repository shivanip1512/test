package com.cannontech.database.data.device;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.Device;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceDirectCommSettings;

public class GridAdvisorBase extends TwoWayDevice {
    private static final Logger log = YukonLogManager.getLogger(GridAdvisorBase.class);
    private DeviceDirectCommSettings deviceDirectCommSettings = null;
    private DeviceAddress deviceAddress = null;
    
    public GridAdvisorBase(PaoType paoType) {
        super(paoType);
    }
    
    @Override
    public void add() throws SQLException {
        super.add();
        getDeviceDirectCommSettings().add();
        getDeviceAddress().add();
    }

    @Override
    public void addPartial() throws SQLException {
        super.addPartial();
        getDeviceDirectCommSettings().addPartial();
        getDeviceAddress().addPartial();
    }

    @Override
    public boolean allowRebroadcast() {
        return super.allowRebroadcast();
    }

    @Override
    public void delete() throws SQLException {
        getDeviceDirectCommSettings().delete();
        getDeviceAddress().delete();
        super.delete();
    }

    @Override
    public void deletePartial() throws SQLException {
        super.deletePartial();
        getDeviceDirectCommSettings().deletePartial();
        getDeviceAddress().deletePartial();
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
        getDeviceAddress().retrieve();
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getDeviceDirectCommSettings().setDbConnection(conn);
        getDeviceAddress().setDbConnection(conn);
    }

    @Override
    public void setDevice(Device newValue) {
        super.setDevice(newValue);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceDirectCommSettings().setDeviceID( deviceID);
        getDeviceAddress().setDeviceID(deviceID);
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
        getDeviceAddress().update();
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
    
    public Integer getAddress() 
    {
       return getDeviceAddress().getMasterAddress();
    }

    public void setAddress( Integer newAddress )
    {
       getDeviceAddress().setMasterAddress( newAddress );
    }    

    public DeviceAddress getDeviceAddress()
    {
       if( deviceAddress == null )
            deviceAddress = new DeviceAddress();

       return deviceAddress;
    }

    public void setDeviceAddress(DeviceAddress deviceAddr)
    {
       this.deviceAddress = deviceAddr;
    }


}
