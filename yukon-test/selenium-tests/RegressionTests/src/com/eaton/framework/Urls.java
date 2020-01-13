package com.eaton.framework;

public final class Urls {

    private Urls() {
    }

    public static final String LOGIN = "/login.jsp";

    public static final class CapControl {

        private CapControl() {
        }

        public static final String DASHBOARD = "/capcontrol/tier/areas";
        public static final String SCHEDULES = "/capcontrol/schedules";

        public static final String STRATEGIES = "/capcontrol/strategies";
        public static final String STRATEGIES_CREATE = "/capcontrol/strategies/create";

        public static final String POINT_IMPORT = "/bulk/pointImport/upload";
        public static final String REPORTS = "/analysis/Reports.jsp?groupType=CAP_CONTROL";

        public static final String MOVED_CAP_BANKS = "/capcontrol/move/movedCapBanks";

        public static final String SUBSTATION_LIST = "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubstations__";
        public static final String SUBSTATION_CREATE = "/capcontrol/substations/create";

        public static final String SUBSTATION_BUS_LIST = "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubBuses__";
        public static final String SUBSTATION_BUS_CREATE = "/capcontrol/buses/create";

        public static final String FEEDER_LIST = "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oFeeders__";
        public static final String FEEDER_CREATE = "/capcontrol/feeders/create";

        public static final String CAP_BANK_LIST = "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oBanks__";
        public static final String CAP_BANK_CREATE = "/capcontrol/capbanks/create";

        public static final String CBC_LIST = "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oCBCs__";
        public static final String CBC_CREATE = "/capcontrol/cbc/create";

        public static final String REGULATOR_LIST = "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oRegulators__";
        public static final String REGULATOR_CREATE = "/capcontrol/regulators/create";

        public static final String REGULATOR_SETUP = "/capcontrol/regulator-setup";

        public static final String DMV_TEST_LIST = "/capcontrol/dmvTestList";
        public static final String DMV_TEST_CREATE = "/capcontrol/dmvTest/create";

        public static final String AREA_CREATE = "/capcontrol/areas/create";

        public static final String SPECIAL_AREA_CREATE = "/capcontrol/areas/special/create";
    }

    public static final class DemandResponse {

        private DemandResponse() {
        }

        public static final String DASHBOARD = "/dr/home";
        public static final String SCENARIOS = "/dr/scenario/list";
        public static final String CONTROL_AREA = "/dr/controlArea/list";
        public static final String PROGRAMS = "/dr/program/list";
        public static final String LOAD_GROUPS = "/dr/loadGroup/list";
        public static final String ESTIMATE_LOAD = "/dr/estimatedLoad/home";
        public static final String SETUP = "/dr/setup/list";
        public static final String BULK_UPDATE = "/bulk/update/upload";
        public static final String REPORTS = "/analysis/Reports.jsp?groupType=LOAD_MANAGEMENT";

        public static final String CI_CURTAILMENT = "/dr/cc/home";

        public static final String CI_PROGRAM_LIST = "/dr/cc/programList";
        public static final String CI_PROGRAM_CREATE = "/dr/cc/programCreate";
        public static final String CI_PROGRAM_EDIT = "/dr/cc/programDetail/";

        public static final String CI_GROUP_LIST = "/dr/cc/groupList";
        public static final String CI_GROUP_CREATE = "/dr/cc/groupCreate";
        public static final String CI_GROUP_EDIT = "/dr/cc/groupDetail/";

        public static final String CI_CUSTOMER_LIST = "/dr/cc/customerList";
    }

}
