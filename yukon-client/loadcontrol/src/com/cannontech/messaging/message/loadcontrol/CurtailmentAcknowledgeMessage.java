package com.cannontech.messaging.message.loadcontrol;


/**
 * ScheduleCommand objects are sent to the CBC server to request that an operation
 * be done on the given strategy.  Clients only send CBCCommands
 * and the server only receives them.
 */

public class CurtailmentAcknowledgeMessage extends LmMessage {

    private int yukonId;
    private int curtailReferenceId;
    private String acknowledgeStatus = null;
    private String ipAddressOfAckUser = null;
    private String userIdName = null;
    private String nameOfAckPerson = null;
    private String curtailmentNotes = null;

    // The following are the different statuses that an acknowledgement can be in
    public static final String UNACKNOWLEDGED = "Unacknowledged";
    public static final String ACKNOWLEDGED = "Acknowledged";
    public static final String NOT_REQUIRED = "NotRequired";
    public static final String VERBAL = "Verbal";

    public java.lang.String getAcknowledgeStatus() {
        return acknowledgeStatus;
    }

    public java.lang.String getCurtailmentNotes() {
        return curtailmentNotes;
    }

    public int getCurtailReferenceId() {
        return curtailReferenceId;
    }

    public java.lang.String getIpAddressOfAckUser() {
        return ipAddressOfAckUser;
    }

    public java.lang.String getNameOfAckPerson() {
        return nameOfAckPerson;
    }

    public java.lang.String getUserIdName() {
        return userIdName;
    }

    public int getYukonId() {
        return yukonId;
    }

    public void setAcknowledgeStatus(java.lang.String newAcknowledgeStatus) {
        acknowledgeStatus = newAcknowledgeStatus;
    }

    public void setCurtailmentNotes(java.lang.String newCurtailmentNotes) {
        curtailmentNotes = newCurtailmentNotes;
    }

    public void setCurtailReferenceId(int newCurtailReferenceId) {
        curtailReferenceId = newCurtailReferenceId;
    }

    public void setIpAddressOfAckUser(java.lang.String newIpAddressOfAckUser) {
        ipAddressOfAckUser = newIpAddressOfAckUser;
    }

    public void setNameOfAckPerson(java.lang.String newNameOfAckPerson) {
        nameOfAckPerson = newNameOfAckPerson;
    }

    public void setUserIdName(java.lang.String newUserIdName) {
        userIdName = newUserIdName;
    }

    public void setYukonId(int newYukonId) {
        yukonId = newYukonId;
    }
}
