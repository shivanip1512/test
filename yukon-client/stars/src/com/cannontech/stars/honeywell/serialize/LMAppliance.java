/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.honeywell.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class LMAppliance implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

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
     * Returns the value of field 'APPLIANCE_ID'.
     * 
     * @return the value of field 'APPLIANCE_ID'.
    **/
    public java.lang.String getAPPLIANCE_ID()
    {
        return this._APPLIANCE_ID;
    } //-- java.lang.String getAPPLIANCE_ID() 

    /**
     * Returns the value of field 'CATEGORY'.
     * 
     * @return the value of field 'CATEGORY'.
    **/
    public java.lang.String getCATEGORY()
    {
        return this._CATEGORY;
    } //-- java.lang.String getCATEGORY() 

    /**
     * Returns the value of field 'DESCRIPTION'.
     * 
     * @return the value of field 'DESCRIPTION'.
    **/
    public java.lang.String getDESCRIPTION()
    {
        return this._DESCRIPTION;
    } //-- java.lang.String getDESCRIPTION() 

    /**
     * Returns the value of field 'INVENTORYID'.
     * 
     * @return the value of field 'INVENTORYID'.
    **/
    public java.lang.String getINVENTORYID()
    {
        return this._INVENTORYID;
    } //-- java.lang.String getINVENTORYID() 

    /**
     * Returns the value of field 'NOTES'.
     * 
     * @return the value of field 'NOTES'.
    **/
    public java.lang.String getNOTES()
    {
        return this._NOTES;
    } //-- java.lang.String getNOTES() 

    /**
     * Returns the value of field 'TYPE_CD'.
     * 
     * @return the value of field 'TYPE_CD'.
    **/
    public java.lang.String getTYPE_CD()
    {
        return this._TYPE_CD;
    } //-- java.lang.String getTYPE_CD() 

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
     * Sets the value of field 'APPLIANCE_ID'.
     * 
     * @param APPLIANCE_ID the value of field 'APPLIANCE_ID'.
    **/
    public void setAPPLIANCE_ID(java.lang.String APPLIANCE_ID)
    {
        this._APPLIANCE_ID = APPLIANCE_ID;
    } //-- void setAPPLIANCE_ID(java.lang.String) 

    /**
     * Sets the value of field 'CATEGORY'.
     * 
     * @param CATEGORY the value of field 'CATEGORY'.
    **/
    public void setCATEGORY(java.lang.String CATEGORY)
    {
        this._CATEGORY = CATEGORY;
    } //-- void setCATEGORY(java.lang.String) 

    /**
     * Sets the value of field 'DESCRIPTION'.
     * 
     * @param DESCRIPTION the value of field 'DESCRIPTION'.
    **/
    public void setDESCRIPTION(java.lang.String DESCRIPTION)
    {
        this._DESCRIPTION = DESCRIPTION;
    } //-- void setDESCRIPTION(java.lang.String) 

    /**
     * Sets the value of field 'INVENTORYID'.
     * 
     * @param INVENTORYID the value of field 'INVENTORYID'.
    **/
    public void setINVENTORYID(java.lang.String INVENTORYID)
    {
        this._INVENTORYID = INVENTORYID;
    } //-- void setINVENTORYID(java.lang.String) 

    /**
     * Sets the value of field 'NOTES'.
     * 
     * @param NOTES the value of field 'NOTES'.
    **/
    public void setNOTES(java.lang.String NOTES)
    {
        this._NOTES = NOTES;
    } //-- void setNOTES(java.lang.String) 

    /**
     * Sets the value of field 'TYPE_CD'.
     * 
     * @param TYPE_CD the value of field 'TYPE_CD'.
    **/
    public void setTYPE_CD(java.lang.String TYPE_CD)
    {
        this._TYPE_CD = TYPE_CD;
    } //-- void setTYPE_CD(java.lang.String) 

    /**
     * 
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
