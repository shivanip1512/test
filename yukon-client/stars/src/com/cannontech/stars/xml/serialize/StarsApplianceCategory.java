/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsApplianceCategory.java,v 1.4 2002/08/30 18:23:14 alauinger Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision: 1.4 $ $Date: 2002/08/30 18:23:14 $
**/
public class StarsApplianceCategory implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _category;

    private java.lang.String _description;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsApplianceCategory() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsApplianceCategory()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'category'.
     * 
     * @return the value of field 'category'.
    **/
    public java.lang.String getCategory()
    {
        return this._category;
    } //-- java.lang.String getCategory() 

    /**
     * Returns the value of field 'description'.
     * 
     * @return the value of field 'description'.
    **/
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

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
     * Sets the value of field 'category'.
     * 
     * @param category the value of field 'category'.
    **/
    public void setCategory(java.lang.String category)
    {
        this._category = category;
    } //-- void setCategory(java.lang.String) 

    /**
     * Sets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
    **/
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsApplianceCategory unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsApplianceCategory) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsApplianceCategory.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsApplianceCategory unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
