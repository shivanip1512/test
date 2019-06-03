package com.cannontech.web.api.errorHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.api.errorHandler.model.ApiError;
import com.cannontech.web.api.errorHandler.model.ApiFieldError;
import com.cannontech.web.api.errorHandler.model.ApiGlobalError;
import com.cannontech.web.api.token.AuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = YukonLogManager.getLogger(ApiExceptionHandler.class);
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    // 401
    @ExceptionHandler({ AuthenticationException.class})
    public ResponseEntity<Object> handleAuthenticationException(final Exception ex, final WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        final ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), "Authentication Required", uniqueKey);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }
    
 // 401
    @ExceptionHandler({NotAuthorizedException.class})
    public ResponseEntity<Object> handleNotAuthorizedException(final Exception ex, final WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        final ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), "User Not Authorized", uniqueKey);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<Object> handleNotFoundException(final Exception ex, final WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Not Found", uniqueKey);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DataIntegrityViolationException.class, SQLException.class})
    protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex,
            WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        log.error("Database error" + ex.getMessage());
        if (ex.getCause() instanceof ConstraintViolationException) {

            return new ResponseEntity<Object>(
                new ApiError(HttpStatus.CONFLICT.value(), "Database error", uniqueKey), new HttpHeaders(),
                HttpStatus.CONFLICT);
        }
        return new ResponseEntity<Object>(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Database error", uniqueKey),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
            WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        ApiError apiError = new ApiError(BAD_REQUEST.value(),
            String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(),
                ex.getValue(), ex.getRequiredType().getSimpleName()), uniqueKey);
        return new ResponseEntity<Object>(apiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        final String message = ex.getRequestPartName() + " part is missing";
        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), message, uniqueKey);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        String error = ex.getParameterName() + " parameter is missing";

        return new ResponseEntity<Object>(new ApiError(BAD_REQUEST.ordinal(), error, uniqueKey), new HttpHeaders(),
            HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media type is ");
        builder.append("application/json").append(", ");

        return new ResponseEntity<Object>(new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
            builder.substring(0, builder.length() - 2), uniqueKey), new HttpHeaders(),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * This Method handles MethodArgumentNotValidException, converts it into
     * custom error objects and send it a as UNPROCESSABLE_ENTITY error.
     * This also converts error messages to i18 messages.
     * 
     * This is required to properly format validation errors in API calls.
     *
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, 
            HttpStatus status,
            WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

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

        ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation error", apiFieldErrors, apiGlobalErrors, uniqueKey);

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        String error = "Malformed JSON request";
        return new ResponseEntity<Object>(new ApiError(HttpStatus.BAD_REQUEST.value(), error, uniqueKey),
            new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        String error = "Error writing JSON output";

        return new ResponseEntity<Object>(
            new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), error, uniqueKey), new HttpHeaders(),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        if (ex.getCause() instanceof ResponseStatusException) {
            final ApiError apiError =
                new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), uniqueKey);
            return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        final ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Unexpected exception - cause unknown", uniqueKey);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     *  Log API processing error with root cause.
     */
    private static void logApiException(final WebRequest request, Throwable t, String uniqueKey) {
        Throwable rc = CtiUtilities.getRootCause(t);
        String url = ((ServletWebRequest) request).getRequest().getRequestURL().toString();
        log.error(" API processing error with {" + uniqueKey + "}: " + url, t);
        log.error("Root cause was: ", rc);

    }

    /**
     * Build json response for no Handler Found in application.
     */
    public static void noHandlerFoundException(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String uniqueKey = CtiUtilities.getYKUniqueKey();
        String url = ServletUtil.getFullURL(request);
        log.error(uniqueKey + " No mapping for " + request.getMethod() + " " + url);
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(),
            String.format("Could not find the %s method for URL %s", request.getMethod(), url), uniqueKey);
        parseToJson(response, apiError, HttpStatus.NOT_FOUND);

    }

    /**
     * Handle Method Not Supported exception for API calls
     */
    
    public static void handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(new ServletWebRequest(request), ex, uniqueKey);
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

        final ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED.value(), builder.toString(), uniqueKey);

        parseToJson(response, apiError, HttpStatus.METHOD_NOT_ALLOWED);

    }

    public static void parseToJson(HttpServletResponse response, ApiError apiError, HttpStatus httpStatus)
            throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        ObjectMapper mapper = new ObjectMapper();
        try (PrintWriter out = response.getWriter()) {
            out.print(mapper.writeValueAsString(apiError));
            out.flush();
        }
    }

}
