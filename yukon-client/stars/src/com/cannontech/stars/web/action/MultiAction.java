package com.cannontech.stars.web.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
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
	
	private SOAPMessage reqMsg = null;
	private ArrayList actionList = new ArrayList();
	private boolean error = false;
	
	public void addAction(ActionBase action, HttpServletRequest req, HttpSession session) {
		if (error) return;
		
		actionList.add( action );
		try {
			if (reqMsg == null) {
				reqMsg = SOAPUtil.createMessage();
				reqMsg.getSOAPPart().getEnvelope().getBody().addChildElement( StarsConstants.STARS_OPERATION );
			}
			
			SOAPMessage message = action.build(req, session);
			SOAPUtil.mergeSOAPMsgOfOperation( reqMsg, message );
		}
		catch (Exception e) {
			e.printStackTrace();
			error = true;
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, action.getClass() + ": invalid request parameters" );
		}
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		if (reqMsg == null || error) return null;
		return reqMsg;
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

}
