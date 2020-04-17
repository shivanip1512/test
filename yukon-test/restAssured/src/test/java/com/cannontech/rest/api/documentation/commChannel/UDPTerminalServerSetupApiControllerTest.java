package com.cannontech.rest.api.documentation.commChannel;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.commChannel.helper.CommChannelHelper;
import com.cannontech.rest.api.commChannel.request.MockPaoType;
import com.cannontech.rest.api.commChannel.request.MockPortBase;
import com.cannontech.rest.api.commChannel.request.MockPortDelete;
import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class UDPTerminalServerSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    MockPortBase udpTerminalServerPort = null;
    private String portId = null;
    public final static String CONTEXT_PORT_ID = "id";

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
        udpTerminalServerPort = CommChannelHelper.buildCommChannel(MockPaoType.UDPPORT);
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    @Test
    public void Test_UDPTerminalServer_01_Create() {
        List<FieldDescriptor> terminalServerPortDescriptor = CommChannelHelper.buildUDPTerminalServerPortDescriptor();
        List<FieldDescriptor> list = new ArrayList<>(terminalServerPortDescriptor);
        list.add(0, fieldWithPath("id").type(JsonFieldType.NUMBER).description("Port Id"));
        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}", requestFields(terminalServerPortDescriptor),
                        responseFields(list)))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(udpTerminalServerPort)
                .when()
                .post(ApiCallHelper.getProperty("createPort"))
                .then()
                .extract()
                .response();

        portId = response.path(CommChannelHelper.CONTEXT_PORT_ID).toString();
        assertTrue("Port Id should not be Null", portId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_UDPTerminalServer_01_Create" })
    public void Test_UDPTerminalServer_02_Update() {

        List<FieldDescriptor> tcpTerminalServerDescriptor = CommChannelHelper.buildUDPTerminalServerPortDescriptor();
        List<FieldDescriptor> list = new ArrayList<>(tcpTerminalServerDescriptor);
        list.add(0, fieldWithPath("id").type(JsonFieldType.NUMBER).description("Port Id"));
        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}", requestFields(tcpTerminalServerDescriptor), responseFields(list)))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(udpTerminalServerPort)
                .when()
                .post(ApiCallHelper.getProperty("updatePort") + portId)
                .then()
                .extract()
                .response();
        portId = response.path(CommChannelHelper.CONTEXT_PORT_ID).toString();
        assertTrue("Port Id should not be Null", portId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_UDPTerminalServer_01_Create" })
    public void Test_UDPTerminalServer_03_Get() {
        List<FieldDescriptor> terminalServerPortDescriptor = CommChannelHelper.buildUDPTerminalServerPortDescriptor();
        List<FieldDescriptor> list = new ArrayList<>(terminalServerPortDescriptor);
        list.add(0, fieldWithPath("id").type(JsonFieldType.NUMBER).description("Port Id"));
        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}", responseFields(list)))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .when()
                .get(ApiCallHelper.getProperty("getPort") + portId)
                .then()
                .extract()
                .response();
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_UDPTerminalServer_01_Create" })
    public void Test_UDPTerminalServer_04_Delete() {
        MockPortDelete tcpPortDeleteObject = MockPortDelete.builder()
                .name(CommChannelHelper.getTcpPortName(MockPaoType.UDPPORT))
                .build();
        Response response = given(documentationSpec).filter(document("{ClassName}/{methodName}",
                requestFields(fieldWithPath("name").type(JsonFieldType.STRING).description("Comm Channel Name")),
                responseFields(fieldWithPath("portId").type(JsonFieldType.NUMBER).description("Port Id"))))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(tcpPortDeleteObject)
                .when()
                .delete(ApiCallHelper.getProperty("deletePort") + portId)
                .then()
                .extract()
                .response();
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }
}
