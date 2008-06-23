package com.cannontech.stars.dr.optout.service.impl;

import java.util.Calendar;
import java.util.Date;

import javax.mail.MessagingException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.service.OptOutNotificationService;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.util.OptOutConstraintViolatedException;
import com.cannontech.stars.util.OptOutRuleFormatException;
import com.cannontech.stars.web.action.ProgramOptOutAction;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramOptOut;
import com.cannontech.stars.xml.serialize.StarsProgramOptOutResponse;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.user.YukonUserContext;

public class OptOutServiceImpl implements OptOutService {
    private static final String CODE_INVALID_STARTDATE = "yukon.dr.consumer.optoutresult.invalidStartDate";
    private static final String CODE_CHECK_RULE = "yukon.dr.consumer.optoutresult.checkRule";
    private static final String CODE_TIMES_CONSTRAINT_VIOLATED = "yukon.dr.consumer.optoutresult.timesConstraintViolated";
    private static final String CODE_HOURS_CONTSTRAINT_VIOLATED = "yukon.dr.consumer.optoutresult.hoursConstraintViolated";
    private static final String CODE_DUEDATE_CONTSTRAINT_VIOLATED = "yukon.dr.consumer.optoutresult.dueDateConstraintViolated";
    private static final String CODE_FAILURE = "yukon.dr.consumer.optoutresult.failure";
    private static final String CODE_SUCCESS = "yukon.dr.consumer.optoutresult.success";
    
    private final MessageSourceResolvable invalidStartDateResolvable = new YukonMessageSourceResolvable(CODE_INVALID_STARTDATE);
    private final MessageSourceResolvable failureResolvable = new YukonMessageSourceResolvable(CODE_FAILURE);
    private final MessageSourceResolvable successResolvable = new YukonMessageSourceResolvable(CODE_SUCCESS);
    
    private final Logger logger = YukonLogManager.getLogger(OptOutServiceImpl.class);
    private final OptOutAction optOutAction = new OptOutAction();
    private DateFormattingService dateFormattingService;
    private ECMappingDao ecMappingDao;
    private OptOutNotificationService optOutNotificationService;
    
    @Override
    public MessageSourceResolvable processOptOutRequest(CustomerAccount customerAccount,
            OptOutRequest request, YukonUserContext yukonUserContext) {
        
        final Calendar now = dateFormattingService.getCalendar(yukonUserContext);
        final Date today = getToday(now);
        final int hoursRemainingInDay = getHoursRemainingInDay(now);

        boolean isValidStartDate = isValidStartDate(request.getStartDate(), today);
        if (!isValidStartDate) {
            return invalidStartDateResolvable;
        }
        
        boolean isSameDay = DateUtils.isSameDay(request.getStartDate(), today);
        if (isSameDay) {
            request.setDurationInHours(hoursRemainingInDay);
            request.setStartDate(null); // Same day OptOut's have null startDates.
        }
        
        MessageSourceResolvable resolvable = optOutAction.doAction(customerAccount, 
                                                                   request,
                                                                   yukonUserContext);
        
        return resolvable;
    }
    
    private Date getToday(Calendar cal) {
        Calendar tempCal = (Calendar) cal.clone();
        tempCal.set(Calendar.HOUR_OF_DAY, 0);
        tempCal.set(Calendar.MINUTE, 0);
        tempCal.set(Calendar.SECOND, 0);
        tempCal.set(Calendar.MILLISECOND, 0);
        
        Date today = tempCal.getTime();
        return today;
    }
    
    private boolean isValidStartDate(Date startDate, Date todayDate) {
        if (startDate == null) return false;
        
        long startTime = startDate.getTime();
        long todayTime = todayDate.getTime();
        
        boolean result = startTime >= todayTime;
        return result;
    }
    
    private int getHoursRemainingInDay(Calendar cal) {
        cal = DateUtils.round(cal, Calendar.HOUR_OF_DAY);
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        int diff = 24 - hourOfDay;
        return diff;
    }
    
    @Autowired
    public void setDateFormattingService(
            DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setOptOutNotificationService(
            OptOutNotificationService optOutNotificationService) {
        this.optOutNotificationService = optOutNotificationService;
    }
    
    @Deprecated
    private final class OptOutAction {
        private final ProgramOptOutAction action = new ProgramOptOutAction();
        
        @Deprecated
        public MessageSourceResolvable doAction(CustomerAccount customerAccount, 
                OptOutRequest request, YukonUserContext yukonUserContext) {
            
            final StarsProgramOptOut optOut = new StarsProgramOptOut();
            optOut.addAllInventoryIds(request.getInventoryIdList());
            optOut.setStartDateTime(request.getStartDate());
            optOut.setPeriod(request.getDurationInHours());
            
            LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(customerAccount);
            
            LiteStarsCustAccountInformation liteAcctInfo = 
                energyCompany.getCustAccountInformation(customerAccount.getAccountId(), true);
            
            StarsCustAccountInformation starsAcctInfo = 
                energyCompany.getStarsCustAccountInformation(liteAcctInfo);
            
            try {
                SOAPMessage respMsg = action.process(optOut, 
                                                     liteAcctInfo,
                                                     yukonUserContext.getYukonUser(),
                                                     energyCompany,
                                                     null);
                
                StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation(respMsg);
                StarsFailure failure = operation.getStarsFailure();
                if (failure != null) {
                    Exception e = failure.getException();
                    if (e != null) throw e;
                    return failureResolvable;
                }
                
                StarsProgramOptOutResponse resp = operation.getStarsProgramOptOutResponse();
                if (resp == null) return failureResolvable;

                ProgramOptOutAction.parseResponse(resp, starsAcctInfo, energyCompany);
                
                try {
                    optOutNotificationService.sendNotification(customerAccount,
                                                               energyCompany,
                                                               request,
                                                               yukonUserContext);
                } catch (MessagingException e) {
                    logger.error(e);
                }
                
                return successResolvable;
                
            } catch (OptOutRuleFormatException e) {
                
                String rule = e.getRule();
                return new YukonMessageSourceResolvable(CODE_CHECK_RULE, rule);
                
            } catch (OptOutConstraintViolatedException e2) {
                
                switch (e2.getType()) {
                    case TIMES_ALLOWED : {
                        return new YukonMessageSourceResolvable(CODE_TIMES_CONSTRAINT_VIOLATED,
                                                e2.getAllowed(),
                                                e2.getActual(),
                                                e2.getPeriod());
                    }
                    case HOURS_ALLOWED : {
                        return new YukonMessageSourceResolvable(CODE_HOURS_CONTSTRAINT_VIOLATED,
                                                e2.getAllowed(),
                                                e2.getActual(),
                                                e2.getPeriod());
                    }
                    case DUE_TIME : {
                        return new YukonMessageSourceResolvable(CODE_DUEDATE_CONTSTRAINT_VIOLATED,
                                                e2.getDueDate());
                    }
                    default : return failureResolvable;
                }
                
            } catch (Exception e3) {
                return failureResolvable;
            }
            
        }
    }
    
}
