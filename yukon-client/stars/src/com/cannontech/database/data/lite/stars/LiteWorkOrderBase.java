package com.cannontech.database.data.lite.stars;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteWorkOrderBase extends LiteBase {

	private String orderNumber = null;
	private int workTypeID = com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID;
	private int currentStateID = com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID;
	private int serviceCompanyID = com.cannontech.database.db.stars.report.ServiceCompany.NONE_INT;
	private long dateReported = 0;
	private long dateScheduled = 0;
	private long dateCompleted = 0;
	private String description = null;
	private String actionTaken = null;
	private String orderedBy = null;
	private int accountID = com.cannontech.database.db.stars.customer.CustomerAccount.NONE_INT;
	
	public LiteWorkOrderBase() {
		super();
		setLiteType( LiteTypes.STARS_WORK_ORDER_BASE );
	}
	
	public LiteWorkOrderBase(int orderID) {
		super();
		setOrderID( orderID );
		setLiteType( LiteTypes.STARS_WORK_ORDER_BASE );
	}
	
	public int getOrderID() {
		return getLiteID();
	}
	
	public void setOrderID(int orderID) {
		setLiteID( orderID );
	}
	/**
	 * Returns the actionTaken.
	 * @return String
	 */
	public String getActionTaken() {
		return actionTaken;
	}

	/**
	 * Returns the currentStateID.
	 * @return int
	 */
	public int getCurrentStateID() {
		return currentStateID;
	}

	/**
	 * Returns the dateCompleted.
	 * @return long
	 */
	public long getDateCompleted() {
		return dateCompleted;
	}

	/**
	 * Returns the dateReported.
	 * @return long
	 */
	public long getDateReported() {
		return dateReported;
	}

	/**
	 * Returns the dateScheduled.
	 * @return long
	 */
	public long getDateScheduled() {
		return dateScheduled;
	}

	/**
	 * Returns the description.
	 * @return String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the orderedBy.
	 * @return String
	 */
	public String getOrderedBy() {
		return orderedBy;
	}

	/**
	 * Returns the orderNumber.
	 * @return String
	 */
	public String getOrderNumber() {
		return orderNumber;
	}

	/**
	 * Returns the serviceCompanyID.
	 * @return int
	 */
	public int getServiceCompanyID() {
		return serviceCompanyID;
	}

	/**
	 * Returns the workTypeID.
	 * @return int
	 */
	public int getWorkTypeID() {
		return workTypeID;
	}

	/**
	 * Sets the actionTaken.
	 * @param actionTaken The actionTaken to set
	 */
	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}

	/**
	 * Sets the currentStateID.
	 * @param currentStateID The currentStateID to set
	 */
	public void setCurrentStateID(int currentStateID) {
		this.currentStateID = currentStateID;
	}

	/**
	 * Sets the dateCompleted.
	 * @param dateCompleted The dateCompleted to set
	 */
	public void setDateCompleted(long dateCompleted) {
		this.dateCompleted = dateCompleted;
	}

	/**
	 * Sets the dateReported.
	 * @param dateReported The dateReported to set
	 */
	public void setDateReported(long dateReported) {
		this.dateReported = dateReported;
	}

	/**
	 * Sets the dateScheduled.
	 * @param dateScheduled The dateScheduled to set
	 */
	public void setDateScheduled(long dateScheduled) {
		this.dateScheduled = dateScheduled;
	}

	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the orderedBy.
	 * @param orderedBy The orderedBy to set
	 */
	public void setOrderedBy(String orderedBy) {
		this.orderedBy = orderedBy;
	}

	/**
	 * Sets the orderNumber.
	 * @param orderNumber The orderNumber to set
	 */
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * Sets the serviceCompanyID.
	 * @param serviceCompanyID The serviceCompanyID to set
	 */
	public void setServiceCompanyID(int serviceCompanyID) {
		this.serviceCompanyID = serviceCompanyID;
	}

	/**
	 * Sets the workTypeID.
	 * @param workTypeID The workTypeID to set
	 */
	public void setWorkTypeID(int workTypeID) {
		this.workTypeID = workTypeID;
	}

	/**
	 * Returns the accountID.
	 * @return int
	 */
	public int getAccountID() {
		return accountID;
	}

	/**
	 * Sets the accountID.
	 * @param accountID The accountID to set
	 */
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

}
