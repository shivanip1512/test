/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id: StarsInventory.java,v 1.1 2002/07/16 19:50:06 Yao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * 
 * @version $Revision: 1.1 $ $Date: 2002/07/16 19:50:06 $
**/
public abstract class StarsInventory implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private java.lang.String _category;

    private java.lang.String _installationCompany;

    private org.exolab.castor.types.Date _receiveDate;

    private org.exolab.castor.types.Date _installDate;

    private org.exolab.castor.types.Date _removeDate;

    private java.lang.String _altTrackingNumber;

    private java.lang.String _voltage;

    private java.lang.String _notes;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsInventory() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsInventory()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
    **/
    public java.lang.String getAltTrackingNumber()
    {
        return this._altTrackingNumber;
    } //-- java.lang.String getAltTrackingNumber() 

    /**
    **/
    public java.lang.String getCategory()
    {
        return this._category;
    } //-- java.lang.String getCategory() 

    /**
    **/
    public org.exolab.castor.types.Date getInstallDate()
    {
        return this._installDate;
    } //-- org.exolab.castor.types.Date getInstallDate() 

    /**
    **/
    public java.lang.String getInstallationCompany()
    {
        return this._installationCompany;
    } //-- java.lang.String getInstallationCompany() 

    /**
    **/
    public int getInventoryID()
    {
        return this._inventoryID;
    } //-- int getInventoryID() 

    /**
    **/
    public java.lang.String getNotes()
    {
        return this._notes;
    } //-- java.lang.String getNotes() 

    /**
    **/
    public org.exolab.castor.types.Date getReceiveDate()
    {
        return this._receiveDate;
    } //-- org.exolab.castor.types.Date getReceiveDate() 

    /**
    **/
    public org.exolab.castor.types.Date getRemoveDate()
    {
        return this._removeDate;
    } //-- org.exolab.castor.types.Date getRemoveDate() 

    /**
    **/
    public java.lang.String getVoltage()
    {
        return this._voltage;
    } //-- java.lang.String getVoltage() 

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
     * @param out
    **/
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.DocumentHandler handler)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * @param altTrackingNumber
    **/
    public void setAltTrackingNumber(java.lang.String altTrackingNumber)
    {
        this._altTrackingNumber = altTrackingNumber;
    } //-- void setAltTrackingNumber(java.lang.String) 

    /**
     * 
     * @param category
    **/
    public void setCategory(java.lang.String category)
    {
        this._category = category;
    } //-- void setCategory(java.lang.String) 

    /**
     * 
     * @param installDate
    **/
    public void setInstallDate(org.exolab.castor.types.Date installDate)
    {
        this._installDate = installDate;
    } //-- void setInstallDate(org.exolab.castor.types.Date) 

    /**
     * 
     * @param installationCompany
    **/
    public void setInstallationCompany(java.lang.String installationCompany)
    {
        this._installationCompany = installationCompany;
    } //-- void setInstallationCompany(java.lang.String) 

    /**
     * 
     * @param inventoryID
    **/
    public void setInventoryID(int inventoryID)
    {
        this._inventoryID = inventoryID;
        this._has_inventoryID = true;
    } //-- void setInventoryID(int) 

    /**
     * 
     * @param notes
    **/
    public void setNotes(java.lang.String notes)
    {
        this._notes = notes;
    } //-- void setNotes(java.lang.String) 

    /**
     * 
     * @param receiveDate
    **/
    public void setReceiveDate(org.exolab.castor.types.Date receiveDate)
    {
        this._receiveDate = receiveDate;
    } //-- void setReceiveDate(org.exolab.castor.types.Date) 

    /**
     * 
     * @param removeDate
    **/
    public void setRemoveDate(org.exolab.castor.types.Date removeDate)
    {
        this._removeDate = removeDate;
    } //-- void setRemoveDate(org.exolab.castor.types.Date) 

    /**
     * 
     * @param voltage
    **/
    public void setVoltage(java.lang.String voltage)
    {
        this._voltage = voltage;
    } //-- void setVoltage(java.lang.String) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
