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

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class GrainDryer implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private DryerType _dryerType;

    private BinSize _binSize;

    private BlowerEnergySource _blowerEnergySource;

    private BlowerHorsePower _blowerHorsePower;

    private BlowerHeatSource _blowerHeatSource;


      //----------------/
     //- Constructors -/
    //----------------/

    public GrainDryer() {
        super();
    } //-- com.cannontech.stars.xml.serialize.GrainDryer()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'binSize'.
     * 
     * @return the value of field 'binSize'.
    **/
    public BinSize getBinSize()
    {
        return this._binSize;
    } //-- BinSize getBinSize() 

    /**
     * Returns the value of field 'blowerEnergySource'.
     * 
     * @return the value of field 'blowerEnergySource'.
    **/
    public BlowerEnergySource getBlowerEnergySource()
    {
        return this._blowerEnergySource;
    } //-- BlowerEnergySource getBlowerEnergySource() 

    /**
     * Returns the value of field 'blowerHeatSource'.
     * 
     * @return the value of field 'blowerHeatSource'.
    **/
    public BlowerHeatSource getBlowerHeatSource()
    {
        return this._blowerHeatSource;
    } //-- BlowerHeatSource getBlowerHeatSource() 

    /**
     * Returns the value of field 'blowerHorsePower'.
     * 
     * @return the value of field 'blowerHorsePower'.
    **/
    public BlowerHorsePower getBlowerHorsePower()
    {
        return this._blowerHorsePower;
    } //-- BlowerHorsePower getBlowerHorsePower() 

    /**
     * Returns the value of field 'dryerType'.
     * 
     * @return the value of field 'dryerType'.
    **/
    public DryerType getDryerType()
    {
        return this._dryerType;
    } //-- DryerType getDryerType() 

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
     * Sets the value of field 'binSize'.
     * 
     * @param binSize the value of field 'binSize'.
    **/
    public void setBinSize(BinSize binSize)
    {
        this._binSize = binSize;
    } //-- void setBinSize(BinSize) 

    /**
     * Sets the value of field 'blowerEnergySource'.
     * 
     * @param blowerEnergySource the value of field
     * 'blowerEnergySource'.
    **/
    public void setBlowerEnergySource(BlowerEnergySource blowerEnergySource)
    {
        this._blowerEnergySource = blowerEnergySource;
    } //-- void setBlowerEnergySource(BlowerEnergySource) 

    /**
     * Sets the value of field 'blowerHeatSource'.
     * 
     * @param blowerHeatSource the value of field 'blowerHeatSource'
    **/
    public void setBlowerHeatSource(BlowerHeatSource blowerHeatSource)
    {
        this._blowerHeatSource = blowerHeatSource;
    } //-- void setBlowerHeatSource(BlowerHeatSource) 

    /**
     * Sets the value of field 'blowerHorsePower'.
     * 
     * @param blowerHorsePower the value of field 'blowerHorsePower'
    **/
    public void setBlowerHorsePower(BlowerHorsePower blowerHorsePower)
    {
        this._blowerHorsePower = blowerHorsePower;
    } //-- void setBlowerHorsePower(BlowerHorsePower) 

    /**
     * Sets the value of field 'dryerType'.
     * 
     * @param dryerType the value of field 'dryerType'.
    **/
    public void setDryerType(DryerType dryerType)
    {
        this._dryerType = dryerType;
    } //-- void setDryerType(DryerType) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.GrainDryer unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.GrainDryer) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.GrainDryer.class, reader);
    } //-- com.cannontech.stars.xml.serialize.GrainDryer unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
