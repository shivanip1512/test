package com.cannontech.web.visualDisplays.service;

import java.util.List;

import com.cannontech.web.visualDisplays.model.PowerSuppliersEnum;

public interface VisualDisplaysService {

	/**
	 * This method will loop through all PowerSupplier type defined in PowerSupplierEnum, look for a point ID
	 * for the Current Load Object ID in the FDR table. If found, it will be added to the available power suppliers list.
	 * @return
	 */
	public List<PowerSuppliersEnum> getAvailablePowerSuppliers();
}
