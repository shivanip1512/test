/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: SASimple.java,v 1.2 2006/06/21 17:12:19 alauinger Exp $
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
 * @version $Revision: 1.2 $ $Date: 2006/06/21 17:12:19 $
**/
public class SASimple implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _operationalAddress;


      //----------------/
     //- Constructors -/
    //----------------/

    public SASimple() {
        super();
    } //-- com.cannontech.stars.xml.serialize.SASimple()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'operationalAddress'.
     * 
     * @return the value of field 'operationalAddress'.
    **/
    public java.lang.String getOperationalAddress()
    {
        return this._operationalAddress;
    } //-- java.lang.String getOperationalAddress() 

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
     * Sets the value of field 'operationalAddress'.
     * 
     * @param operationalAddress the value of field
     * 'operationalAddress'.
    **/
    public void setOperationalAddress(java.lang.String operationalAddress)
    {
        this._operationalAddress = operationalAddress;
    } //-- void setOperationalAddress(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.SASimple unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.SASimple) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.SASimple.class, reader);
    } //-- com.cannontech.stars.xml.serialize.SASimple unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
