/*
 * Created on Feb 2, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis;

import java.util.Vector;

import com.cannontech.analysis.controller.*;
import com.cannontech.analysis.tablemodel.*;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;

/**
 * @author snebben
 *
 */
public enum ReportTypes {
    
    SYSTEM_LOG("System Log", SystemLogModel.class, ReportGroup.ADMINISTRATIVE),
    POINT_DATA_INTERVAL("Point Data Interval", PointDataIntervalModel.class, ReportGroup.ADMINISTRATIVE),
    POINT_DATA_SUMMARY("Point Data Summary", PointDataSummaryModel.class, ReportGroup.ADMINISTRATIVE),  //Peaks/Usage
    
    CARRIER_DATA("Carrier Data", CarrierDBModel.class, ReportGroup.DATABASE),
    CARRIER_ROUTE_MACRO_DATA("Carrier Route Macro Data", RouteDBModel.class, ReportGroup.DATABASE),
    CARRIER_ROUTE_DATA("Carrier Route Data", RouteMacroModel.class, ReportGroup.DATABASE),
    REPEATER_ROLE_COLLISION("Repeater Role Collision", RepeaterRoleCollisionModel.class, ReportGroup.DATABASE),
    
    METER_READ("Meter Reads", MeterReadModel.class, ReportGroup.METERING),
    METER_OUTAGE_LOG("Meter Outages Log", MeterOutageModel.class, ReportGroup.METERING),
    METER_OUTAGE_COUNT("Meter Outage Counts", MeterOutageCountModel.class, ReportGroup.METERING),
    METER_DISCONNECT_STATUS("Meter Disconnect Status", DisconnectModel.class, ReportGroup.METERING),
    LP_SETUP_DATA("Load Profile Setup Data", LPSetupDBModel.class, ReportGroup.METERING),
    LP_POINT_DATA_SUMMARY("LP Point Data Summary", LPDataSummaryModel.class, ReportGroup.METERING),
    SCHEDULED_METER_READS("Scheduled Meter Reads", ScheduledMeterReadModel.class, ReportGroup.METERING),
    METER_USAGE("Meter Usage", MeterUsageModel.class, ReportGroup.METERING),
    DISCONNECT_COLLAR_DATA("Disconnect Collar Data", DisconnectCollarController.class, ReportGroup.METERING),
    SCAN_RATE_SETUP_DATA("Scan Rate Setup Data", ScanRateSetupDBModel.class, ReportGroup.METERING),
    
    LM_SYSTEM_LOG("Load Management System Log", LMControlLogModel.class, ReportGroup.LOAD_MANAGEMENT),
    LG_ACCOUNTING("Load Group Accounting", LoadGroupModel.class, ReportGroup.LOAD_MANAGEMENT),
    LM_DAILY_PEAKS("Load Management Daily Peaks", DailyPeaksModel.class, ReportGroup.LOAD_MANAGEMENT),
    LOAD_CONTROL_VERIFICATION("Load Control Verification", LoadControlVerificationModel.class, ReportGroup.LOAD_MANAGEMENT),
    
    /** Cap Bank Reports */
    CAP_BANK_DETAILS("Cap Bank Details Report", CapBankListModel.class, ReportGroup.CAP_CONTROL,
            "Detailed report of the capbanks, including information on its location, size, cbc, feeder and substation bus."),
    CAP_CONTROL_NEW_ACTIVITY("New Activity Report", CapControlNewActivityModel.class, ReportGroup.CAP_CONTROL,
            "Detailed report of capbank activity, including manual and automated controls, operation outcomes, and capcontrol database changes."),
    CAP_CONTROL_CURRENT_STATUS("Current Status Report", CapControlCurrentStatusModel.class, ReportGroup.CAP_CONTROL,
            "Detailed report of cap bank statuses, based on var response information"),
    CAP_CONTROL_SCHEDULE_ACTIVITY("Schedule Activity Report", CapControlEventLogModel.class, ReportGroup.CAP_CONTROL,
            "Detailed report of ending bank status activity or all schedule activity"),
    MAX_DAILY_OPERATIONS("Max Daily Operations Report", MaxDailyOpsModel.class, ReportGroup.CAP_CONTROL,
            "Detailed report showing a 6 week history of capbanks that have reached max daily operations limit."),
    CAP_CONTROL_STATE_COMPARISON("State Comparisons Report", CapControlStateComparisonController.class, ReportGroup.CAP_CONTROL,
            "Detailed report showing a comparison of capbank states set by var response against its two way cbc reported state."),
    CAP_CONTROL_OPERATIONS("Operations Report", CapControlOperationsController.class, ReportGroup.CAP_CONTROL,
            "Detailed report showing capbank operations and their related outcomes."),
    CBC_INVENTORY("CBC Inventory Report", CBCInventoryController.class, ReportGroup.CAP_CONTROL,
            "Detailed report showing cbc information and attached parent objects."),
    CAP_CONTROL_CONFIRMATION_PERCENTAGE("Confirmation Percentage Report", CapControlConfirmationPercentageController.class, ReportGroup.CAP_CONTROL,
            "Detailed report showing operational percentages for failed, questionable, and successful operation outcomes."),
    CAP_CONTROL_RETRIES("Retries Report", CapControlRetriesController.class, ReportGroup.CAP_CONTROL,
            "Detailed report showing capbank control retry information."),
    CAP_BANK_OPERATIONS_PERFORMANCE("Cap Bank Operations Performance Report", CapBankOperationsPerformanceController.class, ReportGroup.CAP_CONTROL,
            "Detailed report showing capbank operational information for combinations of failed, questionable, and successful outcomes based on a user entered percentage level."),
    CAP_CONTROL_SCHEDULE_DETAIL("Schedule Detail Report", CapControlScheduleDetailController.class, ReportGroup.CAP_CONTROL,
            "Detailed report showing all capcontrol schedules and thier specifics."),
    CAP_CONTROL_DISABLED_DEVICES("Disabled Devices Report", CapControlDisabledDevicesController.class, ReportGroup.CAP_CONTROL,
            "Report showing disabled capcontrol devices."),
    CAP_CONTROL_MAINTENANCE_PENDING("Maintenance Pending Report", CapControlMaintenancePendingController.class, ReportGroup.CAP_CONTROL,
            "Report showing sites pending maintenance."),
    CAP_CONTROL_MAX_OPS_ALARMS("Max Ops Alarms Report", CapBankMaxOpsAlarmsController.class, ReportGroup.CAP_CONTROL,
            "Report showing capbanks with max operation alarms"),
    CAP_CONTROL_ABNORMAL_TELEMETRY_DATA("Abnormal Telemetry Data Report", AbnormalTelemetryDataController.class, ReportGroup.CAP_CONTROL,
            "Report showing feeders with abnormal telemetry data."),
    CAP_CONTROL_VAR_CHANGE("Var Change Report", CapControlVarChangeController.class, ReportGroup.CAP_CONTROL,
            "Report showing change in var values."),
    CAP_CONTROL_ASSET_AVAILABILITY("Asset Availability Report", CapControlAssetAvailabilityController.class, ReportGroup.CAP_CONTROL,
            "Report showing asset availability."),
    CAP_CONTROL_TIME_CONTROLLED_CAPBANKS("Time Controlled Cap Banks Report", TimeControlledCapBanksController.class, ReportGroup.CAP_CONTROL,
            "Report showing all time controlled capbanks."),
    VAR_IMBALANCE_ON_EXECUTION("Var Imbalance on Execution Report", VarImbalanceOnExecutionController.class, ReportGroup.CAP_CONTROL,
            "Report showing the var imbalance on execution."),
    
    COMM_STATISTICS("Communication Statistics", StatisticModel.class, ReportGroup.STATISTICAL),
    //STATISTIC_HISTORY_PORT_DATA("Historical Daily Port Statistics", StatisticHistoryPortModel.class, ReportGroup.STATISTICAL),
    //STATISTIC_HISTORY_DEVICE_DATA("Historical Daily Device Statistics", StatisticHistoryDeviceModel.class, ReportGroup.STATISTICAL),
    //STATISTIC_HISTORY_TRANSMITTER_DATA("Historical Daily Transmitter Statistics", StatisticHistoryTransmitterModel.class, ReportGroup.STATISTICAL),
    
    EC_ACTIVITY_LOG_SUMMARY("Energy Company Activity Log Summary", ActivityModel.class, ReportGroup.STARS),
    EC_ACTIVITY_LOG_DETAIL("Energy Company Activity Log Detail", ActivityDetailModel.class, ReportGroup.STARS),
    PROGRAM_DETAIL("Progam Detail", ProgramDetailModel.class, ReportGroup.STARS),
    EC_WORK_ORDER("Work Order", WorkOrderModel.class, ReportGroup.STARS),
    STARS_LM_SUMMARY("STARS Load Management Summary", StarsLMSummaryModel.class, ReportGroup.STARS),
    STARS_LM_DETAIL("STARS Load Management Detail", StarsLMDetailModel.class, ReportGroup.STARS),
    LM_CONTROL_DETAIL("Detailed Customer Control Report", LMControlDetailController.class, ReportGroup.STARS),
    //TODO  STARS_AMR_SUMMARY("STARS AMR Summary", StarsAMRSummaryModel.class, ReportGroup.STARS),
    STARS_AMR_DETAIL("STARS AMR Detail", StarsAMRDetailModel.class, ReportGroup.STARS),
    
    // commercial curtailment
    CCURT_EVENT_SUMMARY("Event Summary", CurtailmentEventSummaryController.class, ReportGroup.CCURT),
    CCURT_INTERRUPTION_SUMMARY("Interruption Summary", CurtailmentInterruptionSummaryController.class, ReportGroup.CCURT),
    
        //Custom Settlement data.  Determined by EnergyCompanyID
    HECO_LMEVENT_SUMMARY("LM Event Summary", HECO_LMEventSummaryModel.class, null),
    HECO_MONTHLY_BILLING("Monthly Billing Settlement", HECO_MonthlyBillingSettlementModel.class, null),
    HECO_CUSTOMER_MONTHLY_BILLING("Customer Monthly Billing Settlement", HECO_CustomerMonthlyBillingSettlementModel.class, null),
    HECO_DSMIS("DSMIS Settlement", HECO_DSMISModel.class, null);

    
    @SuppressWarnings("unchecked")
    private ReportTypes(String title, Class modelClass, ReportGroup reportGroup) {
        this.title = title;
        this.modelClass = modelClass;
        this.reportGroup = reportGroup;
    }

    @SuppressWarnings("unchecked")
    private ReportTypes(String title, Class modelClass, ReportGroup reportGroup, String description) {
        this.title = title;
        this.modelClass = modelClass;
        this.reportGroup = reportGroup;
        this.description = description;
    }
    
    private String title;
    /** Class <? extends ReportModelBase> modelClass; OR Class <? extends ReportController> modelClass*/
    @SuppressWarnings("unchecked")
    private Class modelClass;
    private ReportGroup reportGroup;
    private String description;
    
    public String getDescription() {
        return description;
    }

    @SuppressWarnings("unchecked")
    public Class getModelClass() {
        return modelClass;
    }

    public ReportGroup getReportGroup() {
        return reportGroup;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Retuns int[] of settlement reportTypes for yukonDefID 
     * @param yukonDefID
     * @return
     */
    public static Vector<ReportTypes> getSettlementReportTypes(int yukonDefID)
    {
        switch (yukonDefID)
        {
            case YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO:
            {
                Vector<ReportTypes> reportTypes = new Vector<ReportTypes>(4);
                reportTypes.add(HECO_LMEVENT_SUMMARY);
                reportTypes.add(HECO_MONTHLY_BILLING);
                reportTypes.add(HECO_CUSTOMER_MONTHLY_BILLING);
                reportTypes.add(HECO_DSMIS);
            }
//          case YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_XCEL_ISOC:   //TODO
            default :
                return new Vector<ReportTypes>();
        }
    }
    /**
     * Returns int[] of all reportTypes for groupID
     * @param groupID
     * @return
     */
    public static Vector<ReportTypes> getGroupReportTypes(ReportGroup reportGroup){
        
        Vector<ReportTypes> reportTypes = new Vector<ReportTypes>();
        
        for(ReportTypes reportType: ReportTypes.values()) {
            if ( reportType.reportGroup == reportGroup)
                reportTypes.add(reportType);                
        }
        return reportTypes;
    }
    
    /**
     * Creates and returns an instance of ReportModelBase (or extended class) for type
     * @return com.cannontech.database.model.DBTreeModel
     * @param type int
     */
    @SuppressWarnings("unchecked")
    public static ReportController create(ReportTypes reportType) {
    
        ReportController returnVal = null;
        
        try
        {
            // What's about to happen here isn't pretty.
            // I'm trying to bridge the gap between the old
            // way of creating reports and the new way.
            Class modelClass = reportType.getModelClass();
            if (ReportController.class.isAssignableFrom(modelClass)) {
                returnVal = (ReportController) modelClass.newInstance();
            } else {
                ReportModelBase oldModel = (ReportModelBase) modelClass.newInstance();
                returnVal = new ReportControllerAdapter(oldModel);
            }
            
        }
        catch( IllegalAccessException e1 )
        {
            CTILogger.error( e1.getMessage(), e1 );
        }
        catch( InstantiationException e2 )
        {
            CTILogger.error( e2.getMessage(), e2 );
        }
        return returnVal;
    }
}
