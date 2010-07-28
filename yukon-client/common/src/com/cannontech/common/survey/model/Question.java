package com.cannontech.common.survey.model;

import java.util.List;

import com.google.common.collect.Lists;

public class Question {
    private int surveyQuestionId;
    private int surveyId;
    private String questionKey;
    private boolean answerRequired;

    private QuestionType questionType;
    private List<Answer> answers = Lists.newArrayList();
    private boolean textAnswerAllowed;

    private int displayOrder;

    public int getSurveyQuestionId() {
        return surveyQuestionId;
    }

    public void setSurveyQuestionId(int surveyQuestionId) {
        this.surveyQuestionId = surveyQuestionId;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public String getQuestionKey() {
        return questionKey;
    }

    public void setQuestionKey(String questionKey) {
        this.questionKey = questionKey;
    }

    public boolean isAnswerRequired() {
        return answerRequired;
    }

    public void setAnswerRequired(boolean answerRequired) {
        this.answerRequired = answerRequired;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public boolean isTextAnswerAllowed() {
        return textAnswerAllowed;
    }

    public void setTextAnswerAllowed(boolean textAnswerAllowed) {
        this.textAnswerAllowed = textAnswerAllowed;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
