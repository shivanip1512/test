/*
 * Created on Dec 27, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis.data.stars;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkOrder {
	
	private int energyCompanyID = -1;
	private int orderID = 0;
	private int inventoryID = 0;
	
	public WorkOrder() {
	}
	
	public WorkOrder(int energyCompanyID, int orderID, int inventoryID) {
		this.energyCompanyID = energyCompanyID;
		this.orderID = orderID;
		this.inventoryID = inventoryID;
	}

	/**
	 * @return
	 */
	public int getEnergyCompanyID() {
		return energyCompanyID;
	}

	/**
	 * @return
	 */
	public int getInventoryID() {
		return inventoryID;
	}

	/**
	 * @return
	 */
	public int getOrderID() {
		return orderID;
	}

	/**
	 * @param i
	 */
	public void setEnergyCompanyID(int i) {
		energyCompanyID = i;
	}

	/**
	 * @param i
	 */
	public void setInventoryID(int i) {
		inventoryID = i;
	}

	/**
	 * @param i
	 */
	public void setOrderID(int i) {
		orderID = i;
	}

}
