package com.cannontech.stars.database.db;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.stars.energyCompany.EcMappingCategory;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ECToGenericMapping extends DBPersistent {

	private Integer energyCompanyID = null;
	private Integer itemID = null;
	private EcMappingCategory mappingCategory = null;
	
	public static final String[] SETTER_COLUMNS = {};
	
	public static final String[] CONSTRAINT_COLUMNS = {
		"EnergyCompanyID", "ItemID", "MappingCategory"
	};
	
	public static final String TABLE_NAME = "ECToGenericMapping";

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	@Override
    public void add() throws SQLException {
        Object[] addValues = {
        	getEnergyCompanyID(), getItemID(), getMappingCategory().getDatabaseRepresentation()
        };

        add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	@Override
    public void delete() throws SQLException {
        Object[] constraintValues = {
        	getEnergyCompanyID(), getItemID(), getMappingCategory().getDatabaseRepresentation()
        };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	@Override
    public void retrieve() throws SQLException {
		throw new RuntimeException( "This method is not supported" );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	@Override
    public void update() throws SQLException {
		throw new RuntimeException( "This method is not supported" );
	}

	/**
	 * Returns the energyCompanyID.
	 * @return Integer
	 */
	public Integer getEnergyCompanyID() {
		return energyCompanyID;
	}

	/**
	 * Returns the itemID.
	 * @return Integer
	 */
	public Integer getItemID() {
		return itemID;
	}

	/**
	 * Returns the mappingCategory.
	 * @return EcMappingCategory
	 */
	public EcMappingCategory getMappingCategory() {
		return mappingCategory;
	}

	/**
	 * Sets the energyCompanyID.
	 * @param energyCompanyID The energyCompanyID to set
	 */
	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

	/**
	 * Sets the itemID.
	 * @param itemID The itemID to set
	 */
	public void setItemID(Integer itemID) {
		this.itemID = itemID;
	}

	/**
	 * Sets the mappingCategory.
	 * @param mappingCategory The mappingCategory to set
	 */
	public void setMappingCategory(EcMappingCategory mappingCategory) {
		this.mappingCategory = mappingCategory;
	}

}
