package com.cannontech.stars.web.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsWebConfigFactory;
import com.cannontech.stars.xml.StarsGetEnrollmentProgramsResponseFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
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
            
            StarsGetEnrollmentPrograms getEnrProgs = reqOper.getStarsGetEnrollmentPrograms();
            int energyCompanyID = getEnrProgs.getEnergyCompanyID();
            
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
            
            ArrayList liteAppCats = com.cannontech.stars.web.servlet.SOAPServer.getAllApplianceCategories( energyCompanyID );
            StarsGetEnrollmentProgramsResponse resp = new StarsGetEnrollmentProgramsResponse();
            
            // Generate the category name, example values: "LMPrograms", "LMPrograms-Switch", "LMPrograms-Thermostat"
            String wholeCatName = "LMPrograms";
            if (getEnrProgs.getCategory() != null && getEnrProgs.getCategory().length() > 0)
            	wholeCatName += "-" + getEnrProgs.getCategory();
            	
            for (int i = 0; i < liteAppCats.size(); i++) {
            	LiteApplianceCategory liteAppCat = (LiteApplianceCategory) liteAppCats.get(i);
            	
            	// Find only LM programs in the specified category
            	LiteLMProgram[] liteProgs = liteAppCat.getPublishedPrograms();
            	ArrayList progsInCat = new ArrayList();
            	for (int j = 0; j < liteProgs.length; j++) {
            		if (liteProgs[j].getProgramCategory().startsWith( wholeCatName ))
            			progsInCat.add( liteProgs[j] );
            	}
            	
            	if (progsInCat.size() > 0)
            		resp.addStarsApplianceCategory( StarsLiteFactory.createStarsApplianceCategory(liteAppCat, progsInCat) );
            }
            
            respOper.setStarsGetEnrollmentProgramsResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot get the enrollment program list") );
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
			
            StarsGetEnrollmentProgramsResponse programs = operation.getStarsGetEnrollmentProgramsResponse();
            if (programs == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            String category = reqOper.getStarsGetEnrollmentPrograms().getCategory();
            StringBuffer attName = new StringBuffer( ServletUtils.ATT_ENROLLMENT_PROGRAMS );
            if (category != null && category.length() > 0)
            	attName.append("_").append( category.toUpperCase() );

        	StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
    		user.setAttribute(attName.toString(), programs);
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
