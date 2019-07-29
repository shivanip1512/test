package com.cannontech.web.dev.database.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.development.model.BulkFakePointInjectionDto;
import com.cannontech.development.model.DevPaoType;
import com.cannontech.development.model.RfnTestEvent;
import com.cannontech.development.service.BulkPointDataInjectionService;
import com.cannontech.development.service.RfnEventTestingService;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingUpdateDao;
import com.cannontech.user.UserUtils;
import com.cannontech.web.dev.DevDbSetupTask;
import com.cannontech.web.dev.database.service.DevDatabasePopulationService;
import com.google.common.collect.Maps;

public class DevBuildDatabasePopulationService {

    // Don't make this static, we don't want the logger to initialize before we get a chance
    // to set the application name
    private Logger log = YukonLogManager.getLogger(DevBuildDatabasePopulationService.class);
    private static DevDatabasePopulationService devDatabasePopulationService;
    private static BulkPointDataInjectionService bulkPointDataInjectionService;
    private static RfnEventTestingService rfnEventTestingService;
    private static DeviceGroupService deviceGroupService;
    private static GlobalSettingUpdateDao globalSettingUpdateDao;
    
    private final static int RFN_SERIAL_FROM = 1000;
    private final static int RFN_SERIAL_TO = 1015;
    private final static int RFN_NUM_DAYS_BEFORE_NOW_EVENTS_SHOULD_START = 10;
    
    public static void main(String[] args) {
        BootstrapUtils.setApplicationName(ApplicationId.DEV_DATABASE_POPULATION);
        new DevBuildDatabasePopulationService().run();
    }

    public void run() {
        try {
            YukonSpringHook.setDefaultContext(YukonSpringHook.WEB_BEAN_FACTORY_KEY);
            devDatabasePopulationService = YukonSpringHook.getBean(DevDatabasePopulationService.class);
            bulkPointDataInjectionService = YukonSpringHook.getBean(BulkPointDataInjectionService.class);
            rfnEventTestingService = YukonSpringHook.getBean(RfnEventTestingService.class);
            deviceGroupService = YukonSpringHook.getBean(DeviceGroupService.class);
            globalSettingUpdateDao = YukonSpringHook.getBean(GlobalSettingUpdateDao.class);

            String groupName = deviceGroupService.getFullPath(SystemGroupEnum.DEVICE_TYPES)+ PaoType.RFWMETER.getPaoTypeName();
            insertInitialDatabaseData();
            insertRfnMetersAndEvents();

            insertMeterUsagePointData();
            insertWaterUsagePointData(groupName);
        } catch (Exception e) {
            log.error("An Exception was thrown during database population. Database population may not have successfully finished. ", e);
            System.exit(1);
        } finally {
            YukonSpringHook.shutdownContext();
            System.exit(0);
        }
    }
    
    /**
     * Updates role properties, inserts meters (mcts, rfns, rfwmeters), 
     * adds an energy company and some accounts (with some hardware per),
     * and adds some cap control objects
     */
    private void insertInitialDatabaseData() {
        // Setup task 
        DevDbSetupTask task = new DevDbSetupTask();
        List<DevPaoType> meters = task.getDevAmr().getMeterTypes();
        //Select all meter types
        for (DevPaoType dpt : meters) {
            dpt.setCreate(true);
        }
        task.getDevAmr().setMeterTypes(meters);
        task.getDevAmr().setNumAdditionalMeters(50);
        task.getDevStars().setNewEnergyCompanyName("Cooper EC");

        log.info("executing initial database population...");
        devDatabasePopulationService.executeFullDatabasePopulation(task);

        globalSettingUpdateDao.updateSettingValue(GlobalSettingType.GOOGLE_ANALYTICS_ENABLED, false, UserUtils.getYukonUser());
    }
    
    /**
     * Send out rfn events (which will also create these meters since they won't be in the db).
     * The service manager must be started for this to do anything
     */
    private void insertRfnMetersAndEvents() {
        log.info("inserting rfn meters and events...");
        
        Map<RfnConditionType, Integer> eventTypeCountMap = Maps.newHashMapWithExpectedSize(RfnConditionType.values().length);
        for (RfnConditionType type : RfnConditionType.values()) {
            eventTypeCountMap.put(type, 0);
        }

        // logging variables
        int counter = 0;
        final int numMeters = RFN_SERIAL_TO - RFN_SERIAL_FROM;
        final int numToSend = eventTypeCountMap.size() * numMeters * RFN_NUM_DAYS_BEFORE_NOW_EVENTS_SHOULD_START;

        Instant temp = new Instant().minus(Duration.standardDays(RFN_NUM_DAYS_BEFORE_NOW_EVENTS_SHOULD_START));
        while (temp.isBeforeNow()) {
            for (Entry<RfnConditionType, Integer> eventType : eventTypeCountMap.entrySet()) {
                RfnTestEvent rfnTestEvent = new RfnTestEvent();
                rfnTestEvent.setTimestamp(temp);
                rfnTestEvent.setSerialFrom(RFN_SERIAL_FROM);
                rfnTestEvent.setSerialTo(RFN_SERIAL_TO);
                rfnTestEvent.setManufacturerModel(RfnManufacturerModel.RFN_420FL);
                rfnTestEvent.setRfnConditionType(eventType.getKey());
                eventType.setValue(eventType.getValue() + 1); //add one to the event counter
                rfnTestEvent.setCount(Long.valueOf(eventType.getValue()));
                int mod = eventType.getValue() % 2;
                boolean cleared = mod == 1 ? true : false; // toggle cleared flag on even/odd counts
                rfnTestEvent.setCleared(cleared);

                counter = counter + numMeters + 1;
                log.debug("Sending events. On event " + counter + "/" + numToSend);

                rfnEventTestingService.sendEventsAndAlarms(rfnTestEvent);
            }
            
            temp = temp.plus(Duration.standardMinutes(1435)); // to offset the events (number of minutes in a day is 1440)
        }
        
    }
    
    /**
     * Inserts water usage point data in a way that indicates a water leak
     * (for testing the Water Leak Report)
     */
    private void insertWaterUsagePointData(String groupName) {
        BulkFakePointInjectionDto bulkInjection = new BulkFakePointInjectionDto();
        bulkInjection.setAttribute(BuiltInAttribute.USAGE_WATER);
        bulkInjection.setGroupName(groupName);
        bulkInjection.setIncremental(true);
        bulkInjection.setValueLow(1.123);
        bulkInjection.setValueHigh(3.456);
        bulkInjection.setAlgorithm("normal");
        bulkInjection.setArchive(true);
        bulkInjection.setDecimalPlaces(3);
        bulkInjection.setPeriod(Period.hours(1));
        bulkInjection.setPeriodWindow(Period.seconds(0));
        
        LocalDate nowLocalDate = new LocalDate();
        Instant start = TimeUtil.toMidnightAtBeginningOfDay(nowLocalDate, DateTimeZone.UTC).minus(Duration.standardDays(5));
        bulkInjection.setStart(start);
        Instant stop = TimeUtil.toMidnightAtBeginningOfDay(nowLocalDate, DateTimeZone.UTC).plus(Duration.standardDays(1));
        bulkInjection.setStop(stop);

        log.info("inserting water usage point data...");
        bulkPointDataInjectionService.excecuteInjection(bulkInjection);
    }
    
    private void insertMeterUsagePointData() {
        /* USAGE */
        BulkFakePointInjectionDto usageData = new BulkFakePointInjectionDto();
        usageData.setAttribute(BuiltInAttribute.USAGE);
        usageData.setIncremental(true);
        usageData.setDecimalPlaces(3);
        /* random values */
        usageData.setValueLow(2.123);
        usageData.setValueHigh(4.123);

        log.info("inserting meter usage point data...");
        bulkPointDataInjectionService.excecuteInjection(usageData);

        /* DEMAND */
        BulkFakePointInjectionDto demandData = new BulkFakePointInjectionDto();
        demandData.setAttribute(BuiltInAttribute.DEMAND);
        demandData.setIncremental(false);
        demandData.setDecimalPlaces(3);
        /* random values */
        demandData.setValueLow(1.513);
        demandData.setValueHigh(4.981);
        
        log.info("inserting meter demand point data...");
        bulkPointDataInjectionService.excecuteInjection(demandData);
    }
}
