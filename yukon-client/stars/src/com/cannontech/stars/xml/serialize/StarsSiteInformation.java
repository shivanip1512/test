/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id: StarsSiteInformation.java,v 1.1 2002/07/16 19:50:09 Yao Exp $
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
 * 
 * @version $Revision: 1.1 $ $Date: 2002/07/16 19:50:09 $
**/
public class StarsSiteInformation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _substationName;

    private java.lang.String _feeder;

    private java.lang.String _pole;

    private java.lang.String _transformerSize;

    private java.lang.String _serviceVoltage;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSiteInformation() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsSiteInformation()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getFeeder()
    {
        return this._feeder;
    } //-- java.lang.String getFeeder() 

    /**
    **/
    public java.lang.String getPole()
    {
        return this._pole;
    } //-- java.lang.String getPole() 

    /**
    **/
    public java.lang.String getServiceVoltage()
    {
        return this._serviceVoltage;
    } //-- java.lang.String getServiceVoltage() 

    /**
    **/
    public java.lang.String getSubstationName()
    {
        return this._substationName;
    } //-- java.lang.String getSubstationName() 

    /**
    **/
    public java.lang.String getTransformerSize()
    {
        return this._transformerSize;
    } //-- java.lang.String getTransformerSize() 

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
     * @param feeder
    **/
    public void setFeeder(java.lang.String feeder)
    {
        this._feeder = feeder;
    } //-- void setFeeder(java.lang.String) 

    /**
     * 
     * @param pole
    **/
    public void setPole(java.lang.String pole)
    {
        this._pole = pole;
    } //-- void setPole(java.lang.String) 

    /**
     * 
     * @param serviceVoltage
    **/
    public void setServiceVoltage(java.lang.String serviceVoltage)
    {
        this._serviceVoltage = serviceVoltage;
    } //-- void setServiceVoltage(java.lang.String) 

    /**
     * 
     * @param substationName
    **/
    public void setSubstationName(java.lang.String substationName)
    {
        this._substationName = substationName;
    } //-- void setSubstationName(java.lang.String) 

    /**
     * 
     * @param transformerSize
    **/
    public void setTransformerSize(java.lang.String transformerSize)
    {
        this._transformerSize = transformerSize;
    } //-- void setTransformerSize(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsSiteInformation unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsSiteInformation) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsSiteInformation.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsSiteInformation unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
