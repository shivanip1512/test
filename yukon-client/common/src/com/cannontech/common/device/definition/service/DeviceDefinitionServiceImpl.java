package com.cannontech.common.device.definition.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.PointService;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;

/**
 * Implementation class for DeviceDefinitionService
 */
public class DeviceDefinitionServiceImpl implements DeviceDefinitionService {

    private DeviceDefinitionDao deviceDefinitionDao = null;
    private PointService pointService = null;
    private PointDao pointDao = null;

    @Autowired
    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }

    @Autowired
    public void setPointService(PointService pointService) {
        this.pointService = pointService;
    }

    @Autowired
    public void setPointDao(PointDao pointDao) {
		this.pointDao = pointDao;
	}
    
    public List<PointBase> createDefaultPointsForDevice(SimpleDevice device) {

        List<PointBase> pointList = new ArrayList<PointBase>();
        Set<PointTemplate> pointTemplates = deviceDefinitionDao.getInitPointTemplates(device.getDeviceType());
        for (PointTemplate template : pointTemplates) {
            pointList.add(pointService.createPoint(device.getDeviceId(), template));
        }

        return pointList;
    }

    public List<PointBase> createAllPointsForDevice(SimpleDevice device) {

        List<PointBase> pointList = new ArrayList<PointBase>();
        Set<PointTemplate> pointTemplates = deviceDefinitionDao.getAllPointTemplates(device.getDeviceType());
        for (PointTemplate template : pointTemplates) {
            pointList.add(pointService.createPoint(device.getDeviceId(), template));
        }

        return pointList;
    }

    public Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap() {
        return deviceDefinitionDao.getDeviceDisplayGroupMap();
    }

    public boolean isDeviceTypeChangeable(SimpleDevice device) {
        DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(device.getDeviceType());
        return deviceDefinition.isChangeable();
    }

    public Set<DeviceDefinition> getChangeableDevices(SimpleDevice device) {

        // Make sure this device can be changed
        if (!this.isDeviceTypeChangeable(device)) {
            return Collections.emptySet();
        }

        DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(device.getDeviceType());

        // Get all of the devices in the device's change group
        Set<DeviceDefinition> devices = deviceDefinitionDao.getDevicesThatDeviceCanChangeTo(deviceDefinition);

        // Remove the current device
        devices.remove(deviceDefinition);
        return devices;
    }

    public Set<PointTemplate> getPointTemplatesToAdd(SimpleDevice device,
            DeviceDefinition newDefinition) {
		this.validateChange(device, newDefinition);
		// points to add are points that are defined for newDefinition minus points being
		// transfered (i.e. minus new points from getPointTemplatesForTransfer)
		
		// for this method, templates are being compared on the new definition and therefore
		// should be compared by the identifier (although including name won't hurt, because
		// everything is from the same definition)
		
		Set<PointTemplate> existingTemplates = deviceDefinitionDao.getInitPointTemplates(newDefinition);
		HashSet<PointTemplate> result = new HashSet<PointTemplate>(existingTemplates);
		
		List<PointTemplateTransferPair> pointTemplatesToTransfer = getPointTemplatesToTransfer(device, newDefinition);
		for (PointTemplateTransferPair pointTemplateTransferPair : pointTemplatesToTransfer) {
			result.remove(pointTemplateTransferPair.newDefinitionTemplate);
		}
		
		return result;
	}

    public Set<PointIdentifier> getPointTemplatesToRemove(SimpleDevice device,
            DeviceDefinition newDefinition) {
		this.validateChange(device, newDefinition);
		// points to remove are points that exist on device (AND are defined) minus points being
		// transfered (i.e. minus old points from getPointTemplatesForTransfer)
		
		// for this method, templates are being compared on the new definition and therefore
		// should be compared by the identifier (although including name won't hurt, because
		// everything is from the same definition)
		
		HashSet<PointIdentifier> result = new HashSet<PointIdentifier>();
		Set<PointTemplate> existingPointTemplates = getExistingPointTemplates(device);
		for (PointTemplate pointTemplate : existingPointTemplates) {
			result.add(pointTemplate.getPointIdentifier());
		}
		
		List<PointTemplateTransferPair> pointTemplatesToTransfer = getPointTemplatesToTransfer(device, newDefinition);
		for (PointTemplateTransferPair pointTemplateTransferPair : pointTemplatesToTransfer) {
			result.remove(pointTemplateTransferPair.oldDefinitionTemplate);
		}
		
		return result;
	}

    public List<PointTemplateTransferPair> getPointTemplatesToTransfer(SimpleDevice device,
            DeviceDefinition newDefinition) {

		this.validateChange(device, newDefinition);
		
		Set<PointTemplate> existingTemplates = this.getExistingPointTemplates(device);
		Set<PointTemplate> supportedTemplates = deviceDefinitionDao.getAllPointTemplates(newDefinition);
		
		// Form pairs of points by comparing names
		List<PointTemplateTransferPair> templates = new ArrayList<PointTemplateTransferPair>();
		for (PointTemplate oldTemplate : existingTemplates) {
			for (PointTemplate newTemplate : supportedTemplates) {
				// here's the big check that determines what points are the same
				// note we're comparing the names of the names of the templates
				// so that any changes to the point's name in the DB are ignored
				if (oldTemplate.getName().equals(newTemplate.getName())) {
					PointTemplateTransferPair pair = new PointTemplateTransferPair();
					pair.oldDefinitionTemplate = oldTemplate.getPointIdentifier();
					pair.newDefinitionTemplate = newTemplate;
					templates.add(pair);
				}
			}
		}
		
		return templates;
	}

    /**
     * Helper method to determine if the device can be changed into the new
     * definition type
     * @param device - Device to change
     * @param newDefinition - Definition of type to change to
     * @return True if the device can be changed into the given definition type
     */
    private void validateChange(SimpleDevice device, DeviceDefinition newDefinition) {

        DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(device.getDeviceType());

        if (deviceDefinition.getChangeGroup() == null
                || !deviceDefinition.getChangeGroup().equals(newDefinition.getChangeGroup())) {

            throw new IllegalArgumentException(device + " cannot be changed into a "
                    + newDefinition.getDisplayName());
        }

    }

    /**
     * Helper method to get the list of point templates that correspond to
     * litePoints that exist for the given device
     * @param device - Device to get pointTemplates for
     * @return A set of existing point templates (returns a new copy each time
     *         the method is called)
     */
    private Set<PointTemplate> getExistingPointTemplates(SimpleDevice device) {

        Set<PointTemplate> templates = new HashSet<PointTemplate>();
    	List<LitePoint> existingPoints = pointDao.getLitePointsByPaObjectId(device.getDeviceId());
    	Set<PointTemplate> existingTemplates = deviceDefinitionDao.getAllPointTemplates(device.getDeviceType());
    	
    	for (LitePoint litePoint : existingPoints) {
    	    PointIdentifier pointIdentifier = getDevicePointIdentifier(litePoint);
			for (PointTemplate template : existingTemplates) {
				if (pointIdentifier.equals(template.getPointIdentifier()))
					templates.add(template);
			}
		}
        return templates;
    }
    
    private PointIdentifier getDevicePointIdentifier(LitePoint point) {
        return new PointIdentifier(point.getPointType(), point.getPointOffset());
    }
}