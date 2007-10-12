/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id: Mct4xxLookup.java,v 1.1 2007/10/12 14:44:59 stacey Exp $
 */

package com.cannontech.common.device.definition.model.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.ArrayList;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class Mct4xxLookup.
 * 
 * @version $Revision: 1.1 $ $Date: 2007/10/12 14:44:59 $
 */
public class Mct4xxLookup implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _mappingList
     */
    private java.util.ArrayList _mappingList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Mct4xxLookup() 
     {
        super();
        _mappingList = new ArrayList();
    } //-- com.cannontech.common.device.definition.model.castor.Mct4xxLookup()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addMapping
     * 
     * 
     * 
     * @param vMapping
     */
    public void addMapping(com.cannontech.common.device.definition.model.castor.Mapping vMapping)
        throws java.lang.IndexOutOfBoundsException
    {
        _mappingList.add(vMapping);
    } //-- void addMapping(com.cannontech.common.device.definition.model.castor.Mapping) 

    /**
     * Method addMapping
     * 
     * 
     * 
     * @param index
     * @param vMapping
     */
    public void addMapping(int index, com.cannontech.common.device.definition.model.castor.Mapping vMapping)
        throws java.lang.IndexOutOfBoundsException
    {
        _mappingList.add(index, vMapping);
    } //-- void addMapping(int, com.cannontech.common.device.definition.model.castor.Mapping) 

    /**
     * Method clearMapping
     * 
     */
    public void clearMapping()
    {
        _mappingList.clear();
    } //-- void clearMapping() 

    /**
     * Method enumerateMapping
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateMapping()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_mappingList.iterator());
    } //-- java.util.Enumeration enumerateMapping() 

    /**
     * Method getMapping
     * 
     * 
     * 
     * @param index
     * @return Mapping
     */
    public com.cannontech.common.device.definition.model.castor.Mapping getMapping(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _mappingList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.cannontech.common.device.definition.model.castor.Mapping) _mappingList.get(index);
    } //-- com.cannontech.common.device.definition.model.castor.Mapping getMapping(int) 

    /**
     * Method getMapping
     * 
     * 
     * 
     * @return Mapping
     */
    public com.cannontech.common.device.definition.model.castor.Mapping[] getMapping()
    {
        int size = _mappingList.size();
        com.cannontech.common.device.definition.model.castor.Mapping[] mArray = new com.cannontech.common.device.definition.model.castor.Mapping[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.cannontech.common.device.definition.model.castor.Mapping) _mappingList.get(index);
        }
        return mArray;
    } //-- com.cannontech.common.device.definition.model.castor.Mapping[] getMapping() 

    /**
     * Method getMappingCount
     * 
     * 
     * 
     * @return int
     */
    public int getMappingCount()
    {
        return _mappingList.size();
    } //-- int getMappingCount() 

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
     * Method removeMapping
     * 
     * 
     * 
     * @param vMapping
     * @return boolean
     */
    public boolean removeMapping(com.cannontech.common.device.definition.model.castor.Mapping vMapping)
    {
        boolean removed = _mappingList.remove(vMapping);
        return removed;
    } //-- boolean removeMapping(com.cannontech.common.device.definition.model.castor.Mapping) 

    /**
     * Method setMapping
     * 
     * 
     * 
     * @param index
     * @param vMapping
     */
    public void setMapping(int index, com.cannontech.common.device.definition.model.castor.Mapping vMapping)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _mappingList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _mappingList.set(index, vMapping);
    } //-- void setMapping(int, com.cannontech.common.device.definition.model.castor.Mapping) 

    /**
     * Method setMapping
     * 
     * 
     * 
     * @param mappingArray
     */
    public void setMapping(com.cannontech.common.device.definition.model.castor.Mapping[] mappingArray)
    {
        //-- copy array
        _mappingList.clear();
        for (int i = 0; i < mappingArray.length; i++) {
            _mappingList.add(mappingArray[i]);
        }
    } //-- void setMapping(com.cannontech.common.device.definition.model.castor.Mapping) 

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
        return (com.cannontech.common.device.definition.model.castor.Mct4xxLookup) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.Mct4xxLookup.class, reader);
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
