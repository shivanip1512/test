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

import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.FieldValidator;
import org.exolab.castor.xml.TypeValidator;
import org.exolab.castor.xml.XMLFieldDescriptor;
import org.exolab.castor.xml.handlers.*;
import org.exolab.castor.xml.util.XMLFieldDescriptorImpl;
import org.exolab.castor.xml.validators.*;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsAppDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String nsPrefix;

    private java.lang.String nsURI;

    private java.lang.String xmlName;

    private org.exolab.castor.xml.XMLFieldDescriptor identity;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsAppDescriptor() {
        super();
        xmlName = "stars-App";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        
        //-- set grouping compositor
        setCompositorAsSequence();
        //-- initialize attribute descriptors
        
        //-- _applianceID
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_applianceID", "applianceID", NodeType.Attribute);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                if(!target.hasApplianceID())
                    return null;
                return new Integer(target.getApplianceID());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteApplianceID();
                        return;
                    }
                    target.setApplianceID( ((Integer)value).intValue());
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        addFieldDescriptor(desc);
        
        //-- validation code for: _applianceID
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _applianceCategoryID
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_applianceCategoryID", "applianceCategoryID", NodeType.Attribute);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                if(!target.hasApplianceCategoryID())
                    return null;
                return new Integer(target.getApplianceCategoryID());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteApplianceCategoryID();
                        return;
                    }
                    target.setApplianceCategoryID( ((Integer)value).intValue());
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        addFieldDescriptor(desc);
        
        //-- validation code for: _applianceCategoryID
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _inventoryID
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_inventoryID", "inventoryID", NodeType.Attribute);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                if(!target.hasInventoryID())
                    return null;
                return new Integer(target.getInventoryID());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteInventoryID();
                        return;
                    }
                    target.setInventoryID( ((Integer)value).intValue());
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        addFieldDescriptor(desc);
        
        //-- validation code for: _inventoryID
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _programID
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_programID", "programID", NodeType.Attribute);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                if(!target.hasProgramID())
                    return null;
                return new Integer(target.getProgramID());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteProgramID();
                        return;
                    }
                    target.setProgramID( ((Integer)value).intValue());
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        addFieldDescriptor(desc);
        
        //-- validation code for: _programID
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _loadNumber
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_loadNumber", "loadNumber", NodeType.Attribute);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                if(!target.hasLoadNumber())
                    return null;
                return new Integer(target.getLoadNumber());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteLoadNumber();
                        return;
                    }
                    target.setLoadNumber( ((Integer)value).intValue());
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        addFieldDescriptor(desc);
        
        //-- validation code for: _loadNumber
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- initialize element descriptors
        
        //-- _yearManufactured
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_yearManufactured", "YearManufactured", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                if(!target.hasYearManufactured())
                    return null;
                return new Integer(target.getYearManufactured());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteYearManufactured();
                        return;
                    }
                    target.setYearManufactured( ((Integer)value).intValue());
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _yearManufactured
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _manufacturer
        desc = new XMLFieldDescriptorImpl(Manufacturer.class, "_manufacturer", "Manufacturer", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                return target.getManufacturer();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    target.setManufacturer( (Manufacturer) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new Manufacturer();
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _manufacturer
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        desc.setValidator(fieldValidator);
        
        //-- _location
        desc = new XMLFieldDescriptorImpl(Location.class, "_location", "Location", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                return target.getLocation();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    target.setLocation( (Location) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new Location();
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _location
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        desc.setValidator(fieldValidator);
        
        //-- _serviceCompany
        desc = new XMLFieldDescriptorImpl(ServiceCompany.class, "_serviceCompany", "ServiceCompany", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                return target.getServiceCompany();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    target.setServiceCompany( (ServiceCompany) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new ServiceCompany();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _serviceCompany
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _notes
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_notes", "Notes", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                return target.getNotes();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    target.setNotes( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _notes
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _modelNumber
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_modelNumber", "ModelNumber", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                return target.getModelNumber();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    target.setModelNumber( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _modelNumber
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _KWCapacity
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_KWCapacity", "KWCapacity", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                if(!target.hasKWCapacity())
                    return null;
                return new Integer(target.getKWCapacity());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteKWCapacity();
                        return;
                    }
                    target.setKWCapacity( ((Integer)value).intValue());
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _KWCapacity
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _efficiencyRating
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_efficiencyRating", "EfficiencyRating", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                if(!target.hasEfficiencyRating())
                    return null;
                return new Integer(target.getEfficiencyRating());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteEfficiencyRating();
                        return;
                    }
                    target.setEfficiencyRating( ((Integer)value).intValue());
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _efficiencyRating
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _airConditioner
        desc = new XMLFieldDescriptorImpl(AirConditioner.class, "_airConditioner", "AirConditioner", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                return target.getAirConditioner();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    target.setAirConditioner( (AirConditioner) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new AirConditioner();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _airConditioner
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _waterHeater
        desc = new XMLFieldDescriptorImpl(WaterHeater.class, "_waterHeater", "WaterHeater", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                return target.getWaterHeater();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    target.setWaterHeater( (WaterHeater) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new WaterHeater();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _waterHeater
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _dualFuel
        desc = new XMLFieldDescriptorImpl(DualFuel.class, "_dualFuel", "DualFuel", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                return target.getDualFuel();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    target.setDualFuel( (DualFuel) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new DualFuel();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _dualFuel
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _generator
        desc = new XMLFieldDescriptorImpl(Generator.class, "_generator", "Generator", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                return target.getGenerator();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    target.setGenerator( (Generator) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new Generator();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _generator
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _grainDryer
        desc = new XMLFieldDescriptorImpl(GrainDryer.class, "_grainDryer", "GrainDryer", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                return target.getGrainDryer();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    target.setGrainDryer( (GrainDryer) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new GrainDryer();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _grainDryer
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _storageHeat
        desc = new XMLFieldDescriptorImpl(StorageHeat.class, "_storageHeat", "StorageHeat", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                return target.getStorageHeat();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    target.setStorageHeat( (StorageHeat) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StorageHeat();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _storageHeat
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _heatPump
        desc = new XMLFieldDescriptorImpl(HeatPump.class, "_heatPump", "HeatPump", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                return target.getHeatPump();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    target.setHeatPump( (HeatPump) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new HeatPump();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _heatPump
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _irrigation
        desc = new XMLFieldDescriptorImpl(Irrigation.class, "_irrigation", "Irrigation", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsApp target = (StarsApp) object;
                return target.getIrrigation();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsApp target = (StarsApp) object;
                    target.setIrrigation( (Irrigation) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new Irrigation();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _irrigation
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
    } //-- com.cannontech.stars.xml.serialize.StarsAppDescriptor()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public org.exolab.castor.mapping.AccessMode getAccessMode()
    {
        return null;
    } //-- org.exolab.castor.mapping.AccessMode getAccessMode() 

    /**
    **/
    public org.exolab.castor.mapping.ClassDescriptor getExtends()
    {
        return null;
    } //-- org.exolab.castor.mapping.ClassDescriptor getExtends() 

    /**
    **/
    public org.exolab.castor.mapping.FieldDescriptor getIdentity()
    {
        return identity;
    } //-- org.exolab.castor.mapping.FieldDescriptor getIdentity() 

    /**
    **/
    public java.lang.Class getJavaClass()
    {
        return com.cannontech.stars.xml.serialize.StarsApp.class;
    } //-- java.lang.Class getJavaClass() 

    /**
    **/
    public java.lang.String getNameSpacePrefix()
    {
        return nsPrefix;
    } //-- java.lang.String getNameSpacePrefix() 

    /**
    **/
    public java.lang.String getNameSpaceURI()
    {
        return nsURI;
    } //-- java.lang.String getNameSpaceURI() 

    /**
    **/
    public org.exolab.castor.xml.TypeValidator getValidator()
    {
        return this;
    } //-- org.exolab.castor.xml.TypeValidator getValidator() 

    /**
    **/
    public java.lang.String getXMLName()
    {
        return xmlName;
    } //-- java.lang.String getXMLName() 

}
