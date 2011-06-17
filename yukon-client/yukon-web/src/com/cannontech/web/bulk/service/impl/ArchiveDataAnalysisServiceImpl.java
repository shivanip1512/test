package com.cannontech.web.bulk.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.ArchiveDataAnalysisCallbackResult;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.ArchiveData;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.bulk.model.DevicePointValuesHolder;
import com.cannontech.common.bulk.model.ReadType;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.web.bulk.model.ArchiveDataAnalysisBackingBean;
import com.cannontech.web.bulk.service.ArchiveDataAnalysisService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ArchiveDataAnalysisServiceImpl implements ArchiveDataAnalysisService {
    private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    private AttributeService attributeService;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private BulkProcessor bulkProcessor = null;
    private RecentResultsCache<BackgroundProcessResultHolder> bpRecentResultsCache;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    private RawPointHistoryDao rawPointHistoryDao;
    private PaoDao paoDao;
    private static Map<BuiltInAttribute, Integer> lpAttributeChannelMap;
    
    {
        lpAttributeChannelMap = Maps.newLinkedHashMap();
        lpAttributeChannelMap.put(BuiltInAttribute.LOAD_PROFILE, 1);
        lpAttributeChannelMap.put(BuiltInAttribute.PROFILE_CHANNEL_2, 2);
        lpAttributeChannelMap.put(BuiltInAttribute.PROFILE_CHANNEL_3, 3);
        lpAttributeChannelMap.put(BuiltInAttribute.VOLTAGE_PROFILE, 4);
    }
    
    @Override
    public int createAnalysis(ArchiveDataAnalysisBackingBean archiveDataAnalysisBackingBean) {
        Instant start = archiveDataAnalysisBackingBean.getStartInstant();
        Instant stop = archiveDataAnalysisBackingBean.getStopInstant();
        Interval dateTimeRange = new Interval(start, stop);
        BuiltInAttribute attribute = archiveDataAnalysisBackingBean.getSelectedAttribute();
        Duration intervalDuration = archiveDataAnalysisBackingBean.getSelectedIntervalDuration();
        boolean excludeBadQualities = archiveDataAnalysisBackingBean.getExcludeBadQualities();
        
        int analysisId = archiveDataAnalysisDao.createNewAnalysis(attribute, intervalDuration, excludeBadQualities, dateTimeRange);
        return analysisId;
    }
    
    @Override
    public String startAnalysis(ArchiveDataAnalysisBackingBean archiveDataAnalysisBackingBean, int analysisId) {
        Analysis analysis = archiveDataAnalysisDao.getAnalysisById(analysisId);
        SingleProcessor<YukonDevice> analysisProcessor = getAnalysisProcessor(archiveDataAnalysisBackingBean, analysis);
        String resultsId = startProcessor(archiveDataAnalysisBackingBean.getDeviceCollection(), analysisProcessor);
        return resultsId;
    }
    
    @Override
    public List<DevicePointValuesHolder> getDevicePointValuesList(List<DeviceArchiveData> dataList) {
        List<DevicePointValuesHolder> devicePointValuesList = Lists.newArrayList();
        
        for(DeviceArchiveData data : dataList) {
            int paoId = data.getId().getPaoId();
            LiteYukonPAObject pao = paoDao.getLiteYukonPAO(paoId);
            DevicePointValuesHolder devicePointValues = new DevicePointValuesHolder(pao.getPaoName());
            
            List<Double> pointValues = Lists.newArrayList();
            
            for(ArchiveData deviceData : data.getArchiveData()) {
                Double pointValue = null;
                Integer changeId = deviceData.getChangeId();
                
                if(changeId != null) {
                    PointValueHolder pointValueHolder = rawPointHistoryDao.getPointValue(changeId);
                    pointValue = pointValueHolder.getValue();
                }
                
                pointValues.add(pointValue);
            }
            devicePointValues.setPointValues(pointValues);
            devicePointValuesList.add(devicePointValues);
        }
        return devicePointValuesList;
    }
    
    @Override
    public Interval getDateTimeRangeForDisplay(Interval dateRange, Duration intervalLength) {
        DateTime newStart = dateRange.getStart().minus(intervalLength);
        DateTime newEnd = dateRange.getEnd().minus(intervalLength);

        return new Interval(newStart, newEnd);
    }
    
    private String startProcessor(DeviceCollection deviceCollection, Processor<YukonDevice> processor) {
        //Set up callback
        String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup(null);
        StoredDeviceGroup processingExceptionGroup = temporaryDeviceGroupService.createTempGroup(null);
        ArchiveDataAnalysisCallbackResult callbackResult = new ArchiveDataAnalysisCallbackResult(resultsId,
                                                                                                 deviceCollection,
                                                                                                 successGroup,
                                                                                                 processingExceptionGroup,
                                                                                                 deviceGroupMemberEditorDao,
                                                                                                 deviceGroupCollectionHelper);
        
        //Set up cache
        bpRecentResultsCache.addResult(resultsId, callbackResult);
        
        //Run analysis in background process
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<SimpleDevice>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, processor, callbackResult);
        
        return resultsId;
    }
    
    private SingleProcessor<YukonDevice> getAnalysisProcessor(ArchiveDataAnalysisBackingBean backingBean, final Analysis analysis) {
        final boolean excludeBadPointQualities = backingBean.getExcludeBadQualities();
        final BuiltInAttribute attribute = backingBean.getSelectedAttribute();
        final Interval dateRange = backingBean.getDateRange();
        
        SingleProcessor<YukonDevice> analysisProcessor = new SingleProcessor<YukonDevice>() {
            @Override
            public void process(YukonDevice device) throws ProcessingException {
                int deviceId = device.getPaoIdentifier().getPaoId();
                
                try {
                    LitePoint point = attributeService.getPointForAttribute(device, attribute);
                    Map<Instant, Integer> map = archiveDataAnalysisDao.getDeviceSlotValues(analysis.getAnalysisId(), point.getPointID(), excludeBadPointQualities);
                    DeviceArchiveData data = new DeviceArchiveData(device.getPaoIdentifier(), attribute, dateRange);
                    for(Entry<Instant, Integer> entry : map.entrySet()) {
                        Instant date = entry.getKey();
                        Interval intervalRange = new Interval(date, analysis.getIntervalLength());
                        Integer changeId = entry.getValue();
                        ArchiveData readData = new ArchiveData(intervalRange, ReadType.DATA_PRESENT, changeId);
                        data.addArchiveData(readData);
                    }
                    archiveDataAnalysisDao.insertSlotValues(deviceId, analysis.getAnalysisId(), data);
                } catch(IllegalUseOfAttribute illegal) {
                    String error = "Invalid attribute " + attribute + " for device with id " + device.getPaoIdentifier().getPaoId();
                    throw new ProcessingException(error);
                }
            }
        };
        
        return analysisProcessor;
    }
    
    @Resource(name="recentResultsCache")
    public void setBackgroundProcessRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.bpRecentResultsCache = recentResultsCache;
    }
    
    @Resource(name="transactionPerItemProcessor")
    public void setBulkProcessor(BulkProcessor bulkProcessor) {
        this.bulkProcessor = bulkProcessor;
    }
    
    @Autowired
    public void setTemporaryDeviceGroupService(TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }
    
    @Autowired
    public void setArchiveDataAnalysisDao(ArchiveDataAnalysisDao archiveDataAnalysisDao) {
        this.archiveDataAnalysisDao = archiveDataAnalysisDao;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }
    
    @Autowired
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Autowired
    public void setRawPointHistoryDao(RawPointHistoryDao rawPointHistoryDao) {
        this.rawPointHistoryDao = rawPointHistoryDao;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
