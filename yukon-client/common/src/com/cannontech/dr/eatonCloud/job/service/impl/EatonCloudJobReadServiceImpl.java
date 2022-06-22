package com.cannontech.dr.eatonCloud.job.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobReadService;
import com.cannontech.dr.eatonCloud.model.EatonCloudChannel;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudDataReadService;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

public class EatonCloudJobReadServiceImpl implements EatonCloudJobReadService{
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private RecentEventParticipationDao recentEventParticipationDao;
    @Autowired private EatonCloudDataReadService eatonCloudDataReadService;
    
    private static final Logger log = YukonLogManager.getLogger(EatonCloudJobReadServiceImpl.class);
    
    private static final String DATE_FORMAT = "MM-dd-yyyy HH:mm:ss";
    private AtomicBoolean isReadingDevices = new AtomicBoolean(false);
    // <external event id, Pair<next read time, job creation time>>
    private Map<Integer, Pair<Instant, Instant>> nextRead = new ConcurrentHashMap<>();
    
    private List<EatonCloudChannel> channelsToRead = List.of(EatonCloudChannel.EVENT_STATE,
            EatonCloudChannel.ACTIVATION_STATUS_R1, EatonCloudChannel.ACTIVATION_STATUS_R2,
            EatonCloudChannel.ACTIVATION_STATUS_R3, EatonCloudChannel.ACTIVATION_STATUS_R4);

    @PostConstruct
    public void init() {
        String siteGuid = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
        if (Strings.isNullOrEmpty(siteGuid)) {
            return;
        }
        schedule();
    }
    
    private void schedule() {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (isReadingDevices.compareAndSet(false, true)) {
                try {
                    readDevices();
                } catch (Exception e) {
                    log.error("Error reading devices", e);
                }
                isReadingDevices.set(false);
            }
        }, 0, 1, TimeUnit.MINUTES);
    }
    
    @Override
    public void setupDeviceRead(EventSummary summary, Instant jobCreationTime, int currentTry) {
       // TODO:
       /* int readTimeFromNowInMinutes = summary.getCommand().getDutyCyclePeriod() == null ? 5 : IntMath.divide(
                summary.getCommand().getDutyCyclePeriod() / 60,
                2, RoundingMode.CEILING);*/
        
        // using for testing
        
        int readTimeFromNowInMinutes = 2;    
        Instant nextReadTime = DateTime.now().plusMinutes(readTimeFromNowInMinutes).toInstant();
        
        log.info(summary.getLogSummary(false) + "Next device Try:{} in {} minutes at {}",
                currentTry,
                readTimeFromNowInMinutes,
                nextReadTime.toDateTime().toString(DATE_FORMAT));
        
        nextRead.put(summary.getEventId(), Pair.of(nextReadTime, jobCreationTime));
    }
    
    private void readDevices() {
        try {
            Iterator<Entry<Integer, Pair<Instant, Instant>>> iter = nextRead.entrySet().iterator();
            // For each key in cache
            while (iter.hasNext()) {
                Entry<Integer, Pair<Instant, Instant>> entry = iter.next();
                Integer eventId = entry.getKey();
                Instant nextRead = entry.getValue().getKey();
                Instant jobCreationTime = entry.getValue().getValue();
                if (nextRead.isEqualNow() || nextRead.isBeforeNow()) {
                    Range<Instant> range = new Range<>(jobCreationTime, true, Instant.now(), true);
                    Set<Integer> devicesToRead = recentEventParticipationDao.getDeviceIdsByExternalEventIdAndStatuses(eventId,
                            List.of(ControlEventDeviceStatus.SUCCESS_RECEIVED));
                    if (!devicesToRead.isEmpty()) {
                        Multimap<PaoIdentifier, PointData> result = eatonCloudDataReadService.collectDataForRead(devicesToRead,
                                range, channelsToRead, "READ AFTER SHED event id:" + eventId);
                        log.info(
                                "[id:{}] Read devices:{} Read succeeded for {} devices for dates from:{} to:{} [job created at {}]",
                                eventId,
                                devicesToRead.size(),
                                result.asMap().keySet().size(),
                                range.getMin().toDateTime().toString(DATE_FORMAT),
                                range.getMax().toDateTime().toString(DATE_FORMAT),
                                jobCreationTime.toDateTime().toString(DATE_FORMAT));
                    } else {
                        log.info(
                                "[id:{}] Read devices:{}. Devices with status SUCCESS_RECEIVED not found for dates from:{} to:{} [job created at {}] ",
                                eventId,
                                0,
                                range.getMin().toDateTime().toString(DATE_FORMAT),
                                range.getMax().toDateTime().toString(DATE_FORMAT),
                                jobCreationTime.toDateTime().toString(DATE_FORMAT));
                    }
                    iter.remove();
                }
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
    }
}
