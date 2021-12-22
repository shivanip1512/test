package com.cannontech.yukon.system.metrics.producer;

import org.apache.logging.log4j.core.Logger;
import org.joda.time.DateTime;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.services.rfn.endpoint.MeterReadingArchiveRequestListener;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricIntervalProducer;


public class RfnMeterReadingPointDataCountProducer extends YukonMetricIntervalProducer {
    private static final Logger log = YukonLogManager.getLogger(RfnMeterReadingPointDataCountProducer.class);

    @Override
    public YukonMetric produce() {
        YukonMetric yukonMetric = null;
        if (shouldProduce()) {
            try {
                int pointDataCount = MeterReadingArchiveRequestListener.getPointDataCount();
                yukonMetric = new YukonMetric();
                yukonMetric.setPointInfo(YukonMetricPointInfo.RFN_METER_READING_ARCHIVE_REQUESTS_POINT_DATA_GENERATED_COUNT);
                yukonMetric.setTimestamp(new DateTime());
                yukonMetric.setValue(pointDataCount);
                debug(yukonMetric, log);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return yukonMetric;
    }

    @Override
    public boolean shouldProduce() {
        return true;
    }

    @Override
    public long getPeriodInMinutes() {
        return 60;
    }

    @Override
    public YukonMetricPointInfo getYukonMetricPointInfo() {
        return YukonMetricPointInfo.RFN_METER_READING_ARCHIVE_REQUESTS_POINT_DATA_GENERATED_COUNT;
    }

}
