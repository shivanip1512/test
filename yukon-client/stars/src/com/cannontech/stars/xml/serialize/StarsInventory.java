/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsInventory.java,v 1.10 2002/09/26 22:26:27 zyao Exp $
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
 * @version $Revision: 1.10 $ $Date: 2002/09/26 22:26:27 $
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

    private java.util.Date _receiveDate;

    private java.util.Date _installDate;

    private java.util.Date _removeDate;

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
    public java.lang.String getInstallationCompany()
    {
        return this._installationCompany;
    } //-- java.lang.String getInstallationCompany() 

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
     * Returns the value of field 'voltage'.
     * 
     * @return the value of field 'voltage'.
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
    public void setInstallationCompany(java.lang.String installationCompany)
    {
        this._installationCompany = installationCompany;
    } //-- void setInstallationCompany(java.lang.String) 

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
     * Sets the value of field 'voltage'.
     * 
     * @param voltage the value of field 'voltage'.
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
