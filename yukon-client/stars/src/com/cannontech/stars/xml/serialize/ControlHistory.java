/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id: ControlHistory.java,v 1.1 2002/07/16 19:50:01 Yao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Date;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * 
 * @version $Revision: 1.1 $ $Date: 2002/07/16 19:50:01 $
**/
public class ControlHistory implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _controlType;

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
    **/
    public int getControlDuration()
    {
        return this._controlDuration;
    } //-- int getControlDuration() 

    /**
    **/
    public java.lang.String getControlType()
    {
        return this._controlType;
    } //-- java.lang.String getControlType() 

    /**
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
     * @param controlDuration
    **/
    public void setControlDuration(int controlDuration)
    {
        this._controlDuration = controlDuration;
        this._has_controlDuration = true;
    } //-- void setControlDuration(int) 

    /**
     * 
     * @param controlType
    **/
    public void setControlType(java.lang.String controlType)
    {
        this._controlType = controlType;
    } //-- void setControlType(java.lang.String) 

    /**
     * 
     * @param startDateTime
    **/
    public void setStartDateTime(java.util.Date startDateTime)
    {
        this._startDateTime = startDateTime;
    } //-- void setStartDateTime(java.util.Date) 

    /**
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
