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

import java.util.Vector;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

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

    private java.util.Vector _starsCustAccountBriefList;

    private StarsFailure _starsFailure;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSearchCustomerAccountResponse() {
        super();
        _starsCustAccountBriefList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsSearchCustomerAccountResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsCustAccountBrief
    **/
    public void addStarsCustAccountBrief(StarsCustAccountBrief vStarsCustAccountBrief)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsCustAccountBriefList.addElement(vStarsCustAccountBrief);
    } //-- void addStarsCustAccountBrief(StarsCustAccountBrief) 

    /**
     * 
     * 
     * @param index
     * @param vStarsCustAccountBrief
    **/
    public void addStarsCustAccountBrief(int index, StarsCustAccountBrief vStarsCustAccountBrief)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsCustAccountBriefList.insertElementAt(vStarsCustAccountBrief, index);
    } //-- void addStarsCustAccountBrief(int, StarsCustAccountBrief) 

    /**
    **/
    public java.util.Enumeration enumerateStarsCustAccountBrief()
    {
        return _starsCustAccountBriefList.elements();
    } //-- java.util.Enumeration enumerateStarsCustAccountBrief() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsCustAccountBrief getStarsCustAccountBrief(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCustAccountBriefList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsCustAccountBrief) _starsCustAccountBriefList.elementAt(index);
    } //-- StarsCustAccountBrief getStarsCustAccountBrief(int) 

    /**
    **/
    public StarsCustAccountBrief[] getStarsCustAccountBrief()
    {
        int size = _starsCustAccountBriefList.size();
        StarsCustAccountBrief[] mArray = new StarsCustAccountBrief[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsCustAccountBrief) _starsCustAccountBriefList.elementAt(index);
        }
        return mArray;
    } //-- StarsCustAccountBrief[] getStarsCustAccountBrief() 

    /**
    **/
    public int getStarsCustAccountBriefCount()
    {
        return _starsCustAccountBriefList.size();
    } //-- int getStarsCustAccountBriefCount() 

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
    public void removeAllStarsCustAccountBrief()
    {
        _starsCustAccountBriefList.removeAllElements();
    } //-- void removeAllStarsCustAccountBrief() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsCustAccountBrief removeStarsCustAccountBrief(int index)
    {
        java.lang.Object obj = _starsCustAccountBriefList.elementAt(index);
        _starsCustAccountBriefList.removeElementAt(index);
        return (StarsCustAccountBrief) obj;
    } //-- StarsCustAccountBrief removeStarsCustAccountBrief(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsCustAccountBrief
    **/
    public void setStarsCustAccountBrief(int index, StarsCustAccountBrief vStarsCustAccountBrief)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCustAccountBriefList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsCustAccountBriefList.setElementAt(vStarsCustAccountBrief, index);
    } //-- void setStarsCustAccountBrief(int, StarsCustAccountBrief) 

    /**
     * 
     * 
     * @param starsCustAccountBriefArray
    **/
    public void setStarsCustAccountBrief(StarsCustAccountBrief[] starsCustAccountBriefArray)
    {
        //-- copy array
        _starsCustAccountBriefList.removeAllElements();
        for (int i = 0; i < starsCustAccountBriefArray.length; i++) {
            _starsCustAccountBriefList.addElement(starsCustAccountBriefArray[i]);
        }
    } //-- void setStarsCustAccountBrief(StarsCustAccountBrief) 

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
