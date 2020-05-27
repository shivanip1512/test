package com.cannontech.rest.api.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.common.model.MockPointType;

public class ApiUtils {

    public static String buildFriendlyName(MockPaoType paoType, String removePrefix, String addSuffix) {
        return StringUtils.remove(WordUtils.capitalizeFully(StringUtils.removeStartIgnoreCase(paoType.name(), removePrefix), '_'), "_") + addSuffix;
    }

    public static String buildFriendlyNameForPoint(MockPointType pointType, String removePrefix, String addSuffix) {
        return StringUtils.remove(WordUtils.capitalizeFully(StringUtils.removeStartIgnoreCase(pointType.name(), removePrefix), '_'), "_") + addSuffix;
    }
}
