package com.eaton.framework;

public final class Urls {

    private Urls() {
    }

    public static final String LOGIN = "/login.jsp";
    public static final String LOGOUT = "/servlet/LoginController/logout";
    public static final String HOME = "/dashboards/-1/view?dashboardPageType=MAIN";
    public static final String SUPPORT = "/support";
    public static final String SITE_MAP = "/sitemap";
    public static final String EDIT = "/edit";
    
    public static final class Ami {

        public static final String AMI = "/dashboards/-2/view?dashboardPageType=AMI";
        public static final String DASHBOARD = "/meter/start";
        public static final String BILLING = "/billing/home";
        public static final String BULK_IMPORT = "/bulk/import/upload";
        public static final String BULK_UPDATE = "/bulk/update/upload";
        public static final String POINT_IMPORT = "/bulk/pointImport/upload";
        public static final String LEGACY_IMPORTER = "/amr/bulkimporter/home";
        public static final String REPORTS = "/analysis/Reports.jsp?groupType=METERING";
        
        public static final String METER_DETAIL = "/meter/home?deviceId=";
    }
    
    public static final class Meter {
        
        public static final String DETAIL = "/meter/home?deviceId=";
    }

    public static final class DemandResponse {

        public static final String DASHBOARD = "/dr/home";
        public static final String SCENARIOS = "/dr/scenario/list";
        public static final String CONTROL_AREA = "/dr/controlArea/list";
        public static final String PROGRAMS = "/dr/program/list";
        public static final String LOAD_GROUPS = "/dr/loadGroup/list";
        public static final String ESTIMATE_LOAD = "/dr/estimatedLoad/home";
        public static final String SETUP = "/dr/setup/list";
        public static final String BULK_UPDATE = "/bulk/update/upload";
        public static final String REPORTS = "/analysis/Reports.jsp?groupType=LOAD_MANAGEMENT";
        
        public static final String SETUP_FILTER = "/dr/setup/filter?filterByType=";
        
        public static final String LOAD_GROUP_CREATE = "/dr/setup/loadGroup/create";
        public static final String LOAD_GROUP_DETAIL = "/dr/setup/loadGroup/";
        public static final String LOAD_GROUP_EDIT = "/dr/setup/loadGroup/";
        public static final String LOAD_GROUP_SETUP_LIST = "/dr/setup/filter?filterByType=LOAD_GROUP";
        
        public static final String LOAD_PROGRAM_CREATE = "/dr/setup/loadProgram/create";
        public static final String LOAD_PROGRAM_DETAILS = "/dr/setup/loadProgram/";
        public static final String LOAD_PROGRAM_EDIT = "/dr/setup/loadProgram/";
        public static final String LOAD_PROGRAM_SETUP_LIST = "/dr/setup/filter?filterByType=LOAD_PROGRAM";
        
        public static final String CONTROL_AREA_CREATE = "/dr/setup/controlArea/create";
        public static final String CONTROL_AREA_DETAILS = "/dr/setup/controlArea/";
        public static final String CONTROL_AREA_EDIT = "/dr/setup/controlArea/";
        public static final String CONTROL_AREA_SETUP_LIST = "/dr/setup/filter?filterByType=CONTROL_AREA";
        
        public static final String ACTIVE_CONTROL_AREAS = "/dr/controlArea/list?state=active";
        public static final String ACTIVE_PROGRAMS = "/dr/program/list?state=ACTIVE";
        public static final String ACTIVE_LOAD_GROUPS = "/dr/loadGroup/list?state=active";
        
        public static final String SCENARIO_CREATE = "/dr/setup/controlScenario/create";
        public static final String SCENARIO_DETAILS = "/dr/setup/controlScenario/";
        public static final String SCENARIO_EDIT = "/dr/setup/controlScenario/";
        public static final String SCENARIO_SETUP_LIST = "/dr/setup/filter?filterByType=CONTROL_SCENARIO";        

        public static final String CI_CURTAILMENT = "/dr/cc/home";

        public static final String CI_PROGRAM_LIST = "/dr/cc/programList";
        public static final String CI_PROGRAM_CREATE = "/dr/cc/programCreate";
        public static final String CI_PROGRAM_EDIT = "/dr/cc/programDetail/";

        public static final String CI_GROUP_LIST = "/dr/cc/groupList";
        public static final String CI_GROUP_CREATE = "/dr/cc/groupCreate";
        public static final String CI_GROUP_EDIT = "/dr/cc/groupDetail/";

        public static final String CI_CUSTOMER_LIST = "/dr/cc/customerList";
    }
    
    public static final class Filters {
        public static final String LOAD_GROUP = "LOAD_GROUP";
        public static final String LOAD_PROGRAM = "LOAD_PROGRAM";
        public static final String CONTROL_AREA = "CONTROL_AREA";
        public static final String CONTROl_SCENARIO = "CONTROL_SCENARIO";
    }

    public static final class CapControl {

        public static final String DASHBOARD = "/capcontrol/tier/areas";
        public static final String SCHEDULES = "/capcontrol/schedules";
        public static final String STRATEGIES = "/capcontrol/strategies";
        public static final String RECENT_TEMP_MOVES = "/capcontrol/move/movedCapBanks";
        public static final String ORPHANS = "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubstations__";
        public static final String REGULATOR_SETUP = "/capcontrol/regulator-setup";
        public static final String IMPORT = "/capcontrol/import/view";
        public static final String REPORTS = "/analysis/Reports.jsp?groupType=CAP_CONTROL";

        public static final String POINT_IMPORT = "/bulk/pointImport/upload";

        public static final String STRATEGIES_CREATE = "/capcontrol/strategies/create";
        public static final String MOVED_CAP_BANKS = "/capcontrol/move/movedCapBanks";

        public static final String SUBSTATION_LIST = "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubstations__";
        public static final String SUBSTATION_CREATE = "/capcontrol/substations/create";
        public static final String SUBSTATION_DETAIL = "/capcontrol/substations/";
        public static final String SUBSTATION_EDIT = "/capcontrol/substations/";

        public static final String SUBSTATION_BUS_LIST = "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubBuses__";
        public static final String SUBSTATION_BUS_CREATE = "/capcontrol/buses/create";
        public static final String SUBSTATION_BUS_DETAIL = "/capcontrol/buses/";
        public static final String SUBSTATION_BUS_EDIT = "/capcontrol/buses/";

        public static final String FEEDER_LIST = "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oFeeders__";
        public static final String FEEDER_CREATE = "/capcontrol/feeders/create";
        public static final String FEEDER_DETAIL = "/capcontrol/feeders/";
        public static final String FEEDER_EDIT = "/capcontrol/feeders/";

        public static final String CAP_BANK_LIST = "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oBanks__";
        public static final String CAP_BANK_CREATE = "/capcontrol/capbanks/create";
        public static final String CAP_BANK_DETAIL = "/capcontrol/capbanks/";
        public static final String CAP_BANK_EDIT = "/capcontrol/capbanks/";

        public static final String CBC_LIST = "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oCBCs__";
        public static final String CBC_CREATE = "/capcontrol/cbc/create";
        public static final String CBC_DETAIL = "/capcontrol/cbc/";
        public static final String CBC_EDIT = "/capcontrol/cbc/";

        public static final String REGULATOR_LIST = "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oRegulators__";
        public static final String REGULATOR_CREATE = "/capcontrol/regulators/create";
        public static final String REGULATOR_DETAIL = "/capcontrol/regulators/";
        public static final String REGULATOR_EDIT = "/capcontrol/regulators/";

        public static final String DMV_TEST_LIST = "/capcontrol/dmvTestList";
        public static final String DMV_TEST_CREATE = "/capcontrol/dmvTest/create";

        public static final String AREA_CREATE = "/capcontrol/areas/create";
        public static final String AREA_DETAIL = "/capcontrol/areas/";
        public static final String AREA_EDIT = "/capcontrol/areas/";

        public static final String SPECIAL_AREA_CREATE = "/capcontrol/areas/special/create";
        
        public static final String SCHEDULES_LIST = "/capcontrol/schedules";
        public static final String SCHEDULES_ASSIGNMENTS = "/capcontrol/schedules/assignments";
    }

    public static final class Assets {

        public static final String DASHBOARD = "/stars/operator/inventory/home";
        public static final String GATEWAYS = "/stars/gateways";
        public static final String RELAYS = "/stars/relay";
        public static final String RTUS = "/stars/rtu-list";
        public static final String OPT_OUT_STATUS = "/stars/operator/optOut/admin";
        public static final String WORK_ORDERS = "/operator/WorkOrder/WorkOrder.jsp";
        public static final String IMPORT = "/stars/operator/account/accountImport";
        public static final String REPORTS = "/analysis/Reports.jsp?groupType=STARS";
        public static final String COMM_CHANNELS_LIST = "/stars/device/commChannel/list";
        public static final String COMM_CHANNEL_DETAIL = "/stars/device/commChannel/";
    }
    
    public static final class Tools {
        
        public static final String COLLECTION_ACTIONS = "/collectionActions/home";
        public static final String COMMANDER = "/tools/commander";
        public static final String DATA_EXPORTER = "/tools/data-exporter/view";
        public static final String DATA_VIEWER = "/tools/data-viewer";
        public static final String DEVICE_CONFIGURATION = "/deviceConfiguration/home";
        public static final String DEVICE_GROUP = "/group/editor/home";
        public static final String SCHEDULES = "/group/scheduledGroupRequestExecutionResults/jobs";
        public static final String SCRIPTS = "/macsscheduler/schedules/view";
        public static final String TRENDS = "/tools/trends";
        public static final String REPORTS = "/analysis/Reports.jsp";
    }
    
    public static final class Admin {
        
        public static final String CONFIGURATION = "/admin/config/view";
        public static final String ENERGY_COMPANY = "/admin/energyCompany/home";
        public static final String MAINTENANCE = "/admin/maintenance/view";
        public static final String MULTI_SPEAK = "/multispeak/setup/home";
        public static final String SUBSTATIONS = "/admin/substations/routeMapping/view";
        public static final String USERS_AND_GROUPS = "/admin/users-groups/home";
        public static final String REPORTS = "/analysis/Reports.jsp?groupType=ADMINISTRATIVE";
        
        public static final String USER_DETAILS = "/admin/users/";
        public static final String USER_EDIT = "/admin/users/";
        public static final String ROLE_GROUP_DETAILS = "/admin/role-groups/";
        public static final String ROLE_GROUP_EDIT = "/admin/role-groups/";
        public static final String USER_GROUP_DETAILS = "/admin/user-groups/";
        public static final String USER_GROUP_EDIT = "/admin/user-groups/";
        public static final String USERS_TAB = "/admin/users-groups/home#users-tab";
        public static final String USER_GROUPS_TAB = "/admin/users-groups/home#users-groups-tab";
        public static final String ROLE_GROUPS_TAB = "/admin/users-groups/home#role-groups-tab";
        
        public static final String CREATE_ENERGY_COMPANY = "/admin/energyCompany/new?create=";
        public static final String ENERGY_COMPANY_GENERAL_INFO = "/admin/energyCompany/general/view?ecId=";
        public static final String ENERGY_COMPANY_LIST = "/admin/energyCompany/home";
        public static final String ENERGY_COMPANY_EDIT = "/admin/energyCompany/general/update";
        public static final String ENERGY_COMPANY_DELETE = "/admin/energyCompany/general/delete?ecId=";
        public static final String ENERGY_COMPANY_WAREHOUSE_LIST = "/admin/energyCompany/warehouse/home?ecId=";
        public static final String ENERGY_COMPANY_WAREHOUSE_CREATE = "/admin/energyCompany/warehouse/new?ecId=";
        public static final String ENERGY_COMPANY_OPERATOR_USER_CREATE = "/admin/energyCompany/operatorLogin/new?ecId=";
        public static final String ENERGY_COMPANY_OPERATOR_USER_LIST = "/admin/energyCompany/operatorLogin/home?ecId=";
        public static final String ENERGY_COMPANY_OPERATOR_USER_EDIT = "/admin/energyCompany/operatorLogin/edit?ecId=";
        public static final String ENERGY_COMPANY_OPERATOR_USER_DETAILS = "/admin/energyCompany/operatorLogin/view?ecId=";
        public static final String ENERGY_COMPANY_OPERATOR_LOGIN_ID = "&operatorLoginId=";
    }    
}
