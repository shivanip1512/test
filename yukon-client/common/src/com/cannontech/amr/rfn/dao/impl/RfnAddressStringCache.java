package com.cannontech.amr.rfn.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.database.db.device.RfnAddress;
import com.google.common.collect.Maps;

class RfnAddressStringCache {
    private Map<String, Map<String, SerialLookup>> cache = Maps.newHashMap();

    /**
     * Queries the string cache for an individual RfnIdentifier.
     */
    Integer query(RfnIdentifier rfnIdentifier) {
        return Optional.ofNullable(cache.get(rfnIdentifier.getSensorManufacturer()))
                       .map(manufacturerMap -> manufacturerMap.get(rfnIdentifier.getSensorModel()))
                       .map(modelMap -> modelMap.get(rfnIdentifier.getSensorSerialNumber()))
                       .orElse(null);
    }

    /**
     * Queries the string cache for a list of RfnIdentifiers.
     */
    Set<Integer> query(List<RfnIdentifier> rfnIdentifiers) {
        //  Group the identifiers into a map of manufacturer, model, and serial.
        Map<String, Map<String, List<String>>> groupedStringIdentifiers = 
                rfnIdentifiers.stream()
                              .collect(Collectors.groupingBy(RfnIdentifier::getSensorManufacturer,
                                       Collectors.groupingBy(RfnIdentifier::getSensorModel,
                                       Collectors.mapping(RfnIdentifier::getSensorSerialNumber,
                                       Collectors.toList()))));

        return groupedStringIdentifiers.entrySet().stream()
                .flatMap(this::queryByManufacturer)
                .collect(Collectors.toSet());
    }

    /**
     * Queries the manufacturer cache for a collection of model+serials.
     */
    private Stream<Integer> queryByManufacturer(Entry<String, Map<String, List<String>>> manufacturerQuery) {
        String manufacturer = manufacturerQuery.getKey();
        Map<String, List<String>> modelQueries = manufacturerQuery.getValue();

        var modelCache = cache.get(manufacturer);

        if (modelCache == null) {
            // No cached models for this manufacturer
            return Stream.empty();
        }

        return modelQueries.entrySet().stream()
                .flatMap(modelQuery -> queryByModel(modelQuery, modelCache));
    }

    /**
     * Queries the model cache for a list of serials.
     */
    private Stream<Integer> queryByModel(Entry<String, List<String>> modelQuery, Map<String, SerialLookup> modelCache) {
        String model = modelQuery.getKey();
        List<String> serialQuery = modelQuery.getValue();

        SerialLookup serialCache = modelCache.get(model);

        if (serialCache == null) {
            //  No cached serials for this model
            return Stream.empty();
        }

        return serialCache.getAll(serialQuery);
    }

    void putAll(List<RfnAddress> addresses) {
        addresses.stream()
                 .collect(Collectors.groupingBy(RfnAddress::getManufacturer, 
                          Collectors.groupingBy(RfnAddress::getModel,
                          Collectors.toMap(RfnAddress::getSerialNumber, 
                                           RfnAddress::getDeviceID))))
                 .forEach((newManufacturer, newModels) -> {
                     Map<String, SerialLookup> modelCache = getModelCache(newManufacturer);
                     newModels.forEach((model, serialIds) ->
                         getStringSerialLookup(modelCache, model).putAll(serialIds));
                 });
    }

    void put(RfnAddress address) {
        var modelCache = getModelCache(address.getManufacturer());
        getStringSerialLookup(modelCache, address.getModel()).put(address.getSerialNumber(), 
                                                                  address.getDeviceID());
    }

    /**
     * Gets or constructs the model map for the given manufacturer
     * @param manufacturer The manufacturer to look up
     * @return The model map for the manufacturer
     */
    private Map<String, SerialLookup> getModelCache(String manufacturer) {
        return cache.computeIfAbsent(manufacturer, unused -> Maps.newHashMap());
    }

    /**
     * Gets or constructs the SerialLookup for the given model
     * @param modelCache the cache to use
     * @param model the model to look up
     * @return the SerialLookup for the model
     */
    private SerialLookup getStringSerialLookup(Map<String, SerialLookup> modelCache, String model) {
        return modelCache.computeIfAbsent(model, unused -> new SerialLookup());
    }

    public void removeIds(Set<Integer> paoIds, Map<String, Set<String>> stringManufacturerModels) {
        stringManufacturerModels.entrySet().stream()
            .flatMap(e -> Optional.ofNullable(cache.get(e.getKey())).stream()
                                  .flatMap(modelCache -> e.getValue().stream().map(modelCache::get)))
            .forEach(lookup -> lookup.removeAll(paoIds));
    }

    public void removeId(Integer paoId, RfnIdentifier rfnIdentifier) {
        Optional.ofNullable(cache.get(rfnIdentifier.getSensorManufacturer()))
            .map(modelCache -> modelCache.get(rfnIdentifier.getSensorModel()))
            .ifPresent(lookup -> lookup.remove(paoId));
    }
}
