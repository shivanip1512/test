package com.cannontech.web.editor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.Device;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceDialupSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.device.DeviceWindow;
import com.google.common.collect.ImmutableSet;


public class CapControlCBC implements YukonPao {

    private Integer id;
    private String name;
    private PaoType paoType;
    private boolean disableFlag;
    private LiteYukonPAObject parent;
    private LiteYukonPAObject parentRtu;
    private Integer parentRtuId;
    private Map<PointType, List<String>> points;
    private Device device;
    private DeviceCBC deviceCBC;
    private DeviceWindow deviceWindow;
    private Map<String, DeviceScanRate> deviceScanRateMap;
    private DeviceDirectCommSettings deviceDirectCommSettings;
    private DeviceDialupSettings deviceDialupSettings;
    private String ipAddress = CtiUtilities.STRING_NONE;
    private String port = CtiUtilities.STRING_NONE;
    private DeviceAddress deviceAddress;
    private Integer dnpConfigId;
    private boolean editingController;

    private static final Set<PaoType> twoWayTypes = ImmutableSet.of(PaoType.CBC_7020, PaoType.CBC_7022, PaoType.CBC_7023, PaoType.CBC_7024,
        PaoType.CBC_8020, PaoType.CBC_8024, PaoType.CBC_DNP);
    
    private static final Set<PaoType> logicalTypes = ImmutableSet.of(PaoType.CBC_LOGICAL);
    
    private static final Set<PaoType> heartBeatTypes = ImmutableSet.of(PaoType.CBC_8020, PaoType.CBC_8024, PaoType.CBC_DNP, PaoType.CBC_LOGICAL);

    public static final Set<PaoType> getTwoWayTypes() {
        return twoWayTypes;
    }
    
    public static final Set<PaoType> getLogicalTypes() {
        return logicalTypes;
    }

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
    
    public LiteYukonPAObject getParent() {
        return parent;
    }

    public void setParent(LiteYukonPAObject parent) {
        this.parent = parent;
    }

    public LiteYukonPAObject getParentRtu() {
        return parentRtu;
    }

    public void setParentRtu(LiteYukonPAObject parentRtu) {
        this.parentRtu = parentRtu;
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
        this.ipAddress = ipAddress.trim();
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port.trim();
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

    public boolean isEditingController() {
        return editingController;
    }

    public void setEditingController(boolean editingController) {
        this.editingController = editingController;
    }

    public boolean isTwoWay() {
        return twoWayTypes.contains(getPaoType());
    }
    
    public boolean isOneWay() {
        return !isTwoWay() && !isLogical();
    }
    
    public boolean isHeartBeat() {
        return heartBeatTypes.contains(getPaoType());
    }

    public boolean isLogical() {
        return logicalTypes.contains(getPaoType());
    }

    public boolean isEditingIntegrity() {
        return isTwoWay() && (this.deviceScanRateMap.containsKey(DeviceScanRate.TYPE_INTEGRITY));
    }
    
    public void setEditingIntegrity(boolean editingIntegrity) {
        if(!editingIntegrity){
            this.deviceScanRateMap.remove(DeviceScanRate.TYPE_INTEGRITY);
        }
    }

    public boolean isEditingException() {
        return isTwoWay() && (this.deviceScanRateMap.containsKey(DeviceScanRate.TYPE_EXCEPTION));
    }
    
    public void setEditingException(boolean editingException) {
        if(!editingException){
            this.deviceScanRateMap.remove(DeviceScanRate.TYPE_EXCEPTION);
        }
    }

    public Integer getParentRtuId() {
        return parentRtuId;
    }

    public void setParentRtuId(Integer parentRtuId) {
        this.parentRtuId = parentRtuId;
    }

    public static enum ScanGroup implements DisplayableEnum {
        DEFAULT(0),
        FIRST(1),
        SECOND(2);

        private static String baseKey = "yukon.web.modules.capcontrol.cbc.scanGroup.";

        private int dbValue;

        private ScanGroup(int dbValue) {
            this.dbValue = dbValue;
        }

        public int getDbValue() {
            return dbValue;
        }

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }
}
