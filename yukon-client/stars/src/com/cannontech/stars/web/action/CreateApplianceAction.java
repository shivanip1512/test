package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.xml.StarsAppFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.util.*;
import com.cannontech.stars.xml.serialize.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CreateApplianceAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			if (operator == null) return null;
			java.util.Hashtable selectionLists = (java.util.Hashtable) operator.getAttribute( "CUSTOMER_SELECTION_LIST" );

			StarsCreateAppliance newApp = new StarsCreateAppliance();
			
			newApp.setApplianceCategoryID( Integer.parseInt(req.getParameter("Category")) );
			StarsCustSelectionList appCatList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_APPLIANCECATEGORY );
			newApp.setCategoryName( "" );
			for (int i = 0; i < appCatList.getStarsSelectionListEntryCount(); i++) {
				StarsSelectionListEntry entry = appCatList.getStarsSelectionListEntry(i);
				if (entry.getEntryID() == newApp.getApplianceCategoryID()) {
					newApp.setCategoryName( entry.getContent() );
					break;
				}
			}
			
			newApp.setManufacturer( req.getParameter("Manufacturer") );
			newApp.setManufactureYear( req.getParameter("ManuYear") );
			newApp.setLocation( req.getParameter("Location") );
			
			ServiceCompany company = new ServiceCompany();
			company.setEntryID( Integer.parseInt(req.getParameter("Company")) );
			StarsCustSelectionList companyList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY );
			for (int i = 0; i < companyList.getStarsSelectionListEntryCount(); i++) {
				StarsSelectionListEntry entry = companyList.getStarsSelectionListEntry(i);
				if (entry.getEntryID() == company.getEntryID()) {
					company.setContent( entry.getContent() );
					break;
				}
			}			
			newApp.setServiceCompany( company );
			
			newApp.setNotes( req.getParameter("Notes") );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsCreateAppliance( newApp );
			
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

			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            if (operator == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation accountInfo = (LiteStarsCustAccountInformation) operator.getAttribute( "CUSTOMER_ACCOUNT_INFORMATION" );
        	if (accountInfo == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
            
            StarsCreateAppliance newApp = reqOper.getStarsCreateAppliance();
            com.cannontech.database.data.stars.appliance.ApplianceBase app = new com.cannontech.database.data.stars.appliance.ApplianceBase();
            com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
            
            appDB.setAccountID( new Integer(accountInfo.getCustomerAccount().getAccountID()) );
            java.util.ArrayList appCats = com.cannontech.stars.web.servlet.SOAPServer.getAllApplianceCategories(
            		new Integer((int) operator.getEnergyCompanyID()) );
            if (appCats == null || appCats.size() == 0)
            	appDB.setApplianceCategoryID( new Integer(com.cannontech.database.db.stars.appliance.ApplianceCategory.NONE_INT) );
            else {
            	// Find the first appliance category that's in the specified "category"
            	for (int i = 0; i < appCats.size(); i++) {
            		StarsApplianceCategory appCat = (StarsApplianceCategory) appCats.get(i);
            		if (appCat.getCategoryID() == newApp.getApplianceCategoryID()) {
            			appDB.setApplianceCategoryID( new Integer(appCat.getApplianceCategoryID()) );
            			break;
            		}
            	}
            }
            appDB.setLMProgramID( new Integer(0) );
            appDB.setNotes( newApp.getNotes() );
            
            app = (com.cannontech.database.data.stars.appliance.ApplianceBase) Transaction.createTransaction( Transaction.INSERT, app ).execute();
            
            StarsAppliance starsApp = (StarsAppliance) StarsAppFactory.newStarsApp( newApp, StarsAppliance.class );
            starsApp.setApplianceID( app.getApplianceBase().getApplianceID().intValue() );
            starsApp.setLmProgramID( -1 );
            starsApp.setInventoryID( -1 );
            
            accountInfo.getAppliances().add( starsApp );
            
            StarsCreateApplianceResponse resp = new StarsCreateApplianceResponse();
            resp.setStarsAppliance( starsApp );
            respOper.setStarsCreateApplianceResponse( resp );

            SOAPMessage respMsg = SOAPUtil.buildSOAPMessage( respOper );
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
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) return failure.getStatusCode();
			
			StarsCreateApplianceResponse resp = operation.getStarsCreateApplianceResponse();
			StarsAppliance app = resp.getStarsAppliance();
			
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					operator.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
            if (accountInfo == null)
            	return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
            	
			accountInfo.getStarsAppliances().addStarsAppliance( app );
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
