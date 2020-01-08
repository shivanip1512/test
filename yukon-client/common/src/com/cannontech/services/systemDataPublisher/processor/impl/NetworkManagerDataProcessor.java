package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.dao.impl.SystemDataProcessorHelper;
import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;
import com.cannontech.services.systemDataPublisher.yaml.model.SystemDataPublisherFrequency;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

@Service
public class NetworkManagerDataProcessor extends SystemDataProcessor {

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private SystemDataPublisherDao systemDataPublisherDao;
    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    private static final Logger log = YukonLogManager.getLogger(NetworkManagerDataProcessor.class);

    @Override
    public void runScheduler(Entry<SystemDataPublisherFrequency, List<DictionariesField>> entry) {
        String networkManagerHost = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_HOSTNAME);
        String userName = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_USER);
        String password = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_PASSWORD);
        String url = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_URL);
        if (StringUtils.isNotEmpty(url) || (StringUtils.isNotEmpty(networkManagerHost) && StringUtils.isNotEmpty(userName)
                && StringUtils.isNotEmpty(password))) {
            executor.scheduleAtFixedRate(() -> {
                buildAndPublishSystemData(entry.getValue());
            }, 0, entry.getKey().getHours(), TimeUnit.HOURS);
        } else {
            log.info("Network Manager DB configurations not found. Not able to publish data to Cloud Service");
        }
    }

    @Override
    public SystemData buildSystemData(DictionariesField dictionariesField) {
        List<Map<String, Object>> queryResult = null;
        SystemData nmSystemData = null;
        try {
            queryResult = systemDataPublisherDao.getNMSystemData(dictionariesField);
            nmSystemData = SystemDataProcessorHelper.processQueryResult(dictionariesField, queryResult);

        } catch (Exception e) {
            log.debug("Error while executing query." + e);
        }
        return nmSystemData;
    }

}
