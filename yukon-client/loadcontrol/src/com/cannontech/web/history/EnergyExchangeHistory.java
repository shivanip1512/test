package com.cannontech.web.history;

/**
 * Insert the type's description here.
 * Creation date: (7/11/2001 3:34:59 PM)
 * @author: 
 */
public class EnergyExchangeHistory extends BaseHistory {
/**
 * EnergyExchangeHistory constructor comment.
 */
public EnergyExchangeHistory() {
	super();
}
/**
 * EnergyExchangeHistory constructor comment.
 */
public EnergyExchangeHistory(String dbAlias) {
	super(dbAlias);
}
/**
 * Insert the method's description here.
 * Creation date: (7/11/2001 4:01:33 PM)
 * @return com.cannontech.web.history.HEnergyExchangeProgramOffer[]
 */
public HEnergyExchangeProgramOffer[] getEnergyExchangeProgramOffers() {
	String queryStr = "SELECT * FROM LMENERGYEXCHANGEPROGRAMOFFER ORDER BY OFFERID DESC";
	
	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);
	
		java.util.ArrayList offerList = new java.util.ArrayList();
		while (rset.next()) {
			HEnergyExchangeProgramOffer offer = new HEnergyExchangeProgramOffer();
			offer.setStatement(stmt);
			offer.setDeviceId( rset.getLong(1) );
			offer.setOfferId( rset.getLong(2) );
			offer.setRunStatus( rset.getString(3) );
			offer.setOfferDate( rset.getTimestamp(4) );
			
			offerList.add(offer);
		}

		HEnergyExchangeProgramOffer[] ret = new HEnergyExchangeProgramOffer[offerList.size()];
		offerList.toArray(ret);
	
		return ret;
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
		return new HEnergyExchangeProgramOffer[0];
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2001 2:51:11 PM)
 * @return com.cannontech.web.history.HEnergyExchangeProgram[]
 */
public HEnergyExchangeProgram[] getEnergyExchangePrograms() {
	String queryStr = "SELECT LMPROGRAMENERGYEXCHANGE.DEVICEID, YUKONPAOBJECT.PAONAME FROM LMPROGRAMENERGYEXCHANGE, YUKONPAOBJECT ";
	queryStr += "WHERE LMPROGRAMENERGYEXCHANGE.DEVICEID = YUKONPAOBJECT.PAOBJECTID ";
	queryStr += "ORDER BY LMPROGRAMENERGYEXCHANGE.DEVICEID";
	
	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);
	
		java.util.ArrayList programList = new java.util.ArrayList();
		while (rset.next()) {
			HEnergyExchangeProgram program = new HEnergyExchangeProgram();
			program.setStatement(stmt);
			program.setDeviceId( rset.getLong(1) );
			program.setProgramName( rset.getString(2) );

			programList.add(program);
		}

		HEnergyExchangeProgram[] ret = new HEnergyExchangeProgram[programList.size()];
		programList.toArray(ret);
	
		return ret;
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
		return new HEnergyExchangeProgram[0];
	}
}
}
