package com.cannontech.web.api.loggers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

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
            YukonApiValidationUtils.checkIfFieldRequired("loggerName", errors, logger.getLoggerName(),
                    accessor.getMessage(basekey + "loggerName"));
            validateLoggerName(errors, logger.getLoggerName(), accessor.getMessage(basekey + "loggerName"));
            // validate logger level
            YukonApiValidationUtils.checkIfFieldRequired("level", errors, logger.getLevel(),
                    accessor.getMessage(basekey + "loggerLevel"));
            // validate expiration TODO only applicable for userLogger
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

    public void validateLoggerName(Errors errors, String loggerName, String i18Text) {
        if (!errors.hasFieldErrors("loggerName")) {
            YukonApiValidationUtils.checkExceedsMaxLength(errors, "loggerName", loggerName, 200);
            YukonApiValidationUtils.checkWhitelistedCharacter(errors, "loggerName", loggerName, i18Text);
            List<YukonLogger> loggers = new ArrayList<>();
            loggers = loggerService.getLoggers(null, null, null, null);
            
            List<String> loggerNames = loggers.stream()
                    .map(YukonLogger::getLoggerName)
                    .collect(Collectors.toList());
            if (loggerNames.contains(loggerName)) {
                errors.rejectValue("loggerName", ApiErrorDetails.ALREADY_EXISTS.getCodeString(), new Object[] { loggerName }, "");
            }
        }
    }

    public void validateExpirationDate(Errors errors, YukonLogger logger, String string) {
        if (logger.getExpirationDate() != null) {
            if (logger.getLoggerType().equals(LoggerType.USER_LOGGER)) {
                SimpleDateFormat sdfrmt = new SimpleDateFormat("MM/dd/yyyy");
                Date date = null;
                sdfrmt.setLenient(false);
                try {
                    date = sdfrmt.parse(logger.getExpirationDate().toString());
                } catch (ParseException e) {
                    errors.rejectValue("expirationDate", ApiErrorDetails.NOT_SUPPORTED.getCodeString(),
                            new Object[] { logger.getExpirationDate().toString() }, "");
                }
            } else {
                errors.rejectValue("expirationDate", ApiErrorDetails.NOT_SUPPORTED.getCodeString(),
                        new Object[] { logger.getExpirationDate() }, "");
            }
        }
    }

}
