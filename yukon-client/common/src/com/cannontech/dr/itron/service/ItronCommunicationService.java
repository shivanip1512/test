package com.cannontech.dr.itron.service;

import com.cannontech.common.inventory.Hardware;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.dr.account.model.AccountDto;

public interface ItronCommunicationService {

    /**
     * Use to add individual HAN devices. The add device API
     * allows a utility to provide self-service capability to their
     * customers to add a ZigBee, Direct-to-Grid, or ESI devices
     * from the utility's web portal,
     */
    public void addDevice(Hardware hardware, AccountDto account);

    /**
     * Use to create and add a service point, which can include customer's
     * Account and Location information.
     */
    public void addServicePoint(AccountDto account, String macAddress);

    /**
     * Removes existing Device from Service Point
     */
    void removeDeviceFromServicePoint(String macAddress);
  
    void createGroup(LiteYukonPAObject pao);

    void enroll(int accountId, int deviceId, LiteYukonPAObject programPao, LiteYukonPAObject groupPao);

    void unenroll(int accountId);
}
