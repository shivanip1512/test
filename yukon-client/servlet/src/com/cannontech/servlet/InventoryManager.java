package com.cannontech.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.dbeditor.DBDeletionFuncs;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.AddSNRangeTask;
import com.cannontech.stars.util.task.ConfigSNRangeTask;
import com.cannontech.stars.util.task.DeleteSNRangeTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.util.task.UpdateSNRangeTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareConfigAction;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.DeviceType;
import com.cannontech.stars.xml.serialize.LMHardware;
import com.cannontech.stars.xml.serialize.MCT;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardware;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class InventoryManager extends HttpServlet {
	
	private String action = null;
	private String referer = null;
	private String redirect = null;

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
	{
		HttpSession session = req.getSession(false);
		if (session == null) {
			resp.sendRedirect( req.getContextPath() + SOAPClient.LOGIN_URL ); return;
		}
        
		StarsYukonUser user = (StarsYukonUser)
				session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		if (user == null) {
			resp.sendRedirect( req.getContextPath() + SOAPClient.LOGIN_URL );
			return;
		}
        
		referer = req.getParameter( ServletUtils.ATT_REFERRER );
		if (referer == null) referer = req.getHeader( "referer" );
		redirect = req.getParameter( ServletUtils.ATT_REDIRECT );
		if (redirect == null) redirect = referer;
		
		action = req.getParameter( "action" );
		if (action == null) action = "";
		
		if (action.equalsIgnoreCase( "SelectInventory" ))
			selectInventory( user, req, session );
		else if (action.equalsIgnoreCase( "SelectDevice" ))
			selectDevice( user, req, session );
		else if (action.equalsIgnoreCase( "CheckInventory" )) {
			session.setAttribute( ServletUtils.ATT_REDIRECT, redirect );
			//session.setAttribute( ServletUtils.ATT_REFERRER, referer );
			checkInventory( user, req, session );
		}
		else if (action.equalsIgnoreCase("CreateLMHardware")) {
			String redir = req.getContextPath() + "/servlet/SOAPClient?action=" + action;
			if (req.getParameter("Wizard") != null) redir += "&Wizard=true";
			session.setAttribute( ServletUtils.ATT_REDIRECT, redir );
			checkInventory( user, req, session );
		}
		else if (action.equalsIgnoreCase("UpdateLMHardware")) {
			String redir = req.getContextPath() + "/servlet/SOAPClient?action=" + action +
					"&REDIRECT=" + req.getParameter(ServletUtils.ATT_REDIRECT) +
					"&REFERRER=" + req.getParameter(ServletUtils.ATT_REFERRER);
			session.setAttribute( ServletUtils.ATT_REDIRECT, redir );
			checkInventory( user, req, session );
		}
		else if (action.equalsIgnoreCase("DeleteLMHardware")) {
			String redir = req.getContextPath() + "/servlet/SOAPClient?action=" + action +
					"&REDIRECT=" + req.getParameter(ServletUtils.ATT_REDIRECT) +
					"&REFERRER=" + req.getParameter(ServletUtils.ATT_REFERRER);
			session.setAttribute( ServletUtils.ATT_REDIRECT, redir );
			deleteLMHardware( user, req, session );
		}
		else if (action.equalsIgnoreCase("UpdateInventory"))
			updateInventory( user, req, session );
		else if (action.equalsIgnoreCase("DeleteInventory")) {
			String redir = req.getContextPath() + "/servlet/SOAPClient?action=DeleteLMHardware" +
					"&REDIRECT=" + req.getParameter(ServletUtils.ATT_REDIRECT) +
					"&REFERRER=" + req.getParameter(ServletUtils.ATT_REFERRER);
			session.setAttribute( ServletUtils.ATT_REDIRECT, redir );
			deleteInventory( user, req, session );
		}
		else if (action.equalsIgnoreCase("ConfigHardware"))
			configLMHardware( user, req, session );
		else if (action.equalsIgnoreCase("ConfirmCheck"))
			confirmCheck( user, req, session );
		else if (action.equalsIgnoreCase("ConfirmDelete"))
			confirmDelete( user, req, session );
		else if (action.equalsIgnoreCase("AddSNRange"))
			addSNRange( user, req, session );
		else if (action.equalsIgnoreCase("UpdateSNRange"))
			updateSNRange( user, req, session );
		else if (action.equalsIgnoreCase("DeleteSNRange"))
			deleteSNRange( user, req, session );
		else if (action.equalsIgnoreCase("ConfigSNRange"))
			configSNRange( user, req, session );
		else if (action.equalsIgnoreCase("SendSwitchCommands"))
			sendSwitchCommands( user, req, session );
		else if (action.equalsIgnoreCase("RemoveSwitchCommands"))
			removeSwitchCommands( user, req, session );
		else if (action.equalsIgnoreCase("SearchInventory"))
			searchInventory( user, req, session );
		else if (action.equalsIgnoreCase("CreateHardware"))
			createLMHardware( user, req, session );
		else if (action.equalsIgnoreCase("CreateMCT"))
			createMCT( user, req, session );
		else if (action.equalsIgnoreCase( "SelectLMHardware" ))
			selectLMHardware( user, req, session );
		
		resp.sendRedirect( redirect );
	}
	
	/**
	 * Called from Consumer/SelectInv.jsp to select a hardware from inventory to insert
	 * into an account or update an existing hardware in that account. If the selected
	 * hardware is assigned to another account, go to CheckInv.jsp to ask for confirmation. 
	 */
	private void selectInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		int invID = Integer.parseInt( req.getParameter("InvID") );
		LiteInventoryBase liteInv = energyCompany.getInventoryBrief( invID, true );
		Integer invNo = (Integer) session.getAttribute( InventoryManagerUtil.STARS_INVENTORY_NO );
		
		if (liteInv.getAccountID() == CtiUtilities.NONE_ID) {
			String referer = (String) session.getAttribute( ServletUtils.ATT_REFERRER );
			if (referer.indexOf("Wizard") < 0) {
				// If we're trying to create/update a hardware and it's not in the wizard,
				// create/update the hardware immediately after this step. Let the user
				// update the hardware information later.
				StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
						session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
				LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation(
						starsAcctInfo.getStarsCustomerAccount().getAccountID(), true );
				
				if (invNo == null) {
					StarsCreateLMHardware createHw = new StarsCreateLMHardware();
					StarsLiteFactory.setStarsInv( createHw, liteInv, energyCompany );
					createHw.setRemoveDate( null );
					createHw.setInstallDate( new Date() );
					createHw.setInstallationNotes( "" );
					
					try {
						liteInv = CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany );
					}
					catch (WebClientException e) {
						CTILogger.error( e.getMessage(), e );
						session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
						redirect = req.getContextPath() + "/operator/Consumer/SerialNumber.jsp?action=New";
						return;
					}
					
					session.removeAttribute( ServletUtils.ATT_REDIRECT );
					StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
					CreateLMHardwareAction.parseResponse( createHw, starsInv, starsAcctInfo, session );
					
					// REDIRECT set in the CreateLMHardwareAction.parseResponse() method above
					redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
				}
				else {
					StarsUpdateLMHardware updateHw = new StarsUpdateLMHardware();
					StarsLiteFactory.setStarsInv( updateHw, liteInv, energyCompany );
					updateHw.setRemoveDate( null );
					updateHw.setInstallDate( new Date() );
					updateHw.setInstallationNotes( "" );
					
					int origInvID = starsAcctInfo.getStarsInventories().getStarsInventory( invNo.intValue() ).getInventoryID();
					StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
					deleteHw.setInventoryID( origInvID );
					
					try {
						liteInv = UpdateLMHardwareAction.updateInventory( updateHw, deleteHw, liteAcctInfo, user );
					}
					catch (WebClientException e) {
						CTILogger.error( e.getMessage(), e );
						session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
						redirect = req.getContextPath() + "/operator/Consumer/SerialNumber.jsp?InvNo="  + invNo;
						return;
					}
					
					session.setAttribute( ServletUtils.ATT_REDIRECT, req.getContextPath() + "/operator/Consumer/Inventory.jsp?InvNo=" + invNo );
					StarsInventory starsInv = StarsLiteFactory.createStarsInventory(liteInv, energyCompany);
					UpdateLMHardwareAction.parseResponse(origInvID, starsInv, starsAcctInfo, session);
					
					// REDIRECT set in the CreateLMHardwareAction.parseResponse() method above
					redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
				}
			}
			else {	// Inside new account wizard
				StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
				starsInv.setRemoveDate( null );
				starsInv.setInstallDate( new Date() );
				starsInv.setInstallationNotes( "" );
				
				session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP, starsInv );
				redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
			}
		}
		else {
			// The hardware is installed with another account, go to CheckInv.jsp to let the user confirm the action
			session.setAttribute(InventoryManagerUtil.INVENTORY_TO_CHECK, liteInv);
			redirect = req.getContextPath() + "/operator/Consumer/CheckInv.jsp";
		}
	}
	
	/**
	 * Called from SelectMCT.jsp when a device is selected. If the device is not assigned to any account
	 * (either in warehouse or not in inventory yet), populate the hardware information for the next page.
	 * Otherwise go to CheckInv.jsp asking for confirmation
	 */
	private void selectDevice(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		int categoryID = Integer.parseInt( req.getParameter("CategoryID") );
		int deviceID = Integer.parseInt( req.getParameter("DeviceID") );
		
		LiteInventoryBase liteInv = null;
		try {
			liteInv = energyCompany.getDevice( deviceID );
		}
		catch (ObjectInOtherEnergyCompanyException e) {
			session.setAttribute( InventoryManagerUtil.INVENTORY_TO_CHECK, e );
			redirect = req.getContextPath() + "/operator/Consumer/CheckInv.jsp";
			return;
		}
		
		LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO( deviceID );
		Integer invNo = (Integer) session.getAttribute( InventoryManagerUtil.STARS_INVENTORY_NO );
		
		if (liteInv == null || liteInv.getAccountID() == CtiUtilities.NONE_ID) {
			String referer = (String) session.getAttribute( ServletUtils.ATT_REFERRER );
			if (referer.indexOf("Wizard") < 0) {
				StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
						session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
				LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation(
						starsAcctInfo.getStarsCustomerAccount().getAccountID(), true );
				
				if (invNo == null) {
					StarsCreateLMHardware createHw = null;
					if (liteInv == null) {
						createHw = (StarsCreateLMHardware) StarsFactory.newStarsInv(StarsCreateLMHardware.class);
						createHw.setDeviceID( deviceID );
						if (ECUtils.isMCT( categoryID )) {
							createHw.setDeviceType( (DeviceType)StarsFactory.newStarsCustListEntry(
									energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT), DeviceType.class) );
						}
					}
					else {
						createHw = new StarsCreateLMHardware();
						StarsLiteFactory.setStarsInv( createHw, liteInv, energyCompany );
						createHw.setRemoveDate( null );
						createHw.setInstallDate( new Date() );
						createHw.setInstallationNotes( "" );
					}
					
					try {
						liteInv = CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany );
					}
					catch (WebClientException e) {
						CTILogger.error( e.getMessage(), e );
						session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
						redirect = req.getContextPath() + "/operator/Consumer/SerialNumber.jsp?action=New";
						return;
					}
					
					session.removeAttribute( ServletUtils.ATT_REDIRECT );
					StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
					CreateLMHardwareAction.parseResponse( createHw, starsInv, starsAcctInfo, session );
					
					// REDIRECT set in the CreateLMHardwareAction.parseResponse() method above
					redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
				}
				else {
					StarsUpdateLMHardware updateHw = null;
					if (liteInv == null) {
						updateHw = (StarsUpdateLMHardware) StarsFactory.newStarsInv(StarsUpdateLMHardware.class);
						updateHw.setDeviceID( deviceID );
						if (ECUtils.isMCT( categoryID )) {
							updateHw.setDeviceType( (DeviceType)StarsFactory.newStarsCustListEntry(
									energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT), DeviceType.class) );
						}
					}
					else {
						updateHw = new StarsUpdateLMHardware();
						StarsLiteFactory.setStarsInv( updateHw, liteInv, energyCompany );
						updateHw.setRemoveDate( null );
						updateHw.setInstallDate( new Date() );
						updateHw.setInstallationNotes( "" );
					}
					
					int origInvID = starsAcctInfo.getStarsInventories().getStarsInventory( invNo.intValue() ).getInventoryID();
					StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
					deleteHw.setInventoryID( origInvID );
					
					try {
						liteInv = UpdateLMHardwareAction.updateInventory( updateHw, deleteHw, liteAcctInfo, user );
					}
					catch (WebClientException e) {
						CTILogger.error( e.getMessage(), e );
						session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
						redirect = req.getContextPath() + "/operator/Consumer/SerialNumber.jsp?InvNo="  + invNo;
						return;
					}
					
					session.setAttribute( ServletUtils.ATT_REDIRECT, req.getContextPath() + "/operator/Consumer/Inventory.jsp?InvNo=" + invNo );
					StarsInventory starsInv = StarsLiteFactory.createStarsInventory(liteInv, energyCompany);
					UpdateLMHardwareAction.parseResponse(origInvID, starsInv, starsAcctInfo, session);
					
					// REDIRECT set in the CreateLMHardwareAction.parseResponse() method above
					redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
				}
			}
			else {	// Inside new account wizard
				if (liteInv == null) {
					// The device in not in inventory yet
					StarsInventory starsInv = (StarsInventory) StarsFactory.newStarsInv(StarsInventory.class);
					starsInv.setDeviceID( deviceID );
					
					if (ECUtils.isMCT( categoryID )) {
						starsInv.setDeviceType( (DeviceType)StarsFactory.newStarsCustListEntry(
								energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT), DeviceType.class) );
						
						MCT mct = new MCT();
						mct.setDeviceName( PAOFuncs.getYukonPAOName(deviceID) );
						starsInv.setMCT( mct );
					}
					
					session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP, starsInv );
					redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
				}
				else {
					// The device is in the warehouse
					StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
					starsInv.setRemoveDate( null );
					starsInv.setInstallDate( new Date() );
					starsInv.setInstallationNotes( "" );
					
					session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP, starsInv );
					redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
				}
			}
		}
		else {
			// The device is installed with another account
			session.setAttribute(InventoryManagerUtil.INVENTORY_TO_CHECK, liteInv);
			redirect = req.getContextPath() + "/operator/Consumer/CheckInv.jsp";
		}
	}
	
	/**
	 * Check the inventory for hardware with the specified device type and serial # (device name).
	 * If inventory checking is set to false, save the request parameters and redirect to SOAPClient.
	 * Otherwise, show the result in CheckInv.jsp. 
	 */
	private void checkInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		boolean invChecking = AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.INVENTORY_CHECKING);
		
		int devTypeID = Integer.parseInt( req.getParameter("DeviceType") );
		String serialNo = req.getParameter("SerialNo");
		String deviceName = req.getParameter("DeviceName");
		
		int categoryID = ECUtils.getInventoryCategoryID( devTypeID, energyCompany );
		
		if (invChecking) {
			// Save the request parameters
			StarsInventory starsInv = (StarsInventory) StarsFactory.newStarsInv(StarsInventory.class);
			starsInv.setDeviceType( (DeviceType)StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry(devTypeID), DeviceType.class) );
			
			if (ECUtils.isLMHardware(categoryID)) {
				LMHardware hw = new LMHardware();
				hw.setManufacturerSerialNumber( serialNo );
				starsInv.setLMHardware( hw );
			}
			else if (ECUtils.isMCT(categoryID)) {
				MCT mct = new MCT();
				mct.setDeviceName( deviceName );
				starsInv.setMCT( mct );
			}
			
			session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP, starsInv );
		}
		
		LiteInventoryBase liteInv = null;
		
		try {
			if (ECUtils.isLMHardware( categoryID )) {
				liteInv = energyCompany.searchForLMHardware( devTypeID, serialNo );
				session.setAttribute( InventoryManagerUtil.INVENTORY_TO_CHECK, liteInv );
			}
			else {
				liteInv = energyCompany.searchForDevice( categoryID, deviceName );
				session.setAttribute( InventoryManagerUtil.INVENTORY_TO_CHECK, liteInv );
			}
		}
		catch (ObjectInOtherEnergyCompanyException e) {
			if (action.equalsIgnoreCase("CreateLMHardware") ||
				action.equalsIgnoreCase("UpdateLMHardware"))
			{
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE,
						"The hardware or device already exists in the inventory list of <i>" + e.getEnergyCompany().getName() + "</i>." );
				redirect = referer;
			}
			else {
				session.setAttribute( InventoryManagerUtil.INVENTORY_TO_CHECK, e );
				redirect = req.getContextPath() + "/operator/Consumer/CheckInv.jsp";
			}
			
			return;
		}
		
		try {
			if (action.equalsIgnoreCase("CreateLMHardware")) {
				// Request from CreateHardware.jsp, no inventory checking
				ServletUtils.saveRequest( req, session, new String[]
					{"DeviceType", "SerialNo", "DeviceName", "CreateMCT", "MCTType", "PhysicalAddr", "MeterNumber", "MCTRoute",
					"DeviceLabel", "AltTrackNo", "ReceiveDate", "RemoveDate", "Voltage", "Notes", "InstallDate", "ServiceCompany", "InstallNotes", "Route"} );
				
				StarsCreateLMHardware createHw = new StarsCreateLMHardware();
				InventoryManagerUtil.setStarsInv( createHw, req, energyCompany );
				
				if (liteInv != null) {
					if (req.getParameter("CreateMCT") != null) {
						session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The specified device name already exists");
						redirect = referer;
						return;
					}
					
					createHw.setInventoryID( liteInv.getInventoryID() );
					createHw.setDeviceID( liteInv.getDeviceID() );
				}
				else if (req.getParameter("CreateMCT") == null) {
					session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The device name is not found. To create a new device, check the \"Create new device\".");
					redirect = referer;
					return;
				}
				
				StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
						session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
				LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation(
						starsAcctInfo.getStarsCustomerAccount().getAccountID(), true );
				
				try {
					liteInv = CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany );
				}
				catch (WebClientException e) {
					CTILogger.error( e.getMessage(), e );
					session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
					redirect = referer;
					return;
				}
				
				session.removeAttribute( ServletUtils.ATT_REDIRECT );
				StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
				CreateLMHardwareAction.parseResponse( createHw, starsInv, starsAcctInfo, session );
				
				// REDIRECT set in the CreateLMHardwareAction.parseResponse() method above
				redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
/*				
				// Save the request parameters 
				StarsOperation operation = CreateLMHardwareAction.getRequestOperation( req, energyCompany );
				session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_OPERATION, operation );
				
				if (liteInv != null) {
					operation.getStarsCreateLMHardware().setInventoryID( liteInv.getInventoryID() );
					operation.getStarsCreateLMHardware().setDeviceID( liteInv.getDeviceID() );
				}
				
				// Redirect to SOAPClient
				redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);*/
			}
			else if (action.equalsIgnoreCase("UpdateLMHardware")) {
				// Request from Inventory.jsp, device type or serial # must have been changed
				StarsOperation operation = UpdateLMHardwareAction.getRequestOperation( req, energyCompany );
				StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
				deleteHw.setInventoryID( Integer.parseInt(req.getParameter("OrigInvID")) );
				operation.setStarsDeleteLMHardware( deleteHw );
				session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_OPERATION, operation );
				
				if (liteInv != null) {
					operation.getStarsUpdateLMHardware().setInventoryID( liteInv.getInventoryID() );
					operation.getStarsUpdateLMHardware().setDeviceID( liteInv.getDeviceID() );
				}
				
				// Forward to DeleteInv.jsp to confirm removal of the old hardware
				LiteInventoryBase liteInvOld = energyCompany.getInventory( deleteHw.getInventoryID(), true );
				session.setAttribute( InventoryManagerUtil.INVENTORY_TO_DELETE, liteInvOld );
				redirect = req.getContextPath() + "/operator/Consumer/DeleteInv.jsp";
			}
			else
				redirect = req.getContextPath() + "/operator/Consumer/CheckInv.jsp";
		}
		catch (WebClientException e) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
			redirect = referer;
		}
	}
	
	/**
	 * Confirmation from CheckInv.jsp 
	 */
	private void confirmCheck(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		LiteInventoryBase liteInv = (LiteInventoryBase) session.getAttribute( InventoryManagerUtil.INVENTORY_TO_CHECK );
		Integer invNo = (Integer) session.getAttribute( InventoryManagerUtil.STARS_INVENTORY_NO );
		
		String referer = (String) session.getAttribute( ServletUtils.ATT_REFERRER );
		if (referer.indexOf("Wizard") < 0) {
			StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
					session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation(
					starsAcctInfo.getStarsCustomerAccount().getAccountID(), true );
			
			if (invNo == null) {
				StarsCreateLMHardware createHw = new StarsCreateLMHardware();
				if (liteInv != null) {
					StarsLiteFactory.setStarsInv( createHw, liteInv, energyCompany );
					createHw.setRemoveDate( null );
					createHw.setInstallDate( new Date() );
					createHw.setInstallationNotes( "" );
				}
				else {
					StarsInventory starsInv = (StarsInventory) session.getAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP );
					if (starsInv.getMCT() != null) {
						// To create a MCT, we need more information
						redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
						return;
					}
					
					createHw.setInventoryID( -1 );
					createHw.setDeviceType( starsInv.getDeviceType() );
					createHw.setLMHardware( starsInv.getLMHardware() );
					createHw.setMCT( starsInv.getMCT() );
				}
				
				try {
					liteInv = CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany );
				}
				catch (WebClientException e) {
					CTILogger.error( e.getMessage(), e );
					session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
					redirect = req.getContextPath() + "/operator/Consumer/SerialNumber.jsp?action=New";
					return;
				}
				
				session.removeAttribute( ServletUtils.ATT_REDIRECT );
				StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
				CreateLMHardwareAction.parseResponse( createHw, starsInv, starsAcctInfo, session );
				
				redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
			}
			else {
				StarsUpdateLMHardware updateHw = new StarsUpdateLMHardware();
				if (liteInv != null) {
					StarsLiteFactory.setStarsInv( updateHw, liteInv, energyCompany );
					updateHw.setRemoveDate( null );
					updateHw.setInstallDate( new Date() );
					updateHw.setInstallationNotes( "" );
				}
				else {
					StarsInventory starsInv = (StarsInventory) session.getAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP );
					if (starsInv.getMCT() != null) {
						// To create a MCT, we need more information
						redirect = req.getContextPath() + "/operator/Consumer/CreateHardware.jsp?InvNo=" + invNo;
						return;
					}
					
					updateHw.setInventoryID( -1 );
					updateHw.setDeviceType( starsInv.getDeviceType() );
					updateHw.setLMHardware( starsInv.getLMHardware() );
					updateHw.setMCT( starsInv.getMCT() );
				}
				
				int origInvID = starsAcctInfo.getStarsInventories().getStarsInventory( invNo.intValue() ).getInventoryID();
				StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
				deleteHw.setInventoryID( origInvID );
				
				StarsOperation operation = new StarsOperation();
				operation.setStarsUpdateLMHardware( updateHw );
				operation.setStarsDeleteLMHardware( deleteHw );
				session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_OPERATION, operation );
				
				String redir = req.getContextPath() + "/servlet/SOAPClient?action=UpdateLMHardware"
						+ "&REDIRECT=" + req.getContextPath() + "/operator/Consumer/Inventory.jsp?InvNo=" + invNo
						+ "&REFERRER=" + session.getAttribute(ServletUtils.ATT_REFERRER);
				session.setAttribute( ServletUtils.ATT_REDIRECT, redir );
				
				LiteInventoryBase liteInvOld = energyCompany.getInventory( origInvID, true );
				session.setAttribute( InventoryManagerUtil.INVENTORY_TO_DELETE, liteInvOld );
				redirect = req.getContextPath() + "/operator/Consumer/DeleteInv.jsp";
			}
		}
		else {
			StarsInventory starsInv = null;
			if (liteInv != null) {
				// This is an existing hardware/device
				starsInv = (StarsInventory) StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
			}
			else {
				// This is a LM hardware to be added to the inventory
				starsInv = (StarsInventory) session.getAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP );
			}
			
			starsInv.setRemoveDate( null );
			starsInv.setInstallDate( new Date() );
			starsInv.setInstallationNotes( "" );
			session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP + invNo, starsInv );
			
			redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
		}
	}
	
	/**
	 * The delete button is clicked on Inventory.jsp 
	 */
	private void deleteLMHardware(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		StarsOperation operation = DeleteLMHardwareAction.getRequestOperation( req, energyCompany.getDefaultTimeZone() );
		session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_OPERATION, operation );
		
		LiteInventoryBase liteInv = energyCompany.getInventory( operation.getStarsDeleteLMHardware().getInventoryID(), true );
		session.setAttribute( InventoryManagerUtil.INVENTORY_TO_DELETE, liteInv );
		
		redirect = req.getContextPath() + "/operator/Consumer/DeleteInv.jsp"; 
	}
	
	/**
	 * Confirmation from DeleteInv.jsp 
	 */
	private void confirmDelete(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		StarsOperation operation = (StarsOperation) session.getAttribute(InventoryManagerUtil.STARS_INVENTORY_OPERATION);
		operation.getStarsDeleteLMHardware().setDeleteFromInventory(
				Boolean.valueOf(req.getParameter("DeletePerm")).booleanValue() );
		
		redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
	}
	
	/**
	 * The submit button is clicked on InventoryDetail.jsp 
	 */
	private void updateInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			int invID = Integer.parseInt( req.getParameter("InvID") );
			LiteInventoryBase liteInv = energyCompany.getInventoryBrief( invID, true );
			
			StarsOperation operation = UpdateLMHardwareAction.getRequestOperation( req, energyCompany );
			UpdateLMHardwareAction.updateInventory( operation.getStarsUpdateLMHardware(), liteInv, energyCompany );
			
			if (req.getParameter("UseHardwareAddressing") != null) {
				StarsLMConfiguration starsCfg = new StarsLMConfiguration();
				InventoryManagerUtil.setStarsLMConfiguration( starsCfg, req );
				UpdateLMHardwareConfigAction.updateLMConfiguration( starsCfg, (LiteStarsLMHardware)liteInv );
			}
			
			if (liteInv.getAccountID() > 0) {
				StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteInv.getAccountID() );
				if (starsAcctInfo != null) {
					StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
					UpdateLMHardwareAction.parseResponse( liteInv.getInventoryID(), starsInv, starsAcctInfo, null );
				}
			}
			
			session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Hardware information updated successfully" );
		}
		catch (WebClientException e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update hardware information");
		}
	}
	
	/**
	 * The delete button is clicked on InventoryDetail.jsp 
	 */
	private void deleteInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		int invID = Integer.parseInt( req.getParameter("InvID") );
		LiteInventoryBase liteInv = energyCompany.getInventoryBrief( invID, true );
		
		if (liteInv.getAccountID() == 0) {
			com.cannontech.database.data.stars.hardware.InventoryBase inventory =
					new com.cannontech.database.data.stars.hardware.InventoryBase();
			inventory.setInventoryID( new Integer(invID) );
			
			try {
				Transaction.createTransaction( Transaction.DELETE, inventory ).execute();
				LiteInventoryBase liteInv2 = energyCompany.deleteInventory( invID );
				LiteInventoryBase liteInv3 = energyCompany.getInventoryBrief( invID, true );
				
				if (liteInv.getDeviceID() > 0 && Boolean.valueOf(req.getParameter("DeleteFromYukon")).booleanValue()) {
					byte status = DBDeletionFuncs.deletionAttempted( liteInv.getDeviceID(), DBDeletionFuncs.DEVICE_TYPE );
					if (status == DBDeletionFuncs.STATUS_DISALLOW)
						throw new WebClientException( DBDeletionFuncs.getTheWarning().toString() );
					
					LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO( liteInv.getDeviceID() );
					DBPersistent dbPer = LiteFactory.convertLiteToDBPers( litePao );
					Transaction.createTransaction( Transaction.DELETE, dbPer ).execute();
					
					DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance().createDBChangeMessages(
							(CTIDbChange)dbPer, DBChangeMsg.CHANGE_TYPE_DELETE );
					for (int i = 0; i < dbChange.length; i++)
						ServerUtils.handleDBChangeMsg( dbChange[i] );
				}
			}
			catch (Exception e) {
				CTILogger.error( e.getMessage(), e );
				if (e instanceof WebClientException)
					session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
				else
					session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete the hardware from inventory" );
				redirect = referer;
			}
		}
		else {
			StarsOperation operation = DeleteLMHardwareAction.getRequestOperation( req, energyCompany.getDefaultTimeZone() );
			session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_OPERATION, operation );
			redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
		}
	}
	
	/**
	 * The config button, config link, or "Save To Batch" link is clicked on InventoryDetail.jsp
	 */
	private void configLMHardware(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			int invID = Integer.parseInt( req.getParameter("InvID") );
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventoryBrief( invID, true );
			
			if (req.getParameter("UseHardwareAddressing") != null) {
				StarsLMConfiguration starsCfg = new StarsLMConfiguration();
				InventoryManagerUtil.setStarsLMConfiguration( starsCfg, req );
				UpdateLMHardwareConfigAction.updateLMConfiguration( starsCfg, liteHw );
			}
			
			if (Boolean.valueOf( req.getParameter("SaveToBatch") ).booleanValue()) {
				UpdateLMHardwareConfigAction.saveSwitchCommand( liteHw, SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE, energyCompany );
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Hardware configuration saved to batch successfully." );
			}
			else {
				YukonSwitchCommandAction.sendConfigCommand( energyCompany, liteHw, true, null );
				
				if (liteHw.getAccountID() > 0) {
					StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteHw.getAccountID() );
					if (starsAcctInfo != null) {
						StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, energyCompany );
						UpdateLMHardwareAction.parseResponse( liteHw.getInventoryID(), starsInv, starsAcctInfo, null );
					}
				}
				
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Hardware configuration sent out successfully." );
			}
			
			session.setAttribute( ServletUtils.ATT_REDIRECT2, redirect );
			session.setAttribute( ServletUtils.ATT_REFERRER2, referer );
			redirect = referer = req.getContextPath() +
					(ECUtils.isOperator(user)? "/operator/Admin/Message.jsp" : "/user/ConsumerStat/stat/Message.jsp");
		}
		catch (WebClientException e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
		}
	}
	
	/**
	 * Add hardwares in the given serial # range to inventory 
	 */
	private void addSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		ServletUtils.saveRequest( req, session,
				new String[] {"From", "To", "DeviceType", "ReceiveDate", "Voltage", "ServiceCompany", "Route"} );
		
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		int snFrom = 0, snTo = 0;
		try {
			snFrom = Integer.parseInt( req.getParameter("From") );
			if (req.getParameter("To").length() > 0)
				snTo = Integer.parseInt( req.getParameter("To") );
			else
				snTo = snFrom;
		}
		catch (NumberFormatException nfe) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid number format in the SN range");
			redirect = referer;
			return;
		}
		
		if (snFrom > snTo) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The 'from' value cannot be greater than the 'to' value");
			redirect = referer;
			return;
		}
		
		Integer devTypeID = Integer.valueOf( req.getParameter("DeviceType") );
		Integer voltageID = Integer.valueOf( req.getParameter("Voltage") );
		Integer companyID = Integer.valueOf( req.getParameter("ServiceCompany") );
		Integer routeID = Integer.valueOf( req.getParameter("Route") );
		
		Date recvDate = null;
		String recvDateStr = req.getParameter("ReceiveDate");
		if (recvDateStr.length() > 0) {
			recvDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(recvDateStr, energyCompany.getDefaultTimeZone());
			if (recvDate == null) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid receive date format");
				redirect = referer;
				return;
			}
		}
		
		// If operation succeed, display the confirmation message on "Message.jsp";
		// if operation failed, show the error on "ResultSet.jsp" (the REDIRECT parameter
		// is set within the AddSNRangeTask.run() method)
		session.setAttribute( ServletUtils.ATT_REDIRECT2, redirect );
		session.setAttribute( ServletUtils.ATT_REFERRER2, referer );
		redirect = req.getContextPath() +
				(ECUtils.isOperator(user)? "/operator/Admin/Message.jsp" : "/user/ConsumerStat/stat/Message.jsp");
		
		session.removeAttribute( ServletUtils.ATT_REDIRECT );
		
		TimeConsumingTask task = new AddSNRangeTask( snFrom, snTo, devTypeID, recvDate, voltageID, companyID, routeID, req );
		long id = ProgressChecker.addTask( task );
		
		// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			
			task = ProgressChecker.getTask(id);
			String redir = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
			
			if (task.getStatus() == AddSNRangeTask.STATUS_FINISHED) {
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
				ProgressChecker.removeTask( id );
				if (redir != null) redirect = redir;
				return;
			}
			
			if (task.getStatus() == AddSNRangeTask.STATUS_ERROR) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
				ProgressChecker.removeTask( id );
				if (redir != null) redirect = redir;
				return;
			}
		}
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		session.setAttribute(ServletUtils.ATT_REFERRER, redirect);
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	/**
	 * Update information of hardwares in the given serial # range 
	 */
	private void updateSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		ServletUtils.saveRequest( req, session,
				new String[] {"From", "To", "DeviceType", "NewDeviceType", "ReceiveDate", "Voltage", "ServiceCompany", "Route"} );
		
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		Integer devTypeID = Integer.valueOf( req.getParameter("DeviceType") );
		
		Integer newDevTypeID = (req.getParameter("NewDeviceType") != null)?
				Integer.valueOf( req.getParameter("NewDeviceType") ) : null;
		if (newDevTypeID != null && newDevTypeID.intValue() == devTypeID.intValue())
			newDevTypeID = null;
		
		String fromStr = req.getParameter("From");
		String toStr = req.getParameter("To");
		Integer snFrom = null;
		Integer snTo = null;
		
		if (!fromStr.equals("*")) {
			try {
				snFrom = Integer.valueOf( fromStr );
				if (!toStr.equals("*")) {
					snTo = Integer.valueOf( toStr );
					if (snFrom.intValue() > snTo.intValue()) {
						session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The 'From' value is greater than the 'To' value");
						redirect = referer;
						return;
					}
				}
			}
			catch (NumberFormatException nfe) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid number format in the SN range");
				redirect = referer;
				return;
			}
		}
		
		Integer voltageID = (req.getParameter("Voltage") != null)?
				Integer.valueOf( req.getParameter("Voltage") ) : null;
		Integer companyID = (req.getParameter("ServiceCompany") != null)?
				Integer.valueOf( req.getParameter("ServiceCompany") ) : null;
		Integer routeID = (req.getParameter("Route") != null)?
				Integer.valueOf( req.getParameter("Route") ) : null;
		
		Date recvDate = null;
		String recvDateStr = req.getParameter("ReceiveDate");
		if (recvDateStr != null && recvDateStr.length() > 0) {
			recvDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(recvDateStr, energyCompany.getDefaultTimeZone());
			if (recvDate == null) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid receive date format");
				redirect = referer;
				return;
			}
		}
		
		if (newDevTypeID == null && recvDate == null && voltageID == null && companyID == null && routeID == null)
			return;
		
		session.setAttribute( ServletUtils.ATT_REDIRECT2, redirect );
		session.setAttribute( ServletUtils.ATT_REFERRER2, referer );
		redirect = req.getContextPath() +
				(ECUtils.isOperator(user)? "/operator/Admin/Message.jsp" : "/user/ConsumerStat/stat/Message.jsp");
		
		session.removeAttribute( ServletUtils.ATT_REDIRECT );
		
		TimeConsumingTask task = new UpdateSNRangeTask( snFrom, snTo, devTypeID, newDevTypeID, recvDate, voltageID, companyID, routeID, req );
		long id = ProgressChecker.addTask( task );
		
		// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			
			task = ProgressChecker.getTask(id);
			String redir = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
			
			if (task.getStatus() == UpdateSNRangeTask.STATUS_FINISHED) {
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
				ProgressChecker.removeTask( id );
				if (redir != null) redirect = redir;
				return;
			}
			
			if (task.getStatus() == UpdateSNRangeTask.STATUS_ERROR) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
				ProgressChecker.removeTask( id );
				if (redir != null) redirect = redir;
				return;
			}
		}
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		session.setAttribute(ServletUtils.ATT_REFERRER, redirect);
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	/**
	 * Delete hardwares in the given serial # range 
	 */
	private void deleteSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		ServletUtils.saveRequest( req, session, new String[] {"From", "To", "DeviceType"} );
		
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		String fromStr = req.getParameter("From");
		String toStr = req.getParameter("To");
		Integer snFrom = null;
		Integer snTo = null;
		
		if (!fromStr.equals("*")) {
			try {
				snFrom = Integer.valueOf( fromStr );
				if (!toStr.equals("*")) {
					snTo = Integer.valueOf( toStr );
					if (snFrom.intValue() > snTo.intValue()) {
						session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The 'From' value is greater than the 'To' value");
						redirect = referer;
						return;
					}
				}
			}
			catch (NumberFormatException nfe) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid number format in the SN range");
				redirect = referer;
				return;
			}
		}
		
		Integer devTypeID = Integer.valueOf( req.getParameter("DeviceType") );
		
		session.setAttribute( ServletUtils.ATT_REDIRECT2, redirect );
		session.setAttribute( ServletUtils.ATT_REFERRER2, referer );
		redirect = req.getContextPath() +
				(ECUtils.isOperator(user)? "/operator/Admin/Message.jsp" : "/user/ConsumerStat/stat/Message.jsp");
		
		session.removeAttribute( ServletUtils.ATT_REDIRECT );
		
		TimeConsumingTask task = new DeleteSNRangeTask( snFrom, snTo, devTypeID, req );
		long id = ProgressChecker.addTask( task );
		
		// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			
			task = ProgressChecker.getTask(id);
			String redir = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
			
			if (task.getStatus() == DeleteSNRangeTask.STATUS_FINISHED) {
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
				ProgressChecker.removeTask( id );
				if (redir != null) redirect = redir;
				return;
			}
			
			if (task.getStatus() == DeleteSNRangeTask.STATUS_ERROR) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
				ProgressChecker.removeTask( id );
				if (redir != null) redirect = redir;
				return;
			}
		}
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		session.setAttribute(ServletUtils.ATT_REFERRER, redirect);
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	/**
	 * Configure hardwares in the given serial # range 
	 */
	private void configSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		boolean configNow = req.getParameter("ConfigNow") != null;
		
		session.setAttribute( ServletUtils.ATT_REDIRECT2, redirect );
		session.setAttribute( ServletUtils.ATT_REFERRER2, referer );
		redirect = req.getContextPath() +
				(ECUtils.isOperator(user)? "/operator/Admin/Message.jsp" : "/user/ConsumerStat/stat/Message.jsp");
		
		session.removeAttribute( ServletUtils.ATT_REDIRECT );
		
		TimeConsumingTask task = new ConfigSNRangeTask( configNow, req );
		long id = ProgressChecker.addTask( task );
		
		// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			
			task = ProgressChecker.getTask(id);
			String redir = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
			
			if (task.getStatus() == ConfigSNRangeTask.STATUS_FINISHED) {
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
				ProgressChecker.removeTask( id );
				if (redir != null) redirect = redir;
				return;
			}
			
			if (task.getStatus() == ConfigSNRangeTask.STATUS_ERROR) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
				ProgressChecker.removeTask( id );
				if (redir != null) redirect = redir;
				return;
			}
		}
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		session.setAttribute(ServletUtils.ATT_REFERRER, redirect);
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	/**
	 * Send scheduled switch commands
	 */
	private void sendSwitchCommands(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			int[] invIDs = null;
			if (req.getParameter("All") == null) {
				String[] values = req.getParameterValues( "InvID" );
				invIDs = new int[ values.length ];
				for (int i = 0; i < values.length; i++)
					invIDs[i] = Integer.parseInt( values[i] );
			}
			
			InventoryManagerUtil.sendSwitchCommands( energyCompany, invIDs );
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Switch commands sent out successfully");
		}
		catch (WebClientException e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
		}
	}
	
	/**
	 * Remove scheduled switch commands 
	 */
	private void removeSwitchCommands(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		SwitchCommandQueue queue = energyCompany.getSwitchCommandQueue();
		
		if (req.getParameter("All") != null) {
			queue.clearCommands( user.getEnergyCompanyID() );
		}
		else {
			String[] values = req.getParameterValues( "InvID" );
			for (int i = 0; i < values.length; i++)
				queue.removeCommand( Integer.parseInt(values[i]) );
		}
		
		session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Switch commands removed successfully");
	}
	
	private void searchInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		session.removeAttribute( InventoryManagerUtil.INVENTORY_SET );
		
		int searchBy = Integer.parseInt( req.getParameter("SearchBy") );
		String searchValue = req.getParameter( "SearchValue" );
		if (searchValue.trim().length() == 0) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Search value cannot be empty");
			return;
		}
		
		// Remember the last search option
		session.setAttribute( ServletUtils.ATT_LAST_INVENTORY_SEARCH_OPTION, new Integer(searchBy) );
		
		boolean searchMembers = AuthFuncs.checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS )
				&& (energyCompany.getChildren().size() > 0);
		ArrayList invList = null; 
		
		if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_SERIAL_NO) {
			invList = energyCompany.searchInventoryBySerialNo( searchValue, searchMembers );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ACCT_NO) {
			ArrayList accounts = energyCompany.searchAccountByAccountNo( searchValue, searchMembers );
			invList = getInventoryByAccounts( accounts, energyCompany );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_PHONE_NO) {
			try {
				String phoneNo = ServletUtils.formatPhoneNumber( searchValue );
				ArrayList accounts = energyCompany.searchAccountByPhoneNo( phoneNo, searchMembers );
				invList = getInventoryByAccounts( accounts, energyCompany );
			}
			catch (WebClientException e) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
				return;
			}
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_LAST_NAME) {
			ArrayList accounts = energyCompany.searchAccountByLastName( searchValue, searchMembers );
			invList = getInventoryByAccounts( accounts, energyCompany );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ORDER_NO) {
			// TODO: The WorkOrderBase table doesn't have InventoryID column, maybe should be added
			ArrayList accounts = energyCompany.searchAccountByOrderNo( searchValue, searchMembers );
			invList = getInventoryByAccounts( accounts, energyCompany );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ADDRESS) {
			ArrayList accounts = energyCompany.searchAccountByAddress( searchValue, searchMembers );
			invList = getInventoryByAccounts( accounts, energyCompany );
		}
		
		if (invList == null || invList.size() == 0) {
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET_DESC, "<div class='ErrorMsg' align='center'>No hardware found matching the search criteria.</div>");
		}
		else {
			LiteInventoryBase liteInv = null;
			if (invList.size() == 1) {
				if (searchMembers) {
					if (((Pair)invList.get(0)).getSecond() == energyCompany)
						liteInv = (LiteInventoryBase) ((Pair)invList.get(0)).getFirst();
				}
				else
					liteInv = (LiteInventoryBase) invList.get(0);
			}
			
			if (liteInv != null) {
				redirect = req.getContextPath() + "/operator/Hardware/InventoryDetail.jsp?InvId=" + liteInv.getInventoryID() + "&src=Search";
			}
			else {
				session.setAttribute(InventoryManagerUtil.INVENTORY_SET, invList);
				session.setAttribute(InventoryManagerUtil.INVENTORY_SET_DESC, "Click on a serial # (device name) to view the hardware details, or click on an account # (if available) to view the account information.");
				session.setAttribute(ServletUtils.ATT_REFERRER, referer);
			}
		}
	}
	
	private void createLMHardware(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			StarsCreateLMHardware createHw = new StarsCreateLMHardware();
			InventoryManagerUtil.setStarsInv( createHw, req, energyCompany );
			
			try {
				LiteInventoryBase existingHw = energyCompany.searchForLMHardware(
						createHw.getDeviceType().getEntryID(), createHw.getLMHardware().getManufacturerSerialNumber() );
				if (existingHw != null)
					throw new WebClientException("Cannot create hardware: serial # already exists.");
			}
			catch (ObjectInOtherEnergyCompanyException e) {
				throw new WebClientException("Cannot create hardware: serial # already exists in the inventory list of <i>" + e.getEnergyCompany().getName() + "</i>.");
			}
			
			LiteInventoryBase liteInv = CreateLMHardwareAction.addInventory( createHw, null, energyCompany );
			
			if (req.getParameter("UseHardwareAddressing") != null) {
				StarsLMConfiguration starsCfg = new StarsLMConfiguration();
				InventoryManagerUtil.setStarsLMConfiguration( starsCfg, req );
				UpdateLMHardwareConfigAction.updateLMConfiguration( starsCfg, (LiteStarsLMHardware)liteInv );
			}
			
			// Append inventory ID to the redirect URL
			redirect += String.valueOf( liteInv.getInventoryID() );
		}
		catch (WebClientException e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
			redirect = referer;
		}
	}
	
	private void createMCT(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			StarsCreateLMHardware createHw = new StarsCreateLMHardware();
			InventoryManagerUtil.setStarsInv( createHw, req, energyCompany );
			
			if (req.getParameter("DeviceName").length() > 0) {
				MCT mct = new MCT();
				mct.setMctType( Integer.parseInt(req.getParameter("MCTType")) );
				mct.setDeviceName( req.getParameter("DeviceName") );
				mct.setPhysicalAddress( Integer.parseInt(req.getParameter("PhysicalAddr")) );
				mct.setMeterNumber( req.getParameter("MeterNumber") );
				mct.setRouteID( Integer.parseInt(req.getParameter("Route")) );
				createHw.setMCT( mct );
			}
			
			LiteInventoryBase liteInv = CreateLMHardwareAction.addInventory( createHw, null, energyCompany );
			
			redirect += String.valueOf( liteInv.getInventoryID() );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to create the new MCT" );
			redirect = referer;
		}
	}
	
	/**
	 * Called from Hardware/SelectInv.jsp to select a hardware for configuration.
	 */
	private void selectLMHardware(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		int invID = Integer.parseInt( req.getParameter("InvID") );
		if (req.getParameter("MemberID") == null) {
			LiteInventoryBase liteInv = energyCompany.getInventoryBrief( invID, true );
			session.setAttribute(InventoryManagerUtil.INVENTORY_TO_CHECK, liteInv);
		}
		else {
			LiteStarsEnergyCompany member = StarsDatabaseCache.getInstance().getEnergyCompany( Integer.parseInt(req.getParameter("MemberID")) );
			LiteInventoryBase liteInv = member.getInventoryBrief( invID, true );
			session.setAttribute(InventoryManagerUtil.INVENTORY_TO_CHECK, new Pair(liteInv, member));
		}
		
		redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
	}
	
	/**
	 * Get all the hardwares in inventory for accounts
	 * @param accounts List of LiteStarsCustAccountInformation, or Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany)
	 * @return List of LiteInventoryBase or Pair(LiteInventoryBase, LiteStarsEnergyCompany), based on the element type of accounts
	 */
	private ArrayList getInventoryByAccounts(ArrayList accounts, LiteStarsEnergyCompany energyCompany) {
		ArrayList invList = new ArrayList();
		
		for (int i = 0; i < accounts.size(); i++) {
			if (accounts.get(i) instanceof Pair) {
				LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) ((Pair)accounts.get(i)).getFirst();
				LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) ((Pair)accounts.get(i)).getSecond();
				
				for (int j = 0; j < liteAcctInfo.getInventories().size(); j++) {
					int invID = ((Integer)liteAcctInfo.getInventories().get(j)).intValue();
					LiteInventoryBase liteInv = company.getInventoryBrief( invID, true );
					invList.add( new Pair(liteInv, company) );
				}
			}
			else {
				LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) accounts.get(i);
				for (int j = 0; j < liteAcctInfo.getInventories().size(); j++) {
					int invID = ((Integer)liteAcctInfo.getInventories().get(j)).intValue();
					invList.add( energyCompany.getInventoryBrief(invID, true) );
				}
			}
		}
		
		return invList;
	}
	
}
