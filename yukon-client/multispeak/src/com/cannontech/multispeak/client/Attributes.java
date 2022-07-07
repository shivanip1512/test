package com.cannontech.multispeak.client;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;

public enum Attributes implements DisplayableEnum {

    TOU("TOU"),
    PEAKDEMAND_USAGE("Peak Demand , Usage"),
    POWERFACTOR("Power Factor"),
    KVAR_KVARH("Kvar , KVarH"),
    RECEIVEDKWH("Received kWh");

    private String dbString;

    private final static ImmutableMap<String, Attributes> lookupByDBStringMap;

    static {
        ImmutableMap.Builder<String, Attributes> attributeBuilder = ImmutableMap.builder();
        for (Attributes attr : values()) {
            attributeBuilder.put(attr.getDbString(), attr);
        }
        lookupByDBStringMap = attributeBuilder.build();
    }

    public static List<Attributes> getAttributesFromDBString(String dbString) {
        checkArgument(dbString != null);
        List<Attributes> attributes = new ArrayList<>();

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

    Attributes(String dbString) {
        this.dbString = dbString;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.multispeakAttributesFieldEnum.." + name();
    }

}
