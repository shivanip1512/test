package com.cannontech.stars.web.action;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.xml.soap.SOAPMessage;
import com.cannontech.stars.xml.util.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class GetLMCtrlHistAction extends ActionBase {

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

    public int parse(SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );
            
            StarsFailure failure = operation.getStarsFailure();
            if (failure != null) return failure.getStatusCode();

			StarsGetLMControlHistoryResponse response = operation.getStarsGetLMControlHistoryResponse();
            if (response == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
            StarsLMControlHistory ctrlHist = response.getStarsLMControlHistory();
            if (ctrlHist == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;

            session.setAttribute("RESPONSE_OPERATION", operation);
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