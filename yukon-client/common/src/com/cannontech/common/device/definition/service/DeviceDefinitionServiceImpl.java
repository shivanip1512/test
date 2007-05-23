package com.cannontech.common.device.definition.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.service.PointService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
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
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.DBPersistent;

/**
 * Implementation class for DeviceDefinitionService
 */
public class DeviceDefinitionServiceImpl implements DeviceDefinitionService {

    private DeviceDefinitionDao deviceDefinitionDao = null;
    private PointService pointService = null;
    private AttributeService attributeService = null;

    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }

    public void setPointService(PointService pointService) {
        this.pointService = pointService;
    }

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    public List<PointBase> createDefaultPointsForDevice(DeviceBase device) {

        List<PointBase> pointList = new ArrayList<PointBase>();
        LiteYukonPAObject liteDevice = getLiteForDevice(device);
        Set<PointTemplate> pointTemplates = deviceDefinitionDao.getInitPointTemplates(liteDevice);
        for (PointTemplate template : pointTemplates) {
            pointList.add(pointService.createPoint(device.getPAObjectID(), template));
        }

        return pointList;
    }

    public List<PointBase> createAllPointsForDevice(DeviceBase device) {

        List<PointBase> pointList = new ArrayList<PointBase>();
        LiteYukonPAObject liteDevice = getLiteForDevice(device);
        Set<PointTemplate> pointTemplates = deviceDefinitionDao.getAllPointTemplates(liteDevice);
        for (PointTemplate template : pointTemplates) {
            pointList.add(pointService.createPoint(device.getPAObjectID(), template));
        }

        return pointList;
    }

    public Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap() {
        return deviceDefinitionDao.getDeviceDisplayGroupMap();
    }

    public boolean isDeviceTypeChangeable(DeviceBase device) {
        LiteYukonPAObject liteDevice = getLiteForDevice(device);
        return deviceDefinitionDao.getDeviceDefinition(liteDevice).isChangeable();
    }

    public Set<DeviceDefinition> getChangeableDevices(DeviceBase device) {

        // Make sure this device can be changed
        if (!this.isDeviceTypeChangeable(device)) {
            throw new IllegalArgumentException("Device " + device.getPAOName()
                    + " is not changeable");
        }

        LiteYukonPAObject liteDevice = getLiteForDevice(device);
        DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(liteDevice);

        // Get all of the devices in the device's change group
        Set<DeviceDefinition> devices = deviceDefinitionDao.getChangeableDevices(deviceDefinition);

        // Remove the current device
        devices.remove(deviceDefinition);
        return devices;
    }

    public Set<PointTemplate> getPointTemplatesToAdd(DeviceBase device,
            DeviceDefinition newDefinition) {

        this.validateChange(device, newDefinition);

        Set<PointTemplate> existingTemplates = this.getExistingPointTemplates(device);
        Set<PointTemplate> newTemplates = deviceDefinitionDao.getInitPointTemplates(newDefinition);

        // Get the set of templates for the new device definition that do not
        // already exist on the current device
        Set<PointTemplate> tempRemoveTemplates = this.getTemplatesWithSameTypeAndAttribute(newTemplates,
                                                                                           existingTemplates);

        // Remove the existing supported points from the set of all new points -
        // all other new points will be added
        newTemplates.removeAll(tempRemoveTemplates);

        return newTemplates;
    }

    public Set<PointTemplate> getPointTemplatesToRemove(DeviceBase device,
            DeviceDefinition newDefinition) {

        this.validateChange(device, newDefinition);

        LiteYukonPAObject liteDevice = getLiteForDevice(device);
        Set<PointTemplate> existingTemplates = deviceDefinitionDao.getAllPointTemplates(liteDevice);

        // Get rid of any point templates for which a point doesn't exist for
        // the device
        Set<PointTemplate> nonExistingPointSet = new HashSet<PointTemplate>();
        for (PointTemplate template : existingTemplates) {
            if (!pointService.pointExistsForDevice(liteDevice, template)) {
                nonExistingPointSet.add(template);
            }
        }
        existingTemplates.removeAll(nonExistingPointSet);

        Set<PointTemplate> supportedTemplates = deviceDefinitionDao.getAllPointTemplates(newDefinition);

        // Get the set of point templates that exist on the device and are
        // supported by the new device definition - these points will not be
        // removed
        Set<PointTemplate> templatesToKeep = this.getTemplatesWithSameTypeAndAttribute(existingTemplates,
                                                                                       supportedTemplates);

        // Remove the set of supported existing point templates from the list of
        // existing points - all other existing points will be removed
        existingTemplates.removeAll(templatesToKeep);

        return existingTemplates;
    }

    public Set<PointTemplate> getPointTemplatesToTransfer(DeviceBase device,
            DeviceDefinition newDefinition) {

        this.validateChange(device, newDefinition);

        Set<PointTemplate> existingTemplates = this.getExistingPointTemplates(device);
        Set<PointTemplate> supportedTemplates = deviceDefinitionDao.getAllPointTemplates(newDefinition);

        // Get the set of point templates that exist on the device and are
        // supported by the new device definition - these points will be
        // transferred
        return this.getTemplatesWithSameTypeAndAttribute(existingTemplates, supportedTemplates);
    }

    public Set<PointTemplate> getNewPointTemplatesForTransfer(DeviceBase device,
            DeviceDefinition newDefinition) {

        this.validateChange(device, newDefinition);

        Set<PointTemplate> existingTemplates = this.getExistingPointTemplates(device);
        Set<PointTemplate> supportedTemplates = deviceDefinitionDao.getAllPointTemplates(newDefinition);

        // Get the set of point templates that exist on the device and are
        // supported by the new device definition - these points will be
        // transferred
        return this.getTemplatesWithSameTypeAndAttribute(supportedTemplates, existingTemplates);
    }

    @SuppressWarnings("unchecked")
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

        if (newDevice instanceof MCT410IL) {

            boolean loadProfileExists = false;
            try {
                LiteYukonPAObject liteDevice = getLiteForDevice(oldDevice);
                attributeService.getPointForAttribute(liteDevice, BuiltInAttribute.LOAD_PROFILE);
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

        LiteYukonPAObject liteDevice = getLiteForDevice(device);

        for (PointTemplate template : transferTemplates) {
            LitePoint litePoint = pointService.getPointForDevice(liteDevice, template);
            PointBase point = (PointBase) LiteFactory.convertLiteToDBPers(litePoint);

            Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, point);
            t.execute();

            PointTemplate newTemplate = this.getTemplateForAttribute(template.getAttribute(),
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
     * Helper method to remove unsupported points from a device that is being
     * changed into another device type
     * @param device - Device to change type
     * @param newDefinition - Definition of new device type
     * @throws TransactionException
     */
    private void removePoints(DeviceBase device, DeviceDefinition newDefinition)
            throws TransactionException {

        Set<PointTemplate> removeTemplates = this.getPointTemplatesToRemove(device, newDefinition);

        LiteYukonPAObject liteDevice = getLiteForDevice(device);

        for (PointTemplate template : removeTemplates) {
            LitePoint litePoint = pointService.getPointForDevice(liteDevice, template);

            PointBase point = (PointBase) LiteFactory.convertLiteToDBPers(litePoint);
            Transaction t = Transaction.createTransaction(Transaction.DELETE, point);
            t.execute();
        }
    }

    /**
     * Helper method to determine if the device can be changed into the new
     * definition type
     * @param device - Device to change
     * @param newDefinition - Definition of type to change to
     * @return True if the device can be changed into the given definition type
     */
    private void validateChange(DeviceBase device, DeviceDefinition newDefinition) {

        LiteYukonPAObject liteDevice = getLiteForDevice(device);
        DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(liteDevice);

        if (deviceDefinition.getChangeGroup() == null
                || !deviceDefinition.getChangeGroup().equals(newDefinition.getChangeGroup())) {

            throw new IllegalArgumentException(device.getPAOName() + " cannot be changed into a "
                    + newDefinition.getDisplayName());
        }

    }

    /**
     * Helper method to get a set of point templates from set1 that have an
     * attribute that is the same as an attribute for a point temlplate in set2
     * @param set1 - Set of point templates to start with
     * @param set2 - Set of point templates to compare to
     * @return The set of point templates with attributes found in both sets
     *         (returns a new copy each time the method is called)
     */
    private Set<PointTemplate> getTemplatesWithSameTypeAndAttribute(Set<PointTemplate> set1,
            Set<PointTemplate> set2) {

        Set<PointTemplate> templates = new HashSet<PointTemplate>();
        for (PointTemplate template1 : set1) {
            for (PointTemplate template2 : set2) {
                if (template1.getAttribute() != null
                        && template1.getAttribute().equals(template2.getAttribute())
                        && template1.getType() == template2.getType()) {
                    templates.add(template1);
                }
            }
        }
        return templates;
    }

    /**
     * Helper method to get the list of point templates that correspond to
     * attribute points that exist for the given device
     * @param device - Device to get pointTemplates for
     * @return A set of existing point templates (returns a new copy each time
     *         the method is called)
     */
    private Set<PointTemplate> getExistingPointTemplates(DeviceBase device) {

        LiteYukonPAObject liteDevice = getLiteForDevice(device);
        Set<Attribute> atributes = attributeService.getAllExistingAtributes(liteDevice);
        Set<PointTemplate> templates = new HashSet<PointTemplate>();
        for (Attribute attribute : atributes) {
            templates.add(deviceDefinitionDao.getPointTemplateForAttribute(liteDevice, attribute));
        }

        return templates;
    }

    /**
     * Helper method to get a point template for a given attribute from a set
     * @param attribute - Attribute to get template for
     * @param templates - Set of templates
     * @return PointTemplate that matches the attribute
     */
    private PointTemplate getTemplateForAttribute(Attribute attribute, Set<PointTemplate> templates) {

        for (PointTemplate template : templates) {
            if (attribute.equals(template.getAttribute())) {
                return template;
            }
        }

        throw new NotFoundException("The set of templates does not contain a template with attribute: "
                + attribute.getKey());
    }
    
    private LiteYukonPAObject getLiteForDevice(DeviceBase deviceBase) {
        return (LiteYukonPAObject) LiteFactory.createLite(deviceBase);
    }

}
