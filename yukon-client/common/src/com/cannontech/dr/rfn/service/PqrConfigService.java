package com.cannontech.dr.rfn.service;

import java.util.Collection;
import java.util.Optional;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.rfn.model.PqrConfig;
import com.cannontech.dr.rfn.model.PqrConfigResult;

/**
 * Service to send Power Quality Response configuration messages to PQR-enabled devices.
 */
public interface PqrConfigService {
    
    /**
     * Send the specfied PQR configuration to these devices. Any hardware that does not support PQR will not be
     * configured. Multiple messages may be sent to each device, depending on the specified configuration.
     * WARNING: The messaging is not throttled by Yukon.
     * @return The result identifier, for retrieving the results of the operation.
     */
    public String sendConfigs(Collection<InventoryIdentifier> inventory, PqrConfig config, LiteYukonUser user);
    
    /**
     * Retrieve the results of a PQR configuration operation by its identifier. The results will be updated as the
     * operation is carried out.
     * @return An Optional containing the result with the specified ID, if it exists.
     */
    public Optional<PqrConfigResult> getResult(String resultId);
}
