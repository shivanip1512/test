/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: MCT.java,v 1.31 2004/12/14 02:15:18 zyao Exp $
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
 * @version $Revision: 1.31 $ $Date: 2004/12/14 02:15:18 $
**/
public class MCT implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _mctType;

    /**
     * keeps track of state for field: _mctType
    **/
    private boolean _has_mctType;

    private int _routeID;

    /**
     * keeps track of state for field: _routeID
    **/
    private boolean _has_routeID;

    private java.lang.String _deviceName;

    private int _physicalAddress;

    /**
     * keeps track of state for field: _physicalAddress
    **/
    private boolean _has_physicalAddress;

    private java.lang.String _meterNumber;


      //----------------/
     //- Constructors -/
    //----------------/

    public MCT() {
        super();
    } //-- com.cannontech.stars.xml.serialize.MCT()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteMctType()
    {
        this._has_mctType= false;
    } //-- void deleteMctType() 

    /**
    **/
    public void deletePhysicalAddress()
    {
        this._has_physicalAddress= false;
    } //-- void deletePhysicalAddress() 

    /**
    **/
    public void deleteRouteID()
    {
        this._has_routeID= false;
    } //-- void deleteRouteID() 

    /**
     * Returns the value of field 'deviceName'.
     * 
     * @return the value of field 'deviceName'.
    **/
    public java.lang.String getDeviceName()
    {
        return this._deviceName;
    } //-- java.lang.String getDeviceName() 

    /**
     * Returns the value of field 'mctType'.
     * 
     * @return the value of field 'mctType'.
    **/
    public int getMctType()
    {
        return this._mctType;
    } //-- int getMctType() 

    /**
     * Returns the value of field 'meterNumber'.
     * 
     * @return the value of field 'meterNumber'.
    **/
    public java.lang.String getMeterNumber()
    {
        return this._meterNumber;
    } //-- java.lang.String getMeterNumber() 

    /**
     * Returns the value of field 'physicalAddress'.
     * 
     * @return the value of field 'physicalAddress'.
    **/
    public int getPhysicalAddress()
    {
        return this._physicalAddress;
    } //-- int getPhysicalAddress() 

    /**
     * Returns the value of field 'routeID'.
     * 
     * @return the value of field 'routeID'.
    **/
    public int getRouteID()
    {
        return this._routeID;
    } //-- int getRouteID() 

    /**
    **/
    public boolean hasMctType()
    {
        return this._has_mctType;
    } //-- boolean hasMctType() 

    /**
    **/
    public boolean hasPhysicalAddress()
    {
        return this._has_physicalAddress;
    } //-- boolean hasPhysicalAddress() 

    /**
    **/
    public boolean hasRouteID()
    {
        return this._has_routeID;
    } //-- boolean hasRouteID() 

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
     * Sets the value of field 'deviceName'.
     * 
     * @param deviceName the value of field 'deviceName'.
    **/
    public void setDeviceName(java.lang.String deviceName)
    {
        this._deviceName = deviceName;
    } //-- void setDeviceName(java.lang.String) 

    /**
     * Sets the value of field 'mctType'.
     * 
     * @param mctType the value of field 'mctType'.
    **/
    public void setMctType(int mctType)
    {
        this._mctType = mctType;
        this._has_mctType = true;
    } //-- void setMctType(int) 

    /**
     * Sets the value of field 'meterNumber'.
     * 
     * @param meterNumber the value of field 'meterNumber'.
    **/
    public void setMeterNumber(java.lang.String meterNumber)
    {
        this._meterNumber = meterNumber;
    } //-- void setMeterNumber(java.lang.String) 

    /**
     * Sets the value of field 'physicalAddress'.
     * 
     * @param physicalAddress the value of field 'physicalAddress'.
    **/
    public void setPhysicalAddress(int physicalAddress)
    {
        this._physicalAddress = physicalAddress;
        this._has_physicalAddress = true;
    } //-- void setPhysicalAddress(int) 

    /**
     * Sets the value of field 'routeID'.
     * 
     * @param routeID the value of field 'routeID'.
    **/
    public void setRouteID(int routeID)
    {
        this._routeID = routeID;
        this._has_routeID = true;
    } //-- void setRouteID(int) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.MCT unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.MCT) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.MCT.class, reader);
    } //-- com.cannontech.stars.xml.serialize.MCT unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
