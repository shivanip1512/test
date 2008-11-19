package com.cannontech.stars.core.service.impl;

import static com.cannontech.stars.util.StarsClientRequestException.DEVICE_ASSIGNED_TO_ANOTHER_ACCOUNT;
import static com.cannontech.stars.util.StarsClientRequestException.INVALID_ARGUMENT;
import static com.cannontech.stars.util.StarsClientRequestException.SERIAL_NUMBER_ALREADY_EXISTS;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.service.StarsInventoryBaseService;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;
import com.cannontech.stars.dr.util.YukonListEntryHelper;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.StarsClientRequestException;

public class StarsInventoryBaseServiceImpl implements StarsInventoryBaseService {

    private static final Logger log = YukonLogManager.getLogger(StarsInventoryBaseServiceImpl.class);

    private SimpleJdbcTemplate jdbcTemplate;
    private StarsSearchDao starsSearchDao;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private ThermostatScheduleDao thermostatScheduleDao;
    private LMHardwareConfigurationDao lmHardwareConfigurationDao;
    private LMHardwareEventDao hardwareEventDao;

    // Injected via Spring-IOC
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Injected via Spring-IOC
    public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
        this.starsSearchDao = starsSearchDao;
    }

    // Injected via Spring-IOC
    public void setStarsCustAccountInformationDao(
            StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }

    // Injected via Spring-IOC
    public void setStarsInventoryBaseDao(
            StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }

    // Injected via Spring-IOC
    public void setThermostatScheduleDao(
            ThermostatScheduleDao thermostatScheduleDao) {
        this.thermostatScheduleDao = thermostatScheduleDao;
    }

    // Injected via Spring-IOC
    public void setLmHardwareConfigurationDao(
            LMHardwareConfigurationDao lmHardwareConfigurationDao) {
        this.lmHardwareConfigurationDao = lmHardwareConfigurationDao;
    }

    public void setHardwareEventDao(LMHardwareEventDao hardwareEventDao) {
        this.hardwareEventDao = hardwareEventDao;
    }

    /**
     * Adds a hardware device to the customer account. If the hardware doesn't
     * exist, then adds it to the inventory and account. If it is in the
     * warehouse, just adds it to the account. Errors out, if it is currently
     * assigned to another account. If replaceAccount is desired, caller needs
     * to remove it from the previous account and add it to the new account.
     * Handles only LMHardware devices for now, will need to support other
     * device types later.
     * @param liteInv
     * @param energyCompany
     * @param user
     * @return LiteInventoryBase
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public LiteInventoryBase addDeviceToAccount(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {

        try {
            boolean lmHardware = InventoryUtils.isLMHardware(liteInv.getCategoryID());
            if (liteInv.getLiteID() <= 0) {
                if (lmHardware) {
                    LiteStarsLMHardware lmHw = (LiteStarsLMHardware) liteInv;
                    // create LMHardware here
                    liteInv = starsInventoryBaseDao.saveLmHardware(lmHw,
                                                                   energyCompany.getEnergyCompanyID());

                    // if Thermostat, initialize the default schedule
                    if (lmHw.isThermostat()) {
                        initThermostatSchedule(lmHw, energyCompany);
                    }
                }
            }
            // existing inventory
            else {
                LiteInventoryBase liteInvPrev = starsInventoryBaseDao.getById(liteInv.getLiteID());
                // Error, if the device is already assigned to another account
                // If replaceAccount is desired, caller needs to remove from the
                // previous account, then add to the new account
                if (liteInvPrev.getAccountID() > 0) {
                    String msg = "Hardware already assigned to another account, inventoryId=[" + liteInv.getLiteID() + "]";
                    msg += ", energyCompany=[" + energyCompany.getName() + "], requested accountId=[" + liteInv.getAccountID() + "]";
                    log.error(msg);
                    throw new StarsClientRequestException(DEVICE_ASSIGNED_TO_ANOTHER_ACCOUNT,
                                                          msg);
                }
                if (lmHardware) {
                    // update LMHardware here
                    liteInv = starsInventoryBaseDao.saveLmHardware((LiteStarsLMHardware) liteInv,
                                                                   energyCompany.getEnergyCompanyID());
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
                addInstallHardwareEvent(liteInv, energyCompany);

                // Add Device status event here
                if (liteInv.getCurrentStateID() > 0) {
                    addDeviceStatusEvent(liteInv, energyCompany);
                }

            }
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return liteInv;
    }

    // initializes the default Thermostat Schedule on the account
    private void initThermostatSchedule(LiteStarsLMHardware lmHw,
            LiteStarsEnergyCompany energyCompany) {

        // get the default Thermostat Schedule for the energyCompany
        int thermostatDefId = YukonListEntryHelper.getYukonDefinitionId(energyCompany,
                                                                        YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE,
                                                                        lmHw.getLmHardwareTypeID());
        HardwareType hwType = HardwareType.valueOf(thermostatDefId);
        ThermostatSchedule schedule = thermostatScheduleDao.getEnergyCompanyDefaultSchedule(lmHw.getAccountID(),
                                                                                            hwType);

        // save the schedule, if a default schedule is defined for the
        // energyCompany
        if (schedule != null) {
            // schedule - set accountId, inventoryId; name defaults to none
            schedule.setAccountId(lmHw.getAccountID());
            schedule.setInventoryId(lmHw.getInventoryID());

            // save the default thermostat schedule on the account
            thermostatScheduleDao.save(schedule);
        }
    }

    // maps the static load group, if static load group mapping exists
    private void initStaticLoadGroup(LiteStarsLMHardware lmHw,
            LiteStarsEnergyCompany energyCompany) {
        // get the static load group mapping
        LiteStarsCustAccountInformation liteAcct = starsCustAccountInformationDao.getById(lmHw.getAccountID(),
                                                                                          energyCompany.getEnergyCompanyID());
        LMHardwareConfiguration lmHwConfig = liteAcct.getStaticLoadGroupMapping(lmHw,
                                                                                energyCompany);

        // save the static load group mapping
        if (lmHwConfig != null) {
            lmHardwareConfigurationDao.add(lmHwConfig);
        }
    }

    // adds the Install hardware event
    private void addInstallHardwareEvent(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany) {

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

        hardwareEventDao.add(lmHwEvent, energyCompany.getEnergyCompanyID());
    }

    // adds the Device status event
    private void addDeviceStatusEvent(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany) {

        // get the entry ids needed to add device status events
        int hwEventTypeID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE)
                                         .getEntryID();
        int completedActId = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED)
                                          .getEntryID();
        int termActID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION)
                                     .getEntryID();
        int tempTermActID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION)
                                         .getEntryID();
        // update device status
        liteInv.updateDeviceStatus();
        int statusDefID = DaoFactory.getYukonListDao()
                                    .getYukonListEntry(liteInv.getCurrentStateID())
                                    .getYukonDefID();
        if (statusDefID != liteInv.getDeviceStatus()) {
            // add the device status event here
            LiteLMHardwareEvent lmHwEvent = null;
            if (statusDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL) {
                // If device status is available, add "Completed" event
                // event
                lmHwEvent = new LiteLMHardwareEvent();
                lmHwEvent.setActionID(completedActId);

            } else if (statusDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL) {
                // If device status is unavailable, add "Termination" event
                lmHwEvent = new LiteLMHardwareEvent();
                lmHwEvent.setActionID(termActID);
            } else if (statusDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL) {
                // If device status is temporary unavailable, add
                // "Temp Termination" event
                lmHwEvent = new LiteLMHardwareEvent();
                lmHwEvent.setActionID(tempTermActID);
            }
            if (lmHwEvent != null) {
                lmHwEvent.setInventoryID(liteInv.getInventoryID());
                lmHwEvent.setEventTypeID(hwEventTypeID);
                lmHwEvent.setEventDateTime(new Date().getTime());
                lmHwEvent.setNotes("Event added to match the device status");
                hardwareEventDao.add(lmHwEvent,
                                     energyCompany.getEnergyCompanyID());
            }
        }
    }

    /**
     * Updates a hardware device info on the customer account. Ex., Field
     * install date, Service Company etc. Handles only LMHardware devices for
     * now, will need to support other device types later.
     * @param liteInv
     * @param energyCompany
     * @param user
     * @return LiteInventoryBase
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public LiteInventoryBase updateDeviceOnAccount(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user) {

        try {
            boolean lmHardware = InventoryUtils.isLMHardware(liteInv.getCategoryID());
            if (lmHardware) {
                LiteStarsLMHardware lmHw = (LiteStarsLMHardware) liteInv;

                // serialNumber validation here
                String newSerialNo = lmHw.getManufacturerSerialNumber();
                if (StringUtils.isBlank(newSerialNo)) {
                    String msg = "Serial number is required";
                    log.error(msg);
                    throw new StarsClientRequestException(INVALID_ARGUMENT, msg);
                }

                // see if serialNumber is changed, if so, see it doesn't already
                // exist on another device
                LiteStarsLMHardware lmHwPrev = (LiteStarsLMHardware) starsSearchDao.getDevice(lmHw.getInventoryID(),
                                                                                              energyCompany);
                if (!lmHwPrev.getManufacturerSerialNumber().equals(newSerialNo) && starsSearchDao.searchLMHardwareBySerialNumber(newSerialNo,
                                                                                                                                 energyCompany) != null) {
                    String msg = "Serial number already exists on another Device, serialNumber=[" + newSerialNo + "]";
                    msg += ", energyCompany=[" + energyCompany.getName() + "], updated inventoryId=[" + liteInv.getInventoryID() + "]";
                    log.error(msg);
                    throw new StarsClientRequestException(SERIAL_NUMBER_ALREADY_EXISTS,
                                                          msg);
                }

                // save LMHardware here
                liteInv = starsInventoryBaseDao.saveLmHardware(lmHw,
                                                               energyCompany.getEnergyCompanyID());
            }
            // update install event
            updateInstallHardwareEvent(liteInv, energyCompany);

        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return liteInv;
    }

    // Update the Install hardware event
    private void updateInstallHardwareEvent(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany) {

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
                hardwareEventDao.update(hwEvent);
            }
        }
    }

    /**
     * Removes a hardware device from the customer account. If
     * deleteFromInventory is true, deletes the device from the Inventory also.
     * Handles only LMHardware devices for now, will need to support other
     * device types later.
     * @param liteInv
     * @param deleteFromInventory
     * @param energyCompany
     * @param user
     * @return
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeDeviceFromAccount(LiteInventoryBase liteInv,
            boolean deleteFromInventory, LiteStarsEnergyCompany energyCompany,
            LiteYukonUser user) {
        try {
            if (deleteFromInventory) {
                starsInventoryBaseDao.deleteInventoryBase(liteInv.getInventoryID());
            } else {
                long removeDate = liteInv.getRemoveDate();
                if (removeDate <= 0) {
                    removeDate = new Date().getTime();
                }
                liteInv.setRemoveDate(removeDate);

                // add UnInstall hardware event
                addUnInstallHardwareEvent(liteInv, energyCompany);

                // UnEnrolls the inventory from all its programs
                LMHardwareControlGroupDao lmHardwareControlGroupDao = YukonSpringHook.getBean("lmHardwareControlGroupDao",
                                                                                              LMHardwareControlGroupDao.class);
                lmHardwareControlGroupDao.unenrollHardware(liteInv.getInventoryID());

                // Delete appliances for the account/inventory id
                boolean lmHardware = InventoryUtils.isLMHardware(liteInv.getCategoryID());
                if (lmHardware) {
                    ApplianceDao applianceDao = YukonSpringHook.getBean("applianceDao",
                                                                        ApplianceDao.class);
                    applianceDao.deleteAppliancesByAccountIdAndInventoryId(liteInv.getAccountID(),
                                                                           liteInv.getInventoryID());
                }

                // update the Inventory to remove it from the account
                LiteInventoryBase liteInvDB = starsInventoryBaseDao.getById(liteInv.getInventoryID());                
                liteInvDB.setRemoveDate(removeDate);
                starsInventoryBaseDao.removeInventoryFromAccount(liteInvDB);
            }

        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // adds the UnInstall hardware event
    private void addUnInstallHardwareEvent(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany) {

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

        hardwareEventDao.add(lmHwEvent, energyCompany.getEnergyCompanyID());
    }

}
