/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsDeleteServiceRequest.java,v 1.25 2004/06/07 16:45:38 zyao Exp $
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
 * @version $Revision: 1.25 $ $Date: 2004/06/07 16:45:38 $
**/
public class StarsDeleteServiceRequest implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _orderID;

    /**
     * keeps track of state for field: _orderID
    **/
    private boolean _has_orderID;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsDeleteServiceRequest() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsDeleteServiceRequest()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteOrderID()
    {
        this._has_orderID= false;
    } //-- void deleteOrderID() 

    /**
     * Returns the value of field 'orderID'.
     * 
     * @return the value of field 'orderID'.
    **/
    public int getOrderID()
    {
        return this._orderID;
    } //-- int getOrderID() 

    /**
    **/
    public boolean hasOrderID()
    {
        return this._has_orderID;
    } //-- boolean hasOrderID() 

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
     * Sets the value of field 'orderID'.
     * 
     * @param orderID the value of field 'orderID'.
    **/
    public void setOrderID(int orderID)
    {
        this._orderID = orderID;
        this._has_orderID = true;
    } //-- void setOrderID(int) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsDeleteServiceRequest unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsDeleteServiceRequest) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsDeleteServiceRequest.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsDeleteServiceRequest unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
