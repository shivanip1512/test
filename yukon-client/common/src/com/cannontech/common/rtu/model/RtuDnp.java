package com.cannontech.common.rtu.model;

import java.util.List;
import java.util.Map;

import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.HeartbeatConfiguration;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.device.DeviceWindow;

public class RtuDnp implements YukonPao {

    private Integer id;
    private String name;
    private PaoType paoType;
    private boolean disableFlag;
    private Map<String, DeviceScanRate> deviceScanRateMap;
    private DeviceAddress deviceAddress;
    private DeviceWindow deviceWindow;
    private List<DisplayableDevice> childDevices;
    private DNPConfiguration dnpConfig;
    private HeartbeatConfiguration heartbeatConfig;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PaoType getPaoType() {
        return paoType;
    }

    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return PaoIdentifier.of(id, paoType);
    }

    public boolean isDisableFlag() {
        return disableFlag;
    }

    public void setDisableFlag(boolean disableFlag) {
        this.disableFlag = disableFlag;
    }

    public Map<String, DeviceScanRate> getDeviceScanRateMap() {
        return deviceScanRateMap;
    }

    public void setDeviceScanRateMap(Map<String, DeviceScanRate> deviceScanRateMap) {
        this.deviceScanRateMap = deviceScanRateMap;
    }

    public DeviceAddress getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(DeviceAddress deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public boolean isEditingIntegrity() {
        return this.deviceScanRateMap.containsKey(DeviceScanRate.TYPE_INTEGRITY);
    }
    
    public void setEditingIntegrity(boolean editingIntegrity) {
        if(!editingIntegrity){
            this.deviceScanRateMap.remove(DeviceScanRate.TYPE_INTEGRITY);
        }
    }

    public boolean isEditingException() {
        return this.deviceScanRateMap.containsKey(DeviceScanRate.TYPE_EXCEPTION);
    }
    
    public void setEditingException(boolean editingException) {
        if(!editingException){
            this.deviceScanRateMap.remove(DeviceScanRate.TYPE_EXCEPTION);
        }
    }

    public DeviceWindow getDeviceWindow() {
        return deviceWindow;
    }

    public void setDeviceWindow(DeviceWindow deviceWindow) {
        this.deviceWindow = deviceWindow;
    }

    public List<DisplayableDevice> getChildDevices() {
        return childDevices;
    }

    public void setChildDevices(List<DisplayableDevice> childDevices) {
        this.childDevices = childDevices;
    }

    public DNPConfiguration getDnpConfig() {
        return dnpConfig;
    }

    public void setDnpConfig(DNPConfiguration dnpConfig) {
        this.dnpConfig = dnpConfig;
    }

    public HeartbeatConfiguration getHeartbeatConfig() {
        return heartbeatConfig;
    }

    public void setHeartbeatConfig(HeartbeatConfiguration heartbeatConfig) {
        this.heartbeatConfig = heartbeatConfig;
    }
}
