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
public class DualFuel implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private SwitchOverType _switchOverType;

    private int _secondaryKWCapacity;

    /**
     * keeps track of state for field: _secondaryKWCapacity
    **/
    private boolean _has_secondaryKWCapacity;

    private SecondaryEnergySource _secondaryEnergySource;


      //----------------/
     //- Constructors -/
    //----------------/

    public DualFuel() {
        super();
    } //-- com.cannontech.stars.xml.serialize.DualFuel()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteSecondaryKWCapacity()
    {
        this._has_secondaryKWCapacity= false;
    } //-- void deleteSecondaryKWCapacity() 

    /**
     * Returns the value of field 'secondaryEnergySource'.
     * 
     * @return the value of field 'secondaryEnergySource'.
    **/
    public SecondaryEnergySource getSecondaryEnergySource()
    {
        return this._secondaryEnergySource;
    } //-- SecondaryEnergySource getSecondaryEnergySource() 

    /**
     * Returns the value of field 'secondaryKWCapacity'.
     * 
     * @return the value of field 'secondaryKWCapacity'.
    **/
    public int getSecondaryKWCapacity()
    {
        return this._secondaryKWCapacity;
    } //-- int getSecondaryKWCapacity() 

    /**
     * Returns the value of field 'switchOverType'.
     * 
     * @return the value of field 'switchOverType'.
    **/
    public SwitchOverType getSwitchOverType()
    {
        return this._switchOverType;
    } //-- SwitchOverType getSwitchOverType() 

    /**
    **/
    public boolean hasSecondaryKWCapacity()
    {
        return this._has_secondaryKWCapacity;
    } //-- boolean hasSecondaryKWCapacity() 

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
     * Sets the value of field 'secondaryEnergySource'.
     * 
     * @param secondaryEnergySource the value of field
     * 'secondaryEnergySource'.
    **/
    public void setSecondaryEnergySource(SecondaryEnergySource secondaryEnergySource)
    {
        this._secondaryEnergySource = secondaryEnergySource;
    } //-- void setSecondaryEnergySource(SecondaryEnergySource) 

    /**
     * Sets the value of field 'secondaryKWCapacity'.
     * 
     * @param secondaryKWCapacity the value of field
     * 'secondaryKWCapacity'.
    **/
    public void setSecondaryKWCapacity(int secondaryKWCapacity)
    {
        this._secondaryKWCapacity = secondaryKWCapacity;
        this._has_secondaryKWCapacity = true;
    } //-- void setSecondaryKWCapacity(int) 

    /**
     * Sets the value of field 'switchOverType'.
     * 
     * @param switchOverType the value of field 'switchOverType'.
    **/
    public void setSwitchOverType(SwitchOverType switchOverType)
    {
        this._switchOverType = switchOverType;
    } //-- void setSwitchOverType(SwitchOverType) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.DualFuel unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.DualFuel) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.DualFuel.class, reader);
    } //-- com.cannontech.stars.xml.serialize.DualFuel unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
