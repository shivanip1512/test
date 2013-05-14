package com.cannontech.web.history;

/**
 * Insert the type's description here.
 * Creation date: (7/13/2001 3:30:44 PM)
 * @author: 
 */
public class HCurtailCustomerActivity {
	private java.sql.Statement stmt = null;
	private long customerId = 0;
	private long curtailReferenceId = 0;
	private String ackStatus = null;
	private java.util.Date ackDateTime = null;
	private String nameOfAckPerson = null;
	private String ackLateFlag = null;
/**
 * HCurtailCustomerActivity constructor comment.
 */
public HCurtailCustomerActivity() {
	super();
}
	public java.util.Date getAckDateTime() {
		return ackDateTime;
	}
	public String getAckLateFlag() {
		return ackLateFlag;
	}
	public String getAckStatus() {
		return ackStatus;
	}
	public long getCurtailReferenceId() {
		return curtailReferenceId;
	}
	public long getCustomerId() {
		return customerId;
	}
	public String getNameOfAckPerson() {
		return nameOfAckPerson;
	}
	public void setAckDateTime(java.util.Date ackDateTime) {
		this.ackDateTime = ackDateTime;
	}
	public void setAckLateFlag(String ackLateFlag) {
		this.ackLateFlag = ackLateFlag;
	}
	public void setAckStatus(String ackStatus) {
		this.ackStatus = ackStatus;
	}
	public void setCurtailReferenceId(long curtailReferenceId) {
		this.curtailReferenceId = curtailReferenceId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public void setNameOfAckPerson(String nameOfAckPerson) {
		this.nameOfAckPerson = nameOfAckPerson;
	}
	public void setStatement(java.sql.Statement stmt) {
		this.stmt = stmt;
	}
}
