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
 * @version $Revision$ $Date$
**/
public class CustomerInformation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _ACCOUNTID;

    private java.lang.String _CUSTOMERNUMBER;

    private java.lang.String _ACCOUNTNUMBER;

    private java.lang.String _ACCOUNTNOTES;

    private java.lang.String _PROPERTYNUMBER;

    private java.lang.String _PROPERTYNOTES;

    private java.lang.String _STREETADDRESS1;

    private java.lang.String _STREETADDRESS2;

    private java.lang.String _STREETADDRESSCITY;

    private java.lang.String _STREETADDRESSSTATECODE;

    private java.lang.String _STREETADDRESSZIPCODE;

    private java.lang.String _SUBSTATIONNAME;

    private java.lang.String _FEEDER;

    private java.lang.String _POLE;

    private java.lang.String _TRANSFORMERSIZE;

    private java.lang.String _SERVICEVOLTAGE;

    private java.lang.String _BILLINGADDRESS1;

    private java.lang.String _BILLINGADDRESS2;

    private java.lang.String _BILLINGADDRESSCITY;

    private java.lang.String _BILLINGADDRESSSTATECODE;

    private java.lang.String _BILLINGADDRESSZIPCODE;

    private java.lang.String _ACCOUNT_ID;

    private java.lang.String _ADDRESS_ID;


      //----------------/
     //- Constructors -/
    //----------------/

    public CustomerInformation() {
        super();
    } //-- com.cannontech.stars.honeywell.serialize.CustomerInformation()


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
     * Returns the value of field 'ACCOUNTNOTES'.
     * 
     * @return the value of field 'ACCOUNTNOTES'.
    **/
    public java.lang.String getACCOUNTNOTES()
    {
        return this._ACCOUNTNOTES;
    } //-- java.lang.String getACCOUNTNOTES() 

    /**
     * Returns the value of field 'ACCOUNTNUMBER'.
     * 
     * @return the value of field 'ACCOUNTNUMBER'.
    **/
    public java.lang.String getACCOUNTNUMBER()
    {
        return this._ACCOUNTNUMBER;
    } //-- java.lang.String getACCOUNTNUMBER() 

    /**
     * Returns the value of field 'ACCOUNT_ID'.
     * 
     * @return the value of field 'ACCOUNT_ID'.
    **/
    public java.lang.String getACCOUNT_ID()
    {
        return this._ACCOUNT_ID;
    } //-- java.lang.String getACCOUNT_ID() 

    /**
     * Returns the value of field 'ADDRESS_ID'.
     * 
     * @return the value of field 'ADDRESS_ID'.
    **/
    public java.lang.String getADDRESS_ID()
    {
        return this._ADDRESS_ID;
    } //-- java.lang.String getADDRESS_ID() 

    /**
     * Returns the value of field 'BILLINGADDRESS1'.
     * 
     * @return the value of field 'BILLINGADDRESS1'.
    **/
    public java.lang.String getBILLINGADDRESS1()
    {
        return this._BILLINGADDRESS1;
    } //-- java.lang.String getBILLINGADDRESS1() 

    /**
     * Returns the value of field 'BILLINGADDRESS2'.
     * 
     * @return the value of field 'BILLINGADDRESS2'.
    **/
    public java.lang.String getBILLINGADDRESS2()
    {
        return this._BILLINGADDRESS2;
    } //-- java.lang.String getBILLINGADDRESS2() 

    /**
     * Returns the value of field 'BILLINGADDRESSCITY'.
     * 
     * @return the value of field 'BILLINGADDRESSCITY'.
    **/
    public java.lang.String getBILLINGADDRESSCITY()
    {
        return this._BILLINGADDRESSCITY;
    } //-- java.lang.String getBILLINGADDRESSCITY() 

    /**
     * Returns the value of field 'BILLINGADDRESSSTATECODE'.
     * 
     * @return the value of field 'BILLINGADDRESSSTATECODE'.
    **/
    public java.lang.String getBILLINGADDRESSSTATECODE()
    {
        return this._BILLINGADDRESSSTATECODE;
    } //-- java.lang.String getBILLINGADDRESSSTATECODE() 

    /**
     * Returns the value of field 'BILLINGADDRESSZIPCODE'.
     * 
     * @return the value of field 'BILLINGADDRESSZIPCODE'.
    **/
    public java.lang.String getBILLINGADDRESSZIPCODE()
    {
        return this._BILLINGADDRESSZIPCODE;
    } //-- java.lang.String getBILLINGADDRESSZIPCODE() 

    /**
     * Returns the value of field 'CUSTOMERNUMBER'.
     * 
     * @return the value of field 'CUSTOMERNUMBER'.
    **/
    public java.lang.String getCUSTOMERNUMBER()
    {
        return this._CUSTOMERNUMBER;
    } //-- java.lang.String getCUSTOMERNUMBER() 

    /**
     * Returns the value of field 'FEEDER'.
     * 
     * @return the value of field 'FEEDER'.
    **/
    public java.lang.String getFEEDER()
    {
        return this._FEEDER;
    } //-- java.lang.String getFEEDER() 

    /**
     * Returns the value of field 'POLE'.
     * 
     * @return the value of field 'POLE'.
    **/
    public java.lang.String getPOLE()
    {
        return this._POLE;
    } //-- java.lang.String getPOLE() 

    /**
     * Returns the value of field 'PROPERTYNOTES'.
     * 
     * @return the value of field 'PROPERTYNOTES'.
    **/
    public java.lang.String getPROPERTYNOTES()
    {
        return this._PROPERTYNOTES;
    } //-- java.lang.String getPROPERTYNOTES() 

    /**
     * Returns the value of field 'PROPERTYNUMBER'.
     * 
     * @return the value of field 'PROPERTYNUMBER'.
    **/
    public java.lang.String getPROPERTYNUMBER()
    {
        return this._PROPERTYNUMBER;
    } //-- java.lang.String getPROPERTYNUMBER() 

    /**
     * Returns the value of field 'SERVICEVOLTAGE'.
     * 
     * @return the value of field 'SERVICEVOLTAGE'.
    **/
    public java.lang.String getSERVICEVOLTAGE()
    {
        return this._SERVICEVOLTAGE;
    } //-- java.lang.String getSERVICEVOLTAGE() 

    /**
     * Returns the value of field 'STREETADDRESS1'.
     * 
     * @return the value of field 'STREETADDRESS1'.
    **/
    public java.lang.String getSTREETADDRESS1()
    {
        return this._STREETADDRESS1;
    } //-- java.lang.String getSTREETADDRESS1() 

    /**
     * Returns the value of field 'STREETADDRESS2'.
     * 
     * @return the value of field 'STREETADDRESS2'.
    **/
    public java.lang.String getSTREETADDRESS2()
    {
        return this._STREETADDRESS2;
    } //-- java.lang.String getSTREETADDRESS2() 

    /**
     * Returns the value of field 'STREETADDRESSCITY'.
     * 
     * @return the value of field 'STREETADDRESSCITY'.
    **/
    public java.lang.String getSTREETADDRESSCITY()
    {
        return this._STREETADDRESSCITY;
    } //-- java.lang.String getSTREETADDRESSCITY() 

    /**
     * Returns the value of field 'STREETADDRESSSTATECODE'.
     * 
     * @return the value of field 'STREETADDRESSSTATECODE'.
    **/
    public java.lang.String getSTREETADDRESSSTATECODE()
    {
        return this._STREETADDRESSSTATECODE;
    } //-- java.lang.String getSTREETADDRESSSTATECODE() 

    /**
     * Returns the value of field 'STREETADDRESSZIPCODE'.
     * 
     * @return the value of field 'STREETADDRESSZIPCODE'.
    **/
    public java.lang.String getSTREETADDRESSZIPCODE()
    {
        return this._STREETADDRESSZIPCODE;
    } //-- java.lang.String getSTREETADDRESSZIPCODE() 

    /**
     * Returns the value of field 'SUBSTATIONNAME'.
     * 
     * @return the value of field 'SUBSTATIONNAME'.
    **/
    public java.lang.String getSUBSTATIONNAME()
    {
        return this._SUBSTATIONNAME;
    } //-- java.lang.String getSUBSTATIONNAME() 

    /**
     * Returns the value of field 'TRANSFORMERSIZE'.
     * 
     * @return the value of field 'TRANSFORMERSIZE'.
    **/
    public java.lang.String getTRANSFORMERSIZE()
    {
        return this._TRANSFORMERSIZE;
    } //-- java.lang.String getTRANSFORMERSIZE() 

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
     * Sets the value of field 'ACCOUNTNOTES'.
     * 
     * @param ACCOUNTNOTES the value of field 'ACCOUNTNOTES'.
    **/
    public void setACCOUNTNOTES(java.lang.String ACCOUNTNOTES)
    {
        this._ACCOUNTNOTES = ACCOUNTNOTES;
    } //-- void setACCOUNTNOTES(java.lang.String) 

    /**
     * Sets the value of field 'ACCOUNTNUMBER'.
     * 
     * @param ACCOUNTNUMBER the value of field 'ACCOUNTNUMBER'.
    **/
    public void setACCOUNTNUMBER(java.lang.String ACCOUNTNUMBER)
    {
        this._ACCOUNTNUMBER = ACCOUNTNUMBER;
    } //-- void setACCOUNTNUMBER(java.lang.String) 

    /**
     * Sets the value of field 'ACCOUNT_ID'.
     * 
     * @param ACCOUNT_ID the value of field 'ACCOUNT_ID'.
    **/
    public void setACCOUNT_ID(java.lang.String ACCOUNT_ID)
    {
        this._ACCOUNT_ID = ACCOUNT_ID;
    } //-- void setACCOUNT_ID(java.lang.String) 

    /**
     * Sets the value of field 'ADDRESS_ID'.
     * 
     * @param ADDRESS_ID the value of field 'ADDRESS_ID'.
    **/
    public void setADDRESS_ID(java.lang.String ADDRESS_ID)
    {
        this._ADDRESS_ID = ADDRESS_ID;
    } //-- void setADDRESS_ID(java.lang.String) 

    /**
     * Sets the value of field 'BILLINGADDRESS1'.
     * 
     * @param BILLINGADDRESS1 the value of field 'BILLINGADDRESS1'.
    **/
    public void setBILLINGADDRESS1(java.lang.String BILLINGADDRESS1)
    {
        this._BILLINGADDRESS1 = BILLINGADDRESS1;
    } //-- void setBILLINGADDRESS1(java.lang.String) 

    /**
     * Sets the value of field 'BILLINGADDRESS2'.
     * 
     * @param BILLINGADDRESS2 the value of field 'BILLINGADDRESS2'.
    **/
    public void setBILLINGADDRESS2(java.lang.String BILLINGADDRESS2)
    {
        this._BILLINGADDRESS2 = BILLINGADDRESS2;
    } //-- void setBILLINGADDRESS2(java.lang.String) 

    /**
     * Sets the value of field 'BILLINGADDRESSCITY'.
     * 
     * @param BILLINGADDRESSCITY the value of field
     * 'BILLINGADDRESSCITY'.
    **/
    public void setBILLINGADDRESSCITY(java.lang.String BILLINGADDRESSCITY)
    {
        this._BILLINGADDRESSCITY = BILLINGADDRESSCITY;
    } //-- void setBILLINGADDRESSCITY(java.lang.String) 

    /**
     * Sets the value of field 'BILLINGADDRESSSTATECODE'.
     * 
     * @param BILLINGADDRESSSTATECODE the value of field
     * 'BILLINGADDRESSSTATECODE'.
    **/
    public void setBILLINGADDRESSSTATECODE(java.lang.String BILLINGADDRESSSTATECODE)
    {
        this._BILLINGADDRESSSTATECODE = BILLINGADDRESSSTATECODE;
    } //-- void setBILLINGADDRESSSTATECODE(java.lang.String) 

    /**
     * Sets the value of field 'BILLINGADDRESSZIPCODE'.
     * 
     * @param BILLINGADDRESSZIPCODE the value of field
     * 'BILLINGADDRESSZIPCODE'.
    **/
    public void setBILLINGADDRESSZIPCODE(java.lang.String BILLINGADDRESSZIPCODE)
    {
        this._BILLINGADDRESSZIPCODE = BILLINGADDRESSZIPCODE;
    } //-- void setBILLINGADDRESSZIPCODE(java.lang.String) 

    /**
     * Sets the value of field 'CUSTOMERNUMBER'.
     * 
     * @param CUSTOMERNUMBER the value of field 'CUSTOMERNUMBER'.
    **/
    public void setCUSTOMERNUMBER(java.lang.String CUSTOMERNUMBER)
    {
        this._CUSTOMERNUMBER = CUSTOMERNUMBER;
    } //-- void setCUSTOMERNUMBER(java.lang.String) 

    /**
     * Sets the value of field 'FEEDER'.
     * 
     * @param FEEDER the value of field 'FEEDER'.
    **/
    public void setFEEDER(java.lang.String FEEDER)
    {
        this._FEEDER = FEEDER;
    } //-- void setFEEDER(java.lang.String) 

    /**
     * Sets the value of field 'POLE'.
     * 
     * @param POLE the value of field 'POLE'.
    **/
    public void setPOLE(java.lang.String POLE)
    {
        this._POLE = POLE;
    } //-- void setPOLE(java.lang.String) 

    /**
     * Sets the value of field 'PROPERTYNOTES'.
     * 
     * @param PROPERTYNOTES the value of field 'PROPERTYNOTES'.
    **/
    public void setPROPERTYNOTES(java.lang.String PROPERTYNOTES)
    {
        this._PROPERTYNOTES = PROPERTYNOTES;
    } //-- void setPROPERTYNOTES(java.lang.String) 

    /**
     * Sets the value of field 'PROPERTYNUMBER'.
     * 
     * @param PROPERTYNUMBER the value of field 'PROPERTYNUMBER'.
    **/
    public void setPROPERTYNUMBER(java.lang.String PROPERTYNUMBER)
    {
        this._PROPERTYNUMBER = PROPERTYNUMBER;
    } //-- void setPROPERTYNUMBER(java.lang.String) 

    /**
     * Sets the value of field 'SERVICEVOLTAGE'.
     * 
     * @param SERVICEVOLTAGE the value of field 'SERVICEVOLTAGE'.
    **/
    public void setSERVICEVOLTAGE(java.lang.String SERVICEVOLTAGE)
    {
        this._SERVICEVOLTAGE = SERVICEVOLTAGE;
    } //-- void setSERVICEVOLTAGE(java.lang.String) 

    /**
     * Sets the value of field 'STREETADDRESS1'.
     * 
     * @param STREETADDRESS1 the value of field 'STREETADDRESS1'.
    **/
    public void setSTREETADDRESS1(java.lang.String STREETADDRESS1)
    {
        this._STREETADDRESS1 = STREETADDRESS1;
    } //-- void setSTREETADDRESS1(java.lang.String) 

    /**
     * Sets the value of field 'STREETADDRESS2'.
     * 
     * @param STREETADDRESS2 the value of field 'STREETADDRESS2'.
    **/
    public void setSTREETADDRESS2(java.lang.String STREETADDRESS2)
    {
        this._STREETADDRESS2 = STREETADDRESS2;
    } //-- void setSTREETADDRESS2(java.lang.String) 

    /**
     * Sets the value of field 'STREETADDRESSCITY'.
     * 
     * @param STREETADDRESSCITY the value of field
     * 'STREETADDRESSCITY'.
    **/
    public void setSTREETADDRESSCITY(java.lang.String STREETADDRESSCITY)
    {
        this._STREETADDRESSCITY = STREETADDRESSCITY;
    } //-- void setSTREETADDRESSCITY(java.lang.String) 

    /**
     * Sets the value of field 'STREETADDRESSSTATECODE'.
     * 
     * @param STREETADDRESSSTATECODE the value of field
     * 'STREETADDRESSSTATECODE'.
    **/
    public void setSTREETADDRESSSTATECODE(java.lang.String STREETADDRESSSTATECODE)
    {
        this._STREETADDRESSSTATECODE = STREETADDRESSSTATECODE;
    } //-- void setSTREETADDRESSSTATECODE(java.lang.String) 

    /**
     * Sets the value of field 'STREETADDRESSZIPCODE'.
     * 
     * @param STREETADDRESSZIPCODE the value of field
     * 'STREETADDRESSZIPCODE'.
    **/
    public void setSTREETADDRESSZIPCODE(java.lang.String STREETADDRESSZIPCODE)
    {
        this._STREETADDRESSZIPCODE = STREETADDRESSZIPCODE;
    } //-- void setSTREETADDRESSZIPCODE(java.lang.String) 

    /**
     * Sets the value of field 'SUBSTATIONNAME'.
     * 
     * @param SUBSTATIONNAME the value of field 'SUBSTATIONNAME'.
    **/
    public void setSUBSTATIONNAME(java.lang.String SUBSTATIONNAME)
    {
        this._SUBSTATIONNAME = SUBSTATIONNAME;
    } //-- void setSUBSTATIONNAME(java.lang.String) 

    /**
     * Sets the value of field 'TRANSFORMERSIZE'.
     * 
     * @param TRANSFORMERSIZE the value of field 'TRANSFORMERSIZE'.
    **/
    public void setTRANSFORMERSIZE(java.lang.String TRANSFORMERSIZE)
    {
        this._TRANSFORMERSIZE = TRANSFORMERSIZE;
    } //-- void setTRANSFORMERSIZE(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.honeywell.serialize.CustomerInformation unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.honeywell.serialize.CustomerInformation) Unmarshaller.unmarshal(com.cannontech.stars.honeywell.serialize.CustomerInformation.class, reader);
    } //-- com.cannontech.stars.honeywell.serialize.CustomerInformation unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
