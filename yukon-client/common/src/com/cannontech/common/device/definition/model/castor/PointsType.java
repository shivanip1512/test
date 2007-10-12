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

import java.util.ArrayList;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class PointsType.
 * 
 * @version $Revision$ $Date$
 */
public class PointsType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id
     */
    private java.lang.String _id;

    /**
     * Field _pointList
     */
    private java.util.ArrayList _pointList;


      //----------------/
     //- Constructors -/
    //----------------/

    public PointsType() 
     {
        super();
        _pointList = new ArrayList();
    } //-- com.cannontech.common.device.definition.model.castor.PointsType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addPoint
     * 
     * 
     * 
     * @param vPoint
     */
    public void addPoint(com.cannontech.common.device.definition.model.castor.Point vPoint)
        throws java.lang.IndexOutOfBoundsException
    {
        _pointList.add(vPoint);
    } //-- void addPoint(com.cannontech.common.device.definition.model.castor.Point) 

    /**
     * Method addPoint
     * 
     * 
     * 
     * @param index
     * @param vPoint
     */
    public void addPoint(int index, com.cannontech.common.device.definition.model.castor.Point vPoint)
        throws java.lang.IndexOutOfBoundsException
    {
        _pointList.add(index, vPoint);
    } //-- void addPoint(int, com.cannontech.common.device.definition.model.castor.Point) 

    /**
     * Method clearPoint
     * 
     */
    public void clearPoint()
    {
        _pointList.clear();
    } //-- void clearPoint() 

    /**
     * Method enumeratePoint
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumeratePoint()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_pointList.iterator());
    } //-- java.util.Enumeration enumeratePoint() 

    /**
     * Returns the value of field 'id'.
     * 
     * @return String
     * @return the value of field 'id'.
     */
    public java.lang.String getId()
    {
        return this._id;
    } //-- java.lang.String getId() 

    /**
     * Method getPoint
     * 
     * 
     * 
     * @param index
     * @return Point
     */
    public com.cannontech.common.device.definition.model.castor.Point getPoint(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _pointList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.cannontech.common.device.definition.model.castor.Point) _pointList.get(index);
    } //-- com.cannontech.common.device.definition.model.castor.Point getPoint(int) 

    /**
     * Method getPoint
     * 
     * 
     * 
     * @return Point
     */
    public com.cannontech.common.device.definition.model.castor.Point[] getPoint()
    {
        int size = _pointList.size();
        com.cannontech.common.device.definition.model.castor.Point[] mArray = new com.cannontech.common.device.definition.model.castor.Point[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.cannontech.common.device.definition.model.castor.Point) _pointList.get(index);
        }
        return mArray;
    } //-- com.cannontech.common.device.definition.model.castor.Point[] getPoint() 

    /**
     * Method getPointCount
     * 
     * 
     * 
     * @return int
     */
    public int getPointCount()
    {
        return _pointList.size();
    } //-- int getPointCount() 

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
     * Method removePoint
     * 
     * 
     * 
     * @param vPoint
     * @return boolean
     */
    public boolean removePoint(com.cannontech.common.device.definition.model.castor.Point vPoint)
    {
        boolean removed = _pointList.remove(vPoint);
        return removed;
    } //-- boolean removePoint(com.cannontech.common.device.definition.model.castor.Point) 

    /**
     * Sets the value of field 'id'.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(java.lang.String id)
    {
        this._id = id;
    } //-- void setId(java.lang.String) 

    /**
     * Method setPoint
     * 
     * 
     * 
     * @param index
     * @param vPoint
     */
    public void setPoint(int index, com.cannontech.common.device.definition.model.castor.Point vPoint)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _pointList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _pointList.set(index, vPoint);
    } //-- void setPoint(int, com.cannontech.common.device.definition.model.castor.Point) 

    /**
     * Method setPoint
     * 
     * 
     * 
     * @param pointArray
     */
    public void setPoint(com.cannontech.common.device.definition.model.castor.Point[] pointArray)
    {
        //-- copy array
        _pointList.clear();
        for (int i = 0; i < pointArray.length; i++) {
            _pointList.add(pointArray[i]);
        }
    } //-- void setPoint(com.cannontech.common.device.definition.model.castor.Point) 

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
        return (com.cannontech.common.device.definition.model.castor.PointsType) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.PointsType.class, reader);
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
