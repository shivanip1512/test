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
public abstract class StarsApp implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _applianceID;

    /**
     * keeps track of state for field: _applianceID
    **/
    private boolean _has_applianceID;

    private int _applianceCategoryID;

    /**
     * keeps track of state for field: _applianceCategoryID
    **/
    private boolean _has_applianceCategoryID;

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private int _lmProgramID;

    /**
     * keeps track of state for field: _lmProgramID
    **/
    private boolean _has_lmProgramID;

    private java.lang.String _categoryName;

    private java.lang.String _manufacturer;

    private java.lang.String _manufactureYear;

    private java.lang.String _location;

    private ServiceCompany _serviceCompany;

    private java.lang.String _notes;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsApp() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsApp()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteApplianceCategoryID()
    {
        this._has_applianceCategoryID= false;
    } //-- void deleteApplianceCategoryID() 

    /**
    **/
    public void deleteApplianceID()
    {
        this._has_applianceID= false;
    } //-- void deleteApplianceID() 

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
    **/
    public void deleteLmProgramID()
    {
        this._has_lmProgramID= false;
    } //-- void deleteLmProgramID() 

    /**
     * Returns the value of field 'applianceCategoryID'.
     * 
     * @return the value of field 'applianceCategoryID'.
    **/
    public int getApplianceCategoryID()
    {
        return this._applianceCategoryID;
    } //-- int getApplianceCategoryID() 

    /**
     * Returns the value of field 'applianceID'.
     * 
     * @return the value of field 'applianceID'.
    **/
    public int getApplianceID()
    {
        return this._applianceID;
    } //-- int getApplianceID() 

    /**
     * Returns the value of field 'categoryName'.
     * 
     * @return the value of field 'categoryName'.
    **/
    public java.lang.String getCategoryName()
    {
        return this._categoryName;
    } //-- java.lang.String getCategoryName() 

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
     * Returns the value of field 'lmProgramID'.
     * 
     * @return the value of field 'lmProgramID'.
    **/
    public int getLmProgramID()
    {
        return this._lmProgramID;
    } //-- int getLmProgramID() 

    /**
     * Returns the value of field 'location'.
     * 
     * @return the value of field 'location'.
    **/
    public java.lang.String getLocation()
    {
        return this._location;
    } //-- java.lang.String getLocation() 

    /**
     * Returns the value of field 'manufactureYear'.
     * 
     * @return the value of field 'manufactureYear'.
    **/
    public java.lang.String getManufactureYear()
    {
        return this._manufactureYear;
    } //-- java.lang.String getManufactureYear() 

    /**
     * Returns the value of field 'manufacturer'.
     * 
     * @return the value of field 'manufacturer'.
    **/
    public java.lang.String getManufacturer()
    {
        return this._manufacturer;
    } //-- java.lang.String getManufacturer() 

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
     * Returns the value of field 'serviceCompany'.
     * 
     * @return the value of field 'serviceCompany'.
    **/
    public ServiceCompany getServiceCompany()
    {
        return this._serviceCompany;
    } //-- ServiceCompany getServiceCompany() 

    /**
    **/
    public boolean hasApplianceCategoryID()
    {
        return this._has_applianceCategoryID;
    } //-- boolean hasApplianceCategoryID() 

    /**
    **/
    public boolean hasApplianceID()
    {
        return this._has_applianceID;
    } //-- boolean hasApplianceID() 

    /**
    **/
    public boolean hasInventoryID()
    {
        return this._has_inventoryID;
    } //-- boolean hasInventoryID() 

    /**
    **/
    public boolean hasLmProgramID()
    {
        return this._has_lmProgramID;
    } //-- boolean hasLmProgramID() 

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
     * Sets the value of field 'applianceCategoryID'.
     * 
     * @param applianceCategoryID the value of field
     * 'applianceCategoryID'.
    **/
    public void setApplianceCategoryID(int applianceCategoryID)
    {
        this._applianceCategoryID = applianceCategoryID;
        this._has_applianceCategoryID = true;
    } //-- void setApplianceCategoryID(int) 

    /**
     * Sets the value of field 'applianceID'.
     * 
     * @param applianceID the value of field 'applianceID'.
    **/
    public void setApplianceID(int applianceID)
    {
        this._applianceID = applianceID;
        this._has_applianceID = true;
    } //-- void setApplianceID(int) 

    /**
     * Sets the value of field 'categoryName'.
     * 
     * @param categoryName the value of field 'categoryName'.
    **/
    public void setCategoryName(java.lang.String categoryName)
    {
        this._categoryName = categoryName;
    } //-- void setCategoryName(java.lang.String) 

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
     * Sets the value of field 'lmProgramID'.
     * 
     * @param lmProgramID the value of field 'lmProgramID'.
    **/
    public void setLmProgramID(int lmProgramID)
    {
        this._lmProgramID = lmProgramID;
        this._has_lmProgramID = true;
    } //-- void setLmProgramID(int) 

    /**
     * Sets the value of field 'location'.
     * 
     * @param location the value of field 'location'.
    **/
    public void setLocation(java.lang.String location)
    {
        this._location = location;
    } //-- void setLocation(java.lang.String) 

    /**
     * Sets the value of field 'manufactureYear'.
     * 
     * @param manufactureYear the value of field 'manufactureYear'.
    **/
    public void setManufactureYear(java.lang.String manufactureYear)
    {
        this._manufactureYear = manufactureYear;
    } //-- void setManufactureYear(java.lang.String) 

    /**
     * Sets the value of field 'manufacturer'.
     * 
     * @param manufacturer the value of field 'manufacturer'.
    **/
    public void setManufacturer(java.lang.String manufacturer)
    {
        this._manufacturer = manufacturer;
    } //-- void setManufacturer(java.lang.String) 

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
     * Sets the value of field 'serviceCompany'.
     * 
     * @param serviceCompany the value of field 'serviceCompany'.
    **/
    public void setServiceCompany(ServiceCompany serviceCompany)
    {
        this._serviceCompany = serviceCompany;
    } //-- void setServiceCompany(ServiceCompany) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
