/*
 * Created on Apr 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsAppliance;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.database.db.hardware.LMHardwareConfiguration;
import com.cannontech.stars.database.db.hardware.StaticLoadGroupMapping;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.hardware.service.impl.PorterExpressComCommandBuilder;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.google.common.collect.Lists;

/**
 * @author yao
 *
 * TODO: See YUK-3063 for details on this topic at Xcel
 */
public class AdjustStaticLoadGroupMappingsTask extends TimeConsumingTask {
	
	LiteStarsEnergyCompany energyCompany = null;
	boolean fullReset = false;
    boolean sendConfig = true;
    String redirect;
	HttpSession session;
	
	List<StaticLoadGroupMapping> mappingsToAdjust = null;
	List<LiteLmHardwareBase> hwsToAdjust = null;
	List<LiteLmHardwareBase> configurationSet = new ArrayList<LiteLmHardwareBase>();
    List<String> failureInfo = new ArrayList<String>();
    //don't need since liteHw already has the config loaded....HashMap<Integer, com.cannontech.database.db.stars.hardware.LMHardwareConfiguration> existingConfigEntries;
	int numSuccess = 0, numFailure = 0;
	int numToBeConfigured = 0;
	
	public AdjustStaticLoadGroupMappingsTask(LiteStarsEnergyCompany energyCompany, boolean fullReset, boolean sendConfig, String redirect, HttpSession session)
	{
		this.energyCompany = energyCompany;
		this.fullReset = fullReset;
        this.sendConfig = sendConfig;
        this.redirect = redirect;
		this.session = session;
	}

	@Override
    public String getProgressMsg() {
		if (numToBeConfigured == 0)
            return "Mapping task is sorting static mappings and inventory items";
	    else if (fullReset) {
			if (status == STATUS_FINISHED && numFailure == 0) {
				return numSuccess + " switches have been mapped successfully to addressing groups.";
			}
			else
				return numSuccess + " of " + numToBeConfigured + " switches have been mapped to addressing groups.";
		}
		else {
			if (status == STATUS_FINISHED && numFailure == 0) {
			    return numSuccess + " switches mapped to addressing groups.";
			}
			else
				return numSuccess + " of " + numToBeConfigured + " switches mapped to addressing groups.";
		}
	}

	public void run() {
		StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        
        StringBuffer logEntry = new StringBuffer();
        if (fullReset)
            logEntry.append("Full reset ");
        else
            logEntry.append("Adjusting all zero entries ");
        if (sendConfig)
            logEntry.append("and configs will be sent out if possible.");
        else
            logEntry.append("but configs will NOT be attempted.");
            
        ActivityLogger.logEvent( user.getUserID(), "STATIC LOAD GROUP MAPPING ACTIVITY", logEntry.toString());
        
		mappingsToAdjust = StaticLoadGroupMapping.getAllStaticLoadGroups();
		if (mappingsToAdjust == null || mappingsToAdjust.size() < 1) {
			status = STATUS_ERROR;
			errorMsg = "There are no static load group mappings available.";
			return;
		}
		
		status = STATUS_RUNNING;
		
		boolean searchMembers = DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS )
				&& energyCompany.hasChildEnergyCompanies();
		hwsToAdjust = new ArrayList<LiteLmHardwareBase>();
        
        InventoryBaseDao inventoryBaseDao = 
        	YukonSpringHook.getBean("inventoryBaseDao", InventoryBaseDao.class);
        LMHardwareControlGroupDao lmHardwareControlGroupDao = 
            YukonSpringHook.getBean("lmHardwareControlGroupDao", LMHardwareControlGroupDao.class);        
        
		List<YukonEnergyCompany> energyCompanyList = Lists.newArrayList();
		if (!searchMembers) {
			energyCompanyList.add(energyCompany);
		} else {
			energyCompanyList.addAll(ECUtils.getAllDescendants(energyCompany));
		} 
		
		if (!fullReset) {
			// If not a full reset, we will want to only look for those with an addressing group ID of zero
			hwsToAdjust = inventoryBaseDao.getAllLMHardwareWithoutLoadGroups(energyCompanyList);
		} else {
			hwsToAdjust = inventoryBaseDao.getAllLMHardware(energyCompanyList);
		}
		
		numToBeConfigured = hwsToAdjust.size();
		if (numToBeConfigured == 0) {
			status = STATUS_ERROR;
			errorMsg = "No inventory could be loaded.";
			return;
		}
		
        String options = null;
        
		for (int i = 0; i < hwsToAdjust.size(); i++) {
            if (isCanceled) {
                status = STATUS_CANCELED;
                return;
            }
            
            LiteLmHardwareBase liteHw = hwsToAdjust.get(i);
			LiteStarsEnergyCompany company = energyCompany;
						
            //get the current Configuration
            LMHardwareConfiguration configDB = LMHardwareConfiguration.getLMHardwareConfigurationFromInvenID(liteHw.getInventoryID());
            
            if (configDB == null) {
                configurationSet.add( hwsToAdjust.get(i) );
                numFailure++;
                failureInfo.add("An LMHardwareConfiguration entry for switch " + liteHw.getManufacturerSerialNumber() 
                                + " could not be found.  It is likely this switch has not been added to an account.");
                continue;
            }
                
            LiteAccountInfo liteAcctInfo = energyCompany.getCustAccountInformation( liteHw.getAccountID(), true );
            //get zipCode
            LiteAddress address = energyCompany.getAddress(liteAcctInfo.getAccountSite().getStreetAddressID());
            String zip = address.getZipCode();
            if (zip.length() > 5) {
                zip = zip.substring(0, 5);
            }
            //get ConsumptionType
            LiteCustomer cust = liteAcctInfo.getCustomer();
            Integer consumptionType = -1;
            if (cust instanceof LiteCICustomer && cust.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI) {
                consumptionType = ((LiteCICustomer)cust).getCICustType();
            }
            //get ApplianceCategoryID
            Integer applianceCatID = -1;
            int programId = -1;
            for (LiteStarsAppliance liteApp : liteAcctInfo.getAppliances()) {
                if (liteApp.getApplianceID() == configDB.getApplianceID().intValue()){
                    applianceCatID = liteApp.getApplianceCategory().getApplianceCategoryId();
                    programId = liteApp.getProgramID();
                    break;
                }
            }
            if (applianceCatID == -1) {
                configurationSet.add( hwsToAdjust.get(i) );
                numFailure++;
                failureInfo.add("An appliance could not be detected for serial number " + liteHw.getManufacturerSerialNumber() 
                                + ".  It is likely that this switch is not assigned to an account, or no appliance was created on that account.");
                continue;
            }
            
            //get SwitchTypeID 
            Integer devType = liteHw.getLmHardwareTypeID();
            StaticLoadGroupMapping groupMapping = StaticLoadGroupMapping.getAStaticLoadGroupMapping(applianceCatID, zip, consumptionType, devType);
            if (groupMapping == null) {
                configurationSet.add( hwsToAdjust.get(i) );
                numFailure++;
                failureInfo.add("A static mapping could not be determined for serial number " + liteHw.getManufacturerSerialNumber() 
                                + ".  ApplianceCategoryID=" + applianceCatID + ", ZipCode=" + zip + ", ConsumptionTypeID=" 
                                + consumptionType + ",SwitchTypeID=" + devType);
                continue;
            }
            
            try {
                configDB.setAddressingGroupID(groupMapping.getLoadGroupID());
                try {
                    Transaction.createTransaction( Transaction.UPDATE, configDB ).execute();
                    
                    LMHardwareControlGroup existingEnrollment = 
                        lmHardwareControlGroupDao.findCurrentEnrollmentByInventoryIdAndProgramIdAndAccountId(liteHw.getInventoryID(), programId, liteHw.getAccountID());
                    if (existingEnrollment != null) {
                        existingEnrollment.setLmGroupId(groupMapping.getLoadGroupID());
                        lmHardwareControlGroupDao.update(existingEnrollment);
                    } else {
                        configurationSet.add( hwsToAdjust.get(i) );
                        numFailure++;
                        failureInfo.add("An LMHardwareControlGroup entry for switch " + liteHw.getManufacturerSerialNumber() 
                                        + " could not be found.  It is likely this switch has not been added to an account.");
                        continue;                        
                    }
                } catch(TransactionException e) {
                    throw new WebClientException(e.getMessage());
                }
                options = "GroupID:" + groupMapping.getLoadGroupID(); 
                
                if (sendConfig) {
                    PorterExpressComCommandBuilder xcomCommandBuilder = YukonSpringHook.getBean("porterExpressComCommandBuilder", PorterExpressComCommandBuilder.class);
                    xcomCommandBuilder.fileWriteConfigCommand(company, liteHw, false, options);
    				
    				if (liteHw.getAccountID() > 0) {
    					StarsCustAccountInformation starsAcctInfo = company.getStarsCustAccountInformation( liteHw.getAccountID() );
    					if (starsAcctInfo != null) {
    						StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, company );
    						StarsUtils.populateInventoryFields( starsAcctInfo, starsInv );
    					}
    				}
    			}
    			
                /*
                 * Jon Gill may wish to use the queue instead of going straight out to his batch files
                 *else {
    				SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
    				cmd.setEnergyCompanyID( company.getLiteID() );
    				cmd.setAccountID( liteHw.getAccountID() );
    				cmd.setInventoryID( liteHw.getInventoryID() );
    				cmd.setCommandType( SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE );
    				cmd.setInfoString( options );
    				
    				SwitchCommandQueue.getInstance().addCommand( cmd, false );
    			}*/
    			
    			numSuccess++;
			}
			catch (WebClientException e) {
				CTILogger.error( e.getMessage() , e );
				if (errorMsg == null) errorMsg = e.getMessage();
				configurationSet.add( hwsToAdjust.get(i) );
				numFailure++;
                failureInfo.add("A static mapping could not be saved for serial number " + liteHw.getManufacturerSerialNumber() 
                                    + ".  ApplianceCategoryID=" + applianceCatID + ", ZipCode=" + zip + ", ConsumptionTypeID=" 
                                    + consumptionType + ",SwitchTypeID=" + devType);
			}
		}
		
		//if (!fullReset) SwitchCommandQueue.getInstance().addCommand( null, true );
		status = STATUS_FINISHED;
         ActivityLogger.logEvent( user.getUserID(), "STATIC LOAD GROUP MAPPING ACTIVITY", "Attempt succeeded." );
        session.removeAttribute( InventoryManagerUtil.INVENTORY_SET );
		session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Addressing Groups were reset to static mapped values for " + numSuccess + " switches");
        
        if (numFailure > 0) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Addressing Groups adjustment failed for " + numFailure + " switches");
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET, configurationSet);
			session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		}
	}
}
