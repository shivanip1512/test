package com.cannontech.messaging.message.loadcontrol.data;

import java.util.Vector;


public class ProgramEnergyExchange extends Program {

    private Integer minNotifyTime = null;
    private String heading = null;
    private String messageHeader = null;
    private String messageFooter = null;
    private String canceledMsg = null;
    private String stoppedEarlyMsg = null;

    // stores com.cannontech.loadcontrol.data.LMEnergyExchangeOffer objects
    private Vector<EnergyExchangeOffer> energyExchangeOffers = null;

    // stores com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer objects
    private Vector<EnergyExchangeCustomer> energyExchangeCustomers = null;

    public com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage createScheduledStartMsg(
        java.util.Date start, java.util.Date stop, int gearNumber, java.util.Date notifyTime, String additionalInfo,
        int constraintFalg) {
        return null;
    }

    public com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage createScheduledStopMsg(
        java.util.Date start, java.util.Date stop, int gearNumber, String additionalInfo) {
        return null;
    }

    public com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage createStartStopNowMsg(
        java.util.Date stopTime, int gearNumber, String additionalInfo, boolean isStart, int constraintFlag) {
        return null;
    }

    public java.lang.String getCanceledMsg() {
        return canceledMsg;
    }

    public Vector<EnergyExchangeCustomer> getEnergyExchangeCustomers() {
        return energyExchangeCustomers;
    }

    public Vector<EnergyExchangeOffer> getEnergyExchangeOffers() {
        return energyExchangeOffers;
    }

    public java.lang.String getHeading() {
        return heading;
    }

    public java.lang.String getMessageFooter() {
        return messageFooter;
    }

    public java.lang.String getMessageHeader() {
        return messageHeader;
    }

    public java.lang.Integer getMinNotifyTime() {
        return minNotifyTime;
    }

    public java.util.GregorianCalendar getStartTime() {
        // not implemented yet
        return null;
    }

    public java.lang.String getStoppedEarlyMsg() {
        return stoppedEarlyMsg;
    }

    public java.util.GregorianCalendar getStopTime() {
        // not implemented yet
        return null;
    }

    public void setCanceledMsg(java.lang.String newCanceledMsg) {
        canceledMsg = newCanceledMsg;
    }

    public void setEnergyExchangeCustomers(Vector<EnergyExchangeCustomer> newEnergyExchangeCustomers) {
        energyExchangeCustomers = newEnergyExchangeCustomers;
    }

    public void setEnergyExchangeOffers(Vector<EnergyExchangeOffer> newEnergyExchangeOffers) {
        energyExchangeOffers = newEnergyExchangeOffers;
    }

    public void setHeading(java.lang.String newHeading) {
        heading = newHeading;
    }

    public void setMessageFooter(java.lang.String newMessageFooter) {
        messageFooter = newMessageFooter;
    }

    public void setMessageHeader(java.lang.String newMessageHeader) {
        messageHeader = newMessageHeader;
    }

    public void setMinNotifyTime(java.lang.Integer newMinNotifyTime) {
        minNotifyTime = newMinNotifyTime;
    }

    public void setStoppedEarlyMsg(java.lang.String newStoppedEarlyMsg) {
        stoppedEarlyMsg = newStoppedEarlyMsg;
    }
}
