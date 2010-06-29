package com.cannontech.stars.dr.hardware.model;

import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.Sets;

/**
 * Enum which represents load management hardware types
 */
public enum HardwareType implements DatabaseRepresentationSource{
    /* These are real MCTs that exist as yukon paos. Because they are paos ie. MCT-470, MCT-410 ect.  they do not
     * have a yukon definition so it is defined as zero.*/
    YUKON_METER(CtiUtilities.NONE_ZERO_ID, InventoryCategory.MCT, LMHardwareClass.METER, null),

    /* These are meters that only exist in stars in the MeterHardwareBase table. 
     * The yukon list entry text for their definition is 'MCT' which is unfortunate and should probably change.*/
    NON_YUKON_METER(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT, InventoryCategory.NON_YUKON_METER, LMHardwareClass.METER, null),

    /* Switches */
    LCR_5000_EXPRESSCOM(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH, HardwareConfigType.EXPRESSCOM),
    LCR_5000_VERSACOM(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_VCOM, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH, HardwareConfigType.VERSACOM),
    LCR_4000(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_4000, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH, HardwareConfigType.VERSACOM),
    LCR_3000(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3000, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH, HardwareConfigType.VERSACOM),
    LCR_3102(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3102, InventoryCategory.TWO_WAY_RECEIVER, LMHardwareClass.SWITCH, HardwareConfigType.EXPRESSCOM),
    LCR_2000(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_2000, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH, HardwareConfigType.VERSACOM),
    LCR_1000(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_1000, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH, null),
    SA_205(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH, HardwareConfigType.SA205),
    SA_305(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA305, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH, HardwareConfigType.SA305),
    SA_SIMPLE(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA_SIMPLE, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.SWITCH, HardwareConfigType.SA_SIMPLE),

    /* Thermostats */
    EXPRESSSTAT(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.THERMOSTAT, HardwareConfigType.EXPRESSCOM),
    COMMERCIAL_EXPRESSSTAT(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.THERMOSTAT, HardwareConfigType.EXPRESSCOM),
    EXPRESSSTAT_HEAT_PUMP(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.THERMOSTAT, HardwareConfigType.EXPRESSCOM),
    UTILITY_PRO(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_UTILITYPRO, InventoryCategory.ONE_WAY_RECEIVER, LMHardwareClass.THERMOSTAT, HardwareConfigType.EXPRESSCOM),
    ENERGYPRO(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO, InventoryCategory.TWO_WAY_RECEIVER, LMHardwareClass.THERMOSTAT, HardwareConfigType.EXPRESSCOM);

    // this key prefix can be found in the following file:
    // consumer.xml
    private final static String keyPrefix = "yukon.dr.consumer.hardware.type.";
    
    private int definitionId;
    private InventoryCategory inventoryCategory;
    private LMHardwareClass lmHardwareClass;
    private HardwareConfigType hardwareConfigType;

    private HardwareType(int definitionId, InventoryCategory inventoryCategory,
            LMHardwareClass lmHardwareClass,
            HardwareConfigType hardwareConfigType) {
        this.definitionId = definitionId;
        this.inventoryCategory = inventoryCategory;
        this.lmHardwareClass = lmHardwareClass;
        this.hardwareConfigType = hardwareConfigType;
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

    public HardwareConfigType getHardwareConfigType() {
        return hardwareConfigType;
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
    
    @Override
    public Object getDatabaseRepresentation() {
        return getDefinitionId();
    }
    
    /* HELPERS */
    
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
        return hardwareConfigType == HardwareConfigType.EXPRESSCOM;
    }

    public int getNumRelays() {
        return isExpressCom() ? 8 : 4;
    }

    public boolean isHasTamperDetect() {
        return hardwareConfigType == HardwareConfigType.SA205
            || hardwareConfigType == HardwareConfigType.SA305
            || hardwareConfigType == HardwareConfigType.SA_SIMPLE;
    }

    public boolean isHasProgramSplinter() {
        return hardwareConfigType == HardwareConfigType.EXPRESSCOM;
    }

    public boolean isConfigurable() {
        return hardwareConfigType != HardwareConfigType.SA_SIMPLE
            && hardwareConfigType != null;
    }

    /**
     * Returns a new HashSet<HardwareType> of hardware types that
     * have the lm hardware class specified.
     * @param lmHardwareClass the class to filter by.
     * @return Set<HardwareType> The filtered set of hardware types.
     */
    public static Set<HardwareType> getForClass(LMHardwareClass lmHardwareClass) {
        HashSet<HardwareType> classSet = Sets.newHashSet();
        for(HardwareType type : values()) {
            if(type.getLMHardwareClass() == lmHardwareClass) {
                classSet.add(type);
            }
        }
        return classSet;
    }
}