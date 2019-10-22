package com.cannontech.rest.api.utilities;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import java.lang.reflect.Method;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;

import io.restassured.builder.RequestSpecBuilder;
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

}
