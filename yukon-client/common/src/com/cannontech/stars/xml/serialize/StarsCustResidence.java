package com.cannontech.stars.xml.serialize;

public abstract class StarsCustResidence {
    private ResidenceType _residenceType;
    private ConstructionMaterial _constructionMaterial;
    private DecadeBuilt _decadeBuilt;
    private SquareFeet _squareFeet;
    private InsulationDepth _insulationDepth;
    private GeneralCondition _generalCondition;
    private MainCoolingSystem _mainCoolingSystem;
    private MainHeatingSystem _mainHeatingSystem;
    private NumberOfOccupants _numberOfOccupants;
    private OwnershipType _ownershipType;
    private MainFuelType _mainFuelType;
    private String _notes;


    public StarsCustResidence() {
        
    }

    public ConstructionMaterial getConstructionMaterial() {
        return this._constructionMaterial;
    }

    public DecadeBuilt getDecadeBuilt() {
        return this._decadeBuilt;
    }

    public GeneralCondition getGeneralCondition() {
        return this._generalCondition;
    }

    public InsulationDepth getInsulationDepth() {
        return this._insulationDepth;
    }

    public MainCoolingSystem getMainCoolingSystem() {
        return this._mainCoolingSystem;
    }

    public MainFuelType getMainFuelType() {
        return this._mainFuelType;
    }

    public MainHeatingSystem getMainHeatingSystem() {
        return this._mainHeatingSystem;
    }

    public java.lang.String getNotes() {
        return this._notes;
    }

    public NumberOfOccupants getNumberOfOccupants() {
        return this._numberOfOccupants;
    }

    public OwnershipType getOwnershipType() {
        return this._ownershipType;
    }

    public ResidenceType getResidenceType() {
        return this._residenceType;
    }

    public SquareFeet getSquareFeet() {
        return this._squareFeet;
    }

    public void setConstructionMaterial(ConstructionMaterial constructionMaterial) {
        this._constructionMaterial = constructionMaterial;
    }

    public void setDecadeBuilt(DecadeBuilt decadeBuilt) {
        this._decadeBuilt = decadeBuilt;
    }

    public void setGeneralCondition(GeneralCondition generalCondition) {
        this._generalCondition = generalCondition;
    }

    public void setInsulationDepth(InsulationDepth insulationDepth) {
        this._insulationDepth = insulationDepth;
    }

    public void setMainCoolingSystem(MainCoolingSystem mainCoolingSystem) {
        this._mainCoolingSystem = mainCoolingSystem;
    }

    public void setMainFuelType(MainFuelType mainFuelType) {
        this._mainFuelType = mainFuelType;
    }

    public void setMainHeatingSystem(MainHeatingSystem mainHeatingSystem) {
        this._mainHeatingSystem = mainHeatingSystem;
    }

    public void setNotes(java.lang.String notes) {
        this._notes = notes;
    }

    public void setNumberOfOccupants(NumberOfOccupants numberOfOccupants) {
        this._numberOfOccupants = numberOfOccupants;
    }

    public void setOwnershipType(OwnershipType ownershipType) {
        this._ownershipType = ownershipType;
    }

    public void setResidenceType(ResidenceType residenceType) {
        this._residenceType = residenceType;
    }

    public void setSquareFeet(SquareFeet squareFeet) {
        this._squareFeet = squareFeet;
    }

}
