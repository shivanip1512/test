package com.cannontech.stars.web.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelperHolder;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
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
public class DeleteLMHardwareAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsOperation operation = null;
			if (req.getParameter("InvID") != null) {
				// Request directly from the webpage
				operation = getRequestOperation( req );
			}
			else {
				// Request redirected from InventoryManager
				operation = (StarsOperation) session.getAttribute(InventoryManagerUtil.STARS_INVENTORY_OPERATION);
				session.removeAttribute( InventoryManagerUtil.STARS_INVENTORY_OPERATION );
			}
			
			return SOAPUtil.buildSOAPMessage( operation );
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
        	
			StarsDeleteLMHardware delHw = reqOper.getStarsDeleteLMHardware();
			StarsInventoryBaseDao starsInventoryBaseDao = 
				YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class);
			LiteInventoryBase liteInv = starsInventoryBaseDao.getByInventoryId(delHw.getInventoryID());
			
			if (liteInv.getAccountID() == 0) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "The hardware doesn't belong to any customer account") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
            //Use account in session, if inventory belongs to it
            //else retreive account (ex. inventory deleted from inventory list)
            LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            if (liteAcctInfo == null || liteAcctInfo.getAccountID() != liteInv.getAccountID()) {
                liteAcctInfo = energyCompany.getCustAccountInformation( liteInv.getAccountID(), true );    
            }			
			
			removeInventory( delHw, liteAcctInfo, energyCompany );
			
        	// Response will be handled here, instead of in parse()
			StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteInv.getAccountID() );
			if (starsAcctInfo != null)
				parseResponse( delHw.getInventoryID(), starsAcctInfo );
			
			respOper.setStarsSuccess( new StarsSuccess() );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot delete the hardware") );
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
			StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
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
	
	public static StarsOperation getRequestOperation(HttpServletRequest req) {
		StarsDeleteLMHardware delHw = new StarsDeleteLMHardware();
		delHw.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
		
		String delAction = req.getParameter("DeleteAction");
		if (delAction == null || delAction.equalsIgnoreCase("DeleteFromInventory")) {
			delHw.setDeleteFromInventory( true );
		}
		else if (delAction.equalsIgnoreCase("DeleteFromYukon")) {
			delHw.setDeleteFromInventory( true );
			delHw.setDeleteFromYukon( true );
		}
		
		StarsOperation operation = new StarsOperation();
		operation.setStarsDeleteLMHardware( delHw );
		return operation;
	}
	
	/**
	 * Remove a hardware from a customer account. If liteAcctInfo is null,
	 * it means the account this hardware belongs to has also been deleted.
	 */
	public static void removeInventory(StarsDeleteLMHardware deleteHw, 
	                                   LiteStarsCustAccountInformation liteAcctInfo, 
	                                   LiteStarsEnergyCompany energyCompany)
		throws WebClientException
	{
        ApplianceDao applianceDao = YukonSpringHook.getBean("applianceDao", ApplianceDao.class);
	    
		try {
			StarsInventoryBaseDao starsInventoryBaseDao = 
				YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class);
			LiteInventoryBase liteInv = starsInventoryBaseDao.getByInventoryId(deleteHw.getInventoryID());
			
			try {
    		    // Unenrolls the inventory from all its programs (inside below catch block as well)
				if (liteAcctInfo != null && liteAcctInfo.getAccountID() > 0) {  //Enrollments are only applicable for inventory assigned to an account
                    EnrollmentHelperService enrollmentHelperService = YukonSpringHook.getBean("enrollmentService", EnrollmentHelperService.class);
                    EnrollmentHelper enrollmentHelper = new EnrollmentHelper();

    				CustomerAccountDao customerAccountDao = YukonSpringHook.getBean("customerAccountDao", CustomerAccountDao.class);
        			CustomerAccount customerAccount = customerAccountDao.getById(liteAcctInfo.getAccountID());
	    			enrollmentHelper.setAccountNumber(customerAccount.getAccountNumber());
	    
	    			LMHardwareBaseDao lmHardwareBaseDao = YukonSpringHook.getBean("hardwareBaseDao", LMHardwareBaseDao.class);
	    			LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(deleteHw.getInventoryID());
	    			enrollmentHelper.setSerialNumber(lmHardwareBase.getManufacturerSerialNumber());
	    			
	    			EnrollmentHelperHolder enrollmentHelperHolder = new EnrollmentHelperHolder(enrollmentHelper, customerAccount, lmHardwareBase, energyCompany);
	    			enrollmentHelperService.doEnrollment(enrollmentHelperHolder, EnrollmentEnum.UNENROLL, energyCompany.getUser());
    			}
    			
			} catch (NotFoundException e) {
			    // able to ignore because it is possible that we don't have an LMHardwareBase but that we have a reference to a yukonPaobject instead
	        }
			
			if (deleteHw.getDeleteFromInventory()) {
				InventoryManagerUtil.deleteInventory( liteInv, energyCompany, deleteHw.getDeleteFromYukon() );
			}
			else {
				java.util.Date removeDate = deleteHw.getRemoveDate();
				if (removeDate == null) removeDate = new java.util.Date();
        		
				// Add "Uninstall" to hardware events
				com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
				com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
				com.cannontech.database.db.stars.event.LMCustomerEventBase eventBaseDB = event.getLMCustomerEventBase();
        		
				int hwEventEntryID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
				int uninstallActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_UNINSTALL ).getEntryID();
    			
				eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
				eventBaseDB.setActionID( new Integer(uninstallActID) );
				eventBaseDB.setEventDateTime( removeDate );
				if (liteAcctInfo != null && liteAcctInfo.getAccountID() > 0) {
					eventBaseDB.setNotes( "Removed from account #" + liteAcctInfo.getCustomerAccount().getAccountNumber() );
				}
				eventDB.setInventoryID( new Integer(liteInv.getInventoryID()) );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
				
				event = Transaction.createTransaction( Transaction.INSERT, event ).execute();
				
				if (liteInv instanceof LiteStarsLMHardware)
					applianceDao.deleteAppliancesByAccountIdAndInventoryId(liteAcctInfo.getAccountID(), liteInv.getInventoryID());
				
				// Removes any entries found in the inventoryBase Table
				com.cannontech.database.db.stars.hardware.InventoryBase invDB =
						new com.cannontech.database.db.stars.hardware.InventoryBase();
				StarsLiteFactory.setInventoryBase( invDB, liteInv );
				
				invDB.setAccountID( new Integer(CtiUtilities.NONE_ZERO_ID) );
				invDB.setRemoveDate( removeDate );
				invDB.setDeviceLabel( "" );
				Transaction.createTransaction( Transaction.UPDATE, invDB ).execute();
				
			}
    		
			if (liteAcctInfo != null && liteAcctInfo.getAccountID() > 0) {
				if (InventoryUtils.isLMHardware( liteInv.getCategoryID() )) {
                    List<LiteStarsAppliance> liteApps = liteAcctInfo.getAppliances();
					
					for (int i = 0; i < liteApps.size(); i++) {
						LiteStarsAppliance liteApp = liteApps.get(i);
						
						if (liteApp.getInventoryID() == liteInv.getInventoryID()) {
							liteApp.setInventoryID( 0 );
							
							for (int j = 0; j < liteAcctInfo.getPrograms().size(); j++) {
								LiteStarsLMProgram liteProg = liteAcctInfo.getPrograms().get(j);
								
								if (liteProg.getProgramID() == liteApp.getProgramID()) {
									liteApp.setProgramID(0);
									liteProg.setGroupID( 0 );
									break;
								}
							}
						}
					}
				}
    			
				liteAcctInfo.getInventories().remove( new Integer(liteInv.getInventoryID()) );
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			if (e instanceof WebClientException)
				throw (WebClientException)e;

			throw new WebClientException( "Failed to remove the hardware", e );
		}
	}
	
	public static void parseResponse(int invID, StarsCustAccountInformation starsAcctInfo) {
		for (int i = 0; i < starsAcctInfo.getStarsAppliances().getStarsApplianceCount(); i++) {
			StarsAppliance appliance = starsAcctInfo.getStarsAppliances().getStarsAppliance(i);
			if (appliance.getInventoryID() == invID) {
				appliance.setInventoryID( 0 );
				for (int j = 0; j < starsAcctInfo.getStarsLMPrograms().getStarsLMProgramCount(); j++) {
					StarsLMProgram program = starsAcctInfo.getStarsLMPrograms().getStarsLMProgram(j);
					if (program.getProgramID() == appliance.getProgramID()) {
						program.setGroupID( 0 );
						break;
					}
				}
			}
		}
		
		StarsInventories inventories = starsAcctInfo.getStarsInventories();
		for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
			if (inventories.getStarsInventory(i).getInventoryID() == invID) {
				inventories.removeStarsInventory(i);
				return;
			}
		}
	}

}
