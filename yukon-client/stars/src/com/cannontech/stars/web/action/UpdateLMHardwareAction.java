package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.InventoryManager;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsAppliances;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
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
			
			StarsEnergyCompanySettings ecSettings =
					(StarsEnergyCompanySettings) user.getAttribute(ServletUtils.ATT_ENERGY_COMPANY_SETTINGS);
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
        catch (WebClientException we) {
        	session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, we.getMessage() );
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
            int origInvID = -1;
            
            if (deleteHw != null) {
            	// Build up request message for adding new hardware and preserving old hardware configuration
            	StarsCreateLMHardware createHw = (StarsCreateLMHardware)
            			StarsFactory.newStarsInv( updateHw, StarsCreateLMHardware.class );
            	
            	if (createHw.getLMHardware() != null) {
					for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
						LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
						if (liteApp.getInventoryID() == deleteHw.getInventoryID()) {
							StarsLMHardwareConfig starsConfig = new StarsLMHardwareConfig();
							starsConfig.setApplianceID( liteApp.getApplianceID() );
							starsConfig.setGroupID( liteApp.getAddressingGroupID() );
							createHw.getLMHardware().addStarsLMHardwareConfig( starsConfig );
						}
					}
            	}
            	
            	DeleteLMHardwareAction.removeInventory( deleteHw, liteAcctInfo, energyCompany );
            	
				liteInv = CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany );
				origInvID = deleteHw.getInventoryID();
            }
            else {
				liteInv = energyCompany.getInventory( updateHw.getInventoryID(), true );
				origInvID = updateHw.getInventoryID();
				
				if (liteInv.getAccountID() == 0) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, "The hardware doesn't belong to any customer account") );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
				
				if (ServerUtils.isOperator(user)) {
					updateInventory( updateHw, liteInv, energyCompany );
				}
				else {
					com.cannontech.database.db.stars.hardware.InventoryBase invDB =
							new com.cannontech.database.db.stars.hardware.InventoryBase();
					invDB.setDeviceLabel( updateHw.getDeviceLabel() );
					
					invDB = (com.cannontech.database.db.stars.hardware.InventoryBase)
							Transaction.createTransaction( Transaction.UPDATE, invDB ).execute();
					
					liteInv.setDeviceLabel( invDB.getDeviceLabel() );
				}
            }
            
			// Response will be handled here, instead of in parse()
			StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteInv.getAccountID() );
			if (starsAcctInfo != null) {
				StarsInventory starsInv = StarsLiteFactory.createStarsInventory(liteInv, energyCompany);
				parseResponse(origInvID, starsInv, starsAcctInfo, session);
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
	
	public static StarsOperation getRequestOperation(HttpServletRequest req, TimeZone tz) throws WebClientException {
		StarsOperation operation = new StarsOperation();
		StarsUpdateLMHardware updateHw = new StarsUpdateLMHardware();
		
		if (req.getParameter("DeviceID") != null) {
			// This comes from operator side
			InventoryManager.setStarsInv( updateHw, req, tz );
			
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
	
	public static void updateInventory(StarsUpdateLMHardware updateHw, LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany)
		throws WebClientException
	{
		try {
			if (liteInv instanceof LiteStarsLMHardware) {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteInv;
				String serialNo = updateHw.getLMHardware().getManufacturerSerialNumber();
				
				if (serialNo.equals(""))
					throw new WebClientException( "Serial # cannot be empty" );
				
				try {
					if (!liteHw.getManufacturerSerialNumber().equals(serialNo) &&
						energyCompany.searchForLMHardware(updateHw.getDeviceType().getEntryID(), serialNo) != null)
						throw new WebClientException( "Serial # already exists" );
				}
				catch (ObjectInOtherEnergyCompanyException e) {
					throw new WebClientException( "The hardware is found in another energy company. Please contact " + energyCompany.getParent().getName() + " for more information." );
				}
				
				com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
						new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				StarsLiteFactory.setLMHardwareBase( hw, liteHw );
				
				hw.getLMHardwareBase().setManufacturerSerialNumber( serialNo );
				StarsFactory.setInventoryBase( hw.getInventoryBase(), updateHw );
				
				hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
						Transaction.createTransaction( Transaction.UPDATE, hw ).execute();
				
				StarsLiteFactory.setLiteStarsLMHardware( liteHw, hw );
			}
			else {
				com.cannontech.database.db.stars.hardware.InventoryBase invDB =
						new com.cannontech.database.db.stars.hardware.InventoryBase();
				StarsLiteFactory.setInventoryBase( invDB, liteInv );
				StarsFactory.setInventoryBase( invDB, updateHw );
				
				invDB = (com.cannontech.database.db.stars.hardware.InventoryBase)
						Transaction.createTransaction( Transaction.UPDATE, invDB ).execute();
				
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
						
						eventDB = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
								Transaction.createTransaction( Transaction.UPDATE, eventDB ).execute();
	            		
						StarsLiteFactory.setLiteLMCustomerEvent( liteEvent, eventDB );
					}
					
					break;
				}
			}
		}
		catch (TransactionException e) {
			e.printStackTrace();
			throw new WebClientException( "Failed to update the hardware information" );
		}
	}
	
	private void parseResponse(int origInvID, StarsInventory starsInv, StarsCustAccountInformation starsAcctInfo, HttpSession session) {
		StarsInventories starsInvs = starsAcctInfo.getStarsInventories();
		
		for (int i = 0; i < starsInvs.getStarsInventoryCount(); i++) {
			if (starsInvs.getStarsInventory(i).getInventoryID() == origInvID) {
				starsInvs.removeStarsInventory(i);
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
		
		int invNo = 0;
		for (; invNo < starsInvs.getStarsInventoryCount(); invNo++) {
			StarsInventory inv = starsInvs.getStarsInventory(invNo);
			if (inv.getDeviceLabel().compareTo(starsInv.getDeviceLabel()) > 0)
				break;
		}
		
		starsInvs.addStarsInventory( invNo, starsInv );
		
		String redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
		if (redirect != null) {
			// redirect should ends with "InvNo=X" or "Item=X", replace "X" with the new location
			int pos = redirect.lastIndexOf( "InvNo=" );
			if (pos >= 0) {
				// Request from Inventory.jsp
				session.setAttribute(ServletUtils.ATT_REDIRECT, redirect.substring(0, pos+6) + invNo);
			}
			else {
				pos = redirect.lastIndexOf( "Item=" );
				if (pos >= 0) {
					// Request from NewLabel.jsp, only count thermostats
					int itemNo = 0;
					for (int i = 0; i < invNo; i++) {
						StarsInventory inv = starsInvs.getStarsInventory(i);
						if (inv.getLMHardware() != null && inv.getLMHardware().getStarsThermostatSettings() != null)
							itemNo++;
					}
					
					session.setAttribute(ServletUtils.ATT_REDIRECT, redirect.substring(0, pos+5) + itemNo);
				}
			}
		}
	}

}
