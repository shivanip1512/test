package com.cannontech.rest.api.deviceReading;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.deviceReadings.request.MockBuiltInAttribute;
import com.cannontech.rest.api.deviceReadings.request.MockDeviceReadingsRequest;
import com.cannontech.rest.api.deviceReadings.request.MockDeviceReadingsSelector;
import com.cannontech.rest.api.deviceReadings.request.MockIdentifierType;
import com.cannontech.rest.api.devicereading.helper.DeviceReadingHelper;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

@Test
public class DeviceReadingsApiTest {

    private final static String meterNumber = "789456";
    private final static String InvalidPaoId = "9632123";
    private final static String InvalidPaoName = "856326";
    private final static String validPaoName = "789456";
    private final static String validPaoId = "78945";
    private final static String validSensorSerialNumber= "456123";
    private final static String invalidSensorSerialNumber= "45556";

    @Test
    public void DeviceReadings_01_Get(ITestContext context) {

        Log.startTestCase("DeviceReadings_01_Get");

        MockDeviceReadingsRequest deviceReadingsRequest = DeviceReadingHelper
                .buildDeviceReadingRequest(MockIdentifierType.METERNUMBER, meterNumber);
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getLatestReading", "getLatestReading", deviceReadingsRequest);

        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");
        
        Log.endTestCase("DeviceReadings_01_Get");
    }

    @Test
    public void DeviceReadings_02_IdentifierValueAsBlankValidation(ITestContext context) {

        Log.startTestCase("DeviceReadings_02_IdentifierValueAsBlankValidation");

        MockDeviceReadingsRequest deviceReadingsRequest = DeviceReadingHelper
                .buildDeviceReadingRequest(MockIdentifierType.METERNUMBER, "");
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getLatestReading", "getLatestReading", deviceReadingsRequest);

        assertTrue(getResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(getResponse, "Validation error"),
                 "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(getResponse, "deviceReadingsSelectors[0].identifier.value",
                  "Identifier Value is required."), "Expected code in response is not correct");

        Log.endTestCase("DeviceReadings_02_IdentifierValueAsBlankValidation");
    }

    @Test
    public void DeviceReadings_03_InvalidMeterNumber(ITestContext context) {

        Log.startTestCase("DeviceReadings_03_InvalidMeterNumber");
        String meterNumber = "2156656";
        MockDeviceReadingsRequest deviceReadingsRequest = DeviceReadingHelper
                .buildDeviceReadingRequest(MockIdentifierType.METERNUMBER, meterNumber);
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getLatestReading", "getLatestReading", deviceReadingsRequest);
        assertTrue(getResponse.statusCode() == 500, "Status code should be 500");
        assertTrue(ValidationHelper.validateErrorMessage(getResponse, "Unknown meter number " + meterNumber),
                  "Expected error message Should contains Text: " + "Unknown meter number " + meterNumber);

        Log.endTestCase("DeviceReadings_03_InvalidMeterNumber");

    }

    @Test
    public void DeviceReadings_04_AtrributesRequired(ITestContext context) {

        Log.startTestCase("DeviceReadings_04_AtrributesRequired");
        MockDeviceReadingsSelector selector = new MockDeviceReadingsSelector();
        selector.setAttributes(new ArrayList<MockBuiltInAttribute>());
        List<MockDeviceReadingsSelector> selectors = new ArrayList<>();
        selectors.add(selector);
        MockDeviceReadingsRequest deviceReadingsRequest = DeviceReadingHelper
                .buildDeviceReadingRequest(MockIdentifierType.METERNUMBER, meterNumber);
        deviceReadingsRequest.setDeviceReadingsSelectors(selectors);
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getLatestReading", "getLatestReading", deviceReadingsRequest);
        assertTrue(getResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(getResponse, "Validation error"),
                "Expected error message Should contains Text: " + "Validation error");

        assertTrue(ValidationHelper.validateFieldError(getResponse, "deviceReadingsSelectors[0].attributes",
                   "At least one Attribute must be present."),"Expected code in response is not correct");
        
        Log.endTestCase("DeviceReadings_04_AtrributesRequired");
    }

    @Test
    public void DeviceReadings_05_IdentifierTypeAsBlankValidation(ITestContext context) {

        Log.startTestCase("DeviceReadings_05_IdentifierTypeAsBlankValidation");

        MockDeviceReadingsRequest deviceReadingsRequest = DeviceReadingHelper
                .buildDeviceReadingRequest(MockIdentifierType.METERNUMBER, meterNumber);
        deviceReadingsRequest.getDeviceReadingsSelectors().get(0).getIdentifier().setIdentifierType(null);
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getLatestReading", "getLatestReading", deviceReadingsRequest);

        assertTrue(getResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(getResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(getResponse, "deviceReadingsSelectors[0].identifier.identifierType",
                   "Identifier Type is required."), "Expected code in response is not correct");

        Log.endTestCase("DeviceReadings_05_IdentifierTypeAsBlankValidation");
    }

    @Test
    public void DeviceReadings_06_ValidPaoId(ITestContext context) {

        Log.startTestCase("DeviceReadings_06_ValidPaoId");

        MockDeviceReadingsRequest deviceReadingsRequest = DeviceReadingHelper
                .buildDeviceReadingRequest(MockIdentifierType.PAOID, validPaoId);
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getLatestReading", "getLatestReading", deviceReadingsRequest);

        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        Log.endTestCase("DeviceReadings_06_ValidPaoId");
    }

    @Test
    public void DeviceReadings_07_InvalidPaoId(ITestContext context) {

        Log.startTestCase("DeviceReadings_07_InvalidPaoId");
        MockDeviceReadingsRequest deviceReadingsRequest = DeviceReadingHelper.buildDeviceReadingRequest(MockIdentifierType.PAOID,
                InvalidPaoId);
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getLatestReading", "getLatestReading", deviceReadingsRequest);
        assertTrue(getResponse.statusCode() == 400, "Status code should be 400");
        assertTrue(ValidationHelper.validateErrorMessage(getResponse, "A PAObject with id "+ InvalidPaoId +" cannot be found."),
                "Expected error message Should contains Text: " + "A PAObject with id "+ InvalidPaoId +" cannot be found.");

        Log.endTestCase("DeviceReadings_07_InvalidPaoId");

    }

    @Test
    public void DeviceReadings_08_ValidPaoName(ITestContext context) {

        Log.startTestCase("DeviceReadings_08_ValidPaoName");

        MockDeviceReadingsRequest deviceReadingsRequest = DeviceReadingHelper.buildDeviceReadingRequest(MockIdentifierType.PAONAME, validPaoName);
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getLatestReading", "getLatestReading", deviceReadingsRequest);

        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        Log.endTestCase("DeviceReadings_08_ValidPaoName");
    }

    @Test
    public void DeviceReadings_09_InvalidPaoName(ITestContext context) {

        Log.startTestCase("DeviceReadings_09_InvalidPaoName");
        MockDeviceReadingsRequest deviceReadingsRequest = DeviceReadingHelper.buildDeviceReadingRequest( MockIdentifierType.PAONAME, InvalidPaoName);
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getLatestReading", "getLatestReading", deviceReadingsRequest);
        assertTrue(getResponse.statusCode() == 500, "Status code should be 500");
        assertTrue(ValidationHelper.validateErrorMessage(getResponse, "Pao Object not found for Pao name: " + InvalidPaoName),
                "Expected error message Should contains Text: " + "Pao Object not found for Pao name: " + InvalidPaoName);

        Log.endTestCase("DeviceReadings_09_InvalidPaoName");
    }
    
    @Test
    public void DeviceReadings_10_ValidSensorSerialNumber(ITestContext context) {

        Log.startTestCase("DeviceReadings_10_ValidSensorSerialNumber");

        MockDeviceReadingsRequest deviceReadingsRequest = DeviceReadingHelper.buildDeviceReadingRequest(MockIdentifierType.SERIALNUMBER, validSensorSerialNumber);
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getLatestReading", "getLatestReading", deviceReadingsRequest);

        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        Log.endTestCase("DeviceReadings_10_ValidSensorSerialNumber");
    }

    @Test
    public void DeviceReadings_11_InvalidSensorSerialNumber(ITestContext context) {

        Log.startTestCase("DeviceReadings_11_InvalidSensorSerialNumber");
        
        MockDeviceReadingsRequest deviceReadingsRequest = DeviceReadingHelper.buildDeviceReadingRequest( MockIdentifierType.SERIALNUMBER, invalidSensorSerialNumber);
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getLatestReading", "getLatestReading", deviceReadingsRequest);
        assertTrue(getResponse.statusCode() == 400, "Status code should be 400");
        assertTrue(ValidationHelper.validateErrorMessage(getResponse, "No device found for Serial Number: " + invalidSensorSerialNumber),
                "Expected error message Should contains Text: " + "No device found for Serial Number: " + invalidSensorSerialNumber);

        Log.endTestCase("DeviceReadings_11_InvalidSensorSerialNumber");
    }
}
