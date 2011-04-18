package com.cannontech.common.constants;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;

public enum SelectionListCategory implements DisplayableEnum {
    SYSTEM( YukonRole.INVENTORY, YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES,
           YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE),
    HARDWARE(YukonRole.INVENTORY, YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES,
             YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE),
    CALL_TRACKING(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ACCOUNT_CALL_TRACKING),
    SERVICE_ORDER(YukonRole.WORK_ORDER,
                  YukonRoleProperty.OPERATOR_CONSUMER_INFO_WORK_ORDERS),
    CONTROL(YukonRole.ODDS_FOR_CONTROL),
    THERMOSTAT,
    ACCOUNT(YukonRole.INVENTORY, YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES,
            YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE),
    CAP_CONTROL,
    APPLIANCE(null, YukonRoleProperty.OPERATOR_CONSUMER_INFO_APPLIANCES,
              YukonRoleProperty.OPERATOR_CONSUMER_INFO_APPLIANCES_CREATE),
    APPLIANCE_AIR_CONDITIONING(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER),
    APPLIANCE_WATER_HEATER(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER),
    APPLIANCE_DUEL_FUEL(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL),
    APPLIANCE_GRAIN_DRYER(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER),
    APPLIANCE_STORAGE_HEAT(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT),
    APPLIANCE_HEAT_PUMP(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP),
    APPLIANCE_IRRIGATION(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION),
    APPLIANCE_GENERATOR(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR),
    RESIDENCE(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ACCOUNT_RESIDENCE);

    private final static String keyPrefix = "yukon.dr.selectionListCategory.";
    private final Integer listEntryType;
    private final YukonRole role;
    private final YukonRoleProperty[] roleProperties;

    private SelectionListCategory() {
        this.listEntryType = null;
        this.role = null;
        this.roleProperties = new YukonRoleProperty[] {};
    }

    private SelectionListCategory(YukonRole role, YukonRoleProperty... roleProperties) {
        this.listEntryType = null;
        this.role = role;
        this.roleProperties = roleProperties;
    }
    
    private SelectionListCategory(Integer listEntryType) {
        this.listEntryType = listEntryType;
        this.role = null;
        this.roleProperties = new YukonRoleProperty[] {};
    }

    private SelectionListCategory(YukonRoleProperty roleProperty) {
        this(null, roleProperty);
    }

    public Integer getListEntryType() {
        return listEntryType;
    }

    public YukonRole getRole() {
        return listEntryType == null ? role : APPLIANCE.role;
    }

    public YukonRoleProperty[] getRoleProperties() {
        return listEntryType == null ? roleProperties : APPLIANCE.roleProperties;
    }

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
