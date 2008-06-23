
package com.cannontech.stars.xml.serialize;

import java.util.Vector;

public class StarsExitInterviewQuestions {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsExitInterviewQuestionList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsExitInterviewQuestions() {
        super();
        _starsExitInterviewQuestionList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsExitInterviewQuestion
    **/
    public void addStarsExitInterviewQuestion(StarsExitInterviewQuestion vStarsExitInterviewQuestion)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsExitInterviewQuestionList.addElement(vStarsExitInterviewQuestion);
    } //-- void addStarsExitInterviewQuestion(StarsExitInterviewQuestion) 

    /**
     * 
     * 
     * @param index
     * @param vStarsExitInterviewQuestion
    **/
    public void addStarsExitInterviewQuestion(int index, StarsExitInterviewQuestion vStarsExitInterviewQuestion)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsExitInterviewQuestionList.insertElementAt(vStarsExitInterviewQuestion, index);
    } //-- void addStarsExitInterviewQuestion(int, StarsExitInterviewQuestion) 

    /**
    **/
    public java.util.Enumeration enumerateStarsExitInterviewQuestion()
    {
        return _starsExitInterviewQuestionList.elements();
    } //-- java.util.Enumeration enumerateStarsExitInterviewQuestion() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsExitInterviewQuestion getStarsExitInterviewQuestion(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsExitInterviewQuestionList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsExitInterviewQuestion) _starsExitInterviewQuestionList.elementAt(index);
    } //-- StarsExitInterviewQuestion getStarsExitInterviewQuestion(int) 

    /**
    **/
    public StarsExitInterviewQuestion[] getStarsExitInterviewQuestion()
    {
        int size = _starsExitInterviewQuestionList.size();
        StarsExitInterviewQuestion[] mArray = new StarsExitInterviewQuestion[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsExitInterviewQuestion) _starsExitInterviewQuestionList.elementAt(index);
        }
        return mArray;
    } //-- StarsExitInterviewQuestion[] getStarsExitInterviewQuestion() 

    /**
    **/
    public int getStarsExitInterviewQuestionCount()
    {
        return _starsExitInterviewQuestionList.size();
    } //-- int getStarsExitInterviewQuestionCount() 

    /**
    **/
    public void removeAllStarsExitInterviewQuestion()
    {
        _starsExitInterviewQuestionList.removeAllElements();
    } //-- void removeAllStarsExitInterviewQuestion() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsExitInterviewQuestion removeStarsExitInterviewQuestion(int index)
    {
        java.lang.Object obj = _starsExitInterviewQuestionList.elementAt(index);
        _starsExitInterviewQuestionList.removeElementAt(index);
        return (StarsExitInterviewQuestion) obj;
    } //-- StarsExitInterviewQuestion removeStarsExitInterviewQuestion(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsExitInterviewQuestion
    **/
    public void setStarsExitInterviewQuestion(int index, StarsExitInterviewQuestion vStarsExitInterviewQuestion)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsExitInterviewQuestionList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsExitInterviewQuestionList.setElementAt(vStarsExitInterviewQuestion, index);
    } //-- void setStarsExitInterviewQuestion(int, StarsExitInterviewQuestion) 

    /**
     * 
     * 
     * @param starsExitInterviewQuestionArray
    **/
    public void setStarsExitInterviewQuestion(StarsExitInterviewQuestion[] starsExitInterviewQuestionArray)
    {
        //-- copy array
        _starsExitInterviewQuestionList.removeAllElements();
        for (int i = 0; i < starsExitInterviewQuestionArray.length; i++) {
            _starsExitInterviewQuestionList.addElement(starsExitInterviewQuestionArray[i]);
        }
    } //-- void setStarsExitInterviewQuestion(StarsExitInterviewQuestion) 

}
