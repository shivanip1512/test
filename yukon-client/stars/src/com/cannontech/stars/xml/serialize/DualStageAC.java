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
public class DualStageAC implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private Tonnage _tonnage;
    private Tonnage _stageTwoTonnage;

    private ACType _ACType;


      //----------------/
     //- Constructors -/
    //----------------/

    public DualStageAC() {
        super();
    } //-- com.cannontech.stars.xml.serialize.DualStageAC()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'ACType'.
     * 
     * @return the value of field 'ACType'.
    **/
    public ACType getACType()
    {
        return this._ACType;
    } //-- ACType getACType() 

    /**
     * Returns the value of field 'tonnage'.
     * 
     * @return the value of field 'tonnage'.
    **/
    public Tonnage getTonnage()
    {
        return this._tonnage;
    } //-- Tonnage getTonnage() 

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
     * Sets the value of field 'ACType'.
     * 
     * @param ACType the value of field 'ACType'.
    **/
    public void setACType(ACType ACType)
    {
        this._ACType = ACType;
    } //-- void setACType(ACType) 

    /**
     * Sets the value of field 'tonnage'.
     * 
     * @param tonnage the value of field 'tonnage'.
    **/
    public void setTonnage(Tonnage tonnage)
    {
        this._tonnage = tonnage;
    } //-- void setTonnage(Tonnage) 

    public Tonnage getStageTwoTonnage() {
        return _stageTwoTonnage;
    }


    public void setStageTwoTonnage(Tonnage stagetwo_tonnage) {
        _stageTwoTonnage = stagetwo_tonnage;
    }
    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.DualStageAC unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.DualStageAC) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.DualStageAC.class, reader);
    } //-- com.cannontech.stars.xml.serialize.DualStageAC unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 


}
