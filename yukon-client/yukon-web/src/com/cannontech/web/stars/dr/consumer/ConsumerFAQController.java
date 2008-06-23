package com.cannontech.web.stars.dr.consumer;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/consumer/faq")
public class ConsumerFAQController extends AbstractConsumerController {
    private static final String keyPrefix = "yukon.dr.consumer.faq.question.";
    private static final String question = ".question";
    private static final String answer = ".answer";
    private static final String subject = ".subject";

    @CheckRole(ResidentialCustomerRole.ROLEID)
    @CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_FAQ)
    @RequestMapping(method = RequestMethod.GET)
    public String view(YukonUserContext yukonUserContext, ModelMap map) {

        Map<String, Map<String, String>> questions = getQuestionMap(yukonUserContext);
        map.addAttribute("questions", questions);
        
        return "consumer/faq.jsp";
    }
    
    private Map<String, Map<String, String>> getQuestionMap(YukonUserContext yukonUserContext) {
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
    
}
