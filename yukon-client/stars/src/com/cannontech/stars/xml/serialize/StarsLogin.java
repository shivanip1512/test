/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsLogin.java,v 1.34 2003/04/04 18:02:36 zyao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import com.cannontech.stars.xml.serialize.types.StarsLoginType;
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
 * @version $Revision: 1.34 $ $Date: 2003/04/04 18:02:36 $
**/
public class StarsLogin extends com.cannontech.stars.xml.serialize.StarsUsr 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private com.cannontech.stars.xml.serialize.types.StarsLoginType _loginType;


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
     * Returns the value of field 'loginType'.
     * 
     * @return the value of field 'loginType'.
    **/
    public com.cannontech.stars.xml.serialize.types.StarsLoginType getLoginType()
    {
        return this._loginType;
    } //-- com.cannontech.stars.xml.serialize.types.StarsLoginType getLoginType() 

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
     * Sets the value of field 'loginType'.
     * 
     * @param loginType the value of field 'loginType'.
    **/
    public void setLoginType(com.cannontech.stars.xml.serialize.types.StarsLoginType loginType)
    {
        this._loginType = loginType;
    } //-- void setLoginType(com.cannontech.stars.xml.serialize.types.StarsLoginType) 

    /**
     * 
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
