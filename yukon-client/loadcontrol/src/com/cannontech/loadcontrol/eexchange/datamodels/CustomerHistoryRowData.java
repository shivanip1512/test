package com.cannontech.loadcontrol.eexchange.datamodels;

/**
 * Insert the type's description here.
 * Creation date: (8/22/2001 3:40:20 PM)
 * @author: 
 */
public class CustomerHistoryRowData 
{
	private int customerID = 0;
	private int offerID = 0;
	private int revisionNumber = 0;
	
	private String customerName = null;
	private String accept = null;
  private Double total = null;
	private String acceptPerson = null;

	private double[] hrCommittedTotal = null;
/**
 * CustomerHistoryRowData constructor comment.
 */
public CustomerHistoryRowData() {
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
			   (o instanceof CustomerHistoryRowData) &&
  			   ((CustomerHistoryRowData)o).getOfferID() == getOfferID() &&
  			   ((CustomerHistoryRowData)o).getRevisionNumber() == getRevisionNumber() &&
  			   ((CustomerHistoryRowData)o).getCustomerID() == getCustomerID() );
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 3:43:36 PM)
 * @return java.lang.String
 */
public java.lang.String getAccept() {
	return accept;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 3:43:36 PM)
 * @return java.lang.String
 */
public java.lang.String getAcceptPerson() {
	return acceptPerson;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:42:39 PM)
 * @return int
 */
public int getCustomerID() {
	return customerID;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 3:43:36 PM)
 * @return java.lang.String
 */
public java.lang.String getCustomerName() {
	return customerName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 4:43:19 PM)
 * @return double[]
 */
public double[] getHrCommittedTotal() {
	return hrCommittedTotal;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:42:39 PM)
 * @return int
 */
public int getOfferID() {
	return offerID;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 3:46:08 PM)
 * @return int
 */
public int getRevisionNumber() {
	return revisionNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 3:43:36 PM)
 * @return java.lang.Double
 */
public java.lang.Double getTotal() {
	return total;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 3:43:36 PM)
 * @param newAccept java.lang.String
 */
public void setAccept(java.lang.String newAccept) {
	accept = newAccept;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 3:43:36 PM)
 * @param newAcceptPerson java.lang.String
 */
public void setAcceptPerson(java.lang.String newAcceptPerson) {
	acceptPerson = newAcceptPerson;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:42:39 PM)
 * @param newCustomerID int
 */
public void setCustomerID(int newCustomerID) {
	customerID = newCustomerID;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 3:43:36 PM)
 * @param newCustomerName java.lang.String
 */
public void setCustomerName(java.lang.String newCustomerName) {
	customerName = newCustomerName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 4:43:19 PM)
 * @param newHrCommittedTotal double[]
 */
public void setHrCommittedTotal(double[] newHrCommittedTotal) {
	hrCommittedTotal = newHrCommittedTotal;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:42:39 PM)
 * @param newOfferID int
 */
public void setOfferID(int newOfferID) {
	offerID = newOfferID;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 3:46:08 PM)
 * @param newRevisionNumber int
 */
public void setRevisionNumber(int newRevisionNumber) {
	revisionNumber = newRevisionNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 3:43:36 PM)
 * @param newTotal java.lang.Double
 */
public void setTotal(java.lang.Double newTotal) {
	total = newTotal;
}
}
