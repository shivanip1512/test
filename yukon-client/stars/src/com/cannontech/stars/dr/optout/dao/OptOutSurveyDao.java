package com.cannontech.stars.dr.optout.dao;

import java.util.List;

import org.joda.time.ReadableInstant;

import com.cannontech.stars.dr.optout.model.OptOutSurvey;
import com.cannontech.stars.dr.optout.model.SurveyResult;
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

    public List<SurveyResult> findSurveyResults(int surveyId,
            int questionId, Iterable<Integer> answerIds,
            boolean includeOtherAnswers, boolean includeUnanswered,
            boolean includeScheduledSurveys,
            ReadableInstant begin, ReadableInstant end,
            String accountNumber, String serialNumber);
}
