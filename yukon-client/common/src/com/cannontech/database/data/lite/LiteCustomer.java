package com.cannontech.database.data.lite;

import java.util.List;

import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class LiteCustomer extends LiteBase {

    private int primaryContactID = 0;
    private int customerTypeID = com.cannontech.database.data.customer.CustomerTypes.INVALID;
    private String timeZone = null;
    private String customerNumber = CtiUtilities.STRING_NONE;
    private int rateScheduleID = CtiUtilities.NONE_ZERO_ID;
    private String altTrackNum = CtiUtilities.STRING_NONE;
    private String temperatureUnit = TemperatureUnit.FAHRENHEIT.getLetter();

    // non-persistent data,
    private List<LiteContact> additionalContacts = null;

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
     * This is loaded when returned from databaseCache. 
     * Unnecessary to load/set when this class is only used for Customer database table updates as it is non-persistent wrt this class.
     */
    public void setAdditionalContacts(List<LiteContact> additionalContacts) {
        this.additionalContacts = additionalContacts;
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
     * Will lazy load if null. DatabaseCache (CICustomerLoader) loads this 
     *  as do most the CustomerDao calls that utilize TypeAwareCustomerRowMapper.
     * One that is known to not load this when LitCustomer is loaded is CustomerDao.callbackWithAllCiCustomers. 
     */
    public synchronized List<LiteContact> getAdditionalContacts() {
        if (additionalContacts == null) {
            additionalContacts = Lists.newArrayList();
            additionalContacts.addAll(YukonSpringHook.getBean(ContactDao.class).getAdditionalContactsForCustomer(getCustomerID()));
        }
        return additionalContacts;
    }

    /**
     * Used for residential customers only. 
     * A residential customer should belong to only one energy company.
     */
    public int getEnergyCompanyID() {
        return energyCompanyID;
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