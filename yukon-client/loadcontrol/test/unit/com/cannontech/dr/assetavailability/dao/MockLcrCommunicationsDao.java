package com.cannontech.dr.assetavailability.dao;

import java.util.Collection;
import java.util.Map;

import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.dr.assetavailability.AllRelayCommunicationTimes;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.DeviceCommunicationTimes;

public class MockLcrCommunicationsDao implements DynamicLcrCommunicationsDao {

    @Override
    public Map<Integer, DeviceCommunicationTimes> findTimes(Collection<Integer> deviceIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Map<Integer, AllRelayCommunicationTimes> findAllRelayCommunicationTimes(Collection<Integer> deviceIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void insertData(AssetAvailabilityPointDataTimes times) {
        throw new MethodNotImplementedException();
    }

}
