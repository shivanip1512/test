package com.cannontech.database.db.stars.customer;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CustomerResidence extends DBPersistent {
	
	private Integer accountSiteID = null;
	private Integer residenceTypeID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer constructionMaterialID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer decadeBuiltID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer squareFeetID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer insulationDepthID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer generalConditionID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer mainCoolingSystemID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer mainHeatingSystemID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer numberOfOccupantsID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer ownershipTypeID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer mainFuelTypeID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private String notes = "";
	
	public static final String[] SETTER_COLUMNS = {
		"ResidenceTypeID", "ConstructionMaterialID", "DecadeBuiltID", "SquareFeetID",
		"InsulationDepthID", "GeneralConditionID", "MainCoolingSystemID", "MainHeatingSystemID",
		"NumberOfOccupantsID", "OwnershipTypeID", "MainFuelTypeID", "Notes"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "AccountSiteID" };
	
	public static final String TABLE_NAME = "CustomerResidence";
	
	public CustomerResidence() {
		super();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = {
			getAccountSiteID(), getResidenceTypeID(), getConstructionMaterialID(),
			getDecadeBuiltID(), getSquareFeetID(), getInsulationDepthID(),
			getGeneralConditionID(), getMainCoolingSystemID(), getMainHeatingSystemID(),
			getNumberOfOccupantsID(), getOwnershipTypeID(), getMainFuelTypeID(), getNotes()
		};
		
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = { getAccountSiteID() };
		
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getAccountSiteID() };
		
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if (results.length == SETTER_COLUMNS.length) {
			setResidenceTypeID( (Integer) results[0] );
			setConstructionMaterialID( (Integer) results[1] );
			setDecadeBuiltID( (Integer) results[2] );
			setSquareFeetID( (Integer) results[3] );
			setInsulationDepthID( (Integer) results[4] );
			setGeneralConditionID( (Integer) results[5] );
			setMainCoolingSystemID( (Integer) results[6] );
			setMainHeatingSystemID( (Integer) results[7] );
			setNumberOfOccupantsID( (Integer) results[8] );
			setOwnershipTypeID( (Integer) results[9] );
			setMainFuelTypeID( (Integer) results[10] );
			setNotes( (String) results[11] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getResidenceTypeID(), getConstructionMaterialID(), getDecadeBuiltID(), getSquareFeetID(),
			getInsulationDepthID(), getGeneralConditionID(), getMainCoolingSystemID(), getMainHeatingSystemID(),
			getNumberOfOccupantsID(), getOwnershipTypeID(), getMainFuelTypeID(), getNotes()
		};
		Object[] constraintValues = { getAccountSiteID() };
		
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	
	public static CustomerResidence getCustomerResidence(int accountSiteID) {
		String sql = "SELECT AccountSiteID, " +
				SETTER_COLUMNS[0] + ", " +
				SETTER_COLUMNS[1] + ", " +
				SETTER_COLUMNS[2] + ", " +
				SETTER_COLUMNS[3] + ", " +
				SETTER_COLUMNS[4] + ", " +
				SETTER_COLUMNS[5] + ", " +
				SETTER_COLUMNS[6] + ", " +
				SETTER_COLUMNS[7] + ", " +
				SETTER_COLUMNS[8] + ", " +
				SETTER_COLUMNS[9] + ", " +
				SETTER_COLUMNS[10] + ", " +
				SETTER_COLUMNS[11] +
				" FROM " + TABLE_NAME +
				" WHERE AccountSiteID = " + accountSiteID;
		
		com.cannontech.database.SqlStatement stmt =
				new com.cannontech.database.SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			if (stmt.getRowCount() == 0) return null;
			
			Object[] row = stmt.getRow(0);
			CustomerResidence custRes = new CustomerResidence();
			custRes.setAccountSiteID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
			custRes.setResidenceTypeID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
			custRes.setConstructionMaterialID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
			custRes.setDecadeBuiltID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
			custRes.setSquareFeetID( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
			custRes.setInsulationDepthID( new Integer(((java.math.BigDecimal) row[5]).intValue()) );
			custRes.setGeneralConditionID( new Integer(((java.math.BigDecimal) row[6]).intValue()) );
			custRes.setMainCoolingSystemID( new Integer(((java.math.BigDecimal) row[7]).intValue()) );
			custRes.setMainHeatingSystemID( new Integer(((java.math.BigDecimal) row[8]).intValue()) );
			custRes.setNumberOfOccupantsID( new Integer(((java.math.BigDecimal) row[9]).intValue()) );
			custRes.setOwnershipTypeID( new Integer(((java.math.BigDecimal) row[10]).intValue()) );
			custRes.setMainFuelTypeID( new Integer(((java.math.BigDecimal) row[11]).intValue()) );
			custRes.setNotes( (String) row[12] );
			
			return custRes;
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}

	/**
	 * Returns the accountSiteID.
	 * @return Integer
	 */
	public Integer getAccountSiteID() {
		return accountSiteID;
	}

	/**
	 * Returns the constructionMaterialID.
	 * @return Integer
	 */
	public Integer getConstructionMaterialID() {
		return constructionMaterialID;
	}

	/**
	 * Returns the decadeBuiltID.
	 * @return Integer
	 */
	public Integer getDecadeBuiltID() {
		return decadeBuiltID;
	}

	/**
	 * Returns the generalConditionID.
	 * @return Integer
	 */
	public Integer getGeneralConditionID() {
		return generalConditionID;
	}

	/**
	 * Returns the insulationDepthID.
	 * @return Integer
	 */
	public Integer getInsulationDepthID() {
		return insulationDepthID;
	}

	/**
	 * Returns the mainCoolingSystemID.
	 * @return Integer
	 */
	public Integer getMainCoolingSystemID() {
		return mainCoolingSystemID;
	}

	/**
	 * Returns the mainFuelTypeID.
	 * @return Integer
	 */
	public Integer getMainFuelTypeID() {
		return mainFuelTypeID;
	}

	/**
	 * Returns the mainHeatingSystemID.
	 * @return Integer
	 */
	public Integer getMainHeatingSystemID() {
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
	 * @return Integer
	 */
	public Integer getNumberOfOccupantsID() {
		return numberOfOccupantsID;
	}

	/**
	 * Returns the ownershipTypeID.
	 * @return Integer
	 */
	public Integer getOwnershipTypeID() {
		return ownershipTypeID;
	}

	/**
	 * Returns the residenceTypeID.
	 * @return Integer
	 */
	public Integer getResidenceTypeID() {
		return residenceTypeID;
	}

	/**
	 * Returns the squareFeetID.
	 * @return Integer
	 */
	public Integer getSquareFeetID() {
		return squareFeetID;
	}

	/**
	 * Sets the accountSiteID.
	 * @param accountSiteID The accountSiteID to set
	 */
	public void setAccountSiteID(Integer accountSiteID) {
		this.accountSiteID = accountSiteID;
	}

	/**
	 * Sets the constructionMaterialID.
	 * @param constructionMaterialID The constructionMaterialID to set
	 */
	public void setConstructionMaterialID(Integer constructionMaterialID) {
		this.constructionMaterialID = constructionMaterialID;
	}

	/**
	 * Sets the decadeBuiltID.
	 * @param decadeBuiltID The decadeBuiltID to set
	 */
	public void setDecadeBuiltID(Integer decadeBuiltID) {
		this.decadeBuiltID = decadeBuiltID;
	}

	/**
	 * Sets the generalConditionID.
	 * @param generalConditionID The generalConditionID to set
	 */
	public void setGeneralConditionID(Integer generalConditionID) {
		this.generalConditionID = generalConditionID;
	}

	/**
	 * Sets the insulationDepthID.
	 * @param insulationDepthID The insulationDepthID to set
	 */
	public void setInsulationDepthID(Integer insulationDepthID) {
		this.insulationDepthID = insulationDepthID;
	}

	/**
	 * Sets the mainCoolingSystemID.
	 * @param mainCoolingSystemID The mainCoolingSystemID to set
	 */
	public void setMainCoolingSystemID(Integer mainCoolingSystemID) {
		this.mainCoolingSystemID = mainCoolingSystemID;
	}

	/**
	 * Sets the mainFuelTypeID.
	 * @param mainFuelTypeID The mainFuelTypeID to set
	 */
	public void setMainFuelTypeID(Integer mainFuelTypeID) {
		this.mainFuelTypeID = mainFuelTypeID;
	}

	/**
	 * Sets the mainHeatingSystemID.
	 * @param mainHeatingSystemID The mainHeatingSystemID to set
	 */
	public void setMainHeatingSystemID(Integer mainHeatingSystemID) {
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
	public void setNumberOfOccupantsID(Integer numberOfOccupantsID) {
		this.numberOfOccupantsID = numberOfOccupantsID;
	}

	/**
	 * Sets the ownershipTypeID.
	 * @param ownershipTypeID The ownershipTypeID to set
	 */
	public void setOwnershipTypeID(Integer ownershipTypeID) {
		this.ownershipTypeID = ownershipTypeID;
	}

	/**
	 * Sets the residenceTypeID.
	 * @param residenceTypeID The residenceTypeID to set
	 */
	public void setResidenceTypeID(Integer residenceTypeID) {
		this.residenceTypeID = residenceTypeID;
	}

	/**
	 * Sets the squareFeetID.
	 * @param squareFeetID The squareFeetID to set
	 */
	public void setSquareFeetID(Integer squareFeetID) {
		this.squareFeetID = squareFeetID;
	}

}
