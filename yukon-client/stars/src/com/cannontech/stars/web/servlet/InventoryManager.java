package com.cannontech.stars.web.servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.InstallationCompany;
import com.cannontech.stars.xml.serialize.LMDeviceType;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsLMHardware;
import com.cannontech.stars.xml.serialize.StarsLMHw;
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
	
	public static final String INVENTORY_CHECKING_TIME_EARLY = "EARLY";
	public static final String INVENTORY_CHECKING_TIME_LATE = "LATE";
	public static final String INVENTORY_CHECKING_TIME_NONE = "NONE";
	
	public static final String STARS_LM_HARDWARE_TEMP = "STARS_LM_HARDWARE_TEMP";
	public static final String LM_HARDWARE_TO_CHECK = "LM_HARDWARE_TO_CHECK";
	public static final String LM_HARDWARE_TO_DELETE = "LM_HARDWARE_TO_DELETE";
	
	public static final String STARS_OPERATION_REQUEST = "STARS_OPERATION_REQUEST";
	
	public static final String NAV_BACK_STEP = "NAV_BACK_STEP";
	
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
        
        SOAPClient.initSOAPServer( req );
        
		referer = req.getHeader( "referer" );
		redirect = req.getParameter( ServletUtils.ATT_REDIRECT );
		if (redirect == null) redirect = referer;
		
		String action = req.getParameter( "action" );
		if (action == null) action = "";
		
		if (action.equalsIgnoreCase( "SelectInventory" ))
			selectInventory( user, req, session );
		else if (action.equalsIgnoreCase( "CheckInventory" )) {
			session.setAttribute( ServletUtils.ATT_REDIRECT, redirect );
			checkInventory( action, user, req, session );
		}
		else if (action.equalsIgnoreCase("CreateLMHardware")) {
			String redir = req.getContextPath() + "/servlet/SOAPClient?action=" + action;
			if (req.getParameter("Wizard") != null) redir += "&Wizard=true";
			session.setAttribute( ServletUtils.ATT_REDIRECT, redir );
			checkInventory( action, user, req, session );
		}
		else if (action.equalsIgnoreCase("UpdateLMHardware")) {
			String redir = req.getContextPath() + "/servlet/SOAPClient?action=" + action +
					"&REDIRECT=" + req.getParameter(ServletUtils.ATT_REDIRECT) +
					"&REFERRER=" + req.getParameter(ServletUtils.ATT_REFERRER);
			session.setAttribute( ServletUtils.ATT_REDIRECT, redir );
			checkInventory( action, user, req, session );
		}
		else if (action.equalsIgnoreCase("DeleteLMHardware")) {
			String redir = req.getContextPath() + "/servlet/SOAPClient?action=" + action +
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
		else if (action.equalsIgnoreCase("ConfigSNRange"))
			configSNRange( user, req, session );
		else if (action.equalsIgnoreCase("SendSwitchCommands")) {
			sendSwitchCommands( user, req, session );
		}
		
		resp.sendRedirect( redirect );
	}
	
	/**
	 * Called from SelectInv.jsp when a hardware is selected. If the hardware is in the warehouse,
	 * populate its information on the next page, otherwise go to CheckInv.jsp asking for confirmation. 
	 */
	private void selectInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		int invID = Integer.parseInt( req.getParameter("InvID") );
		LiteStarsLMHardware liteHw = energyCompany.getLMHardware(invID, true);
		
		if (liteHw.getAccountID() == CtiUtilities.NONE_ID) {
			// The hardware is in warehouse, so populate the hardware information
			StarsLMHardware starsHw = new StarsLMHardware();
			StarsLiteFactory.setStarsLMHardware( starsHw, liteHw, energyCompany );
			starsHw.setRemoveDate( null );
			starsHw.setInstallDate( new Date() );
			starsHw.setInstallationNotes( "" );
			session.setAttribute( STARS_LM_HARDWARE_TEMP, starsHw );
			
			redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
		}
		else {
			// The hardware is installed with another account, go to CheckInv.jsp to let the user confirm the action
			session.setAttribute(LM_HARDWARE_TO_CHECK, liteHw);
			redirect = req.getContextPath() + "/operator/Hardware/CheckInv.jsp";
		}
	}
	
	/**
	 * Search the inventory for hardware with the specified device type and serial #
	 */
	private void checkInventory(String action, StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		String invCheckTime = AuthFuncs.getRolePropertyValue(user.getYukonUser(), ConsumerInfoRole.INVENTORY_CHECKING_TIME);
		boolean invCheckEarly = invCheckTime.equalsIgnoreCase(INVENTORY_CHECKING_TIME_EARLY);
		boolean invCheckLate = invCheckTime.equalsIgnoreCase(INVENTORY_CHECKING_TIME_LATE);
		
		StarsLMHardware starsHw = (StarsLMHardware) StarsFactory.newStarsLMHw( StarsLMHardware.class );
		setStarsLMHardware( starsHw, req );
		starsHw.getLMDeviceType().setContent(
				energyCompany.getYukonListEntry(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, starsHw.getLMDeviceType().getEntryID()).getEntryText() );
		if (invCheckEarly)	// Request from SerialNumber.jsp
			session.setAttribute( STARS_LM_HARDWARE_TEMP, starsHw );
		
		LiteStarsLMHardware liteHw = energyCompany.searchForLMHardware(
				starsHw.getLMDeviceType().getEntryID(), starsHw.getManufactureSerialNumber() );
		if (invCheckEarly || invCheckLate)
			session.setAttribute( LM_HARDWARE_TO_CHECK, liteHw );
		
		if (action.equalsIgnoreCase("CreateLMHardware")) {
			// Request from CreateHardware.jsp, inventory checking time = LATE or NONE 
			StarsOperation operation = CreateLMHardwareAction.getRequestOperation( req );
			session.setAttribute( STARS_OPERATION_REQUEST, operation );
			if (!invCheckLate) {
				if (liteHw != null) operation.getStarsCreateLMHardware().setInventoryID( liteHw.getInventoryID() );
				redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
				return;
			}
		}
		else if (action.equalsIgnoreCase("UpdateLMHardware")) {
			// Request from Inventory.jsp, inventory checking time = LATE or NONE
			StarsOperation operation = UpdateLMHardwareAction.getRequestOperation( req );
			StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
			deleteHw.setInventoryID( Integer.parseInt(req.getParameter("OrigInvID")) );
			operation.setStarsDeleteLMHardware( deleteHw );
			session.setAttribute( STARS_OPERATION_REQUEST, operation );
			
			if (!invCheckLate) {
				if (liteHw != null) operation.getStarsUpdateLMHardware().setInventoryID( liteHw.getInventoryID() );
				// Forward to DeleteInv.jsp to confirm removal of the old hardware
				LiteStarsLMHardware liteHwOld = energyCompany.getLMHardware( deleteHw.getInventoryID(), true );
				session.setAttribute( LM_HARDWARE_TO_DELETE, liteHwOld );
				redirect = req.getContextPath() + "/operator/Hardware/DeleteInv.jsp";
				return;
			}
		}
		
		redirect = req.getContextPath() + "/operator/Hardware/CheckInv.jsp";
	}
	
	/**
	 * Confirmation from CheckInv.jsp 
	 */
	private void confirmCheck(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		LiteStarsLMHardware liteHw = (LiteStarsLMHardware) session.getAttribute( LM_HARDWARE_TO_CHECK );
		
		if (AuthFuncs.getRolePropertyValue(user.getYukonUser(), ConsumerInfoRole.INVENTORY_CHECKING_TIME).equalsIgnoreCase(INVENTORY_CHECKING_TIME_EARLY)) {
			// Inventory checking time = EARLY, populate hardware information on the next page
			StarsLMHardware starsHw = (StarsLMHardware) session.getAttribute( STARS_LM_HARDWARE_TEMP );
			if (starsHw == null) {
				starsHw = (StarsLMHardware) StarsFactory.newStarsLMHw( StarsLMHardware.class );
				session.setAttribute( STARS_LM_HARDWARE_TEMP, starsHw );
			}
			if (liteHw != null) {
				StarsLiteFactory.setStarsLMHardware( starsHw, liteHw, energyCompany );
				starsHw.setRemoveDate( null );
				starsHw.setInstallDate( new Date() );
				starsHw.setInstallationNotes( "" );
			}
			
			redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
		}
		else {
			// Inventory checking time = LATE or NONE, update hardware information and submit
			// In this case, only set the inventory ID, the values entered by user will be used for the rest.
			StarsOperation operation = (StarsOperation) session.getAttribute(STARS_OPERATION_REQUEST);
			if (operation.getStarsCreateLMHardware() != null) {
				if (liteHw != null)
					operation.getStarsCreateLMHardware().setInventoryID( liteHw.getInventoryID() );
				redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
			}
			else if (operation.getStarsUpdateLMHardware() != null) {
				if (liteHw != null)
					operation.getStarsUpdateLMHardware().setInventoryID( liteHw.getInventoryID() );
				
				// Forward to DeleteInv.jsp to confirm removal of the old hardware
				LiteStarsLMHardware liteHwOld = energyCompany.getLMHardware(
						operation.getStarsDeleteLMHardware().getInventoryID(), true );
				session.setAttribute( LM_HARDWARE_TO_DELETE, liteHwOld );
				session.setAttribute( NAV_BACK_STEP, new Integer(-2) );	// Since we just visited CheckInv.jsp, it needs two steps to get back to Inventory.jsp
				redirect = req.getContextPath() + "/operator/Hardware/DeleteInv.jsp";
			}
		}
	}
	
	/**
	 * Called from Inventory.jsp when a hardware is to be removed from the customer account 
	 */
	private void deleteInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		StarsOperation operation = DeleteLMHardwareAction.getRequestOperation( req );
		session.setAttribute( STARS_OPERATION_REQUEST, operation );
		
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		LiteStarsLMHardware liteHw = energyCompany.getLMHardware( operation.getStarsDeleteLMHardware().getInventoryID(), true );
		session.setAttribute( LM_HARDWARE_TO_DELETE, liteHw );
		
		redirect = req.getContextPath() + "/operator/Hardware/DeleteInv.jsp"; 
	}
	
	/**
	 * Confirmation from DeleteInv.jsp 
	 */
	private void confirmDelete(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		StarsOperation operation = (StarsOperation) session.getAttribute(STARS_OPERATION_REQUEST);
		operation.getStarsDeleteLMHardware().setDeleteFromInventory(
				Boolean.valueOf(req.getParameter("DeletePerm")).booleanValue() );
		
		redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
	}
	
	/**
	 * Add hardwares in the given serial # range to inventory 
	 */
	private void addSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		java.sql.Connection conn = null;
		
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
		
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			int numSuccess = 0, numFailure = 0;
			
			Integer devTypeID = Integer.valueOf( req.getParameter("DeviceType") );
			Date recvDate = com.cannontech.util.ServletUtil.parseDateStringLiberally( req.getParameter("ReceiveDate") );
			Integer voltageID = Integer.valueOf( req.getParameter("Voltage") );
			Integer companyID = Integer.valueOf( req.getParameter("ServiceCompany") );
			Integer categoryID = new Integer( CreateLMHardwareAction.getInventoryCategoryID(devTypeID.intValue(), energyCompany) ); 
			
			java.util.Hashtable snTable = com.cannontech.database.db.stars.hardware.LMHardwareBase.searchForSNRange(
					devTypeID.intValue(), String.valueOf(snFrom), String.valueOf(snTo), user.getEnergyCompanyID(), conn );
			
			for (int sn = snFrom; sn <= snTo; sn++) {
				String serialNo = String.valueOf(sn);
				Integer invID = (Integer) snTable.get( serialNo );
				if (invID != null) {
					CTILogger.info("***Failed to add hardware with serial # " + serialNo + ", serial # already exists");
					numFailure++;
					continue;
				}
				
				com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
						new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hardware.getLMHardwareBase();
				com.cannontech.database.db.stars.hardware.InventoryBase invDB = hardware.getInventoryBase();
				
				invDB.setInstallationCompanyID( companyID );
				invDB.setCategoryID( categoryID );
				invDB.setReceiveDate( recvDate );
				invDB.setVoltageID( voltageID );
				invDB.setDeviceLabel( serialNo );
				hwDB.setManufacturerSerialNumber( serialNo );
				hwDB.setLMHardwareTypeID( devTypeID );
				hardware.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				hardware.setDbConnection( conn );
				hardware.add();
				
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) StarsLiteFactory.createLite( hardware );
				energyCompany.addLMHardware( liteHw );
				numSuccess++;
			}
			
			if (numSuccess > 0)
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, numSuccess + " hardware(s) added to inventory successfully");
			if (numFailure > 0)
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, numFailure + " hardware(s) failed to be added to inventory");
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to add hardwares to inventory");
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
	}
	
	/**
	 * Update information of hardwares in the given serial # range 
	 */
	private void updateSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		java.sql.Connection conn = null;
		
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
		
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			int numSuccess = 0, numFailure = 0;
			
			Integer devTypeID = Integer.valueOf( req.getParameter("DeviceType") );
			Date recvDate = (req.getParameter("ReceiveDate") != null)?
					com.cannontech.util.ServletUtil.parseDateStringLiberally( req.getParameter("ReceiveDate") ) : null;
			Integer voltageID = (req.getParameter("Voltage") != null)?
					Integer.valueOf( req.getParameter("Voltage") ) : null;
			Integer companyID = (req.getParameter("ServiceCompany") != null)?
					Integer.valueOf( req.getParameter("ServiceCompany") ) : null;
			if (recvDate == null && voltageID == null && companyID == null)
				return;
			
			java.util.Hashtable snTable = com.cannontech.database.db.stars.hardware.LMHardwareBase.searchForSNRange(
					devTypeID.intValue(), String.valueOf(snFrom), String.valueOf(snTo), user.getEnergyCompanyID(), conn );
			
			for (int sn = snFrom; sn <= snTo; sn++) {
				String serialNo = String.valueOf(sn);
				Integer invID = (Integer) snTable.get( serialNo );
				if (invID == null) {
					CTILogger.info("***Failed to update hardware with serial # " + serialNo + ", serial # doesn't exist");
					numFailure++;
					continue;
				}
				
				LiteStarsLMHardware liteHw = energyCompany.getBriefLMHardware( invID.intValue(), true );
				com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
						(com.cannontech.database.data.stars.hardware.LMHardwareBase) StarsLiteFactory.createDBPersistent( liteHw );
				com.cannontech.database.db.stars.hardware.InventoryBase invDB = hardware.getInventoryBase();
				
				if (companyID != null)
					invDB.setInstallationCompanyID( companyID );
				if (recvDate != null)
					invDB.setReceiveDate( recvDate );
				if (voltageID != null)
					invDB.setVoltageID( voltageID );
				invDB.setDbConnection( conn );
				invDB.update();
				
				StarsLiteFactory.setLiteLMHardwareBase( liteHw, hardware );
				numSuccess++;
			}
			
			if (numSuccess > 0)
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, numSuccess + " hardware(s) updated successfully");
			if (numFailure > 0)
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, numFailure + " hardware(s) failed to be updated");
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update hardware information");
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
	}
	
	/**
	 * Configure hardwares in the given serial # range 
	 */
	private void configSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		java.sql.Connection conn = null;
		
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
		
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			int numSuccess = 0, numFailure = 0;
			
			Integer devTypeID = Integer.valueOf( req.getParameter("DeviceType") );
			boolean configNow = req.getParameter("ConfigNow") != null;
			SwitchCommandQueue cmdQueue = (configNow)?
					null : energyCompany.getSwitchCommandQueue();
			
			java.util.Hashtable snTable = com.cannontech.database.db.stars.hardware.LMHardwareBase.searchForSNRange(
					devTypeID.intValue(), String.valueOf(snFrom), String.valueOf(snTo), user.getEnergyCompanyID(), conn );
			
			for (int sn = snFrom; sn <= snTo; sn++) {
				String serialNo = String.valueOf(sn);
				Integer invID = (Integer) snTable.get( serialNo );
				if (invID == null) {
					CTILogger.info("***Failed to configure hardware with serial # " + serialNo + ", serial # doesn't exist");
					numFailure++;
					continue;
				}
				
				if (configNow)
					YukonSwitchCommandAction.sendConfigCommand(energyCompany, invID.intValue(), true, conn);
				else {
					SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
					cmd.setEnergyCompanyID( user.getEnergyCompanyID() );
					cmd.setInventoryID( invID.intValue() );
					cmd.setSerialNumber( serialNo );
					cmd.setCommandType( SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE );
					cmdQueue.addCommand( cmd, false );
				}
				numSuccess++;
			}
			if (!configNow) cmdQueue.addCommand( null, true );
			
			if (numSuccess > 0) {
				if (configNow)
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, numSuccess + " hardware(s) configured successfully");
				else
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, numSuccess + " hardware configuration scheduled successfully");
			}
			if (numFailure > 0) {
				if (configNow)
					session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, numFailure + " hardware(s) failed to be configured");
				else
					session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, numFailure + " hardware configuration failed to be scheduled");
			}
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to configure hardwares");
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
	}
	
	/**
	 * Send all the scheduled switch commands
	 */
	private void sendSwitchCommands(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		java.sql.Connection conn = null;
		
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			SwitchCommandQueue.SwitchCommand[] commands =
					energyCompany.getSwitchCommandQueue().getCommands( user.getEnergyCompanyID() );
			for (int i = 0; i < commands.length; i++) {
				if (commands[i].getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE ))
					YukonSwitchCommandAction.sendConfigCommand(energyCompany, commands[i].getInventoryID(), true, conn);
				else if (commands[i].getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_ENABLE ))
					YukonSwitchCommandAction.sendEnableCommand(energyCompany, commands[i].getInventoryID(), conn);
				else if (commands[i].getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_DISABLE ))
					YukonSwitchCommandAction.sendDisableCommand(energyCompany, commands[i].getInventoryID(), conn);
			}
			
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Scheduled switch commands sent out successfully");
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to send scheduled switch commands");
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
	}
	
	/**
	 * Store hardware information entered by user into a StarsLMHw object 
	 */
	public static void setStarsLMHardware(StarsLMHw starsHw, HttpServletRequest req) {
		if (req.getParameter("DeviceType") != null) {
			LMDeviceType type = new LMDeviceType();
			type.setEntryID( Integer.parseInt(req.getParameter("DeviceType")) );
			starsHw.setLMDeviceType( type );
		}
		
		if (req.getParameter("Voltage") != null) {
			Voltage volt = new Voltage();
			volt.setEntryID( Integer.parseInt(req.getParameter("Voltage")) );
			starsHw.setVoltage( volt );
		}
		
		if (req.getParameter("ServiceCompany") != null) {
			InstallationCompany company = new InstallationCompany();
			company.setEntryID( Integer.parseInt(req.getParameter("ServiceCompany")) );
			starsHw.setInstallationCompany( company );
		}
		
		if (req.getParameter("InvID") != null)
			starsHw.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
		if (req.getParameter("SerialNo") != null)
			starsHw.setManufactureSerialNumber( req.getParameter("SerialNo") );
		if (req.getParameter("DeviceLabel") != null)
			starsHw.setDeviceLabel( req.getParameter("DeviceLabel") );
		if (req.getParameter("AltTrackNo") != null)
			starsHw.setAltTrackingNumber( req.getParameter("AltTrackNo") );
		if (req.getParameter("ReceiveDate") != null && req.getParameter("ReceiveDate").length() > 0)
			starsHw.setReceiveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("ReceiveDate")) );
		if (req.getParameter("InstallDate") != null && req.getParameter("InstallDate").length() > 0)
			starsHw.setInstallDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("InstallDate")) );
		if (req.getParameter("RemoveDate") != null && req.getParameter("RemoveDate").length() > 0)
			starsHw.setRemoveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("RemoveDate")) );
		if (req.getParameter("Notes") != null)
			starsHw.setNotes( req.getParameter("Notes") );
		if (req.getParameter("InstallNotes") != null)
			starsHw.setInstallationNotes( req.getParameter("InstallNotes") );
	}

}
