package com.cannontech.common.dr.setup;

import static com.google.common.base.Preconditions.checkArgument;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum Relays implements DisplayableEnum {
    RELAY_1('1'),
    RELAY_2('2'),
    RELAY_3('3'),
    RELAY_4('4');

    private final static ImmutableSet<Relays> relays;
    private final static ImmutableMap<Character, Relays> lookupByRelay;
    static {
        relays = ImmutableSet.of(
            RELAY_1, 
            RELAY_2, 
            RELAY_3, 
            RELAY_4);

        ImmutableMap.Builder<Character, Relays> builder = ImmutableMap.builder();
        for(Relays relays : values()) {
            builder.put(relays.getRelayNumber(), relays);
        }
        lookupByRelay = builder.build();
    }

    public static ImmutableSet<Relays> getRelays() {
        return relays;
    }

    Relays(Character relayNumber) {
        this.relayNumber = relayNumber;
    }

    private Character relayNumber;

    public Character getRelayNumber() {
        return relayNumber;
    }

    public static Relays getDisplayValue(Character relayNumber) throws IllegalArgumentException {
        Relays relays = lookupByRelay.get(relayNumber);
        checkArgument(relays != null, relayNumber);
        return relays;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.loadGroup." + name();
    }
}
