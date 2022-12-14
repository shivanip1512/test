package com.cannontech.common.constants;

import static com.cannontech.common.constants.YukonListEntryTypes.*;
import static com.cannontech.common.constants.YukonSelectionListEnum.*;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

public enum YukonDefinition implements DisplayableEnum, DatabaseRepresentationSource {
    CUST_EVENT_LMPROGRAM(YUK_DEF_ID_CUST_EVENT_LMPROGRAM, LM_CUSTOMER_EVENT),
    CUST_EVENT_LMHARDWARE(YUK_DEF_ID_CUST_EVENT_LMHARDWARE, LM_CUSTOMER_EVENT),
    CUST_EVENT_LMTHERMOSTAT_MANUAL(YUK_DEF_ID_CUST_EVENT_LMTHERMOSTAT_MANUAL, LM_CUSTOMER_EVENT),
    CUST_ACT_SIGNUP(YUK_DEF_ID_CUST_ACT_SIGNUP, LM_CUSTOMER_ACTION),
    CUST_ACT_PENDING(YUK_DEF_ID_CUST_ACT_PENDING, LM_CUSTOMER_ACTION),
    CUST_ACT_COMPLETED(YUK_DEF_ID_CUST_ACT_COMPLETED, LM_CUSTOMER_ACTION),
    CUST_ACT_TERMINATION(YUK_DEF_ID_CUST_ACT_TERMINATION, LM_CUSTOMER_ACTION),
    CUST_ACT_TEMP_TERMINATION(YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION, LM_CUSTOMER_ACTION),
    CUST_ACT_FUTURE_ACTIVATION(YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION, LM_CUSTOMER_ACTION),
    CUST_ACT_INSTALL(YUK_DEF_ID_CUST_ACT_INSTALL, LM_CUSTOMER_ACTION),
    CUST_ACT_CONFIG(YUK_DEF_ID_CUST_ACT_CONFIG, LM_CUSTOMER_ACTION),
    CUST_ACT_PROGRAMMING(YUK_DEF_ID_CUST_ACT_PROGRAMMING, LM_CUSTOMER_ACTION),
    CUST_ACT_MANUAL_OPTION(YUK_DEF_ID_CUST_ACT_MANUAL_OPTION, LM_CUSTOMER_ACTION),
    CUST_ACT_UNINSTALL(YUK_DEF_ID_CUST_ACT_UNINSTALL, LM_CUSTOMER_ACTION),
    INV_CAT_ONEWAYREC(YUK_DEF_ID_INV_CAT_ONEWAYREC, INVENTORY_CATEGORY),
    INV_CAT_TWOWAYREC(YUK_DEF_ID_INV_CAT_TWOWAYREC, INVENTORY_CATEGORY),
    INV_CAT_YUKON_METER(YUK_DEF_ID_INV_CAT_YUKON_METER, INVENTORY_CATEGORY),
    INV_CAT_NON_YUKON_METER(YUK_DEF_ID_INV_CAT_NON_YUKON_METER, INVENTORY_CATEGORY),
    DEV_TYPE_EXPRESSSTAT(YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT, DEVICE_TYPE),
    DEV_TYPE_LCR_5000_XCOM(YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM, DEVICE_TYPE),
    DEV_TYPE_NON_YUKON_METER(YUK_DEF_ID_DEV_TYPE_NON_YUKON_METER, DEVICE_TYPE),
    DEV_TYPE_ELECTRIC_METER(YUK_DEF_ID_DEV_TYPE_ELECTRIC_METER, DEVICE_TYPE),
    DEV_TYPE_COMM_EXPRESSSTAT(YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT, DEVICE_TYPE),
    DEV_TYPE_LCR_4700(YUK_DEF_ID_DEV_TYPE_LCR_4700, DEVICE_TYPE),
    DEV_TYPE_LCR_4600(YUK_DEF_ID_DEV_TYPE_LCR_4600, DEVICE_TYPE),
    DEV_TYPE_LCR_4000(YUK_DEF_ID_DEV_TYPE_LCR_4000, DEVICE_TYPE),
    DEV_TYPE_LCR_3100(YUK_DEF_ID_DEV_TYPE_LCR_3100, DEVICE_TYPE),
    DEV_TYPE_LCR_3000(YUK_DEF_ID_DEV_TYPE_LCR_3000, DEVICE_TYPE),
    DEV_TYPE_LCR_2000(YUK_DEF_ID_DEV_TYPE_LCR_2000, DEVICE_TYPE),
    DEV_TYPE_LCR_1000(YUK_DEF_ID_DEV_TYPE_LCR_1000, DEVICE_TYPE),
    DEV_TYPE_SA205(YUK_DEF_ID_DEV_TYPE_SA205, DEVICE_TYPE),
    DEV_TYPE_SA305(YUK_DEF_ID_DEV_TYPE_SA305, DEVICE_TYPE),
    DEV_TYPE_LCR_5000_VCOM(YUK_DEF_ID_DEV_TYPE_LCR_5000_VCOM, DEVICE_TYPE),
    DEV_TYPE_SA_SIMPLE(YUK_DEF_ID_DEV_TYPE_SA_SIMPLE, DEVICE_TYPE),
    DEV_TYPE_EXPRESSSTAT_HEATPUMP(YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP, DEVICE_TYPE),
    DEV_TYPE_UTILITYPRO(YUK_DEF_ID_DEV_TYPE_UTILITYPRO, DEVICE_TYPE),
    DEV_TYPE_UTILITYPRO_G2(YUK_DEF_ID_DEV_TYPE_UTILITYPRO_G2, DEVICE_TYPE),
    DEV_TYPE_UTILITYPRO_G3(YUK_DEF_ID_DEV_TYPE_UTILITYPRO_G3, DEVICE_TYPE),
    DEV_TYPE_ZIGBEE_UTILITYPRO(YUK_DEF_ID_DEV_TYPE_ZIGBEE_UTILITYPRO, DEVICE_TYPE),
    DEV_TYPE_LCR_3102(YUK_DEF_ID_DEV_TYPE_LCR_3102, DEVICE_TYPE),
    DEV_TYPE_DIGI_GATEWAY(YUK_DEF_ID_DEV_TYPE_DIGI_GATEWAY, DEVICE_TYPE),
    DEV_TYPE_LCR_6200_XCOM(YUK_DEF_ID_DEV_TYPE_LCR_6200_XCOM, DEVICE_TYPE),
    DEV_TYPE_LCR_6200_ZIGBEE(YUK_DEF_ID_DEV_TYPE_LCR_6200_ZIGBEE, DEVICE_TYPE),
    DEV_TYPE_LCR_6200_RFN(YUK_DEF_ID_DEV_TYPE_LCR_6200_RFN, DEVICE_TYPE),
    DEV_TYPE_LCR_6600_XCOM(YUK_DEF_ID_DEV_TYPE_LCR_6600_XCOM, DEVICE_TYPE),
    DEV_TYPE_LCR_6600_ZIGBEE(YUK_DEF_ID_DEV_TYPE_LCR_6600_ZIGBEE, DEVICE_TYPE),
    DEV_TYPE_LCR_6600_RFN(YUK_DEF_ID_DEV_TYPE_LCR_6600_RFN, DEVICE_TYPE),
    DEV_TYPE_LCR_6700_RFN(YUK_DEF_ID_DEV_TYPE_LCR_6700_RFN, DEVICE_TYPE),
    DEV_TYPE_LCR_6601S(YUK_DEF_ID_DEV_TYPE_LCR_6601S, DEVICE_TYPE),
    DEV_TYPE_LCR_6600S(YUK_DEF_ID_DEV_TYPE_LCR_6600S, DEVICE_TYPE),
    DEV_TYPE_LCR_6200C(YUK_DEF_ID_DEV_TYPE_LCR_6200C, DEVICE_TYPE),
    DEV_TYPE_LCR_6600C(YUK_DEF_ID_DEV_TYPE_LCR_6600C, DEVICE_TYPE),
    DEV_TYPE_ECOBEE_SMART_SI(YUK_DEF_ID_DEV_TYPE_ECOBEE_SMART_SI, DEVICE_TYPE),
    DEV_TYPE_ECOBEE_3(YUK_DEF_ID_DEV_TYPE_ECOBEE_3, DEVICE_TYPE),
    DEV_TYPE_ECOBEE_3_LITE(YUK_DEF_ID_DEV_TYPE_ECOBEE_3_LITE, DEVICE_TYPE),
    DEV_TYPE_ECOBEE_SMART(YUK_DEF_ID_DEV_TYPE_ECOBEE_SMART, DEVICE_TYPE),
    DEV_TYPE_HONEYWELL_9000(YUK_DEF_ID_DEV_TYPE_HONEYWELL_9000, DEVICE_TYPE),
    DEV_TYPE_HONEYWELL_VISIONPRO_8000(YUK_DEF_ID_DEV_TYPE_HONEYWELL_VISIONPRO_8000, DEVICE_TYPE),
    DEV_TYPE_HONEYWELL_FOCUSPRO(YUK_DEF_ID_DEV_TYPE_HONEYWELL_FOCUSPRO, DEVICE_TYPE),
    DEV_TYPE_HONEYWELL_THERMOSTAT(YUK_DEF_ID_DEV_TYPE_HONEYWELL_THERMOSTAT, DEVICE_TYPE),
    DEV_TYPE_NEST_THERMOSTAT(YUK_DEF_ID_DEV_TYPE_NEST, DEVICE_TYPE),
    APP_CAT_DEFAULT(YUK_DEF_ID_APP_CAT_DEFAULT, APPLIANCE_CATEGORY),
    APP_CAT_AIR_CONDITIONER(YUK_DEF_ID_APP_CAT_AIR_CONDITIONER, APPLIANCE_CATEGORY),
    APP_CAT_WATER_HEATER(YUK_DEF_ID_APP_CAT_WATER_HEATER, APPLIANCE_CATEGORY),
    APP_CAT_STORAGE_HEAT(YUK_DEF_ID_APP_CAT_STORAGE_HEAT, APPLIANCE_CATEGORY),
    APP_CAT_HEAT_PUMP(YUK_DEF_ID_APP_CAT_HEAT_PUMP, APPLIANCE_CATEGORY),
    APP_CAT_DUAL_FUEL(YUK_DEF_ID_APP_CAT_DUAL_FUEL, APPLIANCE_CATEGORY),
    APP_CAT_GENERATOR(YUK_DEF_ID_APP_CAT_GENERATOR, APPLIANCE_CATEGORY),
    APP_CAT_GRAIN_DRYER(YUK_DEF_ID_APP_CAT_GRAIN_DRYER, APPLIANCE_CATEGORY),
    APP_CAT_IRRIGATION(YUK_DEF_ID_APP_CAT_IRRIGATION, APPLIANCE_CATEGORY),
    APP_CAT_CHILLER(YUK_DEF_ID_APP_CAT_CHILLER, APPLIANCE_CATEGORY),
    APP_CAT_DUALSTAGE(YUK_DEF_ID_APP_CAT_DUALSTAGE, APPLIANCE_CATEGORY),
    SERV_STAT_PENDING(YUK_DEF_ID_SERV_STAT_PENDING, SERVICE_STATUS),
    SERV_STAT_SCHEDULED(YUK_DEF_ID_SERV_STAT_SCHEDULED, SERVICE_STATUS),
    SERV_STAT_COMPLETED(YUK_DEF_ID_SERV_STAT_COMPLETED, SERVICE_STATUS),
    SERV_STAT_CANCELLED(YUK_DEF_ID_SERV_STAT_CANCELLED, SERVICE_STATUS),
    SERV_STAT_ASSIGNED(YUK_DEF_ID_SERV_STAT_ASSIGNED, SERVICE_STATUS),
    SERV_STAT_RELEASED(YUK_DEF_ID_SERV_STAT_RELEASED, SERVICE_STATUS),
    SERV_STAT_PROCESSED(YUK_DEF_ID_SERV_STAT_PROCESSED, SERVICE_STATUS),
    SERV_STAT_HOLD(YUK_DEF_ID_SERV_STAT_HOLD, SERVICE_STATUS),

    SERV_TYPE_SERVICE_CALL(YUK_DEF_ID_SERV_TYPE_SERVICE_CALL, SERVICE_TYPE),
    SERV_TYPE_INSTALL(YUK_DEF_ID_SERV_TYPE_INSTALL, SERVICE_TYPE),
    SERV_TYPE_ACTIVATION(YUK_DEF_ID_SERV_TYPE_ACTIVATION, SERVICE_TYPE),
    SERV_TYPE_DEACTIVATION(YUK_DEF_ID_SERV_TYPE_DEACTIVATION, SERVICE_TYPE),
    SERV_TYPE_REMOVAL(YUK_DEF_ID_SERV_TYPE_REMOVAL, SERVICE_TYPE),
    SERV_TYPE_REPAIR(YUK_DEF_ID_SERV_TYPE_REPAIR, SERVICE_TYPE),
    SERV_TYPE_OTHER(YUK_DEF_ID_SERV_TYPE_OTHER, SERVICE_TYPE),
    SERV_TYPE_MAINTENANCE(YUK_DEF_ID_SERV_TYPE_MAINTENANCE, SERVICE_TYPE),

    SEARCH_TYPE_ACCT_NO(YUK_DEF_ID_SEARCH_TYPE_ACCT_NO, SEARCH_TYPE),
    SEARCH_TYPE_PHONE_NO(YUK_DEF_ID_SEARCH_TYPE_PHONE_NO, SEARCH_TYPE),
    SEARCH_TYPE_LAST_NAME(YUK_DEF_ID_SEARCH_TYPE_LAST_NAME, SEARCH_TYPE),
    SEARCH_TYPE_SERIAL_NO(YUK_DEF_ID_SEARCH_TYPE_SERIAL_NO, SEARCH_TYPE),
    SEARCH_TYPE_MAP_NO(YUK_DEF_ID_SEARCH_TYPE_MAP_NO, SEARCH_TYPE),
    SEARCH_TYPE_ADDRESS(YUK_DEF_ID_SEARCH_TYPE_ADDRESS, SEARCH_TYPE),
    SEARCH_TYPE_ALT_TRACK_NO(YUK_DEF_ID_SEARCH_TYPE_ALT_TRACK_NO, SEARCH_TYPE),
    SEARCH_TYPE_METER_NO(YUK_DEF_ID_SEARCH_TYPE_METER_NO, SEARCH_TYPE),
    SEARCH_TYPE_COMPANY_NAME(YUK_DEF_ID_SEARCH_TYPE_COMPANY_NAME, SEARCH_TYPE),
    DEV_STAT_AVAIL(YUK_DEF_ID_DEV_STAT_AVAIL, DEVICE_STATUS),
    DEV_STAT_TEMP_UNAVAIL(YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL, DEVICE_STATUS),
    DEV_STAT_UNAVAIL(YUK_DEF_ID_DEV_STAT_UNAVAIL, DEVICE_STATUS),
    DEV_STAT_ORDERED(YUK_DEF_ID_DEV_STAT_ORDERED, DEVICE_STATUS),
    DEV_STAT_SHIPPED(YUK_DEF_ID_DEV_STAT_SHIPPED, DEVICE_STATUS),
    DEV_STAT_RECEIVED(YUK_DEF_ID_DEV_STAT_RECEIVED, DEVICE_STATUS),
    DEV_STAT_ISSUED(YUK_DEF_ID_DEV_STAT_ISSUED, DEVICE_STATUS),
    DEV_STAT_INSTALLED(YUK_DEF_ID_DEV_STAT_INSTALLED, DEVICE_STATUS),
    DEV_STAT_REMOVED(YUK_DEF_ID_DEV_STAT_REMOVED, DEVICE_STATUS),
    MANU_UNKNOWN(YUK_DEF_ID_MANU_UNKNOWN, MANUFACTURER),
    LOC_UNKNOWN(YUK_DEF_ID_LOC_UNKNOWN, APP_LOCATION),
    TOW_WEEKDAY(YUK_DEF_ID_TOW_WEEKDAY, TIME_OF_WEEK),
    TOW_WEEKEND(YUK_DEF_ID_TOW_WEEKEND, TIME_OF_WEEK),
    TOW_SATURDAY(YUK_DEF_ID_TOW_SATURDAY, TIME_OF_WEEK),
    TOW_SUNDAY(YUK_DEF_ID_TOW_SUNDAY, TIME_OF_WEEK),
    TOW_MONDAY(YUK_DEF_ID_TOW_MONDAY, TIME_OF_WEEK),
    TOW_TUESDAY(YUK_DEF_ID_TOW_TUESDAY, TIME_OF_WEEK),
    TOW_WEDNESDAY(YUK_DEF_ID_TOW_WEDNESDAY, TIME_OF_WEEK),
    TOW_THURSDAY(YUK_DEF_ID_TOW_THURSDAY, TIME_OF_WEEK),
    TOW_FRIDAY(YUK_DEF_ID_TOW_FRIDAY, TIME_OF_WEEK),
    THERM_MODE_DEFAULT(YUK_DEF_ID_THERM_MODE_DEFAULT, THERMOSTAT_MODE),
    THERM_MODE_COOL(YUK_DEF_ID_THERM_MODE_COOL, THERMOSTAT_MODE),
    THERM_MODE_HEAT(YUK_DEF_ID_THERM_MODE_HEAT, THERMOSTAT_MODE),
    THERM_MODE_OFF(YUK_DEF_ID_THERM_MODE_OFF, THERMOSTAT_MODE),
    THERM_MODE_AUTO(YUK_DEF_ID_THERM_MODE_AUTO, THERMOSTAT_MODE),
    THERM_MODE_EMERGENCY_HEAT(YUK_DEF_ID_THERM_MODE_EMERGENCY_HEAT, THERMOSTAT_MODE),
    FAN_STAT_DEFAULT(YUK_DEF_ID_FAN_STAT_DEFAULT, THERMOSTAT_FAN_STATE),
    FAN_STAT_AUTO(YUK_DEF_ID_FAN_STAT_AUTO, THERMOSTAT_FAN_STATE),
    FAN_STAT_ON(YUK_DEF_ID_FAN_STAT_ON, THERMOSTAT_FAN_STATE),
    FAN_STAT_CIRCULATE(YUK_DEF_ID_FAN_STAT_CIRCULATE, THERMOSTAT_FAN_STATE),
    INV_SEARCH_BY_SERIAL_NO(YUK_DEF_ID_INV_SEARCH_BY_SERIAL_NO, INV_SEARCH_BY),
    INV_SEARCH_BY_ACCT_NO(YUK_DEF_ID_INV_SEARCH_BY_ACCT_NO, INV_SEARCH_BY),
    INV_SEARCH_BY_PHONE_NO(YUK_DEF_ID_INV_SEARCH_BY_PHONE_NO, INV_SEARCH_BY),
    INV_SEARCH_BY_LAST_NAME(YUK_DEF_ID_INV_SEARCH_BY_LAST_NAME, INV_SEARCH_BY),
    INV_SEARCH_BY_ORDER_NO(YUK_DEF_ID_INV_SEARCH_BY_ORDER_NO, INV_SEARCH_BY),
    INV_SEARCH_BY_ADDRESS(YUK_DEF_ID_INV_SEARCH_BY_ADDRESS, INV_SEARCH_BY),
    INV_SEARCH_BY_ALT_TRACK_NO(YUK_DEF_ID_INV_SEARCH_BY_ALT_TRACK_NO, INV_SEARCH_BY),
    INV_SEARCH_BY_METER_NO(YUK_DEF_ID_INV_SEARCH_BY_METER_NO, INV_SEARCH_BY),
    INV_SEARCH_BY_DEVICE_NAME(YUK_DEF_ID_INV_SEARCH_BY_DEVICE_NAME, INV_SEARCH_BY),
    INV_SORT_BY_SERIAL_NO(YUK_DEF_ID_INV_SORT_BY_SERIAL_NO, INV_SORT_BY),
    INV_SORT_BY_INST_DATE(YUK_DEF_ID_INV_SORT_BY_INST_DATE, INV_SORT_BY),
    INV_FILTER_BY_DEV_TYPE(YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE, INV_FILTER_BY),
    INV_FILTER_BY_SRV_COMPANY(YUK_DEF_ID_INV_FILTER_BY_SRV_COMPANY, INV_FILTER_BY),
    INV_FILTER_BY_APPLIANCE_TYPE(YUK_DEF_ID_INV_FILTER_BY_APPLIANCE_TYPE, INV_FILTER_BY),
    INV_FILTER_BY_DEV_STATUS(YUK_DEF_ID_INV_FILTER_BY_DEV_STATUS, INV_FILTER_BY),
    INV_FILTER_BY_MEMBER(YUK_DEF_ID_INV_FILTER_BY_MEMBER, INV_FILTER_BY),
    INV_FILTER_BY_WAREHOUSE(YUK_DEF_ID_INV_FILTER_BY_WAREHOUSE, INV_FILTER_BY),
    INV_FILTER_BY_SERIAL_RANGE_MIN(YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MIN, INV_FILTER_BY),
    INV_FILTER_BY_SERIAL_RANGE_MAX(YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MAX, INV_FILTER_BY),
    INV_FILTER_BY_POSTAL_CODES(YUK_DEF_ID_INV_FILTER_BY_POSTAL_CODES, INV_FILTER_BY),
    INV_FILTER_BY_CUST_TYPE(YUK_DEF_ID_INV_FILTER_BY_CUST_TYPE, INV_FILTER_BY),

    SO_SEARCH_BY_ORDER_NO(YUK_DEF_ID_SO_SEARCH_BY_ORDER_NO, SO_SEARCH_BY),
    SO_SEARCH_BY_ACCT_NO(YUK_DEF_ID_SO_SEARCH_BY_ACCT_NO, SO_SEARCH_BY),
    SO_SEARCH_BY_PHONE_NO(YUK_DEF_ID_SO_SEARCH_BY_PHONE_NO, SO_SEARCH_BY),
    SO_SEARCH_BY_LAST_NAME(YUK_DEF_ID_SO_SEARCH_BY_LAST_NAME, SO_SEARCH_BY),
    SO_SEARCH_BY_SERIAL_NO(YUK_DEF_ID_SO_SEARCH_BY_SERIAL_NO, SO_SEARCH_BY),
    SO_SEARCH_BY_ADDRESS(YUK_DEF_ID_SO_SEARCH_BY_ADDRESS, SO_SEARCH_BY),
    SO_SORT_BY_ORDER_NO(YUK_DEF_ID_SO_SORT_BY_ORDER_NO, SO_SORT_BY),
    SO_SORT_BY_DATE_TIME(YUK_DEF_ID_SO_SORT_BY_DATE_TIME, SO_SORT_BY),
    SO_SORT_BY_SERV_COMP(YUK_DEF_ID_SO_SORT_BY_SERV_COMP, SO_SORT_BY),
    SO_SORT_BY_SERV_TYPE(YUK_DEF_ID_SO_SORT_BY_SERV_TYPE, SO_SORT_BY),
    SO_SORT_BY_SERV_STAT(YUK_DEF_ID_SO_SORT_BY_SERV_STAT, SO_SORT_BY),
    SO_SORT_BY_CUST_TYPE(YUK_DEF_ID_SO_SORT_BY_CUST_TYPE, SO_SORT_BY),
    SO_FILTER_BY_STATUS(YUK_DEF_ID_SO_FILTER_BY_STATUS, SO_FILTER_BY),
    SO_FILTER_BY_SRV_TYPE(YUK_DEF_ID_SO_FILTER_BY_SRV_TYPE, SO_FILTER_BY),
    SO_FILTER_BY_SRV_COMPANY(YUK_DEF_ID_SO_FILTER_BY_SRV_COMPANY, SO_FILTER_BY),
    SO_FILTER_BY_SRV_COMP_CODES(YUK_DEF_ID_SO_FILTER_BY_SRV_COMP_CODES, SO_FILTER_BY),
    SO_FILTER_BY_CUST_TYPE(YUK_DEF_ID_SO_FILTER_BY_CUST_TYPE, SO_FILTER_BY),

    RATE_SCHED_J(YUK_DEF_ID_RATE_SCHED_J, RATE_SCHEDULE),
    RATE_SCHED_PS(YUK_DEF_ID_RATE_SCHED_PS, RATE_SCHEDULE),
    RATE_SCHED_PSO(YUK_DEF_ID_RATE_SCHED_PSO, RATE_SCHEDULE),
    RATE_SCHED_PLS(YUK_DEF_ID_RATE_SCHED_PLS, RATE_SCHEDULE),
    RATE_SCHED_PP(YUK_DEF_ID_RATE_SCHED_PP, RATE_SCHEDULE),
    RATE_SCHED_PT(YUK_DEF_ID_RATE_SCHED_PT, RATE_SCHEDULE),

    SETTLEMENT_HECO(YUK_DEF_ID_SETTLEMENT_HECO, SETTLEMENT_TYPE)
    ;

    private final static String baseKey = "yukon.dr.selectionList.definition.";
    private final static ImmutableMap<Integer, YukonDefinition> byDefinitionId;
    private final static ImmutableMultimap<YukonSelectionListEnum, YukonDefinition> listDefinitions;
    static {
        ImmutableMap.Builder<Integer, YukonDefinition> builder = ImmutableMap.builder();

        ImmutableMultimap.Builder<YukonSelectionListEnum, YukonDefinition> listBuilder = ImmutableMultimap.builder();
        for (YukonDefinition definition : values()) {
            builder.put(definition.definitionId, definition);
            if (definition.relevantList != null) {
                listBuilder.put(definition.relevantList, definition);
            }
        }

        byDefinitionId = builder.build();
        listDefinitions = listBuilder.build();
    }

    private final int definitionId;
    private final YukonSelectionListEnum relevantList;

    private YukonDefinition(int definitionId, YukonSelectionListEnum relevantList) {
        this.definitionId = definitionId;
        this.relevantList = relevantList;
    }

    public static YukonDefinition getById(Integer definitionId) {
        return byDefinitionId.get(definitionId);
    }

    public static ImmutableCollection<YukonDefinition> valuesForList(YukonSelectionListEnum list) {
        return listDefinitions.get(list);
    }

    public YukonSelectionListEnum getRelevantList() {
        return relevantList;
    }

    public int getDefinitionId() {
        return definitionId;
    }

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

    @Override
    public Object getDatabaseRepresentation() {
        return null;
    }
}
