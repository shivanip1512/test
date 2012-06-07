package com.cannontech.common.device.creation.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.constants.YukonDefinition;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.device.creation.BadTemplateDeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.RfnBase;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.device.range.DlcAddressRangeService;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public class DeviceCreationServiceImpl implements DeviceCreationService {

    @Autowired private DeviceDao deviceDao = null;
    @Autowired private PaoDao paoDao = null;
    @Autowired private PointDao pointDao = null;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao = null;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DlcAddressRangeService dlcAddressRangeService;
    @Autowired private PaoCreationHelper paoCreationHelper;
    @Autowired private DBPersistentDao dbPersistentDao = null;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private YukonListDao yukonListDao;
    
    @Transactional
    public SimpleDevice createDeviceByTemplate(String templateName, String newDeviceName, boolean copyPoints) {

        //get template
        DeviceBase templateDevice = retrieveExistingDeviceByTemplate(templateName);
        
        int templateDeviceId = templateDevice.getPAObjectID();
        PaoType paoType = PaoType.getForDbString(templateDevice.getPAOType());
        PaoIdentifier templateIdentifier = new PaoIdentifier(templateDeviceId, paoType);
        
        //copy device from a template
        DeviceBase newDevice = buildNewDeviceFromTemplate(templateDevice,newDeviceName);

        //create new device
        SimpleDevice newYukonDevice = createNewDeviceByTemplate(newDevice, templateIdentifier, templateName, copyPoints);

        return newYukonDevice;

    }
  
    @Transactional
    public SimpleDevice createRfnDeviceByTemplate(String templateName, String newDeviceName, String model, String manufacturer, String serialNumber, boolean copyPoints)
            throws DeviceCreationException, BadConfigurationException {
        
        //get template, validate that a template is an RFN Template
        DeviceBase templateDevice = retrieveExistingDeviceByTemplate(templateName);
        
        
        int templateDeviceId = templateDevice.getPAObjectID();
        PaoType paoType = PaoType.getForDbString(templateDevice.getPAOType());
        PaoIdentifier templateIdentifier = new PaoIdentifier(templateDeviceId, paoType);
        
        if (PaoClass.getForDbString(templateDevice.getPAOClass()) != PaoClass.RFMESH) {
            throw new DeviceCreationException(String.format("Could not create new device named '%s' from template '%s'. Template '%s' must be an RFN Device",
                                                            newDeviceName,
                                                            templateName));
        }
        
        // copy device
        RfnBase newDevice = (RfnBase) buildNewDeviceFromTemplate(templateDevice,newDeviceName);
        
        // set RFN values
        newDevice.getRfnAddress().setSerialNumber(serialNumber);
        newDevice.getRfnAddress().setManufacturer(manufacturer);
        newDevice.getRfnAddress().setModel(model);
        
        // create new device
        SimpleDevice newYukonDevice = createNewDeviceByTemplate(newDevice, templateIdentifier, templateName, copyPoints);
        
        /** SUPER HACK: This is to initialize inventory tables (for now) for device types that need them (rf lcr's) */
        List<HardwareType> hardwareTypes = HardwareType.getForPaoType(newYukonDevice.getDeviceType());
        if (!CollectionUtils.isEmpty(hardwareTypes) && hardwareTypes.size() == 1) {
            String ecName = configurationSource.getString("RFN_ENERGY_COMPANY_NAME");
            if (StringUtils.isEmpty(ecName)) {
                throw new BadConfigurationException("RF Yukon systems with DR devices are required to specify the RFN_ENERGY_COMPANY_NAME configuration property in master.cfg");
            }
            final LiteEnergyCompany ec = energyCompanyDao.getEnergyCompanyByName(ecName);
            YukonEnergyCompany yec = new YukonEnergyCompany() {
                @Override
                public String getName() {
                    return ec.getName();
                }
                @Override
                public LiteYukonUser getEnergyCompanyUser() {
                    return null; // don't care
                }
                @Override
                public int getEnergyCompanyId() {
                    return ec.getEnergyCompanyID();
                }
                @Override
                public DateTimeZone getDefaultDateTimeZone() {
                    return null; // don't care
                }
            };
            
            HardwareType ht = hardwareTypes.get(0);
            Hardware h = new Hardware();
            h.setSerialNumber(serialNumber);
            h.setDeviceId(newYukonDevice.getDeviceId());
            h.setEnergyCompanyId(ec.getEnergyCompanyID());
            h.setFieldReceiveDate(new Date());
            
            List<YukonListEntry> hardwareTypeEntries = yukonListDao.getYukonListEntry(ht.getDefinitionId(), yec);
            h.setHardwareTypeEntryId(hardwareTypeEntries.get(0).getEntryID());
            
            List<YukonListEntry> statusTypeEntries = yukonListDao.getYukonListEntry(YukonDefinition.DEV_STAT_INSTALLED.getDefinitionId(), yec);
            h.setDeviceStatusEntryId(statusTypeEntries.get(0).getEntryID());
            
            List<YukonSelectionList> lists = yukonListDao.getSelectionListsByEnergyCompanyId(ec.getEnergyCompanyID());
            for (YukonSelectionList list : lists) {
                if (list.getListName() == YukonSelectionListEnum.DEVICE_VOLTAGE.getListName()) {
                    h.setVoltageEntryId(list.getYukonListEntries().get(0).getEntryID());
                }
            }
            
            /** END SUPER HACK */
        }

        return newYukonDevice;

    }
    
    @Transactional
    public SimpleDevice createCarrierDeviceByDeviceType(int deviceType, String name, int address, int routeId, boolean createPoints) throws DeviceCreationException {

        // test
        PaoType type = PaoType.getForId(deviceType);
        if (!dlcAddressRangeService.isEnforcedAddress(type, address)) {
            throw new DeviceCreationException("Invalid address: " + address + ".");
        }
        else if (StringUtils.isBlank(name)) {
            throw new DeviceCreationException("Device name is blank.");
        }

        // create
        int newDeviceId = paoDao.getNextPaoId();
        CarrierBase newDevice = (CarrierBase) DeviceFactory.createDevice(deviceType);
        newDevice.setDeviceID(newDeviceId);
        newDevice.setPAOName(name);
        newDevice.setAddress(address);
        newDevice.getDeviceRoutes().setRouteID(routeId);

        SimpleDevice yukonDevice = createNewDeviceByType(newDevice, createPoints, type);
        return yukonDevice;

    }

    @Transactional
    public SimpleDevice createRfnDeviceByDeviceType(PaoType type, String name, String model, String manufacturer, String serialNumber, boolean createPoints) throws DeviceCreationException {
        
        // verify that the device type is RFN device type
        if (type.getPaoClass() != PaoClass.RFMESH) {
            throw new DeviceCreationException(String.format("Could not create new device named '%s'. Device Type must be RFN Device Type.",  name));
        }
       
        // create device
        int newDeviceId = paoDao.getNextPaoId();
        RfnBase newDevice = (RfnBase) DeviceFactory.createDevice(type.getDeviceTypeId());
        newDevice.setDeviceID(newDeviceId);
        newDevice.setPAOName(name);
        newDevice.getRfnAddress().setSerialNumber(serialNumber);
        newDevice.getRfnAddress().setManufacturer(manufacturer);
        newDevice.getRfnAddress().setModel(model);

        SimpleDevice yukonDevice = createNewDeviceByType(newDevice, createPoints, type);
        return yukonDevice;
    }

    private SimpleDevice createNewDeviceByType(DeviceBase newDevice, boolean createPoints, PaoType type) {
        try {
            dbPersistentDao.performDBChangeWithNoMsg(newDevice, TransactionType.INSERT);

            // MAKE YukonDevice
            PaoIdentifier paoIdentifier =
                new PaoIdentifier(newDevice.getDevice().getDeviceID(), type);
            SimpleDevice yukonDevice = new SimpleDevice(paoIdentifier);

            // CREATE POINTS
            if (createPoints) {
                paoCreationHelper.addDefaultPointsToPao(yukonDevice);
            }
            // db change msg.  Process Device dbChange AFTER device AND points have been inserted into DB.
            paoCreationHelper.processDbChange(yukonDevice, DbChangeType.ADD);
            return yukonDevice;
        } catch (PersistenceException e) {
            throw new DeviceCreationException("Could not create new device.", e);
        }
    }

    private SimpleDevice createNewDeviceByTemplate(DeviceBase newDevice, PaoIdentifier templateIdentifier, String templateName,
                                                   boolean copyPoints) {
        try {
            dbPersistentDao.performDBChangeWithNoMsg(newDevice, TransactionType.INSERT);

            // MAKE new, template YukonDevice
            PaoType paoType = PaoType.getForDbString(newDevice.getPAOType());
            PaoIdentifier paoIdentifier =
                new PaoIdentifier(newDevice.getDevice().getDeviceID(), paoType);
            SimpleDevice newYukonDevice = new SimpleDevice(paoIdentifier);

            // COPY POINTS
            if (copyPoints) {
                List<PointBase> points = pointDao.getPointsForPao(templateIdentifier.getPaoId());
                paoCreationHelper.applyPoints(newYukonDevice, points);
            }

            // db change msg. Process Device dbChange AFTER device AND points have been inserted into DB.
            paoCreationHelper.processDbChange(newYukonDevice, DbChangeType.ADD);

            SimpleDevice templateYukonDevice = new SimpleDevice(templateIdentifier);

            // add to template's device groups
            addToTemplatesGroups(templateYukonDevice, newYukonDevice);
            return newYukonDevice;
        } catch (PersistenceException e) {
            throw new DeviceCreationException(String.format("Could not create new device named '%s' from template '%s'",
                                                            newDevice.getPAOName(),
                                                            templateName), e);
        }

    }
    
    private DeviceBase buildNewDeviceFromTemplate(DeviceBase templateDevice, String newDeviceName) {

        int newDeviceId = paoDao.getNextPaoId();
        DeviceBase newDevice = templateDevice;
        newDevice.setDeviceID(newDeviceId);
        newDevice.setPAOName(newDeviceName);
        return newDevice;

    }
    
    private DeviceBase retrieveExistingDeviceByTemplate(String templateName) {
        
        try {
            SimpleDevice templateYukonDevice = deviceDao.getYukonDeviceObjectByName(templateName);
            int templateDeviceId = templateYukonDevice.getDeviceId();

            DeviceBase templateDevice = DeviceFactory.createDevice(templateYukonDevice.getDeviceType());
            templateDevice.setDeviceID(templateDeviceId);
            dbPersistentDao.retrieveDBPersistent(templateDevice);

            return templateDevice;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new BadTemplateDeviceCreationException(templateName);
        }catch (PersistenceException e) {
            throw new DeviceCreationException("Could not load template device from database: " + templateName, e);
        }
        
    }
    
    private void addToTemplatesGroups(SimpleDevice templateDevice, SimpleDevice newDevice) {
        
        StoredDeviceGroup rootGroup = deviceGroupEditorDao.getRootGroup();
        Set<StoredDeviceGroup> templatesGroups = deviceGroupMemberEditorDao.getGroupMembership(rootGroup, templateDevice);
        
        for (StoredDeviceGroup templateGroup : templatesGroups) {
            deviceGroupMemberEditorDao.addDevices(templateGroup, newDevice);
        }
    }

}
