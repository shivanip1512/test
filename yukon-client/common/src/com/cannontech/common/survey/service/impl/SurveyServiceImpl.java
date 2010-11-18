package com.cannontech.common.survey.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFragmentUiFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.survey.dao.SurveyDao;
import com.cannontech.common.survey.dao.impl.SurveyRowMapper;
import com.cannontech.common.survey.model.Answer;
import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.ResolvedAnswer;
import com.cannontech.common.survey.model.ResolvedQuestion;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.common.survey.service.SurveyService;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SurveyServiceImpl implements SurveyService {
    private FilterService filterService;
    private SurveyDao surveyDao;
    private YukonUserContextMessageSourceResolver messageSourceResolver;

    private final static RowMapperWithBaseQuery<Survey> rowMapper =
        new SurveyRowMapper();

    @Override
    public SearchResult<Survey> findSurveys(final int energyCompanyId,
            int startIndex, int count) {

        UiFilter<Survey> filter = new SqlFragmentUiFilter<Survey>() {
            @Override
            protected void getSqlFragment(SqlBuilder sql) {
                sql.append("energyCompanyId").eq(energyCompanyId);
            }
        };

        SearchResult<Survey> retVal =
            filterService.filter(filter, null, startIndex, count, rowMapper);
        return retVal;
    }
    
    @Override
    public List<ResolvedQuestion> getResolvedQuestionsBySurveyId(int surveyId, YukonUserContext userContext) {
        List<Question> questions = surveyDao.getQuestionsBySurveyId(surveyId);
        Survey survey = surveyDao.getSurveyById(surveyId);
        List<ResolvedQuestion> retVal = Lists.newArrayList();
        for (Question question : questions) {
            ResolvedQuestion resolvedQuestion = new ResolvedQuestion();          
            resolvedQuestion.setQuestion(question);
                        
            final MessageSourceAccessor messageSourceAccessor = 
                messageSourceResolver.getMessageSourceAccessor(userContext);
            try {
                String resolvedString = messageSourceAccessor.getMessage(survey.getBaseKey(question));
                resolvedQuestion.setQuestionText(resolvedString);
                resolvedQuestion.setValid(true);
            } catch (NoSuchMessageException e) {
                resolvedQuestion.setQuestionText(survey.getBaseKey(question));
                resolvedQuestion.setValid(false);
            }
            
            try {
                String resolvedString = messageSourceAccessor.getMessage(survey.getBaseKey(question) + ".pleaseChoose");
                resolvedQuestion.setPleaseChooseText(resolvedString);
                resolvedQuestion.setPleaseChooseValid(true);
            } catch (NoSuchMessageException e) {
                resolvedQuestion.setPleaseChooseText(survey.getBaseKey(question) + ".pleaseChoose");
                resolvedQuestion.setPleaseChooseValid(false);
            }
            
            try {
                String resolvedString = messageSourceAccessor.getMessage(survey.getBaseKey(question) + ".other");
                resolvedQuestion.setOtherText(resolvedString);
                resolvedQuestion.setOtherValid(true);
            } catch (NoSuchMessageException e) {
                resolvedQuestion.setOtherText(survey.getBaseKey(question) + ".other");
                resolvedQuestion.setOtherValid(false);
            }
            
            List<ResolvedAnswer> resolvedAnswers = Lists.newArrayList();
            for (Answer answer : question.getAnswers()) {
                ResolvedAnswer resolvedAnswer = new ResolvedAnswer();

                resolvedAnswer.setAnswer(answer);
                            
                try {
                    String resolvedString = messageSourceAccessor.getMessage(survey.getBaseKey(question) + "." + answer.getAnswerKey());
                    resolvedAnswer.setAnswerText(resolvedString);
                    resolvedAnswer.setValid(true);
                } catch (NoSuchMessageException e) {
                    resolvedAnswer.setAnswerText(survey.getBaseKey(question) + "." + answer.getAnswerKey());
                    resolvedAnswer.setValid(false);
                }
                
                resolvedAnswers.add(resolvedAnswer);
            }
            
            resolvedQuestion.setResolvedAnswers(resolvedAnswers);
            
            retVal.add(resolvedQuestion);
            
        }
        return retVal;
    }
    
    @Override
    public List<MessageSourceResolvable> errorsForResolvedQuestions(List<ResolvedQuestion> questions){
        List<MessageSourceResolvable> retVal = Lists.newArrayList();
        
        for(ResolvedQuestion question : questions) {
            if(!question.isValid())
                retVal.add(new YukonMessageSourceResolvable("yukon.web.error.i18nKeyMissing", question.getQuestionText()));
            if(!question.isOtherValid() && question.getQuestion().isTextAnswerAllowed())
                retVal.add(new YukonMessageSourceResolvable("yukon.web.error.i18nKeyMissing", question.getOtherText()));

            for( ResolvedAnswer answer : question.getResolvedAnswers()) {
                if(!answer.isValid())
                    retVal.add(new YukonMessageSourceResolvable("yukon.web.error.i18nKeyMissing", answer.getAnswerText()));
            }
        }
        return retVal;       
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }
    
    @Autowired
    public void setSurveyDao(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}
