/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.honeywell.serialize;

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
 * @version $Revision$ $Date$
**/
public class LMAppliance implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _id;

    private int _rowData;

    /**
     * keeps track of state for field: _rowData
    **/
    private boolean _has_rowData;

    private java.lang.String _APPLIANCE_ID;

    private java.lang.String _INVENTORYID;

    private java.lang.String _DESCRIPTION;

    private java.lang.String _CATEGORY;

    private java.lang.String _NOTES;

    private java.lang.String _TYPE_CD;


      //----------------/
     //- Constructors -/
    //----------------/

    public LMAppliance() {
        super();
    } //-- com.cannontech.stars.honeywell.serialize.LMAppliance()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteRowData()
    {
        this._has_rowData= false;
    } //-- void deleteRowData() 

    /**
    **/
    public java.lang.String getAPPLIANCE_ID()
    {
        return this._APPLIANCE_ID;
    } //-- java.lang.String getAPPLIANCE_ID() 

    /**
    **/
    public java.lang.String getCATEGORY()
    {
        return this._CATEGORY;
    } //-- java.lang.String getCATEGORY() 

    /**
    **/
    public java.lang.String getDESCRIPTION()
    {
        return this._DESCRIPTION;
    } //-- java.lang.String getDESCRIPTION() 

    /**
    **/
    public java.lang.String getINVENTORYID()
    {
        return this._INVENTORYID;
    } //-- java.lang.String getINVENTORYID() 

    /**
    **/
    public java.lang.String getId()
    {
        return this._id;
    } //-- java.lang.String getId() 

    /**
    **/
    public java.lang.String getNOTES()
    {
        return this._NOTES;
    } //-- java.lang.String getNOTES() 

    /**
    **/
    public int getRowData()
    {
        return this._rowData;
    } //-- int getRowData() 

    /**
    **/
    public java.lang.String getTYPE_CD()
    {
        return this._TYPE_CD;
    } //-- java.lang.String getTYPE_CD() 

    /**
    **/
    public boolean hasRowData()
    {
        return this._has_rowData;
    } //-- boolean hasRowData() 

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
     * @param APPLIANCE_ID
    **/
    public void setAPPLIANCE_ID(java.lang.String APPLIANCE_ID)
    {
        this._APPLIANCE_ID = APPLIANCE_ID;
    } //-- void setAPPLIANCE_ID(java.lang.String) 

    /**
     * 
     * @param CATEGORY
    **/
    public void setCATEGORY(java.lang.String CATEGORY)
    {
        this._CATEGORY = CATEGORY;
    } //-- void setCATEGORY(java.lang.String) 

    /**
     * 
     * @param DESCRIPTION
    **/
    public void setDESCRIPTION(java.lang.String DESCRIPTION)
    {
        this._DESCRIPTION = DESCRIPTION;
    } //-- void setDESCRIPTION(java.lang.String) 

    /**
     * 
     * @param INVENTORYID
    **/
    public void setINVENTORYID(java.lang.String INVENTORYID)
    {
        this._INVENTORYID = INVENTORYID;
    } //-- void setINVENTORYID(java.lang.String) 

    /**
     * 
     * @param id
    **/
    public void setId(java.lang.String id)
    {
        this._id = id;
    } //-- void setId(java.lang.String) 

    /**
     * 
     * @param NOTES
    **/
    public void setNOTES(java.lang.String NOTES)
    {
        this._NOTES = NOTES;
    } //-- void setNOTES(java.lang.String) 

    /**
     * 
     * @param rowData
    **/
    public void setRowData(int rowData)
    {
        this._rowData = rowData;
        this._has_rowData = true;
    } //-- void setRowData(int) 

    /**
     * 
     * @param TYPE_CD
    **/
    public void setTYPE_CD(java.lang.String TYPE_CD)
    {
        this._TYPE_CD = TYPE_CD;
    } //-- void setTYPE_CD(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static com.cannontech.stars.honeywell.serialize.LMAppliance unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.honeywell.serialize.LMAppliance) Unmarshaller.unmarshal(com.cannontech.stars.honeywell.serialize.LMAppliance.class, reader);
    } //-- com.cannontech.stars.honeywell.serialize.LMAppliance unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
