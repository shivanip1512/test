package com.cannontech.database.data.lite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.spring.YukonSpringHook;

public class LiteCustomer extends LiteBase {

    private int primaryContactID = 0;
    private int customerTypeID = com.cannontech.database.data.customer.CustomerTypes.INVALID;
    private String timeZone = null;
    private String customerNumber = CtiUtilities.STRING_NONE;
    private int rateScheduleID = CtiUtilities.NONE_ZERO_ID;
    private String altTrackNum = CtiUtilities.STRING_NONE;
    private String temperatureUnit = TemperatureUnit.FAHRENHEIT.getLetter();

    // non-persistent data,
    // contains com.cannontech.database.data.lite.LiteContact
    private Vector<LiteContact> additionalContacts = null;

    // contains int ,Used for residential customers only
    private Vector<Integer> accountIDs = null;
    private int energyCompanyID = -1;

    public LiteCustomer() {
        super();
        setCustomerID(-1); // LiteBase sets this to 0 as a default, but 0 is a valid customerId
        setLiteType(LiteTypes.CUSTOMER);
    }

    public LiteCustomer(int customerID) {
        super();
        setCustomerID(customerID);
        setLiteType(LiteTypes.CUSTOMER);
    }

    public int getCustomerID() {
        return getLiteID();
    }

    public void setCustomerID(int customerID) {
        setLiteID(customerID);
    }

    public void retrieve(String dbAlias) {
        PreparedStatement pstmt = null;
        java.sql.Connection conn = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(dbAlias);

            String sql = "SELECT PrimaryContactID, CustomerTypeID, TimeZone, CustomerNumber, RateScheduleID, AltTrackNum, TemperatureUnit" + " FROM " + Customer.TABLE_NAME + " WHERE CustomerID = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, getCustomerID());
            rset = pstmt.executeQuery();

            if (rset.next()) {
                setPrimaryContactID(rset.getInt(1));
                setCustomerTypeID(rset.getInt(2));
                setTimeZone(rset.getString(3));
                setCustomerNumber(rset.getString(4));
                setRateScheduleID(rset.getInt(5));
                setAltTrackingNumber(rset.getString(6));
                setTemperatureUnit(rset.getString(7));
            } else
                throw new IllegalStateException("Unable to find the Customer with CustomerID = " + getCustomerID());
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt, conn);
        }
    }

    private synchronized void retrieveAccountIDs() {
        PreparedStatement pstmt = null;
        java.sql.Connection conn = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getYukonConnection();

            String sql = "SELECT acct.AccountID, map.EnergyCompanyID " + " FROM CustomerAccount acct, ECToAccountMapping map " + " WHERE acct.CustomerID = ?" + " AND acct.AccountID = map.AccountID";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, getCustomerID());
            rset = pstmt.executeQuery();

            accountIDs.removeAllElements();

            while (rset.next()) {
                accountIDs.add(new Integer(rset.getInt(1)));
                energyCompanyID = rset.getInt(2);
            }

        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt, conn);
        }
    }

    /**
     * Returns the customerTypeID.
     */
    public int getCustomerTypeID() {
        return customerTypeID;
    }

    /**
     * Returns the primaryContactID.
     */
    public int getPrimaryContactID() {
        return primaryContactID;
    }

    /**
     * Sets the additionalContacts.
     */
    public void setAdditionalContacts(Vector<LiteContact> additionalContacts) {
        this.additionalContacts = additionalContacts;
    }

    public void setAdditionalContacts(List<LiteContact> additionalContacts) {

        this.additionalContacts = new Vector<LiteContact>();
        this.additionalContacts.addAll(additionalContacts);
    }

    /**
     * Sets the customerTypeID.
     */
    public void setCustomerTypeID(int customerTypeID) {
        this.customerTypeID = customerTypeID;
    }

    /**
     * Sets the primaryContactID.
     */
    public void setPrimaryContactID(int primaryContactID) {
        this.primaryContactID = primaryContactID;
    }

    /**
     * Returns the timeZone.
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the timeZone.
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    /**
     * Returns the additionalContacts.
     */
    public synchronized Vector<LiteContact> getAdditionalContacts() {
        if (additionalContacts == null) {
            additionalContacts = new Vector<LiteContact>(10);
            additionalContacts.removeAllElements();
            additionalContacts.addAll(YukonSpringHook.getBean(ContactDao.class)
                                                     .getAdditionalContactsForCustomer(getCustomerID()));
        }
        return additionalContacts;
    }

    /**
     * Returns the customer account IDs
     */
    public synchronized Vector<Integer> getAccountIDs() {
        if (accountIDs == null) {
            accountIDs = new Vector<Integer>(1);
            retrieveAccountIDs();
        }
        return accountIDs;
    }

    /**
     * Used for residential customers only. 
     * A residential customer should belong to only one energy company.
     */
    public int getEnergyCompanyID() {
        if (energyCompanyID == -1) {
            getAdditionalContacts();
            getAccountIDs();
        }
        return energyCompanyID;
    }

    public void setAccountIDs(Vector<Integer> vector) {
        accountIDs = vector;
    }

    public void setEnergyCompanyID(int i) {
        energyCompanyID = i;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String custNum) {
        this.customerNumber = custNum;
    }

    public String getAltTrackingNumber() {
        return altTrackNum;
    }

    public void setAltTrackingNumber(String altNum) {
        this.altTrackNum = altNum;
    }

    public int getRateScheduleID() {
        return rateScheduleID;
    }

    public void setRateScheduleID(int rSched) {
        this.rateScheduleID = rSched;
    }
}