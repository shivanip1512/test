/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsAppliance.java,v 1.5 2002/09/06 22:37:20 zyao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision: 1.5 $ $Date: 2002/09/06 22:37:20 $
**/
public class StarsAppliance implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _applianceID;

    /**
     * keeps track of state for field: _applianceID
    **/
    private boolean _has_applianceID;

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

    private int _applianceCategoryID;

    /**
     * keeps track of state for field: _applianceCategoryID
    **/
    private boolean _has_applianceCategoryID;

    private java.lang.String _categoryDescription;

    private java.lang.String _notes;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsAppliance() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsAppliance()


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
     * Returns the value of field 'categoryDescription'.
     * 
     * @return the value of field 'categoryDescription'.
    **/
    public java.lang.String getCategoryDescription()
    {
        return this._categoryDescription;
    } //-- java.lang.String getCategoryDescription() 

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
     * Returns the value of field 'notes'.
     * 
     * @return the value of field 'notes'.
    **/
    public java.lang.String getNotes()
    {
        return this._notes;
    } //-- java.lang.String getNotes() 

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
     * Sets the value of field 'categoryDescription'.
     * 
     * @param categoryDescription the value of field
     * 'categoryDescription'.
    **/
    public void setCategoryDescription(java.lang.String categoryDescription)
    {
        this._categoryDescription = categoryDescription;
    } //-- void setCategoryDescription(java.lang.String) 

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
     * Sets the value of field 'notes'.
     * 
     * @param notes the value of field 'notes'.
    **/
    public void setNotes(java.lang.String notes)
    {
        this._notes = notes;
    } //-- void setNotes(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsAppliance unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsAppliance) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsAppliance.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsAppliance unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
