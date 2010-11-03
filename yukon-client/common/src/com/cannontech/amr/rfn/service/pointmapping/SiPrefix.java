package com.cannontech.amr.rfn.service.pointmapping;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Based on http://en.wikipedia.org/wiki/International_System_of_Units
 */
public enum SiPrefix {
    yocto(-24),
    zepto(-21),
    atto(-18),
    femto(-15),
    pico(-12),
    nano(-9),
    micro(-6),
    milli(-3),
    centi(-2),
    deci(-1),
    deca(1),
    hecto(2),
    kilo(3, "Kilo"),
    mega(6, "Mega"),
    giga(9, "Giga"),
    tera(12, "Tera"),
    peta(15, "Peta"),
    exa(18, "Exa"),
    zetta(21, "Zetta"),
    yotta(24, "Yotta"),
    ;

    private SiPrefix(int exponent, String... otherSpellings) {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        builder.add(name());
        builder.addAll(Arrays.asList(otherSpellings));
        this.spellings = builder.build();
        this.exponent = exponent;
        this.factor = Math.pow(10, exponent);
    }
    private Set<String> spellings;
    private int exponent;
    private double factor;
    private final static Map<String, SiPrefix> allSpellings;
    
    static {
        ImmutableMap.Builder<String, SiPrefix> builder = ImmutableMap.builder();
        for (SiPrefix prefix : values()) {
            for (String spelling : prefix.getSpellings()) {
                builder.put(spelling, prefix);
            }
        }
        allSpellings = builder.build();
    }
    
    public Set<String> getSpellings() {
        return spellings;
    }
    
    public int getExponent() {
        return exponent;
    }
    
    public double getFactor() {
        return factor;
    }
    
    public static Map<String, SiPrefix> getAllspellings() {
        return allSpellings;
    }
}