/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsAppliances.java,v 1.62 2004/01/21 17:52:17 zyao Exp $
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
 * @version $Revision: 1.62 $ $Date: 2004/01/21 17:52:17 $
**/
public class StarsAppliances implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsApplianceList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsAppliances() {
        super();
        _starsApplianceList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsAppliances()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsAppliance
    **/
    public void addStarsAppliance(StarsAppliance vStarsAppliance)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsApplianceList.addElement(vStarsAppliance);
    } //-- void addStarsAppliance(StarsAppliance) 

    /**
     * 
     * 
     * @param index
     * @param vStarsAppliance
    **/
    public void addStarsAppliance(int index, StarsAppliance vStarsAppliance)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsApplianceList.insertElementAt(vStarsAppliance, index);
    } //-- void addStarsAppliance(int, StarsAppliance) 

    /**
    **/
    public java.util.Enumeration enumerateStarsAppliance()
    {
        return _starsApplianceList.elements();
    } //-- java.util.Enumeration enumerateStarsAppliance() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsAppliance getStarsAppliance(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsApplianceList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsAppliance) _starsApplianceList.elementAt(index);
    } //-- StarsAppliance getStarsAppliance(int) 

    /**
    **/
    public StarsAppliance[] getStarsAppliance()
    {
        int size = _starsApplianceList.size();
        StarsAppliance[] mArray = new StarsAppliance[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsAppliance) _starsApplianceList.elementAt(index);
        }
        return mArray;
    } //-- StarsAppliance[] getStarsAppliance() 

    /**
    **/
    public int getStarsApplianceCount()
    {
        return _starsApplianceList.size();
    } //-- int getStarsApplianceCount() 

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
    public void removeAllStarsAppliance()
    {
        _starsApplianceList.removeAllElements();
    } //-- void removeAllStarsAppliance() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsAppliance removeStarsAppliance(int index)
    {
        java.lang.Object obj = _starsApplianceList.elementAt(index);
        _starsApplianceList.removeElementAt(index);
        return (StarsAppliance) obj;
    } //-- StarsAppliance removeStarsAppliance(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsAppliance
    **/
    public void setStarsAppliance(int index, StarsAppliance vStarsAppliance)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsApplianceList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsApplianceList.setElementAt(vStarsAppliance, index);
    } //-- void setStarsAppliance(int, StarsAppliance) 

    /**
     * 
     * 
     * @param starsApplianceArray
    **/
    public void setStarsAppliance(StarsAppliance[] starsApplianceArray)
    {
        //-- copy array
        _starsApplianceList.removeAllElements();
        for (int i = 0; i < starsApplianceArray.length; i++) {
            _starsApplianceList.addElement(starsApplianceArray[i]);
        }
    } //-- void setStarsAppliance(StarsAppliance) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsAppliances unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsAppliances) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsAppliances.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsAppliances unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
