/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: ControlHistory.java,v 1.71 2004/05/04 16:10:36 zyao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Date;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision: 1.71 $ $Date: 2004/05/04 16:10:36 $
**/
public class ControlHistory implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Date _startDateTime;

    private int _controlDuration;

    /**
     * keeps track of state for field: _controlDuration
    **/
    private boolean _has_controlDuration;


      //----------------/
     //- Constructors -/
    //----------------/

    public ControlHistory() {
        super();
    } //-- com.cannontech.stars.xml.serialize.ControlHistory()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'controlDuration'.
     * 
     * @return the value of field 'controlDuration'.
    **/
    public int getControlDuration()
    {
        return this._controlDuration;
    } //-- int getControlDuration() 

    /**
     * Returns the value of field 'startDateTime'.
     * 
     * @return the value of field 'startDateTime'.
    **/
    public java.util.Date getStartDateTime()
    {
        return this._startDateTime;
    } //-- java.util.Date getStartDateTime() 

    /**
    **/
    public boolean hasControlDuration()
    {
        return this._has_controlDuration;
    } //-- boolean hasControlDuration() 

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
     * Sets the value of field 'controlDuration'.
     * 
     * @param controlDuration the value of field 'controlDuration'.
    **/
    public void setControlDuration(int controlDuration)
    {
        this._controlDuration = controlDuration;
        this._has_controlDuration = true;
    } //-- void setControlDuration(int) 

    /**
     * Sets the value of field 'startDateTime'.
     * 
     * @param startDateTime the value of field 'startDateTime'.
    **/
    public void setStartDateTime(java.util.Date startDateTime)
    {
        this._startDateTime = startDateTime;
    } //-- void setStartDateTime(java.util.Date) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.ControlHistory unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.ControlHistory) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.ControlHistory.class, reader);
    } //-- com.cannontech.stars.xml.serialize.ControlHistory unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
