/*
 * Created on Nov 13, 2003
 */
package com.cannontech.cttp;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.cttp.schema.cttp_OperationType;

/**
 * @author aaron
 */
public class CttpMessageHandlerProvider {
	
	private Map handlerMap = new HashMap();
	
	{
		handlerMap.put(Cttp.CTTP_SERVER_STATUS_REQUEST, new ServerStatusRequestHandler());
		handlerMap.put(Cttp.CTTP_GROUP_STATUS_REQUEST, new GroupStatusRequestHandler());
		handlerMap.put(Cttp.CTTP_SUBMIT_COMMAND_REQUEST, new SubmitCommandRequestHandler());
		handlerMap.put(Cttp.CTTP_COMMAND_STATUS_REQUEST, new CommandStatusRequestHandler());
	}
	/**
	 * Get a handler to handle one of the cttp requests
	 * msgType is a constant string defined in the Cttp class.
	 * @param msgType
	 * @return
	 */
	public CttpMessageHandler getMessageHandler(String msgType) {
		return (CttpMessageHandler) handlerMap.get(msgType);
	}
	

	public CttpMessageHandler getMessageHandler(CttpRequest req) {
	
		cttp_OperationType cttpOp = req.getCttpOperation();
		String reqType = null;
		if(cttpOp.hascttp_ServerStatusRequest()) {
			reqType = Cttp.CTTP_SERVER_STATUS_REQUEST;
		}
		else
		if(cttpOp.hascttp_SubmitCommandRequest()) {
			reqType = Cttp.CTTP_SUBMIT_COMMAND_REQUEST;
		}
		else
		if(cttpOp.hascttp_CommandStatusRequest()) {
			reqType = Cttp.CTTP_COMMAND_STATUS_REQUEST;
		}
		else
		if(cttpOp.hascttp_GroupStatusRequest()) {
			reqType = Cttp.CTTP_GROUP_STATUS_REQUEST;
		}
		
		return getMessageHandler(reqType);
	}
}
