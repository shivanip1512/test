/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: SASimple.java,v 1.1 2005/01/20 00:37:07 yao Exp $
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
 * @version $Revision: 1.1 $ $Date: 2005/01/20 00:37:07 $
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
