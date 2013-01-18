package com.cannontech.web.bulk.model.collection;

import java.util.List;

import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchOrderBy;
import com.cannontech.common.bulk.collection.device.DeviceCollection;

public interface DeviceFilterCollectionHelper {
    public DeviceCollection createDeviceGroupCollection(final List<FilterBy> filterBys, final MeterSearchOrderBy orderBy);
}
