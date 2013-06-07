package com.cannontech.messaging.message.loadcontrol.data;

public class CiCustomerBase {

    private long customerId;
    private String companyName;
    private double customerDemandLevel;
    private double curtailAmount;
    private String curtailmentAgreement;
    private String timeZone;
    private long customerOrder;

    /**
     * Returns the companyName.
     * @return String
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Returns the curtailAmount.
     * @return double
     */
    public double getCurtailAmount() {
        return curtailAmount;
    }

    /**
     * Returns the curtailmentAgreement.
     * @return String
     */
    public String getCurtailmentAgreement() {
        return curtailmentAgreement;
    }

    /**
     * Returns the customerDemandLevel.
     * @return double
     */
    public double getCustomerDemandLevel() {
        return customerDemandLevel;
    }

    /**
     * Returns the customerId.
     * @return long
     */
    public long getCustomerId() {
        return customerId;
    }

    /**
     * Returns the customerOrder.
     * @return long
     */
    public long getCustomerOrder() {
        return customerOrder;
    }

    /**
     * Returns the timeZone.
     * @return String
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the companyName.
     * @param companyName The companyName to set
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Sets the curtailAmount.
     * @param curtailAmount The curtailAmount to set
     */
    public void setCurtailAmount(double curtailAmount) {
        this.curtailAmount = curtailAmount;
    }

    /**
     * Sets the curtailmentAgreement.
     * @param curtailmentAgreement The curtailmentAgreement to set
     */
    public void setCurtailmentAgreement(String curtailmentAgreement) {
        this.curtailmentAgreement = curtailmentAgreement;
    }

    /**
     * Sets the customerDemandLevel.
     * @param customerDemandLevel The customerDemandLevel to set
     */
    public void setCustomerDemandLevel(double customerDemandLevel) {
        this.customerDemandLevel = customerDemandLevel;
    }

    /**
     * Sets the customerId.
     * @param customerId The customerId to set
     */
    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    /**
     * Sets the customerOrder.
     * @param customerOrder The customerOrder to set
     */
    public void setCustomerOrder(long customerOrder) {
        this.customerOrder = customerOrder;
    }

    /**
     * Sets the timeZone.
     * @param timeZone The timeZone to set
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isRampingIn() {
        return false;
    }

    public boolean isRampingOut() {
        return false;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getCompanyName();
    }
}
