package com.cannontech.services.systemDataPublisher.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.common.device.data.collection.model.DataCollectionSummary;
import com.cannontech.common.pao.PaoType;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;
import com.google.common.collect.ImmutableSet;

public interface SystemDataPublisherDao {

    /**
     * This method is to execute query to get System Data. The return type of this method is kept as 
     * List<Map<String, Object>> which will help in if the query will return different data type of object.
     * 
     */
    List<Map<String, Object>> getSystemData(CloudDataConfiguration cloudDataConfiguration);
    
    /**
     * This method is to execute query to get System Data from Network Manager. Using Network Manager DB
     * connection we will get the result and form the system data to be pushed to topic.
     * 
     */
    List<Map<String, Object>> getNMSystemData(CloudDataConfiguration cloudDataConfiguration);
    
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
