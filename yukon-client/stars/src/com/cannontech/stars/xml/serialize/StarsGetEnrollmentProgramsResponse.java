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
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsGetEnrollmentProgramsResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsApplianceCategoryList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsGetEnrollmentProgramsResponse() {
        super();
        _starsApplianceCategoryList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsGetEnrollmentProgramsResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsApplianceCategory
    **/
    public void addStarsApplianceCategory(StarsApplianceCategory vStarsApplianceCategory)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsApplianceCategoryList.addElement(vStarsApplianceCategory);
    } //-- void addStarsApplianceCategory(StarsApplianceCategory) 

    /**
     * 
     * 
     * @param index
     * @param vStarsApplianceCategory
    **/
    public void addStarsApplianceCategory(int index, StarsApplianceCategory vStarsApplianceCategory)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsApplianceCategoryList.insertElementAt(vStarsApplianceCategory, index);
    } //-- void addStarsApplianceCategory(int, StarsApplianceCategory) 

    /**
    **/
    public java.util.Enumeration enumerateStarsApplianceCategory()
    {
        return _starsApplianceCategoryList.elements();
    } //-- java.util.Enumeration enumerateStarsApplianceCategory() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsApplianceCategory getStarsApplianceCategory(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsApplianceCategoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsApplianceCategory) _starsApplianceCategoryList.elementAt(index);
    } //-- StarsApplianceCategory getStarsApplianceCategory(int) 

    /**
    **/
    public StarsApplianceCategory[] getStarsApplianceCategory()
    {
        int size = _starsApplianceCategoryList.size();
        StarsApplianceCategory[] mArray = new StarsApplianceCategory[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsApplianceCategory) _starsApplianceCategoryList.elementAt(index);
        }
        return mArray;
    } //-- StarsApplianceCategory[] getStarsApplianceCategory() 

    /**
    **/
    public int getStarsApplianceCategoryCount()
    {
        return _starsApplianceCategoryList.size();
    } //-- int getStarsApplianceCategoryCount() 

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
    public void removeAllStarsApplianceCategory()
    {
        _starsApplianceCategoryList.removeAllElements();
    } //-- void removeAllStarsApplianceCategory() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsApplianceCategory removeStarsApplianceCategory(int index)
    {
        java.lang.Object obj = _starsApplianceCategoryList.elementAt(index);
        _starsApplianceCategoryList.removeElementAt(index);
        return (StarsApplianceCategory) obj;
    } //-- StarsApplianceCategory removeStarsApplianceCategory(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsApplianceCategory
    **/
    public void setStarsApplianceCategory(int index, StarsApplianceCategory vStarsApplianceCategory)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsApplianceCategoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsApplianceCategoryList.setElementAt(vStarsApplianceCategory, index);
    } //-- void setStarsApplianceCategory(int, StarsApplianceCategory) 

    /**
     * 
     * 
     * @param starsApplianceCategoryArray
    **/
    public void setStarsApplianceCategory(StarsApplianceCategory[] starsApplianceCategoryArray)
    {
        //-- copy array
        _starsApplianceCategoryList.removeAllElements();
        for (int i = 0; i < starsApplianceCategoryArray.length; i++) {
            _starsApplianceCategoryList.addElement(starsApplianceCategoryArray[i]);
        }
    } //-- void setStarsApplianceCategory(StarsApplianceCategory) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsGetEnrollmentProgramsResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsGetEnrollmentProgramsResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsGetEnrollmentProgramsResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsGetEnrollmentProgramsResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
