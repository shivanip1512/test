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
 * Class FeaturesType.
 * 
 * @version $Revision$ $Date$
 */
public class FeaturesType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _featureList
     */
    private java.util.ArrayList _featureList;


      //----------------/
     //- Constructors -/
    //----------------/

    public FeaturesType() 
     {
        super();
        _featureList = new ArrayList();
    } //-- com.cannontech.common.device.definition.model.castor.FeaturesType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addFeature
     * 
     * 
     * 
     * @param vFeature
     */
    public void addFeature(com.cannontech.common.device.definition.model.castor.Feature vFeature)
        throws java.lang.IndexOutOfBoundsException
    {
        _featureList.add(vFeature);
    } //-- void addFeature(com.cannontech.common.device.definition.model.castor.Feature) 

    /**
     * Method addFeature
     * 
     * 
     * 
     * @param index
     * @param vFeature
     */
    public void addFeature(int index, com.cannontech.common.device.definition.model.castor.Feature vFeature)
        throws java.lang.IndexOutOfBoundsException
    {
        _featureList.add(index, vFeature);
    } //-- void addFeature(int, com.cannontech.common.device.definition.model.castor.Feature) 

    /**
     * Method clearFeature
     * 
     */
    public void clearFeature()
    {
        _featureList.clear();
    } //-- void clearFeature() 

    /**
     * Method enumerateFeature
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateFeature()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_featureList.iterator());
    } //-- java.util.Enumeration enumerateFeature() 

    /**
     * Method getFeature
     * 
     * 
     * 
     * @param index
     * @return Feature
     */
    public com.cannontech.common.device.definition.model.castor.Feature getFeature(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _featureList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.cannontech.common.device.definition.model.castor.Feature) _featureList.get(index);
    } //-- com.cannontech.common.device.definition.model.castor.Feature getFeature(int) 

    /**
     * Method getFeature
     * 
     * 
     * 
     * @return Feature
     */
    public com.cannontech.common.device.definition.model.castor.Feature[] getFeature()
    {
        int size = _featureList.size();
        com.cannontech.common.device.definition.model.castor.Feature[] mArray = new com.cannontech.common.device.definition.model.castor.Feature[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.cannontech.common.device.definition.model.castor.Feature) _featureList.get(index);
        }
        return mArray;
    } //-- com.cannontech.common.device.definition.model.castor.Feature[] getFeature() 

    /**
     * Method getFeatureCount
     * 
     * 
     * 
     * @return int
     */
    public int getFeatureCount()
    {
        return _featureList.size();
    } //-- int getFeatureCount() 

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
     * Method removeFeature
     * 
     * 
     * 
     * @param vFeature
     * @return boolean
     */
    public boolean removeFeature(com.cannontech.common.device.definition.model.castor.Feature vFeature)
    {
        boolean removed = _featureList.remove(vFeature);
        return removed;
    } //-- boolean removeFeature(com.cannontech.common.device.definition.model.castor.Feature) 

    /**
     * Method setFeature
     * 
     * 
     * 
     * @param index
     * @param vFeature
     */
    public void setFeature(int index, com.cannontech.common.device.definition.model.castor.Feature vFeature)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _featureList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _featureList.set(index, vFeature);
    } //-- void setFeature(int, com.cannontech.common.device.definition.model.castor.Feature) 

    /**
     * Method setFeature
     * 
     * 
     * 
     * @param featureArray
     */
    public void setFeature(com.cannontech.common.device.definition.model.castor.Feature[] featureArray)
    {
        //-- copy array
        _featureList.clear();
        for (int i = 0; i < featureArray.length; i++) {
            _featureList.add(featureArray[i]);
        }
    } //-- void setFeature(com.cannontech.common.device.definition.model.castor.Feature) 

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
        return (com.cannontech.common.device.definition.model.castor.FeaturesType) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.FeaturesType.class, reader);
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
