package com.cannontech.common.survey.model;

public class Answer {
    private int surveyQuestionAnswerId;
    private int surveyQuestionId;
    private String answerKey;
    private int displayOrder;

    public Answer() {
    }

    public Answer(int surveyQuestionId, String answerKey, int displayOrder) {
        this.surveyQuestionId = surveyQuestionId;
        this.answerKey = answerKey;
        this.displayOrder = displayOrder;
    }

    public int getSurveyQuestionAnswerId() {
        return surveyQuestionAnswerId;
    }

    public void setSurveyQuestionAnswerId(int surveyQuestionAnswerId) {
        this.surveyQuestionAnswerId = surveyQuestionAnswerId;
    }

    public int getSurveyQuestionId() {
        return surveyQuestionId;
    }

    public void setSurveyQuestionId(int surveyQuestionId) {
        this.surveyQuestionId = surveyQuestionId;
    }

    public String getAnswerKey() {
        return answerKey;
    }

    public void setAnswerKey(String answerKey) {
        this.answerKey = answerKey;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
