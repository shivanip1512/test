package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
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
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMHardware;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardware;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareResponse;
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

			StarsOperation operation = null;
			if (req.getParameter("InvID") != null) {
				// Request from Inventory.jsp or ChangeLabel.jsp
				operation = getRequestOperation( req );
			}
			else {
				// Request redirected from InventoryManager
				operation = (StarsOperation) session.getAttribute(InventoryManager.STARS_OPERATION_REQUEST);
				session.removeAttribute( InventoryManager.STARS_OPERATION_REQUEST );
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
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        	conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
            
            StarsUpdateLMHardware updateHw = reqOper.getStarsUpdateLMHardware();
            StarsDeleteLMHardware deleteHw = reqOper.getStarsDeleteLMHardware();
            LiteStarsLMHardware liteHw = null;
            
            if (deleteHw != null) {
            	// Build up request message for adding the new hardware and save the old hardware configuration
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
            	
            	DeleteLMHardwareAction.removeLMHardware(
						deleteHw.getInventoryID(), deleteHw.getDeleteFromInventory(), liteAcctInfo, null, energyCompany, conn );
				liteHw = CreateLMHardwareAction.addLMHardware( createHw, liteAcctInfo, energyCompany, conn );
            }
            else {
				liteHw = energyCompany.getLMHardware( updateHw.getInventoryID(), true );
				
				com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
						(com.cannontech.database.data.stars.hardware.LMHardwareBase) StarsLiteFactory.createDBPersistent( liteHw );
				
				boolean fromOperator = (updateHw.getManufactureSerialNumber() != null);
				if (fromOperator) {
					hw.getLMHardwareBase().setManufacturerSerialNumber( updateHw.getManufactureSerialNumber() );
					hw.getInventoryBase().setAlternateTrackingNumber( updateHw.getAltTrackingNumber() );
					if (updateHw.getInstallDate() != null)
						hw.getInventoryBase().setInstallDate( updateHw.getInstallDate() );
					else
						hw.getInventoryBase().setInstallDate( new Date(0) );
					if (updateHw.getReceiveDate() != null)
						hw.getInventoryBase().setReceiveDate( updateHw.getReceiveDate() );
					else
						hw.getInventoryBase().setReceiveDate( new Date(0) );
					if (updateHw.getRemoveDate() != null)
						hw.getInventoryBase().setRemoveDate( updateHw.getRemoveDate() );
					else
						hw.getInventoryBase().setRemoveDate( new Date(0) );
					hw.getInventoryBase().setNotes( updateHw.getNotes() );
					hw.getInventoryBase().setInstallationCompanyID( new Integer(updateHw.getInstallationCompany().getEntryID()) );
					if (updateHw.getDeviceLabel().trim().length() > 0)
						hw.getInventoryBase().setDeviceLabel( updateHw.getDeviceLabel() );
					else
						hw.getInventoryBase().setDeviceLabel( updateHw.getManufactureSerialNumber() );
				}
				else {
					if (updateHw.getDeviceLabel().trim().length() > 0)
						hw.getInventoryBase().setDeviceLabel( updateHw.getDeviceLabel() );
					else
						hw.getInventoryBase().setDeviceLabel( liteHw.getManufactureSerialNumber() );
				}
				
				hw.setDbConnection( conn );
				hw.update();
				
				StarsLiteFactory.setLiteLMHardwareBase( liteHw, hw );
				
				if (fromOperator) {
					// Update the "install" event if necessary
					int installEntryID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL).getEntryID();
					
					ArrayList hwHist = liteHw.getLmHardwareHistory();
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
            }
            
            StarsUpdateLMHardwareResponse resp = new StarsUpdateLMHardwareResponse();
            resp.setStarsLMHardware( StarsLiteFactory.createStarsLMHardware(liteHw, energyCompany) );
            
            respOper.setStarsUpdateLMHardwareResponse( resp );
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
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            if (accountInfo == null)
            	return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
			
			StarsLMHardware hw = respOper.getStarsUpdateLMHardwareResponse().getStarsLMHardware();
			int origInvID = (reqOper.getStarsDeleteLMHardware() == null)?
					hw.getInventoryID() : reqOper.getStarsDeleteLMHardware().getInventoryID();
			
			StarsInventories starsInvs = accountInfo.getStarsInventories();
			for (int i = 0; i < starsInvs.getStarsLMHardwareCount(); i++) {
				if (starsInvs.getStarsLMHardware(i).getInventoryID() == origInvID) {
					if (hw.getInventoryID() == origInvID) {
						starsInvs.setStarsLMHardware(i, hw);
					}
					else {
						starsInvs.removeStarsLMHardware(i);
						int idx = 0;
						for (; idx < starsInvs.getStarsLMHardwareCount(); idx++) {
							StarsLMHardware starsHw = starsInvs.getStarsLMHardware(idx);
							if (starsHw.getDeviceLabel().compareTo( hw.getDeviceLabel() ) > 0)
								break;
						}
						starsInvs.addStarsLMHardware( idx, hw );
						
						String redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
						// redirect should ends with "InvNo=X" or "Item=X", replace "X" with the new location
						int pos = redirect.lastIndexOf( '=' );
						session.setAttribute(ServletUtils.ATT_REDIRECT, redirect.substring(0, pos+1) + idx);
						
						StarsAppliances starsApps = accountInfo.getStarsAppliances();
						for (int j = 0; j < starsApps.getStarsApplianceCount(); j++) {
							if (starsApps.getStarsAppliance(j).getInventoryID() == origInvID)
								starsApps.getStarsAppliance(j).setInventoryID( hw.getInventoryID() );
						}
					}
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
	
	public static StarsOperation getRequestOperation(HttpServletRequest req) {
		StarsOperation operation = new StarsOperation();
		StarsUpdateLMHardware updateHw = new StarsUpdateLMHardware();
		
		if (req.getParameter("SerialNo") != null) {
			// This comes from operator side
			InventoryManager.setStarsLMHardware( updateHw, req );
			
			int origInvID = Integer.parseInt( req.getParameter("OrigInvID") );
			if (origInvID != updateHw.getInventoryID()) {
				StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
				deleteHw.setInventoryID( origInvID );
				operation.setStarsDeleteLMHardware( deleteHw );
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

}
