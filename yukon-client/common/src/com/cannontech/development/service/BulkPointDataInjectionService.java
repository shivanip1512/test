package com.cannontech.development.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.development.model.BulkFakePointInjectionDto;
import com.cannontech.message.dispatch.message.PointData;

public interface BulkPointDataInjectionService {
    void excecuteInjection(BulkFakePointInjectionDto bulkInjection);

    void excecuteInjectionByDevice(BulkFakePointInjectionDto bulkInjection);

    List<PointData> createPointData(Attribute attribute, double valueHigh, double valueLow, SimpleDevice device,
            List<Instant> timestamps);
}
