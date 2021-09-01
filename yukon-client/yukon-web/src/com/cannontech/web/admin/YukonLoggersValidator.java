package com.cannontech.web.admin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.clientutils.logger.service.YukonLoggerService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.log.model.LoggerType;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class YukonLoggersValidator extends SimpleValidator<YukonLogger> {
    private final static String basekey = "yukon.web.modules.adminSetup.config.loggers.";
    private final static String errorkey = "yukon.web.error.";

    @Autowired private YukonLoggerService loggerService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private MessageSourceAccessor accessor;

    public YukonLoggersValidator() {
        super(YukonLogger.class);
    }

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    @Override
    protected void doValidation(YukonLogger logger, Errors errors) {
        if (logger != null) {
            validateLoggerName(errors, logger, accessor.getMessage(basekey + "loggerName"));

            YukonValidationUtils.checkIfFieldRequired("level", errors, logger.getLevel(),
                    accessor.getMessage(basekey + "loggerLevel"));

            if (logger.getExpirationDate() != null) {
                validateExpirationDate(errors, logger, accessor.getMessage(basekey + "expirationDate"));
            }
            if (logger.getNotes() != null) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "notes", logger.getNotes(), 255);
            }
        }
    }

    public void validateLoggerName(Errors errors, YukonLogger logger, String i18Text) {
        YukonValidationUtils.checkIfFieldRequired("loggerName", errors, logger.getLoggerName(), i18Text);
        if (!errors.hasFieldErrors("loggerName")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "loggerName", logger.getLoggerName(), 200);
        }
        if (!errors.hasFieldErrors("loggerName")) {
            if (logger.getLoggerName().startsWith(".") || logger.getLoggerName().endsWith(".")) {
                errors.rejectValue("loggerName", errorkey + "isWhitelistedCharacter");
            }
        }
        if (!errors.hasFieldErrors("loggerName")) {
            YukonValidationUtils.checkWhitelistedCharacter(errors, "loggerName", logger.getLoggerName(), i18Text);
            List<YukonLogger> loggers = new ArrayList<>();
            loggers = loggerService.getLoggers(null, null, null, null);
            
            loggers.stream()
            .filter(tempLogger -> tempLogger.getLoggerName().equals(logger.getLoggerName()))
            .findAny()
            .ifPresent(presentLogger -> {
                 if (logger.getLoggerId() == -1 || presentLogger.getLoggerId() != logger.getLoggerId()) {
                     errors.rejectValue("loggerName", errorkey + "nameConflict");
                 }
            });
        }
    }
    
    public void validateExpirationDate(Errors errors, YukonLogger logger, String i18Text) {
        if (logger.getLoggerType() == LoggerType.USER_LOGGER) {
            Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
            if (!errors.hasFieldErrors("expirationDate") && logger.getExpirationDate().before(today)) {
                errors.rejectValue("expirationDate", errorkey + "date.inFuture", new Object[] { i18Text }, "");
            }
        } else {
            errors.rejectValue("expirationDate", errorkey + "notSupported", new Object[] { logger.getExpirationDate() }, "");
        }
    }

}
