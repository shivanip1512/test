package com.cannontech.rest.api.documentation;

import static io.restassured.RestAssured.baseURI;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.documentation.DocumentationFields.Copy;
import com.cannontech.rest.api.documentation.DocumentationFields.Create;
import com.cannontech.rest.api.documentation.DocumentationFields.Delete;
import com.cannontech.rest.api.documentation.DocumentationFields.DeleteWithBody;
import com.cannontech.rest.api.documentation.DocumentationFields.Get;
import com.cannontech.rest.api.documentation.DocumentationFields.GetWithBody;
import com.cannontech.rest.api.documentation.DocumentationFields.Update;
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
        return post(fields);
    }

    /**
     * Make a POST call for request and response fields to generate restDocumentation.
     * Request contains object (as defined by body object), response contains updated object.
     * @return value in response having identifier of responseFieldPath
     */
    @Deprecated
    protected String updateDoc() {
        Update fields = buildUpdateFields();
        validateFields("updateDoc", fields);
        return post(fields);
    }

    /**
     * Make a PUT call for request and response fields to generate restDocumentation.
     * Request contains object (as defined by body object), response contains updated object.
     * @return value in response having identifier of responseFieldPath
     */
    protected String updateAllDoc() {
        Update fields = buildUpdateFields();
        validateFields("updateDoc", fields);
        return put(fields);
    }

    /**
     * Make a PATCH call for request and response fields to generate restDocumentation.
     * Request contains object (as defined by body object), response contains updated object.
     * @return value in response having identifier of responseFieldPath
     */
    protected String updatePartialDoc() {
        Update fields = buildUpdateFields();
        validateFields("updateDoc", fields);
        return patch(fields);
    }

    /**
     * Make a POST call for request and response fields to generate restDocumentation.
     * Request contains object (as defined by body object), response contains newly created object (as copied from object with copyId). 
     * @return value in response having identifier of responseFieldPath 
     */
    protected String copyDoc() {
        Copy fields = buildCopyFields();
        validateFields("copyDoc", fields);
        return post(fields);
    }

    /**
     * Helper method to make a POST call having request and response fields with a body to generate restDocumentation.
     * @return value in response having identifier of responseFieldPath
     */
    private String post(DocumentationFields.Create fields) {
        RequestSpecification header = getHeader(fields.requestFields, fields.responseFields);
        return RestApiDocumentationUtility.post(header, fields.responseFieldPath, fields.responseFieldDesc, fields.body, fields.url);
    }

    /**
     * Helper method to make a PUT call having request and response fields with a body to generate restDocumentation.
     * @return value in response having identifier of responseFieldPath
     */
    private String put(DocumentationFields.Create fields) {
        RequestSpecification header = getHeader(fields.requestFields, fields.responseFields);
        return RestApiDocumentationUtility.put(header, fields.responseFieldPath, fields.responseFieldDesc, fields.body,
                fields.url);
    }
    

    /**
     * Helper method to make a PATCH call having request and response fields with a body to generate restDocumentation.
     * @return value in response having identifier of responseFieldPath
     */
    private String patch(DocumentationFields.Create fields) {
        RequestSpecification header = getHeader(fields.requestFields, fields.responseFields);
        return RestApiDocumentationUtility.patch(header, fields.responseFieldPath, fields.responseFieldDesc, fields.body,
                fields.url);
    }

    /**
     * Make a GET call for responseFields of getId
     */
    public void getDoc() {
        RequestSpecification header;
        Get fields = buildGetFields();
        validateFields("getDoc", fields);
        if (fields instanceof GetWithBody) {
            GetWithBody getFields = (GetWithBody)fields;
            header = getHeader(getFields.requestFields, getFields.responseFields);
            RestApiDocumentationUtility.get(header, getFields.body, getFields.url);
        } else {
            header = getHeader(null, fields.responseFields);
            RestApiDocumentationUtility.get(header, null, fields.url);
        }
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
                .get(url)
                .then().extract().response();
        RestApiDocumentationUtility.validateStatusCode(response);
    }
    
    /**
     * Make a basic DELETE call
     */
    protected void deleteDoc() {
        RequestSpecification header; 
        Delete fields = buildDeleteFields();
        validateFields("deleteDoc", fields);
        if (fields instanceof DeleteWithBody) { // we have an object to use
            DeleteWithBody deleteFields = (DeleteWithBody)fields;
            header = getHeader(deleteFields.requestFields, deleteFields.responseFields);
            RestApiDocumentationUtility.delete(header, deleteFields.body, deleteFields.url);
        } else {   // just have an id to use
            header = getHeader(null, null);
            RestApiDocumentationUtility.delete(header, null, fields.url);
        }
    }

    /**
     * Helper method to build json header for request fields and response fields.
     * @param requestFields if null, will not be included in filter
     * @param responseFields if null, will not be included in filter
     * @return
     */
    private RequestSpecification getHeader(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields) {
        return RestApiDocumentationUtility.getHeader(documentationSpec, requestFields, responseFields);
    }
    
    /**
     * Helper method to assert DocumentationFields are set for respective method
     * @param methodName
     * @param fields
     */
    private void validateFields(String methodName, Object fields) {
        assertNotNull("Fields not set for use with " + methodName, fields);
    }
    
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