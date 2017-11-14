package com.cannontech.web.dev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.amr.deviceDataMonitor.service.impl.DeviceDataMonitorServiceImpl;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.DailyDigestTestParams;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler.MonitorState;
import com.cannontech.common.smartNotification.model.InfrastructureWarningsEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.smartNotification.service.SmartNotificationSubscriptionService;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dev.service.InfrastructureWarningsGeneratorService;
import com.cannontech.web.infrastructure.service.InfrastructureWarningsRefreshService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

@Controller
public class SmartNotificationsTestingController {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationsTestingController.class);
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired protected IDatabaseCache cache;
    @Autowired private SmartNotificationEventCreationService eventCreationService;
    @Autowired private SmartNotificationEventDao eventDao;
    @Autowired private InfrastructureWarningsGeneratorService infrastructureWarningsGeneratorService;
    @Autowired private InfrastructureWarningsRefreshService infrastructureWarningsRefreshService;
    @Autowired private MonitorCacheService monitorCacheService;
    @Autowired private DeviceDataMonitorServiceImpl monitorService;
    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired private SmartNotificationSubscriptionService subscriptionService;
    @Autowired private YukonUserDao yukonUserDao;
    private Executor executor = Executors.newCachedThreadPool();
    private JmsTemplate jmsTemplate;
    
    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(true);
        jmsTemplate.setPubSubDomain(false);
    }
    
    @RequestMapping("smartNotificationsSimulator")
    public String smartNotificationsSimulator() {
        return "smartNotificationsSimulator.jsp";
    }
    
    @RequestMapping("clearAllSubscriptions")
    public String clearAllSubscriptions(FlashScope flash) {
        subscriptionDao.deleteAllSubcriptions();
        flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("All Subscriptions have been cleared."));
        return "redirect:smartNotificationsSimulator";
    }
    
    @RequestMapping("clearAllEvents")
    public String clearAllEvents(FlashScope flash) {
        eventDao.deleteAllEvents();
        flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("All Events have been cleared."));
        return "redirect:smartNotificationsSimulator";
    }
    
    @RequestMapping("createRealEvents")
    public String createRealEvents(FlashScope flash) {
        monitorCacheService.getDeviceDataMonitors().forEach(m -> {
            monitorService.recaclulate(m);
        });
        infrastructureWarningsRefreshService.initiateRecalculation();
        flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Requested recualctulation."));
        return "redirect:smartNotificationsSimulator";
    }

    /**
     * Returns index of the next object if object doesn't exists, start from 0.
     */
    private int getNextSubscriptionIndex(List<SmartNotificationSubscription> subscriptions, int index) {
        try {
            subscriptions.get(index + 1);
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
        return index + 1;
    }
    
    @RequestMapping("createEvents")
    public String createEvents(@RequestParam int waitTime, @RequestParam int eventsPerMessage,
            @RequestParam int numberOfMessages, FlashScope flash) {
        Map<SmartNotificationEventType, List<SmartNotificationSubscription>> subscriptionsByType =
            subscriptionDao.getAllSubscriptions().stream().collect(Collectors.groupingBy(e -> e.getType()));
        
        // send total "numberOfMessages" with "eventsPerMessage" waiting "waitTime"
        // between sending each set of events
        
        ArrayList<Integer> deviceIds = new ArrayList<>(cache.getAllMeters().keySet());
        
        Set<SmartNotificationEventType> done = Collections.synchronizedSet(new HashSet<>());
        
        Random random = new Random();
        Arrays.asList(SmartNotificationEventType.values()).forEach(type -> {
            executor.execute(() -> {
                log.info("Simulating events for " + type);
                List<SmartNotificationSubscription> subscriptions = subscriptionsByType.get(type);
                if (subscriptions != null) {
                    List<SmartNotificationEvent> events = new ArrayList<>();

                    int i = 0;
                    int nextIndex = -1;
                    while (i++ < numberOfMessages) {
                        int index = random.nextInt(deviceIds.size());
                        nextIndex = getNextSubscriptionIndex(subscriptions, nextIndex);
                        
                        if(type == SmartNotificationEventType.INFRASTRUCTURE_WARNING){
                            List<InfrastructureWarningType> types = Lists.newArrayList(InfrastructureWarningType.values());
                            Collections.shuffle(types);
                            InfrastructureWarning warning = infrastructureWarningsGeneratorService.genarate(types.get(0));
                            events.add(InfrastructureWarningsEventAssembler.assemble(Instant.now(), warning));
                        } else if (type == SmartNotificationEventType.DEVICE_DATA_MONITOR) {
                            int monitorId = DeviceDataMonitorEventAssembler.getMonitorId(
                                subscriptions.get(nextIndex).getParameters());
                            String monitorName = monitorCacheService.getDeviceDataMonitors().stream().filter(
                                m -> m.getId() == monitorId).findFirst().get().getName();
                            events.add(DeviceDataMonitorEventAssembler.assemble(Instant.now(), monitorId, monitorName,
                                MonitorState.IN_VIOLATION, deviceIds.get(index)));
                        }
                    }

                    log.info("Created events " + events.size() + " " + type);

                    for (List<SmartNotificationEvent> part : Lists.partition(events, eventsPerMessage)) {
                        try {
                            TimeUnit.SECONDS.sleep(waitTime);
                            log.info("Sending " + part.size() + " " + type);
                            eventCreationService.send(type, part);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                done.add(type);
            });

        });

        while (done.size() != subscriptionsByType.size()) {
            //wait to display success until all events are generated.
            continue;
        }
        flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Created test events."));
        return "redirect:smartNotificationsSimulator";
    }

    @RequestMapping(value="saveSubscription", method=RequestMethod.POST)
    public String saveSmartNotificationsSubscription(@ModelAttribute("subscription") SmartNotificationSubscription subscription, 
                                                     @RequestParam int userGroupId, @RequestParam boolean generateTestEmailAddresses, YukonUserContext userContext) throws Exception {
        List<Integer> userIds = yukonUserDao.getUserIdsForUserGroup(userGroupId);
        userIds.forEach(id -> {
            subscription.setId(0);
            subscription.setUserId(id);
            if (generateTestEmailAddresses) {
                subscription.setRecipient(id + "@eaton.com");
            }
            subscriptionService.saveSubscription(subscription, userContext);
        });
        return "redirect:smartNotificationsSimulator";
    }
    
    @RequestMapping(value="startDailyDigest")
    public String startDailyDigest(@RequestParam Integer hour, FlashScope flash) {
        log.info("Initiating a test daily digest for " + hour + ":00");
        jmsTemplate.convertAndSend(JmsApiDirectory.SMART_NOTIFICATION_DAILY_DIGEST_TEST.getQueue().getName(), 
                                   new DailyDigestTestParams(hour));
        flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Initiated a daily digest for " + hour + ":00"));
        return "redirect:smartNotificationsSimulator";
    }
}
