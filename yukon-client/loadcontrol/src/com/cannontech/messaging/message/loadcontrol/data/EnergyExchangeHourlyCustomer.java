package com.cannontech.messaging.message.loadcontrol.data;

public class EnergyExchangeHourlyCustomer {
    private int customerID;
    private int offerID;
    private int revisionNumber;
    private int hour; // 0-23
    private double amountCommitted;

    public EnergyExchangeHourlyCustomer() {
        super();
    }

    public double getAmountCommitted() {
        return amountCommitted;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getHour() {
        return hour;
    }

    public int getOfferID() {
        return offerID;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setAmountCommitted(double newAmountCommitted) {
        amountCommitted = newAmountCommitted;
    }

    public void setCustomerID(int newCustomerID) {
        customerID = newCustomerID;
    }

    public void setHour(int newHour) {
        hour = newHour;
    }

    public void setOfferID(int newOfferID) {
        offerID = newOfferID;
    }

    public void setRevisionNumber(int newRevisionNumber) {
        revisionNumber = newRevisionNumber;
    }

    public String toString() {
        return "( LMEnergyExchangeHourlyCustomer, cust ID: " + getCustomerID() + " offer ID-rev: " + getOfferID() +
               "-" + getRevisionNumber() + " hour: " + getHour() + " amt commited: " + getAmountCommitted() + " )";
    }
}
