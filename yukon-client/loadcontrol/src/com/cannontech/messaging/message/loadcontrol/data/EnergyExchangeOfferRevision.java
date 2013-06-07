package com.cannontech.messaging.message.loadcontrol.data;

import java.util.Date;
import java.util.Vector;

public class EnergyExchangeOfferRevision {
    private Integer offerId = null;
    private Integer revisionNumber = null;
    private Date actionDateTime = null;
    private Date notificationDateTime = null;
    private Date offerExpirationDateTime = null;
    private String additionalInfo = null;

    // contains EnergyExchangeHourlyOffer
    private Vector<EnergyExchangeHourlyOffer> energyExchangeHourlyOffers = null;

    public EnergyExchangeOfferRevision() {
        super();
    }

    public java.util.Date getActionDateTime() {
        return actionDateTime;
    }

    public java.lang.String getAdditionalInfo() {
        return additionalInfo;
    }

    public java.util.Vector<EnergyExchangeHourlyOffer> getEnergyExchangeHourlyOffers() {
        return energyExchangeHourlyOffers;
    }

    public java.util.Date getNotificationDateTime() {
        return notificationDateTime;
    }

    public java.util.Date getOfferExpirationDateTime() {
        return offerExpirationDateTime;
    }

    public java.lang.Integer getOfferId() {
        return offerId;
    }

    public java.lang.Integer getRevisionNumber() {
        return revisionNumber;
    }

    public void setActionDateTime(java.util.Date newActionDateTime) {
        actionDateTime = newActionDateTime;
    }

    public void setAdditionalInfo(java.lang.String newAdditionalInfo) {
        additionalInfo = newAdditionalInfo;
    }

    public void
        setEnergyExchangeHourlyOffers(java.util.Vector<EnergyExchangeHourlyOffer> newEnergyExchangeHourlyOffers) {
        energyExchangeHourlyOffers = newEnergyExchangeHourlyOffers;
    }

    public void setNotificationDateTime(java.util.Date newNotificationDateTime) {
        notificationDateTime = newNotificationDateTime;
    }

    public void setOfferExpirationDateTime(java.util.Date newOfferExpirationDateTime) {
        offerExpirationDateTime = newOfferExpirationDateTime;
    }

    public void setOfferId(java.lang.Integer newOfferId) {
        offerId = newOfferId;
    }

    public void setRevisionNumber(java.lang.Integer newRevisionNumber) {
        revisionNumber = newRevisionNumber;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("( LMEnergyExchangeOfferRevision, Offer Id-rev #: ");
        buf.append(getOfferId());
        buf.append("-");
        buf.append(getRevisionNumber());
        buf.append(" notification timestamp: ");
        buf.append(getNotificationDateTime());
        buf.append(" offer expiration: ");
        buf.append(getOfferExpirationDateTime());
        buf.append("\n Hourly price/amount \n");

        for (EnergyExchangeHourlyOffer item : getEnergyExchangeHourlyOffers()) {
            buf.append(item.toString());
        }

        return buf.toString();
    }
}
