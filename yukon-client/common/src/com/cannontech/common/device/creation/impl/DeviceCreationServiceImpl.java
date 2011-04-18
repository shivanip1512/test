package com.cannontech.common.device.creation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

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
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.core.dao.DBPersistentDao;
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
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.device.range.PlcAddressRangeService;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public class DeviceCreationServiceImpl implements DeviceCreationService {

    private DeviceDao deviceDao = null;
    private PaoDao paoDao = null;
    private PointDao pointDao = null;
    private PaoGroupsWrapper paoGroupsWrapper = null;
    private DBPersistentDao dbPersistentDao = null;
    private DeviceGroupEditorDao deviceGroupEditorDao = null;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private PaoDefinitionService paoDefinitionService = null;
    private PlcAddressRangeService plcAddressRangeService;
    
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

        // COPY POINTS
        if (copyPoints) {

            List<PointBase> points = this.getPointsForPao(templateDeviceId);
            this.applyPoints(newDevice, points);
        }
        // db change msg.  Process Device dbChange AFTER device AND points have been inserted into DB.
        processDeviceDbChange(newDevice);

        // MAKE new, template YukonDevice
        newYukonDevice.setDeviceId(newDeviceId);
        newYukonDevice.setType(paoGroupsWrapper.getDeviceType(newDevice.getPAOType()));

        SimpleDevice templateYukonDevice = new SimpleDevice();
        templateYukonDevice.setDeviceId(templateDeviceId);
        templateYukonDevice.setType(paoGroupsWrapper.getDeviceType(templateDevice.getPAOType()));

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
            
            // db change msg
            processDeviceDbChange(newDevice);
            
            // MAKE YukonDevice
            yukonDevice.setDeviceId(newDeviceId);
            yukonDevice.setType(deviceType);
            
            // CREATE POINTS
            if (createPoints) {
                List<PointBase> points = paoDefinitionService.createDefaultPointsForPao(yukonDevice);
                this.applyPoints(newDevice, points);
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
    
    private void applyPoints(DeviceBase device, List<PointBase> points) {
        
        int deviceId = device.getPAObjectID();
        
        MultiDBPersistent pointsToAdd = new MultiDBPersistent();
        Vector<DBPersistent> newPoints = pointsToAdd.getDBPersistentVector();

        for (PointBase point : points) {
        
            int nextPointId = pointDao.getNextPointId();
            point.setPointID(nextPointId);
            point.getPoint().setPaoID(deviceId);
            
            newPoints.add(point);
        }
        
        // insert
        dbPersistentDao.performDBChangeWithNoMsg(pointsToAdd, TransactionType.INSERT);
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

    private void processDeviceDbChange(DeviceBase newDevice) {

        DBChangeMsg msg = new DBChangeMsg(newDevice.getPAObjectID(),
                                          DBChangeMsg.CHANGE_PAO_DB,
                                          PAOGroups.STRING_CAT_DEVICE,
                                          newDevice.getPAOType(),
                                          DbChangeType.ADD );
        dbPersistentDao.processDBChange(msg);
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
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }
    
    @Required
    public void setDbPersistantDao(DBPersistentDao dbPersistantDao) {
        this.dbPersistentDao = dbPersistantDao;
    }
    
    @Required
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    
    @Required
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Required
    public void setPaoDefinitionService(PaoDefinitionService paoDefinitionService) {
        this.paoDefinitionService = paoDefinitionService;
    }
    
    @Autowired
    public void setPlcAddressRangeService(PlcAddressRangeService plcAddressRangeService) {
        this.plcAddressRangeService = plcAddressRangeService;
    }
    
    
}
