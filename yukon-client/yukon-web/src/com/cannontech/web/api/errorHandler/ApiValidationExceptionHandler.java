package com.cannontech.web.api.errorHandler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.errorHandler.model.ApiErrorsView;
import com.cannontech.web.api.errorHandler.model.ApiFieldError;
import com.cannontech.web.api.errorHandler.model.ApiGlobalError;

/**
 * This interceptor handles MethodArgumentNotValidException, converts it into 
 * custom error objects and send it a as UNPROCESSABLE_ENTITY error.
 * This also converts error messages to i18 messages.
 * 
 * This is required to properly format validation errors in API calls.
 *
 */
@ControllerAdvice
public class ApiValidationExceptionHandler extends ResponseEntityExceptionHandler {
    
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, 
            HttpStatus status,
            WebRequest request) {
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        BindingResult bindingResult = ex
                .getBindingResult();

        List<ApiFieldError> apiFieldErrors = bindingResult
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ApiFieldError(
                        fieldError.getField(),
                        messageSourceAccessor.getMessage(fieldError.getCode(), fieldError.getArguments()),
                        fieldError.getRejectedValue())
                )
                .collect(Collectors.toList());

        List<ApiGlobalError> apiGlobalErrors = bindingResult
                .getGlobalErrors()
                .stream()
                .map(globalError -> new ApiGlobalError(
                        globalError.getCode())
                )
                .collect(Collectors.toList());

        ApiErrorsView apiErrorsView = new ApiErrorsView(apiFieldErrors, apiGlobalErrors);

        return new ResponseEntity<>(apiErrorsView, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
