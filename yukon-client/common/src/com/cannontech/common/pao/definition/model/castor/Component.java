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

import com.cannontech.common.pao.definition.model.castor.types.ComponentTypeType;
import com.cannontech.common.pao.definition.model.castor.types.OperatorType;
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
     * Field _point
     */
    private java.lang.String _point;

    /**
     * Field _componentType
     */
    private com.cannontech.common.pao.definition.model.castor.types.ComponentTypeType _componentType = com.cannontech.common.pao.definition.model.castor.types.ComponentTypeType.valueOf("Operation");

    /**
     * Field _operator
     */
    private com.cannontech.common.pao.definition.model.castor.types.OperatorType _operator = com.cannontech.common.pao.definition.model.castor.types.OperatorType.valueOf("+");


      //----------------/
     //- Constructors -/
    //----------------/

    public Component() 
     {
        super();
        setComponentType(com.cannontech.common.pao.definition.model.castor.types.ComponentTypeType.valueOf("Operation"));
        setOperator(com.cannontech.common.pao.definition.model.castor.types.OperatorType.valueOf("+"));
    } //-- com.cannontech.common.pao.definition.model.castor.Component()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'componentType'.
     * 
     * @return ComponentTypeType
     * @return the value of field 'componentType'.
     */
    public com.cannontech.common.pao.definition.model.castor.types.ComponentTypeType getComponentType()
    {
        return this._componentType;
    } //-- com.cannontech.common.pao.definition.model.castor.types.ComponentTypeType getComponentType() 

    /**
     * Returns the value of field 'operator'.
     * 
     * @return OperatorType
     * @return the value of field 'operator'.
     */
    public com.cannontech.common.pao.definition.model.castor.types.OperatorType getOperator()
    {
        return this._operator;
    } //-- com.cannontech.common.pao.definition.model.castor.types.OperatorType getOperator() 

    /**
     * Returns the value of field 'point'.
     * 
     * @return String
     * @return the value of field 'point'.
     */
    public java.lang.String getPoint()
    {
        return this._point;
    } //-- java.lang.String getPoint() 

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
     * Sets the value of field 'componentType'.
     * 
     * @param componentType the value of field 'componentType'.
     */
    public void setComponentType(com.cannontech.common.pao.definition.model.castor.types.ComponentTypeType componentType)
    {
        this._componentType = componentType;
    } //-- void setComponentType(com.cannontech.common.pao.definition.model.castor.types.ComponentTypeType) 

    /**
     * Sets the value of field 'operator'.
     * 
     * @param operator the value of field 'operator'.
     */
    public void setOperator(com.cannontech.common.pao.definition.model.castor.types.OperatorType operator)
    {
        this._operator = operator;
    } //-- void setOperator(com.cannontech.common.pao.definition.model.castor.types.OperatorType) 

    /**
     * Sets the value of field 'point'.
     * 
     * @param point the value of field 'point'.
     */
    public void setPoint(java.lang.String point)
    {
        this._point = point;
    } //-- void setPoint(java.lang.String) 

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
