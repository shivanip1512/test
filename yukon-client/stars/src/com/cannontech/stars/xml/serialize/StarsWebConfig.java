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
 * @version $Revision$ $Date$
**/
public class StarsWebConfig implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _logoLocation;

    private java.lang.String _description;

    private java.lang.String _alternateDisplayName;

    private java.lang.String _URL;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsWebConfig() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsWebConfig()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'alternateDisplayName'.
     * 
     * @return the value of field 'alternateDisplayName'.
    **/
    public java.lang.String getAlternateDisplayName()
    {
        return this._alternateDisplayName;
    } //-- java.lang.String getAlternateDisplayName() 

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
     * Returns the value of field 'logoLocation'.
     * 
     * @return the value of field 'logoLocation'.
    **/
    public java.lang.String getLogoLocation()
    {
        return this._logoLocation;
    } //-- java.lang.String getLogoLocation() 

    /**
     * Returns the value of field 'URL'.
     * 
     * @return the value of field 'URL'.
    **/
    public java.lang.String getURL()
    {
        return this._URL;
    } //-- java.lang.String getURL() 

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
     * Sets the value of field 'alternateDisplayName'.
     * 
     * @param alternateDisplayName the value of field
     * 'alternateDisplayName'.
    **/
    public void setAlternateDisplayName(java.lang.String alternateDisplayName)
    {
        this._alternateDisplayName = alternateDisplayName;
    } //-- void setAlternateDisplayName(java.lang.String) 

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
     * Sets the value of field 'logoLocation'.
     * 
     * @param logoLocation the value of field 'logoLocation'.
    **/
    public void setLogoLocation(java.lang.String logoLocation)
    {
        this._logoLocation = logoLocation;
    } //-- void setLogoLocation(java.lang.String) 

    /**
     * Sets the value of field 'URL'.
     * 
     * @param URL the value of field 'URL'.
    **/
    public void setURL(java.lang.String URL)
    {
        this._URL = URL;
    } //-- void setURL(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsWebConfig unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsWebConfig) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsWebConfig.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsWebConfig unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
