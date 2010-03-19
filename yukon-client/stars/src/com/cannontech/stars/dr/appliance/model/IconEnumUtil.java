package com.cannontech.stars.dr.appliance.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class IconEnumUtil {

    public static <T extends IconEnum> ImmutableMap<String, T> buildByFilenameMap(
            T[] values) {
        Builder<String, T> byFilenameBuilder = ImmutableMap.builder();

        for (T icon : values) {
            if (icon.getFilename() != null) {
                byFilenameBuilder.put(icon.getFilename(), icon);
            }
        }
        return byFilenameBuilder.build();
    }
}
