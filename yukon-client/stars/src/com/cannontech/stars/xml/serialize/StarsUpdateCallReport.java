/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.xml.serialize;

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
public class StarsUpdateCallReport implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsCallReportList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUpdateCallReport() {
        super();
        _starsCallReportList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateCallReport()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsCallReport
    **/
    public void addStarsCallReport(StarsCallReport vStarsCallReport)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsCallReportList.addElement(vStarsCallReport);
    } //-- void addStarsCallReport(StarsCallReport) 

    /**
     * 
     * 
     * @param index
     * @param vStarsCallReport
    **/
    public void addStarsCallReport(int index, StarsCallReport vStarsCallReport)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsCallReportList.insertElementAt(vStarsCallReport, index);
    } //-- void addStarsCallReport(int, StarsCallReport) 

    /**
    **/
    public java.util.Enumeration enumerateStarsCallReport()
    {
        return _starsCallReportList.elements();
    } //-- java.util.Enumeration enumerateStarsCallReport() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsCallReport getStarsCallReport(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCallReportList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsCallReport) _starsCallReportList.elementAt(index);
    } //-- StarsCallReport getStarsCallReport(int) 

    /**
    **/
    public StarsCallReport[] getStarsCallReport()
    {
        int size = _starsCallReportList.size();
        StarsCallReport[] mArray = new StarsCallReport[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsCallReport) _starsCallReportList.elementAt(index);
        }
        return mArray;
    } //-- StarsCallReport[] getStarsCallReport() 

    /**
    **/
    public int getStarsCallReportCount()
    {
        return _starsCallReportList.size();
    } //-- int getStarsCallReportCount() 

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
    public void removeAllStarsCallReport()
    {
        _starsCallReportList.removeAllElements();
    } //-- void removeAllStarsCallReport() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsCallReport removeStarsCallReport(int index)
    {
        java.lang.Object obj = _starsCallReportList.elementAt(index);
        _starsCallReportList.removeElementAt(index);
        return (StarsCallReport) obj;
    } //-- StarsCallReport removeStarsCallReport(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsCallReport
    **/
    public void setStarsCallReport(int index, StarsCallReport vStarsCallReport)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCallReportList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsCallReportList.setElementAt(vStarsCallReport, index);
    } //-- void setStarsCallReport(int, StarsCallReport) 

    /**
     * 
     * 
     * @param starsCallReportArray
    **/
    public void setStarsCallReport(StarsCallReport[] starsCallReportArray)
    {
        //-- copy array
        _starsCallReportList.removeAllElements();
        for (int i = 0; i < starsCallReportArray.length; i++) {
            _starsCallReportList.addElement(starsCallReportArray[i]);
        }
    } //-- void setStarsCallReport(StarsCallReport) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsUpdateCallReport unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsUpdateCallReport) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsUpdateCallReport.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateCallReport unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
