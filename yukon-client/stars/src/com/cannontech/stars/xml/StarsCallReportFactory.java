package com.cannontech.stars.xml;

import com.cannontech.common.constants.YukonListFuncs;
import com.cannontech.stars.xml.serialize.StarsCallRprt;
import com.cannontech.stars.xml.serialize.StarsCallReport;
import com.cannontech.stars.xml.serialize.StarsCallReportHistory;
import com.cannontech.stars.xml.serialize.CallType;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.report.CallReportBase;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsCallReportFactory {
	
	public static StarsCallRprt newStarsCallReport(StarsCallRprt call, Class type) {
		try {
			StarsCallRprt starsCall = (StarsCallRprt) type.newInstance();
			
			starsCall.setCallID( call.getCallID() );
			starsCall.setCallNumber( call.getCallNumber() );
			starsCall.setCallDate( call.getCallDate() );
			starsCall.setCallType( call.getCallType() );
			starsCall.setDescription( call.getDescription() );
			starsCall.setTakenBy( call.getTakenBy() );
			
			return starsCall;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void setCallReportBase(CallReportBase callDB, StarsCallRprt call) {
		callDB.setCallNumber( call.getCallNumber() );
		callDB.setCallTypeID( new Integer(call.getCallType().getEntryID()) );
		callDB.setDateTaken( call.getCallDate() );
		callDB.setDescription( call.getDescription() );
		callDB.setTakenBy( call.getTakenBy() );
	}
	
	public static StarsCallReport[] getStarsCallReports(Integer accountID) {
        com.cannontech.database.db.stars.report.CallReportBase[] calls =
        		com.cannontech.database.db.stars.report.CallReportBase.getAllAccountCallReports( accountID );
        if (calls == null) return null;
        
        StarsCallReport[] callRprts = new StarsCallReport[ calls.length ];
        for (int i = 0; i < calls.length; i++) {
        	callRprts[i] = new StarsCallReport();
        	
        	callRprts[i].setCallID( calls[i].getCallID().intValue() );
			callRprts[i].setCallNumber( StarsLiteFactory.forceNotNull(calls[i].getCallNumber()) );
        	callRprts[i].setCallDate( calls[i].getDateTaken() );
        	callRprts[i].setTakenBy( StarsLiteFactory.forceNotNull(calls[i].getTakenBy()) );
        	callRprts[i].setDescription( StarsLiteFactory.forceNotNull(calls[i].getDescription()) );
        	
        	CallType callType = new CallType();
        	StarsLiteFactory.setStarsCustListEntry( callType, YukonListFuncs.getYukonListEntry(calls[i].getCallTypeID().intValue()) );
        	callRprts[i].setCallType( callType );
        }
        
        return callRprts;
	}
}
