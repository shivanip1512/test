package com.cannontech.common.validation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.definition.model.PaoPointIdentifier;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.service.PointReadService;
import com.cannontech.common.events.loggers.ValidationEventLogService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.WaitableExecutor;
import com.cannontech.common.validation.dao.RphTagDao;
import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.impl.YukonPaoRowMapper;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.user.UserUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.ImmutableList.Builder;

@ManagedResource
public class RawPointHistoryValidationService {
    private long changeIdChunkSize = 20000;
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private PointReadService pointReadService;
    private ScheduledExecutorService executorService;
    private ValidationMonitorDao validationMonitorDao;
    private AttributeService attributeService;
    private RphTagDao rphTagDao;
    private RawPointHistoryDao rawPointHistoryDao;
    private PersistedSystemValueDao persistedSystemValueDao;
    private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    private ValidationEventLogService validationEventLogService;
    private PaoLoadingService paoLoadingService;
    
    private AtomicLong changeIdsEvaluated = new AtomicLong(0);
    private AtomicLong rowsPulled = new AtomicLong(0);
    private AtomicLong lastChunkSize = new AtomicLong(0);
    private AtomicLong lastChunkCommitted = new AtomicLong(0);
    private AtomicLong totalMsProcessing = new AtomicLong(0);
    private AtomicLong workingExecutions = new AtomicLong(0);
    private AtomicLong sleepingExecutions = new AtomicLong(0);
    
    private static Logger log = YukonLogManager.getLogger(RawPointHistoryValidationService.class);
    
    private static ParameterizedRowMapper<RawPointHistoryWrapper> rawPointHistoryRowMapper = new ParameterizedRowMapper<RawPointHistoryWrapper>()  {
        public RawPointHistoryWrapper mapRow(ResultSet rs, int rowNum) throws SQLException {

            RawPointHistoryWrapper wrapper = new RawPointHistoryWrapper();
            wrapper.timestamp = new DateTime(rs.getTimestamp("TimeStamp"));
            wrapper.value = rs.getDouble("value");
            wrapper.changeId = rs.getInt("ChangeId");

            return wrapper;
        }
    };

    @PostConstruct
    public void init() throws InterruptedException {
        // wipe extraneous RphTags from database
        long lastChangeIdProcessed = persistedSystemValueDao.getLongValue(PersistedSystemValueKey.VALIDATION_ENGINE_LAST_CHANGE_ID);
        lastChunkCommitted.set(lastChangeIdProcessed);
        int tagsCleared = rphTagDao.clearTagsAfter(lastChangeIdProcessed, RphTag.getAllValidation());
        validationEventLogService.validationEngineStartup(lastChangeIdProcessed, tagsCleared);
        
        // consider using global here
        executorService = Executors.newScheduledThreadPool(8); // must be greater than 1
        
        executorService.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                boolean didSomething;
                long startingIdForLoggingPurposes = -1;
                sleepingExecutions.incrementAndGet();
                do {
                    long newLast;
                    try {
                        workingExecutions.incrementAndGet();
                        long lastChangeIdProcessed = persistedSystemValueDao.getLongValue(PersistedSystemValueKey.VALIDATION_ENGINE_LAST_CHANGE_ID);
                        startingIdForLoggingPurposes = lastChangeIdProcessed + 1;
                        newLast = processChunkOfRows(lastChangeIdProcessed);
                        String logMessage = "Processed " + lastChangeIdProcessed + " to " + newLast + " (" + changeIdsEvaluated + ", " + rowsPulled + ")";
                        if (lastChangeIdProcessed == newLast) {
                            log.debug(logMessage);
                        } else {
                            log.info(logMessage);
                        }
                        didSomething = newLast != lastChangeIdProcessed;
                        lastChunkSize.set(newLast - lastChangeIdProcessed);
                        lastChangeIdProcessed = newLast;
                        persistedSystemValueDao.setValue(PersistedSystemValueKey.VALIDATION_ENGINE_LAST_CHANGE_ID, lastChangeIdProcessed);
                        lastChunkCommitted.set(lastChangeIdProcessed);
                    } catch (Exception e) {
                        log.info("Unable to process from " + startingIdForLoggingPurposes + ", sleeping", e);
                        didSomething = false;
                    }
                } while (didSomething);
            }
        }, 2, 1, TimeUnit.MINUTES);

    }

    public static class RawPointHistoryWorkUnit {
        public PaoPointIdentifier paoPointIdentifier;
        public int pointId;
        public RawPointHistoryWrapper thisValue;
    }

    public static class RawPointHistoryWrapper {
        public DateTime timestamp;
        public double value;
        public int changeId;

        public double getValue() {
            return value;
        }

        public DateTime getTime() {
            return timestamp;
        }
        
        @Override
        public String toString() {
            return String.format("%d:%.1f:%s", changeId, value, timestamp);
        }
    }
    
    public static class AnalysisResult {
        public boolean peakInTheMiddle;
        public boolean considerReRead;

        public AnalysisResult(boolean peakInTheMiddle, boolean considerReRead) {
            this.peakInTheMiddle = peakInTheMiddle;
            this.considerReRead = considerReRead;
        }
    }

    private long processChunkOfRows(long lastChangeIdProcessed) throws ProcessingException {
        SqlStatementBuilder sql1 = new SqlStatementBuilder();
        sql1.append("select max(changeId) from RawPointHistory");
        long maxChangeId = yukonJdbcTemplate.queryForLong(sql1);
        
        final long stopChangeId = Ordering.natural().min(lastChangeIdProcessed + changeIdChunkSize, maxChangeId);

        if (stopChangeId <= lastChangeIdProcessed) {
            return lastChangeIdProcessed;
        }
        
        final SetMultimap<ValidationMonitor, Integer> deviceGroupCache = validationMonitorDao.loadValidationMonitors();

        SqlStatementBuilder sql2 = new SqlStatementBuilder();
        sql2.append("select rph.ChangeId, rph.Value, rph.Timestamp, ");
        sql2.append("  p.PAObjectID, ypo.Type, p.POINTTYPE, p.POINTID, p.POINTOFFSET, p.POINTTYPE");
        sql2.append("from RAWPOINTHISTORY rph");
        sql2.append("  join POINT p on rph.POINTID = p.POINTID");
        sql2.append("  join YukonPAObject ypo on ypo.PAObjectID = p.PAObjectID");
        sql2.append("where rph.CHANGEID").gt(lastChangeIdProcessed);
        sql2.append("  and rph.CHANGEID").lte(stopChangeId);

        final YukonPaoRowMapper yukonPaoRowMapper = new YukonPaoRowMapper();
        
        final WaitableExecutor waitableExecutor = new WaitableExecutor(executorService);

        yukonJdbcTemplate.query(sql2, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                PaoIdentifier paoIdentifier = yukonPaoRowMapper.mapRow(rs, 1);
                int pointOffset = rs.getInt("PointOffset");
                String pointTypeStr = rs.getString("PointType");
                PointType pointType = PointType.getForString(pointTypeStr);

                final PaoPointIdentifier paoPointIdentifier = new PaoPointIdentifier(paoIdentifier, 
                                                                                     new PointIdentifier(pointType, pointOffset));

                int pointId = rs.getInt("PointId");

                final RawPointHistoryWorkUnit workUnit = new RawPointHistoryWorkUnit();
                workUnit.paoPointIdentifier = paoPointIdentifier;
                workUnit.pointId = pointId;
                workUnit.thisValue = rawPointHistoryRowMapper.mapRow(rs, 1);
                
                waitableExecutor.execute(new Runnable() {
                    public void run() {
                        try {
                            if (!attributeService.isPointAttribute(paoPointIdentifier, BuiltInAttribute.USAGE)) return;
                            
                            ImmutableSet<ValidationMonitor> descriptions = findValidationMonitors(deviceGroupCache,
                                                                                                  paoPointIdentifier);
                            if (descriptions.isEmpty()) return;
                            
                            processWorkUnit(workUnit, descriptions);
                        } catch (Exception e) {
                            log.warn("Unable to processWorkUnit for changeId " + workUnit.thisValue.changeId, e);
                        }
                    }

                });

            }
        });

        try {
            waitableExecutor.await(); // wait for completion
        } catch (Exception e) {
            log.warn("Caught unexpected exception while waiting for executor", e);
            throw new ProcessingException(e);
        } 

        return stopChangeId;
    }
    
    private ImmutableSet<ValidationMonitor> findValidationMonitors(SetMultimap<ValidationMonitor, Integer> deviceGroupCache,
                                                                       PaoPointIdentifier paoPointIdentifier) {
        ImmutableSet.Builder<ValidationMonitor> builder = ImmutableSet.builder();
        for (ValidationMonitor validationMonitor : deviceGroupCache.keySet()) {
            if (deviceGroupCache.containsEntry(validationMonitor, paoPointIdentifier.getPaoIdentifier().getPaoId())) {
                builder.add(validationMonitor);
            }
        }
        ImmutableSet<ValidationMonitor> descriptions = builder.build();
        return descriptions;
    }

    private void processWorkUnit(final RawPointHistoryWorkUnit workUnit, Collection<ValidationMonitor> validationMonitors) {
        long startTime = System.currentTimeMillis();
        
        VendorSpecificSqlBuilder builder1 = vendorSpecificSqlBuilderFactory.create();
        SqlBuilder sql1a = builder1.buildFor(DatabaseVendor.MS2000);
        sql1a.append("select top 2 rph.ChangeId, rph.Timestamp, rph.value")
             .append("from RAWPOINTHISTORY rph")
             .append("where rph.POINTID").eq(workUnit.pointId)
             .append("  and CHANGEID").lt(workUnit.thisValue.changeId)
             .append("  and Timestamp").lt(workUnit.thisValue.timestamp)
             .append("order by Timestamp desc, ChangeId desc;");

        SqlBuilder sql1b = builder1.buildOther();
        sql1b.append("select * from (")
             .append("  select rph.ChangeId, rph.Timestamp, rph.value, ")
             .append("    ROW_NUMBER() over (order by Timestamp desc, ChangeId desc) rn")
             .append("  from RAWPOINTHISTORY rph")
             .append("  where rph.POINTID").eq(workUnit.pointId)
             .append("    and CHANGEID").lt(workUnit.thisValue.changeId)
             .append("    and Timestamp").lt(workUnit.thisValue.timestamp)
             .append(") numberedRows")
             .append("where numberedRows.rn <= 2")
             .append("order by Timestamp desc, ChangeId desc;");
        
        VendorSpecificSqlBuilder builder2 = vendorSpecificSqlBuilderFactory.create();
        SqlBuilder sql2a = builder2.buildFor(DatabaseVendor.MS2000);
        sql2a.append("select top 2 rph.ChangeId, rph.Timestamp, rph.value")
             .append("from RAWPOINTHISTORY rph")
             .append("where rph.POINTID = ").eq(workUnit.pointId)
             .append("  and CHANGEID").lt(workUnit.thisValue.changeId)
             .append("  and Timestamp").gt(workUnit.thisValue.timestamp)
             .append("order by Timestamp, ChangeId;");
        
        SqlBuilder sql2b = builder2.buildOther();
        sql2b.append("select * from (")
             .append("  select rph.ChangeId, rph.Timestamp, rph.value,")
             .append("    ROW_NUMBER() over (order by Timestamp, ChangeId) rn")
             .append("  from RAWPOINTHISTORY rph")
             .append("  where rph.POINTID").eq(workUnit.pointId)
             .append("    and CHANGEID").lt(workUnit.thisValue.changeId)
             .append("    and Timestamp").gt(workUnit.thisValue.timestamp)
             .append(") numberedRows")
             .append("where numberedRows.rn <= 2")
             .append("order by Timestamp, ChangeId;");
        
        List<RawPointHistoryWrapper> valuesBefore = yukonJdbcTemplate.query(builder1, rawPointHistoryRowMapper);
        List<RawPointHistoryWrapper> valuesAfter = yukonJdbcTemplate.query(builder2, rawPointHistoryRowMapper);
        
        Builder<RawPointHistoryWrapper> builder = ImmutableList.builder();
        builder.addAll(Iterables.reverse(valuesAfter));
        builder.add(workUnit.thisValue);
        builder.addAll(valuesBefore);
        
        List<RawPointHistoryWrapper> values = builder.build();
        
        rowsPulled.addAndGet(values.size());

        if (values.size() < 2) {
            // nothing we can do
            return;
        }
        
        // store detected tags in Multimap
        Multimap<RawPointHistoryWrapper, RphTag> tags = HashMultimap.create(0,0); // most values should be clean
        

        if (values.size() == 2) {
            // special case handling for two rows
            for (ValidationMonitor validationMonitor : validationMonitors) {
                processThreeHistoryRows(workUnit, validationMonitor, values, tags);
            }
        } else {
            // process every set of three rows, starting with the least recent
            for (int i = 0; i + 2 < values.size(); ++i) {
                List<RawPointHistoryWrapper> subList = values.subList(values.size() - 3 - i, values.size() - i);
                for (ValidationMonitor validationMonitor : validationMonitors) {
                    processThreeHistoryRows(workUnit, validationMonitor, subList, tags);
                }
            }
        }
        
        writeOutRphTags(tags);
        long totalTime = System.currentTimeMillis() - startTime;
        totalMsProcessing.addAndGet(totalTime);
        changeIdsEvaluated.incrementAndGet();
    }

    private void writeOutRphTags(Multimap<RawPointHistoryWrapper, RphTag> tags) {
        
        Collection<Entry<RawPointHistoryWrapper, RphTag>> entries = tags.entries();
        for (Entry<RawPointHistoryWrapper, RphTag> entry : entries) {
            rphTagDao.insertTag(entry.getKey().changeId, entry.getValue());
        }
    }

    /**
     * This method analyzes three RawPointHistory rows. "Three" is definitely a magic number here. It has been 
     * decided that the whole algorithm will be designed around looking at the current values plus the previous
     * two (1+2=3), changing this assumption would require a major rewrite of this whole service. In the design
     * phase, these three values were commonly referred to using traditional mathematical sequence notation.
     * For example the current reading would be U<sub>n</sub> and the current time would be T<sub>n</sub>.
     * Thus the previous reading would be U<sub>n-1</sub> and so forth. This translates neatly into array
     * indexes for the reverse sorted values parameter. So, the current reading is at index 0 (the "n" reading), 
     * the previous reading is at index 1 (the "n-1" reading), and its previous reading is at index 2 (the "n-2"
     * reading). Throughout the comments and variable names of this method, these are shortened even further to
     * simply the index, so the n-2 value at index 2 is referred to as the "2" value.
     * 
     * <p>
     * The following should always be true for the List<RawPointHistoryWrapper> parameter.
     * <ul>
     * <li>values.size() == 2 || values.size() == 3
     * <li>!values.get(2).getTime().isAfter(values.get(1).getTime())
     * <li>!values.get(1).getTime().isAfter(values.get(0).getTime()) 
     * </ul>
     * 
     * This method is separate from the processThreeHistoryRows method for testability.
     * 
     * @param workUnit
     * @param validationMonitor
     * @param values
     * @param tags
     * @return 
     */
    public static AnalysisResult analyzeThreeHistoryRows(RawPointHistoryWorkUnit workUnit, 
            ValidationMonitor validationMonitor, 
            List<RawPointHistoryWrapper> values,
            Multimap<RawPointHistoryWrapper, RphTag> tags) {
        
        boolean peakInTheMiddle = false;
        boolean considerReRead = false;
        
        // look for peaks if there are at least 3 pieces of history
        if (values.size() >= 3) {
            boolean jumpUp = values.get(1).getValue() > values.get(2).getValue() + validationMonitor.getKwhReadingError();
            boolean jumpDown = values.get(1).getValue() < values.get(2).getValue() - validationMonitor.getKwhReadingError();
            boolean fall = values.get(0).getValue() < values.get(1).getValue() - validationMonitor.getKwhReadingError();
            boolean increasing = values.get(0).getValue() >= values.get(2).getValue() - validationMonitor.getKwhReadingError();
            double height = calculateHeight(values.get(2), values.get(1), values.get(0));
            boolean peakIsGreatEnough = Math.abs(height) > validationMonitor.getPeakHeightMinimum();
            LogHelper.trace(log, "for %d: jumpUp=%b, jumpDown=%b, fall=%b, increasing=%b, height=%.1f, peakIsGreatEnough=%b", workUnit.thisValue.changeId, jumpUp, jumpDown, fall, increasing, height, peakIsGreatEnough);


            // look for Peak Up
            if (jumpUp && fall && increasing && peakIsGreatEnough) {
                tags.put(values.get(1), RphTag.PU);
                peakInTheMiddle = true;
                LogHelper.debug(log, "PU (%.1f) detected for %s: %s", height, workUnit.paoPointIdentifier, values);
            }

            // look for Peak Down
            if (jumpDown && increasing & peakIsGreatEnough) {
                tags.put(values.get(1), RphTag.PD);
                peakInTheMiddle = true;
                LogHelper.debug(log, "PD (%.1f) detected for %s: %s", height, workUnit.paoPointIdentifier, values);
            }
        }
        
        // look for unreasonable values if there are at least 2 pieces of history
        if (values.size() >= 2) {
            // if the 1 value has a peak, use the 2 value for unreasonability checks
            RawPointHistoryWrapper previousValue = values.get(1);
            if (peakInTheMiddle) {
                previousValue = values.get(2); // it should be impossible to have a peak on the 1 value 
                                               // and not have a 2 value
            }

            // look for Unreasonable Up
            RawPointHistoryWrapper currentValue = values.get(0);
            double avgKwhPerDay = calculateLowerAvgKwhPerDay(previousValue, currentValue, validationMonitor.getKwhSlopeError());
            if (avgKwhPerDay > validationMonitor.getReasonableMaxKwhPerDay()) {
                tags.put(values.get(0), RphTag.UU);

                // Check if re read should be performed. The goal here is to get
                // an extra reading so that we can detect a possible peak. But,
                // we don't want to loop if a meter is simply recording a lot of
                // usage at the moment or the utility hasn't correctly configured
                // the usage limits. To prevent looping, we'll look at the usage delta
                // between the 2 and 1 values (essentially the same as looking to see 
                // if the 1 value has a UU tag, but without hitting the DB). If the 
                // usage was high between 2 and 1, we will NOT ask for a another reading.

                considerReRead = true;
                if (values.size() > 2 && !peakInTheMiddle) {
                    double avgKwhPerDay2to1 = calculateLowerAvgKwhPerDay(values.get(2), values.get(1), validationMonitor.getKwhSlopeError());
                    if (avgKwhPerDay2to1 > validationMonitor.getReasonableMaxKwhPerDay()) {
                        // no need for another read
                        considerReRead = false;
                    }
                }
                LogHelper.debug(log, "UU (%.1f) detected for %s (reread=%b): %s", avgKwhPerDay, workUnit.paoPointIdentifier, considerReRead, values);
            }

            // look for Unreasonable Down
            if (currentValue.getValue() < previousValue.getValue() - validationMonitor.getKwhReadingError()) {
                RphTag resultTag = RphTag.UD;
                considerReRead = true;

                // look for Unreasonable Down from Changeout
                double avgKwhPerDayFromZero = calculateLowerAvgKwhPerDayFromZero(previousValue, currentValue);
                if (avgKwhPerDayFromZero <= validationMonitor.getReasonableMaxKwhPerDay()) {
                    resultTag = RphTag.UDC;
                    considerReRead = false;
                }

                tags.put(values.get(0), resultTag);
                LogHelper.debug(log, "%s (%.1f) detected for %s (reread=%b): %s", resultTag, avgKwhPerDay, workUnit.paoPointIdentifier, considerReRead, values);
            }

        }

        return new AnalysisResult(peakInTheMiddle, considerReRead);
    }

    private void processThreeHistoryRows(RawPointHistoryWorkUnit workUnit, ValidationMonitor validationMonitor,
            List<RawPointHistoryWrapper> values, Multimap<RawPointHistoryWrapper, RphTag> tags) {
        
        AnalysisResult analysisResult = analyzeThreeHistoryRows(workUnit, validationMonitor, values, tags);
        
        if (analysisResult.considerReRead && validationMonitor.isReReadOnUnreasonable() ) {
            // there is no point rereading if the data is over a day old
            DateTime readingTime = values.get(0).getTime();
            Duration timeSinceReading = new Duration(readingTime, null); // null means now
            
            if (timeSinceReading.isShorterThan(Duration.standardDays(1))) {
                // re read meter
                pointReadService.backgroundReadPoint(workUnit.paoPointIdentifier, CommandRequestExecutionType.VEE_RE_READ,  UserUtils.getYukonUser());
                LogHelper.debug(log, "Sumbitting reread for a %s old reading for %s", timeSinceReading.toPeriod(), workUnit.paoPointIdentifier);
                
                PaoIdentifier paoIdentifier = workUnit.paoPointIdentifier.getPaoIdentifier();
                PointIdentifier pointIdentifier = workUnit.paoPointIdentifier.getPointIdentifier();
                DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(paoIdentifier);
                
                validationEventLogService.unreasonableValueCausedReRead(paoIdentifier.getPaoId(), displayablePao.getName(), paoIdentifier.getPaoType(), workUnit.pointId, pointIdentifier.getPointType(), pointIdentifier.getOffset());
            }
        }
        
        if (validationMonitor.isSetQuestionableOnPeak() && analysisResult.peakInTheMiddle) {
            int changeId = values.get(1).changeId; // peak in the "middle" means the 1 value
            rawPointHistoryDao.changeQuality(changeId, PointQuality.Questionable);
            
            PaoIdentifier paoIdentifier = workUnit.paoPointIdentifier.getPaoIdentifier();
            PointIdentifier pointIdentifier = workUnit.paoPointIdentifier.getPointIdentifier();
            DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(paoIdentifier);
            validationEventLogService.changedQualityOnPeakedValue(changeId, paoIdentifier.getPaoId(), displayablePao.getName(), paoIdentifier.getPaoType(), workUnit.pointId, pointIdentifier.getPointType(), pointIdentifier.getOffset());
        }
    }

    /**
     * Calculate the height or severity of the peak when compared to the previous and subsequent values (base1 and base2).
     * 
     * @param base1 a normal value
     * @param peak a value that has been determined to be a peak
     * @param base2 a normal value
     * @return
     */
    public static double calculateHeight(RawPointHistoryWrapper base1, RawPointHistoryWrapper peak, RawPointHistoryWrapper base2) {
        double kwhDelta = base2.getValue() - base1.getValue();
        Duration deltaDuration = new Duration(base1.getTime(), base2.getTime()); 
        double baseKwh = 0;
        if (deltaDuration.getMillis() <= 0) {
            baseKwh = (base1.getValue() + base2.getValue()) / 2;
        } else {
            // use simple interpolation to determine what the kWh might have been when the peak occurred 
            Duration duration1toPeak = new Duration(base1.getTime(), peak.getTime());
            double slope = kwhDelta / deltaDuration.getMillis();
            double deltaKwhBaseRise = slope * duration1toPeak.getMillis();
            baseKwh = base1.getValue() + deltaKwhBaseRise;
        }
        
        double height = peak.getValue() - baseKwh;
        return height;
    }

    public static double calculateLowerAvgKwhPerDay(RawPointHistoryWrapper previousValue, RawPointHistoryWrapper currentValue, double readingError) {
        double kwhDelta = currentValue.getValue() - previousValue.getValue();
        double signum = Math.signum(kwhDelta);
        kwhDelta = Math.abs(kwhDelta);
        kwhDelta = Math.max(0.0, kwhDelta - readingError);
        
        Duration deltaDuration = new Duration(previousValue.getTime(), currentValue.getTime());
        double daysDelta = (double)deltaDuration.getStandardSeconds() / TimeUnit.DAYS.toSeconds(1);

        double avgKwhPerDay = 0.0;
        if (daysDelta != 0.0) {
            avgKwhPerDay = kwhDelta / daysDelta;
        }
        return avgKwhPerDay * signum;
    }

    public static double calculateLowerAvgKwhPerDayFromZero(RawPointHistoryWrapper previousValue, RawPointHistoryWrapper currentValue) {
        double kwhDelta = currentValue.getValue() - 0; // pretend previousValue was zero
        Duration deltaDuration = new Duration(previousValue.getTime(), currentValue.getTime());
        double daysDelta = (double)deltaDuration.getStandardSeconds() / TimeUnit.DAYS.toSeconds(1);

        double avgKwhPerDay = 0.0;
        if (daysDelta != 0.0) {
            avgKwhPerDay = kwhDelta / daysDelta;
        }
        return avgKwhPerDay;
    }
    
    @ManagedAttribute
    public long getChangeIdChunkSize() {
        return changeIdChunkSize;
    }
    
    @ManagedAttribute
    public long getRowsPulled() {
        return rowsPulled.get();
    }
    
    @ManagedAttribute
    public long getChangeIdsEvaluated() {
        return changeIdsEvaluated.get();
    }
    
    @ManagedAttribute
    public float getTimePerChange() {
        return (float)totalMsProcessing.get() / changeIdsEvaluated.get();
    }
    
    @ManagedAttribute
    public long getLastChunkCommitted() {
        return lastChunkCommitted.get();
    }
    
    @ManagedAttribute
    public float getAverageRowsPerChange() {
        return (float)rowsPulled.get() / changeIdsEvaluated.get();
    }
    
    @ManagedAttribute
    public float getChunksPerWakeup() {
        return (float)workingExecutions.get() / sleepingExecutions.get();
    }
    
    @ManagedAttribute
    public float getLastChunkSizePercent() {
        return 100f * lastChunkSize.get() / changeIdChunkSize;
    }
    
    @ManagedAttribute
    public float getAverageChunkSize() {
        return (float)changeIdsEvaluated.get() / workingExecutions.get();
    }
    
    @ManagedAttribute
    public float getAverageRowsPerWakeup() {
        return (float)changeIdsEvaluated.get() / sleepingExecutions.get();
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setPointReadService(PointReadService pointReadService) {
        this.pointReadService = pointReadService;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setValidationMonitorDao(ValidationMonitorDao validationMonitorDao) {
        this.validationMonitorDao = validationMonitorDao;
    }
    
    @Autowired
    public void setVendorSpecificSqlBuilderFactory(
            VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory) {
        this.vendorSpecificSqlBuilderFactory = vendorSpecificSqlBuilderFactory;
    }
    
    @Autowired
    public void setPersistedSystemValueDao(PersistedSystemValueDao persistedSystemValueDao) {
        this.persistedSystemValueDao = persistedSystemValueDao;
    }
    
    @Autowired
    public void setRphTagDao(RphTagDao rphTagDao) {
        this.rphTagDao = rphTagDao;
    }
    
    @Autowired
    public void setRawPointHistoryDao(RawPointHistoryDao rawPointHistoryDao) {
        this.rawPointHistoryDao = rawPointHistoryDao;
    }
    
    @Autowired
    public void setValidationEventLogService(ValidationEventLogService validationEventLogService) {
        this.validationEventLogService = validationEventLogService;
    }
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
		this.paoLoadingService = paoLoadingService;
	}
}
