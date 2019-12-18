package com.cannontech.services.systemDataPublisher.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;

public interface SystemDataPublisherDao {

    /**
     * This method is to execute query to get System Data. The return type of this method is kept as 
     * List<Map<String, Object>> which will help in if the query will return different data type of object.
     * 
     */
    List<Map<String, Object>> getSystemData(DictionariesField dictionariesField);
}
