package com.cannontech.stars.dr.account.dao;

import java.util.List;

import com.cannontech.stars.dr.account.model.CustomerGraph;

public interface CustomerGraphDao {

	public void insert(CustomerGraph customerGraph);
	public void update(CustomerGraph customerGraph);
	public List<CustomerGraph> getByCustomerId(int customerId);
	public void deleteAllCustomerGraphsByCustomerId(int customerId);
	public String getGraphName(int graphDefinitionId);
}
