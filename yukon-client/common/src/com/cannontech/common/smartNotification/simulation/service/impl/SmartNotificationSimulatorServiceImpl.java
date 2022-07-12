package com.cannontech.common.smartNotification.simulation.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.DailyDigestTestParams;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler;
import com.cannontech.common.smartNotification.model.EatonCloudDrEventAssembler;
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
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.meterDisconnect.DrMeterControlStatus;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.infrastructure.model.SmartNotificationSimulatorSettings;
import com.cannontech.infrastructure.simulation.service.InfrastructureWarningsGeneratorService;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.stars.dr.account.dao.ApplianceAndProgramDao;
import com.cannontech.stars.dr.account.model.ProgramLoadGroup;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class SmartNotificationSimulatorServiceImpl implements SmartNotificationSimulatorService {
    private static final Logger log = YukonLogManager.getSmartNotificationsLogger(SmartNotificationSimulatorServiceImpl.class);
    @Autowired protected IDatabaseCache cache;
    @Autowired private SmartNotificationEventCreationService eventCreationService;
    @Autowired private SmartNotificationEventDao eventDao;
    @Autowired private InfrastructureWarningsGeneratorService infrastructureWarningsGeneratorService;
    @Autowired private MonitorCacheService monitorCacheService;
    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private ApplianceAndProgramDao applianceAndProgramDao;

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
                    createInfrastructureWarningEvent(settings);
                } else if (type == SmartNotificationEventType.DEVICE_DATA_MONITOR) {
                    createDeviceDataMonitorEvents(settings);
                } else if (type == SmartNotificationEventType.EATON_CLOUD_DR) {
                    createEatonCloudDrEvents(settings);
                }else {
                    log.info("Event:{} not supported by simulator", type);
                }
            });
        });
        return new SimulatorResponseBase(true);
    }

    private void createInfrastructureWarningEvent(SmartNotificationSimulatorSettings settings) {
        if (settings.isAllTypes() || settings.getType() == SmartNotificationEventType.INFRASTRUCTURE_WARNING) {
            List<SmartNotificationEvent> events = new ArrayList<>();
            int totalMsgs = settings.getEventsPerType() * settings.getEventsPerMessage();
            for (int i = 0; i < totalMsgs; i++) {
                InfrastructureWarning warning = null;
                if (i % 3 == 0) {
                    warning = infrastructureWarningsGeneratorService
                            .generate(InfrastructureWarningType.GATEWAY_DATA_STREAMING_LOAD);
                } else if (i % 2 == 0) {
                    warning = infrastructureWarningsGeneratorService.generate(InfrastructureWarningType.GATEWAY_FAILSAFE);
                } else {
                    warning = infrastructureWarningsGeneratorService.generate(InfrastructureWarningType.GATEWAY_CONNECTED_NODES);
                }
                events.add(InfrastructureWarningsEventAssembler.assemble(Instant.now(), warning));
            }
            if (settings.getWaitTimeSec() > 0) {
                send(events, SmartNotificationEventType.INFRASTRUCTURE_WARNING, settings.getEventsPerMessage(),
                        settings.getWaitTimeSec());
            } else {
                eventCreationService.send(SmartNotificationEventType.INFRASTRUCTURE_WARNING, events);
                log.info("{} Completed sending events:{}", SmartNotificationEventType.INFRASTRUCTURE_WARNING, events.size());
            }
        }
    }

    private void createMeterDrEvents() {
        if ( !settings.isAllTypes() && settings.getType() == SmartNotificationEventType.METER_DR) {
            List<SmartNotificationEvent> events = new ArrayList<>();
            int totalMsgs = settings.getEventsPerType() * settings.getEventsPerMessage();
            for (int i = 0; i < totalMsgs; i++) {
                Map<String, Long> statistics = getStatistics();
                events.add(MeterDrEventAssembler.assemble(statistics, "Test Program #" + i));
            }

            if (settings.getWaitTimeSec() > 0) {
                send(events, SmartNotificationEventType.METER_DR, settings.getEventsPerMessage(),
                        settings.getWaitTimeSec());
            } else {
                eventCreationService.send(SmartNotificationEventType.METER_DR, events);
                log.info("{} Completed sending events:{}", SmartNotificationEventType.METER_DR, events.size());
            }
        }
    }
    
    
    private void createEatonCloudDrEvents(SmartNotificationSimulatorSettings settings) {
        if (!settings.isAllTypes() && settings.getType() == SmartNotificationEventType.EATON_CLOUD_DR) {
            List<SmartNotificationEvent> events = new ArrayList<>();
            int totalMsgs = settings.getEventsPerType();

            Optional<LiteYukonPAObject> group = cache.getAllLMGroups().stream()
                    .filter(g -> g.getPaoType() == PaoType.LM_GROUP_EATON_CLOUD).findFirst();
            if (group.isEmpty()) {
                return;
            }
            List<ProgramLoadGroup> programsByLMGroupId = applianceAndProgramDao.getProgramsByLMGroupId(group.get().getLiteID());
            if (programsByLMGroupId.isEmpty()) {
                return;
            }
            int programId = programsByLMGroupId.get(0).getPaobjectId();
            String program = cache.getAllLMPrograms().stream().filter(p -> p.getLiteID() == programId).findFirst().get()
                    .getPaoName();
            for (int i = 0; i < totalMsgs; i++) {
                // when i is 0 - total 100 failed 99
                // when i is 1 - total 200 failed 198
                SmartNotificationEvent event = EatonCloudDrEventAssembler.assemble(group.get().getPaoName(), program,
                        (i + 1) * 100, ((i + 1) * 100) - (i + 1), true);
                events.add(event);
            }

            if (settings.getWaitTimeSec() > 0) {
                send(events, SmartNotificationEventType.EATON_CLOUD_DR, 1, settings.getWaitTimeSec());
            } else {
                eventCreationService.send(SmartNotificationEventType.METER_DR, events);
                log.info("{} Completed sending events:{}", SmartNotificationEventType.METER_DR, events.size());
            }
        }
    }

    private Map<String, Long> getStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        List<String> statuses = Lists.newArrayList(DrMeterControlStatus.FAILED_ARMED.name(),
                DrMeterControlStatus.CONTROL_CONFIRMED.name(), DrMeterControlStatus.CONTROL_FAILED.name(),
                DrMeterControlStatus.CONTROL_FAILED.name(), DrMeterControlStatus.CONTROL_UNKNOWN.name());
        for (int i = 0; i < 10; i++) {
            String randomElement = statuses.get(rand.nextInt(statuses.size()));
            statistics.compute(randomElement, (key, value) -> value == null ? 1 : value + 1);
        }
        return statistics;
    }

    private void createDeviceDataMonitorEvents(SmartNotificationSimulatorSettings settings) {
        if (settings.isAllTypes() || settings.getType() == SmartNotificationEventType.DEVICE_DATA_MONITOR) {
          
            List<DeviceDataMonitor> monitors = monitorCacheService.getEnabledDeviceDataMonitors();
            try {
                monitors.removeIf(m -> m.getId() != Integer.parseInt(settings.getParameter()));
            } catch (NumberFormatException e) {
                // user selected all monitors
            }
            
           
            monitors.forEach(monitor -> {
                executor.execute(() -> {
                    int totalMsgs = settings.getEventsPerMessage() * settings.getEventsPerType();
                    List<SmartNotificationEvent> events = new ArrayList<>();
                    List<Integer> allIds = new ArrayList<Integer>(deviceGroupService.getDeviceIds(List.of(monitor.getGroup())));
                    for (int i = 0; i < totalMsgs; i++) {
                        int index = rand.nextInt(allIds.size());
                        events.add(DeviceDataMonitorEventAssembler.assemble(Instant.now(), monitor.getId(),
                                monitor.getName(), MonitorState.IN_VIOLATION, allIds.get(index)));
                    }
                    if (settings.getWaitTimeSec() > 0) {
                        send(events, SmartNotificationEventType.DEVICE_DATA_MONITOR, settings.getEventsPerMessage(),
                                settings.getWaitTimeSec());
                    } else {
                        eventCreationService.send(SmartNotificationEventType.DEVICE_DATA_MONITOR, events);
                        log.info("Monitor {}: Completed sending events:{}", events.size(), monitor.getName());
                    }
                });
            });
        }
    }
    
    private void send(List<SmartNotificationEvent> events, SmartNotificationEventType type, int eventsPerMessage, int waitTime) {
        send(events, type, eventsPerMessage, waitTime, "");
    }

    
    private void send(List<SmartNotificationEvent> events, SmartNotificationEventType type, int eventsPerMessage, int waitTime, String info) {
        for (List<SmartNotificationEvent> part : Lists.partition(events, eventsPerMessage)) {
            try {
                part.forEach(p -> p.setTimestamp(Instant.now()));
                log.info("Waited:{}s Events per msg:{} Total:{} Events:{}", waitTime, eventsPerMessage, part.size(), part);
                eventCreationService.send(type, part);
                TimeUnit.SECONDS.sleep(waitTime);
            } catch (InterruptedException e) {
                log.error("InterruptedException", e);
            }
        }
        log.info("Complete {} {} Events:{}", type, info, events.size());
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
