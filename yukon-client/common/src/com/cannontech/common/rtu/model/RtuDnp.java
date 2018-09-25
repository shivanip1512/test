package com.cannontech.common.rtu.model;

import java.util.List;
import java.util.Map;

import org.joda.time.LocalTime;

import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.device.DeviceWindow;
import com.google.common.collect.Maps;

public class RtuDnp implements YukonPao {

    private Integer id;
    private String name;
    private PaoType paoType = PaoType.RTU_DNP;
    private boolean disableFlag;
    private Map<String, DeviceScanRate> deviceScanRateMap = Maps.newHashMap();
    private DeviceAddress deviceAddress;
    private DeviceWindow deviceWindow = new DeviceWindow();
    private DeviceDirectCommSettings deviceDirectCommSettings;
    private List<DisplayableDevice> childDevices;
    private Integer dnpConfigId;
    private String ipAddress = CtiUtilities.STRING_NONE;
    private String port = CtiUtilities.STRING_NONE;
    private boolean isPointsAvailable;
    private boolean copyPoints;
    private boolean copyFlag;
  
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

    public boolean isScanWindow() {
        return this.deviceWindow.getWinClose() != 0 || this.deviceWindow.getWinOpen() != 0;
    }
    
    public void setScanWindow(boolean scanWindow) {
        if(!scanWindow){
            this.deviceWindow.setWinClose(0);
            this.deviceWindow.setWinOpen(0);
        }
    }
    
    public LocalTime getWinOpenTime() {
        return LocalTime.fromMillisOfDay(this.deviceWindow.getWinOpen() * 1000);
    }
    
    public LocalTime getWinCloseTime() {
        return LocalTime.fromMillisOfDay(this.deviceWindow.getWinClose() * 1000);
    }

    public DeviceDirectCommSettings getDeviceDirectCommSettings() {
        return deviceDirectCommSettings;
    }

    public void setDeviceDirectCommSettings(DeviceDirectCommSettings deviceDirectCommSettings) {
        this.deviceDirectCommSettings = deviceDirectCommSettings;
    }

    public Integer getDnpConfigId() {
        return dnpConfigId;
    }

    public void setDnpConfigId(Integer dnpConfigId) {
        this.dnpConfigId = dnpConfigId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

	public boolean isPointsAvailable() {
		return isPointsAvailable;
	}

	public void setPointsAvailable(boolean isPointsAvailable) {
		this.isPointsAvailable = isPointsAvailable;
	}

	public boolean isCopyPoints() {
		return copyPoints;
	}

	public void setCopyPoints(boolean copyPoints) {
		this.copyPoints = copyPoints;
	}

	public boolean isCopyFlag() {
		return copyFlag;
	}

	public void setCopyFlag(boolean copyFlag) {
		this.copyFlag = copyFlag;
	}
}
