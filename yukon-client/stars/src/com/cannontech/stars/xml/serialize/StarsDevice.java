/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsDevice.java,v 1.4 2003/12/23 21:20:33 zyao Exp $
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
 * @version $Revision: 1.4 $ $Date: 2003/12/23 21:20:33 $
**/
public abstract class StarsDevice extends com.cannontech.stars.xml.serialize.StarsInventory 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _deviceName;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsDevice() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsDevice()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'deviceName'.
     * 
     * @return the value of field 'deviceName'.
    **/
    public java.lang.String getDeviceName()
    {
        return this._deviceName;
    } //-- java.lang.String getDeviceName() 

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
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * Sets the value of field 'deviceName'.
     * 
     * @param deviceName the value of field 'deviceName'.
    **/
    public void setDeviceName(java.lang.String deviceName)
    {
        this._deviceName = deviceName;
    } //-- void setDeviceName(java.lang.String) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
