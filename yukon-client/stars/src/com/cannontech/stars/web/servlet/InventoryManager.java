package com.cannontech.stars.web.servlet;

import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class InventoryManager extends HttpServlet {
	
	public static final String STARS_LM_HARDWARE_TEMP = "STARS_LM_HARDWARE_TEMP";
	public static final String LM_HARDWARE_TO_CHECK = "LM_HARDWARE_TO_CHECK";
	public static final String LM_HARDWARE_TO_MOVE = "LM_HARDWARE_TO_MOVE";
	
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
		else if (action.equalsIgnoreCase( "CheckInventory" ))
			checkInventory( user, req, session );
		else if (action.equalsIgnoreCase( "AddLMHardware" ))
			addLMHardware( user, req, session );
		
		resp.sendRedirect( redirect );
	}
	
	private void selectInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
/*		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		int invID = Integer.parseInt( req.getParameter("InvID") );
		LiteStarsLMHardware liteHw = energyCompany.getLMHardware(invID, true);
		
		if (liteHw.getAccountID() == CtiUtilities.NONE_ID) {
			// The hardware is in warehouse, so populate the hardware information
			StarsLMHardware starsHw = (StarsLMHardware) session.getAttribute(STARS_LM_HARDWARE_TEMP);
			StarsLiteFactory.setStarsLMHardware( starsHw, liteHw, energyCompany );
			starsHw.setRemoveDate( null );
			starsHw.setInstallDate( new Date() );
			redirect = (String) session.getAttribute( ServletUtils.ATT_REFERRER );
		}
		else {
			session.setAttribute(LM_HARDWARE_TO_CHECK, liteHw);
			redirect = req.getContextPath() + "/operator/Hardware/CheckInv.jsp";
		}
*/
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		if (req.getParameter("InvID") != null) {
			// Submitted from SelectInv.jsp
			int invID = Integer.parseInt( req.getParameter("InvID") );
			LiteStarsLMHardware liteHw = energyCompany.getLMHardware(invID, true);
			
			if (liteHw.getAccountID() == CtiUtilities.NONE_ID) {
				// The hardware is in warehouse, so populate the hardware information
				StarsLMHardware starsHw = new StarsLMHardware();
				StarsLiteFactory.setStarsLMHardware( starsHw, liteHw, energyCompany );
				starsHw.setRemoveDate( null );
				starsHw.setInstallDate( new Date() );
				session.setAttribute( STARS_LM_HARDWARE_TEMP, starsHw );
				
				redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
			}
			else {
				session.setAttribute(LM_HARDWARE_TO_CHECK, liteHw);
				redirect = req.getContextPath() + "/operator/Hardware/CheckInv.jsp";
			}
		}
		else {
			// Submitted from CheckInv.jsp
			StarsLMHardware starsHw = (StarsLMHardware) session.getAttribute( STARS_LM_HARDWARE_TEMP );
			if (starsHw == null) {
				starsHw = (StarsLMHardware) StarsFactory.newStarsLMHw( StarsLMHardware.class );
				session.setAttribute( STARS_LM_HARDWARE_TEMP, starsHw );
			}
			else {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) session.getAttribute( LM_HARDWARE_TO_CHECK );
				if (liteHw != null)
					StarsLiteFactory.setStarsLMHardware( starsHw, liteHw, energyCompany );
				starsHw.setRemoveDate( null );
				starsHw.setInstallDate( new Date() );
			}
				
			redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
		}
	}
	
	private void checkInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
/*		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		LiteStarsLMHardware liteHw = (LiteStarsLMHardware) session.getAttribute( LM_HARDWARE_TO_CHECK );
		session.removeAttribute( LM_HARDWARE_TO_CHECK );
		
		StarsLMHardware starsHw = (StarsLMHardware) session.getAttribute( STARS_LM_HARDWARE_TEMP );
		
		if (req.getParameter("NewHardware") != null) {
			try {
				liteHw = createLMHardware(starsHw, energyCompany);
				StarsLiteFactory.setStarsLMHardware( starsHw, liteHw, energyCompany );
			}
			catch (Exception e) {
				e.printStackTrace();
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Failed to create new hardware");
			}
		}
		else if (req.getParameter("SelectHardware") != null) {
			StarsLiteFactory.setStarsLMHardware( starsHw, liteHw, energyCompany );
		}
		else if (req.getParameter("MoveHardware") != null) {
			session.setAttribute(LM_HARDWARE_TO_MOVE, liteHw);
			StarsLiteFactory.setStarsLMHardware( starsHw, liteHw, energyCompany );
		}
		
		redirect = (String) session.getAttribute(ServletUtils.ATT_REFERRER);
*/
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		LiteStarsLMHardware liteHw = energyCompany.searchBySerialNumber( req.getParameter("SerialNo") );
		session.setAttribute( LM_HARDWARE_TO_CHECK, liteHw );
		
		session.setAttribute( ServletUtils.ATT_REFERRER, referer );
		session.setAttribute( ServletUtils.ATT_REDIRECT, req.getParameter(ServletUtils.ATT_REDIRECT) );
		
		StarsLMHardware starsHw = (StarsLMHardware) StarsFactory.newStarsLMHw( StarsLMHardware.class );
		starsHw.setManufactureSerialNumber( req.getParameter("SerialNo") );
		session.setAttribute( STARS_LM_HARDWARE_TEMP, starsHw );
		
		redirect = req.getContextPath() + "/operator/Hardware/CheckInv.jsp";
	}
	
	private void addLMHardware(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		StarsLMHardware starsHw = new StarsLMHardware();
		setStarsLMHardware( starsHw, req );
		session.setAttribute( STARS_LM_HARDWARE_TEMP, starsHw );
		
		String referer = req.getHeader( "referer" );
		session.setAttribute( ServletUtils.ATT_REFERRER, referer );
		String wizard = req.getParameter( "Wizard" );
		
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		LiteStarsLMHardware liteHw = energyCompany.searchBySerialNumber( starsHw.getManufactureSerialNumber() );
		LiteStarsLMHardware liteHwToMove = (LiteStarsLMHardware) session.getAttribute(LM_HARDWARE_TO_MOVE);
		
		if (liteHw != null && (liteHw.getAccountID() == CtiUtilities.NONE_ID || liteHw.equals(liteHwToMove))) {
			redirect = req.getContextPath() + "/servlet/SOAPClient?action=CreateLMHardware";
			if (wizard != null) redirect += "&Wizard=" + wizard;
		}
		else {
			session.setAttribute(LM_HARDWARE_TO_CHECK, liteHw);
			session.setAttribute( "Wizard", wizard );
			redirect = req.getContextPath() + "/operator/Hardware/CheckInv.jsp?Final=true";
		}
	}
	
	public static void setStarsLMHardware(StarsLMHw starsHw, HttpServletRequest req) {
		LMDeviceType type = new LMDeviceType();
		type.setEntryID( Integer.parseInt(req.getParameter("DeviceType")) );
		starsHw.setLMDeviceType( type );
		
		Voltage volt = new Voltage();
		volt.setEntryID( Integer.parseInt(req.getParameter("Voltage")) );
		starsHw.setVoltage( volt );
		
		InstallationCompany company = new InstallationCompany();
		company.setEntryID( Integer.parseInt(req.getParameter("ServiceCompany")) );
		starsHw.setInstallationCompany( company );
		
		if (req.getParameter("InvID") != null)
			starsHw.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
		starsHw.setManufactureSerialNumber( req.getParameter("SerialNo") );
		starsHw.setDeviceLabel( req.getParameter("DeviceLabel") );
		starsHw.setAltTrackingNumber( req.getParameter("AltTrackNo") );
		if (req.getParameter("ReceiveDate") != null && req.getParameter("ReceiveDate").length() > 0)
			starsHw.setReceiveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("ReceiveDate")) );
		if (req.getParameter("InstallDate") != null && req.getParameter("InstallDate").length() > 0)
			starsHw.setInstallDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("InstallDate")) );
		if (req.getParameter("RemoveDate") != null && req.getParameter("RemoveDate").length() > 0)
			starsHw.setRemoveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("RemoveDate")) );
		starsHw.setNotes( req.getParameter("Notes") );
		starsHw.setInstallationNotes( req.getParameter("InstallNotes") );
	}

}
