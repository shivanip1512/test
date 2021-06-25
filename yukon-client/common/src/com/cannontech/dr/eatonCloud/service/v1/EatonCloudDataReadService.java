package com.cannontech.dr.eatonCloud.service.v1;

import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.Range;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Multimap;

public interface EatonCloudDataReadService {

    /**
     * Retrieves updated data for the specified devices from the Eaton Cloud. Attempts to retrieve data for all attributes on each
     * device.
     * Non Cellular-LCR devices will be ignored.
     */
    Multimap<PaoIdentifier, PointData> collectDataForRead(Set<Integer> deviceIds, Range<Instant> range);

}
