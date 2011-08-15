package com.cannontech.stars.dr.thermostat.model;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.stars.dr.hardware.model.HeatCoolSettingType;
import com.cannontech.stars.dr.hardware.model.ListEntryEnum;

/**
 * Enum which represents thermostat modes
 */
public enum ThermostatMode implements ListEntryEnum, DisplayableEnum {
    DEFAULT(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_DEFAULT, "default", HeatCoolSettingType.OTHER), 
    COOL(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_COOL, "cool", HeatCoolSettingType.COOL), 
    HEAT(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_HEAT, "heat", HeatCoolSettingType.HEAT), 
    EMERGENCY_HEAT(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_EMERGENCY_HEAT, "emheat",
                   HeatCoolSettingType.HEAT),
    OFF(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_OFF, "off", HeatCoolSettingType.OTHER);

    // this key prefix can be found in the following file:
    // com.cannontech.yukon.web.modules.consumer.xml
    private final static String keyPrefix = "yukon.common.thermostat.mode.";

    private int definitionId;
    private String commandString;
    private HeatCoolSettingType heatCoolSettingType;

    private ThermostatMode(int definitionId, String commandString, HeatCoolSettingType heatCoolSettingType) {
        this.definitionId = definitionId;
        this.commandString = commandString;
        this.heatCoolSettingType = heatCoolSettingType;
    }

    public int getDefinitionId() {
        return definitionId;
    }

    public String getCommandString() {
        return commandString;
    }
    
    public HeatCoolSettingType getHeatCoolSettingType() {
        return heatCoolSettingType;
    }
    
    public boolean isHeatOrCool() {
        return heatCoolSettingType != HeatCoolSettingType.OTHER;
    }
    
    /**
     * Overloaded method to get the enum value for a definitionId
     * @param definitionId - Definition id to get enum for
     * @return Enum value
     */
    public static ThermostatMode valueOf(int definitionId) {

        ThermostatMode[] values = ThermostatMode.values();
        for (ThermostatMode mode : values) {
            int modeDefinitionId = mode.getDefinitionId();
            if (definitionId == modeDefinitionId) {
                return mode;
            }
        }

        throw new IllegalArgumentException("No ThermostatMode found for definitionId: " + definitionId);

    }

    /**
     * I18N key for the display text for this action
     * @return Display key
     */    
    public String getFormatKey() {
        return keyPrefix + name();
    }
    
    public String getValue(){
        return toString();
    }

    @Override
    public String getListName() {
        return YukonSelectionListDefs.YUK_LIST_NAME_THERMOSTAT_MODE;
    }
}
