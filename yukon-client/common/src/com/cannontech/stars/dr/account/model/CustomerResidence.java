package com.cannontech.stars.dr.account.model;


public class CustomerResidence {

	private int accountSiteId;
	private int residenceTypeId = 0;
	private int constructionMaterialId = 0;
	private int decadeBuiltId = 0;
	private int squareFeetId = 0;
	private int insulationDepthId = 0;
	private int generalConditionId = 0;
	private int mainCoolingSystemId = 0;
	private int mainHeatingSystemId = 0;
	private int numberOfOccupantsId = 0;
	private int ownershipTypeId = 0;
	private int mainFuelTypeId = 0;
	private String notes = "";
	
	public int getAccountSiteId() {
		return accountSiteId;
	}
	public void setAccountSiteId(int accountSiteId) {
		this.accountSiteId = accountSiteId;
	}
	public int getResidenceTypeId() {
		return residenceTypeId;
	}
	public void setResidenceTypeId(int residenceTypeId) {
		this.residenceTypeId = residenceTypeId;
	}
	public int getConstructionMaterialId() {
		return constructionMaterialId;
	}
	public void setConstructionMaterialId(int constructionMaterialId) {
		this.constructionMaterialId = constructionMaterialId;
	}
	public int getDecadeBuiltId() {
		return decadeBuiltId;
	}
	public void setDecadeBuiltId(int decadeBuiltId) {
		this.decadeBuiltId = decadeBuiltId;
	}
	public int getSquareFeetId() {
		return squareFeetId;
	}
	public void setSquareFeetId(int squareFeetId) {
		this.squareFeetId = squareFeetId;
	}
	public int getInsulationDepthId() {
		return insulationDepthId;
	}
	public void setInsulationDepthId(int insulationDepthId) {
		this.insulationDepthId = insulationDepthId;
	}
	public int getGeneralConditionId() {
		return generalConditionId;
	}
	public void setGeneralConditionId(int generalConditionId) {
		this.generalConditionId = generalConditionId;
	}
	public int getMainCoolingSystemId() {
		return mainCoolingSystemId;
	}
	public void setMainCoolingSystemId(int mainCoolingSystemId) {
		this.mainCoolingSystemId = mainCoolingSystemId;
	}
	public int getMainHeatingSystemId() {
		return mainHeatingSystemId;
	}
	public void setMainHeatingSystemId(int mainHeatingSystemId) {
		this.mainHeatingSystemId = mainHeatingSystemId;
	}
	public int getNumberOfOccupantsId() {
		return numberOfOccupantsId;
	}
	public void setNumberOfOccupantsId(int numberOfOccupantsId) {
		this.numberOfOccupantsId = numberOfOccupantsId;
	}
	public int getOwnershipTypeId() {
		return ownershipTypeId;
	}
	public void setOwnershipTypeId(int ownershipTypeId) {
		this.ownershipTypeId = ownershipTypeId;
	}
	public int getMainFuelTypeId() {
		return mainFuelTypeId;
	}
	public void setMainFuelTypeId(int mainFuelTypeId) {
		this.mainFuelTypeId = mainFuelTypeId;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
}
