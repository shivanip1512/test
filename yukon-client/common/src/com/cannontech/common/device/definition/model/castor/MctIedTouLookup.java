/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id: MctIedTouLookup.java,v 1.1 2007/10/12 14:44:59 stacey Exp $
 */

package com.cannontech.common.device.definition.model.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.ArrayList;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class MctIedTouLookup.
 * 
 * @version $Revision: 1.1 $ $Date: 2007/10/12 14:44:59 $
 */
public class MctIedTouLookup implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _touMappingList
     */
    private java.util.ArrayList _touMappingList;


      //----------------/
     //- Constructors -/
    //----------------/

    public MctIedTouLookup() 
     {
        super();
        _touMappingList = new ArrayList();
    } //-- com.cannontech.common.device.definition.model.castor.MctIedTouLookup()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addTouMapping
     * 
     * 
     * 
     * @param vTouMapping
     */
    public void addTouMapping(com.cannontech.common.device.definition.model.castor.TouMapping vTouMapping)
        throws java.lang.IndexOutOfBoundsException
    {
        _touMappingList.add(vTouMapping);
    } //-- void addTouMapping(com.cannontech.common.device.definition.model.castor.TouMapping) 

    /**
     * Method addTouMapping
     * 
     * 
     * 
     * @param index
     * @param vTouMapping
     */
    public void addTouMapping(int index, com.cannontech.common.device.definition.model.castor.TouMapping vTouMapping)
        throws java.lang.IndexOutOfBoundsException
    {
        _touMappingList.add(index, vTouMapping);
    } //-- void addTouMapping(int, com.cannontech.common.device.definition.model.castor.TouMapping) 

    /**
     * Method clearTouMapping
     * 
     */
    public void clearTouMapping()
    {
        _touMappingList.clear();
    } //-- void clearTouMapping() 

    /**
     * Method enumerateTouMapping
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateTouMapping()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_touMappingList.iterator());
    } //-- java.util.Enumeration enumerateTouMapping() 

    /**
     * Method getTouMapping
     * 
     * 
     * 
     * @param index
     * @return TouMapping
     */
    public com.cannontech.common.device.definition.model.castor.TouMapping getTouMapping(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _touMappingList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.cannontech.common.device.definition.model.castor.TouMapping) _touMappingList.get(index);
    } //-- com.cannontech.common.device.definition.model.castor.TouMapping getTouMapping(int) 

    /**
     * Method getTouMapping
     * 
     * 
     * 
     * @return TouMapping
     */
    public com.cannontech.common.device.definition.model.castor.TouMapping[] getTouMapping()
    {
        int size = _touMappingList.size();
        com.cannontech.common.device.definition.model.castor.TouMapping[] mArray = new com.cannontech.common.device.definition.model.castor.TouMapping[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.cannontech.common.device.definition.model.castor.TouMapping) _touMappingList.get(index);
        }
        return mArray;
    } //-- com.cannontech.common.device.definition.model.castor.TouMapping[] getTouMapping() 

    /**
     * Method getTouMappingCount
     * 
     * 
     * 
     * @return int
     */
    public int getTouMappingCount()
    {
        return _touMappingList.size();
    } //-- int getTouMappingCount() 

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
     * Method removeTouMapping
     * 
     * 
     * 
     * @param vTouMapping
     * @return boolean
     */
    public boolean removeTouMapping(com.cannontech.common.device.definition.model.castor.TouMapping vTouMapping)
    {
        boolean removed = _touMappingList.remove(vTouMapping);
        return removed;
    } //-- boolean removeTouMapping(com.cannontech.common.device.definition.model.castor.TouMapping) 

    /**
     * Method setTouMapping
     * 
     * 
     * 
     * @param index
     * @param vTouMapping
     */
    public void setTouMapping(int index, com.cannontech.common.device.definition.model.castor.TouMapping vTouMapping)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _touMappingList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _touMappingList.set(index, vTouMapping);
    } //-- void setTouMapping(int, com.cannontech.common.device.definition.model.castor.TouMapping) 

    /**
     * Method setTouMapping
     * 
     * 
     * 
     * @param touMappingArray
     */
    public void setTouMapping(com.cannontech.common.device.definition.model.castor.TouMapping[] touMappingArray)
    {
        //-- copy array
        _touMappingList.clear();
        for (int i = 0; i < touMappingArray.length; i++) {
            _touMappingList.add(touMappingArray[i]);
        }
    } //-- void setTouMapping(com.cannontech.common.device.definition.model.castor.TouMapping) 

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
        return (com.cannontech.common.device.definition.model.castor.MctIedTouLookup) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.MctIedTouLookup.class, reader);
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
