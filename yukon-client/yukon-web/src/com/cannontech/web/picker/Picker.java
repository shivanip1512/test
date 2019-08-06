package com.cannontech.web.picker;

import java.util.Collection;
import java.util.List;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Override this class to implement a picker.  Instances of implementing
 * classes create pickers which can be used via the various picker tags or
 * the Javascript Picker class.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({ 
    @JsonSubTypes.Type(value = AppCatFormulaPicker.class, name = "APP_CAT_FORMULA_PICKER"),
    @JsonSubTypes.Type(value = AssignedProgramPicker.class, name = "ASSIGNED_PROGRAM_PICKER"),
    @JsonSubTypes.Type(value = AvailableCapBankPicker.class, name = "AVAILABLE_CAPBANK_PICKER"),
    @JsonSubTypes.Type(value = AvailableLmHardwarePicker.class, name = "AVAILABLE_LMHARDWARE_PICKER"),
    @JsonSubTypes.Type(value = AvailableMctPicker.class, name = "AVAILABLE_MCT_PICKER"),
    @JsonSubTypes.Type(value = AvailableVoltageRegulatorGangPicker.class, name = "AVAILABLE_VOLTAGE_REGULATOR_GANG_PICKER"),
    @JsonSubTypes.Type(value = AvailableVoltageRegulatorPhasePicker.class, name = "AVAILABLE_VOLTAGE_REGULATOR_PHASE_PICKER"),
    @JsonSubTypes.Type(value = AvailableLoadProgramPicker.class, name = "AVAILABLE_LOAD_PROGRAM_PICKER"),
    @JsonSubTypes.Type(value = BasePicker.class, name = "BASE_PICKER"),
    @JsonSubTypes.Type(value = CapControlCBCOrphanPicker.class, name = "CAPCONTROL_CBC_ORPHAN_PICKER"),
    @JsonSubTypes.Type(value = CapControlSubstationBusPicker.class, name = "CAPCONTROL_SUBSTATION_BUS_PICKER"),
    @JsonSubTypes.Type(value = ConfigurationCategoryPicker.class, name = "CONFIGURATION_CATEGORY_PICKER"),
    @JsonSubTypes.Type(value = CustomerAccountPicker.class, name = "CUSTOMER_ACCOUNT_PICKER"),
    @JsonSubTypes.Type(value = DashboardUsersPicker.class, name = "DASHBOARD_USERS_PICKER"),
    @JsonSubTypes.Type(value = DatabaseMigrationPicker.class, name = "DATABASE_MIGRATION_PICKER"),
    @JsonSubTypes.Type(value = DatabasePicker.class, name = "DATABASE_PICKER"),
    @JsonSubTypes.Type(value = DeviceActionsRoleUserPicker.class, name = "DEVICE_ACTIONS_ROLE_USER_PICKER"),
    @JsonSubTypes.Type(value = DmvTestPicker.class, name = "DMV_TEST_PICKER"),
    @JsonSubTypes.Type(value = DrUntrackedMctPicker.class, name = "DR_UNTRACKED_MCT_PICKER"),
    @JsonSubTypes.Type(value = ECOperatorCandidatePicker.class, name = "EC_OPERATOR_CANDIDATE_PICKER"),
    @JsonSubTypes.Type(value = FilterablePointPicker.class, name = "FILTERABLE_POINT_PICKER"),
    @JsonSubTypes.Type(value = GearFormulaPicker.class, name = "GEAR_FORMULA_PICKER"),
    @JsonSubTypes.Type(value = LmHardwareBasePicker.class, name = "LM_HARDWARE_BASE_PICKER"),
    @JsonSubTypes.Type(value = LmProgramPicker.class, name = "LM_PROGRAM_PICKER"),
    @JsonSubTypes.Type(value = LoginGroupPicker.class, name = "LOGIN_GROUP_PICKER"),
    @JsonSubTypes.Type(value = LucenePicker.class, name = "LUCENE_PICKER"),
    @JsonSubTypes.Type(value = MonitorPicker.class, name = "MONITOR_PICKER"),
    @JsonSubTypes.Type(value = NonResidentailUserPicker.class, name = "NON_RESIDENTIAL_USER_PICKER"),
    @JsonSubTypes.Type(value = NotificationGroupPicker.class, name = "NOTIFICATION_GROUP_PICKER"),
    @JsonSubTypes.Type(value = PaoPermissionCheckingPicker.class, name = "PAO_PERMISSION_CHECKING_PICKER"),
    @JsonSubTypes.Type(value = PaoPicker.class, name = "PAO_PICKER"),
    @JsonSubTypes.Type(value = PointPicker.class, name = "POINT_PICKER"),
    @JsonSubTypes.Type(value = RegulatorPicker.class, name = "REGULATOR_PICKER"),
    @JsonSubTypes.Type(value = SurveyPicker.class, name = "SURVEY_PICKER"),
    @JsonSubTypes.Type(value = TrendPicker.class, name = "TREND_PICKER"),
    @JsonSubTypes.Type(value = UnassignedApplianceCategoryPicker.class, name = "UNASSIGNED_APPIANCE_CATEGORY_PICKER"),
    @JsonSubTypes.Type(value = UnassignedGearPicker.class, name = "UNASSIGNED_GEAR_PICKER"),
    @JsonSubTypes.Type(value = UnassignedGearPicker.class, name = "UNASSIGNED_PROGRAM_PICKER"),
    @JsonSubTypes.Type(value = UserGroupPicker.class, name = "USER_GROUP_PICKER"),
    @JsonSubTypes.Type(value = UserPicker.class, name = "USER_PICKER"),

    })
public interface Picker<T> {

    /**
     * @return the name of the field which is to be treated as the id.
     */
    public String getIdFieldName();

    @JsonDeserialize(as = YukonMessageSourceResolvable.class)
    public MessageSourceResolvable getDialogTitle();

    /**
     * @return an array of instances of OutputColumn where each instance
     *         represents a column in the results displayed to the user.
     */
    @JsonIgnore
    public List<OutputColumn> getOutputColumns();

    /**
     * Search for data matching ss.
     * @param ss The query string entered. If this is null or empty, the query
     *            should return any valid matches.
     * @param start The zero-based index of the first match to return.
     * @param count The maximum number of matches to return. The size of the
     *            return value should be less than or equal to count.
     * @param extraArgs optional string which comes from the "extraArgs" argument to the 
     * @return An instance of SearchResult populated appropriately.
     */
    public SearchResults<T> search(String ss, int start, int count,
            String extraArgs, YukonUserContext userContext);

    /**
     * Perform a search for the specific ids requested.  This is used to
     * populate the picker if there were previously saved results.
     */
    public SearchResults<T> search(Collection<Integer> initialIds, String extraArgs, YukonUserContext userContext);
}
