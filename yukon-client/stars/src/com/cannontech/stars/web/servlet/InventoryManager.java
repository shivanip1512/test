package com.cannontech.stars.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
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
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.DeviceType;
import com.cannontech.stars.xml.serialize.InstallationCompany;
import com.cannontech.stars.xml.serialize.LMHardware;
import com.cannontech.stars.xml.serialize.MCT;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsInv;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsOperation;
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
		
		if (user.getAttribute(ServletUtils.ATT_CONTEXT_SWITCHED) != null) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Operation not allowed because you are currently checking information of a member. To make any changes, you must first log into the member energy company through \"Member Management\"." );
			resp.sendRedirect( referer );
			return;
		}
		
		if (action.equalsIgnoreCase( "SelectInventory" ))
			selectInventory( user, req, session );
		else if (action.equalsIgnoreCase( "SelectDevice" ))
			selectDevice( user, req, session );
		else if (action.equalsIgnoreCase( "CheckInventory" )) {
			session.setAttribute( ServletUtils.ATT_REDIRECT, redirect );
			session.setAttribute( ServletUtils.ATT_REFERRER, referer );
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
		else if (action.equalsIgnoreCase("UpdateInventory")) {
			String redir = req.getContextPath() + "/servlet/SOAPClient?action=UpdateLMHardware" +
					"&REDIRECT=" + req.getParameter(ServletUtils.ATT_REDIRECT) +
					"&REFERRER=" + req.getParameter(ServletUtils.ATT_REFERRER);
			session.setAttribute( ServletUtils.ATT_REDIRECT, redir );
			updateInventory( user, req, session );
		}
		else if (action.equalsIgnoreCase("DeleteInventory")) {
			String redir = req.getContextPath() + "/servlet/SOAPClient?action=DeleteLMHardware" +
					"&REDIRECT=" + req.getParameter(ServletUtils.ATT_REDIRECT) +
					"&REFERRER=" + req.getParameter(ServletUtils.ATT_REFERRER);
			session.setAttribute( ServletUtils.ATT_REDIRECT, redir );
			deleteInventory( user, req, session );
		}
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
		
		resp.sendRedirect( redirect );
	}
	
	/**
	 * Called from SelectInv.jsp when a hardware is selected. If the hardware is in the warehouse,
	 * populate its information for the next page, otherwise go to CheckInv.jsp asking for confirmation. 
	 */
	private void selectInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		int invID = Integer.parseInt( req.getParameter("InvID") );
		LiteInventoryBase liteInv = energyCompany.getInventory( invID, true );
		
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
			redirect = req.getContextPath() + "/operator/Consumer/CheckInv.jsp?InOther=true";
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
			else
				redirect = req.getContextPath() + "/operator/Consumer/CheckInv.jsp?InOther=true";
			
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
			int devTypeID = Integer.parseInt( req.getParameter("DeviceType") );
			int invID = Integer.parseInt( req.getParameter("InvID") );
			LiteInventoryBase liteInv = energyCompany.getInventoryBrief( invID, true );
			
			StarsOperation operation = UpdateLMHardwareAction.getRequestOperation(req, energyCompany.getDefaultTimeZone());
			if (liteInv.getAccountID() ==  0) {
				UpdateLMHardwareAction.updateInventory( operation.getStarsUpdateLMHardware(), liteInv, energyCompany );
			}
			else {
				session.setAttribute( STARS_INVENTORY_OPERATION, operation );
				redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
			}
		}
		catch (WebClientException e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
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
			}
			catch (TransactionException e) {
				e.printStackTrace();
			}
		}
		else {
			StarsOperation operation = DeleteLMHardwareAction.getRequestOperation( req, energyCompany.getDefaultTimeZone() );
			session.setAttribute( STARS_INVENTORY_OPERATION, operation );
			redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
		}
	}
	
	/**
	 * Add hardwares in the given serial # range to inventory 
	 */
	private void addSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		int snFrom = 0, snTo = 0;
		try {
			snFrom = Integer.parseInt( req.getParameter("From") );
			if (req.getParameter("To").length() > 0)
				snTo = Integer.parseInt( req.getParameter("To") );
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
		
		Integer devTypeID = Integer.valueOf( req.getParameter("DeviceType") );
		Integer voltageID = Integer.valueOf( req.getParameter("Voltage") );
		Integer companyID = Integer.valueOf( req.getParameter("ServiceCompany") );
		Integer categoryID = new Integer( ECUtils.getInventoryCategoryID(devTypeID.intValue(), energyCompany) );
		
		Date recvDate = null;
		String recvDateStr = req.getParameter("ReceiveDate");
		if (recvDateStr.length() > 0) {
			recvDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(recvDateStr, energyCompany.getDefaultTimeZone());
			if (recvDate == null) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid format for receive date.");
				return;
			}
		}
		
		ArrayList hardwareSet = new ArrayList();
		ArrayList serialNoSet = new ArrayList();
		int numSuccess = 0, numFailure = 0;
		
		TreeMap snTable = com.cannontech.database.db.stars.hardware.LMHardwareBase.searchBySNRange(
				devTypeID.intValue(), String.valueOf(snFrom), String.valueOf(snTo), user.getEnergyCompanyID() );
		if (snTable == null) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to add serial range" );
			return;
		}
		
		for (int sn = snFrom; sn <= snTo; sn++) {
			String serialNo = String.valueOf(sn);
			Integer invID = (Integer) snTable.get( serialNo );
			if (invID != null) {
				hardwareSet.add( energyCompany.getInventoryBrief(invID.intValue(), true) );
				numFailure++;
				continue;
			}
			
			try {
				com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
						new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hardware.getLMHardwareBase();
				com.cannontech.database.db.stars.hardware.InventoryBase invDB = hardware.getInventoryBase();
				
				invDB.setInstallationCompanyID( companyID );
				invDB.setCategoryID( categoryID );
				if (recvDate != null)
					invDB.setReceiveDate( recvDate );
				invDB.setVoltageID( voltageID );
				invDB.setDeviceLabel( serialNo );
				hwDB.setManufacturerSerialNumber( serialNo );
				hwDB.setLMHardwareTypeID( devTypeID );
				hardware.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				
				hardware = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
						Transaction.createTransaction( Transaction.INSERT, hardware ).execute();
				
				LiteStarsLMHardware liteHw = new LiteStarsLMHardware();
				StarsLiteFactory.setLiteStarsLMHardware( liteHw, hardware );
				energyCompany.addInventory( liteHw );
				
				numSuccess++;
			}
			catch (TransactionException e) {
				CTILogger.error( e.getMessage(), e );
				serialNoSet.add( serialNo );
				numFailure++;
			}
		}
		
		session.removeAttribute( INVENTORY_SET );
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " hardwares added to inventory successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " hardwares failed (serial numbers may already exist). They are listed below:</span><br>";
			if (serialNoSet.size() > 0) {
				resultDesc += "<br><table width='100' cellspacing='0' cellpadding='0' border='0' align='center' class='TableCell'>";
				for (int i = 0; i < serialNoSet.size(); i++) {
					String serialNo = (String) serialNoSet.get(i);
					resultDesc += "<tr><td align='center'>" + serialNo + "</td></tr>";
				}
				resultDesc += "</table><br>";
			}
			
			session.setAttribute(INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REFERRER, referer);
			redirect = req.getContextPath() + "/operator/Hardware/ResultSet.jsp";
		}
		else if (numSuccess > 0) {
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, numSuccess + " hardwares added to inventory successfully");
		}
	}
	
	/**
	 * Update information of hardwares in the given serial # range 
	 */
	private void updateSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
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
		
		Integer devTypeID = Integer.valueOf( req.getParameter("DeviceType") );
		Integer voltageID = (req.getParameter("Voltage") != null)?
				Integer.valueOf( req.getParameter("Voltage") ) : null;
		Integer companyID = (req.getParameter("ServiceCompany") != null)?
				Integer.valueOf( req.getParameter("ServiceCompany") ) : null;
		
		Date recvDate = null;
		String recvDateStr = req.getParameter("ReceiveDate");
		if (recvDateStr != null && recvDateStr.length() > 0) {
			recvDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(recvDateStr, energyCompany.getDefaultTimeZone());
			if (recvDate == null) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid format for receive date.");
				return;
			}
		}
		
		if (recvDate == null && voltageID == null && companyID == null)
			return;
		
		ArrayList hardwareSet = new ArrayList();
		int numSuccess = 0, numFailure = 0;
		
		TreeMap snTable = com.cannontech.database.db.stars.hardware.LMHardwareBase.searchBySNRange(
				devTypeID.intValue(), fromStr, toStr, user.getEnergyCompanyID() );
		if (snTable == null) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to update serial range" );
			return;
		}
		
		java.util.Iterator it = snTable.values().iterator();
		while (it.hasNext()) {
			Integer invID = (Integer) it.next();
			LiteInventoryBase liteInv = energyCompany.getInventoryBrief( invID.intValue(), true );
			
			try {
				com.cannontech.database.db.stars.hardware.InventoryBase invDB =
						new com.cannontech.database.db.stars.hardware.InventoryBase();
				StarsLiteFactory.setInventoryBase( invDB, liteInv );
				
				if (companyID != null)
					invDB.setInstallationCompanyID( companyID );
				if (recvDate != null)
					invDB.setReceiveDate( recvDate );
				if (voltageID != null)
					invDB.setVoltageID( voltageID );
				
				invDB = (com.cannontech.database.db.stars.hardware.InventoryBase)
						Transaction.createTransaction( Transaction.UPDATE, invDB ).execute();
				
				StarsLiteFactory.setLiteInventoryBase( liteInv, invDB );
				numSuccess++;
			}
			catch (TransactionException e) {
				CTILogger.error( e.getMessage(), e );
				hardwareSet.add( liteInv );
				numFailure++;
			}
		}
		
		session.removeAttribute( INVENTORY_SET );
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " hardwares updated successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " hardwares failed. They are listed below:</span><br>";
			
			session.setAttribute(INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REFERRER, referer);
			redirect = req.getContextPath() + "/operator/Hardware/ResultSet.jsp";
		}
		else if (numSuccess > 0) {
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, numSuccess + " hardwares updated successfully");
		}
	}
	
	/**
	 * Delete hardwares in the given serial # range 
	 */
	private void deleteSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
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
		
		Integer devTypeID = Integer.valueOf( req.getParameter("DeviceType") );
		int categoryID = ECUtils.getInventoryCategoryID( devTypeID.intValue(), energyCompany );
		
		// If device type is MCT, the from string must be "*" (delete all MCTs)
		if (ECUtils.isMCT(categoryID) && fromStr != null) return;
		
		int numSuccess = 0, numFailure = 0;
		ArrayList hardwareSet = new ArrayList();
		
		if (ECUtils.isMCT(categoryID)) {
			ArrayList inventory = energyCompany.loadAllInventory();
			ArrayList mctList = new ArrayList();
			
			synchronized (inventory) {
				for (int i = 0; i < inventory.size(); i++) {
					LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
					if (ECUtils.isMCT( liteInv.getCategoryID() )) {
						if (liteInv.getAccountID() > 0) {
							hardwareSet.add( liteInv );
							numFailure++;
						}
						else
							mctList.add( liteInv );
					}
				}
			}
			
			for (int i = 0; i < mctList.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) mctList.get(i);
				
				try {
					com.cannontech.database.data.stars.hardware.InventoryBase inv =
							new com.cannontech.database.data.stars.hardware.InventoryBase();
					inv.setInventoryID( new Integer(liteInv.getInventoryID()) );
					
					Transaction.createTransaction( Transaction.DELETE, inv ).execute();
					
					energyCompany.deleteInventory( liteInv.getInventoryID() );
					numSuccess++;
				}
				catch (TransactionException e) {
					e.printStackTrace();
					hardwareSet.add( liteInv );
					numFailure++;
				}
			}
		}
		else {
			java.util.TreeMap snTable = com.cannontech.database.db.stars.hardware.LMHardwareBase.searchBySNRange(
					devTypeID.intValue(), fromStr, toStr, user.getEnergyCompanyID() );
			if (snTable == null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete serial range" );
				return;
			}
			
			java.util.Iterator it = snTable.values().iterator();
			while (it.hasNext()) {
				Integer invID = (Integer) it.next();
				LiteInventoryBase liteInv = energyCompany.getInventoryBrief( invID.intValue(), true );
				
				if (liteInv.getAccountID() > 0) {
					hardwareSet.add( liteInv );
					numFailure++;
					continue;
				}
				
				try {
					com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
							new com.cannontech.database.data.stars.hardware.LMHardwareBase();
					hardware.setInventoryID( invID );
					
					Transaction.createTransaction( Transaction.DELETE, hardware ).execute();
					
					energyCompany.deleteInventory( invID.intValue() );
					numSuccess++;
				}
				catch (TransactionException e) {
					CTILogger.error( e.getMessage(), e );
					hardwareSet.add( liteInv );
					numFailure++;
				}
			}
		}
		
		session.removeAttribute( INVENTORY_SET );
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " hardwares deleted successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " hardwares failed (If a hardware is assigned to an account, you must remove it from the account before deleting it). " +
					"They are listed below:</span><br>";
			
			session.setAttribute(INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REFERRER, referer);
			redirect = req.getContextPath() + "/operator/Hardware/ResultSet.jsp";
		}
		else if (numSuccess > 0) {
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, numSuccess + " hardwares deleted successfully");
		}
	}
	
	/**
	 * Configure hardwares in the given serial # range 
	 */
	private void configSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
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
		
		Integer devTypeID = Integer.valueOf( req.getParameter("DeviceType") );
		boolean configNow = req.getParameter("ConfigNow") != null;
		
		SwitchCommandQueue cmdQueue = (configNow)?
				null : energyCompany.getSwitchCommandQueue();
		
		int numSuccess = 0, numFailure = 0;
		ArrayList hardwareSet = new ArrayList();
		
		java.util.TreeMap snTable = com.cannontech.database.db.stars.hardware.LMHardwareBase.searchBySNRange(
				devTypeID.intValue(), fromStr, toStr, user.getEnergyCompanyID() );
		if (snTable == null) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to config serial range" );
			return;
		}
		
		java.util.Iterator it = snTable.values().iterator();
		while (it.hasNext()) {
			Integer invID = (Integer) it.next();
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory(invID.intValue(), true);
			
			try {
				if (configNow) {
					YukonSwitchCommandAction.sendConfigCommand(energyCompany, liteHw, true);
				}
				else {
					SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
					cmd.setEnergyCompanyID( user.getEnergyCompanyID() );
					cmd.setInventoryID( invID.intValue() );
					cmd.setSerialNumber( liteHw.getManufacturerSerialNumber() );
					cmd.setCommandType( SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE );
					cmdQueue.addCommand( cmd, false );
				}
				
				numSuccess++;
			}
			catch (WebClientException e) {
				CTILogger.error( e.getMessage() , e );
				hardwareSet.add( liteHw );
				numFailure++;
			}
		}
		
		if (!configNow) cmdQueue.addCommand( null, true );
		
		session.removeAttribute( INVENTORY_SET );
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + "Configuration of " + numSuccess + " hardwares " +
					((configNow)? "sent out" : "scheduled") + " successfully.</span><br>";
			resultDesc += "<span class='ErrorMsg'>" + numFailure + " hardware(s) failed. They are listed below:</span><br>";
			
			session.setAttribute(INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REFERRER, referer);
			redirect = req.getContextPath() + "/operator/Hardware/ResultSet.jsp";
		}
		else if (numSuccess > 0) {
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Configuration of " + numSuccess + " hardwares " +
					((configNow)? "sent out" : "scheduled") + " successfully.");
		}
	}
	
	/**
	 * Send all the scheduled switch commands
	 */
	private void sendSwitchCommands(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			SwitchCommandQueue.SwitchCommand[] commands =
					energyCompany.getSwitchCommandQueue().getCommands( user.getEnergyCompanyID() );
			if (commands.length == 0) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "There is no scheduled switch command");
				return;
			}
			
			for (int i = 0; i < commands.length; i++) {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory(commands[i].getInventoryID(), true);
				
				if (commands[i].getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE ))
					YukonSwitchCommandAction.sendConfigCommand( energyCompany, liteHw, true );
				else if (commands[i].getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_ENABLE ))
					YukonSwitchCommandAction.sendEnableCommand( energyCompany, liteHw );
				else if (commands[i].getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_DISABLE ))
					YukonSwitchCommandAction.sendDisableCommand( energyCompany, liteHw );
			}
			
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, commands.length + " scheduled switch commands sent out successfully");
		}
		catch (WebClientException e) {
			e.printStackTrace();
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
		LiteInventoryBase[] hardwares = null; 
		
		if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_SERIAL_NO) {
			hardwares = energyCompany.searchInventoryBySerialNo( searchValue, searchMembers );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ACCT_NO) {
			hardwares = energyCompany.searchInventoryByAccountNo( searchValue, searchMembers );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_PHONE_NO) {
			try {
				String phoneNo = ServletUtils.formatPhoneNumber( searchValue );
				hardwares = energyCompany.searchInventoryByPhoneNo( phoneNo, searchMembers );
			}
			catch (WebClientException e) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
				return;
			}
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_LAST_NAME) {
			hardwares = energyCompany.searchInventoryByLastName( searchValue, searchMembers );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ORDER_NO) {
			// TODO: The WorkOrderBase table doesn't have InventoryID column, maybe should be added
			if (AuthFuncs.checkRoleProperty( user.getYukonUser(), ConsumerInfoRole.ORDER_NUMBER_AUTO_GEN ))
				searchValue = ServerUtils.AUTO_GEN_NUM_PREC + searchValue;
			hardwares = energyCompany.searchInventoryByOrderNo( searchValue, searchMembers );
		}
		
		if (hardwares == null || hardwares.length == 0) {
			session.setAttribute(INVENTORY_SET_DESC, "<div class='ErrorMsg' align='center'>No hardwares found matching the search criteria.</div>");
		}
		else {
			LiteInventoryBase liteInv = null;
			if (hardwares.length == 1)
				liteInv = energyCompany.getInventoryBrief( hardwares[0].getInventoryID(), false );
			
			if (liteInv != null) {
				redirect = req.getContextPath() + "/operator/Hardware/InventoryDetail.jsp?InvId=" + liteInv.getInventoryID() + "&src=Search";
			}
			else {
				ArrayList invList = new ArrayList();
				for (int i = 0; i < hardwares.length; i++)
					invList.add( hardwares[i] );
				
				session.setAttribute(INVENTORY_SET, invList);
				session.setAttribute(INVENTORY_SET_DESC, "Click on a serial # (device name) to view the hardware details, or click on an account # (if available) to view the account information.");
				session.setAttribute(ServletUtils.ATT_REFERRER, referer);
			}
		}
	}
	
	/**
	 * Store hardware information entered by user into a StarsLMHw object 
	 */
	public static void setStarsInv(StarsInv starsInv, HttpServletRequest req, TimeZone tz) throws WebClientException {
		if (req.getParameter("InvID") != null)
			starsInv.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
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
				throw new WebClientException("Invalid date format '" + recvDateStr + "'");
			starsInv.setReceiveDate( recvDate );
		}
		
		String instDateStr = req.getParameter("InstallDate");
		if (instDateStr != null && instDateStr.length() > 0) {
			Date instDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(instDateStr, tz);
			if (instDate == null)
				throw new WebClientException("Invalid date format '" + instDateStr + "'");
			starsInv.setInstallDate( instDate );
		}
		
		String remvDateStr = req.getParameter("RemoveDate");
		if (remvDateStr != null && remvDateStr.length() > 0) {
			Date remvDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(remvDateStr, tz);
			if (remvDate == null)
				throw new WebClientException("Invalid date format '" + remvDateStr + "'");
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

}
