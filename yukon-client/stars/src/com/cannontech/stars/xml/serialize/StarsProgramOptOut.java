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
public class StarsProgramOptOut implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Date _reenableDateTime;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsProgramOptOut() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsProgramOptOut()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'reenableDateTime'.
     * 
     * @return the value of field 'reenableDateTime'.
    **/
    public java.util.Date getReenableDateTime()
    {
        return this._reenableDateTime;
    } //-- java.util.Date getReenableDateTime() 

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
     * Sets the value of field 'reenableDateTime'.
     * 
     * @param reenableDateTime the value of field 'reenableDateTime'
    **/
    public void setReenableDateTime(java.util.Date reenableDateTime)
    {
        this._reenableDateTime = reenableDateTime;
    } //-- void setReenableDateTime(java.util.Date) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsProgramOptOut unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsProgramOptOut) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsProgramOptOut.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsProgramOptOut unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
