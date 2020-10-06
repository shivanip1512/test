package com.cannontech.web.api;

public class ApiURL {

    public static final String drLoadGroupSaveUrl = "/dr/setup/loadGroup/create";
    public static final String drLoadGroupRetrieveUrl = "/dr/setup/loadGroup/";
    public static final String drLoadGroupDeleteUrl = "/dr/setup/loadGroup/delete/";
    public static final String drLoadGroupCopyUrl = "/dr/setup/loadGroup/copy/";
    public static final String drLoadGroupUpdateUrl = "/dr/setup/loadGroup/update/";
    public static final String drRetrieveAvailableLoadGroupsUrl = "/dr/setup/loadGroup/availableLoadGroup";
    public static final String drPointGroupStartStateUrl = "/dr/setup/loadGroup/getPointGroupStartState/";

    public static final String drSetupFilterUrl = "/dr/setup/filter";
    public static final String retrieveAllRoutesUrl = "/core/setup/route/allRoutes";
    public static final String pickerBuildUrl = "/picker/build/";
    public static final String pickerSearchUrl = "/picker/search";
    public static final String pickerIdSearchUrl = "/picker/idSearch";

    public static final String drMacroLoadGroupUrl = "/dr/macroLoadGroups";

    public static final String drLoadProgramUrl = "/dr/loadPrograms";
    public static final String drAvailableLoadGroupsUrl = "/dr/loadPrograms/availableLoadGroups/";
    public static final String drAvailableNotificationGroupsUrl = "/dr/loadPrograms/availableNotificationGroups/";
    public static final String drAvailableDirectMemberControlsUrl = "/dr/loadPrograms/availableDirectMemberControls/";
    public static final String drLoadProgramAvailableProgramsUrl = "/dr/loadPrograms/availablePrograms";
    public static final String drGetGearsForLoadProgram = "/dr/loadPrograms/getGearsForProgram/";
    public static final String drGearRetrieveUrl = "/dr/loadPrograms/gear/";

    public static final String drAllProgramConstraintUrl = "/dr/constraints/getAllProgramConstraint";
    public static final String drProgramConstraintUrl = "/dr/constraints";
    public static final String drHolidayScheduleUrl = "/dr/constraints/getHolidaySchedules";
    public static final String drSeasonScheduleUrl = "/dr/constraints/getSeasonSchedules";

    public static final String drControlAreaUrl = "/dr/controlAreas";
    public static final String drNormalStateUrl = "/dr/controlAreas/normalState/";

    public static final String drControlScenarioUrl = "/dr/controlScenarios";

    public static final String commChannelUrl = "/devices/commChannels";

    public static final String virtualDeviceUrl = "/device/virtualDevices";

    public static final String trendUrl = "/trends";
    
    public static final String attributeUrl = "/attributes";
    public static final String attributeAssignmentsUrl = "/attributeAssignments";
    
    public static final String aggregateDataReportUrl = "/aggregateIntervalDataReport/report";

}
