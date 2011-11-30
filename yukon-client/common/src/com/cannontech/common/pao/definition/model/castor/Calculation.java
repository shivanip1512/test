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
import java.util.ArrayList;
import java.util.Enumeration;
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
    private java.lang.String _updateType = "Constant";

    /**
     * Field _componentList
     */
    private java.util.ArrayList _componentList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Calculation() 
     {
        super();
        setUpdateType("Constant");
        _componentList = new ArrayList();
    } //-- com.cannontech.common.pao.definition.model.castor.Calculation()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addComponent
     * 
     * 
     * 
     * @param vComponent
     */
    public void addComponent(com.cannontech.common.pao.definition.model.castor.Component vComponent)
        throws java.lang.IndexOutOfBoundsException
    {
        _componentList.add(vComponent);
    } //-- void addComponent(com.cannontech.common.pao.definition.model.castor.Component) 

    /**
     * Method addComponent
     * 
     * 
     * 
     * @param index
     * @param vComponent
     */
    public void addComponent(int index, com.cannontech.common.pao.definition.model.castor.Component vComponent)
        throws java.lang.IndexOutOfBoundsException
    {
        _componentList.add(index, vComponent);
    } //-- void addComponent(int, com.cannontech.common.pao.definition.model.castor.Component) 

    /**
     * Method clearComponent
     * 
     */
    public void clearComponent()
    {
        _componentList.clear();
    } //-- void clearComponent() 

    /**
     * Method enumerateComponent
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateComponent()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_componentList.iterator());
    } //-- java.util.Enumeration enumerateComponent() 

    /**
     * Method getComponent
     * 
     * 
     * 
     * @param index
     * @return Component
     */
    public com.cannontech.common.pao.definition.model.castor.Component getComponent(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _componentList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.cannontech.common.pao.definition.model.castor.Component) _componentList.get(index);
    } //-- com.cannontech.common.pao.definition.model.castor.Component getComponent(int) 

    /**
     * Method getComponent
     * 
     * 
     * 
     * @return Component
     */
    public com.cannontech.common.pao.definition.model.castor.Component[] getComponent()
    {
        int size = _componentList.size();
        com.cannontech.common.pao.definition.model.castor.Component[] mArray = new com.cannontech.common.pao.definition.model.castor.Component[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.cannontech.common.pao.definition.model.castor.Component) _componentList.get(index);
        }
        return mArray;
    } //-- com.cannontech.common.pao.definition.model.castor.Component[] getComponent() 

    /**
     * Method getComponentCount
     * 
     * 
     * 
     * @return int
     */
    public int getComponentCount()
    {
        return _componentList.size();
    } //-- int getComponentCount() 

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
     * @return String
     * @return the value of field 'updateType'.
     */
    public java.lang.String getUpdateType()
    {
        return this._updateType;
    } //-- java.lang.String getUpdateType() 

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
     * Method removeComponent
     * 
     * 
     * 
     * @param vComponent
     * @return boolean
     */
    public boolean removeComponent(com.cannontech.common.pao.definition.model.castor.Component vComponent)
    {
        boolean removed = _componentList.remove(vComponent);
        return removed;
    } //-- boolean removeComponent(com.cannontech.common.pao.definition.model.castor.Component) 

    /**
     * Method setComponent
     * 
     * 
     * 
     * @param index
     * @param vComponent
     */
    public void setComponent(int index, com.cannontech.common.pao.definition.model.castor.Component vComponent)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _componentList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _componentList.set(index, vComponent);
    } //-- void setComponent(int, com.cannontech.common.pao.definition.model.castor.Component) 

    /**
     * Method setComponent
     * 
     * 
     * 
     * @param componentArray
     */
    public void setComponent(com.cannontech.common.pao.definition.model.castor.Component[] componentArray)
    {
        //-- copy array
        _componentList.clear();
        for (int i = 0; i < componentArray.length; i++) {
            _componentList.add(componentArray[i]);
        }
    } //-- void setComponent(com.cannontech.common.pao.definition.model.castor.Component) 

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
    public void setUpdateType(java.lang.String updateType)
    {
        this._updateType = updateType;
    } //-- void setUpdateType(java.lang.String) 

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
