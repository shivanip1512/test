package com.cannontech.common.survey.service;

import java.util.List;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.user.YukonUserContext;

public interface SurveyService {
    public SearchResults<Survey> findSurveys(int energyCompanyId, int startIndex, int count);

    public boolean areAllSurveyKeysForContextValid(int surveyId, YukonUserContext userContext);

    public List<MessageSourceResolvable> getKeyErrorsForQuestions(int surveyId,
            YukonUserContext userContext);
}
