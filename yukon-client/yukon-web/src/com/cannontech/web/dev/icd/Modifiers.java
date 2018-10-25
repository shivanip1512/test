package com.cannontech.web.dev.icd;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.cannontech.common.stream.StreamUtils;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Modifiers {
    PRIMARY              ("Primary"),
    SECONDARY            ("Secondary"),

    PHASE_A              ("Phase A"),
    PHASE_B              ("Phase B"),
    PHASE_C              ("Phase C"),

    QUADRANT_1           ("Quadrant 1"),
    QUADRANT_2           ("Quadrant 2"),
    QUADRANT_3           ("Quadrant 3"),
    QUADRANT_4           ("Quadrant 4"),

    FUNDAMENTAL          ("Fundamental"),
    HARMONIC             ("Harmonic"),

    LEADING              ("Leading"),
    LAGGING              ("Lagging"),

    NET_FLOW             ("Net Flow"),
    MINIMUM              ("Min"),
    AVERAGE              ("Avg"),
    MAXIMUM              ("Max"),
    PREVIOUS             ("Previous"),
    DAILY_MAXIMUM        ("Daily Max"),
    CUMULATIVE           ("Cumulative"),
    CONTINUOUS_CUMULATIVE("Continuous Cumulative"),

    A_TO_B               ("A to B"),
    B_TO_C               ("B to C"),
    C_TO_A               ("C to A"),
    NEUTRAL_TO_GROUND    ("Phase Neutral->Ground"),
    A_TO_NEUTRAL         ("A to Neutral"),
    B_TO_NEUTRAL         ("B to Neutral"),
    C_TO_NEUTRAL         ("C to Neutral"),

    TOU_RATE_A           ("TOU Rate A"),
    TOU_RATE_B           ("TOU Rate B"),
    TOU_RATE_C           ("TOU Rate C"),
    TOU_RATE_D           ("TOU Rate D"),
    TOU_RATE_E           ("TOU Rate E"),
    TOU_RATE_F           ("TOU Rate F"),
    TOU_RATE_G           ("TOU Rate G"),

    GIGA                 ("Giga",   Type.SI_PREFIX),
    MEGA                 ("Mega",   Type.SI_PREFIX),
    KILO                 ("Kilo",   Type.SI_PREFIX),
    MILLI                ("milli",  Type.SI_PREFIX),
    MICRO                ("Micro",  Type.SI_PREFIX),
    DECI                 ("Tenths", Type.SI_PREFIX),
    OVERFLOW             ("Overflow"),

    COINCIDENT_VALUE_1   ("Coincident Value 1", Type.COINCIDENT),
    COINCIDENT_VALUE_2   ("Coincident Value 2", Type.COINCIDENT),
    COINCIDENT_VALUE_3   ("Coincident Value 3", Type.COINCIDENT),
    COINCIDENT_VALUE_4   ("Coincident Value 4", Type.COINCIDENT),
    COINCIDENT_VALUE_5   ("Coincident Value 5", Type.COINCIDENT),
    COINCIDENT_VALUE_6   ("Coincident Value 6", Type.COINCIDENT),
    COINCIDENT_VALUE_7   ("Coincident Value 7", Type.COINCIDENT);
    
    enum Type {
        PLAIN,
        COINCIDENT,
        SI_PREFIX
    };
    
    private static final Map<String, Modifiers> nameLookup;
    
    static {
        nameLookup = Stream.of(values())
            .collect(StreamUtils.mapToSelf(e -> e.commonName));
    }
    
    String commonName;
    Type modifierType = Type.PLAIN;
    
    Modifiers(String commonName) {
        this.commonName = commonName;
    }
    
    Modifiers(String commonName, Type modifierType) {
        this(commonName);
        this.modifierType = modifierType;
    }
    
    public String getCommonName() {
        return commonName;
    }

    public boolean isCoincident() {
        return modifierType == Type.COINCIDENT;
    }

    public boolean isSiPrefix() {
        return modifierType == Type.SI_PREFIX;
    }

    @JsonCreator
    public static Modifiers getByCommonName(String commonName) {
        return Optional.ofNullable(nameLookup.get(commonName))
                .orElseThrow(() ->
                    new RuntimeException("Unknown modifier name " + commonName));
    }
}