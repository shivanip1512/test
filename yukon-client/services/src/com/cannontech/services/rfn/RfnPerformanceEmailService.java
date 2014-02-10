package com.cannontech.services.rfn;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.FormattingTemplateProcessor;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.db.notification.NotificationGroup;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.notif.outputs.Contactable;
import com.cannontech.notif.outputs.NotifMapContactable;
import com.cannontech.notif.outputs.StandardEmailHandler;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.PreferenceOnOff;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailService;
import com.cannontech.tools.email.EmailServiceHtmlMessage;
import com.cannontech.user.SystemUserContext;
import com.cannontech.user.YukonUserContext;

public class RfnPerformanceEmailService {

    @Autowired private PerformanceVerificationDao performanceVerificationDao;
    @Autowired private ScheduledExecutor scheduledExecutor;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private TemplateProcessorFactory templateProcessorFactory;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private EmailService emailService;
    @Autowired private NotificationGroupDao notificationGroupDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    private static final Logger log = YukonLogManager.getLogger(RfnPerformanceEmailService.class);
    Resource emailTemplate = null;
    private static final String rowTemplate = "<tr>" +
            "<{cell}>{date}</{cell}>" +
            "<{cell}>{successful}</{cell}>" +
            "<{cell}>{unsuccessful}</{cell}>" +
            "<{cell}>{unknown}</{cell}>" +
            "<{cell}>{percentage}</{cell}>" +
          "</tr>";

    @PostConstruct
    public void init() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixAm = now.withTime(6, 0, 0, 0);
        if (now.isAfter(sixAm)) {
            sixAm = sixAm.plusDays(1);
        }
        long secondsUntilSixAm = Seconds.secondsBetween(now, sixAm).getSeconds();

        scheduledExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                init();
                PreferenceOnOff preference = globalSettingDao.getEnum(GlobalSettingType.RF_BROADCAST_PERFORMANCE, PreferenceOnOff.class);
                if (preference.equals(PreferenceOnOff.ON)) {
                    EmailServiceHtmlMessage htmlMessage = generateEmail();
                    sendEmails(htmlMessage);
                }
            }
        }, secondsUntilSixAm, TimeUnit.SECONDS);
    }

    private void sendEmails(final EmailServiceHtmlMessage htmlMessageTemplate) {
        //TODO Header has a link. Where should the link go?
        int notifGroupId = NotificationGroup.NONE_NOTIFICATIONGROUP_ID;

        final LiteNotificationGroup liteNotifGroup = notificationGroupDao.getLiteNotificationGroup(notifGroupId);

        if (liteNotifGroup == null) {
            log.info("Ignoring notification request because notification group with id " + notifGroupId + " doesn't exist.");
            return; // we "handled" it, by not sending anything
        }

        if (liteNotifGroup.isDisabled()) {
            log.info("Ignoring notification request because notification group is disabled: group=" + liteNotifGroup);
            return; // we "handled" it, by not sending anything
        }

        final List<Contactable> contactables = NotifMapContactable.getContactablesForGroup(liteNotifGroup);

        for (final Contactable contact : contactables) {
            final List<LiteContactNotification> notifications = contact.getNotifications(StandardEmailHandler.checker);
            for (LiteContactNotification addr : notifications) {
                String emailTo = addr.getNotification();
                try {
                    EmailServiceHtmlMessage message = 
                            new EmailServiceHtmlMessage(
                                InternetAddress.parse(emailTo),
                                htmlMessageTemplate.getSubject(),
                                htmlMessageTemplate.getBody(),
                                htmlMessageTemplate.getHtmlBody()
                                );
                    emailService.sendMessage(message);
                } catch (MessagingException e) {
                    log.warn("Unable to email message for " + contact + " to address " + emailTo + ".", e);
                }
            }
        }
    }

    public EmailServiceHtmlMessage generateEmail() {
        final YukonUserContext userContext = new SystemUserContext();
        FormattingTemplateProcessor tp = templateProcessorFactory.getFormattingTemplateProcessor(userContext);
        DateFormat dateFormatter = dateFormattingService.getDateFormatter(DateFormatEnum.DATE, userContext);
        DateTimeFormatter dateTimeFormatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATEHM, userContext);
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
 
        Map<String, Object> theaderMap = new HashMap<>();
        theaderMap.put("cell", "th");
        theaderMap.put("date", accessor.getMessage("yukon.web.defaults.date"));
        theaderMap.put("successful", accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.successful"));
        theaderMap.put("unsuccessful", accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.unsuccessful"));
        theaderMap.put("unknown",accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.unknown"));
        theaderMap.put("percentage", accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.percentage"));
        String theader = tp.process(rowTemplate, theaderMap);

        Instant now = Instant.now();
        Instant yesterday = now.minus(Duration.standardDays(1));
        Instant lastWeek = yesterday.minus(Duration.standardDays(7));
        Range<Instant> reportDates = new Range<Instant>(lastWeek, false, yesterday, true);

        List<PerformanceVerificationEventMessageStats> reports = performanceVerificationDao.getReports(reportDates);

        Comparator<PerformanceVerificationEventMessageStats> mostRecentFirst = new Comparator<PerformanceVerificationEventMessageStats>(){
            @Override
            public int compare(PerformanceVerificationEventMessageStats event1, PerformanceVerificationEventMessageStats event2) {
                return - event1.getTimeMessageSent().compareTo(event2.getTimeMessageSent());
            }
        };
        Collections.sort(reports, mostRecentFirst);

        StringBuilder tbody = new StringBuilder();

        for (PerformanceVerificationEventMessageStats report : reports) {

            Instant date = report.getTimeMessageSent();
            String formattedDate = dateFormatter.format(date.toDate());
            double percentSuccess = report.getPercentSuccess();
            String precentageStr = MessageFormat.format("{0, number,##.#%}", percentSuccess);

            Map<String, Object> map = new HashMap<>();
            map.put("cell", "td");
            map.put("date", formattedDate);
            map.put("successful", report.getNumSuccesses());
            map.put("unsuccessful", report.getNumFailures());
            map.put("unknown", report.getNumUnknowns());
            map.put("percentage", precentageStr);
            String rowText = tp.process(rowTemplate, map);
            tbody.append(rowText).append("\n");
        }

        String formattedDate = dateTimeFormatter.print(now);

        //TODO Header has a link. Where should the link go?
        String subject = accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.emailSubject");
        String header = accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.emailHeader");
        String footer = accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.emailFooter", formattedDate);

        Map<String, String> emailMap = new HashMap<>();
        emailMap.put("header", header);
        emailMap.put("theader", theader);
        emailMap.put("tbody", tbody.toString());
        emailMap.put("footer", footer);

        String htmlBody = null;
        try {
            if (emailTemplate == null) {
                emailTemplate = new ClassPathResource("com/cannontech/services/rfn/rfnPerformanceEmail.tmp");
            }
            htmlBody = tp.process(emailTemplate, emailMap);
        } catch (IOException e) {
            log.error("Could not open template for RFN Performance Email");
        }


        EmailServiceHtmlMessage htmlMessage = null;
        try {
            htmlMessage = new EmailServiceHtmlMessage(
                new InternetAddress[] {new InternetAddress()},
                subject,
                htmlBody,
                htmlBody);
        } catch (MessagingException e) {
            //Unreachable. Only throws exception when it can't parse a 'from' address, which is null in this case.
        }

        return htmlMessage;
    };
}