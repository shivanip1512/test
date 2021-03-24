package com.cannontech.common.smartNotification.simulation.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.DailyDigestTestParams;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler.MonitorState;
import com.cannontech.common.smartNotification.model.InfrastructureWarningsEventAssembler;
import com.cannontech.common.smartNotification.model.MeterDrEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.smartNotification.simulation.service.SmartNotificationSimulatorService;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.dr.meterDisconnect.DrMeterControlStatus;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.infrastructure.model.SmartNotificationSimulatorSettings;
import com.cannontech.infrastructure.simulation.service.InfrastructureWarningsGeneratorService;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class SmartNotificationSimulatorServiceImpl implements SmartNotificationSimulatorService {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationSimulatorServiceImpl.class);
    @Autowired protected IDatabaseCache cache;
    @Autowired private SmartNotificationEventCreationService eventCreationService;
    @Autowired private SmartNotificationEventDao eventDao;
    @Autowired private InfrastructureWarningsGeneratorService infrastructureWarningsGeneratorService;
    @Autowired private MonitorCacheService monitorCacheService;
    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private DeviceGroupService deviceGroupService;

    private YukonJmsTemplate jmsTemplate;
    private static final Random rand = new Random();
    private SmartNotificationSimulatorSettings settings;
    
    private Executor executor = Executors.newCachedThreadPool();

    @PostConstruct
    public void init() {
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.SMART_NOTIFICATION_DAILY_DIGEST_TEST);
    }

    @Override
    public SimulatorResponseBase clearAllSubscriptions() {
        subscriptionDao.deleteAllSubcriptions();
        return new SimulatorResponseBase(true);
    }
    
    @Override
    public SimulatorResponseBase clearAllEvents() {
        eventDao.deleteAllEvents();
        return new SimulatorResponseBase(true);
    }

    @Override
    public SimulatorResponseBase createEvents() {
        refreshSettings();
        SmartNotificationSimulatorSettings settings = getCurrentSettings();
        List<SmartNotificationEventType> types = settings.isAllTypes() ? Arrays
                .asList(SmartNotificationEventType.values()) : Lists.newArrayList(settings.getType());
        types.forEach(type -> {
            executor.execute(() -> {
                if (type == SmartNotificationEventType.METER_DR) {
                    createMeterDrEvents();
                } else if (type == SmartNotificationEventType.INFRASTRUCTURE_WARNING) {
                    createInfrastructureWaringEvent(settings);
                } else if (type == SmartNotificationEventType.DEVICE_DATA_MONITOR) {
                    createDeviceDataMonitorEvents(settings);
                } else {
                    log.info("Event:{} not supported by simulator", type);
                }
            });
        });
        return new SimulatorResponseBase(true);
    }

    private void createInfrastructureWaringEvent(SmartNotificationSimulatorSettings settings) {
        if (settings.isAllTypes() || settings.getType() == SmartNotificationEventType.INFRASTRUCTURE_WARNING) {
            List<InfrastructureWarningType> types = Lists.newArrayList(InfrastructureWarningType.values());
            List<SmartNotificationEvent> events = new ArrayList<>();
            int totalMsgs = settings.getEventsPerType() * settings.getEventsPerMessage();
            for (int i = 0; i < totalMsgs; i++) {
                int index = rand.nextInt(types.size());
                InfrastructureWarning warning = infrastructureWarningsGeneratorService.generate(types.get(index));
                events.add(InfrastructureWarningsEventAssembler.assemble(Instant.now(), warning));
            }
            if (settings.getWaitTimeSec() > 0) {
                send(events, SmartNotificationEventType.INFRASTRUCTURE_WARNING, settings.getEventsPerMessage(),
                        settings.getWaitTimeSec());
            } else {
                eventCreationService.send(SmartNotificationEventType.INFRASTRUCTURE_WARNING, events);
                log.info("Completed sending events:{} all at once for:{}", events.size(),
                        SmartNotificationEventType.INFRASTRUCTURE_WARNING);
            }
        }
    }

    private void createDeviceDataMonitorEvents(SmartNotificationSimulatorSettings settings) {
        if (settings.isAllTypes() || settings.getType() == SmartNotificationEventType.DEVICE_DATA_MONITOR) {
            List<SmartNotificationEvent> events = new ArrayList<>();
            List<DeviceDataMonitor> monitors = new ArrayList<>(monitorCacheService.getDeviceDataMonitors());
            if(!settings.isAllTypes()) {
                monitors.removeIf(m -> m.getId() != Integer.parseInt(settings.getParameter()));
            }
           
            int totalMsgs = settings.getEventsPerMessage() * settings.getEventsPerType();

            monitors.forEach(monitor -> {
                List<Integer> allIds = new ArrayList<Integer>(deviceGroupService.getDeviceIds(List.of(monitor.getGroup())));
                for (int i = 0; i < totalMsgs; i++) {
                    int index = rand.nextInt(allIds.size());
                    events.add(DeviceDataMonitorEventAssembler.assemble(Instant.now(), monitor.getId(),
                            monitor.getName(), MonitorState.IN_VIOLATION, allIds.get(index)));
                }
            });
            if (settings.getWaitTimeSec() > 0) {
                send(events, SmartNotificationEventType.DEVICE_DATA_MONITOR, settings.getEventsPerMessage(),
                        settings.getWaitTimeSec());
            } else {
                eventCreationService.send(SmartNotificationEventType.DEVICE_DATA_MONITOR, events);
                log.info("Completed sending events:{} all at once for:{}", events.size(),
                        SmartNotificationEventType.DEVICE_DATA_MONITOR);
            }
        }
    }
    
    
    private void send(List<SmartNotificationEvent> events, SmartNotificationEventType type, int eventsPerMessage, int waitTime) {
        for (List<SmartNotificationEvent> part : Lists.partition(events, eventsPerMessage)) {
            try {
                part.forEach(p -> p.setTimestamp(Instant.now()));
                log.info("Waited:{}s Events per msg:{} Tolal:{} Events:{}", waitTime, eventsPerMessage, part.size(), part);
                eventCreationService.send(type, part);
                TimeUnit.SECONDS.sleep(waitTime);
            } catch (InterruptedException e) {
                log.error("InterruptedException", e);
            }
        }
        log.info("Complete:{} Events:{}", type, events.size());
    }

    private void createMeterDrEvents() {
        if (settings.isAllTypes() || settings.getType() == SmartNotificationEventType.METER_DR) {
            List<SmartNotificationEvent> events = new ArrayList<>();
            Map<String, Long> statistics = new HashMap<>();
            List<String> statuses = Lists.newArrayList(DrMeterControlStatus.FAILED_ARMED.name(),
                    DrMeterControlStatus.CONTROL_CONFIRMED.name(), DrMeterControlStatus.CONTROL_FAILED.name(),
                    DrMeterControlStatus.CONTROL_FAILED.name(), DrMeterControlStatus.CONTROL_UNKNOWN.name());
            for (int i = 0; i < 10; i++) {
                String randomElement = statuses.get(rand.nextInt(statuses.size()));
                statistics.compute(randomElement, (key, value) -> value == null ? 1 : value + 1);
            }
            events.add(MeterDrEventAssembler.assemble(statistics, "Test Program"));
            eventCreationService.send(SmartNotificationEventType.METER_DR, events);
            log.info("Sending events:{} for:{}", events.size());
        }
    }
    
    @Override 
    public SimulatorResponseBase startDailyDigest() {
        refreshSettings();
        log.info("Initiating a test daily digest for {}:00", settings.getDailyDigestHour());
        jmsTemplate.convertAndSend(new DailyDigestTestParams(settings.getDailyDigestHour()));
        return new SimulatorResponseBase(true);
    }

    @Override
    public SmartNotificationSimulatorSettings getCurrentSettings() {
        if (settings == null) {
            refreshSettings();
        }
        return settings;
    }

    private void refreshSettings() {
        settings = new SmartNotificationSimulatorSettings(
                yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_DAILY_DIGEST_HOUR),
                yukonSimulatorSettingsDao.getBooleanValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_ALL_EVENT_TYPES),
                SmartNotificationEventType.valueOf(
                        yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENT_TYPE)),
                yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENT_PARAMETER),
                yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENTS_PER_TYPE),
                yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENTS_PER_MESSAGE),
                yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_WAIT_TIME_SEC));
    }
}
