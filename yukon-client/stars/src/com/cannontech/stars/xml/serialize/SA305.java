/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: SA305.java,v 1.8 2004/11/24 23:21:56 zyao Exp $
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
 * @version $Revision: 1.8 $ $Date: 2004/11/24 23:21:56 $
**/
public class SA305 implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _utility;

    /**
     * keeps track of state for field: _utility
    **/
    private boolean _has_utility;

    private int _group;

    /**
     * keeps track of state for field: _group
    **/
    private boolean _has_group;

    private int _division;

    /**
     * keeps track of state for field: _division
    **/
    private boolean _has_division;

    private int _substation;

    /**
     * keeps track of state for field: _substation
    **/
    private boolean _has_substation;

    private int _rateFamily;

    /**
     * keeps track of state for field: _rateFamily
    **/
    private boolean _has_rateFamily;

    private int _rateMember;

    /**
     * keeps track of state for field: _rateMember
    **/
    private boolean _has_rateMember;

    private int _rateHierarchy;

    /**
     * keeps track of state for field: _rateHierarchy
    **/
    private boolean _has_rateHierarchy;


      //----------------/
     //- Constructors -/
    //----------------/

    public SA305() {
        super();
    } //-- com.cannontech.stars.xml.serialize.SA305()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'division'.
     * 
     * @return the value of field 'division'.
    **/
    public int getDivision()
    {
        return this._division;
    } //-- int getDivision() 

    /**
     * Returns the value of field 'group'.
     * 
     * @return the value of field 'group'.
    **/
    public int getGroup()
    {
        return this._group;
    } //-- int getGroup() 

    /**
     * Returns the value of field 'rateFamily'.
     * 
     * @return the value of field 'rateFamily'.
    **/
    public int getRateFamily()
    {
        return this._rateFamily;
    } //-- int getRateFamily() 

    /**
     * Returns the value of field 'rateHierarchy'.
     * 
     * @return the value of field 'rateHierarchy'.
    **/
    public int getRateHierarchy()
    {
        return this._rateHierarchy;
    } //-- int getRateHierarchy() 

    /**
     * Returns the value of field 'rateMember'.
     * 
     * @return the value of field 'rateMember'.
    **/
    public int getRateMember()
    {
        return this._rateMember;
    } //-- int getRateMember() 

    /**
     * Returns the value of field 'substation'.
     * 
     * @return the value of field 'substation'.
    **/
    public int getSubstation()
    {
        return this._substation;
    } //-- int getSubstation() 

    /**
     * Returns the value of field 'utility'.
     * 
     * @return the value of field 'utility'.
    **/
    public int getUtility()
    {
        return this._utility;
    } //-- int getUtility() 

    /**
    **/
    public boolean hasDivision()
    {
        return this._has_division;
    } //-- boolean hasDivision() 

    /**
    **/
    public boolean hasGroup()
    {
        return this._has_group;
    } //-- boolean hasGroup() 

    /**
    **/
    public boolean hasRateFamily()
    {
        return this._has_rateFamily;
    } //-- boolean hasRateFamily() 

    /**
    **/
    public boolean hasRateHierarchy()
    {
        return this._has_rateHierarchy;
    } //-- boolean hasRateHierarchy() 

    /**
    **/
    public boolean hasRateMember()
    {
        return this._has_rateMember;
    } //-- boolean hasRateMember() 

    /**
    **/
    public boolean hasSubstation()
    {
        return this._has_substation;
    } //-- boolean hasSubstation() 

    /**
    **/
    public boolean hasUtility()
    {
        return this._has_utility;
    } //-- boolean hasUtility() 

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
     * Sets the value of field 'division'.
     * 
     * @param division the value of field 'division'.
    **/
    public void setDivision(int division)
    {
        this._division = division;
        this._has_division = true;
    } //-- void setDivision(int) 

    /**
     * Sets the value of field 'group'.
     * 
     * @param group the value of field 'group'.
    **/
    public void setGroup(int group)
    {
        this._group = group;
        this._has_group = true;
    } //-- void setGroup(int) 

    /**
     * Sets the value of field 'rateFamily'.
     * 
     * @param rateFamily the value of field 'rateFamily'.
    **/
    public void setRateFamily(int rateFamily)
    {
        this._rateFamily = rateFamily;
        this._has_rateFamily = true;
    } //-- void setRateFamily(int) 

    /**
     * Sets the value of field 'rateHierarchy'.
     * 
     * @param rateHierarchy the value of field 'rateHierarchy'.
    **/
    public void setRateHierarchy(int rateHierarchy)
    {
        this._rateHierarchy = rateHierarchy;
        this._has_rateHierarchy = true;
    } //-- void setRateHierarchy(int) 

    /**
     * Sets the value of field 'rateMember'.
     * 
     * @param rateMember the value of field 'rateMember'.
    **/
    public void setRateMember(int rateMember)
    {
        this._rateMember = rateMember;
        this._has_rateMember = true;
    } //-- void setRateMember(int) 

    /**
     * Sets the value of field 'substation'.
     * 
     * @param substation the value of field 'substation'.
    **/
    public void setSubstation(int substation)
    {
        this._substation = substation;
        this._has_substation = true;
    } //-- void setSubstation(int) 

    /**
     * Sets the value of field 'utility'.
     * 
     * @param utility the value of field 'utility'.
    **/
    public void setUtility(int utility)
    {
        this._utility = utility;
        this._has_utility = true;
    } //-- void setUtility(int) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.SA305 unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.SA305) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.SA305.class, reader);
    } //-- com.cannontech.stars.xml.serialize.SA305 unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
