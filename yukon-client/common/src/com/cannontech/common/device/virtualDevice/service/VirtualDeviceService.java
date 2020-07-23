package com.cannontech.common.device.virtualDevice.service;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.virtualDevice.VirtualDeviceModel;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PaginatedResponse;
import com.cannontech.common.pao.LiteYukonPaoSortableField;

public interface VirtualDeviceService {

    /*
     * Create a new virtual device
     */
    VirtualDeviceModel create(VirtualDeviceModel virtualDevice);

    /*
     * Retrieve an existing virtual device
     */
    VirtualDeviceModel retrieve(int virtualDeviceId);

    /*
     * Update an existing virtual device
     */
    VirtualDeviceModel update(int virtualDeviceId, VirtualDeviceModel virtualDevice);

    /*
     * Delete a virtual device
     */
    int delete(int id);

    /*
     * Get a list of LiteYukonPAObjects
     */
    PaginatedResponse<DeviceBaseModel> list(LiteYukonPaoSortableField sort_by, Direction direction, Integer page,
            Integer items_per_page);
}
