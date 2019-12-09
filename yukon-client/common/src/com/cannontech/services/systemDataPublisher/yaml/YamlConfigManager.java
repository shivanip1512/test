package com.cannontech.services.systemDataPublisher.yaml;

import java.util.List;
import java.util.Map;

import com.cannontech.services.systemDataPublisher.service.SystemDataPublisher;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;

public interface YamlConfigManager {

    /**
     * This method will return the map of publisher to dictionaries values. On initialize of 
     * YamlConfigManagerImpl class the yaml configuration is read and will be returned.
     */
    Map<SystemDataPublisher, List<DictionariesField>> getMapOfPublisherToDictionaries();

}
