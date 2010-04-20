package com.cannontech.stars.dr.hardware.model;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;

/**
 * Enum which represents load management hardware types
 */
public enum HardwareType {
    /* These are real MCTs that exist as yukon paos. Because they are paos ie. MCT-470, MCT-410 ect.  they do not
     * have a yukon definition so it is defined as zero.*/
    YUKON_METER(CtiUtilities.NONE_ZERO_ID, InventoryCategory.MCT, LMHardwareClass.METER),
    
    /* These are meters that only exist in stars in the MeterHardwareBase table. 
     * The yukon list entry text for their definition is 'MCT' which is unfortunate and should probably change.*/
    NON_YUKON_METER(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT, InventoryCategory.NON_YUKON_METER, LMHardwareClass.METER),
    
    /* Switches */
    LCR_5000_EXPRESSCOM(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH), 
    LCR_5000_VERSACOM(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_VCOM, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH), 
    LCR_4000(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_4000, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH), 
    LCR_3000(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3000, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH), 
    LCR_3102(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3102, InventoryCategory.TWO_WAY_RECEIVER, LMHardwareClass.SWITCH), 
    LCR_2000(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_2000, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH), 
    LCR_1000(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_1000, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH), 
    SA_205(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH), 
    SA_305(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA305, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH), 
    SA_SIMPLE(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA_SIMPLE, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH),
    
    /* Thermostats */
    EXPRESSSTAT(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.THERMOSTAT), 
    COMMERCIAL_EXPRESSSTAT(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.THERMOSTAT), 
    EXPRESSSTAT_HEAT_PUMP(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.THERMOSTAT), 
    UTILITY_PRO(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_UTILITYPRO, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.THERMOSTAT), 
    ENERGYPRO(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO, InventoryCategory.TWO_WAY_RECEIVER, LMHardwareClass.THERMOSTAT);

    // this key prefix can be found in the following file:
    // consumer.xml
    private final static String keyPrefix = "yukon.dr.consumer.hardware.type.";
    
    private int definitionId;
    private InventoryCategory inventoryCategory;
    private LMHardwareClass lmHardwareClass;

    private HardwareType(int definitionId, InventoryCategory inventoryCategory, LMHardwareClass lmHardwareClass) {
        this.definitionId = definitionId;
        this.inventoryCategory = inventoryCategory;
        this.lmHardwareClass = lmHardwareClass;
    }

    public int getDefinitionId() {
        return definitionId;
    }
    
    public InventoryCategory getInventoryCategory() {
        return inventoryCategory;
    }
    
    public LMHardwareClass getLMHardwareClass() {
        return lmHardwareClass;
    }
    
    /**
     * Overloaded method to get the enum value for a definitionId
     * @param definitionId - Definition id to get enum for
     * @return Enum value
     */
    public static HardwareType valueOf(int definitionId) {

        HardwareType[] values = HardwareType.values();
        for (HardwareType type : values) {
            int typeDefinitionId = type.getDefinitionId();
            if (definitionId == typeDefinitionId) {
                return type;
            }
        }

        throw new IllegalArgumentException("No HardwareType found for definitionId: " + definitionId);

    }
    
    /**
     * I18N key for the display text for this action
     * @return Display key
     */
    public String getDisplayKey() {
        return keyPrefix + name();
    }
    
    /**
     * Returns true if this hardware type is a thermostat.
     * @return boolean
     */
    public boolean isThermostat() {
        return getLMHardwareClass() == LMHardwareClass.THERMOSTAT;
    }
    
    /**
     * Returns true if this hardware type is a meter/mct.
     * @return boolean
     */
    public boolean isMeter() {
        return getLMHardwareClass() == LMHardwareClass.METER;
    }
    
    /**
     * Returns true if this hardware type is a switch/lcr.
     * @return boolean
     */
    public boolean isSwitch() {
        return getLMHardwareClass() == LMHardwareClass.SWITCH;
    }
    
    /**
     * Returns true if this is a two way device.
     * @return boolean
     */
    public boolean isTwoWay() {
        return getInventoryCategory() == InventoryCategory.TWO_WAY_RECEIVER;
    }

    public boolean isExpressCom() {
        return definitionId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM
            || definitionId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT
            || definitionId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT
            || definitionId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO
            || definitionId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_UTILITYPRO
            || definitionId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP
            || definitionId == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3102;
    }

    public int getNumRelays() {
        return isExpressCom() ? 8 : 4;
    }
}