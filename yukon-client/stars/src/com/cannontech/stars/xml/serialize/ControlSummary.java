/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: ControlSummary.java,v 1.69 2004/03/24 23:09:38 zyao Exp $
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
 * @version $Revision: 1.69 $ $Date: 2004/03/24 23:09:38 $
**/
public class ControlSummary implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _dailyTime;

    /**
     * keeps track of state for field: _dailyTime
    **/
    private boolean _has_dailyTime;

    private int _monthlyTime;

    /**
     * keeps track of state for field: _monthlyTime
    **/
    private boolean _has_monthlyTime;

    private int _seasonalTime;

    /**
     * keeps track of state for field: _seasonalTime
    **/
    private boolean _has_seasonalTime;

    private int _annualTime;

    /**
     * keeps track of state for field: _annualTime
    **/
    private boolean _has_annualTime;


      //----------------/
     //- Constructors -/
    //----------------/

    public ControlSummary() {
        super();
    } //-- com.cannontech.stars.xml.serialize.ControlSummary()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'annualTime'.
     * 
     * @return the value of field 'annualTime'.
    **/
    public int getAnnualTime()
    {
        return this._annualTime;
    } //-- int getAnnualTime() 

    /**
     * Returns the value of field 'dailyTime'.
     * 
     * @return the value of field 'dailyTime'.
    **/
    public int getDailyTime()
    {
        return this._dailyTime;
    } //-- int getDailyTime() 

    /**
     * Returns the value of field 'monthlyTime'.
     * 
     * @return the value of field 'monthlyTime'.
    **/
    public int getMonthlyTime()
    {
        return this._monthlyTime;
    } //-- int getMonthlyTime() 

    /**
     * Returns the value of field 'seasonalTime'.
     * 
     * @return the value of field 'seasonalTime'.
    **/
    public int getSeasonalTime()
    {
        return this._seasonalTime;
    } //-- int getSeasonalTime() 

    /**
    **/
    public boolean hasAnnualTime()
    {
        return this._has_annualTime;
    } //-- boolean hasAnnualTime() 

    /**
    **/
    public boolean hasDailyTime()
    {
        return this._has_dailyTime;
    } //-- boolean hasDailyTime() 

    /**
    **/
    public boolean hasMonthlyTime()
    {
        return this._has_monthlyTime;
    } //-- boolean hasMonthlyTime() 

    /**
    **/
    public boolean hasSeasonalTime()
    {
        return this._has_seasonalTime;
    } //-- boolean hasSeasonalTime() 

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
     * Sets the value of field 'annualTime'.
     * 
     * @param annualTime the value of field 'annualTime'.
    **/
    public void setAnnualTime(int annualTime)
    {
        this._annualTime = annualTime;
        this._has_annualTime = true;
    } //-- void setAnnualTime(int) 

    /**
     * Sets the value of field 'dailyTime'.
     * 
     * @param dailyTime the value of field 'dailyTime'.
    **/
    public void setDailyTime(int dailyTime)
    {
        this._dailyTime = dailyTime;
        this._has_dailyTime = true;
    } //-- void setDailyTime(int) 

    /**
     * Sets the value of field 'monthlyTime'.
     * 
     * @param monthlyTime the value of field 'monthlyTime'.
    **/
    public void setMonthlyTime(int monthlyTime)
    {
        this._monthlyTime = monthlyTime;
        this._has_monthlyTime = true;
    } //-- void setMonthlyTime(int) 

    /**
     * Sets the value of field 'seasonalTime'.
     * 
     * @param seasonalTime the value of field 'seasonalTime'.
    **/
    public void setSeasonalTime(int seasonalTime)
    {
        this._seasonalTime = seasonalTime;
        this._has_seasonalTime = true;
    } //-- void setSeasonalTime(int) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.ControlSummary unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.ControlSummary) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.ControlSummary.class, reader);
    } //-- com.cannontech.stars.xml.serialize.ControlSummary unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
