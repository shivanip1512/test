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

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsLMHardwareConfig implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _applianceID;

    /**
     * keeps track of state for field: _applianceID
    **/
    private boolean _has_applianceID;

    private int _groupID;

    /**
     * keeps track of state for field: _groupID
    **/
    private boolean _has_groupID;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLMHardwareConfig() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsLMHardwareConfig()


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
    public void deleteGroupID()
    {
        this._has_groupID= false;
    } //-- void deleteGroupID() 

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
     * Returns the value of field 'groupID'.
     * 
     * @return the value of field 'groupID'.
    **/
    public int getGroupID()
    {
        return this._groupID;
    } //-- int getGroupID() 

    /**
    **/
    public boolean hasApplianceID()
    {
        return this._has_applianceID;
    } //-- boolean hasApplianceID() 

    /**
    **/
    public boolean hasGroupID()
    {
        return this._has_groupID;
    } //-- boolean hasGroupID() 

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
     * Sets the value of field 'groupID'.
     * 
     * @param groupID the value of field 'groupID'.
    **/
    public void setGroupID(int groupID)
    {
        this._groupID = groupID;
        this._has_groupID = true;
    } //-- void setGroupID(int) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsLMHardwareConfig unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsLMHardwareConfig) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsLMHardwareConfig.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsLMHardwareConfig unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
