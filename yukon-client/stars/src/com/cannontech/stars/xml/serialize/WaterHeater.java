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
public class WaterHeater implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private NumberOfGallons _numberOfGallons;

    private EnergySource _energySource;

    private int _numberOfElements;

    /**
     * keeps track of state for field: _numberOfElements
    **/
    private boolean _has_numberOfElements;


      //----------------/
     //- Constructors -/
    //----------------/

    public WaterHeater() {
        super();
    } //-- com.cannontech.stars.xml.serialize.WaterHeater()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteNumberOfElements()
    {
        this._has_numberOfElements= false;
    } //-- void deleteNumberOfElements() 

    /**
     * Returns the value of field 'energySource'.
     * 
     * @return the value of field 'energySource'.
    **/
    public EnergySource getEnergySource()
    {
        return this._energySource;
    } //-- EnergySource getEnergySource() 

    /**
     * Returns the value of field 'numberOfElements'.
     * 
     * @return the value of field 'numberOfElements'.
    **/
    public int getNumberOfElements()
    {
        return this._numberOfElements;
    } //-- int getNumberOfElements() 

    /**
     * Returns the value of field 'numberOfGallons'.
     * 
     * @return the value of field 'numberOfGallons'.
    **/
    public NumberOfGallons getNumberOfGallons()
    {
        return this._numberOfGallons;
    } //-- NumberOfGallons getNumberOfGallons() 

    /**
    **/
    public boolean hasNumberOfElements()
    {
        return this._has_numberOfElements;
    } //-- boolean hasNumberOfElements() 

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
     * Sets the value of field 'energySource'.
     * 
     * @param energySource the value of field 'energySource'.
    **/
    public void setEnergySource(EnergySource energySource)
    {
        this._energySource = energySource;
    } //-- void setEnergySource(EnergySource) 

    /**
     * Sets the value of field 'numberOfElements'.
     * 
     * @param numberOfElements the value of field 'numberOfElements'
    **/
    public void setNumberOfElements(int numberOfElements)
    {
        this._numberOfElements = numberOfElements;
        this._has_numberOfElements = true;
    } //-- void setNumberOfElements(int) 

    /**
     * Sets the value of field 'numberOfGallons'.
     * 
     * @param numberOfGallons the value of field 'numberOfGallons'.
    **/
    public void setNumberOfGallons(NumberOfGallons numberOfGallons)
    {
        this._numberOfGallons = numberOfGallons;
    } //-- void setNumberOfGallons(NumberOfGallons) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.WaterHeater unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.WaterHeater) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.WaterHeater.class, reader);
    } //-- com.cannontech.stars.xml.serialize.WaterHeater unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
