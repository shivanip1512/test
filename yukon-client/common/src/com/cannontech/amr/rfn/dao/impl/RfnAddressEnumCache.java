package com.cannontech.amr.rfn.dao.impl;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cannontech.common.rfn.model.RfnManufacturerModel;

class RfnAddressEnumCache {

    private Map<RfnManufacturerModel, SerialLookup> cache = new EnumMap<>(RfnManufacturerModel.class);

    /**
     * Gets or constructs a new SerialLookup for the given RfnManufacturerModel.
     */
    private SerialLookup getModelCache(RfnManufacturerModel mm) {
        return cache.computeIfAbsent(mm, unused -> new SerialLookup());
    }

    /**
     * Queries the enum cache for an individual RfnManufacturerModel and serial.
     */
    Integer query(RfnManufacturerModel rmm, String serial) {
        return Optional.ofNullable(cache.get(rmm))
                .map(modelMap -> modelMap.get(serial))
                .orElse(null);
    }

    /**
     * Queries the enum cache for a collection of RfnManufacturerModel serial numbers.
     */
    Set<Integer> query(Map<RfnManufacturerModel, List<String>> parsedIdentifiers) {
        return parsedIdentifiers.entrySet().stream()
                    .flatMap(e -> 
                            Optional.ofNullable(cache.get(e.getKey()))
                                    .map(serialCache -> serialCache.getAll(e.getValue()))
                                    .orElse(Stream.empty()))
                    .collect(Collectors.toSet());
    }

    public void putAll(RfnManufacturerModel mm, Map<String, Integer> serialIds) {
        getModelCache(mm).putAll(serialIds);
    }

    public void put(RfnManufacturerModel mm, String serialNumber, Integer paoId) {
        getModelCache(mm).put(serialNumber, paoId);
    }

    void removeIds(Set<Integer> paoIds, Set<RfnManufacturerModel> enumManufacturerModels) {
        enumManufacturerModels.stream()
            .flatMap(emm -> Optional.ofNullable(cache.get(emm)).stream())
            .forEach(lookup -> lookup.removeAll(paoIds));
    }
    
    void removeId(Integer paoId, RfnManufacturerModel mm) {
        Optional.ofNullable(cache.get(mm))
            .ifPresent(lookup -> lookup.remove(paoId));
    }
}
