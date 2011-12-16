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
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class PointChoiceSequence.
 * 
 * @version $Revision$ $Date$
 */
public class PointChoiceSequence implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _multiplier
     */
    private com.cannontech.common.pao.definition.model.castor.Multiplier _multiplier;

    /**
     * Field _unitofmeasure
     */
    private com.cannontech.common.pao.definition.model.castor.Unitofmeasure _unitofmeasure;

    /**
     * Field _decimalplaces
     */
    private com.cannontech.common.pao.definition.model.castor.Decimalplaces _decimalplaces;

    /**
     * Field _analogstategroup
     */
    private com.cannontech.common.pao.definition.model.castor.Analogstategroup _analogstategroup;


      //----------------/
     //- Constructors -/
    //----------------/

    public PointChoiceSequence() 
     {
        super();
    } //-- com.cannontech.common.pao.definition.model.castor.PointChoiceSequence()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'analogstategroup'.
     * 
     * @return Analogstategroup
     * @return the value of field 'analogstategroup'.
     */
    public com.cannontech.common.pao.definition.model.castor.Analogstategroup getAnalogstategroup()
    {
        return this._analogstategroup;
    } //-- com.cannontech.common.pao.definition.model.castor.Analogstategroup getAnalogstategroup() 

    /**
     * Returns the value of field 'decimalplaces'.
     * 
     * @return Decimalplaces
     * @return the value of field 'decimalplaces'.
     */
    public com.cannontech.common.pao.definition.model.castor.Decimalplaces getDecimalplaces()
    {
        return this._decimalplaces;
    } //-- com.cannontech.common.pao.definition.model.castor.Decimalplaces getDecimalplaces() 

    /**
     * Returns the value of field 'multiplier'.
     * 
     * @return Multiplier
     * @return the value of field 'multiplier'.
     */
    public com.cannontech.common.pao.definition.model.castor.Multiplier getMultiplier()
    {
        return this._multiplier;
    } //-- com.cannontech.common.pao.definition.model.castor.Multiplier getMultiplier() 

    /**
     * Returns the value of field 'unitofmeasure'.
     * 
     * @return Unitofmeasure
     * @return the value of field 'unitofmeasure'.
     */
    public com.cannontech.common.pao.definition.model.castor.Unitofmeasure getUnitofmeasure()
    {
        return this._unitofmeasure;
    } //-- com.cannontech.common.pao.definition.model.castor.Unitofmeasure getUnitofmeasure() 

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
     * Sets the value of field 'analogstategroup'.
     * 
     * @param analogstategroup the value of field 'analogstategroup'
     */
    public void setAnalogstategroup(com.cannontech.common.pao.definition.model.castor.Analogstategroup analogstategroup)
    {
        this._analogstategroup = analogstategroup;
    } //-- void setAnalogstategroup(com.cannontech.common.pao.definition.model.castor.Analogstategroup) 

    /**
     * Sets the value of field 'decimalplaces'.
     * 
     * @param decimalplaces the value of field 'decimalplaces'.
     */
    public void setDecimalplaces(com.cannontech.common.pao.definition.model.castor.Decimalplaces decimalplaces)
    {
        this._decimalplaces = decimalplaces;
    } //-- void setDecimalplaces(com.cannontech.common.pao.definition.model.castor.Decimalplaces) 

    /**
     * Sets the value of field 'multiplier'.
     * 
     * @param multiplier the value of field 'multiplier'.
     */
    public void setMultiplier(com.cannontech.common.pao.definition.model.castor.Multiplier multiplier)
    {
        this._multiplier = multiplier;
    } //-- void setMultiplier(com.cannontech.common.pao.definition.model.castor.Multiplier) 

    /**
     * Sets the value of field 'unitofmeasure'.
     * 
     * @param unitofmeasure the value of field 'unitofmeasure'.
     */
    public void setUnitofmeasure(com.cannontech.common.pao.definition.model.castor.Unitofmeasure unitofmeasure)
    {
        this._unitofmeasure = unitofmeasure;
    } //-- void setUnitofmeasure(com.cannontech.common.pao.definition.model.castor.Unitofmeasure) 

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
        return (com.cannontech.common.pao.definition.model.castor.PointChoiceSequence) Unmarshaller.unmarshal(com.cannontech.common.pao.definition.model.castor.PointChoiceSequence.class, reader);
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
