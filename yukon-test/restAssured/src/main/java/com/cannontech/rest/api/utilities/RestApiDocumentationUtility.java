package com.cannontech.rest.api.utilities;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;

import com.cannontech.rest.api.common.ApiCallHelper;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestApiDocumentationUtility {

    public static RequestSpecification buildRequestSpecBuilder(ManualRestDocumentation restDocumentation, Method method) {

        return new RequestSpecBuilder()
                    .addFilter(documentationConfiguration(restDocumentation)
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

    /**
     * Helper method to build json header for request fields and response fields.
     * @param requestFields if null, will not be included in filter
     * @param responseFields if null, will not be included in filter
     * @return
     */
    public static RequestSpecification getHeader(RequestSpecification documentationSpec, List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields) {
        assertNotNull("RequestSpecification documentationSpec cannot be null.", documentationSpec);

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
     * Helper method to make a POST call having request and response fields with a body to generate restDocumentation.
     * @return value in response having identifier of responseFieldPath
     */
    public static String post(RequestSpecification header,
            String responseFieldPath, String responseFieldDesc, Object body, String url) {
        assertNotNull("RequestSpecification header cannot be null.", header);
        assertNotNull("Url cannot be null.", url);
        
        RequestSpecification spec = header;
        if (body != null) {
            spec.body(body);
        }
        Response response = spec
                .when()
                    .post(url)
                .then()
                    .extract()
                    .response();
        String idStr = validateNotNull(response, responseFieldPath, responseFieldDesc);
        validateStatusCode(response);
        return idStr;
    }

    /**
     * Helper method to make a PUT call having request and response fields with a body to generate restDocumentation.
     * @return value in response having identifier of responseFieldPath
     */
    public static String put(RequestSpecification header,
            String responseFieldPath, String responseFieldDesc, Object body, String url) {
        assertNotNull("RequestSpecification header cannot be null.", header);
        assertNotNull("Url cannot be null.", url);

        RequestSpecification spec = header;
        if (body != null) {
            spec.body(body);
        }
        Response response = spec
                .when()
                .put(url)
                .then()
                .extract()
                .response();
        String idStr = validateNotNull(response, responseFieldPath, responseFieldDesc);
        validateStatusCode(response);
        return idStr;
    }

    public static void delete(RequestSpecification header, Object body, String url) {
        assertNotNull("RequestSpecification header cannot be null.", header);
        assertNotNull("Url cannot be null.", url);
        
        RequestSpecification spec = header;
        if (body != null) {
            spec.body(body);
        }
        Response response = spec
            .when()
                .delete(url)
            .then()
                .extract()
                .response();
        validateStatusCode(response);
    }
    
    public static void get(RequestSpecification header, Object body, String url) {
        assertNotNull("RequestSpecification header cannot be null.", header);
        assertNotNull("Url cannot be null.", url);
        
        RequestSpecification spec = header;
        if (body != null) {
            spec.body(body);
        }
        Response response = spec
            .when()
                .get(url)
            .then()
                .extract()
                .response();
        validateStatusCode(response);
    }
    
    /**
     * Helper method to assert Successful response status.
     * @param response
     */
    public static void validateStatusCode(Response response) {
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }
    
    /**
     * Helper method to assert response is not null and return found value.
     * @param response
     * @param responseFieldPath path of value to return
     * @param responseFieldDesc description of value to return
     * @return value in response having responseFieldPath 
     */
    public static String validateNotNull(Response response, String responseFieldPath, String responseFieldDesc) {
        String idStr = response.path(responseFieldPath).toString();
        assertTrue(responseFieldDesc + " should not be Null", idStr != null);
        return idStr;
    }
}
