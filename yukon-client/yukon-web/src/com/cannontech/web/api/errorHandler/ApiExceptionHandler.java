package com.cannontech.web.api.errorHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.cannontech.api.error.model.ApiErrorCategory;
import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.api.exception.NotSupportedException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.DataDependencyException;
import com.cannontech.common.exception.LMObjectDeletionFailureException;
import com.cannontech.common.exception.LoadProgramProcessingException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.exception.TypeNotSupportedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.HoneywellProcessingException;
import com.cannontech.core.dao.MacroLoadGroupProcessingException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.spring.filtering.exceptions.InvalidFilteringParametersException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.api.error.model.ApiErrorModel;
import com.cannontech.web.api.error.model.ApiFieldErrorModel;
import com.cannontech.web.api.errorHandler.model.ApiError;
import com.cannontech.web.api.token.AuthenticationException;
import com.cannontech.web.spring.parameters.exceptions.InvalidPagingParametersException;
import com.cannontech.web.spring.parameters.exceptions.InvalidSortingParametersException;
import com.cannontech.web.tools.points.service.PointEditorService.AttachedException;
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

        ApiErrorModel apiErrorModel = buildGlobalErrorResponse(ApiErrorDetails.AUTHENTICATION_INVALID, request, uniqueKey);
        return new ResponseEntity<Object>(apiErrorModel, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }
    
    // 403
    @ExceptionHandler({NotAuthorizedException.class})
    public ResponseEntity<Object> handleNotAuthorizedException(final Exception ex, final WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);
        ApiErrorModel apiErrorModel = buildGlobalErrorResponse(ApiErrorDetails.NOT_AUTHORIZED, request, uniqueKey);
        return new ResponseEntity<Object>(apiErrorModel, new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({AttachedException.class})
    public ResponseEntity<Object> handleBadRequestException(final AttachedException ex, final WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        String reason = messageSourceAccessor.getMessage(ex.getStatus());
        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), reason, uniqueKey);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ DataIntegrityViolationException.class, SQLException.class, PersistenceException.class })
    protected ResponseEntity<Object> handleDataIntegrityViolation(Exception ex, WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        log.error("Database error" + ex.getMessage());
        if (ex.getCause() instanceof ConstraintViolationException) {
            ApiErrorModel apiErrorModel = buildGlobalErrorResponse(ApiErrorDetails.OBJECT_ALREADY_EXISTS, request, uniqueKey);
            return new ResponseEntity<Object>(apiErrorModel, new HttpHeaders(),
                    HttpStatus.CONFLICT);
        }
        ApiErrorModel apiErrorModel = buildGlobalErrorResponse(ApiErrorDetails.DATABASE_ERROR, request, uniqueKey);
        return new ResponseEntity<Object>(apiErrorModel, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
            WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        String detailMessage = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(),
                ex.getValue(), ex.getRequiredType().getSimpleName());
        ApiErrorModel apiErrorModel = buildGlobalErrorResponse(ApiErrorDetails.METHOD_ARGUMENT_MISMATCH, request, uniqueKey);
        apiErrorModel.setDetail(detailMessage);
        return new ResponseEntity<Object>(apiErrorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ NotFoundException.class,
                        LoadProgramProcessingException.class,
                        MacroLoadGroupProcessingException.class,
                        HoneywellProcessingException.class,
                        LMObjectDeletionFailureException.class,
                        TypeNotSupportedException.class,
                        DynamicDataAccessException.class,
                        IllegalUseOfAttribute.class,
                        InvalidFilteringParametersException.class,
                        InvalidSortingParametersException.class,
                        InvalidPagingParametersException.class,
                        DuplicateException.class,
                        NotSupportedException.class})
    public ResponseEntity<Object> handleBadRequestException(final Exception ex, final WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        ApiErrorModel apiErrorModel = buildGlobalErrorResponse(ApiErrorDetails.BAD_REQUEST, request, uniqueKey);
        apiErrorModel.setDetail(ex.getMessage());
        return new ResponseEntity<Object>(apiErrorModel, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler({ DataDependencyException.class })
    public ResponseEntity<Object> handleDataDependencyException(final Exception ex, final WebRequest request) {
    
        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);
        
        DataDependencyException dde = (DataDependencyException) ex;
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        final String dependencyKey = "yukon.web.api.error.dataDependencyException";
        
        List<String> dependencies = new ArrayList<>();
        dde.getDependencies().forEach((k,v) -> dependencies.add(messageSourceAccessor.getMessage(dependencyKey + ".type." + k, StringUtils.join(v, ", "))));
        String errorMsg = messageSourceAccessor.getMessage(dependencyKey, dde.getDependentObject(), StringUtils.join(dependencies, "; "));

        ApiErrorModel apiErrorModel = buildGlobalErrorResponse(ApiErrorDetails.BAD_REQUEST, request, uniqueKey);
        apiErrorModel.setDetail(errorMsg);
        return new ResponseEntity<Object>(apiErrorModel, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        final String message = ex.getRequestPartName() + " part is missing";
        ApiErrorModel apiErrorModel = buildGlobalErrorResponse(ApiErrorDetails.BAD_REQUEST, request, uniqueKey);
        apiErrorModel.setDetail(message);
        return new ResponseEntity<Object>(apiErrorModel, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        String error = ex.getParameterName() + " parameter is missing";
        ApiErrorModel apiErrorModel = buildGlobalErrorResponse(ApiErrorDetails.BAD_REQUEST, request, uniqueKey);
        apiErrorModel.setDetail(error);
        return new ResponseEntity<Object>(apiErrorModel, new HttpHeaders(), HttpStatus.BAD_REQUEST);
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
        
        ApiErrorModel apiErrorModel = buildGlobalErrorResponse(ApiErrorDetails.BAD_REQUEST, request, uniqueKey);
        apiErrorModel.setDetail( builder.substring(0, builder.length() - 2));

        return new ResponseEntity<Object>(apiErrorModel, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
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
        log.info("Validation Error {" + uniqueKey + "}: ");

        BindingResult bindingResult = ex.getBindingResult();
        
        ApiErrorModel apiErrorModel= buildValidationErrorResponse(bindingResult, request, uniqueKey);

        return new ResponseEntity<>(apiErrorModel, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        String errorMessage = "Malformed JSON request";
        if (ex.getRootCause() instanceof TypeNotSupportedException) {
            errorMessage = ex.getRootCause().getMessage();
        }

        ApiErrorModel apiErrorModel = buildGlobalErrorResponse(ApiErrorDetails.BAD_REQUEST, request, uniqueKey);
        apiErrorModel.setDetail(errorMessage);
        return new ResponseEntity<Object>(apiErrorModel, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        String error = "Error writing JSON output";
        
        ApiErrorModel apiErrorModel = buildGlobalErrorResponse(ApiErrorDetails.BAD_REQUEST, request, uniqueKey);
        apiErrorModel.setDetail(error);

        return new ResponseEntity<Object>(apiErrorModel, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {

        String uniqueKey = CtiUtilities.getYKUniqueKey();
        logApiException(request, ex, uniqueKey);

        ApiErrorModel apiErrorModel = buildGlobalErrorResponse(ApiErrorDetails.BAD_REQUEST, request, uniqueKey);
        apiErrorModel.setDetail("Unexpected exception - cause unknown");
        return new ResponseEntity<Object>(apiErrorModel, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
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

    /**
     * Build and return ApiErrors for Global Error Response for the specified exception.
     */
    private ApiErrorModel buildGlobalErrorResponse(ApiErrorDetails errorDetails , WebRequest request, String uniqueKey) {
        return buildGlobalErrors(errorDetails, uniqueKey, request);
    }

    /**
     * Build and return ApiErrors for Global Error Response with field errors.
     */
    private ApiErrorModel buildValidationErrorResponse(BindingResult bindingResult, WebRequest request, String uniqueKey) {
        List<ApiFieldErrorModel> errors = new ArrayList<ApiFieldErrorModel>();
        if(CollectionUtils.isNotEmpty(bindingResult.getGlobalErrors())) {
            // TODO: Discuss this : Return Global Errors when response contains both field errors and global errors. This may
            // happen where we are using reject() and rejectValue() in validator classes.
            ApiErrorDetails errorDetails = ApiErrorDetails.getError(bindingResult.getGlobalErrors().get(0).getCode());
            return buildGlobalErrors(errorDetails, uniqueKey, request);
        }
        bindingResult.getFieldErrors().stream().forEach(
                fieldError -> {
                    ApiFieldErrorModel error = new ApiFieldErrorModel();
                    ApiErrorDetails childError = ApiErrorDetails.getError(fieldError.getCode());
                    error.setTitle(childError.getTitle());
                    error.setType(childError.getType());
                    error.setCode(childError.getCode());
                    error.setDetail(childError.getDefaultMessage());
                    error.setField(fieldError.getField());
                    error.setParameters(fieldError.getArguments());
                    error.setRejectedValue(String.valueOf(fieldError.getRejectedValue()));
                    errors.add(error);
                });
        ApiErrorDetails childError = ApiErrorDetails.getError(bindingResult.getFieldErrors().get(0).getCode());
        ApiErrorModel apiErrors = buildGlobalErrors(childError, uniqueKey, request);
        apiErrors.setErrors(errors);
        return apiErrors;
    }

    private ApiErrorModel buildGlobalErrors(ApiErrorDetails errorDetails, String uniqueKey, WebRequest request) {
        ApiErrorModel apiErrors = new ApiErrorModel();
        if (errorDetails.getCategory() == ApiErrorCategory.NONE) {
            apiErrors.setCode(errorDetails.getCode());
            apiErrors.setDetail(errorDetails.getDefaultMessage());
            apiErrors.setLogRef(uniqueKey);
            apiErrors.setRequestUri(ServletUtil.getFullURL(((ServletWebRequest) request).getRequest()));
            apiErrors.setTitle(errorDetails.getTitle());
            apiErrors.setType(errorDetails.getType());
        } else {
            ApiErrorCategory category = errorDetails.getCategory();
            apiErrors.setCode(category.getCode());
            apiErrors.setDetail(category.getDefaultMessage());
            apiErrors.setLogRef(uniqueKey);
            apiErrors.setRequestUri(ServletUtil.getFullURL(((ServletWebRequest) request).getRequest()));
            apiErrors.setTitle(category.getTitle());
            apiErrors.setType(category.getType());
        }
        return apiErrors;
    }
}
