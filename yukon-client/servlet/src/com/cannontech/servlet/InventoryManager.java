package com.cannontech.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.hardware.MeterHardwareBase;
import com.cannontech.database.data.stars.purchasing.PurchasingMultiDelete;
import com.cannontech.database.db.stars.purchasing.DeliverySchedule;
import com.cannontech.database.db.stars.purchasing.Invoice;
import com.cannontech.database.db.stars.purchasing.PurchasePlan;
import com.cannontech.database.db.stars.purchasing.ScheduleTimePeriod;
import com.cannontech.database.db.stars.purchasing.Shipment;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.AddSNRangeTask;
import com.cannontech.stars.util.task.AddShipmentSNRangeTask;
import com.cannontech.stars.util.task.AdjustStaticLoadGroupMappingsTask;
import com.cannontech.stars.util.task.ConfigSNRangeTask;
import com.cannontech.stars.util.task.DeleteSNRangeTask;
import com.cannontech.stars.util.task.ManipulateInventoryTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.util.task.UpdateSNRangeTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.web.action.MultiAction;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareConfigAction;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.web.bean.InventoryBean;
import com.cannontech.stars.web.bean.ManipulationBean;
import com.cannontech.stars.web.bean.NonYukonMeterBean;
import com.cannontech.stars.web.bean.PurchaseBean;
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
import com.cannontech.util.ServletUtil;
import com.cannontech.web.navigation.CtiNavObject;


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
		if (referer == null) referer = ((CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
		redirect = req.getParameter( ServletUtils.ATT_REDIRECT );
		if (redirect == null) redirect = referer;
		 
		// If parameter "ConfirmOnMessagePage" specified, the confirm/error message will be displayed on Message.jsp
		if (req.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null) {
			session.setAttribute( ServletUtils.ATT_MSG_PAGE_REDIRECT, redirect );
			session.setAttribute( ServletUtils.ATT_MSG_PAGE_REFERRER, referer );
			redirect = referer = req.getContextPath() + "/operator/Admin/Message.jsp";
		}
		
		action = req.getParameter( "action" );
		if (action == null) action = "";
		
		if (action.equalsIgnoreCase( "InsertInventory" ))
			addInventoryToAccount( user, req, session );
		else if (action.equalsIgnoreCase( "InsertDevice" ))
			addDeviceToAccount( user, req, session );
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
		else if (action.equalsIgnoreCase("UpdateInventory"))
			updateInventory( user, req, session );
		else if (action.equalsIgnoreCase("DeleteInventory")) {
            String att_redir = req.getParameter(ServletUtils.ATT_REDIRECT);
            String refer = req.getParameter(ServletUtils.ATT_REFERRER);
            if( att_redir == null)
                att_redir = req.getContextPath() + "/operator/Consumer/Update.jsp";
            if( refer == null)
                refer = req.getContextPath() + "/operator/Consumer/Update.jsp";
			String redir = req.getContextPath() + "/servlet/SOAPClient?action=DeleteLMHardware" +
					"&REDIRECT=" + att_redir +
					"&REFERRER=" + refer;
			session.setAttribute( ServletUtils.ATT_REDIRECT, redir );
			deleteInventory( user, req, session );
		}
		else if (action.equalsIgnoreCase("ConfigLMHardware"))
			configLMHardware( user, req, session );
		else if (action.equalsIgnoreCase("ConfirmCheck"))
			confirmCheck( user, req, session );
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
        else if (action.equalsIgnoreCase("FileWriteSwitchCommands"))
            fileWriteSwitchCommands( user, req, session );
		else if (action.equalsIgnoreCase("SearchInventory"))
			searchInventory( user, req, session );
		else if (action.equalsIgnoreCase("CreateHardware"))
			createLMHardware( user, req, session );
		else if (action.equalsIgnoreCase("CreateMCT"))
			createMCT( user, req, session );
		else if (action.equalsIgnoreCase("SelectLMHardware"))
			selectLMHardware( user, req, session );
		else if (action.equalsIgnoreCase("SelectDevice"))
			selectDevice( user, req, session );
        else if (action.equalsIgnoreCase("MeterProfileSave"))
            saveMeterProfile( user, req, session );
        else if (action.equalsIgnoreCase("FiltersUpdated"))
            updateFilters( user, req, session, false );
        else if (action.equalsIgnoreCase("FiltersUpdatedForSelection"))
            updateFilters( user, req, session, true );
        else if (action.equalsIgnoreCase("ApplyActions"))
            applyActions( user, req, session );
        else if (action.equalsIgnoreCase("ViewInventoryResults"))
            viewInventoryResults( user, req, session );
        else if (action.equalsIgnoreCase("ApplyOrdering"))
            applyOrderingToInventory( user, req, session );
        else if (action.equalsIgnoreCase("ManipulateInventoryResults"))
            manipulateResults( user, req, session );
        else if (action.equalsIgnoreCase("ManipulateSelectedResults"))
            manipulateSelectedResults( user, req, session );
        else if (action.equalsIgnoreCase("ConfigureInventoryResults"))
            configureRedirect( user, req, session );
        else if (action.equalsIgnoreCase("ConfigureResultSet"))
            configureResults( user, req, session );
        else if (action.startsWith("StaticLoadGroupMap"))
            resetStaticLoadGroupMappings(user, req, session);
        /**
         * Purchasing...this should go in its own servlet sometime soon.
         */
        else if (action.equalsIgnoreCase("PurchaseChange"))
            handlePurchasePlanChange( user, req, session );
        else if (action.equalsIgnoreCase("RequestNewPurchasePlan"))
            requestNewPurchasePlan( user, req, session );
        else if (action.equalsIgnoreCase("LoadPurchasePlan"))
            loadPurchasePlan( user, req, session );
        else if (action.equalsIgnoreCase("DeletePurchasePlan"))
            deletePurchasePlan( user, req, session );
        else if (action.equalsIgnoreCase("DeletePurchasePlanConfirmed"))
            deletePurchasePlanConfirmed( user, req, session );
        else if (action.equalsIgnoreCase("RequestNewDeliverySchedule"))
            requestNewDeliverySchedule( user, req, session );
        else if (action.equalsIgnoreCase("LoadDeliverySchedule"))
            loadDeliverySchedule( user, req, session );
        else if (action.equalsIgnoreCase("DeliveryScheduleChange"))
            handleDeliveryScheduleChange( user, req, session );
        else if (action.equalsIgnoreCase("RequestNewTimePeriod"))
            requestNewTimePeriod( user, req, session );
        else if (action.equalsIgnoreCase("LoadTimePeriod"))
            loadTimePeriod( user, req, session );
        else if (action.equalsIgnoreCase("TimePeriodChange"))
            handleTimePeriodChange( user, req, session );
        else if (action.equalsIgnoreCase("RequestNewShipment"))
            requestNewShipment( user, req, session );
        else if (action.equalsIgnoreCase("LoadShipment"))
            loadShipment( user, req, session );
        else if (action.equalsIgnoreCase("ShipmentChange"))
            handleShipmentChange( user, req, session );
        else if (action.equalsIgnoreCase("RequestNewInvoice"))
            requestNewInvoice( user, req, session );
        else if (action.equalsIgnoreCase("LoadInvoice"))
            loadInvoice( user, req, session );
        else if (action.equalsIgnoreCase("InvoiceChange"))
            handleInvoiceChange( user, req, session );
        else if (action.equalsIgnoreCase("ShipmentSNRangeAdd"))
            addSerialNumbersForShipment( user, req, session );
        
		resp.sendRedirect( redirect );
	}
	
	/**
	 * Called from Consumer/SelectInv.jsp to select a hardware from inventory to insert
	 * into an account or update an existing hardware in that account. If the selected
	 * hardware is assigned to another account, go to CheckInv.jsp to ask for confirmation. 
	 */
	private void addInventoryToAccount(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		int invID = Integer.parseInt( req.getParameter("InvID") );
		LiteInventoryBase liteInv = energyCompany.getInventoryBrief( invID, true );
		Integer invNo = (Integer) session.getAttribute( InventoryManagerUtil.STARS_INVENTORY_NO );
		
		if (liteInv.getAccountID() == CtiUtilities.NONE_ZERO_ID) {
			String referer = (String) session.getAttribute( ServletUtils.ATT_REFERRER2 );
			//quick fix for the Xcel demo...need to find a more stable wizard check solution for the long term
			MultiAction wizardly = (MultiAction) session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
			//if (referer.indexOf("Wizard") < 0) {
            
            if (wizardly == null) {
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
					
					// REDIRECT set in the UpdateLMHardwareAction.parseResponse() method above
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
	private void addDeviceToAccount(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
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
		
		LiteYukonPAObject litePao = DaoFactory.getPaoDao().getLiteYukonPAO( deviceID );
		Integer invNo = (Integer) session.getAttribute( InventoryManagerUtil.STARS_INVENTORY_NO );
		
		if (liteInv == null || liteInv.getAccountID() == CtiUtilities.NONE_ZERO_ID) {
			String referer = (String) session.getAttribute( ServletUtils.ATT_REFERRER2 );
			//quick fix for the Xcel demo...need to find a more stable wizard check solution for the long term
			MultiAction wizardly = (MultiAction) session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
			if (wizardly == null) {
				StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
						session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
				LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation(
						starsAcctInfo.getStarsCustomerAccount().getAccountID(), true );
				
				if (invNo == null) {
					StarsCreateLMHardware createHw = null;
					if (liteInv == null) {
						createHw = (StarsCreateLMHardware) StarsFactory.newStarsInv(StarsCreateLMHardware.class);
						createHw.setDeviceID( deviceID );
						if (InventoryUtils.isMCT( categoryID )) {
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
						if (InventoryUtils.isMCT( categoryID )) {
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
					
					// REDIRECT set in the UpdateLMHardwareAction.parseResponse() method above
					redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
				}
			}
			else {	// Inside new account wizard
				if (liteInv == null) {
					// The device in not in inventory yet
					StarsInventory starsInv = (StarsInventory) StarsFactory.newStarsInv(StarsInventory.class);
					starsInv.setDeviceID( deviceID );
					
					if (InventoryUtils.isMCT( categoryID )) 
                    {
						starsInv.setDeviceType( (DeviceType)StarsFactory.newStarsCustListEntry(
								energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT), DeviceType.class) );
						
						MCT mct = new MCT();
                        try
                        {
                            mct.setDeviceName( DaoFactory.getPaoDao().getYukonPAOName(deviceID) );
                        }
                        catch(NotFoundException e)
                        {
                            CTILogger.error(e.getMessage(), e);
                            mct.setDeviceName( "(none)");
                        }
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
	 * If inventory checking is set to false, add the hardware to the customer account;
	 * Otherwise, show the result of inventory checking in CheckInv.jsp. 
	 */
	private void checkInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		boolean invChecking = DaoFactory.getAuthDao().checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.INVENTORY_CHECKING);
        InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
        if(iBean == null)
        {
            session.setAttribute("inventoryBean", new InventoryBean());
            iBean = (InventoryBean) session.getAttribute("inventoryBean");
        }
        iBean.setCheckInvenForAccount(false);
        
		String devType = req.getParameter("DeviceType");
        int devTypeID = 1;
        if(devType != null) 
            devTypeID = Integer.parseInt( req.getParameter("DeviceType") );
		String serialNo = req.getParameter("SerialNo");
		String deviceName = req.getParameter("DeviceName");
		
		int categoryID = InventoryUtils.getInventoryCategoryID( devTypeID, energyCompany );
		
		if (invChecking && categoryID > 0) {
			// Save the request parameters
			StarsInventory starsInv = (StarsInventory) StarsFactory.newStarsInv(StarsInventory.class);
			starsInv.setDeviceType( (DeviceType)StarsFactory.newStarsCustListEntry(
					DaoFactory.getYukonListDao().getYukonListEntry(devTypeID), DeviceType.class) );
			
			if (InventoryUtils.isLMHardware(categoryID)) {
				LMHardware hw = new LMHardware();
				hw.setManufacturerSerialNumber( serialNo );
				starsInv.setLMHardware( hw );
			}
			else if (InventoryUtils.isMCT(categoryID)) {
				MCT mct = new MCT();
				mct.setDeviceName( deviceName );
				starsInv.setMCT( mct );
			}
			
			session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP, starsInv );
		}
		
		LiteInventoryBase liteInv = null;
		
		try {
			if (categoryID > 0 && InventoryUtils.isLMHardware( categoryID )) {
				liteInv = energyCompany.searchForLMHardware( devTypeID, serialNo );
				session.setAttribute( InventoryManagerUtil.INVENTORY_TO_CHECK, liteInv );
			}
			else if (categoryID > 0) {
				liteInv = energyCompany.searchForDevice( categoryID, deviceName );
				session.setAttribute( InventoryManagerUtil.INVENTORY_TO_CHECK, liteInv );
			}
            else {
                /*
                 * TODO: We will need a new way to find MCTs now that we removed the device type pulldown for Xcel
                 * Will using the inventory filters by serial number actually return a Yukon MCT?  Will not since
                 * the filters assume lmhardware.
                 * UPDATED: This section never returns hardware and I do not like this approach anyway.
                 * Instead I am rolling back the Xcel changes and adding the device type back in so we can find Yukon
                 * MCTs.
                 */
                ArrayList tempList = new ArrayList();
                tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_MEMBER), energyCompany.getName(), String.valueOf(energyCompany.getEnergyCompanyID())));
                tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MIN), serialNo, serialNo));
                tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MAX), serialNo, serialNo));
                //session.setAttribute( ServletUtils.FILTER_INVEN_LIST, tempList );
                iBean.setFilterByList(tempList);
                iBean.setCheckInvenForAccount(true);
                redirect = req.getContextPath() + "/operator/Consumer/CheckInv.jsp";
                return;
                
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
					"DeviceLabel", "AltTrackNo", "fieldReceiveDate", "FieldRemoveDate", "Voltage", "Notes", "InstallDate", "ServiceCompany", "InstallNotes", "Route"} );
				
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
				
				// REDIRECT set in the UpdateLMHardwareAction.parseResponse() method above
				redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
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
				UpdateLMHardwareConfigAction.updateLMConfiguration( starsCfg, (LiteStarsLMHardware)liteInv, energyCompany );
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
			boolean deleteFromYukon = req.getParameter("DeleteAction") != null
					&& req.getParameter("DeleteAction").equalsIgnoreCase("DeleteFromYukon");
			
			try {
				InventoryManagerUtil.deleteInventory( liteInv, energyCompany, deleteFromYukon );
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
			StarsOperation operation = DeleteLMHardwareAction.getRequestOperation( req );
			session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_OPERATION, operation );
			redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
		}
        
        /*
         * This is used when inventory is deleted, but was linked to from outside the member's inventory page, i.e 
         * the parent energy company inventory list.  Need to first logout of the member to return
         * to the parent list.
         */
        CtiNavObject nav = (CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE);
        if(nav.isInternalLogin() && ! nav.isMemberECAdmin()) {
            redirect = req.getContextPath() + "/servlet/LoginController?ACTION=LOGOUT";
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
				UpdateLMHardwareConfigAction.updateLMConfiguration( starsCfg, liteHw, energyCompany );
			}
			
			if (Boolean.valueOf( req.getParameter("SaveConfigOnly") ).booleanValue()) {
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Hardware configuration saved successfully." );
			}
			else {
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
			}
			/*
			session.setAttribute( ServletUtils.ATT_REDIRECT2, redirect );
			session.setAttribute( ServletUtils.ATT_REFERRER2, referer );
			redirect = referer = req.getContextPath() +
					(StarsUtils.isOperator(user)? "/operator/Admin/Message.jsp" : "/user/ConsumerStat/stat/Message.jsp");
		*/}
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
				new String[] {"Member", "From", "To", "DeviceType", "DeviceState", "ReceiveDate", "Voltage", "ServiceCompany", "Route"} );
		
		LiteStarsEnergyCompany member = null;
		if (req.getParameter("Member") != null)
			member = StarsDatabaseCache.getInstance().getEnergyCompany( Integer.parseInt(req.getParameter("Member")) );
		else
			member = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
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
        Integer devStateID = Integer.valueOf( req.getParameter("DeviceState") );
		Integer voltageID = Integer.valueOf( req.getParameter("Voltage") );
		Integer companyID = Integer.valueOf( req.getParameter("ServiceCompany") );
		Integer routeID = Integer.valueOf( req.getParameter("Route") );
		
		Date recvDate = null;
        /*String recvDateStr = req.getParameter("ReceiveDate");
		if (recvDateStr.length() > 0) {
			recvDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(recvDateStr, member.getDefaultTimeZone());
			if (recvDate == null) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid receive date format");
				redirect = referer;
				return;
			}
		}*/
		
		// if operation completed, but not all serial numbers added, show the result on "ResultSet.jsp"
		// (the REDIRECT parameter is set within the AddSNRangeTask.run() method)
		session.removeAttribute( ServletUtils.ATT_REDIRECT );
		
		TimeConsumingTask task = new AddSNRangeTask( member, snFrom, snTo, devTypeID, devStateID, new Date(), voltageID, companyID, routeID, req );
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
				redirect = referer;
				return;
			}
		}
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		session.setAttribute(ServletUtils.ATT_REFERRER, referer);
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	/**
	 * Update information of hardwares in the given serial # range 
	 */
	private void updateSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		ServletUtils.saveRequest( req, session,
				new String[] {"Member", "From", "To", "DeviceType", "NewDeviceType", "ReceiveDate", "Voltage", "ServiceCompany", "Route"} );
		
		LiteStarsEnergyCompany member = null;
		if (req.getParameter("Member") != null)
			member = StarsDatabaseCache.getInstance().getEnergyCompany( Integer.parseInt(req.getParameter("Member")) );
		else
			member = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
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
			recvDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(recvDateStr, member.getDefaultTimeZone());
			if (recvDate == null) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid receive date format");
				redirect = referer;
				return;
			}
		}
		
		if (newDevTypeID == null && recvDate == null && voltageID == null && companyID == null && routeID == null)
			return;
		
		session.removeAttribute( ServletUtils.ATT_REDIRECT );
		
		TimeConsumingTask task = new UpdateSNRangeTask( member, snFrom, snTo, devTypeID, newDevTypeID, recvDate, voltageID, companyID, routeID, req );
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
				redirect = referer;
				return;
			}
		}
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		session.setAttribute(ServletUtils.ATT_REFERRER, referer);
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	/**
	 * Delete hardwares in the given serial # range 
	 */
	private void deleteSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		ServletUtils.saveRequest( req, session, new String[] {"Member", "From", "To", "DeviceType"} );
		
		LiteStarsEnergyCompany member = null;
		if (req.getParameter("Member") != null)
			member = StarsDatabaseCache.getInstance().getEnergyCompany( Integer.parseInt(req.getParameter("Member")) );
		else
			member = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
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
		
		session.removeAttribute( ServletUtils.ATT_REDIRECT );
		
		TimeConsumingTask task = new DeleteSNRangeTask( member, snFrom, snTo, devTypeID, req );
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
				redirect = referer;
				return;
			}
		}
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		session.setAttribute(ServletUtils.ATT_REFERRER, referer);
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	/**
	 * Configure hardwares in the given serial # range 
	 */
	private void configSNRange(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		boolean configNow = req.getParameter("ConfigNow") != null;
		
		session.removeAttribute( ServletUtils.ATT_REDIRECT );
		
		TimeConsumingTask task = new ConfigSNRangeTask( energyCompany, configNow, req );
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
				redirect = referer;
				return;
			}
		}
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		session.setAttribute(ServletUtils.ATT_REFERRER, referer);
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	/**
	 * Send scheduled switch commands
	 */
	private void sendSwitchCommands(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			Hashtable batchConfig = InventoryManagerUtil.getBatchConfigSubmission();
			
			if (req.getParameter("All") != null) {
				int memberID = Integer.parseInt(req.getParameter("All"));
				
				ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
				for (int i = 0; i < descendants.size(); i++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
					if (memberID >= 0 && company.getLiteID() != memberID) continue;
					
					SwitchCommandQueue.SwitchCommand[] commands = SwitchCommandQueue.getInstance().getCommands( company.getLiteID(), false );
					if (commands != null && commands.length > 0) {
						for (int j = 0; j < commands.length; j++)
							InventoryManagerUtil.sendSwitchCommand( commands[j] );
						
						String msg = commands.length + " switch commands sent successfully";
						ActivityLogger.logEvent(user.getUserID(), -1, company.getLiteID(), -1, ActivityLogActions.HARDWARE_SEND_BATCH_CONFIG_ACTION, msg);
						batchConfig.put( company.getEnergyCompanyID(), new Object[]{new Date(), msg} );
					}
				}
			}
			else {
				String[] values = req.getParameterValues( "InvID" );
				Hashtable numCmdSentMap = new Hashtable();
				
				for (int i = 0; i < values.length; i++) {
					int invID = Integer.parseInt( values[i] );
					SwitchCommandQueue.SwitchCommand cmd = SwitchCommandQueue.getInstance().getCommand( invID, false );
					InventoryManagerUtil.sendSwitchCommand( cmd );
					
					Integer energyCompanyID = new Integer(cmd.getEnergyCompanyID());
					Integer numCmdSent = (Integer) numCmdSentMap.get( energyCompanyID );
					if (numCmdSent == null)
						numCmdSent = new Integer(1);
					else
						numCmdSent = new Integer(numCmdSent.intValue() + 1);
					numCmdSentMap.put( energyCompanyID, numCmdSent );
				}
				
				Iterator it = numCmdSentMap.keySet().iterator();
				while (it.hasNext()) {
					Integer energyCompanyID = (Integer) it.next();
					Integer numCmdSent = (Integer) numCmdSentMap.get( energyCompanyID );
					String msg = numCmdSent + " switch commands sent successfully";
					ActivityLogger.logEvent(user.getUserID(), -1, energyCompanyID.intValue(), -1, ActivityLogActions.HARDWARE_SEND_BATCH_CONFIG_ACTION, msg);
					batchConfig.put( energyCompanyID, new Object[]{new Date(), msg} );
				}
			}
			
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
		
		if (req.getParameter("All") != null) {
			int memberID = Integer.parseInt(req.getParameter("All"));
			
			ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
			for (int i = 0; i < descendants.size(); i++) {
				LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
				if (memberID >= 0 && company.getLiteID() != memberID) continue;
				
				SwitchCommandQueue.getInstance().clearCommands( company.getLiteID() );
			}
		}
		else {
			String[] values = req.getParameterValues( "InvID" );
			for (int i = 0; i < values.length; i++)
				SwitchCommandQueue.getInstance().removeCommand( Integer.parseInt(values[i]) );
		}
		
		session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Switch commands removed successfully");
	}
    
    /**
     * 
     * Write batched switch commands out to appropriate split files instead of allowing the 
     * timer task to handle it at midnight.  Overall use determined by SystemRole.BATCHED_SWITCH_COMMAND_TOGGLE
     */
    private void fileWriteSwitchCommands(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
        try {
            Hashtable batchConfig = InventoryManagerUtil.getBatchConfigSubmission();
            
            if (req.getParameter("All") != null) 
            {
                int memberID = Integer.parseInt(req.getParameter("All"));
                
                ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
                for (int i = 0; i < descendants.size(); i++) 
                {
                    LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
                    if (memberID >= 0 && company.getLiteID() != memberID) continue;
                    
                    SwitchCommandQueue.SwitchCommand[] commands = SwitchCommandQueue.getInstance().getCommands( company.getLiteID(), false );
                    if (commands != null && commands.length > 0) {
                        for (int j = 0; j < commands.length; j++)
                            InventoryManagerUtil.sendSwitchCommand( commands[j] );
                        
                        String msg = commands.length + " switch commands sent successfully";
                        ActivityLogger.logEvent(user.getUserID(), -1, company.getLiteID(), -1, ActivityLogActions.HARDWARE_SEND_BATCH_CONFIG_ACTION, msg);
                        batchConfig.put( company.getEnergyCompanyID(), new Object[]{new Date(), msg} );
                    }
                }
            }
            else {
                String[] values = req.getParameterValues( "InvID" );
                Hashtable numCmdSentMap = new Hashtable();
                
                for (int i = 0; i < values.length; i++) {
                    int invID = Integer.parseInt( values[i] );
                    SwitchCommandQueue.SwitchCommand cmd = SwitchCommandQueue.getInstance().getCommand( invID, false );
                    InventoryManagerUtil.sendSwitchCommand( cmd );
                    
                    Integer energyCompanyID = new Integer(cmd.getEnergyCompanyID());
                    Integer numCmdSent = (Integer) numCmdSentMap.get( energyCompanyID );
                    if (numCmdSent == null)
                        numCmdSent = new Integer(1);
                    else
                        numCmdSent = new Integer(numCmdSent.intValue() + 1);
                    numCmdSentMap.put( energyCompanyID, numCmdSent );
                }
                
                Iterator it = numCmdSentMap.keySet().iterator();
                while (it.hasNext()) {
                    Integer energyCompanyID = (Integer) it.next();
                    Integer numCmdSent = (Integer) numCmdSentMap.get( energyCompanyID );
                    String msg = numCmdSent + " switch commands sent successfully";
                    ActivityLogger.logEvent(user.getUserID(), -1, energyCompanyID.intValue(), -1, ActivityLogActions.HARDWARE_SEND_BATCH_CONFIG_ACTION, msg);
                    batchConfig.put( energyCompanyID, new Object[]{new Date(), msg} );
                }
            }
            
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Switch commands sent out successfully");
        }
        catch (WebClientException e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
        }
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
        session.setAttribute( ServletUtils.ATT_LAST_INVENTORY_SEARCH_VALUE, new String(searchValue) );
		
		boolean searchMembers = DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS )
				&& (energyCompany.getChildren().size() > 0);
		
		ArrayList invList = null;
		try {
			invList = InventoryManagerUtil.searchInventory( energyCompany, searchBy, searchValue, searchMembers ); 
		}
		catch (WebClientException e) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
			return;
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
		LiteStarsEnergyCompany member = null;
		if (req.getParameter("Member") != null)
			member = StarsDatabaseCache.getInstance().getEnergyCompany( Integer.parseInt(req.getParameter("Member")) );
		else
			member = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		ServletUtils.saveRequest(req, session, new String[] {
			"Member", "DeviceType", "SerialNo", "DeviceLabel", "AltTrackNo", "ReceiveDate", "Voltage", "ServiceCompany", "Notes", "Route"});
		
		try {
			StarsCreateLMHardware createHw = new StarsCreateLMHardware();
			InventoryManagerUtil.setStarsInv( createHw, req, member );
			
			try {
				LiteInventoryBase existingHw = member.searchForLMHardware(
						createHw.getDeviceType().getEntryID(), createHw.getLMHardware().getManufacturerSerialNumber() );
				if (existingHw != null)
					throw new WebClientException("Cannot create hardware: serial # already exists.");
			}
			catch (ObjectInOtherEnergyCompanyException e) {
				throw new WebClientException("Cannot create hardware: serial # already exists in the inventory list of <i>" + e.getEnergyCompany().getName() + "</i>.");
			}
			
			LiteInventoryBase liteInv = CreateLMHardwareAction.addInventory( createHw, null, member );
			
			if (req.getParameter("UseHardwareAddressing") != null) {
				StarsLMConfiguration starsCfg = new StarsLMConfiguration();
				InventoryManagerUtil.setStarsLMConfiguration( starsCfg, req );
				UpdateLMHardwareConfigAction.updateLMConfiguration( starsCfg, (LiteStarsLMHardware)liteInv, member );
			}
			
			if (member.getLiteID() != user.getEnergyCompanyID()) {
				StarsAdmin.switchContext( user, req, session, member.getLiteID() );
				session = req.getSession( false );
			}
			
			// Append inventory ID to the redirect URL
			redirect += String.valueOf( liteInv.getInventoryID() );
			
			// Make the "Back to List" link on the inventory details page default to Inventory.jsp
			session.removeAttribute( ServletUtils.ATT_REFERRER2 );
		}
		catch (WebClientException e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
			redirect = referer;
		}
	}
	
	private void createMCT(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany member = null;
		if (req.getParameter("Member") != null)
			member = StarsDatabaseCache.getInstance().getEnergyCompany( Integer.parseInt(req.getParameter("Member")) );
		else
			member = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		ServletUtils.saveRequest(req, session, new String[] {
			"Member", "MCTType", "DeviceName", "PhysicalAddr", "MeterNumber", "MCTRoute", "DeviceLabel", "AltTrackNo", "ReceiveDate", "Voltage", "ServiceCompany", "Notes"});
		
		try {
			StarsCreateLMHardware createHw = new StarsCreateLMHardware();
			InventoryManagerUtil.setStarsInv( createHw, req, member );
			
			LiteInventoryBase liteInv = CreateLMHardwareAction.addInventory( createHw, null, member );
			
			if (member.getLiteID() != user.getEnergyCompanyID()) {
				StarsAdmin.switchContext( user, req, session, member.getLiteID() );
				session = req.getSession( false );
			}
			
			// Append inventory ID to the redirect URL
			redirect += String.valueOf( liteInv.getInventoryID() );
			
			// Make the "Back to List" link on the inventory details page default to Inventory.jsp
			session.removeAttribute( ServletUtils.ATT_REFERRER2 );
		}
		catch (WebClientException e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
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
			session.setAttribute(InventoryManagerUtil.INVENTORY_SELECTED, liteInv);
		}
		else {
			LiteStarsEnergyCompany member = StarsDatabaseCache.getInstance().getEnergyCompany( Integer.parseInt(req.getParameter("MemberID")) );
			LiteInventoryBase liteInv = member.getInventoryBrief( invID, true );
			session.setAttribute(InventoryManagerUtil.INVENTORY_SELECTED, new Pair(liteInv, member));
		}
	}
	
	/**
	 * Called from Hardware/SelectMCT.jsp to select a MCT to add to the inventory.
	 */
	private void selectDevice(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		int deviceID = Integer.parseInt( req.getParameter("DeviceID") );
		LiteYukonPAObject litePao = DaoFactory.getPaoDao().getLiteYukonPAO( deviceID );
		session.setAttribute(InventoryManagerUtil.DEVICE_SELECTED, litePao);
		
		redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
	}
    
    private void updateFilters(StarsYukonUser user, HttpServletRequest req, HttpSession session, boolean hwSelection) 
    {
        LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
        String[] selectionIDs = req.getParameterValues("SelectionIDs");
        String[] filterTexts = req.getParameterValues("FilterTexts");
        String[] yukonDefIDs = req.getParameterValues("YukonDefIDs");
        ArrayList filters = new ArrayList();
        InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
        
        if(filterTexts == null)
        {
            //session.setAttribute("inventoryBean", iBean);
            session.setAttribute( ServletUtils.FILTER_INVEN_LIST, filters );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "There are no filters defined.");
            redirect = referer;
            return;
        }
        
        for(int j = 0; j < filterTexts.length; j++)
        {
            FilterWrapper wrapper = new FilterWrapper(yukonDefIDs[j], filterTexts[j], selectionIDs[j] );
            filters.add(wrapper);
        }
        
        iBean.setFilterByList(filters);
        iBean.setViewResults(false);
        iBean.setCheckInvenForAccount(false);
        iBean.setPage(1);
        session.setAttribute("inventoryBean", iBean);
        session.setAttribute( ServletUtils.FILTER_INVEN_LIST, filters );
        if(hwSelection)
            redirect = req.getContextPath() + "/operator/Consumer/SelectInv.jsp";
        else    
            redirect = req.getContextPath() + "/operator/Hardware/Inventory.jsp";
    };
    
    private void applyActions(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
        String[] selectionIDs = req.getParameterValues("SelectionIDs");
        String[] actionTexts = req.getParameterValues("ActionTexts");
        String[] actionTypeIDs = req.getParameterValues("ActionTypeIDs");
        List<String> appliedActions = new ArrayList();
        
        Integer newDevTypeID = null;
        Integer newServiceCompanyID = null;
        Integer newWarehouseID = null;
        Integer newDevStateID = null;
        Integer newEnergyCompanyID = null;
        
        if(selectionIDs != null && selectionIDs.length > 0 && selectionIDs.length == actionTypeIDs.length)
        {
            for(int j = 0; j < selectionIDs.length; j++)
            {
                if(new Integer(actionTypeIDs[j]).intValue() == ServletUtils.ACTION_CHANGEDEVICE)
                    newDevTypeID = new Integer(selectionIDs[j]);
                else if(new Integer(actionTypeIDs[j]).intValue() == ServletUtils.ACTION_TOSERVICECOMPANY)
                    newServiceCompanyID = new Integer(selectionIDs[j]);
                else if(new Integer(actionTypeIDs[j]).intValue() ==  ServletUtils.ACTION_TOWAREHOUSE)
                    newWarehouseID = new Integer(selectionIDs[j]);    
                else if(new Integer(actionTypeIDs[j]).intValue() == ServletUtils.ACTION_CHANGESTATE)
                    newDevStateID = new Integer(selectionIDs[j]);
                
                appliedActions.add(actionTexts[j]);
            }
            
            InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
            ManipulationBean mBean = (ManipulationBean) session.getAttribute("manipBean");
            mBean.setActionsApplied(appliedActions);
            ArrayList theWares = iBean.getInventoryList();
            if(theWares.size() < 1)
            {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "There is no selected inventory on which to apply actions.");
                redirect = referer;
                return;
            }
            
            session.removeAttribute( ServletUtils.ATT_REDIRECT );
            TimeConsumingTask task = new ManipulateInventoryTask( mBean.getEnergyCompany(), newEnergyCompanyID, theWares, newDevTypeID, newDevStateID, newServiceCompanyID, newWarehouseID, req );
            long id = ProgressChecker.addTask( task );
            String redir = req.getContextPath() + "/operator/Hardware/InvenResultSet.jsp";
            
            //Wait 2 seconds for the task to finish (or error out), if not, then go to the progress page
            for (int i = 0; i < 2; i++) 
            {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {}
                
                task = ProgressChecker.getTask(id);
                
                if (task.getStatus() == UpdateSNRangeTask.STATUS_FINISHED) {
                    session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
                    ProgressChecker.removeTask( id );
                    if (redir != null) redirect = redir;
                    return;
                }
                
                if (task.getStatus() == UpdateSNRangeTask.STATUS_ERROR) {
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
                    ProgressChecker.removeTask( id );
                    redirect = referer;
                    return;
                }
            }
            
            session.setAttribute(ServletUtils.ATT_REDIRECT, redir);
            session.setAttribute(ServletUtils.ATT_REFERRER, redir);
            redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
        }
    }
	
    private void viewInventoryResults(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
        iBean.setViewResults(!iBean.getViewResults());
        iBean.setPage(1);
        //session.setAttribute("inventoryBean", iBean);
        
        redirect = req.getContextPath() + "/operator/Hardware/Inventory.jsp";
    }
    
    private void applyOrderingToInventory(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
        iBean.setSortBy(Integer.parseInt(req.getParameter("SortBy")));
        iBean.setSortOrder(Integer.parseInt(req.getParameter("SortOrder")));
        //session.setAttribute("inventoryBean", iBean);
        
        redirect = req.getContextPath() + "/operator/Hardware/Inventory.jsp";
    }
    
    private void saveMeterProfile(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        NonYukonMeterBean mBean = (NonYukonMeterBean) session.getAttribute("meterBean");
        MeterHardwareBase currentMeter = mBean.getCurrentMeter();
        String accountDestination = new String();
        boolean success = false;
        boolean isNew = false;
        
        currentMeter.getMeterHardwareBase().setMeterNumber(req.getParameter("MeterNumber"));
        currentMeter.getMeterHardwareBase().setMeterTypeID(new Integer(req.getParameter("MeterType")));
        currentMeter.getInventoryBase().setAlternateTrackingNumber(req.getParameter("AltTrackNo"));
        currentMeter.getInventoryBase().setDeviceLabel(req.getParameter("DeviceLabel"));
        currentMeter.getInventoryBase().setVoltageID(new Integer(req.getParameter("Voltage")));
        currentMeter.getInventoryBase().setNotes(req.getParameter("Notes"));
        YukonListEntry categoryEntry = mBean.getEnergyCompany().getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_NON_YUKON_METER);
        if( categoryEntry != null)
        	currentMeter.getInventoryBase().setCategoryID(categoryEntry.getEntryID());
        
        try
        {
            //new meter
            if(mBean.getCurrentMeterID() == -1)
            {
                currentMeter.setInventoryID(null);
                currentMeter.setAccountID(mBean.getCurrentAccountID());
                currentMeter.setEnergyCompanyID(mBean.getEnergyCompany().getEnergyCompanyID());
                Transaction.createTransaction(Transaction.INSERT, currentMeter).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "New meter added to inventory and this account.");
                accountDestination = req.getContextPath() + "/operator/Consumer/Update.jsp";
                success = true;
                isNew = true;
            }
            else
            {
                Transaction.createTransaction(Transaction.UPDATE, currentMeter).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Meter successfully updated in the database.");
                accountDestination = req.getContextPath() + "/operator/Consumer/MeterProfile.jsp?MetRef=" + currentMeter.getInventoryBase().getInventoryID().toString();
                success = true;
            }
            
            /**
             * Switch mapping to non-Yukon dumb meters
             */
            String[] assignedSwitchIDs = req.getParameterValues("SwitchIDs");
            
            boolean deleteSuccess = MeterHardwareBase.deleteAssignedSwitches(currentMeter.getInventoryBase().getInventoryID());
                        
            if(assignedSwitchIDs != null && assignedSwitchIDs.length > 0)
            {
                if(deleteSuccess)
                {
                    for(int j = 0; j < assignedSwitchIDs.length; j++)
                    {
                        boolean updateSuccess = MeterHardwareBase.updateAssignedSwitch(Integer.parseInt(assignedSwitchIDs[j]), currentMeter.getInventoryBase().getInventoryID().intValue());
                        
                        //something wrong
                        if(!updateSuccess)
                        {
                            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Switch assignment to this meter failed.");
                            break;
                        }
                    }
                }
                else
                {
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Could not delete previous switch assignment to this meter.  Assignment not updated.");
                }
            }
            
        }
        catch (TransactionException e) 
        {
            CTILogger.error( e.getMessage(), e );
            e.printStackTrace();
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Meter information could not be saved to the database.  Transaction failed.");
        }
        
        if(success)
        {
            /*LINE OF DEATH*/
            LiteInventoryBase liteInv = (LiteInventoryBase)StarsLiteFactory.createLite(currentMeter);
           /* if(liteInv.getDeviceLabel().length() < 1)
                liteInv.setDeviceLabel(current)*/
            
            StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
                    session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            LiteStarsCustAccountInformation liteAcctInfo = mBean.getEnergyCompany().getCustAccountInformation(
                    starsAcctInfo.getStarsCustomerAccount().getAccountID(), true );
            
            if (isNew) 
            {
                StarsCreateLMHardware createHw = new StarsCreateLMHardware();
                StarsLiteFactory.setStarsInv( createHw, liteInv, mBean.getEnergyCompany() );
                createHw.setRemoveDate( null );
                createHw.setInstallDate( new Date() );
                createHw.setInstallationNotes( "" );
                        
                try 
                {
                    liteInv = CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, mBean.getEnergyCompany() );
                }
                catch (WebClientException e) 
                {
                    CTILogger.error( e.getMessage(), e );
                    session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
                    redirect = req.getContextPath() + "/operator/Consumer/MeterProfile.jsp?MetRef=-1";
                    return;
                }
                        
                session.removeAttribute( ServletUtils.ATT_REDIRECT );
                StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, mBean.getEnergyCompany() );
                starsInv.setMeterNumber(currentMeter.getMeterHardwareBase().getMeterNumber());
                CreateLMHardwareAction.parseResponse( createHw, starsInv, starsAcctInfo, session );
                
                // REDIRECT set in the CreateLMHardwareAction.parseResponse() method above
                redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
            }
            else 
            {
                session.setAttribute( ServletUtils.ATT_REDIRECT,  req.getContextPath() + "/operator/Hardware/MeterProfile.jsp?MetRef=" + currentMeter.getInventoryBase().getInventoryID().toString() );
                StarsInventory starsInv = StarsLiteFactory.createStarsInventory(liteInv, mBean.getEnergyCompany());
                starsInv.setMeterNumber(currentMeter.getMeterHardwareBase().getMeterNumber());
                UpdateLMHardwareAction.parseResponse(currentMeter.getInventoryBase().getInventoryID(), starsInv, starsAcctInfo, session);
            }

            /*LINE OF DEATH*/
            DBChangeMsg dbChangeMessage = new DBChangeMsg(
                  mBean.getCurrentAccountID(),
                  DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
                  DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                  DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                  DBChangeMsg.CHANGE_TYPE_UPDATE
            );
            dbChangeMessage.setSource("STARS Self-Message");
            ServerUtils.handleDBChangeMsg(dbChangeMessage);
        }
        
        if(referer.contains("Consumer"))
        {
            redirect = accountDestination;
        }
        else
            redirect = req.getContextPath() + "/operator/Hardware/MeterProfile.jsp?MetRef=" + currentMeter.getInventoryBase().getInventoryID().toString();
    }
    
    private void manipulateSelectedResults(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        String[] selections = req.getParameterValues("checkMultiInven");
        if( selections == null)//none selected
        {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "There are no individual Inventories checked to manipulate.");
            redirect = referer;
            return;
        }
        int [] selectionIDs = new int[selections.length];
        for ( int i = 0; i < selections.length; i++)
            selectionIDs[i] = Integer.valueOf(selections[i]).intValue();
        
        InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
        ArrayList inventoryList = new ArrayList(); 
        for ( int i = 0; i < iBean.getInventoryList().size(); i ++)
        {
              LiteInventoryBase liteInvBase = null;
              if (iBean.getInventoryList().get(i) instanceof Pair)
                    liteInvBase = (LiteInventoryBase)((Pair)iBean.getInventoryList().get(i)).getFirst();
              else if( iBean.getInventoryList().get(i) instanceof LiteInventoryBase)
                    liteInvBase = (LiteInventoryBase)iBean.getInventoryList().get(i);
        for (int j = 0; j < selectionIDs.length; j++)
              {
                    if( liteInvBase.getInventoryID() == selectionIDs[j])
                    {
                          inventoryList.add(iBean.getInventoryList().get(i));
                          break;
                    }
              }
        }
                
        iBean.setInventoryList(inventoryList);
        iBean.setNumberOfRecords(String.valueOf((iBean.getInventoryList().size())));
        //session.setAttribute("inventoryBean", iBean);
        
        manipulateResults(user, req, session);
    }
    
    private void manipulateResults(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        redirect = req.getContextPath() + "/operator/Hardware/ChangeInventory.jsp";
    }
    
    private void configureRedirect(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        redirect = req.getContextPath() + "/operator/Hardware/ConfigureInventory.jsp";
    }
    
    private void configureResults(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
        if(iBean.getInventoryList() != null && iBean.getInventoryList().size() > 0)
        {
            session.setAttribute(InventoryManagerUtil.SN_RANGE_TO_CONFIG, iBean.getInventoryList());
            /*
             * Might as well piggyback on some old code.
             */
            configSNRange(user, req, session);
            session.setAttribute(ServletUtils.ATT_REDIRECT, req.getContextPath() + "/operator/Hardware/Inventory.jsp");
        }
        else
        {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "No inventory results are available to configure.");
            redirect = req.getContextPath() + "/operator/Hardware/Inventory.jsp";
        }
    }
    
    private void resetStaticLoadGroupMappings(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        boolean resetAll = true;
        boolean sendConfig = true;
        action = req.getParameter( "action" );
        if(action.startsWith("StaticLoadGroupMapSetDefaults"))
            resetAll = false;
        if(action.endsWith("NoConfig"))
            sendConfig = false;
        
        TimeConsumingTask task = new AdjustStaticLoadGroupMappingsTask( energyCompany, resetAll, sendConfig, req );
        long id = ProgressChecker.addTask( task );
        
        // Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
        for (int i = 0; i < 5; i++) 
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {}
            
            task = ProgressChecker.getTask(id);
            String redir = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
            
            if (task.getStatus() == AddSNRangeTask.STATUS_FINISHED) 
            {
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
                ProgressChecker.removeTask( id );
                if (redir != null) redirect = redir;
            }
            
            if (task.getStatus() == AddSNRangeTask.STATUS_ERROR) {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
                ProgressChecker.removeTask( id );
                redirect = referer;
                return;
            }
        }
        referer = req.getContextPath() + "/operator/Hardware/PowerUserStaticLoadGroupReset.jsp";
        redirect = req.getContextPath() + "/operator/Hardware/PowerUserStaticLoadGroupReset.jsp";
        session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
        session.setAttribute(ServletUtils.ATT_REFERRER, referer);
        redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
    }
    
    private void handlePurchasePlanChange(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        PurchasePlan currentPlan = pBean.getCurrentPlan();
        
        currentPlan.setPlanName(req.getParameter("name"));
        currentPlan.setPoDesignation(req.getParameter("poNumber"));
        currentPlan.setAccountingCode(req.getParameter("accountingCode"));
        
        try
        {
            //new purchase plan
            if(currentPlan.getPurchaseID() == null)
            {
                currentPlan.setPurchaseID(PurchasePlan.getNextPurchaseID());
                currentPlan.setEnergyCompanyID(pBean.getEnergyCompany().getEnergyCompanyID());
                Transaction.createTransaction(Transaction.INSERT, currentPlan).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "New purchase plan added to database.");
            }
            else
            {
                Transaction.createTransaction(Transaction.UPDATE, currentPlan).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Purchase plan successfully updated in the database.");
            }
            
            /**
             * Delivery Schedules --
             * Only really have to worry about deletes: updates and news have already been added
             **/
            String[] scheduleIDs = req.getParameterValues("schedules");
            List<DeliverySchedule> oldList = DeliverySchedule.getAllDeliverySchedulesForAPlan(currentPlan.getPurchaseID());
            
            for(int i = 0; i < oldList.size(); i++)
            {
                boolean isFound = false;
                if(scheduleIDs != null)
                {
                    for(int j = 0; j < scheduleIDs.length; j++)
                    {
                        if(scheduleIDs[j].compareTo(oldList.get(i).getScheduleID().toString()) == 0)
                        {
                            isFound = true;
                            break;
                        }
                    }
                }
                /*
                 * In old list only, must have been removed
                 */
                if(!isFound)
                {
                    Transaction.createTransaction(Transaction.DELETE, oldList.get(i)).execute();
                }
            }
            
            /**
             * Invoices --
             * Only really have to worry about deletes: updates and news have already been added
             **/
            String[] invoiceIDs = req.getParameterValues("invoices");
            List<Invoice> oldInvoiceList = Invoice.getAllInvoicesForPurchasePlan(currentPlan.getPurchaseID());
            
            for(int i = 0; i < oldInvoiceList.size(); i++)
            {
                boolean isFound = false;
                if(invoiceIDs != null)
                {
                    for(int j = 0; j < invoiceIDs.length; j++)
                    {
                        if(invoiceIDs[j].compareTo(oldInvoiceList.get(i).getInvoiceID().toString()) == 0)
                        {
                            isFound = true;
                            break;
                        }
                    }
                }
                /*
                 * In old list only, must have been removed
                 */
                if(!isFound)
                {
                    Transaction.createTransaction(Transaction.DELETE, oldInvoiceList.get(i)).execute();
                }
            }
        }
        catch (TransactionException e) 
        {
            CTILogger.error( e.getMessage(), e );
            e.printStackTrace();
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Purchase plan could not be saved to the database.  Transaction failed.");
        }
        
        redirect = req.getContextPath() + "/operator/Hardware/PurchaseTrack.jsp";
    }
    
    private void requestNewPurchasePlan(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    { 
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        pBean.setCurrentPlan(new PurchasePlan());
        
        redirect = req.getContextPath() + "/operator/Hardware/PurchaseTrack.jsp";
    }
    
    private void loadPurchasePlan(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    { 
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        List<PurchasePlan> purchasePlans = pBean.getAvailablePlans();
        Integer idToLoad = new Integer(req.getParameter("plans"));
        
        for(int j = 0; j < purchasePlans.size(); j++)
        {
            if(purchasePlans.get(j).getPurchaseID().compareTo(idToLoad) == 0)
                pBean.setCurrentPlan(purchasePlans.get(j));
        }
        
        redirect = req.getContextPath() + "/operator/Hardware/PurchaseTrack.jsp";
    }
    
    private void deletePurchasePlan(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    { 
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        List<PurchasePlan> purchasePlans = pBean.getAvailablePlans();
        Integer idToLoad = new Integer(req.getParameter("plans"));
        
        for(int j = 0; j < purchasePlans.size(); j++)
        {
            if(purchasePlans.get(j).getPurchaseID().compareTo(idToLoad) == 0)
                pBean.setCurrentPlan(purchasePlans.get(j));
        }
        
        session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "WARNING: You will be deleting all delivery schedules, shipments, and invoices under the specified purchase plan.  Continue to delete?");
        redirect = req.getContextPath() + "/operator/Hardware/DeletePurchaseTrack.jsp";
    }
    
    private void deletePurchasePlanConfirmed(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    { 
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        PurchasingMultiDelete plannedDeath = new PurchasingMultiDelete();
        plannedDeath.setPurchasePlan(pBean.getCurrentPlan());
        plannedDeath.setSchedules(pBean.getAvailableSchedules());
        plannedDeath.setShipments(pBean.getAvailableShipments());
        plannedDeath.setInvoices(pBean.getAvailableInvoices());
        
        try
        {
            Transaction.createTransaction(Transaction.DELETE, plannedDeath).execute();
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Plan " + pBean.getCurrentPlan().getPlanName() + " was successfully deleted.");
        }
        catch (TransactionException e) 
        {
            CTILogger.error( e.getMessage(), e );
            e.printStackTrace();
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Plan deletion did NOT complete.  Transaction failed.");
        }
        
        /*
         * Set current plan as the next in the list (next most recently created)
         */
        pBean.setCurrentPlan(pBean.getAvailablePlans().get(0));
        redirect = req.getContextPath() + "/operator/Hardware/PurchaseTrack.jsp";
    }
    
    private void requestNewDeliverySchedule(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    { 
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        pBean.setCurrentSchedule(new DeliverySchedule());
        pBean.getCurrentSchedule().setPurchasePlanID(pBean.getCurrentPlan().getPurchaseID());
        
        redirect = req.getContextPath() + "/operator/Hardware/DeliverySchedule.jsp";
    }
    
    private void loadDeliverySchedule(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    { 
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        List<DeliverySchedule> schedules = pBean.getAvailableSchedules();
        Integer idToLoad = new Integer(req.getParameter("schedules"));
        
        for(int j = 0; j < schedules.size(); j++)
        {
            if(schedules.get(j).getScheduleID().compareTo(idToLoad) == 0)
                pBean.setCurrentSchedule(schedules.get(j));
        }
        
        redirect = req.getContextPath() + "/operator/Hardware/DeliverySchedule.jsp";
    }
    
    private void handleDeliveryScheduleChange(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        DeliverySchedule currentSchedule = pBean.getCurrentSchedule();
        
        currentSchedule.setScheduleName(req.getParameter("name"));
        currentSchedule.setModelID(new Integer(req.getParameter("modelType")));
        currentSchedule.setStyleNumber(req.getParameter("styleNumber"));
        currentSchedule.setOrderNumber(req.getParameter("orderNumber"));
        currentSchedule.setQuotedPricePerUnit(new Double(req.getParameter("pricePerUnit")));
        
        try
        {
            //new schedule
            if(currentSchedule.getScheduleID() == null)
            {
                currentSchedule.setScheduleID(DeliverySchedule.getNextScheduleID());
                currentSchedule.setPurchasePlanID(pBean.getCurrentPlan().getPurchaseID());
                Transaction.createTransaction(Transaction.INSERT, currentSchedule).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "New delivery schedule added to this purchase plan.");
            }
            else
            {
                Transaction.createTransaction(Transaction.UPDATE, currentSchedule).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Delivery schedule successfully updated in the database.");
            }
            
            /**
             * Time periods --
             * Only really have to worry about deletes: updates and news have already been added
             */
            String[] timeIDs = req.getParameterValues("times");
            List<ScheduleTimePeriod> oldList = ScheduleTimePeriod.getAllTimePeriodsForDeliverySchedule(currentSchedule.getScheduleID());
          
            for(int i = 0; i < oldList.size(); i++)
            {
                boolean isFound = false;
                
                if(timeIDs != null)
                {    
                    for(int j = 0; j < timeIDs.length; j++)
                    {
                        if(timeIDs[j].compareTo(oldList.get(i).getTimePeriodID().toString()) == 0)
                        {
                            isFound = true;
                            break;
                        }
                    }
                }
                /*
                 * In old list only, must have been removed
                 */
                if(!isFound)
                {
                    Transaction.createTransaction(Transaction.DELETE, oldList.get(i)).execute();
                }
            }
            
            /**
             * Shipments --
             * Only really have to worry about deletes: updates and news have already been added
             */
            String[] shipmentIDs = req.getParameterValues("shipments");
            List<Shipment> oldShipList = Shipment.getAllShipmentsForDeliverySchedule(currentSchedule.getScheduleID());
            
            for(int i = 0; i < oldShipList.size(); i++)
            {
                boolean isFound = false;
                
                if(shipmentIDs != null)
                {    
                    for(int j = 0; j < shipmentIDs.length; j++)
                    {
                        if(shipmentIDs[j].compareTo(oldShipList.get(i).getShipmentID().toString()) == 0)
                        {
                            isFound = true;
                            break;
                        }
                    }
                }
                /*
                 * In old list only, must have been removed
                 */
                if(!isFound)
                {
                    Transaction.createTransaction(Transaction.DELETE, oldShipList.get(i)).execute();
                }
            }
        }
        catch (TransactionException e) 
        {
            CTILogger.error( e.getMessage(), e );
            e.printStackTrace();
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Delivery schedule could not be saved to the database.  Transaction failed.");
        }
        
        redirect = req.getContextPath() + "/operator/Hardware/DeliverySchedule.jsp";
    }
    
    private void requestNewTimePeriod(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    { 
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        pBean.setCurrentTimePeriod(new ScheduleTimePeriod());
        pBean.getCurrentTimePeriod().setScheduleID(pBean.getCurrentSchedule().getScheduleID());
        
        redirect = req.getContextPath() + "/operator/Hardware/ScheduleTimePeriod.jsp";
    }
    
    private void loadTimePeriod(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    { 
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        List<ScheduleTimePeriod> times = pBean.getAvailableTimePeriods();
        Integer idToLoad = new Integer(req.getParameter("times"));
        
        for(int j = 0; j < times.size(); j++)
        {
            if(times.get(j).getTimePeriodID().compareTo(idToLoad) == 0)
                pBean.setCurrentTimePeriod(times.get(j));
        }
        
        redirect = req.getContextPath() + "/operator/Hardware/ScheduleTimePeriod.jsp";
    }
    
    private void handleTimePeriodChange(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        ScheduleTimePeriod currentPeriod = pBean.getCurrentTimePeriod();
        
        currentPeriod.setScheduleID(pBean.getCurrentSchedule().getScheduleID());
        currentPeriod.setTimePeriodName(req.getParameter("name"));
        currentPeriod.setQuantity(new Integer(req.getParameter("quantity")));
        Date shipDate = ServletUtil.parseDateStringLiberally( req.getParameter("shipDate"), pBean.getEnergyCompany().getDefaultTimeZone());
        if (shipDate == null)
        {
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid ship date '" + req.getParameter("ShipDate") + "'");
            redirect = req.getContextPath() + "/operator/Hardware/ScheduleTimePeriod.jsp";
        }
        
        currentPeriod.setPredictedShipDate(shipDate);
        
        try
        {
            /**
             * new time period
             */
            if(currentPeriod.getTimePeriodID() == null)
            {
                currentPeriod.setTimePeriodID(ScheduleTimePeriod.getNextTimePeriodID());
                Transaction.createTransaction(Transaction.INSERT, currentPeriod).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "New time projection added to this delivery schedule.");
            }
            else
            {
                Transaction.createTransaction(Transaction.UPDATE, currentPeriod).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Time projection successfully updated in the database.");
            }
        }
        catch (TransactionException e) 
        {
            CTILogger.error( e.getMessage(), e );
            e.printStackTrace();
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Time projection could not be saved to the database.  Transaction failed.");
        }
        
        redirect = req.getContextPath() + "/operator/Hardware/DeliverySchedule.jsp";
    }
    
    private void requestNewShipment(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    { 
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        pBean.setCurrentShipment(new Shipment());
        
        redirect = req.getContextPath() + "/operator/Hardware/Shipment.jsp";
    }
    
    private void loadShipment(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    { 
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        List<Shipment> shipments = pBean.getAvailableShipments();
        Integer idToLoad = new Integer(req.getParameter("shipments"));
        
        for(int j = 0; j < shipments.size(); j++)
        {
            if(shipments.get(j).getShipmentID().compareTo(idToLoad) == 0)
                pBean.setCurrentShipment(shipments.get(j));
        }
        
        redirect = req.getContextPath() + "/operator/Hardware/Shipment.jsp";
    }
    
    private void handleShipmentChange(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        Shipment currentShipment = pBean.getCurrentShipment();
        boolean shipmentFailed = false;
         
        currentShipment.setShipmentNumber(req.getParameter("name"));
        String warehouse = req.getParameter("warehouse");
        if(warehouse != null)
            currentShipment.setWarehouseID(new Integer(warehouse));
        else
            currentShipment.setWarehouseID(new Integer(0));
        
        Date orderedDate = ServletUtil.parseDateStringLiberally( req.getParameter("orderingDate"), pBean.getEnergyCompany().getDefaultTimeZone());
        Date shipDate = ServletUtil.parseDateStringLiberally( req.getParameter("shipDate"), pBean.getEnergyCompany().getDefaultTimeZone());
        Date receivedDate = ServletUtil.parseDateStringLiberally( req.getParameter("receivingDate"), pBean.getEnergyCompany().getDefaultTimeZone());
        if (shipDate == null)
        {
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid start date '" + req.getParameter("StartDate") + "'");
            redirect = req.getContextPath() + "/operator/Hardware/ScheduleTimePeriod.jsp";
        }
        
        currentShipment.setShipDate(shipDate);
        currentShipment.setReceivedDate(receivedDate);
        currentShipment.setOrderedDate(orderedDate);
        
        currentShipment.setActualPricePerUnit(new Double(req.getParameter("pricePerUnit")));
        currentShipment.setSalesTax(new Double(req.getParameter("salesTax")));
        currentShipment.setShippingCharges(new Double(req.getParameter("shippingCharges")));
        currentShipment.setOtherCharges(new Double(req.getParameter("otherCharges")));
        currentShipment.setSalesTotal(new Double(req.getParameter("total")));
        currentShipment.setAmountPaid(new Double(req.getParameter("amountPaid")));
        
        String serialStart = (req.getParameter("serialStart"));
        String serialEnd = (req.getParameter("serialEnd"));
        if(serialStart != null)
            currentShipment.setSerialNumberStart(serialStart);
        if(serialEnd != null)
            currentShipment.setSerialNumberEnd(serialEnd);
        
        try
        {
            /**
             * new time period
             */
            if(currentShipment.getShipmentID() == null)
            {
                currentShipment.setShipmentID(Shipment.getNextShipmentID());
                currentShipment.setScheduleID(pBean.getCurrentSchedule().getScheduleID());
                Transaction.createTransaction(Transaction.INSERT, currentShipment).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "New shipment added to this delivery schedule.");
            }
            else
            {
                Transaction.createTransaction(Transaction.UPDATE, currentShipment).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Shipment successfully updated in the database.");
             }
        }
        catch (TransactionException e) 
        {
            CTILogger.error( e.getMessage(), e );
            e.printStackTrace();
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Shipment could not be saved to the database.  Transaction failed.");
            shipmentFailed = true;
        }
        
        redirect = req.getContextPath() + "/operator/Hardware/DeliverySchedule.jsp";
        
        if(!shipmentFailed)
        {
            /*
             * Going to do some automation here
             * A: Need to check for existence of serial numbers already in the system
             * B: Create new with currentState = Ordered in appropriate warehouse
             * C: Probably will need to spin off a process task so we can time it
             * */
            if(serialStart != null && serialEnd != null)
            {
                InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
                if(iBean == null)
                {
                    session.setAttribute("inventoryBean", new InventoryBean());
                    iBean = (InventoryBean) session.getAttribute("inventoryBean");
                }
                
                /*
                 * Let's cheat a little and use the inventoryBean filtering to look for serial range.
                 */
                ArrayList tempList = new ArrayList();
                String devType = pBean.getCurrentSchedule().getModelID().toString();
                tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE), devType, devType));
                tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MAX), serialEnd, serialEnd));
                tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MIN), serialStart, serialStart));
                iBean.setFilterByList(tempList);
                
                iBean.setShipmentCheck(true);
                ArrayList found = iBean.getLimitedHardwareList();
                iBean.setShipmentCheck(false);
                //failure
                
                pBean.setAllowSerialNumberInput(true);
                if(found == null)
                {
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Inventory was unable to determine if serial range is pre-existing.  It is unsafe to create this range.");
                    redirect = req.getContextPath() + "/operator/Hardware/Shipment.jsp";
                }
                //found some, better not create this range
                else if(found.size() > 0)
                {
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The specified serial range includes switches already in inventory.  Unable to create new range.");
                    redirect = req.getContextPath() + "/operator/Hardware/Shipment.jsp";
                }
                else
                {
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Actual serial range has not yet been created in inventory.  Please verify the following information and click submit.");
                    redirect = req.getContextPath() + "/operator/Hardware/ShipmentSNRangeAdd.jsp";
                    pBean.setAllowSerialNumberInput(false);
                }
                    
            }
            else
                redirect = req.getContextPath() + "/operator/Hardware/DeliverySchedule.jsp";
        }
    }
    
    private void addSerialNumbersForShipment(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        Shipment shipment = pBean.getCurrentShipment();
        
        TimeConsumingTask task = new AddShipmentSNRangeTask( pBean.getSerialNumberMember(), shipment.getSerialNumberStart(), shipment.getSerialNumberEnd(), 
                                                             pBean.getCurrentSchedule().getModelID(), new Integer(pBean.getSerialNumberDeviceState().getEntryID()), 
                                                             shipment.getWarehouseID(), req );
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
                /**
                 * Need to make sure that the newly created serial range is saved to the shipment
                 */
                try
                {
                    Transaction.createTransaction(Transaction.UPDATE, shipment).execute();
                }
                catch (TransactionException e) 
                {
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Database error.  Serial range was added to inventory, but the shipment serial range fields could not be saved.");
                }
                return;
            }
            
            if (task.getStatus() == AddSNRangeTask.STATUS_ERROR) {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
                ProgressChecker.removeTask( id );
                redirect = referer;
                return;
            }
        }
        referer = req.getContextPath() + "/operator/Hardware/DeliverySchedule.jsp";
        redirect = req.getContextPath() + "/operator/Hardware/DeliverySchedule.jsp";
        session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
        session.setAttribute(ServletUtils.ATT_REFERRER, referer);
        redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
    }
    
    private void requestNewInvoice(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    { 
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        pBean.setCurrentInvoice(new Invoice());
        
        redirect = req.getContextPath() + "/operator/Hardware/Invoice.jsp";
    }
    
    private void loadInvoice(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    { 
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        List<Invoice> invoices = pBean.getAvailableInvoices();
        Integer idToLoad = new Integer(req.getParameter("invoices"));
        
        for(int j = 0; j < invoices.size(); j++)
        {
            if(invoices.get(j).getInvoiceID().compareTo(idToLoad) == 0)
                pBean.setCurrentInvoice(invoices.get(j));
        }
        
        redirect = req.getContextPath() + "/operator/Hardware/Invoice.jsp";
    }
    
    private void handleInvoiceChange(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        Invoice currentInvoice = pBean.getCurrentInvoice();
        
        currentInvoice.setInvoiceDesignation(req.getParameter("name"));
        currentInvoice.setDateSubmitted(ServletUtil.parseDateStringLiberally( req.getParameter("dateSubmitted"), pBean.getEnergyCompany().getDefaultTimeZone()));
        currentInvoice.setDatePaid(ServletUtil.parseDateStringLiberally( req.getParameter("datePaid"), pBean.getEnergyCompany().getDefaultTimeZone()));
        String authorized = req.getParameter("authorized");
        if(authorized != null)
            currentInvoice.setAuthorized("Y");
        else
            currentInvoice.setAuthorized("N");
        String hasPaid = req.getParameter("hasPaid");
        if(hasPaid != null)
            currentInvoice.setHasPaid("Y");
        else
            currentInvoice.setHasPaid("N");
        currentInvoice.setAuthorizedBy(req.getParameter("authorizedBy"));
        currentInvoice.setTotalQuantity(new Integer(req.getParameter("quantity")));
        currentInvoice.setAuthorizedNumber(req.getParameter("authorizedNum"));
                
        try
        {
            //new invoice
            if(currentInvoice.getInvoiceID() == null)
            {
                currentInvoice.setInvoiceID(Invoice.getNextInvoiceID());
                currentInvoice.setPurchasePlanID(pBean.getCurrentPlan().getPurchaseID());
                Transaction.createTransaction(Transaction.INSERT, currentInvoice).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "New invoice added to this purchase plan.");
                redirect = req.getContextPath() + "/operator/Hardware/Invoice.jsp";
            }
            else
            {
                Transaction.createTransaction(Transaction.UPDATE, currentInvoice).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Invoice successfully updated in the database.");
                redirect = req.getContextPath() + "/operator/Hardware/PurchaseTrack.jsp";
            }
            
            /**
             * Shipments --
             * For invoices, we need to worry about new and deleted ones.
             */
            String[] shipmentIDs = req.getParameterValues("shipments");
            List<Shipment> oldShipList = Shipment.getAllShipmentsForInvoice(currentInvoice.getInvoiceID());
            
            for(int i = 0; i < oldShipList.size(); i++)
            {
                boolean isFound = false;
                
                if(shipmentIDs != null)
                {    
                    for(int j = 0; j < shipmentIDs.length; j++)
                    {
                        if(shipmentIDs[j].compareTo(oldShipList.get(i).getShipmentID().toString()) == 0)
                        {
                            isFound = true;
                            break;
                        }
                    }
                }
                /*
                 * In old list only, must have been removed
                 * Just don't delete the whole shipment, just the mapping!!!!
                 */
                if(!isFound)
                {
                    Transaction.createTransaction(Transaction.DELETE_PARTIAL, oldShipList.get(i)).execute();
                }
            }
            
            if(shipmentIDs != null)
            {
                for(int j = 0; j < shipmentIDs.length; j++)
                {
                    boolean isFound = false;
                    
                    for(int i = 0; i < oldShipList.size(); i++)
                    {
                        if(shipmentIDs[j].compareTo(oldShipList.get(i).getShipmentID().toString()) == 0)
                        {
                            isFound = true;
                            break;
                        }
                    }
                    
                    /*
                     * In new list only, must have been added
                     */
                    if(!isFound)
                    {
                        currentInvoice.setShipmentID(new Integer(shipmentIDs[j]));
                        Transaction.createTransaction(Transaction.ADD_PARTIAL, currentInvoice).execute();
                    }
                }
            }
        }
        catch (TransactionException e) 
        {
            CTILogger.error( e.getMessage(), e );
            e.printStackTrace();
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invoice could not be saved to the database.  Transaction failed.");
        }
    }
}
