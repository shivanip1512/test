/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsSubstations.java,v 1.2 2005/01/20 00:37:04 yao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision: 1.2 $ $Date: 2005/01/20 00:37:04 $
**/
public class StarsSubstations implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsSubstationList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSubstations() {
        super();
        _starsSubstationList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsSubstations()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsSubstation
    **/
    public void addStarsSubstation(StarsSubstation vStarsSubstation)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsSubstationList.addElement(vStarsSubstation);
    } //-- void addStarsSubstation(StarsSubstation) 

    /**
     * 
     * 
     * @param index
     * @param vStarsSubstation
    **/
    public void addStarsSubstation(int index, StarsSubstation vStarsSubstation)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsSubstationList.insertElementAt(vStarsSubstation, index);
    } //-- void addStarsSubstation(int, StarsSubstation) 

    /**
    **/
    public java.util.Enumeration enumerateStarsSubstation()
    {
        return _starsSubstationList.elements();
    } //-- java.util.Enumeration enumerateStarsSubstation() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsSubstation getStarsSubstation(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsSubstationList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsSubstation) _starsSubstationList.elementAt(index);
    } //-- StarsSubstation getStarsSubstation(int) 

    /**
    **/
    public StarsSubstation[] getStarsSubstation()
    {
        int size = _starsSubstationList.size();
        StarsSubstation[] mArray = new StarsSubstation[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsSubstation) _starsSubstationList.elementAt(index);
        }
        return mArray;
    } //-- StarsSubstation[] getStarsSubstation() 

    /**
    **/
    public int getStarsSubstationCount()
    {
        return _starsSubstationList.size();
    } //-- int getStarsSubstationCount() 

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
    public void removeAllStarsSubstation()
    {
        _starsSubstationList.removeAllElements();
    } //-- void removeAllStarsSubstation() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsSubstation removeStarsSubstation(int index)
    {
        java.lang.Object obj = _starsSubstationList.elementAt(index);
        _starsSubstationList.removeElementAt(index);
        return (StarsSubstation) obj;
    } //-- StarsSubstation removeStarsSubstation(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsSubstation
    **/
    public void setStarsSubstation(int index, StarsSubstation vStarsSubstation)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsSubstationList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsSubstationList.setElementAt(vStarsSubstation, index);
    } //-- void setStarsSubstation(int, StarsSubstation) 

    /**
     * 
     * 
     * @param starsSubstationArray
    **/
    public void setStarsSubstation(StarsSubstation[] starsSubstationArray)
    {
        //-- copy array
        _starsSubstationList.removeAllElements();
        for (int i = 0; i < starsSubstationArray.length; i++) {
            _starsSubstationList.addElement(starsSubstationArray[i]);
        }
    } //-- void setStarsSubstation(StarsSubstation) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsSubstations unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsSubstations) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsSubstations.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsSubstations unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
