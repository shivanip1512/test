package com.cannontech.stars.dr.optout.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.i18n.service.YukonUserContextService;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.service.OptOutNotificationService;
import com.cannontech.stars.dr.optout.service.OptOutNotificationUtil;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;

public class OptOutNotificationServiceImpl implements OptOutNotificationService {
    private final static Logger logger = YukonLogManager.getLogger(OptOutNotificationServiceImpl.class);

    private final static String subjectI18nKey = "yukon.dr.consumer.optoutnotification.subject";
    private final static String optOutMessageBodyKey = "yukon.dr.consumer.optoutnotification.messageBody";
    private final static String cancelScheduledOptOutMessageBodyKey =
            "yukon.dr.consumer.cancelscheduledoptoutnotification.messageBody";
    private final static String reenableMessageBodyKey = "yukon.dr.consumer.reenableoptoutnotification.messageBody";

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private EmailService emailService;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private StarsCustAccountInformationDao starsCustAccountInformationDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private YukonUserContextService userContextService;

    @Override
    public void sendOptOutNotification(CustomerAccount customerAccount, EnergyCompany energyCompany,
            OptOutRequest request, LiteYukonUser user) throws MessagingException {
        YukonUserContext yukonUserContext = getDefaultEnergyCompanyUserContext(energyCompany);

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(yukonUserContext);

        String subject = messageSourceAccessor.getMessage(subjectI18nKey);
        String messageBody = messageSourceAccessor.getMessage(optOutMessageBodyKey);

        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.customerAccount = customerAccount;
        notificationInfo.energyCompany = energyCompany;
        notificationInfo.optOutRequest = request;
        notificationInfo.subject = subject;
        notificationInfo.messageBody = messageBody;
        notificationInfo.userContext = yukonUserContext;

        sendNotification(notificationInfo);
    }

    @Override
    public void sendCancelScheduledNotification(CustomerAccount customerAccount, EnergyCompany energyCompany,
            OptOutRequest request, LiteYukonUser user) throws MessagingException {
        YukonUserContext yukonUserContext = getDefaultEnergyCompanyUserContext(energyCompany);

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(yukonUserContext);

        String subject = messageSourceAccessor.getMessage(subjectI18nKey);
        String messageBody = messageSourceAccessor.getMessage(cancelScheduledOptOutMessageBodyKey);

        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.customerAccount = customerAccount;
        notificationInfo.energyCompany = energyCompany;
        notificationInfo.optOutRequest = request;
        notificationInfo.subject = subject;
        notificationInfo.messageBody = messageBody;
        notificationInfo.userContext = yukonUserContext;

        sendNotification(notificationInfo);
    }

    @Override
    public void sendReenableNotification(CustomerAccount customerAccount, EnergyCompany energyCompany,
            OptOutRequest request, LiteYukonUser user) throws MessagingException {
        YukonUserContext yukonUserContext = getDefaultEnergyCompanyUserContext(energyCompany);

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(yukonUserContext);

        String subject = messageSourceAccessor.getMessage(subjectI18nKey);
        String messageBody = messageSourceAccessor.getMessage(reenableMessageBodyKey);

        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.customerAccount = customerAccount;
        notificationInfo.energyCompany = energyCompany;
        notificationInfo.optOutRequest = request;
        notificationInfo.subject = subject;
        notificationInfo.messageBody = messageBody;
        notificationInfo.userContext = yukonUserContext;

        sendNotification(notificationInfo);
    }

    /**
     * Helper method to get a default user context for an energy company
     * @param energyCompany - Energy company to get context for
     * @return Default context
     */
    private YukonUserContext getDefaultEnergyCompanyUserContext(YukonEnergyCompany energyCompany) {
        YukonUserContext userContext = userContextService.getEnergyCompanyDefaultUserContext(energyCompany.getEnergyCompanyUser());

        return userContext;
    }

    private void sendNotification(NotificationInfo notificationInfo) throws MessagingException {
        int ecId = notificationInfo.energyCompany.getEnergyCompanyId();

        String recipientsCsvString =
            ecSettingDao.getString(EnergyCompanySettingType.OPTOUT_NOTIFICATION_RECIPIENTS, ecId);

        if (StringUtils.isBlank(recipientsCsvString)) {
            throw new AddressException("Property \"optout_notification_recipients\" is not set.");
        }

        String fromAddress = ecSettingDao.getString(EnergyCompanySettingType.ADMIN_EMAIL_ADDRESS, ecId);
        if (fromAddress == null || fromAddress.trim().length() == 0) {
            fromAddress = StarsUtils.ADMIN_EMAIL_ADDRESS;
        }
        String subject = notificationInfo.subject;
        String messageBody = getMessageBody(notificationInfo);

        if (messageBody == null) {
            logger.debug("The message body is empty, no notification message will be sent.");
            return;
        }

        EmailMessage message = new EmailMessage(new InternetAddress(fromAddress),
            InternetAddress.parse(recipientsCsvString), subject, messageBody);
        emailService.sendMessage(message);
    }

    private String getMessageBody(NotificationInfo notificationInfo) {
        long durationInHours = notificationInfo.optOutRequest.getDurationInHours();
        if (durationInHours <= 0) {
            return null;
        }

        ReadableInstant optOutStartInstant = notificationInfo.optOutRequest.getStartDate();
        DateTime optOutDateTime = new DateTime(optOutStartInstant, notificationInfo.userContext.getJodaTimeZone());

        if (optOutStartInstant == null) {
            optOutStartInstant = new Instant();
        }

        ReadableDuration optOutDuration = Duration.standardHours(durationInHours);
        DateTime reenableDate = optOutDateTime.plus(optOutDuration);

        String formattedOptOutDate =
            dateFormattingService.format(optOutStartInstant, DateFormatEnum.DATEHM, notificationInfo.userContext);

        String formattedReenableDate =
            dateFormattingService.format(reenableDate, DateFormatEnum.DATEHM, notificationInfo.userContext);

        LiteAccountInfo liteAcctInfo =
            starsCustAccountInformationDao.getByAccountId(notificationInfo.customerAccount.getAccountId());

        List<Integer> inventoryIdList = notificationInfo.optOutRequest.getInventoryIdList();
        List<LiteLmHardwareBase> hardwares = new ArrayList<LiteLmHardwareBase>();

        for (Integer inventoryId : inventoryIdList) {
            hardwares.add((LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId));
        }

        String accountInfo = OptOutNotificationUtil.getAccountInformation(liteAcctInfo);
        String programInfo = OptOutNotificationUtil.getProgramInformation(liteAcctInfo, hardwares);
        String questions = OptOutNotificationUtil.getQuestions(notificationInfo.optOutRequest.getQuestions());

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("accountInfo", accountInfo);
        values.put("programInfo", programInfo);
        values.put("optOutDate", formattedOptOutDate);
        values.put("reenableDate", formattedReenableDate);
        values.put("questions", questions);

        SimpleTemplateProcessor templateProcessor = new SimpleTemplateProcessor();
        String template = notificationInfo.messageBody;

        String messageBody = templateProcessor.process(template, values);
        return messageBody;
    }

    private static class NotificationInfo {
        CustomerAccount customerAccount;
        YukonEnergyCompany energyCompany;
        OptOutRequest optOutRequest;
        String subject;
        String messageBody;
        YukonUserContext userContext;
    }
}
