/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id: Command.java,v 1.3 2007/10/12 14:44:59 stacey Exp $
 */

package com.cannontech.common.device.definition.model.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.ArrayList;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class Command.
 * 
 * @version $Revision: 1.3 $ $Date: 2007/10/12 14:44:59 $
 */
public class Command implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _cmdList
     */
    private java.util.ArrayList _cmdList;

    /**
     * Field _pointRefList
     */
    private java.util.ArrayList _pointRefList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Command() 
     {
        super();
        _cmdList = new ArrayList();
        _pointRefList = new ArrayList();
    } //-- com.cannontech.common.device.definition.model.castor.Command()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addCmd
     * 
     * 
     * 
     * @param vCmd
     */
    public void addCmd(com.cannontech.common.device.definition.model.castor.Cmd vCmd)
        throws java.lang.IndexOutOfBoundsException
    {
        _cmdList.add(vCmd);
    } //-- void addCmd(com.cannontech.common.device.definition.model.castor.Cmd) 

    /**
     * Method addCmd
     * 
     * 
     * 
     * @param index
     * @param vCmd
     */
    public void addCmd(int index, com.cannontech.common.device.definition.model.castor.Cmd vCmd)
        throws java.lang.IndexOutOfBoundsException
    {
        _cmdList.add(index, vCmd);
    } //-- void addCmd(int, com.cannontech.common.device.definition.model.castor.Cmd) 

    /**
     * Method addPointRef
     * 
     * 
     * 
     * @param vPointRef
     */
    public void addPointRef(com.cannontech.common.device.definition.model.castor.PointRef vPointRef)
        throws java.lang.IndexOutOfBoundsException
    {
        _pointRefList.add(vPointRef);
    } //-- void addPointRef(com.cannontech.common.device.definition.model.castor.PointRef) 

    /**
     * Method addPointRef
     * 
     * 
     * 
     * @param index
     * @param vPointRef
     */
    public void addPointRef(int index, com.cannontech.common.device.definition.model.castor.PointRef vPointRef)
        throws java.lang.IndexOutOfBoundsException
    {
        _pointRefList.add(index, vPointRef);
    } //-- void addPointRef(int, com.cannontech.common.device.definition.model.castor.PointRef) 

    /**
     * Method clearCmd
     * 
     */
    public void clearCmd()
    {
        _cmdList.clear();
    } //-- void clearCmd() 

    /**
     * Method clearPointRef
     * 
     */
    public void clearPointRef()
    {
        _pointRefList.clear();
    } //-- void clearPointRef() 

    /**
     * Method enumerateCmd
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateCmd()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_cmdList.iterator());
    } //-- java.util.Enumeration enumerateCmd() 

    /**
     * Method enumeratePointRef
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumeratePointRef()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_pointRefList.iterator());
    } //-- java.util.Enumeration enumeratePointRef() 

    /**
     * Method getCmd
     * 
     * 
     * 
     * @param index
     * @return Cmd
     */
    public com.cannontech.common.device.definition.model.castor.Cmd getCmd(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _cmdList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.cannontech.common.device.definition.model.castor.Cmd) _cmdList.get(index);
    } //-- com.cannontech.common.device.definition.model.castor.Cmd getCmd(int) 

    /**
     * Method getCmd
     * 
     * 
     * 
     * @return Cmd
     */
    public com.cannontech.common.device.definition.model.castor.Cmd[] getCmd()
    {
        int size = _cmdList.size();
        com.cannontech.common.device.definition.model.castor.Cmd[] mArray = new com.cannontech.common.device.definition.model.castor.Cmd[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.cannontech.common.device.definition.model.castor.Cmd) _cmdList.get(index);
        }
        return mArray;
    } //-- com.cannontech.common.device.definition.model.castor.Cmd[] getCmd() 

    /**
     * Method getCmdCount
     * 
     * 
     * 
     * @return int
     */
    public int getCmdCount()
    {
        return _cmdList.size();
    } //-- int getCmdCount() 

    /**
     * Returns the value of field 'name'.
     * 
     * @return String
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Method getPointRef
     * 
     * 
     * 
     * @param index
     * @return PointRef
     */
    public com.cannontech.common.device.definition.model.castor.PointRef getPointRef(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _pointRefList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.cannontech.common.device.definition.model.castor.PointRef) _pointRefList.get(index);
    } //-- com.cannontech.common.device.definition.model.castor.PointRef getPointRef(int) 

    /**
     * Method getPointRef
     * 
     * 
     * 
     * @return PointRef
     */
    public com.cannontech.common.device.definition.model.castor.PointRef[] getPointRef()
    {
        int size = _pointRefList.size();
        com.cannontech.common.device.definition.model.castor.PointRef[] mArray = new com.cannontech.common.device.definition.model.castor.PointRef[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.cannontech.common.device.definition.model.castor.PointRef) _pointRefList.get(index);
        }
        return mArray;
    } //-- com.cannontech.common.device.definition.model.castor.PointRef[] getPointRef() 

    /**
     * Method getPointRefCount
     * 
     * 
     * 
     * @return int
     */
    public int getPointRefCount()
    {
        return _pointRefList.size();
    } //-- int getPointRefCount() 

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
     * Method removeCmd
     * 
     * 
     * 
     * @param vCmd
     * @return boolean
     */
    public boolean removeCmd(com.cannontech.common.device.definition.model.castor.Cmd vCmd)
    {
        boolean removed = _cmdList.remove(vCmd);
        return removed;
    } //-- boolean removeCmd(com.cannontech.common.device.definition.model.castor.Cmd) 

    /**
     * Method removePointRef
     * 
     * 
     * 
     * @param vPointRef
     * @return boolean
     */
    public boolean removePointRef(com.cannontech.common.device.definition.model.castor.PointRef vPointRef)
    {
        boolean removed = _pointRefList.remove(vPointRef);
        return removed;
    } //-- boolean removePointRef(com.cannontech.common.device.definition.model.castor.PointRef) 

    /**
     * Method setCmd
     * 
     * 
     * 
     * @param index
     * @param vCmd
     */
    public void setCmd(int index, com.cannontech.common.device.definition.model.castor.Cmd vCmd)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _cmdList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _cmdList.set(index, vCmd);
    } //-- void setCmd(int, com.cannontech.common.device.definition.model.castor.Cmd) 

    /**
     * Method setCmd
     * 
     * 
     * 
     * @param cmdArray
     */
    public void setCmd(com.cannontech.common.device.definition.model.castor.Cmd[] cmdArray)
    {
        //-- copy array
        _cmdList.clear();
        for (int i = 0; i < cmdArray.length; i++) {
            _cmdList.add(cmdArray[i]);
        }
    } //-- void setCmd(com.cannontech.common.device.definition.model.castor.Cmd) 

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Method setPointRef
     * 
     * 
     * 
     * @param index
     * @param vPointRef
     */
    public void setPointRef(int index, com.cannontech.common.device.definition.model.castor.PointRef vPointRef)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _pointRefList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _pointRefList.set(index, vPointRef);
    } //-- void setPointRef(int, com.cannontech.common.device.definition.model.castor.PointRef) 

    /**
     * Method setPointRef
     * 
     * 
     * 
     * @param pointRefArray
     */
    public void setPointRef(com.cannontech.common.device.definition.model.castor.PointRef[] pointRefArray)
    {
        //-- copy array
        _pointRefList.clear();
        for (int i = 0; i < pointRefArray.length; i++) {
            _pointRefList.add(pointRefArray[i]);
        }
    } //-- void setPointRef(com.cannontech.common.device.definition.model.castor.PointRef) 

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
        return (com.cannontech.common.device.definition.model.castor.Command) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.Command.class, reader);
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
