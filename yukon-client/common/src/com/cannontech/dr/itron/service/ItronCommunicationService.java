package com.cannontech.dr.itron.service;

import com.cannontech.common.inventory.Hardware;

public interface ItronCommunicationService {

    /**
     * Use to add individual HAN devices. The add device API
     * allows a utility to provide self-service capability to their
     * customers to add a ZigBee, Direct-to-Grid, or ESI devices
     * from the utility's web portal,
     */
    void addDevice(Hardware hardware);

    /**
     * Use to create and add a service point, which can include customer's
     * Account and Location information.
     */
    void addServicePoint(int accountId, int energyCompanyId, int inventoryId);
}
