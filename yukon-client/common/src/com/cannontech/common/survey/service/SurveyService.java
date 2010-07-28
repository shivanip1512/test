package com.cannontech.common.survey.service;

import com.cannontech.common.search.SearchResult;
import com.cannontech.common.survey.model.Survey;

public interface SurveyService {
    SearchResult<Survey> findSurveys(int energyCompanyId, int startIndex,
                                     int count);

}
