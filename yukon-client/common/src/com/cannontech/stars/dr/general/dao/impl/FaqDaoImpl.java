package com.cannontech.stars.dr.general.dao.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.general.dao.FaqDao;
import com.cannontech.user.YukonUserContext;


public class FaqDaoImpl implements FaqDao {

    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private static final String keyPrefix = "yukon.dr.consumer.faq.question.";
    private static final String question = ".question";
    private static final String answer = ".answer";
    private static final String subject = ".subject";

    public Map<String, Map<String, String>> getQuestionMap(YukonUserContext yukonUserContext) {
        MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(yukonUserContext);

        final Map<String, Map<String, String>> resultMap = new LinkedHashMap<String, Map<String,String>>();

        int index = 1;
        boolean done = false;

        while (!done) {
            try {
                int currentIndex = index++;

                String subjectCode = keyPrefix + currentIndex + subject;
                String subjectValue = messageSourceAccessor.getMessage(subjectCode);

                String questionCode = keyPrefix + currentIndex + question;
                String questionValue = messageSourceAccessor.getMessage(questionCode);

                String answerCode = keyPrefix + currentIndex + answer;
                String answerValue = messageSourceAccessor.getMessage(answerCode);

                Map<String, String> subjectMap = resultMap.get(subjectValue);
                if (subjectMap == null) {
                    subjectMap = new LinkedHashMap<String, String>();
                    resultMap.put(subjectValue, subjectMap);
                }

                subjectMap.put(questionValue, answerValue);

            } catch (NoSuchMessageException e) {
                done = true;
            }
        }

        return resultMap;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}