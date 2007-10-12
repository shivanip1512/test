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
 * Class Type.
 * 
 * @version $Revision$ $Date$
 */
public class Type implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _value
     */
    private int _value;

    /**
     * keeps track of state for field: _value
     */
    private boolean _has_value;

    /**
     * Field _javaConstant
     */
    private java.lang.String _javaConstant;

    /**
     * Field _changeGroup
     */
    private java.lang.String _changeGroup;


      //----------------/
     //- Constructors -/
    //----------------/

    public Type() 
     {
        super();
    } //-- com.cannontech.common.device.definition.model.castor.Type()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteValue
     * 
     */
    public void deleteValue()
    {
        this._has_value= false;
    } //-- void deleteValue() 

    /**
     * Returns the value of field 'changeGroup'.
     * 
     * @return String
     * @return the value of field 'changeGroup'.
     */
    public java.lang.String getChangeGroup()
    {
        return this._changeGroup;
    } //-- java.lang.String getChangeGroup() 

    /**
     * Returns the value of field 'javaConstant'.
     * 
     * @return String
     * @return the value of field 'javaConstant'.
     */
    public java.lang.String getJavaConstant()
    {
        return this._javaConstant;
    } //-- java.lang.String getJavaConstant() 

    /**
     * Returns the value of field 'value'.
     * 
     * @return int
     * @return the value of field 'value'.
     */
    public int getValue()
    {
        return this._value;
    } //-- int getValue() 

    /**
     * Method hasValue
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasValue()
    {
        return this._has_value;
    } //-- boolean hasValue() 

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
     * Sets the value of field 'changeGroup'.
     * 
     * @param changeGroup the value of field 'changeGroup'.
     */
    public void setChangeGroup(java.lang.String changeGroup)
    {
        this._changeGroup = changeGroup;
    } //-- void setChangeGroup(java.lang.String) 

    /**
     * Sets the value of field 'javaConstant'.
     * 
     * @param javaConstant the value of field 'javaConstant'.
     */
    public void setJavaConstant(java.lang.String javaConstant)
    {
        this._javaConstant = javaConstant;
    } //-- void setJavaConstant(java.lang.String) 

    /**
     * Sets the value of field 'value'.
     * 
     * @param value the value of field 'value'.
     */
    public void setValue(int value)
    {
        this._value = value;
        this._has_value = true;
    } //-- void setValue(int) 

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
        return (com.cannontech.common.device.definition.model.castor.Type) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.Type.class, reader);
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
