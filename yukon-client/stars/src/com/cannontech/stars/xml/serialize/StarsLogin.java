/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id: StarsLogin.java,v 1.1 2002/07/16 19:50:07 Yao Exp $
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
 * Login to the STARS server
 * @version $Revision: 1.1 $ $Date: 2002/07/16 19:50:07 $
**/
public class StarsLogin implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _username;

    private java.lang.String _password;

    private java.lang.String _dbAlias;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLogin() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsLogin()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getDbAlias()
    {
        return this._dbAlias;
    } //-- java.lang.String getDbAlias() 

    /**
    **/
    public java.lang.String getPassword()
    {
        return this._password;
    } //-- java.lang.String getPassword() 

    /**
    **/
    public java.lang.String getUsername()
    {
        return this._username;
    } //-- java.lang.String getUsername() 

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
     * @param dbAlias
    **/
    public void setDbAlias(java.lang.String dbAlias)
    {
        this._dbAlias = dbAlias;
    } //-- void setDbAlias(java.lang.String) 

    /**
     * 
     * @param password
    **/
    public void setPassword(java.lang.String password)
    {
        this._password = password;
    } //-- void setPassword(java.lang.String) 

    /**
     * 
     * @param username
    **/
    public void setUsername(java.lang.String username)
    {
        this._username = username;
    } //-- void setUsername(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsLogin unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsLogin) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsLogin.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsLogin unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
