/*
 * Created on Nov 13, 2003
 */
package com.cannontech.cttp;

import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.version.VersionTools;
import com.cannontech.cttp.schema.cttp_OperationType;
import com.cannontech.cttp.schema.cttp_ServerStatusResponseType;
import com.cannontech.cttp.schema.cttp_ServerStatusType;

/**
 * Simple return a ServerStatusResponse, there isn't much interesting in the incoming message.
 * @author Aaron Lauinger
 */
public class ServerStatusRequestHandler implements CttpMessageHandler {
		
	/* (non-Javadoc) 
	 * @see com.cannontech.cttp.CttpMessageHandler#handleMessage(com.cannontech.cttp.schema.cttp_OperationType)
	 */
	public CttpResponse handleMessage(CttpRequest req) throws Exception {
		
		CTILogger.debug("Handling a server status request");
			
		
		cttp_OperationType cttpResp = new cttp_OperationType();
		
		cttp_ServerStatusResponseType cttpStatusResp = new cttp_ServerStatusResponseType();
		
		cttp_ServerStatusType cttpServerStatus = new cttp_ServerStatusType(); 	
		cttpServerStatus.addtimestamp(Cttp.formatCTTPDate(new Date()));
		cttpServerStatus.addversion(VersionTools.getYUKON_VERSION());
		cttpServerStatus.addowner("Cannon Technologies");
		cttpServerStatus.addsupportContact("Technical Support");
		cttpServerStatus.addsupportPhone("763-595-7775");
		cttpServerStatus.addsupportEmail("support@cannontech.com");
		
		cttpStatusResp.addcttp_ServerStatus(cttpServerStatus); 
		cttpResp.addcttp_ServerStatusResponse(cttpStatusResp); 
		
		return new CttpResponse(cttpResp);
	}

}
