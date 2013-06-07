package com.cannontech.messaging.message.loadcontrol.data;

import java.util.Date;


public class CurtailCustomer extends CiCustomerBase implements Group {

    // possible values for ackStatus
    public static final String ACK_UNACKNOWLEDGED = "UnAcknowledged";
    public static final String ACK_ACKNOWLEDGED = "Acknowledged";
    public static final String ACK_NOT_REQUIRED = "Not Required";
    public static final String ACK_VERBAL = "Verbal";

    private boolean requireAck;
    private long curtailRefId;
    private String ackStatus = null;
    private Date ackDateTime = null;
    private String ipAddress = null;
    private String userIDname = null;
    private String nameOfAckPerson = null;
    private String curtailmentNotes = null;
    private boolean ackLateFlag;

    public Integer getYukonId() {
        return new Integer((int)getCustomerId());
    }

    public java.util.Date getAckDateTime() {
        return ackDateTime;
    }

    public boolean getAckLateFlag() {
        return ackLateFlag;
    }

    public java.lang.String getAckStatus() {
        return ackStatus;
    }

    public java.lang.String getCurtailmentNotes() {
        return curtailmentNotes;
    }

    public long getCurtailRefId() {
        return curtailRefId;
    }

    public String getGroupControlStateString() {
        return getAckStatus();
    }

    public java.util.Date getGroupTime() {
        return getAckDateTime();
    }

    public java.lang.String getIpAddress() {
        return ipAddress;
    }

    public java.lang.String getNameOfAckPerson() {
        return nameOfAckPerson;
    }

    public boolean getRequireAck() {
        return requireAck;
    }

    public String getStatistics() {
        // not sure for this one yet, just use dashes
        return "  ---";
        // return getUserIDname() + "@" + getIpAddress();
    }

    public java.lang.String getUserIDname() {
        return userIDname;
    }

    public void setAckDateTime(java.util.Date newAckDateTime) {
        ackDateTime = newAckDateTime;
    }

    public void setAckLateFlag(boolean newAckLateFlag) {
        ackLateFlag = newAckLateFlag;
    }

    public void setAckStatus(java.lang.String newAckStatus) {
        ackStatus = newAckStatus;
    }

    public void setCurtailmentNotes(java.lang.String newCurtailmentNotes) {
        curtailmentNotes = newCurtailmentNotes;
    }

    public void setCurtailRefId(long newCurtailRefId) {
        curtailRefId = newCurtailRefId;
    }

    public void setIpAddress(java.lang.String newIpAddress) {
        ipAddress = newIpAddress;
    }

    public void setNameOfAckPerson(java.lang.String newNameOfAckPerson) {
        nameOfAckPerson = newNameOfAckPerson;
    }

    public void setRequireAck(boolean newRequireAck) {
        requireAck = newRequireAck;
    }

    public void setUserIDname(java.lang.String newUserIDname) {
        userIDname = newUserIDname;
    }

    public String getName() {
        return getCompanyName();
    }

    public java.lang.Boolean getDisableFlag() {
        return java.lang.Boolean.FALSE; // can never be disabled
    }

    public Double getReduction() {
        return getCurtailAmount();
    }

    public Integer getOrder() {
        return new Integer((int)getCustomerOrder());
    }

}
