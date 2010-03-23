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
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.device.TwoWayLCR;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
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
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.util.InventoryUtils;
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
    private DBPersistentDao dbPersistentDao;
    private StarsSearchDao starsSearchDao;
    private StarsDatabaseCache starsDatabaseCache;

    @Override
    public HardwareDto getHardwareDto(int inventoryId, int energyCompanyId) {
        HardwareDto hardwareDto = new HardwareDto();
        hardwareDto.setInventoryId(inventoryId);
        hardwareDto.setEnergyCompanyId(energyCompanyId);
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        
        /* Set Hardware Basics */
        InventoryBase inventoryBase = inventoryBaseDao.getById(inventoryId);
        int categoryId = inventoryBase.getCategoryId();
        YukonListEntry categoryEntry = yukonListDao.getYukonListEntry(categoryId);
        
        hardwareDto.setDeviceId(inventoryBase.getDeviceId());
        hardwareDto.setDeviceLabel(inventoryBase.getDeviceLabel());
        hardwareDto.setAltTrackingNumber(inventoryBase.getAlternateTrackingNumber());
        hardwareDto.setVoltageEntryId(inventoryBase.getVoltageId());
        
        DateTime beginningOfJavaTime = new DateTime().withDate(1970, 1, 1);
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
        hardwareDto.setWarehouseId(Warehouse.getWarehouseFromInventoryID(inventoryBase.getInventoryId()).getWarehouseID());
        List<LiteLMHardwareEvent> events = lmHardwareEventDao.getByInventoryId(inventoryBase.getInventoryId());
        String installNotes = null;
        for(LiteLMHardwareEvent event : events) {
            YukonListEntry entry = yukonListDao.getYukonListEntry(event.getActionID());
            if(StringUtils.isBlank(installNotes)){
                /* The list of events is retrieved newest to oldest 
                 * so the first install in the list will be the most recent */
                if(entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL) {
                    installNotes = event.getNotes();
                    break;
                }
            }
        }
        hardwareDto.setInstallNotes(installNotes);
        
        /* Set Hardware Details based on type(Switch, Thermostat, or MCT */
        try {
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryBase.getInventoryId());
            /* Must be a switch or thermostat. */
            YukonListEntry deviceTypeEntry = yukonListDao.getYukonListEntry(lmHardwareBase.getLMHarewareTypeId());
            hardwareDto.setDeviceType(deviceTypeEntry.getEntryText());
            hardwareDto.setSerialNumber(lmHardwareBase.getManufacturerSerialNumber());
            hardwareDto.setDeviceName(lmHardwareBase.getManufacturerSerialNumber());
            hardwareDto.setRouteId(lmHardwareBase.getRouteId());
            
            boolean isThermostat = isThermostat(categoryId, lmHardwareBase.getLMHarewareTypeId());
            hardwareDto.setIsThermostat(isThermostat);
            
            /* Two Way LCR's */
            if (!hardwareDto.getIsThermostat() && InventoryUtils.isTwoWayLcr(deviceTypeEntry.getEntryID())) {
                hardwareDto.setIsTwoWayLcr(true);
                if(inventoryBase.getDeviceId() > 0){
                    LiteYukonPAObject pao = paoDao.getLiteYukonPAO(inventoryBase.getDeviceId());
                    hardwareDto.setTwoWayDeviceName(pao.getPaoName());
                } else {
                    hardwareDto.setTwoWayDeviceName("(none chosen)");
                }
            }
        } catch (NotFoundException e) {
            /* Not a thermostat or switch */
            if(categoryEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT) {
                /* Hardware is an MCT */
                hardwareDto.setIsMct(true);
                
                if(inventoryBase.getDeviceId() > 0) {
                    /* The device id has been set to a real MCT. */
                    LiteYukonPAObject pao =  paoDao.getLiteYukonPAO(inventoryBase.getDeviceId());
                    PaoType deviceType = PaoType.getForId(pao.getType());
                    hardwareDto.setDeviceType(deviceType.getPaoTypeName());
                    hardwareDto.setDeviceName(pao.getPaoName());
                } else {
                    /* Not attached to a real MCT yet. Use label for name if you can */
                    /* and use the MCT list enty of the energy company as the device type. */
                    YukonListEntry mctDeviceType = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT);
                    hardwareDto.setDeviceType(mctDeviceType.getEntryText());
                    if(StringUtils.isNotBlank(inventoryBase.getDeviceLabel())){
                        hardwareDto.setDeviceName(inventoryBase.getDeviceLabel());
                    } else {
                        hardwareDto.setDeviceName(CtiUtilities.STRING_NONE);
                    }
                }
            } else {
                /* This is not a device we know about, maybe we should throw something here. */
            }
        }
        
        return hardwareDto;
    }

    @Override
    public ListMultimap<String, HardwareDto> getHardwareMapForAccount(int accountId, int energyCompanyId) {
        ListMultimap<String, HardwareDto> hardwareMap = ArrayListMultimap.create();
        
        List<Integer> inventoryIds = inventoryBaseDao.getInventoryIdsByAccountId(accountId);
        for(int inventoryId : inventoryIds) {
            HardwareDto hardwareDto = getHardwareDto(inventoryId, energyCompanyId);
            if(hardwareDto.getIsMct()) {
                hardwareMap.put("meters", hardwareDto);
            } else if (hardwareDto.getIsThermostat()) {
                hardwareMap.put("thermostats", hardwareDto);
            } else {
                hardwareMap.put("switches", hardwareDto);
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
        
        YukonListDao yukonListDao = YukonSpringHook.getBean("yukonListDao", YukonListDao.class);
        YukonListEntry entry = yukonListDao.getYukonListEntry(deviceTypeEntry.getEntryID());
        String deviceTypeName = entry.getEntryText();
        int yukonDeviceTypeId = PAOGroups.getDeviceType(deviceTypeName);
        
        SimpleDevice yukonDevice = null;
        try {
            int serialNumber = Integer.parseInt(lmHardwareBase.getManufacturerSerialNumber());
            yukonDevice = deviceCreationService.createDeviceByDeviceType(yukonDeviceTypeId, deviceName, serialNumber , lmHardwareBase.getRouteId(), true);
            
            // set demand rate on new device
            LiteYukonPAObject paoDevice = paoDao.getLiteYukonPAO(yukonDevice.getDeviceId());
            YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(paoDevice);
            DeviceLoadProfile deviceLoadProfile = ((TwoWayLCR)yukonPaobject).getDeviceLoadProfile();
            deviceLoadProfile.setLastIntervalDemandRate(300);
            dbPersistentDao.performDBChange(yukonPaobject, DBChangeMsg.CHANGE_TYPE_UPDATE);
            
        } catch (DeviceCreationException e) {
            throw new StarsTwoWayLcrYukonDeviceCreationException("unknown", e);
        } catch (NumberFormatException nfe){
            throw new StarsTwoWayLcrYukonDeviceCreationException("unknown", nfe);
        }
        
        return yukonDevice;
    }
    
    @Override
    @Transactional
    public boolean updateHardware(HardwareDto hardwareDto) {
        InventoryBase inventoryBase = inventoryBaseDao.getById(hardwareDto.getInventoryId());
        setInventoryFieldsFromDTO(inventoryBase, hardwareDto);
        
        try {
            LiteInventoryBase liteInventoryBase = starsSearchDao.searchLMHardwareBySerialNumber(hardwareDto.getSerialNumber(), hardwareDto.getEnergyCompanyId());
            if(liteInventoryBase != null && liteInventoryBase.getInventoryID() != hardwareDto.getInventoryId()){
                throw new StarsDeviceSerialNumberAlreadyExistsException();
            }
        } catch (ObjectInOtherEnergyCompanyException e) {/*Won't Happen*/}
        
        /* Update InventoryBase */
        inventoryBaseDao.update(inventoryBase);
        
        
        /* Update route */
        if(hardwareDto.getRouteId() != null) {
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryBase.getInventoryId());
            lmHardwareBase.setRouteId(hardwareDto.getRouteId());
            lmHardwareBaseDao.update(lmHardwareBase);
        }
        
        /* Update warehouse mapping */
        Warehouse.moveInventoryToAnotherWarehouse(hardwareDto.getInventoryId(), hardwareDto.getWarehouseId());
        
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
    private boolean isThermostat(int categoryId, int lmHarewareTypeId) {
        /* There must be a better way to do this */
        YukonListEntry invCatEntry = yukonListDao.getYukonListEntry(categoryId);
        YukonListEntry devTypeEntry = yukonListDao.getYukonListEntry(lmHarewareTypeId);
        
        if (invCatEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC &&
            (devTypeEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT ||
            devTypeEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT ||
            devTypeEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP ||
            devTypeEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_UTILITYPRO)) {
            return true;
        } else if (invCatEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC &&
            devTypeEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Populates InventoryBase with update fields of the HardwareDto object.
     */
    private void setInventoryFieldsFromDTO(InventoryBase inventoryBase, HardwareDto hardwareDto) {
        inventoryBase.setDeviceLabel(hardwareDto.getDeviceLabel());
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
    public void setDBPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
    @Autowired
    public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
        this.starsSearchDao = starsSearchDao;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
}