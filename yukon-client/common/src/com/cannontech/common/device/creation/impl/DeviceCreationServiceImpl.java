package com.cannontech.common.device.creation.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.device.creation.BadTemplateDeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.IEDBase;
import com.cannontech.database.data.device.RfnBase;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.device.range.DlcAddressRangeService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;

public class DeviceCreationServiceImpl implements DeviceCreationService {

    private static final Logger log = YukonLogManager.getLogger(DeviceCreationServiceImpl.class);
    
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DlcAddressRangeService dlcAddressRangeService;
    @Autowired private PaoCreationHelper paoCreationHelper;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private DbChangeManager dbChangeManager;
    
    @Override
    @Transactional
    public SimpleDevice createDeviceByTemplate(String templateName, String newDeviceName, boolean copyPoints) {

        //get template
        DeviceBase templateDevice = retrieveExistingDeviceByTemplate(templateName);
        
        int templateDeviceId = templateDevice.getPAObjectID();
        PaoType paoType = templateDevice.getPaoType();
        PaoIdentifier templateIdentifier = new PaoIdentifier(templateDeviceId, paoType);
        
        //copy device from a template
        DeviceBase newDevice = buildNewDeviceFromTemplate(templateDevice,newDeviceName);

        //create new device
        SimpleDevice newYukonDevice = createNewDeviceByTemplate(newDevice, templateIdentifier, templateName, copyPoints);

        return newYukonDevice;

    }
  
    @Override
    @Transactional
    public SimpleDevice createRfnDeviceByTemplate(String templateName, String newDeviceName,
            RfnIdentifier rfnIdentifier, boolean copyPoints)
            throws DeviceCreationException, BadConfigurationException {
        
        //get template, validate that a template is an RFN Template
        DeviceBase templateDevice = retrieveExistingDeviceByTemplate(templateName);
        
        if (!rfnIdentifier.isValid()) {
            throw new DeviceCreationException("Serial Number, Manufacturer, and Model fields must all be empty or all be filled in.", "invalidRfnIdentifier");
        }
        int templateDeviceId = templateDevice.getPAObjectID();
        PaoType paoType = templateDevice.getPaoType();

        if ((!YukonValidationUtils.isRfnSerialNumberValid(rfnIdentifier.getSensorSerialNumber()))) {
            throw new DeviceCreationException("Device serial number must be alphanumeric and serial number length must be less than 30",
                                              "maxLength");

        }
        PaoIdentifier templateIdentifier = new PaoIdentifier(templateDeviceId, paoType);
        
        if (templateDevice.getPaoType().getPaoClass() != PaoClass.RFMESH) {
            throw new DeviceCreationException(String.format("Could not create new device named '%s' from template '%s'. Template must be an RFN Device",
                                                            newDeviceName,
                                                            templateName),
                                              "invalidRfnTemplate",
                                              newDeviceName,
                                              templateName);
        }
        
        // copy device
        RfnBase newDevice = (RfnBase) buildNewDeviceFromTemplate(templateDevice,newDeviceName);
        
        // set RFN values
        newDevice.getRfnAddress().setRfnIdentifier(rfnIdentifier);
        // create new device
        SimpleDevice newYukonDevice = createNewDeviceByTemplate(newDevice, templateIdentifier, templateName, copyPoints);
        
        return newYukonDevice;
    }
    
    @Override
    @Transactional
    public SimpleDevice createCarrierDeviceByDeviceType(PaoType paoType, String name, int address, int routeId, boolean createPoints) throws DeviceCreationException {

        // test
        if (!dlcAddressRangeService.isValidEnforcedAddress(paoType, address)) {
            throw new DeviceCreationException("Invalid address: " + address + ".");
        }
        else if (StringUtils.isBlank(name)) {
            throw new DeviceCreationException("Device name is blank.");
        }
        
        if (!(PaoUtils.isValidPaoName(name))) {
            throw new DeviceCreationException("Device name cannot include any of the following characters: / \\ ,\" ' |");
        }

        // create
        int newDeviceId = paoDao.getNextPaoId();
        CarrierBase newDevice = (CarrierBase) DeviceFactory.createDevice(paoType);
        newDevice.setDeviceID(newDeviceId);
        newDevice.setPAOName(name);
        newDevice.getDeviceCarrierSettings().setAddress(address);
        newDevice.getDeviceRoutes().setRouteID(routeId);
        
        SimpleDevice yukonDevice = createNewDeviceByType(newDevice, createPoints, paoType);
        return yukonDevice;

    }
    
    @Override
    @Transactional
    public SimpleDevice createDeviceByDeviceType(PaoType paoType, String name) throws DeviceCreationException {

        if (StringUtils.isBlank(name)) {
            throw new DeviceCreationException("Device name is blank.");
        }
        
        if (!(PaoUtils.isValidPaoName(name))) {
            throw new DeviceCreationException("Device name cannot include any of the following characters: / \\ ,\" ' |");
        }

        // create
        int newDeviceId = paoDao.getNextPaoId();
        DeviceBase newDevice = DeviceFactory.createDevice(paoType);
        newDevice.setDeviceID(newDeviceId);
        newDevice.setPAOName(name);
       
        SimpleDevice yukonDevice = createNewDeviceByType(newDevice, true, paoType);
        return yukonDevice;

    }

    @Override
    @Transactional
    public SimpleDevice createRfnDeviceByDeviceType(PaoType type, String name, RfnIdentifier rfId, boolean createPoints) throws DeviceCreationException {
        
        // verify that the device type is RFN device type
        if (type.getPaoClass() != PaoClass.RFMESH) {
            throw new DeviceCreationException(String.format("Could not create new device named '%s'. Device Type must be RFN Device Type.",  name), "invalidRfnDeviceType", name);
        }
        if (!rfId.isValid()) {
            throw new DeviceCreationException("Serial Number, Manufacturer, and Model fields must all be empty or all be filled in.", "invalidRfnIdentifier");
        }
        // create device
        int newDeviceId = paoDao.getNextPaoId();
        RfnBase newDevice = (RfnBase) DeviceFactory.createDevice(type);
        newDevice.setDeviceID(newDeviceId);       
        
        if (StringUtils.isBlank(name) || !(PaoUtils.isValidPaoName(name))) {
            throw new DeviceCreationException("Device name cannot be blank or include any of the following characters: / \\ ,\" ' |",
                                              "invalidChars");
        }

        if ((!YukonValidationUtils.isRfnSerialNumberValid(rfId.getSensorSerialNumber()))) {
            throw new DeviceCreationException("Device serial number must be alphanumeric and serial number length must be less than 30",
                                              "maxLength");

        }
        
        newDevice.setPAOName(name);
        newDevice.getRfnAddress().setRfnIdentifier(rfId);

        SimpleDevice yukonDevice = createNewDeviceByType(newDevice, createPoints, type);
        return yukonDevice;
    }

    @Override
    @Transactional
    public SimpleDevice createIEDDeviceByDeviceType(PaoType paoType, String name, int portId, boolean createPoints) throws DeviceCreationException {

        if (StringUtils.isBlank(name)) {
            throw new DeviceCreationException("Device name is blank.");
        }
        
        if (!(PaoUtils.isValidPaoName(name))) {
            throw new DeviceCreationException("Device name cannot include any of the following characters: / \\ ,\" ' |");
        }

        // create
        int newDeviceId = paoDao.getNextPaoId();
        IEDBase newDevice = (IEDBase) DeviceFactory.createDevice(paoType);
        newDevice.setDeviceID(newDeviceId);
        newDevice.setPAOName(name);        
        newDevice.setDeviceDirectCommSettings(new DeviceDirectCommSettings(newDeviceId, portId));
        newDevice.getDeviceIEDDefaults();
        SimpleDevice yukonDevice = createNewDeviceByType(newDevice, createPoints, paoType);
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
            dbChangeManager.processPaoDbChange(yukonDevice, DbChangeType.ADD);
            
            log.debug("Created device " + yukonDevice);

            return yukonDevice;
        } catch (PersistenceException e) {
            throw new DeviceCreationException("Could not create new device.", "invalidNewDevice", e);
        }
    }

    private SimpleDevice createNewDeviceByTemplate(DeviceBase newDevice, PaoIdentifier templateIdentifier, String templateName,
                                                   boolean copyPoints) {
        try {
            dbPersistentDao.performDBChangeWithNoMsg(newDevice, TransactionType.INSERT);

            // MAKE new, template YukonDevice
            PaoType paoType = newDevice.getPaoType();
            PaoIdentifier paoIdentifier =
                new PaoIdentifier(newDevice.getDevice().getDeviceID(), paoType);
            SimpleDevice newYukonDevice = new SimpleDevice(paoIdentifier);

            // COPY POINTS
            if (copyPoints) {
                List<PointBase> points = pointDao.getPointsForPao(templateIdentifier.getPaoId());
                paoCreationHelper.applyPoints(newYukonDevice, points);
            }

            // db change msg. Process Device dbChange AFTER device AND points have been inserted into DB.
            dbChangeManager.processPaoDbChange(newYukonDevice, DbChangeType.ADD);
            
            log.debug("Created device " + newYukonDevice);

            SimpleDevice templateYukonDevice = new SimpleDevice(templateIdentifier);

            // add to template's device groups
            addToTemplatesGroups(templateYukonDevice, newYukonDevice);
            return newYukonDevice;
        } catch (PersistenceException e) {
            throw new DeviceCreationException(String.format("Could not create new device named '%s' from template '%s', there is a database conflict with another device",
                                                            newDevice.getPAOName(),
                                                            templateName),
                                              "invalidTemplate",
                                              newDevice.getPAOName(),
                                              templateName,
                                              e);
        }

    }
    
    private DeviceBase buildNewDeviceFromTemplate(DeviceBase templateDevice, String newDeviceName) {

        int newDeviceId = paoDao.getNextPaoId();
        DeviceBase newDevice = templateDevice;
        newDevice.setDeviceID(newDeviceId);
        
        if (StringUtils.isBlank(newDeviceName) || !(PaoUtils.isValidPaoName(newDeviceName))) {
            throw new DeviceCreationException("Device name cannot be blank or include any of the following characters: / \\ ,\" ' |", "invalidChars");
        }
        
        newDevice.setPAOName(newDeviceName);
        return newDevice;

    }
    
    private DeviceBase retrieveExistingDeviceByTemplate(String templateName) {
        try {
            SimpleDevice templateYukonDevice = deviceDao.getYukonDeviceObjectByName(templateName);
            return retrieveExistingDeviceByTemplate(templateYukonDevice);

        } catch (IncorrectResultSizeDataAccessException e) {
            boolean disableTemplateAutoCreation =
                configurationSource.getBoolean(MasterConfigBoolean.DISABLE_RFN_TEMPLATE_AUTO_CREATION, false);
            if (!disableTemplateAutoCreation) {
                SimpleDevice templateYukonDevice = createDeviceForTemplate(templateName);
                if (templateYukonDevice != null) {
                    return retrieveExistingDeviceByTemplate(templateYukonDevice);
                }
            }
            throw new BadTemplateDeviceCreationException(templateName);
        } catch (PersistenceException e) {
            throw new DeviceCreationException("Could not load template device from database: " + templateName,
                                              "invalidTemplateDevice",
                                              templateName,
                                              e);
        }

    }
    
    /*
     * Validates template name and creates device for the template.
     */
    private SimpleDevice createDeviceForTemplate(String templateName) {

        log.info("Auto creating device for template: "+templateName);
        // Here assumption is that manufacturer name will not have "_"
        String[] parsedTemplateNm = StringUtils.split(templateName, "_", 3);
        List<String> parsedTemplateName = Arrays.asList(parsedTemplateNm);

        if (parsedTemplateName.size() == 3) {
            String prefix = parsedTemplateName.get(0) + "_";
            String templatePrefix = configurationSource.getString(MasterConfigString.RFN_METER_TEMPLATE_PREFIX,
                                                                  "*RfnTemplate_");
            if (prefix.equals(templatePrefix)) {
                RfnIdentifier templateWithNoSerialNumber = new RfnIdentifier(null,
                                                                             parsedTemplateName.get(1),
                                                                             parsedTemplateName.get(2));
                RfnManufacturerModel rfnModel = RfnManufacturerModel.of(templateWithNoSerialNumber);
                if (rfnModel != null) {
                    SimpleDevice yukonDevice = createRfnDeviceByDeviceType(rfnModel.getType(),
                                                                           templateName,
                                                                           RfnIdentifier.createBlank(),
                                                                           true);
                    log.info("Auto created device for template: "+templateName);
                    return yukonDevice;
                }
            }
        }
        return null;
    }

    /*
     * Retrieves existing template device
     */
    private DeviceBase retrieveExistingDeviceByTemplate(SimpleDevice templateYukonDevice) {
        int templateDeviceId = templateYukonDevice.getDeviceId();

        DeviceBase templateDevice = DeviceFactory.createDevice(templateYukonDevice.getDeviceType());
        templateDevice.setDeviceID(templateDeviceId);
        dbPersistentDao.retrieveDBPersistent(templateDevice);
        return templateDevice;

    }
    
    private void addToTemplatesGroups(SimpleDevice templateDevice, SimpleDevice newDevice) {
        
        StoredDeviceGroup rootGroup = deviceGroupEditorDao.getRootGroup();
        Set<StoredDeviceGroup> templatesGroups = deviceGroupMemberEditorDao.getGroupMembership(rootGroup, templateDevice);
        
        for (StoredDeviceGroup templateGroup : templatesGroups) {
            deviceGroupMemberEditorDao.addDevices(templateGroup, newDevice);
        }
    }

}
