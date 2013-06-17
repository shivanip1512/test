package com.cannontech.loadcontrol.data;

/**
 * @author aaron
 */
public class LMCICustomerBase 
{
	private Long customerID;
	private String companyName;
	private Double customerDemandLevel;
	private Double curtailAmount;
	private String curtailmentAgreement;
	private String timeZone;
	private Long customerOrder;
	
	/**
	 * Returns the companyName.
	 * @return String
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Returns the curtailAmount.
	 * @return Double
	 */
	public Double getCurtailAmount() {
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
	 * @return Double
	 */
	public Double getCustomerDemandLevel() {
		return customerDemandLevel;
	}

	/**
	 * Returns the customerID.
	 * @return Long
	 */
	public Long getCustomerID() {
		return customerID;
	}

	/**
	 * Returns the customerOrder.
	 * @return Long
	 */
	public Long getCustomerOrder() {
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
	public void setCurtailAmount(Double curtailAmount) {
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
	public void setCustomerDemandLevel(Double customerDemandLevel) {
		this.customerDemandLevel = customerDemandLevel;
	}

	/**
	 * Sets the customerID.
	 * @param customerID The customerID to set
	 */
	public void setCustomerID(Long customerID) {
		this.customerID = customerID;
	}

	/**
	 * Sets the customerOrder.
	 * @param customerOrder The customerOrder to set
	 */
	public void setCustomerOrder(Long customerOrder) {
		this.customerOrder = customerOrder;
	}

	/**
	 * Sets the timeZone.
	 * @param timeZone The timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getCompanyName();
	}

	public boolean isRampingIn()
	{
		return false;
	}

	public boolean isRampingOut()
	{
		return false;
	}

}
