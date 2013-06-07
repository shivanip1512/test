package com.cannontech.messaging.message.loadcontrol.data;

import java.util.Date;
import java.util.Vector;

public class EnergyExchangeCustomerReply {
    private int customerId;
    private int offerId;
    private String acceptStatus = null;
    private Date acceptDateTime = null;
    private int revisionNumber;
    private String ipAddressOfAcceptUser = null;
    private String userIdName = null;
    private String nameOfAcceptPerson = null;
    private String energyExchangeNotes = null;

    private Vector<EnergyExchangeHourlyCustomer> energyExchangeHourlyCustomer = null;

    // possible states for the acceptStatus string
    public static final String STRING_NO_RESPONSE = "NoResponse";
    public static final String STRING_ACCEPTED = "Accepted";
    public static final String STRING_DECLINED = "Declined";

    public EnergyExchangeCustomerReply() {
        super();
    }

    public java.util.Date getAcceptDateTime() {
        return acceptDateTime;
    }

    public String getAcceptStatus() {
        return acceptStatus;
    }

    public int getCustomerId() {
        return customerId;
    }

    public Vector<EnergyExchangeHourlyCustomer> getEnergyExchangeHourlyCustomer() {
        return energyExchangeHourlyCustomer;
    }

    public String getEnergyExchangeNotes() {
        return energyExchangeNotes;
    }

    public String getIpAddressOfAcceptUser() {
        return ipAddressOfAcceptUser;
    }

    public String getNameOfAcceptPerson() {
        return nameOfAcceptPerson;
    }

    public int getOfferId() {
        return offerId;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public String getUserIdName() {
        return userIdName;
    }

    public void setAcceptDateTime(java.util.Date newAcceptDateTime) {
        acceptDateTime = newAcceptDateTime;
    }

    public void setAcceptStatus(String newAcceptStatus) {
        acceptStatus = newAcceptStatus;
    }

    public void setCustomerId(int newCustomerId) {
        customerId = newCustomerId;
    }

    public void setEnergyExchangeHourlyCustomer(Vector<EnergyExchangeHourlyCustomer> newEnergyExchangeHourlyCustomer) {
        energyExchangeHourlyCustomer = newEnergyExchangeHourlyCustomer;
    }

    public void setEnergyExchangeNotes(String newEnergyExchangeNotes) {
        energyExchangeNotes = newEnergyExchangeNotes;
    }

    public void setIpAddressOfAcceptUser(String newIpAddressOfAcceptUser) {
        ipAddressOfAcceptUser = newIpAddressOfAcceptUser;
    }

    public void setNameOfAcceptPerson(String newNameOfAcceptPerson) {
        nameOfAcceptPerson = newNameOfAcceptPerson;
    }

    public void setOfferId(int newOfferId) {
        offerId = newOfferId;
    }

    public void setRevisionNumber(int newRevisionNumber) {
        revisionNumber = newRevisionNumber;
    }

    public void setUserIdName(String newUserIdName) {
        userIdName = newUserIdName;
    }

    public String toString() {
        return "( LMEnergyExchangeCustomerReply, Offer Id: " + getOfferId() + " customer Id: " + getCustomerId() +
               " accept status: " + getAcceptStatus() + " accept timestamp: " + getAcceptDateTime() + " )";
    }
}
