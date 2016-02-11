package com.cannontech.development.service;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.development.dao.impl.RphSimulatorDaoImpl.RphSimulatorPointType;
import com.cannontech.development.model.BulkFakePointInjectionDto;
import com.cannontech.message.dispatch.message.PointData;

public interface BulkPointDataInjectionService {
    void excecuteInjection(BulkFakePointInjectionDto bulkInjection);

    void excecuteInjectionByDevice(BulkFakePointInjectionDto bulkInjection);

    List<PointData> createPointData(Attribute attribute, double valueHigh, double valueLow, SimpleDevice device,
            List<Instant> timestamps);

    /**
     * Insert point data for existing devices.
     */
    void insertPointData(List<Integer> devicesId, RphSimulatorPointType type, double valueLow, double valueHigh,
            Instant start, Instant stop, Duration standardDuration);
}
