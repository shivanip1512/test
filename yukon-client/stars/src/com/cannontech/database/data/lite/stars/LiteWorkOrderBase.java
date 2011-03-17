package com.cannontech.database.data.lite.stars;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.db.company.EnergyCompany;

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
	private String additionalOrderNumber = null;
	private int accountID = com.cannontech.database.db.stars.customer.CustomerAccount.NONE_INT;
	
	private int energyCompanyID = EnergyCompany.DEFAULT_ENERGY_COMPANY_ID;
	
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
		if (actionTaken == null) {
			//check for null and return "".  
			//This will resolve null values being stored in database instead of empty strings, in older jsps.
			return "";
		}
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

	public int getEnergyCompanyID() {
		return energyCompanyID;
	}

	public void setEnergyCompanyID(int energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

	public void retrieve()
	{
		try
		{
			SqlStatement stat = new SqlStatement( "SELECT ORDERNUMBER, WORKTYPEID, CURRENTSTATEID, SERVICECOMPANYID, DATEREPORTED, " +
												" ORDEREDBY, DESCRIPTION, DATESCHEDULED, DATECOMPLETED, ACTIONTAKEN, ACCOUNTID, ADDITIONALORDERNUMBER, ENERGYCOMPANYID " +
												" FROM " + com.cannontech.database.db.stars.report.WorkOrderBase.TABLE_NAME + " WOB, ECTOWORKORDERMAPPING MAP " +
												" WHERE WOB.ORDERID = MAP.WORKORDERID " + 
												" AND WOB.ORDERID = " + getLiteID(), CtiUtilities.getDatabaseAlias());

			stat.execute();

			if (stat.getRowCount() <= 0)
				throw new IllegalStateException(
					"Unable to find the WorkOrderBase with OrderID = " + getLiteID());

			Object[] results = stat.getRow(0);
            setOrderNumber( (String) results[0] );
            setWorkTypeID(( (java.math.BigDecimal) results[1] ).intValue());
            setCurrentStateID(( (java.math.BigDecimal) results[2] ).intValue());
            setServiceCompanyID(( (java.math.BigDecimal) results[3] ).intValue());
            setDateReported( new java.util.Date(((java.sql.Timestamp) results[4]).getTime()).getTime() );
            setOrderedBy( (String) results[5] );
            setDescription( (String) results[6] );
            setDateScheduled( new java.util.Date(((java.sql.Timestamp) results[7]).getTime()).getTime() );
            setDateCompleted( new java.util.Date(((java.sql.Timestamp) results[8]).getTime()).getTime() );
            setActionTaken( (String) results[9] );
            setAccountID(( (java.math.BigDecimal) results[10] ).intValue());
            setAdditionalOrderNumber( (String) results[11]);
            setEnergyCompanyID(( (java.math.BigDecimal) results[12]).intValue());
		}
		catch (Exception e)
		{
			CTILogger.error(e.getMessage(), e);
		}
	}

	public String getAdditionalOrderNumber() {
		return additionalOrderNumber;
	}

	public void setAdditionalOrderNumber(String additionalOrderNumber) {
		this.additionalOrderNumber = additionalOrderNumber;
	}

}
