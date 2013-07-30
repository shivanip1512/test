package com.cannontech.amr.device;

import java.util.Set;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

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
    private final static Set<Attribute> allAttributes;

    static {
        Builder<Attribute> builder = ImmutableSet.builder();
        for (ProfileAttributeChannel profileAttributeChannel : values()) {
            builder.add(profileAttributeChannel.attribute);
        }
        allAttributes = builder.build();
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
        return allAttributes;
    }
}
