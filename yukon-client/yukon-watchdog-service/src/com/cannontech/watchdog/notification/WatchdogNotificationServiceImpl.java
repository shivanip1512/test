package com.cannontech.watchdog.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.WatchdogAssembler;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdogs.impl.ServiceStatusWatchdog;

@Component
public class WatchdogNotificationServiceImpl implements WatchdogNotificationService {
    private static final Logger log = YukonLogManager.getLogger(WatchdogNotificationServiceImpl.class);

    @Autowired private SmartNotificationEventCreationService smartNotificationEventCreationService;
    @Autowired private EmailService emailService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    private List<ServiceStatusWatchdog> serviceStatusWatchers;
    private MessageSourceAccessor messageSourceAccessor;
    
    private final List<YukonServices> requiedServicesForSmartNotif = new ArrayList<>(
        Arrays.asList(YukonServices.MESSAGEBROKER, YukonServices.NOTIFICATIONSERVICE, YukonServices.SERVICEMANAGER));
    
    @PostConstruct
    public void init() {
        messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
    }
    
    @Autowired
    void setWatchers(List<ServiceStatusWatchdog> serviceStatusWatchdog) {
        this.serviceStatusWatchers = serviceStatusWatchdog;
    }
    
    @Override
    public void sendNotification(SmartNotificationEventType type, List<SmartNotificationEvent> events) {
        // Get list of stopped services out of those which are required for smart notification (Broker, Service Manager and Notification services)
        List <YukonServices> stoppedServices = getStoppedServices();
        // Check If all required services are running for smart notification else send internal notification.
        if (stoppedServices.isEmpty()) {
        // TODO: This is temporary code, this will have to be replaced
            smartNotificationEventCreationService.send(type, events);
        } else {
            sendInternalNotification(stoppedServices);
        }

    }

    // Return a list of Stopped services which are required for smart notification i.e Broker, Service Manager and Notification.
    private synchronized List<YukonServices> getStoppedServices() {
        List <YukonServices> stoppedServices = new ArrayList<>();
        serviceStatusWatchers.forEach(e -> {
            if (requiedServicesForSmartNotif.contains(e.getServiceName())) {
                if (!e.isServiceRunning()) {
                    stoppedServices.add(e.getServiceName());
                }
            }
        });
        return stoppedServices;
    }

    // Create an email for internal notification and add all stopped services names to it.
    private void sendInternalNotification(List<YukonServices> stoppedServices) {
        try {
            String to = globalSettingDao.getString(GlobalSettingType.CONTACT_EMAIL);
            String subject = messageSourceAccessor.getMessage("yukon.watchdog.notification.subject");
            StringBuilder msgBuilder = new StringBuilder();
            String message = messageSourceAccessor.getMessage("yukon.watchdog.notification.text");
            msgBuilder.append(message + "\n");
            // Append all the stopped services names to the email body.
            for (YukonServices s : stoppedServices) {
                msgBuilder.append("\n");
                msgBuilder.append(messageSourceAccessor.getMessage("yukon.watchdog.notification." + s.toString()));
            }
            EmailMessage emailMessage = new EmailMessage(InternetAddress.parse(to), subject, msgBuilder.toString());
            emailService.sendMessage(emailMessage);
        } catch (Exception e) {
            log.error("Watch dog is unable to send Internal Notification");
        }
    }

    public List<SmartNotificationEvent> assemble(List<WatchdogWarnings> warnings, Instant now) {
        return warnings.stream().map(warning -> WatchdogAssembler.assemble(warning, now)).collect(Collectors.toList());
    }

}

