package com.cannontech.watchdog.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.SmtpHelper;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.WatchdogAssembler;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.util.WebserverUrlResolver;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.tools.email.SystemEmailSettingsType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdogs.impl.ServiceStatusWatchdog;

@Component
public class WatchdogNotificationServiceImpl implements WatchdogNotificationService {
    private static final Logger log = YukonLogManager.getLogger(WatchdogNotificationServiceImpl.class);

    @Autowired private SmartNotificationEventCreationService smartNotificationEventCreationService;
    @Autowired private EmailService emailService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private WebserverUrlResolver webserverUrlResolver;
    @Autowired private SmtpHelper smtpHelper;
    
    private List<ServiceStatusWatchdog> serviceStatusWatchers;
    private MessageSourceAccessor messageSourceAccessor;
    private DateTime lastNotificationSendTime;
    private static List<String> sendToEmailIds;
    private static String sender;
    
    private final List<YukonServices> requiredServicesForSmartNotif = new ArrayList<>(
            Arrays.asList(YukonServices.MESSAGEBROKER, YukonServices.NOTIFICATIONSERVICE, YukonServices.SERVICEMANAGER,
                    YukonServices.DATABASE));
    
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
            log.info("Sending events to smart notification framework");
            smartNotificationEventCreationService.send(type, events);
        } else {
            log.info("Sending events to internal notification");
            sendInternalNotification(stoppedServices);
        }
    }

    // Return a list of Stopped services which are required for smart notification i.e Broker, Service Manager and Notification.
    private synchronized List<YukonServices> getStoppedServices() {
        List<YukonServices> stoppedServices = serviceStatusWatchers.stream()
                                                                   .filter(e -> (requiredServicesForSmartNotif.contains(e.getServiceName()) && !e.isServiceRunning()))
                                                                   .map(ServiceStatusWatchdog::getServiceName)
                                                                   .collect(Collectors.toList());

        return stoppedServices;

    }

    // Create an email for internal notification and add all stopped services names to it.
    private void sendInternalNotification(List<YukonServices> stoppedServices) {
        if (!shouldSendInternalNotification()) {
            log.info("Not sending any notification now as notification was send at " + lastNotificationSendTime);
        } else {
            try {
                loadEmailIds();
                if (CollectionUtils.isEmpty(sendToEmailIds)) {
                    throw new NotFoundException("No user subscribed for notification for watchdog");
                }
                String subject = messageSourceAccessor.getMessage("yukon.watchdog.notification.subject");
                StringBuilder msgBuilder = new StringBuilder();
                String message = messageSourceAccessor.getMessage("yukon.watchdog.notification.text");
                msgBuilder.append(message + "\n");
                // Append all the stopped services names to the email body.
                for (YukonServices s : stoppedServices) {
                    msgBuilder.append("\n");
                    msgBuilder.append(messageSourceAccessor.getMessage("yukon.watchdog.notification." + s.toString()));
                }
                msgBuilder.append("\n\nSee " + webserverUrlResolver.getUrlBase());
                EmailMessage emailMessage = EmailMessage.newMessageBccOnly(subject, msgBuilder.toString(), sender,
                        sendToEmailIds);
                emailService.sendMessage(emailMessage);
            } catch (Exception e) {
                log.error("Watch dog is unable to send Internal Notification " + e);
            }
        }
    }

    /**
     * Update Email IDs of subscribers and Sender.
     */
    private void loadEmailIds() {
        sendToEmailIds = Arrays.asList(
                smtpHelper.getCachedValue(SystemEmailSettingsType.WATCHDOG_SUBSCRIBER_EMAILS.getKey()).split("\\s*,\\s*"));
        sender = smtpHelper.getCachedValue(SystemEmailSettingsType.MAIL_FROM_ADDRESS.getKey());
    }

    /*
     * Check if internal notification should be send.
     * Do not send notification if notification was send less then an hour ago.
     */
    private synchronized boolean shouldSendInternalNotification() {
        if (lastNotificationSendTime == null) {
            lastNotificationSendTime = DateTime.now();
            return true;
        }

        if (Hours.hoursBetween(lastNotificationSendTime, DateTime.now()).getHours() < 1) {
            return false;
        }
        return true;
    }
    
    public List<SmartNotificationEvent> assemble(List<WatchdogWarnings> warnings, Instant now) {
        return warnings.stream().map(warning -> WatchdogAssembler.assemble(warning, now)).collect(Collectors.toList());
    }

}

