package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import java.util.*;
import com.cannontech.stars.xml.util.*;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.web.StarsUser;
import com.cannontech.stars.xml.serialize.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GetCustSelListsAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsGetCustSelectionLists getSelLists = new StarsGetCustSelectionLists();
			
			int energyCompanyID = 0;
			String companyIDStr = req.getParameter("CompanyID");
			if (companyIDStr != null && companyIDStr.length() > 0)
				try {
					energyCompanyID = Integer.parseInt( companyIDStr );
				}
				catch (NumberFormatException e) {}
			if (energyCompanyID > 0)
				getSelLists.setEnergyCompanyID( energyCompanyID );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsGetCustSelectionLists( getSelLists );
			
			return SOAPUtil.buildSOAPMessage( operation );
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
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = new StarsOperation();
            
            StarsGetCustSelectionLists getSelLists = reqOper.getStarsGetCustSelectionLists();
            int energyCompanyID = getSelLists.getEnergyCompanyID();
            
            if (energyCompanyID <= 0) {
				StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
				StarsUser user = (StarsUser) session.getAttribute("USER");
	            if (operator == null && user == null) {
	            	StarsFailure failure = new StarsFailure();
	            	failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
	            	failure.setDescription( "Session invalidated, please login again" );
	            	respOper.setStarsFailure( failure );
	            	return SOAPUtil.buildSOAPMessage( respOper );
	            }
	            
	            if (operator != null)
	            	energyCompanyID = (int) operator.getEnergyCompanyID();
	            else
	            	energyCompanyID = user.getEnergyCompanyID();
            }
            
            Hashtable selectionListTable = com.cannontech.stars.util.CommonUtils.getSelectionListTable( new Integer(energyCompanyID) );
            StarsGetCustSelectionListsResponse response = new StarsGetCustSelectionListsResponse();
            
            Iterator it = selectionListTable.values().iterator();
            while (it.hasNext()) {
            	StarsCustSelectionList list = (StarsCustSelectionList) it.next();
            	response.addStarsCustSelectionList( list );
            }
            
            respOper.setStarsGetCustSelectionListsResponse( response );
            return SOAPUtil.buildSOAPMessage( respOper );
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
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) return failure.getStatusCode();
			
            StarsGetCustSelectionListsResponse lists = operation.getStarsGetCustSelectionListsResponse();
            if (lists == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
            Hashtable selectionListTable = new Hashtable();
            for (int i = 0; i < lists.getStarsCustSelectionListCount(); i++) {
            	StarsCustSelectionList list = lists.getStarsCustSelectionList(i);
            	selectionListTable.put( list.getListName(), list );
            }

			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsUser user = (StarsUser) session.getAttribute("USER");
			if (operator != null)
	            operator.setAttribute( "CUSTOMER_SELECTION_LIST", selectionListTable );
	        else
	            user.setAttribute( "CUSTOMER_SELECTION_LIST", selectionListTable );
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
