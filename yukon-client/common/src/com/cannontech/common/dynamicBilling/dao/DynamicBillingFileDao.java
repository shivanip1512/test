package com.cannontech.common.dynamicBilling.dao;

import java.util.List;

import com.cannontech.common.dynamicBilling.model.DynamicFormat;

/**
 * Data access class for dynamic billing formats
 */
public interface DynamicBillingFileDao {

	public DynamicFormat retrieve(int formatID);

	public void save(DynamicFormat format);

	public void delete(int formatID);

	public List<DynamicFormat> retrieveAll();
	
	public boolean isFormatNameUnique(DynamicFormat format); 
	
}
