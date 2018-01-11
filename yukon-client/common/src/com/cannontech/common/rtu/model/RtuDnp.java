package com.cannontech.common.rtu.model;

import java.util.List;
import java.util.Map;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceScanRate;


public class RtuDnp implements YukonPao {

    private Integer id;
    private String name;
    private PaoType paoType;
    private boolean disableFlag;
    private Map<PointType, List<String>> points;
    private Map<String, DeviceScanRate> deviceScanRateMap;
    private DeviceAddress deviceAddress;
    private Integer dnpConfigId;
    
    //TODO:  Need to add Scan Window options, Child devices

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

    public Map<PointType, List<String>> getPoints() {
        return points;
    }

    public void setPoints(Map<PointType, List<String>> points) {
        this.points = points;
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

    public Integer getDnpConfigId() {
        return dnpConfigId;
    }

    public void setDnpConfigId(Integer dnpConfiguration) {
        this.dnpConfigId = dnpConfiguration;
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
}
