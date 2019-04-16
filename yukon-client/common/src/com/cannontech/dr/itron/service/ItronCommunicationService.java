package com.cannontech.dr.itron.service;

import java.util.List;
import java.util.zip.ZipFile;

import org.joda.time.Duration;

import com.cannontech.common.inventory.Hardware;
import com.cannontech.dr.itron.service.impl.RecentEventParticipationItronData;
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
     * Attempts to sync Yukon account with Itron
     * 1. Finds all itron programs and groups for account
     * 2. If the group or the program is not created with itron, sends request to create
     * 3. Sends enrollment request to itron to map programs to account
     * 4. Finds all devices in the groups
     * 5. Excludes all opted out inventory
     * 6. Finds mac address for each device
     * 7. For each group sends all mac addresses to itron
     */
    void enroll(int accountId, int groupId);

    /**
     * Attempts to sync Yukon account with Itron
     * 1. Finds all itron programs and groups for account
     * 2. Sends enrollment request to itron to map programs to account
     * 3. Finds all devices in the groups
     * 4. Excludes all opted out inventory
     * 5. Finds mac address for each device
     * 6. For each group sends all mac addresses to itron
     */
    void unenroll(int accountId, int groupId);

    /**
     * Attempts to sync Yukon account with Itron
     * 1. Finds the group device is enrolled into
     * 2. Finds all devices in the group
     * 3. Excludes all opted out inventory
     * 4. Finds mac address for each device
     * 5. Sends all mac addresses to itron for the group
     * 6. Send cancel load control event with itron group id and mac addess to itron
     */
    void optOut(int accountId, int deviceId, int inventoryId);

    /**
     * Attempts to sync Yukon account with Itron
     * 1. Finds the group device is enrolled into
     * 2. Finds all devices in the group
     * 3. Excludes all opted out inventory
     * 4. Finds mac address for each device
     * 5. Sends all mac addresses to itron for the group
     */
    void optIn(int accountId, int inventoryId);

    /**
     * Sends restore request to Itron
     */
    void sendRestore(int yukonGroupId);

    /**
     * Sends control request to Itron
     * @return two values utilized from the caller to initialize Recent Event Participation table entry;
     */
    RecentEventParticipationItronData sendDREventForGroup(int yukonGroupId, int dutyCycleType, int dutyCyclePercent, int dutyCyclePeriod, int criticality,
            boolean rampIn, boolean rampOut, Duration controlDuration);

    /**
     * Downloads devices logs for all devices from itron, copies and zips the files to ExportArchive/Itron
     */
    ZipFile exportDeviceLogs(Long startRecordId, Long endRecordId);

    /**
     * Downloads devices logs for an itron device group, copies and zips the files to ExportArchive/Itron
     */

    ZipFile exportDeviceLogsForItronGroup(Long startRecordId, Long endRecordId, List<Integer> deviceId);
}
