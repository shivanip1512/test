/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsSaveThermostatScheduleResponse.java,v 1.20 2006/06/21 17:12:19 alauinger Exp $
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
 * @version $Revision: 1.20 $ $Date: 2006/06/21 17:12:19 $
**/
public class StarsSaveThermostatScheduleResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsThermostatProgram _starsThermostatProgram;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSaveThermostatScheduleResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsSaveThermostatScheduleResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsThermostatProgram'.
     * 
     * @return the value of field 'starsThermostatProgram'.
    **/
    public StarsThermostatProgram getStarsThermostatProgram()
    {
        return this._starsThermostatProgram;
    } //-- StarsThermostatProgram getStarsThermostatProgram() 

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
     * Sets the value of field 'starsThermostatProgram'.
     * 
     * @param starsThermostatProgram the value of field
     * 'starsThermostatProgram'.
    **/
    public void setStarsThermostatProgram(StarsThermostatProgram starsThermostatProgram)
    {
        this._starsThermostatProgram = starsThermostatProgram;
    } //-- void setStarsThermostatProgram(StarsThermostatProgram) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsSaveThermostatScheduleResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsSaveThermostatScheduleResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsSaveThermostatScheduleResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsSaveThermostatScheduleResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
