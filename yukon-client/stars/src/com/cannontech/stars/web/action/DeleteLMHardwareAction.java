package com.cannontech.stars.web.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.InventoryManager;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsFailure;
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
	
	public static final int TARGET_DELETE_FROM_INVENTORY = -1;
	public static final int TARGET_TO_WAREHOUSE = 0;
	public static final int TARGET_ACCOUNT_DELETED = 1;
	public static final int TARGET_TO_ANOTHER_ACCOUNT = 2;

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsOperation operation = null;
			if (req.getParameter("InvID") != null) {
				// Request directly from the webpage
				operation = getRequestOperation( req );
			}
			else {
				// Request redirected from InventoryManager
				operation = (StarsOperation) session.getAttribute(InventoryManager.STARS_INVENTORY_OPERATION);
				session.removeAttribute( InventoryManager.STARS_INVENTORY_OPERATION );
			}
			
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
		java.sql.Connection conn = null;
        
		try {
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );

			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
        	
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
        	
			StarsDeleteLMHardware delHw = reqOper.getStarsDeleteLMHardware();
			LiteInventoryBase liteInv = energyCompany.getInventory( delHw.getInventoryID(), true );
			
			if (liteInv.getAccountID() == 0) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, "The hardware doesn't belong to any customer account") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( liteInv.getAccountID(), true );
			
			int target = delHw.getDeleteFromInventory()? TARGET_DELETE_FROM_INVENTORY : TARGET_TO_WAREHOUSE;
			removeInventory( delHw.getInventoryID(), liteAcctInfo, energyCompany, target, conn );
        	
        	// Response will be handled here, instead of in parse()
			StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteInv.getAccountID() );
			if (starsAcctInfo != null)
				parseResponse( delHw.getInventoryID(), starsAcctInfo );
			
			respOper.setStarsSuccess( new StarsSuccess() );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			e.printStackTrace();
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot delete the hardware") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
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
			e.printStackTrace();
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static StarsOperation getRequestOperation(HttpServletRequest req) {
		StarsDeleteLMHardware delHw = new StarsDeleteLMHardware();
		if (req.getParameter("OrigInvID") != null)
			delHw.setInventoryID( Integer.parseInt(req.getParameter("OrigInvID")) );
		else {
			delHw.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
			delHw.setDeleteFromInventory( true );
		}
			
		StarsOperation operation = new StarsOperation();
		operation.setStarsDeleteLMHardware( delHw );
		return operation;
	}
	
	/**
	 * Remove a hardware from a customer account. According to the "target" parameter
	 * the removed hardware could be deleted from inventory permanantly, moved to warehouse,
	 * moved to warehouse because account is deleted, or moved to another customer account.
	 */
	public static void removeInventory(int invID, LiteStarsCustAccountInformation liteAcctInfo,
			LiteStarsEnergyCompany energyCompany, int target, java.sql.Connection conn)
			throws java.sql.SQLException
	{
		LiteInventoryBase liteInv = energyCompany.getInventory( invID, true );
        
        if (target == TARGET_DELETE_FROM_INVENTORY) {
        	com.cannontech.database.data.stars.hardware.InventoryBase inventory =
        			new com.cannontech.database.data.stars.hardware.InventoryBase();
			inventory.setInventoryID( new Integer(invID) );
			inventory.setDbConnection( conn );
			inventory.delete();
        	
        	energyCompany.deleteInventory( invID );
        }
        else {
			// Add "Uninstall" to hardware events
			com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
			com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
			com.cannontech.database.db.stars.event.LMCustomerEventBase eventBaseDB = event.getLMCustomerEventBase();
        	
			int hwEventEntryID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
			int uninstallActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_UNINSTALL ).getEntryID();
    		
			eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
			eventBaseDB.setActionID( new Integer(uninstallActID) );
			eventBaseDB.setEventDateTime( new java.util.Date() );
			eventBaseDB.setNotes( "Removed from account #" + liteAcctInfo.getCustomerAccount().getAccountNumber() );
			eventDB.setInventoryID( new Integer(invID) );
			event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
			event.setDbConnection( conn );
			event.add();
    	
			if (target == TARGET_TO_WAREHOUSE || target == TARGET_ACCOUNT_DELETED) {
				LiteInventoryBase liteInvNew = null;
				
				if (liteInv instanceof LiteStarsLMHardware) {
					com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
							new com.cannontech.database.data.stars.hardware.LMHardwareBase();
					StarsLiteFactory.setLMHardwareBase( hardware, (LiteStarsLMHardware)liteInv );
					hardware.setDbConnection( conn );
					hardware.deleteLMHardwareBase( false );
					
					liteInvNew = new LiteStarsLMHardware();
					StarsLiteFactory.setLiteStarsLMHardware( (LiteStarsLMHardware)liteInvNew, hardware );
				}
				else {
					com.cannontech.database.data.stars.event.LMHardwareEvent.deleteAllLMHardwareEvents(new Integer(invID), conn);
					
					com.cannontech.database.data.stars.hardware.InventoryBase inventory =
							new com.cannontech.database.data.stars.hardware.InventoryBase();
					StarsLiteFactory.setInventoryBase( inventory.getInventoryBase(), liteInv );
					inventory.setDbConnection( conn );
					inventory.deleteInventoryBase( false );
					
					liteInvNew = new LiteInventoryBase();
					StarsLiteFactory.setLiteInventoryBase( liteInvNew, inventory.getInventoryBase() );
				}
				
				energyCompany.deleteInventory( invID );
				energyCompany.addInventory( liteInvNew );
			}
        }
    	
    	if (target != TARGET_ACCOUNT_DELETED) {
    		if (ECUtils.isLMHardware( liteInv.getCategoryID() )) {
				ArrayList liteApps = liteAcctInfo.getAppliances();
				
				for (int i = 0; i < liteApps.size(); i++) {
					LiteStarsAppliance liteApp = (LiteStarsAppliance) liteApps.get(i);
					
					if (liteApp.getInventoryID() == invID) {
						liteApp.setInventoryID( 0 );
						
						for (int j = 0; j < liteAcctInfo.getLmPrograms().size(); j++) {
							LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(j);
							
							if (liteProg.getLmProgram().getProgramID() == liteApp.getLmProgramID()) {
								liteProg.setGroupID( 0 );
								break;
							}
						}
					}
				}
    		}
    		
			liteAcctInfo.getInventories().remove( new Integer(invID) );
    		
			// Remove the account from two-way thermostat account list if necessary
			if (liteInv instanceof LiteStarsLMHardware &&
				((LiteStarsLMHardware) liteInv).isTwoWayThermostat() &&
				!liteAcctInfo.hasTwoWayThermostat(energyCompany))
			{
				ArrayList accountList = energyCompany.getAccountsWithGatewayEndDevice();
				synchronized (accountList) {
					accountList.remove( liteAcctInfo );
				}
			}
    	}
	}
	
	public static void parseResponse(int invID, StarsCustAccountInformation starsAcctInfo) {
		for (int i = 0; i < starsAcctInfo.getStarsAppliances().getStarsApplianceCount(); i++) {
			StarsAppliance appliance = starsAcctInfo.getStarsAppliances().getStarsAppliance(i);
			if (appliance.getInventoryID() == invID) {
				appliance.setInventoryID( 0 );
				for (int j = 0; j < starsAcctInfo.getStarsLMPrograms().getStarsLMProgramCount(); j++) {
					StarsLMProgram program = starsAcctInfo.getStarsLMPrograms().getStarsLMProgram(j);
					if (program.getProgramID() == appliance.getLmProgramID()) {
						program.setGroupID( 0 );
						break;
					}
				}
			}
		}
		
		StarsInventories inventories = starsAcctInfo.getStarsInventories();
		
		for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
			if (inventories.getStarsLMHardware(i).getInventoryID() == invID) {
				inventories.removeStarsLMHardware(i);
				return;
			}
		}
		
		for (int i = 0; i < inventories.getStarsMCTCount(); i++) {
			if (inventories.getStarsMCT(i).getInventoryID() == invID) {
				inventories.removeStarsMCT(i);
				return;
			}
		}
	}

}
