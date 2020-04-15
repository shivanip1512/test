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
import java.util.Arrays;
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

public class TcpPortSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String portId = null;
    private MockPortBase tcpPort = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);

        tcpPort = CommChannelHelper.buildCommChannel(MockPaoType.TCPPORT);
    }

    private FieldDescriptor[] buildTcpPortDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("type").type(JsonFieldType.STRING).description("Channel Type"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("Comm Channel Name"),
                fieldWithPath("enable").type(JsonFieldType.BOOLEAN).description("Enable Channel"),
                fieldWithPath("baudRate").type(JsonFieldType.STRING).description("Baud Rate " +
                                                                    "Possible values for Baud Rate are : "+
                                                                    " BAUD_300," +
                                                                    " BAUD_1200" +
                                                                    " BAUD_2400," +
                                                                    " BAUD_4800," +
                                                                    " BAUD_9600," +
                                                                    " BAUD_14400," +
                                                                    " BAUD_28800," +
                                                                    " BAUD_38400," +
                                                                    " BAUD_57600," +
                                                                    " BAUD_115200"),
                fieldWithPath("timing.preTxWait").type(JsonFieldType.NUMBER).description("Pre Tx Wait").optional(),
                fieldWithPath("timing.rtsToTxWait").type(JsonFieldType.NUMBER).description("RTS To Tx Wait").optional(),
                fieldWithPath("timing.postTxWait").type(JsonFieldType.NUMBER).description("Post Tx Wait").optional(),
                fieldWithPath("timing.receiveDataWait").type(JsonFieldType.NUMBER).description("Receive Data Wait").optional(),
                fieldWithPath("timing.extraTimeOut").type(JsonFieldType.NUMBER).description("Additional Time Out").optional(),
        };
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    @Test
    public void Test_TcpPort_01_Create() {
        List<FieldDescriptor> tcpPortDescriptor = Arrays.asList(buildTcpPortDescriptor());
        List<FieldDescriptor> list = new ArrayList<>(tcpPortDescriptor);
        list.add(0, fieldWithPath("id").type(JsonFieldType.NUMBER).description("Port Id"));
        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}", requestFields(tcpPortDescriptor), responseFields(list)))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(tcpPort)
                .when()
                .post(ApiCallHelper.getProperty("createPort"))
                .then()
                .extract()
                .response();
        portId = response.path(CommChannelHelper.CONTEXT_PORT_ID).toString();
        assertTrue("Port Id should not be Null", portId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_TcpPort_01_Create" })
    public void Test_TcpPort_02_Update() {
        
        List<FieldDescriptor> tcpPortDescriptor = Arrays.asList(buildTcpPortDescriptor());
        List<FieldDescriptor> list = new ArrayList<>(tcpPortDescriptor);
        list.add(0, fieldWithPath("id").type(JsonFieldType.NUMBER).description("Port Id"));
        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}", requestFields(tcpPortDescriptor), responseFields(list)))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(tcpPort)
                .when()
                .post(ApiCallHelper.getProperty("updatePort") + portId)
                .then()
                .extract()
                .response();
        portId = response.path(CommChannelHelper.CONTEXT_PORT_ID).toString();
        assertTrue("Port Id should not be Null", portId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test(dependsOnMethods = { "Test_TcpPort_01_Create" })
    public void Test_TcpPort_03_Get() {
        List<FieldDescriptor> tcpPortDescriptor = Arrays.asList(buildTcpPortDescriptor());
        List<FieldDescriptor> list = new ArrayList<>(tcpPortDescriptor);
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

    @Test(dependsOnMethods = { "Test_TcpPort_01_Create" })
    public void Test_TcpPort_04_Delete() {
        MockPortDelete tcpPortDeleteObject = MockPortDelete.builder()
                .name(CommChannelHelper.getTcpPortName(MockPaoType.TCPPORT))
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
