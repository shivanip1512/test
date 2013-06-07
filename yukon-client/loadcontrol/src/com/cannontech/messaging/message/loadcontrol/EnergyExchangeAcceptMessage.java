package com.cannontech.messaging.message.loadcontrol;

public class EnergyExchangeAcceptMessage extends LmMessage {

    public static final String NO_RESPONSE_ACCEPT_STATUS = "NoResponse";
    public static final String ACCEPTED_ACCEPT_STATUS = "Accepted";
    public static final String DECLINED_ACCEPT_STATUS = "Declined";

    private Integer yukonId = null;
    private Integer offerId = null;
    private Integer revisionNumber = null;
    private String acceptStatus = "(none)";
    private String ipAddressOfCustomer = "(none)";
    private String userIdName = "(none)";
    private String nameOfAcceptPerson = "(none)";
    private String energyExchangeNotes = "(none)";
    private Double[] amountCommitted = null; // 24 hourly amounts

    public java.lang.String getAcceptStatus() {
        return acceptStatus;
    }

    public java.lang.Double[] getAmountCommitted() {
        if (amountCommitted == null) {
            amountCommitted = new Double[24];
            Double val = new Double(0.0);

            for (int i = 0; i < 24; i++)
                amountCommitted[i] = val;
        }

        return amountCommitted;
    }

    public java.lang.String getEnergyExchangeNotes() {
        return energyExchangeNotes;
    }

    public java.lang.String getIpAddressOfCustomer() {
        return ipAddressOfCustomer;
    }

    public java.lang.String getNameOfAcceptPerson() {
        return nameOfAcceptPerson;
    }

    public java.lang.Integer getOfferId() {
        return offerId;
    }

    public java.lang.Integer getRevisionNumber() {
        return revisionNumber;
    }

    public java.lang.String getUserIdName() {
        return userIdName;
    }

    public java.lang.Integer getYukonId() {
        return yukonId;
    }

    public void setAcceptStatus(java.lang.String newAcceptStatus) {
        acceptStatus = newAcceptStatus;
    }

    public void setAmountCommitted(java.lang.Double[] newAmountCommitted) {
        amountCommitted = newAmountCommitted;
    }

    public void setEnergyExchangeNotes(java.lang.String newEnergyExchangeNotes) {
        energyExchangeNotes = newEnergyExchangeNotes;
    }

    public void setIpAddressOfCustomer(java.lang.String newIpAddressOfCustomer) {
        ipAddressOfCustomer = newIpAddressOfCustomer;
    }

    public void setNameOfAcceptPerson(java.lang.String newNameOfAcceptPerson) {
        nameOfAcceptPerson = newNameOfAcceptPerson;
    }

    public void setOfferId(java.lang.Integer newOfferId) {
        offerId = newOfferId;
    }

    public void setRevisionNumber(java.lang.Integer newRevisionNumber) {
        revisionNumber = newRevisionNumber;
    }

    public void setUserIdName(java.lang.String newUserIdName) {
        userIdName = newUserIdName;
    }

    public void setYukonId(java.lang.Integer newYukonId) {
        yukonId = newYukonId;
    }
}
