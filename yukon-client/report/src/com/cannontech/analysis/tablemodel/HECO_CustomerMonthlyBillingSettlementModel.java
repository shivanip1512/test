package com.cannontech.analysis.tablemodel;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.lm.LMEvent;
import com.cannontech.analysis.data.lm.SettlementCustomer;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;

/**
 * Created on Nov 15, 2005
 * Extending classes must implement:
 *   addDataRow(ResultSet)	- add a "row" object to the data Vector
 *   buildSQLStatement()	- Returns the sql query statment
 * 
 * @author snebben
 */
public class HECO_CustomerMonthlyBillingSettlementModel extends HECO_SettlementModelBase
{
	protected final int NUMBER_COLUMNS = 21;	// This is the number of STATIC Colums.  Customers are columns, but are added dynamically
	
	private static String title = "Monthly Customer Billing Settlement Report";
			
	//ROW indexes (this model displays rows instead of columns)	
	public static final int CUSTOMER_NAME_DATA = 0;
	public static final int CUSTOMER_NUMBER_DATA = 1;
	public static final int ACCOUNT_NUMBER_DATA = 2;
	public static final int DSM_APPLICATION_NUMBER_DATA = 3;
	public static final int RATE_SCHEDULE_DATA = 4;
	
	public static final int MAX_MONTHLY_MEASURED_DEMAND_DATA = 5;	
	public static final int FIRM_SERVICE_LEVEL_DATA = 6;
	public static final int CONTRACTED_INTERRUPTIBLE_LOAD_DATA = 7;
	public static final int CIDLC_DEMAND_CHARGE_DATA = 8;
	
	
	public static final int CONTROLLED_DEMAND_INCENTIVE_DATA = 9;
	public static final int ENERGY_REDUCTION_INCENTIVE_DATA = 10;
	public static final int EFSL_TOTAL_CHARGE_DATA = 11;
	
	public static final int TOTAL_CIDLC_CREDITS_DEBITS_DATA = 12;
	
	public static final int LOAD_CONTROL_EVENT_SUMMARY_DATA = 13;
	public static final int TOTAL_DISPATCHED_HOURS_DATA = 14;
	public static final int TOTAL_UF_HOURS_DATA = 15;
	public static final int EVENT_TYPE_HEADING_DATA = 16;
	public static final int EVENT_DATE_HEADING_DATA = 17;
	public static final int EVENT_START_TIME_HEADING_DATA = 18;
	public static final int EVENT_STOP_TIME_HEADING_DATA = 19;
	public static final int EVENT_DURATION_HEADING_DATA = 20;
	
	
	public static final String CUSTOMER_NAME_STRING = "Customer Name";
	public static final String CUSTOMER_NUMBER_STRING = "Customer Number";
	public static final String ACCOUNT_NUMBER_STRING = "Account Number";
	public static final String DSM_APPLICATION_NUMBER_STRING = "DSM Application Number";
	public static final String RATE_SCHEDULE_STRING = "Rate Schedule";
	
	public static final String MAX_MONTHLY_MEASURED_DEMAND_STRING = "Max Monthly Measured Demand";
	public static final String FIRM_SERVICE_LEVEL_STRING = "Firm Service Level";
	public static final String CONTRACTED_INTERRUPTIBLE_LOAD_STRING = "Contracted Interruptible Load";
	public static final String CIDLC_DEMAND_CHARGE_STRING = "CIDLC Demand Charge";
	
	public static final String CONTROLLED_DEMAND_INCENTIVE_STRING = "Controlled Demand Incentive Amount";
	public static final String ENERGY_REDUCTION_INCENTIVE_STRING = "Energy Reduction Incentive Amount";
	
	public static final String EFSL_TOTAL_CHARGE_STRING = "Total Excess Firm Service Level Charge";
	public static final String TOTAL_CIDLC_CREDITS_DEBITS_STRING = "Net Billing Credit/Charge";
	public static final String LOAD_CONTROL_EVENT_SUMMARY_STRING = "Load Control Event Summary";
	public static final String TOTAL_DISPATCHED_HOURS_STRING = "Total Dispatched Control";
	public static final String TOTAL_UF_HOURS_STRING = "Total Underfrequency Control";
	
	public static final String EVENT_TYPE_HEADING_STRING = "Type";
	public static final String EVENT_DATE_HEADING_STRING = "Date";
	public static final String EVENT_START_TIME_HEADING_STRING = "Start";
	public static final String EVENT_STOP_TIME_HEADING_STRING = "Stop";
	public static final String EVENT_DURATION_HEADING_STRING = "Duration";

	private Double totalDispatchedHours = null;
	private Double totalUFHours = null; 
		
	public static void main(String[] args)
	{
		HECO_CustomerMonthlyBillingSettlementModel model = new HECO_CustomerMonthlyBillingSettlementModel();
		GregorianCalendar tempCal = new GregorianCalendar();
		tempCal.setTime(model.getStartDate());
		tempCal.set(Calendar.DAY_OF_MONTH, 15);
		model.setStartDate(tempCal.getTime());
//		model.collectData();
		model.getColumnNames();
		
	}
	/**
	 * Constructor class
	 */
	public HECO_CustomerMonthlyBillingSettlementModel(Date start_, Date stop_)
	{
		super(start_, stop_);//default type
	}

	/**
	 * Constructor class
	 */
	public HECO_CustomerMonthlyBillingSettlementModel()
	{
		this(null, null);		
	}

	/**
	 * Constructor class
	 * Only ONE energycompanyID is used 
	 */
	public HECO_CustomerMonthlyBillingSettlementModel(Integer ecID_)
	{
		this();//default type
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof Integer)
		{
			int custIndex = ((Integer)o).intValue() / getDataObjects().size();
			Integer customerID = getCustomerIDS()[custIndex];
			SettlementCustomer settleCust = getSettlementCustomer(customerID);
			int dataObjectsIndex = ((Integer)o).intValue() % (getDataObjects().size());
			LMEvent lmEvent = (LMEvent)getDataObjects().get(dataObjectsIndex);
			switch (columnIndex)
			{
				case CUSTOMER_NAME_DATA :
					return settleCust.getCICustomerBase().getCiCustomerBase().getCompanyName();
				case CUSTOMER_NUMBER_DATA:
					return settleCust.getCICustomerBase().getCustomer().getCustomerNumber();
				case ACCOUNT_NUMBER_DATA:
				{
					LiteCustomer liteCust = DaoFactory.getCustomerDao().getLiteCustomer(settleCust.getCustomerID().intValue());
					Vector acctIDs = liteCust.getAccountIDs();
					if( acctIDs != null && !acctIDs.isEmpty())
					{
						LiteAccountInfo lscai = getLiteStarsEC().getCustAccountInformation( ((Integer)acctIDs.get(0)).intValue(), true);
						return lscai.getCustomerAccount().getAccountNumber();
					}

					return "Acct # ?";
				}
				case DSM_APPLICATION_NUMBER_DATA:
					return settleCust.getCICustomerBase().getCustomer().getAltTrackingNumber();
				case RATE_SCHEDULE_DATA:
					return DaoFactory.getYukonListDao().getYukonListEntry(settleCust.getCICustomerBase().getCustomer().getRateScheduleID().intValue()).getEntryText();
				case CIDLC_DEMAND_CHARGE_DATA:
					return decimalFormat.format(settleCust.getCIDLCDemandCharge());
				case FIRM_SERVICE_LEVEL_DATA:
					return decimalFormat.format(settleCust.getDemandLevel());
				case CONTRACTED_INTERRUPTIBLE_LOAD_DATA:
					return decimalFormat.format(settleCust.getCurtailableLoad());
				case MAX_MONTHLY_MEASURED_DEMAND_DATA:
					return decimalFormat.format(settleCust.getMaxMonthlyDemand());
	
				case CONTROLLED_DEMAND_INCENTIVE_DATA:
					return decimalFormat.format(settleCust.getControlledDemandIncentive());
				case ENERGY_REDUCTION_INCENTIVE_DATA:
					return decimalFormat.format(getERIIncentiveAmounts()[custIndex]);

				case EFSL_TOTAL_CHARGE_DATA:
					return decimalFormat.format( new Double(
													getEfslDispatchedCharges()[custIndex].doubleValue() + 
													getEfslEmergencyCharges()[custIndex].doubleValue() + 
													getEfslUnderfrequecyCharges()[custIndex].doubleValue()));
				case TOTAL_CIDLC_CREDITS_DEBITS_DATA:
				return decimalFormat.format( new Double(
												getEfslDispatchedCharges()[custIndex].doubleValue() + 
												getEfslEmergencyCharges()[custIndex].doubleValue() + 
												getEfslUnderfrequecyCharges()[custIndex].doubleValue() +
												settleCust.getControlledDemandIncentive().doubleValue() + 
												getERIIncentiveAmounts()[custIndex].doubleValue()));
												
				case LOAD_CONTROL_EVENT_SUMMARY_DATA:
					return "Hours";
				case TOTAL_DISPATCHED_HOURS_DATA:
					return intFormat.format(getTotalDispatchedHours());
				case TOTAL_UF_HOURS_DATA:
					return intFormat.format(getTotalUFHours());
					
				case EVENT_TYPE_HEADING_DATA:
					return lmEvent.getGroupName();
				case EVENT_DATE_HEADING_DATA:
					return dateFormat.format(lmEvent.getActualStartDateTime());
				case EVENT_START_TIME_HEADING_DATA:
					return timeFormat.format(lmEvent.getActualStartDateTime());
				case EVENT_STOP_TIME_HEADING_DATA:
					return timeFormat.format(lmEvent.getActualStopDateTime());
				case EVENT_DURATION_HEADING_DATA:
					return decimalFormat.format(lmEvent.getRoundedDuration());
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[]{
				CUSTOMER_NAME_STRING,
				CUSTOMER_NUMBER_STRING,
				ACCOUNT_NUMBER_STRING,
				DSM_APPLICATION_NUMBER_STRING,
				RATE_SCHEDULE_STRING, 
				MAX_MONTHLY_MEASURED_DEMAND_STRING,
				FIRM_SERVICE_LEVEL_STRING,
				CONTRACTED_INTERRUPTIBLE_LOAD_STRING,
				CIDLC_DEMAND_CHARGE_STRING,
				CONTROLLED_DEMAND_INCENTIVE_STRING,
				ENERGY_REDUCTION_INCENTIVE_STRING,
				EFSL_TOTAL_CHARGE_STRING,
				TOTAL_CIDLC_CREDITS_DEBITS_STRING,
				
				LOAD_CONTROL_EVENT_SUMMARY_STRING,
				TOTAL_DISPATCHED_HOURS_STRING,
				TOTAL_UF_HOURS_STRING,
				
				EVENT_TYPE_HEADING_STRING,
				EVENT_DATE_HEADING_STRING,
				EVENT_START_TIME_HEADING_STRING,
				EVENT_STOP_TIME_HEADING_STRING,
				EVENT_DURATION_HEADING_STRING
			};
		}
		return columnNames;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnTypes()
	 */
	public Class[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = new Class[NUMBER_COLUMNS];
				for (int i = 0; i < NUMBER_COLUMNS; i++)
					columnTypes[i] = String.class;
		}
			
		return columnTypes;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{
		if(columnProperties == null)
		{
			int colHeight = 14;
			if (columnProperties == null) {
				columnProperties = new ColumnProperties[]{
					//posX, posY, width, height, numberFormatString

					//HEADER
					new ColumnProperties(0, 1, 175, null),					//CUSTOMER_NAME_STRING,
					new ColumnProperties(0, (colHeight*1), 175, null),		//CUSTOMER_NUMBER_STRING,
					new ColumnProperties(0, (colHeight*2), 175, null),		//ACCOUNT_NUMBER_STRING,
					new ColumnProperties(0, (colHeight*3), 175, null),		//DSM_APPLICATION_NUMBER_STRING,
					new ColumnProperties(0, (colHeight*4), 175, null),		//RATE_SCHEDULE_STRING,

					new ColumnProperties(0, (colHeight*6), 175, null),		//MAX_MONTHLY_MEASURED_DEMAND_STRING
					new ColumnProperties(0, (colHeight*7), 175, null),		//FIRM_SERVICE_LEVEL_STRING,
					new ColumnProperties(0, (colHeight*8), 175, null),		//CONTRACTED_INTERRUPTIBLE_LOAD_STRING,
					new ColumnProperties(0, (colHeight*9), 175, null),		//CIDLC_DEMAND_CHARNGE_STRING,

					new ColumnProperties(0, (colHeight*11), 175, null),		//CONTROLLED_DEMAND_INCENTIVE_STRING,
					new ColumnProperties(0, (colHeight*12), 175, null),		//ENERGY_REDUCTION_INCENTIVE_STRING,
					new ColumnProperties(0, (colHeight*13), 175, null),		//EFSL_TOTAL_CHARGE_STRING
					new ColumnProperties(0, (colHeight*14), 175, null),		//TOTAL_CIDLC_CREDITS_DEBITS_STRING

					new ColumnProperties(0, (colHeight*16), 175, null),		//LOAD_CONTROL_EVENT_SUMMARY_STRING
					new ColumnProperties(0, (colHeight*17), 175, null),		//TOTAL_DISPATCHED_HOURS_STRING
					new ColumnProperties(0, (colHeight*18), 175, null),		//TOTAL_UF_HOURS_STRING

					//ITEM BAND
					new ColumnProperties(0, (colHeight*20), 175, null),		//EVENT_TYPE_HEADING_STRING			
					new ColumnProperties(175, (colHeight*20), 75, null),	//EVENT_DATE_HEADING_STRING
					new ColumnProperties(250, (colHeight*20), 75, null),	//EVENT_START_TIME_HEADING_STRING
					new ColumnProperties(325, (colHeight*20), 75, null),	//EVENT_STOP_TIME_HEADING_STRING
					new ColumnProperties(400, (colHeight*20), 75, null)		//EVENT_DURATION_HEADING_STRING
				};				
			}
		}		
		return columnProperties;
	}
	
	protected void loadDataVector()
	{
		int size = getDataObjects().size();	
		for (int i = 0; i < (getCustomerIDS().length * size); i++)	//loop (number of customers) for each customer's data to display
		{
			getData().add(new Integer( i));
		}
	}	/**
	 * @return
	 */
	public Double getTotalDispatchedHours()
	{
		if( totalDispatchedHours == null)
			loadTotalControlHours();

		return totalDispatchedHours;
	}

	/**
	 * @return
	 */
	public Double getTotalUFHours()
	{
		if( totalUFHours == null)
			loadTotalControlHours();
		return totalUFHours;
	}

	private void loadTotalControlHours ()
	{
		double totalDisp = 0;
		double totalUF = 0;
		for (int i = 0; i < getDataObjects().size(); i++)
		{
			LMEvent event = (LMEvent)getDataObjects().get(i);
			switch (event.getEventType())
			{
				case LMEvent.DISPATCHED_EVENT:
					totalDisp += event.getRoundedDuration().doubleValue();
					break;
				case LMEvent.UF_EVENT:
					totalUF += event.getRoundedDuration().doubleValue();
				default :
					break;
			}
		}
		totalDispatchedHours = new Double(totalDisp);
		totalUFHours = new Double(totalUF);
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}	
	
}