/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsApplianceCategory.java,v 1.73 2004/05/10 22:13:28 zyao Exp $
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
 * @version $Revision: 1.73 $ $Date: 2004/05/10 22:13:28 $
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

    private int _categoryID;

    /**
     * keeps track of state for field: _categoryID
    **/
    private boolean _has_categoryID;

    private java.lang.String _description;

    private StarsWebConfig _starsWebConfig;

    private java.util.Vector _starsEnrLMProgramList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsApplianceCategory() {
        super();
        _starsEnrLMProgramList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsApplianceCategory()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsEnrLMProgram
    **/
    public void addStarsEnrLMProgram(StarsEnrLMProgram vStarsEnrLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsEnrLMProgramList.addElement(vStarsEnrLMProgram);
    } //-- void addStarsEnrLMProgram(StarsEnrLMProgram) 

    /**
     * 
     * 
     * @param index
     * @param vStarsEnrLMProgram
    **/
    public void addStarsEnrLMProgram(int index, StarsEnrLMProgram vStarsEnrLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsEnrLMProgramList.insertElementAt(vStarsEnrLMProgram, index);
    } //-- void addStarsEnrLMProgram(int, StarsEnrLMProgram) 

    /**
    **/
    public void deleteApplianceCategoryID()
    {
        this._has_applianceCategoryID= false;
    } //-- void deleteApplianceCategoryID() 

    /**
    **/
    public void deleteCategoryID()
    {
        this._has_categoryID= false;
    } //-- void deleteCategoryID() 

    /**
    **/
    public java.util.Enumeration enumerateStarsEnrLMProgram()
    {
        return _starsEnrLMProgramList.elements();
    } //-- java.util.Enumeration enumerateStarsEnrLMProgram() 

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
     * Returns the value of field 'categoryID'.
     * 
     * @return the value of field 'categoryID'.
    **/
    public int getCategoryID()
    {
        return this._categoryID;
    } //-- int getCategoryID() 

    /**
     * Returns the value of field 'description'.
     * 
     * @return the value of field 'description'.
    **/
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsEnrLMProgram getStarsEnrLMProgram(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsEnrLMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsEnrLMProgram) _starsEnrLMProgramList.elementAt(index);
    } //-- StarsEnrLMProgram getStarsEnrLMProgram(int) 

    /**
    **/
    public StarsEnrLMProgram[] getStarsEnrLMProgram()
    {
        int size = _starsEnrLMProgramList.size();
        StarsEnrLMProgram[] mArray = new StarsEnrLMProgram[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsEnrLMProgram) _starsEnrLMProgramList.elementAt(index);
        }
        return mArray;
    } //-- StarsEnrLMProgram[] getStarsEnrLMProgram() 

    /**
    **/
    public int getStarsEnrLMProgramCount()
    {
        return _starsEnrLMProgramList.size();
    } //-- int getStarsEnrLMProgramCount() 

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
    public boolean hasCategoryID()
    {
        return this._has_categoryID;
    } //-- boolean hasCategoryID() 

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
    public void removeAllStarsEnrLMProgram()
    {
        _starsEnrLMProgramList.removeAllElements();
    } //-- void removeAllStarsEnrLMProgram() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsEnrLMProgram removeStarsEnrLMProgram(int index)
    {
        java.lang.Object obj = _starsEnrLMProgramList.elementAt(index);
        _starsEnrLMProgramList.removeElementAt(index);
        return (StarsEnrLMProgram) obj;
    } //-- StarsEnrLMProgram removeStarsEnrLMProgram(int) 

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
     * Sets the value of field 'categoryID'.
     * 
     * @param categoryID the value of field 'categoryID'.
    **/
    public void setCategoryID(int categoryID)
    {
        this._categoryID = categoryID;
        this._has_categoryID = true;
    } //-- void setCategoryID(int) 

    /**
     * Sets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
    **/
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * 
     * 
     * @param index
     * @param vStarsEnrLMProgram
    **/
    public void setStarsEnrLMProgram(int index, StarsEnrLMProgram vStarsEnrLMProgram)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsEnrLMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsEnrLMProgramList.setElementAt(vStarsEnrLMProgram, index);
    } //-- void setStarsEnrLMProgram(int, StarsEnrLMProgram) 

    /**
     * 
     * 
     * @param starsEnrLMProgramArray
    **/
    public void setStarsEnrLMProgram(StarsEnrLMProgram[] starsEnrLMProgramArray)
    {
        //-- copy array
        _starsEnrLMProgramList.removeAllElements();
        for (int i = 0; i < starsEnrLMProgramArray.length; i++) {
            _starsEnrLMProgramList.addElement(starsEnrLMProgramArray[i]);
        }
    } //-- void setStarsEnrLMProgram(StarsEnrLMProgram) 

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
