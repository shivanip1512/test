package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: MultiAction.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Sep 4, 2002 10:46:11 AM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class MultiAction implements ActionBase {
	
	private ArrayList actionList = new ArrayList();
	private Hashtable actionMsgMap = new Hashtable();
	private ActionBase failedAction = null;
	
	public void addAction(ActionBase action, SOAPMessage message) {
		// Look for action of the same class. If found, replace it; otherwise add the new action
		boolean actionFound = false;
		for (int i = 0; i < actionList.size(); i++) {
			ActionBase act = (ActionBase) actionList.get(i);
			if (action.getClass().equals( act.getClass() )) {
				actionList.set(i, action);
				actionFound = true;
				break;
			}
		}
		
		if (!actionFound) actionList.add( action );
		actionMsgMap.put( action.getClass(), message );
	}
	
	public SOAPMessage getRequestMessage(ActionBase action) {
		return (SOAPMessage) actionMsgMap.get( action.getClass() );
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			SOAPMessage reqMsg = SOAPUtil.createMessage();
			reqMsg.getSOAPPart().getEnvelope().getBody().addChildElement( StarsConstants.STARS_OPERATION );
			
			for (int i = 0; i < actionList.size(); i++) {
				ActionBase action = (ActionBase) actionList.get(i);
				SOAPMessage message = (SOAPMessage) actionMsgMap.get( action.getClass() );
				SOAPUtil.mergeSOAPMsgOfOperation( reqMsg, message );
			}
			
			return reqMsg;
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to build request message" );
		}
		
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
		try {
			SOAPMessage respMsg = SOAPUtil.createMessage();
			respMsg.getSOAPPart().getEnvelope().getBody().addChildElement( StarsConstants.STARS_OPERATION );
			
			for (int i = 0; i < actionList.size(); i++) {
				ActionBase action = (ActionBase) actionList.get(i);
				SOAPMessage msg = action.process(reqMsg, session);
				
				StarsOperation oper = SOAPUtil.parseSOAPMsgForOperation( msg );
				if (oper.getStarsFailure() != null) {
					// If one operation failed, then the whole operation failed
					failedAction = action;
					
					StarsOperation respOper = new StarsOperation();
					respOper.setStarsFailure( oper.getStarsFailure() );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
				
				SOAPUtil.mergeSOAPMsgOfOperation(respMsg, msg);
			}
			
			return respMsg;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
		try {
			for (int i = 0; i < actionList.size(); i++) {
				ActionBase action = (ActionBase) actionList.get(i);
				int res = action.parse(reqMsg, respMsg, session);
				if (res != 0) return res;
			}
            
			return 0;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
        
		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public ActionBase getFailedAction() {
		ActionBase action = failedAction;
		failedAction = null;
		return action;
	}

}
