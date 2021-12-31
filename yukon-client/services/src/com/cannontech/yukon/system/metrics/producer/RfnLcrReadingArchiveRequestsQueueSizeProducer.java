package com.cannontech.yukon.system.metrics.producer;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.yukon.system.metrics.helper.YukonMetricHelper;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricIntervalProducer;

public class RfnLcrReadingArchiveRequestsQueueSizeProducer extends YukonMetricIntervalProducer {
    private static final Logger log = YukonLogManager.getLogger(RfnLcrReadingArchiveRequestsQueueSizeProducer.class);
    private static final String queueName = JmsApiDirectory.RFN_LCR_READ_ARCHIVE.getQueueName();
    @Autowired private YukonMetricHelper helper;

    @Override
    public YukonMetric produce() {
        long queueSize = helper.getQueueSize(ApplicationId.SERVICE_MANAGER, queueName);
        YukonMetric metric = new YukonMetric(getYukonMetricPointInfo(), queueSize, new DateTime());
        debug(metric, log);
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
    public long getPeriodInMinutes() {
        return 60;
    }
}
