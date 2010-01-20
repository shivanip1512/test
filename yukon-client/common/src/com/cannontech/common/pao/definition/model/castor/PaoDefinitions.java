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
 * Class PaoDefinitions.
 * 
 * @version $Revision$ $Date$
 */
public class PaoDefinitions implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _paoList
     */
    private java.util.ArrayList _paoList;


      //----------------/
     //- Constructors -/
    //----------------/

    public PaoDefinitions() 
     {
        super();
        _paoList = new ArrayList();
    } //-- com.cannontech.common.pao.definition.model.castor.PaoDefinitions()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addPao
     * 
     * 
     * 
     * @param vPao
     */
    public void addPao(com.cannontech.common.pao.definition.model.castor.Pao vPao)
        throws java.lang.IndexOutOfBoundsException
    {
        _paoList.add(vPao);
    } //-- void addPao(com.cannontech.common.pao.definition.model.castor.Pao) 

    /**
     * Method addPao
     * 
     * 
     * 
     * @param index
     * @param vPao
     */
    public void addPao(int index, com.cannontech.common.pao.definition.model.castor.Pao vPao)
        throws java.lang.IndexOutOfBoundsException
    {
        _paoList.add(index, vPao);
    } //-- void addPao(int, com.cannontech.common.pao.definition.model.castor.Pao) 

    /**
     * Method clearPao
     * 
     */
    public void clearPao()
    {
        _paoList.clear();
    } //-- void clearPao() 

    /**
     * Method enumeratePao
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumeratePao()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_paoList.iterator());
    } //-- java.util.Enumeration enumeratePao() 

    /**
     * Method getPao
     * 
     * 
     * 
     * @param index
     * @return Pao
     */
    public com.cannontech.common.pao.definition.model.castor.Pao getPao(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _paoList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.cannontech.common.pao.definition.model.castor.Pao) _paoList.get(index);
    } //-- com.cannontech.common.pao.definition.model.castor.Pao getPao(int) 

    /**
     * Method getPao
     * 
     * 
     * 
     * @return Pao
     */
    public com.cannontech.common.pao.definition.model.castor.Pao[] getPao()
    {
        int size = _paoList.size();
        com.cannontech.common.pao.definition.model.castor.Pao[] mArray = new com.cannontech.common.pao.definition.model.castor.Pao[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.cannontech.common.pao.definition.model.castor.Pao) _paoList.get(index);
        }
        return mArray;
    } //-- com.cannontech.common.pao.definition.model.castor.Pao[] getPao() 

    /**
     * Method getPaoCount
     * 
     * 
     * 
     * @return int
     */
    public int getPaoCount()
    {
        return _paoList.size();
    } //-- int getPaoCount() 

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
     * Method removePao
     * 
     * 
     * 
     * @param vPao
     * @return boolean
     */
    public boolean removePao(com.cannontech.common.pao.definition.model.castor.Pao vPao)
    {
        boolean removed = _paoList.remove(vPao);
        return removed;
    } //-- boolean removePao(com.cannontech.common.pao.definition.model.castor.Pao) 

    /**
     * Method setPao
     * 
     * 
     * 
     * @param index
     * @param vPao
     */
    public void setPao(int index, com.cannontech.common.pao.definition.model.castor.Pao vPao)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _paoList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _paoList.set(index, vPao);
    } //-- void setPao(int, com.cannontech.common.pao.definition.model.castor.Pao) 

    /**
     * Method setPao
     * 
     * 
     * 
     * @param paoArray
     */
    public void setPao(com.cannontech.common.pao.definition.model.castor.Pao[] paoArray)
    {
        //-- copy array
        _paoList.clear();
        for (int i = 0; i < paoArray.length; i++) {
            _paoList.add(paoArray[i]);
        }
    } //-- void setPao(com.cannontech.common.pao.definition.model.castor.Pao) 

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
        return (com.cannontech.common.pao.definition.model.castor.PaoDefinitions) Unmarshaller.unmarshal(com.cannontech.common.pao.definition.model.castor.PaoDefinitions.class, reader);
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
