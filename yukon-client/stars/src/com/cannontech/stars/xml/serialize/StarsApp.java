package com.cannontech.stars.xml.serialize;

public abstract class StarsApp {
    private int applianceID;
    private boolean hasApplianceID;
    private int applianceCategoryID;
    private boolean hasApplianceCategoryID;
    private int inventoryID;
    private boolean hasInventoryID;
    private int programID;
    private boolean hasProgramID;
    private int loadNumber;
    private boolean hasLoadNumber;
    private int yearManufactured;
    private boolean hasYearManufactured;
    private Manufacturer manufacturer;
    private Location location;
    private ServiceCompany serviceCompany;
    private String notes;
    private String modelNumber;
    private double KWCapacity;
    private boolean hasKWCapacity;
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
        this.hasKWCapacity = false;
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

    public double getKWCapacity() {
        return this.KWCapacity;
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

    public boolean hasKWCapacity() {
        return this.hasKWCapacity;
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

    public void setKWCapacity(double KWCapacity) {
        this.KWCapacity = KWCapacity;
        this.hasKWCapacity = true;
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
