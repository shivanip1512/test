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
public class CustomerContact implements java.io.Serializable {


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
    public java.lang.String getEXPR1()
    {
        return this._EXPR1;
    } //-- java.lang.String getEXPR1() 

    /**
    **/
    public java.lang.String getFIRSTNAME()
    {
        return this._FIRSTNAME;
    } //-- java.lang.String getFIRSTNAME() 

    /**
    **/
    public java.lang.String getId()
    {
        return this._id;
    } //-- java.lang.String getId() 

    /**
    **/
    public java.lang.String getLASTNAME()
    {
        return this._LASTNAME;
    } //-- java.lang.String getLASTNAME() 

    /**
    **/
    public java.lang.String getPERSONCOMPANY_ID()
    {
        return this._PERSONCOMPANY_ID;
    } //-- java.lang.String getPERSONCOMPANY_ID() 

    /**
    **/
    public java.lang.String getPHONE1()
    {
        return this._PHONE1;
    } //-- java.lang.String getPHONE1() 

    /**
    **/
    public java.lang.String getPHONE2()
    {
        return this._PHONE2;
    } //-- java.lang.String getPHONE2() 

    /**
    **/
    public java.lang.String getPHONE_ID()
    {
        return this._PHONE_ID;
    } //-- java.lang.String getPHONE_ID() 

    /**
    **/
    public int getRowData()
    {
        return this._rowData;
    } //-- int getRowData() 

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
     * @param EXPR1
    **/
    public void setEXPR1(java.lang.String EXPR1)
    {
        this._EXPR1 = EXPR1;
    } //-- void setEXPR1(java.lang.String) 

    /**
     * 
     * @param FIRSTNAME
    **/
    public void setFIRSTNAME(java.lang.String FIRSTNAME)
    {
        this._FIRSTNAME = FIRSTNAME;
    } //-- void setFIRSTNAME(java.lang.String) 

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
     * @param LASTNAME
    **/
    public void setLASTNAME(java.lang.String LASTNAME)
    {
        this._LASTNAME = LASTNAME;
    } //-- void setLASTNAME(java.lang.String) 

    /**
     * 
     * @param PERSONCOMPANY_ID
    **/
    public void setPERSONCOMPANY_ID(java.lang.String PERSONCOMPANY_ID)
    {
        this._PERSONCOMPANY_ID = PERSONCOMPANY_ID;
    } //-- void setPERSONCOMPANY_ID(java.lang.String) 

    /**
     * 
     * @param PHONE1
    **/
    public void setPHONE1(java.lang.String PHONE1)
    {
        this._PHONE1 = PHONE1;
    } //-- void setPHONE1(java.lang.String) 

    /**
     * 
     * @param PHONE2
    **/
    public void setPHONE2(java.lang.String PHONE2)
    {
        this._PHONE2 = PHONE2;
    } //-- void setPHONE2(java.lang.String) 

    /**
     * 
     * @param PHONE_ID
    **/
    public void setPHONE_ID(java.lang.String PHONE_ID)
    {
        this._PHONE_ID = PHONE_ID;
    } //-- void setPHONE_ID(java.lang.String) 

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
