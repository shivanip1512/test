package com.cannontech.rest.api.documentation;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.utilities.RestApiDocumentationUtility;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public abstract class DocumentationBase {

    protected ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    protected RequestSpecification documentationSpec;

    @BeforeMethod
    protected void setUp(Method method) {
        baseURI = ApiCallHelper.getProperty("baseURI");
        this.restDocumentation.beforeTest(getClass(), method.getName());
        this.documentationSpec = RestApiDocumentationUtility.buildRequestSpecBuilder(restDocumentation, method);
    }

    @AfterMethod
    protected void tearDown() {
        this.restDocumentation.afterTest();
    }

    /**
     * Make a POST call for request and response fields to generate restDocumentation.
     * Request contains object (as defined by body object), response contains newly created object
     * @param requestFields 
     * @param responseFields
     * @param responseFieldPath path of value to return
     * @param responseFieldDesc description of value to return
     * @param body the object of the request
     * @param url
     * @return value in response having identifier of responseFieldPath 
     */
    protected String createDoc(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields, 
            String responseFieldPath, String responseFieldDesc, Object body, String url) {
        return post(requestFields, responseFields, responseFieldPath, responseFieldDesc, body, url, null);
    }
    
    /**
     * Make a POST call for request and response fields to generate restDocumentation.
     * Request contains object (as defined by body object), response contains updated object.
     * @param requestFields 
     * @param responseFields
     * @param responseFieldPath path of value to return
     * @param responseFieldDesc description of value to return
     * @param body the object of the request
     * @param url 
     * @return value in response having identifier of responseFieldPath 
     * @param updateId the object identifier to request as part of url (ie: url=url/updateId)
     * @return
     */
    protected String updateDoc(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields, 
            String responseFieldPath, String responseFieldDesc, Object body, String url, String updateId) {
        return post(requestFields, responseFields, responseFieldPath, responseFieldDesc, body, url, updateId);
    }
    
    /**
     * Make a POST call for request and response fields to generate restDocumentation.
     * Request contains object (as defined by body object), response contains newly created object (as copied from object with copyId). 
     * @param requestFields
     * @param responseFields
     * @param responseFieldPath path of value to return
     * @param responseFieldDesc description of value to return
     * @param body the object of the request
     * @param url
     * @param copyId the object identifier to request as part of url (ie: url=url/copyId)
     * @return value in response having identifier of responseFieldPath 
     */
    protected String copyDoc(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields, 
            String responseFieldPath, String responseFieldDesc, Object body, String url, String copyId) {
        return post(requestFields, responseFields, responseFieldPath, responseFieldDesc, body, url, copyId);
    }
    
    /**
     * Helper method to make a POST call having request and response fields with a body to generate restDocumentation.
     * @param requestFields
     * @param responseFields
     * @param responseFieldPath path of value to return
     * @param responseFieldDesc description of value to return
     * @param body the object of the request
     * @param url
     * @param id the object identifier to request as part of url (ie: url=url/id)
     * @return value in response having identifier of responseFieldPath
     */
    private String post(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields, 
            String responseFieldPath, String responseFieldDesc, Object body, String url, String id) {
        String urlWithId = ApiCallHelper.getProperty(url) + (StringUtils.defaultIfEmpty(id, ""));
        Response response = getHeader(requestFields, responseFields)
                .body(body)
                .when()
                .post(urlWithId)
                .then().extract().response();
        String idStr = validateNotNull(response, responseFieldPath, responseFieldDesc);
        validateStatusCode(response);
        return idStr;
    }

    /**
     * Make a GET call for responseFields of getId
     * Request has no object, id parameter only. Response contains object of getId. 
     * @param responseFields
     * @param url
     * @param getId the object identifier to request as part of url (ie: url=url/getId)
     */
    protected void getOneDoc(List<FieldDescriptor> responseFields, String url, String getId) {
        get(responseFields, url, getId);
    }

    /**
     * Make a GET call for responseFields
     * Request has no object, response contains object of getId.
     * @param responseFields
     * @param url
     */
    protected void getAllDoc(List<FieldDescriptor> responseFields, String url) {
        get(responseFields, url, null);
    }
    
    /**
     * Helper method to make a GET call for responseFields of id
     * @param responseFields
     * @param url
     * @param id the object identifier to request as part of url (ie: url=url/id)
     */
    private void get(List<FieldDescriptor> responseFields, String url, String id) {
        String urlWithId = ApiCallHelper.getProperty(url) + (StringUtils.defaultIfEmpty(id, ""));
        Response response = getHeader(null, responseFields)
                .when()
                .get(urlWithId)
                .then().extract().response();
        validateStatusCode(response);
    }
    
    /**
     * Make a basic DELETE call for deleteId
     * Request has no object, id parameter only.
     * @param url
     * @param deleteId
     */
    protected void deleteDoc(String url, String deleteId) {
        Response response = getHeader(null, null)
                .when()
                .delete(ApiCallHelper.getProperty(url) + deleteId)
                .then().extract().response();
        validateStatusCode(response);
    }

    /**
     * Make a DELETE call for deleteId having request fields and response fields
     * Request contains object (as defined by body object).
     * @param requestFields
     * @param responseFields
     * @param body the object of the request
     * @param url
     * @param deleteId
     */
    protected void deleteWithFieldsDoc(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields, Object body, String url, String deleteId) {
        Response response = getHeader(requestFields, responseFields)
                .body(body)
                .when()
                .delete(ApiCallHelper.getProperty(url) + deleteId)
                .then().extract().response();
        validateStatusCode(response);
    }

    /**
     * Helper method to build json header for request fields and response fields.
     * @param requestFields if null, will not be included in filter
     * @param responseFields if null, will not be included in filter
     * @return
     */
    private RequestSpecification getHeader(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields) {
        RestDocumentationFilter filter;
        String documentIdentifier = "{ClassName}/{methodName}";
        if (CollectionUtils.isNotEmpty(requestFields) && CollectionUtils.isNotEmpty(responseFields)) {
            filter = document(documentIdentifier, requestFields(requestFields), responseFields(responseFields));
        } else if (CollectionUtils.isNotEmpty(requestFields)) {
            filter = document(documentIdentifier, requestFields(responseFields));
        } else if (CollectionUtils.isNotEmpty(responseFields)) {
            filter = document(documentIdentifier, responseFields(responseFields));
        } else {
            filter = document(documentIdentifier);
        }

        return given(documentationSpec)
                .filter(filter)
                .accept("application/json")
                .contentType("application/json")
                .header("Authorization", "Bearer " + ApiCallHelper.authToken);
    }
    
    /**
     * Helper method to assert Successful response status.
     * @param response
     */
    private void validateStatusCode(Response response) {
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }
    
    /**
     * Helper method to assert response is not null and return found value.
     * @param response
     * @param responseFieldPath path of value to return
     * @param responseFieldDesc description of value to return
     * @return value in response having responseFieldPath 
     */
    private String validateNotNull(Response response, String responseFieldPath, String responseFieldDesc) {
        String idStr = response.path(responseFieldPath).toString();
        assertTrue(responseFieldDesc + " should not be Null", idStr != null);
        return idStr;
    }
}