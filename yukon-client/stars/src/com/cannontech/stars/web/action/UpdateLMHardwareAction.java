package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.InventoryManager;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsAppliances;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMHardware;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsMCT;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardware;
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
public class UpdateLMHardwareAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute(ServletUtils.ATT_ENERGY_COMPANY_SETTINGS);
			TimeZone tz = TimeZone.getTimeZone( ecSettings.getStarsEnergyCompany().getTimeZone() );
			if (tz == null) tz = TimeZone.getDefault();

			StarsOperation operation = null;
			if (req.getParameter("InvID") != null) {
				// Request from Inventory.jsp or ChangeLabel.jsp
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
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			
        	conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
            
            StarsUpdateLMHardware updateHw = reqOper.getStarsUpdateLMHardware();
            StarsDeleteLMHardware deleteHw = reqOper.getStarsDeleteLMHardware();
            
            LiteInventoryBase liteInv = null;
            
            if (deleteHw != null) {
            	// Build up request message for adding new hardware and preserving old hardware configuration
            	StarsCreateLMHardware createHw = (StarsCreateLMHardware)
            			StarsFactory.newStarsLMHw( updateHw, StarsCreateLMHardware.class );
            	
            	for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
            		LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
            		if (liteApp.getInventoryID() == deleteHw.getInventoryID()) {
						StarsLMHardwareConfig starsConfig = new StarsLMHardwareConfig();
						starsConfig.setApplianceID( liteApp.getApplianceID() );
						starsConfig.setGroupID( liteApp.getAddressingGroupID() );
						createHw.addStarsLMHardwareConfig( starsConfig );
            		}
            	}
            	
            	int target = deleteHw.getDeleteFromInventory()?
            			DeleteLMHardwareAction.TARGET_ACCOUNT_DELETED :
            			DeleteLMHardwareAction.TARGET_TO_WAREHOUSE;
            	DeleteLMHardwareAction.removeInventory(deleteHw.getInventoryID(), liteAcctInfo, energyCompany, target, conn);
            	
				liteInv = CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany, conn );
            }
            else {
				liteInv = energyCompany.getInventory( updateHw.getInventoryID(), true );
				
				if (liteInv.getAccountID() == 0) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_SESSION_INVALID, "The hardware doesn't belong to any customer account") );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
				
				if (ServerUtils.isOperator(user)) {
					updateInventory(updateHw, liteInv, energyCompany, conn);
				}
				else {
					com.cannontech.database.db.stars.hardware.InventoryBase invDB =
							new com.cannontech.database.db.stars.hardware.InventoryBase();
					StarsLiteFactory.setInventoryBase( invDB, liteInv );
					
					if (updateHw.getDeviceLabel().trim().length() > 0)
						invDB.setDeviceLabel( updateHw.getDeviceLabel() );
					else
						invDB.setDeviceLabel( ((LiteStarsLMHardware)liteInv).getManufactureSerialNumber() );
				
					invDB.setDbConnection( conn );
					invDB.update();
					
					liteInv.setDeviceLabel( invDB.getDeviceLabel() );
				}
            }
            
			// Response will be handled here, instead of in parse()
			StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteInv.getAccountID() );
			if (starsAcctInfo != null) {
				StarsInventory starsInv = StarsLiteFactory.createStarsInventory(liteInv, energyCompany);
				parseResponse(liteInv.getInventoryID(), starsInv, starsAcctInfo, session);
			}
            
            respOper.setStarsSuccess( new StarsSuccess() );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update the hardware information") );
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
        	StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = respOper.getStarsFailure();
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
	
	public static StarsOperation getRequestOperation(HttpServletRequest req, TimeZone tz) {
		StarsOperation operation = new StarsOperation();
		StarsUpdateLMHardware updateHw = new StarsUpdateLMHardware();
		
		if (req.getParameter("SerialNo") != null) {
			// This comes from operator side
			InventoryManager.setStarsInventory( updateHw, req, tz );
			
			if (req.getParameter("OrigInvID") != null) {
				int origInvID = Integer.parseInt( req.getParameter("OrigInvID") );
				if (origInvID != updateHw.getInventoryID()) {
					StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
					deleteHw.setInventoryID( origInvID );
					operation.setStarsDeleteLMHardware( deleteHw );
				}
			}
		}
		else {
			// This comes from consumer side
			updateHw.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
			updateHw.setDeviceLabel( req.getParameter("DeviceLabel") );
		}
			
		operation.setStarsUpdateLMHardware( updateHw );
		return operation;
	}
	
	public static void updateInventory(StarsUpdateLMHardware updateHw, LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany, java.sql.Connection conn)
	throws java.sql.SQLException {
		if (liteInv instanceof LiteStarsLMHardware) {
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteInv;
			
			com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
					new com.cannontech.database.data.stars.hardware.LMHardwareBase();
			StarsLiteFactory.setLMHardwareBase( hw, liteHw );
			
			hw.getLMHardwareBase().setManufacturerSerialNumber( updateHw.getManufactureSerialNumber() );
			StarsFactory.setInventoryBase( hw.getInventoryBase(), updateHw );
			
			hw.setDbConnection( conn );
			hw.update();
			
			StarsLiteFactory.setLiteStarsLMHardware( liteHw, hw );
		}
		else {
			com.cannontech.database.db.stars.hardware.InventoryBase invDB =
					new com.cannontech.database.db.stars.hardware.InventoryBase();
			StarsLiteFactory.setInventoryBase( invDB, liteInv );
			StarsFactory.setInventoryBase( invDB, updateHw );
			
			invDB.setDbConnection( conn );
			invDB.update();
			
			StarsLiteFactory.setLiteInventoryBase( liteInv, invDB );
		}
		
		// Update the "install" event if necessary
		int installEntryID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL).getEntryID();
					
		ArrayList hwHist = liteInv.getInventoryHistory();
		for (int i = hwHist.size() - 1; i >= 0; i--) {
			LiteLMHardwareEvent liteEvent = (LiteLMHardwareEvent) hwHist.get(i);
			
			if (liteEvent.getActionID() == installEntryID) {
				if (!liteEvent.getNotes().equals( updateHw.getInstallationNotes() )) {
					com.cannontech.database.data.stars.event.LMHardwareEvent event =
							(com.cannontech.database.data.stars.event.LMHardwareEvent) StarsLiteFactory.createDBPersistent( liteEvent );
					com.cannontech.database.db.stars.event.LMCustomerEventBase eventDB = event.getLMCustomerEventBase();
								
					eventDB.setNotes( updateHw.getInstallationNotes() );
					eventDB.setDbConnection( conn );
					eventDB.update();
	            					
					StarsLiteFactory.setLiteLMCustomerEvent( liteEvent, eventDB );
				}
				
				break;
			}
		}
	}
	
	private void parseResponse(int origInvID, StarsInventory starsInv, StarsCustAccountInformation starsAcctInfo, HttpSession session) {
		StarsInventories starsInvs = starsAcctInfo.getStarsInventories();
		int index = 0;
		
		if (starsInv instanceof StarsLMHardware) {
			for (int i = 0; i < starsInvs.getStarsLMHardwareCount(); i++) {
				if (starsInvs.getStarsLMHardware(i).getInventoryID() == origInvID) {
					starsInvs.removeStarsLMHardware(i);
					break;
				}
				
			}
			
			StarsAppliances starsApps = starsAcctInfo.getStarsAppliances();
			if (starsApps != null) {
				for (int i = 0; i < starsApps.getStarsApplianceCount(); i++) {
					if (starsApps.getStarsAppliance(i).getInventoryID() == origInvID)
						starsApps.getStarsAppliance(i).setInventoryID( starsInv.getInventoryID() );
				}
			}
			
			int idx = 0;
			for (; idx < starsInvs.getStarsLMHardwareCount(); idx++) {
				StarsLMHardware hw = starsInvs.getStarsLMHardware(idx);
				if (hw.getDeviceLabel().compareTo( starsInv.getDeviceLabel() ) > 0)
					break;
			}
			
			starsInvs.addStarsLMHardware( idx, (StarsLMHardware)starsInv );
			index = idx;
		}
		else if (starsInv instanceof StarsMCT) {
			for (int i = 0; i < starsInvs.getStarsMCTCount(); i++) {
				if (starsInvs.getStarsMCT(i).getInventoryID() == origInvID) {
					starsInvs.removeStarsMCT(i);
					break;
				}
			}
			
			int idx = 0;
			for (; idx < starsInvs.getStarsMCTCount(); idx++) {
				StarsMCT mct = starsInvs.getStarsMCT(idx);
				if (mct.getDeviceLabel().compareTo( starsInv.getDeviceLabel() ) > 0)
					break;
			}
			
			starsInvs.addStarsMCT( idx, (StarsMCT)starsInv );
			index = starsInvs.getStarsLMHardwareCount() + idx;
		}
		
		String redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
		if (redirect.indexOf("InvId=") < 0) {
			// redirect should ends with "InvNo=X" or "Item=X", replace "X" with the new location
			int pos = redirect.lastIndexOf( '=' );
			session.setAttribute(ServletUtils.ATT_REDIRECT, redirect.substring(0, pos+1) + index);
		}
	}

}
