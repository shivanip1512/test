/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsApplianceCategory.java,v 1.9 2002/09/25 15:09:14 zyao Exp $
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
 * @version $Revision: 1.9 $ $Date: 2002/09/25 15:09:14 $
**/
public class StarsApplianceCategory implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _applianceCategoryID;

    /**
     * keeps track of state for field: _applianceCategoryID
    **/
    private boolean _has_applianceCategoryID;

    private java.lang.String _categoryName;

    private StarsWebConfig _starsWebConfig;

    private java.util.Vector _starsEnrollmentLMProgramList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsApplianceCategory() {
        super();
        _starsEnrollmentLMProgramList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsApplianceCategory()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsEnrollmentLMProgram
    **/
    public void addStarsEnrollmentLMProgram(StarsEnrollmentLMProgram vStarsEnrollmentLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsEnrollmentLMProgramList.addElement(vStarsEnrollmentLMProgram);
    } //-- void addStarsEnrollmentLMProgram(StarsEnrollmentLMProgram) 

    /**
     * 
     * 
     * @param index
     * @param vStarsEnrollmentLMProgram
    **/
    public void addStarsEnrollmentLMProgram(int index, StarsEnrollmentLMProgram vStarsEnrollmentLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsEnrollmentLMProgramList.insertElementAt(vStarsEnrollmentLMProgram, index);
    } //-- void addStarsEnrollmentLMProgram(int, StarsEnrollmentLMProgram) 

    /**
    **/
    public void deleteApplianceCategoryID()
    {
        this._has_applianceCategoryID= false;
    } //-- void deleteApplianceCategoryID() 

    /**
    **/
    public java.util.Enumeration enumerateStarsEnrollmentLMProgram()
    {
        return _starsEnrollmentLMProgramList.elements();
    } //-- java.util.Enumeration enumerateStarsEnrollmentLMProgram() 

    /**
     * Returns the value of field 'applianceCategoryID'.
     * 
     * @return the value of field 'applianceCategoryID'.
    **/
    public int getApplianceCategoryID()
    {
        return this._applianceCategoryID;
    } //-- int getApplianceCategoryID() 

    /**
     * Returns the value of field 'categoryName'.
     * 
     * @return the value of field 'categoryName'.
    **/
    public java.lang.String getCategoryName()
    {
        return this._categoryName;
    } //-- java.lang.String getCategoryName() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsEnrollmentLMProgram getStarsEnrollmentLMProgram(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsEnrollmentLMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsEnrollmentLMProgram) _starsEnrollmentLMProgramList.elementAt(index);
    } //-- StarsEnrollmentLMProgram getStarsEnrollmentLMProgram(int) 

    /**
    **/
    public StarsEnrollmentLMProgram[] getStarsEnrollmentLMProgram()
    {
        int size = _starsEnrollmentLMProgramList.size();
        StarsEnrollmentLMProgram[] mArray = new StarsEnrollmentLMProgram[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsEnrollmentLMProgram) _starsEnrollmentLMProgramList.elementAt(index);
        }
        return mArray;
    } //-- StarsEnrollmentLMProgram[] getStarsEnrollmentLMProgram() 

    /**
    **/
    public int getStarsEnrollmentLMProgramCount()
    {
        return _starsEnrollmentLMProgramList.size();
    } //-- int getStarsEnrollmentLMProgramCount() 

    /**
     * Returns the value of field 'starsWebConfig'.
     * 
     * @return the value of field 'starsWebConfig'.
    **/
    public StarsWebConfig getStarsWebConfig()
    {
        return this._starsWebConfig;
    } //-- StarsWebConfig getStarsWebConfig() 

    /**
    **/
    public boolean hasApplianceCategoryID()
    {
        return this._has_applianceCategoryID;
    } //-- boolean hasApplianceCategoryID() 

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
    public void removeAllStarsEnrollmentLMProgram()
    {
        _starsEnrollmentLMProgramList.removeAllElements();
    } //-- void removeAllStarsEnrollmentLMProgram() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsEnrollmentLMProgram removeStarsEnrollmentLMProgram(int index)
    {
        java.lang.Object obj = _starsEnrollmentLMProgramList.elementAt(index);
        _starsEnrollmentLMProgramList.removeElementAt(index);
        return (StarsEnrollmentLMProgram) obj;
    } //-- StarsEnrollmentLMProgram removeStarsEnrollmentLMProgram(int) 

    /**
     * Sets the value of field 'applianceCategoryID'.
     * 
     * @param applianceCategoryID the value of field
     * 'applianceCategoryID'.
    **/
    public void setApplianceCategoryID(int applianceCategoryID)
    {
        this._applianceCategoryID = applianceCategoryID;
        this._has_applianceCategoryID = true;
    } //-- void setApplianceCategoryID(int) 

    /**
     * Sets the value of field 'categoryName'.
     * 
     * @param categoryName the value of field 'categoryName'.
    **/
    public void setCategoryName(java.lang.String categoryName)
    {
        this._categoryName = categoryName;
    } //-- void setCategoryName(java.lang.String) 

    /**
     * 
     * 
     * @param index
     * @param vStarsEnrollmentLMProgram
    **/
    public void setStarsEnrollmentLMProgram(int index, StarsEnrollmentLMProgram vStarsEnrollmentLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsEnrollmentLMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsEnrollmentLMProgramList.setElementAt(vStarsEnrollmentLMProgram, index);
    } //-- void setStarsEnrollmentLMProgram(int, StarsEnrollmentLMProgram) 

    /**
     * 
     * 
     * @param starsEnrollmentLMProgramArray
    **/
    public void setStarsEnrollmentLMProgram(StarsEnrollmentLMProgram[] starsEnrollmentLMProgramArray)
    {
        //-- copy array
        _starsEnrollmentLMProgramList.removeAllElements();
        for (int i = 0; i < starsEnrollmentLMProgramArray.length; i++) {
            _starsEnrollmentLMProgramList.addElement(starsEnrollmentLMProgramArray[i]);
        }
    } //-- void setStarsEnrollmentLMProgram(StarsEnrollmentLMProgram) 

    /**
     * Sets the value of field 'starsWebConfig'.
     * 
     * @param starsWebConfig the value of field 'starsWebConfig'.
    **/
    public void setStarsWebConfig(StarsWebConfig starsWebConfig)
    {
        this._starsWebConfig = starsWebConfig;
    } //-- void setStarsWebConfig(StarsWebConfig) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsApplianceCategory unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsApplianceCategory) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsApplianceCategory.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsApplianceCategory unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
