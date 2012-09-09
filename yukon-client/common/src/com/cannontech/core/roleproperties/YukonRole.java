package com.cannontech.core.roleproperties;

import static com.cannontech.core.roleproperties.YukonRoleCategory.Application;
import static com.cannontech.core.roleproperties.YukonRoleCategory.CapControl;
import static com.cannontech.core.roleproperties.YukonRoleCategory.Consumer;
import static com.cannontech.core.roleproperties.YukonRoleCategory.LoadControl;
import static com.cannontech.core.roleproperties.YukonRoleCategory.Notifications;
import static com.cannontech.core.roleproperties.YukonRoleCategory.Operator;
import static com.cannontech.core.roleproperties.YukonRoleCategory.System;

import org.apache.commons.lang.Validate;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.roles.ApplicationRoleDefs;
import com.cannontech.roles.CapControlRoleDefs;
import com.cannontech.roles.ConsumerRoleDefs;
import com.cannontech.roles.LMRoleDefs;
import com.cannontech.roles.NotificationsRoleDefs;
import com.cannontech.roles.OperatorRoleDefs;
import com.cannontech.roles.YukonRoleDefs;
import com.cannontech.roles.capcontrol.CBCOnelineSettingsRole;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Ordering;

public enum YukonRole implements DisplayableEnum, DatabaseRepresentationSource {
    APPLICATION_BILLING(Application, ApplicationRoleDefs.BILLING_ROLEID),
    COMMANDER(Application, ApplicationRoleDefs.COMMANDER_ROLEID),
    DATABASE_EDITOR(Application, ApplicationRoleDefs.DATABASE_EDITOR_ROLEID),
    APPLICATION_ESUBSTATION_EDITOR(Application, ApplicationRoleDefs.ESUBSTATION_EDITOR_ROLEID),
    PASSWORD_POLICY(Application, ApplicationRoleDefs.PASSWORD_POLICY_ROLEID),
    REPORTING(Application, ApplicationRoleDefs.REPORTING_ROLEID),
    TABULAR_DISPLAY_CONSOLE(Application, ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_ROLEID),
    TRENDING(Application, ApplicationRoleDefs.TRENDING_ROLEID),
    WEB_CLIENT(Application, ApplicationRoleDefs.WEB_CLIENT_ROLEID),
    
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
    ENERGY_COMPANY(Operator, YukonRoleDefs.ENERGY_COMPANY_ROLEID),
    OPERATOR_ESUBSTATION_DRAWINGS(Operator, OperatorRoleDefs.ESUBSTATION_DRAWINGS_ROLEID),
    INVENTORY(Operator, OperatorRoleDefs.INVENTORY_ROLEID),
    METERING(Operator, OperatorRoleDefs.METERING_ROLEID),
    ODDS_FOR_CONTROL(Operator, OperatorRoleDefs.ODDS_FOR_CONTROL_ROLEID),
    SCHEDULER(Operator, OperatorRoleDefs.SCHEDULER_ROLEID),
    WORK_ORDER(Operator, OperatorRoleDefs.WORK_ORDER_ROLEID),
    
    AUTHENTICATION(System, YukonRoleDefs.AUTHENTICATION_ROLEID),
    SYSTEM_BILLING(System, YukonRoleDefs.BILLING_ROLEID),
    CALC_HISTORICAL(System, ApplicationRoleDefs.CALC_HISTORICAL_ROLEID),
    SYSTEM_CONFIGURATION(System, YukonRoleDefs.CONFIGURATION_ROLEID),
    MULTISPEAK(System, YukonRoleDefs.MULTISPEAK_ROLEID),
    VOICE_SERVER(System, YukonRoleDefs.VOICE_SERVER_ROLEID),
    WEB_GRAPH(System, ApplicationRoleDefs.WEB_GRAPH_ROLEID),
    SYSTEM(System, YukonRoleDefs.SYSTEM_ROLEID),
    ;

    private final YukonRoleCategory category;
    private final int roleId;

    public final static Function<YukonRole, String> CATEGORY_FUNCTION = new Function<YukonRole, String>() {
        @Override
        public String apply(YukonRole input) {
            if (input == null) {
                return null;
            }
            return input.getCategory().name();
        }
    };
    public final static Function<YukonRole, String> NAME_FUNCTION = new Function<YukonRole, String>() {
        @Override
        public String apply(YukonRole input) {
            return input.name();
        }
    };

    public final static Ordering<YukonRole> CATEGORY_COMPARATOR = Ordering.natural().nullsFirst().onResultOf(CATEGORY_FUNCTION);
    public final static Ordering<YukonRole> CATEGORY_AND_NAME_COMPARATOR = CATEGORY_COMPARATOR.compound(Ordering.natural().nullsFirst().onResultOf(NAME_FUNCTION));

    private final static ImmutableMultimap<YukonRoleCategory, YukonRole> lookupByCategory;
    static {
        ImmutableMultimap.Builder<YukonRoleCategory, YukonRole>  builder = ImmutableMultimap.builder();
        for (YukonRole role : values()) {
            builder.put(role.getCategory(), role);
        }
        lookupByCategory = builder.build();
    }

    private final static ImmutableMap<Integer, YukonRole> lookup;
    
    static {
        Builder<Integer, YukonRole> builder = ImmutableMap.builder();
        for (YukonRole yukonRole : values()) {
            builder.put(yukonRole.roleId, yukonRole);
        }
        lookup = builder.build();
    }

    private YukonRole(YukonRoleCategory category, int roleId) {
        this.category = category;
        this.roleId = roleId;
    }
    
    public YukonRoleCategory getCategory() {
        return category;
    }
    
    public static YukonRole getForId(int roleId) throws IllegalArgumentException {
        YukonRole yukonRole = lookup.get(roleId);
        Validate.notNull(yukonRole);
        return yukonRole;
    }
    
    public static ImmutableCollection<YukonRole> getForCategory(YukonRoleCategory category) throws IllegalArgumentException {
        ImmutableCollection<YukonRole> rolesInCategory = lookupByCategory.get(category);
        return rolesInCategory;
    }
    
    public int getRoleId() {
        return roleId;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.role." + name();
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return roleId;
    }
    
    @Override
    public String toString() {
        return name()+" ("+category+") ";
    }
}