package com.cannontech.stars.core.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.LocationService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.service.StarsInventoryBaseService;
import com.cannontech.stars.core.service.StarsTwoWayLcrYukonDeviceAssignmentService;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLMHardwareEvent;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelperHolder;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.exception.Lcr3102YukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyAssignedException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.honeywell.HoneywellBuilder;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.StarsInvalidArgumentException;
import com.cannontech.stars.ws.LmDeviceDto;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.util.Validator;

public class StarsInventoryBaseServiceImpl implements StarsInventoryBaseService {
    
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private ApplianceDao applianceDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private EnrollmentHelperService enrollmentService;
    @Autowired private GatewayDeviceDao gatewayDeviceDao;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private HoneywellBuilder honeywellBuilder;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private LMHardwareConfigurationDao lmHardwareConfigurationDao;
    @Autowired private LMHardwareEventDao hardwareEventDao;
    @Autowired private LmHardwareBaseDao hardwareBaseDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private SelectionListService selectionListService;
    @Autowired private StarsCustAccountInformationDao starsCustAccountInformationDao;
    @Autowired private StarsSearchDao starsSearchDao;
    @Autowired private StarsTwoWayLcrYukonDeviceAssignmentService starsTwoWayLcrYukonDeviceAssignmentService;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private LocationService locationService;
    @Autowired private ItronCommunicationService itronCommunicationService;
    @Autowired private AccountService accountService;
    @Autowired private DeviceDao deviceDao;

    // ADD DEVICE TO ACCOUNT
    @Override
    @Transactional
    public LiteInventoryBase addDeviceToAccount(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user, boolean allowCreateLcrIfAlreadyHasAssignedDevice) throws Lcr3102YukonDeviceCreationException {

        boolean lmHardware = InventoryUtils.isLMHardware(liteInv.getCategoryID());
        if (liteInv.getLiteID() <= 0) {
            if (lmHardware) {
                LiteLmHardwareBase lmHw = (LiteLmHardwareBase) liteInv;
                // create LMHardware here
                liteInv = inventoryBaseDao.saveLmHardware(lmHw, energyCompany.getEnergyCompanyId());
                dbChangeManager.processDbChange(lmHw.getInventoryID(), DBChangeMsg.CHANGE_INVENTORY_DB,
                    DBChangeMsg.CAT_INVENTORY_DB, DbChangeType.ADD);
            }
        }
        // existing inventory
        else {
            LiteInventoryBase liteInvPrev = inventoryBaseDao.getByInventoryId(liteInv.getLiteID());
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
                liteInv = inventoryBaseDao.saveLmHardware((LiteLmHardwareBase) liteInv,
                                                               energyCompany.getEnergyCompanyId());
                dbChangeManager.processDbChange(liteInv.getInventoryID(), DBChangeMsg.CHANGE_INVENTORY_DB,
                    DBChangeMsg.CAT_INVENTORY_DB, DbChangeType.UPDATE);
            }
        }

        // Account specific setup
        if (liteInv.getAccountID() > 0) {
            if (lmHardware) {
                LiteLmHardwareBase lmHw = (LiteLmHardwareBase) liteInv;
                // update static LMHardwareConfiguration here
                if (VersionTools.staticLoadGroupMappingExists()) {
                    initStaticLoadGroup(lmHw, energyCompany);
                }
            }
            
            InventoryIdentifier inventoryIdentifier = inventoryDao.getYukonInventory(liteInv.getInventoryID());
            if (inventoryIdentifier.getHardwareType().isItron()) {
                AccountDto account = accountService.getAccountDto(liteInv.getAccountID(), energyCompany.getEnergyCompanyId());
                String macAddress = deviceDao.getDeviceMacAddress(liteInv.getDeviceID());
                itronCommunicationService.addServicePoint(account, macAddress);
            }
            
            // add install hardware event here
            addInstallHardwareEvent(liteInv, "", energyCompany, user);

            // Add Device status event here
            if (liteInv.getCurrentStateID() > 0) {
                addDeviceStatusEvent(liteInv, energyCompany, user);
            }

        }
        
        // CREATE ADDITIONAL YUKON DEVICE FOR TWO WAY LCR
        if(lmHardware) {
            LiteLmHardwareBase lmHw = (LiteLmHardwareBase) liteInv;
            int hardwareTypeID = lmHw.getLmHardwareTypeID();
            if (InventoryUtils.is3102(hardwareTypeID)) {
                
                YukonListEntry entry = yukonListDao.getYukonListEntry(hardwareTypeID);
                String deviceTypeName = entry.getEntryText();
                int yukonDeviceTypeId = PaoType.getPaoTypeId(deviceTypeName);
                if (!PaoType.getForId(yukonDeviceTypeId).isTwoWayLcr()) {
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
    public void initStaticLoadGroup(LiteLmHardwareBase lmHw, YukonEnergyCompany energyCompany) {
        // get the static load group mapping
        LiteAccountInfo liteAcct = starsCustAccountInformationDao.getByAccountId(lmHw.getAccountID());
        LMHardwareConfiguration lmHwConfig = lmHardwareConfigurationDao.getStaticLoadGroupMapping(liteAcct,
                                                                                                 lmHw,
                                                                                                 energyCompany);
        // save the static load group mapping
        if (lmHwConfig != null) {
            lmHardwareConfigurationDao.add(lmHwConfig);
        }
    }

    @Override
    public void addInstallHardwareEvent(LiteInventoryBase liteInv, String installNotes,
                                        YukonEnergyCompany energyCompany, LiteYukonUser user) {

        int hwEventTypeID = selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE)
                                         .getEntryID();
        int installActionID = selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL)
                                           .getEntryID();

        LiteLMHardwareEvent lmHwEvent = new LiteLMHardwareEvent();
        lmHwEvent.setInventoryID(liteInv.getInventoryID());
        lmHwEvent.setEventTypeID(hwEventTypeID);
        lmHwEvent.setActionID(installActionID);
        lmHwEvent.setEventDateTime(liteInv.getInstallDate());
        lmHwEvent.setNotes(installNotes);
        lmHwEvent.setAuthorizedBy(user.getUsername());

        hardwareEventDao.add(lmHwEvent, energyCompany.getEnergyCompanyId());
    }

    // adds the Device status event
    private void addDeviceStatusEvent(LiteInventoryBase liteInv, YukonEnergyCompany lsec, LiteYukonUser user) {

        // get the entry ids needed to add device status events
        int hwEventTypeID = selectionListService.getListEntry(lsec, YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID();
        int completedActId = selectionListService.getListEntry(lsec, YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID();
        
        int statusDefID = yukonListDao.getYukonListEntry(liteInv.getCurrentStateID()).getYukonDefID();
        int devicesCurrentStatus = inventoryBaseDao.getDeviceStatus(liteInv.getInventoryID());
        
        if (statusDefID != devicesCurrentStatus) {
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
                hardwareEventDao.add(lmHwEvent, lsec.getEnergyCompanyId());                
            }
        }
    }

    
    // UPDATE DEVICE ON ACCOUNT
    @Override
    @Transactional
    public LiteInventoryBase updateDeviceOnAccount(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany lsec, 
            LiteYukonUser user, LmDeviceDto dto) {

        try {
            boolean lmHardware = InventoryUtils.isLMHardware(liteInv.getCategoryID());
            if (lmHardware) {
                LiteLmHardwareBase lmHw = (LiteLmHardwareBase) liteInv;

                // serialNumber validation here
                String newSerialNo = lmHw.getManufacturerSerialNumber();
                if (StringUtils.isBlank(newSerialNo)) {
                    throw new StarsInvalidArgumentException("Serial Number is required");
                }
                
                if (StringUtils.isBlank(liteInv.getDeviceLabel())) {
                    liteInv.setDeviceLabel(newSerialNo);
                }

                // see if serialNumber is changed, if so, see it doesn't already
                // exist on another device
                LiteLmHardwareBase lmHwPrev = (LiteLmHardwareBase) starsSearchDao.getById(lmHw.getInventoryID(), lsec);
                boolean changingSerialNo = !lmHwPrev.getManufacturerSerialNumber().equals(newSerialNo);
                if (changingSerialNo && starsSearchDao.searchLmHardwareBySerialNumber(newSerialNo, lsec) != null) {
                    throw new StarsDeviceSerialNumberAlreadyExistsException(newSerialNo, lsec.getName());
                }

                // save LMHardware here
                liteInv = inventoryBaseDao.saveLmHardware(lmHw, lsec.getEnergyCompanyId());
                dbChangeManager.processDbChange(lmHw.getInventoryID(), DBChangeMsg.CHANGE_INVENTORY_DB,
                    DBChangeMsg.CAT_INVENTORY_DB, DbChangeType.UPDATE);

                InventoryIdentifier inventoryIdentifier = inventoryDao.getYukonInventory(liteInv.getInventoryID());
                
                if (changingSerialNo && inventoryIdentifier.getHardwareType().isRf()) {
                    RfnDevice rfnDevice = rfnDeviceDao.getDeviceForId(liteInv.getDeviceID());
                    RfnIdentifier previous = rfnDevice.getRfnIdentifier();
                    RfnIdentifier rfnIdentifier = new RfnIdentifier(lmHw.getManufacturerSerialNumber(), previous.getSensorManufacturer(), previous.getSensorModel());
                    rfnDevice = new RfnDevice(rfnDevice.getName(), rfnDevice.getPaoIdentifier(), rfnIdentifier);
                    rfnDeviceDao.updateDevice(rfnDevice);
                }

                // CREATE ADDITIONAL YUKON DEVICE FOR TWO WAY LCR
                // - only if this is a Two Way LCR that does not yet have a Yukon device assigned to it
                // - updateDeviceOnAccount() does not support updating a Yukon device already assigned
                int hardwareTypeID = lmHw.getLmHardwareTypeID();
                if (InventoryUtils.is3102(hardwareTypeID)) {
                    if (liteInv.getDeviceID() < 1) {
                        
                        YukonListEntry entry = yukonListDao.getYukonListEntry(hardwareTypeID);
                        String deviceTypeName = entry.getEntryText();
                        int yukonDeviceTypeId = PaoType.getPaoTypeId(deviceTypeName);
                        if (!PaoType.getForId(yukonDeviceTypeId).isTwoWayLcr()) {
                            throw new StarsDeviceSerialNumberAlreadyExistsException("Selected yukon device must be a Two Way LCR.");
                        } 
                        
                        starsTwoWayLcrYukonDeviceAssignmentService.assignNewDeviceToLcr(liteInv, lsec, yukonDeviceTypeId, null, null, false);
                    }
                }

                if (inventoryIdentifier.getHardwareType().isHoneywell()) {
                    String macAddress = dto.getMacAddress();
                    Integer deviceVendorUserId = dto.getDeviceVendorUserId();
                    if (StringUtils.isBlank(macAddress) || !Validator.isMacAddress(macAddress)) {
                        throw new StarsInvalidArgumentException("Valid MAC Address is required");
                    } else if (deviceVendorUserId == null) {
                        throw new StarsInvalidArgumentException("Valid Device Vendor User Id is required");
                    }
                    YukonListEntry entry = yukonListDao.getYukonListEntry(hardwareTypeID);
                    String entryText = entry.getEntryText();
                    PaoType paoType = PaoType.getForDbString(entryText);
                    PaoIdentifier honeywellWifiIdentifier = new PaoIdentifier(liteInv.getDeviceID(), paoType);
                    honeywellBuilder.updateDevice(lmHw.getInventoryID(), macAddress, liteInv.getDeviceID(),
                        dto.getDeviceVendorUserId(), honeywellWifiIdentifier);
                }
            }
            // update install event
            updateInstallHardwareEvent(liteInv, lsec, user);
        } catch (ObjectInOtherEnergyCompanyException e) {
            throw new RuntimeException(e);
        }

        return liteInv;
    }

    // Update the Install hardware event
    private void updateInstallHardwareEvent(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {

        // Update the "install" event if necessary
        int installActionID = selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL)
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
    public void removeDeviceFromAccount(LiteInventoryBase lib,
            LiteStarsEnergyCompany lsec,
            LiteYukonUser user) {
        
        int inventoryId = lib.getInventoryID();
        InventoryIdentifier identifier = inventoryDao.getYukonInventory(inventoryId);
        
        boolean enrollable = identifier.getHardwareType().isEnrollable();
        if (enrollable) {
            // Unenroll the inventory from all its programs
            unenrollHardware(lib, user, lsec);
        }
        
        int accountId = lib.getAccountID();
        long remove = lib.getRemoveDate();
        Instant removeInstant;
        if (remove <= 0) {
            removeInstant = new Instant();
        } else {
            removeInstant = new Instant(remove);
        }
        lib.setRemoveDate(remove);

        // add UnInstall hardware event
        addUnInstallHardwareEvent(lib, lsec, user);

        // Delete appliances for the account/inventory id
        if (enrollable) {
            applianceDao.deleteAppliancesByAccountIdAndInventoryId(accountId, inventoryId);
        }
        
        // Remove inventory to account thermostat schedule mapping if it exists.
        accountThermostatScheduleDao.unmapThermostatsFromSchedules(Collections.singletonList(inventoryId));

        String removeLbl = lib.getManufacturerSerialNumber();
        // update the Inventory to remove it from the account
        inventoryBaseDao.removeInventoryFromAccount(inventoryId, removeInstant, removeLbl);
        dbChangeManager.processDbChange(lib.getInventoryID(), DBChangeMsg.CHANGE_INVENTORY_DB,
            DBChangeMsg.CAT_INVENTORY_DB, DbChangeType.UPDATE);

        // cleaup gateway assignments for zigbee devices
        HardwareClass hardwareClass = identifier.getHardwareType().getHardwareClass();
        if (hardwareClass == HardwareClass.GATEWAY) {
            gatewayDeviceDao.removeDevicesFromGateway(lib.getDeviceID());
        } else if (identifier.getHardwareType().isZigbee()) {
            gatewayDeviceDao.unassignDeviceFromGateway(lib.getDeviceID());
        }
        
        locationService.deleteLocation(lib.getDeviceID(), user);
        // log removal
        CustomerAccount account = customerAccountDao.getById(accountId);
        hardwareEventLogService.hardwareRemoved(user, lib.getDeviceLabel(), account.getAccountNumber());
    }

    // Unenrolls the inventory from all its programs
    private void unenrollHardware(LiteInventoryBase liteInv, LiteYukonUser user, LiteStarsEnergyCompany energyCompany) {
        EnrollmentHelper enrollmentHelper = new EnrollmentHelper();
        CustomerAccount customerAccount = customerAccountDao.getById(liteInv.getAccountID());
        enrollmentHelper.setAccountNumber(customerAccount.getAccountNumber());

        LMHardwareBase lmHardwareBase = hardwareBaseDao.getById(liteInv.getInventoryID());
        enrollmentHelper.setSerialNumber(lmHardwareBase.getManufacturerSerialNumber());
        
        EnrollmentHelperHolder enrollmentHelperHolder = new EnrollmentHelperHolder(enrollmentHelper, customerAccount, lmHardwareBase);
        enrollmentService.doEnrollment(enrollmentHelperHolder, EnrollmentEnum.UNENROLL, user);        
    }
    
    // adds the UnInstall hardware event
    private void addUnInstallHardwareEvent(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {

        int hwEventTypeID = selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE)
                                         .getEntryID();
        int unInstallActionID = selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_UNINSTALL)
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
    
}