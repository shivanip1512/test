package com.cannontech.database.db.stars;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

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
	private String mappingCategory = null;
	
	public static final String[] SETTER_COLUMNS = {};
	
	public static final String[] CONSTRAINT_COLUMNS = {
		"EnergyCompanyID", "ItemID", "MappingCategory"
	};
	
	public static final String TABLE_NAME = "ECToGenericMapping";

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
        Object[] addValues = {
        	getEnergyCompanyID(), getItemID(), getMappingCategory()
        };

        add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
        Object[] constraintValues = {
        	getEnergyCompanyID(), getItemID(), getMappingCategory()
        };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
	}
	
	public static ECToGenericMapping[] getAllMappingItems(Integer energyCompanyID) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE EnergyCompanyID = " + energyCompanyID.toString();
		
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
				
		try {
			stmt.execute();
			ECToGenericMapping[] items = new ECToGenericMapping[ stmt.getRowCount() ];
			
			for (int i = 0; i < items.length; i++) {
				Object[] row = stmt.getRow(i);
				items[i] = new ECToGenericMapping();
				
				items[i].setEnergyCompanyID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				items[i].setItemID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				items[i].setMappingCategory( (String) row[2] );
			}
			
			return items;
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
	
	public static ECToGenericMapping[] getAllMappingItems(Integer energyCompanyID, String category) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE EnergyCompanyID = " + energyCompanyID.toString()
				   + " AND MappingCategory LIKE '" + category + "%'";
		
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
				
		try {
			stmt.execute();
			ECToGenericMapping[] items = new ECToGenericMapping[ stmt.getRowCount() ];
			
			for (int i = 0; i < items.length; i++) {
				Object[] row = stmt.getRow(i);
				items[i] = new ECToGenericMapping();
				
				items[i].setEnergyCompanyID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				items[i].setItemID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				items[i].setMappingCategory( (String) row[2] );
			}
			
			return items;
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		
		return null;
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
	 * @return String
	 */
	public String getMappingCategory() {
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
	public void setMappingCategory(String mappingCategory) {
		this.mappingCategory = mappingCategory;
	}

}
