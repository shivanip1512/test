/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id: StarsSearchCustomerAccount.java,v 1.1 2002/07/16 19:50:08 Yao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import com.cannontech.stars.xml.serialize.types.StarsSearchByType;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * Search for existing customer account
 * @version $Revision: 1.1 $ $Date: 2002/07/16 19:50:08 $
**/
public class StarsSearchCustomerAccount implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private com.cannontech.stars.xml.serialize.types.StarsSearchByType _searchBy;

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
    **/
    public com.cannontech.stars.xml.serialize.types.StarsSearchByType getSearchBy()
    {
        return this._searchBy;
    } //-- com.cannontech.stars.xml.serialize.types.StarsSearchByType getSearchBy() 

    /**
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
     * @param searchBy
    **/
    public void setSearchBy(com.cannontech.stars.xml.serialize.types.StarsSearchByType searchBy)
    {
        this._searchBy = searchBy;
    } //-- void setSearchBy(com.cannontech.stars.xml.serialize.types.StarsSearchByType) 

    /**
     * 
     * @param searchValue
    **/
    public void setSearchValue(java.lang.String searchValue)
    {
        this._searchValue = searchValue;
    } //-- void setSearchValue(java.lang.String) 

    /**
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
