package com.cannontech.dr.ecobee.service;

/**
    Status codes that are returned in Ecobee API responses.
    These descriptions were retrieved from https://www.ecobee.com/home/developer/api/documentation/v1/auth/auth-req-resp.shtml
    on 04/28/2014.
    
    0   Success                     
        Your request was successfully received and processed.
    1   Authentication failed.      
        Invalid credentials supplied to the registration request, or invalid token. Request registration again.
    2   Not authorized.             
        Attempted to access resources which user is not authorized for. Ensure the thermostat identifiers requested are 
        correct.
    3   Processing error.           
        General catch-all error for a number of internal errors. Additional info may be provided in the message. Check 
        your request. Contact support if persists.
    4   Serialization error.        
        An internal error mapping data to or from the API transmission format. Contact support.
    5   Invalid request format.     
        An error mapping the request data to internal data objects. Ensure that the properties being sent match 
        properties in the specification.
    6   Too many thermostat in selection match criteria.    
        Too many identifiers are specified in the Selecton.selectionMatch property. Current limit is 25 per request.
    7   Validation error.           
        The update request contained values out of range or too large for the field being updated. See the additional 
        message information as to what caused the validation failure.
    8   Invalid function.   
        The "type" property of the function does not match an available function. Check your request parameters.
    9   Invalid selection.  
        The Selection.selectionType property contains an invalid value.
    10  Invalid page.   
        The page requested in the request is invalid. Occurs if the page is less than 1 or more than the number of 
        available pages for the request.
    11  Function error. 
        An error occurred processing a function. Ensure required properties are provided.
    12  Post not supported for request. 
        The request URL does not support POST.
    13  Get not supported for request.  
        The request URL does not support GET.
    14  Authentication token has expired. 
        Refresh your tokens.  See Token Refresh
    15  Duplicate data violation.   
        Fix the data which is duplicated and re-post.
 */
public enum EcobeeStatusCode {
    SUCCESS(0),
    AUTHENTICATION_FAILED(1),
    NOT_AUTHORIZED(2),
    PROCESSING_ERROR(3),
    SERIALIZATION_ERROR(4),
    INVALID_REQUEST_FORMAT(5),
    TOO_MANY_THERMOSTATS(6),
    VALIDATION_ERROR(7),
    INVALID_FUNCTION(8),
    INVALID_SELECTION(9),
    INVALID_PAGE(10),
    FUNCTION_ERROR(11),
    POST_NOT_SUPPORTED(12),
    GET_NOT_SUPPORTED(13),
    AUTHENTICATION_EXPIRED(14),
    DUPLICATE_DATA_VIOLATION(15),
    ;
    
    private int code;
    
    private EcobeeStatusCode(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
