/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id: StarsAppliance.java,v 1.1 2002/07/16 19:50:02 Yao Exp $
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
 * @version $Revision: 1.1 $ $Date: 2002/07/16 19:50:02 $
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

    private StarsApplianceCategory _starsApplianceCategory;

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
    **/
    public int getApplianceID()
    {
        return this._applianceID;
    } //-- int getApplianceID() 

    /**
    **/
    public int getInventoryID()
    {
        return this._inventoryID;
    } //-- int getInventoryID() 

    /**
    **/
    public int getLmProgramID()
    {
        return this._lmProgramID;
    } //-- int getLmProgramID() 

    /**
    **/
    public java.lang.String getNotes()
    {
        return this._notes;
    } //-- java.lang.String getNotes() 

    /**
    **/
    public StarsApplianceCategory getStarsApplianceCategory()
    {
        return this._starsApplianceCategory;
    } //-- StarsApplianceCategory getStarsApplianceCategory() 

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
     * @param out
    **/
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.DocumentHandler handler)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.DocumentHandler) 

    /**
     * 
     * @param applianceID
    **/
    public void setApplianceID(int applianceID)
    {
        this._applianceID = applianceID;
        this._has_applianceID = true;
    } //-- void setApplianceID(int) 

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
     * @param lmProgramID
    **/
    public void setLmProgramID(int lmProgramID)
    {
        this._lmProgramID = lmProgramID;
        this._has_lmProgramID = true;
    } //-- void setLmProgramID(int) 

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
     * @param starsApplianceCategory
    **/
    public void setStarsApplianceCategory(StarsApplianceCategory starsApplianceCategory)
    {
        this._starsApplianceCategory = starsApplianceCategory;
    } //-- void setStarsApplianceCategory(StarsApplianceCategory) 

    /**
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
