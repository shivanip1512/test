package com.cannontech.database.data.device;

import java.util.HashMap;
import java.util.Iterator;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.device.DeviceWindow;

public abstract class TwoWayDevice extends DeviceBase {

    private DeviceWindow deviceWindow = null;
    private HashMap<String, DeviceScanRate> deviceScanRateMap = null;

    public TwoWayDevice(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();

        Iterator<DeviceScanRate> it = getDeviceScanRateMap().values().iterator();
        while (it.hasNext()) {
            it.next().add();
        }

        getDeviceWindow().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {

        if (!isPartialDelete) {
            DeviceScanRate.deleteDeviceScanRates(getDevice().getDeviceID(), getDbConnection());
            getDeviceWindow().delete();
        }

        super.delete();
    }

    @Override
    public void deletePartial() throws java.sql.SQLException {
       DeviceScanRate.deleteDeviceScanRates(getDevice().getDeviceID(), getDbConnection());
       getDeviceWindow().delete();
       super.deletePartial();
    }
    
    public HashMap<String, DeviceScanRate> getDeviceScanRateMap() {
        if (deviceScanRateMap == null) {
            deviceScanRateMap = new HashMap<String, DeviceScanRate>();
        }
        return deviceScanRateMap;
    }

    public com.cannontech.database.db.device.DeviceWindow getDeviceWindow() {
        if (deviceWindow == null) {
            deviceWindow = new DeviceWindow();
        }

        return deviceWindow;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getDeviceWindow().retrieve();

        try {
            DeviceScanRate rArray[] = DeviceScanRate.getDeviceScanRates(getDevice().getDeviceID(), getDbConnection());

            for (int i = 0; i < rArray.length; i++) {
                getDeviceScanRateMap().put(rArray[i].getScanType(), rArray[i]);
            }

            Iterator<DeviceScanRate> it = getDeviceScanRateMap().values().iterator();
            
            while (it.hasNext()) {
                DBPersistent o = it.next();
                o.setDbConnection(getDbConnection());
                o.retrieve();
                o.setDbConnection(null);
            }

        } catch (java.sql.SQLException e) {
            CTILogger.error(e.getMessage(), e);
        }
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getDeviceWindow().setDbConnection(conn);

        Iterator<DeviceScanRate> it = getDeviceScanRateMap().values().iterator();
        while (it.hasNext()) {
            it.next().setDbConnection(conn);
        }
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceWindow().setDeviceID(deviceID);

        Iterator<DeviceScanRate> it = getDeviceScanRateMap().values().iterator();
        while (it.hasNext()) {
            it.next().setDeviceID(deviceID);
        }
    }

    /**
     * Ensure this guy is never null
     */
    public void setDeviceScanRateMap(HashMap<String, DeviceScanRate> newValue) {

        if (newValue == null) {
            newValue = new HashMap<String, DeviceScanRate>();
        }
        this.deviceScanRateMap = newValue;
    }

    public void setDeviceWindow(DeviceWindow newDeviceWindow) {
        deviceWindow = newDeviceWindow;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getDeviceWindow().update();

        DeviceScanRate.deleteDeviceScanRates(getDevice().getDeviceID(), getDbConnection());

        Iterator<DeviceScanRate> it = getDeviceScanRateMap().values().iterator();
        while (it.hasNext()) {
            it.next().add();
        }
    }
}