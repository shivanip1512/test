package com.cannontech.common.bulk.service.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.ArchiveDataAnalysisCallbackResult;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.ArchiveAnalysisProfileReadResult;
import com.cannontech.common.bulk.model.ArchiveData;
import com.cannontech.common.bulk.model.ArchiveDataAnalysisBackingBean;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.bulk.service.ArchiveDataAnalysisService;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;

public class ArchiveDataAnalysisServiceImpl implements ArchiveDataAnalysisService {
    private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    private AttributeService attributeService;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private BulkProcessor bulkProcessor = null;
    private RecentResultsCache<BackgroundProcessResultHolder> bpRecentResultsCache;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    private CommandRequestDeviceExecutor commandRequestExecutor;
    private RecentResultsCache<ArchiveAnalysisProfileReadResult> crdRecentResultsCache;
    private static final Map<BuiltInAttribute, Integer> lpAttributeChannelMap;
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");
    
    static {
        Builder<BuiltInAttribute, Integer> builder = ImmutableMap.builder();
        builder.put(BuiltInAttribute.LOAD_PROFILE, 1);
        builder.put(BuiltInAttribute.PROFILE_CHANNEL_2, 2);
        builder.put(BuiltInAttribute.PROFILE_CHANNEL_3, 3);
        builder.put(BuiltInAttribute.VOLTAGE_PROFILE, 4);
        lpAttributeChannelMap = builder.build();
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
    public String runProfileReads(int analysisId, LiteYukonUser user) {
        Analysis analysis = archiveDataAnalysisDao.getAnalysisById(analysisId);
        List<DeviceArchiveData> data = archiveDataAnalysisDao.getSlotValues(analysisId);
        
        List<CommandRequestDevice> requests = getProfileRequests(analysis, data);
        
        StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup(null);
        StoredDeviceGroup failureGroup = temporaryDeviceGroupService.createTempGroup(null);
        final ArchiveAnalysisProfileReadResult result = new ArchiveAnalysisProfileReadResult(deviceGroupMemberEditorDao, 
                                                                                             deviceGroupCollectionHelper, 
                                                                                             successGroup, 
                                                                                             failureGroup, 
                                                                                             requests);
       
        CommandCompletionCallbackAdapter<CommandRequestDevice> callback = new CommandCompletionCallbackAdapter<CommandRequestDevice>() {
            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                result.commandSucceeded(command);
            }
            
            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                result.commandFailed(command);
            }
            
            @Override
            public void complete() {
                result.setComplete();
            }
            
            @Override
            public void processingExceptionOccured(String reason) {
                result.processingExceptionOccurred(reason);
            }
        };
        
        String resultId = crdRecentResultsCache.addResult(result);
        
        CommandRequestExecutionTemplate<CommandRequestDevice> creTemplate = commandRequestExecutor.getExecutionTemplate(DeviceRequestType.ARCHIVE_DATA_ANALYSIS_LP_READ, user);
        creTemplate.execute(requests, callback);
        
        return resultId;
    }
    
    @Override
    public ArchiveAnalysisProfileReadResult getProfileReadResultById(String resultId) {
        return crdRecentResultsCache.getResult(resultId);
    }
    
    private List<CommandRequestDevice> getProfileRequests(Analysis analysis, List<DeviceArchiveData> dataList) {
        List<CommandRequestDevice> commands = Lists.newArrayList();
       
        for(DeviceArchiveData data : dataList) {
            BuiltInAttribute attribute = (BuiltInAttribute)data.getAttribute();
            if(!attribute.isProfile()) {
                throw new IllegalArgumentException("Cannot create a pofile request for non-profile attribute \"" 
                                                       + attribute.getDescription() + "\"");
            }
            
            int channel = lpAttributeChannelMap.get(attribute);
            
            Deque<Interval> dateRangesNeedingReads = getDateRangesToRead(data);
            
            for(Interval dateRange : dateRangesNeedingReads) {
                analysis.getIntervalLength();
                String datesString = getDatesString(dateRange);
                String commandString = "getvalue lp channel " + channel + " " + datesString;
                
                CommandRequestDevice command = new CommandRequestDevice();
                command.setDevice(new SimpleDevice(data.getId()));
                command.setCommand(commandString);
                commands.add(command);
            }
        }
        return commands;
    }
    
    private String getDatesString(Interval dateRange) {
        String startString = formatter.print(dateRange.getStartMillis());
        String endString = formatter.print(dateRange.getEndMillis());
        return startString + " " + endString;
    }
    
    private Deque<Interval> getDateRangesToRead(DeviceArchiveData data) {
        //get date ranges that need reads
        Deque<Interval> intervals = new ArrayDeque<Interval>();
        for(ArchiveData archiveData : data.getArchiveData()) {
            //look only for intervals with no value in DB
            if(!archiveData.isDataPresent()) {
                Interval thisInterval = archiveData.getArchiveRange();
                if(intervals.isEmpty()) {
                    intervals.add(thisInterval); //stop - stop
                } else {
                    //Combine intervals if the space between is less than 2*6 full intervals, because
                    //it's more efficient to do 2 unnecessary reads (of 6 intervals) than to reset the 
                    //lp pointer in the meter.
                    //If lastInterval is less than 6 interval-durations long, treat it as though it is 6 long,
                    //since a read always returns 6 interval chunks.
                    Interval lastInterval = intervals.getLast();
                    Duration intervalLength = thisInterval.toDuration();
                    int additionalIntervals = getIntervalsLessThanSix(lastInterval, intervalLength);
                    if(lessThanTwelveIntervalsApart(lastInterval, thisInterval, intervalLength, additionalIntervals)) {
                        Interval combinedInterval = new Interval(lastInterval.getStart(), thisInterval.getEnd());
                        intervals.removeLast();
                        intervals.add(combinedInterval);
                    } else {
                        intervals.add(thisInterval);
                    }
                }
            }
        }
        return intervals;
    }
    
    /**
     * Takes an Interval representing a period of time and a Duration representing the length of a
     * load profile interval. Determines if the time period's duration is less than six LP
     * intervals in length. 
     * 
     * Returns the number of LP intervals less than six, if that is the case, or 0 if the time 
     * period is equal to or greater than six LP intervals long. The time period should be
     * divisible by the LP interval duration. If it is not, the partial interval will be treated as 
     * a full interval.
     */
    private int getIntervalsLessThanSix(Interval interval, Duration intervalLength) {
        long intervalLengthInMillis = intervalLength.getMillis();
        Duration sixIntervalsDuration = new Duration(intervalLengthInMillis * 6);
        
        Duration intervalDuration = interval.toDuration();
        if(intervalDuration.isLongerThan(sixIntervalsDuration) || intervalDuration.isEqual(sixIntervalsDuration)) {
            return 0;
        }
        
        Duration timeLessThanSix = sixIntervalsDuration.minus(intervalDuration);
        int intervalsLessThanSix = 0;
        while(timeLessThanSix.isLongerThan(intervalLength) || timeLessThanSix.isEqual(intervalLength)) {
            timeLessThanSix = timeLessThanSix.minus(intervalLength);
            intervalsLessThanSix++;
        }
        return intervalsLessThanSix;
    }
    
    /**
     * Takes two Intervals representing periods of time, a Duration representing the length of a
     * load profile interval, and an int representing a number of "extra" intervals. The Intervals
     * should be ordered such that the first Interval spans a period of time before the second
     * Interval, although the two Intervals may abut or overlap. The extra intervals value should
     * be the length of first Interval (in LP intervals) minus six, or 0 if the length of the first
     * Interval is =< six LP intervals. This is obtained through the "getIntervalsLessThanSix"
     * method.
     * 
     * Determines the number of load profile intervals between the end of the first interval and the 
     * beginning of the second interval. ExtraIntervals > 0 indicate that the firstInterval is less 
     * than six load profile intervals long. In this case, it is treated as though it were six load 
     * profile intervals long, as a single load profile read will always read at least six intervals.
     * 
     * Returns false if the space between the firstInterval and secondInterval, plus the 
     * extraIntervals, is greater than twelve load profile intervals (i.e. two LP reads). Returns
     * true if the space between intervals, plus the extraIntervals, is less than twelve load profile
     * intervals, or if the two intervals overlap or abut.
     */
    private boolean lessThanTwelveIntervalsApart(Interval firstInterval, Interval secondInterval, Duration intervalLength, int extraIntervals) {
        Interval intervalBetween = firstInterval.gap(secondInterval);
        if(intervalBetween==null) {
            //intervals overlap or abut
            return true;
        }
        Duration timeBetween = intervalBetween.toDuration();
        
        Duration twelveIntervalLength = new Duration(intervalLength.getMillis()*12);
        Duration maxDurationBetween = twelveIntervalLength;
        
        while(extraIntervals > 0) {
            maxDurationBetween = maxDurationBetween.plus(intervalLength);
            extraIntervals--;
        }
        
        return timeBetween.isShorterThan(maxDurationBetween);
    }
    
    private String startProcessor(DeviceCollection deviceCollection, Processor<YukonDevice> processor) {
        //Set up callback
        String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        ArchiveDataAnalysisCallbackResult callbackResult = new ArchiveDataAnalysisCallbackResult(resultsId, deviceCollection);
        
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
        
        SingleProcessor<YukonDevice> analysisProcessor = new SingleProcessor<YukonDevice>() {
            @Override
            public void process(YukonDevice device) throws ProcessingException {
                try {
                    LitePoint point = attributeService.getPointForAttribute(device, attribute);
                    archiveDataAnalysisDao.insertSlotValues(device.getPaoIdentifier(), analysis, point.getPointID(), excludeBadPointQualities);
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
    public void setCommandRequestExecutor(CommandRequestDeviceExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }
    
    @Resource(name="adaProfileReadRecentResultsCache")
    public void setAdaProfileReadRecentResultsCache(RecentResultsCache<ArchiveAnalysisProfileReadResult> recentResultsCache) {
        crdRecentResultsCache = recentResultsCache;
    }
}
