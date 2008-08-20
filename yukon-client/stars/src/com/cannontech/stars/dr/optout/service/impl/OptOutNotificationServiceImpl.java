package com.cannontech.stars.dr.optout.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.service.OptOutNotificationService;
import com.cannontech.stars.dr.optout.service.OptOutNotificationUtil;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.web.action.ProgramOptOutAction;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.user.YukonUserContext;

public class OptOutNotificationServiceImpl implements OptOutNotificationService {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String DELIMITER = ",";

    private static final String CODE_SUBJECT = "yukon.dr.consumer.optoutnotification.subject";
    private static final String CODE_MESSAGEBODY = "yukon.dr.consumer.optoutnotification.messageBody";
    
    private final Logger logger = YukonLogManager.getLogger(OptOutNotificationServiceImpl.class);
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private DateFormattingService dateFormattingService;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    
    @Override
    public void sendNotification(final CustomerAccount customerAccount,  
            final LiteStarsEnergyCompany energyCompany, final OptOutRequest request, 
            final YukonUserContext yukonUserContext) throws MessagingException {
        
        MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
        
        final Holder holder = new Holder();
        holder.customerAccount = customerAccount;
        holder.energyCompany = energyCompany;
        holder.request = request;
        holder.messageSourceAccessor = messageSourceAccessor;
        holder.yukonUserContext = yukonUserContext;
        
        String recipientsCsvString = 
            energyCompany.getEnergyCompanySetting( EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS );
        
        if (StringUtils.isBlank(recipientsCsvString)) {
            throw new AddressException("Property \"optout_notification_recipients\" is not set."); 
        }
        
        String[] recipients = recipientsCsvString.split(DELIMITER);
        String fromAddress = energyCompany.getAdminEmailAddress();
        String subject = messageSourceAccessor.getMessage(CODE_SUBJECT);
        String messageBody = getMessageBody(holder);
        
        if (messageBody == null) {
            logger.debug("The message body is empty, no notification message will be sent.");
            return;
        }
        
        logger.debug(new StringBuilder()
                     .append("Recipients: ").append(Arrays.toString(recipients)).append(LINE_SEPARATOR)
                     .append("From: ").append(fromAddress).append(LINE_SEPARATOR)
                     .append("Subject: ").append(subject).append(LINE_SEPARATOR)
                     .append("Message Body: ").append(messageBody).append(LINE_SEPARATOR)
                     .toString());
        
        EmailMessage emailMsg = new EmailMessage(recipients, subject, messageBody);
        emailMsg.setFrom(fromAddress);
        emailMsg.send();
    }
    
    @SuppressWarnings("unchecked")
    private String getMessageBody(Holder holder) {
        final int durationInHours = holder.request.getDurationInHours();
        if (durationInHours <= 0) return null;
        
        final Calendar cal = dateFormattingService.getCalendar(holder.yukonUserContext);
        Date optOutDate = holder.request.getStartDate();
        
        if (optOutDate == null) {
            optOutDate = cal.getTime();
        } else {
            cal.setTime(optOutDate);
        }

        cal.add( Calendar.HOUR_OF_DAY, durationInHours);
        Date reenableDate = cal.getTime();
        
        String formattedOptOutDate = dateFormattingService.formatDate(optOutDate, 
                                                                      DateFormatEnum.DATEHM,
                                                                      holder.yukonUserContext);
        
        String formattedReenableDate = dateFormattingService.formatDate(reenableDate,
                                                                        DateFormatEnum.DATEHM,
                                                                        holder.yukonUserContext);
        
        LiteStarsCustAccountInformation liteAcctInfo = 
            starsCustAccountInformationDao.getById(holder.customerAccount.getAccountId(),
                                                   holder.energyCompany.getEnergyCompanyID());

        List<Integer> inventoryIdList = holder.request.getInventoryIdList();
        List<LiteStarsLMHardware> hardwares;
        
        if (inventoryIdList.size() > 0) {
            hardwares = new ArrayList<LiteStarsLMHardware>();
            for (final Integer inventoryId : inventoryIdList) {
                hardwares.add((LiteStarsLMHardware) starsInventoryBaseDao.getById(inventoryId));
            }    
        }
        else {
            hardwares = ProgramOptOutAction.getAffectedHardwares(liteAcctInfo, holder.energyCompany);
        }
        
        String accountInfo = OptOutNotificationUtil.getAccountInformation(holder.energyCompany, liteAcctInfo);
        String programInfo = OptOutNotificationUtil.getProgramInformation(holder.energyCompany, liteAcctInfo, hardwares);
        String questions = OptOutNotificationUtil.getQuestions(holder.request.getQuestions());
        
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("accountInfo", accountInfo);
        values.put("programInfo", programInfo);
        values.put("optOutDate", formattedOptOutDate);
        values.put("reenableDate", formattedReenableDate);
        values.put("questions", questions);
        
        final SimpleTemplateProcessor templateProcessor = new SimpleTemplateProcessor();
        String template = holder.messageSourceAccessor.getMessage(CODE_MESSAGEBODY);
        
        String messageBody = templateProcessor.process(template, values);
        return messageBody;
    }
    
    private final class Holder {
        CustomerAccount customerAccount;
        LiteStarsEnergyCompany energyCompany;
        OptOutRequest request;
        MessageSourceAccessor messageSourceAccessor;
        YukonUserContext yukonUserContext;
    }
    
    @Autowired
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

    @Autowired
    public void setDateFormattingService(
            DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setStarsCustAccountInformationDao(
            StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }
    
    @Autowired
    public void setStarsInventoryBaseDao(
			StarsInventoryBaseDao starsInventoryBaseDao) {
		this.starsInventoryBaseDao = starsInventoryBaseDao;
	}
    
}
