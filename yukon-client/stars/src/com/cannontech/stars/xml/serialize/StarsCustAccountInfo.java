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



/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public abstract class StarsCustAccountInfo implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsCustomerAccount _starsCustomerAccount;

    private StarsLMPrograms _starsLMPrograms;

    private StarsAppliances _starsAppliances;

    private StarsInventories _starsInventories;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustAccountInfo() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsCustAccountInfo()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsAppliances'.
     * 
     * @return the value of field 'starsAppliances'.
    **/
    public StarsAppliances getStarsAppliances()
    {
        return this._starsAppliances;
    } //-- StarsAppliances getStarsAppliances() 

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
     * Returns the value of field 'starsInventories'.
     * 
     * @return the value of field 'starsInventories'.
    **/
    public StarsInventories getStarsInventories()
    {
        return this._starsInventories;
    } //-- StarsInventories getStarsInventories() 

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
     * Sets the value of field 'starsAppliances'.
     * 
     * @param starsAppliances the value of field 'starsAppliances'.
    **/
    public void setStarsAppliances(StarsAppliances starsAppliances)
    {
        this._starsAppliances = starsAppliances;
    } //-- void setStarsAppliances(StarsAppliances) 

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
     * Sets the value of field 'starsInventories'.
     * 
     * @param starsInventories the value of field 'starsInventories'
    **/
    public void setStarsInventories(StarsInventories starsInventories)
    {
        this._starsInventories = starsInventories;
    } //-- void setStarsInventories(StarsInventories) 

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
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
