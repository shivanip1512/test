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
public class Generator implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private TransferSwitchType _transferSwitchType;

    private TransferSwitchManufacturer _transferSwitchManufacturer;

    private int _peakKWCapacity;

    /**
     * keeps track of state for field: _peakKWCapacity
    **/
    private boolean _has_peakKWCapacity;

    private int _fuelCapGallons;

    /**
     * keeps track of state for field: _fuelCapGallons
    **/
    private boolean _has_fuelCapGallons;

    private int _startDelaySeconds;

    /**
     * keeps track of state for field: _startDelaySeconds
    **/
    private boolean _has_startDelaySeconds;


      //----------------/
     //- Constructors -/
    //----------------/

    public Generator() {
        super();
    } //-- com.cannontech.stars.xml.serialize.Generator()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteFuelCapGallons()
    {
        this._has_fuelCapGallons= false;
    } //-- void deleteFuelCapGallons() 

    /**
    **/
    public void deletePeakKWCapacity()
    {
        this._has_peakKWCapacity= false;
    } //-- void deletePeakKWCapacity() 

    /**
    **/
    public void deleteStartDelaySeconds()
    {
        this._has_startDelaySeconds= false;
    } //-- void deleteStartDelaySeconds() 

    /**
     * Returns the value of field 'fuelCapGallons'.
     * 
     * @return the value of field 'fuelCapGallons'.
    **/
    public int getFuelCapGallons()
    {
        return this._fuelCapGallons;
    } //-- int getFuelCapGallons() 

    /**
     * Returns the value of field 'peakKWCapacity'.
     * 
     * @return the value of field 'peakKWCapacity'.
    **/
    public int getPeakKWCapacity()
    {
        return this._peakKWCapacity;
    } //-- int getPeakKWCapacity() 

    /**
     * Returns the value of field 'startDelaySeconds'.
     * 
     * @return the value of field 'startDelaySeconds'.
    **/
    public int getStartDelaySeconds()
    {
        return this._startDelaySeconds;
    } //-- int getStartDelaySeconds() 

    /**
     * Returns the value of field 'transferSwitchManufacturer'.
     * 
     * @return the value of field 'transferSwitchManufacturer'.
    **/
    public TransferSwitchManufacturer getTransferSwitchManufacturer()
    {
        return this._transferSwitchManufacturer;
    } //-- TransferSwitchManufacturer getTransferSwitchManufacturer() 

    /**
     * Returns the value of field 'transferSwitchType'.
     * 
     * @return the value of field 'transferSwitchType'.
    **/
    public TransferSwitchType getTransferSwitchType()
    {
        return this._transferSwitchType;
    } //-- TransferSwitchType getTransferSwitchType() 

    /**
    **/
    public boolean hasFuelCapGallons()
    {
        return this._has_fuelCapGallons;
    } //-- boolean hasFuelCapGallons() 

    /**
    **/
    public boolean hasPeakKWCapacity()
    {
        return this._has_peakKWCapacity;
    } //-- boolean hasPeakKWCapacity() 

    /**
    **/
    public boolean hasStartDelaySeconds()
    {
        return this._has_startDelaySeconds;
    } //-- boolean hasStartDelaySeconds() 

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
     * Sets the value of field 'fuelCapGallons'.
     * 
     * @param fuelCapGallons the value of field 'fuelCapGallons'.
    **/
    public void setFuelCapGallons(int fuelCapGallons)
    {
        this._fuelCapGallons = fuelCapGallons;
        this._has_fuelCapGallons = true;
    } //-- void setFuelCapGallons(int) 

    /**
     * Sets the value of field 'peakKWCapacity'.
     * 
     * @param peakKWCapacity the value of field 'peakKWCapacity'.
    **/
    public void setPeakKWCapacity(int peakKWCapacity)
    {
        this._peakKWCapacity = peakKWCapacity;
        this._has_peakKWCapacity = true;
    } //-- void setPeakKWCapacity(int) 

    /**
     * Sets the value of field 'startDelaySeconds'.
     * 
     * @param startDelaySeconds the value of field
     * 'startDelaySeconds'.
    **/
    public void setStartDelaySeconds(int startDelaySeconds)
    {
        this._startDelaySeconds = startDelaySeconds;
        this._has_startDelaySeconds = true;
    } //-- void setStartDelaySeconds(int) 

    /**
     * Sets the value of field 'transferSwitchManufacturer'.
     * 
     * @param transferSwitchManufacturer the value of field
     * 'transferSwitchManufacturer'.
    **/
    public void setTransferSwitchManufacturer(TransferSwitchManufacturer transferSwitchManufacturer)
    {
        this._transferSwitchManufacturer = transferSwitchManufacturer;
    } //-- void setTransferSwitchManufacturer(TransferSwitchManufacturer) 

    /**
     * Sets the value of field 'transferSwitchType'.
     * 
     * @param transferSwitchType the value of field
     * 'transferSwitchType'.
    **/
    public void setTransferSwitchType(TransferSwitchType transferSwitchType)
    {
        this._transferSwitchType = transferSwitchType;
    } //-- void setTransferSwitchType(TransferSwitchType) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.Generator unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.Generator) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.Generator.class, reader);
    } //-- com.cannontech.stars.xml.serialize.Generator unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
