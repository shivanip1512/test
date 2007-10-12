/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cannontech.common.device.definition.model.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.ArrayList;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class DeviceDefinitions.
 * 
 * @version $Revision$ $Date$
 */
public class DeviceDefinitions implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _deviceList
     */
    private java.util.ArrayList _deviceList;


      //----------------/
     //- Constructors -/
    //----------------/

    public DeviceDefinitions() 
     {
        super();
        _deviceList = new ArrayList();
    } //-- com.cannontech.common.device.definition.model.castor.DeviceDefinitions()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addDevice
     * 
     * 
     * 
     * @param vDevice
     */
    public void addDevice(com.cannontech.common.device.definition.model.castor.Device vDevice)
        throws java.lang.IndexOutOfBoundsException
    {
        _deviceList.add(vDevice);
    } //-- void addDevice(com.cannontech.common.device.definition.model.castor.Device) 

    /**
     * Method addDevice
     * 
     * 
     * 
     * @param index
     * @param vDevice
     */
    public void addDevice(int index, com.cannontech.common.device.definition.model.castor.Device vDevice)
        throws java.lang.IndexOutOfBoundsException
    {
        _deviceList.add(index, vDevice);
    } //-- void addDevice(int, com.cannontech.common.device.definition.model.castor.Device) 

    /**
     * Method clearDevice
     * 
     */
    public void clearDevice()
    {
        _deviceList.clear();
    } //-- void clearDevice() 

    /**
     * Method enumerateDevice
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateDevice()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_deviceList.iterator());
    } //-- java.util.Enumeration enumerateDevice() 

    /**
     * Method getDevice
     * 
     * 
     * 
     * @param index
     * @return Device
     */
    public com.cannontech.common.device.definition.model.castor.Device getDevice(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _deviceList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.cannontech.common.device.definition.model.castor.Device) _deviceList.get(index);
    } //-- com.cannontech.common.device.definition.model.castor.Device getDevice(int) 

    /**
     * Method getDevice
     * 
     * 
     * 
     * @return Device
     */
    public com.cannontech.common.device.definition.model.castor.Device[] getDevice()
    {
        int size = _deviceList.size();
        com.cannontech.common.device.definition.model.castor.Device[] mArray = new com.cannontech.common.device.definition.model.castor.Device[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.cannontech.common.device.definition.model.castor.Device) _deviceList.get(index);
        }
        return mArray;
    } //-- com.cannontech.common.device.definition.model.castor.Device[] getDevice() 

    /**
     * Method getDeviceCount
     * 
     * 
     * 
     * @return int
     */
    public int getDeviceCount()
    {
        return _deviceList.size();
    } //-- int getDeviceCount() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return boolean
     */
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
     * Method marshal
     * 
     * 
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Method removeDevice
     * 
     * 
     * 
     * @param vDevice
     * @return boolean
     */
    public boolean removeDevice(com.cannontech.common.device.definition.model.castor.Device vDevice)
    {
        boolean removed = _deviceList.remove(vDevice);
        return removed;
    } //-- boolean removeDevice(com.cannontech.common.device.definition.model.castor.Device) 

    /**
     * Method setDevice
     * 
     * 
     * 
     * @param index
     * @param vDevice
     */
    public void setDevice(int index, com.cannontech.common.device.definition.model.castor.Device vDevice)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _deviceList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _deviceList.set(index, vDevice);
    } //-- void setDevice(int, com.cannontech.common.device.definition.model.castor.Device) 

    /**
     * Method setDevice
     * 
     * 
     * 
     * @param deviceArray
     */
    public void setDevice(com.cannontech.common.device.definition.model.castor.Device[] deviceArray)
    {
        //-- copy array
        _deviceList.clear();
        for (int i = 0; i < deviceArray.length; i++) {
            _deviceList.add(deviceArray[i]);
        }
    } //-- void setDevice(com.cannontech.common.device.definition.model.castor.Device) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return Object
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.common.device.definition.model.castor.DeviceDefinitions) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.DeviceDefinitions.class, reader);
    } //-- java.lang.Object unmarshal(java.io.Reader) 

    /**
     * Method validate
     * 
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
