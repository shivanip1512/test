package com.cannontech.stars.dr.hardware.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
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
    public Multimap<Integer, PaoIdentifier> getRelayToDeviceMapByLoadGroups(Collection<Integer> loadGroupIds);
}
