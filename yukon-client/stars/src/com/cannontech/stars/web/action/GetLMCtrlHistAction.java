package com.cannontech.stars.web.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.db.stars.*;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.web.StarsUser;
import com.cannontech.stars.xml.*;
import com.cannontech.stars.xml.serialize.ControlHistory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInfo;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetLMControlHistory;
import com.cannontech.stars.xml.serialize.StarsGetLMControlHistoryResponse;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class GetLMCtrlHistAction implements ActionBase {

    public GetLMCtrlHistAction() {
        super();
    }

    public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
            StarsGetLMControlHistory getHist = new StarsGetLMControlHistory();

            int groupID = -1;
            if (req.getParameter("Group") != null)
                try {
                    groupID = Integer.parseInt( req.getParameter("Group") );
                }
                catch (NumberFormatException e) {}
            getHist.setGroupID( groupID );
            getHist.setPeriod( StarsCtrlHistPeriod.valueOf(req.getParameter("Period")) );

            StarsOperation operation = new StarsOperation();
            operation.setStarsGetLMControlHistory( getHist );

            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );
            
            StarsFailure failure = operation.getStarsFailure();
            if (failure != null) return failure.getStatusCode();

			StarsGetLMControlHistoryResponse response = operation.getStarsGetLMControlHistoryResponse();
            if (response == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
            StarsLMControlHistory ctrlHist = response.getStarsLMControlHistory();
            if (ctrlHist == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			// Update today's control history
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsGetLMControlHistory getCtrlHist = reqOper.getStarsGetLMControlHistory();
            StarsLMControlHistory ctrlHistToday = new StarsLMControlHistory();
            Date today = com.cannontech.util.ServletUtil.getToday();
            
            for (int i = 0; i < ctrlHist.getControlHistoryCount(); i++) {
            	ControlHistory hist = ctrlHist.getControlHistory(i);
            	if ( hist.getStartDateTime().before(today) ) break;
            	ctrlHistToday.addControlHistory( hist );
            }
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsUser user = (StarsUser) session.getAttribute("USER");
			StarsCustAccountInfo accountInfo = null;
			
			if (operator != null)
				accountInfo = (StarsCustAccountInfo) operator.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
			else
				accountInfo = (StarsCustAccountInfo) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
				
			StarsLMPrograms programs = accountInfo.getStarsLMPrograms();
			for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
				StarsLMProgram program = programs.getStarsLMProgram(i);
				if (program.getGroupID() == getCtrlHist.getGroupID())
					program.getStarsLMControlHistory().setControlHistory( ctrlHistToday.getControlHistory() );
			}

			if (operator != null)
	            operator.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + "LM_CONTROL_HISTORY", ctrlHist );
	        else
	        	user.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + "LM_CONTROL_HISTORY", ctrlHist );
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }

    public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = new StarsOperation();
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsUser user = (StarsUser) session.getAttribute("USER");
            if (operator == null && user == null) {
            	StarsFailure failure = new StarsFailure();
            	failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
            	failure.setDescription( "Session invalidated, please login again" );
            	respOper.setStarsFailure( failure );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }

            StarsGetLMControlHistory getHist = reqOper.getStarsGetLMControlHistory();
            StarsLMControlHistory starsCtrlHist = new StarsLMControlHistory();

            StarsLMControlHistory ctrlHist = LMControlHistory.getStarsLMControlHistory(
                	new Integer(getHist.getGroupID()), getHist.getPeriod(), getHist.getGetSummary() );
                
            StarsGetLMControlHistoryResponse response = new StarsGetLMControlHistoryResponse();
            response.setStarsLMControlHistory( ctrlHist );
            respOper.setStarsGetLMControlHistoryResponse( response );

            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}