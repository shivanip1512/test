/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsUpdateCallReportResponse.java,v 1.24 2004/06/25 21:37:03 zyao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision: 1.24 $ $Date: 2004/06/25 21:37:03 $
**/
public class StarsUpdateCallReportResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsCallReport _starsCallReport;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUpdateCallReportResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateCallReportResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsCallReport'.
     * 
     * @return the value of field 'starsCallReport'.
    **/
    public StarsCallReport getStarsCallReport()
    {
        return this._starsCallReport;
    } //-- StarsCallReport getStarsCallReport() 

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
     * Sets the value of field 'starsCallReport'.
     * 
     * @param starsCallReport the value of field 'starsCallReport'.
    **/
    public void setStarsCallReport(StarsCallReport starsCallReport)
    {
        this._starsCallReport = starsCallReport;
    } //-- void setStarsCallReport(StarsCallReport) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsUpdateCallReportResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsUpdateCallReportResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsUpdateCallReportResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateCallReportResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
