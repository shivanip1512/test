package com.cannontech.rest.api.dr.setup;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class LoadGroupSetupApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private String paoId = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = 
                new RequestSpecBuilder()
                        .addFilter(documentationConfiguration(this.restDocumentation)
                            .operationPreprocessors()
                            .withRequestDefaults(removeHeaders("Authorization"), 
                                                 removeHeaders("Accept"),
                                                 removeHeaders("Host"),
                                                 removeHeaders("Content-Type"),
                                                 removeHeaders("Content-Length"),
                                                 prettyPrint())
                            .withResponseDefaults(removeHeaders("X-Frame-Options"),
                                                  removeHeaders("X-Content-Type-Options"),
                                                  removeHeaders("Content-Security-Policy"),
                                                  removeHeaders("Strict-Transport-Security"),
                                                  removeHeaders("X-XSS-Protection"),
                                                  removeHeaders("Content-Length"),
                                                  removeHeaders("Content-Type"),
                                                  removeHeaders("Date"),
                                                  prettyPrint())
                            .and()
                            .snippets()
                            .withDefaults(HttpDocumentation.httpRequest(),
                                            HttpDocumentation.httpResponse()))
                        .build();
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    @Test
    public void Test_LmGroupMeterDisconnect_Create() {
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}", requestFields(
                                    fieldWithPath("LM_GROUP_METER_DISCONNECT.name").type(JsonFieldType.STRING).description("Load Group Name"), 
                                    fieldWithPath("LM_GROUP_METER_DISCONNECT.type").type(JsonFieldType.STRING).description("Load Group Type"),
                                    fieldWithPath("LM_GROUP_METER_DISCONNECT.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
                                    fieldWithPath("LM_GROUP_METER_DISCONNECT.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
                                    fieldWithPath("LM_GROUP_METER_DISCONNECT.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control")),
                                    responseFields(fieldWithPath("groupId").type(JsonFieldType.NUMBER).description("Load Group Id"))))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .body(ApiCallHelper.getInputFile("loadgroup\\lmGroupMeterDisconnectCreate.json"))
                                .when()
                                .post(ApiCallHelper.getProperty("saveloadgroup"))
                                .then()
                                .extract()
                                .response();


        paoId = response.path("groupId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

   @Test
    public void Test_LmGroupMeterDisconnect_Get() {

        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}",
                    responseFields(fieldWithPath("LM_GROUP_METER_DISCONNECT.id").type(JsonFieldType.NUMBER).description("Load Group Id"),
                        fieldWithPath("LM_GROUP_METER_DISCONNECT.name").type(JsonFieldType.STRING).description("Load Group Name"),
                        fieldWithPath("LM_GROUP_METER_DISCONNECT.type").type(JsonFieldType.STRING).description("Load Group Type"),
                        fieldWithPath("LM_GROUP_METER_DISCONNECT.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
                        fieldWithPath("LM_GROUP_METER_DISCONNECT.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
                        fieldWithPath("LM_GROUP_METER_DISCONNECT.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control"))))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                .when()
                .get(ApiCallHelper.getProperty("getloadgroup") + paoId)
                .then()
                .extract()
                .response();
        assertTrue("Status code should be 200", response.statusCode() == 200);

        JsonPath jsonPath = response.jsonPath();
        HashMap loadGroupData = jsonPath.get("LM_GROUP_METER_DISCONNECT");
        String name = (String) loadGroupData.get("name");
        assertTrue("Name Should be : Meter_disconnect_get", "Test Meter Disconnet123".equals(name));
        String type = (String) loadGroupData.get("type");
        assertTrue("Type Should be : LM_GROUP_METER_DISCONNECT", "LM_GROUP_METER_DISCONNECT".equals(type));
        float kWCapacity = (float) loadGroupData.get("kWCapacity");
        assertTrue("kWCapacity Should be : 123", 163 == kWCapacity);
        boolean disableGroup = (boolean) loadGroupData.get("disableGroup");
        assertTrue("Group Should be disabled : ", !disableGroup);
        boolean disableControl = (boolean) loadGroupData.get("disableControl");
        assertTrue("Control Should be disabled : ", !disableControl);
    }

}
