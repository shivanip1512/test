package com.cannontech.stars.web.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

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
	
	private Vector actionVector = new Vector();
	private Vector reqMsgVector = new Vector();
	
	public Vector getActionVector() {
		return actionVector;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		if (actionVector.size() == 0) return null;
		
		try {
			SOAPMessage reqMsg = SOAPUtil.createMessage();
			reqMsg.getSOAPPart().getEnvelope().getBody().addChildElement( StarsConstants.STARS_OPERATION );
			
			for (int i = 0; i < actionVector.size(); i++) {
				ActionBase action = (ActionBase) actionVector.get(i);
				SOAPMessage message = action.build(req, session);
				reqMsgVector.addElement( message );
				SOAPUtil.mergeSOAPMsgOfOperation( reqMsg, message );
			}
			
			return reqMsg;
		}
		catch (Exception e) {
			e.printStackTrace();
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
			
			for (int i = 0; i < actionVector.size(); i++) {
				ActionBase action = (ActionBase) actionVector.get(i);
				SOAPMessage msg = action.process(reqMsg, session);
				
				StarsOperation resOper = SOAPUtil.parseSOAPMsgForOperation( msg );
				if (resOper.getStarsFailure() != null) {
					// If one operation failed, then the whole operation failed
					StarsOperation respOper = new StarsOperation();
					respOper.setStarsFailure( resOper.getStarsFailure() );
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
            for (int i = 0; i < actionVector.size(); i++) {
            	ActionBase action = (ActionBase) actionVector.get(i);
            	SOAPMessage message = (SOAPMessage) reqMsgVector.get(i);
            	int res = action.parse(message, respMsg, session);
            	if (res != 0) return res;
            }
            
			return 0;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
