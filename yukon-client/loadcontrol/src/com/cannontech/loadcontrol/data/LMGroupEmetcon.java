package com.cannontech.loadcontrol.data;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:06:09 PM)
 * @author: 
 */
public class LMGroupEmetcon extends LMDirectGroupBase
{
	private Integer goldAddress = null;
	private Integer silverAddress = null;
	private String addressUsage = null;
	private String relayUsage = null;
	private Integer routeID = null;
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 11:27:45 AM)
 * @return java.lang.String
 */
public java.lang.String getAddressUsage() {
	return addressUsage;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 11:27:45 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getGoldAddress() {
	return goldAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 11:27:45 AM)
 * @return java.lang.String
 */
public java.lang.String getRelayUsage() {
	return relayUsage;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 1:11:01 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getRouteID() {
	return routeID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 11:27:45 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSilverAddress() {
	return silverAddress;
}

/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 11:27:45 AM)
 * @param newAddressUsage java.lang.String
 */
public void setAddressUsage(java.lang.String newAddressUsage) {
	addressUsage = newAddressUsage;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 11:27:45 AM)
 * @param newGoldAddress java.lang.Integer
 */
public void setGoldAddress(java.lang.Integer newGoldAddress) {
	goldAddress = newGoldAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 11:27:45 AM)
 * @param newRelayUsage java.lang.String
 */
public void setRelayUsage(java.lang.String newRelayUsage) {
	relayUsage = newRelayUsage;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 1:11:01 PM)
 * @param newRouteID java.lang.Integer
 */
public void setRouteID(java.lang.Integer newRouteID) {
	routeID = newRouteID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 11:27:45 AM)
 * @param newSilverAddress java.lang.Integer
 */
public void setSilverAddress(java.lang.Integer newSilverAddress) {
	silverAddress = newSilverAddress;
}
}
