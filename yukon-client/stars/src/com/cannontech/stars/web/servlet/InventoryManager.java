package com.cannontech.stars.web.servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
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
