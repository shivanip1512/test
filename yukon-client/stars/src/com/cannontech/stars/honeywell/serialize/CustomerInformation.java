/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.honeywell.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * 
 * @version $Revision$ $Date$
**/
public class CustomerInformation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _id;

    private int _rowData;

    /**
     * keeps track of state for field: _rowData
    **/
    private boolean _has_rowData;

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
    **/
    public void deleteRowData()
    {
        this._has_rowData= false;
    } //-- void deleteRowData() 

    /**
    **/
    public java.lang.String getACCOUNTID()
    {
        return this._ACCOUNTID;
    } //-- java.lang.String getACCOUNTID() 

    /**
    **/
    public java.lang.String getACCOUNTNOTES()
    {
        return this._ACCOUNTNOTES;
    } //-- java.lang.String getACCOUNTNOTES() 

    /**
    **/
    public java.lang.String getACCOUNTNUMBER()
    {
        return this._ACCOUNTNUMBER;
    } //-- java.lang.String getACCOUNTNUMBER() 

    /**
    **/
    public java.lang.String getACCOUNT_ID()
    {
        return this._ACCOUNT_ID;
    } //-- java.lang.String getACCOUNT_ID() 

    /**
    **/
    public java.lang.String getADDRESS_ID()
    {
        return this._ADDRESS_ID;
    } //-- java.lang.String getADDRESS_ID() 

    /**
    **/
    public java.lang.String getBILLINGADDRESS1()
    {
        return this._BILLINGADDRESS1;
    } //-- java.lang.String getBILLINGADDRESS1() 

    /**
    **/
    public java.lang.String getBILLINGADDRESS2()
    {
        return this._BILLINGADDRESS2;
    } //-- java.lang.String getBILLINGADDRESS2() 

    /**
    **/
    public java.lang.String getBILLINGADDRESSCITY()
    {
        return this._BILLINGADDRESSCITY;
    } //-- java.lang.String getBILLINGADDRESSCITY() 

    /**
    **/
    public java.lang.String getBILLINGADDRESSSTATECODE()
    {
        return this._BILLINGADDRESSSTATECODE;
    } //-- java.lang.String getBILLINGADDRESSSTATECODE() 

    /**
    **/
    public java.lang.String getBILLINGADDRESSZIPCODE()
    {
        return this._BILLINGADDRESSZIPCODE;
    } //-- java.lang.String getBILLINGADDRESSZIPCODE() 

    /**
    **/
    public java.lang.String getCUSTOMERNUMBER()
    {
        return this._CUSTOMERNUMBER;
    } //-- java.lang.String getCUSTOMERNUMBER() 

    /**
    **/
    public java.lang.String getFEEDER()
    {
        return this._FEEDER;
    } //-- java.lang.String getFEEDER() 

    /**
    **/
    public java.lang.String getId()
    {
        return this._id;
    } //-- java.lang.String getId() 

    /**
    **/
    public java.lang.String getPOLE()
    {
        return this._POLE;
    } //-- java.lang.String getPOLE() 

    /**
    **/
    public java.lang.String getPROPERTYNOTES()
    {
        return this._PROPERTYNOTES;
    } //-- java.lang.String getPROPERTYNOTES() 

    /**
    **/
    public java.lang.String getPROPERTYNUMBER()
    {
        return this._PROPERTYNUMBER;
    } //-- java.lang.String getPROPERTYNUMBER() 

    /**
    **/
    public int getRowData()
    {
        return this._rowData;
    } //-- int getRowData() 

    /**
    **/
    public java.lang.String getSERVICEVOLTAGE()
    {
        return this._SERVICEVOLTAGE;
    } //-- java.lang.String getSERVICEVOLTAGE() 

    /**
    **/
    public java.lang.String getSTREETADDRESS1()
    {
        return this._STREETADDRESS1;
    } //-- java.lang.String getSTREETADDRESS1() 

    /**
    **/
    public java.lang.String getSTREETADDRESS2()
    {
        return this._STREETADDRESS2;
    } //-- java.lang.String getSTREETADDRESS2() 

    /**
    **/
    public java.lang.String getSTREETADDRESSCITY()
    {
        return this._STREETADDRESSCITY;
    } //-- java.lang.String getSTREETADDRESSCITY() 

    /**
    **/
    public java.lang.String getSTREETADDRESSSTATECODE()
    {
        return this._STREETADDRESSSTATECODE;
    } //-- java.lang.String getSTREETADDRESSSTATECODE() 

    /**
    **/
    public java.lang.String getSTREETADDRESSZIPCODE()
    {
        return this._STREETADDRESSZIPCODE;
    } //-- java.lang.String getSTREETADDRESSZIPCODE() 

    /**
    **/
    public java.lang.String getSUBSTATIONNAME()
    {
        return this._SUBSTATIONNAME;
    } //-- java.lang.String getSUBSTATIONNAME() 

    /**
    **/
    public java.lang.String getTRANSFORMERSIZE()
    {
        return this._TRANSFORMERSIZE;
    } //-- java.lang.String getTRANSFORMERSIZE() 

    /**
    **/
    public boolean hasRowData()
    {
        return this._has_rowData;
    } //-- boolean hasRowData() 

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
     * @param ACCOUNTID
    **/
    public void setACCOUNTID(java.lang.String ACCOUNTID)
    {
        this._ACCOUNTID = ACCOUNTID;
    } //-- void setACCOUNTID(java.lang.String) 

    /**
     * 
     * @param ACCOUNTNOTES
    **/
    public void setACCOUNTNOTES(java.lang.String ACCOUNTNOTES)
    {
        this._ACCOUNTNOTES = ACCOUNTNOTES;
    } //-- void setACCOUNTNOTES(java.lang.String) 

    /**
     * 
     * @param ACCOUNTNUMBER
    **/
    public void setACCOUNTNUMBER(java.lang.String ACCOUNTNUMBER)
    {
        this._ACCOUNTNUMBER = ACCOUNTNUMBER;
    } //-- void setACCOUNTNUMBER(java.lang.String) 

    /**
     * 
     * @param ACCOUNT_ID
    **/
    public void setACCOUNT_ID(java.lang.String ACCOUNT_ID)
    {
        this._ACCOUNT_ID = ACCOUNT_ID;
    } //-- void setACCOUNT_ID(java.lang.String) 

    /**
     * 
     * @param ADDRESS_ID
    **/
    public void setADDRESS_ID(java.lang.String ADDRESS_ID)
    {
        this._ADDRESS_ID = ADDRESS_ID;
    } //-- void setADDRESS_ID(java.lang.String) 

    /**
     * 
     * @param BILLINGADDRESS1
    **/
    public void setBILLINGADDRESS1(java.lang.String BILLINGADDRESS1)
    {
        this._BILLINGADDRESS1 = BILLINGADDRESS1;
    } //-- void setBILLINGADDRESS1(java.lang.String) 

    /**
     * 
     * @param BILLINGADDRESS2
    **/
    public void setBILLINGADDRESS2(java.lang.String BILLINGADDRESS2)
    {
        this._BILLINGADDRESS2 = BILLINGADDRESS2;
    } //-- void setBILLINGADDRESS2(java.lang.String) 

    /**
     * 
     * @param BILLINGADDRESSCITY
    **/
    public void setBILLINGADDRESSCITY(java.lang.String BILLINGADDRESSCITY)
    {
        this._BILLINGADDRESSCITY = BILLINGADDRESSCITY;
    } //-- void setBILLINGADDRESSCITY(java.lang.String) 

    /**
     * 
     * @param BILLINGADDRESSSTATECODE
    **/
    public void setBILLINGADDRESSSTATECODE(java.lang.String BILLINGADDRESSSTATECODE)
    {
        this._BILLINGADDRESSSTATECODE = BILLINGADDRESSSTATECODE;
    } //-- void setBILLINGADDRESSSTATECODE(java.lang.String) 

    /**
     * 
     * @param BILLINGADDRESSZIPCODE
    **/
    public void setBILLINGADDRESSZIPCODE(java.lang.String BILLINGADDRESSZIPCODE)
    {
        this._BILLINGADDRESSZIPCODE = BILLINGADDRESSZIPCODE;
    } //-- void setBILLINGADDRESSZIPCODE(java.lang.String) 

    /**
     * 
     * @param CUSTOMERNUMBER
    **/
    public void setCUSTOMERNUMBER(java.lang.String CUSTOMERNUMBER)
    {
        this._CUSTOMERNUMBER = CUSTOMERNUMBER;
    } //-- void setCUSTOMERNUMBER(java.lang.String) 

    /**
     * 
     * @param FEEDER
    **/
    public void setFEEDER(java.lang.String FEEDER)
    {
        this._FEEDER = FEEDER;
    } //-- void setFEEDER(java.lang.String) 

    /**
     * 
     * @param id
    **/
    public void setId(java.lang.String id)
    {
        this._id = id;
    } //-- void setId(java.lang.String) 

    /**
     * 
     * @param POLE
    **/
    public void setPOLE(java.lang.String POLE)
    {
        this._POLE = POLE;
    } //-- void setPOLE(java.lang.String) 

    /**
     * 
     * @param PROPERTYNOTES
    **/
    public void setPROPERTYNOTES(java.lang.String PROPERTYNOTES)
    {
        this._PROPERTYNOTES = PROPERTYNOTES;
    } //-- void setPROPERTYNOTES(java.lang.String) 

    /**
     * 
     * @param PROPERTYNUMBER
    **/
    public void setPROPERTYNUMBER(java.lang.String PROPERTYNUMBER)
    {
        this._PROPERTYNUMBER = PROPERTYNUMBER;
    } //-- void setPROPERTYNUMBER(java.lang.String) 

    /**
     * 
     * @param rowData
    **/
    public void setRowData(int rowData)
    {
        this._rowData = rowData;
        this._has_rowData = true;
    } //-- void setRowData(int) 

    /**
     * 
     * @param SERVICEVOLTAGE
    **/
    public void setSERVICEVOLTAGE(java.lang.String SERVICEVOLTAGE)
    {
        this._SERVICEVOLTAGE = SERVICEVOLTAGE;
    } //-- void setSERVICEVOLTAGE(java.lang.String) 

    /**
     * 
     * @param STREETADDRESS1
    **/
    public void setSTREETADDRESS1(java.lang.String STREETADDRESS1)
    {
        this._STREETADDRESS1 = STREETADDRESS1;
    } //-- void setSTREETADDRESS1(java.lang.String) 

    /**
     * 
     * @param STREETADDRESS2
    **/
    public void setSTREETADDRESS2(java.lang.String STREETADDRESS2)
    {
        this._STREETADDRESS2 = STREETADDRESS2;
    } //-- void setSTREETADDRESS2(java.lang.String) 

    /**
     * 
     * @param STREETADDRESSCITY
    **/
    public void setSTREETADDRESSCITY(java.lang.String STREETADDRESSCITY)
    {
        this._STREETADDRESSCITY = STREETADDRESSCITY;
    } //-- void setSTREETADDRESSCITY(java.lang.String) 

    /**
     * 
     * @param STREETADDRESSSTATECODE
    **/
    public void setSTREETADDRESSSTATECODE(java.lang.String STREETADDRESSSTATECODE)
    {
        this._STREETADDRESSSTATECODE = STREETADDRESSSTATECODE;
    } //-- void setSTREETADDRESSSTATECODE(java.lang.String) 

    /**
     * 
     * @param STREETADDRESSZIPCODE
    **/
    public void setSTREETADDRESSZIPCODE(java.lang.String STREETADDRESSZIPCODE)
    {
        this._STREETADDRESSZIPCODE = STREETADDRESSZIPCODE;
    } //-- void setSTREETADDRESSZIPCODE(java.lang.String) 

    /**
     * 
     * @param SUBSTATIONNAME
    **/
    public void setSUBSTATIONNAME(java.lang.String SUBSTATIONNAME)
    {
        this._SUBSTATIONNAME = SUBSTATIONNAME;
    } //-- void setSUBSTATIONNAME(java.lang.String) 

    /**
     * 
     * @param TRANSFORMERSIZE
    **/
    public void setTRANSFORMERSIZE(java.lang.String TRANSFORMERSIZE)
    {
        this._TRANSFORMERSIZE = TRANSFORMERSIZE;
    } //-- void setTRANSFORMERSIZE(java.lang.String) 

    /**
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
