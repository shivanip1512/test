package com.cannontech.analysis;

import java.util.Vector;

import com.cannontech.analysis.controller.*;
import com.cannontech.analysis.tablemodel.*;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.i18n.DisplayableEnum;

public enum ReportTypes implements DisplayableEnum {
    
    SYSTEM_LOG(SystemLogModel.class, ReportGroup.ADMINISTRATIVE),
    POINT_DATA_INTERVAL(PointDataIntervalModel.class, ReportGroup.ADMINISTRATIVE),
    POINT_DATA_SUMMARY(PointDataSummaryModel.class, ReportGroup.ADMINISTRATIVE), //Peaks/Usage
    DEVICE_READINGS(DeviceReadingsController.class, ReportGroup.ADMINISTRATIVE),
    DEVICE_READ_STATISTICS_SUMMARY(DeviceReadStatisticsSummaryController.class, ReportGroup.ADMINISTRATIVE),
        
    CARRIER_DATA(CarrierDBModel.class, ReportGroup.DATABASE),
    CARRIER_ROUTE_MACRO_DATA(RouteMacroModel.class, ReportGroup.DATABASE),
    CARRIER_ROUTE_DATA(RouteDBModel.class, ReportGroup.DATABASE),
    REPEATER_ROLE_COLLISION(RepeaterRoleCollisionModel.class, ReportGroup.DATABASE),
        
    METER_READ(MeterReadModel.class, ReportGroup.METERING),
    METER_OUTAGE_LOG(MeterOutageModel.class, ReportGroup.METERING),
    METER_OUTAGE_COUNT(MeterOutageCountModel.class, ReportGroup.METERING),
    METER_DISCONNECT_STATUS(DisconnectModel.class, ReportGroup.METERING),
    LP_SETUP_DATA(LPSetupDBModel.class, ReportGroup.METERING),
    LP_POINT_DATA_SUMMARY(LPDataSummaryModel.class, ReportGroup.METERING),      
    SCHEDULED_METER_READS(ScheduledMeterReadModel.class, ReportGroup.METERING),
    METER_USAGE(MeterUsageController.class, ReportGroup.METERING),
    MCT_CONFIG(MCTConfigController.class, ReportGroup.METERING),
    DISCONNECT_COLLAR_DATA(DisconnectCollarController.class, ReportGroup.METERING),
    SCAN_RATE_SETUP_DATA(ScanRateSetupDBModel.class, ReportGroup.METERING),
        
    LM_SYSTEM_LOG(LMControlLogModel.class, ReportGroup.LOAD_MANAGEMENT),
    LG_ACCOUNTING(LoadGroupModel.class, ReportGroup.LOAD_MANAGEMENT),
    LM_DAILY_PEAKS(DailyPeaksModel.class, ReportGroup.LOAD_MANAGEMENT),
    LOAD_CONTROL_VERIFICATION(LoadControlVerificationModel.class, ReportGroup.LOAD_MANAGEMENT),
    LCR3102Data(LCR3102DataController.class, ReportGroup.LOAD_MANAGEMENT),
    PROGRAM_AND_GEAR_CONTROL(ProgramAndGearControlController.class, ReportGroup.LOAD_MANAGEMENT),
    
    /* Cap Control Reports */
	// Administrative
	CAP_BANK_DETAILS(CapBankListModel.class, ReportGroup.CAP_CONTROL),
    CBC_INVENTORY(CBCInventoryController.class, ReportGroup.CAP_CONTROL),
    CBC_SPECIALAREA_ASSIGNMENT(CBCSpecialAreaAssignmentController.class, ReportGroup.CAP_CONTROL),
    STRATEGY_ASSIGNMENT(StrategyAssignmentController.class, ReportGroup.CAP_CONTROL),
    CAP_CONTROL_UNSOLICITED_MESSAGES(CapControlUnsolicitedMessagesController.class, ReportGroup.CAP_CONTROL),
    CAP_CONTROL_DISABLED_DEVICES(CapControlDisabledDevicesController.class, ReportGroup.CAP_CONTROL),
    CAP_CONTROL_MAINTENANCE_PENDING(CapControlMaintenancePendingController.class, ReportGroup.CAP_CONTROL),
    CAP_CONTROL_ABNORMAL_TELEMETRY_DATA(AbnormalTelemetryDataController.class, ReportGroup.CAP_CONTROL),
    CAP_CONTROL_ASSET_UNAVAILABILITY(CapControlAssetUnavailabilityController.class, ReportGroup.CAP_CONTROL),
    // Schedules
    CAP_CONTROL_SCHEDULE_ACTIVITY(CapControlEventLogModel.class, ReportGroup.CAP_CONTROL),
    CAP_CONTROL_SCHEDULE_DETAIL(CapControlScheduleDetailController.class, ReportGroup.CAP_CONTROL),
    CAP_CONTROL_TIME_CONTROLLED_CAPBANKS(TimeControlledCapBanksController.class, ReportGroup.CAP_CONTROL),
    // Performance
    CAP_CONTROL_CURRENT_STATUS(CapControlCurrentStatusModel.class, ReportGroup.CAP_CONTROL),
    CAP_CONTROL_STATE_COMPARISON(CapControlStateComparisonController.class, ReportGroup.CAP_CONTROL),
    CAP_CONTROL_NEW_ACTIVITY(CapControlNewActivityModel.class, ReportGroup.CAP_CONTROL),
    CAP_CONTROL_OPERATIONS(CapControlOperationsController.class, ReportGroup.CAP_CONTROL),
    CAP_BANK_OPERATIONS_PERFORMANCE(CapBankOperationsPerformanceController.class, ReportGroup.CAP_CONTROL),
    CAP_CONTROL_CONFIRMATION_PERCENTAGE(CapControlConfirmationPercentageController.class, ReportGroup.CAP_CONTROL),
    CAP_BANK_RECENT_MAX_DAILY_OPERATIONS(CapBankRecentMaxDailyOpsModel.class, ReportGroup.CAP_CONTROL),
    CAP_BANK_MAX_OPS_EXCEEDED(CapBankMaxOpsExceededController.class, ReportGroup.CAP_CONTROL),
    CAP_CONTROL_RETRIES(CapControlRetriesController.class, ReportGroup.CAP_CONTROL),
    CAP_CONTROL_VAR_CHANGE(CapControlVarChangeController.class, ReportGroup.CAP_CONTROL),
    VAR_IMBALANCE_ON_EXECUTION(VarImbalanceOnExecutionController.class, ReportGroup.CAP_CONTROL),
    /*END of Cap Control Reports*/
            
    COMM_STATISTICS(StatisticModel.class, ReportGroup.STATISTICAL),
    DEVICE_REQUEST_DETAIL(DeviceRequestDetailController.class, ReportGroup.STATISTICAL),
    
    EC_ACTIVITY_LOG_SUMMARY(ActivityModel.class, ReportGroup.STARS),
    EC_ACTIVITY_LOG_DETAIL(ActivityDetailModel.class, ReportGroup.STARS),
    PROGRAM_DETAIL(ProgramDetailModel.class, ReportGroup.STARS),
    EC_WORK_ORDER(WorkOrderModel.class, ReportGroup.STARS),
    STARS_LM_SUMMARY(StarsLMSummaryModel.class, ReportGroup.STARS),
    STARS_LM_DETAIL(StarsLMDetailModel.class, ReportGroup.STARS),
    LM_CONTROL_SUMMARY(LMControlSummaryController.class, ReportGroup.STARS),
    LM_CONTROL_DETAIL(LMControlDetailController.class, ReportGroup.STARS),
    //TODO  STARS_AMR_SUMMARY(StarsAMRSummaryModel.class, ReportGroup.STARS),
    STARS_AMR_DETAIL(StarsAMRDetailModel.class, ReportGroup.STARS),
    OPT_OUT_INFO(OptOutInfoController.class, ReportGroup.STARS),
    OPT_OUT_LIMIT(OptOutLimitController.class, ReportGroup.STARS),
        
    // commercial curtailment
    CCURT_EVENT_SUMMARY(CurtailmentEventSummaryController.class, ReportGroup.CCURT),
    CCURT_INTERRUPTION_SUMMARY(CurtailmentInterruptionSummaryController.class, ReportGroup.CCURT),
    
        //Custom Settlement data.  Determined by EnergyCompanyID
    HECO_LMEVENT_SUMMARY(HECO_LMEventSummaryModel.class, null),
    HECO_MONTHLY_BILLING(HECO_MonthlyBillingSettlementModel.class, null),
    HECO_CUSTOMER_MONTHLY_BILLING(HECO_CustomerMonthlyBillingSettlementModel.class, null),
    HECO_DSMIS(HECO_DSMISModel.class, null),
    ZIGBEE_CONTROL_EVENT_DEVICE(ZigbeeControlledDeviceController.class, ReportGroup.LOAD_MANAGEMENT);

    
    @SuppressWarnings("unchecked")
    private ReportTypes(Class modelClass, ReportGroup reportGroup) {
        this.modelClass = modelClass;
        this.reportGroup = reportGroup;
    }
    
    /** Class <? extends ReportModelBase> modelClass; OR Class <? extends ReportController> modelClass*/
    @SuppressWarnings("unchecked")
    private Class modelClass;
    private ReportGroup reportGroup;
    private static final String baseKey = "yukon.web.reportTypes.";
    
    @SuppressWarnings("unchecked")
    public Class getModelClass() {
        return modelClass;
    }

    public ReportGroup getReportGroup() {
        return reportGroup;
    }

    @Override
    public String getFormatKey() {
        return this.baseKey + name();
    }

    public String getDescriptionKey() {
    	return this.baseKey + name() + ".description";
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
