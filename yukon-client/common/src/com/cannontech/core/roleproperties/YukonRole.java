package com.cannontech.core.roleproperties;

import org.apache.commons.lang.Validate;

import com.cannontech.roles.ApplicationRoleDefs;
import com.cannontech.roles.CICustomerRoleDefs;
import com.cannontech.roles.CapControlRoleDefs;
import com.cannontech.roles.ConsumerRoleDefs;
import com.cannontech.roles.LMRoleDefs;
import com.cannontech.roles.NotificationsRoleDefs;
import com.cannontech.roles.OperatorRoleDefs;
import com.cannontech.roles.YukonRoleDefs;
import com.cannontech.roles.capcontrol.CBCOnelineSettingsRole;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import static com.cannontech.core.roleproperties.YukonRoleCategory.*;

public enum YukonRole {
    APPLICATION_BILLING(Application, ApplicationRoleDefs.BILLING_ROLEID),
    CALC_HISTORICAL(Application, ApplicationRoleDefs.CALC_HISTORICAL_ROLEID),
    COMMANDER(Application, ApplicationRoleDefs.COMMANDER_ROLEID),
    DATABASE_EDITOR(Application, ApplicationRoleDefs.DATABASE_EDITOR_ROLEID),
    APPLICATION_ESUBSTATION_EDITOR(Application, ApplicationRoleDefs.ESUBSTATION_EDITOR_ROLEID),
    REPORTING(Application, ApplicationRoleDefs.REPORTING_ROLEID),
    TABULAR_DISPLAY_CONSOLE(Application, ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_ROLEID),
    TRENDING(Application, ApplicationRoleDefs.TRENDING_ROLEID),
    WEB_CLIENT(Application, ApplicationRoleDefs.WEB_CLIENT_ROLEID),
    WEB_GRAPH(Application, ApplicationRoleDefs.WEB_GRAPH_ROLEID),
    CI_ESUBSTATION_DRAWINGS(CiCustomer, CICustomerRoleDefs.ESUBSTATION_DRAWINGS_ROLEID),
    USER_CONTROL(CiCustomer, CICustomerRoleDefs.USER_CONTROL_ROLEID),
    CBC_SETTINGS(CapControl, CapControlRoleDefs.CBC_SETTINGS_ROLEID),
    CBC_ONELINE_CAP_SETTINGS(CapControl, CBCOnelineSettingsRole.CAP_ROLEID),
    CBC_ONELINE_FEEDER_SETTINGS(CapControl, CBCOnelineSettingsRole.FDR_ROLEID),
    CBC_ONELINE_SUB_SETTINGS(CapControl, CBCOnelineSettingsRole.SUB_ROLEID),
    RESIDENTIAL_CUSTOMER(Consumer, ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_ROLEID),
    LM_DIRECT_LOADCONTROL(LoadControl, LMRoleDefs.DIRECT_LOADCONTROL_ROLEID),
    NOTIFICATION_CONFIGURATION(Notifications, NotificationsRoleDefs.CONFIGURATION_ROLEID),
    IVR(Notifications, NotificationsRoleDefs.IVR_ROLEID),
    OPERATOR_ADMINISTRATOR(Operator, OperatorRoleDefs.ADMINISTRATOR_ROLEID),
    CI_CURTAILMENT(Operator, OperatorRoleDefs.CI_CURTAILMENT_ROLEID),
    CONSUMER_INFO(Operator, OperatorRoleDefs.CONSUMER_INFO_ROLEID),
    DEVICE_ACTIONS(Operator, OperatorRoleDefs.DEVICE_ACTIONS_ROLEID),
    OPERATOR_ESUBSTATION_DRAWINGS(Operator, OperatorRoleDefs.ESUBSTATION_DRAWINGS_ROLEID),
    INVENTORY(Operator, OperatorRoleDefs.INVENTORY_ROLEID),
    METERING(Operator, OperatorRoleDefs.METERING_ROLEID),
    ODDS_FOR_CONTROL(Operator, OperatorRoleDefs.ODDS_FOR_CONTROL_ROLEID),
    SCHEDULER(Operator, OperatorRoleDefs.SCHEDULER_ROLEID),
    WORK_ORDER(Operator, OperatorRoleDefs.WORK_ORDER_ROLEID),
    AUTHENTICATION(System, YukonRoleDefs.AUTHENTICATION_ROLEID),
    SYSTEM_BILLING(System, YukonRoleDefs.BILLING_ROLEID),
    SYSTEM_CONFIGURATION(System, YukonRoleDefs.CONFIGURATION_ROLEID),
    ENERGY_COMPANY(System, YukonRoleDefs.ENERGY_COMPANY_ROLEID),
    MULTISPEAK(System, YukonRoleDefs.MULTISPEAK_ROLEID),
    SYSTEM(System, YukonRoleDefs.SYSTEM_ROLEID),
    VOICE_SERVER(System, YukonRoleDefs.VOICE_SERVER_ROLEID),
    ;
    
    private final YukonRoleCategory category;
    private final int roleId;

    private YukonRole(YukonRoleCategory category, int roleId) {
        this.category = category;
        this.roleId = roleId;
    }
    
    public YukonRoleCategory getCategory() {
        return category;
    }
    
    private final static ImmutableMap<Integer, YukonRole> lookup;
    
    static {
        Builder<Integer, YukonRole> builder = ImmutableMap.builder();
        for (YukonRole yukonRole : values()) {
            builder.put(yukonRole.roleId, yukonRole);
        }
        lookup = builder.build();
    }
    
    public static YukonRole getForId(int roleId) {
        YukonRole yukonRole = lookup.get(roleId);
        Validate.notNull(yukonRole);
        return yukonRole;
    }

}
