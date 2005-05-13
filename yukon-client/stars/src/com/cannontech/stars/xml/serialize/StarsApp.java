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
 * @version $Revision$ $Date$
**/
public abstract class StarsApp implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _applianceID;

    /**
     * keeps track of state for field: _applianceID
    **/
    private boolean _has_applianceID;

    private int _applianceCategoryID;

    /**
     * keeps track of state for field: _applianceCategoryID
    **/
    private boolean _has_applianceCategoryID;

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private int _programID;

    /**
     * keeps track of state for field: _programID
    **/
    private boolean _has_programID;

    private int _loadNumber;

    /**
     * keeps track of state for field: _loadNumber
    **/
    private boolean _has_loadNumber;

    private int _yearManufactured;

    /**
     * keeps track of state for field: _yearManufactured
    **/
    private boolean _has_yearManufactured;

    private Manufacturer _manufacturer;

    private Location _location;

    private ServiceCompany _serviceCompany;

    private java.lang.String _notes;

    private java.lang.String _modelNumber;

    private double _KWCapacity;

    /**
     * keeps track of state for field: _KWCapacity
    **/
    private boolean _has_KWCapacity;

    private double _efficiencyRating;

    /**
     * keeps track of state for field: _efficiencyRating
    **/
    private boolean _has_efficiencyRating;

    private AirConditioner _airConditioner;

    private WaterHeater _waterHeater;

    private DualFuel _dualFuel;

    private Generator _generator;

    private GrainDryer _grainDryer;

    private StorageHeat _storageHeat;

    private HeatPump _heatPump;

    private Irrigation _irrigation;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsApp() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsApp()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteApplianceCategoryID()
    {
        this._has_applianceCategoryID= false;
    } //-- void deleteApplianceCategoryID() 

    /**
    **/
    public void deleteApplianceID()
    {
        this._has_applianceID= false;
    } //-- void deleteApplianceID() 

    /**
    **/
    public void deleteEfficiencyRating()
    {
        this._has_efficiencyRating= false;
    } //-- void deleteEfficiencyRating() 

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
    **/
    public void deleteKWCapacity()
    {
        this._has_KWCapacity= false;
    } //-- void deleteKWCapacity() 

    /**
    **/
    public void deleteLoadNumber()
    {
        this._has_loadNumber= false;
    } //-- void deleteLoadNumber() 

    /**
    **/
    public void deleteProgramID()
    {
        this._has_programID= false;
    } //-- void deleteProgramID() 

    /**
    **/
    public void deleteYearManufactured()
    {
        this._has_yearManufactured= false;
    } //-- void deleteYearManufactured() 

    /**
     * Returns the value of field 'airConditioner'.
     * 
     * @return the value of field 'airConditioner'.
    **/
    public AirConditioner getAirConditioner()
    {
        return this._airConditioner;
    } //-- AirConditioner getAirConditioner() 

    /**
     * Returns the value of field 'applianceCategoryID'.
     * 
     * @return the value of field 'applianceCategoryID'.
    **/
    public int getApplianceCategoryID()
    {
        return this._applianceCategoryID;
    } //-- int getApplianceCategoryID() 

    /**
     * Returns the value of field 'applianceID'.
     * 
     * @return the value of field 'applianceID'.
    **/
    public int getApplianceID()
    {
        return this._applianceID;
    } //-- int getApplianceID() 

    /**
     * Returns the value of field 'dualFuel'.
     * 
     * @return the value of field 'dualFuel'.
    **/
    public DualFuel getDualFuel()
    {
        return this._dualFuel;
    } //-- DualFuel getDualFuel() 

    /**
     * Returns the value of field 'efficiencyRating'.
     * 
     * @return the value of field 'efficiencyRating'.
    **/
    public double getEfficiencyRating()
    {
        return this._efficiencyRating;
    } //-- double getEfficiencyRating() 

    /**
     * Returns the value of field 'generator'.
     * 
     * @return the value of field 'generator'.
    **/
    public Generator getGenerator()
    {
        return this._generator;
    } //-- Generator getGenerator() 

    /**
     * Returns the value of field 'grainDryer'.
     * 
     * @return the value of field 'grainDryer'.
    **/
    public GrainDryer getGrainDryer()
    {
        return this._grainDryer;
    } //-- GrainDryer getGrainDryer() 

    /**
     * Returns the value of field 'heatPump'.
     * 
     * @return the value of field 'heatPump'.
    **/
    public HeatPump getHeatPump()
    {
        return this._heatPump;
    } //-- HeatPump getHeatPump() 

    /**
     * Returns the value of field 'inventoryID'.
     * 
     * @return the value of field 'inventoryID'.
    **/
    public int getInventoryID()
    {
        return this._inventoryID;
    } //-- int getInventoryID() 

    /**
     * Returns the value of field 'irrigation'.
     * 
     * @return the value of field 'irrigation'.
    **/
    public Irrigation getIrrigation()
    {
        return this._irrigation;
    } //-- Irrigation getIrrigation() 

    /**
     * Returns the value of field 'KWCapacity'.
     * 
     * @return the value of field 'KWCapacity'.
    **/
    public double getKWCapacity()
    {
        return this._KWCapacity;
    } //-- double getKWCapacity() 

    /**
     * Returns the value of field 'loadNumber'.
     * 
     * @return the value of field 'loadNumber'.
    **/
    public int getLoadNumber()
    {
        return this._loadNumber;
    } //-- int getLoadNumber() 

    /**
     * Returns the value of field 'location'.
     * 
     * @return the value of field 'location'.
    **/
    public Location getLocation()
    {
        return this._location;
    } //-- Location getLocation() 

    /**
     * Returns the value of field 'manufacturer'.
     * 
     * @return the value of field 'manufacturer'.
    **/
    public Manufacturer getManufacturer()
    {
        return this._manufacturer;
    } //-- Manufacturer getManufacturer() 

    /**
     * Returns the value of field 'modelNumber'.
     * 
     * @return the value of field 'modelNumber'.
    **/
    public java.lang.String getModelNumber()
    {
        return this._modelNumber;
    } //-- java.lang.String getModelNumber() 

    /**
     * Returns the value of field 'notes'.
     * 
     * @return the value of field 'notes'.
    **/
    public java.lang.String getNotes()
    {
        return this._notes;
    } //-- java.lang.String getNotes() 

    /**
     * Returns the value of field 'programID'.
     * 
     * @return the value of field 'programID'.
    **/
    public int getProgramID()
    {
        return this._programID;
    } //-- int getProgramID() 

    /**
     * Returns the value of field 'serviceCompany'.
     * 
     * @return the value of field 'serviceCompany'.
    **/
    public ServiceCompany getServiceCompany()
    {
        return this._serviceCompany;
    } //-- ServiceCompany getServiceCompany() 

    /**
     * Returns the value of field 'storageHeat'.
     * 
     * @return the value of field 'storageHeat'.
    **/
    public StorageHeat getStorageHeat()
    {
        return this._storageHeat;
    } //-- StorageHeat getStorageHeat() 

    /**
     * Returns the value of field 'waterHeater'.
     * 
     * @return the value of field 'waterHeater'.
    **/
    public WaterHeater getWaterHeater()
    {
        return this._waterHeater;
    } //-- WaterHeater getWaterHeater() 

    /**
     * Returns the value of field 'yearManufactured'.
     * 
     * @return the value of field 'yearManufactured'.
    **/
    public int getYearManufactured()
    {
        return this._yearManufactured;
    } //-- int getYearManufactured() 

    /**
    **/
    public boolean hasApplianceCategoryID()
    {
        return this._has_applianceCategoryID;
    } //-- boolean hasApplianceCategoryID() 

    /**
    **/
    public boolean hasApplianceID()
    {
        return this._has_applianceID;
    } //-- boolean hasApplianceID() 

    /**
    **/
    public boolean hasEfficiencyRating()
    {
        return this._has_efficiencyRating;
    } //-- boolean hasEfficiencyRating() 

    /**
    **/
    public boolean hasInventoryID()
    {
        return this._has_inventoryID;
    } //-- boolean hasInventoryID() 

    /**
    **/
    public boolean hasKWCapacity()
    {
        return this._has_KWCapacity;
    } //-- boolean hasKWCapacity() 

    /**
    **/
    public boolean hasLoadNumber()
    {
        return this._has_loadNumber;
    } //-- boolean hasLoadNumber() 

    /**
    **/
    public boolean hasProgramID()
    {
        return this._has_programID;
    } //-- boolean hasProgramID() 

    /**
    **/
    public boolean hasYearManufactured()
    {
        return this._has_yearManufactured;
    } //-- boolean hasYearManufactured() 

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
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * Sets the value of field 'airConditioner'.
     * 
     * @param airConditioner the value of field 'airConditioner'.
    **/
    public void setAirConditioner(AirConditioner airConditioner)
    {
        this._airConditioner = airConditioner;
    } //-- void setAirConditioner(AirConditioner) 

    /**
     * Sets the value of field 'applianceCategoryID'.
     * 
     * @param applianceCategoryID the value of field
     * 'applianceCategoryID'.
    **/
    public void setApplianceCategoryID(int applianceCategoryID)
    {
        this._applianceCategoryID = applianceCategoryID;
        this._has_applianceCategoryID = true;
    } //-- void setApplianceCategoryID(int) 

    /**
     * Sets the value of field 'applianceID'.
     * 
     * @param applianceID the value of field 'applianceID'.
    **/
    public void setApplianceID(int applianceID)
    {
        this._applianceID = applianceID;
        this._has_applianceID = true;
    } //-- void setApplianceID(int) 

    /**
     * Sets the value of field 'dualFuel'.
     * 
     * @param dualFuel the value of field 'dualFuel'.
    **/
    public void setDualFuel(DualFuel dualFuel)
    {
        this._dualFuel = dualFuel;
    } //-- void setDualFuel(DualFuel) 

    /**
     * Sets the value of field 'efficiencyRating'.
     * 
     * @param efficiencyRating the value of field 'efficiencyRating'
    **/
    public void setEfficiencyRating(double efficiencyRating)
    {
        this._efficiencyRating = efficiencyRating;
        this._has_efficiencyRating = true;
    } //-- void setEfficiencyRating(double) 

    /**
     * Sets the value of field 'generator'.
     * 
     * @param generator the value of field 'generator'.
    **/
    public void setGenerator(Generator generator)
    {
        this._generator = generator;
    } //-- void setGenerator(Generator) 

    /**
     * Sets the value of field 'grainDryer'.
     * 
     * @param grainDryer the value of field 'grainDryer'.
    **/
    public void setGrainDryer(GrainDryer grainDryer)
    {
        this._grainDryer = grainDryer;
    } //-- void setGrainDryer(GrainDryer) 

    /**
     * Sets the value of field 'heatPump'.
     * 
     * @param heatPump the value of field 'heatPump'.
    **/
    public void setHeatPump(HeatPump heatPump)
    {
        this._heatPump = heatPump;
    } //-- void setHeatPump(HeatPump) 

    /**
     * Sets the value of field 'inventoryID'.
     * 
     * @param inventoryID the value of field 'inventoryID'.
    **/
    public void setInventoryID(int inventoryID)
    {
        this._inventoryID = inventoryID;
        this._has_inventoryID = true;
    } //-- void setInventoryID(int) 

    /**
     * Sets the value of field 'irrigation'.
     * 
     * @param irrigation the value of field 'irrigation'.
    **/
    public void setIrrigation(Irrigation irrigation)
    {
        this._irrigation = irrigation;
    } //-- void setIrrigation(Irrigation) 

    /**
     * Sets the value of field 'KWCapacity'.
     * 
     * @param KWCapacity the value of field 'KWCapacity'.
    **/
    public void setKWCapacity(double KWCapacity)
    {
        this._KWCapacity = KWCapacity;
        this._has_KWCapacity = true;
    } //-- void setKWCapacity(int) 

    /**
     * Sets the value of field 'loadNumber'.
     * 
     * @param loadNumber the value of field 'loadNumber'.
    **/
    public void setLoadNumber(int loadNumber)
    {
        this._loadNumber = loadNumber;
        this._has_loadNumber = true;
    } //-- void setLoadNumber(int) 

    /**
     * Sets the value of field 'location'.
     * 
     * @param location the value of field 'location'.
    **/
    public void setLocation(Location location)
    {
        this._location = location;
    } //-- void setLocation(Location) 

    /**
     * Sets the value of field 'manufacturer'.
     * 
     * @param manufacturer the value of field 'manufacturer'.
    **/
    public void setManufacturer(Manufacturer manufacturer)
    {
        this._manufacturer = manufacturer;
    } //-- void setManufacturer(Manufacturer) 

    /**
     * Sets the value of field 'modelNumber'.
     * 
     * @param modelNumber the value of field 'modelNumber'.
    **/
    public void setModelNumber(java.lang.String modelNumber)
    {
        this._modelNumber = modelNumber;
    } //-- void setModelNumber(java.lang.String) 

    /**
     * Sets the value of field 'notes'.
     * 
     * @param notes the value of field 'notes'.
    **/
    public void setNotes(java.lang.String notes)
    {
        this._notes = notes;
    } //-- void setNotes(java.lang.String) 

    /**
     * Sets the value of field 'programID'.
     * 
     * @param programID the value of field 'programID'.
    **/
    public void setProgramID(int programID)
    {
        this._programID = programID;
        this._has_programID = true;
    } //-- void setProgramID(int) 

    /**
     * Sets the value of field 'serviceCompany'.
     * 
     * @param serviceCompany the value of field 'serviceCompany'.
    **/
    public void setServiceCompany(ServiceCompany serviceCompany)
    {
        this._serviceCompany = serviceCompany;
    } //-- void setServiceCompany(ServiceCompany) 

    /**
     * Sets the value of field 'storageHeat'.
     * 
     * @param storageHeat the value of field 'storageHeat'.
    **/
    public void setStorageHeat(StorageHeat storageHeat)
    {
        this._storageHeat = storageHeat;
    } //-- void setStorageHeat(StorageHeat) 

    /**
     * Sets the value of field 'waterHeater'.
     * 
     * @param waterHeater the value of field 'waterHeater'.
    **/
    public void setWaterHeater(WaterHeater waterHeater)
    {
        this._waterHeater = waterHeater;
    } //-- void setWaterHeater(WaterHeater) 

    /**
     * Sets the value of field 'yearManufactured'.
     * 
     * @param yearManufactured the value of field 'yearManufactured'
    **/
    public void setYearManufactured(int yearManufactured)
    {
        this._yearManufactured = yearManufactured;
        this._has_yearManufactured = true;
    } //-- void setYearManufactured(int) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
