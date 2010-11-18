package com.cannontech.common.survey.model;

import java.util.List;

public class ResolvedQuestion {
    private Question question;
    private String   questionText;
    private String   otherText;
    private String   pleaseChooseText;
    private boolean  valid;
    private boolean  otherValid;
    private boolean  pleaseChooseValid;
    private List<ResolvedAnswer> resolvedAnswers;
    
    public List<ResolvedAnswer> getResolvedAnswers(){
        return resolvedAnswers;
    }
    
    public void setResolvedAnswers(List<ResolvedAnswer> resolvedAnswers) {
        this.resolvedAnswers = resolvedAnswers;
    }
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getOtherText() {
        return otherText;
    }

    public void setOtherText(String otherText) {
        this.otherText = otherText;
    }

    public String getPleaseChooseText() {
        return pleaseChooseText;
    }

    public void setPleaseChooseText(String pleaseChooseText) {
        this.pleaseChooseText = pleaseChooseText;
    }

    public boolean isOtherValid() {
        return otherValid;
    }

    public void setOtherValid(boolean otherValid) {
        this.otherValid = otherValid;
    }

    public boolean isPleaseChooseValid() {
        return pleaseChooseValid;
    }

    public void setPleaseChooseValid(boolean pleaseChooseValid) {
        this.pleaseChooseValid = pleaseChooseValid;
    }
}
