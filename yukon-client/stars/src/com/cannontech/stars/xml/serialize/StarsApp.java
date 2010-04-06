package com.cannontech.stars.xml.serialize;

import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;

public abstract class StarsApp {
    private int applianceID;
    private boolean hasApplianceID;
    private int applianceCategoryID;
    private ApplianceCategory applianceCategory;
    private boolean hasApplianceCategoryID;
    private int inventoryID;
    private boolean hasInventoryID;
    private int programID;
    private boolean hasProgramID;
    private boolean hasAddressingGroupID;
    private int addressingGroupID;
    private int loadNumber;
    private boolean hasLoadNumber;
    private int yearManufactured;
    private boolean hasYearManufactured;
    private Manufacturer manufacturer;
    private Location location;
    private ServiceCompany serviceCompany;
    private String notes;
    private String modelNumber;
    private double kwCapacity;
    private boolean hasKwCapacity;
    private double efficiencyRating;
    private boolean hasEfficiencyRating;
    private AirConditioner airConditioner;
    private WaterHeater waterHeater;
    private DualFuel dualFuel;
    private Generator generator;
    private GrainDryer grainDryer;
    private StorageHeat storageHeat;
    private HeatPump heatPump;
    private Irrigation irrigation;
    private Chiller chiller;
    private DualStageAC dualStageAC;

    public StarsApp() {
        this.location = new Location();
        this.manufacturer = new Manufacturer();
        this.applianceCategory = new ApplianceCategory();
        this.airConditioner = new AirConditioner();
        this.waterHeater = new WaterHeater();
        this.dualFuel = new DualFuel();
        this.generator = new Generator();
        this.grainDryer = new GrainDryer();
        this.storageHeat = new StorageHeat();
        this.heatPump = new HeatPump();
        this.irrigation = new Irrigation();
        this.chiller = new Chiller();
        this.dualStageAC = new DualStageAC();
    }

    public void deleteApplianceCategoryID() {
        this.hasApplianceCategoryID = false;
    } 

    public void deleteApplianceID() {
        this.hasApplianceID = false;
    } 

    public void deleteEfficiencyRating() {
        this.hasEfficiencyRating = false;
    } 

    public void deleteInventoryID() {
        this.hasInventoryID = false;
    } 

    public void deleteKWCapacity() {
        this.hasKwCapacity = false;
    } 

    public void deleteLoadNumber() {
        this.hasLoadNumber = false;
    } 

    public void deleteProgramID() {
        this.hasProgramID = false;
    } 

    public void deleteYearManufactured() {
        this.hasYearManufactured = false;
    } 

    public AirConditioner getAirConditioner() {
        return this.airConditioner;
    }

    public int getApplianceCategoryID() {
        return this.applianceCategoryID;
    }

    public int getApplianceID() {
        return this.applianceID;
    }

    public DualFuel getDualFuel() {
        return this.dualFuel;
    }

    public double getEfficiencyRating() {
        return this.efficiencyRating;
    }

    public Generator getGenerator() {
        return this.generator;
    }

    public GrainDryer getGrainDryer() {
        return this.grainDryer;
    }

    public HeatPump getHeatPump() {
        return this.heatPump;
    }

    public int getInventoryID() {
        return this.inventoryID;
    }

    public Irrigation getIrrigation() {
        return this.irrigation;
    } 

    public double getKwCapacity() {
        return this.kwCapacity;
    }

    public int getAddressingGroupID() {
        return this.addressingGroupID;
    }
    
    public int getLoadNumber() {
        return this.loadNumber;
    }

    public Location getLocation() {
        return this.location;
    }

    public Manufacturer getManufacturer() {
        return this.manufacturer;
    }

    public String getModelNumber() {
        return this.modelNumber;
    } 

    public String getNotes() {
        return this.notes;
    } 

    public int getProgramID() {
        return this.programID;
    }

    public ServiceCompany getServiceCompany() {
        return this.serviceCompany;
    }

    public StorageHeat getStorageHeat() {
        return this.storageHeat;
    }

    public WaterHeater getWaterHeater() {
        return this.waterHeater;
    }

    public int getYearManufactured() {
        return this.yearManufactured;
    }

    public boolean hasApplianceCategoryID() {
        return this.hasApplianceCategoryID;
    }

    public boolean hasApplianceID() {
        return this.hasApplianceID;
    }

    public boolean hasEfficiencyRating() {
        return this.hasEfficiencyRating;
    }

    public boolean hasInventoryID() {
        return this.hasInventoryID;
    }

    public boolean hasKwCapacity() {
        return this.hasKwCapacity;
    }

    public boolean hasAddressingGroupID() {
        return this.hasAddressingGroupID;
    }
    
    public boolean hasLoadNumber() {
        return this.hasLoadNumber;
    }

    public boolean hasProgramID() {
        return this.hasProgramID;
    }

    public boolean hasYearManufactured() {
        return this.hasYearManufactured;
    }

    public void setAirConditioner(AirConditioner airConditioner) {
        this.airConditioner = airConditioner;
    }

    public void setApplianceCategoryID(int applianceCategoryID) {
        this.applianceCategoryID = applianceCategoryID;
        this.hasApplianceCategoryID = true;
        ApplianceCategoryDao applianceCategoryDao = YukonSpringHook.getBean("applianceCategoryDao", ApplianceCategoryDao.class);
        this.applianceCategory = applianceCategoryDao.getById(applianceCategoryID);
    }

    public ApplianceCategory getApplianceCategory() {
        return applianceCategory;
    }
    public void setApplianceCategory(ApplianceCategory applianceCategory) {
        this.applianceCategory = applianceCategory;
    }
    
    public void setApplianceID(int applianceID) {
        this.applianceID = applianceID;
        this.hasApplianceID = true;
    }

    public void setDualFuel(DualFuel dualFuel) {
        this.dualFuel = dualFuel;
    }

    public void setEfficiencyRating(double efficiencyRating) {
        this.efficiencyRating = efficiencyRating;
        this.hasEfficiencyRating = true;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    } 

    public void setGrainDryer(GrainDryer grainDryer) {
        this.grainDryer = grainDryer;
    } 

    public void setHeatPump(HeatPump heatPump) {
        this.heatPump = heatPump;
    } 

    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
        this.hasInventoryID = true;
    }

    public void setIrrigation(Irrigation irrigation) {
        this.irrigation = irrigation;
    } 

    public void setKwCapacity(double kwCapacity) {
        this.kwCapacity = kwCapacity;
        this.hasKwCapacity = true;
    }

    public void setAddressingGroupID(int addressingGroupID) {
        this.addressingGroupID = addressingGroupID;
        this.hasAddressingGroupID = true;
    }
    
    public void setLoadNumber(int loadNumber) {
        this.loadNumber = loadNumber;
        this.hasLoadNumber = true;
    }

    public void setLocation(Location location) {
        this.location = location;
    } 

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    } 

    public void setModelNumber(java.lang.String modelNumber) {
        this.modelNumber = modelNumber;
    } 

    public void setNotes(java.lang.String notes) {
        this.notes = notes;
    } 

    public void setProgramID(int programID) {
        this.programID = programID;
        this.hasProgramID = true;
    }

    public void setServiceCompany(ServiceCompany serviceCompany) {
        this.serviceCompany = serviceCompany;
    } 

    public void setStorageHeat(StorageHeat storageHeat) {
        this.storageHeat = storageHeat;
    }

    public void setWaterHeater(WaterHeater waterHeater) {
        this.waterHeater = waterHeater;
    } 

    public void setYearManufactured(int yearManufactured) {
        this.yearManufactured = yearManufactured;
        this.hasYearManufactured = true;
    }

    public Chiller getChiller() {
        return chiller;
    }

    public void setChiller(Chiller chiller) {
        this.chiller = chiller;
    }

    public DualStageAC getDualStageAC() {
        return dualStageAC;
    }

    public void setDualStageAC(DualStageAC dualStageAC) {
        this.dualStageAC = dualStageAC;
    }

}
