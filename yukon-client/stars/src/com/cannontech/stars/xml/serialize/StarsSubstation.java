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
public class StarsSubstation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _substationID;

    /**
     * keeps track of state for field: _substationID
    **/
    private boolean _has_substationID;

    private java.lang.String _substationName;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSubstation() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsSubstation()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteSubstationID()
    {
        this._has_substationID= false;
    } //-- void deleteSubstationID() 

    /**
     * Returns the value of field 'substationID'.
     * 
     * @return the value of field 'substationID'.
    **/
    public int getSubstationID()
    {
        return this._substationID;
    } //-- int getSubstationID() 

    /**
     * Returns the value of field 'substationName'.
     * 
     * @return the value of field 'substationName'.
    **/
    public java.lang.String getSubstationName()
    {
        return this._substationName;
    } //-- java.lang.String getSubstationName() 

    /**
    **/
    public boolean hasSubstationID()
    {
        return this._has_substationID;
    } //-- boolean hasSubstationID() 

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
     * Sets the value of field 'substationID'.
     * 
     * @param substationID the value of field 'substationID'.
    **/
    public void setSubstationID(int substationID)
    {
        this._substationID = substationID;
        this._has_substationID = true;
    } //-- void setSubstationID(int) 

    /**
     * Sets the value of field 'substationName'.
     * 
     * @param substationName the value of field 'substationName'.
    **/
    public void setSubstationName(java.lang.String substationName)
    {
        this._substationName = substationName;
    } //-- void setSubstationName(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsSubstation unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsSubstation) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsSubstation.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsSubstation unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
