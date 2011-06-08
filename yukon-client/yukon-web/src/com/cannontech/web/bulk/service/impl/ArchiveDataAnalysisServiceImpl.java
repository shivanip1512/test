package com.cannontech.web.bulk.service.impl;

import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
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
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.web.bulk.model.ArchiveDataAnalysisBackingBean;
import com.cannontech.web.bulk.service.ArchiveDataAnalysisService;

public class ArchiveDataAnalysisServiceImpl implements ArchiveDataAnalysisService {
    private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    private AttributeService attributeService;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private BulkProcessor bulkProcessor = null;
    private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    
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
        recentResultsCache.addResult(resultsId, callbackResult);
        
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
                        //ArchiveReadStatus readStatus = new ArchiveReadStatus(date, ReadType.DATA_PRESENT, changeId);
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
    public void setRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
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
}
