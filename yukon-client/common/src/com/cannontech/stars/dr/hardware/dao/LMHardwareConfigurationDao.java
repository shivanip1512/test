package com.cannontech.stars.dr.hardware.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.dr.assetavailability.DeviceRelayApplianceCategories;
import com.cannontech.dr.assetavailability.InventoryRelayAppliances;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.google.common.collect.Multimap;

public interface LMHardwareConfigurationDao {
    
    public void add(LMHardwareConfiguration lmHardwareConfiguration);
    
    public void delete(LMHardwareConfiguration lmHardwareConfiguration);
    
    public void update(LMHardwareConfiguration lmHardwareConfiguration);

    public void delete(int invetoryId);

    public void delete(Collection<Integer> inventoryIds);

    public void deleteForAppliance(int applianceId);

    public void deleteForAppliances(Collection<Integer> applianceIds);
    
    public LMHardwareConfiguration getStaticLoadGroupMapping(
            LiteAccountInfo liteAcct, LiteLmHardwareBase lmHw,
            LiteStarsEnergyCompany energyCompany);

    public List<LMHardwareConfiguration> getForInventoryId(int inventoryId);    

    /**
     * For a collection of inventory, gets the applianceCategoryIds of each appliance mapped to the
     * device and relay.
     */
    public DeviceRelayApplianceCategories getDeviceRelayApplianceCategoryId(Iterable<Integer> inventoryIds);
    
    /**
     * @return An object encompassing inventoryId to Relay to ApplianceId mapping for all inventory specified.
     */
    public InventoryRelayAppliances getInventoryRelayAppliances(Iterable<Integer> inventoryIds);
    
    /**
     * Gets a multimap of inventoryId to applianceCategoryId for appliances attached to the specified inventory.
     * If an inventory has multiple attached appliances in the same applianceCategory, there will be duplicate entries.
     */
    public Multimap<Integer, Integer> getInventoryApplianceMap(Iterable<Integer> inventoryIds);

    /**
     * Gets a map of deviceId to relayId for inventory in the specified groups.
     */
    Map<Integer, Integer> getDeviceIdToRelayMapByLoadGroups(Iterable<Integer> loadGroupIds);
}
