/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsSendOptOutNotification.java,v 1.7 2005/01/04 20:45:05 yao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision: 1.7 $ $Date: 2005/01/04 20:45:05 $
**/
public class StarsSendOptOutNotification implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsExitInterviewQuestionList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSendOptOutNotification() {
        super();
        _starsExitInterviewQuestionList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsSendOptOutNotification()


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
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

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

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsSendOptOutNotification unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsSendOptOutNotification) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsSendOptOutNotification.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsSendOptOutNotification unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
