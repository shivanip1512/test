package com.cannontech.loadcontrol.eexchange.datamodels;

/**
 * Insert the type's description here.
 * Creation date: (8/2/2001 2:00:59 PM)
 * @author: 
 */
public class HourRowData 
{
	//valid valuse are 1-24
	private int hourEnding = 1;


	private double offerPrice = 0.0;
	private double target = 0.0;
	private boolean valid = true;
	
/**
 * HourRowData constructor comment.
 */
public HourRowData() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 2:05:04 PM)
 * @return int
 */
public int getHourEnding() {
	return hourEnding;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 2:05:04 PM)
 * @return double
 */
public double getOfferPrice() {
	return offerPrice;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 2:05:04 PM)
 * @return double
 */
public double getTarget() {
	return target;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 2:27:45 PM)
 * @return boolean
 */
public boolean isValid() {
	return valid;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 2:05:04 PM)
 * @param newHourEnding int
 */
public void setHourEnding(int newHourEnding) {
	hourEnding = newHourEnding;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 2:05:04 PM)
 * @param newOfferPrice long
 */
public void setOfferPrice(double newOfferPrice) {
	offerPrice = newOfferPrice;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 2:05:04 PM)
 * @param newTarget double
 */
public void setTarget(double newTarget) {
	target = newTarget;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 2:27:45 PM)
 * @param newValid boolean
 */
public void setValid(boolean newValid) {
	valid = newValid;
}
}
