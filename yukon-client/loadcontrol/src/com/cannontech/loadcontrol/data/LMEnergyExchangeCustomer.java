package com.cannontech.loadcontrol.data;

/**
 * Creation date: (5/28/2001 2:02:11 PM)
 * @author: Aaron Lauinger
 */
import java.util.Vector;

public class LMEnergyExchangeCustomer extends LMCICustomerBase {

	
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
 * Creation date: (5/28/2001 2:07:05 PM)
 * @return java.util.Vector
 */
public java.util.Vector getEnergyExchangeCustomerReplies() {
	return energyExchangeCustomerReplies;
}
/**
 * Creation date: (5/28/2001 2:07:05 PM)
 * @param newEnergyExchangeCustomerReplies java.util.Vector
 */
public void setEnergyExchangeCustomerReplies(java.util.Vector newEnergyExchangeCustomerReplies) {
	energyExchangeCustomerReplies = newEnergyExchangeCustomerReplies;
}
}
