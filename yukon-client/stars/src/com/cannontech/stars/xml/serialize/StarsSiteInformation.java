/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsSiteInformation.java,v 1.63 2004/01/28 20:28:57 zyao Exp $
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
 * @version $Revision: 1.63 $ $Date: 2004/01/28 20:28:57 $
**/
public class StarsSiteInformation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _siteID;

    /**
     * keeps track of state for field: _siteID
    **/
    private boolean _has_siteID;

    private Substation _substation;

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
    public void deleteSiteID()
    {
        this._has_siteID= false;
    } //-- void deleteSiteID() 

    /**
     * Returns the value of field 'feeder'.
     * 
     * @return the value of field 'feeder'.
    **/
    public java.lang.String getFeeder()
    {
        return this._feeder;
    } //-- java.lang.String getFeeder() 

    /**
     * Returns the value of field 'pole'.
     * 
     * @return the value of field 'pole'.
    **/
    public java.lang.String getPole()
    {
        return this._pole;
    } //-- java.lang.String getPole() 

    /**
     * Returns the value of field 'serviceVoltage'.
     * 
     * @return the value of field 'serviceVoltage'.
    **/
    public java.lang.String getServiceVoltage()
    {
        return this._serviceVoltage;
    } //-- java.lang.String getServiceVoltage() 

    /**
     * Returns the value of field 'siteID'.
     * 
     * @return the value of field 'siteID'.
    **/
    public int getSiteID()
    {
        return this._siteID;
    } //-- int getSiteID() 

    /**
     * Returns the value of field 'substation'.
     * 
     * @return the value of field 'substation'.
    **/
    public Substation getSubstation()
    {
        return this._substation;
    } //-- Substation getSubstation() 

    /**
     * Returns the value of field 'transformerSize'.
     * 
     * @return the value of field 'transformerSize'.
    **/
    public java.lang.String getTransformerSize()
    {
        return this._transformerSize;
    } //-- java.lang.String getTransformerSize() 

    /**
    **/
    public boolean hasSiteID()
    {
        return this._has_siteID;
    } //-- boolean hasSiteID() 

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
     * Sets the value of field 'feeder'.
     * 
     * @param feeder the value of field 'feeder'.
    **/
    public void setFeeder(java.lang.String feeder)
    {
        this._feeder = feeder;
    } //-- void setFeeder(java.lang.String) 

    /**
     * Sets the value of field 'pole'.
     * 
     * @param pole the value of field 'pole'.
    **/
    public void setPole(java.lang.String pole)
    {
        this._pole = pole;
    } //-- void setPole(java.lang.String) 

    /**
     * Sets the value of field 'serviceVoltage'.
     * 
     * @param serviceVoltage the value of field 'serviceVoltage'.
    **/
    public void setServiceVoltage(java.lang.String serviceVoltage)
    {
        this._serviceVoltage = serviceVoltage;
    } //-- void setServiceVoltage(java.lang.String) 

    /**
     * Sets the value of field 'siteID'.
     * 
     * @param siteID the value of field 'siteID'.
    **/
    public void setSiteID(int siteID)
    {
        this._siteID = siteID;
        this._has_siteID = true;
    } //-- void setSiteID(int) 

    /**
     * Sets the value of field 'substation'.
     * 
     * @param substation the value of field 'substation'.
    **/
    public void setSubstation(Substation substation)
    {
        this._substation = substation;
    } //-- void setSubstation(Substation) 

    /**
     * Sets the value of field 'transformerSize'.
     * 
     * @param transformerSize the value of field 'transformerSize'.
    **/
    public void setTransformerSize(java.lang.String transformerSize)
    {
        this._transformerSize = transformerSize;
    } //-- void setTransformerSize(java.lang.String) 

    /**
     * 
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
