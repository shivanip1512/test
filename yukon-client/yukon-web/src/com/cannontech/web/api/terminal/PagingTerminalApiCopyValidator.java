package com.cannontech.web.api.terminal;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.terminal.model.TerminalCopy;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class PagingTerminalApiCopyValidator extends SimpleValidator<TerminalCopy> {

    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private MessageSourceAccessor accessor;
    private final static String commonkey = "yukon.common.";

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    public PagingTerminalApiCopyValidator() {
        super(TerminalCopy.class);
    }

    protected void doValidation(TerminalCopy terminalCopy, Errors errors) {
        String nameI18nText = accessor.getMessage(commonkey + "name");
        yukonApiValidationUtils.validateCopyPaoName(terminalCopy.getName(), errors, nameI18nText);
        yukonApiValidationUtils.checkIfFieldRequired("copyPoints", errors, terminalCopy.getCopyPoints(), "Copy Points");

    }

}
