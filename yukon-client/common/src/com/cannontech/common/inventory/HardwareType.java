package com.cannontech.common.inventory;

import static com.cannontech.common.constants.YukonListEntryTypes.*;
import static com.cannontech.common.inventory.HardwareClass.*;
import static com.cannontech.common.inventory.HardwareConfigType.*;
import static com.cannontech.common.inventory.InventoryCategory.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.ListMultimap;
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
     * They do not represent MCT's or link to pao tables.*/
    NON_YUKON_METER(YUK_DEF_ID_DEV_TYPE_NON_YUKON_METER, InventoryCategory.NON_YUKON_METER, METER, null, false, false, false),

    /* Switches */
    LCR_6600_ZIGBEE(YUK_DEF_ID_DEV_TYPE_LCR_6600_ZIGBEE, TWO_WAY_RECEIVER, SWITCH, SEP, false, false, false),
    LCR_6600_EXPRESSCOM(YUK_DEF_ID_DEV_TYPE_LCR_6600_XCOM, ONE_WAY_RECEIVER, SWITCH, EXPRESSCOM, false, true, false),
    LCR_6600_RFN(YUK_DEF_ID_DEV_TYPE_LCR_6600_RFN, TWO_WAY_RECEIVER, SWITCH, EXPRESSCOM, false, true, false),
    
    LCR_6200_ZIGBEE(YUK_DEF_ID_DEV_TYPE_LCR_6200_ZIGBEE, TWO_WAY_RECEIVER, SWITCH, SEP, false, false, false),
    LCR_6200_EXPRESSCOM(YUK_DEF_ID_DEV_TYPE_LCR_6200_XCOM, ONE_WAY_RECEIVER, SWITCH, EXPRESSCOM, false, true, false),
    LCR_6200_RFN(YUK_DEF_ID_DEV_TYPE_LCR_6200_RFN, TWO_WAY_RECEIVER, SWITCH, EXPRESSCOM, false, true, false),
    
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
    
    
    private final static ImmutableSet<HardwareType> rfTypes;
    private final static ImmutableSet<HardwareType> saTypes = ImmutableSet.of(SA_205, SA_305, SA_SIMPLE);
    private final static ImmutableSet<HardwareType> zigbeeTypes;
    private final static ImmutableSet<HardwareType> zigbeeEndpointTypes;
    private final static ImmutableSet<HardwareType> utilityProTypes;
    private final static ImmutableSet<HardwareType> autoModeEnableTypes;
    
    private final static ImmutableSet<HardwareType> supportsChangeType;
    private final static ImmutableSet<HardwareType> supportsAddByRange;
    
    private final static ListMultimap<PaoType, HardwareType> starsToPaoMap;
    
    static {
        Builder<HardwareType> builder = ImmutableSet.builder();
        builder.add(UTILITY_PRO_ZIGBEE);
        builder.add(LCR_6200_ZIGBEE);
        builder.add(LCR_6600_ZIGBEE);
        zigbeeEndpointTypes = builder.build();
        
        builder.add(DIGI_GATEWAY);
        zigbeeTypes = builder.build();
        
        builder = ImmutableSet.builder();
        builder.add(LCR_6600_EXPRESSCOM);
        builder.add(LCR_6200_EXPRESSCOM);
        builder.add(LCR_5000_EXPRESSCOM);
        builder.add(LCR_5000_VERSACOM);
        builder.add(LCR_4000);
        builder.add(LCR_3000);
        builder.add(LCR_2000);
        builder.add(LCR_1000);
        builder.add(SA_205);
        builder.add(SA_305);
        builder.add(SA_SIMPLE);
        builder.add(EXPRESSSTAT);
        builder.add(COMMERCIAL_EXPRESSSTAT);
        builder.add(EXPRESSSTAT_HEAT_PUMP);
        builder.add(UTILITY_PRO);
        builder.add(UTILITY_PRO_G2);
        builder.add(UTILITY_PRO_G3);
        supportsChangeType = builder.build();
        supportsAddByRange = builder.build();
        
        rfTypes = ImmutableSet.of(LCR_6200_RFN, LCR_6600_RFN);
        
        utilityProTypes =  ImmutableSet.of(UTILITY_PRO, UTILITY_PRO_G2, UTILITY_PRO_G3, UTILITY_PRO_ZIGBEE);
        autoModeEnableTypes =  ImmutableSet.of(UTILITY_PRO, UTILITY_PRO_G2, UTILITY_PRO_G3);
        
        // PaoType map
        starsToPaoMap = ArrayListMultimap.create();
        starsToPaoMap.put(PaoType.DIGIGATEWAY, DIGI_GATEWAY);
        starsToPaoMap.put(PaoType.ZIGBEE_ENDPOINT, UTILITY_PRO_ZIGBEE);
        starsToPaoMap.put(PaoType.ZIGBEE_ENDPOINT, LCR_6200_ZIGBEE);
        starsToPaoMap.put(PaoType.ZIGBEE_ENDPOINT, LCR_6600_ZIGBEE);
        starsToPaoMap.put(PaoType.LCR3102, LCR_3102);
        starsToPaoMap.put(PaoType.LCR6200_RFN, LCR_6200_RFN);
        starsToPaoMap.put(PaoType.LCR6600_RFN, LCR_6600_RFN);
    }
    
    // this key prefix can be found in the following file:
    // consumer.xml
    private final static String keyPrefix = "yukon.dr.consumer.hardware.type.";
    
    private final int definitionId;
    private final InventoryCategory inventoryCategory;
    private final HardwareClass hardwareClass;
    private final HardwareConfigType hardwareConfigType;
    
    //TODO Drop booleans and turn these into sets as well?
    private final boolean supportsSchedules;
    private final boolean supportsOptOut;
    private final boolean supportsManualAdjustment;

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
    
    public boolean isRf() {
        return rfTypes.contains(this);
    }
    
    /**
     * Returns true if this hardware type is a thermostat.
     */
    public boolean isThermostat() {
        return getHardwareClass().isThermostat();
    }
    
    /**
     * Returns true if this hardware type is a gateway (ZigBee gateway device).
     */
    public boolean isGateway() {
        return getHardwareClass().isGateway();
    }
    
    /**
     * Returns true if this hardware type is a ZigBee device.
     */
    public boolean isZigbee() {
        return zigbeeTypes.contains(this);
    }

    /**
     * Returns true if this hardware type is a ZigBee endpoint (non gateway ZigBee device).
     */
    public boolean isZigbeeEndpoint() {
        return zigbeeEndpointTypes.contains(this);
    }
    
    /**
     * Returns true if this hardware type supports the 'change device type' action.
     */
    public boolean isSupportsChangeType() {
        return supportsChangeType.contains(this);
    }
    
    /**
     * Returns set of all hardware types that support the 'change device type' action.
     */
    public static Set<HardwareType> getSupportsChangeType() {
        return supportsChangeType;
    }
    
    /**
     * Returns true if this hardware type supports the 'add by range' action.
     */
    public boolean isSupportsAddByRange() {
        return supportsAddByRange.contains(this);
    }
    
    /**
	 * Returns true if this hardware type supports receiving messages
	 */
	public boolean isSupportsTextMessages() {
		return isUtilityProType();
	}

    /**
     * Returns set of all hardware types that support the 'add by range' action.
     */
    public static Set<HardwareType> getSupportsAddByRange() {
        return supportsAddByRange;
    }

    /**
     * Returns true if this hardware type is a meter(MeterHardwareBase or MCT).
     */
    public boolean isMeter() {
        return getHardwareClass().isMeter() ;
    }
    
    /**
     * Returns true if this hardware type is a switch or lcr.
     */
    public boolean isSwitch() {
        return getHardwareClass().isSwitch();
    }
    
    /**
     * Returns true if this is a two way device.
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
     * Returns true if this hardware type is a Utility Pro type of thermostat.
     */
    public boolean isUtilityProType() {
        return utilityProTypes.contains(this);
    }
    
    /**
     * Returns true if this hardware type is a Scientific America device.
     */
    public boolean isSA() {
        return saTypes.contains(this);
    }
    
    /**
     * Returns a list of hardware types that map to a pao type, or an empty list if no
     * mappings exist for that pao type.  MCT types are not supported, only dr style devices.
     */
    public static List<HardwareType> getForPaoType(PaoType type) {
        return starsToPaoMap.get(type);
    }
    
    public PaoType getForHardwareType() {
        for (PaoType paoType : starsToPaoMap.keySet()) {
            if (starsToPaoMap.get(paoType).contains(this)) return paoType;
        }
        throw new IllegalArgumentException("Unknown PaoType for HardwareType: " + this);
    }
    
    public boolean isLmHardware() {
        return getInventoryCategory().isLmHardware();
    }
    
}