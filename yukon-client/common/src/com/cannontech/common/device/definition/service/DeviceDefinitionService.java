package com.cannontech.common.device.definition.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.data.point.PointBase;

/**
 * Class which provides functionality for manipulating devices based on their
 * definition
 */
public interface DeviceDefinitionService {

	public static class PointTemplateTransferPair {
        public PointIdentifier oldDefinitionTemplate;
        public PointTemplate newDefinitionTemplate;
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((newDefinitionTemplate == null) ? 0
                    : newDefinitionTemplate.hashCode());
            result = prime * result + ((oldDefinitionTemplate == null) ? 0
                    : oldDefinitionTemplate.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PointTemplateTransferPair other = (PointTemplateTransferPair) obj;
            if (newDefinitionTemplate == null) {
                if (other.newDefinitionTemplate != null)
                    return false;
            } else if (!newDefinitionTemplate.equals(other.newDefinitionTemplate))
                return false;
            if (oldDefinitionTemplate == null) {
                if (other.oldDefinitionTemplate != null)
                    return false;
            } else if (!oldDefinitionTemplate.equals(other.oldDefinitionTemplate))
                return false;
            return true;
        }
        @Override
        public String toString() {
            ToStringBuilder b = new ToStringBuilder(this);
            b.append("oldDefinitionTemplate", oldDefinitionTemplate);
            b.append("newDefinitionTemplate", newDefinitionTemplate);
            return b.toString();
        }
        

    }

    /**
     * Method to create all of the default points for the given device. NOTE:
     * this will create the points in memory ONLY - the default points will NOT
     * be persisted
     * @param device - Device to create points for
     * @return A list of the default points for the device (returns a new copy
     *         each time the method is called)
     */
    public abstract List<PointBase> createDefaultPointsForDevice(SimpleDevice device);

    /**
     * Method to create all of the points for the given device. NOTE: this will
     * create the points in memory ONLY - the points will NOT be persisted
     * @param device - Device to create points for
     * @return A list of all the points for the device (returns a new copy each
     *         time the method is called)
     */
    public abstract List<PointBase> createAllPointsForDevice(SimpleDevice device);

    /**
     * Method to get a map of device display groups and their associated device
     * types
     * @return An immutable map with key: display group name, value: list of
     *         device display
     */
    public abstract Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap();

    /**
     * Method used to determine if a device can have its type changed
     * @param device - Device to change
     * @return True if the device's type can be changed
     */
    public abstract boolean isDeviceTypeChangeable(SimpleDevice device);

    /**
     * Method to get a set of device definitions for devices that the given
     * device can be changed into
     * @param device - Device to change
     * @return A set of device definitions that the given device can change into
     *         (returns a new copy each time the method is called)
     */
    public abstract Set<DeviceDefinition> getChangeableDevices(SimpleDevice device);
    
    /**
     * Method to get a set of point templates that will be added to the given
     * device if its type is changed to the given device definition
     * @param device - Device to change type
     * @param deviceDefinition - Definition of type to change to
     * @return Set of points that will be added to the device (returns a new
     *         copy each time the method is called)
     */
    public abstract Set<PointTemplate> getPointTemplatesToAdd(SimpleDevice device,
            DeviceDefinition deviceDefinition);

    /**
     * Method to get a set of points that will be removed from the given device
     * if its type is changed to the given device definition
     * @param device - Device to change type
     * @param deviceDefinition - Definition of type to change to
     * @return Set of points that will be removed from the device (returns a new
     *         copy each time the method is called)
     */
    public abstract Set<PointIdentifier> getPointTemplatesToRemove(SimpleDevice device,
            DeviceDefinition deviceDefinition);
    
    /**
     * Method to get a set of points that will be transfered from the given
     * device type to the new device type if its type is changed to the given
     * device definition
     * @param device - Device to change type
     * @param deviceDefinition - Definition of type to change to
     * @return Set of point templates that will be transfered from the device
     *         (returns a new copy each time the method is called)
     */
    public abstract Set<PointTemplateTransferPair> getPointTemplatesToTransfer(SimpleDevice device,
            DeviceDefinition deviceDefinition);
}