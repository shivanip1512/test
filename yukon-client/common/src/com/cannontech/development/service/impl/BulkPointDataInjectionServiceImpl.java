package com.cannontech.development.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.development.model.BulkFakePointInjectionDto;
import com.cannontech.development.service.BulkPointDataInjectionService;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class BulkPointDataInjectionServiceImpl implements BulkPointDataInjectionService {
    private static final Logger log = YukonLogManager.getLogger(BulkPointDataInjectionServiceImpl.class);
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private PaoDao paoDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private AttributeService attributeService;
    
    @Override
    public void excecuteInjection(BulkFakePointInjectionDto bulkInjection) {
        List<LitePoint> litePoints;
        if (!CollectionUtils.isEmpty(bulkInjection.getYukonPaos())) {
            litePoints = getLitePointsFromPaosWithAttribute(bulkInjection.getYukonPaos(), bulkInjection.getAttribute());
        } else {
            litePoints = getLitePointListOfDevicesInGroupWithAttribute(bulkInjection);
        }
        bulkInjection.setInjectionCount(0);
        Map<Integer, Double> valueMap = Maps.newHashMap();
        Random rand = new Random();
        Instant iterableInstant = new Instant(bulkInjection.getStart());
        while (iterableInstant.isBefore(bulkInjection.getStop())) {
            for (LitePoint litePoint : litePoints) {
                PointData pointData = new PointData();
                pointData.setId(litePoint.getPointID());
                long randWindow = (long) getRandomWithinRange(0, bulkInjection.getPeriodWindow()
                        .toStandardDuration().getMillis());
                pointData.setTime(iterableInstant.plus(randWindow).toInstant().toDate());
                Collections.shuffle(bulkInjection.getPointQualities());
                pointData.setPointQuality(bulkInjection.getPointQualities().get(0));
                double value = 0.0;
                if (bulkInjection.getAlgorithm().equalsIgnoreCase("normal")) {
                    do {
                        double nextGaussian = rand.nextGaussian();
                        double standDeviation =
                            (bulkInjection.getValueHigh() - bulkInjection.getValueLow()) / 6; // +-3 standard deviations
                        value = nextGaussian * standDeviation + bulkInjection.getMean();
                    } while (value < bulkInjection.getValueLow()
                             || value > bulkInjection.getValueHigh());
                    if (bulkInjection.isIncremental()) {
                        Double mappedValue = valueMap.get(litePoint.getPointID());
                        if (mappedValue != null) {
                            value = mappedValue + value;
                        }
                        valueMap.put(litePoint.getPointID(), value);
                    }
                } else {
                    value = getRandomWithinRange(bulkInjection.getValueLow(), bulkInjection.getValueHigh());
                }
                
                double roundedVal = getRoundedValue(value, bulkInjection.getDecimalPlaces());
                pointData.setValue(roundedVal);
                pointData.setTagsPointMustArchive(bulkInjection.isArchive());
                pointData.setType(litePoint.getPointTypeEnum().getPointTypeId());
                dynamicDataSource.putValue(pointData);
                log.info("point data sent from bulk injector: " + pointData);
                bulkInjection.incrementInjectionCount();
            }
            Duration duration = bulkInjection.getPeriod().toStandardDuration();
            iterableInstant = iterableInstant.plus(duration);
        }
        log.info("Number of points sent from bulk injector: " + bulkInjection.getInjectionCount());
    }
    
    private double getRoundedValue(double value, int decimalPlaces) {
        double scaler = Math.pow(10, decimalPlaces);
        double roundedValue = (double)Math.round(value * scaler) / scaler;
        return roundedValue;
    }

    private List<LitePoint> getLitePointListOfDevicesInGroupWithAttribute(BulkFakePointInjectionDto bulkInjection) {
        DeviceGroup group = deviceGroupService.resolveGroupName(bulkInjection.getGroupName());
        List<SimpleDevice> supportedDevices =
            attributeService.getDevicesInGroupThatSupportAttribute(group,
                                                                   bulkInjection.getAttribute());
        return getLitePointsFromSimpleDevicesWithAttribute(supportedDevices, bulkInjection.getAttribute());
    }

    private List<LitePoint> getLitePointsFromSimpleDevicesWithAttribute(List<SimpleDevice> simpleDevices, Attribute attribute) {
        List<LitePoint> litePoints = Lists.newArrayList();
        for (SimpleDevice simpleDevice : simpleDevices) {
            YukonPao yukonPao = paoDao.getYukonPao(simpleDevice.getDeviceId());
            LitePoint point = attributeService.getPointForAttribute(yukonPao, attribute);
            litePoints.add(point);
        }
        return litePoints;
    }

    private List<LitePoint> getLitePointsFromPaosWithAttribute(List<YukonPao> yukonPaos, Attribute attribute) {
        List<LitePoint> litePoints = Lists.newArrayList();
        for (YukonPao yukonPao : yukonPaos) {
            LitePoint point = attributeService.getPointForAttribute(yukonPao, attribute);
            litePoints.add(point);
        }
        return litePoints;
    }

    private double getRandomWithinRange(double min, double max) {
        return (min + (int) (Math.random() * ((max - min) + 1)));
    }
}
