package com.cannontech.common.survey.service.impl;

import java.util.List;

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
import com.cannontech.common.survey.model.QuestionType;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.common.survey.service.SurveyService;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

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
    public boolean areAllSurveyKeysForContextValid(int surveyId, YukonUserContext userContext) {
        List<Question> questions = surveyDao.getQuestionsBySurveyId(surveyId);
        Survey survey = surveyDao.getSurveyById(surveyId);
        final MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(userContext);
        
        for (Question question : questions) {
            
            String baseKey = survey.getBaseKey(question);
            String otherKey = baseKey + ".other";
            
            if (!isI18nKeyResolvable(baseKey, messageSourceAccessor))
                return false;
            
            if (question.getQuestionType() == QuestionType.DROP_DOWN
                    && question.isTextAnswerAllowed()
                    && !isI18nKeyResolvable(otherKey, messageSourceAccessor)) {
                return false;
            }
            
            for (Answer answer : question.getAnswers()) {
                String answerKey = baseKey + "." + answer.getAnswerKey();
                if (!isI18nKeyResolvable(answerKey, messageSourceAccessor))
                    return false;
            }
        }
        
        return true;
    }

    private boolean isI18nKeyResolvable(String baseKey, final MessageSourceAccessor messageSourceAccessor) {
        try {
            messageSourceAccessor.getMessage(baseKey);
        } catch (NoSuchMessageException e) {
            return false;
        }
        return true;
    }
    
    @Override
    public List<MessageSourceResolvable> getKeyErrorsForQuestions(int surveyId, YukonUserContext userContext){
        List<MessageSourceResolvable> retVal = Lists.newArrayList();
        List<Question> questions = surveyDao.getQuestionsBySurveyId(surveyId);
        Survey survey = surveyDao.getSurveyById(surveyId);
        final MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(userContext);
        
        for (Question question : questions) {
            String baseKey = survey.getBaseKey(question);

            if (!isI18nKeyResolvable(baseKey, messageSourceAccessor))
                retVal.add(new YukonMessageSourceResolvable("yukon.web.error.i18nKeyMissing", baseKey));

            if (question.getQuestionType() == QuestionType.DROP_DOWN) {
                if (question.isTextAnswerAllowed()) {
                    String otherKey = baseKey + ".other";
                    if (!isI18nKeyResolvable(otherKey, messageSourceAccessor))
                        retVal.add(new YukonMessageSourceResolvable("yukon.web.error.i18nKeyMissing", otherKey));
                }

                for (Answer answer : question.getAnswers()) {
                    String answerKey = baseKey + "." + answer.getAnswerKey();
                    if (!isI18nKeyResolvable(answerKey, messageSourceAccessor))
                        retVal.add(new YukonMessageSourceResolvable("yukon.web.error.i18nKeyMissing", answerKey));
                }
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
