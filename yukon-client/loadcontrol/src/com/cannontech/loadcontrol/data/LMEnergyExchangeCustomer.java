package com.cannontech.loadcontrol.data;

/**
 * Creation date: (5/28/2001 2:02:11 PM)
 * @author: Aaron Lauinger
 */
import java.util.Vector;

public class LMEnergyExchangeCustomer {

	private Integer yukonID = null;
	private String yukonCategory = null;
	private String yukonClass = null;
	private String yukonName = null;
	private Integer yukonType = null;
	private String yukonDescription = null;
	private Boolean disableFlag = null;
	private Integer customerOrder = null;
	private String customerTimeZone = null;

	// expect this vector to contain instances of 
	// com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply
	private Vector energyExchangeCustomerReplies = null;
/**
 * LMEnergyExchangeCustomer constructor comment.
 */
public LMEnergyExchangeCustomer() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:20:10 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCustomerOrder() {
	return customerOrder;
}
/**
 * Creation date: (5/28/2001 2:07:05 PM)
 * @return java.lang.String
 */
public java.lang.String getCustomerTimeZone() {
	return customerTimeZone;
}
/**
 * Creation date: (5/28/2001 2:07:05 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getDisableFlag() {
	return disableFlag;
}
/**
 * Creation date: (5/28/2001 2:07:05 PM)
 * @return java.util.Vector
 */
public java.util.Vector getEnergyExchangeCustomerReplies() {
	return energyExchangeCustomerReplies;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:42:03 AM)
 * @return java.lang.String
 */
public java.lang.String getYukonCategory() {
	return yukonCategory;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:42:03 AM)
 * @return java.lang.String
 */
public java.lang.String getYukonClass() {
	return yukonClass;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:42:03 AM)
 * @return java.lang.String
 */
public java.lang.String getYukonDescription() {
	return yukonDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:42:03 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getYukonID() {
	return yukonID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:42:03 AM)
 * @return java.lang.String
 */
public java.lang.String getYukonName() {
	return yukonName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:42:03 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getYukonType() {
	return yukonType;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:20:10 PM)
 * @param newCustomerOrder java.lang.Integer
 */
public void setCustomerOrder(java.lang.Integer newCustomerOrder) {
	customerOrder = newCustomerOrder;
}
/**
 * Creation date: (5/28/2001 2:07:05 PM)
 * @param newCustomerTimeZone java.lang.String
 */
public void setCustomerTimeZone(java.lang.String newCustomerTimeZone) {
	customerTimeZone = newCustomerTimeZone;
}
/**
 * Creation date: (5/28/2001 2:07:05 PM)
 * @param newDisableFlag java.lang.Boolean
 */
public void setDisableFlag(java.lang.Boolean newDisableFlag) {
	disableFlag = newDisableFlag;
}
/**
 * Creation date: (5/28/2001 2:07:05 PM)
 * @param newEnergyExchangeCustomerReplies java.util.Vector
 */
public void setEnergyExchangeCustomerReplies(java.util.Vector newEnergyExchangeCustomerReplies) {
	energyExchangeCustomerReplies = newEnergyExchangeCustomerReplies;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:42:03 AM)
 * @param newYukonCategory java.lang.String
 */
public void setYukonCategory(java.lang.String newYukonCategory) {
	yukonCategory = newYukonCategory;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:42:03 AM)
 * @param newYukonClass java.lang.String
 */
public void setYukonClass(java.lang.String newYukonClass) {
	yukonClass = newYukonClass;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:42:03 AM)
 * @param newYukonDescription java.lang.String
 */
public void setYukonDescription(java.lang.String newYukonDescription) {
	yukonDescription = newYukonDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:42:03 AM)
 * @param newYukonID java.lang.Integer
 */
public void setYukonID(java.lang.Integer newYukonID) {
	yukonID = newYukonID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:42:03 AM)
 * @param newYukonName java.lang.String
 */
public void setYukonName(java.lang.String newYukonName) {
	yukonName = newYukonName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:42:03 AM)
 * @param newYukonType java.lang.Integer
 */
public void setYukonType(java.lang.Integer newYukonType) {
	yukonType = newYukonType;
}
/**
 * Creation date: (6/3/2001 2:26:33 PM)
 * @return java.lang.String
 */
public String toString() {
	return getYukonName();
}
}
