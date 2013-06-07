package com.cannontech.messaging.message.loadcontrol.data;

public class EnergyExchangeHourlyOffer {
    private int offerId;
    private int revisionNumber;
    private int hour;
    private double price;
    private double amountRequested;

    public EnergyExchangeHourlyOffer() {
        super();
    }

    public double getAmountRequested() {
        return amountRequested;
    }

    public int getHour() {
        return hour;
    }

    public int getOfferId() {
        return offerId;
    }

    public double getPrice() {
        return price;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setAmountRequested(double newAmountRequested) {
        amountRequested = newAmountRequested;
    }

    public void setHour(int newHour) {
        hour = newHour;
    }

    public void setOfferId(int newOfferId) {
        offerId = newOfferId;
    }

    public void setPrice(double newPrice) {
        price = newPrice;
    }

    public void setRevisionNumber(int newRevisionNumber) {
        revisionNumber = newRevisionNumber;
    }

    public String toString() {
        return "( LMEnergyExchangeHourlyOffer, offer ID-rev: " + getOfferId() + "-" + getRevisionNumber() + " hour: " +
               getHour() + " price: " + getPrice() + " amt requested: " + getAmountRequested() + " )";
    }
}
