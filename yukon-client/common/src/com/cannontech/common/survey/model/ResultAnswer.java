package com.cannontech.common.survey.model;

public class ResultAnswer {
    private int surveyResultAnswerId;
    private int surveyResultId;
    private int surveyQuestionId;
    private Integer surveyQuestionAnswerId;
    private String textAnswer;

    public ResultAnswer(int surveyQuestionId, Integer surveyQuestionAnswerId,
            String textAnswer) {
        surveyResultAnswerId = 0;
        surveyResultId = 0;
        this.surveyQuestionId = surveyQuestionId;
        this.surveyQuestionAnswerId = surveyQuestionAnswerId;
        this.textAnswer = textAnswer;
    }

    public int getSurveyResultAnswerId() {
        return surveyResultAnswerId;
    }

    public void setSurveyResultAnswerId(int surveyResultAnswerId) {
        this.surveyResultAnswerId = surveyResultAnswerId;
    }

    public int getSurveyResultId() {
        return surveyResultId;
    }

    public void setSurveyResultId(int surveyResultId) {
        this.surveyResultId = surveyResultId;
    }

    public int getSurveyQuestionId() {
        return surveyQuestionId;
    }

    public void setSurveyQuestionId(int surveyQuestionId) {
        this.surveyQuestionId = surveyQuestionId;
    }

    public Integer getSurveyQuestionAnswerId() {
        return surveyQuestionAnswerId;
    }

    public void setSurveyQuestionAnswerId(Integer surveyQuestionAnswerId) {
        this.surveyQuestionAnswerId = surveyQuestionAnswerId;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }
}
