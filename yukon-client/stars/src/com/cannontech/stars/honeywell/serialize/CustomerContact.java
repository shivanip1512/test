/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.honeywell.serialize;

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
public class CustomerContact implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _ACCOUNTID;

    private java.lang.String _FIRSTNAME;

    private java.lang.String _LASTNAME;

    private java.lang.String _PHONE1;

    private java.lang.String _PHONE2;

    private java.lang.String _PERSONCOMPANY_ID;

    private java.lang.String _PHONE_ID;

    private java.lang.String _EXPR1;


      //----------------/
     //- Constructors -/
    //----------------/

    public CustomerContact() {
        super();
    } //-- com.cannontech.stars.honeywell.serialize.CustomerContact()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'ACCOUNTID'.
     * 
     * @return the value of field 'ACCOUNTID'.
    **/
    public java.lang.String getACCOUNTID()
    {
        return this._ACCOUNTID;
    } //-- java.lang.String getACCOUNTID() 

    /**
     * Returns the value of field 'EXPR1'.
     * 
     * @return the value of field 'EXPR1'.
    **/
    public java.lang.String getEXPR1()
    {
        return this._EXPR1;
    } //-- java.lang.String getEXPR1() 

    /**
     * Returns the value of field 'FIRSTNAME'.
     * 
     * @return the value of field 'FIRSTNAME'.
    **/
    public java.lang.String getFIRSTNAME()
    {
        return this._FIRSTNAME;
    } //-- java.lang.String getFIRSTNAME() 

    /**
     * Returns the value of field 'LASTNAME'.
     * 
     * @return the value of field 'LASTNAME'.
    **/
    public java.lang.String getLASTNAME()
    {
        return this._LASTNAME;
    } //-- java.lang.String getLASTNAME() 

    /**
     * Returns the value of field 'PERSONCOMPANY_ID'.
     * 
     * @return the value of field 'PERSONCOMPANY_ID'.
    **/
    public java.lang.String getPERSONCOMPANY_ID()
    {
        return this._PERSONCOMPANY_ID;
    } //-- java.lang.String getPERSONCOMPANY_ID() 

    /**
     * Returns the value of field 'PHONE1'.
     * 
     * @return the value of field 'PHONE1'.
    **/
    public java.lang.String getPHONE1()
    {
        return this._PHONE1;
    } //-- java.lang.String getPHONE1() 

    /**
     * Returns the value of field 'PHONE2'.
     * 
     * @return the value of field 'PHONE2'.
    **/
    public java.lang.String getPHONE2()
    {
        return this._PHONE2;
    } //-- java.lang.String getPHONE2() 

    /**
     * Returns the value of field 'PHONE_ID'.
     * 
     * @return the value of field 'PHONE_ID'.
    **/
    public java.lang.String getPHONE_ID()
    {
        return this._PHONE_ID;
    } //-- java.lang.String getPHONE_ID() 

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
     * Sets the value of field 'ACCOUNTID'.
     * 
     * @param ACCOUNTID the value of field 'ACCOUNTID'.
    **/
    public void setACCOUNTID(java.lang.String ACCOUNTID)
    {
        this._ACCOUNTID = ACCOUNTID;
    } //-- void setACCOUNTID(java.lang.String) 

    /**
     * Sets the value of field 'EXPR1'.
     * 
     * @param EXPR1 the value of field 'EXPR1'.
    **/
    public void setEXPR1(java.lang.String EXPR1)
    {
        this._EXPR1 = EXPR1;
    } //-- void setEXPR1(java.lang.String) 

    /**
     * Sets the value of field 'FIRSTNAME'.
     * 
     * @param FIRSTNAME the value of field 'FIRSTNAME'.
    **/
    public void setFIRSTNAME(java.lang.String FIRSTNAME)
    {
        this._FIRSTNAME = FIRSTNAME;
    } //-- void setFIRSTNAME(java.lang.String) 

    /**
     * Sets the value of field 'LASTNAME'.
     * 
     * @param LASTNAME the value of field 'LASTNAME'.
    **/
    public void setLASTNAME(java.lang.String LASTNAME)
    {
        this._LASTNAME = LASTNAME;
    } //-- void setLASTNAME(java.lang.String) 

    /**
     * Sets the value of field 'PERSONCOMPANY_ID'.
     * 
     * @param PERSONCOMPANY_ID the value of field 'PERSONCOMPANY_ID'
    **/
    public void setPERSONCOMPANY_ID(java.lang.String PERSONCOMPANY_ID)
    {
        this._PERSONCOMPANY_ID = PERSONCOMPANY_ID;
    } //-- void setPERSONCOMPANY_ID(java.lang.String) 

    /**
     * Sets the value of field 'PHONE1'.
     * 
     * @param PHONE1 the value of field 'PHONE1'.
    **/
    public void setPHONE1(java.lang.String PHONE1)
    {
        this._PHONE1 = PHONE1;
    } //-- void setPHONE1(java.lang.String) 

    /**
     * Sets the value of field 'PHONE2'.
     * 
     * @param PHONE2 the value of field 'PHONE2'.
    **/
    public void setPHONE2(java.lang.String PHONE2)
    {
        this._PHONE2 = PHONE2;
    } //-- void setPHONE2(java.lang.String) 

    /**
     * Sets the value of field 'PHONE_ID'.
     * 
     * @param PHONE_ID the value of field 'PHONE_ID'.
    **/
    public void setPHONE_ID(java.lang.String PHONE_ID)
    {
        this._PHONE_ID = PHONE_ID;
    } //-- void setPHONE_ID(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.honeywell.serialize.CustomerContact unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.honeywell.serialize.CustomerContact) Unmarshaller.unmarshal(com.cannontech.stars.honeywell.serialize.CustomerContact.class, reader);
    } //-- com.cannontech.stars.honeywell.serialize.CustomerContact unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
