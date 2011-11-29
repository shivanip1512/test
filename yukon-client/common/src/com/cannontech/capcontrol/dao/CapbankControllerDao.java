package com.cannontech.capcontrol.dao;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.providers.fields.DeviceAddressFields;
import com.cannontech.common.pao.service.providers.fields.DeviceFields;
import com.cannontech.common.pao.service.providers.fields.DeviceScanRateFields;
import com.cannontech.common.pao.service.providers.fields.DeviceWindowFields;
import com.cannontech.common.search.SearchResult;

public interface CapbankControllerDao {
	
    /**
     * Assigns a cbc to a capbank and performs the necessary dbchange message sending.
     * @param controllerId the id of the CBC being assigned
     * @param capbankName the name of the capbank being assigned to.
     * @return true if the assignment occurred with only one row in the db updated, false otherwise.
     */
    public boolean assignController(int controllerId, String capbankName);
    
    /**
     * Assigns a cbc to a capbank and performs the necessary dbchange message sending.
     * @param capbankId the id of the capbank being assigned to.
     * @param controllerId the id of the CBC being assigned
     * @return true if the assignment occurred with only one row in the db updated, false otherwise.
     */
    public boolean assignController(int capbankId, int controllerId);

    /**
     * Removes all assignments for the given controller
     * @param controller the paoId of the CBC
     * @return true if the unassignment occurred with only one row in the db 
     * updated, false otherwise.
     */
    public boolean unassignController(int controller);
    
    // TODO: Find a nice place for these to live. -jg
    /**
     * Retrieves the DeviceFields data for a CBC.
     * @param paoIdentifier the {@link PaoIdentifier} representing the CBC.
     * @return DeviceFields containing the data from the DeviceFields table in the DB.
     */
    public DeviceFields getDeviceData(PaoIdentifier paoIdentifier);
    
    /**
     * Retrieves the {@link DeviceScanRateFields} data for a CBC.
     * @param paoIdentifier the {@link PaoIdentifier} representing the CBC.
     * @return {@link DeviceScanRateFields} containing the data from the 
     * {@link DeviceScanRateFields} table in the DB.
     */
    public DeviceScanRateFields getDeviceScanRateData(PaoIdentifier paoIdentifier);
    
    /**
     * Retrieves the {@link DeviceWindowFields} data for a CBC.
     * @param paoIdentifier the {@link PaoIdentifier} representing the CBC.
     * @return {@link DeviceWindowFields} containing the data from the {@link DeviceWindowFields} 
     * table in the database.
     */
    public DeviceWindowFields getDeviceWindowData(PaoIdentifier paoIdentifier);
    
    /**
     * Retrieves the {@link DeviceAddressFields} data for a CBC.
     * @param paoIdentifier the {@link PaoIdentifier} representing the CBC.
     * @return {@link DeviceAddressFields} containing the data from the {@link DeviceAddressFields}
     * table in the database.
     */
    public DeviceAddressFields getDeviceAddressData(PaoIdentifier paoIdentifier);
	
	public SearchResult<LiteCapControlObject> getOrphans(final int start,final int count);
}