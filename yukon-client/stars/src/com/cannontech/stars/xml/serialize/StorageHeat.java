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
public class StorageHeat implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StorageType _storageType;

    private int _peakKWCapacity;

    /**
     * keeps track of state for field: _peakKWCapacity
    **/
    private boolean _has_peakKWCapacity;

    private int _hoursToRecharge;

    /**
     * keeps track of state for field: _hoursToRecharge
    **/
    private boolean _has_hoursToRecharge;


      //----------------/
     //- Constructors -/
    //----------------/

    public StorageHeat() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StorageHeat()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteHoursToRecharge()
    {
        this._has_hoursToRecharge= false;
    } //-- void deleteHoursToRecharge() 

    /**
    **/
    public void deletePeakKWCapacity()
    {
        this._has_peakKWCapacity= false;
    } //-- void deletePeakKWCapacity() 

    /**
     * Returns the value of field 'hoursToRecharge'.
     * 
     * @return the value of field 'hoursToRecharge'.
    **/
    public int getHoursToRecharge()
    {
        return this._hoursToRecharge;
    } //-- int getHoursToRecharge() 

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
     * Returns the value of field 'storageType'.
     * 
     * @return the value of field 'storageType'.
    **/
    public StorageType getStorageType()
    {
        return this._storageType;
    } //-- StorageType getStorageType() 

    /**
    **/
    public boolean hasHoursToRecharge()
    {
        return this._has_hoursToRecharge;
    } //-- boolean hasHoursToRecharge() 

    /**
    **/
    public boolean hasPeakKWCapacity()
    {
        return this._has_peakKWCapacity;
    } //-- boolean hasPeakKWCapacity() 

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
     * Sets the value of field 'hoursToRecharge'.
     * 
     * @param hoursToRecharge the value of field 'hoursToRecharge'.
    **/
    public void setHoursToRecharge(int hoursToRecharge)
    {
        this._hoursToRecharge = hoursToRecharge;
        this._has_hoursToRecharge = true;
    } //-- void setHoursToRecharge(int) 

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
     * Sets the value of field 'storageType'.
     * 
     * @param storageType the value of field 'storageType'.
    **/
    public void setStorageType(StorageType storageType)
    {
        this._storageType = storageType;
    } //-- void setStorageType(StorageType) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StorageHeat unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StorageHeat) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StorageHeat.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StorageHeat unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
