package com.cannontech.stars.web.action;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.RoleTypes;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPClient;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
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
public class GetEnergyCompanySettingsAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsGetEnergyCompanySettings getSettings = new StarsGetEnergyCompanySettings();
			
			int energyCompanyID = 0;
			String companyIDStr = req.getParameter("CompanyID");
			if (companyIDStr != null && companyIDStr.length() > 0)
				try {
					energyCompanyID = Integer.parseInt( companyIDStr );
				}
				catch (NumberFormatException e) {}
			if (energyCompanyID > 0)
				getSettings.setEnergyCompanyID( energyCompanyID );
			getSettings.setProgramCategory( req.getParameter("ProgCat") );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsGetEnergyCompanySettings( getSettings );
			
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
            
            StarsGetEnergyCompanySettings getSettings = reqOper.getStarsGetEnergyCompanySettings();
            int energyCompanyID = getSettings.getEnergyCompanyID();
            
        	StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (energyCompanyID <= 0) {
            	if (user != null)
            		energyCompanyID = user.getEnergyCompanyID();
	            else {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
	            }
            }
            
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            StarsGetEnergyCompanySettingsResponse resp = new StarsGetEnergyCompanySettingsResponse();
            
            if (SOAPServer.isClientLocal()) {
            	resp.setStarsEnergyCompany( energyCompany.getStarsEnergyCompany() );
	            resp.setStarsWebConfig( energyCompany.getStarsWebConfig(energyCompany.getWebConfigID()) );
            	resp.setStarsEnrollmentPrograms( energyCompany.getStarsEnrollmentPrograms(getSettings.getProgramCategory()) );
	            if (ServerUtils.isOperator( user )) {
	            	resp.setStarsCustomerSelectionLists( energyCompany.getStarsCustomerSelectionLists(user) );
					if (AuthFuncs.checkRole( user.getYukonUser(), RoleTypes.CONSUMERINFO_HARDWARES ) != null
						|| AuthFuncs.checkRole( user.getYukonUser(), RoleTypes.CONSUMERINFO_WORKORDERS) != null)
						resp.setStarsServiceCompanies( energyCompany.getStarsServiceCompanies() );
					if (AuthFuncs.checkRole( user.getYukonUser(), RoleTypes.CONSUMERINFO_PROGRAMS_OPTOUT) != null)
						resp.setStarsExitInterviewQuestions( energyCompany.getStarsExitInterviewQuestions() );
	            }
	            else if (ServerUtils.isResidentialCustomer( user )) {
	            	if (AuthFuncs.checkRole( user.getYukonUser(), RoleTypes.CONSUMERINFO_QUESTIONS ) != null)
		            	resp.setStarsCustomerFAQs( energyCompany.getStarsCustomerFAQs() );
					if (AuthFuncs.checkRole( user.getYukonUser(), RoleTypes.CONSUMERINFO_PROGRAMS_OPTOUT) != null)
						resp.setStarsExitInterviewQuestions( energyCompany.getStarsExitInterviewQuestions() );
					if (AuthFuncs.checkRole( user.getYukonUser(), RoleTypes.CONSUMERINFO_THERMOSTAT) != null)
						resp.setStarsDefaultThermostatSettings( energyCompany.getStarsDefaultThermostatSettings() );
	            }
	        	
	        	user.setAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS, resp );
	        	if (resp.getStarsCustomerSelectionLists() != null) {
		            Hashtable selectionListTable = new Hashtable();
		            for (int i = 0; i < resp.getStarsCustomerSelectionLists().getStarsCustSelectionListCount(); i++) {
		            	StarsCustSelectionList list = resp.getStarsCustomerSelectionLists().getStarsCustSelectionList(i);
		            	selectionListTable.put( list.getListName(), list );
		            }
		        	user.setAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS, selectionListTable );
	        	}
            }
            else {
            	resp.setStarsEnergyCompany( StarsLiteFactory.createStarsEnergyCompany(energyCompany) );
	            resp.setStarsWebConfig( energyCompany.getStarsWebConfig(energyCompany.getWebConfigID()) );
            	resp.setStarsEnrollmentPrograms( StarsLiteFactory.createStarsEnrollmentPrograms(
            			energyCompany.getAllApplianceCategories(), getSettings.getProgramCategory(), energyCompanyID) );
	            if (ServerUtils.isOperator( user )) {
	            	resp.setStarsCustomerSelectionLists( energyCompany.getStarsCustomerSelectionLists(user) );
					if (AuthFuncs.checkRole( user.getYukonUser(), RoleTypes.CONSUMERINFO_HARDWARES ) != null
						|| AuthFuncs.checkRole( user.getYukonUser(), RoleTypes.CONSUMERINFO_WORKORDERS) != null)
						resp.setStarsServiceCompanies( energyCompany.getStarsServiceCompanies() );
					if (AuthFuncs.checkRole( user.getYukonUser(), RoleTypes.CONSUMERINFO_PROGRAMS_OPTOUT) != null)
						resp.setStarsExitInterviewQuestions( energyCompany.getStarsExitInterviewQuestions() );
	            }
	            else if (ServerUtils.isResidentialCustomer( user )) {
	            	if (AuthFuncs.checkRole( user.getYukonUser(), RoleTypes.CONSUMERINFO_QUESTIONS ) != null)
		            	resp.setStarsCustomerFAQs( energyCompany.getStarsCustomerFAQs() );
					if (AuthFuncs.checkRole( user.getYukonUser(), RoleTypes.CONSUMERINFO_PROGRAMS_OPTOUT) != null)
						resp.setStarsExitInterviewQuestions( energyCompany.getStarsExitInterviewQuestions() );
					if (AuthFuncs.checkRole( user.getYukonUser(), RoleTypes.CONSUMERINFO_THERMOSTAT) != null)
						resp.setStarsDefaultThermostatSettings( energyCompany.getStarsDefaultThermostatSettings() );
	            }
            }
            
            respOper.setStarsGetEnergyCompanySettingsResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
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
			
            StarsGetEnergyCompanySettingsResponse resp = operation.getStarsGetEnergyCompanySettingsResponse();
            if (resp == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
            if (!SOAPClient.isServerLocal()) {
	        	StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
	        	
	        	user.setAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS, resp );
	        	if (resp.getStarsCustomerSelectionLists() != null) {
		            Hashtable selectionListTable = new Hashtable();
		            for (int i = 0; i < resp.getStarsCustomerSelectionLists().getStarsCustSelectionListCount(); i++) {
		            	StarsCustSelectionList list = resp.getStarsCustomerSelectionLists().getStarsCustSelectionList(i);
		            	selectionListTable.put( list.getListName(), list );
		            }
		        	user.setAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS, selectionListTable );
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
