package com.cannontech.database.data.constants;

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
public class YukonSelectionList extends DBPersistent {
	
	private com.cannontech.database.db.constants.YukonSelectionList yukonSelectionList = null;
	private Integer energyCompanyID = null;
	
	public YukonSelectionList() {
		super();
	}
	
	public void setListID(Integer listID) {
		getYukonSelectionList().setListID( listID );
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection( conn );
		getYukonSelectionList().setDbConnection( conn );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
    	if (getEnergyCompanyID() == null)
    		throw new java.sql.SQLException("Add: setEnergyCompanyID() must be called before this function");
    	
    	getYukonSelectionList().add();
        
    	// Add to mapping table
    	Object[] addValues = {
    		getEnergyCompanyID(),
    		getYukonSelectionList().getListID(),
    		com.cannontech.database.db.constants.YukonSelectionList.TABLE_NAME
    	};
    	add("ECToGenericMapping", addValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
    	// delete from mapping table
    	String[] constraintColumns = {
    		"ItemID", "MappingCategory"
    	};
    	Object[] constraintValues = {
    		getYukonSelectionList().getListID(),
    		com.cannontech.database.db.constants.YukonSelectionList.TABLE_NAME
    	};
    	delete("ECToGenericMapping", constraintColumns, constraintValues);
    	
    	getYukonSelectionList().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getYukonSelectionList().retrieve();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getYukonSelectionList().update();
	}

	/**
	 * Returns the energyCompanyID.
	 * @return Integer
	 */
	public Integer getEnergyCompanyID() {
		return energyCompanyID;
	}

	/**
	 * Returns the yukonSelectionList.
	 * @return com.cannontech.database.db.constants.YukonSelectionList
	 */
	public com.cannontech.database.db.constants.YukonSelectionList getYukonSelectionList() {
		if (yukonSelectionList == null)
			yukonSelectionList = new com.cannontech.database.db.constants.YukonSelectionList();
		return yukonSelectionList;
	}

	/**
	 * Sets the energyCompanyID.
	 * @param energyCompanyID The energyCompanyID to set
	 */
	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

	/**
	 * Sets the yukonSelectionList.
	 * @param yukonSelectionList The yukonSelectionList to set
	 */
	public void setYukonSelectionList(com.cannontech.database.db.constants.YukonSelectionList yukonSelectionList) {
		this.yukonSelectionList = yukonSelectionList;
	}

}
