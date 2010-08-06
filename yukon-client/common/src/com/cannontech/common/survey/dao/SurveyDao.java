package com.cannontech.common.survey.dao;

import java.util.List;

import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.core.dao.DuplicateException;

public interface SurveyDao {
    Survey getSurveyById(int surveyId);

    Question getQuestionById(int surveyQuestionId);
    List<Question> getQuestionsBySurveyId(int surveyId);

    /**
     * Get the next survey key available. If two users add a survey at the same
     * time, it can return the same key twice, but uniqueness checking in the
     * save method should reject the second one saved.
     */
    String getNextSurveyKey();

    /**
     * Works like getNextSurveyKey.
     */
    String getNextQuestionKey(int surveyId);

    /**
     * Saves a given survey. The survey will be saved as a new survey if its
     * surveyId is 0. In this case, the surveyId property will be updated with
     * he new id.
     * @param survey The survey to save.
     * @throws DuplicateException if the surveyKey is in use by another survey
     */
    void saveSurvey(Survey survey);

    /**
     * Saves a given question. The question will be saved as a new question if
     * its questionId is 0. In this case, the questionId property will be
     * updated with he new id.
     * @param question The question to save.
     * @throws DuplicateException if the questionKey is in use by another
     *             question in the same survey
     */
    void saveQuestion(Question question);

    void moveQuestionUp(Question question);
    void moveQuestionDown(Question question);

    boolean isInUse(int surveyId);
    void deleteSurvey(int surveyId);

    void deleteQuestion(int surveyQuestionId);
}
