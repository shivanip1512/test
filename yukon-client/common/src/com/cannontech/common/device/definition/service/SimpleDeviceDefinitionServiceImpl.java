package com.cannontech.common.device.definition.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.service.PointService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.device.range.DeviceAddressRange;

/**
 * Implementation class for DeviceDefinitionService
 */
public class SimpleDeviceDefinitionServiceImpl implements SimpleDeviceDefinitionService {

    private DeviceDefinitionDao deviceDefinitionDao = null;
    private PointService pointService = null;
    private PointDao pointDao = null;
    private DeviceDao deviceDao = null;
    private PaoDao paoDao = null;

    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }

    public void setPointService(PointService pointService) {
        this.pointService = pointService;
    }

    public void setPointDao(PointDao pointDao) {
		this.pointDao = pointDao;
	}
    
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
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
            return Collections.emptySet();
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
        return this.getCommonTemplates(supportedTemplates, existingTemplates);
    }

    public Set<PointTemplate> getPointTemplatesToAdd(YukonDevice meter,
                                                     DeviceDefinition newDefinition) {

        this.validateChange(meter, newDefinition);

        Set<PointTemplate> existingTemplates = this.getExistingPointTemplates(meter);
        Set<PointTemplate> newTemplates = deviceDefinitionDao.getInitPointTemplates(newDefinition);

        // Get the set of templates for the new device definition that do not
        // already exist on the current device
		Set<PointTemplate> tempRemoveTemplates = this.getCommonTemplates(newTemplates, existingTemplates);

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
            if (!pointService.pointExistsForDevice(device, template.getDevicePointIdentifier())) {
                nonExistingPointSet.add(template);
            }
        }
        existingTemplates.removeAll(nonExistingPointSet);

        Set<PointTemplate> supportedTemplates = deviceDefinitionDao.getAllPointTemplates(newDefinition);

        // Get the set of point templates that exist on the device and are
        // supported by the new device definition - these points will not be
        // removed
		Set<PointTemplate> templatesToKeep = this.getCommonTemplates(existingTemplates, supportedTemplates);

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
		return this.getCommonTemplates(existingTemplates, supportedTemplates);
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
     * Helper method to get a set of point templates 
     * from set1 that equal a point temlplate in set2
     * @param set1 - Set of point templates to start with
     * @param set2 - Set of point templates to compare to
     * @return The set of point templates with pointTemplates found in both sets
     *         (returns a new copy each time the method is called)
     */
    private Set<PointTemplate> getCommonTemplates(Set<PointTemplate> set1,
            Set<PointTemplate> set2) {

    	Set<PointTemplate> templates = new HashSet<PointTemplate>();
        for (PointTemplate template1 : set1) {
            for (PointTemplate template2 : set2) {
                if (template1.compareTo(template2) == 0) {
                    templates.add(template1);
                }
            }
        }
        return templates;
    }

    /**
     * Helper method to get the list of point templates that correspond to
     * litePoints that exist for the given device
     * @param device - Device to get pointTemplates for
     * @return A set of existing point templates (returns a new copy each time
     *         the method is called)
     */
    private Set<PointTemplate> getExistingPointTemplates(YukonDevice device) {

        Set<PointTemplate> templates = new HashSet<PointTemplate>();
    	List<LitePoint> existingPoints = pointDao.getLitePointsByPaObjectId(device.getDeviceId());
    	Set<PointTemplate> existingTemplates = deviceDefinitionDao.getAllPointTemplates(device);
    	
    	for (LitePoint litePoint : existingPoints) {
			for (PointTemplate template : existingTemplates) {
				if (litePoint.getPointName().equals(template.getName()) &&
						litePoint.getPointOffset() == template.getOffset() &&
						litePoint.getPointType() == template.getType())
					templates.add(template);
			}
		}
        return templates;
    }
    
    public void changeAddress(YukonDevice device, int newAddress) throws IllegalArgumentException {
        
        boolean validAddressForType = DeviceAddressRange.isValidRange(device.getType(), newAddress);
        
        if (!validAddressForType) {
            throw new IllegalArgumentException("Address not in valid range for device type: " + newAddress);
        }
        
        deviceDao.changeAddress(device.getDeviceId(), newAddress);
    }
    
    public void changeRoute(int deviceId, String newRouteName) throws IllegalArgumentException {
        
        Integer routeId = paoDao.getRouteIdForRouteName(newRouteName);
        
        if (routeId == null) {
            throw new IllegalArgumentException("Invalid route name: " + newRouteName);
        }
        
        deviceDao.changeRoute(deviceId, routeId);
    }
}