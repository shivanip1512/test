package com.cannontech.analysis.tablemodel;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.lm.SettlementCustomer;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.db.customer.CICustomerPointData;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;

/**
 * Extending classes must implement:
 *   addDataRow(ResultSet)    - add a "row" object to the data Vector
 *   buildSQLStatement()    - Returns the sql query statment
 */
public class HECO_MonthlyBillingSettlementModel extends HECO_SettlementModelBase {
    protected final int NUMBER_ROWS = 17;
    protected final int NUMBER_COLUMNS = 2;    // This is the number of STATIC Colums.  Customers are columns, but are added dynamically

    public final int HEADER_COLUMN = 0;
    public final int MONTHLY_TOTALS_COLUMN = 1;

    private static String title = "Monthly Billing Settlement Report";

    //ROW indexes (this model displays rows instead of columns)
    public static final int CUSTOMER_NAME_DATA = 0;
    public static final int CUSTOMER_NUMBER_DATA = 1;
    public static final int ACCOUNT_NUMBER_DATA = 2;
    public static final int DSM_APPLICATION_NUMBER_DATA = 3;
    public static final int RATE_SCHEDULE_DATA = 4;
    public static final int CIDLC_DEMAND_CHARGE_DATA = 5;
    public static final int FIRM_SERVICE_LEVEL_DATA = 6;
    public static final int CONTRACTED_INTERRUPTIBLE_LOAD_DATA = 7;
    public static final int MAX_MONTHLY_MEASURED_DEMAND_DATA = 8;

    public static final int CONTROLLED_DEMAND_INCENTIVE_DATA = 9;
    public static final int ENERGY_REDUCTION_INCENTIVE_DATA = 10;
    public static final int TOTAL_CIDLC_INCENTIVE_DATA = 11;

    public static final int EFSL_DISPATCHED_DATA = 12;
    public static final int EFSL_EMERGENCY_DATA = 13;
    public static final int EFSL_UNDERFREQUENCY_DATA = 14;
    public static final int EFSL_TOTAL_CHARGE_DATA = 15;

    public static final int TOTAL_CIDLC_CREDITS_DEBITS_DATA = 16;

    public static final String CUSTOMER_NAME_STRING = "Customer Name";
    public static final String CUSTOMER_NUMBER_STRING = "Customer Number";
    public static final String ACCOUNT_NUMBER_STRING = "Account Number";
    public static final String DSM_APPLICATION_NUMBER_STRING = "DSM Application Number";
    public static final String RATE_SCHEDULE_STRING = "Rate Schedule";
    public static final String CIDLC_DEMAND_CHARGE_STRING = "CIDLC Demand Charge";
    public static final String FIRM_SERVICE_LEVEL_STRING = "Firm Service Level";
    public static final String CONTRACTED_INTERRUPTIBLE_LOAD_STRING = "Contracted Interruptible Load";
    public static final String MAX_MONTHLY_MEASURED_DEMAND_STRING = "Max Monthly Measured Demand";

    public static final String CONTROLLED_DEMAND_INCENTIVE_STRING = "Controlled Demand Incentive";
    public static final String ENERGY_REDUCTION_INCENTIVE_STRING = "Energy Reduction Incentive";
    public static final String TOTAL_CIDLC_INCENTIVE_STRING = "Total CIDLC Incentive";

    public static final String EFSL_DISPATCHED_STRING = "EFSLC Dispatched";
    public static final String EFSL_EMERGENCY_STRING = "EFSLC Emergency";
    public static final String EFSL_UNDERFREQUENCY_STRING = "EFSLC Underfrequency";
    public static final String EFSL_TOTAL_CHARGE_STRING = "Total EFSL Charge";
    public static final String TOTAL_CIDLC_CREDITS_DEBITS_STRING = "Total CIDLC Credits/Debits";

    private static final String[] ROW_HEADINGS = new String[] {
        CUSTOMER_NAME_STRING,
        CUSTOMER_NUMBER_STRING,
        ACCOUNT_NUMBER_STRING,
        DSM_APPLICATION_NUMBER_STRING,
        RATE_SCHEDULE_STRING,
        CIDLC_DEMAND_CHARGE_STRING,
        FIRM_SERVICE_LEVEL_STRING,
        CONTRACTED_INTERRUPTIBLE_LOAD_STRING,
        MAX_MONTHLY_MEASURED_DEMAND_STRING,
        CONTROLLED_DEMAND_INCENTIVE_STRING,
        ENERGY_REDUCTION_INCENTIVE_STRING,
        TOTAL_CIDLC_INCENTIVE_STRING,
        EFSL_DISPATCHED_STRING,
        EFSL_EMERGENCY_STRING,
        EFSL_UNDERFREQUENCY_STRING,
        EFSL_TOTAL_CHARGE_STRING,
        TOTAL_CIDLC_CREDITS_DEBITS_STRING
    };

    public static void main(String[] args) {
        HECO_MonthlyBillingSettlementModel model = new HECO_MonthlyBillingSettlementModel();
        GregorianCalendar tempCal = new GregorianCalendar();
        tempCal.setTime(model.getStartDate());
        tempCal.set(Calendar.DAY_OF_MONTH, 15);
        model.setStartDate(tempCal.getTime());
        model.getColumnNames();

    }
    /**
     * Constructor class
     */
    public HECO_MonthlyBillingSettlementModel(Date start_, Date stop_) {
        super(start_, stop_);//default type
    }

    public HECO_MonthlyBillingSettlementModel() {
        this(null, null);
    }

    /**
     * Constructor class
     * Only ONE energycompanyID is used
     */
    public HECO_MonthlyBillingSettlementModel(Integer ecID_) {
        this();//default type
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
     */
    @Override
    public Object getAttribute(int columnIndex, Object o) {
        if(o instanceof Integer) {
            int rowIndex = ((Integer)o).intValue();
            if(columnIndex == HEADER_COLUMN) {
                return ROW_HEADINGS[rowIndex];
            }

            if(columnIndex == MONTHLY_TOTALS_COLUMN) {
                switch(rowIndex) {
                    case CONTROLLED_DEMAND_INCENTIVE_DATA:
                        return decimalFormat.format(getControlledDemandIncentiveTotal());
                    case ENERGY_REDUCTION_INCENTIVE_DATA:
                        return decimalFormat.format(getERIIncentiveAmountsTotal());
                    case TOTAL_CIDLC_INCENTIVE_DATA:
                        return decimalFormat.format(getAllTotalCIDLCIncentive());

                    case EFSL_DISPATCHED_DATA:
                        return decimalFormat.format(getEfslDispatchedTotal());
                    case EFSL_EMERGENCY_DATA:
                        return decimalFormat.format(getEfslEmergencyTotal());
                    case EFSL_UNDERFREQUENCY_DATA:
                        return decimalFormat.format(getEfslUnderfrequecyTotal());
                    case EFSL_TOTAL_CHARGE_DATA:
                        return decimalFormat.format(getAllTotalEFSLCharges());
                    case TOTAL_CIDLC_CREDITS_DEBITS_DATA:
                        return decimalFormat.format(getAllTotalCIDLCredits());
                    default:
                        return "";
                }
            }

            for(int i = 0; i < getCustomerIDS().length; i++) {
                if(columnIndex == i + NUMBER_COLUMNS) {
                    SettlementCustomer settleCust = getSettlementCustomer(getCustomerIDS()[i]);
                    switch (rowIndex) {
                        case CUSTOMER_NAME_DATA :
                            return settleCust.getCICustomerBase().getCiCustomerBase().getCompanyName();
                        case CUSTOMER_NUMBER_DATA:
                            return settleCust.getCICustomerBase().getCustomer().getCustomerNumber();
                        case ACCOUNT_NUMBER_DATA: {
                            try {
                                CustomerAccount customerAccount = YukonSpringHook.getBean(CustomerAccountDao.class).getAccountByCustomerId(settleCust.getCustomerID().intValue());
                                return customerAccount.getAccountNumber();
                            } catch (IncorrectResultSizeDataAccessException e) {
                                return "Acct # ?";
                            }
                        }
                        case DSM_APPLICATION_NUMBER_DATA:
                            return settleCust.getCICustomerBase().getCustomer().getAltTrackingNumber();
                        case RATE_SCHEDULE_DATA:
                            return YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(settleCust.getCICustomerBase().getCustomer().getRateScheduleID().intValue()).getEntryText();
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
                            return decimalFormat.format(getERIIncentiveAmounts()[i]);
                        case TOTAL_CIDLC_INCENTIVE_DATA:
                            return decimalFormat.format(getTotalCIDLCIncentive(i));

                        case EFSL_DISPATCHED_DATA:
                            return decimalFormat.format(getEfslDispatchedCharges()[i]);
                        case EFSL_EMERGENCY_DATA:
                            return decimalFormat.format(getEfslEmergencyCharges()[i]);
                        case EFSL_UNDERFREQUENCY_DATA:
                            return decimalFormat.format(getEfslUnderfrequecyCharges()[i]);
                        case EFSL_TOTAL_CHARGE_DATA:
                            return decimalFormat.format(getTotalEFSLCharges(i));
                        case TOTAL_CIDLC_CREDITS_DEBITS_DATA:
                        return decimalFormat.format(getTotalCIDLCredits(i));
                    }
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getColumnNames()
     */
    @Override
    public String[] getColumnNames() {
        if(columnNames == null) {
            columnNames = new String[NUMBER_COLUMNS + getCustomerIDS().length];
            columnNames [0] = "Field Name";
            columnNames [1] = "Month Totals";
            int i = 0;
            Iterator iter = getSettlementCustomerMap().entrySet().iterator();
            while(iter.hasNext()) {
                SettlementCustomer settleCust = (SettlementCustomer)((Map.Entry)iter.next()).getValue();
                columnNames [NUMBER_COLUMNS + (i++)] = settleCust.getCICustomerBase().getCiCustomerBase().getCompanyName();

                CICustomerPointData pointData = settleCust.getPointData(CICustomerPointData.TYPE_SETTLEMENT);
                if(pointData != null) {
                    pointIDCSV += pointData.getPointID().intValue() + ",";
                }
            }
        }
        return columnNames;
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getColumnTypes()
     */
    @Override
    public Class[] getColumnTypes() {
        if(columnTypes == null) {
            columnTypes = new Class[NUMBER_COLUMNS + getCustomerIDS().length];
                for (int i = 0; i < NUMBER_COLUMNS + getCustomerIDS().length; i++) {
                    columnTypes[i] = String.class;
                }
        }

        return columnTypes;
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getColumnProperties()
     */
    @Override
    public ColumnProperties[] getColumnProperties() {
        if(columnProperties == null) {
            int offset = 0;
            columnProperties = new ColumnProperties[NUMBER_COLUMNS + getCustomerIDS().length];
            //posX, posY, width, height, numberFormatString

            columnProperties[0] = new ColumnProperties(offset, 1, 150, null);
            columnProperties[1] = new ColumnProperties(offset+=150, 1, 75, null);
            offset += 75;   //update to current position.

            int width = 75;
            for (int i = 0; i < getCustomerIDS().length; i++) {
                offset = getAdjustedStartOffset(offset, width);
                columnProperties[NUMBER_COLUMNS + i] = new ColumnProperties(offset, 1, width, null);
                offset += width;
            }
        }
        return columnProperties;
    }

    @Override
    protected void loadDataVector() {
        for (int i = 0; i < NUMBER_ROWS; i++) {
            getData().add(new Integer(i));
        }
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getTitleString()
     */
    @Override
    public String getTitleString() {
        return title;
    }
}