package com.cannontech.web.support;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.util.MatchStyle;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class SiteMapHelper {

    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonEnergyCompanyService energyCompanyService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ObjectFormattingService objectFormattingService;
    
    public Map<SiteMapCategory, List<SiteMapWrapper>> getSiteMap(YukonUserContext context){
        Multimap<SiteMapCategory, SiteMapWrapper> map = ArrayListMultimap.create();

        for ( SiteMapPage page : SiteMapPage.values() ) {
            SiteMapWrapper wrapper = wrapPage(page, context.getYukonUser());
            if(wrapper != null){
                map.put(page.getCategory(), wrapper);
            }
        }

        Map<SiteMapCategory, Collection<SiteMapWrapper>> multiMap = map.asMap();

        List<SiteMapCategory> categories = Lists.newArrayList(multiMap.keySet());
        Collections.sort(categories);

        Map<SiteMapCategory, List<SiteMapWrapper>> realMap = Maps.newLinkedHashMap();
        for(SiteMapCategory category: categories){
            List<SiteMapWrapper> pages = Lists.newArrayList(multiMap.get(category));
            pages = objectFormattingService.sortDisplayableValues(pages, null, null, context);
            realMap.put(category, pages);
        }
        return realMap;
    }
    
    private SiteMapWrapper wrapPage(SiteMapPage page, LiteYukonUser user){
        SiteMapWrapper wrapper = new SiteMapWrapper();
        wrapper.setPage(page);
        wrapper.setEnabled(false);

        PermissionLevel hasPermission = doesHavePermission(page, user);
        switch(hasPermission){
        case ACCESS:
            wrapper.setEnabled(true);
            break;
        case VIEW:
            wrapper.setEnabled(false);
            String permissionList = getPermissionDisplayList(page);
            wrapper.setRequiredPermissions(permissionList);
            break;
        case HIDE:
            return null;
        }
        return wrapper;
    }
    
    private String getPermissionDisplayList(SiteMapPage page) {
        String permissions = "";
        for(Object permission : page.getPermissions()){
            if ( permission instanceof YukonRole) {
                permissions += ((YukonRole) permission).name() + " ";
            } else if (permission instanceof YukonRoleProperty) {
                permissions += ((YukonRoleProperty) permission).name() + " ";
            } else if (permission instanceof GlobalSettingType) {
                permissions += ((GlobalSettingType) permission).name() + " ";
            } else if (permission instanceof MasterConfigBooleanKeysEnum) {
                permissions += ((MasterConfigBooleanKeysEnum) permission).name() + " ";
            } else if (permission instanceof OtherPermission) {
                switch((OtherPermission) permission){
                case EC_OPERATOR:
                    permissions += "EC Operator ";
                    break;
                case HIDEABLE:
                    break;
                }
            } else {
                throw new IllegalArgumentException("Permission type not supported: " + permission);
            }
        }
        return permissions;
    }
    
    private PermissionLevel doesHavePermission(SiteMapPage page, LiteYukonUser user) {
        Object[] permissions = page.getPermissions();
        MatchStyle matchStyle = page.getMatchStyle();

        if(permissions.length== 0) return PermissionLevel.ACCESS;

        PermissionLevel noPermission = PermissionLevel.VIEW;

        for(Object permission : permissions){
            boolean hasPermission = false;
            if ( permission instanceof YukonRole) {
                hasPermission = rolePropertyDao.checkRole((YukonRole) permission, user);
            } else if (permission instanceof YukonRoleProperty) {
                hasPermission = rolePropertyDao.checkProperty((YukonRoleProperty) permission, user);
            } else if (permission instanceof GlobalSettingType) {
                hasPermission = globalSettingDao.getBoolean((GlobalSettingType) permission);
            } else if (permission instanceof MasterConfigBooleanKeysEnum) {
                hasPermission = configurationSource.getBoolean((MasterConfigBooleanKeysEnum) permission);
            } else if (permission instanceof OtherPermission) {
                switch((OtherPermission) permission){
                case EC_OPERATOR:
                    hasPermission = energyCompanyService.isEnergyCompanyOperator(user);
                    break;
                case HIDEABLE:
                    hasPermission = true;
                    noPermission = PermissionLevel.HIDE;
                    
                }
            } else {
                throw new IllegalArgumentException("Permission type not supported: " + permission);
            }
            //Short-circuit out if "any" and one is true, or "all" and one is false.
            if(hasPermission && matchStyle == MatchStyle.any) return PermissionLevel.ACCESS;
            if(! hasPermission && matchStyle == MatchStyle.all) return noPermission;
        }
        //Return true if "all" and no non-matches, false if "any" and no matches
        if(matchStyle == MatchStyle.all) return PermissionLevel.ACCESS;
        if(matchStyle == MatchStyle.any) return noPermission;
        throw new InvalidParameterException("MatchStyle must be either Matchstyle.any or Matchstyle.all");
    }
    
    public class SiteMapWrapper implements DisplayableEnum{

        private boolean enabled;
        private SiteMapPage page;
        private String requiredPermissions;

        public String getRequiredPermissions() {
            return requiredPermissions;
        }
        public void setRequiredPermissions(String requiredPermissions) {
            this.requiredPermissions = requiredPermissions;
        }
        public boolean isEnabled() {
            return enabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        public SiteMapPage getPage() {
            return page;
        }
        public void setPage(SiteMapPage page) {
            this.page = page;
        }
        @Override
        public String getFormatKey() {
            return page.getFormatKey();
        }
    }
    
    public enum SiteMapCategory implements DisplayableEnum {
        AMI,
        DR,
        VV,
        ASSETS,
        TOOLS,
        ADMIN,
        SUPPORT,
        DEVELOPMENT, 
        ;

        @Override
        public String getFormatKey() {
            return "yukon.web.menu." + name().toLowerCase(Locale.US);
        }
    }

    public enum OtherPermission { 
        EC_OPERATOR,
        HIDEABLE;
    }

    public enum PermissionLevel {
        ACCESS,
        VIEW,
        HIDE
    }

    public enum SiteMapPage implements DisplayableEnum {
        METERING(SiteMapCategory.AMI, "yukon.web.modules.amr.meteringStart.pageName", "/meter/start", 
                MatchStyle.any, YukonRole.METERING, YukonRole.APPLICATION_BILLING, YukonRole.SCHEDULER, YukonRole.DEVICE_ACTIONS),
        BILLING(SiteMapCategory.AMI, "yukon.web.menu.config.amr.billing", "/billing/home", 
                MatchStyle.all, YukonRole.APPLICATION_BILLING),
        TRENDS(SiteMapCategory.AMI, "yukon.web.modules.operator.metering.trends.title", "/operator/Metering/Metering.jsp", 
                MatchStyle.all, YukonRole.TRENDING),
        IMPORT_AMI(SiteMapCategory.AMI, "yukon.web.menu.import", "/amr/bulkimporter/home", 
                MatchStyle.all, YukonRoleProperty.IMPORTER_ENABLED),
        AMI_REPORTS(SiteMapCategory.AMI, "yukon.web.menu.config.reporting.reports.metering", "/analysis/Reports.jsp?groupType=METERING", 
                MatchStyle.all, YukonRoleProperty.AMR_REPORTS_GROUP),
        ARCHIVE_DATA_ANALYSIS(SiteMapCategory.AMI, "yukon.web.modules.tools.bulk.analysis.home.pageName", "/bulk/archiveDataAnalysis/list/view", 
                MatchStyle.all, YukonRoleProperty.ARCHIVED_DATA_ANALYSIS),
        PHASE_DETECT(SiteMapCategory.AMI, "yukon.web.modules.amr.phaseDetect.home.pageDescription", "/amr/phaseDetect/home", 
                MatchStyle.all, YukonRoleProperty.PHASE_DETECT),
        METER_EVENTS_REPORT(SiteMapCategory.AMI, "yukon.web.modules.amr.meterEventsReport.report.pageName", "/amr/meterEventsReport/selectDevices",
                MatchStyle.all),
        WATER_LEAK_REPORT(SiteMapCategory.AMI, "yukon.web.modules.amr.waterLeakReport.report.pageName", "/amr/waterLeakReport/report?initReport=true",
                MatchStyle.all),
        BILLING_SCHEDULES(SiteMapCategory.AMI, "yukon.web.menu.config.amr.billing.schedules", "/billing/schedules", 
                MatchStyle.all, YukonRole.APPLICATION_BILLING),
        CREATE_BILLING_SCHEDULES(SiteMapCategory.AMI, "yukon.web.modules.amr.scheduledGroupRequestHome.CREATE.pageDescription", "/group/scheduledGroupRequestExecution/home", 
                MatchStyle.all, YukonRoleProperty.MANAGE_SCHEDULES),
        REVIEW_FLAGGED_POINTS(SiteMapCategory.AMI, "yukon.web.widgets.validationMonitorsWidget.review", "/common/veeReview/home",
                MatchStyle.all, YukonRoleProperty.MANAGE_SCHEDULES),

        DR_DASHBOARD(SiteMapCategory.DR, "yukon.web.modules.dr.home.pageName", "/dr/home", 
                MatchStyle.all, YukonRoleProperty.DEMAND_RESPONSE),
        CONTROL_AREAS(SiteMapCategory.DR, "yukon.web.modules.dr.controlAreaList.pageName", "/dr/controlArea/list", 
                MatchStyle.all, YukonRoleProperty.DEMAND_RESPONSE, YukonRoleProperty.SHOW_CONTROL_AREAS),
        SCENARIOS(SiteMapCategory.DR, "yukon.web.modules.dr.scenarioList.pageName", "/dr/controlArea/list", 
                MatchStyle.all, YukonRoleProperty.DEMAND_RESPONSE, YukonRoleProperty.SHOW_SCENARIOS),
        PROGRAMS(SiteMapCategory.DR, "yukon.web.modules.dr.programList.pageName", "/dr/program/list", 
                MatchStyle.all, YukonRoleProperty.DEMAND_RESPONSE),
        LOAD_GROUPS(SiteMapCategory.DR, "yukon.web.modules.dr.loadGroupList.pageName", "/dr/loadGroup/list", 
                MatchStyle.all, YukonRoleProperty.DEMAND_RESPONSE),
        DR_REPORTS(SiteMapCategory.DR, "yukon.web.menu.config.reporting.reports.management", "/analysis/Reports.jsp?groupType=LOAD_MANAGEMENT", 
                MatchStyle.all, YukonRoleProperty.LOAD_MANAGEMENT_REPORTS_GROUP),
        CI_CURTAILMENT(SiteMapCategory.DR, "yukon.web.menu.portal.loadResponse.ciCurtailment", "/cc/programSelect.jsf", 
                MatchStyle.all, YukonRole.CI_CURTAILMENT),
        ODDS_FOR_CONTROL(SiteMapCategory.DR, "yukon.web.oddsForControl", "Consumer/Odds.jsp", 
                MatchStyle.all, YukonRole.ODDS_FOR_CONTROL),
        ACTIVE_CONTROL_AREAS(SiteMapCategory.DR, "yukon.web.modules.dr.home.activeControlAreasQuickSearch", "/dr/controlArea/list?state=active", 
                MatchStyle.all, YukonRoleProperty.DEMAND_RESPONSE, YukonRoleProperty.SHOW_CONTROL_AREAS),
        ACTIVE_PROGRAMS(SiteMapCategory.DR, "yukon.web.modules.dr.home.activeProgramsQuickSearch", "/dr/program/list?state=ACTIVE", 
                MatchStyle.all, YukonRoleProperty.DEMAND_RESPONSE),
        ACTIVE_LOAD_GROUPS(SiteMapCategory.DR, "yukon.web.modules.dr.home.activeLoadGroupsQuickSearch", "/dr/loadGroup/list?state=active", 
                MatchStyle.all, YukonRoleProperty.DEMAND_RESPONSE),
        DIRECT_CONTROL(SiteMapCategory.DR, "yukon.web.direct", "/operator/LoadControl/oper_direct.jsp",
                MatchStyle.all, YukonRoleProperty.DIRECT_CONTROL),

        AREAS(SiteMapCategory.VV, "yukon.web.modules.capcontrol.areas.normal.pageName", "/capcontrol/tier/areas", 
                MatchStyle.all, YukonRoleProperty.CAP_CONTROL_ACCESS),
        IVVC_SCHEDULES(SiteMapCategory.VV, "yukon.web.modules.capcontrol.schedules.pageName", "/capcontrol/schedule/schedules", 
                MatchStyle.all, YukonRoleProperty.CAP_CONTROL_ACCESS),
        STRATEGIES(SiteMapCategory.VV, "yukon.web.modules.capcontrol.strategies.pageName", "/capcontrol/strategy/strategies", 
                MatchStyle.all, YukonRoleProperty.CAP_CONTROL_ACCESS),
        MOVED_CAP_BANKS(SiteMapCategory.VV, "yukon.web.modules.capcontrol.movedCapBanks.pageName", "/capcontrol/strategy/strategies", 
                MatchStyle.all, YukonRoleProperty.CAP_CONTROL_ACCESS),
        ORPHANS(SiteMapCategory.VV, "yukon.web.menu.config.capcontrol.orphans", "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubstations__", 
                MatchStyle.all, YukonRoleProperty.CAP_CONTROL_ACCESS),
        IMPORT(SiteMapCategory.VV, "yukon.web.modules.capcontrol.import.pageTitle", "/capcontrol/import/view", 
                MatchStyle.all, YukonRoleProperty.CAP_CONTROL_ACCESS, YukonRoleProperty.CAP_CONTROL_IMPORTER),
        IVVC_REPORTS(SiteMapCategory.VV, "yukon.web.menu.config.reporting.reports.capcontrol", "/analysis/Reports.jsp?groupType=CAP_CONTROL", 
                MatchStyle.all, YukonRoleProperty.CAP_CONTROL_REPORTS_GROUP),
        ESUB(SiteMapCategory.VV, "yukon.web.esubstation", "/esub/home", 
                MatchStyle.all, YukonRoleProperty.OPERATOR_ESUBSTATION_DRAWINGS_VIEW),
        SCHEDULE_ASSIGNMENTS(SiteMapCategory.VV, "yukon.web.modules.capcontrol.scheduleAssignments.pageName", "/capcontrol/schedule/scheduleAssignments", 
                MatchStyle.all, YukonRoleProperty.CAP_CONTROL_ACCESS),
        SPECIAL_AREAS(SiteMapCategory.VV, "yukon.web.modules.capcontrol.areas.special.pageName", "/capcontrol/tier/areas?isSpecialArea=true", 
                MatchStyle.all, YukonRoleProperty.CAP_CONTROL_ACCESS),

        CREATE_ACCOUNT(SiteMapCategory.ASSETS, "yukon.web.modules.operator.account.CREATE.pageName", "/stars/operator/account/accountCreate", 
                MatchStyle.all, OtherPermission.EC_OPERATOR, YukonRoleProperty.OPERATOR_NEW_ACCOUNT_WIZARD),
        ACCOUNTS_AND_INVENTORY(SiteMapCategory.ASSETS, "yukon.web.modules.operator.inventory.home.pageName", "/stars/operator/inventory/home", 
                MatchStyle.all, YukonRole.INVENTORY),
        OPT_OUT(SiteMapCategory.ASSETS, "yukon.web.modules.dr.optOutAdmin.pageName", "/stars/operator/optOut/admin", 
                MatchStyle.any, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_STATUS, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS,YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT),
        WORK_ORDERS(SiteMapCategory.ASSETS, "yukon.web.workOrders", "/operator/WorkOrder/WorkOrder.jsp", 
                MatchStyle.all, YukonRole.WORK_ORDER),
        PURCHASING(SiteMapCategory.ASSETS, "yukon.web.purchasing", "operator/Hardware/PurchaseTrack.jsp", 
                MatchStyle.all, YukonRoleProperty.PURCHASING_ACCESS),
        SWITCHCOMMANDS(SiteMapCategory.ASSETS, "yukon.web.viewBatchCommands", "/operator/Admin/SwitchCommands.jsp", 
                MatchStyle.all, YukonRoleProperty.ADMIN_VIEW_BATCH_COMMANDS, OtherPermission.EC_OPERATOR),
        SURVEYS(SiteMapCategory.ASSETS, "yukon.web.modules.survey.list.pageName", "/stars/survey/list", 
                MatchStyle.all, OtherPermission.HIDEABLE , YukonRoleProperty.OPERATOR_SURVEY_EDIT),
        STARS_IMPORT(SiteMapCategory.ASSETS, "yukon.web.menu.import", "/stars/operator/account/accountImport", 
                MatchStyle.all, YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT),
        ENROLLMENT_MIGRATION(SiteMapCategory.ASSETS, "yukon.web.migrateEnrollmentInformation", "/operator/Consumer/MigrateEnrollment.jsp", 
                MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBooleanKeysEnum.ENABLE_MIGRATE_ENROLLMENT),
        ENABLE_GENERIC_UPLOAD(SiteMapCategory.ASSETS, "yukon.web.genericUpload", "/operator/Consumer/GenericUpload.jsp", 
                MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBooleanKeysEnum.ENABLE_GENERIC_UPLOAD),
        STARS_REPORTS(SiteMapCategory.ASSETS, "yukon.web.menu.config.reporting.reports.stars", "/analysis/Reports.jsp?groupType=STARS", 
                MatchStyle.all, YukonRoleProperty.STARS_REPORTS_GROUP),
        SERVICE_ORDER_LIST(SiteMapCategory.ASSETS, "yukon.web.serviceOrderList", "/operator/WorkOrder/WOFilter.jsp", 
                MatchStyle.all, YukonRole.WORK_ORDER),
        SCHEDULED_OPT_OUT(SiteMapCategory.ASSETS, "yukon.web.menu.portal.administration.viewScheduledOptOutEvents", "/stars/operator/optOut/admin/viewScheduled", 
                MatchStyle.all, YukonRole.CONSUMER_INFO, YukonRoleProperty.ADMIN_VIEW_OPT_OUT_EVENTS),
        ZIGBEE_PROBLEM_DEVICES(SiteMapCategory.ASSETS, "yukon.web.modules.operator.inventory.home.zbProblemDevices", "/stars/operator/inventory/zbProblemDevices/view", 
                MatchStyle.all, YukonRole.INVENTORY),
        WORK_ORDER_REPORTS(SiteMapCategory.ASSETS, "yukon.web.menu.assets.workOrderReports", "/operator/WorkOrder/Report.jsp", 
                MatchStyle.all, YukonRole.WORK_ORDER),
        NEW_WORK_ORDER(SiteMapCategory.ASSETS, "yukon.web.menu.assets.newWorkOrder", "/operator/WorkOrder/CreateOrder.jsp", 
                MatchStyle.all, YukonRole.WORK_ORDER),

        CONFIGURATION(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.config.pageName", "/adminSetup/config/view", 
                MatchStyle.all, YukonRoleProperty.ADMIN_SUPER_USER),
        ENERGY_COMPANY(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.systemAdministration.energyCompanyAdministration", "/adminSetup/energyCompany/home", 
                MatchStyle.all, OtherPermission.EC_OPERATOR),
        ACTIVE_JOBS(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.jobsscheduler.active.pageName", "/adminSetup/jobsscheduler/active", 
                MatchStyle.all),
        JOB_STATUS(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.jobsscheduler.status.pageName", "/adminSetup/jobsscheduler/status", 
                MatchStyle.all),
        ALL_JOBS(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.jobsscheduler.all.pageName", "/adminSetup/jobsscheduler/jobs", 
                MatchStyle.all),
        MAINTENANCE(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.systemAdministration.maintenance", "/adminSetup/maintenance/view", 
                MatchStyle.all, YukonRoleProperty.ADMIN_SUPER_USER),
        MULTISPEAK(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.systemAdministration.multiSpeakSetup", "/multispeak/setup/home", 
                MatchStyle.all, YukonRoleProperty.ADMIN_MULTISPEAK_SETUP),
        SECURITY(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.systemAdministration.security", "/adminSetup/security/view", 
                MatchStyle.all, YukonRoleProperty.ADMIN_SUPER_USER),
        SUBSTATIONS(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.systemAdministration.substations", "/adminSetup/substations/routeMapping/view", 
                MatchStyle.all, YukonRole.OPERATOR_ADMINISTRATOR),
        USERS_GROUPS(SiteMapCategory.ADMIN, "yukon.web.menu.admin.usersAndGroups", "/adminSetup/userEditor/home", 
                MatchStyle.all, YukonRoleProperty.ADMIN_SUPER_USER),
        ADMIN_REPORTS(SiteMapCategory.ADMIN, "yukon.web.menu.config.reporting.reports.administrator", "/analysis/Reports.jsp?groupType=ADMINISTRATIVE", 
                MatchStyle.all, YukonRoleProperty.ADMIN_REPORTS_GROUP),
        SYSTEM_ADMIN(SiteMapCategory.ADMIN, "yukon.web.menu.portal.administration.systemAdministration", "/adminSetup/systemAdmin", 
                MatchStyle.all, YukonRole.OPERATOR_ADMINISTRATOR),
        DATABASE_REPORTS(SiteMapCategory.ADMIN, "yukon.web.menu.config.reporting.reports.database", "/analysis/Reports.jsp?groupType=DATABASE", 
                MatchStyle.all, YukonRoleProperty.DATABASE_REPORTS_GROUP),
        STATISTICAL_REPORTS(SiteMapCategory.ADMIN, "yukon.web.menu.config.reporting.reports.statistical", "/analysis/Reports.jsp?groupType=STATISTICAL", 
                MatchStyle.all, YukonRoleProperty.STATISTICAL_REPORTS_GROUP),
        CCURT_REPORTS(SiteMapCategory.ADMIN, "yukon.web.menu.config.reporting.reports.cni", "/analysis/Reports.jsp?groupType=CCURT", 
                MatchStyle.all, YukonRoleProperty.CI_CURTAILMENT_REPORTS_GROUP),
        SETTLEMENT_REPORTS(SiteMapCategory.ADMIN, "yukon.web.menu.config.reporting.reports.settlement", "/analysis/Reports.jsp?groupType=SETTLEMENT", 
                MatchStyle.all, OtherPermission.HIDEABLE, YukonRoleProperty.SETTLEMENT_REPORTS_GROUP),
        ENCRYPTED_ROUTES(SiteMapCategory.ADMIN, "yukon.web.modules.adminSetup.security.routesBox.title", "/adminSetup/security/view", 
                MatchStyle.all, YukonRoleProperty.ADMIN_SUPER_USER),

        BULK_OPERATIONS(SiteMapCategory.TOOLS, "yukon.web.modules.tools.bulk.home.pageName", "/bulk/bulkHome", 
                MatchStyle.all),
        COMMANDER(SiteMapCategory.TOOLS, "yukon.web.modules.commanderSelect.pageTitle", "/apps/SelectDevice.jsp", 
                MatchStyle.all),
        DATA_EXPORT(SiteMapCategory.TOOLS, "yukon.web.menu.portal.analysis.archivedDateExport", "/amr/archivedValuesExporter/view", 
                MatchStyle.all, YukonRoleProperty.ARCHIVED_DATA_EXPORT),
        DEVICE_CONFIGURATION(SiteMapCategory.TOOLS, "yukon.web.menu.portal.administration.deviceConfiguration", "/deviceConfiguration/home", 
                MatchStyle.all, YukonRoleProperty.ADMIN_VIEW_CONFIG),
        DEVICE_DEFINITIONS(SiteMapCategory.TOOLS, "yukon.web.menu.config.support.information.deviceDef", "/common/deviceDefinition.xml", 
                MatchStyle.all),
        DEVICE_GROUPS(SiteMapCategory.TOOLS, "yukon.web.menu.config.amr.devicegroups", "/group/editor/home", 
                MatchStyle.all, YukonRoleProperty.ADMIN_VIEW_CONFIG),
        DEVICE_UPLOAD(SiteMapCategory.TOOLS, "yukon.web.menu.tools.deviceGroupUpload", "/group/updater/upload", 
                MatchStyle.all, YukonRoleProperty.BULK_UPDATE_OPERATION),
        BULK_IMPORT(SiteMapCategory.TOOLS, "yukon.web.modules.tools.bulk.importUpload.pageName", "/bulk/import/upload", 
                MatchStyle.all, YukonRoleProperty.BULK_UPDATE_OPERATION),
        BULK_UPDATE(SiteMapCategory.TOOLS, "yukon.web.modules.tools.bulk.updateUpload.pageName", "/bulk/update/upload", 
                MatchStyle.all, YukonRoleProperty.BULK_UPDATE_OPERATION),
        POINT_IMPORT(SiteMapCategory.TOOLS, "yukon.web.modules.tools.bulk.pointImport.pageName", "/bulk/pointImport/upload", 
                MatchStyle.all, YukonRoleProperty.ADD_REMOVE_POINTS),
        COLLECTION_ACTIONS(SiteMapCategory.TOOLS, "yukon.web.modules.tools.bulk.collectionActions.pageName", "/bulk/deviceSelection", 
                MatchStyle.all, YukonRoleProperty.ADD_REMOVE_POINTS),
        FDR_TRANSLATIONS(SiteMapCategory.TOOLS, "yukon.web.modules.tools.bulk.fdrTranslationManagement.pageName", "/bulk/fdrTranslationManager/home", 
                MatchStyle.all, YukonRoleProperty.FDR_TRANSLATION_MANAGER),
        MSP_TO_LM_MAPPING(SiteMapCategory.TOOLS, "yukon.web.menu.portal.analysis.visualDisplays", "/multispeak/visualDisplays/loadManagement/home", 
                MatchStyle.all, OtherPermission.HIDEABLE  ,GlobalSettingType.MSP_LM_MAPPING_SETUP),
        SCHEDULES(SiteMapCategory.TOOLS, "yukon.web.modules.amr.billing.jobs.title", "/group/scheduledGroupRequestExecutionResults/jobs", 
                MatchStyle.all, YukonRole.SCHEDULER),
        SCRIPTS(SiteMapCategory.TOOLS, "yukon.web.menu.config.amr.scheduler", "/macsscheduler/schedules/view", 
                MatchStyle.all, YukonRole.SCHEDULER),

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
        LOG_VIEWER(SiteMapCategory.SUPPORT, "yukon.web.menu.portal.administration.viewLogs", "/support/logging/menu", 
                MatchStyle.all, YukonRoleProperty.ADMIN_VIEW_LOGS),
        DATABASE_VALIDATION(SiteMapCategory.SUPPORT, "yukon.web.modules.support.databaseValidate.pageName", "/support/database/validate/home", 
                MatchStyle.all, YukonRoleProperty.ADMIN_VIEW_LOGS),
        EVENT_LOG(SiteMapCategory.SUPPORT, "yukon.web.modules.support.eventViewer.pageName", "/common/eventLog/viewByCategory", 
                MatchStyle.all, YukonRoleProperty.ADMIN_EVENT_LOGS),
        FILE_EXPORT_HISTORY(SiteMapCategory.SUPPORT, "yukon.web.modules.support.fileExportHistory.pageName", "/support/fileExportHistory/list", 
                MatchStyle.all),
        THREAD_DUMP(SiteMapCategory.SUPPORT, "yukon.web.modules.support.threadDump.pageName", "/support/threadDump", 
                MatchStyle.all),
        ROUTE_USAGE(SiteMapCategory.SUPPORT, "yukon.web.modules.support.routeUsage.pageName", "/support/routeUsage", 
                MatchStyle.all),

        POINT_INJECTION(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.support.pointInjection.pageName", "/support/development/pointInjection/main", 
                MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE),
        BULK_POINT_INJECTION(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.support.bulkPointInjection.pageName", "/support/development/bulkPointInjection/main", 
                MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE),
        UI_TOOLKIT(SiteMapCategory.DEVELOPMENT, "yukon.web.menu.config.support.development.uiToolkitDemo", "/support/development/uiToolkitDemo/main", 
                MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE),
        UI_DEMO(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.support.uiDemos.pageName", "/support/development/uiDemos/main", 
                MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE),
        SETUP_DATABASE(SiteMapCategory.DEVELOPMENT, "yukon.web.menu.config.support.development.setupDatabase", "/support/development/setupDatabase/main", 
                MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE),
        MISC_METHODS(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.support.miscellaneousMethod.pageName", "/support/development/miscellaneousMethod/main", 
                MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE),
        RFN_TEST(SiteMapCategory.DEVELOPMENT, "yukon.web.modules.support.rfnTest.pageName", "/support/development/rfn/viewBase", 
                MatchStyle.all, OtherPermission.HIDEABLE, MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE),
        ;

        private SiteMapCategory category;
        private String link;
        private String nameKey;
        private MatchStyle matchStyle;
        private Object[] permissions;

        private SiteMapPage(SiteMapCategory category, String nameKey, String link, MatchStyle matchStyle, Object... permissions) {
            this.category = category;
            this.link = link;
            this.matchStyle = matchStyle;
            this.permissions = permissions;
            this.nameKey = nameKey;
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
            return nameKey;
        }
    }
}
