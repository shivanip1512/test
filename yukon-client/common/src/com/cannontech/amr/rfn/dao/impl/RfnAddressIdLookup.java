package com.cannontech.amr.rfn.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.database.db.device.RfnAddress;
import com.google.common.collect.Maps;

class RfnAddressIdLookup {

    /**
     * Map of Manufacturer and Model string to SerialLookup.
     */
    private Map<String, SerialLookup> cache = Maps.newHashMap();
    
    /**
     * Creates a combined key out of an RfnIdentifier's manufacturer and model.
     * @param rfnIdentifier
     * @return The combined key, as a String.
     */
    private static String combinedKey(RfnIdentifier rfnIdentifier) {
        return rfnIdentifier.getSensorManufacturer() + " " + rfnIdentifier.getSensorModel();
    }

    /**
     * Creates a combined key out of an RfnAddress's manufacturer and model.
     * @param rfnAddress
     * @return The combined key, as a String.
     */
    private static String combinedKey(RfnAddress rfnAddress) {
        //  Sensor model first, since that is most likely to mismatch
        return rfnAddress.getModel() + rfnAddress.getManufacturer();
    }

    
    /**
     * Queries the cache for an individual RfnIdentifier.
     */
    Integer get(RfnIdentifier rfnIdentifier) {
        return Optional.ofNullable(cache.get(combinedKey(rfnIdentifier)))
                       .map(modelMap -> modelMap.get(rfnIdentifier.getSensorSerialNumber()))
                       .orElse(null);
    }

    /**
     * Queries the cache for a list of RfnIdentifiers.
     */
    Set<Integer> get(Iterable<RfnIdentifier> rfnIdentifiers) {
        Map<String, List<String>> keyToIdentifiers = 
                StreamUtils.stream(rfnIdentifiers)
                          .collect(Collectors.groupingBy(RfnAddressIdLookup::combinedKey,
                                   Collectors.mapping(RfnIdentifier::getSensorSerialNumber, 
                                   Collectors.toList())));
        
        return keyToIdentifiers.entrySet().stream()
                       .flatMap(e -> Optional.ofNullable(cache.get(e.getKey()))
                                             .map(lookup -> lookup.getAll(e.getValue()))
                                             .stream())
                       .flatMap(Function.identity())
                       .collect(Collectors.toSet());
    }

    /**
     * Puts a single address mapping into the cache.
     */
    void put(RfnAddress address) {
        cache.computeIfAbsent(combinedKey(address), unused -> new SerialLookup())
             .put(address.getSerialNumber(), address.getDeviceID());
    }

    /**
     * Puts multiple address mappings into the cache.
     */
    void putAll(List<RfnAddress> addresses) {
        addresses.stream()
                 .collect(Collectors.groupingBy(RfnAddressIdLookup::combinedKey, 
                          Collectors.toMap(RfnAddress::getSerialNumber, 
                                           RfnAddress::getDeviceID)))
                 .forEach((key, serialIds) -> 
                     cache.computeIfAbsent(key, unused -> new SerialLookup())
                          .putAll(serialIds));
    }

    /**
     * Removes multiple paoIds from the cache.  Relatively expensive, since it has to iterate all serial mappings.
     * @param paoIds
     * @param rfnIdentifiers
     */
    void remove(Set<Integer> paoIds) {
        cache.values().forEach(serialLookup -> serialLookup.removeAll(paoIds));
    }
}