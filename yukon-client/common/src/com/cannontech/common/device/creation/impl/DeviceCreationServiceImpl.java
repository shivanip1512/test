package com.cannontech.common.device.creation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.creation.BadTemplateDeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.device.range.PlcAddressRangeService;
import com.cannontech.message.dispatch.message.DbChangeType;

public class DeviceCreationServiceImpl implements DeviceCreationService {

    private DeviceDao deviceDao = null;
    private PaoDao paoDao = null;
    private PointDao pointDao = null;
    private DeviceGroupEditorDao deviceGroupEditorDao = null;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private PlcAddressRangeService plcAddressRangeService;
    private PaoCreationHelper paoCreationHelper;
    
    @Transactional
    public SimpleDevice createDeviceByTemplate(String templateName, String newDeviceName, boolean copyPoints) {
        
        SimpleDevice newYukonDevice = new SimpleDevice();


        // CREATE NEW DEVICE
        DeviceBase templateDevice = getExistingDeviceByTemplate(templateName);
        int templateDeviceId = templateDevice.getPAObjectID();

        int newDeviceId = paoDao.getNextPaoId();
        DeviceBase newDevice = templateDevice;
        newDevice.setDeviceID(newDeviceId);
        newDevice.setPAOName(newDeviceName);

        try {
            Transaction.createTransaction(TransactionType.INSERT, newDevice).execute();
        } catch (TransactionException e) {
            throw new DeviceCreationException(String.format("Could not create new device named '%s' from template '%s'", newDeviceName, templateName), e);
        }

        // MAKE new, template YukonDevice
        newYukonDevice.setDeviceId(newDeviceId);
        newYukonDevice.setDeviceType(PaoType.getForDbString(newDevice.getPAOType()));

        // COPY POINTS
        if (copyPoints) {
            List<PointBase> points = this.getPointsForPao(templateDeviceId);
            paoCreationHelper.applyPoints(newYukonDevice, points);
        }
        
        // db change msg.  Process Device dbChange AFTER device AND points have been inserted into DB.
        paoCreationHelper.processDbChange(newYukonDevice, DbChangeType.ADD);

        SimpleDevice templateYukonDevice = new SimpleDevice();
        templateYukonDevice.setDeviceId(templateDeviceId);
        templateYukonDevice.setDeviceType(PaoType.getForDbString(templateDevice.getPAOType()));

        // add to template's device groups
        addToTemplatesGroups(templateYukonDevice, newYukonDevice);


        return newYukonDevice;
    }
    
    @Transactional
    public SimpleDevice createCarrierDeviceByDeviceType(int deviceType, String name, int address, int routeId, boolean createPoints) throws DeviceCreationException {

        SimpleDevice yukonDevice = new SimpleDevice();

        try {
            
            // test
            PaoType type = PaoType.getForId(deviceType);   
            if (!plcAddressRangeService.isValidAddress(type, address)) {
                throw new DeviceCreationException("Invalid address: " + address + ".");
            }
            else if (StringUtils.isBlank(name)) {
                throw new DeviceCreationException("Device name is blank.");
            }

            // create
            int newDeviceId = paoDao.getNextPaoId();
            CarrierBase newDevice = (CarrierBase)DeviceFactory.createDevice(deviceType);
            newDevice.setDeviceID(newDeviceId);
            newDevice.setPAOName(name);
            newDevice.setAddress(address);
            newDevice.getDeviceRoutes().setRouteID(routeId);

            Transaction.createTransaction(TransactionType.INSERT, newDevice).execute();
            
            // MAKE YukonDevice
            yukonDevice.setDeviceId(newDeviceId);
            yukonDevice.setType(deviceType);

            // db change msg
            paoCreationHelper.processDbChange(yukonDevice, DbChangeType.ADD);
            
            // CREATE POINTS
            if (createPoints) {
                paoCreationHelper.addDefaultPointsToPao(yukonDevice);
            }
            
        }
        catch (TransactionException e) {
            throw new DeviceCreationException("Could not create new device.", e);
        }
        
        return yukonDevice;
    }
    
    private DeviceBase getExistingDeviceByTemplate(String templateName) {
        
        
        try {
            SimpleDevice templateYukonDevice = deviceDao.getYukonDeviceObjectByName(templateName);
            int templateDeviceId = templateYukonDevice.getDeviceId();

            DeviceBase templateDevice = DeviceFactory.createDevice(templateYukonDevice.getDeviceType());
            templateDevice.setDeviceID(templateDeviceId);
            templateDevice = Transaction.createTransaction(TransactionType.RETRIEVE, templateDevice).execute();

            return templateDevice;
        }
        catch (IncorrectResultSizeDataAccessException e) {
            throw new BadTemplateDeviceCreationException(templateName);
        }
        catch (TransactionException e) {
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

    private List<PointBase> getPointsForPao(int id) {
        
        List<LitePoint> litePoints = pointDao.getLitePointsByPaObjectId(id);
        List<PointBase> points = new ArrayList<PointBase>(litePoints.size());
        
        for (LitePoint litePoint: litePoints) {
            
            PointBase pointBase = (PointBase)LiteFactory.createDBPersistent(litePoint);
            
            try {
                Transaction.createTransaction(TransactionType.RETRIEVE, pointBase).execute();
                points.add(pointBase);
            }
            catch (TransactionException e) {
                throw new DeviceCreationException("Could not retrieve points for new device.", e);
            }
        }

        return points;
    }

    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Required
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Required
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    
    @Required
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Autowired
    public void setPlcAddressRangeService(PlcAddressRangeService plcAddressRangeService) {
        this.plcAddressRangeService = plcAddressRangeService;
    }
    
    @Autowired
    public void setPaoCreationHelper(PaoCreationHelper paoCreationHelper) {
        this.paoCreationHelper = paoCreationHelper;
    }
}
