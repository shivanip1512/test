package com.cannontech.stars.web.action;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListFuncs;
import com.cannontech.database.data.lite.stars.LiteCustomerFAQ;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.StarsCustomerFAQ;
import com.cannontech.stars.xml.serialize.StarsCustomerFAQGroup;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetCustomerFAQs;
import com.cannontech.stars.xml.serialize.StarsGetCustomerFAQsResponse;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.util.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GetCustomerFAQsAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsGetCustomerFAQs getFAQs = new StarsGetCustomerFAQs();
			
			int energyCompanyID = 0;
			String companyIDStr = req.getParameter("CompanyID");
			if (companyIDStr != null && companyIDStr.length() > 0)
				try {
					energyCompanyID = Integer.parseInt( companyIDStr );
				}
				catch (NumberFormatException e) {}
			if (energyCompanyID > 0)
				getFAQs.setEnergyCompanyID( energyCompanyID );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsGetCustomerFAQs( getFAQs );
			
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (Exception e) {
			e.printStackTrace();
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
		}
		
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        StarsOperation respOper = new StarsOperation();
        
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            
            StarsGetCustomerFAQs getFAQs = reqOper.getStarsGetCustomerFAQs();
            int energyCompanyID = getFAQs.getEnergyCompanyID();
            
            if (energyCompanyID <= 0) {
				StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
	            if (user != null)
	            	energyCompanyID = user.getEnergyCompanyID();
	            else {
	            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
	            }
            }
            
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
            respOper.setStarsGetCustomerFAQsResponse( energyCompany.getStarsGetCustomerFAQsResponse() );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot get customer FAQs") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
            }
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
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
            StarsGetCustomerFAQsResponse faqs = operation.getStarsGetCustomerFAQsResponse();
            if (faqs == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;

			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        	user.setAttribute(ServletUtils.ATT_CUSTOMER_FAQS, faqs);
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
