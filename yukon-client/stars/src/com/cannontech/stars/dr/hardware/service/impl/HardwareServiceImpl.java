package com.cannontech.stars.dr.hardware.service.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionServiceImpl;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.AddByRange;
import com.cannontech.stars.dr.hardware.model.Hardware;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.dr.hardware.service.NotSupportedException;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class HardwareServiceImpl implements HardwareService {

    @Autowired private ApplianceDao applianceDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private EnrollmentHelperService enrollmentHelperService;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private LMHardwareBaseDao lmHardwareBaseDao;
    @Autowired private LMHardwareEventDao lmHardwareEventDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private StarsInventoryBaseDao starsInventoryBaseDao;
    @Autowired private OptOutService optOutService;
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private HardwareTypeExtensionServiceImpl hardwareTypeExtensionService;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private PaoDao paoDao;
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private YukonListDao yukonListDao;
    
    @Override
    @Transactional
    public void deleteHardware(YukonUserContext context, boolean delete, int inventoryId) 
    throws NotFoundException, CommandCompletionException, SQLException, PersistenceException, WebClientException {
        
        LiteYukonUser user = context.getYukonUser();
        boolean deleteMCT = false;
        String accountNumber = null;
        
        LiteInventoryBase lib = starsInventoryBaseDao.getByInventoryId(inventoryId);
        int accountId = lib.getAccountID();
        
        YukonEnergyCompany ec = yukonEnergyCompanyService.getEnergyCompanyByInventoryId(inventoryId);
        
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
                
                enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.UNENROLL, user);
                
            } catch (NotFoundException ignore) {
                /* Ignore this if we are not an LMHardwareBase such as mct's */
                deleteMCT = true;
            }
        }
        
        if (delete) {
            InventoryIdentifier id = inventoryDao.getYukonInventory(inventoryId);
            YukonPao pao = paoDao.getYukonPao(lib.getDeviceID());
            
            // Warn the ExtensionService we are about to delete.
            hardwareTypeExtensionService.preDeleteCleanup(pao, id);
            
            /* Delete this hardware from the database */
            /*TODO handle this with new code, not with this util. */
            InventoryManagerUtil.deleteInventory( lib, ec, deleteMCT);

            // Give the Extension service a chance to clean up its tables
            hardwareTypeExtensionService.deleteDevice(pao, id);
            
            // Log hardware deletion
            hardwareEventLogService.hardwareDeleted(user, lib.getDeviceLabel());
        } else {
            removeFromAccount(user, lib, accountNumber);
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
        int hwEventEntryId = ec.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
        int uninstallActionId = ec.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_UNINSTALL ).getEntryID();
        
        /* Add an uninstall event for this hardware */
        LiteLMHardwareEvent liteLMHardwareEvent = new LiteLMHardwareEvent();
        liteLMHardwareEvent.setInventoryID(inventoryId);
        liteLMHardwareEvent.setActionID(uninstallActionId);
        liteLMHardwareEvent.setEventTypeID(hwEventEntryId);
        liteLMHardwareEvent.setEventDateTime(removeDate.getTime());
        
        liteLMHardwareEvent.setNotes("Removed from account #" + accountNumber);
        
        lmHardwareEventDao.add(liteLMHardwareEvent, ec.getEnergyCompanyId());
        
        if (lib instanceof LiteStarsLMHardware) {
            applianceDao.deleteAppliancesByAccountIdAndInventoryId(accountId, inventoryId);
        }
        
        /* Removes any entries found in the InventoryBase */
        InventoryBase inventoryBase =  inventoryBaseDao.getById(inventoryId);
        inventoryBase.setAccountId(CtiUtilities.NONE_ZERO_ID);
        inventoryBase.setRemoveDate(new Timestamp(removeDate.getTime()));
        inventoryBase.setDeviceLabel("");
        inventoryBaseDao.update(inventoryBase);
        
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
    public void changeType(YukonUserContext context, InventoryIdentifier inv, HardwareType type) throws ObjectInOtherEnergyCompanyException {
        if (!type.isSupportsChangeType()) {
            throw new NotSupportedException(inv);
        } else {
            HardwareType original = inv.getHardwareType();
            if (type != original) {
                Hardware hardware = hardwareUiService.getHardware(inv.getInventoryId());
                hardware.setHardwareType(type);
                hardwareUiService.updateHardware(context.getYukonUser(), hardware);
            }
        }
    }

    @Override
    public void changeDeviceStatus(YukonUserContext context, InventoryIdentifier inv, int statusEntryId, HttpSession session) throws ObjectInOtherEnergyCompanyException {
        Hardware hardware = hardwareUiService.getHardware(inv.getInventoryId());
        if (hardware.getDeviceStatusEntryId() != statusEntryId) {
            hardware.setDeviceStatusEntryId(statusEntryId);
            boolean changed = hardwareUiService.updateHardware(context.getYukonUser(), hardware);
            if (changed) {
                EventUtils.logSTARSEvent(context.getYukonUser().getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, hardware.getDeviceStatusEntryId(), inv.getInventoryId(), session);
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
        YukonEnergyCompany ec = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
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
        
        int id = hardwareUiService.createHardware(hardware, null, user);
        
        return new InventoryIdentifier(id, type);
    }
    
}