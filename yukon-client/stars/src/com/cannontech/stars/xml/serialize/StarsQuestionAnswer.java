/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public abstract class StarsQuestionAnswer implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _questionID;

    /**
     * keeps track of state for field: _questionID
    **/
    private boolean _has_questionID;

    private QuestionType _questionType;

    private java.lang.String _question;

    private AnswerType _answerType;

    private java.lang.String _answer;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsQuestionAnswer() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsQuestionAnswer()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteQuestionID()
    {
        this._has_questionID= false;
    } //-- void deleteQuestionID() 

    /**
     * Returns the value of field 'answer'.
     * 
     * @return the value of field 'answer'.
    **/
    public java.lang.String getAnswer()
    {
        return this._answer;
    } //-- java.lang.String getAnswer() 

    /**
     * Returns the value of field 'answerType'.
     * 
     * @return the value of field 'answerType'.
    **/
    public AnswerType getAnswerType()
    {
        return this._answerType;
    } //-- AnswerType getAnswerType() 

    /**
     * Returns the value of field 'question'.
     * 
     * @return the value of field 'question'.
    **/
    public java.lang.String getQuestion()
    {
        return this._question;
    } //-- java.lang.String getQuestion() 

    /**
     * Returns the value of field 'questionID'.
     * 
     * @return the value of field 'questionID'.
    **/
    public int getQuestionID()
    {
        return this._questionID;
    } //-- int getQuestionID() 

    /**
     * Returns the value of field 'questionType'.
     * 
     * @return the value of field 'questionType'.
    **/
    public QuestionType getQuestionType()
    {
        return this._questionType;
    } //-- QuestionType getQuestionType() 

    /**
    **/
    public boolean hasQuestionID()
    {
        return this._has_questionID;
    } //-- boolean hasQuestionID() 

    /**
    **/
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * 
     * 
     * @param out
    **/
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * Sets the value of field 'answer'.
     * 
     * @param answer the value of field 'answer'.
    **/
    public void setAnswer(java.lang.String answer)
    {
        this._answer = answer;
    } //-- void setAnswer(java.lang.String) 

    /**
     * Sets the value of field 'answerType'.
     * 
     * @param answerType the value of field 'answerType'.
    **/
    public void setAnswerType(AnswerType answerType)
    {
        this._answerType = answerType;
    } //-- void setAnswerType(AnswerType) 

    /**
     * Sets the value of field 'question'.
     * 
     * @param question the value of field 'question'.
    **/
    public void setQuestion(java.lang.String question)
    {
        this._question = question;
    } //-- void setQuestion(java.lang.String) 

    /**
     * Sets the value of field 'questionID'.
     * 
     * @param questionID the value of field 'questionID'.
    **/
    public void setQuestionID(int questionID)
    {
        this._questionID = questionID;
        this._has_questionID = true;
    } //-- void setQuestionID(int) 

    /**
     * Sets the value of field 'questionType'.
     * 
     * @param questionType the value of field 'questionType'.
    **/
    public void setQuestionType(QuestionType questionType)
    {
        this._questionType = questionType;
    } //-- void setQuestionType(QuestionType) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
