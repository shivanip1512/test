package com.cannontech.database.data.lite;

import com.cannontech.common.constants.YukonListEntryTypes;

public class LiteCICustomer extends LiteCustomer {
    private int mainAddressID = 0;
    private String companyName = null;
    private double demandLevel = 0.0;
    private double curtailAmount = 0.0;
    private int ciCustType = YukonListEntryTypes.CUSTOMER_TYPE_COMMERCIAL;

    /**
     * CICustomerBase objects extend Customer. 
     * Therefore, you should always have a customerId to construct this object with.
     * use {@link #LiteCICustomer(int)}
     */
    @Deprecated 
    public LiteCICustomer() {
        this(-1);
    }

    public LiteCICustomer(int id) {
        super(id);
        setLiteType(LiteTypes.CUSTOMER_CI);
    }

    /**
     * Returns the companyName.
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Returns the curtailAmount.
     */
    public double getCurtailAmount() {
        return curtailAmount;
    }

    /**
     * Returns the demandLevel.
     */
    public double getDemandLevel() {
        return demandLevel;
    }

    /**
     * Returns the mainAddressID.
     */
    public int getMainAddressID() {
        return mainAddressID;
    }

    /**
     * Sets the companyName.
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Sets the curtailAmount.
     */
    public void setCurtailAmount(double curtailAmount) {
        this.curtailAmount = curtailAmount;
    }

    /**
     * Sets the demandLevel.
     */
    public void setDemandLevel(double demandLevel) {
        this.demandLevel = demandLevel;
    }

    /**
     * Sets the mainAddressID.
     */
    public void setMainAddressID(int mainAddressID) {
        this.mainAddressID = mainAddressID;
    }

    @Override
    public String toString() {
        return getCompanyName();
    }

    public int getCICustType() {
        return ciCustType;
    }

    public void setCICustType(int ciCustType) {
        this.ciCustType = ciCustType;
    }
}