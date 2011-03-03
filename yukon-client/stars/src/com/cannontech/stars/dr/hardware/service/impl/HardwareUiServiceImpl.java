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
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryCategory;
import com.cannontech.common.inventory.LMHardwareClass;
import com.cannontech.common.model.DigiGateway;
import com.cannontech.common.model.ZigbeeThermostat;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteMeterHardwareBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.stars.core.service.StarsInventoryBaseService;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.event.dao.LMCustomerEventBaseDao;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.model.CustomerAction;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.stars.dr.hardware.model.HardwareHistory;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.model.SwitchAssignment;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class HardwareUiServiceImpl implements HardwareUiService {

    private HardwareEventLogService hardwareEventLogService;
    private InventoryBaseDao inventoryBaseDao;
    private YukonListDao yukonListDao;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private LMHardwareEventDao lmHardwareEventDao;
    private PaoDao paoDao;
    private LMCustomerEventBaseDao lmCustomerEventBaseDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private DeviceCreationService deviceCreationService;
    private StarsSearchDao starsSearchDao;
    private StarsDatabaseCache starsDatabaseCache;
    private WarehouseDao warehouseDao;
    private PaoLoadingService paoLoadingService;
    private StarsInventoryBaseService starsInventoryBaseService;
    private ZigbeeDeviceService zigbeeDeviceService;
    private AttributeService attributeService;
    
    @Override
    public HardwareDto getHardwareDto(int inventoryId, int energyCompanyId, int accountId) {
        HardwareDto hardwareDto = new HardwareDto();
        hardwareDto.setInventoryId(inventoryId);
        hardwareDto.setEnergyCompanyId(energyCompanyId);
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        
        /* Set Hardware Basics */
        LiteInventoryBase liteInventoryBase = starsInventoryBaseDao.getByInventoryId(inventoryId);
        
        int categoryId = liteInventoryBase.getCategoryID();
        YukonListEntry categoryNameYLE = yukonListDao.getYukonListEntry(categoryId);
        hardwareDto.setCategoryName(categoryNameYLE.getEntryText());
        
        hardwareDto.setDeviceId(liteInventoryBase.getDeviceID());
        hardwareDto.setDisplayLabel(liteInventoryBase.getDeviceLabel());
        hardwareDto.setAltTrackingNumber(liteInventoryBase.getAlternateTrackingNumber());
        hardwareDto.setVoltageEntryId(liteInventoryBase.getVoltageID());
        
        /* For some reason some (not real) dates were saved with a time of 19:00 instead of 18:00. */
        Instant beginningOfJavaTime = new Instant(0).plus(Duration.standardDays(1));
        Instant installDT = new Instant(liteInventoryBase.getInstallDate());
        if(installDT.isAfter(beginningOfJavaTime)){
            hardwareDto.setFieldInstallDate(installDT.toDate());
        } else {
            hardwareDto.setFieldInstallDate(null);
        }
        
        Instant receiveDT = new Instant(liteInventoryBase.getReceiveDate());
        if(receiveDT.isAfter(beginningOfJavaTime)){
            hardwareDto.setFieldReceiveDate(receiveDT.toDate());
        } else {
            hardwareDto.setFieldReceiveDate(null);
        }
        
        Instant removeDT = new Instant(liteInventoryBase.getRemoveDate());
        if(removeDT.isAfter(beginningOfJavaTime)){
            hardwareDto.setFieldRemoveDate(removeDT.toDate());
        } else {
            hardwareDto.setFieldRemoveDate(null);
        }
        
        hardwareDto.setDeviceNotes(liteInventoryBase.getNotes());
        hardwareDto.setDeviceStatusEntryId(liteInventoryBase.getCurrentStateID());
        hardwareDto.setOriginalDeviceStatusEntryId(liteInventoryBase.getCurrentStateID());
        hardwareDto.setServiceCompanyId(liteInventoryBase.getInstallationCompanyID());
        
        Warehouse warehouse = warehouseDao.findWarehouseForInventoryId(inventoryId);
        hardwareDto.setWarehouseId(warehouse == null ? 0 : warehouse.getWarehouseID());
        
        List<LiteLMHardwareEvent> events = lmHardwareEventDao.getByInventoryId(liteInventoryBase.getInventoryID());
        String installNotes = null;
        for(LiteLMHardwareEvent event : events) {
            YukonListEntry entry = yukonListDao.getYukonListEntry(event.getActionID());
            CustomerAction action = CustomerAction.valueOf(entry.getYukonDefID());
            /* The list of events is retrieved newest to oldest 
             * so the first install in the list will be the most recent */
            if(action == CustomerAction.INSTALL) {
                installNotes = SqlUtils.convertDbValueToString(event.getNotes());
                break;
            }
        }
        hardwareDto.setInstallNotes(installNotes);
        
        /* Set Hardware Details based on type(Switch, Thermostat, or MCT */
        YukonListEntry categoryEntry = yukonListDao.getYukonListEntry(categoryId);
        InventoryCategory hardwareCategory = InventoryCategory.valueOf(categoryEntry.getYukonDefID());
        
        if(hardwareCategory == InventoryCategory.MCT) {
            /* Hardware is an MCT */
            hardwareDto.setHardwareType(HardwareType.YUKON_METER);
            
            if(liteInventoryBase.getDeviceID() > 0) {
                /* The device id has been set to a real MCT. */
                YukonPao pao = paoDao.getYukonPao(liteInventoryBase.getDeviceID());
                DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(pao);
                hardwareDto.setDisplayType(displayablePao.getPaoIdentifier().getPaoType().getPaoTypeName());
                hardwareDto.setDisplayName(displayablePao.getName());
            } else {
                /* Not attached to a real MCT yet. Use label for name if you can */
                /* and use the MCT list enty of the energy company as the device type. */
                YukonListEntry mctDeviceType = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT);
                hardwareDto.setDisplayType(mctDeviceType.getEntryText());
                if(StringUtils.isNotBlank(liteInventoryBase.getDeviceLabel())){
                    hardwareDto.setDisplayName(liteInventoryBase.getDeviceLabel());
                }
            }
        } else if(hardwareCategory == InventoryCategory.NON_YUKON_METER) {
            LiteMeterHardwareBase liteMeterHardwareBase = (LiteMeterHardwareBase) liteInventoryBase;
            /* Hardware is an meter but being handle by stars instead of being a yukon mct (pao) */
            hardwareDto.setMeterNumber(liteMeterHardwareBase.getMeterNumber());
            hardwareDto.setDisplayName(liteMeterHardwareBase.getMeterNumber());
            hardwareDto.setHardwareTypeEntryId(liteMeterHardwareBase.getMeterTypeID());
            
            YukonListEntry hardwareTypeEntry = yukonListDao.getYukonListEntry(liteMeterHardwareBase.getMeterTypeID());
            HardwareType hardwareType = HardwareType.valueOf(hardwareTypeEntry.getYukonDefID());
            hardwareDto.setHardwareType(hardwareType);
            
            YukonListEntry mctDeviceType = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT);
            hardwareDto.setDisplayType(mctDeviceType.getEntryText());
            
            List<Integer> assignedIds = starsInventoryBaseDao.getSwitchAssignmentsForMeter(inventoryId);
            
            for(SwitchAssignment assignement : getSwitchAssignments(assignedIds, accountId)) {
                hardwareDto.getSwitchAssignments().add(assignement);
            }
            
        } else if(hardwareCategory == InventoryCategory.ONE_WAY_RECEIVER || hardwareCategory == InventoryCategory.TWO_WAY_RECEIVER) {
            /* Must be a switch or thermostat. */
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(liteInventoryBase.getInventoryID());
            
            hardwareDto.setHardwareTypeEntryId(lmHardwareBase.getLMHarewareTypeId());
            
            YukonListEntry hardwareTypeEntry = yukonListDao.getYukonListEntry(lmHardwareBase.getLMHarewareTypeId());
            HardwareType hardwareType = HardwareType.valueOf(hardwareTypeEntry.getYukonDefID());
            hardwareDto.setHardwareType(hardwareType);
            
            YukonListEntry deviceTypeEntry = yukonListDao.getYukonListEntry(lmHardwareBase.getLMHarewareTypeId());
            hardwareDto.setDisplayType(deviceTypeEntry.getEntryText());
            hardwareDto.setSerialNumber(lmHardwareBase.getManufacturerSerialNumber());
            hardwareDto.setDisplayName(lmHardwareBase.getManufacturerSerialNumber());
            hardwareDto.setRouteId(lmHardwareBase.getRouteId());
            
            /* Two Way LCR's */
            if (hardwareType.isSwitch() && hardwareCategory == InventoryCategory.TWO_WAY_RECEIVER) {
                if(liteInventoryBase.getDeviceID() > 0){
                    YukonPao pao = paoDao.getYukonPao(liteInventoryBase.getDeviceID());
                    DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(pao);
                    hardwareDto.setTwoWayDeviceName(displayablePao.getName());
                }
            }
            
            LMHardwareClass hardwareClass = hardwareDto.getHardwareType().getLMHardwareClass();
            hardwareDto.setHardwareClass(hardwareClass);
            
            /* Zigbee Devices */
            if(hardwareType.isZigbee()) {
                /* Util Pro Tstat */
                if (hardwareType.isThermostat()) {
                    ZigbeeThermostat zigbeeThermostat = zigbeeDeviceService.getZigbeeThermostat(liteInventoryBase.getDeviceID()); 
                    
                    hardwareDto.setInstallCode(zigbeeThermostat.getInstallCode());
                    
                    LitePoint linkPt = attributeService.getPointForAttribute(zigbeeThermostat, BuiltInAttribute.ZIGBEE_LINK_STATUS);
                    hardwareDto.setCommissionedId(linkPt.getLiteID());
                    
                } else if (hardwareType.isGateway()) {
                    DigiGateway digiGateway = zigbeeDeviceService.getDigiGateway(liteInventoryBase.getDeviceID());
                    
                    hardwareDto.setMacAddress(digiGateway.getMacAddress());
                    hardwareDto.setFirmwareVersion(digiGateway.getFirmwareVersion());
                    
                    LitePoint linkPt = attributeService.getPointForAttribute(digiGateway, BuiltInAttribute.ZIGBEE_LINK_STATUS);
                    hardwareDto.setCommissionedId(linkPt.getLiteID());
                    
                } else {
                    /* This is an unknown Zigbee device type */
                    throw new NotFoundException("Device type not found for inventory id:" + inventoryId);
                }
            }
            
        } else {
            /* This is not a device we know about, maybe we should throw something smarter here. */
            throw new NotFoundException("Device type not found for inventory id:" + inventoryId);
        }
        
        return hardwareDto;
    }
    
    @Override
    public ListMultimap<LMHardwareClass, HardwareDto> getHardwareMapForAccount(int accountId, int energyCompanyId) {
        ListMultimap<LMHardwareClass, HardwareDto> hardwareMap = ArrayListMultimap.create();
        
        List<Integer> inventoryIds = inventoryBaseDao.getInventoryIdsByAccountId(accountId);
        for(int inventoryId : inventoryIds) {
            HardwareDto hardwareDto = getHardwareDto(inventoryId, energyCompanyId, accountId);
            HardwareType hardwareType = hardwareDto.getHardwareType();
            if (hardwareType.isMeter()) {
                hardwareMap.put(LMHardwareClass.METER, hardwareDto);
            } else if (hardwareType.isThermostat()) {
                hardwareMap.put(LMHardwareClass.THERMOSTAT, hardwareDto);
            } else if (hardwareType.isGateway()) {
                hardwareMap.put(LMHardwareClass.GATEWAY, hardwareDto);
            } else {
                hardwareMap.put(LMHardwareClass.SWITCH, hardwareDto);
            }
        }
        
        return hardwareMap;
    }
    
    @Override
    public SimpleDevice createTwoWayDevice(YukonUserContext userContext, int inventoryId, String deviceName) 
    throws StarsTwoWayLcrYukonDeviceCreationException {
        String errorMsgKey = "";
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        YukonListEntry deviceTypeEntry = yukonListDao.getYukonListEntry(lmHardwareBase.getLMHarewareTypeId());
        
        if (StringUtils.isBlank(deviceName)) {
            errorMsgKey = "mustSupplyName";
        } else {
            List<LiteYukonPAObject> existingDevicesWithName = paoDao.getLiteYukonPaoByName(deviceName, false);
            if (existingDevicesWithName.size() > 0) {
                errorMsgKey = "nameAlreadyExists";
            }
        }
        
        if(StringUtils.isNotBlank(errorMsgKey)){
            throw new StarsTwoWayLcrYukonDeviceCreationException(errorMsgKey);
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
            throw new StarsTwoWayLcrYukonDeviceCreationException("unknown", e);
        } catch (NumberFormatException nfe){
            throw new StarsTwoWayLcrYukonDeviceCreationException("nonNumericSerialNumber", nfe);
        }
        
        // Logging Hardware Creation
        hardwareEventLogService.hardwareCreated(userContext.getYukonUser(), lmHardwareBase.getManufacturerSerialNumber());
        
        return yukonDevice;
    }
    
    @Override
    @Transactional
    public boolean updateHardware(YukonUserContext userContext, HardwareDto hardwareDto) 
    throws ObjectInOtherEnergyCompanyException {
        LiteInventoryBase liteInventoryBase = starsInventoryBaseDao.getByInventoryId(hardwareDto.getInventoryId());
        setInventoryFieldsFromDto(liteInventoryBase, hardwareDto);
        
        checkSerialNumber(hardwareDto);
        
        HardwareType hardwareType = hardwareDto.getHardwareType();
        if(hardwareType.isMeter()) {
            if(hardwareType.getInventoryCategory() == InventoryCategory.MCT) {
                starsInventoryBaseDao.saveInventoryBase(liteInventoryBase, hardwareDto.getEnergyCompanyId());
            } else if (hardwareType.getInventoryCategory() == InventoryCategory.NON_YUKON_METER) {
                /* Update MeterHardwareBase */
                LiteMeterHardwareBase meterHardwareBase = (LiteMeterHardwareBase)liteInventoryBase;
                meterHardwareBase.setMeterNumber(hardwareDto.getMeterNumber());
                starsInventoryBaseDao.saveMeterHardware(meterHardwareBase, hardwareDto.getEnergyCompanyId());
                
                /* Update LMHardwareToMeterMapping */
                List<Integer> assignedSwitches = Lists.newArrayList();
                for (SwitchAssignment assignment : hardwareDto.getSwitchAssignments()) {
                    if(assignment.isAssigned()) {
                        assignedSwitches.add(assignment.getInventoryId());
                    }
                }
                starsInventoryBaseDao.saveSwitchAssignments(hardwareDto.getInventoryId(), assignedSwitches);
            }
        } else {
            LiteStarsLMHardware lmHardware = (LiteStarsLMHardware)liteInventoryBase;
            
            /* Update Route */
            lmHardware.setRouteID(hardwareDto.getRouteId());
            
            /* Update Serial Number */
            lmHardware.setManufacturerSerialNumber(hardwareDto.getSerialNumber());
            
            starsInventoryBaseDao.saveLmHardware(lmHardware, hardwareDto.getEnergyCompanyId());
            
            // Log Serial Number Change
            if (!hardwareDto.getSerialNumber().equals(lmHardware.getManufacturerSerialNumber())) {
                hardwareEventLogService.serialNumberChanged(userContext.getYukonUser(), lmHardware.getManufacturerSerialNumber(), hardwareDto.getSerialNumber());
            }
        }
        
        /* Update warehouse mapping */
        if(hardwareDto.getWarehouseId() != null) {
            warehouseDao.moveInventoryToAnotherWarehouse(hardwareDto.getInventoryId(), hardwareDto.getWarehouseId());
        }
        
        /* Update the installation notes */
        if(hardwareDto.getInstallNotes() != null) {
            List<LiteLMHardwareEvent> events = lmHardwareEventDao.getByInventoryId(liteInventoryBase.getInventoryID());
            for(LiteLMHardwareEvent event : events) {
                YukonListEntry entry = yukonListDao.getYukonListEntry(event.getActionID());
                if(entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL) {
                    /* The list of events is retrieved newest to oldest 
                     * so the first install in the list will be the most recent */
                    Date installDate = hardwareDto.getFieldInstallDate() == null ? new Date(event.getEventDateTime()) : hardwareDto.getFieldInstallDate();
                    lmCustomerEventBaseDao.updateNotesForEvent(event.getEventID(), installDate, SqlUtils.convertStringToDbValue(hardwareDto.getInstallNotes()));
                }
            }
        }
        
        //Pass to the Zigbee Service to handle any Zigbee specifics required
        zigbeeDeviceService.updateDevice(hardwareDto);
        
        // Log hardware update
        hardwareEventLogService.hardwareUpdated(userContext.getYukonUser(), hardwareDto.getSerialNumber());
        
        /* Tell controller to spawn event for device status change if necessary */
        if(hardwareDto.getOriginalDeviceStatusEntryId() != null) {
            if(liteInventoryBase.getCurrentStateID() != hardwareDto.getOriginalDeviceStatusEntryId()){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<HardwareHistory> getHardwareHistory(int inventoryId) {
        List<LiteLMHardwareEvent> events = lmHardwareEventDao.getByInventoryId(inventoryId);
        List<HardwareHistory> hardwareHistory = Lists.newArrayList();
        for(LiteLMHardwareEvent event : events) {
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
            
            LiteInventoryBase liteInventoryBase = starsInventoryBaseDao.getByInventoryId(inventoryId);
            if(liteInventoryBase.getAccountID() != accountId) {
                throw new NotAuthorizedException("The Inventory with id: " + inventoryId + " does not belong to the current customer account with id: " + accountId);
            }
        }
    }
    
    @Override
    @Transactional
    public int createHardware(HardwareDto hardwareDto, int accountId, YukonUserContext userContext) throws ObjectInOtherEnergyCompanyException {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        int inventoryId;
        String deviceLabel;
        
        checkSerialNumber(hardwareDto);
        
        HardwareType hardwareType;
        if(hardwareDto.getHardwareTypeEntryId() > 0){
            YukonListEntry hardwareTypeEntry = yukonListDao.getYukonListEntry(hardwareDto.getHardwareTypeEntryId());
            hardwareType = HardwareType.valueOf(hardwareTypeEntry.getYukonDefID());
        } else {
            /* This will be a real yukon MCT, they don't have a type coming from a yukon list entry. */
            hardwareType = HardwareType.YUKON_METER;
        }
        hardwareDto.setHardwareType(hardwareType);
        
        if(hardwareType.isMeter()) {
            if(hardwareType.getInventoryCategory() == InventoryCategory.MCT) {
                /* InventoryBase */
                LiteInventoryBase inventoryBase = getInventory(hardwareDto, accountId, energyCompany);
                
                inventoryId = starsInventoryBaseDao.saveInventoryBase(inventoryBase, energyCompany.getEnergyCompanyId()).getInventoryID();
                
                starsInventoryBaseService.addInstallHardwareEvent(inventoryBase, energyCompany, userContext.getYukonUser());
                
                // label for Logging hardware creation
                deviceLabel = inventoryBase.getDeviceLabel();
            } else {
                /* MeterHardwareBase */
                LiteMeterHardwareBase meterHardwareBase = getMeterHardware(hardwareDto, accountId, energyCompany); 
                
                inventoryId = starsInventoryBaseDao.saveMeterHardware(meterHardwareBase, hardwareDto.getEnergyCompanyId()).getInventoryID();
                
                /* Update LMHardwareToMeterMapping */
                List<Integer> assignedSwitches = Lists.newArrayList();
                for (SwitchAssignment assignment : hardwareDto.getSwitchAssignments()) {
                    if(assignment.isAssigned()) {
                        assignedSwitches.add(assignment.getInventoryId());
                    }
                }
                starsInventoryBaseDao.saveSwitchAssignments(inventoryId, assignedSwitches);
                
                // label for Logging hardware creation
                deviceLabel = meterHardwareBase.getDeviceLabel();
            }
        } else {
            /* LMHardwareBase and InventoryBase*/
            LiteStarsLMHardware lmHardware = getLmHardware(hardwareDto, accountId, energyCompany); 
            inventoryId = starsInventoryBaseDao.saveLmHardware(lmHardware, energyCompany.getEnergyCompanyId()).getInventoryID();
            
            if(hardwareType.isThermostat()) {
                starsInventoryBaseService.initThermostatSchedule(lmHardware, energyCompany);
            }
            
            if (VersionTools.staticLoadGroupMappingExists()) {
                starsInventoryBaseService.initStaticLoadGroup(lmHardware, energyCompany);
            }
            
            starsInventoryBaseService.addInstallHardwareEvent(lmHardware, energyCompany, userContext.getYukonUser());
            
            // label for Logging hardware creation
            deviceLabel = lmHardware.getDeviceLabel();
        }
        
        // This is set now for the ZigbeeDeviceService
        hardwareDto.setInventoryId(inventoryId);
        
        // Pass to the Zigbee Service to handle any Zigbee specifics required
        zigbeeDeviceService.createDevice(hardwareDto);
        
        // Log hardware creation
        hardwareEventLogService.hardwareCreated(userContext.getYukonUser(), deviceLabel);
        
        return inventoryId;
    }
    
    @Override
    public void addDeviceToAccount(LiteInventoryBase liteInventoryBase, int accountId, boolean fromAccount, 
                                   LiteStarsEnergyCompany energyCompany,
                                   LiteYukonUser user) {
        if(fromAccount) {
            starsInventoryBaseService.removeDeviceFromAccount(liteInventoryBase, false, energyCompany, user);
        }
        
        liteInventoryBase.setAccountID(accountId);
        liteInventoryBase.setRemoveDate(new Instant(0).toDate().getTime());
        liteInventoryBase.setInstallDate(new Date().getTime());
        
        starsInventoryBaseService.addDeviceToAccount(liteInventoryBase, energyCompany, user, false);
    }
    
    @Override
    public void addYukonMeter(int meterId, int accountId, YukonUserContext userContext) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        try {
            LiteInventoryBase liteInventoryBase = starsInventoryBaseDao.getByDeviceId(meterId);
            liteInventoryBase.setAccountID(accountId);
            liteInventoryBase.setRemoveDate(0);
            Date now = new Date();
            liteInventoryBase.setInstallDate(now.getTime());
            
            starsInventoryBaseDao.saveInventoryBase(liteInventoryBase, energyCompany.getEnergyCompanyId()).getInventoryID();

            /* Reset the installation notes */
            List<LiteLMHardwareEvent> events = lmHardwareEventDao.getByInventoryId(liteInventoryBase.getInventoryID());
            for(LiteLMHardwareEvent event : events) {
                YukonListEntry entry = yukonListDao.getYukonListEntry(event.getActionID());
                if(entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL) {
                    /* The list of events is retrieved newest to oldest 
                     * so the first install in the list will be the most recent */
                    lmCustomerEventBaseDao.updateNotesForEvent(event.getEventID(), now, "");
                }
            }
            
        } catch (NotFoundException nfe) {
            /* This meter has never been added to a stars account before.  Add it to InventoryBase */
            HardwareDto hardwareDto = new HardwareDto();
            hardwareDto.setDeviceId(meterId);
            hardwareDto.setFieldInstallDate(new Date());
            hardwareDto.setHardwareTypeEntryId(HardwareType.YUKON_METER.getDefinitionId());
            try {
                createHardware(hardwareDto, accountId, userContext);
            } catch (ObjectInOtherEnergyCompanyException e) {/* Ignore */}
        }
        
    }
    
    @Override
    public List<SwitchAssignment> getSwitchAssignments(List<Integer> assignedIds, int accountId) {
        List<DisplayableLmHardware> switchesOnAccount = starsInventoryBaseDao.getLmHardwareForAccount(accountId, LMHardwareClass.SWITCH);
        List<SwitchAssignment> switchAssignments = Lists.newArrayList();
        for(DisplayableLmHardware dhw : switchesOnAccount) {
            
            /* If this switch is assigned to a different meter, skip it. */
            Integer meterId = starsInventoryBaseDao.findMeterAssignment(dhw.getInventoryId());
            if(meterId != null && !assignedIds.contains(dhw.getInventoryId())){
                continue;
            }
            
            SwitchAssignment switchAssignment = new SwitchAssignment();
            switchAssignment.setInventoryId(dhw.getInventoryId());
            switchAssignment.setSerialNumber(dhw.getSerialNumber());
            switchAssignment.setLabel(dhw.getLabel());
            
            if(assignedIds.contains(dhw.getInventoryId())) {
                switchAssignment.setAssigned(true);
            }
            switchAssignments.add(switchAssignment);
        }
        return switchAssignments;
    }
    
    @Override
    public void changeOutInventory(int oldInventoryId, int changeOutId, YukonUserContext userContext, boolean isMeter) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        LiteInventoryBase old = starsInventoryBaseDao.getByInventoryId(oldInventoryId);
        
        /* If this is a meter change out the changeOutId will be a pao id */
        if(isMeter) {
            int accountId = old.getAccountID();
            starsInventoryBaseService.removeDeviceFromAccount(old, false, energyCompany, userContext.getYukonUser());
            addYukonMeter(changeOutId, accountId, userContext);
        } else {
            LiteInventoryBase changeOut = starsInventoryBaseDao.getByInventoryId(changeOutId);
            changeOut.setAccountID(old.getAccountID());
            starsInventoryBaseService.removeDeviceFromAccount(old, false, energyCompany, userContext.getYukonUser());
            starsInventoryBaseService.addDeviceToAccount(changeOut, energyCompany, userContext.getYukonUser(), false);
        }
        
    }
    
    /* HELPERS */
    
    /**
     * Returns the HardwareType enum entry for the given type id.
     * If the energy company uses yukon for meters then type id 
     * will be zero.
     */
    public HardwareType getHardwareTypeById(int hardwareTypeId) {
        Integer hardwareTypeDefinitionId;
        if(hardwareTypeId > 0) {
            /* This is a stars object that will live in either LMHardwareBase or MeterHardwareBase */
            YukonListEntry entry = yukonListDao.getYukonListEntry(hardwareTypeId);
            hardwareTypeDefinitionId = entry.getYukonDefID();
        } else {
            /* This is a real MCT that exists as a yukon pao.  It will only have a row in InventoryBase 
             * so there is no type id. */
            hardwareTypeDefinitionId = 0;
        }
        
        return HardwareType.valueOf(hardwareTypeDefinitionId);
    }
    
    /**
     * Returns the category id base on hardware type id which either comes from 
     * LMHardwareBase:LMHardwareTypeID or from MeterHardwareBase:MeterTypeID.
     * If this energy company uses yukon for meters, they will only exist in InventoryBase
     * and they won't have a type id; their category defaults to 'MCT'. For stars meters the 
     * category will be 'NON_YUKON_METER'.
     */
    private int getCategoryIdForTypeId(int hardwareTypeId, LiteStarsEnergyCompany energyCompany) {
        HardwareType hardwareType = getHardwareTypeById(hardwareTypeId);

        /* The category id is the entry id of the yukon list entry for this category in this energy company. */
        YukonListEntry categoryEntryOfEnergyCompany = energyCompany.getYukonListEntry(hardwareType.getInventoryCategory().getDefinitionId());
        return categoryEntryOfEnergyCompany.getEntryID();
    }
    
    /**
     * Serial numbers can only be used once among all energy companies that are relatives.
     * @throws StarsDeviceSerialNumberAlreadyExistsException
     * @throws ObjectInOtherEnergyCompanyException
     */
    public void checkSerialNumber(HardwareDto hardwareDto) throws ObjectInOtherEnergyCompanyException {
        LiteInventoryBase possibleDuplicate = starsSearchDao.searchLMHardwareBySerialNumber(hardwareDto.getSerialNumber(), hardwareDto.getEnergyCompanyId());
        if(possibleDuplicate != null && possibleDuplicate.getInventoryID() != hardwareDto.getInventoryId()){
            throw new StarsDeviceSerialNumberAlreadyExistsException();
        }
    }
    
    private LiteStarsLMHardware getLmHardware(HardwareDto hardwareDto, int accountId, LiteStarsEnergyCompany energyCompany) {
        LiteStarsLMHardware lmHardware = new LiteStarsLMHardware();
        lmHardware.setAccountID(accountId);
        
        /* InventoryBase fields */
        int categoryId = getCategoryIdForTypeId(hardwareDto.getHardwareTypeEntryId(), energyCompany);
        lmHardware.setCategoryID(categoryId);
        lmHardware.setEnergyCompanyId(energyCompany.getEnergyCompanyId());
        
        /* InventoryBase fields from Dto*/
        setInventoryFieldsFromDto(lmHardware, hardwareDto);

        /* LMHardwareBase Fields */
        lmHardware.setManufacturerSerialNumber(hardwareDto.getSerialNumber());
        lmHardware.setLmHardwareTypeID(hardwareDto.getHardwareTypeEntryId());
        
        if(!hardwareDto.getHardwareType().isGateway()) {
            lmHardware.setRouteID(hardwareDto.getRouteId());
        }
        
        return lmHardware;
    }
    
    private LiteMeterHardwareBase getMeterHardware(HardwareDto hardwareDto, int accountId, LiteStarsEnergyCompany energyCompany) {
        LiteMeterHardwareBase meterHardware = new LiteMeterHardwareBase();
        meterHardware.setAccountID(accountId);
        
        /* InventoryBase fields */
        int categoryId = getCategoryIdForTypeId(hardwareDto.getHardwareTypeEntryId(), energyCompany);
        meterHardware.setCategoryID(categoryId);
        meterHardware.setEnergyCompanyId(energyCompany.getEnergyCompanyId());
        
        /* InventoryBase fields from Dto*/
        setInventoryFieldsFromDto(meterHardware, hardwareDto);

        /* LMHardwareBase Fields */
        meterHardware.setMeterNumber(hardwareDto.getMeterNumber());
        YukonListEntry typeEntry = energyCompany.getYukonListEntry(HardwareType.NON_YUKON_METER.getDefinitionId());
        meterHardware.setMeterTypeID(typeEntry.getEntryID());

        return meterHardware;
    }
    
    private LiteInventoryBase getInventory(HardwareDto hardwareDto, int accountId, LiteStarsEnergyCompany energyCompany) {
        LiteInventoryBase liteInventoryBase = new LiteInventoryBase();
        liteInventoryBase.setAccountID(accountId);
        
        /* InventoryBase fields */
        int categoryId = getCategoryIdForTypeId(hardwareDto.getHardwareTypeEntryId(), energyCompany);
        liteInventoryBase.setCategoryID(categoryId);
        liteInventoryBase.setEnergyCompanyId(energyCompany.getEnergyCompanyId());
        
        /* InventoryBase fields from Dto*/
        setInventoryFieldsFromDto(liteInventoryBase, hardwareDto);
        
        return liteInventoryBase;
    }
    
    /**
     * Populates LiteInventoryBase with update fields of the HardwareDto object.
     */
    private void setInventoryFieldsFromDto(LiteInventoryBase liteInventoryBase, HardwareDto hardwareDto) {
        liteInventoryBase.setDeviceLabel(hardwareDto.getDisplayLabel());
        liteInventoryBase.setAlternateTrackingNumber(hardwareDto.getAltTrackingNumber());
        
        if(hardwareDto.getVoltageEntryId() != null) {
            liteInventoryBase.setVoltageID(hardwareDto.getVoltageEntryId());
        }
        
        if(hardwareDto.getFieldInstallDate() != null) {
            liteInventoryBase.setInstallDate(hardwareDto.getFieldInstallDate().getTime());
        }
        
        if(hardwareDto.getFieldReceiveDate() != null) {
            liteInventoryBase.setReceiveDate(hardwareDto.getFieldReceiveDate().getTime());
        }
        
        if(hardwareDto.getFieldRemoveDate() != null) {
            liteInventoryBase.setRemoveDate(hardwareDto.getFieldRemoveDate().getTime());
        }
        
        liteInventoryBase.setNotes(hardwareDto.getDeviceNotes());
        
        if(hardwareDto.getDeviceStatusEntryId() != null) {
            liteInventoryBase.setCurrentStateID(hardwareDto.getDeviceStatusEntryId());
        }
        
        if(hardwareDto.getServiceCompanyId() != null) {
            liteInventoryBase.setInstallationCompanyID(hardwareDto.getServiceCompanyId());
        }
        
        if(hardwareDto.getDeviceId() != null) {
            liteInventoryBase.setDeviceID(hardwareDto.getDeviceId());
        }
    }
    
    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }
    
    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao){
        this.yukonListDao = yukonListDao;
    }
    
    @Autowired
    public void setHardwareEventLogService(HardwareEventLogService hardwareEventLogService) {
        this.hardwareEventLogService = hardwareEventLogService;
    }
    
    @Autowired
    public void setLMHardareBaseDao(LMHardwareBaseDao lmHardwareBaseDao){
        this.lmHardwareBaseDao = lmHardwareBaseDao;
    }
    
    @Autowired
    public void setLMHardwareEventDao(LMHardwareEventDao lmHardwareEventDao) {
        this.lmHardwareEventDao = lmHardwareEventDao;
    }
    
    @Autowired
    public void setLMCustomerEventBaseDao(LMCustomerEventBaseDao lmCustomerEventBaseDao) {
        this.lmCustomerEventBaseDao = lmCustomerEventBaseDao;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
   
    @Autowired
    public void setDeviceCreationService(DeviceCreationService deviceCreationService){
        this.deviceCreationService = deviceCreationService;
    }
    
    @Autowired
    public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
        this.starsSearchDao = starsSearchDao;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setWarehouseDao(WarehouseDao warehouseDao) {
        this.warehouseDao = warehouseDao;
    }
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }
    
    @Autowired
    public void setStarsInventoryBaseService(StarsInventoryBaseService starsInventoryBaseService) {
        this.starsInventoryBaseService = starsInventoryBaseService;
    }
    
    @Autowired
    public void setZigbeeDeviceService(ZigbeeDeviceService zigbeeDeviceService) {
        this.zigbeeDeviceService = zigbeeDeviceService;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}