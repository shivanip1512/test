package com.cannontech.stars.web.action;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.Location;
import com.cannontech.stars.xml.serialize.Manufacturer;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateAppliance;
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
public class UpdateApplianceAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
			
			StarsUpdateAppliance updateApp = new StarsUpdateAppliance();
			updateApp.setApplianceID( Integer.parseInt(req.getParameter("AppID")) );
			updateApp.setYearManufactured( req.getParameter("ManuYear") );
			updateApp.setNotes( req.getParameter("Notes") );
			updateApp.setModelNumber( req.getParameter("ModelNo") );
			
			Manufacturer manu = (Manufacturer) StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER, Integer.parseInt(req.getParameter("Manufacturer"))),
					Manufacturer.class );
			updateApp.setManufacturer( manu );
			
			Location loc = (Location) StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_LOCATION, Integer.parseInt(req.getParameter("Location"))),
					Location.class );
			updateApp.setLocation( loc );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateAppliance( updateApp );
			
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
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
            
            StarsUpdateAppliance updateApp = reqOper.getStarsUpdateAppliance();
            for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
            	LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
            	if (liteApp.getApplianceID() == updateApp.getApplianceID()) {
            		liteApp.setManufacturerID( updateApp.getManufacturer().getEntryID() );
            		liteApp.setYearManufactured( Integer.parseInt(updateApp.getYearManufactured()) );
            		liteApp.setLocationID( updateApp.getLocation().getEntryID() );
            		liteApp.setNotes( updateApp.getNotes() );
            		liteApp.setModelNumber( updateApp.getModelNumber() );
            		
            		com.cannontech.database.data.stars.appliance.ApplianceBase app =
            				(com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
    				Transaction.createTransaction(Transaction.UPDATE, app.getApplianceBase()).execute();
            		break;
            	}
            }
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription("Appliance information updated successfully");
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update the appliance information") );
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
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            if (accountInfo == null)
            	return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
			
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsUpdateAppliance updateApp = reqOper.getStarsUpdateAppliance();
			
			for (int i = 0; i < accountInfo.getStarsAppliances().getStarsApplianceCount(); i++) {
				StarsAppliance app = accountInfo.getStarsAppliances().getStarsAppliance(i);
				if (app.getApplianceID() == updateApp.getApplianceID()) {
					app.setManufacturer( updateApp.getManufacturer() );
					app.setYearManufactured( updateApp.getYearManufactured() );
					app.setLocation( updateApp.getLocation() );
					app.setNotes( updateApp.getNotes() );
					break;
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
