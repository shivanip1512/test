package com.cannontech.stars.dr.optout.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.service.OptOutNotificationService;
import com.cannontech.stars.dr.optout.service.OptOutNotificationUtil;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.user.YukonUserContext;

public class OptOutNotificationServiceImpl implements OptOutNotificationService {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String DELIMITER = ",";

    private static final String CODE_SUBJECT = "yukon.dr.consumer.optoutnotification.subject";
    private static final String OPT_OUT_MESSAGEBODY = "yukon.dr.consumer.optoutnotification.messageBody";
    private static final String CANCEL_SCHEDULED_OPT_OUT_MESSAGEBODY = "yukon.dr.consumer.cancelscheduledoptoutnotification.messageBody";
    private static final String REENABLE_MESSAGEBODY = "yukon.dr.consumer.reenableoptoutnotification.messageBody";
    
    private final Logger logger = YukonLogManager.getLogger(OptOutNotificationServiceImpl.class);
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private DateFormattingService dateFormattingService;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    private InventoryBaseDao inventoryBaseDao;
    private YukonUserContextService yukonUserContextService;
    
    @Override
    public void sendOptOutNotification(final CustomerAccount customerAccount,  
            final LiteStarsEnergyCompany energyCompany, final OptOutRequest request, 
            final LiteYukonUser user) throws MessagingException {
        
    	YukonUserContext yukonUserContext = this.getDefaultEnergyCompanyUserContext(energyCompany);
    	
        MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
        
        String subject = messageSourceAccessor.getMessage(CODE_SUBJECT);
        String messageBody = messageSourceAccessor.getMessage(OPT_OUT_MESSAGEBODY);
        
        final Holder holder = new Holder();
        holder.customerAccount = customerAccount;
        holder.energyCompany = energyCompany;
        holder.request = request;
        holder.subject = subject;
        holder.messageBody = messageBody;
        holder.yukonUserContext = yukonUserContext;
        
        this.sendNotification(holder);
        
    }
    

	@Override
	public void sendCancelScheduledNotification(
			CustomerAccount customerAccount,
			LiteStarsEnergyCompany energyCompany, OptOutRequest request,
			LiteYukonUser user) throws MessagingException {

		YukonUserContext yukonUserContext = this.getDefaultEnergyCompanyUserContext(energyCompany);
		
		MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
        
        String subject = messageSourceAccessor.getMessage(CODE_SUBJECT);
        String messageBody = messageSourceAccessor.getMessage(CANCEL_SCHEDULED_OPT_OUT_MESSAGEBODY);
        
        final Holder holder = new Holder();
        holder.customerAccount = customerAccount;
        holder.energyCompany = energyCompany;
        holder.request = request;
        holder.subject = subject;
        holder.messageBody = messageBody;
        holder.yukonUserContext = yukonUserContext;
        
        this.sendNotification(holder);
		
	}

	@Override
	public void sendReenableNotification(CustomerAccount customerAccount,
			LiteStarsEnergyCompany energyCompany, OptOutRequest request,
			LiteYukonUser user) throws MessagingException {

		YukonUserContext yukonUserContext = this.getDefaultEnergyCompanyUserContext(energyCompany);
		
		MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
        
        String subject = messageSourceAccessor.getMessage(CODE_SUBJECT);
        String messageBody = messageSourceAccessor.getMessage(REENABLE_MESSAGEBODY);
        
        final Holder holder = new Holder();
        holder.customerAccount = customerAccount;
        holder.energyCompany = energyCompany;
        holder.request = request;
        holder.subject = subject;
        holder.messageBody = messageBody;
        holder.yukonUserContext = yukonUserContext;
        
        this.sendNotification(holder);
		
	}
	
	/**
	 * Helper method to get a default user context for an energy company
	 * @param energyCompany - Energy company to get context for
	 * @return Default context
	 */
	private YukonUserContext getDefaultEnergyCompanyUserContext(
			LiteStarsEnergyCompany energyCompany) {

    	YukonUserContext userContext = 
    		yukonUserContextService.getEnergyCompanyDefaultUserContext(energyCompany.getUser());

    	return userContext;
	}
	
	private void sendNotification(Holder holder) throws MessagingException {
		
		LiteStarsEnergyCompany energyCompany = holder.energyCompany;
		
		String recipientsCsvString = 
            energyCompany.getEnergyCompanySetting( EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS );
        
        if (StringUtils.isBlank(recipientsCsvString)) {
            throw new AddressException("Property \"optout_notification_recipients\" is not set."); 
        }
        
        String[] recipients = recipientsCsvString.split(DELIMITER);
        String fromAddress = energyCompany.getAdminEmailAddress();
        String subject = holder.subject;
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
    
    private String getMessageBody(Holder holder) {
        final long durationInHours = holder.request.getDurationInHours();
        if (durationInHours <= 0) return null;
        
        ReadableInstant optOutStartInstant = holder.request.getStartDate();
        DateTime optOutDateTime = 
            new DateTime(optOutStartInstant, holder.yukonUserContext.getJodaTimeZone());
        
        if (optOutStartInstant == null) {
            optOutStartInstant = new Instant();
        }

        ReadableDuration optOutDuration = Duration.standardHours(durationInHours); 
        DateTime reenableDate = optOutDateTime.plus(optOutDuration);
        
        String formattedOptOutDate = dateFormattingService.format(optOutStartInstant, 
                                                                  DateFormatEnum.DATEHM,
                                                                  holder.yukonUserContext);
        
        String formattedReenableDate = dateFormattingService.format(reenableDate,
                                                                    DateFormatEnum.DATEHM,
                                                                    holder.yukonUserContext);
        
        LiteAccountInfo liteAcctInfo = 
            starsCustAccountInformationDao.getById(holder.customerAccount.getAccountId(),
                                                   holder.energyCompany.getEnergyCompanyId());

        List<Integer> inventoryIdList = holder.request.getInventoryIdList();
        List<LiteLmHardwareBase> hardwares = new ArrayList<LiteLmHardwareBase>();;
        
        for (final Integer inventoryId : inventoryIdList) {
            hardwares.add((LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId));
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
        String template = holder.messageBody;
        
        String messageBody = templateProcessor.process(template, values);
        return messageBody;
    }
    
    private final class Holder {
        CustomerAccount customerAccount;
        LiteStarsEnergyCompany energyCompany;
        OptOutRequest request;
        String subject;
        String messageBody;
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
    public void setInventoryBaseDao(
			InventoryBaseDao inventoryBaseDao) {
		this.inventoryBaseDao = inventoryBaseDao;
	}

    @Autowired
    public void setYukonUserContextService(
			YukonUserContextService yukonUserContextService) {
		this.yukonUserContextService = yukonUserContextService;
	}
    
}
