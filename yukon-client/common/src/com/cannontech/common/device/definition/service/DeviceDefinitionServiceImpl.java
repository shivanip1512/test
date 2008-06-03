package com.cannontech.common.device.definition.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.service.PointService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.IDeviceMeterGroup;
import com.cannontech.database.data.device.MCT410IL;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.device.lm.IGroupRoute;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;

public class DeviceDefinitionServiceImpl implements DeviceDefinitionService {
    private AttributeService attributeService;
    private PointService pointService;
    private PaoGroupsWrapper paoGroupsWrapper;
    private SimpleDeviceDefinitionService simpleDeviceDefinitionService;
    private DBPersistentDao dbPersistentDao;
    
    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Required
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }
    
    @Required
    public void setPointService(PointService pointService) {
        this.pointService = pointService;
    }
    
    @Required
    public void setSimpleDeviceDefinitionService(
            SimpleDeviceDefinitionService simpleDeviceDefinitionService) {
        this.simpleDeviceDefinitionService = simpleDeviceDefinitionService;
    }
    
    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
    public List<PointBase> createAllPointsForDevice(DeviceBase device) {
        return simpleDeviceDefinitionService.createAllPointsForDevice(getYukonDeviceForDevice(device));
    }

    public List<PointBase> createDefaultPointsForDevice(DeviceBase device) {
        return simpleDeviceDefinitionService.createDefaultPointsForDevice(getYukonDeviceForDevice(device));
    }

    public Set<DeviceDefinition> getChangeableDevices(DeviceBase device) {
        return simpleDeviceDefinitionService.getChangeableDevices(getYukonDeviceForDevice(device));
    }
    
    public Set<DeviceDefinition> getChangeableDevices(YukonDevice device) {
        return simpleDeviceDefinitionService.getChangeableDevices(device);
    }

    public Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap() {
        return simpleDeviceDefinitionService.getDeviceDisplayGroupMap();
    }

    public Set<PointTemplate> getNewPointTemplatesForTransfer(
                                                              DeviceBase device, DeviceDefinition deviceDefinition) {
        return simpleDeviceDefinitionService.getNewPointTemplatesForTransfer(getYukonDeviceForDevice(device), deviceDefinition);
    }

    public Set<PointTemplate> getPointTemplatesToAdd(DeviceBase device, DeviceDefinition deviceDefinition) {
        return simpleDeviceDefinitionService.getPointTemplatesToAdd(getYukonDeviceForDevice(device), deviceDefinition);
    }

    public Set<PointTemplate> getPointTemplatesToRemove(DeviceBase device, DeviceDefinition deviceDefinition) {
        return simpleDeviceDefinitionService.getPointTemplatesToRemove(getYukonDeviceForDevice(device), deviceDefinition);
    }

    public Set<PointTemplate> getPointTemplatesToTransfer(DeviceBase device, DeviceDefinition deviceDefinition) {
        return simpleDeviceDefinitionService.getPointTemplatesToTransfer(getYukonDeviceForDevice(device), deviceDefinition);
    }

    public boolean isDeviceTypeChangeable(DeviceBase device) {
        return simpleDeviceDefinitionService.isDeviceTypeChangeable(getYukonDeviceForDevice(device));
    }
    
    @Transactional
    public void changeDeviceType(YukonDevice currentDevice,
            DeviceDefinition newDefinition) {
        
        DeviceBase yukonPAObject = (DeviceBase) PAOFactory.createPAObject(currentDevice.getDeviceId());
        
        // Load the device to change
        try {
            Transaction t = Transaction.createTransaction(Transaction.RETRIEVE,
                                                          yukonPAObject);

            yukonPAObject = (DeviceBase) t.execute();
        } catch (TransactionException e) {
            throw new DataRetrievalFailureException("Could not load device from db", e);
        }

        // Change the device's type
        DeviceBase changedDevice = this.changeDeviceType(yukonPAObject, newDefinition);
        
        // Save the changes
        try {
            Transaction t = Transaction.createTransaction(Transaction.UPDATE, changedDevice);
            t.execute();
        } catch(TransactionException e) {
            throw new PersistenceException("Could not save device type change", e);
        }
        
        DBChangeMsg[] changeMsgs = changedDevice.getDBChangeMsgs(DBChangeMsg.CHANGE_TYPE_UPDATE);
        // Send DBChangeMsgs
        for (DBChangeMsg msg : changeMsgs) {
            dbPersistentDao.processDBChange(msg);
        }
        
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public DeviceBase changeDeviceType(DeviceBase currentDevice, DeviceDefinition newDefinition) {

        DeviceBase oldDevice = null;

        // get a deep copy of the current device
        try {
            oldDevice = (DeviceBase) CtiUtilities.copyObject(currentDevice);

            Transaction t = Transaction.createTransaction(Transaction.DELETE_PARTIAL,
                                                          ((DBPersistent) currentDevice));

            currentDevice = (DeviceBase) t.execute();

        } catch (Exception e) {
            CTILogger.error(e);
            CTILogger.info("*** An exception occured when trying to change type of "
                           + currentDevice + ", action aborted.");

            return currentDevice;
        }

        // create a brand new DeviceBase of the new type
        DeviceBase newDevice = DeviceFactory.createDevice(newDefinition.getType());

        // set all the device specific stuff here
        newDevice.setDevice(oldDevice.getDevice());
        newDevice.setPAOName(oldDevice.getPAOName());
        newDevice.setDisableFlag(oldDevice.getPAODisableFlag());
        newDevice.setPAOStatistics(oldDevice.getPAOStatistics());

        // remove then add the new elements for PAOExclusion
        newDevice.getPAOExclusionVector().removeAllElements();
        newDevice.getPAOExclusionVector().addAll(oldDevice.getPAOExclusionVector());

        if (newDevice instanceof CarrierBase && oldDevice instanceof CarrierBase) {
            ((CarrierBase) newDevice).getDeviceCarrierSettings()
            .setAddress(((CarrierBase) oldDevice).getDeviceCarrierSettings()
                        .getAddress());

            ((CarrierBase) newDevice).getDeviceRoutes()
            .setRouteID(((CarrierBase) oldDevice).getDeviceRoutes()
                        .getRouteID());

        } else if (newDevice instanceof IGroupRoute && oldDevice instanceof IGroupRoute) {
            ((IGroupRoute) newDevice).setRouteID(((IGroupRoute) oldDevice).getRouteID());
        } else if (newDevice instanceof IDLCBase && oldDevice instanceof IDLCBase) {
            ((IDLCBase) newDevice).getDeviceIDLCRemote()
            .setAddress(((IDLCBase) oldDevice).getDeviceIDLCRemote()
                        .getAddress());
        }

        if (newDevice instanceof RemoteBase && oldDevice instanceof RemoteBase) {
            ((RemoteBase) newDevice).getDeviceDirectCommSettings()
            .setPortID(((RemoteBase) oldDevice).getDeviceDirectCommSettings()
                       .getPortID());
        }

        if (newDevice instanceof IDeviceMeterGroup && oldDevice instanceof IDeviceMeterGroup) {
            ((IDeviceMeterGroup) newDevice).setDeviceMeterGroup(((IDeviceMeterGroup) oldDevice).getDeviceMeterGroup());
        }

        if (newDevice instanceof TwoWayDevice && oldDevice instanceof TwoWayDevice) {
            ((TwoWayDevice) newDevice).setDeviceScanRateMap(((TwoWayDevice) oldDevice).getDeviceScanRateMap());
        }

        if (newDevice instanceof CapBankController && oldDevice instanceof CapBankController) {
            ((CapBankController) newDevice).setDeviceCBC(((CapBankController) oldDevice).getDeviceCBC());
        }

        if (newDevice instanceof CapBankController702x
                && oldDevice instanceof CapBankController702x) {
            ((CapBankController702x) newDevice).setDeviceAddress(((CapBankController702x) oldDevice).getDeviceAddress());
            ((CapBankController702x) newDevice).setDeviceCBC(((CapBankController702x) oldDevice).getDeviceCBC());
        }
        
        if (newDevice instanceof CapBankControllerDNP
                && oldDevice instanceof CapBankControllerDNP) {
            ((CapBankControllerDNP) newDevice).setDeviceAddress(((CapBankControllerDNP) oldDevice).getDeviceAddress());
            ((CapBankControllerDNP) newDevice).setDeviceCBC(((CapBankControllerDNP) oldDevice).getDeviceCBC());
        }
        
        if (newDevice instanceof MCT410IL) {

            boolean loadProfileExists = false;
            try {
                YukonDevice meter = getYukonDeviceForDevice(oldDevice);
                attributeService.getPointForAttribute(meter, BuiltInAttribute.LOAD_PROFILE);
                loadProfileExists = true;
            } catch (NotFoundException e) {
                // Do nothing - no load profile point
            } catch (IllegalArgumentException e) {
                // Do nothing - no load profile point
            }
            if (loadProfileExists) {
                StringBuffer lp = new StringBuffer(((MCTBase) oldDevice).getDeviceLoadProfile()
                                                   .getLoadProfileCollection());
                lp.delete(1, 4);
                lp.append("NNN");
                ((MCT410IL) newDevice).getDeviceLoadProfile()
                .setLoadProfileCollection(lp.toString());
                ((MCT410IL) newDevice).getDeviceLoadProfile()
                .setLoadProfileDemandRate(((MCTBase) oldDevice).getDeviceLoadProfile()
                                          .getLoadProfileDemandRate());
            } else {
                ((MCT410IL) newDevice).getDeviceLoadProfile().setLoadProfileCollection("NNNN");
                ((MCT410IL) newDevice).getDeviceLoadProfile()
                .setLoadProfileDemandRate(new Integer(3600));
            }

            ((MCT410IL) newDevice).getDeviceLoadProfile().setVoltageDmdRate(new Integer(3600));
            ((MCT410IL) newDevice).getDeviceLoadProfile().setVoltageDmdInterval(new Integer(60));

        }

        try {
            Transaction t = Transaction.createTransaction(Transaction.ADD_PARTIAL,
                                                          ((DBPersistent) newDevice));
            newDevice = (DeviceBase) t.execute();

            this.removePoints(oldDevice, newDefinition);
            this.addPoints(oldDevice, newDefinition);
            this.transferPoints(oldDevice, newDefinition);

        } catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);

        }

        return newDevice;
    }


    /**
     * Helper method to remove unsupported points from a device that is being
     * changed into another device type
     * @param device - Device to change type
     * @param newDefinition - Definition of new device type
     * @throws TransactionException
     */
    private void removePoints(DeviceBase device, DeviceDefinition newDefinition)
    throws TransactionException {

        Set<PointTemplate> removeTemplates = this.getPointTemplatesToRemove(device, newDefinition);

        YukonDevice meter = getYukonDeviceForDevice(device);

        for (PointTemplate template : removeTemplates) {
            LitePoint litePoint = pointService.getPointForDevice(meter, template.getDevicePointIdentifier());

            PointBase point = (PointBase) LiteFactory.convertLiteToDBPers(litePoint);
            Transaction t = Transaction.createTransaction(Transaction.DELETE, point);
            t.execute();
        }
    }

    /**
     * Helper method to add supported points to a device that is being changed
     * into another device type
     * @param device - Device to change type
     * @param newDefinition - Definition of new device type
     * @throws TransactionException
     */
    private void addPoints(DeviceBase device, DeviceDefinition newDefinition)
    throws TransactionException {

        Set<PointTemplate> addTemplates = this.getPointTemplatesToAdd(device, newDefinition);
        for (PointTemplate template : addTemplates) {
            PointBase point = pointService.createPoint(device.getDevice().getDeviceID(), template);

            Transaction t = Transaction.createTransaction(Transaction.INSERT, point);
            t.execute();
        }

    }

    /**
     * Helper method to transfer supported points from a device that is being
     * changed into another device type
     * @param device - Device to change type
     * @param newDefinition - Definition of new device type
     * @throws TransactionException
     */
    private void transferPoints(DeviceBase device, DeviceDefinition newDefinition)
    throws TransactionException {

        Set<PointTemplate> transferTemplates = this.getPointTemplatesToTransfer(device,
                                                                                newDefinition);
        Set<PointTemplate> newTemplates = this.getNewPointTemplatesForTransfer(device,
                                                                               newDefinition);

        YukonDevice meter = getYukonDeviceForDevice(device);

        for (PointTemplate template : transferTemplates) {
            LitePoint litePoint = pointService.getPointForDevice(meter, template.getDevicePointIdentifier());
            PointBase point = (PointBase) LiteFactory.convertLiteToDBPers(litePoint);

            Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, point);
            t.execute();

            PointTemplate newTemplate = this.getTemplateForLitePoint(litePoint,
                                                                     newTemplates);

            // Update the offset
            point.getPoint().setPointOffset(newTemplate.getOffset());

            // Update the multiplier
            double multiplier = newTemplate.getMultiplier();
            if (point instanceof AccumulatorPoint) {
                AccumulatorPoint accPoint = (AccumulatorPoint) point;
                accPoint.getPointAccumulator().setMultiplier(multiplier);
            } else if (point instanceof AnalogPoint) {
                AnalogPoint analogPoint = (AnalogPoint) point;
                analogPoint.getPointAnalog().setMultiplier(multiplier);
            }

            t = Transaction.createTransaction(Transaction.UPDATE, point);
            t.execute();
        }

    }

    /**
     * Helper method to get a point template for a given litePoint from a set
     * @param litePoint - LitePiont to get template for
     * @param templates - Set of templates
     * @return PointTemplate that matches the litePoint
     */
    private PointTemplate getTemplateForLitePoint(LitePoint litePoint, Set<PointTemplate> templates) {

        for (PointTemplate template : templates) {
			if (litePoint.getPointName().equals(template.getName()) &&
					litePoint.getPointOffset() == template.getOffset() &&
					litePoint.getPointType() == template.getType())
				return template;
		}

        throw new NotFoundException("The set of templates does not contain a template with LitePoint: "
                                    + litePoint.toString());
    }

    private YukonDevice getYukonDeviceForDevice(DeviceBase oldDevice) {
        YukonDevice device = new YukonDevice();
        device.setDeviceId(oldDevice.getPAObjectID());
        String typeStr = oldDevice.getPAOType();
        int deviceType = paoGroupsWrapper.getDeviceType(typeStr);
        device.setType(deviceType);
        return device;
    }

}
