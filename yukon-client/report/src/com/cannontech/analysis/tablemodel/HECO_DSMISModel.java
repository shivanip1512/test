package com.cannontech.analysis.tablemodel;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.lm.SettlementCustomer;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;

/**
 * Extending classes must implement:
 *   addDataRow(ResultSet)    - add a "row" object to the data Vector
 *   buildSQLStatement()    - Returns the sql query statment
 */
public class HECO_DSMISModel extends HECO_SettlementModelBase {
    protected final int NUMBER_COLUMNS = 9;

    private static String title = "DSMIS Report";

    //ROW indexes (this model displays rows instead of columns)
    public static final int DSM_APPLICATION_NUMBER_DATA = 0;
    public static final int ACCOUNT_NUMBER_DATA = 1;
    public static final int START_DATE_DATA = 2;

    public static final int CONTROLLED_DEMAND_INCENTIVE_DATA = 3;
    public static final int CONTROLLED_DEMAND_INCENTIVE_AMOUNT_DATA = 4;
    public static final int ENERGY_REDUCTION_INCENTIVE_DATA = 5;
    public static final int ENERGY_REDUCTION_INCENTIVE_AMOUNT_DATA = 6;
    public static final int EFSL_TOTAL_DATA = 7;
    public static final int EFSL_TOTAL_CHARGE_DATA = 8;

    public static final String DSM_APPLICATION_NUMBER_STRING = "DSM Application Number";
    public static final String ACCOUNT_NUMBER_STRING = "Account Number";
    public static final String START_DATE_STRING = "Start Date";

    public static final String CONTROLLED_DEMAND_INCENTIVE_STRING = "CDI";
    public static final String CONTROLLED_DEMAND_INCENTIVE_AMOUNT_STRING = "CDI Amount";
    public static final String ENERGY_REDUCTION_INCENTIVE_STRING = "ERI";
    public static final String ENERGY_REDUCTION_INCENTIVE_AMOUNT_STRING = "ERI Amount";
    public static final String EFSL_TOTAL_STRING = "EFSLC";
    public static final String EFSL_TOTAL_CHARGE_STRING = "EFSLC Amount";


    public static void main(String[] args) {
        HECO_DSMISModel model = new HECO_DSMISModel();
        GregorianCalendar tempCal = new GregorianCalendar();
        tempCal.setTime(model.getStartDate());
        tempCal.set(Calendar.DAY_OF_MONTH, 15);
        model.setStartDate(tempCal.getTime());
        model.getColumnNames();

    }
    /**
     * Constructor class
     */
    public HECO_DSMISModel(Date start_, Date stop_) {
        super(start_, stop_);//default type
        setFieldSeparator("");    //THERE IS NONE, This is a character specific format
        decimalFormat.setNegativePrefix("");
        decimalFormat.setPositivePrefix("");
        decimalFormat.setNegativeSuffix(" ");
        decimalFormat.setPositiveSuffix(" ");
    }

    public HECO_DSMISModel() {
        this(null, null);
    }

    /**
     * Constructor class
     * Only ONE energycompanyID is used
     */
    public HECO_DSMISModel(Integer ecID_) {
        this();//default type
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
     */
    @Override
    public Object getAttribute(int columnIndex, Object o)
    {
        if(o instanceof Integer) {
            int rowIndex = ((Integer)o).intValue();    //customerIndex also

            SettlementCustomer settleCust = getSettlementCustomer(getCustomerIDS()[rowIndex]);
            switch (columnIndex) {
                case DSM_APPLICATION_NUMBER_DATA: {
                    String value = settleCust.getCICustomerBase().getCustomer().getAltTrackingNumber();
                    while (value.length() < 10) {
                        value = " " + value;
                    }

                    return value;
                }
                case ACCOUNT_NUMBER_DATA: {
                    String accountNumber = "";
                    try {
                        CustomerAccount customerAccount = YukonSpringHook.getBean(CustomerAccountDao.class).getAccountByCustomerId(settleCust.getCustomerID().intValue());
                        accountNumber = customerAccount.getAccountNumber();
                    } catch (IncorrectResultSizeDataAccessException e) {
                        // do nothing
                    }

                    accountNumber = StringUtils.leftPad(accountNumber, 11);
                    return accountNumber;
                }
                case START_DATE_DATA:
                    return dateFormat_yyyy.format(getStartDate());
                case CONTROLLED_DEMAND_INCENTIVE_DATA:
                    return "LC1";
                case CONTROLLED_DEMAND_INCENTIVE_AMOUNT_DATA: {
                    String value = decimalFormat.format(settleCust.getControlledDemandIncentive());
                    while(value.length() < 11) {
                        value = " " + value;
                    }
                    return value;
                }
                case ENERGY_REDUCTION_INCENTIVE_DATA:
                    return "LC2";
                case ENERGY_REDUCTION_INCENTIVE_AMOUNT_DATA: {
                    String value = decimalFormat.format(getERIIncentiveAmounts()[rowIndex]);
                    while(value.length() < 11) {
                        value = " " + value;
                    }
                        return value;
                }
                case EFSL_TOTAL_DATA:
                    return "LC3";
                case EFSL_TOTAL_CHARGE_DATA: {
                    String value = decimalFormat.format(getTotalEFSLCharges(rowIndex));
                    while(value.length() < 11) {
                        value = " " + value;
                    }
                        return value;
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
            columnNames = new String[NUMBER_COLUMNS];
            columnNames[0] = DSM_APPLICATION_NUMBER_STRING;
            columnNames[1] = ACCOUNT_NUMBER_STRING;
            columnNames[2] = START_DATE_STRING;
            columnNames[3] = CONTROLLED_DEMAND_INCENTIVE_STRING;
            columnNames[4] = CONTROLLED_DEMAND_INCENTIVE_AMOUNT_STRING;
            columnNames[5] = ENERGY_REDUCTION_INCENTIVE_STRING;
            columnNames[6] = ENERGY_REDUCTION_INCENTIVE_AMOUNT_STRING;
            columnNames[7] = EFSL_TOTAL_STRING;
            columnNames[8] = EFSL_TOTAL_CHARGE_STRING;
        }
        return columnNames;
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getColumnTypes()
     */
    @Override
    public Class[] getColumnTypes() {
        if(columnTypes == null) {
            columnTypes = new Class[NUMBER_COLUMNS];
            for (int i = 0; i < NUMBER_COLUMNS; i++) {
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
            columnProperties = new ColumnProperties[NUMBER_COLUMNS];
                //posX, posY, width, height, numberFormatString

                columnProperties[0] = new ColumnProperties(offset, 1, 75, null);
                columnProperties[1] = new ColumnProperties(offset+=75, 1, 75, null);
                columnProperties[2] = new ColumnProperties(offset+=75, 1, 75, null);
                columnProperties[3] = new ColumnProperties(offset+=75, 1, 75, null);
                columnProperties[4] = new ColumnProperties(offset+=75, 1, 75, null);
                columnProperties[5] = new ColumnProperties(offset+=75, 1, 75, null);
                columnProperties[6] = new ColumnProperties(offset+=75, 1, 75, null);
                columnProperties[7] = new ColumnProperties(offset+=75, 1, 75, null);
                columnProperties[8] = new ColumnProperties(offset+=75, 1, 75, null);
        }
        return columnProperties;
    }

    @Override
    protected void loadDataVector() {
        //Add customerID indecies to data objects
        for (int i = 0; i < getCustomerIDS().length; i++) {
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
