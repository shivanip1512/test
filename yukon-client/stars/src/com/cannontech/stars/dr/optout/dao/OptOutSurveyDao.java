package com.cannontech.stars.dr.optout.dao;

import com.cannontech.stars.dr.optout.model.OptOutSurvey;
import com.google.common.collect.Multimap;


public interface OptOutSurveyDao {
    public OptOutSurvey getOptOutSurveyById(int optOutSurveyId);

    public Multimap<OptOutSurvey, Integer> getLoginGroupsForOptOutSurveys(
            Iterable<OptOutSurvey> optOutSurveys);

    public void saveOptOutSurvey(OptOutSurvey optOutSurvey);

    public void deleteOptOutSurvey(int optOutSurveyId);
}
