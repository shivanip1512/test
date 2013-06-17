package com.cannontech.loadcontrol.data;

/**
 * Creation date: (5/29/2001 10:12:13 AM)
 * @author: Aaron Lauinger
 */
import java.util.Date;
import java.util.Vector;

public class LMEnergyExchangeOffer 
{
	//possible values for runStatus
	public static final String RUN_NULL = "Null";
	public static final String RUN_SCHEDULED = "Scheduled";
	public static final String RUN_OPEN = "Open";
	public static final String RUN_CLOSING = "Closing";
	public static final String RUN_CURTAILMENT_PENDING = "CurtailmentPending";
	public static final String RUN_CURTAILMENT_ACTIVE = "CurtailmentActive";
	public static final String RUN_COMPLETED = "Completed";
	public static final String RUN_CANCELED = "Canceled";
	
	private Integer yukonID = null;
	private Integer offerID = null;
	private String runStatus = null;
	private Date offerDate = null;

	// contains com.cannontech.loadcontrol.data.LMEnergyExchangeOfferRevision
	private Vector energyExchangeOfferRevisions = null;	
/**
 * LMEnergyExchangeOffer constructor comment.
 */
public LMEnergyExchangeOffer() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 2:34:50 PM)
 * @return boolean
 * @param o java.lang.Object
 */
public boolean equals(Object o) 
{
	return ( (o != null) &&
			   (o instanceof LMEnergyExchangeOffer) &&
			   ( ((LMEnergyExchangeOffer)o).getOfferID().intValue() == getOfferID().intValue()) &&
		      ( ((LMEnergyExchangeOffer)o).getYukonID().intValue() == getYukonID().intValue()) );
}
/**
 * Creation date: (5/29/2001 10:15:32 AM)
 * @return java.util.Vector
 */
public java.util.Vector getEnergyExchangeOfferRevisions() {
	return energyExchangeOfferRevisions;
}
/**
 * Creation date: (5/29/2001 10:15:32 AM)
 * @return java.util.Date
 */
public java.util.Date getOfferDate() {
	return offerDate;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:22:32 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getOfferID() {
	return offerID;
}
/**
 * Creation date: (5/29/2001 10:15:32 AM)
 * @return java.lang.String
 */
public java.lang.String getRunStatus() {
	return runStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:22:32 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getYukonID() {
	return yukonID;
}
/**
 * Creation date: (5/29/2001 10:15:32 AM)
 * @param newEnergyExchangeOfferRevisions java.util.Vector
 */
public void setEnergyExchangeOfferRevisions(java.util.Vector newEnergyExchangeOfferRevisions) {
	energyExchangeOfferRevisions = newEnergyExchangeOfferRevisions;
}
/**
 * Creation date: (5/29/2001 10:15:32 AM)
 * @param newOfferDate java.util.Date
 */
public void setOfferDate(java.util.Date newOfferDate) {
	offerDate = newOfferDate;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:22:32 PM)
 * @param newOfferID java.lang.Integer
 */
public void setOfferID(java.lang.Integer newOfferID) {
	offerID = newOfferID;
}
/**
 * Creation date: (5/29/2001 10:15:32 AM)
 * @param newRunStatus java.lang.String
 */
public void setRunStatus(java.lang.String newRunStatus) {
	runStatus = newRunStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:22:32 PM)
 * @param newYukonID java.lang.Integer
 */
public void setYukonID(java.lang.Integer newYukonID) {
	yukonID = newYukonID;
}
/**
 * Creation date: (6/3/2001 2:34:25 PM)
 * @return java.lang.String
 */
public String toString() {
	return "( LMEnergyExchangeOffer, Offer ID: " + getOfferID() + " " + getEnergyExchangeOfferRevisions().size() + " revisions," +
	  	   " program id: " + getYukonID() +     
			" date: " + getOfferDate() +
	       " run status: " + getRunStatus() +
	       " )";
}
}
