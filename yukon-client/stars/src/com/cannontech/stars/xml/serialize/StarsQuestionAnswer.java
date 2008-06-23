package com.cannontech.stars.xml.serialize;

public abstract class StarsQuestionAnswer {
    private int _questionID;
    private boolean _has_questionID;
    private QuestionType _questionType;
    private java.lang.String _question;
    private AnswerType _answerType;
    private java.lang.String _answer;

    public StarsQuestionAnswer() {
        
    }

    public void deleteQuestionID() {
        this._has_questionID = false;
    } 

    public java.lang.String getAnswer() {
        return this._answer;
    } 

    public AnswerType getAnswerType() {
        return this._answerType;
    }

    public String getQuestion() {
        return this._question;
    }

    public int getQuestionID() {
        return this._questionID;
    }

    public QuestionType getQuestionType() {
        return this._questionType;
    }

    public boolean hasQuestionID() {
        return this._has_questionID;
    }

    public void setAnswer(String answer) {
        this._answer = answer;
    }

    public void setAnswerType(AnswerType answerType) {
        this._answerType = answerType;
    }

    public void setQuestion(String question) {
        this._question = question;
    }

    public void setQuestionID(int questionID) {
        this._questionID = questionID;
        this._has_questionID = true;
    }

    public void setQuestionType(QuestionType questionType) {
        this._questionType = questionType;
    }

}
