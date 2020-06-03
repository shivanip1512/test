package com.cannontech.rest.api.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

public class ApiUtils {

    public static String buildFriendlyName(Enum<?> enumType, String removePrefix, String addSuffix) {
        return StringUtils
                .remove(WordUtils.capitalizeFully(StringUtils.removeStartIgnoreCase(enumType.name(), removePrefix), '_'), "_")
                + addSuffix;
    }
}
