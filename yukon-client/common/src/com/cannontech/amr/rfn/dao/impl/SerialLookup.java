package com.cannontech.amr.rfn.dao.impl;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.stream.StreamUtils;
import com.google.common.collect.Maps;

/**
 * A mapping of RFN serial number to paoId.
 * Separates serials into numeric and non-numeric entries and stores them separately.
 * Most meters have numeric serials, others (such as water meters) have non-numeric prefixes.
 * 
 */
class SerialLookup {
    static final Logger log = YukonLogManager.getLogger(SerialLookup.class);

    //  TODO - Eventually replace with Trove, http://trove4j.sourceforge.net/html/overview.html 
    private Map<Long, Integer> numericSerials = Maps.newHashMap();
    private PatriciaTrie<Integer> stringSerials = new PatriciaTrie<>();
    
    public void put(String serial, Integer paoId) {
        if (NumberUtils.isDigits(serial)) {
            try {
                numericSerials.put(Long.parseUnsignedLong(serial), paoId);
                return;
            } catch (@SuppressWarnings("unused") NumberFormatException ex) {
                log.debug("RFN serial is all digits, but cannot be parsed as a Long:" + serial);
            }
        }
        stringSerials.put(serial, paoId);
    }
    
    public void putAll(Map<String, Integer> addresses) {
        addresses.forEach(this::put);
    }
    
    public Integer get(String serial) {
        if (NumberUtils.isDigits(serial)) {
            try {
                return numericSerials.get(Long.parseUnsignedLong(serial));
            } catch (@SuppressWarnings("unused") NumberFormatException ex) {
                log.debug("RFN serial is all digits, but cannot be parsed as a Long:" + serial);
            }
        }
        return stringSerials.get(serial);
    }

    public Stream<Integer> getAll(Iterable<String> serials) {
        return StreamUtils.stream(serials)
                .map(this::get)
                .filter(Objects::nonNull);
    }
    
    /**
     * Removes invalidated paoIds from the numeric and string caches.
     */
    public void removeAll(Set<Integer> invalidatedPaoIds) {
        numericSerials.values().removeAll(invalidatedPaoIds);
        stringSerials.values().removeAll(invalidatedPaoIds);
    }
}