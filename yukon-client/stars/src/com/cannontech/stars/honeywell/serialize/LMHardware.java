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
import java.util.Date;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * 
 * @version $Revision$ $Date$
**/
public class LMHardware implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _id;

    private int _rowData;

    /**
     * keeps track of state for field: _rowData
    **/
    private boolean _has_rowData;

    private java.lang.String _INVENTORYID;

    private java.lang.String _ACCOUNTID;

    private java.util.Date _RECEIVEDATE;

    private java.util.Date _INSTALLDATE;

    private java.util.Date _REMOVEDATE;

    private java.lang.String _ALTERNATETRACKINGNUMBER;

    private java.lang.String _VOLTAGE;

    private java.lang.String _NOTES;

    private java.lang.String _MANUFACTURERSERIALNUMBER;

    private java.lang.String _LMDEVICETYPE;

    private java.lang.String _INSTALLATIONCOMPANYNAME;

    private java.lang.String _ICADDRESS1;

    private java.lang.String _ICADDRESS2;

    private java.lang.String _ICCITY;

    private java.lang.String _ICSTATECODE;

    private java.lang.String _ICZIPCODE;

    private java.lang.String _ICMAINPHONENUMBER;

    private java.lang.String _ICFIRSTNAME;

    private java.lang.String _ICLASTNAME;


      //----------------/
     //- Constructors -/
    //----------------/

    public LMHardware() {
        super();
    } //-- com.cannontech.stars.honeywell.serialize.LMHardware()


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
    public java.lang.String getALTERNATETRACKINGNUMBER()
    {
        return this._ALTERNATETRACKINGNUMBER;
    } //-- java.lang.String getALTERNATETRACKINGNUMBER() 

    /**
    **/
    public java.lang.String getICADDRESS1()
    {
        return this._ICADDRESS1;
    } //-- java.lang.String getICADDRESS1() 

    /**
    **/
    public java.lang.String getICADDRESS2()
    {
        return this._ICADDRESS2;
    } //-- java.lang.String getICADDRESS2() 

    /**
    **/
    public java.lang.String getICCITY()
    {
        return this._ICCITY;
    } //-- java.lang.String getICCITY() 

    /**
    **/
    public java.lang.String getICFIRSTNAME()
    {
        return this._ICFIRSTNAME;
    } //-- java.lang.String getICFIRSTNAME() 

    /**
    **/
    public java.lang.String getICLASTNAME()
    {
        return this._ICLASTNAME;
    } //-- java.lang.String getICLASTNAME() 

    /**
    **/
    public java.lang.String getICMAINPHONENUMBER()
    {
        return this._ICMAINPHONENUMBER;
    } //-- java.lang.String getICMAINPHONENUMBER() 

    /**
    **/
    public java.lang.String getICSTATECODE()
    {
        return this._ICSTATECODE;
    } //-- java.lang.String getICSTATECODE() 

    /**
    **/
    public java.lang.String getICZIPCODE()
    {
        return this._ICZIPCODE;
    } //-- java.lang.String getICZIPCODE() 

    /**
    **/
    public java.lang.String getINSTALLATIONCOMPANYNAME()
    {
        return this._INSTALLATIONCOMPANYNAME;
    } //-- java.lang.String getINSTALLATIONCOMPANYNAME() 

    /**
    **/
    public java.util.Date getINSTALLDATE()
    {
        return this._INSTALLDATE;
    } //-- java.util.Date getINSTALLDATE() 

    /**
    **/
    public java.lang.String getINVENTORYID()
    {
        return this._INVENTORYID;
    } //-- java.lang.String getINVENTORYID() 

    /**
    **/
    public java.lang.String getId()
    {
        return this._id;
    } //-- java.lang.String getId() 

    /**
    **/
    public java.lang.String getLMDEVICETYPE()
    {
        return this._LMDEVICETYPE;
    } //-- java.lang.String getLMDEVICETYPE() 

    /**
    **/
    public java.lang.String getMANUFACTURERSERIALNUMBER()
    {
        return this._MANUFACTURERSERIALNUMBER;
    } //-- java.lang.String getMANUFACTURERSERIALNUMBER() 

    /**
    **/
    public java.lang.String getNOTES()
    {
        return this._NOTES;
    } //-- java.lang.String getNOTES() 

    /**
    **/
    public java.util.Date getRECEIVEDATE()
    {
        return this._RECEIVEDATE;
    } //-- java.util.Date getRECEIVEDATE() 

    /**
    **/
    public java.util.Date getREMOVEDATE()
    {
        return this._REMOVEDATE;
    } //-- java.util.Date getREMOVEDATE() 

    /**
    **/
    public int getRowData()
    {
        return this._rowData;
    } //-- int getRowData() 

    /**
    **/
    public java.lang.String getVOLTAGE()
    {
        return this._VOLTAGE;
    } //-- java.lang.String getVOLTAGE() 

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
     * @param ALTERNATETRACKINGNUMBER
    **/
    public void setALTERNATETRACKINGNUMBER(java.lang.String ALTERNATETRACKINGNUMBER)
    {
        this._ALTERNATETRACKINGNUMBER = ALTERNATETRACKINGNUMBER;
    } //-- void setALTERNATETRACKINGNUMBER(java.lang.String) 

    /**
     * 
     * @param ICADDRESS1
    **/
    public void setICADDRESS1(java.lang.String ICADDRESS1)
    {
        this._ICADDRESS1 = ICADDRESS1;
    } //-- void setICADDRESS1(java.lang.String) 

    /**
     * 
     * @param ICADDRESS2
    **/
    public void setICADDRESS2(java.lang.String ICADDRESS2)
    {
        this._ICADDRESS2 = ICADDRESS2;
    } //-- void setICADDRESS2(java.lang.String) 

    /**
     * 
     * @param ICCITY
    **/
    public void setICCITY(java.lang.String ICCITY)
    {
        this._ICCITY = ICCITY;
    } //-- void setICCITY(java.lang.String) 

    /**
     * 
     * @param ICFIRSTNAME
    **/
    public void setICFIRSTNAME(java.lang.String ICFIRSTNAME)
    {
        this._ICFIRSTNAME = ICFIRSTNAME;
    } //-- void setICFIRSTNAME(java.lang.String) 

    /**
     * 
     * @param ICLASTNAME
    **/
    public void setICLASTNAME(java.lang.String ICLASTNAME)
    {
        this._ICLASTNAME = ICLASTNAME;
    } //-- void setICLASTNAME(java.lang.String) 

    /**
     * 
     * @param ICMAINPHONENUMBER
    **/
    public void setICMAINPHONENUMBER(java.lang.String ICMAINPHONENUMBER)
    {
        this._ICMAINPHONENUMBER = ICMAINPHONENUMBER;
    } //-- void setICMAINPHONENUMBER(java.lang.String) 

    /**
     * 
     * @param ICSTATECODE
    **/
    public void setICSTATECODE(java.lang.String ICSTATECODE)
    {
        this._ICSTATECODE = ICSTATECODE;
    } //-- void setICSTATECODE(java.lang.String) 

    /**
     * 
     * @param ICZIPCODE
    **/
    public void setICZIPCODE(java.lang.String ICZIPCODE)
    {
        this._ICZIPCODE = ICZIPCODE;
    } //-- void setICZIPCODE(java.lang.String) 

    /**
     * 
     * @param INSTALLATIONCOMPANYNAME
    **/
    public void setINSTALLATIONCOMPANYNAME(java.lang.String INSTALLATIONCOMPANYNAME)
    {
        this._INSTALLATIONCOMPANYNAME = INSTALLATIONCOMPANYNAME;
    } //-- void setINSTALLATIONCOMPANYNAME(java.lang.String) 

    /**
     * 
     * @param INSTALLDATE
    **/
    public void setINSTALLDATE(java.util.Date INSTALLDATE)
    {
        this._INSTALLDATE = INSTALLDATE;
    } //-- void setINSTALLDATE(java.util.Date) 

    /**
     * 
     * @param INVENTORYID
    **/
    public void setINVENTORYID(java.lang.String INVENTORYID)
    {
        this._INVENTORYID = INVENTORYID;
    } //-- void setINVENTORYID(java.lang.String) 

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
     * @param LMDEVICETYPE
    **/
    public void setLMDEVICETYPE(java.lang.String LMDEVICETYPE)
    {
        this._LMDEVICETYPE = LMDEVICETYPE;
    } //-- void setLMDEVICETYPE(java.lang.String) 

    /**
     * 
     * @param MANUFACTURERSERIALNUMBER
    **/
    public void setMANUFACTURERSERIALNUMBER(java.lang.String MANUFACTURERSERIALNUMBER)
    {
        this._MANUFACTURERSERIALNUMBER = MANUFACTURERSERIALNUMBER;
    } //-- void setMANUFACTURERSERIALNUMBER(java.lang.String) 

    /**
     * 
     * @param NOTES
    **/
    public void setNOTES(java.lang.String NOTES)
    {
        this._NOTES = NOTES;
    } //-- void setNOTES(java.lang.String) 

    /**
     * 
     * @param RECEIVEDATE
    **/
    public void setRECEIVEDATE(java.util.Date RECEIVEDATE)
    {
        this._RECEIVEDATE = RECEIVEDATE;
    } //-- void setRECEIVEDATE(java.util.Date) 

    /**
     * 
     * @param REMOVEDATE
    **/
    public void setREMOVEDATE(java.util.Date REMOVEDATE)
    {
        this._REMOVEDATE = REMOVEDATE;
    } //-- void setREMOVEDATE(java.util.Date) 

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
     * @param VOLTAGE
    **/
    public void setVOLTAGE(java.lang.String VOLTAGE)
    {
        this._VOLTAGE = VOLTAGE;
    } //-- void setVOLTAGE(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static com.cannontech.stars.honeywell.serialize.LMHardware unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.honeywell.serialize.LMHardware) Unmarshaller.unmarshal(com.cannontech.stars.honeywell.serialize.LMHardware.class, reader);
    } //-- com.cannontech.stars.honeywell.serialize.LMHardware unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
