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
public class StarsEnrLMProgram implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _programID;

    /**
     * keeps track of state for field: _programID
    **/
    private boolean _has_programID;

    private java.lang.String _programName;

    private StarsWebConfig _starsWebConfig;

    private java.util.Vector _addressingGroupList;

    private ChanceOfControl _chanceOfControl;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsEnrLMProgram() {
        super();
        _addressingGroupList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsEnrLMProgram()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vAddressingGroup
    **/
    public void addAddressingGroup(AddressingGroup vAddressingGroup)
        throws java.lang.IndexOutOfBoundsException
    {
        _addressingGroupList.addElement(vAddressingGroup);
    } //-- void addAddressingGroup(AddressingGroup) 

    /**
     * 
     * 
     * @param index
     * @param vAddressingGroup
    **/
    public void addAddressingGroup(int index, AddressingGroup vAddressingGroup)
        throws java.lang.IndexOutOfBoundsException
    {
        _addressingGroupList.insertElementAt(vAddressingGroup, index);
    } //-- void addAddressingGroup(int, AddressingGroup) 

    /**
    **/
    public void deleteProgramID()
    {
        this._has_programID= false;
    } //-- void deleteProgramID() 

    /**
    **/
    public java.util.Enumeration enumerateAddressingGroup()
    {
        return _addressingGroupList.elements();
    } //-- java.util.Enumeration enumerateAddressingGroup() 

    /**
     * 
     * 
     * @param index
    **/
    public AddressingGroup getAddressingGroup(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _addressingGroupList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (AddressingGroup) _addressingGroupList.elementAt(index);
    } //-- AddressingGroup getAddressingGroup(int) 

    /**
    **/
    public AddressingGroup[] getAddressingGroup()
    {
        int size = _addressingGroupList.size();
        AddressingGroup[] mArray = new AddressingGroup[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (AddressingGroup) _addressingGroupList.elementAt(index);
        }
        return mArray;
    } //-- AddressingGroup[] getAddressingGroup() 

    /**
    **/
    public int getAddressingGroupCount()
    {
        return _addressingGroupList.size();
    } //-- int getAddressingGroupCount() 

    /**
     * Returns the value of field 'chanceOfControl'.
     * 
     * @return the value of field 'chanceOfControl'.
    **/
    public ChanceOfControl getChanceOfControl()
    {
        return this._chanceOfControl;
    } //-- ChanceOfControl getChanceOfControl() 

    /**
     * Returns the value of field 'programID'.
     * 
     * @return the value of field 'programID'.
    **/
    public int getProgramID()
    {
        return this._programID;
    } //-- int getProgramID() 

    /**
     * Returns the value of field 'programName'.
     * 
     * @return the value of field 'programName'.
    **/
    public java.lang.String getProgramName()
    {
        return this._programName;
    } //-- java.lang.String getProgramName() 

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
    public boolean hasProgramID()
    {
        return this._has_programID;
    } //-- boolean hasProgramID() 

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
     * 
     * 
     * @param index
    **/
    public AddressingGroup removeAddressingGroup(int index)
    {
        java.lang.Object obj = _addressingGroupList.elementAt(index);
        _addressingGroupList.removeElementAt(index);
        return (AddressingGroup) obj;
    } //-- AddressingGroup removeAddressingGroup(int) 

    /**
    **/
    public void removeAllAddressingGroup()
    {
        _addressingGroupList.removeAllElements();
    } //-- void removeAllAddressingGroup() 

    /**
     * 
     * 
     * @param index
     * @param vAddressingGroup
    **/
    public void setAddressingGroup(int index, AddressingGroup vAddressingGroup)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _addressingGroupList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _addressingGroupList.setElementAt(vAddressingGroup, index);
    } //-- void setAddressingGroup(int, AddressingGroup) 

    /**
     * 
     * 
     * @param addressingGroupArray
    **/
    public void setAddressingGroup(AddressingGroup[] addressingGroupArray)
    {
        //-- copy array
        _addressingGroupList.removeAllElements();
        for (int i = 0; i < addressingGroupArray.length; i++) {
            _addressingGroupList.addElement(addressingGroupArray[i]);
        }
    } //-- void setAddressingGroup(AddressingGroup) 

    /**
     * Sets the value of field 'chanceOfControl'.
     * 
     * @param chanceOfControl the value of field 'chanceOfControl'.
    **/
    public void setChanceOfControl(ChanceOfControl chanceOfControl)
    {
        this._chanceOfControl = chanceOfControl;
    } //-- void setChanceOfControl(ChanceOfControl) 

    /**
     * Sets the value of field 'programID'.
     * 
     * @param programID the value of field 'programID'.
    **/
    public void setProgramID(int programID)
    {
        this._programID = programID;
        this._has_programID = true;
    } //-- void setProgramID(int) 

    /**
     * Sets the value of field 'programName'.
     * 
     * @param programName the value of field 'programName'.
    **/
    public void setProgramName(java.lang.String programName)
    {
        this._programName = programName;
    } //-- void setProgramName(java.lang.String) 

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
    public static com.cannontech.stars.xml.serialize.StarsEnrLMProgram unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsEnrLMProgram) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsEnrLMProgram.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsEnrLMProgram unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
