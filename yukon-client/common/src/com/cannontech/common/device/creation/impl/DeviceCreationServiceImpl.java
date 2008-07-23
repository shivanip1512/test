package com.cannontech.common.device.creation.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.definition.service.DeviceDefinitionService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.Point;
import com.cannontech.device.range.DeviceAddressRange;
import com.cannontech.message.dispatch.message.DBChangeMsg;

public class DeviceCreationServiceImpl implements DeviceCreationService {

    private DeviceDao deviceDao = null;
    private PaoDao paoDao = null;
    private PointDao pointDao = null;
    private PaoGroupsWrapper paoGroupsWrapper = null;
    private DeviceDefinitionService deviceDefinitionService = null;
    private DBPersistentDao dbPersistantDao = null;
    private DeviceGroupEditorDao deviceGroupEditorDao = null;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    
    @Transactional
    public YukonDevice createDeviceByTemplate(String templateName, String newDeviceName, boolean copyPoints) {
        
        YukonDevice newYukonDevice = new YukonDevice();
        YukonDevice templateYukonDevice = new YukonDevice();
        
        try {

            // CREATE NEW MCT DEVICE
            MCTBase templateDevice = getExistingDeviceByTemplate(templateName);
            int templateDeviceId = templateDevice.getPAObjectID();
            
            int newDeviceId = paoDao.getNextPaoId();
            MCTBase newDevice = templateDevice;
            newDevice.setDeviceID(newDeviceId);
            newDevice.setPAOName(newDeviceName);
            
            Transaction.createTransaction(Transaction.INSERT, newDevice).execute();
            
            // db change msg
            processDeviceDbChange(newDevice);

            // COPY POINTS
            if (copyPoints) {
                
                List<PointBase> points = this.getPointsForPao(templateDeviceId);
                this.applyPoints(newDevice, points);
            }
            
            // MAKE new, teamplate YukonDevice
            newYukonDevice.setDeviceId(newDeviceId);
            newYukonDevice.setType(paoGroupsWrapper.getDeviceType(newDevice.getPAOType()));
            
            templateYukonDevice.setDeviceId(templateDeviceId);
            templateYukonDevice.setType(paoGroupsWrapper.getDeviceType(templateDevice.getPAOType()));
            
            // add to template's device groups
            addToTemplatesGroups(templateYukonDevice, newYukonDevice);
            
        }
        catch (TransactionException e) {
            throw new DeviceCreationException("Could not create new device.", e);
        }
        
        return newYukonDevice;
    }
    
    @Transactional
    public YukonDevice createDeviceByDeviceType(int deviceType, String name, int address, int routeId, boolean createPoints) throws SQLException {

        YukonDevice yukonDevice = new YukonDevice();

        try {
            
            // test
            if (!DeviceAddressRange.isValidRange(deviceType, address)) {
                throw new DeviceCreationException("Invalid address for device type (" + address + ").");
            }
            else if (StringUtils.isBlank(name)) {
                throw new DeviceCreationException("Device name is blank.");
            }

            // create
            int newDeviceId = paoDao.getNextPaoId();
            MCTBase newDevice = (MCTBase)DeviceFactory.createDevice(deviceType);
            newDevice.setDeviceID(newDeviceId);
            newDevice.setPAOName(name);
            newDevice.setAddress(address);
            newDevice.getDeviceRoutes().setRouteID(routeId);

            Transaction.createTransaction(Transaction.INSERT, newDevice).execute();
            
            // db change msg
            processDeviceDbChange(newDevice);
            
            if (createPoints) {
                
                List<PointBase> points = deviceDefinitionService.createAllPointsForDevice(newDevice);
                this.applyPoints(newDevice, points);
            }
            
            // MAKE YukonDevice
            yukonDevice.setDeviceId(newDeviceId);
            yukonDevice.setType(deviceType);
            
        }
        catch (TransactionException e) {
            throw new DeviceCreationException("Could not create new device.", e);
        }
        
        return yukonDevice;
    }
    
    private MCTBase getExistingDeviceByTemplate(String templateName) {
        
        MCTBase templateDevice = new MCTBase();
        
        try {
            
            YukonDevice templateYukonDevice = deviceDao.getYukonDeviceObjectByName(templateName);
            int templateDeviceId = templateYukonDevice.getDeviceId();

            templateDevice.setDeviceID(templateDeviceId);
            templateDevice = Transaction.createTransaction(Transaction.RETRIEVE, templateDevice).execute();

        }
        catch (IncorrectResultSizeDataAccessException e) {
            throw new DeviceCreationException("Device for template name not found: " + templateName, e);
        }
        catch (TransactionException e) {
            throw new DeviceCreationException("Could not load template device from database: " + templateName, e);
        }
        
        return templateDevice;
    }
    
    private void addToTemplatesGroups(YukonDevice templateDevice, YukonDevice newDevice) {
        
        StoredDeviceGroup rootGroup = deviceGroupEditorDao.getRootGroup();
        Set<StoredDeviceGroup> templatesGroups = deviceGroupMemberEditorDao.getGroupMembership(rootGroup, templateDevice);
        
        for (StoredDeviceGroup templateGroup : templatesGroups) {
            deviceGroupMemberEditorDao.addDevices(templateGroup, newDevice);
        }
    }
    
    private void applyPoints(MCTBase device, List<PointBase> points) {
        
        int deviceId = device.getPAObjectID();
        
        try {
            
            MultiDBPersistent pointsToAdd = new MultiDBPersistent();
            Vector<DBPersistent> newPoints = new Vector<DBPersistent>(points.size());
            List<Integer> newPointIds = new ArrayList<Integer>();
            
            for (PointBase point : points) {
            
                int nextPointId = pointDao.getNextPointId();
                newPointIds.add(nextPointId);
                
                point.setPointID(nextPointId);
                point.getPoint().setPaoID(deviceId);
                
                newPoints.add(point);
            }
            
            // insert
            pointsToAdd.setDBPersistentVector(newPoints);
            Transaction.createTransaction(Transaction.INSERT, pointsToAdd).execute();
            
            // db change msgs
            for (PointBase point : points) {
                processPointDbChange(point.getPoint());
            }
            
        }
        catch (TransactionException e) {
            throw new DeviceCreationException("Could not apply points to new device.", e);
        }
    }
    
    private List<PointBase> getPointsForPao(int id) {
        
        List<LitePoint> litePoints = pointDao.getLitePointsByPaObjectId(id);
        List<PointBase> points = new ArrayList<PointBase>(litePoints.size());
        
        for (LitePoint litePoint: litePoints) {
            
            PointBase pointBase = (PointBase)LiteFactory.createDBPersistent(litePoint);
            
            try {
                Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, pointBase).execute();
                points.add(pointBase);
            }
            catch (TransactionException e) {
                throw new DeviceCreationException("Could not retrieve points for new device.", e);
            }
        }

        return points;
    }

    private void processDeviceDbChange(MCTBase newDevice) {

        DBChangeMsg msg = new DBChangeMsg(newDevice.getPAObjectID(),
                                          DBChangeMsg.CHANGE_PAO_DB,
                                          PAOGroups.STRING_CAT_DEVICE,
                                          newDevice.getPAOType(),
                                          DBChangeMsg.CHANGE_TYPE_ADD );
        dbPersistantDao.processDBChange(msg);
    }

    private void processPointDbChange(Point point) {

        DBChangeMsg msg = new DBChangeMsg(point.getPointID(),
                                          DBChangeMsg.CHANGE_POINT_DB,
                                          DBChangeMsg.CAT_POINT,
                                          point.getPointType(),
                                          DBChangeMsg.CHANGE_TYPE_ADD );
        dbPersistantDao.processDBChange(msg);
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
    public void setDeviceDefinitionService(DeviceDefinitionService deviceDefinitionService) {
        this.deviceDefinitionService = deviceDefinitionService;
    }
    
    @Required
    public void setDbPersistantDao(DBPersistentDao dbPersistantDao) {
        this.dbPersistantDao = dbPersistantDao;
    }
    
    @Required
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    
    @Required
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
}
