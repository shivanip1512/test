/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsNewCustomerAccount.java,v 1.4 2002/08/30 18:23:16 alauinger Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Create new customer account
 * 
 * @version $Revision: 1.4 $ $Date: 2002/08/30 18:23:16 $
**/
public class StarsNewCustomerAccount implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsCustomerAccount _starsCustomerAccount;

    private StarsLMPrograms _starsLMPrograms;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsNewCustomerAccount() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsNewCustomerAccount()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsCustomerAccount'.
     * 
     * @return the value of field 'starsCustomerAccount'.
    **/
    public StarsCustomerAccount getStarsCustomerAccount()
    {
        return this._starsCustomerAccount;
    } //-- StarsCustomerAccount getStarsCustomerAccount() 

    /**
     * Returns the value of field 'starsLMPrograms'.
     * 
     * @return the value of field 'starsLMPrograms'.
    **/
    public StarsLMPrograms getStarsLMPrograms()
    {
        return this._starsLMPrograms;
    } //-- StarsLMPrograms getStarsLMPrograms() 

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
     * Sets the value of field 'starsCustomerAccount'.
     * 
     * @param starsCustomerAccount the value of field
     * 'starsCustomerAccount'.
    **/
    public void setStarsCustomerAccount(StarsCustomerAccount starsCustomerAccount)
    {
        this._starsCustomerAccount = starsCustomerAccount;
    } //-- void setStarsCustomerAccount(StarsCustomerAccount) 

    /**
     * Sets the value of field 'starsLMPrograms'.
     * 
     * @param starsLMPrograms the value of field 'starsLMPrograms'.
    **/
    public void setStarsLMPrograms(StarsLMPrograms starsLMPrograms)
    {
        this._starsLMPrograms = starsLMPrograms;
    } //-- void setStarsLMPrograms(StarsLMPrograms) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsNewCustomerAccount unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsNewCustomerAccount) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsNewCustomerAccount.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsNewCustomerAccount unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
