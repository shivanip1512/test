package com.cannontech.messaging.message.loadcontrol;

import java.util.Date;

public class EnergyExchangeControlMessage extends LmMessage {

    // command enumeration
    public static final int NEW_OFFER = 0;
    public static final int OFFER_UPDATE = 1;
    public static final int OFFER_REVISION = 2;
    public static final int CLOSE_OFFER = 3;
    public static final int CANCEL_OFFER = 4;

    private Integer command = null;
    private Integer yukonId = null;
    private Integer offerId = null;
    private Date offerDate = null;
    private Date notificationDateTime = null;
    private Date expirationDateTime = null;
    private String additionalInfo = null;
    private Double[] amountRequested = null; // 24 hourly values
    private Integer[] pricesOffered = null; // 24 hourly values

    public java.lang.String getAdditionalInfo() {
        return additionalInfo;
    }

    public java.lang.Double[] getAmountRequested() {
        return amountRequested;
    }

    public java.lang.Integer getCommand() {
        return command;
    }

    public java.util.Date getExpirationDateTime() {
        return expirationDateTime;
    }

    public java.util.Date getNotificationDateTime() {
        return notificationDateTime;
    }

    public java.util.Date getOfferDate() {
        return offerDate;
    }

    public java.lang.Integer getOfferId() {
        return offerId;
    }

    public java.lang.Integer[] getPricesOffered() {
        return pricesOffered;
    }

    public java.lang.Integer getYukonId() {
        return yukonId;
    }

    public void setAdditionalInfo(java.lang.String newAdditionalInfo) {
        additionalInfo = newAdditionalInfo;
    }

    public void setAmountRequested(java.lang.Double[] newAmountRequested) {
        amountRequested = newAmountRequested;
    }

    public void setCommand(java.lang.Integer newCommand) {
        command = newCommand;
    }

    public void setExpirationDateTime(java.util.Date newExpirationDateTime) {
        expirationDateTime = newExpirationDateTime;
    }

    public void setNotificationDateTime(java.util.Date newNotificationDateTime) {
        notificationDateTime = newNotificationDateTime;
    }

    public void setOfferDate(java.util.Date newOfferDate) {
        offerDate = newOfferDate;
    }

    public void setOfferId(java.lang.Integer newOfferId) {
        offerId = newOfferId;
    }

    public void setPricesOffered(java.lang.Integer[] newPricesOffered) {
        pricesOffered = newPricesOffered;
    }

    public void setYukonId(java.lang.Integer newYukonId) {
        yukonId = newYukonId;
    }
}
