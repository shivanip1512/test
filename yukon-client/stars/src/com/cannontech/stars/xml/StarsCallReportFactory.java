package com.cannontech.stars.xml;

import com.cannontech.stars.xml.serialize.StarsCallReport;
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
	
	public static StarsCallReport newStarsCallReport(StarsCallReport call, Class type) {
		try {
			StarsCallReport starsCall = (StarsCallReport) type.newInstance();
			
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
	
	public static void setCallReportBase(CallReportBase callDB, StarsCallReport call) {
		callDB.setCallNumber( call.getCallNumber() );
		callDB.setCallTypeID( new Integer(call.getCallType().getEntryID()) );
		callDB.setDateTaken( call.getCallDate() );
		callDB.setDescription( call.getDescription() );
		callDB.setTakenBy( call.getTakenBy() );
	}
}
