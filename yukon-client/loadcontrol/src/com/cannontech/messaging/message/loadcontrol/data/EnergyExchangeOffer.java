package com.cannontech.messaging.message.loadcontrol.data;

import java.util.Date;
import java.util.Vector;

public class EnergyExchangeOffer {
	// possible values for runStatus
	public static final String RUN_NULL = "Null";
	public static final String RUN_SCHEDULED = "Scheduled";
	public static final String RUN_OPEN = "Open";
	public static final String RUN_CLOSING = "Closing";
	public static final String RUN_CURTAILMENT_PENDING = "CurtailmentPending";
	public static final String RUN_CURTAILMENT_ACTIVE = "CurtailmentActive";
	public static final String RUN_COMPLETED = "Completed";
	public static final String RUN_CANCELED = "Canceled";

	private int yukonId;
	private int offerId;
	private String runStatus = null;
	private Date offerDate = null;

	private Vector<EnergyExchangeOfferRevision> energyExchangeOfferRevisions = null;

	public EnergyExchangeOffer() {
		super();
	}

	public boolean equals(Object o) {
		return ((o != null) && (o instanceof EnergyExchangeOffer)
				&& (((EnergyExchangeOffer) o).getOfferId() == getOfferId()) && (((EnergyExchangeOffer) o)
					.getYukonId() == getYukonId()));
	}

	public java.util.Vector<EnergyExchangeOfferRevision> getEnergyExchangeOfferRevisions() {
		return energyExchangeOfferRevisions;
	}

	public java.util.Date getOfferDate() {
		return offerDate;
	}

	public int getOfferId() {
		return offerId;
	}

	public java.lang.String getRunStatus() {
		return runStatus;
	}

	public int getYukonId() {
		return yukonId;
	}

	public void setEnergyExchangeOfferRevisions(
			Vector<EnergyExchangeOfferRevision> newEnergyExchangeOfferRevisions) {
		energyExchangeOfferRevisions = newEnergyExchangeOfferRevisions;
	}

	public void setOfferDate(java.util.Date newOfferDate) {
		offerDate = newOfferDate;
	}

	public void setOfferId(int newOfferID) {
		offerId = newOfferID;
	}

	public void setRunStatus(java.lang.String newRunStatus) {
		runStatus = newRunStatus;
	}

	public void setYukonId(int newYukonId) {
		yukonId = newYukonId;
	}

	public String toString() {
		return "( LMEnergyExchangeOffer, Offer ID: " + getOfferId() + " "
				+ getEnergyExchangeOfferRevisions().size() + " revisions,"
				+ " program id: " + getYukonId() + " date: " + getOfferDate()
				+ " run status: " + getRunStatus() + " )";
	}
}
