/*
 * Created on Apr 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMConfiguration;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.appliance.ApplianceBase;
import com.cannontech.database.db.stars.hardware.LMHardwareConfiguration;
import com.cannontech.database.db.stars.hardware.StaticLoadGroupMapping;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.UpdateLMHardwareConfigAction;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AdjustStaticLoadGroupMappingsTask extends TimeConsumingTask {
	
	LiteStarsEnergyCompany energyCompany = null;
	boolean fullReset = false;
    boolean sendConfig = true;
	HttpServletRequest request = null;
	
	List<StaticLoadGroupMapping> mappingsToAdjust = null;
	ArrayList hwsToAdjust = null;
	ArrayList configurationSet = new ArrayList();
    List<String> failureInfo = new ArrayList<String>();
    HashMap<Integer, LMHardwareConfiguration> existingsConfigs = new HashMap<Integer, LMHardwareConfiguration>();
    //don't need since liteHw already has the config loaded....HashMap<Integer, com.cannontech.database.db.stars.hardware.LMHardwareConfiguration> existingConfigEntries;
	int numSuccess = 0, numFailure = 0;
	int numToBeConfigured = 0;
	
	public AdjustStaticLoadGroupMappingsTask(LiteStarsEnergyCompany energyCompany, boolean fullReset, boolean sendConfig, HttpServletRequest request)
	{
		this.energyCompany = energyCompany;
		this.fullReset = fullReset;
        this.sendConfig = sendConfig;
		this.request = request;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if(numToBeConfigured == 0)
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

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		HttpSession session = request.getSession(false);
		StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        
        StringBuffer logEntry = new StringBuffer();
        if(fullReset)
            logEntry.append("Full reset ");
        else
            logEntry.append("Adjusting all zero entries ");
        if(sendConfig)
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
				&& energyCompany.getChildren().size() > 0;
		hwsToAdjust = new ArrayList();
        
        if(!fullReset)
            existingsConfigs = LMHardwareConfiguration.getAllLMHardwareConfigurationsWithoutLoadGroups();
        
		if (!searchMembers) {
			List<LiteInventoryBase> hwsFromEC = energyCompany.loadAllInventory(true);
            /*
             * If not a full reset, we will want to only look for those with an addressing group ID of zero
             */
			for (int j = 0; j < hwsFromEC.size(); j++) {
				if (hwsFromEC.get(j) instanceof LiteStarsLMHardware) {
                    if(!fullReset)
                    { 
                        LMHardwareConfiguration config = existingsConfigs.get(hwsFromEC.get(j).getInventoryID());
                        if(config != null) {
                            hwsToAdjust.add( hwsFromEC.get(j) );
                        }
                    }
                    else
                        hwsToAdjust.add( hwsFromEC.get(j) );
                }
			}
		}
        /*May have a problem here if inventory isn't loaded yet since the for loop may move on even if the load task hasn't come back
         */
		else {
			ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
			for (int i = 0; i < descendants.size(); i++) {
				LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
                List<LiteInventoryBase> hwsFromEC = company.loadAllInventory(true);
                /*  
                 * If not a full reset, we will want to only look for those with an addressing group ID of zero
                 */
                for (int j = 0; j < hwsFromEC.size(); j++) {
                    if (hwsFromEC.get(j) instanceof LiteStarsLMHardware) {
                        if(!fullReset)
                        { 
                            LMHardwareConfiguration config = existingsConfigs.get(hwsFromEC.get(j).getInventoryID());
                            if(config != null) {
                                hwsToAdjust.add( hwsFromEC.get(j) );
                            }
                        }
                        else
                            hwsToAdjust.add( hwsFromEC.get(j) );
                    }
                }
			}
		}
		
		numToBeConfigured = hwsToAdjust.size();
		if (numToBeConfigured == 0) {
			status = STATUS_ERROR;
			errorMsg = "No inventory could be loaded.";
			return;
		}
		
        existingsConfigs = null;
		/*TODO: shouldn't need to support hardware addressing, but make sure 
		 *User has specified a new configuration
		 *StarsLMConfiguration hwConfig = null;
         * if (request.getParameter("UseHardwareAddressing") != null) {
			hwConfig = new StarsLMConfiguration();
			try {
				InventoryManagerUtil.setStarsLMConfiguration( hwConfig, request );
			}
			catch (WebClientException e) {
				CTILogger.error( e.getMessage(), e );
				status = STATUS_ERROR;
				errorMsg = e.getMessage();
				return;
			}
		}
		else {*/
        String options = null;
        
		for (int i = 0; i < hwsToAdjust.size(); i++) {
			
            if (isCanceled) {
                status = STATUS_CANCELED;
                return;
            }
            
            LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hwsToAdjust.get(i);;
			LiteStarsEnergyCompany company = energyCompany;
						
            //get the current Configuration
            com.cannontech.database.db.stars.hardware.LMHardwareConfiguration configDB = com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.getLMHardwareConfigurationFromInvenID(liteHw.getInventoryID());
            
            if(configDB == null) {
                configurationSet.add( hwsToAdjust.get(i) );
                numFailure++;
                failureInfo.add("An LMHardwareConfiguration entry for switch " + liteHw.getManufacturerSerialNumber() 
                                + " could not be found.  It is likely this switch has not been added to an account.");
                continue;
            }
                
            LiteStarsCustAccountInformation liteAcctInfo = 
                energyCompany.getCustAccountInformation( liteHw.getAccountID(), true );
            //get zipCode
            LiteAddress address = energyCompany.getAddress(liteAcctInfo.getAccountSite().getStreetAddressID());
            String zip = address.getZipCode();
            if(zip.length() > 5)
                zip = zip.substring(0, 5);
            //get ConsumptionType
            LiteCustomer cust = liteAcctInfo.getCustomer();
            Integer consumptionType = -1;
            if(cust instanceof LiteCICustomer)
                consumptionType = ((LiteCICustomer)cust).getCICustType();
            //get ApplianceCategoryID
            Integer applianceCatID = -1;
            for(int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
                if(liteAcctInfo.getAppliances().get(j).getApplianceID() == configDB.getApplianceID().intValue())
                    applianceCatID = liteAcctInfo.getAppliances().get(j).getApplianceCategoryID();
            }
            if(applianceCatID == -1) {
                configurationSet.add( hwsToAdjust.get(i) );
                numFailure++;
                failureInfo.add("An appliance could not be detected for serial number " + liteHw.getManufacturerSerialNumber() 
                                + ".  It is likely that this switch is not assigned to an account, or no appliance was created on that account.");
                continue;
            }
            
            //get SwitchTypeID 
            Integer devType = liteHw.getLmHardwareTypeID();
            StaticLoadGroupMapping groupMapping = StaticLoadGroupMapping.getAStaticLoadGroupMapping(applianceCatID, zip, consumptionType, devType);
            if(groupMapping == null) {
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
                } 
                catch(TransactionException e) {
                    throw new WebClientException(e.getMessage());
                }
                options = "GroupID:" + groupMapping.getLoadGroupID(); 
                
    			/*TODO: Will I need this to make sure accounts/inventory get the change?
                if (hwConfig != null)
    				UpdateLMHardwareConfigAction.updateLMConfiguration( hwConfig, liteHw, company );
    			*/
    				
                if (sendConfig) {
    				YukonSwitchCommandAction.fileWriteConfigCommand(company, liteHw, false, options);
    				
    				if (liteHw.getAccountID() > 0) {
    					StarsCustAccountInformation starsAcctInfo = company.getStarsCustAccountInformation( liteHw.getAccountID() );
    					if (starsAcctInfo != null) {
    						StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, company );
    						YukonSwitchCommandAction.parseResponse( starsAcctInfo, starsInv );
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
			session.setAttribute(ServletUtils.ATT_REDIRECT, request.getContextPath() + "/operator/Hardware/PowerUserStaticLoadGroupReset.jsp");
		}
	}
}
