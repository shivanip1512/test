/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.honeywell.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Vector;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class DataSetALL implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _items;


      //----------------/
     //- Constructors -/
    //----------------/

    public DataSetALL() {
        super();
        _items = new Vector();
    } //-- com.cannontech.stars.honeywell.serialize.DataSetALL()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vDataSetALLItem
    **/
    public void addDataSetALLItem(com.cannontech.stars.honeywell.serialize.DataSetALLItem vDataSetALLItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.addElement(vDataSetALLItem);
    } //-- void addDataSetALLItem(com.cannontech.stars.honeywell.serialize.DataSetALLItem) 

    /**
     * 
     * 
     * @param index
     * @param vDataSetALLItem
    **/
    public void addDataSetALLItem(int index, com.cannontech.stars.honeywell.serialize.DataSetALLItem vDataSetALLItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.insertElementAt(vDataSetALLItem, index);
    } //-- void addDataSetALLItem(int, com.cannontech.stars.honeywell.serialize.DataSetALLItem) 

    /**
    **/
    public java.util.Enumeration enumerateDataSetALLItem()
    {
        return _items.elements();
    } //-- java.util.Enumeration enumerateDataSetALLItem() 

    /**
     * 
     * 
     * @param index
    **/
    public com.cannontech.stars.honeywell.serialize.DataSetALLItem getDataSetALLItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.cannontech.stars.honeywell.serialize.DataSetALLItem) _items.elementAt(index);
    } //-- com.cannontech.stars.honeywell.serialize.DataSetALLItem getDataSetALLItem(int) 

    /**
    **/
    public com.cannontech.stars.honeywell.serialize.DataSetALLItem[] getDataSetALLItem()
    {
        int size = _items.size();
        com.cannontech.stars.honeywell.serialize.DataSetALLItem[] mArray = new com.cannontech.stars.honeywell.serialize.DataSetALLItem[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.cannontech.stars.honeywell.serialize.DataSetALLItem) _items.elementAt(index);
        }
        return mArray;
    } //-- com.cannontech.stars.honeywell.serialize.DataSetALLItem[] getDataSetALLItem() 

    /**
    **/
    public int getDataSetALLItemCount()
    {
        return _items.size();
    } //-- int getDataSetALLItemCount() 

    /**
    **/
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
     * 
     * 
     * @param out
    **/
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
    **/
    public void removeAllDataSetALLItem()
    {
        _items.removeAllElements();
    } //-- void removeAllDataSetALLItem() 

    /**
     * 
     * 
     * @param index
    **/
    public com.cannontech.stars.honeywell.serialize.DataSetALLItem removeDataSetALLItem(int index)
    {
        java.lang.Object obj = _items.elementAt(index);
        _items.removeElementAt(index);
        return (com.cannontech.stars.honeywell.serialize.DataSetALLItem) obj;
    } //-- com.cannontech.stars.honeywell.serialize.DataSetALLItem removeDataSetALLItem(int) 

    /**
     * 
     * 
     * @param index
     * @param vDataSetALLItem
    **/
    public void setDataSetALLItem(int index, com.cannontech.stars.honeywell.serialize.DataSetALLItem vDataSetALLItem)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException();
        }
        _items.setElementAt(vDataSetALLItem, index);
    } //-- void setDataSetALLItem(int, com.cannontech.stars.honeywell.serialize.DataSetALLItem) 

    /**
     * 
     * 
     * @param dataSetALLItemArray
    **/
    public void setDataSetALLItem(com.cannontech.stars.honeywell.serialize.DataSetALLItem[] dataSetALLItemArray)
    {
        //-- copy array
        _items.removeAllElements();
        for (int i = 0; i < dataSetALLItemArray.length; i++) {
            _items.addElement(dataSetALLItemArray[i]);
        }
    } //-- void setDataSetALLItem(com.cannontech.stars.honeywell.serialize.DataSetALLItem) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.honeywell.serialize.DataSetALL unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.honeywell.serialize.DataSetALL) Unmarshaller.unmarshal(com.cannontech.stars.honeywell.serialize.DataSetALL.class, reader);
    } //-- com.cannontech.stars.honeywell.serialize.DataSetALL unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
