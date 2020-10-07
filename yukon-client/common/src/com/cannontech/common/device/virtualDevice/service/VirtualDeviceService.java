package com.cannontech.common.device.virtualDevice.service;

import com.cannontech.common.device.virtualDevice.VirtualDeviceBaseModel;
import com.cannontech.common.device.virtualDevice.VirtualDeviceSortableField;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PaginatedResponse;
import com.cannontech.database.data.device.VirtualBase;

public interface VirtualDeviceService {

    /*
     * Create a new virtual device
     */
    VirtualDeviceBaseModel<? extends VirtualBase> create(VirtualDeviceBaseModel<? extends VirtualBase> virtualDevice);

    /*
     * Retrieve an existing virtual device
     */
    VirtualDeviceBaseModel<? extends VirtualBase> retrieve(int virtualDeviceId);

    /*
     * Update an existing virtual device
     */
    VirtualDeviceBaseModel<? extends VirtualBase> update(int virtualDeviceId, VirtualDeviceBaseModel<? extends VirtualBase> virtualDevice);

    /*
     * Delete a virtual device
     */
    int delete(int id);

    /*
     * Get a list of LiteYukonPAObjects
     */
    PaginatedResponse<VirtualDeviceBaseModel> getPage(VirtualDeviceSortableField sortBy, Direction direction, Integer page,
            Integer itemsPerPage);
}
