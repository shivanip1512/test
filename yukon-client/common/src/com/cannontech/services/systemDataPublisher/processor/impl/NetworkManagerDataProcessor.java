package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.dao.impl.SystemDataProcessorHelper;
import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

@Service
public class NetworkManagerDataProcessor implements SystemDataProcessor {

    @Autowired private SystemDataPublisherDao systemDataPublisherDao;
    private static final Logger log = YukonLogManager.getLogger(NetworkManagerDataProcessor.class);

    @Override
    public SystemData buildSystemData(CloudDataConfiguration cloudDataConfiguration) {
        List<Map<String, Object>> queryResult = null;
        SystemData nmSystemData = null;
        try {
            queryResult = systemDataPublisherDao.getNMSystemData(cloudDataConfiguration);
            nmSystemData = SystemDataProcessorHelper.processQueryResult(cloudDataConfiguration, queryResult);

        } catch (Exception e) {
            log.debug("Error while executing query." + e);
        }
        return nmSystemData;
    }

    @Override
    public boolean supportsField(String field) {
        return false;
    }

}
