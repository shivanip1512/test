/*
 * Created on Feb 2, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis;

import com.cannontech.analysis.controller.CBCInventoryController;
import com.cannontech.analysis.controller.CapBankOperationsPerformanceController;
import com.cannontech.analysis.controller.CapControlConfirmationPercentageController;
import com.cannontech.analysis.controller.CapControlOperationsController;
import com.cannontech.analysis.controller.CapControlRetriesController;
import com.cannontech.analysis.controller.CapControlScheduleDetailController;
import com.cannontech.analysis.controller.CapControlStateComparisonController;
import com.cannontech.analysis.controller.CurtailmentEventSummaryController;
import com.cannontech.analysis.controller.CurtailmentInterruptionSummaryController;
import com.cannontech.analysis.controller.DisconnectCollarController;
import com.cannontech.analysis.controller.ReportController;
import com.cannontech.analysis.controller.ReportControllerAdapter;
import com.cannontech.analysis.tablemodel.ActivityDetailModel;
import com.cannontech.analysis.tablemodel.ActivityModel;
import com.cannontech.analysis.tablemodel.CapBankListModel;
import com.cannontech.analysis.tablemodel.CapControlCurrentStatusModel;
import com.cannontech.analysis.tablemodel.CapControlEventLogModel;
import com.cannontech.analysis.tablemodel.CapControlNewActivityModel;
import com.cannontech.analysis.tablemodel.CarrierDBModel;
import com.cannontech.analysis.tablemodel.DailyPeaksModel;
import com.cannontech.analysis.tablemodel.DisconnectModel;
import com.cannontech.analysis.tablemodel.HECO_CustomerMonthlyBillingSettlementModel;
import com.cannontech.analysis.tablemodel.HECO_DSMISModel;
import com.cannontech.analysis.tablemodel.HECO_LMEventSummaryModel;
import com.cannontech.analysis.tablemodel.HECO_MonthlyBillingSettlementModel;
import com.cannontech.analysis.tablemodel.LMControlLogModel;
import com.cannontech.analysis.tablemodel.LPDataSummaryModel;
import com.cannontech.analysis.tablemodel.LPSetupDBModel;
import com.cannontech.analysis.tablemodel.LoadControlVerificationModel;
import com.cannontech.analysis.tablemodel.LoadGroupModel;
import com.cannontech.analysis.tablemodel.MaxDailyOpsModel;
import com.cannontech.analysis.tablemodel.MeterOutageModel;
import com.cannontech.analysis.tablemodel.MeterReadModel;
import com.cannontech.analysis.tablemodel.MeterUsageModel;
import com.cannontech.analysis.tablemodel.PointDataIntervalModel;
import com.cannontech.analysis.tablemodel.PointDataSummaryModel;
import com.cannontech.analysis.tablemodel.PowerFailModel;
import com.cannontech.analysis.tablemodel.ProgramDetailModel;
import com.cannontech.analysis.tablemodel.RepeaterRoleCollisionModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.RouteDBModel;
import com.cannontech.analysis.tablemodel.RouteMacroModel;
import com.cannontech.analysis.tablemodel.ScheduledMeterReadModel;
import com.cannontech.analysis.tablemodel.StarsAMRDetailModel;
import com.cannontech.analysis.tablemodel.StarsAMRSummaryModel;
import com.cannontech.analysis.tablemodel.StarsLMDetailModel;
import com.cannontech.analysis.tablemodel.StarsLMSummaryModel;
import com.cannontech.analysis.tablemodel.StatisticHistoryDeviceModel;
import com.cannontech.analysis.tablemodel.StatisticHistoryPortModel;
import com.cannontech.analysis.tablemodel.StatisticHistoryTransmitterModel;
import com.cannontech.analysis.tablemodel.StatisticModel;
import com.cannontech.analysis.tablemodel.SystemLogModel;
import com.cannontech.analysis.tablemodel.WorkOrderModel;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;


/**
 * @author snebben
 *
 * ReportTypes represent a Report in com.cannontech.analysis.report package.
 * (Not to be confused with representing a "tableModel".  They represent a "Report"!)
 */
public class ReportTypes
{
	/** enums for types of objects reported on */
	public static final int NONE = -1;
	public static final int STATISTIC_DATA = 0;

	public static final int SYSTEM_LOG_DATA = 1;
	public static final int LM_CONTROL_LOG_DATA = 2;
	public static final int LG_ACCOUNTING_DATA = 3;
	public static final int LM_DAILY_PEAKS_DATA = 4;
	
	public static final int METER_READ_DATA = 5;
	public static final int METER_OUTAGE_DATA = 6;
	public static final int CARRIER_DB_DATA = 7;
	public static final int POWER_FAIL_DATA = 8;
	public static final int DISCONNECT_METER_DATA = 9;
	public static final int CARRIER_ROUTE_MACRO_DATA = 10;
	public static final int ROUTE_DATA = 11;
//	public static final int LOAD_PROFILE_DATA = 12;
	public static final int LP_SETUP_DATA = 12;
	public static final int LP_SUMMARY_DATA = 13;		//Extension of Point_Data_Summary_Data
	
	public static final int EC_ACTIVITY_LOG_DATA = 14;
	public static final int EC_ACTIVITY_DETAIL_DATA = 15;
	public static final int PROGRAM_DETAIL_DATA = 16;
	
	public static final int EC_WORK_ORDER_DATA = 17;
	
	public static final int STARS_LM_SUMMARY_DATA = 18;
	public static final int STARS_LM_DETAIL_DATA = 19;
	public static final int STARS_AMR_SUMMARY_DATA = 20;
	public static final int STARS_AMR_DETAIL_DATA = 21;

	/** Cap Bank Reports */
	public static final int CBC_BANK_DATA = 22;
	public static final int CAP_CONTROL_NEW_ACTIVITY_DATA = 23;
	public static final int CAP_CONTROL_STATUS_DATA = 24;
	public static final int CAP_CONTROL_SCHEDULE_EVENT_LOG_DATA = 25;

	public static final int POINT_DATA_INTERVAL_DATA = 26;	//Coincidental
	public static final int POINT_DATA_SUMMARY_DATA = 27;	//Peaks/Usage
	
	public static final int LOAD_CONTROL_VERIFICATION_DATA = 28;
	
	//Custom Settlement data.  Determined by EnergyCompanyID
	public static final int HECO_LMEVENT_SUMMARY_DATA = 29;
	public static final int HECO_MONTHLY_BILLING_DATA = 30;
	public static final int HECO_CUSTOMER_MONTHLY_BILLING_DATA = 31;
	public static final int HECO_DSMIS_DATA = 32;
    
    // commercial curtailment
    public static final int CCURT_EVENT_SUMMARY_DATA = 33;
    public static final int CCURT_INTERRUPTION_SUMMARY_DATA = 34;
    
    public static final int MAX_DAILY_OPERATIONS = 35;
    public static final int CAP_CONTROL_STATE_COMPARISON = 36;
    public static final int CAP_CONTROL_OPERATIONS = 37;
    public static final int CBC_INVENTORY = 38;
    public static final int CAP_CONTROL_CONFIRMATION_PERCENTAGE = 39;
    public static final int CAP_CONTROL_RETRIES = 40;
    public static final int CAP_BANK_OPERATIONS_PERFORMANCE = 41;
    public static final int CAP_CONTROL_SCHEDULE_DETAIL= 42;
    
    public static final int SCHEDULED_METER_READ_DATA = 43;
    public static final int REPEATER_ROLE_COLLISION = 44;
    public static final int METER_USAGE_DATA = 45;
    public static final int DISCONNECT_COLLAR = 46;
    public static final int STATISTIC_HISTORY_PORT_DATA = 47;
    public static final int STATISTIC_HISTORY_DEVICE_DATA = 48;
    public static final int STATISTIC_HISTORY_TRANSMITTER_DATA = 49;
    
	private static Class[] typeToClassMap =
	{
		StatisticModel.class,
		SystemLogModel.class,
		LMControlLogModel.class,
		LoadGroupModel.class,
		DailyPeaksModel.class,
		
		MeterReadModel.class,
		MeterOutageModel.class,
		CarrierDBModel.class,
		PowerFailModel.class,
		DisconnectModel.class,
		RouteMacroModel.class,
		RouteDBModel.class,
		LPSetupDBModel.class,
		LPDataSummaryModel.class,
		
		ActivityModel.class,
		ActivityDetailModel.class,
		ProgramDetailModel.class,
		WorkOrderModel.class,
		
		StarsLMSummaryModel.class,
		StarsLMDetailModel.class,
		StarsAMRSummaryModel.class,
		StarsAMRDetailModel.class,
		
		CapBankListModel.class,
		CapControlNewActivityModel.class,
		CapControlCurrentStatusModel.class,
		CapControlEventLogModel.class,
		
		PointDataIntervalModel.class,
		PointDataSummaryModel.class,
		
		LoadControlVerificationModel.class,
		
		HECO_LMEventSummaryModel.class,
		HECO_MonthlyBillingSettlementModel.class,
		HECO_CustomerMonthlyBillingSettlementModel.class,
		HECO_DSMISModel.class,
		CurtailmentEventSummaryController.class,
        CurtailmentInterruptionSummaryController.class,
        MaxDailyOpsModel.class,
        CapControlStateComparisonController.class,
        CapControlOperationsController.class,
        CBCInventoryController.class,
        CapControlConfirmationPercentageController.class,
        CapControlRetriesController.class,
        CapBankOperationsPerformanceController.class,
        CapControlScheduleDetailController.class,
        
        ScheduledMeterReadModel.class,
        RepeaterRoleCollisionModel.class,
        MeterUsageModel.class,
        DisconnectCollarController.class,
        StatisticHistoryPortModel.class,
        StatisticHistoryDeviceModel.class,
        StatisticHistoryTransmitterModel.class
	};
		
	/** String names for report types */
	public static final String STATISTIC_DATA_STRING = "Communication Statistics";
	public static final String STATISTIC_HISTORY_PORT_STRING = "Historical Daily Port Statistics";
	public static final String STATISTIC_HISTORY_DEVICE_STRING = "Historical Daily Device Statistics";
	public static final String STATISTIC_HISTORY_TRANSMITTER_STRING = "Historical Daily Transmitter Statistics";

	public static final String SYSTEM_LOG_DATA_STRING = "System Log";
	public static final String LM_CONTROL_LOG_DATA_STRING = "Load Management Log";
	public static final String LG_ACCOUNTING_DATA_STRING = "Load Group Accounting";
	public static final String LM_DAILY_PEAKS_STRING = "LM Daily Peaks";
	
	public static final String METER_DATA_STRING = "Meter Reads";
	public static final String METER_OUTAGE_STRING = "Meter Outages";
	public static final String CARRIER_DB_DATA_STRING = "Carrier Data";
	public static final String POWER_FAIL_DATA_STRING = "Outage Counts";
	public static final String DISCONNECT_METER_DATA_STRING = "Disconnect Status";
	public static final String CARRIER_ROUTE_MACRO_DATA_STRING = "Carrier Route Macro Data";
	public static final String ROUTE_DATA_STRING = "Route Data";
//	public static final String LOAD_PROFILE_DATA_STRING = "Load Profile Data";
	public static final String LP_SETUP_DATA_STRING = "Load Profile Setup Data";
	public static final String LP_SUMMARY_DATA_STRING = "Load Profile Summary Data";

	public static final String EC_ACTIVITY_LOG_DATA_STRING = "Activity Log - Summary";
	public static final String EC_ACTIVITY_DETAIL_DATA_STRING = "Activity Log - Detail";
	public static final String PROGRAM_DETAIL_DATA_STRING = "Program Status Detail";

	public static final String EC_WORK_ORDER_DATA_STRING = "Work Order";
	public static final String STARS_LM_SUMMARY_DATA_STRING = "LM Summary Data";
	public static final String STARS_LM_DETAIL_DATA_STRING = "LM Detail Data";
	public static final String STARS_AMR_SUMMARY_DATA_STRING = "AMR Summary Data";
	public static final String STARS_AMR_DETAIL_DATA_STRING = "AMR Detail Data";
	
	public static final String CAPBANK_DATA_STRING = "Cap Bank Details";
	public static final String CAP_CONTROL_NEW_ACTIVITY_STRING = "Cap Control New Activity";
	public static final String CAP_CONTROL_STATUS_STRING = "Cap Bank Current Status";
	public static final String CAP_CONTROL_SCHEDULE_EVENT_LOG_STRING = "Cap Control Schedule Activity";
	
	public static final String POINT_DATA_INTERVAL_DATA_STRING = "PointData Interval";
	public static final String POINT_DATA_SUMMARY_DATA_STRING = "PointData Summary";
	
	public static final String LOAD_CONTROL_VERIFICATION_STRING = "Load Control Verification";
	
	//Custom settlement reports, based on EnergyCompanyID
	public static final String HECO_LMEVENT_SUMMARY_STRING = "LM Event Summary";
	public static final String HECO_MONTHLY_BILLING_STRING = "Monthly Billing Settlement";
	public static final String HECO_CUSTOMER_MONTHLY_BILLING_STRING = "Customer Monthly Billing Settlement";
	public static final String HECO_DSMIS_STRING = "DSMIS Settlement";

	public static final String CCURT_EVENT_SUMMARY_STRING = "Event Summary";
	public static final String CCURT_INTERRUPTION_SUMMARY_STRING = "Interruption Summary";
    
    public static final String MAX_DAILY_OPERATIONS_STRING = "Max Daily Operations";
    public static final String CAP_CONTROL_STATE_COMPARISON_STRING= "CapControl State Comparison";
    public static final String CAP_CONTROL_OPERATIONS_STRING= "CapControl Operations";
    public static final String CBC_INVENTORY_STRING= "CBC Inventory";
    public static final String CAP_CONTROL_CONFIRMATION_PERCENTAGE_STRING = "CapControl Confirmation Percentage";
    public static final String CAP_CONTROL_RETRIES_STRING= "CapControl Retries Report";
    public static final String CAP_BANK_OPERATIONS_PERFORMANCE_STRING= "CapBank Operations Performance";
    public static final String CAP_CONTROL_SCHEDULE_DETAIL_STRING= "CapControl Schedule Detail";
    
    public static final String SCHEDULED_METER_READ_STRING = "Scheduled Meter Reads";
    public static final String REPEATER_ROLE_COLLISION_STRING = "Repeater Role Collision Report";
    public static final String METER_USAGE_STRING = "Meter Usage Report";
    public static final String DISCONNECT_COLLAR_STRING = "Disconnect Collar Report";

    public static final String CAPBANK_DATA_DESCRIPTION = "Detailed report of the capbanks, including information on its location, size, cbc, feeder and substation bus.";
    public static final String CAP_CONTROL_NEW_ACTIVITY_DESCRIPTION = "Detailed report of capbank activity, including manual and automated controls, operation outcomes, and capcontrol database changes.'";
    public static final String CAP_CONTROL_STATUS_DESCRIPTION = "Detailed report of cap bank statuses, based on var response information";
    public static final String CAP_CONTROL_SCHEDULE_EVENT_LOG_DESCRIPTION = "Detailed report of ending bank status activity or all schedule activity";    
    public static final String MAX_DAILY_OPERATIONS_DESCRIPTION = "Detailed report showing a 6 week history of capbanks that have reached max daily operations limit.";
    public static final String CAP_CONTROL_STATE_COMPARISON_DESCRIPTION= "Detailed report showing a comparison of capbank states set by var response against its two way cbc reported state.";
    public static final String CAP_CONTROL_OPERATIONS_DESCRIPTION= "Detailed report showing capbank operations and their related outcomes.";
    public static final String CBC_INVENTORY_DESCRIPTION= "Detailed report showing cbc information and attached parent objects.";
    public static final String CAP_CONTROL_CONFIRMATION_PERCENTAGE_DESCRIPTION = "Detailed report showing operational percentages for failed, questionable, and successful operation outcomes.";
    public static final String CAP_CONTROL_RETRIES_DESCRIPTION = "Detailed report showing capbank control retry information.";
    public static final String CAP_BANK_OPERATIONS_PERFORMANCE_DESCRIPTION = "Detailed report showing capbank operational information for combinations of failed, questionable, and successful outcomes based on a user entered percentage level.";
    public static final String CAP_CONTROL_SCHEDULE_DETAIL_DESCRIPTION = "Detailed report showing all capcontrol schedules and thier specifics.";
    
	/** Report String to enum mapping */
	public static final String[] reportName = {
		STATISTIC_DATA_STRING, 
		
		SYSTEM_LOG_DATA_STRING,
		LM_CONTROL_LOG_DATA_STRING,
		LG_ACCOUNTING_DATA_STRING,
		LM_DAILY_PEAKS_STRING,
	
		METER_DATA_STRING,
		METER_OUTAGE_STRING,
		CARRIER_DB_DATA_STRING,
		POWER_FAIL_DATA_STRING,
		DISCONNECT_METER_DATA_STRING,
		CARRIER_ROUTE_MACRO_DATA_STRING,
		ROUTE_DATA_STRING,
		LP_SETUP_DATA_STRING,
		LP_SUMMARY_DATA_STRING,

		EC_ACTIVITY_LOG_DATA_STRING,
		EC_ACTIVITY_DETAIL_DATA_STRING,
		PROGRAM_DETAIL_DATA_STRING,
		
		EC_WORK_ORDER_DATA_STRING,
		STARS_LM_SUMMARY_DATA_STRING,
		STARS_LM_DETAIL_DATA_STRING,
		STARS_AMR_SUMMARY_DATA_STRING,
		STARS_AMR_DETAIL_DATA_STRING,
		
		//Capcontrol
		CAPBANK_DATA_STRING,
		CAP_CONTROL_NEW_ACTIVITY_STRING,
		CAP_CONTROL_STATUS_STRING,
		CAP_CONTROL_SCHEDULE_EVENT_LOG_STRING,
		
		POINT_DATA_INTERVAL_DATA_STRING,
		POINT_DATA_SUMMARY_DATA_STRING,
		
		LOAD_CONTROL_VERIFICATION_STRING,
		
		
		HECO_LMEVENT_SUMMARY_STRING,
		HECO_MONTHLY_BILLING_STRING,
		HECO_CUSTOMER_MONTHLY_BILLING_STRING,
		HECO_DSMIS_STRING,
		CCURT_EVENT_SUMMARY_STRING,
		CCURT_INTERRUPTION_SUMMARY_STRING,
        MAX_DAILY_OPERATIONS_STRING,
        CAP_CONTROL_STATE_COMPARISON_STRING,
        CAP_CONTROL_OPERATIONS_STRING,
        CBC_INVENTORY_STRING,
        CAP_CONTROL_CONFIRMATION_PERCENTAGE_STRING,
        CAP_CONTROL_RETRIES_STRING,
        CAP_BANK_OPERATIONS_PERFORMANCE_STRING,
        CAP_CONTROL_SCHEDULE_DETAIL_STRING,
        
        SCHEDULED_METER_READ_STRING,
        REPEATER_ROLE_COLLISION_STRING,
        METER_USAGE_STRING,
        DISCONNECT_COLLAR_STRING,
        STATISTIC_HISTORY_PORT_STRING,
        STATISTIC_HISTORY_DEVICE_STRING,
        STATISTIC_HISTORY_TRANSMITTER_STRING
	};

    /** Report Description to enum mapping */
    public static final String[] reportDescription = {
        "", "",
        "","","","","","","","","","",
        "","","","","","","","","","",
        //Capcontrol
        CAPBANK_DATA_DESCRIPTION,
        CAP_CONTROL_NEW_ACTIVITY_DESCRIPTION,
        CAP_CONTROL_STATUS_DESCRIPTION,
        CAP_CONTROL_SCHEDULE_EVENT_LOG_DESCRIPTION,
        "","","","","","","","","",
        MAX_DAILY_OPERATIONS_DESCRIPTION,
        CAP_CONTROL_STATE_COMPARISON_DESCRIPTION,
        CAP_CONTROL_OPERATIONS_DESCRIPTION,
        CBC_INVENTORY_DESCRIPTION,
        CAP_CONTROL_CONFIRMATION_PERCENTAGE_DESCRIPTION,
        CAP_CONTROL_RETRIES_DESCRIPTION,
        CAP_BANK_OPERATIONS_PERFORMANCE_DESCRIPTION,
        CAP_CONTROL_SCHEDULE_DETAIL_DESCRIPTION,
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    };    

	/* Report groupings */	
	public static final int ADMIN_REPORTS_GROUP = 0;
	public static final int AMR_REPORTS_GROUP = 1;
	public static final int STATISTICAL_REPORTS_GROUP = 2;
	public static final int LOAD_MANAGEMENT_REPORTS_GROUP = 3;
	public static final int CAP_CONTROL_REPORTS_GROUP = 4;
	public static final int DATABASE_REPORTS_GROUP = 5;
	public static final int STARS_REPORTS_GROUP = 6;
	public static final int OTHER_REPORTS_GROUP = 7;
    public static final int CI_REPORTS_GROUP = 8;
	
	//This is a special case, it needs to be set up based on the current EC settlement mapping.  It will NOT be in the groupToTypeMap
	public static final int SETTLEMENT_REPORTS_GROUP = 100;
	
	public static final String ADMIN_REPORTS_GROUP_STRING = "Administrative";
	public static final String AMR_REPORTS_GROUP_STRING = "AMR";
	public static final String STATISTICAL_REPORTS_GROUP_STRING = "Communication";
	public static final String LOAD_MANAGEMENT_REPORTS_GROUP_STRING = "Load Management";
	public static final String CAP_CONTROL_REPORTS_GROUP_STRING = "Capacitor Control";
	public static final String DATABASE_REPORTS_GROUP_STRING = "Database";
	public static final String STARS_REPORTS_GROUP_STRING = "STARS";
	public static final String OTHER_REPORTS_GROUP_STRING = "Miscellaneous";
    public static final String CI_REPORTS_GOUP_STRING = "C&I";
	
	public static final String SETTLEMENT_REPORTS_GROUP_STRING = "Settlement";

	/** Report String to enum mapping */
	public static final String[] reportGroupName = {
		ADMIN_REPORTS_GROUP_STRING,
		AMR_REPORTS_GROUP_STRING,
		STATISTICAL_REPORTS_GROUP_STRING,
		LOAD_MANAGEMENT_REPORTS_GROUP_STRING,
		CAP_CONTROL_REPORTS_GROUP_STRING,
		DATABASE_REPORTS_GROUP_STRING,
		STARS_REPORTS_GROUP_STRING,
		OTHER_REPORTS_GROUP_STRING,
        CI_REPORTS_GOUP_STRING,
	};

	private static int[] adminGroupReportTypes = new int[]{
        POINT_DATA_INTERVAL_DATA, 
        POINT_DATA_SUMMARY_DATA, 
        EC_ACTIVITY_LOG_DATA, 
        SYSTEM_LOG_DATA
        };
    
	private static int[] amrGroupReportTypes = new int[]{
        METER_READ_DATA, 
        SCHEDULED_METER_READ_DATA, 
        METER_OUTAGE_DATA, 
		POWER_FAIL_DATA, 
        DISCONNECT_METER_DATA, 
        LP_SETUP_DATA, 
        LP_SUMMARY_DATA,
        METER_USAGE_DATA
        };
    
	private static int[] statGroupReportTypes = new int[]{
        STATISTIC_DATA, 
        STATISTIC_HISTORY_PORT_DATA, 
        STATISTIC_HISTORY_DEVICE_DATA, 
        STATISTIC_HISTORY_TRANSMITTER_DATA
        };
	private static int[] lmGroupReportTypes = new int[]{
        LM_CONTROL_LOG_DATA, 
        LG_ACCOUNTING_DATA, 
        LM_DAILY_PEAKS_DATA, 
        LOAD_CONTROL_VERIFICATION_DATA
        };
    
	private static int[] capControlGroupReportTypes = new int[]{
        CBC_BANK_DATA, 
        CAP_CONTROL_NEW_ACTIVITY_DATA, 
        CAP_CONTROL_STATUS_DATA, 
        CAP_CONTROL_SCHEDULE_EVENT_LOG_DATA, 
        MAX_DAILY_OPERATIONS, 
        CAP_CONTROL_STATE_COMPARISON, 
        CAP_CONTROL_OPERATIONS, 
        CBC_INVENTORY, 
        CAP_CONTROL_CONFIRMATION_PERCENTAGE, 
        CAP_CONTROL_RETRIES, 
        CAP_BANK_OPERATIONS_PERFORMANCE, 
        CAP_CONTROL_SCHEDULE_DETAIL};
    
	private static int[] databaseGroupReportTypes = new int[]{
        CARRIER_DB_DATA, 
        CARRIER_ROUTE_MACRO_DATA, 
        ROUTE_DATA, 
        REPEATER_ROLE_COLLISION, 
        DISCONNECT_COLLAR};
    
	private static int[] starsGroupReportTypes = new int[]{
        EC_ACTIVITY_LOG_DATA, 
        EC_ACTIVITY_DETAIL_DATA, 
        PROGRAM_DETAIL_DATA, 
        EC_WORK_ORDER_DATA, 
		STARS_LM_SUMMARY_DATA, 
        STARS_LM_DETAIL_DATA,
		/*TODO STARS_AMR_SUMMARY_DATA,*/ 
        STARS_AMR_DETAIL_DATA
        };
	private static int[] otherGroupReportTypes = new int[0];
	private static int[] settlementGroupReportTypes = new int[0];
    private static int[] ciGroupReportTypes = new int[]{CCURT_EVENT_SUMMARY_DATA, CCURT_INTERRUPTION_SUMMARY_DATA};

	/**
	 * Retuns int[] of settlement reportTypes for yukonDefID 
	 * @param yukonDefID
	 * @return
	 */
	public static int[] getSettlementReportTypes(int yukonDefID)
	{
		switch (yukonDefID)
		{
			case YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO:
			{
				return new int[]{HECO_LMEVENT_SUMMARY_DATA,
										HECO_MONTHLY_BILLING_DATA,
										HECO_CUSTOMER_MONTHLY_BILLING_DATA,
										HECO_DSMIS_DATA
										};
			}
//			case YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_XCEL_ISOC:	//TODO
			default :
				return new int[0];
		}
	}
	/**
	 * Returns int[] of all reportTypes for groupID
	 * @param groupID
	 * @return
	 */
	public static int[] getGroupReportTypes(int groupID)
	{
		switch (groupID)
		{
			case ADMIN_REPORTS_GROUP :
				return adminGroupReportTypes;
			case AMR_REPORTS_GROUP:
				return amrGroupReportTypes;
			case STATISTICAL_REPORTS_GROUP:
				return statGroupReportTypes;
			case LOAD_MANAGEMENT_REPORTS_GROUP:
				return lmGroupReportTypes;
			case CAP_CONTROL_REPORTS_GROUP:
				return capControlGroupReportTypes;
			case DATABASE_REPORTS_GROUP:
				return databaseGroupReportTypes;
			case STARS_REPORTS_GROUP:
				return starsGroupReportTypes;
			case OTHER_REPORTS_GROUP:
				return otherGroupReportTypes;
			case CI_REPORTS_GROUP:
			    return ciGroupReportTypes;
			//Settlement is specific for each energy company, therefore it cannot return anything from this static method but should be handled 
			// in the specific code.				
			case SETTLEMENT_REPORTS_GROUP:
			default :
				return new int[0];	//an empty list
		}
	}
	/**
	 * Returns the String name for reportType
	 * @param reportType
	 * @return
	 */
	public static String getReportName(int reportType)
	{
		return reportName[reportType];
	}
	public static String getReportDescription(int reportType)
    {
	    return reportDescription[reportType];
    }
	/**
	 * Returns the Group string name for reportGroupType
	 * @param reportGroupType
	 * @return
	 */
	public static String getReportGroupName(int reportGroupType)
	{
		if( reportGroupType == SETTLEMENT_REPORTS_GROUP)
			return SETTLEMENT_REPORTS_GROUP_STRING;
		return reportGroupName[reportGroupType];
	}
	
	/**
	 * Creates and returns an instance of ReportModelBase (or extended class) for type
	 * @return com.cannontech.database.model.DBTreeModel
	 * @param type int
	 */
	public static ReportController create(int reportType) {
	
        ReportController returnVal = null;
		
		if( reportType >= 0 && reportType < typeToClassMap.length )
		{
			try
			{
                // What's about to happen here isn't pretty.
                // I'm trying to bridge the gap between the old
                // way of creating reports and the new way.
				Class modelClass = typeToClassMap[reportType];
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
		}
		else
		{
			return null;
		}
		return returnVal;
	}
}
