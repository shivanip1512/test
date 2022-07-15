package com.cannontech.multispeak.client;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public enum MspAttribute implements DisplayableEnum {

    PEAKDEMAND_USAGE("Peak Demand , Usage", BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND),
    POWERFACTOR("Power Factor", BuiltInAttribute.POWER_FACTOR),
    KVAR_KVARH("Kvar , KVarH", BuiltInAttribute.KVAR, BuiltInAttribute.KVARH),
    RECEIVEDKWH("Received kWh", BuiltInAttribute.RECEIVED_KWH),
    TOU("TOU", BuiltInAttribute.DELIVERED_KWH_RATE_A, BuiltInAttribute.DELIVERED_KWH_RATE_B,
               BuiltInAttribute.DELIVERED_KWH_RATE_C, BuiltInAttribute.DELIVERED_KWH_RATE_D,
               BuiltInAttribute.RECEIVED_KWH_RATE_A, BuiltInAttribute.RECEIVED_KWH_RATE_B,
               BuiltInAttribute.RECEIVED_KWH_RATE_C, BuiltInAttribute.RECEIVED_KWH_RATE_D,
               BuiltInAttribute.PEAK_DEMAND_RATE_A, BuiltInAttribute.PEAK_DEMAND_RATE_B,
               BuiltInAttribute.PEAK_DEMAND_RATE_C, BuiltInAttribute.PEAK_DEMAND_RATE_D
           );

    private String dbString;
    private EnumSet<BuiltInAttribute> builtInAttributes;

    private final static ImmutableMap<String, MspAttribute> lookupByDBStringMap;
    private final static ImmutableMap<MspAttribute, EnumSet<BuiltInAttribute>> lookupByMspAttribute;

    static {
        ImmutableMap.Builder<String, MspAttribute> attributeBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<MspAttribute, EnumSet<BuiltInAttribute>> builtInAttributeBuilder = ImmutableMap.builder();
        for (MspAttribute attr : values()) {
            attributeBuilder.put(attr.getDbString(), attr);
            builtInAttributeBuilder.put(attr, attr.getBuiltInAttributes());
        }
        lookupByDBStringMap = attributeBuilder.build();
        lookupByMspAttribute = builtInAttributeBuilder.build();
    }

    public static List<MspAttribute> getAttributesFromDBString(String dbString) {
        checkArgument(dbString != null);
        List<MspAttribute> attributes = new ArrayList<>();

        if (StringUtils.isNotBlank(dbString)) {
            String attributeArray[] = dbString.split(":");

            for (int i = 0; i < attributeArray.length; i++) {
                String attribute = attributeArray[i].trim();
                attributes.add(lookupByDBStringMap.get(attribute));
            }
        }
        return attributes;
    }

    public String getDbString() {
        return dbString;
    }

    public EnumSet<BuiltInAttribute> getBuiltInAttributes() {
        return builtInAttributes;
    }

    public void setBuiltInAttributes(EnumSet<BuiltInAttribute> builtInAttributes) {
        this.builtInAttributes = builtInAttributes;
    }

    MspAttribute(String dbString, BuiltInAttribute... attributes) {
        this.dbString = dbString;
        this.builtInAttributes = Sets.newEnumSet(Arrays.asList(attributes), BuiltInAttribute.class);
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.multispeakAttributesFieldEnum.." + name();
    }

}
