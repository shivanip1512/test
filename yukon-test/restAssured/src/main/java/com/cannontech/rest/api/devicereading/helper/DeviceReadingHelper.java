package com.cannontech.rest.api.devicereading.helper;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import com.cannontech.rest.api.deviceReadings.request.MockBuiltInAttribute;
import com.cannontech.rest.api.deviceReadings.request.MockDeviceReadingsRequest;
import com.cannontech.rest.api.deviceReadings.request.MockDeviceReadingsSelector;
import com.cannontech.rest.api.deviceReadings.request.MockIdentifier;
import com.cannontech.rest.api.deviceReadings.request.MockIdentifierType;

public class DeviceReadingHelper {

    public static MockDeviceReadingsRequest buildDeviceReadingRequest(MockIdentifierType identifierType, String value) {
        List<MockDeviceReadingsSelector> deviceReadingsSelectors = new ArrayList<>();
        MockDeviceReadingsSelector readingsSelector = DeviceReadingHelper.buildDeviceReadingSelector(identifierType, value);
        deviceReadingsSelectors.add(readingsSelector);
        return MockDeviceReadingsRequest.builder()
                                        .deviceReadingsSelectors(deviceReadingsSelectors)
                                        .build();
    }

    public static MockDeviceReadingsSelector buildDeviceReadingSelector(MockIdentifierType identifierType, String value) {
        List<MockBuiltInAttribute> builtInAttribute = new ArrayList<MockBuiltInAttribute>();
        builtInAttribute.add(MockBuiltInAttribute.RFN_BLINK_COUNT);
        return MockDeviceReadingsSelector.builder()
                                         .identifier(buildIdentifier(identifierType, value))
                                         .attributes(builtInAttribute)
                                         .build();
    }

    private static MockIdentifier buildIdentifier(MockIdentifierType identifierType, String value) {
        return MockIdentifier.builder().identifierType(identifierType).value(value).build();
    }

    public static FieldDescriptor[] buildResponseDescriptorForGet() {
        return new FieldDescriptor[] { fieldWithPath("DeviceReadings[].deviceReadingResponse[].identifier.value").type(JsonFieldType.STRING).description("Identifier Value"),
                fieldWithPath("DeviceReadings[].deviceReadingResponse[].identifier.identifierType").type(JsonFieldType.STRING).description("Identifier Type"),
                fieldWithPath("DeviceReadings[].deviceReadingResponse[].attribute").type(JsonFieldType.STRING).optional().description("Attribute"),
                fieldWithPath("DeviceReadings[].deviceReadingResponse[].pointId").type(JsonFieldType.NUMBER).optional().description("Point Id"),
                fieldWithPath("DeviceReadings[].deviceReadingResponse[].type").type(JsonFieldType.NUMBER).optional().description("Point Type"),
                fieldWithPath("DeviceReadings[].deviceReadingResponse[].pointQuality").type(JsonFieldType.STRING).description("Point Quality"),
                fieldWithPath("DeviceReadings[].deviceReadingResponse[].value").type(JsonFieldType.NUMBER).description("Point Value"),
                fieldWithPath("DeviceReadings[].deviceReadingResponse[].time").type(JsonFieldType.STRING).optional().description("Point Data Time Stamp"),
         };
    }
    
    public static FieldDescriptor[] buildRequestDescriptorForGet() {
        return new FieldDescriptor[] { fieldWithPath("deviceReadingsSelectors[].identifier.value").type(JsonFieldType.STRING).description("Identifier Value"),
                fieldWithPath("deviceReadingsSelectors[].identifier.identifierType").type(JsonFieldType.STRING).description("Identifier Type"),
                fieldWithPath("deviceReadingsSelectors[].attributes").type(JsonFieldType.ARRAY).optional().description("Attributes"),
         };
    }
}
