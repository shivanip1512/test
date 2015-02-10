package com.cannontech.common.bulk.service.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.ArchiveDataAnalysisCallbackResult;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.collection.device.ArchiveDataAnalysisCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.model.AdaStatus;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.ArchiveAnalysisProfileReadResult;
import com.cannontech.common.bulk.model.ArchiveData;
import com.cannontech.common.bulk.model.ArchiveDataAnalysisBackingBean;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.bulk.service.ArchiveDataAnalysisHelper;
import com.cannontech.common.bulk.service.ArchiveDataAnalysisService;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.impl.CommandCallbackBase;
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
    private Logger log = YukonLogManager.getLogger(ArchiveDataAnalysisService.class);
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
    private ConfigurationSource configurationSource;
    private ArchiveDataAnalysisCollectionProducer adaCollectionProducer;
    private ArchiveDataAnalysisHelper archiveDataAnalysisHelper;

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
        Period intervalPeriod = archiveDataAnalysisBackingBean.getSelectedInterval();
        boolean excludeBadQualities = archiveDataAnalysisBackingBean.getExcludeBadQualities();
        
        int analysisId = archiveDataAnalysisDao.createNewAnalysis(attribute, intervalPeriod, excludeBadQualities, dateTimeRange);
        return analysisId;
    }
    
    @Override
    public int createAnalysis(int oldAnalysisId) {
        Analysis oldAnalysis = archiveDataAnalysisDao.getAnalysisById(oldAnalysisId);
        Interval dateTimeRange = oldAnalysis.getDateTimeRange();
        BuiltInAttribute attribute = oldAnalysis.getAttribute();
        Period intervalPeriod = oldAnalysis.getIntervalPeriod();
        boolean excludeBadQualities = oldAnalysis.isExcludeBadPointQualities();
        
        int analysisId = archiveDataAnalysisDao.createNewAnalysis(attribute, intervalPeriod, excludeBadQualities, dateTimeRange);
        return analysisId;
    }
    
    @Override
    public String startAnalysis(ArchiveDataAnalysisBackingBean backingBean, int analysisId) {
        String resultsId = runAnalysis(backingBean.getExcludeBadQualities(), backingBean.getSelectedAttribute(), backingBean.getDeviceCollection(), analysisId);
        
        return resultsId;
    }
    
    @Override
    public String startAnalysis(int oldAnalysisId, int newAnalysisId) {
        Analysis oldAnalysis = archiveDataAnalysisDao.getAnalysisById(oldAnalysisId);
        DeviceCollection oldDeviceCollection = adaCollectionProducer.buildDeviceCollection(oldAnalysisId);
        String resultsId = runAnalysis(oldAnalysis.isExcludeBadPointQualities(), oldAnalysis.getAttribute(), oldDeviceCollection, newAnalysisId);
        
        return resultsId;
    }
    
    private String runAnalysis(boolean excludeBadPointQualities, BuiltInAttribute attribute, DeviceCollection deviceCollection, int newAnalysisId) {
        Analysis analysis = archiveDataAnalysisDao.getAnalysisById(newAnalysisId);
        Duration sleepSeconds = configurationSource.getDuration("ADA_SLEEP_SECONDS", Duration.ZERO);
        SingleProcessor<YukonDevice> analysisProcessor = getAnalysisProcessor(excludeBadPointQualities, attribute, analysis, sleepSeconds);
        String resultsId = startProcessor(deviceCollection, analysisProcessor, newAnalysisId);
        archiveDataAnalysisDao.updateStatus(newAnalysisId, AdaStatus.RUNNING, resultsId);
        return resultsId;
    }
    
    @Override
    public String runProfileReads(final int analysisId, LiteYukonUser user) {
        Analysis analysis = archiveDataAnalysisDao.getAnalysisById(analysisId);
        List<DeviceArchiveData> data = archiveDataAnalysisDao.getSlotValues(analysisId);
        
        List<CommandRequestDevice> requests = getProfileRequests(analysis, data);
        
        StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup();
        StoredDeviceGroup failureGroup = temporaryDeviceGroupService.createTempGroup();
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
                archiveDataAnalysisDao.updateStatus(analysisId, AdaStatus.COMPLETE, null);
            }
            
            @Override
            public void processingExceptionOccured(String reason) {
                result.processingExceptionOccurred(reason);
            }
        };
        
        String resultId = crdRecentResultsCache.addResult(result);
        
        CommandRequestExecutionTemplate<CommandRequestDevice> creTemplate = commandRequestExecutor.getExecutionTemplate(DeviceRequestType.ARCHIVE_DATA_ANALYSIS_LP_READ, user);
        creTemplate.execute(requests, callback);
        archiveDataAnalysisDao.updateStatus(analysisId, AdaStatus.READING, resultId);
        
        log.info("Starting ADA profile read for analysis with ID " + analysis.getAnalysisId() + ". "
                 + requests.size() + " requests queued for " + result.getTotalCount() + " devices.");
        return resultId;
    }
    
    @Override
    public ArchiveAnalysisProfileReadResult getProfileReadResultById(String resultId) {
        return crdRecentResultsCache.getResult(resultId);
    }
    
    @Override
    public List<Instant> getIntervalEndTimes(Analysis analysis) {
        return archiveDataAnalysisHelper.getListOfRelevantDateTimes(analysis.getDateTimeRange(), analysis.getIntervalPeriod());
    }
    
    private List<CommandRequestDevice> getProfileRequests(Analysis analysis, List<DeviceArchiveData> dataList) {
        List<CommandRequestDevice> commands = Lists.newArrayList();
       
        for(DeviceArchiveData data : dataList) {
            BuiltInAttribute attribute = (BuiltInAttribute)data.getAttribute();
            if(!attribute.isReadableProfile()) {
                throw new IllegalArgumentException("Cannot create a pofile request for non-profile attribute \"" 
                                                       + attribute + "\"");
            }
            
            int channel = lpAttributeChannelMap.get(attribute);
            
            Deque<Interval> dateRangesNeedingReads = getDateRangesToRead(data);
            
            for(Interval dateRange : dateRangesNeedingReads) {
                String datesString = getDatesString(dateRange);
                String commandString = "getvalue lp channel " + channel + " " + datesString;
                
                CommandRequestDevice command = new CommandRequestDevice();
                command.setDevice(new SimpleDevice(data.getId()));
                command.setCommandCallback(new CommandCallbackBase(commandString));
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
        //get maximum distance back in time that we'll read
        Duration daysBackToReadLp = configurationSource.getDuration("ADA_MAX_LP_READ_AGE", Duration.standardDays(60));
        Instant earliestLpDateAllowed = new Instant().minus(daysBackToReadLp);
        
        //get date ranges that need reads
        Deque<Interval> intervals = new ArrayDeque<Interval>();
        int intervalSkipCount = 0;
        for(ArchiveData archiveData : data.getArchiveData()) {
            Interval thisInterval = archiveData.getArchiveRange();
            if(thisInterval.isBefore(earliestLpDateAllowed)) {
                //if the entire interval is before the earliest date allowed, skip it
                intervalSkipCount++;
                continue;
            }
            
            //look only for intervals with no value in DB
            if(!archiveData.isDataPresent()) {
                if(intervals.isEmpty()) {
                    intervals.add(thisInterval);
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
        if(intervalSkipCount > 0) {
            log.debug("Archive Data Analysis skipped " + intervalSkipCount + "intervals older than " + earliestLpDateAllowed.toDateTime().toString());
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
    
    private String startProcessor(DeviceCollection deviceCollection, Processor<YukonDevice> processor, final int analysisId) {
        //Set up callback
        String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        
        ArchiveDataAnalysisCallbackResult callbackResult = 
            new ArchiveDataAnalysisCallbackResult(resultsId, deviceCollection) {
                @Override
                public void doOnComplete() {
                    archiveDataAnalysisDao.updateStatus(analysisId, AdaStatus.COMPLETE, null);
                }
            };
        
        //Set up cache
        bpRecentResultsCache.addResult(resultsId, callbackResult);
        
        //Run analysis in background process
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<SimpleDevice>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, processor, callbackResult);
        
        return resultsId;
    }
    
    private SingleProcessor<YukonDevice> getAnalysisProcessor(final boolean excludeBadPointQualities, final BuiltInAttribute attribute, final Analysis analysis, final Duration sleepSeconds) {
        
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
                
                try {
                    if(sleepSeconds != Duration.ZERO) {
                        Thread.sleep(sleepSeconds.getMillis());
                    }
                } catch(InterruptedException ie) {
                    log.debug("Sleep interrupted during archive data analysis.", ie);
                    Thread.currentThread().interrupt();
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

    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
    @Autowired
    public void setArchiveDataAnalysisCollectionProducer(ArchiveDataAnalysisCollectionProducer adaCollectionProducer) {
        this.adaCollectionProducer = adaCollectionProducer;
    }
    
    @Autowired
    public void setArchiveDataAnalysisHelper(ArchiveDataAnalysisHelper archiveDataAnalysisHelper) {
        this.archiveDataAnalysisHelper = archiveDataAnalysisHelper;
    }
    
    @Resource(name="adaProfileReadRecentResultsCache")
    public void setAdaProfileReadRecentResultsCache(RecentResultsCache<ArchiveAnalysisProfileReadResult> recentResultsCache) {
        crdRecentResultsCache = recentResultsCache;
    }
}
