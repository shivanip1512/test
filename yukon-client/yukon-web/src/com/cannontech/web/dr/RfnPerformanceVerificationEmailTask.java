package com.cannontech.web.dr;

import static com.cannontech.system.GlobalSettingType.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
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
import com.cannontech.common.util.WebserverUrlResolver;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.dr.model.MutablePerformanceVerificationEventStats;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationEventStats;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.system.OnOff;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailHtmlMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;

public class RfnPerformanceVerificationEmailTask extends YukonTaskBase {
    private final static Logger log = YukonLogManager.getLogger(RfnPerformanceVerificationEmailTask.class);

    private static final String rowTemplate = "<tr style=\"{rowStyle}\">" +
            "<{cell} style=\"{cellStyle}\">{date}</{cell}>" +
            "<{cell} style=\"{cellStyle}\">{successful}</{cell}>" +
            "<{cell} style=\"{cellStyle}\">{failure}</{cell}>" +
            "<{cell} style=\"{cellStyle}\">{unknown}</{cell}>" +
            "<{cell} style=\"{cellStyle}\">{percentage}</{cell}>" +
            "</tr>";
    private static final String trStyle_Alt ="background-color: #f6f6f6;";
    private static final String tdStyle ="line-height: 18px; padding: 0px 15px; text-align: right;";
    private static final String thStyle ="line-height: 18px; padding: 0px 15px; text-align: left; border-bottom: solid 1px;";
    
    private final static InternetAddress[] EMPTY_TO = new InternetAddress[] { new InternetAddress() };
    
    @Autowired private PerformanceVerificationDao performanceVerificationDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private TemplateProcessorFactory templateProcessorFactory;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private EmailService emailService;
    @Autowired private NotificationGroupDao notificationGroupDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private WebserverUrlResolver webserverUrlResolver;
    
    private final Resource emailTemplate;
    
    private String notificationGroups;
    private String additionalEmails;

    public RfnPerformanceVerificationEmailTask() {
        emailTemplate = new ClassPathResource("com/cannontech/web/dr/rfnPerformanceEmail.tmp");
    }
    
    @Override
    public void start() {
        OnOff preference = globalSettingDao.getEnum(RF_BROADCAST_PERFORMANCE, OnOff.class);
        if (preference.isOn()) {
            EmailHtmlMessage template = generateEmail();
            sendEmails(template);
        }
    }
    
    private void sendEmails(final EmailHtmlMessage htmlMessageTemplate) {
        int[] notifGroupIds = StringUtils.parseIntString(notificationGroups);
        
        final List<LiteNotificationGroup> notifGroups = new ArrayList<>();
        for (int groupId : notifGroupIds) {
            LiteNotificationGroup group = notificationGroupDao.getLiteNotificationGroup(groupId);
            if(group != null) {
                notifGroups.add(group);
            } else {
                log.warn("RFN Performance Verification is configured to email notification group with id "
                         + groupId + ", but that group has been deleted.");
            }
        }

        if (notifGroups.isEmpty()) {
            log.info("Ignoring request because no notification groups have been specified");
            return; // we "handled" it, by not sending anything
        }

        List<String> notifEmailsAddresses = new ArrayList<>();
        for (LiteNotificationGroup notifGroup : notifGroups) {
            if (!notifGroup.isDisabled()) {
                List<String> notifEmails = notificationGroupDao.getContactNoficationEmails(notifGroup);
                notifEmailsAddresses.addAll(notifEmails);
            } else {
                log.info("Not sending emails because notification group is disabled: group=" + notifGroup);
            }
        }
        
        if (!isEmpty(additionalEmails)) {
            List<String> additionalEmailAddresses = StringUtils.parseStringsForList(additionalEmails, ", ");
            if (!additionalEmailAddresses.isEmpty()) {
                notifEmailsAddresses.addAll(additionalEmailAddresses);
            }
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
        theaderData.put("rowStyle", "");
        theaderData.put("cell", "th");
        theaderData.put("cellStyle", thStyle);
        theaderData.put("date", accessor.getMessage("yukon.common.date"));
        theaderData.put("successful", accessor.getMessage("yukon.web.modules.dr.rf.broadcast.eventDetail.status.SUCCESS"));
        theaderData.put("failure", accessor.getMessage("yukon.web.modules.dr.rf.broadcast.eventDetail.status.FAILURE"));
        theaderData.put("unknown",accessor.getMessage("yukon.web.modules.dr.rf.broadcast.eventDetail.status.UNKNOWN"));
        theaderData.put("percentage", accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.percentage"));
        String theader = tp.process(rowTemplate, theaderData);

        Instant now = new Instant();
        Instant yesterday = now.minus(Duration.standardDays(1));
        Instant lastWeek = yesterday.minus(Duration.standardDays(7));
        Range<Instant> reportDates = new Range<>(lastWeek, false, yesterday, true);

        List<PerformanceVerificationEventMessageStats> reports = performanceVerificationDao.getReports(reportDates);

        Map<DateTime, MutablePerformanceVerificationEventStats> eventStatsByDate = new HashMap<>();
        for (PerformanceVerificationEventMessageStats report : reports) {
            DateTime day = report.getTimeMessageSent().toDateTime().withTimeAtStartOfDay();
            MutablePerformanceVerificationEventStats stats = eventStatsByDate.get(day);

            if (stats == null) {
                stats = new MutablePerformanceVerificationEventStats();
                stats.addStats(report.getNumSuccesses(), report.getNumFailures(), report.getNumUnknowns());
                eventStatsByDate.put(day, stats);
            } else  {
                stats.addStats(report.getNumSuccesses(), report.getNumFailures(), report.getNumUnknowns());
            }
        }

        List<DateTime> eventDates = new ArrayList<>(eventStatsByDate.keySet());
        Collections.sort(eventDates, Collections.reverseOrder());

        boolean altStyledRow = false;
        StringBuilder tbody = new StringBuilder();
        for (DateTime date : eventDates) {

            PerformanceVerificationEventStats eventStats = eventStatsByDate.get(date).getImmutable();
            String formattedDate = dateFormatter.format(date.toDate());
            double percentSuccess = eventStats.getPercentSuccess();
            String precentageStr = MessageFormat.format("{0, number,##.#%}", percentSuccess);
            String rowStyle = altStyledRow ? trStyle_Alt : "";

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("rowStyle", rowStyle);
            templateData.put("cell", "td");
            templateData.put("cellStyle", tdStyle);
            templateData.put("date", formattedDate);
            templateData.put("successful", eventStats.getNumSuccesses());
            templateData.put("failure", eventStats.getNumFailures());
            templateData.put("unknown", eventStats.getNumUnknowns());
            templateData.put("percentage", precentageStr);
            String rowText = tp.process(rowTemplate, templateData);
            tbody.append(rowText).append("\n");
            altStyledRow = !altStyledRow;
        }

        String formattedDate = dateTimeFormatter.print(now);

        String yesterdayParam = instantToDateUrlParam(yesterday);
        String lastWeekParam = instantToDateUrlParam(lastWeek);
        String linkUrl = webserverUrlResolver.getUrl("/dr/rf/details?from=" + lastWeekParam + "&to=" + yesterdayParam);

        String subject = accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.email.subject");
        String header = accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.email.header", linkUrl);
        String footer = accessor.getMessage("yukon.web.modules.dr.home.rfPerformance.email.footer", formattedDate);

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
            return new EmailHtmlMessage(EMPTY_TO, subject, htmlBody, htmlBody);
        } catch (MessagingException e) {
            // Unreachable. Only throws exception when it can't parse a 'from' address, which is null in this case.
            throw new RuntimeException(e);
        }
    };

    private String instantToDateUrlParam(Instant instant) {
        DateFormat dateFormatter = dateFormattingService.getDateFormatter(DateFormatEnum.DATE, YukonUserContext.system);
        String unescaped = dateFormatter.format(instant.toDate());
        try {
            return URLEncoder.encode(unescaped, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Unreachable. UTF-8 must be supported
            throw new RuntimeException(e);
        }
    }
    
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