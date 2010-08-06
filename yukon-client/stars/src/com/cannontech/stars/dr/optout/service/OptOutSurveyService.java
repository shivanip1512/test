package com.cannontech.stars.dr.optout.service;

import com.cannontech.common.search.SearchResult;
import com.cannontech.stars.dr.optout.model.OptOutSurvey;

public interface OptOutSurveyService {
    SearchResult<OptOutSurvey> findSurveys(int energyCompanyId, int startIndex,
            int count);
}
