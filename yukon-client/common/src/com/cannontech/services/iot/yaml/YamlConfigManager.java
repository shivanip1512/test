package com.cannontech.services.iot.yaml;

import java.util.List;
import java.util.Map;

import com.cannontech.services.iot.service.IOTPublisher;
import com.cannontech.services.iot.yaml.model.DictionariesField;

public interface YamlConfigManager {

    /**
     * This method will return the map of publisher to dictionaries values. On initialize of 
     * YamlConfigManagerImpl class the yaml configuration is read and will be returned.
     */
    Map<IOTPublisher, List<DictionariesField>> getMapOfPublisherToDictionaries();

}
