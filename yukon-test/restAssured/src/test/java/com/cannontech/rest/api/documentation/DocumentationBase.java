package com.cannontech.rest.api.documentation;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
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
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.documentation.DocumentationFields.*;
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
     * @return value in response having identifier of responseFieldPath 
     */
    protected String createDoc() {
        Create fields = buildCreateFields();
        validateFields("createDoc", fields);
        return post(fields.requestFields, fields.responseFields, fields.responseFieldPath, fields.responseFieldDesc, fields.body, fields.url, null);
    }
    
    /**
     * Make a POST call for request and response fields to generate restDocumentation.
     * Request contains object (as defined by body object), response contains updated object.
     * @return value in response having identifier of responseFieldPath
     */
    protected String updateDoc() {
        Update fields = buildUpdateFields();
        validateFields("updateDoc", fields);
        return post(fields.requestFields, fields.responseFields, fields.responseFieldPath, fields.responseFieldDesc, fields.body, fields.url, fields.id);
    }
    
    /**
     * Make a POST call for request and response fields to generate restDocumentation.
     * Request contains object (as defined by body object), response contains newly created object (as copied from object with copyId). 
     * @return value in response having identifier of responseFieldPath 
     */
    protected String copyDoc() {
        Copy fields = buildCopyFields();
        validateFields("copyDoc", fields);
        return post(fields.requestFields, fields.responseFields, fields.responseFieldPath, fields.responseFieldDesc, fields.body, fields.url, fields.id);
    }

    /**
     * Helper method to make a POST call having request and response fields with a body to generate restDocumentation.
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
     */
    public void getDoc() {
        Response response;
        Get fields = buildGetFields();
        validateFields("getDoc", fields);
        
        if (fields instanceof GetWithBody) {
            GetWithBody getFields = (GetWithBody)fields;
            response = getHeader(getFields.requestFields, getFields.responseFields)
                    .when()
                    .body(getFields.body)
                    .get(ApiCallHelper.getProperty(fields.url) + fields.id)
                    .then().extract().response();
        } else {
            response = getHeader(null, fields.responseFields)
                .when()
                .get(ApiCallHelper.getProperty(fields.url) + fields.id)
                .then().extract().response();
        }
        validateStatusCode(response);
    }
    
    /**
     * Make a GET call for responseFields
     * Request has no object, response contains object of getId.
     * @param responseFields
     * @param url
     * TODO - not sure what to do with this yet...
     */
    protected void getAllDoc(List<FieldDescriptor> responseFields, String url) {
        Response response = getHeader(null, responseFields)
                .when()
                .get(ApiCallHelper.getProperty(url))
                .then().extract().response();
        validateStatusCode(response);
    }
    
    /**
     * Make a basic DELETE call
     */
    protected void deleteDoc() {
        Response response;
        Delete fields = buildDeleteFields();
        validateFields("deleteDoc", fields);
        
        if (fields instanceof DeleteWithBody) { // we have an object to use
            DeleteWithBody deleteFields = (DeleteWithBody)fields;
            response = getHeader(deleteFields.requestFields, deleteFields.responseFields)
                    .body(deleteFields.body)
                    .when()
                    .delete(ApiCallHelper.getProperty(fields.url) + fields.id)
                    .then().extract().response();
        } else {   // just have an id to use
            response = getHeader(null, null)
                    .when()
                    .delete(ApiCallHelper.getProperty(fields.url) + fields.id)
                    .then().extract().response();
        }
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
    
    /**
     * Helper method to assert DocumentationFields are set for respective method
     * @param methodName
     * @param fields
     */
    private void validateFields(String methodName, Object fields) {
        assertNotNull("Fields not set for use with " + methodName, fields);
    }
    
    protected abstract MockPaoType getMockPaoType();
    
    /**
     * Return the DocumentationFields for use with getDoc
     */
    protected abstract DocumentationFields.Get buildGetFields();

    /**
     * Return the DocumentationFields for use with createDoc
     */
    protected abstract DocumentationFields.Create buildCreateFields();

    /**
     * Return the DocumentationFields for use with updateDoc
     */
    protected abstract DocumentationFields.Update buildUpdateFields();

    /**
     * Return the DocumentationFields for use with copyDoc
     */
    protected abstract DocumentationFields.Copy buildCopyFields();
    
    /**
     * Return the DocumentationFields for use with deleteDoc 
     */
    protected abstract DocumentationFields.Delete buildDeleteFields();

}