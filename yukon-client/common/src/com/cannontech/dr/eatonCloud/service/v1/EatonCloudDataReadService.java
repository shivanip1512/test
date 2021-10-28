package com.cannontech.dr.eatonCloud.service.v1;

import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.Range;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Multimap;

public interface EatonCloudDataReadService {

    /**
     * Retrieves point data for the specified devices from the Eaton Cloud. Attempts to retrieve data for all attributes on each
     * device. Non Cellular-LCR devices will be ignored. This method doesn't throw an exception and will return nothing for the
     * device that had an error.
     * 
     * debugReadType - example collection actions, hourly read etc
     */
    Multimap<PaoIdentifier, PointData> collectDataForRead(Set<Integer> deviceIds, Range<Instant> range, String debugReadType);

    /**
     * Retrieves point data for the specified device from the Eaton Cloud.
     * 
     * @throws EatonCloudCommunicationExceptionV1 or EatonCloudException if the data was not retrieved
     */
    Multimap<PaoIdentifier, PointData> collectDataForRead(Integer deviceId, Range<Instant> range);
}
