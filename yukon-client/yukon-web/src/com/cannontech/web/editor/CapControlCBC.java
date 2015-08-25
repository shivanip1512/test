package com.cannontech.web.editor;

import java.util.List;
import java.util.Map;

import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.Device;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceDialupSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.device.DeviceWindow;
import com.cannontech.database.db.pao.YukonPAObject;

public class CapControlCBC {

    private boolean disableFlag;
    private String cbcParent;
    private Map<PointType, List<String>> points;
    private Device device;
    private DeviceCBC deviceCBC;
    private DeviceWindow deviceWindow;
    Map<String, DeviceScanRate> deviceScanRateMap;
    private DeviceDirectCommSettings deviceDirectCommSettings;
    private DeviceDialupSettings deviceDialupSettings;
    private String ipAddress = CtiUtilities.STRING_NONE;
    private String port = CtiUtilities.STRING_NONE;
    private DeviceAddress deviceAddress;
    private DNPConfiguration dnpConfiguration;
    private boolean editingController;
    private boolean twoWay;
    private boolean editingIntegrity;
    private boolean editingException;
    private YukonPAObject yukonPAObject;

    public String getCbcParent() {
        return cbcParent;
    }

    public void setCbcParent(String cbcParent) {
        this.cbcParent = cbcParent;
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

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public DeviceCBC getDeviceCBC() {
        return deviceCBC;
    }

    public void setDeviceCBC(DeviceCBC deviceCBC) {
        this.deviceCBC = deviceCBC;
    }

    public DeviceWindow getDeviceWindow() {
        return deviceWindow;
    }

    public void setDeviceWindow(DeviceWindow deviceWindow) {
        this.deviceWindow = deviceWindow;
    }

    public Map<String, DeviceScanRate> getDeviceScanRateMap() {
        return deviceScanRateMap;
    }

    public void setDeviceScanRateMap(Map<String, DeviceScanRate> deviceScanRateMap) {
        this.deviceScanRateMap = deviceScanRateMap;
    }

    public DeviceDirectCommSettings getDeviceDirectCommSettings() {
        return deviceDirectCommSettings;
    }

    public void setDeviceDirectCommSettings(DeviceDirectCommSettings deviceDirectCommSettings) {
        this.deviceDirectCommSettings = deviceDirectCommSettings;
    }

    public DeviceDialupSettings getDeviceDialupSettings() {
        return deviceDialupSettings;
    }

    public void setDeviceDialupSettings(DeviceDialupSettings deviceDialupSettings) {
        this.deviceDialupSettings = deviceDialupSettings;
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

    public DeviceAddress getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(DeviceAddress deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public DNPConfiguration getDnpConfiguration() {
        return dnpConfiguration;
    }

    public void setDnpConfiguration(DNPConfiguration dnpConfiguration) {
        this.dnpConfiguration = dnpConfiguration;
    }

    public boolean isEditingController() {
        return editingController;
    }

    public void setEditingController(boolean editingController) {
        this.editingController = editingController;
    }

    public boolean isTwoWay() {
        return twoWay;
    }

    public void setTwoWay(boolean twoWay) {
        this.twoWay = twoWay;
    }

    public boolean isEditingIntegrity() {
        return isTwoWay() && (this.deviceScanRateMap.containsKey(DeviceScanRate.TYPE_INTEGRITY));
    }

    public void setEditingIntegrity(boolean editingIntegrity) {
        this.editingIntegrity = editingIntegrity;
    }

    public boolean isEditingException() {
        return isTwoWay() && (this.deviceScanRateMap.containsKey(DeviceScanRate.TYPE_EXCEPTION));
    }

    public void setEditingException(boolean editingException) {
        this.editingException = editingException;
    }

    public YukonPAObject getYukonPAObject() {
        return yukonPAObject;
    }

    public void setYukonPAObject(YukonPAObject yukonPAObject) {
        this.yukonPAObject = yukonPAObject;
    }

}
