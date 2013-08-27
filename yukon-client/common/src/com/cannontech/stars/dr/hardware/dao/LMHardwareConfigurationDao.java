package com.cannontech.stars.dr.hardware.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.assetavailability.DeviceRelayApplianceCategories;
import com.cannontech.dr.assetavailability.InventoryRelayAppliances;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
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
     * @return A multimap of relay -> device for all inventory devices in the specified load group.
     * This uses the relay mapping specified during program enrollment.
     */
    public Multimap<Integer, PaoIdentifier> getRelayToDeviceMapByLoadGroups(Iterable<Integer> loadGroupIds);
    
    /**
     * @return A multimap of relay -> device for all inventory devices specified.
     * This uses the relay mapping specified during program enrollment.
     */
    public Multimap<Integer, PaoIdentifier> getRelayToDeviceMapByDeviceIds(Iterable<Integer> deviceIds);
    
    /**
     * For a collection of inventory, gets the applianceCategoryIds of each appliance mapped to the
     * device and relay.
     */
    public DeviceRelayApplianceCategories getDeviceRelayApplianceCategoryId(Iterable<Integer> inventoryIds);
    
    /**
     * @return An object encompassing inventoryId to Relay to ApplianceId mapping for all inventory specified.
     */
    public InventoryRelayAppliances getInventoryRelayAppliances(Iterable<Integer> inventoryIds);
}
