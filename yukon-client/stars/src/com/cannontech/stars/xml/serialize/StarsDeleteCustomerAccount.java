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
public class StarsDeleteCustomerAccount implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private boolean _disableReceivers;

    /**
     * keeps track of state for field: _disableReceivers
    **/
    private boolean _has_disableReceivers;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsDeleteCustomerAccount() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsDeleteCustomerAccount()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteDisableReceivers()
    {
        this._has_disableReceivers= false;
    } //-- void deleteDisableReceivers() 

    /**
     * Returns the value of field 'disableReceivers'.
     * 
     * @return the value of field 'disableReceivers'.
    **/
    public boolean getDisableReceivers()
    {
        return this._disableReceivers;
    } //-- boolean getDisableReceivers() 

    /**
    **/
    public boolean hasDisableReceivers()
    {
        return this._has_disableReceivers;
    } //-- boolean hasDisableReceivers() 

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
     * Sets the value of field 'disableReceivers'.
     * 
     * @param disableReceivers the value of field 'disableReceivers'
    **/
    public void setDisableReceivers(boolean disableReceivers)
    {
        this._disableReceivers = disableReceivers;
        this._has_disableReceivers = true;
    } //-- void setDisableReceivers(boolean) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsDeleteCustomerAccount unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsDeleteCustomerAccount) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsDeleteCustomerAccount.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsDeleteCustomerAccount unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
