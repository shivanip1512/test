package com.cannontech.common.survey.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.Result;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.core.dao.DuplicateException;

public interface SurveyDao {
    public Survey getSurveyById(int surveyId);

    public Question getQuestionById(int surveyQuestionId);

    /**
     * Get a list of questions in their presentation order for a given survey.
     */
    public List<Question> getQuestionsBySurveyId(int surveyId);

    /**
     * Get a map of questions by question id for a given survey.
     */
    public Map<Integer, Question> getQuestionMapBySurveyId(int surveyId);

    /**
     * Get the next survey key available. If two users add a survey at the same
     * time, it can return the same key twice, but uniqueness checking in the
     * save method should reject the second one saved.
     */
    public String getNextSurveyKey();

    /**
     * Works like getNextSurveyKey.
     */
    public String getNextQuestionKey(int surveyId);

    /**
     * Saves a given survey. The survey will be saved as a new survey if its
     * surveyId is 0. In this case, the surveyId property will be updated with
     * he new id.
     * @param survey The survey to save.
     * @throws DuplicateException if the surveyKey is in use by another survey
     */
    public void saveSurvey(Survey survey);

    /**
     * Saves a given question. The question will be saved as a new question if
     * its questionId is 0. In this case, the questionId property will be
     * updated with he new id.
     * @param question The question to save.
     * @throws DuplicateException if the questionKey is in use by another
     *             question in the same survey
     */
    public void saveQuestion(Question question);

    public void moveQuestionUp(Question question);
    public void moveQuestionDown(Question question);

    public boolean isInUse(int surveyId);
    public void deleteSurvey(int surveyId);

    public void deleteQuestion(int surveyQuestionId);

    public void saveResult(Result result);
}
