package com.cannontech.database.data.lite.stars;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteCustomerResidence extends LiteBase {
	
	private int residenceTypeID = CtiUtilities.NONE_ZERO_ID;
	private int constructionMaterialID = CtiUtilities.NONE_ZERO_ID;
	private int decadeBuiltID = CtiUtilities.NONE_ZERO_ID;
	private int squareFeetID = CtiUtilities.NONE_ZERO_ID;
	private int insulationDepthID = CtiUtilities.NONE_ZERO_ID;
	private int generalConditionID = CtiUtilities.NONE_ZERO_ID;
	private int mainCoolingSystemID = CtiUtilities.NONE_ZERO_ID;
	private int mainHeatingSystemID = CtiUtilities.NONE_ZERO_ID;
	private int numberOfOccupantsID = CtiUtilities.NONE_ZERO_ID;
	private int ownershipTypeID = CtiUtilities.NONE_ZERO_ID;
	private int mainFuelTypeID = CtiUtilities.NONE_ZERO_ID;
	private String notes = null;
	
	public LiteCustomerResidence() {
		setLiteType( LiteTypes.STARS_CUSTOMER_RESIDENCE );
	}
	
	public LiteCustomerResidence(int acctSiteID) {
		setLiteType( LiteTypes.STARS_CUSTOMER_RESIDENCE );
		setAccountSiteID( acctSiteID );
	}
	
	public int getAccountSiteID() {
		return getLiteID();
	}
	
	public void setAccountSiteID(int acctSiteID) {
		setLiteID( acctSiteID );
	}

	/**
	 * Returns the constructionMaterialID.
	 * @return int
	 */
	public int getConstructionMaterialID() {
		return constructionMaterialID;
	}

	/**
	 * Returns the decadeBuiltID.
	 * @return int
	 */
	public int getDecadeBuiltID() {
		return decadeBuiltID;
	}

	/**
	 * Returns the generalConditionID.
	 * @return int
	 */
	public int getGeneralConditionID() {
		return generalConditionID;
	}

	/**
	 * Returns the insulationDepthID.
	 * @return int
	 */
	public int getInsulationDepthID() {
		return insulationDepthID;
	}

	/**
	 * Returns the mainCoolingSystemID.
	 * @return int
	 */
	public int getMainCoolingSystemID() {
		return mainCoolingSystemID;
	}

	/**
	 * Returns the mainFuelTypeID.
	 * @return int
	 */
	public int getMainFuelTypeID() {
		return mainFuelTypeID;
	}

	/**
	 * Returns the mainHeatingSystemID.
	 * @return int
	 */
	public int getMainHeatingSystemID() {
		return mainHeatingSystemID;
	}

	/**
	 * Returns the notes.
	 * @return String
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Returns the numberOfOccupantsID.
	 * @return int
	 */
	public int getNumberOfOccupantsID() {
		return numberOfOccupantsID;
	}

	/**
	 * Returns the ownershipTypeID.
	 * @return int
	 */
	public int getOwnershipTypeID() {
		return ownershipTypeID;
	}

	/**
	 * Returns the residenceTypeID.
	 * @return int
	 */
	public int getResidenceTypeID() {
		return residenceTypeID;
	}

	/**
	 * Returns the squareFeetID.
	 * @return int
	 */
	public int getSquareFeetID() {
		return squareFeetID;
	}

	/**
	 * Sets the constructionMaterialID.
	 * @param constructionMaterialID The constructionMaterialID to set
	 */
	public void setConstructionMaterialID(int constructionMaterialID) {
		this.constructionMaterialID = constructionMaterialID;
	}

	/**
	 * Sets the decadeBuiltID.
	 * @param decadeBuiltID The decadeBuiltID to set
	 */
	public void setDecadeBuiltID(int decadeBuiltID) {
		this.decadeBuiltID = decadeBuiltID;
	}

	/**
	 * Sets the generalConditionID.
	 * @param generalConditionID The generalConditionID to set
	 */
	public void setGeneralConditionID(int generalConditionID) {
		this.generalConditionID = generalConditionID;
	}

	/**
	 * Sets the insulationDepthID.
	 * @param insulationDepthID The insulationDepthID to set
	 */
	public void setInsulationDepthID(int insulationDepthID) {
		this.insulationDepthID = insulationDepthID;
	}

	/**
	 * Sets the mainCoolingSystemID.
	 * @param mainCoolingSystemID The mainCoolingSystemID to set
	 */
	public void setMainCoolingSystemID(int mainCoolingSystemID) {
		this.mainCoolingSystemID = mainCoolingSystemID;
	}

	/**
	 * Sets the mainFuelTypeID.
	 * @param mainFuelTypeID The mainFuelTypeID to set
	 */
	public void setMainFuelTypeID(int mainFuelTypeID) {
		this.mainFuelTypeID = mainFuelTypeID;
	}

	/**
	 * Sets the mainHeatingSystemID.
	 * @param mainHeatingSystemID The mainHeatingSystemID to set
	 */
	public void setMainHeatingSystemID(int mainHeatingSystemID) {
		this.mainHeatingSystemID = mainHeatingSystemID;
	}

	/**
	 * Sets the notes.
	 * @param notes The notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Sets the numberOfOccupantsID.
	 * @param numberOfOccupantsID The numberOfOccupantsID to set
	 */
	public void setNumberOfOccupantsID(int numberOfOccupantsID) {
		this.numberOfOccupantsID = numberOfOccupantsID;
	}

	/**
	 * Sets the ownershipTypeID.
	 * @param ownershipTypeID The ownershipTypeID to set
	 */
	public void setOwnershipTypeID(int ownershipTypeID) {
		this.ownershipTypeID = ownershipTypeID;
	}

	/**
	 * Sets the residenceTypeID.
	 * @param residenceTypeID The residenceTypeID to set
	 */
	public void setResidenceTypeID(int residenceTypeID) {
		this.residenceTypeID = residenceTypeID;
	}

	/**
	 * Sets the squareFeetID.
	 * @param squareFeetID The squareFeetID to set
	 */
	public void setSquareFeetID(int squareFeetID) {
		this.squareFeetID = squareFeetID;
	}

}
