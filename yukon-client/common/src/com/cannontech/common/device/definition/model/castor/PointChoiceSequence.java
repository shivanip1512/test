/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cannontech.common.device.definition.model.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

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
    private com.cannontech.common.device.definition.model.castor.Multiplier _multiplier;

    /**
     * Field _unitofmeasure
     */
    private com.cannontech.common.device.definition.model.castor.Unitofmeasure _unitofmeasure;


      //----------------/
     //- Constructors -/
    //----------------/

    public PointChoiceSequence() 
     {
        super();
    } //-- com.cannontech.common.device.definition.model.castor.PointChoiceSequence()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'multiplier'.
     * 
     * @return Multiplier
     * @return the value of field 'multiplier'.
     */
    public com.cannontech.common.device.definition.model.castor.Multiplier getMultiplier()
    {
        return this._multiplier;
    } //-- com.cannontech.common.device.definition.model.castor.Multiplier getMultiplier() 

    /**
     * Returns the value of field 'unitofmeasure'.
     * 
     * @return Unitofmeasure
     * @return the value of field 'unitofmeasure'.
     */
    public com.cannontech.common.device.definition.model.castor.Unitofmeasure getUnitofmeasure()
    {
        return this._unitofmeasure;
    } //-- com.cannontech.common.device.definition.model.castor.Unitofmeasure getUnitofmeasure() 

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
     * Sets the value of field 'multiplier'.
     * 
     * @param multiplier the value of field 'multiplier'.
     */
    public void setMultiplier(com.cannontech.common.device.definition.model.castor.Multiplier multiplier)
    {
        this._multiplier = multiplier;
    } //-- void setMultiplier(com.cannontech.common.device.definition.model.castor.Multiplier) 

    /**
     * Sets the value of field 'unitofmeasure'.
     * 
     * @param unitofmeasure the value of field 'unitofmeasure'.
     */
    public void setUnitofmeasure(com.cannontech.common.device.definition.model.castor.Unitofmeasure unitofmeasure)
    {
        this._unitofmeasure = unitofmeasure;
    } //-- void setUnitofmeasure(com.cannontech.common.device.definition.model.castor.Unitofmeasure) 

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
        return (com.cannontech.common.device.definition.model.castor.PointChoiceSequence) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.PointChoiceSequence.class, reader);
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
