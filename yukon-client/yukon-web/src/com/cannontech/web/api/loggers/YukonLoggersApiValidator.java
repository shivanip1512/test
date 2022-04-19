package com.cannontech.web.api.loggers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.clientutils.logger.service.YukonLoggerService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.log.model.LoggerType;
import com.cannontech.common.log.model.SystemLogger;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;

public class YukonLoggersApiValidator extends SimpleValidator<YukonLogger> {

    private final static String basekey = "yukon.web.modules.adminSetup.config.loggers.";

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private YukonLoggerService loggerService;
    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;

    private MessageSourceAccessor accessor;

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    public YukonLoggersApiValidator() {
        super(YukonLogger.class);
    }

    @Override
    protected void doValidation(YukonLogger logger, Errors errors) {
        if (logger != null) {
            // validate logger name
            String id = ServletUtils.getPathVariable("loggerId");

            List<YukonLogger> loggers = loggerService.getLoggers(null, null, null, null);
            if (id != null && !loggers.stream().filter(log -> log.getLoggerId() == Integer.valueOf(id)).findAny().isPresent()) {
                throw new NotFoundException("Logger Id not found");
            }
            // Update loggerName and type for update request.
            if (id != null) {
                YukonLogger existingLogger = loggers.stream()
                                                    .filter(log -> log.getLoggerId() == Integer.valueOf(id))
                                                    .findFirst()
                                                    .get();
                logger.setLoggerName(existingLogger.getLoggerName());
                logger.setLoggerType(existingLogger.getLoggerType());
            }
            Integer loggerId = id == null ? -1 : Integer.valueOf(id);
            validateLoggerName(errors, logger, accessor.getMessage(basekey + "loggerName"), loggerId, loggers);
            if (!errors.hasFieldErrors("loggerName")) {
                // validate logger type
                yukonApiValidationUtils.checkIfFieldRequired("loggerType", errors, logger.getLoggerType(),
                        accessor.getMessage(basekey + "loggerType"));
                if (!errors.hasFieldErrors("loggerType")) {
                    if (logger.getLoggerType() == LoggerType.SYSTEM_LOGGER) {
                        if (StringUtils.isEmpty(id)) {
                            // System logger creation not supported.
                            if (!SystemLogger.isSystemLogger(logger.getLoggerName())) {
                                errors.rejectValue("loggerType", ApiErrorDetails.NOT_SUPPORTED.getCodeString());
                            }
                        } else {
                            // System logger update check
                            if (!SystemLogger.isSystemLogger(logger.getLoggerName())) {
                                errors.rejectValue("loggerType", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                        new Object[] { LoggerType.USER_LOGGER }, "");
                            }
                        }

                    } else {
                        // User logger check
                        if (SystemLogger.isSystemLogger(logger.getLoggerName())) {
                            errors.rejectValue("loggerType", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                    new Object[] { LoggerType.SYSTEM_LOGGER }, "");
                        }
                    }
                }
            }
            // validate logger level
            yukonApiValidationUtils.checkIfFieldRequired("level", errors, logger.getLevel(),
                    accessor.getMessage(basekey + "loggerLevel"));
            // validate expiration date
            if (logger.getExpirationDate() != null) {
                validateExpirationDate(errors, logger, accessor.getMessage(basekey + "expirationDate"));
            }
            // validate notes
            if (logger.getNotes() != null) {
                yukonApiValidationUtils.checkExceedsMaxLength(errors, "notes", logger.getNotes(),
                        255);
            }
        }

    }

    public void validateLoggerName(Errors errors, YukonLogger logger, String i18Text, Integer loggerId, List<YukonLogger> loggers) {
        yukonApiValidationUtils.checkIfFieldRequired("loggerName", errors, logger.getLoggerName(), i18Text);
        if (!errors.hasFieldErrors("loggerName")) {
            yukonApiValidationUtils.checkExceedsMaxLength(errors, "loggerName", logger.getLoggerName(), 200);
        }
        if (!errors.hasFieldErrors("loggerName")) {
            yukonApiValidationUtils.checkWhitelistedCharacter(errors, "loggerName", logger.getLoggerName(), i18Text);
        }
        if (!errors.hasFieldErrors("loggerName")) {
            if (logger.getLoggerName().startsWith(".") || logger.getLoggerName().endsWith(".")) {
                errors.rejectValue("loggerName", ApiErrorDetails.WHITELIST_CHARACTERS.getCodeString(), new Object[] { i18Text }, "");
            }
        }
        if (!errors.hasFieldErrors("loggerName")) {

            if (loggerId != -1) {
                loggers.stream().filter(tempLogger -> tempLogger.getLoggerId() == loggerId).findAny().ifPresent(presentLogger -> {
                    if (!presentLogger.getLoggerName().equals(logger.getLoggerName())) {
                        errors.rejectValue("loggerName", ApiErrorDetails.NOT_SUPPORTED.getCodeString());
                    }
                });
            } else {

                if (loggers.stream()
                        .filter(tempLogger -> tempLogger.getLoggerName().equals(logger.getLoggerName()))
                        .findAny()
                        .isPresent()) {
                    errors.rejectValue("loggerName", ApiErrorDetails.ALREADY_EXISTS.getCodeString(),
                            new Object[] { logger.getLoggerName() }, "");
                }
            }
        }
    }

    public void validateExpirationDate(Errors errors, YukonLogger logger, String i18text) {
        if (logger.getLoggerType() == LoggerType.USER_LOGGER) {
            Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
            if (logger.getExpirationDate().before(today)) {
                errors.rejectValue("expirationDate", ApiErrorDetails.FUTURE_DATE.getCodeString(), new Object[] { i18text }, "");
            }
        } else {
            errors.rejectValue("expirationDate", ApiErrorDetails.NOT_SUPPORTED.getCodeString(),
                    new Object[] { logger.getExpirationDate() }, "");
        }
    }
}
