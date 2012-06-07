package com.cannontech.stars.dr.optout.dao;

/**
 * Dao class for persisting additional opt outs
 */
public interface OptOutAdditionalDao {

	/**
	 * Method to get the current number of additional opt outs for the 
	 * inventory/account
	 * @param inventoryId - Inventory to get additional opt outs for
	 * @param customerAccountId - Customer account for inventory
	 * @return Number of additional opt outs
	 */
	public int getAdditionalOptOuts(int inventoryId, int customerAccountId);
	
	/**
	 * Method used to give extra opt outs beyond the preset limits
	 * @param inventoryId - Inventory to allow extra opt out on
	 * @param customerAccountId - Customer account for inventory
	 * @param additionalOptOuts - Number of extra opt outs to give
	 */ 
	public void addAdditonalOptOuts(int inventoryId, int customerAccountId, int additionalOptOuts);
}
