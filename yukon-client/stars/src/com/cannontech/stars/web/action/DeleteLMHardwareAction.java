package com.cannontech.stars.web.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServerUtils;
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

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsOperation operation = (StarsOperation) session.getAttribute(InventoryManager.STARS_OPERATION_REQUEST);
			session.removeAttribute( InventoryManager.STARS_OPERATION_REQUEST );
			if (operation == null)
				operation = getRequestOperation( req );
			
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
            
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			if (liteAcctInfo == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
        	
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
        	
			StarsDeleteLMHardware delHw = reqOper.getStarsDeleteLMHardware();
			removeLMHardware( delHw.getInventoryID(), delHw.getDeleteFromInventory(), liteAcctInfo, null, energyCompany, conn );
        	
			StarsSuccess success = new StarsSuccess();
			success.setDescription("Hardware deleted successfully");
            
			respOper.setStarsSuccess( success );
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
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			if (accountInfo == null)
				return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
			
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			parseResponse( reqOper.getStarsDeleteLMHardware().getInventoryID(), accountInfo );
			
			return 0;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static StarsOperation getRequestOperation(HttpServletRequest req) {
		StarsDeleteLMHardware delHw = new StarsDeleteLMHardware();
		delHw.setInventoryID( Integer.parseInt(req.getParameter("OrigInvID")) );
			
		StarsOperation operation = new StarsOperation();
		operation.setStarsDeleteLMHardware( delHw );
		return operation;
	}
	
	/**
	 * Remove a hardware from a customer account
	 * @param invID Inventory ID of the hardware.
	 * @param deletePerm Whether to delete the hardware from inventory permanantly.
	 * If the value is true, parameter nextAccount will be ignored.
	 * @param liteAcctInfo Lite object of the account the hardware is to be removed from.
	 * @param nextAccount Lite object of the account the hardware is to be moved to.
	 * If the hardware is to be moved to the warehouse, set the parameter value to null. Otherwise,
	 * the database update will be withheld until the hardware is added to the next account.
	 * @param energyCompany Lite object of the energy company the hardware belongs to.
	 * @param conn Database connection.
	 * @throws java.sql.SQLException
	 */
	public static void removeLMHardware(int invID, boolean deletePerm, LiteStarsCustAccountInformation liteAcctInfo,
	LiteStarsCustAccountInformation nextAccount, LiteStarsEnergyCompany energyCompany, java.sql.Connection conn)
	throws java.sql.SQLException
	{
		boolean foundHardware = false;
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			if (((Integer) liteAcctInfo.getInventories().get(i)).intValue() == invID) {
				foundHardware = true;
				break;
			}
		}
		if (!foundHardware) return;
    	
		LiteStarsLMHardware liteHw = energyCompany.getLMHardware( invID, true );
        
        if (deletePerm) {
        	com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
        			new com.cannontech.database.data.stars.hardware.LMHardwareBase();
        	hardware.setInventoryID( new Integer(invID) );
        	hardware.setDbConnection( conn );
        	hardware.delete();
        	
        	energyCompany.deleteLMHardware( invID );
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
			String notes = "Move hardware from account #" + liteAcctInfo.getCustomerAccount().getAccountNumber();
			if (nextAccount == null)
				notes += " to warehouse";
			else
				notes += " to account #" + nextAccount.getCustomerAccount().getAccountNumber();
			eventBaseDB.setNotes( notes );
			eventDB.setInventoryID( new Integer(invID) );
			event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
			event.setDbConnection( conn );
			event.add();
    	
			if (nextAccount == null) {
				com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
						(com.cannontech.database.data.stars.hardware.LMHardwareBase) StarsLiteFactory.createDBPersistent(liteHw);
				hw.setDbConnection( conn );
				hw.deleteLMHardwareBase( false );
			
				StarsLiteFactory.setLiteLMHardwareBase( liteHw, hw );
			}
        }
    	
		ArrayList liteApps = liteAcctInfo.getAppliances();
		for (int i = 0; i < liteApps.size(); i++) {
			LiteStarsAppliance liteApp = (LiteStarsAppliance) liteApps.get(i);
			if (liteApp.getInventoryID() == liteHw.getInventoryID()) {
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
    	
		liteAcctInfo.getInventories().remove( new Integer(invID) );
    	
		// Remove the account from two-way thermostat account list if necessary
		if (liteHw.isTwoWayThermostat() &&
			!ServerUtils.hasTwoWayThermostat(liteAcctInfo, energyCompany))
		{
			ArrayList accountList = energyCompany.getAccountsWithGatewayEndDevice();
			synchronized (accountList) {
				accountList.remove( liteAcctInfo );
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
				break;
			}
		}
	}

}
