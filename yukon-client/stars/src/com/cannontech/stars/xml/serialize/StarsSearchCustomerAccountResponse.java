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

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsSearchCustomerAccountResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsCustAccountInformation _starsCustAccountInformation;

    private java.util.Vector _accountIDList;

    private StarsFailure _starsFailure;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSearchCustomerAccountResponse() {
        super();
        _accountIDList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsSearchCustomerAccountResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vAccountID
    **/
    public void addAccountID(int vAccountID)
        throws java.lang.IndexOutOfBoundsException
    {
        _accountIDList.addElement(new Integer(vAccountID));
    } //-- void addAccountID(int) 

    /**
     * 
     * 
     * @param index
     * @param vAccountID
    **/
    public void addAccountID(int index, int vAccountID)
        throws java.lang.IndexOutOfBoundsException
    {
        _accountIDList.insertElementAt(new Integer(vAccountID), index);
    } //-- void addAccountID(int, int) 

    /**
    **/
    public java.util.Enumeration enumerateAccountID()
    {
        return _accountIDList.elements();
    } //-- java.util.Enumeration enumerateAccountID() 

    /**
     * 
     * 
     * @param index
    **/
    public int getAccountID(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _accountIDList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return ((Integer)_accountIDList.elementAt(index)).intValue();
    } //-- int getAccountID(int) 

    /**
    **/
    public int[] getAccountID()
    {
        int size = _accountIDList.size();
        int[] mArray = new int[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = ((Integer)_accountIDList.elementAt(index)).intValue();
        }
        return mArray;
    } //-- int[] getAccountID() 

    /**
    **/
    public int getAccountIDCount()
    {
        return _accountIDList.size();
    } //-- int getAccountIDCount() 

    /**
     * Returns the value of field 'starsCustAccountInformation'.
     * 
     * @return the value of field 'starsCustAccountInformation'.
    **/
    public StarsCustAccountInformation getStarsCustAccountInformation()
    {
        return this._starsCustAccountInformation;
    } //-- StarsCustAccountInformation getStarsCustAccountInformation() 

    /**
     * Returns the value of field 'starsFailure'.
     * 
     * @return the value of field 'starsFailure'.
    **/
    public StarsFailure getStarsFailure()
    {
        return this._starsFailure;
    } //-- StarsFailure getStarsFailure() 

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
     * 
     * 
     * @param index
    **/
    public int removeAccountID(int index)
    {
        java.lang.Object obj = _accountIDList.elementAt(index);
        _accountIDList.removeElementAt(index);
        return ((Integer)obj).intValue();
    } //-- int removeAccountID(int) 

    /**
    **/
    public void removeAllAccountID()
    {
        _accountIDList.removeAllElements();
    } //-- void removeAllAccountID() 

    /**
     * 
     * 
     * @param index
     * @param vAccountID
    **/
    public void setAccountID(int index, int vAccountID)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _accountIDList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _accountIDList.setElementAt(new Integer(vAccountID), index);
    } //-- void setAccountID(int, int) 

    /**
     * 
     * 
     * @param accountIDArray
    **/
    public void setAccountID(int[] accountIDArray)
    {
        //-- copy array
        _accountIDList.removeAllElements();
        for (int i = 0; i < accountIDArray.length; i++) {
            _accountIDList.addElement(new Integer(accountIDArray[i]));
        }
    } //-- void setAccountID(int) 

    /**
     * Sets the value of field 'starsCustAccountInformation'.
     * 
     * @param starsCustAccountInformation the value of field
     * 'starsCustAccountInformation'.
    **/
    public void setStarsCustAccountInformation(StarsCustAccountInformation starsCustAccountInformation)
    {
        this._starsCustAccountInformation = starsCustAccountInformation;
    } //-- void setStarsCustAccountInformation(StarsCustAccountInformation) 

    /**
     * Sets the value of field 'starsFailure'.
     * 
     * @param starsFailure the value of field 'starsFailure'.
    **/
    public void setStarsFailure(StarsFailure starsFailure)
    {
        this._starsFailure = starsFailure;
    } //-- void setStarsFailure(StarsFailure) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsSearchCustomerAccountResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsSearchCustomerAccountResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsSearchCustomerAccountResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsSearchCustomerAccountResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
