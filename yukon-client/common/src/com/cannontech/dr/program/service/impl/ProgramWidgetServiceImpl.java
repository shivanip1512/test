package com.cannontech.dr.program.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.program.widget.model.GearData;
import com.cannontech.common.program.widget.model.ProgramData;
import com.cannontech.common.program.widget.model.ProgramWidgetData;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.dr.program.service.ProgramWidgetService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.ProgramUtils;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.dynamic.receive.LMGroupChanged;
import com.cannontech.loadcontrol.dynamic.receive.LMProgramChanged;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.user.YukonUserContext;

public class ProgramWidgetServiceImpl implements ProgramWidgetService, MessageListener {
    
    private final Logger log = YukonLogManager.getLogger(ProgramWidgetServiceImpl.class);

    @Autowired private LoadControlClientConnection loadControlClientConnection;
    @Autowired private LoadControlProgramDao loadControlProgramDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    public static final int MAX_PROGRAM_TO_DISPLAY_ON_WIDGET = 10;
    private static final int PROGRAM_EVENT_SAFEGAURD_WINDOW = 15000; // 15 Seconds
    private final static String todayKey = "yukon.web.widgets.programWidget.today";
    private List<ProgramData> programsDataCache = new ArrayList<>();
    private List<ProgramData> todaysProgramsDataCache = new ArrayList<>();
    private long tomorrowStartInMillis = 0L;
    private final static String ACTIVE_CSS_CLASS = "green";
    private final static String SCHEDULED_CSS_CLASS = "orange";
    
    @PostConstruct
    public void initialize() {
        loadControlClientConnection.addMessageListener(this);
        loadTodaysProgramsDataCache();
    }
    
    @Override
    public void messageReceived(MessageEvent e) {
        Message obj = e.getMessage();
        // If any LMProgram change event happens, reload the today's program data cache. 
        if (obj instanceof LMProgramChanged || obj instanceof LMGroupChanged) {
            loadTodaysProgramsDataCache();
        }
    }

    /**
     * Load today's ProgramData cache from LMServer cache and DB.
     * Below method is building programData cache for today's programs from LMServer cache and DB.
     * If any program events happens, this method reloads cache to add gear history inside program data.
     * Case-1 : If any program is running and we have changed gear assigned to that program .
     *          In that case we need to update the Program Data object inside cache to maintain the old gear
     *          data (name and start/stop) along with newly assigned gear.
     * Case-2 : If One program is started and stopped on the same day and If we again start that
     *          program on the same day, we need to display old run programData record along with newly started.
     *          In this case we need to build cache from DB (1st time run Program data) and from LM server cache 
     *          (for latest running program).
     * Above two cases are handled in below method where first we are getting all today's active programs from
     * LM server cache and adding them to local cache.
     * Then we are fetching ProgramData records from DB. They might be the old executed program or running
     * ProgramData records. In case of old executed program we are adding them to todaysProgramsDataCacheand
     * if data (gear history) is associated with current running program we are updating that in the
     * programData records which are already there in todaysProgramsDataCache.
     */
    private synchronized void loadTodaysProgramsDataCache() {
        todaysProgramsDataCache.clear();
        DateTime from = new DateTime().withTimeAtStartOfDay();
        DateTime to = from.plusHours(24);
        List<ProgramData> todaysPrograms = getAllTodaysPrograms();
        // Add ProgramData from LM cache
        todaysProgramsDataCache.addAll(todaysPrograms);
        List<ProgramData> todaysProgramsFromDB = getProgramsHistoryDetail(from, to);
        for (ProgramData program : todaysProgramsFromDB) {
            boolean stopTimePresent = true;
            boolean addToCache = true;
            List<GearData> gears = program.getGears();
            for (GearData gear : gears) {
                if (gear.getStopDateTime() == null) {
                    stopTimePresent = false;
                }
            }
            if (stopTimePresent && !isExistInProgramDataCache(program) && !isExistInPreviousDaysProgramsCache(program)) {
                    todaysProgramsDataCache.add(program);
                    addToCache = false;
            } else {
                // Update cached program data with gear information.
                for (ProgramData todaysProgram : todaysPrograms) {
                    if (isSameProgramEvent(todaysProgram, program)) {
                        List<GearData> gearHistory = program.getGears();
                        updateGearInfo(todaysProgram, gearHistory);
                        todaysProgram.setOriginSource(program.getOriginSource());
                        addToCache = false;
                    }
                }
            }
            if (isExistInPreviousDaysProgramsCache(program)) {
                addGearsToProgramHistoryCache(program);
                addToCache = false;
            }
            if (!stopTimePresent && addToCache) {
                todaysProgramsDataCache.add(program);
            }
        }
    }

    @Override
    public ProgramWidgetData buildProgramWidgetData(YukonUserContext userContext) {
    	Map<String, List<ProgramData>> programWidgetData = new LinkedHashMap<>();
        int todaysAndScheduledProgramDataCount = 0;
        List<ProgramData> todaysPrograms = getTodaysProgramData();
        int todaysProgramsCount = todaysPrograms.size();
        todaysAndScheduledProgramDataCount += todaysProgramsCount;
        // List of Programs which are scheduled to execute for next control day after today
        List<ProgramData> futureProgramsToDisplay = new ArrayList<>();
        if (todaysProgramsCount > MAX_PROGRAM_TO_DISPLAY_ON_WIDGET) {
            todaysPrograms = limitData(todaysPrograms, MAX_PROGRAM_TO_DISPLAY_ON_WIDGET);
        }
        List<ProgramData> futurePrograms = getProgramsScheduledForNextControlDayAfterToday();
        todaysAndScheduledProgramDataCount += futurePrograms.size();
        if (!futurePrograms.isEmpty() && todaysProgramsCount < MAX_PROGRAM_TO_DISPLAY_ON_WIDGET) {
            int maxFutureProgramsCount = MAX_PROGRAM_TO_DISPLAY_ON_WIDGET - todaysProgramsCount;
            futureProgramsToDisplay = limitData(futurePrograms, maxFutureProgramsCount);
            String date = dateFormattingService.format(futureProgramsToDisplay.get(0).getStartDateTime(),
                DateFormatEnum.DATE, userContext);
            programWidgetData.put(date, futureProgramsToDisplay);
        }
        if (!todaysPrograms.isEmpty()) {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            programWidgetData.put(accessor.getMessage(todayKey), todaysPrograms);
        }

        int futureAndTodayProgramsCount = futureProgramsToDisplay.size() + todaysProgramsCount;
        if (futureAndTodayProgramsCount < MAX_PROGRAM_TO_DISPLAY_ON_WIDGET) {
            int previousProgramsToDisplayCount =
                MAX_PROGRAM_TO_DISPLAY_ON_WIDGET - futureAndTodayProgramsCount;
            List<ProgramData> limitProgramData = limitData(getProgramsDataCache(), previousProgramsToDisplayCount);
            Map<String, List<ProgramData>> limitedProgramsToDisplay =
                    groupProgramsByStartDate(limitProgramData, userContext);
            programWidgetData.putAll(limitedProgramsToDisplay);
        }
        // Return ProgramWidgetData object with programWidgetData and exact count for todays and future scheduled program data.
        return new ProgramWidgetData(programWidgetData, todaysAndScheduledProgramDataCount);
    }

    /**
     * Returns list of all program which are scheduled to execute for today, next control day after today and
     * previous 7 days.
     * 
     */
    @Override
    public Map<String, List<ProgramData>> buildProgramDetailsData(YukonUserContext userContext) {

        Map<String, List<ProgramData>> programDetailData = new LinkedHashMap<>();

        List<ProgramData> futurePrograms = getProgramsScheduledForNextControlDayAfterToday();
        if (CollectionUtils.isNotEmpty(futurePrograms)) {
            String date = dateFormattingService.format(futurePrograms.get(0).getStartDateTime(), DateFormatEnum.DATE,
                userContext);
            programDetailData.put(date, futurePrograms);
        }
        List<ProgramData> todaysPrograms = getTodaysProgramData();
        if (!todaysPrograms.isEmpty()) {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            programDetailData.put(accessor.getMessage(todayKey), todaysPrograms);
        }

        Map<String, List<ProgramData>> programsToDisplay = groupProgramsByStartDate(getProgramsDataCache(), userContext);
        programDetailData.putAll(programsToDisplay);
        return programDetailData;
    }

    /**
     * Returns list of all program which are executed today or scheduled to execute today 
     * 
     */
    private List<ProgramData> getAllTodaysPrograms() {
        List<ProgramData> todaysProgram = new ArrayList<>();
        try {
            Set<LMProgramBase> allLMProgramBase = loadControlClientConnection.getAllProgramsSet();
            if (CollectionUtils.isNotEmpty(allLMProgramBase)) {
                // Get list of todays programs (without keeping state in mind)
                DateTime todayStartTime = DateTime.now().withTimeAtStartOfDay();
                DateTime todayStopTime = todayStartTime.plusHours(24);
                Interval interval = new Interval(todayStartTime, todayStopTime);
                todaysProgram = filterProgramsForInterval(allLMProgramBase, interval);
            }
        } catch (ConnectionException e) {
            log.warn(e.getMessage());
        }
        return todaysProgram;
    }

    /**
     * Returns list of all program which are scheduled to execute for next control day after today
     * 
     */
    private List<ProgramData> getProgramsScheduledForNextControlDayAfterToday() {
        List<ProgramData> scheduledProgramsForNearestDay = new ArrayList<>();
        try {
            Set<LMProgramBase> allLMProgramBase = loadControlClientConnection.getAllProgramsSet();
            if (CollectionUtils.isNotEmpty(allLMProgramBase)) {
                DateTime now = DateTime.now();
                long startOfTomorrow = now.withTimeAtStartOfDay().plusHours(24).getMillis();
                // Get all the program which are scheduled to start in future (after today) and sort them based on startTime
                List<LMProgramBase> scheduledProgramsAfterToday =
                                        allLMProgramBase.stream()
                                                        .filter(p -> p.getStartTime().getTimeInMillis() >= startOfTomorrow)
                                                        .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                                                        .collect(Collectors.toList());

                // Get the list of scheduled programs for next control day (after today)
                if (CollectionUtils.isNotEmpty(scheduledProgramsAfterToday)) {
                    DateTime nextDayWithAProgramScheduled = new DateTime(scheduledProgramsAfterToday
                                                                         .get(0)
                                                                         .getStartTime()
                                                                         .getTimeInMillis())
                                                                         .withTimeAtStartOfDay();
                   Interval nextDayWithProgramScheduledInterval = new Interval(nextDayWithAProgramScheduled,
                                                                       nextDayWithAProgramScheduled.plusHours(24));
                   scheduledProgramsForNearestDay = 
                              filterProgramsForInterval(scheduledProgramsAfterToday, nextDayWithProgramScheduledInterval);
                }
            }
        } catch (ConnectionException e) {
            log.warn(e.getMessage());
        }
        return scheduledProgramsForNearestDay;
    }

    /**
     * Return top Elements based on passed maxLimit.
     */
    private List<ProgramData> limitData(List<ProgramData> programs, int maxLimit) {
        return programs.stream()
                       .limit(maxLimit)
                       .collect(Collectors.toList());
    }

    /**
     *  Filter programs which started with-in the passed interval and returns
     *  list of ProgramData objects from the filtered programs
     */
    private List<ProgramData> filterProgramsForInterval(Collection<LMProgramBase> programs, Interval interval) {
        return programs.stream()
                       .filter(p -> (interval.contains(p.getStartTime().getTimeInMillis()) && (p.isActive() || p.isScheduled())))
                       .sorted((p1, p2) -> p2.getStartTime().compareTo(p1.getStartTime()))
                       .map(program -> buildProgramData(program))
                       .collect(Collectors.toList());
    }

    /**
     *  Build ProgramData object from passed LMProgramBase object
     */
    private ProgramData buildProgramData(LMProgramBase lmProgramBase) {
       ProgramData programData = new ProgramData.ProgramDataBuilder(lmProgramBase.getYukonID().intValue())
                             .setProgramName(lmProgramBase.getYukonName())
                             .setStartDateTime(new DateTime(lmProgramBase.getStartTime()
                                                                         .getTimeInMillis()))
                             .setOriginSource(ProgramOriginSource.getProgramOriginSource(lmProgramBase.getOriginSource()))
                             .build();
       addStatusCssClass(programData);
       return programData;
    }

    /**
     * Program History detail for current day and past 7 days, getAllProgramControlHistory method 
     * is processing the history data, to show multiple gear action within program, group programs by
     * program history Id and build GearData based on that.
     * 
     */
    private List<ProgramData> getProgramsHistoryDetail(DateTime from, DateTime to) {
        List<ProgramControlHistory> programsHistoryDetail = loadControlProgramDao.getAllProgramControlHistory(from.toDate(), to.toDate());
        List<ProgramData> previousDaysProgramData = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(programsHistoryDetail)) {
            List<ProgramControlHistory> filterProgramWithStartTime = 
                                            programsHistoryDetail.stream()
                                                                 .filter(program -> program.getStartDateTime().compareTo(from.toDate()) >=0)
                                                                 .collect(Collectors.toList());
            Map<Integer, List<ProgramControlHistory>> programsByProgramHistoryId =
                                                        groupProgramsByProgramHistoryId(filterProgramWithStartTime);

            previousDaysProgramData = 
                    programsByProgramHistoryId.entrySet()
                                              .stream()
                                              .map(entry -> buildProgramData(entry.getValue()))
                                              .collect(Collectors.toList());
            previousDaysProgramData.sort((p1, p2) -> p2.getStartDateTime().compareTo(p1.getStartDateTime()));
        }
        return previousDaysProgramData;
    }

    /**
     * Group programs by programHistoryId, So that we have history of program with gears assigned
     * or changed action, which can be on the same day or different day.This is needed as we can have gear
     * assigned to a program on same and different dates, So using programHistoryId we will club them all.
     */
    private Map<Integer, List<ProgramControlHistory>> groupProgramsByProgramHistoryId(List<ProgramControlHistory> programsHistoryDetail) {
        Map<Integer, List<ProgramControlHistory>> programsByProgramHistoryId = 
                         programsHistoryDetail.stream()
                                              .collect(Collectors.groupingBy(
                                                  program -> program.getProgramHistoryId(),
                                                             LinkedHashMap::new, 
                                                             Collectors.toCollection(ArrayList::new)));
        return programsByProgramHistoryId;
    }

    /**
     * This method is used to build program with gears. A Single program can have run with a single or 
     * multiple gear.So This method is to collect all those action inside a single program.
     */
    private ProgramData buildProgramData(List<ProgramControlHistory> programsHistoryDetail) {
        //As per need we have to club all the Program action event(start, gear change, stop) to a 
        //single date on which the Program is started.The program at zero index will give that.
        ProgramControlHistory programHistory = programsHistoryDetail.get(0);
        List<GearData> gears = programsHistoryDetail.stream()
                                                    .map(program -> buildGearData(program, programHistory.getStartDateTime()))
                                                    .collect(Collectors.toList());

        ProgramData programData = new ProgramData.ProgramDataBuilder(programHistory.getProgramId())
                                                 .setStartDateTime(new DateTime(programHistory.getStartDateTime()))
                                                 .setGears(gears)
                                                 .setOriginSource(ProgramOriginSource.getProgramOriginSource(programHistory.getOriginSource()))
                                                 .setProgramName(programHistory.getProgramName())
                                                 .setProgramHistoryId(programHistory.getProgramHistoryId())
                                                 .build();
        addStatusCssClass(programData);
        updateProgramGearInfo(gears, programData);
        return programData;
    }


    private void updateProgramGearInfo(List<GearData> gears, ProgramData programData) {
        outer: for (GearData gear : gears) {
            if (gear.getStopDateTime() == null) {
                try {
                Set<LMProgramBase> allLMProgramBase = loadControlClientConnection.getAllProgramsSet();
                for (LMProgramBase lmProgramBase : allLMProgramBase) {
                    ProgramData program =
                        new ProgramData.ProgramDataBuilder(lmProgramBase.getYukonID().intValue()).setStartDateTime(
                            new DateTime(lmProgramBase.getStartTime().getTimeInMillis())).build();
                    if (isSameProgramEvent(program, programData)) {
                        gear.setKnownGoodStopDateTime(true);
                        break outer;
                    }
                }
                } catch (ConnectionException e) {
                    log.warn(e.getMessage());
                }
            }
        }
    }

    /**
     * Check if the Current Day program is present in previous day programs cache.
     * This is needed to handle the scenario where Programs are running overnight.
     */
    private boolean isExistInPreviousDaysProgramsCache(ProgramData todaysProgram) {
        for (ProgramData previousDayProgram : getProgramsDataCache()) {
            if (previousDayProgram.getProgramHistoryId() == todaysProgram.getProgramHistoryId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add overnight running program gear to previous days program in previous days program cache.
     * This is needed for program running overnight with gears can be clubbed. Also set the startedOnSameDay
     * stoppedOnSameDay flag. As these are from different date, so we can set this directly.
     */
    private void addGearsToProgramHistoryCache(ProgramData todaysProgram) {
        for (ProgramData previousDayProgram : getProgramsDataCache()) {
            if (previousDayProgram.getProgramHistoryId() == todaysProgram.getProgramHistoryId()) {
                List<GearData> gears = compareTodaysProgramGearWithPreviousDayProgramsGear(todaysProgram.getGears(),
                    previousDayProgram.getGears());
                for (GearData gear : gears) {
                    if (gear.getStopDateTime() == null) {
                        gear.setKnownGoodStopDateTime(true);
                    }
                    gear.setStartedOnSameDay(false);
                    gear.setStoppedOnSameDay(false);
                }
                previousDayProgram.getGears().addAll(gears);
            }
        }
    }

    /**
     * Check if current program gears are present in previous days program cache.
     * If not present return the gears and add in previous days program cache.
     */
    private List<GearData> compareTodaysProgramGearWithPreviousDayProgramsGear(List<GearData> currentDayProgramGear, List<GearData> previousDayProgramGear) {
        List<GearData> gears = new ArrayList<>();
        for (GearData currentDay : currentDayProgramGear) {
            boolean presentFlag = false;
            for(GearData previousDayGear : previousDayProgramGear) {
                if (currentDay.getProgramGearHistoryId() == previousDayGear.getProgramGearHistoryId()) {
                    presentFlag = true;
                }
            }
            if (!presentFlag) {
                gears.add(currentDay);
            }
        }
        return gears;
    }

    /**
     * Build GearData from ProgramControlHistory. As the Gear Start -> Gear Change ->
     * Stop Action with start & stop time are already handled inside getAllProgramControlHistory
     * method, So Use that to create GearData.
     */
    private GearData buildGearData(ProgramControlHistory programHistory, Date programStartDate) {
        GearData gearData = new GearData();
        DateTime gearStartTime = new DateTime(programHistory.getStartDateTime());
        DateTime gearStopTime = null;
        if (programHistory.getStopDateTime() != null) {
            gearStopTime = new DateTime(programHistory.getStopDateTime());
        }
        gearData.setGearName(programHistory.getGearName());
        gearData.setStartDateTime(gearStartTime);
        gearData.setStopDateTime(gearStopTime);
        if (programStartDate != null && gearStopTime != null) {
            gearData.setStoppedOnSameDay(DateUtils.isSameDay(programStartDate, gearStopTime.toDate()));
        }
        gearData.setStartedOnSameDay(DateUtils.isSameDay(programStartDate, programHistory.getStartDateTime()));
        gearData.setKnownGoodStopDateTime(programHistory.isKnownGoodStopDateTime());
        gearData.setProgramGearHistoryId(programHistory.getProgramGearHistoryId());
        return gearData;
    }

    /**
     * Group ProgramData by StartDate to show program clubbed inside date.
     */
    private Map<String, List<ProgramData>> groupProgramsByStartDate(List<ProgramData> previousDaysProgram, YukonUserContext userContext) {
        Map<String, List<ProgramData>> programDataByEventTime = 
                      previousDaysProgram.stream()
                                         .collect(Collectors.groupingBy(
                                                  programData -> dateFormattingService.format(programData.getStartDateTime(), 
                                                      DateFormattingService.DateFormatEnum.DATE, 
                                                      userContext),
                                                             LinkedHashMap::new, 
                                                             Collectors.toCollection(ArrayList::new)));
        return programDataByEventTime;
    }

    

    /**
     * Returns true if ProgramData record present in todaysProgramsDataCache.
     */
    private boolean isExistInProgramDataCache(ProgramData programData) {
        if (todaysProgramsDataCache.isEmpty()) {
            return false;
        }
        for (ProgramData pd : todaysProgramsDataCache) {
            if (isSameProgramEvent(pd, programData)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Update gear history data to the passed ProgramData. 
     */
    private void updateGearInfo(ProgramData programData, List<GearData> gearHistory) {
        if (gearHistory.size() > 1) {
            gearHistory.forEach(g -> g.setKnownGoodStopDateTime(true));
            programData.setGears(gearHistory);
        }
    }

    /**
     * Return true if both objects are from the same program event.
     * If both objects belongs to same program (equal programId) and has started at the
     * same time (with 5 seconds time window), consider them as a part of same program event.
     * 5 second window is considered to handle the case where timeStamp in c++ message and DB record
     * differ by few seconds (Ideally both should have same timeStamp)
     */
    private boolean isSameProgramEvent(ProgramData programData1, ProgramData programData2) {
        if (programData1 == null || programData2 == null) {
            return false;
        } else if (programData1.getProgramId() == programData2.getProgramId()) {
            long millis1 = programData1.getStartDateTime().getMillis();
            long millis2 = programData2.getStartDateTime().getMillis();
            long timeWindow = 0;
            if (millis1 >= millis2) {
                timeWindow = millis1 - millis2;
            } else {
                timeWindow = millis2 - millis1;
            }
            return timeWindow <= PROGRAM_EVENT_SAFEGAURD_WINDOW;
        } else {
            return false;
        }
    }

    /**
     * Return sorted today's program data from cache.
     */
    private List<ProgramData> getTodaysProgramData() {
        return getTodaysProgramDataCache().stream()
                                          .sorted((p1, p2) -> (int)(p2.getStartDateTime().getMillis() 
                                                                       - p1.getStartDateTime().getMillis()))
                                          .collect(Collectors.toList());
    }

    /**
     * This is to cache the Programs data so that we will not hit database every time.
     * The cache will be updated each time next day with new request.
     */
    private synchronized List<ProgramData> getProgramsDataCache() {
        long currentTime = DateTimeUtils.currentTimeMillis();
        if (currentTime > tomorrowStartInMillis) {
            DateTime toDate = new DateTime().withTimeAtStartOfDay();
            DateTime fromDate = toDate.minusDays(7);
            programsDataCache.clear();
            log.info("Expiring program cache and reloading cache for date range - From : " + fromDate.toString()
                                            + " To: " + toDate.toString());
            programsDataCache = getProgramsHistoryDetail(fromDate, toDate);
            tomorrowStartInMillis = toDate.plusDays(1).getMillis();
        }
        return programsDataCache;
    }

    /**
     * Return todays ProgramsData Cache.
     * Clear cache and reload it in case of new day comes.
     */
    private synchronized List<ProgramData> getTodaysProgramDataCache() {
        if (!todaysProgramsDataCache.isEmpty()) {
            long startOfToday = todaysProgramsDataCache.get(0).getStartDateTime().withTimeAtStartOfDay().getMillis();
            long startofTomorrow = new DateTime().withTimeAtStartOfDay().plusDays(1).getMillis();
            Duration duration = new Duration(startOfToday, startofTomorrow);
            if (duration.getStandardDays() > 1) {
                loadTodaysProgramsDataCache();
            }
        }
        return todaysProgramsDataCache;
    }

    /**
     * Add CSS class to the programData. Based on this we will show the Origin value on widget and detail page 
     * with color, indicating if the program is active, scheduled or completed.
     */
    private void addStatusCssClass(ProgramData programData) {
        try {
            Set<LMProgramBase> allLMProgramBase = loadControlClientConnection.getAllProgramsSet();
            for (LMProgramBase lmpb : allLMProgramBase) {
                ProgramData program = new ProgramData.ProgramDataBuilder(lmpb.getYukonID().intValue())
                                                     .setStartDateTime(new DateTime(lmpb.getStartTime().getTimeInMillis()))
                                                     .build();
                if (isSameProgramEvent(program, programData)) {
                    if (ProgramUtils.isActive(lmpb)) {
                        programData.setStatusCssClass(ACTIVE_CSS_CLASS);
                        break;
                    } else if (ProgramUtils.isScheduled(lmpb)) {
                        programData.setStatusCssClass(SCHEDULED_CSS_CLASS);
                        break;
                    }
                }
            }
        } catch (ConnectionException e) {
            log.warn(e.getMessage());
        }
    }
}
