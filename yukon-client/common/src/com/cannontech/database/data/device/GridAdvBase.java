package com.cannontech.database.data.device;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.device.*;

public class GridAdvBase extends DeviceBase {
    private static final Logger log = YukonLogManager.getLogger(GridAdvBase.class);
    private DeviceDirectCommSettings deviceDirectCommSettings = null;
    private DeviceAddress deviceAddress = null;
    private DeviceIDLCRemote deviceIDLCRemote = null;
    
    @Override
    public void add() throws SQLException {
        super.add();
        getDeviceDirectCommSettings().add();
        getDeviceAddress().add();
        getDeviceIDLCRemote().add();
    }

    @Override
    public void addPartial() throws SQLException {
        super.addPartial();
        getDeviceDirectCommSettings().addPartial();
        getDeviceAddress().addPartial();
        getDeviceIDLCRemote().addPartial();
    }

    @Override
    public boolean allowRebroadcast() {
        return super.allowRebroadcast();
    }

    @Override
    public void delete() throws SQLException {
        getDeviceDirectCommSettings().delete();
        getDeviceAddress().delete();
        getDeviceIDLCRemote().delete();
        super.delete();
    }

    @Override
    public void deletePartial() throws SQLException {
        super.deletePartial();
        getDeviceDirectCommSettings().deletePartial();
        getDeviceAddress().deletePartial();
        getDeviceIDLCRemote().deletePartial();
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
        getDeviceIDLCRemote().retrieve();
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getDeviceDirectCommSettings().setDbConnection(conn);
        getDeviceAddress().setDbConnection(conn);
        getDeviceIDLCRemote().setDbConnection(conn);
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
        getDeviceAddress().setDeviceID(deviceID);
        getDeviceIDLCRemote().setDeviceID(deviceID);
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
        getDeviceAddress().update();
        getDeviceIDLCRemote().update();
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
    
    public DeviceIDLCRemote getDeviceIDLCRemote() {
        if( deviceIDLCRemote == null )
            deviceIDLCRemote = new DeviceIDLCRemote();
            
        return deviceIDLCRemote;
    }
    public void setDeviceIDLCRemote(DeviceIDLCRemote newValue) {
        this.deviceIDLCRemote = newValue;
    }

}
