package com.cannontech.stars.core.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.service.StarsInventoryBaseService;
import com.cannontech.stars.core.service.StarsTwoWayLcrYukonDeviceAssignmentService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelperHolder;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.exception.Lcr3102YukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyAssignedException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.StarsInvalidArgumentException;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;

public class StarsInventoryBaseServiceImpl implements StarsInventoryBaseService {
    private HardwareEventLogService hardwareEventLogService;
    private StarsSearchDao starsSearchDao;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private LMHardwareConfigurationDao lmHardwareConfigurationDao;
    private LMHardwareEventDao hardwareEventDao;
    private ApplianceDao applianceDao;
    private StarsTwoWayLcrYukonDeviceAssignmentService starsTwoWayLcrYukonDeviceAssignmentService;
    private YukonListDao yukonListDao;
    private EnrollmentHelperService enrollmentService;
    private CustomerAccountDao customerAccountDao;
    private LMHardwareBaseDao hardwareBaseDao;
    private InventoryDao inventoryDao;
    private GatewayDeviceDao gatewayDeviceDao;
    
    // ADD DEVICE TO ACCOUNT
    @Override
    @Transactional
    public LiteInventoryBase addDeviceToAccount(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user, boolean allowCreateLcrIfAlreadyHasAssignedDevice) throws Lcr3102YukonDeviceCreationException {

        boolean lmHardware = InventoryUtils.isLMHardware(liteInv.getCategoryID());
        if (liteInv.getLiteID() <= 0) {
            if (lmHardware) {
                LiteStarsLMHardware lmHw = (LiteStarsLMHardware) liteInv;
                // create LMHardware here
                liteInv = starsInventoryBaseDao.saveLmHardware(lmHw,
                                                               energyCompany.getEnergyCompanyId());
            }
        }
        // existing inventory
        else {
            LiteInventoryBase liteInvPrev = starsInventoryBaseDao.getByInventoryId(liteInv.getLiteID());
            // Error, if the device is already assigned to another account
            // If replaceAccount is desired, caller needs to remove from the
            // previous account, then add to the new account
            if (liteInvPrev.getAccountID() > 0) {
                throw new StarsDeviceAlreadyAssignedException(liteInv.getAccountID(),
                                                              liteInv.getInventoryID(),
                                                              energyCompany.getName());                    
            }
            if (lmHardware) {
                // update LMHardware here
                liteInv = starsInventoryBaseDao.saveLmHardware((LiteStarsLMHardware) liteInv,
                                                               energyCompany.getEnergyCompanyId());
            }
        }

        // Account specific setup
        if (liteInv.getAccountID() > 0) {
            if (lmHardware) {
                LiteStarsLMHardware lmHw = (LiteStarsLMHardware) liteInv;
                // update static LMHardwareConfiguration here
                if (VersionTools.staticLoadGroupMappingExists()) {
                    initStaticLoadGroup(lmHw, energyCompany);
                }
            }
            // add install hardware event here
            addInstallHardwareEvent(liteInv, energyCompany, user);

            // Add Device status event here
            if (liteInv.getCurrentStateID() > 0) {
                addDeviceStatusEvent(liteInv, energyCompany, user);
            }

        }
        
        // CREATE ADDITIONAL YUKON DEVICE FOR TWO WAY LCR
        if(lmHardware) {
        	LiteStarsLMHardware lmHw = (LiteStarsLMHardware) liteInv;
        	int hardwareTypeID = lmHw.getLmHardwareTypeID();
	        if (InventoryUtils.isTwoWayLcr(hardwareTypeID)) {
	        	
	    		YukonListEntry entry = yukonListDao.getYukonListEntry(hardwareTypeID);
	    		String deviceTypeName = entry.getEntryText();
	    		int yukonDeviceTypeId = PAOGroups.getDeviceType(deviceTypeName);
	    		if (!DeviceTypesFuncs.isTwoWayLcr(yukonDeviceTypeId)) {
	    			throw new Lcr3102YukonDeviceCreationException("Selected yukon device must be a Two Way LCR.");
	    		}
	    		
	        	starsTwoWayLcrYukonDeviceAssignmentService.assignNewDeviceToLcr(liteInv, energyCompany, yukonDeviceTypeId, null, null, allowCreateLcrIfAlreadyHasAssignedDevice);
	        }
        }
        
        CustomerAccount customerAccount = customerAccountDao.getById(liteInv.getAccountID());
        hardwareEventLogService.hardwareAdded(user, liteInv.getDeviceLabel(), 
                                              customerAccount.getAccountNumber());
        
        return liteInv;
    }

    @Override
    public void initStaticLoadGroup(LiteStarsLMHardware lmHw, LiteStarsEnergyCompany energyCompany) {
        // get the static load group mapping
        LiteStarsCustAccountInformation liteAcct = starsCustAccountInformationDao.getByAccountId(lmHw.getAccountID());
        LMHardwareConfiguration lmHwConfig = lmHardwareConfigurationDao.getStaticLoadGroupMapping(liteAcct,
                                                                                                 lmHw,
                                                                                                 energyCompany);
        // save the static load group mapping
        if (lmHwConfig != null) {
            lmHardwareConfigurationDao.add(lmHwConfig);
        }
    }

    @Override
    public void addInstallHardwareEvent(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {

        int hwEventTypeID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE)
                                         .getEntryID();
        int installActionID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL)
                                           .getEntryID();

        LiteLMHardwareEvent lmHwEvent = new LiteLMHardwareEvent();
        lmHwEvent.setInventoryID(liteInv.getInventoryID());
        lmHwEvent.setEventTypeID(hwEventTypeID);
        lmHwEvent.setActionID(installActionID);
        lmHwEvent.setEventDateTime(liteInv.getInstallDate());
        lmHwEvent.setNotes(liteInv.getNotes());
        lmHwEvent.setAuthorizedBy(user.getUsername());

        hardwareEventDao.add(lmHwEvent, energyCompany.getEnergyCompanyId());
    }

    // adds the Device status event
    private void addDeviceStatusEvent(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {

        // get the entry ids needed to add device status events
        int hwEventTypeID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE)
                                         .getEntryID();
        int completedActId = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED)
                                          .getEntryID();
        // update device status
        liteInv.updateDeviceStatus();
        int statusDefID = yukonListDao
                                    .getYukonListEntry(liteInv.getCurrentStateID())
                                    .getYukonDefID();
        if (statusDefID != liteInv.getDeviceStatus()) {
            // add the device status event here
            if (statusDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL) {
                // If device status is available, add "Completed" event
                LiteLMHardwareEvent lmHwEvent = new LiteLMHardwareEvent();
                lmHwEvent.setActionID(completedActId);
                lmHwEvent.setInventoryID(liteInv.getInventoryID());
                lmHwEvent.setEventTypeID(hwEventTypeID);
                lmHwEvent.setEventDateTime(new Date().getTime());
                lmHwEvent.setNotes("Event added to match the device status");
                lmHwEvent.setAuthorizedBy(user.getUsername());                
                hardwareEventDao.add(lmHwEvent, energyCompany.getEnergyCompanyId());                
            }
        }
    }

    
    // UPDATE DEVICE ON ACCOUNT
    @Override
    @Transactional
    public LiteInventoryBase updateDeviceOnAccount(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {

        try {
            boolean lmHardware = InventoryUtils.isLMHardware(liteInv.getCategoryID());
            if (lmHardware) {
                LiteStarsLMHardware lmHw = (LiteStarsLMHardware) liteInv;

                // serialNumber validation here
                String newSerialNo = lmHw.getManufacturerSerialNumber();
                if (StringUtils.isBlank(newSerialNo)) {
                    throw new StarsInvalidArgumentException("Serial Number is required");
                }

                // see if serialNumber is changed, if so, see it doesn't already
                // exist on another device
                LiteStarsLMHardware lmHwPrev = (LiteStarsLMHardware) starsSearchDao.getById(lmHw.getInventoryID(), energyCompany);
                if (!lmHwPrev.getManufacturerSerialNumber().equals(newSerialNo) && starsSearchDao.searchLMHardwareBySerialNumber(newSerialNo,
                                                                                                                                 energyCompany) != null) {
                    throw new StarsDeviceSerialNumberAlreadyExistsException(newSerialNo,
                                                                            energyCompany.getName());
                }

                // save LMHardware here
                liteInv = starsInventoryBaseDao.saveLmHardware(lmHw,
                                                               energyCompany.getEnergyCompanyId());
                
                // CREATE ADDITIONAL YUKON DEVICE FOR TWO WAY LCR
                // - only if this is a Two Way LCR that does not yet have a Yukon device assigned to it
                // - updateDeviceOnAccount() does not support updating a Yukon device already assigned
                int hardwareTypeID = lmHw.getLmHardwareTypeID();
                if (InventoryUtils.isTwoWayLcr(hardwareTypeID)) {
                	if (liteInv.getDeviceID() < 1) {
                		
                		YukonListEntry entry = yukonListDao.getYukonListEntry(hardwareTypeID);
        	    		String deviceTypeName = entry.getEntryText();
        	    		int yukonDeviceTypeId = PAOGroups.getDeviceType(deviceTypeName);
        	    		if (!DeviceTypesFuncs.isTwoWayLcr(yukonDeviceTypeId)) {
        	    			throw new StarsDeviceSerialNumberAlreadyExistsException("Selected yukon device must be a Two Way LCR.");
        	    		} 
        	    		
                		starsTwoWayLcrYukonDeviceAssignmentService.assignNewDeviceToLcr(liteInv, energyCompany, yukonDeviceTypeId, null, null, false);
                	}
                }
            }
            // update install event
            updateInstallHardwareEvent(liteInv, energyCompany, user);
        } catch (ObjectInOtherEnergyCompanyException e) {
            throw new RuntimeException(e);
        }

        return liteInv;
    }

    // Update the Install hardware event
    private void updateInstallHardwareEvent(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {

        // Update the "install" event if necessary
        int installActionID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL)
                                           .getEntryID();
        List<LiteLMHardwareEvent> hwHist = hardwareEventDao.getByInventoryAndActionId(liteInv.getInventoryID(),
                                                                                      installActionID);
        // update the most recent event found
        LiteLMHardwareEvent hwEvent = null;
        if (hwHist.size() > 0) {
            hwEvent = hwHist.get(0);
        }
        if (hwEvent != null) {

            if ((liteInv.getInstallDate() > 0 && hwEvent.getEventDateTime() != liteInv.getInstallDate()) || hwEvent.getNotes() == null || !hwEvent.getNotes()
                                                                                                                                                  .equals(liteInv.getNotes())) {

                if (liteInv.getInstallDate() > 0) {
                    hwEvent.setEventDateTime(liteInv.getInstallDate());
                }
                hwEvent.setNotes(liteInv.getNotes());
                hwEvent.setAuthorizedBy(user.getUsername());                
                hardwareEventDao.update(hwEvent);
            }
        }
    }

    
    // REMOVE DEVICE FROM ACCOUNT
    @Override
    @Transactional
    public void removeDeviceFromAccount(LiteInventoryBase liteInventory,
            boolean deleteFromInventory, 
            LiteStarsEnergyCompany energyCompany,
            LiteYukonUser user) {
        
        int inventoryId = liteInventory.getInventoryID();
        InventoryIdentifier identifier = inventoryDao.getYukonInventory(inventoryId);
        
        boolean enrollable = identifier.getHardwareType().isEnrollable();
        if (enrollable) {
            // Unenroll the inventory from all its programs
            unenrollHardware(liteInventory, user, energyCompany);
        }
        
        if (deleteFromInventory) {
            
            starsInventoryBaseDao.deleteInventoryBase(inventoryId);
            hardwareEventLogService.hardwareDeleted(user, liteInventory.getDeviceLabel());
            
        } else {
            int accountId = liteInventory.getAccountID();
            long remove = liteInventory.getRemoveDate();
            Instant removeInstant;
            if (remove <= 0) {
                removeInstant = new Instant();
            } else {
                removeInstant = new Instant(remove);
            }
            liteInventory.setRemoveDate(remove);

            // add UnInstall hardware event
            addUnInstallHardwareEvent(liteInventory, energyCompany, user);

            // Delete appliances for the account/inventory id
            if (enrollable) {
                applianceDao.deleteAppliancesByAccountIdAndInventoryId(accountId, inventoryId);
            }

            // update the Inventory to remove it from the account
            starsInventoryBaseDao.removeInventoryFromAccount(inventoryId, removeInstant);
            
            // cleaup gateway assignments for zigbee devices
            HardwareClass hardwareClass = identifier.getHardwareType().getHardwareClass();
            if (hardwareClass == HardwareClass.GATEWAY) {
                gatewayDeviceDao.removeDevicesFromGateway(liteInventory.getDeviceID());
            } else if (identifier.getHardwareType().isZigbee()) {
                gatewayDeviceDao.unassignDeviceFromGateway(liteInventory.getDeviceID());
            }

            // log removal
            CustomerAccount account = customerAccountDao.getById(accountId);
            hardwareEventLogService.hardwareRemoved(user, liteInventory.getDeviceLabel(), account.getAccountNumber());
        }
    }

    // Unenrolls the inventory from all its programs
    private void unenrollHardware(LiteInventoryBase liteInv, LiteYukonUser user, LiteStarsEnergyCompany energyCompany) {
        EnrollmentHelper enrollmentHelper = new EnrollmentHelper();
        CustomerAccount customerAccount = customerAccountDao.getById(liteInv.getAccountID());
        enrollmentHelper.setAccountNumber(customerAccount.getAccountNumber());

        LMHardwareBase lmHardwareBase = hardwareBaseDao.getById(liteInv.getInventoryID());
        enrollmentHelper.setSerialNumber(lmHardwareBase.getManufacturerSerialNumber());
        
        EnrollmentHelperHolder enrollmentHelperHolder = new EnrollmentHelperHolder(enrollmentHelper, customerAccount, lmHardwareBase, energyCompany);
        enrollmentService.doEnrollment(enrollmentHelperHolder, EnrollmentEnum.UNENROLL, user);        
    }
    
    // adds the UnInstall hardware event
    private void addUnInstallHardwareEvent(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {

        int hwEventTypeID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE)
                                         .getEntryID();
        int unInstallActionID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_UNINSTALL)
                                             .getEntryID();

        LiteLMHardwareEvent lmHwEvent = new LiteLMHardwareEvent();
        lmHwEvent.setInventoryID(liteInv.getInventoryID());
        lmHwEvent.setEventTypeID(hwEventTypeID);
        lmHwEvent.setActionID(unInstallActionID);
        lmHwEvent.setEventDateTime(liteInv.getRemoveDate());
        lmHwEvent.setNotes("Removed from accountId=[" + liteInv.getAccountID() + "]");
        lmHwEvent.setAuthorizedBy(user.getUsername());

        hardwareEventDao.add(lmHwEvent, energyCompany.getEnergyCompanyId());
    }
    
    /* Depenencies */
    
    @Autowired
    public void setHardwareEventLogService(HardwareEventLogService hardwareEventLogService) {
        this.hardwareEventLogService = hardwareEventLogService;
    }
    
    @Autowired
    public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
        this.starsSearchDao = starsSearchDao;
    }

    @Autowired
    public void setStarsCustAccountInformationDao(
            StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }

    @Autowired
    public void setStarsInventoryBaseDao(
            StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }

    @Autowired
    public void setLmHardwareConfigurationDao(
            LMHardwareConfigurationDao lmHardwareConfigurationDao) {
        this.lmHardwareConfigurationDao = lmHardwareConfigurationDao;
    }

    @Autowired    
    public void setHardwareEventDao(LMHardwareEventDao hardwareEventDao) {
        this.hardwareEventDao = hardwareEventDao;
    }

    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }

    @Autowired
    public void setStarsTwoWayLcrYukonDeviceAssignmentService(StarsTwoWayLcrYukonDeviceAssignmentService starsTwoWayLcrYukonDeviceAssignmentService) {
        this.starsTwoWayLcrYukonDeviceAssignmentService = starsTwoWayLcrYukonDeviceAssignmentService;
    }
    
    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }
    
    @Autowired
    public void setEnrollmentService(EnrollmentHelperService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }

    @Autowired
    public void setHardwareBaseDao(LMHardwareBaseDao hardwareBaseDao) {
        this.hardwareBaseDao = hardwareBaseDao;
    }
    
    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
    
    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
    
}