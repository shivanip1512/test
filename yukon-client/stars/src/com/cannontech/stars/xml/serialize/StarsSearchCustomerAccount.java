/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsSearchCustomerAccount.java,v 1.84 2004/07/26 21:33:29 yao Exp $
 */

package com.cannontech.stars.xml.serialize;

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
 * @version $Revision: 1.84 $ $Date: 2004/07/26 21:33:29 $
**/
public class StarsSearchCustomerAccount implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private SearchBy _searchBy;

    private java.lang.String _searchValue;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSearchCustomerAccount() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsSearchCustomerAccount()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'searchBy'.
     * 
     * @return the value of field 'searchBy'.
    **/
    public SearchBy getSearchBy()
    {
        return this._searchBy;
    } //-- SearchBy getSearchBy() 

    /**
     * Returns the value of field 'searchValue'.
     * 
     * @return the value of field 'searchValue'.
    **/
    public java.lang.String getSearchValue()
    {
        return this._searchValue;
    } //-- java.lang.String getSearchValue() 

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
     * Sets the value of field 'searchBy'.
     * 
     * @param searchBy the value of field 'searchBy'.
    **/
    public void setSearchBy(SearchBy searchBy)
    {
        this._searchBy = searchBy;
    } //-- void setSearchBy(SearchBy) 

    /**
     * Sets the value of field 'searchValue'.
     * 
     * @param searchValue the value of field 'searchValue'.
    **/
    public void setSearchValue(java.lang.String searchValue)
    {
        this._searchValue = searchValue;
    } //-- void setSearchValue(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsSearchCustomerAccount unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsSearchCustomerAccount) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsSearchCustomerAccount.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsSearchCustomerAccount unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
