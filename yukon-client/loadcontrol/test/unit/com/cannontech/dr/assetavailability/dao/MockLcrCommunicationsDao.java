package com.cannontech.dr.assetavailability.dao;

import java.util.Collection;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.dr.assetavailability.DeviceCommunicationTimes;

public class MockLcrCommunicationsDao implements LcrCommunicationsDao {

    @Override
    public Map<Integer, DeviceCommunicationTimes> getTimes(Collection<Integer> deviceIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean updateComms(PaoIdentifier paoIdentifier, Instant timestamp) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean updateRuntimeAndComms(PaoIdentifier paoIdentifier, int relay, Instant timestamp) {
        throw new MethodNotImplementedException();
    }

}
