package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.Location;
import com.cannontech.stars.xml.serialize.Manufacturer;
import com.cannontech.stars.xml.serialize.ServiceCompany;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsAppliances;
import com.cannontech.stars.xml.serialize.StarsCreateAppliance;
import com.cannontech.stars.xml.serialize.StarsCreateApplianceResponse;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

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
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			java.util.Hashtable selectionLists = (java.util.Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );

			StarsCreateAppliance newApp = new StarsCreateAppliance();
			newApp.setApplianceCategoryID( Integer.parseInt(req.getParameter("Category")) );
			newApp.setCategoryName( "" );
			newApp.setYearManufactured( req.getParameter("ManuYear") );
			newApp.setNotes( req.getParameter("Notes") );
			newApp.setModelNumber( req.getParameter("ModelNo") );
			
			Manufacturer manu = (Manufacturer) StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER, Integer.parseInt(req.getParameter("Manufacturer"))),
					Manufacturer.class );
			newApp.setManufacturer( manu );
			
			Location loc = (Location) StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_LOCATION, Integer.parseInt(req.getParameter("Location"))),
					Location.class );
			newApp.setLocation( loc );
			
			ServiceCompany company = (ServiceCompany) StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY, Integer.parseInt(req.getParameter("Company"))),
					ServiceCompany.class );
			newApp.setServiceCompany( company );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsCreateAppliance( newApp );
			
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

			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation accountInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (accountInfo == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
        	int energyCompanyID = user.getEnergyCompanyID();
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
            StarsCreateAppliance newApp = reqOper.getStarsCreateAppliance();
            com.cannontech.database.data.stars.appliance.ApplianceBase app = new com.cannontech.database.data.stars.appliance.ApplianceBase();
            com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
            
            appDB.setAccountID( new Integer(accountInfo.getCustomerAccount().getAccountID()) );
            appDB.setLMProgramID( new Integer(0) );
            if (!newApp.getYearManufactured().equals(""))
            	appDB.setYearManufactured( Integer.valueOf(newApp.getYearManufactured()) );
            appDB.setManufacturerID( new Integer(newApp.getManufacturer().getEntryID()) );
            appDB.setLocationID( new Integer(newApp.getLocation().getEntryID()) );
            appDB.setNotes( newApp.getNotes() );
            appDB.setModelNumber( newApp.getModelNumber() );
            
            java.util.ArrayList appCats = energyCompany.getAllApplianceCategories();
            if (appCats == null || appCats.size() == 0)
            	appDB.setApplianceCategoryID( new Integer(com.cannontech.database.db.stars.appliance.ApplianceCategory.NONE_INT) );
            else {
            	// Find the first appliance category that's in the specified "category"
            	for (int i = 0; i < appCats.size(); i++) {
            		LiteApplianceCategory appCat = (LiteApplianceCategory) appCats.get(i);
            		if (appCat.getCategoryID() == newApp.getApplianceCategoryID()) {
            			appDB.setApplianceCategoryID( new Integer(appCat.getApplianceCategoryID()) );
            			break;
            		}
            	}
            }
            
            app = (com.cannontech.database.data.stars.appliance.ApplianceBase) Transaction.createTransaction(Transaction.INSERT, app).execute();
            
            LiteStarsAppliance liteApp = (LiteStarsAppliance) StarsLiteFactory.createLite(app);
            accountInfo.getAppliances().add( liteApp );
            
            StarsCreateApplianceResponse resp = new StarsCreateApplianceResponse();
            resp.setStarsAppliance( StarsLiteFactory.createStarsAppliance(liteApp, energyCompanyID) );
            respOper.setStarsCreateApplianceResponse( resp );

            SOAPMessage respMsg = SOAPUtil.buildSOAPMessage( respOper );
            return respMsg;
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot create the appliance") );
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
			
			StarsCreateApplianceResponse resp = operation.getStarsCreateApplianceResponse();
			StarsAppliance app = resp.getStarsAppliance();
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            if (accountInfo == null)
            	return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
            
            StarsAppliances starsApps = accountInfo.getStarsAppliances();
            int i = -1;
            for (i = starsApps.getStarsApplianceCount() - 1; i >= 0; i--) {
            	StarsAppliance starsApp = starsApps.getStarsAppliance(i);
            	if (starsApp.getCategoryName().compareTo( app.getCategoryName() ) <= 0)
            		break;
            }
			starsApps.addStarsAppliance( i+1, app );
			session.setAttribute( ServletUtils.ATT_REDIRECT, "/operator/Consumer/Appliance.jsp?AppNo=" + String.valueOf(i+1) );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
