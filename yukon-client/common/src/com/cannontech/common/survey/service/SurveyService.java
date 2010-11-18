package com.cannontech.common.survey.service;

import java.util.List;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.search.SearchResult;
import com.cannontech.common.survey.model.ResolvedQuestion;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.user.YukonUserContext;

public interface SurveyService {
    SearchResult<Survey> findSurveys(int energyCompanyId, int startIndex,
                                     int count);

    List<ResolvedQuestion> getResolvedQuestionsBySurveyId(int surveyId,
                                                          YukonUserContext userContext);

    List<MessageSourceResolvable> errorsForResolvedQuestions(List<ResolvedQuestion> questions);
}
