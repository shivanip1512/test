/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsFailure.java,v 1.88 2004/10/26 21:15:52 zyao Exp $
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
 * @version $Revision: 1.88 $ $Date: 2004/10/26 21:15:52 $
**/
public class StarsFailure implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _statusCode;

    /**
     * keeps track of state for field: _statusCode
    **/
    private boolean _has_statusCode;

    private java.lang.String _description;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsFailure() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsFailure()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteStatusCode()
    {
        this._has_statusCode= false;
    } //-- void deleteStatusCode() 

    /**
     * Returns the value of field 'description'.
     * 
     * @return the value of field 'description'.
    **/
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
     * Returns the value of field 'statusCode'.
     * 
     * @return the value of field 'statusCode'.
    **/
    public int getStatusCode()
    {
        return this._statusCode;
    } //-- int getStatusCode() 

    /**
    **/
    public boolean hasStatusCode()
    {
        return this._has_statusCode;
    } //-- boolean hasStatusCode() 

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
     * Sets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
    **/
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * Sets the value of field 'statusCode'.
     * 
     * @param statusCode the value of field 'statusCode'.
    **/
    public void setStatusCode(int statusCode)
    {
        this._statusCode = statusCode;
        this._has_statusCode = true;
    } //-- void setStatusCode(int) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsFailure unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsFailure) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsFailure.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsFailure unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
