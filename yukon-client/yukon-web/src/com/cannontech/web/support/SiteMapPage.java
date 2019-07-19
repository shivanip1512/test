package com.cannontech.web.support;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.userpage.model.SiteMapCategory;
import com.cannontech.common.util.MatchStyle;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.system.GlobalSettingType;

public enum SiteMapPage implements DisplayableEnum {
    METERING(SiteMapCategory.AMI, "yukon.web.modules.amr.meteringStart.pageName", "/meter/start",
            MatchStyle.any, YukonRole.METERING, YukonRole.APPLICATION_BILLING, YukonRole.SCHEDULER, YukonRole.DEVICE_ACTIONS),
    BILLING(SiteMapCategory.AMI, "yukon.web.menu.config.amr.billing", "/billing/home",
            MatchStyle.all, YukonRole.APPLICATION_BILLING),
    TRENDS(SiteMapCategory.AMI, "yukon.web.modules.tools.trends.pageName", "/tools/trends",
            MatchStyle.all, YukonRole.TRENDING),
    AMI_REPORTS(SiteMapCategory.AMI, "yukon.web.menu.config.reporting.reports.metering", "/analysis/Reports.jsp?groupType=METERING",
            MatchStyle.all, YukonRoleProperty.AMR_REPORTS_GROUP),
    ARCHIVE_DATA_ANALYSIS(SiteMapCategory.AMI, "yukon.web.modules.tools.bulk.analysis.home.pageName", "/bulk/archiveDataAnalysis/list/view",
            MatchStyle.all, YukonRoleProperty.ARCHIVED_DATA_ANALYSIS),
    PHASE_DETECT(SiteMapCategory.AMI, "yukon.web.modules.amr.phaseDetect.home.pageDescription", "/amr/phaseDetect/home",
            MatchStyle.all, YukonRoleProperty.PHASE_DETECT),
    METER_EVENTS_REPORT(SiteMapCategory.AMI, "yukon.web.modules.amr.meterEventsReport.report.pageName", "/amr/meterEventsReport/selectDevices",
            MatchStyle.all, YukonRoleProperty.METER_EVENTS),
    WATER_LEAK_REPORT(SiteMapCategory.AMI, "yukon.web.modules.amr.waterLeakReport.report.pageName", "/amr/waterLeakReport/report",
            MatchStyle.all, YukonRoleProperty.WATER_LEAK_REPORT),
    USAGE_THRESHOLD_REPORT(SiteMapCategory.AMI, "yukon.web.modules.amr.usageThresholdReport.report.pageName", "/amr/usageThresholdReport/report",
                      MatchStyle.all, YukonRoleProperty.USAGE_THRESHOLD_REPORT),
    BILLING_SCHEDULES(SiteMapCategory.AMI, "yukon.web.menu.config.amr.billing.schedules", "/billing/schedules",
            MatchStyle.all, YukonRole.APPLICATION_BILLING),
    REVIEW_FLAGGED_POINTS(SiteMapCategory.AMI, "yukon.web.widgets.validationMonitorsWidget.review", "/amr/veeReview/home",
            MatchStyle.all, YukonRoleProperty.VALIDATION_ENGINE),
    MANAGE_DASHBOARDS(SiteMapCategory.AMI, "yukon.web.modules.dashboard.manageDashboards", "/dashboards/manage",
            MatchStyle.all),
    BULK_IMPORT(SiteMapCategory.AMI, "yukon.web.modules.tools.bulk.importUpload.pageName", "/bulk/import/upload",
            MatchStyle.all, YukonRoleProperty.BULK_IMPORT_OPERATION),
    BULK_UPDATE(SiteMapCategory.AMI, "yukon.web.modules.tools.bulk.updateUpload.pageName", "/bulk/update/upload",
            MatchStyle.all, YukonRoleProperty.BULK_UPDATE_OPERATION),
    AMI_POINT_IMPORT(SiteMapCategory.AMI, "yukon.web.modules.tools.bulk.pointImport.pageName", "/bulk/pointImport/upload",
                 MatchStyle.all, YukonRoleProperty.ADD_REMOVE_POINTS),

    DR_DASHBOARD(SiteMapCategory.DR, "yukon.web.modules.dr.home.pageName", "/dr/home",
            MatchStyle.all, YukonRole.DEMAND_RESPONSE),
    CONTROL_AREAS(SiteMapCategory.DR, "yukon.web.modules.dr.controlAreaList.pageName", "/dr/controlArea/list",
            MatchStyle.all, YukonRoleProperty.SHOW_CONTROL_AREAS),
    SCENARIOS(SiteMapCategory.DR, "yukon.web.modules.dr.scenarioList.pageName", "/dr/scenario/list",
            MatchStyle.all, YukonRoleProperty.SHOW_SCENARIOS),
    PROGRAMS(SiteMapCategory.DR, "yukon.web.modules.dr.programList.pageName", "/dr/program/list",
            MatchStyle.all, YukonRole.DEMAND_RESPONSE),
    LOAD_GROUPS(SiteMapCategory.DR, "yukon.web.modules.dr.loadGroupList.pageName", "/dr/loadGroup/list",
            MatchStyle.all, YukonRole.DEMAND_RESPONSE),
    DR_REPORTS(SiteMapCategory.DR, "yukon.web.menu.config.reporting.reports.management", "/analysis/Reports.jsp?groupType=LOAD_MANAGEMENT",
            MatchStyle.all, YukonRoleProperty.LOAD_MANAGEMENT_REPORTS_GROUP),
    CI_CURTAILMENT(SiteMapCategory.DR, "yukon.web.menu.dr.ci", "/dr/cc/home",
            MatchStyle.all, YukonRoleProperty.CURTAILMENT_IS_OPERATOR),
//    NEST_SYNC(SiteMapCategory.DR, "yukon.web.menu.dr.nestSync", "/dr/nest",
//                   MatchStyle.all, GlobalSettingType.NEST_USERNAME),
    ODDS_FOR_CONTROL(SiteMapCategory.DR, "yukon.web.oddsForControl", "/operator/Consumer/Odds.jsp",
            MatchStyle.all, YukonRole.ODDS_FOR_CONTROL),
    ACTIVE_CONTROL_AREAS(SiteMapCategory.DR, "yukon.web.modules.dr.home.activeControlAreasQuickSearch", "/dr/controlArea/list?state=active",
            MatchStyle.all, YukonRoleProperty.SHOW_CONTROL_AREAS),
    ACTIVE_PROGRAMS(SiteMapCategory.DR, "yukon.web.modules.dr.home.activeProgramsQuickSearch", "/dr/program/list?state=ACTIVE",
            MatchStyle.all, YukonRole.DEMAND_RESPONSE),
    ACTIVE_LOAD_GROUPS(SiteMapCategory.DR, "yukon.web.modules.dr.home.activeLoadGroupsQuickSearch", "/dr/loadGroup/list?state=active",
            MatchStyle.all, YukonRole.DEMAND_RESPONSE),
    ESTIMATED_LOAD(SiteMapCategory.DR, "yukon.web.modules.dr.estimatedLoad.pageName", "/dr/estimatedLoad/home",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.ENABLE_ESTIMATED_LOAD, YukonRole.DEMAND_RESPONSE),
    DR_BULK_UPDATE(SiteMapCategory.DR, "yukon.web.modules.tools.bulk.updateUpload.pageName", "/bulk/update/upload",
                MatchStyle.all, YukonRoleProperty.BULK_UPDATE_OPERATION),
    
    AREAS(SiteMapCategory.VV, "yukon.web.modules.capcontrol.areas.pageName", "/capcontrol/tier/areas",
            MatchStyle.all, YukonRoleProperty.CAP_CONTROL_ACCESS),
    IVVC_SCHEDULES(SiteMapCategory.VV, "yukon.web.modules.capcontrol.schedules.pageName", "/capcontrol/schedules",
            MatchStyle.all, YukonRoleProperty.CAP_CONTROL_ACCESS),
    STRATEGIES(SiteMapCategory.VV, "yukon.web.modules.capcontrol.strategies.pageName", "/capcontrol/strategies",
            MatchStyle.all, YukonRoleProperty.CAP_CONTROL_ACCESS),
    MOVED_CAP_BANKS(SiteMapCategory.VV, "yukon.web.modules.capcontrol.movedCapBanks.pageName", "/capcontrol/move/movedCapBanks",
            MatchStyle.all, YukonRoleProperty.CAP_CONTROL_ACCESS),
    ORPHANS(SiteMapCategory.VV, "yukon.web.menu.config.capcontrol.orphans", "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubstations__",
            MatchStyle.all, YukonRoleProperty.CAP_CONTROL_ACCESS),
    IMPORT(SiteMapCategory.VV, "yukon.web.modules.capcontrol.import.pageTitle", "/capcontrol/import/view",
            MatchStyle.all, YukonRoleProperty.CAP_CONTROL_ACCESS, YukonRoleProperty.CAP_CONTROL_IMPORTER),
    IVVC_REPORTS(SiteMapCategory.VV, "yukon.web.menu.config.reporting.reports.capcontrol", "/analysis/Reports.jsp?groupType=CAP_CONTROL",
            MatchStyle.all, YukonRoleProperty.CAP_CONTROL_REPORTS_GROUP),
    POINT_IMPORT(SiteMapCategory.VV, "yukon.web.modules.tools.bulk.pointImport.pageName", "/bulk/pointImport/upload",
             MatchStyle.all, YukonRoleProperty.ADD_REMOVE_POINTS),
    FDR_TRANSLATIONS(SiteMapCategory.VV, "yukon.web.modules.tools.bulk.fdrTranslationManagement.pageName", "/bulk/fdrTranslationManager/home",
             MatchStyle.all, YukonRoleProperty.FDR_TRANSLATION_MANAGER),
    
    CREATE_ACCOUNT(SiteMapCategory.ASSETS, "yukon.web.modules.operator.account.CREATE.pageName", "/stars/operator/account/accountCreate",
            MatchStyle.all, OtherPermission.EC_OPERATOR, YukonRoleProperty.OPERATOR_NEW_ACCOUNT_WIZARD),
    ACCOUNTS_AND_INVENTORY(SiteMapCategory.ASSETS, "yukon.web.modules.operator.inventory.home.pageName", "/stars/operator/inventory/home",
            MatchStyle.any, YukonRole.INVENTORY, YukonRole.CONSUMER_INFO),
    OPT_OUT(SiteMapCategory.ASSETS, "yukon.web.modules.operator.optOutAdmin.pageName", "/stars/operator/optOut/admin",
            MatchStyle.any, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_STATUS, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT),
    WORK_ORDERS(SiteMapCategory.ASSETS, "yukon.web.workOrders", "/operator/WorkOrder/WorkOrder.jsp",
            MatchStyle.all, YukonRole.WORK_ORDER),
    PURCHASING(SiteMapCategory.ASSETS, "yukon.web.purchasing", "/operator/Hardware/PurchaseTrack.jsp",
            MatchStyle.all, YukonRoleProperty.PURCHASING_ACCESS),
    SWITCHCOMMANDS(SiteMapCategory.ASSETS, "yukon.web.viewBatchCommands", "/operator/Admin/SwitchCommands.jsp",
            MatchStyle.all, YukonRoleProperty.ADMIN_VIEW_BATCH_COMMANDS, OtherPermission.EC_OPERATOR),
    OPT_OUT_SURVEYS(SiteMapCategory.ASSETS, "yukon.web.modules.operator.surveyList.pageName", "/stars/optOutSurvey/list",
            MatchStyle.all, OtherPermission.HIDEABLE , YukonRoleProperty.OPERATOR_OPT_OUT_SURVEY_EDIT),
    STARS_IMPORT(SiteMapCategory.ASSETS, "yukon.web.menu.import", "/stars/operator/account/accountImport",
            MatchStyle.all, YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT),
    ENROLLMENT_MIGRATION(SiteMapCategory.ASSETS, "yukon.web.migrateEnrollmentInformation", "/operator/Consumer/MigrateEnrollment.jsp",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.ENABLE_MIGRATE_ENROLLMENT),
    ENABLE_GENERIC_UPLOAD(SiteMapCategory.ASSETS, "yukon.web.genericUpload", "/operator/Consumer/GenericUpload.jsp",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.ENABLE_GENERIC_UPLOAD),
    STARS_REPORTS(SiteMapCategory.ASSETS, "yukon.web.menu.config.reporting.reports.stars", "/analysis/Reports.jsp?groupType=STARS",
            MatchStyle.all, YukonRoleProperty.STARS_REPORTS_GROUP),
    SERVICE_ORDER_LIST(SiteMapCategory.ASSETS, "yukon.web.serviceOrderList", "/operator/WorkOrder/WOFilter.jsp",
            MatchStyle.all, YukonRole.WORK_ORDER),
    ZIGBEE_PROBLEM_DEVICES(SiteMapCategory.ASSETS, "yukon.web.modules.operator.inventory.home.zbProblemDevices", "/stars/operator/inventory/zbProblemDevices/view",
            MatchStyle.all, YukonRole.INVENTORY),
    WORK_ORDER_REPORTS(SiteMapCategory.ASSETS, "yukon.web.menu.assets.workOrderReports", "/operator/WorkOrder/Report.jsp",
            MatchStyle.all, YukonRole.WORK_ORDER),
    GATEWAYS(SiteMapCategory.ASSETS, "yukon.web.menu.assets.gateways", "/stars/gateways", MatchStyle.any, YukonRoleProperty.INFRASTRUCTURE_ADMIN,
             YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE, YukonRoleProperty.INFRASTRUCTURE_DELETE, YukonRoleProperty.INFRASTRUCTURE_VIEW),
    RELAYS(SiteMapCategory.ASSETS, "yukon.web.menu.assets.relays", "/stars/relay", MatchStyle.any, YukonRoleProperty.INFRASTRUCTURE_ADMIN,
        YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE, YukonRoleProperty.INFRASTRUCTURE_DELETE, YukonRoleProperty.INFRASTRUCTURE_VIEW),
    RTUS(SiteMapCategory.ASSETS, "yukon.web.menu.assets.rtus", "/stars/rtu-list", MatchStyle.all, YukonRoleProperty.CBC_DATABASE_EDIT),
    COMPREHENSIVE_MAP(SiteMapCategory.ASSETS, "yukon.web.modules.operator.comprehensiveMap.pageName", "/stars/comprehensiveMap/home", 
                      MatchStyle.any, YukonRoleProperty.INFRASTRUCTURE_ADMIN, YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE, 
                      YukonRoleProperty.INFRASTRUCTURE_DELETE, YukonRoleProperty.INFRASTRUCTURE_VIEW),

    CONFIGURATION(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.config.pageName", "/admin/config/view",
            MatchStyle.all, YukonRoleProperty.ADMIN_SUPER_USER),
    ENERGY_COMPANY(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.systemAdministration.energyCompanyAdministration", "/admin/energyCompany/home",
            MatchStyle.any, YukonRoleProperty.ADMIN_SUPER_USER, OtherPermission.EC_OPERATOR),
    ACTIVE_JOBS(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.jobsscheduler.active.pageName", "/admin/jobsscheduler/active",
            MatchStyle.all, YukonRole.OPERATOR_ADMINISTRATOR),
    JOB_STATUS(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.jobsscheduler.status.pageName", "/admin/jobsscheduler/status",
            MatchStyle.all, YukonRole.OPERATOR_ADMINISTRATOR),
    ALL_JOBS(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.jobsscheduler.all.pageName", "/admin/jobsscheduler/all",
            MatchStyle.all, YukonRole.OPERATOR_ADMINISTRATOR),
    MAINTENANCE(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.systemAdministration.maintenance", "/admin/maintenance/view",
            MatchStyle.all, YukonRoleProperty.ADMIN_SUPER_USER),
    MULTISPEAK(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.systemAdministration.multiSpeakSetup", "/multispeak/setup/home",
            MatchStyle.all, YukonRoleProperty.ADMIN_MULTISPEAK_SETUP),
    SUBSTATIONS(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.systemAdministration.substations", "/admin/substations/routeMapping/view",
            MatchStyle.all, YukonRole.OPERATOR_ADMINISTRATOR),
    USERS_GROUPS(SiteMapCategory.ADMIN, "yukon.web.menu.admin.usersAndGroups", "/admin/users-groups/home",
            MatchStyle.all, YukonRoleProperty.ADMIN_SUPER_USER),
    ADMIN_REPORTS(SiteMapCategory.ADMIN, "yukon.web.menu.config.reporting.reports.administrator", "/analysis/Reports.jsp?groupType=ADMINISTRATIVE",
            MatchStyle.all, YukonRoleProperty.ADMIN_REPORTS_GROUP),
    DATABASE_REPORTS(SiteMapCategory.ADMIN, "yukon.web.menu.config.reporting.reports.database", "/analysis/Reports.jsp?groupType=DATABASE",
            MatchStyle.all, YukonRoleProperty.DATABASE_REPORTS_GROUP),
    STATISTICAL_REPORTS(SiteMapCategory.ADMIN, "yukon.web.menu.config.reporting.reports.statistical", "/analysis/Reports.jsp?groupType=STATISTICAL",
            MatchStyle.all, YukonRoleProperty.STATISTICAL_REPORTS_GROUP),
    CCURT_REPORTS(SiteMapCategory.ADMIN, "yukon.web.menu.config.reporting.reports.cni", "/analysis/Reports.jsp?groupType=CCURT",
            MatchStyle.all, YukonRoleProperty.CI_CURTAILMENT_REPORTS_GROUP),
    SETTLEMENT_REPORTS(SiteMapCategory.ADMIN, "yukon.web.menu.config.reporting.reports.settlement", "/analysis/Reports.jsp?groupType=SETTLEMENT",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.ENABLE_SETTLEMENTS, YukonRoleProperty.ADMIN_REPORTS_GROUP),
    THEMES(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.config.themes.pageName", "/admin/config/themes",
            MatchStyle.all, YukonRoleProperty.ADMIN_SUPER_USER),
    SURVEYS(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.survey.list.pageName", "/stars/survey/list",
            MatchStyle.all, OtherPermission.HIDEABLE , YukonRoleProperty.OPERATOR_SURVEY_EDIT),
    DASHBOARDS(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.config.dashboards.pageName", "dashboards/admin",
               MatchStyle.all, OtherPermission.HIDEABLE, YukonRoleProperty.ADMIN_MANAGE_DASHBOARDS),

    COMMANDER(SiteMapCategory.TOOLS, "yukon.web.modules.tools.commander.pageHeading", "/tools/commander",
            MatchStyle.all, YukonRoleProperty.ENABLE_WEB_COMMANDER),
    CREATE_BILLING_SCHEDULES(SiteMapCategory.TOOLS, "yukon.web.modules.tools.schedules.home.CREATE.pageDescription", "/group/scheduledGroupRequestExecution/home",
            MatchStyle.all, YukonRoleProperty.MANAGE_SCHEDULES),
    DATA_EXPORT(SiteMapCategory.TOOLS, "yukon.web.menu.tools.dataExport", "/tools/data-exporter/view",
            MatchStyle.all, YukonRoleProperty.ARCHIVED_DATA_EXPORT),
    DATA_STREAMING(SiteMapCategory.TOOLS, "yukon.web.menu.tools.dataStreaming", "/tools/dataStreaming/configurations",
            MatchStyle.all, OtherPermission.HIDEABLE, YukonRoleProperty.ADMIN_VIEW_CONFIG,
            MasterConfigBoolean.RF_DATA_STREAMING_ENABLED),
    DEVICE_CONFIGURATION(SiteMapCategory.TOOLS, "yukon.web.menu.tools.deviceConfiguration", "/deviceConfiguration/home",
            MatchStyle.all, YukonRoleProperty.ADMIN_VIEW_CONFIG),
    DEVICE_CONFIGURATION_SUMMARY(SiteMapCategory.TOOLS, "yukon.web.menu.tools.deviceConfigurationSummary", "/deviceConfiguration/summary/view",
            MatchStyle.all, YukonRoleProperty.ADMIN_VIEW_CONFIG),
    DEVICE_GROUPS(SiteMapCategory.TOOLS, "yukon.web.menu.config.amr.devicegroups", "/group/editor/home",
            MatchStyle.all, YukonRole.DEVICE_ACTIONS),
    DEVICE_UPLOAD(SiteMapCategory.TOOLS, "yukon.web.menu.tools.deviceGroupUpload", "/group/updater/upload",
            MatchStyle.all, YukonRoleProperty.BULK_UPDATE_OPERATION),
    COLLECTION_ACTIONS(SiteMapCategory.TOOLS, "yukon.web.modules.tools.collectionActions.home.pageName", "/collectionActions/home",
            MatchStyle.all, YukonRole.DEVICE_ACTIONS),
    RECENT_RESULTS(SiteMapCategory.TOOLS, "yukon.web.modules.tools.collectionActions.recentResults.pageName", "/collectionActions/recentResults",
                   MatchStyle.all, YukonRole.DEVICE_ACTIONS),
    MSP_POWER_SUPPLIER_LOADS(SiteMapCategory.TOOLS, "yukon.web.modules.dr.loadManagement.siteMapName", "/multispeak/visualDisplays/loadManagement/home",
            MatchStyle.all, OtherPermission.HIDEABLE, GlobalSettingType.MSP_LM_MAPPING_SETUP),
    MSP_PROB_FOR_PEAK_LOAD(SiteMapCategory.TOOLS, "yukon.web.modules.dr.probabilityForPeakLoad.pageTitle", "/multispeak/visualDisplays/probabilityForPeak/home",
            MatchStyle.all, OtherPermission.HIDEABLE, GlobalSettingType.MSP_LM_MAPPING_SETUP),
    SCHEDULES(SiteMapCategory.TOOLS, "yukon.web.modules.amr.billing.jobs.title", "/group/scheduledGroupRequestExecutionResults/jobs",
            MatchStyle.all, YukonRole.SCHEDULER),
    SCRIPTS(SiteMapCategory.TOOLS, "yukon.web.menu.config.amr.scheduler", "/macsscheduler/schedules/view",
            MatchStyle.all, YukonRole.SCHEDULER),
    TDC(SiteMapCategory.TOOLS, "yukon.web.modules.tools.tdc.home.pageName", "/tools/data-viewer",
            MatchStyle.all, YukonRole.TABULAR_DISPLAY_CONSOLE),
    PAO_NOTE_SEARCH(SiteMapCategory.TOOLS, "yukon.web.modules.tools.paoNotesSearch.pageName", "/tools/paoNotes/search",
        MatchStyle.all),

    SUPPORT(SiteMapCategory.SUPPORT, "yukon.web.modules.support.support.pageName", "/support",
            MatchStyle.all, YukonRole.OPERATOR_ADMINISTRATOR),
    SYSTEM(SiteMapCategory.SUPPORT, "yukon.web.modules.support.system.pageName", "/support/info",
            MatchStyle.all, YukonRole.OPERATOR_ADMINISTRATOR),
    ERROR_CODES(SiteMapCategory.SUPPORT, "yukon.web.menu.config.support.information.errorCodes", "/support/errorCodes/view",
            MatchStyle.all),
    MANAGE_INDEXES(SiteMapCategory.SUPPORT, "yukon.web.menu.tools.manageIndex", "/index/manage",
            MatchStyle.all, YukonRoleProperty.ADMIN_MANAGE_INDEXES),
    LOCALIZATION(SiteMapCategory.SUPPORT, "yukon.web.modules.support.localization.pageName", "/support/localization/view",
            MatchStyle.all),
    LOG_VIEWER(SiteMapCategory.SUPPORT, "yukon.web.modules.support.logMenu.pageName", "/support/logging/menu",
            MatchStyle.all, YukonRoleProperty.ADMIN_VIEW_LOGS),
    DATABASE_VALIDATION(SiteMapCategory.SUPPORT, "yukon.web.modules.support.databaseValidate.pageName", "/support/database/validate/home",
            MatchStyle.all, YukonRoleProperty.ADMIN_VIEW_LOGS),
    EVENT_LOG(SiteMapCategory.SUPPORT, "yukon.web.modules.support.eventViewer.pageName", "/common/eventLog/viewByCategory",
            MatchStyle.all, YukonRoleProperty.ADMIN_EVENT_LOGS),
    FILE_EXPORT_HISTORY(SiteMapCategory.SUPPORT, "yukon.web.modules.support.fileExportHistory.pageName","/support/fileExportHistory/list",
            MatchStyle.all),
    THREAD_DUMP(SiteMapCategory.SUPPORT, "yukon.web.modules.support.threadDump.pageName", "/support/threadDump",
            MatchStyle.all, YukonRole.OPERATOR_ADMINISTRATOR),
    WATER_NODE(SiteMapCategory.SUPPORT, "yukon.web.modules.support.waterNode.pageName", "/support/waterNode/view",
            MatchStyle.all, YukonRole.METERING),
    ROUTE_USAGE(SiteMapCategory.SUPPORT, "yukon.web.modules.support.routeUsage.pageName", "/support/routeUsage",
            MatchStyle.all),
    DEVICE_DEFINITIONS(SiteMapCategory.SUPPORT, "yukon.web.menu.config.support.information.deviceDef", "/common/deviceDefinition.xml",
            MatchStyle.all),
    DATA_STREAMING_SUPPORT(SiteMapCategory.SUPPORT, "yukon.web.modules.support.dataStreamingSupport.pageName", 
            "/support/dataStreamingSupport", MatchStyle.all, OtherPermission.HIDEABLE,
            MasterConfigBoolean.RF_DATA_STREAMING_ENABLED),
    DATABASE_MIGRATION(SiteMapCategory.SUPPORT, "yukon.web.modules.support.databaseMigration.pageName", "/support/database/migration/home",
            MatchStyle.all, YukonRoleProperty.ADMIN_DATABASE_MIGRATION),
    SYSTEM_METRICS(SiteMapCategory.SUPPORT, "yukon.web.modules.support.systemHealth.pageName", "/support/systemHealth/home", 
            MatchStyle.all),

    DEVELOPMENT(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.dev.home.pageName", "/dev",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.DEVELOPMENT_MODE),
    UI_DEMO(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.dev.styleguide.pageName", "/dev/styleguide",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.DEVELOPMENT_MODE),
    POINT_INJECTION(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.dev.pointInjection.pageName", "/dev/pointInjection/main",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.DEVELOPMENT_MODE),
    BULK_POINT_INJECTION(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.dev.bulkPointInjection.pageName", "/dev/bulkPointInjection/home",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.DEVELOPMENT_MODE),
    SETUP_DATABASE(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.dev.setupDatabase.pageName", "/dev/setupDatabase/main",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.DEVELOPMENT_MODE),
    MISC_METHODS(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.dev.miscellaneousMethod.pageName", "/dev/miscellaneousMethod/main",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.DEVELOPMENT_MODE),
    RFN_TEST(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.dev.rfnTest.pageName", "/dev/rfn/viewBase",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.DEVELOPMENT_MODE),
    ECOBEE_TEST(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.dev.ecobee.mockTest.pageName", "/dev/ecobee/viewBase",
                    MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.DEVELOPMENT_MODE),
    EIM_TEST(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.dev.webServices.eimTest.pageName", "/dev/eimTest/main",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.DEVELOPMENT_MODE, MasterConfigBoolean.ENABLE_WEB_DEBUG_PAGES),
    LOAD_CONTROL_TEST(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.dev.webServices.loadControl.pageName", "/debug/loadControlService/inputs/home",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.DEVELOPMENT_MODE, MasterConfigBoolean.ENABLE_WEB_DEBUG_PAGES),
    ACCOUNT_TEST(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.dev.webServices.account.pageName", "/debug/accountService/inputs/home",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.DEVELOPMENT_MODE, MasterConfigBoolean.ENABLE_WEB_DEBUG_PAGES),
    SYSTEM_METRICS_SIMULATOR(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.dev.systemMetricsSimulator.pageName", "/dev/systemHealthMetricSimulator/home",
            MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBoolean.DEVELOPMENT_MODE, MasterConfigBoolean.ENABLE_WEB_DEBUG_PAGES)
    ;

    public enum OtherPermission { 
        EC_OPERATOR,
        HIDEABLE;
    }

    public enum PermissionLevel {
        ACCESS,
        VIEW,
        HIDE
    }

    private final SiteMapCategory category;
    private final String link;
    private final String messageKey;
    private final MatchStyle matchStyle;
    private final Object[] permissions;

    private SiteMapPage(SiteMapCategory siteMenu, String messageKey, String link, MatchStyle matchStyle,
            Object... permissions) {
        category = siteMenu;
        this.link = link;
        this.matchStyle = matchStyle;
        this.permissions = permissions;
        this.messageKey = messageKey;
    }

    public Object[] getPermissions() {
        return permissions;
    }

    public String getLink() {
        return link;
    }

    public MatchStyle getMatchStyle(){
        return matchStyle;
    }

    public SiteMapCategory getCategory() {
        return category;
    }

    @Override
    public String getFormatKey() {
        return messageKey;
    }
}
