package com.cannontech.stars.energyCompany;

import java.util.HashMap;
import java.util.Map;

/**
 * A composite key used for the EnergyCompanySettingDao cache. 
 * 
 * A key consists of EnergyCompnaySettingType and ecId. The keys generated are cached.
 */
public class ECSettingCacheKey {
    private static final Map<Integer, Map<EnergyCompanySettingType, ECSettingCacheKey>> cacheCache = new HashMap<>();

    final int ecId;
    final EnergyCompanySettingType type;

    public ECSettingCacheKey(EnergyCompanySettingType type, int ecId) {
        this.ecId = ecId;
        this.type = type;
    }

    public static ECSettingCacheKey of(EnergyCompanySettingType type, int ecId) {
        if (!cacheCache.containsKey(ecId)) {
            Map<EnergyCompanySettingType, ECSettingCacheKey> typeMap = new HashMap<>();
            typeMap.put(type, new ECSettingCacheKey(type, ecId));
            cacheCache.put(ecId, typeMap);
        } else if (!cacheCache.get(ecId).containsKey(type)) {
            cacheCache.get(ecId).put(type, new ECSettingCacheKey(type, ecId));
        }
        return cacheCache.get(ecId).get(type);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ecId;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ECSettingCacheKey other = (ECSettingCacheKey) obj;
        if (ecId != other.ecId)
            return false;
        if (type != other.type)
            return false;
        return true;
    }
}

