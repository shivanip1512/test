package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.xml.StarsAppFactory;
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
            	StarsFailure failure = new StarsFailure();
            	failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
            	failure.setDescription( "Session invalidated, please login again" );
            	respOper.setStarsFailure( failure );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            com.cannontech.database.data.stars.customer.CustomerAccount account =
            		(com.cannontech.database.data.stars.customer.CustomerAccount) operator.getAttribute("CUSTOMER_ACCOUNT");
            
            com.cannontech.database.data.stars.appliance.ApplianceBase app = new com.cannontech.database.data.stars.appliance.ApplianceBase();
            com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
            
            StarsCreateAppliance newApp = reqOper.getStarsCreateAppliance();
            appDB.setAccountID( account.getCustomerAccount().getAccountID() );
            com.cannontech.database.db.stars.appliance.ApplianceCategory[] appCats =
            		com.cannontech.database.data.stars.appliance.ApplianceCategory.getAllApplianceCategories( new Integer(newApp.getApplianceCategoryID()) );
            if (appCats == null || appCats.length == 0)
            	appDB.setApplianceCategoryID( new Integer(com.cannontech.database.db.stars.appliance.ApplianceCategory.NONE_INT) );
            else
            	appDB.setApplianceCategoryID( appCats[0].getApplianceCategoryID() );
            appDB.setLMProgramID( new Integer(0) );
            appDB.setNotes( newApp.getNotes() );
            
            app = (com.cannontech.database.data.stars.appliance.ApplianceBase) Transaction.createTransaction( Transaction.INSERT, app ).execute();
            
            StarsCreateApplianceResponse resp = (StarsCreateApplianceResponse) StarsAppFactory.newStarsApp( newApp, StarsCreateApplianceResponse.class );
            resp.setApplianceID( app.getApplianceBase().getApplianceID().intValue() );
            resp.setLmProgramID( -1 );
            resp.setInventoryID( -1 );
            respOper.setStarsCreateApplianceResponse( resp );

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
			
			StarsCreateApplianceResponse resp = operation.getStarsCreateApplianceResponse();
			StarsAppliance app = (StarsAppliance) StarsAppFactory.newStarsApp( resp, StarsAppliance.class );
			
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsCustAccountInfo accountInfo = (StarsCustAccountInfo) operator.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
            if (accountInfo == null)
            	return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
            	
			StarsAppliances appliances = accountInfo.getStarsAppliances();
			appliances.addStarsAppliance( app );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
