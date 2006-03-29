package com.cannontech.database.data.capcontrol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.database.db.capcontrol.CCMonitorBankList;

/**
 * This type was created in VisualAge.
 */
public class CapBank extends CapControlDeviceBase 
{
	// OPStates that a CapBank can be in
	public final static String SWITCHED_OPSTATE = "Switched";
	public final static String FIXED_OPSTATE = "Fixed";	
	public final static String UNINSTALLED_OPSTATE = "Uninstalled";
	public final static String STANDALONE_OPSTATE = "Stand Alone";

	//all control type of switches
	public final static String CONTROL_TYPE_DLC = "CTI DLC";
	public final static String CONTROL_TYPE_PAGING = "CTI Paging";
	public final static String CONTROL_TYPE_TELEMETRIC = "Telemetric";
   public final static String CONTROL_TYPE_FM = "CTI FM";
   public final static String CONTROL_TYPE_FP_PAGING = "FP Paging";

	//all manufactures of switches
	public final static String SWITCHMAN_WESTING = "Westinghouse";
	public final static String SWITCHMAN_ABB = "ABB";
	public final static String SWITCHMAN_COOPER = "Cooper";
	public final static String SWITCHMAN_SIEMENS = "Siemens";
   public final static String SWITCHMAN_TRINETICS = "Trinetics";

	//all types of switches
	public final static String SWITCHTYPE_OIL = "Oil";
   public final static String SWITCHTYPE_VACUUM = "Vacuum";
	
	private com.cannontech.database.db.capcontrol.CapBank capBank = null;
	private List ccMonitorBankList = new ArrayList();
	
/**
 */
public CapBank() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getCapBank().add();
	CCMonitorBankList.deleteMonitorPointsOnCapBankList(getCapBank().getDeviceID());	
	for (Iterator iter = ccMonitorBankList.iterator(); iter.hasNext();) {
		CCMonitorBankList item = (CCMonitorBankList) iter.next();
		item.add();
	}
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException
{
	//remove all the associations this CapBank to any Feeder
	com.cannontech.database.db.capcontrol.CCFeederBankList.deleteCapBanksFromFeederList( 
					null, getCapBank().getDeviceID(), getDbConnection() );

	//Delete from all dynamic CabBank tables here
	delete("DynamicCCCapBank", "CapBankID", getCapBank().getDeviceID() );

	getCapBank().delete();
	
	super.delete();
}
/**
 * This method was created in VisualAge.
 */
public com.cannontech.database.db.capcontrol.CapBank getCapBank() {
	if( capBank == null )
	{
		capBank = new com.cannontech.database.db.capcontrol.CapBank();
	}
	
	return capBank;
}
/**
 * This method was created in VisualAge.
 */
public String getLocation()
{
	return getPAODescription();
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException{
	super.retrieve();	
	getCapBank().retrieve();
	//init the cap bank points  list
	ccMonitorBankList = CCMonitorBankList.getMonitorPointsOnCapBankList(getCapBank().getDeviceID());
}
/**
 * This method was created in VisualAge.
 */
public void setCapBank(com.cannontech.database.db.capcontrol.CapBank newValue) {
	this.capBank = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) {
	super.setDbConnection(conn);
	getCapBank().setDbConnection(conn);
	
	for (Iterator iter = ccMonitorBankList.iterator(); iter.hasNext();) {
		CCMonitorBankList item = (CCMonitorBankList) iter.next();
		item.setDbConnection(conn);
	}
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getCapBank().setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 */
public void setLocation( String loc )
{
	getYukonPAObject().setDescription( loc );
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException{
	super.update();
	getCapBank().update();
	CCMonitorBankList.deleteMonitorPointsOnCapBankList(getCapBank().getDeviceID());	
	for (Iterator iter = ccMonitorBankList.iterator(); iter.hasNext();) {
		CCMonitorBankList item = (CCMonitorBankList) iter.next();
		item.add();
	}
}

public List getCcMonitorBankList() {
	return ccMonitorBankList;
}
public void setCcMonitorBankList(List ccMonitorBankList) {
	this.ccMonitorBankList = ccMonitorBankList;
}

}
