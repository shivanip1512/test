package com.cannontech.rest.api.customAttribute.helper;

import com.cannontech.rest.api.customAttribute.request.MockCustomAttribute;

public class CustomAttributeHelper {

    public static MockCustomAttribute buildAttribute() {
        MockCustomAttribute customAttribute = MockCustomAttribute.builder()
                .name("Custom Attribute API Test")
                .build();
        return customAttribute;
    }
}
