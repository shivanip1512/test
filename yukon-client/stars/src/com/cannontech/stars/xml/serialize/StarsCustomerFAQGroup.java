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

import java.util.Vector;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsCustomerFAQGroup implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _subject;

    private java.util.Vector _starsCustomerFAQList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustomerFAQGroup() {
        super();
        _starsCustomerFAQList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsCustomerFAQGroup()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsCustomerFAQ
    **/
    public void addStarsCustomerFAQ(StarsCustomerFAQ vStarsCustomerFAQ)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsCustomerFAQList.addElement(vStarsCustomerFAQ);
    } //-- void addStarsCustomerFAQ(StarsCustomerFAQ) 

    /**
     * 
     * 
     * @param index
     * @param vStarsCustomerFAQ
    **/
    public void addStarsCustomerFAQ(int index, StarsCustomerFAQ vStarsCustomerFAQ)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsCustomerFAQList.insertElementAt(vStarsCustomerFAQ, index);
    } //-- void addStarsCustomerFAQ(int, StarsCustomerFAQ) 

    /**
    **/
    public java.util.Enumeration enumerateStarsCustomerFAQ()
    {
        return _starsCustomerFAQList.elements();
    } //-- java.util.Enumeration enumerateStarsCustomerFAQ() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsCustomerFAQ getStarsCustomerFAQ(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCustomerFAQList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsCustomerFAQ) _starsCustomerFAQList.elementAt(index);
    } //-- StarsCustomerFAQ getStarsCustomerFAQ(int) 

    /**
    **/
    public StarsCustomerFAQ[] getStarsCustomerFAQ()
    {
        int size = _starsCustomerFAQList.size();
        StarsCustomerFAQ[] mArray = new StarsCustomerFAQ[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsCustomerFAQ) _starsCustomerFAQList.elementAt(index);
        }
        return mArray;
    } //-- StarsCustomerFAQ[] getStarsCustomerFAQ() 

    /**
    **/
    public int getStarsCustomerFAQCount()
    {
        return _starsCustomerFAQList.size();
    } //-- int getStarsCustomerFAQCount() 

    /**
     * Returns the value of field 'subject'.
     * 
     * @return the value of field 'subject'.
    **/
    public java.lang.String getSubject()
    {
        return this._subject;
    } //-- java.lang.String getSubject() 

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
    public void removeAllStarsCustomerFAQ()
    {
        _starsCustomerFAQList.removeAllElements();
    } //-- void removeAllStarsCustomerFAQ() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsCustomerFAQ removeStarsCustomerFAQ(int index)
    {
        java.lang.Object obj = _starsCustomerFAQList.elementAt(index);
        _starsCustomerFAQList.removeElementAt(index);
        return (StarsCustomerFAQ) obj;
    } //-- StarsCustomerFAQ removeStarsCustomerFAQ(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsCustomerFAQ
    **/
    public void setStarsCustomerFAQ(int index, StarsCustomerFAQ vStarsCustomerFAQ)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCustomerFAQList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsCustomerFAQList.setElementAt(vStarsCustomerFAQ, index);
    } //-- void setStarsCustomerFAQ(int, StarsCustomerFAQ) 

    /**
     * 
     * 
     * @param starsCustomerFAQArray
    **/
    public void setStarsCustomerFAQ(StarsCustomerFAQ[] starsCustomerFAQArray)
    {
        //-- copy array
        _starsCustomerFAQList.removeAllElements();
        for (int i = 0; i < starsCustomerFAQArray.length; i++) {
            _starsCustomerFAQList.addElement(starsCustomerFAQArray[i]);
        }
    } //-- void setStarsCustomerFAQ(StarsCustomerFAQ) 

    /**
     * Sets the value of field 'subject'.
     * 
     * @param subject the value of field 'subject'.
    **/
    public void setSubject(java.lang.String subject)
    {
        this._subject = subject;
    } //-- void setSubject(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsCustomerFAQGroup unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsCustomerFAQGroup) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsCustomerFAQGroup.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsCustomerFAQGroup unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
