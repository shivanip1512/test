package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.InventoryManager;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
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
public class DeleteLMHardwareAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsEnergyCompanySettings ecSettings =
					(StarsEnergyCompanySettings) session.getAttribute(ServletUtils.ATT_ENERGY_COMPANY_SETTINGS);
			TimeZone tz = TimeZone.getTimeZone( ecSettings.getStarsEnergyCompany().getTimeZone() );
			if (tz == null) tz = TimeZone.getDefault();
			
			StarsOperation operation = null;
			if (req.getParameter("InvID") != null) {
				// Request directly from the webpage
				operation = getRequestOperation( req, tz );
			}
			else {
				// Request redirected from InventoryManager
				operation = (StarsOperation) session.getAttribute(InventoryManager.STARS_INVENTORY_OPERATION);
				session.removeAttribute( InventoryManager.STARS_INVENTORY_OPERATION );
			}
			
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
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
        	
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        	
			StarsDeleteLMHardware delHw = reqOper.getStarsDeleteLMHardware();
			LiteInventoryBase liteInv = energyCompany.getInventory( delHw.getInventoryID(), true );
			
			if (liteInv.getAccountID() == 0) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "The hardware doesn't belong to any customer account") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( liteInv.getAccountID(), true );
			
			removeInventory( delHw, liteAcctInfo, energyCompany );
        	
        	// Response will be handled here, instead of in parse()
			StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteInv.getAccountID() );
			if (starsAcctInfo != null)
				parseResponse( delHw.getInventoryID(), starsAcctInfo );
			
			respOper.setStarsSuccess( new StarsSuccess() );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot delete the hardware") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
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
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static StarsOperation getRequestOperation(HttpServletRequest req, TimeZone tz) {
		StarsDeleteLMHardware delHw = new StarsDeleteLMHardware();
		if (req.getParameter("OrigInvID") != null) {
			delHw.setInventoryID( Integer.parseInt(req.getParameter("OrigInvID")) );
			delHw.setDeleteFromInventory( false );
			
			if (req.getParameter("RemoveDate") != null && req.getParameter("RemoveDate").length() > 0)
				delHw.setRemoveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("RemoveDate"), tz) );
		}
		else {
			delHw.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
			delHw.setDeleteFromInventory( true );
		}
		
		StarsOperation operation = new StarsOperation();
		operation.setStarsDeleteLMHardware( delHw );
		return operation;
	}
	
	/**
	 * Remove a hardware from a customer account. If liteAcctInfo is null,
	 * it means the account this hardware belongs to has also been deleted.
	 */
	public static void removeInventory(StarsDeleteLMHardware deleteHw, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws WebClientException
	{
		try {
			LiteInventoryBase liteInv = energyCompany.getInventory( deleteHw.getInventoryID(), true );
        	
			if (deleteHw.getDeleteFromInventory()) {
				com.cannontech.database.data.stars.hardware.InventoryBase inventory =
						new com.cannontech.database.data.stars.hardware.InventoryBase();
				inventory.setInventoryID( new Integer(liteInv.getInventoryID()) );
				
				Transaction.createTransaction( Transaction.DELETE, inventory ).execute();
				energyCompany.deleteInventory( liteInv.getInventoryID() );
			}
			else {
				java.util.Date removeDate = deleteHw.getRemoveDate();
				if (removeDate == null) removeDate = new java.util.Date();
        		
				// Add "Uninstall" to hardware events
				com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
				com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
				com.cannontech.database.db.stars.event.LMCustomerEventBase eventBaseDB = event.getLMCustomerEventBase();
        		
				int hwEventEntryID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
				int uninstallActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_UNINSTALL ).getEntryID();
    			
				eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
				eventBaseDB.setActionID( new Integer(uninstallActID) );
				eventBaseDB.setEventDateTime( removeDate );
				if (liteAcctInfo != null)
					eventBaseDB.setNotes( "Removed from account #" + liteAcctInfo.getCustomerAccount().getAccountNumber() );
				eventDB.setInventoryID( new Integer(liteInv.getInventoryID()) );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				
				event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
						Transaction.createTransaction( Transaction.INSERT, event ).execute();
    			
				LiteInventoryBase liteInvNew = null;
				
				if (liteInv instanceof LiteStarsLMHardware) {
					com.cannontech.database.data.stars.hardware.LMHardwareBase.clearLMHardware( liteInv.getInventoryID() );
					
					com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
							new com.cannontech.database.data.stars.hardware.LMHardwareBase();
					StarsLiteFactory.setLMHardwareBase( hardware, (LiteStarsLMHardware)liteInv );
					
					com.cannontech.database.db.stars.hardware.InventoryBase invDB = hardware.getInventoryBase();
					invDB.setAccountID( new Integer(CtiUtilities.NONE_ID) );
					invDB.setRemoveDate( removeDate );
					invDB.setDeviceLabel( "" );
					Transaction.createTransaction( Transaction.UPDATE, invDB ).execute();
					
					liteInvNew = new LiteStarsLMHardware();
					StarsLiteFactory.setLiteStarsLMHardware( (LiteStarsLMHardware)liteInvNew, hardware );
				}
				else {
					com.cannontech.database.data.stars.event.LMHardwareEvent.deleteAllLMHardwareEvents(
							new Integer(liteInv.getInventoryID()) );
					
					com.cannontech.database.db.stars.hardware.InventoryBase invDB =
							new com.cannontech.database.db.stars.hardware.InventoryBase();
					StarsLiteFactory.setInventoryBase( invDB, liteInv );
					invDB.setAccountID( new Integer(CtiUtilities.NONE_ID) );
					invDB.setRemoveDate( removeDate );
					invDB.setDeviceLabel( "" );
					
					invDB = (com.cannontech.database.db.stars.hardware.InventoryBase)
							Transaction.createTransaction( Transaction.UPDATE, invDB ).execute();
					
					liteInvNew = new LiteInventoryBase();
					StarsLiteFactory.setLiteInventoryBase( liteInvNew, invDB );
				}
				
				energyCompany.deleteInventory( liteInv.getInventoryID() );
				energyCompany.addInventory( liteInvNew );
			}
    		
			if (liteAcctInfo != null) {
				if (ECUtils.isLMHardware( liteInv.getCategoryID() )) {
					ArrayList liteApps = liteAcctInfo.getAppliances();
					
					for (int i = 0; i < liteApps.size(); i++) {
						LiteStarsAppliance liteApp = (LiteStarsAppliance) liteApps.get(i);
						
						if (liteApp.getInventoryID() == liteInv.getInventoryID()) {
							liteApp.setInventoryID( 0 );
							
							for (int j = 0; j < liteAcctInfo.getPrograms().size(); j++) {
								LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getPrograms().get(j);
								
								if (liteProg.getProgramID() == liteApp.getProgramID()) {
									liteProg.setGroupID( 0 );
									break;
								}
							}
						}
					}
				}
    			
				liteAcctInfo.getInventories().remove( new Integer(liteInv.getInventoryID()) );
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			throw new WebClientException( "Failed to remove the hardware", e );
		}
	}
	
	public static void parseResponse(int invID, StarsCustAccountInformation starsAcctInfo) {
		for (int i = 0; i < starsAcctInfo.getStarsAppliances().getStarsApplianceCount(); i++) {
			StarsAppliance appliance = starsAcctInfo.getStarsAppliances().getStarsAppliance(i);
			if (appliance.getInventoryID() == invID) {
				appliance.setInventoryID( 0 );
				for (int j = 0; j < starsAcctInfo.getStarsLMPrograms().getStarsLMProgramCount(); j++) {
					StarsLMProgram program = starsAcctInfo.getStarsLMPrograms().getStarsLMProgram(j);
					if (program.getProgramID() == appliance.getProgramID()) {
						program.setGroupID( 0 );
						break;
					}
				}
			}
		}
		
		StarsInventories inventories = starsAcctInfo.getStarsInventories();
		for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
			if (inventories.getStarsInventory(i).getInventoryID() == invID) {
				inventories.removeStarsInventory(i);
				return;
			}
		}
	}

}
