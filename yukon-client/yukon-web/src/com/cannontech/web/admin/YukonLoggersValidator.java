package com.cannontech.web.admin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.clientutils.logger.service.YukonLoggerService;
import com.cannontech.common.i18n.MessageSourceAccessor;
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
        validateLoggerName(errors, logger.getLoggerName(), accessor.getMessage(basekey + "loggerName"));
    }

    public void validateLoggerName(Errors errors, String loggerName, String i18Text) {
        if (!errors.hasFieldErrors("loggerName")) {
            YukonValidationUtils.checkIfFieldRequired("loggerName", errors, loggerName, i18Text);
            YukonValidationUtils.checkExceedsMaxLength(errors, "loggerName", loggerName, 200);
            YukonValidationUtils.checkWhitelistedCharacter(errors, "loggerName", loggerName, i18Text);
            List<YukonLogger> loggers = new ArrayList<>();
            loggers = loggerService.getLoggers(null, null, null, null);
            if (loggers.contains(loggerName)) {
                errors.rejectValue("loggerName", accessor.getMessage(errorkey + "nameConflict"));
            }
        }
    }

}
