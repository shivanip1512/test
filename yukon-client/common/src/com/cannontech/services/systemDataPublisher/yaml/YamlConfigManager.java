package com.cannontech.services.systemDataPublisher.yaml;

import java.util.List;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;

public interface YamlConfigManager {

    /**
     * This method will return the list of dictionaries values. On initialize of YamlConfigManagerImpl class the yaml
     * configuration is read and will be returned.
     */
    List<DictionariesField> getDictionariesFields();

}
