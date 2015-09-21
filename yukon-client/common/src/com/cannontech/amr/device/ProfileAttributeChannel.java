package com.cannontech.amr.device;

import java.util.Set;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Sets;


public enum ProfileAttributeChannel {
    LOAD_PROFILE(BuiltInAttribute.LOAD_PROFILE) {
        @Override
        public int getRate(DeviceLoadProfile deviceLoadProfile) {
            return deviceLoadProfile.getLoadProfileDemandRate();
        }
    },
    PROFILE_CHANNEL_2(BuiltInAttribute.PROFILE_CHANNEL_2) {
        @Override
        public int getRate(DeviceLoadProfile deviceLoadProfile) {
            return deviceLoadProfile.getLoadProfileDemandRate();
        }
    },
    PROFILE_CHANNEL_3(BuiltInAttribute.PROFILE_CHANNEL_3) {
        @Override
        public int getRate(DeviceLoadProfile deviceLoadProfile) {
            return deviceLoadProfile.getLoadProfileDemandRate();
        }
    },
    VOLTAGE_PROFILE(BuiltInAttribute.VOLTAGE_PROFILE) {
        @Override
        public int getRate(DeviceLoadProfile deviceLoadProfile) {
            return deviceLoadProfile.getVoltageDmdRate();
        }
    };

    private final BuiltInAttribute attribute;
    private final static Set<Attribute> loadProfileAttributes;
    private final static Set<Attribute> voltageProfileAttributes;
    
    static {
        Builder<Attribute> loadProfilebuilder = ImmutableSet.builder();
        loadProfilebuilder.add(LOAD_PROFILE.attribute);
        loadProfilebuilder.add(PROFILE_CHANNEL_2.attribute);
        loadProfilebuilder.add(PROFILE_CHANNEL_3.attribute);

        loadProfileAttributes = loadProfilebuilder.build();
    }

    static {
        Builder<Attribute> voltageProfileBuilder = ImmutableSet.builder();
        voltageProfileBuilder.add(VOLTAGE_PROFILE.attribute);

        voltageProfileAttributes = voltageProfileBuilder.build();
    }  
    
    private ProfileAttributeChannel(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public int getChannel() {
        return ordinal() + 1;
    }

    public abstract int getRate(DeviceLoadProfile deviceLoadProfile);

    /**
     * Looks up the ProfileAttributeChannelEnum based on its channel.
     */
    public static ProfileAttributeChannel getForChannel(int channel) {
        return values()[channel - 1];
    }
    
    public final static Set<Attribute> getAttributes() {
        return Sets.union(loadProfileAttributes, voltageProfileAttributes);
    }
    
    public final static Set<Attribute> getLoadProfileAttributes() {
        return loadProfileAttributes;
    }
    
    public final static Set<Attribute> getVoltageProfileAttributes() {
        return voltageProfileAttributes;
    }

}
