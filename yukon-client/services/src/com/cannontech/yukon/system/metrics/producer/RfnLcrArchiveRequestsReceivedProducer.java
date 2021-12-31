package com.cannontech.yukon.system.metrics.producer;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.services.rfn.endpoint.LcrReadingArchiveRequestListener;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricIntervalProducer;

@Service
public class RfnLcrArchiveRequestsReceivedProducer extends YukonMetricIntervalProducer {
    private static final Logger log = YukonLogManager.getLogger(RfnLcrArchiveRequestsReceivedProducer.class);

    @Override
    public YukonMetric produce() {
        YukonMetric metric = new YukonMetric(getYukonMetricPointInfo(),
                LcrReadingArchiveRequestListener.getArchiveRequestsReceivedCount(), new DateTime());
        debug(metric, log);
        return metric;
    }

    @Override
    public boolean shouldProduce() {
        return true;
    }

    @Override
    public YukonMetricPointInfo getYukonMetricPointInfo() {
        return YukonMetricPointInfo.RFN_LCR_READING_ARCHIVE_REQUESTS_RECEIVED;
    }

    @Override
    public long getPeriodInMinutes() {
        return 3;
    }
}