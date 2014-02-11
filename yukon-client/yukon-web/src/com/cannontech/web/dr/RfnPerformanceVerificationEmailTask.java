package com.cannontech.web.dr;

import static com.cannontech.system.GlobalSettingType.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.FormattingTemplateProcessor;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.system.PreferenceOnOff;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailHtmlMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;

public class RfnPerformanceVerificationEmailTask extends YukonTaskBase {
    private final static Logger log = YukonLogManager.getLogger(RfnPerformanceVerificationEmailTask.class);

    private static final String rowTemplate = "<tr>" +
            "<{cell}>{date}</{cell}>" +
            "<{cell}>{successful}</{cell}>" +
            "<{cell}>{unsuccessful}</{cell}>" +
            "<{cell}>{unknown}</{cell}>" +
            "<{cell}>{percentage}</{cell}>" +
            "</tr>";
    
    private final static InternetAddress[] EMPTY_TO = new InternetAddress[] { new InternetAddress() };
    
    @Autowired private PerformanceVerificationDao performanceVerificationDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private TemplateProcessorFactory templateProcessorFactory;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private EmailService emailService;
    @Autowired private NotificationGroupDao notificationGroupDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    
    private final Resource emailTemplate;
    
    private String notificationGroups;
    private String additionalEmails;

    public RfnPerformanceVerificationEmailTask() {
        emailTemplate = new ClassPathResource("com/cannontech/web/dr/rfnPerformanceEmail.tmp");
    }
    
    @Override
    public void start() {
        PreferenceOnOff preference = globalSettingDao.getEnum(RF_BROADCAST_PERFORMANCE, PreferenceOnOff.class);
        if (preference.equals(PreferenceOnOff.ON)) {
            EmailHtmlMessage htmlMessage = generateEmail();
            sendEmails(htmlMessage);
        }
    }
    
    private void sendEmails(final EmailHtmlMessage htmlMessageTemplate) {
        int[] notifGroupIds = StringUtils.parseIntString(notificationGroups);
        
        final List<LiteNotificationGroup> notifGroups = new ArrayList<>();
        for (int groupId : notifGroupIds) {
            notifGroups.add(notificationGroupDao.getLiteNotificationGroup(groupId));
        }

        if (notifGroups.isEmpty()) {
            log.info("Ignoring request because no notification groups have been specified");
            return; // we "handled" it, by not sending anything
        }

        List<String> notifEmailsAddresses = new ArrayList<>();
        for (LiteNotificationGroup notifGroup : notifGroups) {
            if (!notifGroup.isDisabled()) {
                List<String> notifEmails = Arrays.asList(notificationGroupDao.getNotifEmailsByLiteGroup(notifGroup));
                notifEmailsAddresses.addAll(notifEmails);
            } else {
                log.info("Not sending emails because notification group is disabled: group=" + notifGroup);
            }
        }
        
        List<String> additionalEmailAddresses = StringUtils.parseStringsForList(additionalEmails, ", ");
        if (!additionalEmailAddresses.isEmpty()) {
            notifEmailsAddresses.addAll(additionalEmailAddresses);
        }

        for (String emailTo : notifEmailsAddresses) {
            try {
                EmailHtmlMessage message = new EmailHtmlMessage(InternetAddress.parse(emailTo), 
                        htmlMessageTemplate.getSubject(), htmlMessageTemplate.getBody(),
                        htmlMessageTemplate.getHtmlBody());
                emailService.sendMessage(message);
            } catch (MessagingException e) {
                log.warn("Unable to email message to address " + emailTo + ".", e);
            }
        }
    }

    private EmailHtmlMessage generateEmail() {
        FormattingTemplateProcessor tp =
            templateProcessorFactory.getFormattingTemplateProcessor(YukonUserContext.system);
        DateFormat dateFormatter = dateFormattingService.getDateFormatter(DateFormatEnum.DATE, YukonUserContext.system);
        DateTimeFormatter dateTimeFormatter =
            dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATEHM, YukonUserContext.system);
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(YukonUserContext.system);
 
        Map<String, Object> theaderData = new HashMap<>();
        theaderData.put("cell", "th");
        theaderData.put("date", accessor.getMessage("yukon.web.defaults.date"));
        theaderData.put("successful", accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.successful"));
        theaderData.put("unsuccessful", accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.unsuccessful"));
        theaderData.put("unknown",accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.unknown"));
        theaderData.put("percentage", accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.percentage"));
        String theader = tp.process(rowTemplate, theaderData);

        Instant now = new Instant();
        Instant yesterday = now.minus(Duration.standardDays(1));
        Instant lastWeek = yesterday.minus(Duration.standardDays(7));
        Range<Instant> reportDates = new Range<Instant>(lastWeek, false, yesterday, true);

        List<PerformanceVerificationEventMessageStats> reports = performanceVerificationDao.getReports(reportDates);

        Comparator<PerformanceVerificationEventMessageStats> mostRecentFirst =
                new Comparator<PerformanceVerificationEventMessageStats>() {
            @Override
            public int compare(PerformanceVerificationEventMessageStats event1,
                               PerformanceVerificationEventMessageStats event2) {
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

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("cell", "td");
            templateData.put("date", formattedDate);
            templateData.put("successful", report.getNumSuccesses());
            templateData.put("unsuccessful", report.getNumFailures());
            templateData.put("unknown", report.getNumUnknowns());
            templateData.put("percentage", precentageStr);
            String rowText = tp.process(rowTemplate, templateData);
            tbody.append(rowText).append("\n");
        }

        String formattedDate = dateTimeFormatter.print(now);

        // TODO Header has a link. Where should the link go?
        String subject = accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.emailSubject");
        String header = accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.emailHeader");
        String footer = accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.emailFooter", formattedDate);

        Map<String, String> emailData = new HashMap<>();
        emailData.put("header", header);
        emailData.put("theader", theader);
        emailData.put("tbody", tbody.toString());
        emailData.put("footer", footer);

        String htmlBody = null;
        try {
            htmlBody = tp.process(emailTemplate, emailData);
        } catch (IOException e) {
            throw new RuntimeException("Could not open template for RFN Performance Email", e);
        }

        try {
            EmailHtmlMessage htmlMessage = new EmailHtmlMessage(EMPTY_TO, subject, htmlBody, htmlBody);
            return htmlMessage;
        } catch (MessagingException e) {
            // Unreachable. Only throws exception when it can't parse a 'from' address, which is null in this case.
            throw new RuntimeException(e);
        }
    };
    
    public String getNotificationGroups() {
        return notificationGroups;
    }
    
    public void setNotificationGroups(String notificationGroups) {
        this.notificationGroups = notificationGroups;
    }
    
    public String getAdditionalEmails() {
        return additionalEmails;
    }
    
    public void setAdditionalEmails(String additionalEmails) {
        this.additionalEmails = additionalEmails;
    }
}