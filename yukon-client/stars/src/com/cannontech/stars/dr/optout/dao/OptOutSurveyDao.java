package com.cannontech.stars.dr.optout.dao;

import com.cannontech.stars.dr.optout.model.OptOutSurvey;
import com.google.common.collect.Multimap;


public interface OptOutSurveyDao {
    public OptOutSurvey getOptOutSurveyById(int optOutSurveyId);

    public Multimap<OptOutSurvey, Integer> getProgramsForOptOutSurveys(
            Iterable<OptOutSurvey> optOutSurveys);

    public Multimap<Integer, Integer> getCurrentSurveysByProgramId(
            Iterable<Integer> programIds);

    public void saveOptOutSurvey(OptOutSurvey optOutSurvey);

    public void deleteOptOutSurvey(int optOutSurveyId);

    public void saveResult(int surveyResultId, int optOutEventLogId);
}
