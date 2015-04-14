package com.cannontech.common.device.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.bulk.service.ChangeDeviceTypeService.ChangeDeviceTypeInfo;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointBase;

public interface DeviceUpdateService {

    class PointsToProcess {
        private Set<PointBase> pointsToDelete;
        private Set<PointTemplate> pointsToAdd;
        private Map<Integer, PointToTemplate> pointsToTransfer;

        public PointsToProcess(Set<PointBase> pointsToDelete, Set<PointTemplate> pointsToAdd,
                Map<Integer, PointToTemplate> pointsToTransfer) {
            this.pointsToDelete = pointsToDelete;
            this.pointsToAdd = pointsToAdd;
            this.pointsToTransfer = pointsToTransfer;
        }

        public Set<PointBase> getPointsToDelete() {
            return pointsToDelete;
        }

        public Set<PointTemplate> getPointsToAdd() {
            return pointsToAdd;
        }

        public Map<Integer, PointToTemplate> getPointsToTransfer() {
            return pointsToTransfer;
        }
    }

    class PointToTemplate {
        private PointBase point;
        private PointTemplate template;

        public PointToTemplate(PointBase point, PointTemplate template) {
            this.point = point;
            this.template = template;
        }

        public PointBase getPoint() {
            return point;
        }

        public PointTemplate getTemplate() {
            return template;
        }
    }
        
    /**
     * Checks if new address is valid for device, throws {@link IllegalArgumentException} if not.
     * Otherwise delgates to {@link DeviceDao} to perform address change.
     * @param device
     * @param newAddress
     * @throws IllegalArgumentException
     */
    public void changeAddress(YukonDevice device, int newAddress) throws IllegalArgumentException;
    
    /**
     * Checks if route name is a valid route (a known route exists with that name), throws
     * {@link IllegalArgumentException} if not. Otherwise delegates to {@link DeviceDao} to update.
     * @param device
     * @param newAddress
     * @throws IllegalArgumentException
     */
    public void changeRoute(YukonDevice device, String newRouteName) throws IllegalArgumentException;
    
    public void changeRoute(YukonDevice device, int newRouteId) throws IllegalArgumentException;
    
    public void routeDiscovery(YukonDevice device, List<Integer> routeIds, LiteYukonUser liteYukonUser);
    
    /**
     * Method to change a device's type. Note: the returned device must be saved
     * to complete the change
     * @param currentDevice - Device to change
     * @param newDefinition - Definition of type to change to
     * @param info - contains MCT and RFN specific information
     * @return The changed device
     */
    public abstract DeviceBase changeDeviceType(DeviceBase currentDevice, PaoDefinition newDefinition,
            ChangeDeviceTypeInfo info);

    /**
     * Method to change a device's type
     * @param currentDevice - Device to change
     * @param newDefinition - Definition of type to change to
     * @param info - contains MCT and RFN specific information
     */
    public SimpleDevice changeDeviceType(YukonDevice currentDevice, PaoDefinition newDefinition,
            ChangeDeviceTypeInfo info);

    /**
     * Returns the object that contains points to create,delete and transfer
     */
    PointsToProcess getPointsToProccess(DeviceBase oldDevice, PaoType newType);
}
