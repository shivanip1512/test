/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cannontech.common.pao.definition.model.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class Attribute.
 * 
 * @version $Revision$ $Date$
 */
public class Attribute implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _enabled
     */
    private java.lang.Boolean _enabled = new java.lang.Boolean("true");

    /**
     * Internal choice value storage
     */
    private java.lang.Object _choiceValue;

    /**
     * Field _basicLookup
     */
    private com.cannontech.common.pao.definition.model.castor.BasicLookup _basicLookup;

    /**
     * Field _mappedLookup
     */
    private com.cannontech.common.pao.definition.model.castor.MappedLookup _mappedLookup;


      //----------------/
     //- Constructors -/
    //----------------/

    public Attribute() 
     {
        super();
    } //-- com.cannontech.common.pao.definition.model.castor.Attribute()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'basicLookup'.
     * 
     * @return BasicLookup
     * @return the value of field 'basicLookup'.
     */
    public com.cannontech.common.pao.definition.model.castor.BasicLookup getBasicLookup()
    {
        return this._basicLookup;
    } //-- com.cannontech.common.pao.definition.model.castor.BasicLookup getBasicLookup() 

    /**
     * Returns the value of field 'choiceValue'. The field
     * 'choiceValue' has the following description: Internal choice
     * value storage
     * 
     * @return Object
     * @return the value of field 'choiceValue'.
     */
    public java.lang.Object getChoiceValue()
    {
        return this._choiceValue;
    } //-- java.lang.Object getChoiceValue() 

    /**
     * Returns the value of field 'enabled'.
     * 
     * @return Boolean
     * @return the value of field 'enabled'.
     */
    public java.lang.Boolean getEnabled()
    {
        return this._enabled;
    } //-- java.lang.Boolean getEnabled() 

    /**
     * Returns the value of field 'mappedLookup'.
     * 
     * @return MappedLookup
     * @return the value of field 'mappedLookup'.
     */
    public com.cannontech.common.pao.definition.model.castor.MappedLookup getMappedLookup()
    {
        return this._mappedLookup;
    } //-- com.cannontech.common.pao.definition.model.castor.MappedLookup getMappedLookup() 

    /**
     * Returns the value of field 'name'.
     * 
     * @return String
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return boolean
     */
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
     * Method marshal
     * 
     * 
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Sets the value of field 'basicLookup'.
     * 
     * @param basicLookup the value of field 'basicLookup'.
     */
    public void setBasicLookup(com.cannontech.common.pao.definition.model.castor.BasicLookup basicLookup)
    {
        this._basicLookup = basicLookup;
        this._choiceValue = basicLookup;
    } //-- void setBasicLookup(com.cannontech.common.pao.definition.model.castor.BasicLookup) 

    /**
     * Sets the value of field 'enabled'.
     * 
     * @param enabled the value of field 'enabled'.
     */
    public void setEnabled(java.lang.Boolean enabled)
    {
        this._enabled = enabled;
    } //-- void setEnabled(java.lang.Boolean) 

    /**
     * Sets the value of field 'mappedLookup'.
     * 
     * @param mappedLookup the value of field 'mappedLookup'.
     */
    public void setMappedLookup(com.cannontech.common.pao.definition.model.castor.MappedLookup mappedLookup)
    {
        this._mappedLookup = mappedLookup;
        this._choiceValue = mappedLookup;
    } //-- void setMappedLookup(com.cannontech.common.pao.definition.model.castor.MappedLookup) 

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return Object
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.common.pao.definition.model.castor.Attribute) Unmarshaller.unmarshal(com.cannontech.common.pao.definition.model.castor.Attribute.class, reader);
    } //-- java.lang.Object unmarshal(java.io.Reader) 

    /**
     * Method validate
     * 
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
