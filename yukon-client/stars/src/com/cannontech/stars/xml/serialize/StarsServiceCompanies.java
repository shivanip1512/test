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
public class StarsServiceCompanies implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsServiceCompanyList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsServiceCompanies() {
        super();
        _starsServiceCompanyList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsServiceCompanies()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsServiceCompany
    **/
    public void addStarsServiceCompany(StarsServiceCompany vStarsServiceCompany)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsServiceCompanyList.addElement(vStarsServiceCompany);
    } //-- void addStarsServiceCompany(StarsServiceCompany) 

    /**
     * 
     * 
     * @param index
     * @param vStarsServiceCompany
    **/
    public void addStarsServiceCompany(int index, StarsServiceCompany vStarsServiceCompany)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsServiceCompanyList.insertElementAt(vStarsServiceCompany, index);
    } //-- void addStarsServiceCompany(int, StarsServiceCompany) 

    /**
    **/
    public java.util.Enumeration enumerateStarsServiceCompany()
    {
        return _starsServiceCompanyList.elements();
    } //-- java.util.Enumeration enumerateStarsServiceCompany() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsServiceCompany getStarsServiceCompany(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsServiceCompanyList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsServiceCompany) _starsServiceCompanyList.elementAt(index);
    } //-- StarsServiceCompany getStarsServiceCompany(int) 

    /**
    **/
    public StarsServiceCompany[] getStarsServiceCompany()
    {
        int size = _starsServiceCompanyList.size();
        StarsServiceCompany[] mArray = new StarsServiceCompany[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsServiceCompany) _starsServiceCompanyList.elementAt(index);
        }
        return mArray;
    } //-- StarsServiceCompany[] getStarsServiceCompany() 

    /**
    **/
    public int getStarsServiceCompanyCount()
    {
        return _starsServiceCompanyList.size();
    } //-- int getStarsServiceCompanyCount() 

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
    public void removeAllStarsServiceCompany()
    {
        _starsServiceCompanyList.removeAllElements();
    } //-- void removeAllStarsServiceCompany() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsServiceCompany removeStarsServiceCompany(int index)
    {
        java.lang.Object obj = _starsServiceCompanyList.elementAt(index);
        _starsServiceCompanyList.removeElementAt(index);
        return (StarsServiceCompany) obj;
    } //-- StarsServiceCompany removeStarsServiceCompany(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsServiceCompany
    **/
    public void setStarsServiceCompany(int index, StarsServiceCompany vStarsServiceCompany)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsServiceCompanyList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsServiceCompanyList.setElementAt(vStarsServiceCompany, index);
    } //-- void setStarsServiceCompany(int, StarsServiceCompany) 

    /**
     * 
     * 
     * @param starsServiceCompanyArray
    **/
    public void setStarsServiceCompany(StarsServiceCompany[] starsServiceCompanyArray)
    {
        //-- copy array
        _starsServiceCompanyList.removeAllElements();
        for (int i = 0; i < starsServiceCompanyArray.length; i++) {
            _starsServiceCompanyList.addElement(starsServiceCompanyArray[i]);
        }
    } //-- void setStarsServiceCompany(StarsServiceCompany) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsServiceCompanies unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsServiceCompanies) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsServiceCompanies.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsServiceCompanies unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
