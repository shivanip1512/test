package com.cannontech.stars.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

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
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.cache.functions.AuthFuncs;
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
import com.cannontech.stars.util.task.UpdateSNRangeTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareConfigAction;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.DeviceType;
import com.cannontech.stars.xml.serialize.ExpressCom;
import com.cannontech.stars.xml.serialize.InstallationCompany;
import com.cannontech.stars.xml.serialize.LMHardware;
import com.cannontech.stars.xml.serialize.MCT;
import com.cannontech.stars.xml.serialize.SA205;
import com.cannontech.stars.xml.serialize.SA305;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsInv;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.VersaCom;
import com.cannontech.stars.xml.serialize.Voltage;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class InventoryManager extends HttpServlet {
	
	public static final String STARS_INVENTORY_TEMP = "STARS_INVENTORY_TEMP";
	public static final String STARS_INVENTORY_NO = "STARS_INVENTORY_NO";
	
	public static final String STARS_INVENTORY_OPERATION = "STARS_INVENTORY_OPERATION";
	
	public static final String INVENTORY_TO_CHECK = "INVENTORY_TO_CHECK";
	public static final String INVENTORY_TO_DELETE = "INVENTORY_TO_DELETE";
	
	public static final String INVENTORY_SET = "INVENTORY_SET";
	public static final String INVENTORY_SET_DESC = "INVENTORY_SET_DESCRIPTION";
	
	private String action = null;
	private String referer = null;
	private String redirect = null;

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
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
        
		referer = req.getHeader( "referer" );
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
		else if (action.equalsIgnoreCase("ClearSwitchCommands"))
			clearSwitchCommands( user, req, session );
		else if (action.equalsIgnoreCase("SearchInventory"))
			searchInventory( user, req, session );
		else if (action.equalsIgnoreCase("CreateHardware"))
			createLMHardware( user, req, session );
		
		resp.sendRedirect( redirect );
	}
	
	/**
	 * Called from SelectInv.jsp when a hardware is selected. If the hardware is in the warehouse,
	 * populate its information for the next page, otherwise go to CheckInv.jsp asking for confirmation. 
	 */
	private void selectInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		int invID = Integer.parseInt( req.getParameter("InvID") );
		LiteInventoryBase liteInv = energyCompany.getInventoryBrief( invID, true );
		
		if (liteInv.getAccountID() == CtiUtilities.NONE_ID) {
			// The hardware is in warehouse, so populate the hardware information
			StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
			starsInv.setRemoveDate( null );
			starsInv.setInstallDate( new Date() );
			starsInv.setInstallationNotes( "" );
			
			String invNo = (String) session.getAttribute(STARS_INVENTORY_NO);
			session.setAttribute( STARS_INVENTORY_TEMP + invNo, starsInv );
			
			redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
		}
		else {
			// The hardware is installed with another account, go to CheckInv.jsp to let the user confirm the action
			session.setAttribute(INVENTORY_TO_CHECK, liteInv);
			redirect = req.getContextPath() + "/operator/Consumer/CheckInv.jsp";
		}
	}
	
	/**
	 * Called from SelectMCT.jsp when a device is selected. If the device is not assigned to any account
	 * (either in warehouse or not in inventory yet), populate the hardware information for the next page.
	 * Otherwise go to CheckInv.jsp asking for confirmation
	 */
	private void selectDevice(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		int categoryID = Integer.parseInt( req.getParameter("CategoryID") );
		int deviceID = Integer.parseInt( req.getParameter("DeviceID") );
		
		LiteInventoryBase liteInv = null;
		try {
			liteInv = energyCompany.getDevice( deviceID );
		}
		catch (ObjectInOtherEnergyCompanyException e) {
			session.setAttribute( INVENTORY_TO_CHECK, e );
			redirect = req.getContextPath() + "/operator/Consumer/CheckInv.jsp";
			return;
		}
		
		if (liteInv == null) {
			// The device in not in inventory yet
			LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO( deviceID );
			
			if (ECUtils.isMCT( categoryID )) {
				StarsInventory starsInv = (StarsInventory) StarsFactory.newStarsInv(StarsInventory.class);
				starsInv.setDeviceID( deviceID );
				starsInv.setDeviceType( (DeviceType)StarsFactory.newStarsCustListEntry(
						energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT), DeviceType.class) );
				
				MCT mct = new MCT();
				mct.setDeviceName( PAOFuncs.getYukonPAOName(deviceID) );
				starsInv.setMCT( mct );
				
				String invNo = (String) session.getAttribute(STARS_INVENTORY_NO);
				session.setAttribute( STARS_INVENTORY_TEMP + invNo, starsInv );
			}
			
			redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
		}
		else if (liteInv.getAccountID() == CtiUtilities.NONE_ID) {
			// The device is in the warehouse
			StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
			starsInv.setRemoveDate( null );
			starsInv.setInstallDate( new Date() );
			starsInv.setInstallationNotes( "" );
			
			String invNo = (String) session.getAttribute(STARS_INVENTORY_NO);
			session.setAttribute( STARS_INVENTORY_TEMP + invNo, starsInv );
			
			redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
		}
		else {
			// The device is installed with another account
			session.setAttribute(INVENTORY_TO_CHECK, liteInv);
			redirect = req.getContextPath() + "/operator/Consumer/CheckInv.jsp";
		}
	}
	
	/**
	 * Check the inventory for hardware with the specified device type and serial # (device name).
	 * If inventory checking time is NONE, save the request parameters and redirect to SOAPClient.
	 * Otherwise, show the result in CheckInv.jsp or SelectMCT.jsp (only when no meter is found). 
	 */
	private void checkInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		boolean invChecking = AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.INVENTORY_CHECKING);
		
		int devTypeID = Integer.parseInt( req.getParameter("DeviceType") );
		String searchVal = req.getParameter("SerialNo");
		
		if (invChecking) {
			// Save the search values
			StarsInventory starsInv = (StarsInventory) StarsFactory.newStarsInv(StarsInventory.class);
			starsInv.setDeviceType( (DeviceType)StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry(devTypeID), DeviceType.class) );
			
			LMHardware hw = new LMHardware();
			hw.setManufacturerSerialNumber( searchVal );
			starsInv.setLMHardware( hw );
			
			session.setAttribute( STARS_INVENTORY_TEMP, starsInv );
		}
		
		LiteInventoryBase liteInv = null;
		int categoryID = ECUtils.getInventoryCategoryID( devTypeID, energyCompany );
		
		try {
			if (ECUtils.isLMHardware( categoryID )) {
				liteInv = energyCompany.searchForLMHardware( devTypeID, searchVal );
				session.setAttribute( INVENTORY_TO_CHECK, liteInv );
			}
			else {
				liteInv = energyCompany.searchForDevice( categoryID, searchVal );
				if (liteInv != null) {
					session.setAttribute( INVENTORY_TO_CHECK, liteInv );
				}
				else {
					if (ECUtils.isMCT( categoryID ))
						redirect = req.getContextPath() + "/operator/Consumer/SelectMCT.jsp";
					
					try {
						redirect += "?DeviceName=" + java.net.URLEncoder.encode(searchVal, "UTF-8");
					}
					catch (java.io.UnsupportedEncodingException e) {}
					
					session.setAttribute( ServletUtils.ATT_REFERRER2, referer );
					return;
				}
			}
		}
		catch (ObjectInOtherEnergyCompanyException e) {
			if (action.equalsIgnoreCase("CreateLMHardware") ||
				action.equalsIgnoreCase("UpdateLMHardware"))
			{
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE,
						"The hardware is found in another energy company. Please contact " + energyCompany.getParent().getName() + " for more information." );
				redirect = referer;
			}
			else {
				session.setAttribute( INVENTORY_TO_CHECK, e );
				redirect = req.getContextPath() + "/operator/Consumer/CheckInv.jsp";
			}
			
			return;
		}
		
		try {
			if (action.equalsIgnoreCase("CreateLMHardware")) {
				// Request from CreateHardware.jsp, no inventory checking
				// Save the request parameters 
				StarsOperation operation = CreateLMHardwareAction.getRequestOperation(req, energyCompany.getDefaultTimeZone());
				session.setAttribute( STARS_INVENTORY_OPERATION, operation );
				
				if (liteInv != null) {
					operation.getStarsCreateLMHardware().setInventoryID( liteInv.getInventoryID() );
					operation.getStarsCreateLMHardware().setDeviceID( liteInv.getDeviceID() );
				}
				
				// Redirect to SOAPClient
				redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
			}
			else if (action.equalsIgnoreCase("UpdateLMHardware")) {
				// Request from Inventory.jsp, device type or serial # must have been changed
				StarsOperation operation = UpdateLMHardwareAction.getRequestOperation(req, energyCompany.getDefaultTimeZone());
				StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
				deleteHw.setInventoryID( Integer.parseInt(req.getParameter("OrigInvID")) );
				operation.setStarsDeleteLMHardware( deleteHw );
				session.setAttribute( STARS_INVENTORY_OPERATION, operation );
				
				if (liteInv != null) {
					operation.getStarsUpdateLMHardware().setInventoryID( liteInv.getInventoryID() );
					operation.getStarsUpdateLMHardware().setDeviceID( liteInv.getDeviceID() );
				}
				
				// Forward to DeleteInv.jsp to confirm removal of the old hardware
				LiteInventoryBase liteInvOld = energyCompany.getInventory( deleteHw.getInventoryID(), true );
				session.setAttribute( INVENTORY_TO_DELETE, liteInvOld );
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
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		LiteInventoryBase liteInv = (LiteInventoryBase) session.getAttribute( INVENTORY_TO_CHECK );
		StarsInventory starsInv = null;
		
		if (liteInv != null) {
			if (liteInv.getInventoryID() > 0) {
				starsInv = (StarsInventory) StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
				starsInv.setRemoveDate( null );
			}
			else {
				// This is a device to be added to the inventory
				starsInv = (StarsInventory) StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
			}
		}
		else {
			// This is a LM hardware to be added to the inventory
			starsInv = (StarsInventory) session.getAttribute( STARS_INVENTORY_TEMP );
		}
		
		starsInv.setInstallDate( new Date() );
		starsInv.setInstallationNotes( "" );
		
		String invNo = (String) session.getAttribute(STARS_INVENTORY_NO);
		session.setAttribute( STARS_INVENTORY_TEMP + invNo, starsInv );
		
		redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
	}
	
	/**
	 * The delete button is clicked on Inventory.jsp 
	 */
	private void deleteLMHardware(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		StarsOperation operation = DeleteLMHardwareAction.getRequestOperation( req, energyCompany.getDefaultTimeZone() );
		session.setAttribute( STARS_INVENTORY_OPERATION, operation );
		
		LiteInventoryBase liteInv = energyCompany.getInventory( operation.getStarsDeleteLMHardware().getInventoryID(), true );
		session.setAttribute( INVENTORY_TO_DELETE, liteInv );
		
		redirect = req.getContextPath() + "/operator/Consumer/DeleteInv.jsp"; 
	}
	
	/**
	 * Confirmation from DeleteInv.jsp 
	 */
	private void confirmDelete(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		StarsOperation operation = (StarsOperation) session.getAttribute(STARS_INVENTORY_OPERATION);
		operation.getStarsDeleteLMHardware().setDeleteFromInventory(
				Boolean.valueOf(req.getParameter("DeletePerm")).booleanValue() );
		
		redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
	}
	
	/**
	 * The submit button is clicked on InventoryDetail.jsp 
	 */
	private void updateInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			int invID = Integer.parseInt( req.getParameter("InvID") );
			LiteInventoryBase liteInv = energyCompany.getInventoryBrief( invID, true );
			
			StarsOperation operation = UpdateLMHardwareAction.getRequestOperation(req, energyCompany.getDefaultTimeZone());
			UpdateLMHardwareAction.updateInventory( operation.getStarsUpdateLMHardware(), liteInv, energyCompany );
			
			if (req.getParameter("UseHardwareAddressing") != null) {
				StarsLMConfiguration starsCfg = new StarsLMConfiguration();
				setStarsLMConfiguration( starsCfg, req );
				UpdateLMHardwareConfigAction.updateLMConfiguration( starsCfg, (LiteStarsLMHardware)liteInv );
			}
			
			if (liteInv.getAccountID() > 0) {
				StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteInv.getAccountID() );
				if (starsAcctInfo != null) {
					StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
					UpdateLMHardwareAction.parseResponse( liteInv.getInventoryID(), starsInv, starsAcctInfo, null );
				}
			}
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
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		int invID = Integer.parseInt( req.getParameter("InvID") );
		LiteInventoryBase liteInv = energyCompany.getInventoryBrief( invID, true );
		
		if (liteInv.getAccountID() == 0) {
			com.cannontech.database.data.stars.hardware.InventoryBase inventory =
					new com.cannontech.database.data.stars.hardware.InventoryBase();
			inventory.setInventoryID( new Integer(invID) );
			
			try {
				Transaction.createTransaction( Transaction.DELETE, inventory ).execute();
				energyCompany.deleteInventory( invID );
			}
			catch (TransactionException e) {
				CTILogger.error( e.getMessage(), e );
			}
		}
		else {
			StarsOperation operation = DeleteLMHardwareAction.getRequestOperation( req, energyCompany.getDefaultTimeZone() );
			session.setAttribute( STARS_INVENTORY_OPERATION, operation );
			redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
		}
	}
	
	/**
	 * The config button is clicked on InventoryDetail.jsp
	 */
	private void configLMHardware(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			int invID = Integer.parseInt( req.getParameter("InvID") );
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventoryBrief( invID, true );
			
			if (req.getParameter("UseHardwareAddressing") != null) {
				StarsLMConfiguration starsCfg = new StarsLMConfiguration();
				setStarsLMConfiguration( starsCfg, req );
				UpdateLMHardwareConfigAction.updateLMConfiguration( starsCfg, liteHw );
			}
			
			YukonSwitchCommandAction.sendConfigCommand( energyCompany, liteHw, true );
			
			if (liteHw.getAccountID() > 0) {
				StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteHw.getAccountID() );
				if (starsAcctInfo != null) {
					StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, energyCompany );
					UpdateLMHardwareAction.parseResponse( liteHw.getInventoryID(), starsInv, starsAcctInfo, null );
				}
			}
			
			session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Hardware configuration updated successfully" );
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
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		Integer devTypeID = Integer.valueOf( req.getParameter("DeviceType") );
		int categoryID = ECUtils.getInventoryCategoryID( devTypeID.intValue(), energyCompany );
		if (ECUtils.isMCT( categoryID )) {
			String mctType = YukonListFuncs.getYukonListEntry( devTypeID.intValue() ).getEntryText();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Cannot add SN range for device type " + mctType);
			return;
		}
		
		int snFrom = 0, snTo = 0;
		try {
			snFrom = Integer.parseInt( req.getParameter("From") );
			if (req.getParameter("To").length() > 0)
				snTo = Integer.parseInt( req.getParameter("To") );
			else
				snTo = snFrom;
		}
		catch (NumberFormatException nfe) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Serial number must be numerical");
			return;
		}
		
		if (snFrom > snTo) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The 'from' value cannot be greater than the 'to' value");
			return;
		}
		
		Integer voltageID = Integer.valueOf( req.getParameter("Voltage") );
		Integer companyID = Integer.valueOf( req.getParameter("ServiceCompany") );
		
		Date recvDate = null;
		String recvDateStr = req.getParameter("ReceiveDate");
		if (recvDateStr.length() > 0) {
			recvDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(recvDateStr, energyCompany.getDefaultTimeZone());
			if (recvDate == null) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid receive date format, the date should be in the form of 'mm/dd/yy'");
				return;
			}
		}
		
		session.removeAttribute( ServletUtils.ATT_REDIRECT );
		
		AddSNRangeTask task = new AddSNRangeTask( snFrom, snTo, devTypeID, recvDate, voltageID, companyID, req );
		long id = ProgressChecker.addTask( task );
		
		// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			
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
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	/**
	 * Update information of hardwares in the given serial # range 
	 */
	private void updateSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		Integer devTypeID = Integer.valueOf( req.getParameter("DeviceType") );
		int categoryID = ECUtils.getInventoryCategoryID( devTypeID.intValue(), energyCompany );
		if (ECUtils.isMCT( categoryID )) {
			String mctType = YukonListFuncs.getYukonListEntry( devTypeID.intValue() ).getEntryText();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Cannot update SN range for device type " + mctType);
			return;
		}
		
		Integer newDevTypeID = (req.getParameter("NewDeviceType") != null)?
				Integer.valueOf( req.getParameter("NewDeviceType") ) : null;
		if (newDevTypeID != null) {
			if (newDevTypeID.intValue() == devTypeID.intValue()) {
				newDevTypeID = null;
			}
			else {
				int newCatID = ECUtils.getInventoryCategoryID( newDevTypeID.intValue(), energyCompany );
				if (ECUtils.isMCT( newCatID )) {
					String mctType = YukonListFuncs.getYukonListEntry( devTypeID.intValue() ).getEntryText();
					session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Cannot change device type to " + mctType);
					return;
				}
			}
		}
		
		String fromStr = req.getParameter("From");
		String toStr = req.getParameter("To");
		
		if (fromStr.equals("*")) {
			// Update all hardwares in inventory
			fromStr = toStr = null;
		}
		else {
			int snFrom = 0, snTo = 0;
			try {
				snFrom = Integer.parseInt( fromStr );
				if (req.getParameter("To").length() > 0)
					snTo = Integer.parseInt( toStr );
				else
					snTo = snFrom;
			}
			catch (NumberFormatException nfe) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid serial number format");
				return;
			}
			
			if (snFrom > snTo) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "'From' value must be less than or equal to 'to' value");
				return;
			}
			
			fromStr = String.valueOf( snFrom );
			toStr = String.valueOf( snTo );
		}
		
		Integer voltageID = (req.getParameter("Voltage") != null)?
				Integer.valueOf( req.getParameter("Voltage") ) : null;
		Integer companyID = (req.getParameter("ServiceCompany") != null)?
				Integer.valueOf( req.getParameter("ServiceCompany") ) : null;
		
		Date recvDate = null;
		String recvDateStr = req.getParameter("ReceiveDate");
		if (recvDateStr != null && recvDateStr.length() > 0) {
			recvDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(recvDateStr, energyCompany.getDefaultTimeZone());
			if (recvDate == null) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid receive date format, the date should be in the form of 'mm/dd/yy'");
				return;
			}
		}
		
		if (newDevTypeID == null && recvDate == null && voltageID == null && companyID == null)
			return;
		
		session.removeAttribute( ServletUtils.ATT_REDIRECT );
		
		UpdateSNRangeTask task = new UpdateSNRangeTask( fromStr, toStr, devTypeID, newDevTypeID, recvDate, voltageID, companyID, req );
		long id = ProgressChecker.addTask( task );
		
		// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			
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
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	/**
	 * Delete hardwares in the given serial # range 
	 */
	private void deleteSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		String fromStr = req.getParameter("From");
		String toStr = req.getParameter("To");
		
		if (fromStr.equals("*")) {
			// Delete all hardwares in inventory
			fromStr = toStr = null;
		}
		else {
			int snFrom = 0, snTo = 0;
			try {
				snFrom = Integer.parseInt( fromStr );
				if (req.getParameter("To").length() > 0)
					snTo = Integer.parseInt( toStr );
				else
					snTo = snFrom;
			}
			catch (NumberFormatException nfe) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid serial number format");
				return;
			}
			
			if (snFrom > snTo) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "'From' value must be less than or equal to 'to' value");
				return;
			}
			
			fromStr = String.valueOf( snFrom );
			toStr = String.valueOf( snTo );
		}
		
		Integer devTypeID = Integer.valueOf( req.getParameter("DeviceType") );
		
		int categoryID = ECUtils.getInventoryCategoryID( devTypeID.intValue(), energyCompany );
		if (ECUtils.isMCT(categoryID) && fromStr != null) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "If device type is MCT, the 'From' value must be '*' (delete all MCTs)");
			return;
		} 
		
		session.removeAttribute( ServletUtils.ATT_REDIRECT );
		
		DeleteSNRangeTask task = new DeleteSNRangeTask( fromStr, toStr, devTypeID, req );
		long id = ProgressChecker.addTask( task );
		
		// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			
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
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	/**
	 * Configure hardwares in the given serial # range 
	 */
	private void configSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		Integer devTypeID = Integer.valueOf( req.getParameter("DeviceType") );
		int categoryID = ECUtils.getInventoryCategoryID( devTypeID.intValue(), energyCompany );
		if (ECUtils.isMCT( categoryID )) {
			String mctType = YukonListFuncs.getYukonListEntry( devTypeID.intValue() ).getEntryText();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Cannot config SN range for device type " + mctType);
			return;
		}
		
		String fromStr = req.getParameter("From");
		String toStr = req.getParameter("To");
		
		if (fromStr.equals("*")) {
			// Update all hardwares in inventory
			fromStr = toStr = null;
		}
		else {
			int snFrom = 0, snTo = 0;
			try {
				snFrom = Integer.parseInt( fromStr );
				if (req.getParameter("To").length() > 0)
					snTo = Integer.parseInt( toStr );
				else
					snTo = snFrom;
			}
			catch (NumberFormatException nfe) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid serial number format");
				return;
			}
			
			if (snFrom > snTo) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "'From' value must be less than or equal to 'to' value");
				return;
			}
			
			fromStr = String.valueOf( snFrom );
			toStr = String.valueOf( snTo );
		}
		
		boolean configNow = req.getParameter("ConfigNow") != null;
		
		session.removeAttribute( ServletUtils.ATT_REDIRECT );
		
		ConfigSNRangeTask task = new ConfigSNRangeTask( fromStr, toStr, devTypeID, configNow, req );
		long id = ProgressChecker.addTask( task );
		
		// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			
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
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	/**
	 * Send all the scheduled switch commands
	 */
	private void sendSwitchCommands(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			sendSwitchCommands( energyCompany );
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Batched switch commands sent out successfully");
		}
		catch (WebClientException e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
		}
	}
	
	/**
	 * Clear all the scheduled switch commands 
	 */
	private void clearSwitchCommands(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		energyCompany.getSwitchCommandQueue().clearCommands( user.getEnergyCompanyID() );
		session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "All the scheduled switch commands are cleared");
	}
	
	private void searchInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		session.removeAttribute( INVENTORY_SET );
		
		int searchBy = Integer.parseInt( req.getParameter("SearchBy") );
		String searchValue = req.getParameter( "SearchValue" );
		if (searchValue.trim().length() == 0) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Search value cannot be empty");
			return;
		}
		
		boolean searchMembers = energyCompany.getChildren().size() > 0;
		ArrayList invList = null; 
		
		if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_SERIAL_NO) {
			invList = energyCompany.searchInventoryBySerialNo( searchValue, searchMembers );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ACCT_NO) {
			invList = energyCompany.searchInventoryByAccountNo( searchValue, searchMembers );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_PHONE_NO) {
			try {
				String phoneNo = ServletUtils.formatPhoneNumber( searchValue );
				invList = energyCompany.searchInventoryByPhoneNo( phoneNo, searchMembers );
			}
			catch (WebClientException e) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
				return;
			}
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_LAST_NAME) {
			invList = energyCompany.searchInventoryByLastName( searchValue, searchMembers );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ORDER_NO) {
			// TODO: The WorkOrderBase table doesn't have InventoryID column, maybe should be added
			if (AuthFuncs.checkRoleProperty( user.getYukonUser(), ConsumerInfoRole.ORDER_NUMBER_AUTO_GEN ))
				searchValue = ServerUtils.AUTO_GEN_NUM_PREC + searchValue;
			invList = energyCompany.searchInventoryByOrderNo( searchValue, searchMembers );
		}
		
		if (invList == null || invList.size() == 0) {
			session.setAttribute(INVENTORY_SET_DESC, "<div class='ErrorMsg' align='center'>No hardware found matching the search criteria.</div>");
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
				session.setAttribute(INVENTORY_SET, invList);
				session.setAttribute(INVENTORY_SET_DESC, "Click on a serial # (device name) to view the hardware details, or click on an account # (if available) to view the account information.");
				session.setAttribute(ServletUtils.ATT_REFERRER, referer);
			}
		}
	}
	
	private void createLMHardware(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			StarsCreateLMHardware createHw = new StarsCreateLMHardware();
			setStarsInv( createHw, req, energyCompany.getDefaultTimeZone() );
			LiteInventoryBase liteInv = CreateLMHardwareAction.addInventory( createHw, null, energyCompany );
			
			if (req.getParameter("UseHardwareAddressing") != null) {
				StarsLMConfiguration starsCfg = new StarsLMConfiguration();
				setStarsLMConfiguration( starsCfg, req );
				UpdateLMHardwareConfigAction.updateLMConfiguration( starsCfg, (LiteStarsLMHardware)liteInv );
			}
			
			// Append inventory ID to the redirect URL
			redirect += String.valueOf( liteInv.getInventoryID() );
		}
		catch (WebClientException e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
		}
	}
	
	/**
	 * Store hardware information entered by user into a StarsLMHw object 
	 */
	public static void setStarsInv(StarsInv starsInv, HttpServletRequest req, TimeZone tz) throws WebClientException {
		if (req.getParameter("InvID") != null)
			starsInv.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
		else
			starsInv.setInventoryID( -1 );
		
		if (req.getParameter("DeviceID") != null)
			starsInv.setDeviceID( Integer.parseInt(req.getParameter("DeviceID")) );
		if (req.getParameter("DeviceLabel") != null)
			starsInv.setDeviceLabel( req.getParameter("DeviceLabel") );
		if (req.getParameter("AltTrackNo") != null)
			starsInv.setAltTrackingNumber( req.getParameter("AltTrackNo") );
		if (req.getParameter("Notes") != null)
			starsInv.setNotes( req.getParameter("Notes").replaceAll(System.getProperty("line.separator"), "<br>") );
		if (req.getParameter("InstallNotes") != null)
			starsInv.setInstallationNotes( req.getParameter("InstallNotes").replaceAll(System.getProperty("line.separator"), "<br>") );
		
		String recvDateStr = req.getParameter("ReceiveDate");
		if (recvDateStr != null && recvDateStr.length() > 0) {
			Date recvDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(recvDateStr, tz);
			if (recvDate == null)
				throw new WebClientException("Invalid receive date format '" + recvDateStr + "', the date should be in the form of 'mm/dd/yy'");
			starsInv.setReceiveDate( recvDate );
		}
		
		String instDateStr = req.getParameter("InstallDate");
		if (instDateStr != null && instDateStr.length() > 0) {
			Date instDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(instDateStr, tz);
			if (instDate == null)
				throw new WebClientException("Invalid install date format '" + instDateStr + "', the date should be in the form of 'mm/dd/yy'");
			starsInv.setInstallDate( instDate );
		}
		
		String remvDateStr = req.getParameter("RemoveDate");
		if (remvDateStr != null && remvDateStr.length() > 0) {
			Date remvDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(remvDateStr, tz);
			if (remvDate == null)
				throw new WebClientException("Invalid remove date format '" + remvDateStr + "', the date should be in the form of 'mm/dd/yy'");
			starsInv.setRemoveDate( remvDate );
		}
		
		if (req.getParameter("Voltage") != null) {
			Voltage volt = new Voltage();
			volt.setEntryID( Integer.parseInt(req.getParameter("Voltage")) );
			starsInv.setVoltage( volt );
		}
		
		if (req.getParameter("ServiceCompany") != null) {
			InstallationCompany company = new InstallationCompany();
			company.setEntryID( Integer.parseInt(req.getParameter("ServiceCompany")) );
			starsInv.setInstallationCompany( company );
		}
		
		if (req.getParameter("DeviceType") != null) {
			DeviceType devType = new DeviceType();
			devType.setEntryID( Integer.parseInt(req.getParameter("DeviceType")) );
			starsInv.setDeviceType( devType );
		}
		
		int devTypeDefID = YukonListFuncs.getYukonListEntry( starsInv.getDeviceType().getEntryID() ).getYukonDefID();
		if (devTypeDefID != YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT) {
			LMHardware hw = new LMHardware();
			hw.setManufacturerSerialNumber( req.getParameter("SerialNo") );
			if (req.getParameter("Route") != null)
				hw.setRouteID( Integer.parseInt(req.getParameter("Route")) );
			starsInv.setLMHardware( hw );
		}
	}
	
	/**
	 * Search for devices with specified category and device name
	 */
	public static ArrayList searchDevice(int categoryID, String deviceName) {
		ArrayList devList = new ArrayList();
		java.util.List allDevices = null;
		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized (cache) {
			if (ECUtils.isMCT( categoryID ))
				allDevices = cache.getAllMCTs();
			
			if (deviceName == null || deviceName.length() == 0) {
				devList.addAll( allDevices );
			}
			else {
				for (int i = 0; i < allDevices.size(); i++) {
					LiteYukonPAObject litePao = (LiteYukonPAObject) allDevices.get(i);
					if (PAOFuncs.getYukonPAOName( litePao.getYukonID() ).startsWith( deviceName ))
						devList.add( litePao );
				}
			}
		}
		
		return devList;
	}
	
	public static void sendSwitchCommands(LiteStarsEnergyCompany energyCompany) throws WebClientException {
		SwitchCommandQueue queue = energyCompany.getSwitchCommandQueue();
		if (queue == null)
			throw new WebClientException( "Failed to retrieve the batched switch commands" );
		
		SwitchCommandQueue.SwitchCommand[] commands = queue.getCommands( energyCompany.getLiteID() );
		if (commands.length == 0)
			throw new WebClientException( "There is no batched switch command" );
		
		for (int i = 0; i < commands.length; i++) {
			try {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory(commands[i].getInventoryID(), true);
				
				StarsCustAccountInformation starsAcctInfo = null;
				if (liteHw.getAccountID() > 0)
					starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteHw.getAccountID() );
				
				if (commands[i].getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE ))
					YukonSwitchCommandAction.sendConfigCommand( energyCompany, liteHw, true );
				else if (commands[i].getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_DISABLE ))
					YukonSwitchCommandAction.sendDisableCommand( energyCompany, liteHw );
				else if (commands[i].getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_ENABLE ))
					YukonSwitchCommandAction.sendEnableCommand( energyCompany, liteHw );
				
				if (starsAcctInfo != null) {
					StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, energyCompany );
					YukonSwitchCommandAction.parseResponse( starsAcctInfo, starsInv );
				}
			}
			catch (WebClientException e) {
				String errorMsg = "Error in command '" + commands[i].toString() + "'" +
						System.getProperty( "line.separator" ) + e.getMessage();
				CTILogger.error( errorMsg, e );
			}
		}
	}
	
	public static void setStarsLMConfiguration(StarsLMConfiguration starsCfg, HttpServletRequest req) throws WebClientException {
		String[] clps = req.getParameterValues( "ColdLoadPickup" );
		String[] tds = req.getParameterValues( "TamperDetect" );
		
		String clp = "";
		if (clps != null) {
			if (clps.length > 0) clp += clps[0];
			for (int i = 1; i < clps.length; i++)
				clp += "," + clps[i];
		}
		starsCfg.setColdLoadPickup( clp );
		
		String td = "";
		if (tds != null) {
			if (tds.length > 0) td += tds[0];
			for (int i = 1; i < tds.length; i++)
				td += "," + tds[i];
		}
		starsCfg.setTamperDetect( td );
		
		if (req.getParameter("SA205_Slot1") != null) {
			SA205 sa205 = new SA205();
			
			sa205.setSlot1( ServletUtils.parseNumber(req.getParameter("SA205_Slot1"), 0, 4095, "Slot Address") );
			sa205.setSlot2( ServletUtils.parseNumber(req.getParameter("SA205_Slot2"), 0, 4095, "Slot Address") );
			sa205.setSlot3( ServletUtils.parseNumber(req.getParameter("SA205_Slot3"), 0, 4095, "Slot Address") );
			sa205.setSlot4( ServletUtils.parseNumber(req.getParameter("SA205_Slot4"), 0, 4095, "Slot Address") );
			sa205.setSlot5( ServletUtils.parseNumber(req.getParameter("SA205_Slot5"), 0, 4095, "Slot Address") );
			sa205.setSlot6( ServletUtils.parseNumber(req.getParameter("SA205_Slot6"), 0, 4095, "Slot Address") );
			
			starsCfg.setSA205( sa205 );
		}
		else if (req.getParameter("SA305_Utility") != null) {
			SA305 sa305 = new SA305();
			
			sa305.setUtility( ServletUtils.parseNumber(req.getParameter("SA305_Utility"), 0, 15, "Utility", true) );
			sa305.setGroup( ServletUtils.parseNumber(req.getParameter("SA305_Group"), 0, 63, "Group") );
			sa305.setDivision( ServletUtils.parseNumber(req.getParameter("SA305_Division"), 0, 63, "Division") );
			sa305.setSubstation( ServletUtils.parseNumber(req.getParameter("SA305_Substation"), 0, 1023, "Substation") );
			sa305.setRateFamily( ServletUtils.parseNumber(req.getParameter("SA305_RateFamily"), 0, 7, "Rate Family", true) );
			sa305.setRateMember( ServletUtils.parseNumber(req.getParameter("SA305_RateMember"), 0, 15, "Rate Member", true) );
			sa305.setRateHierarchy( ServletUtils.parseNumber(req.getParameter("SA305_RateHierarchy"), 0, 1, "Rate Hierarchy", true) );
			
			starsCfg.setSA305( sa305 );
		}
		else if (req.getParameter("VCOM_Utility") != null) {
			VersaCom vcom = new VersaCom();
			
			vcom.setUtility( ServletUtils.parseNumber(req.getParameter("VCOM_Utility"), 0, 255, "Utility", true) );
			vcom.setSection( ServletUtils.parseNumber(req.getParameter("VCOM_Section"), 1, 254, "Section") );
			
			String[] classAddrs = req.getParameterValues( "VCOM_Class" );
			int classAddr = 0;
			for (int i = 0; i < classAddrs.length; i++)
				classAddr += Integer.parseInt( classAddrs[i] );
			vcom.setClassAddress( classAddr );
			
			String[] divisions = req.getParameterValues( "VCOM_Division" );
			int division = 0;
			for (int i = 0; i < divisions.length; i++)
				division += Integer.parseInt( divisions[i] );
			vcom.setDivision( division );
			
			starsCfg.setVersaCom( vcom );
		}
		else if (req.getParameter("XCOM_SPID") != null) {
			ExpressCom xcom = new ExpressCom();
			
			xcom.setServiceProvider( ServletUtils.parseNumber(req.getParameter("XCOM_SPID"), 0, 65534, "SPID", true) );
			xcom.setGEO( ServletUtils.parseNumber(req.getParameter("XCOM_GEO"), 0, 65534, "GEO") );
			xcom.setSubstation( ServletUtils.parseNumber(req.getParameter("XCOM_SUB"), 0, 65534, "SUB") );
			xcom.setZip( ServletUtils.parseNumber(req.getParameter("XCOM_ZIP"), 0, 16777214, "ZIP") );
			xcom.setUserAddress( ServletUtils.parseNumber(req.getParameter("XCOM_USER"), 0, 65534, "USER") );
			
			String[] feeders = req.getParameterValues( "XCOM_FEED" );
			int feeder = 0;
			for (int i = 0; i < feeders.length; i++)
				feeder += Integer.parseInt( feeders[i] );
			xcom.setFeeder( feeder );
			
			String[] programs = req.getParameterValues("XCOM_Program");
			String program = "";
			for (int i = 0; i < programs.length; i++) {
				ServletUtils.parseNumber(programs[i], 0, 254, "Program" );
				program += programs[i];
				if (i < programs.length - 1) program += ",";
			}
			xcom.setProgram( program );
			
			String[] splinters = req.getParameterValues("XCOM_Splinter");
			String splinter = "";
			for (int i = 0; i < splinters.length; i++) {
				ServletUtils.parseNumber(splinters[i], 0, 254, "Splinter" );
				splinter += String.valueOf( splinters[i] );
				if (i < splinters.length - 1) splinter += ",";
			}
			xcom.setSplinter( splinter );
			
			starsCfg.setExpressCom( xcom );
		}
	}

}
