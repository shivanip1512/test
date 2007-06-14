package com.cannontech.common.device.definition.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.service.PointService;
import com.cannontech.database.data.point.PointBase;

/**
 * Implementation class for DeviceDefinitionService
 */
public class SimpleDeviceDefinitionServiceImpl implements SimpleDeviceDefinitionService {

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

    public List<PointBase> createDefaultPointsForDevice(YukonDevice device) {

        List<PointBase> pointList = new ArrayList<PointBase>();
        Set<PointTemplate> pointTemplates = deviceDefinitionDao.getInitPointTemplates(device);
        for (PointTemplate template : pointTemplates) {
            pointList.add(pointService.createPoint(device.getDeviceId(), template));
        }

        return pointList;
    }

    public List<PointBase> createAllPointsForDevice(YukonDevice meter) {

        List<PointBase> pointList = new ArrayList<PointBase>();
        Set<PointTemplate> pointTemplates = deviceDefinitionDao.getAllPointTemplates(meter);
        for (PointTemplate template : pointTemplates) {
            pointList.add(pointService.createPoint(meter.getDeviceId(), template));
        }

        return pointList;
    }

    public Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap() {
        return deviceDefinitionDao.getDeviceDisplayGroupMap();
    }

    public boolean isDeviceTypeChangeable(YukonDevice meter) {
        return deviceDefinitionDao.getDeviceDefinition(meter).isChangeable();
    }

    public Set<DeviceDefinition> getChangeableDevices(YukonDevice meter) {

        // Make sure this device can be changed
        if (!this.isDeviceTypeChangeable(meter)) {
            throw new IllegalArgumentException("Device " + meter
                    + " is not changeable");
        }

        DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(meter);

        // Get all of the devices in the device's change group
        Set<DeviceDefinition> devices = deviceDefinitionDao.getChangeableDevices(deviceDefinition);

        // Remove the current device
        devices.remove(deviceDefinition);
        return devices;
    }

    public Set<PointTemplate> getNewPointTemplatesForTransfer(YukonDevice meter,
            DeviceDefinition newDefinition) {

        this.validateChange(meter, newDefinition);

        Set<PointTemplate> existingTemplates = this.getExistingPointTemplates(meter);
        Set<PointTemplate> supportedTemplates = deviceDefinitionDao.getAllPointTemplates(newDefinition);

        // Get the set of point templates that exist on the device and are
        // supported by the new device definition - these points will be
        // transferred
        return this.getTemplatesWithSameTypeAndAttribute(supportedTemplates, existingTemplates);
    }

    public Set<PointTemplate> getPointTemplatesToAdd(YukonDevice meter,
                                                     DeviceDefinition newDefinition) {

        this.validateChange(meter, newDefinition);

        Set<PointTemplate> existingTemplates = this.getExistingPointTemplates(meter);
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

    public Set<PointTemplate> getPointTemplatesToRemove(YukonDevice device,
                                                        DeviceDefinition newDefinition) {

        this.validateChange(device, newDefinition);

        Set<PointTemplate> existingTemplates = deviceDefinitionDao.getAllPointTemplates(device);

        // Get rid of any point templates for which a point doesn't exist for
        // the device
        Set<PointTemplate> nonExistingPointSet = new HashSet<PointTemplate>();
        for (PointTemplate template : existingTemplates) {
            if (!pointService.pointExistsForDevice(device, template)) {
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

    public Set<PointTemplate> getPointTemplatesToTransfer(YukonDevice device,
                                                          DeviceDefinition newDefinition) {

        this.validateChange(device, newDefinition);

        Set<PointTemplate> existingTemplates = this.getExistingPointTemplates(device);
        Set<PointTemplate> supportedTemplates = deviceDefinitionDao.getAllPointTemplates(newDefinition);

        // Get the set of point templates that exist on the device and are
        // supported by the new device definition - these points will be
        // transferred
        return this.getTemplatesWithSameTypeAndAttribute(existingTemplates, supportedTemplates);
    }


    /**
     * Helper method to determine if the device can be changed into the new
     * definition type
     * @param device - Device to change
     * @param newDefinition - Definition of type to change to
     * @return True if the device can be changed into the given definition type
     */
    private void validateChange(YukonDevice device, DeviceDefinition newDefinition) {

        DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(device);

        if (deviceDefinition.getChangeGroup() == null
                || !deviceDefinition.getChangeGroup().equals(newDefinition.getChangeGroup())) {

            throw new IllegalArgumentException(device + " cannot be changed into a "
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
    private Set<PointTemplate> getExistingPointTemplates(YukonDevice device) {

        Set<Attribute> atributes = attributeService.getAllExistingAtributes(device);
        Set<PointTemplate> templates = new HashSet<PointTemplate>();
        for (Attribute attribute : atributes) {
            templates.add(deviceDefinitionDao.getPointTemplateForAttribute(device, attribute));
        }

        return templates;
    }

    
}
