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
public class Irrigation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private IrrigationType _irrigationType;

    private HorsePower _horsePower;

    private EnergySource _energySource;

    private SoilType _soilType;

    private MeterLocation _meterLocation;

    private MeterVoltage _meterVoltage;


      //----------------/
     //- Constructors -/
    //----------------/

    public Irrigation() {
        super();
    } //-- com.cannontech.stars.xml.serialize.Irrigation()


      //-----------/
     //- Methods -/
    //-----------/

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
     * Returns the value of field 'horsePower'.
     * 
     * @return the value of field 'horsePower'.
    **/
    public HorsePower getHorsePower()
    {
        return this._horsePower;
    } //-- HorsePower getHorsePower() 

    /**
     * Returns the value of field 'irrigationType'.
     * 
     * @return the value of field 'irrigationType'.
    **/
    public IrrigationType getIrrigationType()
    {
        return this._irrigationType;
    } //-- IrrigationType getIrrigationType() 

    /**
     * Returns the value of field 'meterLocation'.
     * 
     * @return the value of field 'meterLocation'.
    **/
    public MeterLocation getMeterLocation()
    {
        return this._meterLocation;
    } //-- MeterLocation getMeterLocation() 

    /**
     * Returns the value of field 'meterVoltage'.
     * 
     * @return the value of field 'meterVoltage'.
    **/
    public MeterVoltage getMeterVoltage()
    {
        return this._meterVoltage;
    } //-- MeterVoltage getMeterVoltage() 

    /**
     * Returns the value of field 'soilType'.
     * 
     * @return the value of field 'soilType'.
    **/
    public SoilType getSoilType()
    {
        return this._soilType;
    } //-- SoilType getSoilType() 

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
     * Sets the value of field 'horsePower'.
     * 
     * @param horsePower the value of field 'horsePower'.
    **/
    public void setHorsePower(HorsePower horsePower)
    {
        this._horsePower = horsePower;
    } //-- void setHorsePower(HorsePower) 

    /**
     * Sets the value of field 'irrigationType'.
     * 
     * @param irrigationType the value of field 'irrigationType'.
    **/
    public void setIrrigationType(IrrigationType irrigationType)
    {
        this._irrigationType = irrigationType;
    } //-- void setIrrigationType(IrrigationType) 

    /**
     * Sets the value of field 'meterLocation'.
     * 
     * @param meterLocation the value of field 'meterLocation'.
    **/
    public void setMeterLocation(MeterLocation meterLocation)
    {
        this._meterLocation = meterLocation;
    } //-- void setMeterLocation(MeterLocation) 

    /**
     * Sets the value of field 'meterVoltage'.
     * 
     * @param meterVoltage the value of field 'meterVoltage'.
    **/
    public void setMeterVoltage(MeterVoltage meterVoltage)
    {
        this._meterVoltage = meterVoltage;
    } //-- void setMeterVoltage(MeterVoltage) 

    /**
     * Sets the value of field 'soilType'.
     * 
     * @param soilType the value of field 'soilType'.
    **/
    public void setSoilType(SoilType soilType)
    {
        this._soilType = soilType;
    } //-- void setSoilType(SoilType) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.Irrigation unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.Irrigation) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.Irrigation.class, reader);
    } //-- com.cannontech.stars.xml.serialize.Irrigation unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
