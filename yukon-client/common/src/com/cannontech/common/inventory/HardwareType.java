package com.cannontech.common.inventory;

import static com.cannontech.common.constants.YukonListEntryTypes.*;
import static com.cannontech.common.inventory.HardwareClass.*;
import static com.cannontech.common.inventory.HardwareConfigType.*;
import static com.cannontech.common.inventory.InventoryCategory.*;

import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Sets;

/**
 * Enum which represents load management hardware types
 */
public enum HardwareType implements DatabaseRepresentationSource, DisplayableEnum {
    
    /* Meters */
    
    /* These are real MCTs that exist as yukon paos. Because they are paos ie. (MCT-470, MCT-410) they do not
     * have a yukon definition, so it is defined as zero.*/
    YUKON_METER(CtiUtilities.NONE_ZERO_ID, MCT, METER, null, false, false, false),

    /* These are meters that only exist in stars in the MeterHardwareBase table. 
     * They do not represent MCT's or link to pao tables.
     * The yukon list entry text for their definition is 'MCT' which is unfortunate and should probably change.*/
    NON_YUKON_METER(YUK_DEF_ID_DEV_TYPE_MCT, InventoryCategory.NON_YUKON_METER, METER, null, false, false, false),

    /* Switches */
    LCR_6600_ZIGBEE(YUK_DEF_ID_DEV_TYPE_LCR_6600_ZIGBEE, TWO_WAY_RECEIVER, SWITCH, SEP, false, false, false),
    LCR_6600_EXPRESSCOM(YUK_DEF_ID_DEV_TYPE_LCR_6600_XCOM, ONE_WAY_RECEIVER, SWITCH, EXPRESSCOM, false, true, false),
    
    LCR_6200_ZIGBEE(YUK_DEF_ID_DEV_TYPE_LCR_6200_ZIGBEE, TWO_WAY_RECEIVER, SWITCH, SEP, false, false, false),
    LCR_6200_EXPRESSCOM(YUK_DEF_ID_DEV_TYPE_LCR_6200_XCOM, ONE_WAY_RECEIVER, SWITCH, EXPRESSCOM, false, true, false),
    
    LCR_5000_EXPRESSCOM(YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM, ONE_WAY_RECEIVER, SWITCH, EXPRESSCOM, true, true, true),
    LCR_5000_VERSACOM(YUK_DEF_ID_DEV_TYPE_LCR_5000_VCOM, ONE_WAY_RECEIVER, SWITCH, VERSACOM, true, true, true),
    
    LCR_4000(YUK_DEF_ID_DEV_TYPE_LCR_4000, ONE_WAY_RECEIVER, SWITCH, VERSACOM, true, true, true),
    LCR_3000(YUK_DEF_ID_DEV_TYPE_LCR_3000, ONE_WAY_RECEIVER, SWITCH, VERSACOM, true, true, true),
    LCR_3102(YUK_DEF_ID_DEV_TYPE_LCR_3102, TWO_WAY_RECEIVER, SWITCH, EXPRESSCOM, true, true, true),
    LCR_2000(YUK_DEF_ID_DEV_TYPE_LCR_2000, ONE_WAY_RECEIVER, SWITCH, VERSACOM, true, true, true),
    LCR_1000(YUK_DEF_ID_DEV_TYPE_LCR_1000, ONE_WAY_RECEIVER, SWITCH, NOT_CONFIGURABLE, true, true, true),
    
    SA_205(YUK_DEF_ID_DEV_TYPE_SA205, ONE_WAY_RECEIVER, SWITCH, SA205, true, true, true),
    SA_305(YUK_DEF_ID_DEV_TYPE_SA305, ONE_WAY_RECEIVER, SWITCH, SA305, true, true, true),
    SA_SIMPLE(YUK_DEF_ID_DEV_TYPE_SA_SIMPLE, ONE_WAY_RECEIVER, SWITCH, HardwareConfigType.SA_SIMPLE, true, true, true),

    /* Thermostats */
    EXPRESSSTAT(YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT, ONE_WAY_RECEIVER, THERMOSTAT, EXPRESSCOM, true, true, true),
    COMMERCIAL_EXPRESSSTAT(YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT, ONE_WAY_RECEIVER, THERMOSTAT, EXPRESSCOM, true, true, true),
    EXPRESSSTAT_HEAT_PUMP(YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP, ONE_WAY_RECEIVER, THERMOSTAT, EXPRESSCOM, true, true, true),
    UTILITY_PRO(YUK_DEF_ID_DEV_TYPE_UTILITYPRO, ONE_WAY_RECEIVER, THERMOSTAT, EXPRESSCOM, true, true, true),
    UTILITY_PRO_G2(YUK_DEF_ID_DEV_TYPE_UTILITYPRO_G2, ONE_WAY_RECEIVER, THERMOSTAT, EXPRESSCOM, true, true, true),
    UTILITY_PRO_G3(YUK_DEF_ID_DEV_TYPE_UTILITYPRO_G3, ONE_WAY_RECEIVER, THERMOSTAT, EXPRESSCOM, true, true, true),
    UTILITY_PRO_ZIGBEE(YUK_DEF_ID_DEV_TYPE_ZIGBEE_UTILITYPRO, TWO_WAY_RECEIVER, THERMOSTAT, SEP, true, false, true),

    /* Gateways*/
    DIGI_GATEWAY(YUK_DEF_ID_DEV_TYPE_DIGI_GATEWAY, TWO_WAY_RECEIVER, GATEWAY, EXPRESSCOM, true, false, false);
    
    // this key prefix can be found in the following file:
    // consumer.xml
    private final static String keyPrefix = "yukon.dr.consumer.hardware.type.";
    
    private int definitionId;
    private InventoryCategory inventoryCategory;
    private HardwareClass hardwareClass;
    private HardwareConfigType hardwareConfigType;
    
    private static ImmutableSet<HardwareType> zigbeeTypes;
    private static ImmutableSet<HardwareType> zigbeeEndpointTypes;
    private static ImmutableSet<HardwareType> validForChangeType;
    private static ImmutableSet<HardwareType> utilityPROTypes;
    private static ImmutableSet<HardwareType> autoModeEnableTypes;
    static {
        Builder<HardwareType> builder = ImmutableSet.builder();
        builder.add(UTILITY_PRO_ZIGBEE);
        builder.add(LCR_6200_ZIGBEE);
        builder.add(LCR_6600_ZIGBEE);
        zigbeeEndpointTypes = builder.build();
        
        builder.add(DIGI_GATEWAY);
        zigbeeTypes = builder.build();
        
        Builder<HardwareType> validForChangeTypeBuilder = ImmutableSet.builder();
        validForChangeTypeBuilder.add(LCR_6600_EXPRESSCOM);
        validForChangeTypeBuilder.add(LCR_6200_EXPRESSCOM);
        validForChangeTypeBuilder.add(LCR_5000_EXPRESSCOM);
        validForChangeTypeBuilder.add(LCR_5000_VERSACOM);
        validForChangeTypeBuilder.add(LCR_4000);
        validForChangeTypeBuilder.add(LCR_3000);
        validForChangeTypeBuilder.add(LCR_2000);
        validForChangeTypeBuilder.add(LCR_1000);
        validForChangeTypeBuilder.add(SA_205);
        validForChangeTypeBuilder.add(SA_305);
        validForChangeTypeBuilder.add(SA_SIMPLE);
        validForChangeTypeBuilder.add(EXPRESSSTAT);
        validForChangeTypeBuilder.add(COMMERCIAL_EXPRESSSTAT);
        validForChangeTypeBuilder.add(EXPRESSSTAT_HEAT_PUMP);
        validForChangeTypeBuilder.add(UTILITY_PRO);
        validForChangeTypeBuilder.add(UTILITY_PRO_G2);
        validForChangeTypeBuilder.add(UTILITY_PRO_G3);
        validForChangeType = validForChangeTypeBuilder.build();
        
        utilityPROTypes =  ImmutableSet.of(UTILITY_PRO, UTILITY_PRO_G2, UTILITY_PRO_G3);
        autoModeEnableTypes =  ImmutableSet.of(UTILITY_PRO, UTILITY_PRO_G2, UTILITY_PRO_G3);
    }
    
    //TODO Drop booleans and turn these into sets as well?
    private boolean supportsSchedules;
    private boolean supportsOptOut;
    private boolean supportsManualAdjustment;

    private HardwareType(int definitionId, 
                         InventoryCategory inventoryCategory,
                         HardwareClass hardwareClass,
                         HardwareConfigType hardwareConfigType,
                         boolean supportsSchedules,
                         boolean supportsOptOut,
                         boolean supportsManualAdjustment) {
        this.definitionId = definitionId;
        this.inventoryCategory = inventoryCategory;
        this.hardwareClass = hardwareClass;
        this.hardwareConfigType = hardwareConfigType;
        this.supportsSchedules = supportsSchedules;
        this.supportsOptOut = supportsOptOut;
        this.supportsManualAdjustment = supportsManualAdjustment;
    }

    public int getDefinitionId() {
        return definitionId;
    }
    
    public InventoryCategory getInventoryCategory() {
        return inventoryCategory;
    }
    
    public HardwareClass getHardwareClass() {
        return hardwareClass;
    }

    public HardwareConfigType getHardwareConfigType() {
        return hardwareConfigType;
    }
    
    public boolean isSupportsSchedules() {
        return supportsSchedules;
    }

    public boolean isSupportsOptOut() {
        return supportsOptOut;
    }
    
    public boolean isSupportsManualAdjustment() {
        return supportsManualAdjustment;
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
     * Returns true if this hardware type is a thermostat.
     * @return boolean
     */
    public boolean isThermostat() {
        return getHardwareClass().isThermostat();
    }
    
    /**
     * Returns true if this hardware type is a gateway.
     * @return
     */
    public boolean isGateway() {
        return getHardwareClass().isGateway();
    }
    
    public boolean isZigbee() {
        return zigbeeTypes.contains(this);
    }

    public boolean isZigbeeEndpoint() {
        return zigbeeEndpointTypes.contains(this);
    }
    
    public boolean isValidForChangeType() {
        return validForChangeType.contains(this);
    }
    
    public static Set<HardwareType> getValidForChangeTypeSet() {
        return validForChangeType;
    }

    /**
     * Returns true if this hardware type is a meter/mct.
     * @return boolean
     */
    public boolean isMeter() {
        return getHardwareClass().isMeter() ;
    }
    
    /**
     * Returns true if this hardware type is a switch/lcr.
     * @return boolean
     */
    public boolean isSwitch() {
        return getHardwareClass().isSwitch();
    }
    
    /**
     * Returns true if this is a two way device.
     * @return boolean
     */
    public boolean isTwoWay() {
        return getInventoryCategory() == TWO_WAY_RECEIVER;
    }

    public boolean isExpressCom() {
        return hardwareConfigType == EXPRESSCOM;
    }
    
    public int getNumRelays() {
        return isExpressCom() ? 8 : 4;
    }

    public boolean isHasTamperDetect() {
        return hardwareConfigType == SA205
            || hardwareConfigType == SA305
            || hardwareConfigType == HardwareConfigType.SA_SIMPLE;
    }

    public boolean isHasProgramSplinter() {
        return hardwareConfigType == EXPRESSCOM;
    }

    public boolean isConfigurable() {
        return hardwareConfigType != HardwareConfigType.SA_SIMPLE
            && hardwareConfigType != null;
    }

    /**
     * Returns a new HashSet<HardwareType> of hardware types that
     * have the lm hardware class specified.
     * @param hardwareClass the class to filter by.
     * @return Set<HardwareType> The filtered set of hardware types.
     */
    public static Set<HardwareType> getForClass(HardwareClass hardwareClass) {
        HashSet<HardwareType> classSet = Sets.newHashSet();
        for(HardwareType type : values()) {
            if(type.getHardwareClass() == hardwareClass) {
                classSet.add(type);
            }
        }
        return classSet;
    }

    public boolean isEnrollable() {
        return hardwareClass == SWITCH || hardwareClass == THERMOSTAT;
    }

    /**
     * I18N key for the display text for this action
     */
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

    public static ImmutableSet<HardwareType> getZigbeeEndpointTypes() {
        return zigbeeEndpointTypes;
    }
    
    /**
     * Checks if a device has the capability of having auto mode enabled.
     */
    public boolean isAutoModeEnableable() {
        return autoModeEnableTypes.contains(this);
    }

    /**
     * Checks if a device has a base type of UtilityPRO.
     */
    public boolean isUtilityProType() {
        return utilityPROTypes.contains(this);
    }
}