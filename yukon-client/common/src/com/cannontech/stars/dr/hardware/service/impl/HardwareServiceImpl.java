package com.cannontech.stars.dr.hardware.service.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.service.LocationService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLMHardwareEvent;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionServiceImpl;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.AddByRange;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.dr.hardware.service.NotSupportedException;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class HardwareServiceImpl implements HardwareService {

    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private ApplianceDao applianceDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EnrollmentHelperService enrollmentHelperService;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private HardwareTypeExtensionServiceImpl hardwareTypeExtensionService;
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private LMHardwareEventDao lmHardwareEventDao;
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private OptOutService optOutService;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private SelectionListService selectionListService;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private LocationService locationService;

    @Override
    @Transactional
    public void deleteHardware(LiteYukonUser user, boolean delete, int inventoryId) 
    throws NotFoundException, CommandCompletionException, SQLException, PersistenceException {
        
        boolean deletePao = false;
        String accountNumber = null;
        
        LiteInventoryBase lib = inventoryBaseDao.getByInventoryId(inventoryId);
        int accountId = lib.getAccountID();
        
        YukonEnergyCompany ec = ecDao.getEnergyCompanyByInventoryId(inventoryId);
        
        if (accountId > 0) {
            CustomerAccount ca = customerAccountDao.getById(accountId);
            accountNumber = ca.getAccountNumber();
            
            /* Cancel Opt Outs */
            cancelOptOuts(user, lib);
            
            /* Unenroll the hardware */
            LMHardwareBase lmHardwareBase = null;
            try {
                lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
                EnrollmentHelper enrollmentHelper = new EnrollmentHelper();
                enrollmentHelper.setAccountNumber(ca.getAccountNumber());
                enrollmentHelper.setSerialNumber(lmHardwareBase.getManufacturerSerialNumber());

                enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.UNENROLL, user, ca);
            } catch (NotFoundException ignore) {
                /* Ignore this if we are not an LMHardwareBase such as mct's */
                deletePao = true;
            }
        }
        
        if (delete) {
            InventoryIdentifier id = inventoryDao.getYukonInventory(inventoryId);
            YukonPao pao = paoDao.getYukonPao(lib.getDeviceID());
            if (pao.getPaoIdentifier().getPaoType().isRfn()) {
                deletePao = true;
            }
            
            // Warn the ExtensionService we are about to delete.
            hardwareTypeExtensionService.preDeleteCleanup(pao, id);
            
            /* Delete this hardware from the database */
            /*TODO handle this with new code, not with this util. */
            InventoryManagerUtil.deleteInventory( lib, ec, deletePao);

            // Give the Extension service a chance to clean up its tables
            hardwareTypeExtensionService.deleteDevice(pao, id);
            
            // Log hardware deletion
            hardwareEventLogService.hardwareDeleted(user, lib.getDeviceLabel());
        } else {
            removeFromAccount(user, lib, accountNumber);
            dbChangeManager.processDbChange(lib.getInventoryID(), DBChangeMsg.CHANGE_INVENTORY_DB,
                DBChangeMsg.CAT_INVENTORY_DB, DbChangeType.UPDATE);
            locationService.deleteLocation(lib.getDeviceID(), user);
        }
    }
    
    /**
     * Remove it from the account and put it back in general inventory
     * @param user
     * @param lib
     */
    private void removeFromAccount(LiteYukonUser user, LiteInventoryBase lib, String accountNumber) {
        Date removeDate = new Date();
        int accountId = lib.getAccountID();
        int inventoryId = lib.getInventoryID();
        LiteStarsEnergyCompany ec = starsDatabaseCache.getEnergyCompany(lib.getEnergyCompanyId());
        int hwEventEntryId = selectionListService.getListEntry(ec, YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
        int uninstallActionId = selectionListService.getListEntry(ec, YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_UNINSTALL ).getEntryID();
        
        /* Add an uninstall event for this hardware */
        LiteLMHardwareEvent liteLMHardwareEvent = new LiteLMHardwareEvent();
        liteLMHardwareEvent.setInventoryID(inventoryId);
        liteLMHardwareEvent.setActionID(uninstallActionId);
        liteLMHardwareEvent.setEventTypeID(hwEventEntryId);
        liteLMHardwareEvent.setEventDateTime(removeDate.getTime());
        
        liteLMHardwareEvent.setNotes("Removed from account #" + accountNumber);
        
        lmHardwareEventDao.add(liteLMHardwareEvent, ec.getEnergyCompanyId());
        
        if (lib instanceof LiteLmHardwareBase) {
            applianceDao.deleteAppliancesByAccountIdAndInventoryId(accountId, inventoryId);
        }
        // Remove association between inventory and account thermostat schedules.
        accountThermostatScheduleDao.unmapThermostatsFromSchedules(Collections.singletonList(inventoryId));
        
        /* Removes any entries found in the InventoryBase */
        LiteInventoryBase inventoryBase = inventoryBaseDao.getByInventoryId(inventoryId);
        inventoryBase.setAccountID(CtiUtilities.NONE_ZERO_ID);
        inventoryBase.setRemoveDate(removeDate.getTime());
        inventoryBase.setDeviceLabel(inventoryBase.getManufacturerSerialNumber());
        inventoryBaseDao.saveInventoryBase(inventoryBase, ec.getEnergyCompanyId());
        
        InventoryIdentifier id = inventoryDao.getYukonInventory(inventoryId);
        YukonPao pao = paoDao.getYukonPao(lib.getDeviceID());
        hardwareTypeExtensionService.moveDeviceToInventory(pao, id);

        // Log hardware deletion
        hardwareEventLogService.hardwareRemoved(user, lib.getDeviceLabel(), accountNumber);
    }
    
    /**
     * Cancel all optouts
     * @throws CommandCompletionException 
     */
    private void cancelOptOuts(LiteYukonUser user, LiteInventoryBase lib) throws CommandCompletionException {
        List<OptOutEventDto> optOutEvents = optOutEventDao.getCurrentOptOuts(lib.getAccountID(), lib.getInventoryID());
        List<Integer> optOutEventIdList = Lists.newArrayListWithCapacity(optOutEvents.size());
        for(OptOutEventDto event : optOutEvents){
            optOutEventIdList.add(event.getEventId());
        }
        optOutService.cancelOptOut(optOutEventIdList, user);
    }

    @Override
    public void changeType(YukonUserContext context, InventoryIdentifier inv, YukonListEntry typeEntry) throws ObjectInOtherEnergyCompanyException {
        HardwareType type = HardwareType.valueOf(typeEntry.getDefinition().getDefinitionId());
        if (!type.isSupportsChangeType()) {
            throw new NotSupportedException(inv);
        } else {
            HardwareType original = inv.getHardwareType();
            if (type != original) {
                Hardware hardware = hardwareUiService.getHardware(inv.getInventoryId());
                hardware.setHardwareType(type);
                hardware.setHardwareTypeEntryId(typeEntry.getEntryID());
                hardwareUiService.updateHardware(context.getYukonUser(), hardware);
            }
        }
    }

    @Override
    public void changeDeviceStatus(YukonUserContext context, InventoryIdentifier inv, int statusEntryId, int userId) throws ObjectInOtherEnergyCompanyException {
        Hardware hardware = hardwareUiService.getHardware(inv.getInventoryId());
        if (hardware.getDeviceStatusEntryId() != statusEntryId) {
            hardware.setDeviceStatusEntryId(statusEntryId);
            boolean changed = hardwareUiService.updateHardware(context.getYukonUser(), hardware);
            if (changed) {
                EventUtils.logSTARSEvent(userId, EventUtils.EVENT_CATEGORY_INVENTORY, hardware.getDeviceStatusEntryId(), inv.getInventoryId());
            }
        }
    }

    @Override
    public void changeServiceCompany(YukonUserContext context, InventoryIdentifier inv, int serviceCompanyId) throws ObjectInOtherEnergyCompanyException {
        Hardware hardware = hardwareUiService.getHardware(inv.getInventoryId());
        if (hardware.getServiceCompanyId() != serviceCompanyId) {
            hardware.setServiceCompanyId(serviceCompanyId);
            hardwareUiService.updateHardware(context.getYukonUser(), hardware);
        }
    }

    @Override
    public void changeWarehouse(YukonUserContext context, InventoryIdentifier inv, int warehouseId) throws ObjectInOtherEnergyCompanyException {
        Hardware hardware = hardwareUiService.getHardware(inv.getInventoryId());
        if (hardware.getWarehouseId() != warehouseId) {
            hardware.setWarehouseId(warehouseId);
            hardwareUiService.updateHardware(context.getYukonUser(), hardware);
        }
    }

    @Override
    public InventoryIdentifier createForAddByRangeTask(AddByRange abr, long sn, LiteYukonUser user) throws ObjectInOtherEnergyCompanyException {
        YukonEnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        Hardware hardware = new Hardware();
        hardware.setSerialNumber(String.valueOf(sn));
        
        hardware.setHardwareTypeEntryId(abr.getHardwareTypeId());
        YukonListEntry entry = yukonListDao.getYukonListEntry(abr.getHardwareTypeId());
        HardwareType type = HardwareType.valueOf(entry.getYukonDefID());
        hardware.setHardwareType(type);
        
        hardware.setFieldReceiveDate(new Date());
        hardware.setDeviceStatusEntryId(abr.getStatusTypeId());
        hardware.setServiceCompanyId(abr.getServiceCompanyId());
        hardware.setVoltageEntryId(abr.getVoltageTypeId());
        hardware.setEnergyCompanyId(ec.getEnergyCompanyId());
        hardware.setRouteId(abr.getRouteId());
        
        if (type == HardwareType.LCR_3102) {
            hardware.setCreatingNewTwoWayDevice(true);
            hardware.setTwoWayDeviceName("LCR2W " + sn);
        }
        
        int id = hardwareUiService.createHardware(hardware, user);
        
        return new InventoryIdentifier(id, type);
    }
    
}