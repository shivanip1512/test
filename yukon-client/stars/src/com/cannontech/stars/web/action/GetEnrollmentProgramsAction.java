package com.cannontech.stars.web.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.xml.StarsWebConfigFactory;
import com.cannontech.stars.xml.StarsGetEnrollmentProgramsResponseFactory;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsGetEnrollmentProgramsResponse;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: GetEnrollmentProgramsAction.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 29, 2002 4:29:02 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class GetEnrollmentProgramsAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsGetEnrollmentPrograms getEnrProgs = new StarsGetEnrollmentPrograms();
			
			int energyCompanyID = 0;
			String companyIDStr = req.getParameter("CompanyID");
			if (companyIDStr != null && companyIDStr.length() > 0)
				try {
					energyCompanyID = Integer.parseInt( companyIDStr );
				}
				catch (NumberFormatException e) {}
			if (energyCompanyID > 0)
				getEnrProgs.setEnergyCompanyID( energyCompanyID );
			getEnrProgs.setCategory( req.getParameter("Category") );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsGetEnrollmentPrograms( getEnrProgs );
			
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
            
            StarsGetEnrollmentPrograms getEnrProgs = reqOper.getStarsGetEnrollmentPrograms();
            int energyCompanyID = getEnrProgs.getEnergyCompanyID();
            
            if (energyCompanyID <= 0) {
				StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
	            if (operator == null) {
	            	StarsFailure failure = new StarsFailure();
	            	failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
	            	failure.setDescription( "Session invalidated, please login again" );
	            	respOper.setStarsFailure( failure );
	            	return SOAPUtil.buildSOAPMessage( respOper );
	            }
	            
            	energyCompanyID = (int) operator.getEnergyCompanyID();
            }
            	
            StarsGetEnrollmentProgramsResponse response =
            		StarsGetEnrollmentProgramsResponseFactory.getStarsGetEnrollmentProgramsResponse(
            			new Integer(energyCompanyID), getEnrProgs.getCategory() );
            
            respOper.setStarsGetEnrollmentProgramsResponse( response );
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
			
            StarsGetEnrollmentProgramsResponse programs = operation.getStarsGetEnrollmentProgramsResponse();
            if (programs == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            String category = reqOper.getStarsGetEnrollmentPrograms().getCategory();
            StringBuffer attName = new StringBuffer( "ENROLLMENT_PROGRAMS" );
            if (category != null && category.length() > 0)
            	attName.append("_").append( category.toUpperCase() );

			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			// Use (category == null) to tell apart operator and user as a compromise method
			if (operator != null && category == null)
	            operator.setAttribute(attName.toString(), programs);
	        else
	        	session.setAttribute(attName.toString(), programs);
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
