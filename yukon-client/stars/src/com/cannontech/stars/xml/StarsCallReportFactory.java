package com.cannontech.stars.xml;

import com.cannontech.stars.xml.serialize.StarsCallRprt;
import com.cannontech.stars.xml.serialize.StarsCallReport;
import com.cannontech.stars.xml.serialize.StarsCallReportHistory;
import com.cannontech.stars.xml.serialize.CallType;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.database.data.lite.stars.LiteCustomerSelectionList;
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
	
	public static StarsCallReport[] getStarsCallReports(Integer energyCompanyID, Integer accountID) {
        com.cannontech.database.db.stars.report.CallReportBase[] calls =
        		com.cannontech.database.db.stars.report.CallReportBase.getAllAccountCallReports( accountID );
        if (calls == null) return null;
        
        LiteStarsEnergyCompany energyCompany = com.cannontech.stars.web.servlet.SOAPServer.getEnergyCompany( energyCompanyID.intValue() );
        java.util.Hashtable selectionListTable = energyCompany.getAllSelectionLists();
        LiteCustomerSelectionList callTypeList = (LiteCustomerSelectionList) selectionListTable.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_CALLTYPE );
        
        StarsCallReport[] callRprts = new StarsCallReport[ calls.length ];
        for (int i = 0; i < calls.length; i++) {
        	callRprts[i] = new StarsCallReport();
        	
        	callRprts[i].setCallID( calls[i].getCallID().intValue() );
			callRprts[i].setCallNumber( StarsLiteFactory.forceNotNull(calls[i].getCallNumber()) );
        	callRprts[i].setCallDate( calls[i].getDateTaken() );
        	
        	StarsSelectionListEntry[] entries = callTypeList.getListEntries();
        	for (int j = 0; j < entries.length; j++) {
        		if (entries[j].getEntryID() == calls[i].getCallTypeID().intValue()) {
        			CallType callType = (CallType) StarsCustListEntryFactory.newStarsCustListEntry( entries[j], CallType.class );
	            	callRprts[i].setCallType( callType );
        		}
        	}
        	
        	callRprts[i].setTakenBy( StarsLiteFactory.forceNotNull(calls[i].getTakenBy()) );
        	callRprts[i].setDescription( StarsLiteFactory.forceNotNull(calls[i].getDescription()) );
        }
        
        return callRprts;
	}
}
