package com.cannontech.analysis;

import java.util.Vector;

import com.cannontech.analysis.controller.*;
import com.cannontech.analysis.tablemodel.*;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;

public enum ReportTypes {
    
    SYSTEM_LOG("System Log", SystemLogModel.class, ReportGroup.ADMINISTRATIVE,
        "Detailed report of actions, including username and time, which have occurred in the Yukon database."),
    POINT_DATA_INTERVAL("Point Data Interval", PointDataIntervalModel.class, ReportGroup.ADMINISTRATIVE,
        "Detailed report of archived point data values, timestamp, and quality."),
    POINT_DATA_SUMMARY("Point Data Summary", PointDataSummaryModel.class, ReportGroup.ADMINISTRATIVE,  
        "Summary report of archived point data values and timestamps; includes 6 peaks and lows per point."), //Peaks/Usage
    DEVICE_READINGS("Device Readings", DeviceReadingsController.class, ReportGroup.ADMINISTRATIVE,
        "Details report of archived point data values and timestamps, based on attribute selection."),
    DEVICE_READ_STATISTICS_SUMMARY("Device Read Statistics Summary", DeviceReadStatisticsSummaryController.class, ReportGroup.ADMINISTRATIVE,
        "Summary report of percentage of devices with archived readings over selected dates; aggregated by device group."),
        
    CARRIER_DATA("Carrier Data", CarrierDBModel.class, ReportGroup.DATABASE,
        "Utility report for Carrier devices; includes name, disabled status, type, meter number, address, and route."),
    CARRIER_ROUTE_MACRO_DATA("Carrier Route Macro Data", RouteMacroModel.class, ReportGroup.DATABASE,
        "Utility report for macro routes; includes assigned routes, transmitters, CCU bus numbers, CCU amp use, fixed and variable bits."),
    CARRIER_ROUTE_DATA("Carrier Route Data", RouteDBModel.class, ReportGroup.DATABASE,
        "Utility report identifying routes and meter assignments; includes meter name, meter number, address, and type."),
    REPEATER_ROLE_COLLISION("Repeater Role Collision", RepeaterRoleCollisionModel.class, ReportGroup.DATABASE,
        "Utility report identifying routes with same fixed and variable bits."),
        
    METER_READ("Meter Reads", MeterReadModel.class, ReportGroup.METERING,
        "Detailed report of successful or missed meter reads, based on the latest archived point for a meter."),
    METER_OUTAGE_LOG("Meter Outages Log", MeterOutageModel.class, ReportGroup.METERING,
        "Detailed report of archived outage log data per meter; includes meter information, time and duration of outage."),
    METER_OUTAGE_COUNT("Meter Outage Counts", MeterOutageCountModel.class, ReportGroup.METERING,
        "Detailed report of archived blink counts by meter; includes time, values, and delta (count) between readings."),
    METER_DISCONNECT_STATUS("Meter Disconnect Status", DisconnectModel.class, ReportGroup.METERING,
        "Detailed report of archived disconnect status by meter; includes meter information, collar address, time, and disconnect status."),
    LP_SETUP_DATA("Load Profile Setup Data", LPSetupDBModel.class, ReportGroup.METERING,
        "Utility report for meters and profile configurations; includes meter information and profile intervals (as defined in meter setup)."),
    LP_POINT_DATA_SUMMARY("LP Point Data Summary", LPDataSummaryModel.class, ReportGroup.METERING,
        "Summary report of archived profile point data values and timestamps; includes 6 peaks and lows per point."),      
    SCHEDULED_METER_READS("Scheduled Meter Reads (MACS)", ScheduledMeterReadModel.class, ReportGroup.METERING,
        "Detailed report of MACS schedules; includes schedule start/stop; request start/stop, and status codes for meters processed."),
    METER_USAGE("Meter Usage", MeterUsageController.class, ReportGroup.METERING,
        "Detailed report of archived kWh readings and calculated usage (delta between readings)."),
    MCT_CONFIG("MCT 430/470 Config To Device", MCTConfigController.class, ReportGroup.METERING,
        "Utility report identifying 430/470 MCT meters and configuration assignments; meter information, profile and demand rates."),
    DISCONNECT_COLLAR_DATA("Disconnect Collar Data", DisconnectCollarController.class, ReportGroup.METERING,
        "Utility report for meters with disconnect collar assignments; includes meter information and collar address."),
    SCAN_RATE_SETUP_DATA("Scan Rate Setup Data", ScanRateSetupDBModel.class, ReportGroup.METERING,
        "Utility report for meters and scanning configurations; includes meter information and scan intervals (as defined in meter setup)."),
        
    LM_SYSTEM_LOG("Load Management System Log", LMControlLogModel.class, ReportGroup.LOAD_MANAGEMENT,
        "Detailed report of load management actions, including username and time, which have occurred in the Yukon database."),
    LG_ACCOUNTING("Load Group Accounting", LoadGroupModel.class, ReportGroup.LOAD_MANAGEMENT,
        "Detailed report of load control events; includes control start/stop times, duration, and aggregated control durations."),
    LM_DAILY_PEAKS("Load Management Daily Peaks", DailyPeaksModel.class, ReportGroup.LOAD_MANAGEMENT,
        "Detailed report of control area peak point data; includes peak and off peak values, quality, and time."),
    LOAD_CONTROL_VERIFICATION("Load Control Verification", LoadControlVerificationModel.class, ReportGroup.LOAD_MANAGEMENT,
        "Utility report for verifying a transmitter sent or a receiver accepted a load control command."),
    LCR3102Data("LCR 3102 Data", LCR3102DataController.class, ReportGroup.LOAD_MANAGEMENT,
        "Detailed report of archived point data from LCR3102 devices; includes serial number, route, run time load, relay shed time, relay, and timestamp."),
    PROGRAM_AND_GEAR_CONTROL("Program and Gear Control", ProgramAndGearControlController.class, ReportGroup.LOAD_MANAGEMENT,
        "Detailed report of load program, control area, or control scenario control events; includes gear information."),
    
    /* Cap Control Reports */
	// Administrative
	CAP_BANK_DETAILS("Cap Bank Details", CapBankListModel.class, ReportGroup.CAP_CONTROL,
        "Detailed report of the capbanks, including information on its location, size, cbc, feeder and substation bus."),
    CBC_INVENTORY("CBC Inventory", CBCInventoryController.class, ReportGroup.CAP_CONTROL,
        "Detailed report showing cbc information and attached parent objects."),
    CBC_SPECIALAREA_ASSIGNMENT("CBC Special Area Assignment ", CBCSpecialAreaAssignmentController.class, ReportGroup.CAP_CONTROL,
        "Report showing the special area assignment for a cbc."),
    STRATEGY_ASSIGNMENT("Strategy Assignment ", StrategyAssignmentController.class, ReportGroup.CAP_CONTROL,
        "Report showing the devices assigned to each strategy."),
    CAP_CONTROL_UNSOLICITED_MESSAGES("Unsolicited Messages", CapControlUnsolicitedMessagesController.class, ReportGroup.CAP_CONTROL,
        "Report showing unsolicited messages received from a cbc."),
    CAP_CONTROL_DISABLED_DEVICES("Disabled Devices", CapControlDisabledDevicesController.class, ReportGroup.CAP_CONTROL,
        "Report showing disabled capcontrol devices."),
    CAP_CONTROL_MAINTENANCE_PENDING("Maintenance Pending", CapControlMaintenancePendingController.class, ReportGroup.CAP_CONTROL,
        "Report showing sites pending maintenance."),
    CAP_CONTROL_ABNORMAL_TELEMETRY_DATA("Abnormal Telemetry Data", AbnormalTelemetryDataController.class, ReportGroup.CAP_CONTROL,
        "Report showing feeders with abnormal telemetry data."),
    CAP_CONTROL_ASSET_UNAVAILABILITY("Asset Unavailability", CapControlAssetUnavailabilityController.class, ReportGroup.CAP_CONTROL,
        "Report showing number of times capbanks were not available for correction when needed."),
    // Schedules
    CAP_CONTROL_SCHEDULE_ACTIVITY("Schedule Activity", CapControlEventLogModel.class, ReportGroup.CAP_CONTROL,
        "Detailed report of ending bank status activity or all schedule activity"),
    CAP_CONTROL_SCHEDULE_DETAIL("Schedule Detail", CapControlScheduleDetailController.class, ReportGroup.CAP_CONTROL,
        "Detailed report showing all capcontrol schedules and their specifics."),
    CAP_CONTROL_TIME_CONTROLLED_CAPBANKS("Time Controlled Cap Banks", TimeControlledCapBanksController.class, ReportGroup.CAP_CONTROL,
        "Report showing all time controlled capbanks."),
    // Performance
    CAP_CONTROL_CURRENT_STATUS("Current Status", CapControlCurrentStatusModel.class, ReportGroup.CAP_CONTROL,
        "Detailed report of cap bank statuses, based on var response information"),
    CAP_CONTROL_STATE_COMPARISON("State Comparisons", CapControlStateComparisonController.class, ReportGroup.CAP_CONTROL,
        "Detailed report showing a comparison of capbank states set by var response against its two-way cbc reported state."),
    CAP_CONTROL_NEW_ACTIVITY("New Activity", CapControlNewActivityModel.class, ReportGroup.CAP_CONTROL,
        "Detailed report of capbank activity, including manual and automated controls, operation outcomes, and capcontrol database changes."),
    CAP_CONTROL_OPERATIONS("Operations", CapControlOperationsController.class, ReportGroup.CAP_CONTROL,
        "Detailed report showing capbank operations and their related outcomes."),
    CAP_BANK_OPERATIONS_PERFORMANCE("Cap Bank Operations Performance", CapBankOperationsPerformanceController.class, ReportGroup.CAP_CONTROL,
        "Detailed report showing capbank operational information for combinations of failed, questionable, and successful outcomes based on a user entered percentage level."),
    CAP_CONTROL_CONFIRMATION_PERCENTAGE("Confirmation Percentage", CapControlConfirmationPercentageController.class, ReportGroup.CAP_CONTROL,
        "Detailed report showing operational percentages for failed, questionable, and successful operation outcomes."),
    CAP_BANK_RECENT_MAX_DAILY_OPERATIONS("Cap Bank Recent Max Daily Operations", CapBankRecentMaxDailyOpsModel.class, ReportGroup.CAP_CONTROL,
        "Detailed report showing a 6 week history of capbanks that have reached max daily operations limit."),
    CAP_BANK_MAX_OPS_EXCEEDED("Cap Bank Max Ops Exceeded", CapBankMaxOpsExceededController.class, ReportGroup.CAP_CONTROL,
        "Report showing capbanks that have reached max daily operations limit for a custom date range."),
    CAP_CONTROL_RETRIES("Retries", CapControlRetriesController.class, ReportGroup.CAP_CONTROL,
        "Detailed report showing capbank control retry information."),
    CAP_CONTROL_VAR_CHANGE("Var Change", CapControlVarChangeController.class, ReportGroup.CAP_CONTROL,
        "Report showing change in var values."),
    VAR_IMBALANCE_ON_EXECUTION("Var Imbalance on Execution", VarImbalanceOnExecutionController.class, ReportGroup.CAP_CONTROL,
        "Report showing the var imbalance on execution."),
    /*END of Cap Control Reports*/
            
    COMM_STATISTICS("Communication Statistics", StatisticModel.class, ReportGroup.STATISTICAL,
        "Detailed report of communication statistics by device."),
    DEVICE_REQUEST_DETAIL("Device Request Detail", DeviceRequestDetailController.class, ReportGroup.STATISTICAL,
        "Detailed report of data requests by devices."),
    
    EC_ACTIVITY_LOG_SUMMARY("Energy Company Activity Log Summary", ActivityModel.class, ReportGroup.STARS,
        "Summary report of energy company activity; includes aggregate count by contact name, username, account number, and action."),
    EC_ACTIVITY_LOG_DETAIL("Energy Company Activity Log Detail", ActivityDetailModel.class, ReportGroup.STARS,
        "Detailed report of energy company activity; includes timestamp, contact name, username, account number, and action."),
    PROGRAM_DETAIL("Program Detail", ProgramDetailModel.class, ReportGroup.STARS,
        "Detailed report of program and account assignments; includes contact name, account number, and status."),
    EC_WORK_ORDER("Work Order", WorkOrderModel.class, ReportGroup.STARS,
        "Individual work orders, formatted as a working document."),
    STARS_LM_SUMMARY("STARS Load Management Summary", StarsLMSummaryModel.class, ReportGroup.STARS,
        "Summary report of hardware assigned to load groups; includes number of receivers in group and number out of service."),
    STARS_LM_DETAIL("STARS Load Management Detail", StarsLMDetailModel.class, ReportGroup.STARS,
        "Detailed report of hardware assigned to load groups; includes account information, device type, and status."),
    LM_CONTROL_SUMMARY("Customer Control Summary", LMControlSummaryController.class, ReportGroup.STARS,
        "Summary report of totalized control information for load programs."),
    LM_CONTROL_DETAIL("Customer Control Detail", LMControlDetailController.class, ReportGroup.STARS,
        "Detailed report of accounts and program enrollment."),
    //TODO  STARS_AMR_SUMMARY("STARS AMR Summary", StarsAMRSummaryModel.class, ReportGroup.STARS),
    STARS_AMR_DETAIL("STARS AMR Detail", StarsAMRDetailModel.class, ReportGroup.STARS,
        "Detailed report of meters assigned to customers; includes account information, meter information, and latest archived kWh."),
    OPT_OUT_INFO("Opt Out Info", OptOutInfoController.class, ReportGroup.STARS, 
        "Detailed report of control history in regards to opt outs; includes account number, serial number, opt out information, and control history for that account during that opt out."),
    OPT_OUT_LIMIT("Opt Out Limit", OptOutLimitController.class, ReportGroup.STARS,
        "Detailed report of opt outs and the number of opt outs used by a customer; includes account number, serial number, alternate tracking number, and opt out information."),
        
    // commercial curtailment
    CCURT_EVENT_SUMMARY("Event Summary", CurtailmentEventSummaryController.class, ReportGroup.CCURT,
        "Detail report of curtailment events for C&I customers."),
    CCURT_INTERRUPTION_SUMMARY("Interruption Summary", CurtailmentInterruptionSummaryController.class, ReportGroup.CCURT,
        "Summary report of curtailment by customer; includes contract hours, remaining hours, used hours, and curtailment setup information."),
    
        //Custom Settlement data.  Determined by EnergyCompanyID
    HECO_LMEVENT_SUMMARY("LM Event Summary", HECO_LMEventSummaryModel.class, null,
        "LM Event Summary."),
    HECO_MONTHLY_BILLING("Monthly Billing Settlement", HECO_MonthlyBillingSettlementModel.class, null,
        "Monthly Billing Settlement."),
    HECO_CUSTOMER_MONTHLY_BILLING("Customer Monthly Billing Settlement", HECO_CustomerMonthlyBillingSettlementModel.class, null,
        "Customer Monthly Billing Settlement."),
    HECO_DSMIS("DSMIS Settlement", HECO_DSMISModel.class, null,
        "DSMIS Settlement.");

    
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
        Vector<ReportTypes> reportTypes = new Vector<ReportTypes>();
        switch (yukonDefID)
        {
            case YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO:
            {
                reportTypes.add(HECO_LMEVENT_SUMMARY);
                reportTypes.add(HECO_MONTHLY_BILLING);
                reportTypes.add(HECO_CUSTOMER_MONTHLY_BILLING);
                reportTypes.add(HECO_DSMIS);
                break;
            }
//          case YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_XCEL_ISOC:   //TODO
        }
        return reportTypes;
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
