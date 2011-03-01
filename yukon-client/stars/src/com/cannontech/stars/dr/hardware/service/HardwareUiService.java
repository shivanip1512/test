package com.cannontech.stars.dr.hardware.service;

import java.util.List;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.inventory.LMHardwareClass;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.stars.dr.hardware.model.HardwareHistory;
import com.cannontech.stars.dr.hardware.model.SwitchAssignment;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ListMultimap;

public interface HardwareUiService {

    /**
     * Returns a HarwareDto for the given hardware id.
     */
    public HardwareDto getHardwareDto(int inventoryId, int energyCompanyId, int accountId);

    /**
     * Updates hardware and returns true if the state of the hardware changed
     * to spawn an event.
     */
    public boolean updateHardware(YukonUserContext userContext, HardwareDto hardwareDto) throws ObjectInOtherEnergyCompanyException;

    /**
     * Creates and returns a SimpleDevice for an LCR-3102 with the given device name
     */
    public SimpleDevice createTwoWayDevice(YukonUserContext userContext, int inventoryId, String deviceName) throws StarsTwoWayLcrYukonDeviceCreationException;

    /**
     * Returns a list of HardwareHistory for the given inventory id.
     */
    public List<HardwareHistory> getHardwareHistory(int inventoryId);

    /**
     * Retrieves the hardware for account in a list map as either a meter, thermostat or switch
     * where the key LMHardwareClass.METER is for the list of meters, LMHardwareClass.THERMOSTAT
     * for the list of thermostats and LMHardwareClass.SWITCH for the list of switches.
     */
    public ListMultimap<LMHardwareClass, HardwareDto> getHardwareMapForAccount(int accountId, int energyCompanyId);

    /**
     * Checks that the inventory items are assigned to the account and throws a NotAuthorizedExcpetion
     * if any of them are not.
     */
    void validateInventoryAgainstAccount(List<Integer> inventoryIdList, int accountId) throws NotAuthorizedException;

    /**
     * Creates hardware based on hardwareDto settings and returns
     * the resulting inventoryId.
     */
    public int createHardware(HardwareDto hardwareDto, int accountId, YukonUserContext userContext) throws ObjectInOtherEnergyCompanyException;

    /**
     * Adds a device to an acccount.  If fromAccount is true, removes it from it's
     * old account before adding to this account.
     * @param fromAccount if true will be removed from it's current account before added to this one.
     */
    public void addDeviceToAccount(LiteInventoryBase liteInventoryBase, int accountId, boolean fromAccount, LiteStarsEnergyCompany energyCompany, LiteYukonUser user);

    /**
     * Adds a meter to the account.
     */
    public void addYukonMeter(int meterId, int accountId, YukonUserContext userContext);

    /**
     * Returns a list of SwitchAssignment's for the switches assigned to the meter for this account.
     */
    public List<SwitchAssignment> getSwitchAssignments(List<Integer> assignedIds, int accountId);

    /**
     * Removes the inventory with id 'oldInventoryId' from the account and adds the 
     * inventory with id 'changeOutId'.
     * @param isMeter true if this is a meter change out, the changeOutId will be a pao id
     */
    public void changeOutInventory(int oldInventoryId, int changeOutId, YukonUserContext userContext, boolean isMeter);

}