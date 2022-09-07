package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricIntervalProducer;

@Service
public abstract class DeviceGroupDataProcessor extends YukonMetricIntervalProducer {
    private static final Logger log = YukonLogManager.getLogger(DeviceGroupDataProcessor.class);

    @Autowired private DeviceGroupService deviceGroupService;

    @Override
    public YukonMetric produce() {
        YukonMetric metric = new YukonMetric(getYukonMetricPointInfo(), getData(), new DateTime());
        debug(metric, log);
        return metric;
    }

    @Override
    public boolean shouldProduce() {
        return true;
    }

    /**
     * Make service call to get data
     */
    private int getData() {
        DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(getDeviceGroupName());
        return deviceGroupService.getDeviceCount(Arrays.asList(deviceGroup));
    }

    public abstract String getDeviceGroupName();
}