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
 * Class Component.
 * 
 * @version $Revision$ $Date$
 */
public class Component implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _componentType
     */
    private java.lang.String _componentType = "Operation";

    /**
     * Field _operator
     */
    private java.lang.String _operator = "+";

    /**
     * Field _basicCalcLookup
     */
    private com.cannontech.common.pao.definition.model.castor.BasicCalcLookup _basicCalcLookup;


      //----------------/
     //- Constructors -/
    //----------------/

    public Component() 
     {
        super();
        setComponentType("Operation");
        setOperator("+");
    } //-- com.cannontech.common.pao.definition.model.castor.Component()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'basicCalcLookup'.
     * 
     * @return BasicCalcLookup
     * @return the value of field 'basicCalcLookup'.
     */
    public com.cannontech.common.pao.definition.model.castor.BasicCalcLookup getBasicCalcLookup()
    {
        return this._basicCalcLookup;
    } //-- com.cannontech.common.pao.definition.model.castor.BasicCalcLookup getBasicCalcLookup() 

    /**
     * Returns the value of field 'componentType'.
     * 
     * @return String
     * @return the value of field 'componentType'.
     */
    public java.lang.String getComponentType()
    {
        return this._componentType;
    } //-- java.lang.String getComponentType() 

    /**
     * Returns the value of field 'operator'.
     * 
     * @return String
     * @return the value of field 'operator'.
     */
    public java.lang.String getOperator()
    {
        return this._operator;
    } //-- java.lang.String getOperator() 

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
     * Sets the value of field 'basicCalcLookup'.
     * 
     * @param basicCalcLookup the value of field 'basicCalcLookup'.
     */
    public void setBasicCalcLookup(com.cannontech.common.pao.definition.model.castor.BasicCalcLookup basicCalcLookup)
    {
        this._basicCalcLookup = basicCalcLookup;
    } //-- void setBasicCalcLookup(com.cannontech.common.pao.definition.model.castor.BasicCalcLookup) 

    /**
     * Sets the value of field 'componentType'.
     * 
     * @param componentType the value of field 'componentType'.
     */
    public void setComponentType(java.lang.String componentType)
    {
        this._componentType = componentType;
    } //-- void setComponentType(java.lang.String) 

    /**
     * Sets the value of field 'operator'.
     * 
     * @param operator the value of field 'operator'.
     */
    public void setOperator(java.lang.String operator)
    {
        this._operator = operator;
    } //-- void setOperator(java.lang.String) 

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
        return (com.cannontech.common.pao.definition.model.castor.Component) Unmarshaller.unmarshal(com.cannontech.common.pao.definition.model.castor.Component.class, reader);
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
