package com.cannontech.rest.api.utilities;

import java.util.List;

import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockApiFieldError;

import io.restassured.response.ExtractableResponse;

public class ValidationHelper {

    /**
     * This function validates expectedFieldMessage in Error Response and returns boolean 
     */
    public static boolean validateFieldError(ExtractableResponse<?> response, String errorField, String expectedFieldMessage) {
        MockApiError mockApiError = response.as(MockApiError.class);
        List<MockApiFieldError> mockApiFieldError = mockApiError.getFieldErrors();
        for (MockApiFieldError err : mockApiFieldError) {
            if (err.getCode().contains(expectedFieldMessage) && err.getField().equals(errorField)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This function validates expectedErrorMessage in Error Response and returns boolean
     */
    public static boolean validateErrorMessage(ExtractableResponse<?> response, String expectedErrorMessage) {
        MockApiError mockApiError = response.as(MockApiError.class);
        String mockApiErrorMessage = mockApiError.getMessage();

        if (mockApiErrorMessage.contains(expectedErrorMessage)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This function validates expectedErrorCode in Error Response and returns boolean
     */
    public static boolean validateErrorCode(ExtractableResponse<?> response, String expectedErrorCode) {
        MockApiError mockApiError = response.as(MockApiError.class);
        String errorCode = mockApiError.getErrorCode();
        if (errorCode.contains(expectedErrorCode)) {
            return true;
        } else {
            return false;
        }
    }
}
