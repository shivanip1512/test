package com.cannontech.web.stars.dr.operator.hardware.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.event.dao.LMCustomerEventBaseDao;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.model.CustomerAction;
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.hardware.model.InventoryCategory;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareClass;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareDto;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class HardwareServiceImpl implements HardwareService {

    private InventoryBaseDao inventoryBaseDao;
    private YukonListDao yukonListDao;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private LMHardwareEventDao lmHardwareEventDao;
    private PaoDao paoDao;
    private LMCustomerEventBaseDao lmCustomerEventBaseDao;
    private ApplianceDao applianceDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private EnrollmentHelperService enrollmentHelperService;
    private CustomerAccountDao customerAccountDao;
    private DeviceCreationService deviceCreationService;
    private StarsSearchDao starsSearchDao;
    private StarsDatabaseCache starsDatabaseCache;
    private WarehouseDao warehouseDao;
    private PaoLoadingService paoLoadingService;

    @Override
    public HardwareDto getHardwareDto(int inventoryId, int energyCompanyId) {
        HardwareDto hardwareDto = new HardwareDto();
        hardwareDto.setInventoryId(inventoryId);
        hardwareDto.setEnergyCompanyId(energyCompanyId);
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        
        /* Set Hardware Basics */
        InventoryBase inventoryBase = inventoryBaseDao.getById(inventoryId);
        
        int categoryId = inventoryBase.getCategoryId();
        YukonListEntry categoryNameYLE = yukonListDao.getYukonListEntry(categoryId);
        hardwareDto.setCategoryName(categoryNameYLE.getEntryText());
        
        hardwareDto.setDeviceId(inventoryBase.getDeviceId());
        hardwareDto.setDisplayLabel(inventoryBase.getDeviceLabel());
        hardwareDto.setAltTrackingNumber(inventoryBase.getAlternateTrackingNumber());
        hardwareDto.setVoltageEntryId(inventoryBase.getVoltageId());
        
        DateTime beginningOfJavaTime = new DateTime(0);
        DateTime installDT = new DateTime(inventoryBase.getInstallDate());
        if(installDT.isAfter(beginningOfJavaTime)){
            hardwareDto.setFieldInstallDate(installDT.toDate());
        } else {
            hardwareDto.setFieldInstallDate(null);
        }
        
        DateTime receiveDT = new DateTime(inventoryBase.getReceiveDate());
        if(receiveDT.isAfter(beginningOfJavaTime)){
            hardwareDto.setFieldReceiveDate(receiveDT.toDate());
        } else {
            hardwareDto.setFieldReceiveDate(null);
        }
        
        DateTime removeDT = new DateTime(inventoryBase.getRemoveDate());
        if(removeDT.isAfter(beginningOfJavaTime)){
            hardwareDto.setFieldRemoveDate(removeDT.toDate());
        } else {
            hardwareDto.setFieldRemoveDate(null);
        }
        
        hardwareDto.setDeviceNotes(inventoryBase.getNotes());
        hardwareDto.setDeviceStatusEntryId(inventoryBase.getCurrentStateId());
        hardwareDto.setOriginalDeviceStatusEntryId(inventoryBase.getCurrentStateId());
        hardwareDto.setServiceCompanyId(inventoryBase.getInstallationCompanyId());
        
        Warehouse warehouse = warehouseDao.findWarehouseForInventoryId(inventoryId);
        hardwareDto.setWarehouseId(warehouse == null ? 0 : warehouse.getWarehouseID());
        
        List<LiteLMHardwareEvent> events = lmHardwareEventDao.getByInventoryId(inventoryBase.getInventoryId());
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
        InventoryCategory category = InventoryCategory.valueOf(categoryEntry.getYukonDefID());
        
        if(category == InventoryCategory.MCT) {
            /* Hardware is an MCT */
            hardwareDto.setMct(true);
            
            if(inventoryBase.getDeviceId() > 0) {
                /* The device id has been set to a real MCT. */
                YukonPao pao = paoDao.getYukonPao(inventoryBase.getDeviceId());
                DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(pao);
                hardwareDto.setDisplayType(displayablePao.getPaoIdentifier().getPaoType().getPaoTypeName());
                hardwareDto.setDisplayName(displayablePao.getName());
            } else {
                /* Not attached to a real MCT yet. Use label for name if you can */
                /* and use the MCT list enty of the energy company as the device type. */
                YukonListEntry mctDeviceType = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT);
                hardwareDto.setDisplayType(mctDeviceType.getEntryText());
                if(StringUtils.isNotBlank(inventoryBase.getDeviceLabel())){
                    hardwareDto.setDisplayName(inventoryBase.getDeviceLabel());
                } else {
                    hardwareDto.setDisplayName(CtiUtilities.STRING_NONE);
                }
            }
        } else if(category == InventoryCategory.ONE_WAY_RECEIVER || category == InventoryCategory.TWO_WAY_RECEIVER) {
            /* Must be a switch or thermostat. */
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryBase.getInventoryId());
            
            YukonListEntry hardwareTypeEntry = yukonListDao.getYukonListEntry(lmHardwareBase.getLMHarewareTypeId());
            HardwareType hardwareType = HardwareType.valueOf(hardwareTypeEntry.getYukonDefID());
            
            YukonListEntry deviceTypeEntry = yukonListDao.getYukonListEntry(lmHardwareBase.getLMHarewareTypeId());
            hardwareDto.setDisplayType(deviceTypeEntry.getEntryText());
            hardwareDto.setSerialNumber(lmHardwareBase.getManufacturerSerialNumber());
            hardwareDto.setDisplayName(lmHardwareBase.getManufacturerSerialNumber());
            hardwareDto.setRouteId(lmHardwareBase.getRouteId());
            
            hardwareDto.setThermostat(hardwareType.isThermostat());
            
            /* Two Way LCR's */
            if (hardwareType.isSwitch() && hardwareType.isTwoWay()) {
                hardwareDto.setTwoWayLcr(true);
                if(inventoryBase.getDeviceId() > 0){
                    YukonPao pao = paoDao.getYukonPao(inventoryBase.getDeviceId());
                    DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(pao);
                    hardwareDto.setTwoWayDeviceName(displayablePao.getName());
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
            HardwareDto hardwareDto = getHardwareDto(inventoryId, energyCompanyId);
            if(hardwareDto.isMct()) {
                hardwareMap.put(LMHardwareClass.METER, hardwareDto);
            } else if (hardwareDto.isThermostat()) {
                hardwareMap.put(LMHardwareClass.THERMOSTAT, hardwareDto);
            } else {
                hardwareMap.put(LMHardwareClass.SWITCH, hardwareDto);
            }
        }
        
        return hardwareMap;
    }
    
    @Override
    public SimpleDevice createTwoWayDevice(int inventoryId, String deviceName) throws StarsTwoWayLcrYukonDeviceCreationException {
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
            yukonDevice = deviceCreationService.createDeviceByDeviceType(yukonDeviceTypeId, deviceName, serialNumber , lmHardwareBase.getRouteId(), true);
        } catch (DeviceCreationException e) {
            throw new StarsTwoWayLcrYukonDeviceCreationException("unknown", e);
        } catch (NumberFormatException nfe){
            throw new StarsTwoWayLcrYukonDeviceCreationException("nonNumericSerialNumber", nfe);
        }
        
        return yukonDevice;
    }
    
    @Override
    @Transactional
    public boolean updateHardware(HardwareDto hardwareDto) {
        InventoryBase inventoryBase = inventoryBaseDao.getById(hardwareDto.getInventoryId());
        setInventoryFieldsFromDto(inventoryBase, hardwareDto);
        
        try {
            LiteInventoryBase liteInventoryBase = starsSearchDao.searchLMHardwareBySerialNumber(hardwareDto.getSerialNumber(), hardwareDto.getEnergyCompanyId());
            if(liteInventoryBase != null && liteInventoryBase.getInventoryID() != hardwareDto.getInventoryId()){
                throw new StarsDeviceSerialNumberAlreadyExistsException();
            }
        } catch (ObjectInOtherEnergyCompanyException e) {/*Won't Happen*/}
        
        /* Update InventoryBase */
        inventoryBaseDao.update(inventoryBase);
        
        if(!hardwareDto.isMct()){
            /* For switches and thermostats update serial number and route. */
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryBase.getInventoryId());

            /* Update Route */
            lmHardwareBase.setRouteId(hardwareDto.getRouteId());
            
            /* Update Serial Number */
            lmHardwareBase.setManufactoruerSerialNumber(hardwareDto.getSerialNumber());
            
            lmHardwareBaseDao.update(lmHardwareBase);
        }
        
        /* Update warehouse mapping */
        warehouseDao.moveInventoryToAnotherWarehouse(hardwareDto.getInventoryId(), hardwareDto.getWarehouseId());
        
        /* Update the installation notes */
        List<LiteLMHardwareEvent> events = lmHardwareEventDao.getByInventoryId(inventoryBase.getInventoryId());
        for(LiteLMHardwareEvent event : events) {
            YukonListEntry entry = yukonListDao.getYukonListEntry(event.getActionID());
            if(entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL) {
                /* The list of events is retrieved newest to oldest 
                 * so the first install in the list will be the most recent */
                lmCustomerEventBaseDao.updateNotesForEvent(event.getEventID(), hardwareDto.getFieldInstallDate(), hardwareDto.getInstallNotes());
            }
        }
        
        /* Tell controller to spawn event for device status change if necessary */
        if(inventoryBase.getCurrentStateId() != hardwareDto.getOriginalDeviceStatusEntryId()){
            return true;
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
            
            LiteInventoryBase inventory = starsInventoryBaseDao.getByInventoryId(inventoryId);
            if(inventory.getAccountID() != accountId) {
                throw new NotAuthorizedException("The Inventory with id: " + inventoryId + " does not belong to the current customer account with id: " + accountId);
            }
        }
    }
    
    /* History Wrapper */
    public static class HardwareHistory {
        private String action;
        private Date date;
        
        public String getAction() {
            return action;
        }
        
        public void setAction(String action) {
            this.action = action;
        }
        
        public Date getDate() {
            return date;
        }
        
        public void setDate(Date date) {
            this.date = date;
        }
    }
    
    /* HELPERS */
    
    /**
     * Populates InventoryBase with update fields of the HardwareDto object.
     */
    private void setInventoryFieldsFromDto(InventoryBase inventoryBase, HardwareDto hardwareDto) {
        inventoryBase.setDeviceLabel(hardwareDto.getDisplayLabel());
        inventoryBase.setAlternateTrackingNumber(hardwareDto.getAltTrackingNumber());
        inventoryBase.setVoltageId(hardwareDto.getVoltageEntryId());
        
        if(hardwareDto.getFieldInstallDate() != null) {
            inventoryBase.setInstallDate(new Timestamp(hardwareDto.getFieldInstallDate().getTime()));
        }
        
        if(hardwareDto.getFieldReceiveDate() != null) {
            inventoryBase.setReceiveDate(new Timestamp(hardwareDto.getFieldReceiveDate().getTime()));
        }
        
        if(hardwareDto.getFieldRemoveDate() != null) {
            inventoryBase.setRemoveDate(new Timestamp(hardwareDto.getFieldRemoveDate().getTime()));
        }
        
        inventoryBase.setNotes(hardwareDto.getDeviceNotes());
        inventoryBase.setCurrentStateId(hardwareDto.getDeviceStatusEntryId());
        inventoryBase.setInstallationCompanyId(hardwareDto.getServiceCompanyId());
        inventoryBase.setDeviceId(hardwareDto.getDeviceId());
    }
    
    @Override
    @Transactional
    public void deleteHardware(boolean delete, int inventoryId, int accountId, LiteStarsEnergyCompany energyCompany) throws Exception {
        LiteInventoryBase liteInv = starsInventoryBaseDao.getByInventoryId(inventoryId);
        CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        boolean deleteMCT = false;
        
        /* Unenroll the hardware */
        try {
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
            
            EnrollmentHelper enrollmentHelper = new EnrollmentHelper();
            enrollmentHelper.setAccountNumber(customerAccount.getAccountNumber());
            enrollmentHelper.setSerialNumber(lmHardwareBase.getManufacturerSerialNumber());
            
            enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.UNENROLL, energyCompany.getUser());
            
        } catch (NotFoundException ignore) {
            /* Ignore this if we are not an LMHardwareBase such as mct's */
            deleteMCT = true;
        }
        
        if (delete) {
            /* Delete this hardware from the database */
            InventoryManagerUtil.deleteInventory( liteInv, energyCompany, deleteMCT);
        } else {
            /* Just remove it from the account and put it back in general inventory */
            Date removeDate = new Date();
            int hwEventEntryId = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
            int uninstallActionId = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_UNINSTALL ).getEntryID();
            
            /* Add an uninstall event for this hardware */
            LiteLMHardwareEvent liteLMHardwareEvent = new LiteLMHardwareEvent();
            liteLMHardwareEvent.setInventoryID(inventoryId);
            liteLMHardwareEvent.setActionID(uninstallActionId);
            liteLMHardwareEvent.setEventTypeID(hwEventEntryId);
            liteLMHardwareEvent.setEventDateTime(removeDate.getTime());
            
            liteLMHardwareEvent.setNotes( "Removed from account #" + customerAccount.getAccountNumber() );
            
            lmHardwareEventDao.add(liteLMHardwareEvent, energyCompany.getEnergyCompanyID());
            
            if (liteInv instanceof LiteStarsLMHardware) {
                applianceDao.deleteAppliancesByAccountIdAndInventoryId(accountId, inventoryId);
            }
            
            /* Removes any entries found in the InventoryBase */
            InventoryBase inventoryBase =  inventoryBaseDao.getById(inventoryId);
            inventoryBase.setAccountId(CtiUtilities.NONE_ZERO_ID);
            inventoryBase.setRemoveDate(new Timestamp(removeDate.getTime()));
            inventoryBase.setDeviceLabel("");
            inventoryBaseDao.update(inventoryBase);
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
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }
    
    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
    
    @Autowired
    public void setEnrollmentService(EnrollmentHelperService enrollmentHelperService) {
        this.enrollmentHelperService = enrollmentHelperService;
    }
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
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
}