package com.cannontech.dr.itron.service;

import com.cannontech.common.inventory.Hardware;
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
   
    /**
     * Attempts to get itron group id from the database, if doesn't exist sends request to itron to create
     * group, persist the group id returned by itron to the database.
     * 
     * @return itron group id
     */
    long getProgram(int paoId);

    /**
     * Attempts to get itron program id from the database, if doesn't exist sends request to itron to create
     * program, persist the program id returned by itron to the database.
     * 
     * @return itron program id
     */
    long getGroup(int paoId);
}
