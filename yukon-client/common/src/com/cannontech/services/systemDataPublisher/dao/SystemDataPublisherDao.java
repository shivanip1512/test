package com.cannontech.services.systemDataPublisher.dao;

import com.cannontech.common.device.data.collection.model.DataCollectionSummary;
import com.cannontech.common.pao.PaoType;
import com.google.common.collect.ImmutableSet;

public interface SystemDataPublisherDao {

    /**
     * This method returns read rates for the devices in the passed device group.
     * Read rates are the % of meters reported in defined number of days. 
     */
    DataCollectionSummary getReadRate(String deviceGroupName);

    /**
     * This method returns value for Data completeness of devices in the passed device group.
     * Count of number of times device reported in defined number of days for every hour.
     */
    double getDataCompleteness(String deviceGroupName, ImmutableSet<PaoType> paotype);
}
