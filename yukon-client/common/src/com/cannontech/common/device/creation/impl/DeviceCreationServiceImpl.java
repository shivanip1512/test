package com.cannontech.common.device.creation.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;

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
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.RfnBase;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.device.range.DlcAddressRangeService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;

public class DeviceCreationServiceImpl implements DeviceCreationService {

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
        PaoType paoType = PaoType.getForDbString(templateDevice.getPAOType());
        PaoIdentifier templateIdentifier = new PaoIdentifier(templateDeviceId, paoType);
        
        //copy device from a template
        DeviceBase newDevice = buildNewDeviceFromTemplate(templateDevice,newDeviceName);

        //create new device
        SimpleDevice newYukonDevice = createNewDeviceByTemplate(newDevice, templateIdentifier, templateName, copyPoints);

        return newYukonDevice;

    }
  
    @Override
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
        
        return newYukonDevice;
    }
    
    @Override
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

    @Override
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
            dbChangeManager.processPaoDbChange(yukonDevice, DbChangeType.ADD);
            
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
            dbChangeManager.processPaoDbChange(newYukonDevice, DbChangeType.ADD);

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
