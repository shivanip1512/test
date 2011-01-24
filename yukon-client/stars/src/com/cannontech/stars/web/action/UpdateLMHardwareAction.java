package com.cannontech.stars.web.action;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.service.StarsTwoWayLcrYukonDeviceAssignmentService;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceAssignmentException;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsAppliances;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsFailure;
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
    private static final LMHardwareEventDao hardwareEventDao = YukonSpringHook.getBean("hardwareEventDao", LMHardwareEventDao.class);
    
    private static final StarsSearchDao starsSearchDao =  YukonSpringHook.getBean("starsSearchDao", StarsSearchDao.class);
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
				LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
				operation = getRequestOperation( req, energyCompany );
			}
			else {
				// Request redirected from InventoryManager
				operation = (StarsOperation) session.getAttribute(InventoryManagerUtil.STARS_INVENTORY_OPERATION);
				session.removeAttribute( InventoryManagerUtil.STARS_INVENTORY_OPERATION );
			}
			
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (WebClientException we) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, we.getMessage() );
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
            
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            
			StarsUpdateLMHardware updateHw = reqOper.getStarsUpdateLMHardware();
			StarsDeleteLMHardware deleteHw = reqOper.getStarsDeleteLMHardware();
            
			LiteInventoryBase liteInv = null;
			int origInvID = -1;
            
			if (deleteHw != null) {
				try {
					liteInv = updateInventory( updateHw, deleteHw, liteAcctInfo, user );
					origInvID = deleteHw.getInventoryID();
				}
				catch (WebClientException e) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
			}
			else {
                try {
                    liteInv = starsSearchDao.getById(updateHw.getInventoryID(), energyCompany);
                } catch (ObjectInOtherEnergyCompanyException e) {
                    throw new WebClientException("The hardware is found in another energy company [" + e.getYukonEnergyCompany().getName() + "]");
                }			    
				origInvID = updateHw.getInventoryID();
				
				if (liteInv.getAccountID() == 0) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, "The hardware doesn't belong to any customer account") );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
				
				if (StarsUtils.isOperator(user.getYukonUser())) {
					try {
						updateInventory( updateHw, liteInv, energyCompany );
					}
					catch (WebClientException e) {
						respOper.setStarsFailure( StarsFactory.newStarsFailure(
								StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
						return SOAPUtil.buildSOAPMessage( respOper );
					}
				}
				else {
					com.cannontech.database.db.stars.hardware.InventoryBase invDB =
							new com.cannontech.database.db.stars.hardware.InventoryBase();
					StarsLiteFactory.setInventoryBase( invDB, liteInv );
					invDB.setDeviceLabel( updateHw.getDeviceLabel() );
					
					invDB = Transaction.createTransaction( Transaction.UPDATE, invDB ).execute();
					
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
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update the hardware information. " + e.getMessage()));
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
			StarsOperation respOper = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = respOper.getStarsFailure();
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
	
	public static StarsOperation getRequestOperation(HttpServletRequest req, LiteStarsEnergyCompany energyCompany) throws WebClientException {
		StarsOperation operation = new StarsOperation();
		StarsUpdateLMHardware updateHw = new StarsUpdateLMHardware();
		
		if (req.getParameter("DeviceID") != null) {
			// This comes from operator side
			InventoryManagerUtil.setStarsInv( updateHw, req, energyCompany );
			
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
		    //verify inventory exists in the same energyCompany
            starsSearchDao.getById(updateHw.getInventoryID(), energyCompany);
            
			if (liteInv instanceof LiteStarsLMHardware) {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteInv;
				String serialNo = updateHw.getLMHardware().getManufacturerSerialNumber();
				
				if (serialNo.equals(""))
					throw new WebClientException( "Failed to update the hardware, serial # cannot be empty" );
				
				if (!liteHw.getManufacturerSerialNumber().equals(serialNo) &&
				        starsSearchDao.searchLMHardwareBySerialNumber(serialNo, energyCompany) != null) {
				    throw new WebClientException( "Failed to update the hardware, serial # already exists" );
				}

				com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
						new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				StarsLiteFactory.setLMHardwareBase( hw, liteHw );
				
				hw.getLMHardwareBase().setManufacturerSerialNumber( serialNo );
				hw.getLMHardwareBase().setRouteID( new Integer(updateHw.getLMHardware().getRouteID()) );
				StarsFactory.setInventoryBase( hw.getInventoryBase(), updateHw );
				
				hw = Transaction.createTransaction( Transaction.UPDATE, hw ).execute();
				
				StarsLiteFactory.setLiteStarsLMHardware( liteHw, hw );
			}
			else {
				com.cannontech.database.db.stars.hardware.InventoryBase invDB =
						new com.cannontech.database.db.stars.hardware.InventoryBase();
				StarsLiteFactory.setInventoryBase( invDB, liteInv );
				StarsFactory.setInventoryBase( invDB, updateHw );
				
				invDB = Transaction.createTransaction( Transaction.UPDATE, invDB ).execute();
				
				StarsLiteFactory.setLiteInventoryBase( liteInv, invDB );
			}
			
			// Update the "install" event if necessary
			int installEntryID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL).getEntryID();
			
			List<LiteLMHardwareEvent> hwHist = hardwareEventDao.getByInventoryId(liteInv.getLiteID());
			for (LiteLMHardwareEvent liteEvent : hwHist) {
				
				if (liteEvent.getActionID() == installEntryID) {
					if (updateHw.getInstallDate() != null &&
						!isDateEqual( new Date(liteEvent.getEventDateTime()), updateHw.getInstallDate() )
						|| liteEvent.getNotes() == null || !liteEvent.getNotes().equals( updateHw.getInstallationNotes() ))
					{
						com.cannontech.database.data.stars.event.LMHardwareEvent event =
								(com.cannontech.database.data.stars.event.LMHardwareEvent) StarsLiteFactory.createDBPersistent( liteEvent );
						com.cannontech.database.db.stars.event.LMCustomerEventBase eventDB = event.getLMCustomerEventBase();
						if (updateHw.getInstallDate() != null)
							eventDB.setEventDateTime( updateHw.getInstallDate() );
						eventDB.setNotes( updateHw.getInstallationNotes() );
						
						eventDB = Transaction.createTransaction( Transaction.UPDATE, eventDB ).execute();
	            		
						StarsLiteFactory.setLiteLMCustomerEvent( liteEvent, eventDB );
					}
					
					break;
				}
			}
			
			// TWO WAY LCR DEVICE ASSIGNMENT
            if (InventoryUtils.isTwoWayLcr(updateHw.getDeviceType().getEntryID())) {
            	StarsTwoWayLcrYukonDeviceAssignmentService starsTwoWayLcrYukonDeviceAssignmentService = YukonSpringHook.getBean("starsTwoWayLcrYukonDeviceAssignmentService", StarsTwoWayLcrYukonDeviceAssignmentService.class);
            	starsTwoWayLcrYukonDeviceAssignmentService.assignTwoWayLcrDevice(updateHw, liteInv, energyCompany);
            }
            
		}
		catch (TransactionException e) {
			CTILogger.error( e.getMessage(), e );
			throw new WebClientException( "Failed to update the hardware information" );
		} catch (StarsTwoWayLcrYukonDeviceAssignmentException e) {
			throw new WebClientException(e.getMessage(), e);
        } catch (ObjectInOtherEnergyCompanyException e) {
            throw new WebClientException("The hardware is found in another energy company [" + e.getYukonEnergyCompany().getName() + "]");
        }
	}
	
	public static LiteInventoryBase updateInventory(StarsUpdateLMHardware updateHw, StarsDeleteLMHardware deleteHw,
		LiteStarsCustAccountInformation liteAcctInfo, StarsYukonUser user) throws WebClientException
	{
	    
	    EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao = 
	        YukonSpringHook.getBean("energyCompanyRolePropertyDao", EnergyCompanyRolePropertyDao.class);
	    
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		// Build up request message for adding new hardware and preserving old hardware configuration
		StarsCreateLMHardware createHw = (StarsCreateLMHardware)
				StarsFactory.newStarsInv( updateHw, StarsCreateLMHardware.class );
        
		if (createHw.getLMHardware() != null) {
			for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
				LiteStarsAppliance liteApp = liteAcctInfo.getAppliances().get(i);
				if (liteApp.getInventoryID() == deleteHw.getInventoryID()) {
					StarsLMHardwareConfig starsConfig = new StarsLMHardwareConfig();
					starsConfig.setApplianceID( liteApp.getApplianceID() );
					starsConfig.setGroupID( liteApp.getAddressingGroupID() );
					starsConfig.setLoadNumber( liteApp.getLoadNumber() );
					createHw.getLMHardware().addStarsLMHardwareConfig( starsConfig );
				}
			}
		}
        	
		DeleteLMHardwareAction.removeInventory( deleteHw, liteAcctInfo, energyCompany );
        
		LiteInventoryBase liteInv = CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany );
		
		// Send out the configuration command if necessary
		boolean trackHardwareAddressingEnabled =
		    energyCompanyRolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.TRACK_HARDWARE_ADDRESSING, energyCompany);
		boolean automaticConfigurationEnabled = 
		    energyCompanyRolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.AUTOMATIC_CONFIGURATION, energyCompany);
		
		
		if (!trackHardwareAddressingEnabled && automaticConfigurationEnabled &&
			createHw.getLMHardware() != null && createHw.getLMHardware().getStarsLMHardwareConfigCount() > 0) {
			YukonSwitchCommandAction.sendConfigCommand( energyCompany, (LiteStarsLMHardware)liteInv, false, null );
		}
		
		return liteInv;
	}
	
	public static void parseResponse(int origInvID, StarsInventory starsInv, StarsCustAccountInformation starsAcctInfo, HttpSession session) {
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
		
		String deviceLabel = ServletUtils.getInventoryLabel( starsInv );
		int invNo = 0;
		for (; invNo < starsInvs.getStarsInventoryCount(); invNo++) {
			String label = ServletUtils.getInventoryLabel( starsInvs.getStarsInventory(invNo) );
			if (label.compareTo(deviceLabel) > 0)
				break;
		}
		
		starsInvs.addStarsInventory( invNo, starsInv );
		
		if (session != null) {
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
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (StarsUtils.isOperator( user.getYukonUser() ))
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Hardware information updated successfully" );
			else
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Thermostat name updated successfully" );
		}
	}

	private static boolean isDateEqual(Date date1, Date date2) {
        long time1 = (date1 != null)? date1.getTime() : System.currentTimeMillis();
        long time2 = (date2 != null)? date2.getTime() : System.currentTimeMillis();
        return Math.abs(time1 - time2) <= 1000;
    }
}
