/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsInventories.java,v 1.60 2003/12/23 21:20:35 zyao Exp $
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
 * @version $Revision: 1.60 $ $Date: 2003/12/23 21:20:35 $
**/
public class StarsInventories implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsLMHardwareList;

    private java.util.Vector _starsMCTList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsInventories() {
        super();
        _starsLMHardwareList = new Vector();
        _starsMCTList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsInventories()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsLMHardware
    **/
    public void addStarsLMHardware(StarsLMHardware vStarsLMHardware)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMHardwareList.addElement(vStarsLMHardware);
    } //-- void addStarsLMHardware(StarsLMHardware) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMHardware
    **/
    public void addStarsLMHardware(int index, StarsLMHardware vStarsLMHardware)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMHardwareList.insertElementAt(vStarsLMHardware, index);
    } //-- void addStarsLMHardware(int, StarsLMHardware) 

    /**
     * 
     * 
     * @param vStarsMCT
    **/
    public void addStarsMCT(StarsMCT vStarsMCT)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsMCTList.addElement(vStarsMCT);
    } //-- void addStarsMCT(StarsMCT) 

    /**
     * 
     * 
     * @param index
     * @param vStarsMCT
    **/
    public void addStarsMCT(int index, StarsMCT vStarsMCT)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsMCTList.insertElementAt(vStarsMCT, index);
    } //-- void addStarsMCT(int, StarsMCT) 

    /**
    **/
    public java.util.Enumeration enumerateStarsLMHardware()
    {
        return _starsLMHardwareList.elements();
    } //-- java.util.Enumeration enumerateStarsLMHardware() 

    /**
    **/
    public java.util.Enumeration enumerateStarsMCT()
    {
        return _starsMCTList.elements();
    } //-- java.util.Enumeration enumerateStarsMCT() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMHardware getStarsLMHardware(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMHardwareList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsLMHardware) _starsLMHardwareList.elementAt(index);
    } //-- StarsLMHardware getStarsLMHardware(int) 

    /**
    **/
    public StarsLMHardware[] getStarsLMHardware()
    {
        int size = _starsLMHardwareList.size();
        StarsLMHardware[] mArray = new StarsLMHardware[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsLMHardware) _starsLMHardwareList.elementAt(index);
        }
        return mArray;
    } //-- StarsLMHardware[] getStarsLMHardware() 

    /**
    **/
    public int getStarsLMHardwareCount()
    {
        return _starsLMHardwareList.size();
    } //-- int getStarsLMHardwareCount() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsMCT getStarsMCT(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsMCTList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsMCT) _starsMCTList.elementAt(index);
    } //-- StarsMCT getStarsMCT(int) 

    /**
    **/
    public StarsMCT[] getStarsMCT()
    {
        int size = _starsMCTList.size();
        StarsMCT[] mArray = new StarsMCT[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsMCT) _starsMCTList.elementAt(index);
        }
        return mArray;
    } //-- StarsMCT[] getStarsMCT() 

    /**
    **/
    public int getStarsMCTCount()
    {
        return _starsMCTList.size();
    } //-- int getStarsMCTCount() 

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
    public void removeAllStarsLMHardware()
    {
        _starsLMHardwareList.removeAllElements();
    } //-- void removeAllStarsLMHardware() 

    /**
    **/
    public void removeAllStarsMCT()
    {
        _starsMCTList.removeAllElements();
    } //-- void removeAllStarsMCT() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMHardware removeStarsLMHardware(int index)
    {
        java.lang.Object obj = _starsLMHardwareList.elementAt(index);
        _starsLMHardwareList.removeElementAt(index);
        return (StarsLMHardware) obj;
    } //-- StarsLMHardware removeStarsLMHardware(int) 

    /**
     * 
     * 
     * @param index
    **/
    public StarsMCT removeStarsMCT(int index)
    {
        java.lang.Object obj = _starsMCTList.elementAt(index);
        _starsMCTList.removeElementAt(index);
        return (StarsMCT) obj;
    } //-- StarsMCT removeStarsMCT(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMHardware
    **/
    public void setStarsLMHardware(int index, StarsLMHardware vStarsLMHardware)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMHardwareList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsLMHardwareList.setElementAt(vStarsLMHardware, index);
    } //-- void setStarsLMHardware(int, StarsLMHardware) 

    /**
     * 
     * 
     * @param starsLMHardwareArray
    **/
    public void setStarsLMHardware(StarsLMHardware[] starsLMHardwareArray)
    {
        //-- copy array
        _starsLMHardwareList.removeAllElements();
        for (int i = 0; i < starsLMHardwareArray.length; i++) {
            _starsLMHardwareList.addElement(starsLMHardwareArray[i]);
        }
    } //-- void setStarsLMHardware(StarsLMHardware) 

    /**
     * 
     * 
     * @param index
     * @param vStarsMCT
    **/
    public void setStarsMCT(int index, StarsMCT vStarsMCT)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsMCTList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsMCTList.setElementAt(vStarsMCT, index);
    } //-- void setStarsMCT(int, StarsMCT) 

    /**
     * 
     * 
     * @param starsMCTArray
    **/
    public void setStarsMCT(StarsMCT[] starsMCTArray)
    {
        //-- copy array
        _starsMCTList.removeAllElements();
        for (int i = 0; i < starsMCTArray.length; i++) {
            _starsMCTList.addElement(starsMCTArray[i]);
        }
    } //-- void setStarsMCT(StarsMCT) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsInventories unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsInventories) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsInventories.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsInventories unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
