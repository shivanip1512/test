package com.cannontech.yukon.system.metrics.producer;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.yukon.system.metrics.helper.YukonMetricHelper;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricThresholdProducer;

public class RfnLcrReadingArchiveRequestsQueueSizeProducer extends YukonMetricThresholdProducer {
    private static final Logger log = YukonLogManager.getLogger(RfnLcrReadingArchiveRequestsQueueSizeProducer.class);
    private static final String queueName = JmsApiDirectory.RFN_LCR_READ_ARCHIVE.getQueueName();
    private static final String key = "yukonMetricKey";
    private static ConcurrentHashMap<String, YukonMetric> yukonMetricCache = new ConcurrentHashMap<String, YukonMetric>(1);
    private static long thresholdValue;
    private static int escapeValveTime = 15;
    private static int intervalAfterEscapeValve = 5;
    private boolean shouldNotifyForThresholdValue;
    private DateTime firstNotifiedTime;

    @Autowired private YukonMetricHelper helper;

    @PostConstruct
    public void init() {
        thresholdValue = helper.getThresholdValueForArchiveRequests();
    }

    @Override
    public YukonMetric produce() {
        long queueSize = Long.valueOf(String.valueOf(yukonMetricCache.get(key).getValue()));
        YukonMetric metric = new YukonMetric(getYukonMetricPointInfo(), queueSize, new DateTime());
        if (queueSize > thresholdValue) {
            debug(metric, log, thresholdValue);
        } else {
            debug(metric, log);
        }
        return metric;
    }

    @Override
    public boolean shouldProduce() {
        return true;
    }

    @Override
    public YukonMetricPointInfo getYukonMetricPointInfo() {
        return YukonMetricPointInfo.RFN_LCR_READING_ARCHIVE_REQUESTS_QUEUE_SIZE;
    }

    @Override
    public boolean watch() {
        long currentQueueSize = Long.valueOf(String.valueOf(yukonMetricCache.get(key).getValue()));

        if (currentQueueSize > thresholdValue && !shouldNotifyForThresholdValue) {
            shouldNotifyForThresholdValue = true;
            firstNotifiedTime = new DateTime();
        }
        if (currentQueueSize < thresholdValue) {
            shouldNotifyForThresholdValue = false;
        }
        boolean escapeValveFlag = false;
        if (shouldNotifyForThresholdValue) {
            escapeValveFlag = helper.checkForEscapeValve(escapeValveTime, firstNotifiedTime, intervalAfterEscapeValve);
        }
        return shouldNotifyForThresholdValue && escapeValveFlag;
    }

    @Override
    protected YukonMetric checkPreviousValue() {
        long currentQueueSize = helper.getQueueSize(ApplicationId.SERVICE_MANAGER, queueName);
        YukonMetric cachedValue = yukonMetricCache.get(key);
        YukonMetric currentValue = new YukonMetric(getYukonMetricPointInfo(), currentQueueSize, DateTime.now());
        yukonMetricCache.put(key, currentValue);
        if (cachedValue != null) {
            long cachedQueueSize = Long.valueOf(String.valueOf(cachedValue.getValue()));
            if ((cachedQueueSize < thresholdValue && currentQueueSize > thresholdValue)) {
                return cachedValue;
            }
            if (cachedQueueSize > thresholdValue && currentQueueSize < thresholdValue) {
                return currentValue;
            }
        }
        return null;
    }

    @Override
    public long getPeriodInMinutes() {
        return 60;
    }

    @Override
    public boolean shouldGenerateIntervalData() {
        return true;
    }

}
