/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id: AttributesType.java,v 1.2 2007/12/26 17:53:34 tmack Exp $
 */

package com.cannontech.common.device.definition.model.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.ArrayList;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class AttributesType.
 * 
 * @version $Revision: 1.2 $ $Date: 2007/12/26 17:53:34 $
 */
public class AttributesType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _attributeList
     */
    private java.util.ArrayList _attributeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public AttributesType() 
     {
        super();
        _attributeList = new ArrayList();
    } //-- com.cannontech.common.device.definition.model.castor.AttributesType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addAttribute
     * 
     * 
     * 
     * @param vAttribute
     */
    public void addAttribute(com.cannontech.common.device.definition.model.castor.Attribute vAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        _attributeList.add(vAttribute);
    } //-- void addAttribute(com.cannontech.common.device.definition.model.castor.Attribute) 

    /**
     * Method addAttribute
     * 
     * 
     * 
     * @param index
     * @param vAttribute
     */
    public void addAttribute(int index, com.cannontech.common.device.definition.model.castor.Attribute vAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        _attributeList.add(index, vAttribute);
    } //-- void addAttribute(int, com.cannontech.common.device.definition.model.castor.Attribute) 

    /**
     * Method clearAttribute
     * 
     */
    public void clearAttribute()
    {
        _attributeList.clear();
    } //-- void clearAttribute() 

    /**
     * Method enumerateAttribute
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateAttribute()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_attributeList.iterator());
    } //-- java.util.Enumeration enumerateAttribute() 

    /**
     * Method getAttribute
     * 
     * 
     * 
     * @param index
     * @return Attribute
     */
    public com.cannontech.common.device.definition.model.castor.Attribute getAttribute(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _attributeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.cannontech.common.device.definition.model.castor.Attribute) _attributeList.get(index);
    } //-- com.cannontech.common.device.definition.model.castor.Attribute getAttribute(int) 

    /**
     * Method getAttribute
     * 
     * 
     * 
     * @return Attribute
     */
    public com.cannontech.common.device.definition.model.castor.Attribute[] getAttribute()
    {
        int size = _attributeList.size();
        com.cannontech.common.device.definition.model.castor.Attribute[] mArray = new com.cannontech.common.device.definition.model.castor.Attribute[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.cannontech.common.device.definition.model.castor.Attribute) _attributeList.get(index);
        }
        return mArray;
    } //-- com.cannontech.common.device.definition.model.castor.Attribute[] getAttribute() 

    /**
     * Method getAttributeCount
     * 
     * 
     * 
     * @return int
     */
    public int getAttributeCount()
    {
        return _attributeList.size();
    } //-- int getAttributeCount() 

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
     * Method removeAttribute
     * 
     * 
     * 
     * @param vAttribute
     * @return boolean
     */
    public boolean removeAttribute(com.cannontech.common.device.definition.model.castor.Attribute vAttribute)
    {
        boolean removed = _attributeList.remove(vAttribute);
        return removed;
    } //-- boolean removeAttribute(com.cannontech.common.device.definition.model.castor.Attribute) 

    /**
     * Method setAttribute
     * 
     * 
     * 
     * @param index
     * @param vAttribute
     */
    public void setAttribute(int index, com.cannontech.common.device.definition.model.castor.Attribute vAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _attributeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _attributeList.set(index, vAttribute);
    } //-- void setAttribute(int, com.cannontech.common.device.definition.model.castor.Attribute) 

    /**
     * Method setAttribute
     * 
     * 
     * 
     * @param attributeArray
     */
    public void setAttribute(com.cannontech.common.device.definition.model.castor.Attribute[] attributeArray)
    {
        //-- copy array
        _attributeList.clear();
        for (int i = 0; i < attributeArray.length; i++) {
            _attributeList.add(attributeArray[i]);
        }
    } //-- void setAttribute(com.cannontech.common.device.definition.model.castor.Attribute) 

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
        return (com.cannontech.common.device.definition.model.castor.AttributesType) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.AttributesType.class, reader);
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
