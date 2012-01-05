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

import com.cannontech.common.pao.definition.model.castor.types.UpdateTypeType;
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
 * Class Calculation.
 * 
 * @version $Revision$ $Date$
 */
public class Calculation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _forceQualityNormal
     */
    private java.lang.Boolean _forceQualityNormal = new java.lang.Boolean("false");

    /**
     * Field _periodicRate
     */
    private java.lang.Integer _periodicRate = new java.lang.Integer("1");

    /**
     * Field _updateType
     */
    private com.cannontech.common.pao.definition.model.castor.types.UpdateTypeType _updateType = com.cannontech.common.pao.definition.model.castor.types.UpdateTypeType.valueOf("On First Change");

    /**
     * Field _components
     */
    private com.cannontech.common.pao.definition.model.castor.Components _components;


      //----------------/
     //- Constructors -/
    //----------------/

    public Calculation() 
     {
        super();
        setUpdateType(com.cannontech.common.pao.definition.model.castor.types.UpdateTypeType.valueOf("On First Change"));
    } //-- com.cannontech.common.pao.definition.model.castor.Calculation()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'components'.
     * 
     * @return Components
     * @return the value of field 'components'.
     */
    public com.cannontech.common.pao.definition.model.castor.Components getComponents()
    {
        return this._components;
    } //-- com.cannontech.common.pao.definition.model.castor.Components getComponents() 

    /**
     * Returns the value of field 'forceQualityNormal'.
     * 
     * @return Boolean
     * @return the value of field 'forceQualityNormal'.
     */
    public java.lang.Boolean getForceQualityNormal()
    {
        return this._forceQualityNormal;
    } //-- java.lang.Boolean getForceQualityNormal() 

    /**
     * Returns the value of field 'periodicRate'.
     * 
     * @return Integer
     * @return the value of field 'periodicRate'.
     */
    public java.lang.Integer getPeriodicRate()
    {
        return this._periodicRate;
    } //-- java.lang.Integer getPeriodicRate() 

    /**
     * Returns the value of field 'updateType'.
     * 
     * @return UpdateTypeType
     * @return the value of field 'updateType'.
     */
    public com.cannontech.common.pao.definition.model.castor.types.UpdateTypeType getUpdateType()
    {
        return this._updateType;
    } //-- com.cannontech.common.pao.definition.model.castor.types.UpdateTypeType getUpdateType() 

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
     * Sets the value of field 'components'.
     * 
     * @param components the value of field 'components'.
     */
    public void setComponents(com.cannontech.common.pao.definition.model.castor.Components components)
    {
        this._components = components;
    } //-- void setComponents(com.cannontech.common.pao.definition.model.castor.Components) 

    /**
     * Sets the value of field 'forceQualityNormal'.
     * 
     * @param forceQualityNormal the value of field
     * 'forceQualityNormal'.
     */
    public void setForceQualityNormal(java.lang.Boolean forceQualityNormal)
    {
        this._forceQualityNormal = forceQualityNormal;
    } //-- void setForceQualityNormal(java.lang.Boolean) 

    /**
     * Sets the value of field 'periodicRate'.
     * 
     * @param periodicRate the value of field 'periodicRate'.
     */
    public void setPeriodicRate(java.lang.Integer periodicRate)
    {
        this._periodicRate = periodicRate;
    } //-- void setPeriodicRate(java.lang.Integer) 

    /**
     * Sets the value of field 'updateType'.
     * 
     * @param updateType the value of field 'updateType'.
     */
    public void setUpdateType(com.cannontech.common.pao.definition.model.castor.types.UpdateTypeType updateType)
    {
        this._updateType = updateType;
    } //-- void setUpdateType(com.cannontech.common.pao.definition.model.castor.types.UpdateTypeType) 

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
        return (com.cannontech.common.pao.definition.model.castor.Calculation) Unmarshaller.unmarshal(com.cannontech.common.pao.definition.model.castor.Calculation.class, reader);
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
