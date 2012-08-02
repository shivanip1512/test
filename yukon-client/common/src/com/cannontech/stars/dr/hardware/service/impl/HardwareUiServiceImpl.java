package com.cannontech.stars.dr.hardware.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareHistory;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryCategory;
import com.cannontech.common.inventory.SwitchAssignment;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.stars.core.service.StarsInventoryBaseService;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLMHardwareEvent;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteMeterHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.event.dao.LMCustomerEventBaseDao;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.builder.HardwareTypeExtensionService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.exception.Lcr3102YukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.model.CustomerAction;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class HardwareUiServiceImpl implements HardwareUiService {

    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private LMHardwareEventDao lmHardwareEventDao;
    @Autowired private PaoDao paoDao;
    @Autowired private LMCustomerEventBaseDao lmCustomerEventBaseDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private DeviceCreationService deviceCreationService;
    @Autowired private StarsSearchDao starsSearchDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private WarehouseDao warehouseDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private StarsInventoryBaseService starsInventoryBaseService;
    @Autowired private HardwareTypeExtensionService hardwareTypeExtensionService;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private ECMappingDao ecMappingDao;
    
    @Override
    public Hardware getHardware(int inventoryId) {
        int ecId = ecMappingDao.getEnergyCompanyIdForInventoryId(inventoryId);
        
        Hardware hardware = new Hardware();
        hardware.setInventoryId(inventoryId);
        hardware.setEnergyCompanyId(ecId);
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        
        /* Set Hardware Basics */
        LiteInventoryBase liteInventoryBase = inventoryBaseDao.getByInventoryId(inventoryId);
        hardware.setAccountId(liteInventoryBase.getAccountID());
        
        int categoryId = liteInventoryBase.getCategoryID();
        YukonListEntry categoryNameYLE = yukonListDao.getYukonListEntry(categoryId);
        hardware.setCategoryName(categoryNameYLE.getEntryText());
        
        int deviceId = liteInventoryBase.getDeviceID();
        hardware.setDeviceId(deviceId);
        hardware.setDisplayLabel(liteInventoryBase.getDeviceLabel());
        hardware.setAltTrackingNumber(liteInventoryBase.getAlternateTrackingNumber());
        hardware.setVoltageEntryId(liteInventoryBase.getVoltageID());
        
        /* For some reason some (not real) dates were saved with a time of 19:00 instead of 18:00. */
        Instant beginningOfJavaTime = new Instant(0).plus(Duration.standardDays(1));
        Instant installDT = new Instant(liteInventoryBase.getInstallDate());
        if (installDT.isAfter(beginningOfJavaTime)){
            hardware.setFieldInstallDate(installDT.toDate());
        } else {
            hardware.setFieldInstallDate(null);
        }
        
        Instant receiveDT = new Instant(liteInventoryBase.getReceiveDate());
        if (receiveDT.isAfter(beginningOfJavaTime)){
            hardware.setFieldReceiveDate(receiveDT.toDate());
        } else {
            hardware.setFieldReceiveDate(null);
        }
        
        Instant removeDT = new Instant(liteInventoryBase.getRemoveDate());
        if (removeDT.isAfter(beginningOfJavaTime)){
            hardware.setFieldRemoveDate(removeDT.toDate());
        } else {
            hardware.setFieldRemoveDate(null);
        }
        
        hardware.setDeviceNotes(liteInventoryBase.getNotes());
        hardware.setDeviceStatusEntryId(liteInventoryBase.getCurrentStateID());
        hardware.setOriginalDeviceStatusEntryId(liteInventoryBase.getCurrentStateID());
        hardware.setServiceCompanyId(liteInventoryBase.getInstallationCompanyID());
        
        Warehouse warehouse = warehouseDao.findWarehouseForInventoryId(inventoryId);
        hardware.setWarehouseId(warehouse == null ? 0 : warehouse.getWarehouseID());
        
        List<LiteLMHardwareEvent> events = lmHardwareEventDao.getByInventoryId(liteInventoryBase.getInventoryID());
        String installNotes = null;
        for (LiteLMHardwareEvent event : events) {
            YukonListEntry entry = yukonListDao.getYukonListEntry(event.getActionID());
            CustomerAction action = CustomerAction.valueOf(entry.getYukonDefID());
            /* The list of events is retrieved newest to oldest 
             * so the first install in the list will be the most recent */
            if (action == CustomerAction.INSTALL) {
                installNotes = SqlUtils.convertDbValueToString(event.getNotes());
                break;
            }
        }
        hardware.setInstallNotes(installNotes);
        
        /* Set Hardware Details based on type(Switch, Thermostat, or MCT */
        YukonListEntry categoryEntry = yukonListDao.getYukonListEntry(categoryId);
        InventoryCategory hardwareCategory = InventoryCategory.valueOf(categoryEntry.getYukonDefID());
        
        if (hardwareCategory == InventoryCategory.MCT) {
            /* Hardware is an MCT */
            hardware.setHardwareType(HardwareType.YUKON_METER);
            
            if (deviceId > 0) {
                /* The device id has been set to a real MCT. */
                YukonPao pao = paoDao.getYukonPao(deviceId);
                DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(pao);
                String paoType = displayablePao.getPaoIdentifier().getPaoType().getPaoTypeName();
                hardware.setDisplayType(paoType);
                String paoName = displayablePao.getName();
                hardware.setDisplayName(paoType + " " + paoName);
                hardware.setMeterNumber(inventoryDao.getMeterNumberForDevice(deviceId));
            } else {
                /* Not attached to a real MCT yet. Use label for name if you can */
                /* and use the MCT list enty of the energy company as the device type. */
                YukonListEntry mctDeviceType = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_NON_YUKON_METER);
                hardware.setDisplayType(mctDeviceType.getEntryText());
                if (StringUtils.isNotBlank(liteInventoryBase.getDeviceLabel())) {
                    hardware.setDisplayName(liteInventoryBase.getDeviceLabel());
                }
            }
        } else if (hardwareCategory == InventoryCategory.NON_YUKON_METER) {
            LiteMeterHardwareBase liteMeterHardwareBase = (LiteMeterHardwareBase) liteInventoryBase;
            /* Hardware is an meter but being handle by stars instead of being a yukon mct (pao) */
            String meterNumber = liteMeterHardwareBase.getMeterNumber();
            hardware.setMeterNumber(meterNumber);
            hardware.setHardwareTypeEntryId(liteMeterHardwareBase.getMeterTypeID());
            
            YukonListEntry hardwareTypeEntry = yukonListDao.getYukonListEntry(liteMeterHardwareBase.getMeterTypeID());
            HardwareType hardwareType = HardwareType.valueOf(hardwareTypeEntry.getYukonDefID());
            hardware.setHardwareType(hardwareType);
            
            YukonListEntry mctDeviceType = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_NON_YUKON_METER);
            String deviceType = mctDeviceType.getEntryText();
            hardware.setDisplayName(deviceType + " " + meterNumber);
            hardware.setDisplayType(deviceType);
            
            List<Integer> assignedIds = inventoryBaseDao.getSwitchAssignmentsForMeter(inventoryId);
            
            int accountId = inventoryDao.getAccountIdForInventory(inventoryId);
            if (accountId > 0) {
                for (SwitchAssignment assignement : getSwitchAssignments(assignedIds, accountId)) {
                    hardware.getSwitchAssignments().add(assignement);
                }
            }
            
        } else {
            /* Must be a switch or thermostat. */
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(liteInventoryBase.getInventoryID());
            
            hardware.setHardwareTypeEntryId(lmHardwareBase.getLMHarewareTypeId());
            
            YukonListEntry hardwareTypeEntry = yukonListDao.getYukonListEntry(lmHardwareBase.getLMHarewareTypeId());
            HardwareType hardwareType = HardwareType.valueOf(hardwareTypeEntry.getYukonDefID());
            hardware.setHardwareType(hardwareType);
            
            YukonListEntry deviceTypeEntry = yukonListDao.getYukonListEntry(lmHardwareBase.getLMHarewareTypeId());
            String deviceType = deviceTypeEntry.getEntryText();
            hardware.setDisplayType(deviceType);
            String serialNumber = lmHardwareBase.getManufacturerSerialNumber();
            hardware.setSerialNumber(serialNumber);
            hardware.setDisplayName(deviceType + " " + serialNumber);
            hardware.setRouteId(lmHardwareBase.getRouteId());
            
            /* Two Way LCR's */
            if (hardwareType.isSwitch() && hardwareCategory == InventoryCategory.TWO_WAY_RECEIVER) {
                if (deviceId > 0){
                    YukonPao pao = paoDao.getYukonPao(deviceId);
                    DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(pao);
                    hardware.setTwoWayDeviceName(displayablePao.getName());
                }
            }
            
            hardwareTypeExtensionService.retrieveDevice(hardware);
            
        }
        
        return hardware;
    }
    
    @Override
    public ListMultimap<HardwareClass, Hardware> getHardwareMapForAccount(int accountId) {
        ListMultimap<HardwareClass, Hardware> hardwareMap = ArrayListMultimap.create();
        
        List<Integer> inventoryIds = inventoryBaseDao.getInventoryIdsByAccountId(accountId);
        for (int inventoryId : inventoryIds) {
            Hardware hardware = getHardware(inventoryId);
            HardwareType hardwareType = hardware.getHardwareType();
            if (hardwareType.isMeter()) {
                hardwareMap.put(HardwareClass.METER, hardware);
            } else if (hardwareType.isThermostat()) {
                hardwareMap.put(HardwareClass.THERMOSTAT, hardware);
            } else if (hardwareType.isGateway()) {
                hardwareMap.put(HardwareClass.GATEWAY, hardware);
            } else {
                hardwareMap.put(HardwareClass.SWITCH, hardware);
            }
        }
        
        return hardwareMap;
    }
    
    @Override
    public SimpleDevice createTwoWayDevice(LiteYukonUser user, int inventoryId, String deviceName) 
    throws Lcr3102YukonDeviceCreationException {
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        YukonListEntry deviceTypeEntry = yukonListDao.getYukonListEntry(lmHardwareBase.getLMHarewareTypeId());
        
        if (StringUtils.isBlank(deviceName)) {
            throw new Lcr3102YukonDeviceCreationException(Lcr3102YukonDeviceCreationException.Type.REQUIRED);
        } else {
            List<LiteYukonPAObject> existingDevicesWithName = paoDao.getLiteYukonPaoByName(deviceName, false);
            if (existingDevicesWithName.size() > 0) {
                throw new Lcr3102YukonDeviceCreationException(Lcr3102YukonDeviceCreationException.Type.UNAVAILABLE);
            }
        }
        
        YukonListEntry entry = yukonListDao.getYukonListEntry(deviceTypeEntry.getEntryID());
        String deviceTypeName = entry.getEntryText();
        PaoType paoType = PaoType.getForDbString(deviceTypeName);
        int yukonDeviceTypeId = paoType.getDeviceTypeId();
        
        SimpleDevice yukonDevice = null;
        try {
            int serialNumber = Integer.parseInt(lmHardwareBase.getManufacturerSerialNumber());
            yukonDevice = deviceCreationService.createCarrierDeviceByDeviceType(yukonDeviceTypeId, deviceName, serialNumber , lmHardwareBase.getRouteId(), true);
        } catch (DeviceCreationException e) {
            throw new Lcr3102YukonDeviceCreationException(e);
        } catch (NumberFormatException nfe){
            throw new Lcr3102YukonDeviceCreationException(nfe, Lcr3102YukonDeviceCreationException.Type.NON_NUMERIC);
        }
        
        // Logging Hardware Creation
        hardwareEventLogService.hardwareCreated(user, lmHardwareBase.getManufacturerSerialNumber());
        
        return yukonDevice;
    }
    
    @Override
    @Transactional
    public boolean updateHardware(LiteYukonUser user, Hardware hardware) {
        LiteInventoryBase liteInventoryBase = inventoryBaseDao.getByInventoryId(hardware.getInventoryId());
        setInventoryFieldsFromDto(liteInventoryBase, hardware);
        YukonEnergyCompany ec = yukonEnergyCompanyService.getEnergyCompanyByInventoryId(hardware.getInventoryId());
        
        checkSerialNumber(hardware);
        
        HardwareType hardwareType = hardware.getHardwareType();
        if (hardwareType.isMeter()) {
            if (hardwareType.getInventoryCategory() == InventoryCategory.MCT) {
                inventoryBaseDao.saveInventoryBase(liteInventoryBase, ec.getEnergyCompanyId());
            } else if (hardwareType.getInventoryCategory() == InventoryCategory.NON_YUKON_METER) {
                /* Update MeterHardwareBase */
                LiteMeterHardwareBase meterHardwareBase = (LiteMeterHardwareBase)liteInventoryBase;
                meterHardwareBase.setMeterNumber(hardware.getMeterNumber());
                inventoryBaseDao.saveMeterHardware(meterHardwareBase, ec.getEnergyCompanyId());
                
                /* Update LMHardwareToMeterMapping */
                List<Integer> assignedSwitches = Lists.newArrayList();
                for (SwitchAssignment assignment : hardware.getSwitchAssignments()) {
                    if (assignment.isAssigned()) {
                        assignedSwitches.add(assignment.getInventoryId());
                    }
                }
                inventoryBaseDao.saveSwitchAssignments(hardware.getInventoryId(), assignedSwitches);
            }
        } else {
            LiteLmHardwareBase lmHardware = (LiteLmHardwareBase)liteInventoryBase;
            
            /* Update Type/Category */
            YukonListEntry typeEntry = yukonListDao.getYukonListEntry(hardware.getHardwareTypeEntryId());
            // The Inventory Category list only exists on the default energy company.
            List<YukonListEntry> categoryEntry = yukonListDao.getYukonListEntry(hardwareType.getInventoryCategory().getDefinitionId(), starsDatabaseCache.getDefaultEnergyCompany());
            lmHardware.setLmHardwareTypeID(typeEntry.getEntryID());
            lmHardware.setCategoryID(categoryEntry.get(0).getEntryID());
            
            /* Update Route */
            lmHardware.setRouteID(hardware.getRouteId());
            
            /* Update Serial Number */
            lmHardware.setManufacturerSerialNumber(hardware.getSerialNumber());
            
            /* Create two-way device if need be */
            if (hardware.isCreatingNewTwoWayDevice()) {
                SimpleDevice device = createTwoWayDevice(user, hardware.getInventoryId(), hardware.getTwoWayDeviceName());
                lmHardware.setDeviceID(device.getDeviceId());
            }
            inventoryBaseDao.saveLmHardware(lmHardware, ec.getEnergyCompanyId());
            
            
            /* Log Serial Number Change */
            if (!hardware.getSerialNumber().equals(lmHardware.getManufacturerSerialNumber())) {
                hardwareEventLogService.serialNumberChanged(user, lmHardware.getManufacturerSerialNumber(), hardware.getSerialNumber());
            }
            
        }
        
        /* Update warehouse mapping */
        if (hardware.getWarehouseId() != null) {
            warehouseDao.moveInventoryToAnotherWarehouse(hardware.getInventoryId(), hardware.getWarehouseId());
        }
        
        /* Update the installation notes */
        if (hardware.getInstallNotes() != null) {
            List<LiteLMHardwareEvent> events = lmHardwareEventDao.getByInventoryId(liteInventoryBase.getInventoryID());
            for (LiteLMHardwareEvent event : events) {
                YukonListEntry entry = yukonListDao.getYukonListEntry(event.getActionID());
                if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL) {
                    /* The list of events is retrieved newest to oldest 
                     * so the first install in the list will be the most recent */
                    Date installDate = hardware.getFieldInstallDate() == null ? new Date(event.getEventDateTime()) : hardware.getFieldInstallDate();
                    lmCustomerEventBaseDao.updateNotesForEvent(event.getEventID(), installDate, SqlUtils.convertStringToDbValue(hardware.getInstallNotes()));
                }
            }
        }
        
        //Pass to the Service to handle any extensions for the hardwaretype
        hardwareTypeExtensionService.updateDevice(hardware);
        
        // Log hardware update
        hardwareEventLogService.hardwareUpdated(user, hardware.getSerialNumber());
        
        /* Tell controller to spawn event for device status change if necessary */
        if (hardware.getOriginalDeviceStatusEntryId() != null) {
            if (liteInventoryBase.getCurrentStateID() != hardware.getOriginalDeviceStatusEntryId()){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<HardwareHistory> getHardwareHistory(int inventoryId) {
        List<LiteLMHardwareEvent> events = lmHardwareEventDao.getByInventoryId(inventoryId);
        List<HardwareHistory> hardwareHistory = Lists.newArrayList();
        for (LiteLMHardwareEvent event : events) {
            YukonListEntry entry = yukonListDao.getYukonListEntry(event.getActionID());
            HardwareHistory historyEvent = new HardwareHistory();
            historyEvent.setAction(entry.getEntryText());
            historyEvent.setDate(new Date(event.getEventDateTime()));
            hardwareHistory.add(historyEvent);
        }
        return hardwareHistory;
    }
    
    @Override
    public void validateInventoryAgainstAccount(List<Integer> inventoryIdList, int accountId) throws NotAuthorizedException {

        for(Integer inventoryId : inventoryIdList) {
            
            LiteInventoryBase liteInventoryBase = inventoryBaseDao.getByInventoryId(inventoryId);
            if(liteInventoryBase.getAccountID() != accountId) {
                throw new NotAuthorizedException("The Inventory with id: " + inventoryId + " does not belong to the current customer account with id: " + accountId);
            }
        }
    }
    
    @Override
    @Transactional
    public int createHardware(Hardware hardware, LiteYukonUser user) throws ObjectInOtherEnergyCompanyException {
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        int inventoryId;
        String deviceLabel;
        
        checkSerialNumber(hardware);
        
        HardwareType hardwareType;
        if (hardware.getHardwareTypeEntryId() > 0) {
            YukonListEntry hardwareTypeEntry = yukonListDao.getYukonListEntry(hardware.getHardwareTypeEntryId());
            hardwareType = HardwareType.valueOf(hardwareTypeEntry.getYukonDefID());
        } else {
            /* This will be a real yukon MCT, they don't have a type coming from a yukon list entry. */
            hardwareType = HardwareType.YUKON_METER;
        }
        hardware.setHardwareType(hardwareType);
        
        if (hardwareType.isMeter()) {
            if (hardwareType.getInventoryCategory() == InventoryCategory.MCT) {
                /* InventoryBase */
                LiteInventoryBase inventoryBase = buildInventoryBase(hardware, energyCompany);
                
                inventoryId = inventoryBaseDao.saveInventoryBase(inventoryBase, energyCompany.getEnergyCompanyId()).getInventoryID();
                
                if (hardware.getAccountId() > 0) {
                    starsInventoryBaseService.addInstallHardwareEvent(inventoryBase, energyCompany, user);
                }
                                
                // label for Logging hardware creation
                deviceLabel = inventoryBase.getDeviceLabel();
            } else {
                /* MeterHardwareBase */
                LiteMeterHardwareBase meterHardwareBase = buildMeterHardwareBase(hardware, energyCompany); 
                
                inventoryId = inventoryBaseDao.saveMeterHardware(meterHardwareBase, hardware.getEnergyCompanyId()).getInventoryID();
                
                /* Update LMHardwareToMeterMapping */
                List<Integer> assignedSwitches = Lists.newArrayList();
                for (SwitchAssignment assignment : hardware.getSwitchAssignments()) {
                    if (assignment.isAssigned()) {
                        assignedSwitches.add(assignment.getInventoryId());
                    }
                }
                inventoryBaseDao.saveSwitchAssignments(inventoryId, assignedSwitches);
                
                // label for Logging hardware creation
                deviceLabel = meterHardwareBase.getDeviceLabel();
            }
        } else {
            /* LMHardwareBase and InventoryBase */
            LiteLmHardwareBase lmHardware = buildLmHardware(hardware, energyCompany); 
            inventoryId = inventoryBaseDao.saveLmHardware(lmHardware, energyCompany.getEnergyCompanyId()).getInventoryID();
            
            /* Create two-way device if need be */
            if (hardware.isCreatingNewTwoWayDevice()) {
                SimpleDevice device = createTwoWayDevice(user, inventoryId, hardware.getTwoWayDeviceName());
                lmHardware.setDeviceID(device.getDeviceId());
                inventoryBaseDao.updateInventoryBaseDeviceId(inventoryId, device.getDeviceId());
            }
            
            if (VersionTools.staticLoadGroupMappingExists()) {
                starsInventoryBaseService.initStaticLoadGroup(lmHardware, energyCompany);
            }
            
            if (hardware.getAccountId() > 0) {
                starsInventoryBaseService.addInstallHardwareEvent(lmHardware, energyCompany, user);
            }
            
            // label for Logging hardware creation
            deviceLabel = lmHardware.getDeviceLabel();
        }
        
        // This is set now for the extension service
        hardware.setInventoryId(inventoryId);
        
        // Pass to the extension service
        hardwareTypeExtensionService.createDevice(hardware);
        
        // Log hardware creation
        hardwareEventLogService.hardwareCreated(user, deviceLabel);
        
        return inventoryId;
    }
   
    @Override
    public void addDeviceToAccount(LiteInventoryBase liteInventoryBase, int accountId, boolean fromAccount, 
                                   LiteStarsEnergyCompany energyCompany,
                                   LiteYukonUser user) {
        if (fromAccount) {
            starsInventoryBaseService.removeDeviceFromAccount(liteInventoryBase, energyCompany, user);
        }
        
        liteInventoryBase.setAccountID(accountId);
        liteInventoryBase.setRemoveDate(new Instant(0).toDate().getTime());
        liteInventoryBase.setInstallDate(new Date().getTime());
        
        starsInventoryBaseService.addDeviceToAccount(liteInventoryBase, energyCompany, user, false);
    }
    
    @Override
    public int addYukonMeter(int meterId, Integer accountId, LiteYukonUser user) throws ObjectInOtherEnergyCompanyException {
        accountId = accountId == null ? 0 : accountId;
        
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        try {
            LiteInventoryBase liteInventoryBase = inventoryBaseDao.getByDeviceId(meterId);
            liteInventoryBase.setAccountID(accountId);
            liteInventoryBase.setRemoveDate(0);
            Date now = new Date();
            liteInventoryBase.setInstallDate(now.getTime());
            
            inventoryBaseDao.saveInventoryBase(liteInventoryBase, energyCompany.getEnergyCompanyId()).getInventoryID();

            /* Reset the installation notes */
            List<LiteLMHardwareEvent> events = lmHardwareEventDao.getByInventoryId(liteInventoryBase.getInventoryID());
            for (LiteLMHardwareEvent event : events) {
                YukonListEntry entry = yukonListDao.getYukonListEntry(event.getActionID());
                if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL) {
                    /* The list of events is retrieved newest to oldest 
                     * so the first install in the list will be the most recent */
                    lmCustomerEventBaseDao.updateNotesForEvent(event.getEventID(), now, "");
                }
            }
            
            return liteInventoryBase.getInventoryID();
        } catch (NotFoundException nfe) {
            /* This meter has never been added to a stars account before.  Add it to InventoryBase */
            Hardware hardware = new Hardware();
            hardware.setDeviceId(meterId);
            if (accountId > 0) {
                hardware.setFieldInstallDate(new Date());
            }
            hardware.setHardwareTypeEntryId(HardwareType.YUKON_METER.getDefinitionId());
            return createHardware(hardware, user);
        }
        
    }
    
    @Override
    public List<SwitchAssignment> getSwitchAssignments(List<Integer> assignedIds, int accountId) {
        List<DisplayableLmHardware> switchesOnAccount = inventoryBaseDao.getLmHardwareForAccount(accountId, HardwareClass.SWITCH);
        List<SwitchAssignment> switchAssignments = Lists.newArrayList();
        for (DisplayableLmHardware dhw : switchesOnAccount) {
            
            /* If this switch is assigned to a different meter, skip it. */
            Integer meterId = inventoryBaseDao.findMeterAssignment(dhw.getInventoryId());
            if (meterId != null && !assignedIds.contains(dhw.getInventoryId())){
                continue;
            }
            
            SwitchAssignment switchAssignment = new SwitchAssignment();
            switchAssignment.setInventoryId(dhw.getInventoryId());
            switchAssignment.setSerialNumber(dhw.getSerialNumber());
            switchAssignment.setLabel(dhw.getLabel());
            
            if (assignedIds.contains(dhw.getInventoryId())) {
                switchAssignment.setAssigned(true);
            }
            switchAssignments.add(switchAssignment);
        }
        return switchAssignments;
    }
    
    @Override
    public void changeOutInventory(int oldInventoryId, int newInventoryId, LiteYukonUser user, boolean isMeter) {
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        LiteInventoryBase oldInventory = inventoryBaseDao.getByInventoryId(oldInventoryId);
        
        /* If this is a meter change out, the newInventoryId will be a pao id */
        if (isMeter) {
            int accountId = oldInventory.getAccountID();
            starsInventoryBaseService.removeDeviceFromAccount(oldInventory, energyCompany, user);
            addYukonMeter(newInventoryId, accountId, user);
        } else {
            LiteInventoryBase newInventory = inventoryBaseDao.getByInventoryId(newInventoryId);
            newInventory.setAccountID(oldInventory.getAccountID());
            starsInventoryBaseService.removeDeviceFromAccount(oldInventory, energyCompany, user);
            starsInventoryBaseService.addDeviceToAccount(newInventory, energyCompany, user, false);
        }
        
    }
    
    /* HELPERS */
    
    /**
     * Serial numbers can only be used once among all energy companies that are relatives.
     * @throws StarsDeviceSerialNumberAlreadyExistsException
     * @throws ObjectInOtherEnergyCompanyException
     */
    public void checkSerialNumber(Hardware hardware) {
        String serialNumber = hardware.getSerialNumber();
        
        LiteInventoryBase possibleDuplicate = starsSearchDao.searchLmHardwareBySerialNumber(serialNumber, hardware.getEnergyCompanyId());
        Integer inventoryId = hardware.getInventoryId();
        boolean creating = inventoryId == null;
        if (possibleDuplicate != null 
                && (creating || (inventoryId != possibleDuplicate.getInventoryID()))) {
            throw new StarsDeviceSerialNumberAlreadyExistsException();
        }
    }
    
    @Override
    public boolean isSerialNumberInEC(Hardware hardware) {
        try {
            checkSerialNumber(hardware);
        } catch (ObjectInOtherEnergyCompanyException e) {
            return true;
        } catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            return true;
        }
        return false;
    }
    
    private LiteLmHardwareBase buildLmHardware(Hardware hardware, LiteStarsEnergyCompany energyCompany) {
        LiteLmHardwareBase lmHardware = new LiteLmHardwareBase();
        
        /* InventoryBase fields */
        int categoryId = inventoryDao.getCategoryIdForTypeId(hardware.getHardwareTypeEntryId(), energyCompany);
        lmHardware.setCategoryID(categoryId);
        lmHardware.setEnergyCompanyId(energyCompany.getEnergyCompanyId());
        
        /* InventoryBase fields from Dto*/
        setInventoryFieldsFromDto(lmHardware, hardware);

        /* LMHardwareBase Fields */
        lmHardware.setManufacturerSerialNumber(hardware.getSerialNumber());
        lmHardware.setLmHardwareTypeID(hardware.getHardwareTypeEntryId());
        
        if (!hardware.getHardwareType().isGateway()) {
            lmHardware.setRouteID(hardware.getRouteId());
        }
        
        if(StringUtils.isBlank(lmHardware.getDeviceLabel())){
            lmHardware.setDeviceLabel(lmHardware.getManufacturerSerialNumber());
        }
        
        lmHardware.setAlternateTrackingNumber(SqlUtils.convertStringToDbValue(hardware.getAltTrackingNumber()));
        lmHardware.setNotes(SqlUtils.convertStringToDbValue(hardware.getDeviceNotes()));
        
        return lmHardware;
    }
    
    private LiteMeterHardwareBase buildMeterHardwareBase(Hardware hardware, LiteStarsEnergyCompany energyCompany) {
        LiteMeterHardwareBase meterHardware = new LiteMeterHardwareBase();
        
        /* InventoryBase fields */
        int categoryId = inventoryDao.getCategoryIdForTypeId(hardware.getHardwareTypeEntryId(), energyCompany);
        meterHardware.setCategoryID(categoryId);
        meterHardware.setEnergyCompanyId(energyCompany.getEnergyCompanyId());
        
        /* InventoryBase fields from Dto*/
        setInventoryFieldsFromDto(meterHardware, hardware);

        /* LMHardwareBase Fields */
        meterHardware.setMeterNumber(hardware.getMeterNumber());
        YukonListEntry typeEntry = energyCompany.getYukonListEntry(HardwareType.NON_YUKON_METER.getDefinitionId());
        meterHardware.setMeterTypeID(typeEntry.getEntryID());
        
        if(StringUtils.isBlank(meterHardware.getDeviceLabel())){
            meterHardware.setDeviceLabel(meterHardware.getMeterNumber());
        }
        
        meterHardware.setAlternateTrackingNumber(SqlUtils.convertStringToDbValue(hardware.getAltTrackingNumber()));
        meterHardware.setNotes(SqlUtils.convertStringToDbValue(hardware.getDeviceNotes()));
        
        return meterHardware;
    }
    
    private LiteInventoryBase buildInventoryBase(Hardware hardware, LiteStarsEnergyCompany energyCompany) {
        LiteInventoryBase liteInventoryBase = new LiteInventoryBase();
        
        /* InventoryBase fields */
        int categoryId = inventoryDao.getCategoryIdForTypeId(hardware.getHardwareTypeEntryId(), energyCompany);
        liteInventoryBase.setCategoryID(categoryId);
        liteInventoryBase.setEnergyCompanyId(energyCompany.getEnergyCompanyId());
        
        /* InventoryBase fields from Dto*/
        setInventoryFieldsFromDto(liteInventoryBase, hardware);
        
        if(StringUtils.isBlank(liteInventoryBase.getDeviceLabel())){
            LiteYukonPAObject device = DaoFactory.getPaoDao().getLiteYukonPAO(liteInventoryBase.getDeviceID());
            liteInventoryBase.setDeviceLabel(device.getPaoName());
        }
        
        liteInventoryBase.setAlternateTrackingNumber(SqlUtils.convertStringToDbValue(hardware.getAltTrackingNumber()));
        liteInventoryBase.setNotes(SqlUtils.convertStringToDbValue(hardware.getDeviceNotes()));
        
        return liteInventoryBase;
    }
    
    /**
     * Populates LiteInventoryBase with update fields of the HardwareDto object.
     */
    private void setInventoryFieldsFromDto(LiteInventoryBase liteInventoryBase, Hardware hardware) {
        liteInventoryBase.setAccountID(hardware.getAccountId());
        
        if(!StringUtils.isBlank(hardware.getDisplayLabel())){
            liteInventoryBase.setDeviceLabel(hardware.getDisplayLabel());
        } 
        
        if (hardware.getVoltageEntryId() != null) {
            liteInventoryBase.setVoltageID(hardware.getVoltageEntryId());
        }
        
        if (hardware.getFieldInstallDate() != null) {
            liteInventoryBase.setInstallDate(hardware.getFieldInstallDate().getTime());
        }
        
        if (hardware.getFieldReceiveDate() != null) {
            liteInventoryBase.setReceiveDate(hardware.getFieldReceiveDate().getTime());
        }
        
        if (hardware.getFieldRemoveDate() != null) {
            liteInventoryBase.setRemoveDate(hardware.getFieldRemoveDate().getTime());
        } 
        
        if (hardware.getDeviceStatusEntryId() != null) {
            liteInventoryBase.setCurrentStateID(hardware.getDeviceStatusEntryId());
        }
        
        if (hardware.getServiceCompanyId() != null) {
            liteInventoryBase.setInstallationCompanyID(hardware.getServiceCompanyId());
        }
        
        if (hardware.getDeviceId() != null) {
            liteInventoryBase.setDeviceID(hardware.getDeviceId());
        }
        
        liteInventoryBase.setAlternateTrackingNumber(SqlUtils.convertStringToDbValue(hardware.getAltTrackingNumber()));
        liteInventoryBase.setNotes(SqlUtils.convertStringToDbValue(hardware.getDeviceNotes()));
    }

    public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
        this.starsSearchDao = starsSearchDao;
    }
    
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }
    
}