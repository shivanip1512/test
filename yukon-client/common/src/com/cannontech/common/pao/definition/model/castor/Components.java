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
 * Class Components.
 * 
 * @version $Revision$ $Date$
 */
public class Components implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _componentList
     */
    private java.util.ArrayList _componentList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Components() 
     {
        super();
        _componentList = new ArrayList();
    } //-- com.cannontech.common.pao.definition.model.castor.Components()


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
        return (com.cannontech.common.pao.definition.model.castor.Components) Unmarshaller.unmarshal(com.cannontech.common.pao.definition.model.castor.Components.class, reader);
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
