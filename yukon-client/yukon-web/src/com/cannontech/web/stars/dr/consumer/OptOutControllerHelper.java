package com.cannontech.web.stars.dr.consumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.context.NoSuchMessageException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;
import com.cannontech.user.YukonUserContext;

public class OptOutControllerHelper {
    private static final String KEY_QUESTION = "question";
    private static final String KEY_ANSWER = "answer";
    
    private OptOutControllerHelper() {
        throw new UnsupportedOperationException("Instances of this class is not supported");
    }
    
    @SuppressWarnings("unchecked")
    public static List<ScheduledOptOutQuestion> toOptOutQuestionList(String jsonQuestions) {
        if (jsonQuestions == null) return Collections.emptyList();
        
        final List<ScheduledOptOutQuestion> questionList =
            new ArrayList<ScheduledOptOutQuestion>();
        
        final JSONObject jsonObject = new JSONObject(jsonQuestions);
        
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject value = jsonObject.getJSONObject(key);
            
            int index = Integer.parseInt(key);
            String question = value.getString(KEY_QUESTION);
            String answer = value.getString(KEY_ANSWER);
            
            ScheduledOptOutQuestion optOutQuestion =
                new ScheduledOptOutQuestion(index, question, answer);
            questionList.add(optOutQuestion);
        }
        
        return questionList;
    }
    
    public static List<Integer> toInventoryIdList(String jsonInventoryIds) {
        JSONArray jsonArray = new JSONArray(jsonInventoryIds);
        final List<Integer> resultList = new ArrayList<Integer>(jsonArray.length());
        
        for (int x = 0; x < jsonArray.length(); x++) {
            resultList.add(jsonArray.getInt(x));
        }

        return resultList;
    }
    
    public static List<String> getConfirmQuestions(YukonUserContextMessageSourceResolver messageSourceResolver, 
            YukonUserContext yukonUserContext) {
        
        final String keyPrefix = "yukon.dr.consumer.optoutconfirm.question.";
        final MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
        
        final List<String> questionList = new ArrayList<String>();

        int index = 1;
        boolean done = false;

        while (!done) {
            try {
                String code = keyPrefix + index++;
                String question = StringEscapeUtils.escapeHtml(messageSourceAccessor.getMessage(code));
                questionList.add(question);
            } catch (NoSuchMessageException e) {
                done = true;
            }
        }
        
        return questionList;
    }
    
    
    
}
