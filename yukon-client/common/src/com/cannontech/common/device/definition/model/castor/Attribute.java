/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cannontech.common.device.definition.model.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

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
     * Internal choice value storage
     */
    private java.lang.Object _choiceValue;

    /**
     * Field _basicLookup
     */
    private com.cannontech.common.device.definition.model.castor.BasicLookup _basicLookup;

    /**
     * Field _mctIedTouLookup
     */
    private com.cannontech.common.device.definition.model.castor.MctIedTouLookup _mctIedTouLookup;

    /**
     * Field _mct4xxLookup
     */
    private com.cannontech.common.device.definition.model.castor.Mct4xxLookup _mct4xxLookup;


      //----------------/
     //- Constructors -/
    //----------------/

    public Attribute() 
     {
        super();
    } //-- com.cannontech.common.device.definition.model.castor.Attribute()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'basicLookup'.
     * 
     * @return BasicLookup
     * @return the value of field 'basicLookup'.
     */
    public com.cannontech.common.device.definition.model.castor.BasicLookup getBasicLookup()
    {
        return this._basicLookup;
    } //-- com.cannontech.common.device.definition.model.castor.BasicLookup getBasicLookup() 

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
     * Returns the value of field 'mct4xxLookup'.
     * 
     * @return Mct4xxLookup
     * @return the value of field 'mct4xxLookup'.
     */
    public com.cannontech.common.device.definition.model.castor.Mct4xxLookup getMct4xxLookup()
    {
        return this._mct4xxLookup;
    } //-- com.cannontech.common.device.definition.model.castor.Mct4xxLookup getMct4xxLookup() 

    /**
     * Returns the value of field 'mctIedTouLookup'.
     * 
     * @return MctIedTouLookup
     * @return the value of field 'mctIedTouLookup'.
     */
    public com.cannontech.common.device.definition.model.castor.MctIedTouLookup getMctIedTouLookup()
    {
        return this._mctIedTouLookup;
    } //-- com.cannontech.common.device.definition.model.castor.MctIedTouLookup getMctIedTouLookup() 

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
    public void setBasicLookup(com.cannontech.common.device.definition.model.castor.BasicLookup basicLookup)
    {
        this._basicLookup = basicLookup;
        this._choiceValue = basicLookup;
    } //-- void setBasicLookup(com.cannontech.common.device.definition.model.castor.BasicLookup) 

    /**
     * Sets the value of field 'mct4xxLookup'.
     * 
     * @param mct4xxLookup the value of field 'mct4xxLookup'.
     */
    public void setMct4xxLookup(com.cannontech.common.device.definition.model.castor.Mct4xxLookup mct4xxLookup)
    {
        this._mct4xxLookup = mct4xxLookup;
        this._choiceValue = mct4xxLookup;
    } //-- void setMct4xxLookup(com.cannontech.common.device.definition.model.castor.Mct4xxLookup) 

    /**
     * Sets the value of field 'mctIedTouLookup'.
     * 
     * @param mctIedTouLookup the value of field 'mctIedTouLookup'.
     */
    public void setMctIedTouLookup(com.cannontech.common.device.definition.model.castor.MctIedTouLookup mctIedTouLookup)
    {
        this._mctIedTouLookup = mctIedTouLookup;
        this._choiceValue = mctIedTouLookup;
    } //-- void setMctIedTouLookup(com.cannontech.common.device.definition.model.castor.MctIedTouLookup) 

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
        return (com.cannontech.common.device.definition.model.castor.Attribute) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.Attribute.class, reader);
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
