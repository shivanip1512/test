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

    private java.util.Vector _starsBriefCustAccountInfoList;

    private StarsFailure _starsFailure;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSearchCustomerAccountResponse() {
        super();
        _starsBriefCustAccountInfoList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsSearchCustomerAccountResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsBriefCustAccountInfo
    **/
    public void addStarsBriefCustAccountInfo(StarsBriefCustAccountInfo vStarsBriefCustAccountInfo)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsBriefCustAccountInfoList.addElement(vStarsBriefCustAccountInfo);
    } //-- void addStarsBriefCustAccountInfo(StarsBriefCustAccountInfo) 

    /**
     * 
     * 
     * @param index
     * @param vStarsBriefCustAccountInfo
    **/
    public void addStarsBriefCustAccountInfo(int index, StarsBriefCustAccountInfo vStarsBriefCustAccountInfo)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsBriefCustAccountInfoList.insertElementAt(vStarsBriefCustAccountInfo, index);
    } //-- void addStarsBriefCustAccountInfo(int, StarsBriefCustAccountInfo) 

    /**
    **/
    public java.util.Enumeration enumerateStarsBriefCustAccountInfo()
    {
        return _starsBriefCustAccountInfoList.elements();
    } //-- java.util.Enumeration enumerateStarsBriefCustAccountInfo() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsBriefCustAccountInfo getStarsBriefCustAccountInfo(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsBriefCustAccountInfoList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsBriefCustAccountInfo) _starsBriefCustAccountInfoList.elementAt(index);
    } //-- StarsBriefCustAccountInfo getStarsBriefCustAccountInfo(int) 

    /**
    **/
    public StarsBriefCustAccountInfo[] getStarsBriefCustAccountInfo()
    {
        int size = _starsBriefCustAccountInfoList.size();
        StarsBriefCustAccountInfo[] mArray = new StarsBriefCustAccountInfo[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsBriefCustAccountInfo) _starsBriefCustAccountInfoList.elementAt(index);
        }
        return mArray;
    } //-- StarsBriefCustAccountInfo[] getStarsBriefCustAccountInfo() 

    /**
    **/
    public int getStarsBriefCustAccountInfoCount()
    {
        return _starsBriefCustAccountInfoList.size();
    } //-- int getStarsBriefCustAccountInfoCount() 

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
    **/
    public void removeAllStarsBriefCustAccountInfo()
    {
        _starsBriefCustAccountInfoList.removeAllElements();
    } //-- void removeAllStarsBriefCustAccountInfo() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsBriefCustAccountInfo removeStarsBriefCustAccountInfo(int index)
    {
        java.lang.Object obj = _starsBriefCustAccountInfoList.elementAt(index);
        _starsBriefCustAccountInfoList.removeElementAt(index);
        return (StarsBriefCustAccountInfo) obj;
    } //-- StarsBriefCustAccountInfo removeStarsBriefCustAccountInfo(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsBriefCustAccountInfo
    **/
    public void setStarsBriefCustAccountInfo(int index, StarsBriefCustAccountInfo vStarsBriefCustAccountInfo)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsBriefCustAccountInfoList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsBriefCustAccountInfoList.setElementAt(vStarsBriefCustAccountInfo, index);
    } //-- void setStarsBriefCustAccountInfo(int, StarsBriefCustAccountInfo) 

    /**
     * 
     * 
     * @param starsBriefCustAccountInfoArray
    **/
    public void setStarsBriefCustAccountInfo(StarsBriefCustAccountInfo[] starsBriefCustAccountInfoArray)
    {
        //-- copy array
        _starsBriefCustAccountInfoList.removeAllElements();
        for (int i = 0; i < starsBriefCustAccountInfoArray.length; i++) {
            _starsBriefCustAccountInfoList.addElement(starsBriefCustAccountInfoArray[i]);
        }
    } //-- void setStarsBriefCustAccountInfo(StarsBriefCustAccountInfo) 

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
