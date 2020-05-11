package com.cannontech.rest.api.documentation.devicereading;

import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.deviceReadings.request.MockIdentifierType;
import com.cannontech.rest.api.devicereading.helper.DeviceReadingHelper;
import com.cannontech.rest.api.documentation.DocumentationBase;
import com.cannontech.rest.api.documentation.DocumentationFields;
import com.cannontech.rest.api.documentation.DocumentationFields.Copy;
import com.cannontech.rest.api.documentation.DocumentationFields.Create;
import com.cannontech.rest.api.documentation.DocumentationFields.Delete;
import com.cannontech.rest.api.documentation.DocumentationFields.Get;
import com.cannontech.rest.api.documentation.DocumentationFields.Update;

public class DeviceReadingsApiControllerTest extends DocumentationBase {

    /**
     * Test case is to get Device reading for device and to
     * generate Rest api documentation for get request.
     */
    @Test
    public void Test_DeviceReading_Get() {
        getDoc();
    }

    @Override
    protected Create buildCreateFields() {
        return null;
    }

    @Override
    protected Update buildUpdateFields() {
        return null;
    }

    @Override
    protected Get buildGetFields() {
        List<FieldDescriptor> requestFields = Arrays.asList(DeviceReadingHelper.buildRequestDescriptorForGet());
        List<FieldDescriptor> responseFields = Arrays.asList(DeviceReadingHelper.buildResponseDescriptorForGet());
        String meterNumber = ApiCallHelper.getProperty("meterNumber");
        String url = ApiCallHelper.getProperty("getLatestReading") + "getLatestReading";
        return new DocumentationFields.GetWithBody(responseFields, requestFields,
                                            DeviceReadingHelper.buildDeviceReadingRequest(MockIdentifierType.METERNUMBER, meterNumber),
                                            url);
    }

    @Override
    protected Copy buildCopyFields() {
        return null;
    }

    @Override
    protected Delete buildDeleteFields() {
        return null;
    }
}
