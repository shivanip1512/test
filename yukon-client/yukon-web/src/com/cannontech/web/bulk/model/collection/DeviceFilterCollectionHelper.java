package com.cannontech.web.bulk.model.collection;

import java.util.List;

import com.cannontech.amr.csr.model.FilterBy;
import com.cannontech.amr.csr.model.OrderBy;
import com.cannontech.common.bulk.collection.DeviceCollection;

public interface DeviceFilterCollectionHelper {
    public DeviceCollection createDeviceGroupCollection(final List<FilterBy> filterBys, final OrderBy orderBy);
}
