package com.cannontech.stars.web.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.data.lite.stars.LiteStarsLMControlHistory;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.*;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.web.StarsUser;
import com.cannontech.stars.xml.*;
import com.cannontech.stars.xml.serialize.ControlHistory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
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
            getHist.setGetSummary( true );

            StarsOperation operation = new StarsOperation();
            operation.setStarsGetLMControlHistory( getHist );

            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            e.printStackTrace();
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
        }

        return null;
    }

    public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        StarsOperation respOper = new StarsOperation();
        
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsUser user = (StarsUser) session.getAttribute("USER");
            if (operator == null && user == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            int energyCompanyID = (operator != null) ? (int) operator.getEnergyCompanyID() : user.getEnergyCompanyID();

            StarsGetLMControlHistory getHist = reqOper.getStarsGetLMControlHistory();

            LiteStarsLMControlHistory liteCtrlHist = com.cannontech.stars.web.servlet.SOAPServer.getLMControlHistory( energyCompanyID, getHist.getGroupID() );
            StarsLMControlHistory starsCtrlHist = StarsLiteFactory.createStarsLMControlHistory(
            		liteCtrlHist, getHist.getPeriod(), getHist.getGetSummary() );
                
            StarsGetLMControlHistoryResponse response = new StarsGetLMControlHistoryResponse();
            response.setStarsLMControlHistory( starsCtrlHist );
            respOper.setStarsGetLMControlHistoryResponse( response );

            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot get the program's control history") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
            }
        }

        return null;
    }

    public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );
            
            StarsFailure failure = operation.getStarsFailure();
            if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
            	return failure.getStatusCode();
            }

			StarsGetLMControlHistoryResponse response = operation.getStarsGetLMControlHistoryResponse();
            if (response == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
            StarsLMControlHistory ctrlHist = response.getStarsLMControlHistory();
            if (ctrlHist == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			// Update control history
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsUser user = (StarsUser) session.getAttribute("USER");
			StarsCustAccountInformation accountInfo = null;
			
			if (operator != null)
				accountInfo = (StarsCustAccountInformation) operator.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			else
				accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
				
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsGetLMControlHistory getCtrlHist = reqOper.getStarsGetLMControlHistory();
            
			StarsLMPrograms programs = accountInfo.getStarsLMPrograms();
			for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
				StarsLMProgram program = programs.getStarsLMProgram(i);
				if (program.getGroupID() == getCtrlHist.getGroupID()) {
					if (ctrlHist.getControlHistoryCount() > 0)
						program.getStarsLMControlHistory().setControlHistory( ctrlHist.getControlHistory() );
					if (ctrlHist.getControlSummary() != null)
						program.getStarsLMControlHistory().setControlSummary( ctrlHist.getControlSummary() );
				}
			}
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }
}