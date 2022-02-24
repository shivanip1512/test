type MenuItem = {
    titleKey: string | undefined;
    link: string | undefined;
    dividerBefore?: boolean;
};

export const AMI_MENU: MenuItem[] = [
    { titleKey: 'menu.dashboard', link: '/meter/start'},
    { titleKey: 'menu.ami.billing', link: '/billing/home'},
    { titleKey: 'menu.ami.meterProgramming', link: '/amr/meterProgramming/home'},
    { titleKey: 'menu.ami.bulkImport', link: '/bulk/import/upload', dividerBefore: true},
    { titleKey: 'menu.bulkUpdate', link: '/bulk/update/upload'},
    { titleKey: 'menu.ami.legacyImporter', link: '/amr/bulkimporter/home'},
    { titleKey: 'menu.pointImport', link: '/bulk/pointImport/upload'},
    { titleKey: 'menu.reports', link: '/analysis/Reports.jsp?groupType=METERING'},
]

export const DR_MENU: MenuItem[] = [
    { titleKey: 'menu.dashboard', link: '/dr/home'},
    { titleKey: 'menu.dr.scenarios', link: '/dr/scenario/list'},
    { titleKey: 'menu.dr.controlAreas', link: '/dr/controlArea/list'},
    { titleKey: 'menu.dr.programs', link: '/dr/program/list'},
    { titleKey: 'menu.dr.loadGroups', link: '/dr/loadGroup/list'},
    { titleKey: 'menu.dr.estimatedLoad', link: '/dr/estimatedLoad/home'},
    { titleKey: 'menu.dr.setup', link: '/dr/setup/list'},
    { titleKey: 'menu.dr.ciCurtailment', link: '/dr/cc/home'},
    { titleKey: 'menu.bulkUpdate', link: '/bulk/update/upload', dividerBefore: true},
    { titleKey: 'menu.reports', link: '/analysis/Reports.jsp?groupType=LOAD_MANAGEMENT'},
]

export const VOLT_VAR_MENU: MenuItem[] = [
    { titleKey: 'menu.dashboard', link: '/capcontrol/tier/areas'},
    { titleKey: 'menu.voltvar.schedules', link: '/capcontrol/schedules'},
    { titleKey: 'menu.voltvar.strategies', link: '/capcontrol/strategies'},
    { titleKey: 'menu.voltvar.tempMoves', link: '/capcontrol/move/movedCapBanks'},
    { titleKey: 'menu.voltvar.orphans', link: '/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubstations__'},
    { titleKey: 'menu.voltvar.regulatorSetup', link: '/capcontrol/regulator-setup'},
    { titleKey: 'menu.voltvar.dmvTest', link: '/capcontrol/dmvTestList'},
    { titleKey: 'menu.voltvar.import', link: '/capcontrol/import/view', dividerBefore: true},
    { titleKey: 'menu.pointImport', link: '/bulk/pointImport/upload'},
    { titleKey: 'menu.voltvar.manageFDRTranslations', link: '/bulk/fdrTranslationManager/home'},
    { titleKey: 'menu.reports', link: '/analysis/Reports.jsp?groupType=CAP_CONTROL'},
]

export const ASSETS_MENU: MenuItem[] = [
    { titleKey: 'menu.dashboard', link: '/stars/operator/home'},
    { titleKey: 'menu.assets.gateways', link: '/stars/gateways'},
    { titleKey: 'menu.assets.relays', link: '/stars/relay'},
    { titleKey: 'menu.assets.rtus', link: '/stars/rtu-list'},
    { titleKey: 'menu.assets.virtualDevices', link: '/stars/virtualDevices'},
    { titleKey: 'menu.assets.optOutStatus', link: '/stars/operator/optOut/admin'},
    { titleKey: 'menu.assets.workOrders', link: '/operator/WorkOrder/WorkOrder.jsp'},
    { titleKey: 'menu.assets.import', link: '/stars/operator/account/accountImport', dividerBefore: true},
    { titleKey: 'menu.reports', link: '/analysis/Reports.jsp?groupType=STARS'},
]

export const TOOLS_MENU: MenuItem[] = [
    { titleKey: 'menu.tools.collectionActions', link: '/collectionActions/home'},
    { titleKey: 'menu.tools.commander', link: '/tools/commander'},
    { titleKey: 'menu.tools.dataExport', link: '/tools/data-exporter/view'},
    { titleKey: 'menu.tools.dataStreaming', link: '/tools/dataStreaming/configurations'},
    { titleKey: 'menu.tools.dataViewer', link: '/tools/data-viewer'},
    { titleKey: 'menu.tools.deviceConfiguration', link: '/deviceConfiguration/home'},
    { titleKey: 'menu.tools.deviceGroups', link: '/group/editor/home'},
    { titleKey: 'menu.tools.schedules', link: '/group/scheduledGroupRequestExecutionResults/jobs'},
    { titleKey: 'menu.tools.scripts', link: '/macsscheduler/schedules/view'},
    { titleKey: 'menu.tools.trends', link: '/tools/trends'},
    { titleKey: 'menu.reports', link: '/analysis/Reports.jsp', dividerBefore: true},
]

export const ADMIN_MENU: MenuItem[] = [
    { titleKey: 'menu.admin.configuration', link: '/admin/config/view'},
    { titleKey: 'menu.admin.energyCompany', link: '/admin/energyCompany/home'},
    { titleKey: 'menu.admin.maintenance', link: '/admin/maintenance/view'},
    { titleKey: 'menu.admin.multispeak', link: '/multispeak/setup/home'},
    { titleKey: 'menu.admin.substations', link: '/admin/substations/routeMapping/view'},
    { titleKey: 'menu.admin.usersAndGroups', link: '/admin/users-groups/home'},
    { titleKey: 'menu.reports', link: '/analysis/Reports.jsp?groupType=ADMINISTRATIVE', dividerBefore: true},
]