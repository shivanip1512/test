/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsInv.java,v 1.16 2004/06/02 16:30:18 zyao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Date;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision: 1.16 $ $Date: 2004/06/02 16:30:18 $
**/
public abstract class StarsInv implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private int _deviceID;

    /**
     * keeps track of state for field: _deviceID
    **/
    private boolean _has_deviceID;

    private java.lang.String _category;

    private DeviceType _deviceType;

    private java.lang.String _deviceLabel;

    private InstallationCompany _installationCompany;

    private java.util.Date _receiveDate;

    private java.util.Date _installDate;

    private java.util.Date _removeDate;

    private java.lang.String _altTrackingNumber;

    private Voltage _voltage;

    private java.lang.String _notes;

    private DeviceStatus _deviceStatus;

    private java.lang.String _installationNotes;

    private StarsLMHardwareHistory _starsLMHardwareHistory;

    private LMHardware _LMHardware;

    private MCT _MCT;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsInv() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsInv()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteDeviceID()
    {
        this._has_deviceID= false;
    } //-- void deleteDeviceID() 

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
     * Returns the value of field 'altTrackingNumber'.
     * 
     * @return the value of field 'altTrackingNumber'.
    **/
    public java.lang.String getAltTrackingNumber()
    {
        return this._altTrackingNumber;
    } //-- java.lang.String getAltTrackingNumber() 

    /**
     * Returns the value of field 'category'.
     * 
     * @return the value of field 'category'.
    **/
    public java.lang.String getCategory()
    {
        return this._category;
    } //-- java.lang.String getCategory() 

    /**
     * Returns the value of field 'deviceID'.
     * 
     * @return the value of field 'deviceID'.
    **/
    public int getDeviceID()
    {
        return this._deviceID;
    } //-- int getDeviceID() 

    /**
     * Returns the value of field 'deviceLabel'.
     * 
     * @return the value of field 'deviceLabel'.
    **/
    public java.lang.String getDeviceLabel()
    {
        return this._deviceLabel;
    } //-- java.lang.String getDeviceLabel() 

    /**
     * Returns the value of field 'deviceStatus'.
     * 
     * @return the value of field 'deviceStatus'.
    **/
    public DeviceStatus getDeviceStatus()
    {
        return this._deviceStatus;
    } //-- DeviceStatus getDeviceStatus() 

    /**
     * Returns the value of field 'deviceType'.
     * 
     * @return the value of field 'deviceType'.
    **/
    public DeviceType getDeviceType()
    {
        return this._deviceType;
    } //-- DeviceType getDeviceType() 

    /**
     * Returns the value of field 'installDate'.
     * 
     * @return the value of field 'installDate'.
    **/
    public java.util.Date getInstallDate()
    {
        return this._installDate;
    } //-- java.util.Date getInstallDate() 

    /**
     * Returns the value of field 'installationCompany'.
     * 
     * @return the value of field 'installationCompany'.
    **/
    public InstallationCompany getInstallationCompany()
    {
        return this._installationCompany;
    } //-- InstallationCompany getInstallationCompany() 

    /**
     * Returns the value of field 'installationNotes'.
     * 
     * @return the value of field 'installationNotes'.
    **/
    public java.lang.String getInstallationNotes()
    {
        return this._installationNotes;
    } //-- java.lang.String getInstallationNotes() 

    /**
     * Returns the value of field 'inventoryID'.
     * 
     * @return the value of field 'inventoryID'.
    **/
    public int getInventoryID()
    {
        return this._inventoryID;
    } //-- int getInventoryID() 

    /**
     * Returns the value of field 'LMHardware'.
     * 
     * @return the value of field 'LMHardware'.
    **/
    public LMHardware getLMHardware()
    {
        return this._LMHardware;
    } //-- LMHardware getLMHardware() 

    /**
     * Returns the value of field 'MCT'.
     * 
     * @return the value of field 'MCT'.
    **/
    public MCT getMCT()
    {
        return this._MCT;
    } //-- MCT getMCT() 

    /**
     * Returns the value of field 'notes'.
     * 
     * @return the value of field 'notes'.
    **/
    public java.lang.String getNotes()
    {
        return this._notes;
    } //-- java.lang.String getNotes() 

    /**
     * Returns the value of field 'receiveDate'.
     * 
     * @return the value of field 'receiveDate'.
    **/
    public java.util.Date getReceiveDate()
    {
        return this._receiveDate;
    } //-- java.util.Date getReceiveDate() 

    /**
     * Returns the value of field 'removeDate'.
     * 
     * @return the value of field 'removeDate'.
    **/
    public java.util.Date getRemoveDate()
    {
        return this._removeDate;
    } //-- java.util.Date getRemoveDate() 

    /**
     * Returns the value of field 'starsLMHardwareHistory'.
     * 
     * @return the value of field 'starsLMHardwareHistory'.
    **/
    public StarsLMHardwareHistory getStarsLMHardwareHistory()
    {
        return this._starsLMHardwareHistory;
    } //-- StarsLMHardwareHistory getStarsLMHardwareHistory() 

    /**
     * Returns the value of field 'voltage'.
     * 
     * @return the value of field 'voltage'.
    **/
    public Voltage getVoltage()
    {
        return this._voltage;
    } //-- Voltage getVoltage() 

    /**
    **/
    public boolean hasDeviceID()
    {
        return this._has_deviceID;
    } //-- boolean hasDeviceID() 

    /**
    **/
    public boolean hasInventoryID()
    {
        return this._has_inventoryID;
    } //-- boolean hasInventoryID() 

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
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * Sets the value of field 'altTrackingNumber'.
     * 
     * @param altTrackingNumber the value of field
     * 'altTrackingNumber'.
    **/
    public void setAltTrackingNumber(java.lang.String altTrackingNumber)
    {
        this._altTrackingNumber = altTrackingNumber;
    } //-- void setAltTrackingNumber(java.lang.String) 

    /**
     * Sets the value of field 'category'.
     * 
     * @param category the value of field 'category'.
    **/
    public void setCategory(java.lang.String category)
    {
        this._category = category;
    } //-- void setCategory(java.lang.String) 

    /**
     * Sets the value of field 'deviceID'.
     * 
     * @param deviceID the value of field 'deviceID'.
    **/
    public void setDeviceID(int deviceID)
    {
        this._deviceID = deviceID;
        this._has_deviceID = true;
    } //-- void setDeviceID(int) 

    /**
     * Sets the value of field 'deviceLabel'.
     * 
     * @param deviceLabel the value of field 'deviceLabel'.
    **/
    public void setDeviceLabel(java.lang.String deviceLabel)
    {
        this._deviceLabel = deviceLabel;
    } //-- void setDeviceLabel(java.lang.String) 

    /**
     * Sets the value of field 'deviceStatus'.
     * 
     * @param deviceStatus the value of field 'deviceStatus'.
    **/
    public void setDeviceStatus(DeviceStatus deviceStatus)
    {
        this._deviceStatus = deviceStatus;
    } //-- void setDeviceStatus(DeviceStatus) 

    /**
     * Sets the value of field 'deviceType'.
     * 
     * @param deviceType the value of field 'deviceType'.
    **/
    public void setDeviceType(DeviceType deviceType)
    {
        this._deviceType = deviceType;
    } //-- void setDeviceType(DeviceType) 

    /**
     * Sets the value of field 'installDate'.
     * 
     * @param installDate the value of field 'installDate'.
    **/
    public void setInstallDate(java.util.Date installDate)
    {
        this._installDate = installDate;
    } //-- void setInstallDate(java.util.Date) 

    /**
     * Sets the value of field 'installationCompany'.
     * 
     * @param installationCompany the value of field
     * 'installationCompany'.
    **/
    public void setInstallationCompany(InstallationCompany installationCompany)
    {
        this._installationCompany = installationCompany;
    } //-- void setInstallationCompany(InstallationCompany) 

    /**
     * Sets the value of field 'installationNotes'.
     * 
     * @param installationNotes the value of field
     * 'installationNotes'.
    **/
    public void setInstallationNotes(java.lang.String installationNotes)
    {
        this._installationNotes = installationNotes;
    } //-- void setInstallationNotes(java.lang.String) 

    /**
     * Sets the value of field 'inventoryID'.
     * 
     * @param inventoryID the value of field 'inventoryID'.
    **/
    public void setInventoryID(int inventoryID)
    {
        this._inventoryID = inventoryID;
        this._has_inventoryID = true;
    } //-- void setInventoryID(int) 

    /**
     * Sets the value of field 'LMHardware'.
     * 
     * @param LMHardware the value of field 'LMHardware'.
    **/
    public void setLMHardware(LMHardware LMHardware)
    {
        this._LMHardware = LMHardware;
    } //-- void setLMHardware(LMHardware) 

    /**
     * Sets the value of field 'MCT'.
     * 
     * @param MCT the value of field 'MCT'.
    **/
    public void setMCT(MCT MCT)
    {
        this._MCT = MCT;
    } //-- void setMCT(MCT) 

    /**
     * Sets the value of field 'notes'.
     * 
     * @param notes the value of field 'notes'.
    **/
    public void setNotes(java.lang.String notes)
    {
        this._notes = notes;
    } //-- void setNotes(java.lang.String) 

    /**
     * Sets the value of field 'receiveDate'.
     * 
     * @param receiveDate the value of field 'receiveDate'.
    **/
    public void setReceiveDate(java.util.Date receiveDate)
    {
        this._receiveDate = receiveDate;
    } //-- void setReceiveDate(java.util.Date) 

    /**
     * Sets the value of field 'removeDate'.
     * 
     * @param removeDate the value of field 'removeDate'.
    **/
    public void setRemoveDate(java.util.Date removeDate)
    {
        this._removeDate = removeDate;
    } //-- void setRemoveDate(java.util.Date) 

    /**
     * Sets the value of field 'starsLMHardwareHistory'.
     * 
     * @param starsLMHardwareHistory the value of field
     * 'starsLMHardwareHistory'.
    **/
    public void setStarsLMHardwareHistory(StarsLMHardwareHistory starsLMHardwareHistory)
    {
        this._starsLMHardwareHistory = starsLMHardwareHistory;
    } //-- void setStarsLMHardwareHistory(StarsLMHardwareHistory) 

    /**
     * Sets the value of field 'voltage'.
     * 
     * @param voltage the value of field 'voltage'.
    **/
    public void setVoltage(Voltage voltage)
    {
        this._voltage = voltage;
    } //-- void setVoltage(Voltage) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
