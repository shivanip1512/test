package com.cannontech.web.api.loggers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.clientutils.logger.service.YukonLoggerService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.log.model.LoggerType;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;

public class YukonLoggersApiValidator extends SimpleValidator<YukonLogger> {

    private final static String basekey = "yukon.web.modules.adminSetup.config.loggers.";

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private YukonLoggerService loggerService;

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
            Integer loggerId = id == null ? -1 : Integer.valueOf(id);
            validateLoggerName(errors, logger, accessor.getMessage(basekey + "loggerName"), loggerId);
            // validate logger level
            YukonApiValidationUtils.checkIfFieldRequired("level", errors, logger.getLevel(),
                    accessor.getMessage(basekey + "loggerLevel"));
            // validate expiration date
            if (logger.getExpirationDate() != null) {
                validateExpirationDate(errors, logger, accessor.getMessage(basekey + "expirationDate"));
            }
            // validate notes
            if (logger.getNotes() != null) {
                YukonApiValidationUtils.checkExceedsMaxLength(errors, accessor.getMessage(basekey + "notes"), logger.getNotes(),
                        255);
            }
        }

    }

    public void validateLoggerName(Errors errors, YukonLogger logger, String i18Text, Integer loggerId) {
        YukonApiValidationUtils.checkIfFieldRequired("loggerName", errors, logger.getLoggerName(), i18Text);
        if (!errors.hasFieldErrors("loggerName")) {
            YukonApiValidationUtils.checkExceedsMaxLength(errors, "loggerName", logger.getLoggerName(), 200);
        }
        if (!errors.hasFieldErrors("loggerName")) {
            YukonApiValidationUtils.checkWhitelistedCharacter(errors, "loggerName", logger.getLoggerName(), i18Text);
        }
        if (!errors.hasFieldErrors("loggerName")) {
            if (logger.getLoggerName().startsWith(".") || logger.getLoggerName().endsWith(".")) {
                errors.rejectValue("loggerName", ApiErrorDetails.WHITELIST_CHARACTERS.getCodeString());
            }
        }
        if (!errors.hasFieldErrors("loggerName")) {
            List<YukonLogger> loggers = new ArrayList<>();
            loggers = loggerService.getLoggers(null, null, null, null);
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

    public void validateExpirationDate(Errors errors, YukonLogger logger, String string) {
        if (logger.getLoggerType() == LoggerType.USER_LOGGER) {
            Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
            if (logger.getExpirationDate().before(today)) {
                errors.rejectValue("expirationDate", ApiErrorDetails.FUTURE_DATE.getCodeString());
            }
        } else {
            errors.rejectValue("expirationDate", ApiErrorDetails.NOT_SUPPORTED.getCodeString(),
                    new Object[] { logger.getExpirationDate() }, "");
        }
    }
}
