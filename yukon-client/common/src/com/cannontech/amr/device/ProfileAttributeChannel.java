package com.cannontech.amr.device;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.database.db.device.DeviceLoadProfile;

public enum ProfileAttributeChannel {
    LOAD_PROFILE(BuiltInAttribute.LOAD_PROFILE) {
        public int getRate(DeviceLoadProfile deviceLoadProfile) {
            return deviceLoadProfile.getLoadProfileDemandRate();
        }
    },
    PROFILE_CHANNEL_2(BuiltInAttribute.PROFILE_CHANNEL_2) {
        public int getRate(DeviceLoadProfile deviceLoadProfile) {
            return deviceLoadProfile.getLoadProfileDemandRate();
        }
    },
    PROFILE_CHANNEL_3(BuiltInAttribute.PROFILE_CHANNEL_3) {
        public int getRate(DeviceLoadProfile deviceLoadProfile) {
            return deviceLoadProfile.getLoadProfileDemandRate();
        }
    },
    VOLTAGE_PROFILE(BuiltInAttribute.VOLTAGE_PROFILE) {
        public int getRate(DeviceLoadProfile deviceLoadProfile) {
            return deviceLoadProfile.getVoltageDmdRate();
        }
    };

    private final BuiltInAttribute attribute;

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
}
