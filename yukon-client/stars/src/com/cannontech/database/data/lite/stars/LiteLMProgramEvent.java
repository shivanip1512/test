package com.cannontech.database.data.lite.stars;

import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteLMProgramEvent extends LiteLMCustomerEvent {

	private int accountID = com.cannontech.database.db.stars.customer.CustomerAccount.NONE_INT;
	private int lmProgramID = 0;
	
	public LiteLMProgramEvent() {
		super();
	}
	
	public LiteLMProgramEvent(int eventID) {
		super();
		setEventID( eventID );
		setLiteType( LiteTypes.STARS_LMPROGRAM_EVENT );
	}
	
	/**
	 * Returns the accountID.
	 * @return int
	 */
	public int getAccountID() {
		return accountID;
	}

	/**
	 * Returns the lmProgramID.
	 * @return int
	 */
	public int getLmProgramID() {
		return lmProgramID;
	}

	/**
	 * Sets the accountID.
	 * @param accountID The accountID to set
	 */
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	/**
	 * Sets the lmProgramID.
	 * @param lmProgramID The lmProgramID to set
	 */
	public void setLmProgramID(int lmProgramID) {
		this.lmProgramID = lmProgramID;
	}

}
