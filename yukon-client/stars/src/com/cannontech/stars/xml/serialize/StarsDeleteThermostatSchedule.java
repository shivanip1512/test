/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsDeleteThermostatSchedule.java,v 1.10 2004/07/28 22:59:11 yao Exp $
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
 * @version $Revision: 1.10 $ $Date: 2004/07/28 22:59:11 $
**/
public class StarsDeleteThermostatSchedule implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _scheduleID;

    /**
     * keeps track of state for field: _scheduleID
    **/
    private boolean _has_scheduleID;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsDeleteThermostatSchedule() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsDeleteThermostatSchedule()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteScheduleID()
    {
        this._has_scheduleID= false;
    } //-- void deleteScheduleID() 

    /**
     * Returns the value of field 'scheduleID'.
     * 
     * @return the value of field 'scheduleID'.
    **/
    public int getScheduleID()
    {
        return this._scheduleID;
    } //-- int getScheduleID() 

    /**
    **/
    public boolean hasScheduleID()
    {
        return this._has_scheduleID;
    } //-- boolean hasScheduleID() 

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
     * Sets the value of field 'scheduleID'.
     * 
     * @param scheduleID the value of field 'scheduleID'.
    **/
    public void setScheduleID(int scheduleID)
    {
        this._scheduleID = scheduleID;
        this._has_scheduleID = true;
    } //-- void setScheduleID(int) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsDeleteThermostatSchedule unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsDeleteThermostatSchedule) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsDeleteThermostatSchedule.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsDeleteThermostatSchedule unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
