package com.cannontech.rest.api.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.cannontech.rest.api.common.model.MockPaoType;

public class ApiUtils {

    public static String buildFriendlyName(MockPaoType paoType, String removePrefix, String addSuffix) {
        return StringUtils.remove(WordUtils.capitalizeFully(StringUtils.removeStartIgnoreCase(paoType.name(), removePrefix), '_'), "_") + addSuffix;
    }

}
