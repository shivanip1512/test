package com.cannontech.core.roleproperties;

import static com.cannontech.core.roleproperties.YukonRoleCategory.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Ordering;

public enum YukonRole implements DisplayableEnum, DatabaseRepresentationSource {

    // Yukon Grp associated roles moved to GlobalSettings 
    APPLICATION_BILLING(Application, Application.baseRoleId - 6, Application.basePropertyId - 600),
    COMMANDER(Application, Application.baseRoleId - 3, Application.basePropertyId - 300),
    DATABASE_EDITOR(Application, Application.baseRoleId, Application.basePropertyId),
    APPLICATION_ESUBSTATION_EDITOR(Application, Application.baseRoleId - 7, Application.basePropertyId - 700),
    PASSWORD_POLICY(Application, Application.baseRoleId - 10, Application.basePropertyId - 1000),
    REPORTING(Application, Application.baseRoleId - 9, Application.basePropertyId - 900),
    TABULAR_DISPLAY_CONSOLE(Application, Application.baseRoleId - 1, Application.basePropertyId - 100),
    TRENDING(Application, Application.baseRoleId -2, Application.basePropertyId - 200),
    WEB_CLIENT(Application, Application.baseRoleId - 8, Application.basePropertyId - 800),
    
    CBC_SETTINGS(CapControl, CapControl.baseRoleId, CapControl.basePropertyId),
    // The below are a little off. We've merged the Cbc_Oneline "category" into the CapControl category...however, we need to retain the original roleIds.
    CBC_ONELINE_SUB_SETTINGS(CapControl, Cbc_Oneline.baseRoleId, Cbc_Oneline.basePropertyId),
    CBC_ONELINE_FEEDER_SETTINGS(CapControl, Cbc_Oneline.baseRoleId - 1, Cbc_Oneline.basePropertyId - 100),
    CBC_ONELINE_CAP_SETTINGS(CapControl, Cbc_Oneline.baseRoleId - 2, Cbc_Oneline.basePropertyId - 200),

    
    RESIDENTIAL_CUSTOMER(Consumer, Consumer.baseRoleId, Consumer.basePropertyId),
    
    DEMAND_RESPONSE(DemandResponse, DemandResponse.baseRoleId, DemandResponse.basePropertyId),
    
    IVR(Notifications, Notifications.baseRoleId, Notifications.basePropertyId),
    NOTIFICATION_CONFIGURATION(Notifications, Notifications.baseRoleId - 1, Notifications.basePropertyId - 100),
    
    OPERATOR_ADMINISTRATOR(Operator, Operator.baseRoleId, Operator.basePropertyId),
    CI_CURTAILMENT(Operator, Operator.baseRoleId - 11, Operator.basePropertyId - 1100),
    
    /*This one was apparently extended to have two spaces for property IDs, hence the ArrayList implementation
     * of basePropertyID.
     */
    CONSUMER_INFO(Operator, Operator.baseRoleId - 1, Operator.basePropertyId - 100, Operator.basePropertyId - 800),
    METERING(Operator, Operator.baseRoleId - 2, Operator.basePropertyId - 200),
    OPERATOR_ESUBSTATION_DRAWINGS(Operator, Operator.baseRoleId - 6, Operator.basePropertyId - 600),
    ODDS_FOR_CONTROL(Operator, Operator.baseRoleId - 7, Operator.basePropertyId - 700),
    INVENTORY(Operator, Operator.baseRoleId - 9, Operator.basePropertyId - 900),
    WORK_ORDER(Operator, Operator.baseRoleId - 10, Operator.basePropertyId - 1000),
    SCHEDULER(Operator, Operator.baseRoleId - 12, Operator.basePropertyId - 1200),
    DEVICE_ACTIONS(Operator, Operator.baseRoleId - 13, Operator.basePropertyId - 1300),
    DEVICE_MANAGEMENT(Operator, Operator.baseRoleId - 14, Operator.basePropertyId - 1400),
    ;

    private final YukonRoleCategory category;
    private final int roleId;
    private List<Integer> baseRolePropertyIds;

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

    /*
     * Support splitting of baseRolePropertyIDs into chunks, Used by
     * CONSUMER_INFO.
     */
    private YukonRole(YukonRoleCategory category, int roleId, int ... baseRolePropertyIds) {
        this.category = category;
        this.roleId = roleId;
        this.baseRolePropertyIds = new ArrayList<Integer>(1);
        for(int i = 0; i < baseRolePropertyIds.length; i++){
            this.baseRolePropertyIds.add(baseRolePropertyIds[i]);
        }
    }
    
    public YukonRoleCategory getCategory() {
        return category;
    }
    
    public int getBasePropertyId(){
        return this.getBasePropertyId(0);
    }
    
    public int getBasePropertyId(int i){
        return this.baseRolePropertyIds.get(i);
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