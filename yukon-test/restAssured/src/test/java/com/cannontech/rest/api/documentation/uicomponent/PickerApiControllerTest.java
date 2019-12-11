package com.cannontech.rest.api.documentation.uicomponent;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import java.lang.reflect.Method;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.dr.helper.PointPickerHelper;
import com.cannontech.rest.api.uicomponent.request.MockPickerIdSearchCriteria;
import com.cannontech.rest.api.uicomponent.request.MockPickerSearchCriteria;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class PickerApiControllerTest {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private RequestSpecification documentationSpec;
    private FieldDescriptor[] searchFieldDescriptor = null;
    private FieldDescriptor[] idSearchFieldDescriptor = null;
    private MockPickerSearchCriteria pickerSearchCriteria = null;
    private MockPickerIdSearchCriteria pickerIdSearchCriteria = null;

    @BeforeMethod
    public void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
        searchFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("type").type(JsonFieldType.STRING).description("Type of Picker"),
                fieldWithPath("queryString").type(JsonFieldType.STRING).description("Query String"),
                fieldWithPath("startCount").type(JsonFieldType.NUMBER).description("Start Count : Initial Value"),
                fieldWithPath("count").type(JsonFieldType.NUMBER).description("Number of Records"),
                fieldWithPath("extraArgs").type(JsonFieldType.STRING).description("Extra Arguments").optional()

        };

        idSearchFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("type").type(JsonFieldType.STRING).description("Type of Picker"),
                fieldWithPath("initialIds").type(JsonFieldType.VARIES).description("Initial Id Array"),
                fieldWithPath("extraArgs").type(JsonFieldType.STRING).description("Extra Arguments").optional()

        };

        pickerSearchCriteria = (MockPickerSearchCriteria) PointPickerHelper.buildSearchCriteria();
        pickerIdSearchCriteria = (MockPickerIdSearchCriteria) PointPickerHelper.buildIdSearchCriteria();
    }

    @AfterMethod
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    @Test()
    public void Test_Picker_Search_byType() {
        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}", responseFields(PointPickerHelper.pickerOnLoadResponseFields())))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .when()
                .get(ApiCallHelper.getProperty("searchPickerByType") + "devicePointPicker")
                .then()
                .extract()
                .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test()
    public void Test_Picker_Search_byQuery() {
        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}", requestFields(searchFieldDescriptor),
                        responseFields(PointPickerHelper.pickerResponseDataFields())))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(pickerSearchCriteria)
                .when()
                .post(ApiCallHelper.getProperty("searchPickerByQuery"))
                .then()
                .extract()
                .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);

    }

    @Test()
    public void Test_Picker_Search_byId() {
        Response response = given(documentationSpec)
                .filter(document("{ClassName}/{methodName}", requestFields(idSearchFieldDescriptor),
                        responseFields(PointPickerHelper.pickerResponseDataFields())))
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken)
                .body(pickerIdSearchCriteria)
                .when()
                .post(ApiCallHelper.getProperty("searchPickerById"))
                .then()
                .extract()
                .response();

        assertTrue("Status code should be 200", response.statusCode() == 200);

    }

}
