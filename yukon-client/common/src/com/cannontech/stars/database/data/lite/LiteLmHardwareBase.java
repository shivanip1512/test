package com.cannontech.stars.database.data.lite;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.data.hardware.LMConfigurationBase;

public class LiteLmHardwareBase extends LiteInventoryBase {
    
    public static final int THERMOSTAT_TYPE_UNKNOWN = -1;
    public static final int THERMOSTAT_TYPE_IS_NOT = 0;
    public static final int THERMOSTAT_TYPE_ONE_WAY = 1;
    public static final int THERMOSTAT_TYPE_TWO_WAY = 2;
    
    private String manufacturerSerialNumber;
    private int lmHardwareTypeID;
    private int routeID;
    private int configurationID;
    
    // Extended fields
    private int thermostatType = THERMOSTAT_TYPE_UNKNOWN;
    private LiteStarsThermostatSettings thermostatSettings;
    private LiteLMConfiguration lmConfiguration;
    
    public LiteLmHardwareBase() {
        setLiteType(LiteTypes.STARS_LMHARDWARE);
    }
    
    public LiteLmHardwareBase(int inventoryId) {
        super(inventoryId);
        setLiteType(LiteTypes.STARS_LMHARDWARE);
    }
    
    public int getLmHardwareTypeID() {
        return lmHardwareTypeID;
    }
    
    public String getManufacturerSerialNumber() {
        return manufacturerSerialNumber;
    }
    
    public void setLmHardwareTypeID(int lmHardwareTypeId) {
        this.lmHardwareTypeID = lmHardwareTypeId;
    }
    
    public void setManufacturerSerialNumber(String manufacturerSerialNumber) {
        this.manufacturerSerialNumber = manufacturerSerialNumber;
    }
    
    public LiteStarsThermostatSettings getThermostatSettings() {
        return thermostatSettings;
    }
    
    public void setThermostatSettings(LiteStarsThermostatSettings thermostatSettings) {
        this.thermostatSettings = thermostatSettings;
    }
    
    public int getRouteID() {
        return routeID;
    }
    
    public void setRouteID(int routeId) {
        this.routeID = routeId;
    }
    
    public int getConfigurationID() {
        return configurationID;
    }
    
    public void setConfigurationID(int configurationId) {
        this.configurationID = configurationId;
    }
    
    public LiteLMConfiguration getLMConfiguration() {
        
        if (lmConfiguration == null && getConfigurationID() > 0) {
            LMConfigurationBase config = LMConfigurationBase.getLMConfiguration(configurationID, lmHardwareTypeID);
            lmConfiguration = new LiteLMConfiguration();
            StarsLiteFactory.setLiteLMConfiguration(lmConfiguration, config);
        }
        
        return lmConfiguration;
    }
    
    public void setLMConfiguration(LiteLMConfiguration lmConfiguration) {
        this.lmConfiguration = lmConfiguration;
    }
    
    public int getThermostatType() {
        
        if (thermostatType == THERMOSTAT_TYPE_UNKNOWN) {
            updateThermostatType();
        }
        
        return thermostatType;
    }
    
    public void updateThermostatType() {
        
        YukonListDao listDao = YukonSpringHook.getBean(YukonListDao.class);
        YukonListEntry invCatEntry = listDao.getYukonListEntry(getCategoryID());
        YukonListEntry devTypeEntry = listDao.getYukonListEntry(lmHardwareTypeID);
        
        int categoryDefId = invCatEntry.getYukonDefID();
        int deviceTypeDefId = devTypeEntry.getYukonDefID();
        if (categoryDefId == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC 
                && (deviceTypeDefId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT 
                    || deviceTypeDefId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT
                    || deviceTypeDefId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP
                    || deviceTypeDefId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_UTILITYPRO
                    || deviceTypeDefId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_UTILITYPRO_G2
                    || deviceTypeDefId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_UTILITYPRO_G3)){
            thermostatType = THERMOSTAT_TYPE_ONE_WAY;
        } else if (categoryDefId == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC 
                && (deviceTypeDefId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ZIGBEE_UTILITYPRO)) {
            thermostatType = THERMOSTAT_TYPE_TWO_WAY;
        } else {
            thermostatType = THERMOSTAT_TYPE_IS_NOT;
        }
    }
    
    public boolean isOneWayThermostat() {
        return getThermostatType() == THERMOSTAT_TYPE_ONE_WAY;
    }
    
    public boolean isTwoWayThermostat() {
        return getThermostatType() == THERMOSTAT_TYPE_TWO_WAY;
    }
    
    public boolean isThermostat() {
        return isOneWayThermostat() || isTwoWayThermostat();
    }
    
    @Override
    public String toString() {
        return String.format("Hardware [serialNumber=%s, hardwareType=%s, routeId=%s, configurationId=%s]",
                             manufacturerSerialNumber, lmHardwareTypeID, routeID, configurationID);
    }
}