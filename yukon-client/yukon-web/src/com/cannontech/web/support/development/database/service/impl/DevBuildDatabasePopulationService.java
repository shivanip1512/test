package com.cannontech.web.support.development.database.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;

import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.development.model.BulkFakePointInjectionDto;
import com.cannontech.development.model.RfnTestEvent;
import com.cannontech.development.service.BulkPointDataInjectionService;
import com.cannontech.development.service.RfnEventTestingService;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.support.development.DevDbSetupTask;
import com.cannontech.web.support.development.database.objects.DevPaoType;
import com.cannontech.web.support.development.database.service.DevDatabasePopulationService;
import com.google.common.collect.Maps;

public class DevBuildDatabasePopulationService {

    private static Logger log = YukonLogManager.getLogger(DevBuildDatabasePopulationService.class);
    private static DevDatabasePopulationService devDatabasePopulationService;
    private static BulkPointDataInjectionService bulkPointDataInjectionService;
    private static RfnEventTestingService rfnEventTestingService;
    
    private final static String DEFAULT_WATER_NODE_DEVICE_GROUP = SystemGroupEnum.DEVICETYPES.getFullPath() + PaoType.RFWMETER.getPaoTypeName();
    private final static int RFN_SERIAL_FROM = 1000;
    private final static int RFN_SERIAL_TO = 1015;
    private final static int RFN_NUM_DAYS_BEFORE_NOW_EVENTS_SHOULD_START = 10;
    
    public static void main(String[] args) {
        try {
            YukonSpringHook.setDefaultContext(YukonSpringHook.WEB_BEAN_FACTORY_KEY);
            devDatabasePopulationService = YukonSpringHook.getBean(DevDatabasePopulationService.class);
            bulkPointDataInjectionService = YukonSpringHook.getBean(BulkPointDataInjectionService.class);
            rfnEventTestingService = YukonSpringHook.getBean(RfnEventTestingService.class);

            insertInitialDatabaseData();
            insertRfnMetersAndEvents();

            insertMeterUsagePointData();
            insertWaterUsagePointData();
        } catch (Exception e) {
            log.warn("An Exception was thrown during database population. Database population may not have successfully finished. ",e);
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
    private static void insertInitialDatabaseData() {
        // Setup task 
        DevDbSetupTask task = new DevDbSetupTask();
        List<DevPaoType> meters = task.getDevAMR().getMeterTypes();
        //Select all meter types
        for (DevPaoType dpt : meters) {
            dpt.setCreate(true);
        }
        task.getDevAMR().setMeterTypes(meters);
        task.getDevAMR().setNumAdditionalMeters(2);
        task.getDevStars().setNewEnergyCompanyName("Cooper EC");

        log.info("executing initial database population...");
        devDatabasePopulationService.executeFullDatabasePopulation(task);
    }
    
    /**
     * Send out rfn events (which will also create these meters since they won't be in the db).
     * The service manager must be started for this to do anything
     */
    private static void insertRfnMetersAndEvents() {
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
                rfnTestEvent.setManufacturer("LGYR");
                rfnTestEvent.setModel("FocuskWh");
                rfnTestEvent.setRfnConditionType(eventType.getKey());
                eventType.setValue(eventType.getValue() + 1); //add one to the event counter
                rfnTestEvent.setCount(Long.valueOf(eventType.getValue()));
                int mod = eventType.getValue() % 2;
                boolean cleared = mod == 1 ? true : false; // toggle cleared flag on even/odd counts
                rfnTestEvent.setCleared(cleared);

                counter = counter + numMeters + 1;
                log.info("Sending events. On event " + counter + "/" + numToSend);

                rfnEventTestingService.sendEventsAndAlarms(rfnTestEvent);
            }
            
            temp = temp.plus(Duration.standardMinutes(1435)); // to offset the events (number of minutes in a day is 1440)
        }
        
    }
    
    /**
     * Inserts water usage point data in a way that indicates a water leak
     * (for testing the Water Leak Report)
     */
    private static void insertWaterUsagePointData() {
        BulkFakePointInjectionDto bulkInjection = new BulkFakePointInjectionDto();
        bulkInjection.setAttribute(BuiltInAttribute.USAGE_WATER);
        bulkInjection.setGroupName(DEFAULT_WATER_NODE_DEVICE_GROUP);
        bulkInjection.setIncremental(true);
        bulkInjection.setValueLow(1.123);
        bulkInjection.setValueHigh(3.456);
        bulkInjection.setAlgorithm("normal");
        bulkInjection.setArchive(true);
        bulkInjection.setDecimalPlaces(3);
        bulkInjection.setPeriod(Period.hours(1));
        bulkInjection.setPeriodWindow(Period.seconds(0));
        bulkInjection.setStart(new Instant().minus(Duration.standardDays(3)));
        bulkInjection.setStop(new Instant().plus(Duration.standardDays(1)));
        
        log.info("inserting water usage point data...");
        bulkPointDataInjectionService.excecuteInjection(bulkInjection);
    }
    
    private static void insertMeterUsagePointData() {
        BulkFakePointInjectionDto bulkInjection = new BulkFakePointInjectionDto();
        bulkInjection.setAttribute(BuiltInAttribute.USAGE);
        bulkInjection.setIncremental(true);
        bulkInjection.setValueLow(2);
        bulkInjection.setValueHigh(4);
        bulkInjection.setAlgorithm("normal");

        log.info("inserting meter usage point data...");
        bulkPointDataInjectionService.excecuteInjection(bulkInjection);
    }
}
