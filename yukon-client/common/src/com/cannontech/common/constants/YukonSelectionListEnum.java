package com.cannontech.common.constants;

import static com.cannontech.common.constants.SelectionListCategory.*;
import static com.cannontech.common.constants.YukonSelectionListDefs.*;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

public enum YukonSelectionListEnum implements DisplayableEnum, DatabaseRepresentationSource {
	LM_CUSTOMER_EVENT(YUK_LIST_NAME_LM_CUSTOMER_EVENT, SYSTEM, true),
	LM_CUSTOMER_ACTION(YUK_LIST_NAME_LM_CUSTOMER_ACTION, SYSTEM, true),
	INVENTORY_CATEGORY(YUK_LIST_NAME_INVENTORY_CATEGORY, SYSTEM, true),
    CONTACT_TYPE(YUK_LIST_NAME_CONTACT_TYPE, SYSTEM, true),
    CALC_FUNCTIONS(YUK_LIST_NAME_CALC_FUNCTIONS, SYSTEM),
	DEVICE_VOLTAGE(YUK_LIST_NAME_DEVICE_VOLTAGE, HARDWARE), // User Editable
	DEVICE_TYPE(YUK_LIST_NAME_DEVICE_TYPE, HARDWARE, true, true), // User Editable
	DEVICE_STATUS(YUK_LIST_NAME_DEVICE_STATUS, HARDWARE, true), // User Editable
	APPLIANCE_CATEGORY(YUK_LIST_NAME_APPLIANCE_CATEGORY, APPLIANCE, true),
	CALL_TYPE(YUK_LIST_NAME_CALL_TYPE, CALL_TRACKING), // User Editable
	SERVICE_TYPE(YUK_LIST_NAME_SERVICE_TYPE, SERVICE_ORDER, true), // User Editable
	SERVICE_STATUS(YUK_LIST_NAME_SERVICE_STATUS, SERVICE_ORDER, true),
	SEARCH_TYPE(YUK_LIST_NAME_SEARCH_TYPE, SYSTEM, true),
	MANUFACTURER(YUK_LIST_NAME_MANUFACTURER, APPLIANCE), // User Editable
	APP_LOCATION(YUK_LIST_NAME_APP_LOCATION, APPLIANCE), // User Editable
	DEVICE_LOCATION(YUK_LIST_NAME_DEVICE_LOCATION, null),
	CHANCE_OF_CONTROL(YUK_LIST_NAME_CHANCE_OF_CONTROL, CONTROL), // User Editable
	TIME_OF_WEEK(YUK_LIST_NAME_TIME_OF_WEEK, THERMOSTAT, true),
	THERMOSTAT_MODE(YUK_LIST_NAME_THERMOSTAT_MODE, THERMOSTAT, true),
	THERMOSTAT_FAN_STATE(YUK_LIST_NAME_THERMOSTAT_FAN_STATE, THERMOSTAT, true),
	RESIDENCE_TYPE(YUK_LIST_NAME_RESIDENCE_TYPE, RESIDENCE), // User Editable
	CONSTRUCTION_MATERIAL(YUK_LIST_NAME_CONSTRUCTION_MATERIAL, RESIDENCE), // User Editable
	DECADE_BUILT(YUK_LIST_NAME_DECADE_BUILT, RESIDENCE), // User Editable
	SQUARE_FEET(YUK_LIST_NAME_SQUARE_FEET, RESIDENCE), // User Editable
	INSULATION_DEPTH(YUK_LIST_NAME_INSULATION_DEPTH, RESIDENCE), // User Editable
	GENERAL_CONDITION(YUK_LIST_NAME_GENERAL_CONDITION, RESIDENCE), // User Editable
	COOLING_SYSTEM(YUK_LIST_NAME_COOLING_SYSTEM, RESIDENCE), // User Editable
	HEATING_SYSTEM(YUK_LIST_NAME_HEATING_SYSTEM, RESIDENCE), // User Editable
	NUM_OF_OCCUPANTS(YUK_LIST_NAME_NUM_OF_OCCUPANTS, RESIDENCE), // User Editable
	OWNERSHIP_TYPE(YUK_LIST_NAME_OWNERSHIP_TYPE, RESIDENCE), // User Editable
	FUEL_TYPE(YUK_LIST_NAME_FUEL_TYPE, RESIDENCE), // User Editable
	AC_TONNAGE(YUK_LIST_NAME_AC_TONNAGE, APPLIANCE_AIR_CONDITIONING), // User Editable
	AC_TYPE(YUK_LIST_NAME_AC_TYPE, APPLIANCE_AIR_CONDITIONING), // User Editable
	WH_NUM_OF_GALLONS(YUK_LIST_NAME_WH_NUM_OF_GALLONS, APPLIANCE_WATER_HEATER), // User Editable
	WH_ENERGY_SOURCE(YUK_LIST_NAME_WH_ENERGY_SOURCE, APPLIANCE_WATER_HEATER), // User Editable
	WH_LOCATION(YUK_LIST_NAME_WH_LOCATION, APPLIANCE_WATER_HEATER), // User Editable
	DF_SWITCH_OVER_TYPE(YUK_LIST_NAME_DF_SWITCH_OVER_TYPE, APPLIANCE_DUEL_FUEL), // User Editable
	DF_SECONDARY_SOURCE(YUK_LIST_NAME_DF_SECONDARY_SOURCE, APPLIANCE_DUEL_FUEL), // User Editable
	GEN_TRANSFER_SWITCH_TYPE(YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE, APPLIANCE_GENERATOR), // User Editable
	GEN_TRANSFER_SWITCH_MFG(YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG, APPLIANCE_GENERATOR), // User Editable
	GRAIN_DRYER_TYPE(YUK_LIST_NAME_GRAIN_DRYER_TYPE, APPLIANCE_GRAIN_DRYER), // User Editable
	GD_BIN_SIZE(YUK_LIST_NAME_GD_BIN_SIZE, APPLIANCE_GRAIN_DRYER), // User Editable
	GD_ENERGY_SOURCE(YUK_LIST_NAME_GD_ENERGY_SOURCE, APPLIANCE_GRAIN_DRYER), // User Editable
	GD_HORSE_POWER(YUK_LIST_NAME_GD_HORSE_POWER, APPLIANCE_GRAIN_DRYER), // User Editable
	GD_HEAT_SOURCE(YUK_LIST_NAME_GD_HEAT_SOURCE, APPLIANCE_GRAIN_DRYER), // User Editable
	STORAGE_HEAT_TYPE(YUK_LIST_NAME_STORAGE_HEAT_TYPE, APPLIANCE_STORAGE_HEAT), // User Editable
	HEAT_PUMP_TYPE(YUK_LIST_NAME_HEAT_PUMP_TYPE, APPLIANCE_HEAT_PUMP), // User Editable
	HEAT_PUMP_SIZE(YUK_LIST_NAME_HEAT_PUMP_SIZE, APPLIANCE_HEAT_PUMP), // User Editable
	HP_STANDBY_SOURCE(YUK_LIST_NAME_HP_STANDBY_SOURCE, APPLIANCE_HEAT_PUMP), // User Editable
	IRRIGATION_TYPE(YUK_LIST_NAME_IRRIGATION_TYPE, APPLIANCE_IRRIGATION), // User Editable
	IRR_HORSE_POWER(YUK_LIST_NAME_IRR_HORSE_POWER, APPLIANCE_IRRIGATION), // User Editable
	IRR_ENERGY_SOURCE(YUK_LIST_NAME_IRR_ENERGY_SOURCE, APPLIANCE_IRRIGATION), // User Editable
	IRR_SOIL_TYPE(YUK_LIST_NAME_IRR_SOIL_TYPE, APPLIANCE_IRRIGATION), // User Editable
	IRR_METER_LOCATION(YUK_LIST_NAME_IRR_METER_LOCATION, APPLIANCE_IRRIGATION), // User Editable
	IRR_METER_VOLTAGE(YUK_LIST_NAME_IRR_METER_VOLTAGE, APPLIANCE_IRRIGATION), // User Editable
	INV_SEARCH_BY(YUK_LIST_NAME_INV_SEARCH_BY, HARDWARE, true),
	INV_SORT_BY(YUK_LIST_NAME_INV_SORT_BY, HARDWARE, true),
	INV_FILTER_BY(YUK_LIST_NAME_INV_FILTER_BY, HARDWARE, true),
	SO_SEARCH_BY(YUK_LIST_NAME_SO_SEARCH_BY, SERVICE_ORDER, true),
	SO_SORT_BY(YUK_LIST_NAME_SO_SORT_BY, SERVICE_ORDER, true),
	SO_FILTER_BY(YUK_LIST_NAME_SO_FILTER_BY, SERVICE_ORDER, true),
	RATE_SCHEDULE(YUK_LIST_NAME_RATE_SCHEDULE, ACCOUNT, true), // User Editable
    CONSUMPTION_TYPE(YUK_LIST_NAME_CONSUMPTION_TYPE, null),
    EVENT_SYSTEM_CATEGORY(YUK_LIST_NAME_EVENT_SYSTEM_CATEGORY, SYSTEM),
    ACCOUNT_ACTION(YUK_LIST_NAME_ACCOUNT_ACTION, SYSTEM),
    INVENTORY_ACTION(YUK_LIST_NAME_INVENTORY_ACTION, null),
    WORKORDER_ACTION(YUK_LIST_NAME_WORKORDER_ACTION, null),
	SETTLEMENT_TYPE(YUK_LIST_NAME_SETTLEMENT_TYPE, SYSTEM), // User Editable
	CI_CUST_TYPE(YUK_LIST_NAME_CI_CUST_TYPE, ACCOUNT),
	CONTROLLER_TYPE(YUK_LIST_NAME_CONTROLLER_TYPE, CAP_CONTROL),
	SWITCH_MANUFACTURER(YUK_LIST_NAME_SWITCH_MANUFACTURER_TYPE, CAP_CONTROL),
	TYPE_OF_SWITCH(YUK_LIST_NAME_SWITCH_TYPE, CAP_CONTROL);

    private final static String keyPrefix = "yukon.dr.selectionList.";
    private final static ImmutableMap<String, YukonSelectionListEnum> lookupByName;
    private final static ImmutableMultimap<SelectionListCategory, YukonSelectionListEnum> lookupByCategory;

    private final String listName;
    private final SelectionListCategory category;
    private final boolean requiresType;
    private final boolean requiresText;

    static {
        ImmutableMap.Builder<String, YukonSelectionListEnum> nameBuilder = ImmutableMap.builder();
        ImmutableMultimap.Builder<SelectionListCategory, YukonSelectionListEnum> categoryBuilder =
            ImmutableMultimap.builder();

        for (YukonSelectionListEnum list : values()) {
            nameBuilder.put(list.listName, list);
            if (list.getCategory() != null) {
                categoryBuilder.put(list.getCategory(), list);
            }
        }

        lookupByName = nameBuilder.build();
        lookupByCategory = categoryBuilder.build();
    }

    public static YukonSelectionListEnum getForName(String listName) {
        return lookupByName.get(listName);
    }

    private YukonSelectionListEnum(String listName, SelectionListCategory category) {
        this(listName, category, false, false);
    }

    private YukonSelectionListEnum(String listName, SelectionListCategory category,
                                   boolean requiresType) {
        this(listName, category, requiresType, false);
    }

    private YukonSelectionListEnum(String listName, SelectionListCategory category,
                                   boolean requiresType, boolean requiresText) {
        this.listName = listName;
        this.category = category;
        this.requiresType = requiresType;
        this.requiresText = requiresText;
	}

	public String getListName() {
		return listName;
	}

    public SelectionListCategory getCategory() {
        return category;
    }

    public static Iterable<YukonSelectionListEnum> getByCategory(SelectionListCategory category) {
        return lookupByCategory.get(category);
    }

    public boolean isRequiresType() {
        return requiresType;
    }
    
    public boolean isRequiresText() {
        return requiresText;
    }

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

    @Override
    public Object getDatabaseRepresentation() {
        return listName;
    }
}
