package com.cannontech.web.history;

/**
 * Insert the type's description here.
 * Creation date: (7/11/2001 3:58:17 PM)
 * @author: 
 */
public class HEnergyExchangeProgramOffer {
	private java.sql.Statement stmt = null;
	private long deviceId = 0;
	private long offerId = 0;
	private String runStatus = null;
	private java.util.Date offerDate = null;
/**
 * HEnergyExchangeProgramOffer constructor comment.
 */
public HEnergyExchangeProgramOffer() {
	super();
}
	public long getDeviceId() {
		return deviceId;
	}
/**
 * Insert the method's description here.
 * Creation date: (7/11/2001 4:55:29 PM)
 * @return com.cannontech.web.history.HEnergyExchangeOfferRevision[]
 */
public HEnergyExchangeOfferRevision[] getEnergyExchangeOfferRevisions() {
	String queryStr = "SELECT * FROM LMENERGYEXCHANGEOFFERREVISION WHERE OFFERID = " + offerId;
	queryStr += " ORDER BY REVISIONNUMBER";

	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		java.util.ArrayList revisionList = new java.util.ArrayList();
		while (rset.next()) {
			HEnergyExchangeOfferRevision revision = new HEnergyExchangeOfferRevision();
			
			revision.setStatement(stmt);
			revision.setDeviceId(deviceId);
			revision.setOfferId( rset.getLong(1) );
			revision.setRevisionNumber( rset.getInt(2) );
			revision.setActionDateTime( rset.getTimestamp(3) );
			revision.setNotificationDateTime( rset.getTimestamp(4) );
			revision.setExpirationDateTime( rset.getTimestamp(5) );
			revision.setAdditionalInfo( rset.getString(6) );

			revisionList.add(revision);
		}

		HEnergyExchangeOfferRevision[] ret = new HEnergyExchangeOfferRevision[ revisionList.size() ];
		revisionList.toArray(ret);
		
		return ret;
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
		return new HEnergyExchangeOfferRevision[0];
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/11/2001 4:27:38 PM)
 * @return com.cannontech.web.history.HEnergyExchangeProgram
 */
public HEnergyExchangeProgram getEnergyExchangeProgram() {
	String queryStr = "SELECT YUKONPAOBJECT.PAONAME FROM YUKONPAOBJECT WHERE YUKONPAOBJECT.PAOBJECTID = " + deviceId;

	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);

		HEnergyExchangeProgram ret = new HEnergyExchangeProgram();
		if (rset.next()) {
			ret.setStatement(stmt);
			ret.setDeviceId(deviceId);
			ret.setProgramName( rset.getString(1) );
			return ret;
		}
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
	}
	
	return null;
}
	public java.util.Date getOfferDate() {
		return offerDate;
	}
	public long getOfferId() {
		return offerId;
	}
	public String getRunStatus() {
		return runStatus;
	}
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	public void setOfferDate(java.util.Date offerDate) {
		this.offerDate = offerDate;
	}
	public void setOfferId(long offerId) {
		this.offerId = offerId;
	}
	public void setRunStatus(String runStatus) {
		this.runStatus = runStatus;
	}
	public void setStatement(java.sql.Statement stmt) {
		this.stmt = stmt;
	}
/**
 * Creation date: (8/9/2001 5:03:09 PM)
 * @return java.lang.String
 */
public String toString() {
	return String.valueOf(getOfferId());
}
}
