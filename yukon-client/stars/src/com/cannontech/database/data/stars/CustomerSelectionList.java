package com.cannontech.database.data.stars;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.stars.xml.serialize.*;

/**
 * <p>Title: CustomerSelectionList.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 13, 2002 3:06:03 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class CustomerSelectionList extends DBPersistent {
	
	private com.cannontech.database.db.stars.CustomerSelectionList customerSelectionList = null;
    
    private com.cannontech.database.data.company.EnergyCompanyBase energyCompanyBase = null;

	public void setListID(Integer newID) {
		getCustomerSelectionList().setListID( newID );
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection(conn);
		getCustomerSelectionList().setDbConnection(conn);
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
    	if (getEnergyCompanyBase() == null)
    		throw new java.sql.SQLException("Add: setEnergyCompanyBase() must be called before this function");
    		
		getCustomerSelectionList().add();
        
        if (getEnergyCompanyBase().getEnergyCompany() != null) {
        	// Add to mapping table
        	Object[] addValues = {
        		getEnergyCompanyBase().getEnergyCompany().getEnergyCompanyID(),
        		getCustomerSelectionList().getListID(),
        		getCustomerSelectionList().TABLE_NAME
        	};
        	add("ECToGenericMapping", addValues);
        }
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
    	// delete from mapping table
    	delete("ECToGenericMapping", "ItemID", getCustomerSelectionList().getListID());
    	
		getCustomerSelectionList().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getCustomerSelectionList().retrieve();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getCustomerSelectionList().update();
	}
	
	public static com.cannontech.database.db.stars.CustomerSelectionList[] getAllSelectionLists(Integer energyCompanyID) {
    	java.sql.Connection conn = null;

    	try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
            return com.cannontech.database.db.stars.CustomerSelectionList.getAllSelectionLists(energyCompanyID, conn);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if (conn != null) conn.close();
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	return null;
	}

	/**
	 * Returns the customerSelectionList.
	 * @return com.cannontech.database.db.stars.CustomerSelectionList
	 */
	public com.cannontech.database.db.stars.CustomerSelectionList getCustomerSelectionList() {
		if (customerSelectionList == null)
			customerSelectionList = new com.cannontech.database.db.stars.CustomerSelectionList();
		return customerSelectionList;
	}

	/**
	 * Sets the customerSelectionList.
	 * @param customerSelectionList The customerSelectionList to set
	 */
	public void setCustomerSelectionList(
		com.cannontech.database.db.stars.CustomerSelectionList customerSelectionList) {
		this.customerSelectionList = customerSelectionList;
	}

	/**
	 * Returns the energyCompanyBase.
	 * @return com.cannontech.database.data.company.EnergyCompanyBase
	 */
	public com.cannontech.database.data.company.EnergyCompanyBase getEnergyCompanyBase() {
		return energyCompanyBase;
	}

	/**
	 * Sets the energyCompanyBase.
	 * @param energyCompanyBase The energyCompanyBase to set
	 */
	public void setEnergyCompanyBase(
		com.cannontech.database.data.company.EnergyCompanyBase energyCompanyBase) {
		this.energyCompanyBase = energyCompanyBase;
	}

}
