/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsSiteInformation.java,v 1.5 2002/09/06 22:37:23 zyao Exp $
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
 * @version $Revision: 1.5 $ $Date: 2002/09/06 22:37:23 $
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
     * Returns the value of field 'substationName'.
     * 
     * @return the value of field 'substationName'.
    **/
    public java.lang.String getSubstationName()
    {
        return this._substationName;
    } //-- java.lang.String getSubstationName() 

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
     * Sets the value of field 'substationName'.
     * 
     * @param substationName the value of field 'substationName'.
    **/
    public void setSubstationName(java.lang.String substationName)
    {
        this._substationName = substationName;
    } //-- void setSubstationName(java.lang.String) 

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
