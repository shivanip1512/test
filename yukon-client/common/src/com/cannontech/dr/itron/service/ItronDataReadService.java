package com.cannontech.dr.itron.service;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.PointValueHolder;
import com.google.common.collect.Multimap;

public interface ItronDataReadService {

    /**
     * Asks Itron for data for all devices 
     */
    void collectData();
    
    /**
     * Asks Itron for data for one devices, used by "Read Now"
     */
    void collectDataForRead(int deviceId);

    /**
     * Asks Itron for data for a list of devices, used by "Collection actions/Read attribute"
     */
    Multimap<PaoIdentifier, PointValueHolder> collectDataForRead(List<Integer> deviceId);
}
