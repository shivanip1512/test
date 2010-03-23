package com.cannontech.web.stars.dr.operator.hardware.service;

import java.sql.SQLException;
import java.util.List;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceCreationException;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareDto;
import com.cannontech.web.stars.dr.operator.hardware.service.impl.HardwareServiceImpl.HardwareHistory;
import com.google.common.collect.ListMultimap;

public interface HardwareService {

    /**
     * Returns a HarwareDto for the given hardware id.
     * @param inventoryId
     * @return HardwareDto
     */
    public HardwareDto getHardwareDto(int inventoryId, int energyCompanyId);

    /**
     * Updates hardware and returns true if the state of the hardware changed
     * to spawn an event.
     * @param hardwareDto
     * @return boolean
     */
    public boolean updateHardware(HardwareDto hardwareDto);

    /**
     * If delete is true: deletes the hardware, otherwise just removes it from the 
     * account and places it back in general inventory.
     * @param delete
     * @param inventoryId
     * @param accountId
     * @param energyCompany 
     * @throws Exception 
     */
    public void deleteHardware(boolean delete, int inventoryId, int accountId, LiteStarsEnergyCompany energyCompany) throws Exception;

    /**
     * Creates and returns a SimpleDevice for an LCR-3102 with the given device name
     * @param inventoryId
     * @param deviceName
     * @return SimpleDevice
     * @throws SQLException 
     * @throws NumberFormatException 
     */
    public SimpleDevice createTwoWayDevice(int inventoryId, String deviceName) throws StarsTwoWayLcrYukonDeviceCreationException;

    /**
     * Returns a list of HardwareHistory for the given inventory id.
     * @param inventoryId
     * @return List<HardwareHistory>
     */
    public List<HardwareHistory> getHardwareHistory(int inventoryId);

    /**
     * Retrieves the hardware for account in a list map as either a meter, thermostat or switch.
     * @param accountId
     * @param energyCompanyId
     * @return ListMultimap<String, HardwareDto>
     */
    public ListMultimap<String, HardwareDto> getHardwareMapForAccount(int accountId, int energyCompanyId);

}
