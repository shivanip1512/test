package com.cannontech.amr.rfn.service;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.amr.rfn.message.read.ChannelDataStatus;
import com.cannontech.amr.rfn.message.read.DatedChannelData;
import com.cannontech.amr.rfn.model.CalculationData;
import com.cannontech.amr.rfn.model.RfnMeterPlusReadingData;
import com.cannontech.amr.rfn.service.pointmapping.PointValueHandler;
import com.cannontech.amr.rfn.service.pointmapping.UnitOfMeasureToPointMapper;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointValue;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * The purpose of this class is to build {@link PointData} objects to store
 * meter reading values in Yukon.  
 * 
 * {@link #convert(RfnMeterPlusReadingData, List)} simply converts the channel data 
 * received into the corresponding point value and will also return a list of 
 * {@link CalculationData} objects if that particular point is used in the 
 * calculation of another point.
 */
public class RfnChannelDataConverter {
    
    @Autowired private UnitOfMeasureToPointMapper unitOfMeasureToPointMapper;
    @Autowired private AttributeService attributeService;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    private ImmutableSet<PaoTypePointIdentifier> calculationContributors;
    private static final Logger log = YukonLogManager.getLogger(RfnChannelDataConverter.class);
    
    public List<CalculationData> convert(RfnMeterPlusReadingData reading, List<? super PointData> toArchive) {
        
        List<CalculationData> calculationData = Lists.newArrayList();
        
        List<ChannelData> nonDatedChannelData = reading.getRfnMeterReadingData().getChannelDataList();
        List<? extends ChannelData> datedChannelData = reading.getRfnMeterReadingData().getDatedChannelDataList();
        
        List<ChannelData> allChannelData = Lists.newArrayList();
        
        if (nonDatedChannelData != null) {
            allChannelData.addAll(nonDatedChannelData);
        }
        
        if (datedChannelData != null) {
            allChannelData.addAll(datedChannelData);
        }
        
        Instant readingInstant = new Instant(reading.getRfnMeterReadingData().getTimeStamp());
        RfnDevice rfnDevice = reading.getRfnDevice();
        
        for (ChannelData channelData : allChannelData) {
            if (log.isDebugEnabled()) {
                log.debug("Processing " + channelData + " for " + rfnDevice);
            }
            ChannelDataStatus status = channelData.getStatus();
            if (status == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Received null status for channelData, skipping");
                }
                continue;
            }
            if (!status.isOk()) {
                if (log.isDebugEnabled()) {
                    log.debug("Received status of " + status + " for channelData, skipping");
                }
                continue;
            }
            
            PointValueHandler pointValueHandler = unitOfMeasureToPointMapper.findMatch(rfnDevice, channelData);
            if (pointValueHandler == null) {
                log.debug("No PointValueHandler for this channelData");
                continue;
            }
            if (log.isDebugEnabled()) {
                log.debug("Got PointValueHandler " + pointValueHandler);
            }
            
            PaoPointIdentifier ppi = pointValueHandler.getPaoPointIdentifier();
            LitePoint point;
            try {
                point = pointDao.getLitePoint(ppi);
            } catch (NotFoundException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Unable to find point for channelData: " + channelData);
                }
                continue;
            }
            
            PointData pointData = new PointData();
            pointData.setId(point.getPointID());
            pointData.setPointQuality(PointQuality.Normal);
            double value = pointValueHandler.convert(channelData.getValue());

            Double multiplier = point.getMultiplier();
            if (multiplier != null) {
                value *= point.getMultiplier();
            }
            Double dataOffset = point.getDataOffset();
            if (dataOffset != null) {
                value += point.getDataOffset();
            }
            int meterDigits = point.getDecimalDigits();
            if (meterDigits > 0) {
                value %= Math.pow(10, meterDigits);
            }
            pointData.setValue(value);
            if (channelData instanceof DatedChannelData) {
                DatedChannelData dated = (DatedChannelData)channelData;
                pointData.setTime(new Instant(dated.getTimeStamp()).toDate());
            } else {
                pointData.setTime(readingInstant.toDate());
            }
            pointData.setType(ppi.getPointIdentifier().getPointType().getPointTypeId());
            pointData.setTagsPointMustArchive(true); // temporary solution
            
            if (log.isDebugEnabled()) {
                log.debug("PointData converted: " + pointData);
            }
            
            toArchive.add(pointData);
            
            /* If this point is used to calculate other points, return it. */
            PaoTypePointIdentifier ptpi = PaoTypePointIdentifier.of(ppi.getPaoIdentifier().getPaoType(), ppi.getPointIdentifier());
            if (calculationContributors.contains(ptpi)) {
                calculationData.add(CalculationData.of(PaoPointValue.of(ppi, pointData), reading.getRfnMeterReadingData().getRecordInterval()));
            }
        }
        
        return calculationData;
    }
    
    @PostConstruct
    private void setCalculationContributors() {
        Set<PaoType> types = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.RFN_POINT_CALCULATION);
        ImmutableSet<BuiltInAttribute> attributes = ImmutableSet.of(BuiltInAttribute.SUM_KWH, BuiltInAttribute.DELIVERED_KWH, BuiltInAttribute.RECEIVED_KWH,
                                                                    BuiltInAttribute.NET_KWH, BuiltInAttribute.SUM_KVARH, BuiltInAttribute.SUM_KVAH,
                                                                    BuiltInAttribute.KVARH, BuiltInAttribute.USAGE_WATER);
        
        ImmutableSet.Builder<PaoTypePointIdentifier> b = ImmutableSet.builder();
        for (PaoType type : types) {
            for (BuiltInAttribute attribute : attributes) {
                try {
                    PaoTypePointIdentifier ptpi = attributeService.getPaoTypePointIdentifierForAttribute(type, attribute);
                    b.add(ptpi);
                } catch (IllegalUseOfAttribute e) {
                    LogHelper.debug(log, "Attribute: [%s] not valid for pao type: [%s]", attribute, type);
                }
            }
        }
        calculationContributors = b.build();
    }
    
}