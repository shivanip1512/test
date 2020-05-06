package com.cannontech.rest.api.documentation.devicereading;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.deviceReadings.request.MockIdentifierType;
import com.cannontech.rest.api.devicereading.helper.DeviceReadingHelper;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class DeviceReadingsApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private List<FieldDescriptor> deviceReadingsFieldDescriptor = null;
    private List<FieldDescriptor> requestDescriptor=null;
    private final static String meterNumber = ApiCallHelper.getProperty("meterNumber");

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        restDocumentation.beforeTest(getClass(), method.getName());
        documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
        deviceReadingsFieldDescriptor = Arrays.asList(DeviceReadingHelper.buildResponseDescriptorForGet());
        requestDescriptor = Arrays.asList(DeviceReadingHelper.buildRequestDescriptorForGet());
    }
    
    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }
    
    /**
     * Test case is to get Device reading for device and to
     * generate Rest api documentation for get request.
     */
    @Test
    public void Test_DeviceReading_Get() {
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",requestFields(requestDescriptor), responseFields(deviceReadingsFieldDescriptor)))
                                                    .accept("application/json")
                                                    .contentType("application/json")
                                                    .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                                                    .when()
                                                    .body(DeviceReadingHelper.buildDeviceReadingRequest(MockIdentifierType.METERNUMBER, meterNumber))
                                                    .get(ApiCallHelper.getProperty("getLatestReading") + "getLatestReading")
                                                    .then()
                                                    .extract()
                                                    .response();
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }
}
