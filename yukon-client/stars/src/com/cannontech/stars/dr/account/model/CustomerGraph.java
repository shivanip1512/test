package com.cannontech.stars.dr.account.model;

public class CustomerGraph {

	private int graphDefinitionId;
	private int customerId;
	private int customerOrder;
	
	public int getGraphDefinitionId() {
		return graphDefinitionId;
	}
	public void setGraphDefinitionId(int graphDefinitionId) {
		this.graphDefinitionId = graphDefinitionId;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public int getCustomerOrder() {
		return customerOrder;
	}
	public void setCustomerOrder(int customerOrder) {
		this.customerOrder = customerOrder;
	}
}
