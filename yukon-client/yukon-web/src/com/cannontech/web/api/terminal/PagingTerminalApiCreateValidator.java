package com.cannontech.web.api.terminal;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class PagingTerminalApiCreateValidator<T extends TerminalBase<?>> extends SimpleValidator<T> {

    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private MessageSourceAccessor accessor;

    private final static String commonkey = "yukon.common.";

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    public PagingTerminalApiCreateValidator(Class<T> objectType) {
        super(objectType);
    }

    @SuppressWarnings("unchecked")
    public PagingTerminalApiCreateValidator() {
        super((Class<T>) TerminalBase.class);
    }

    @Override
    protected void doValidation(T terminalBase, Errors errors) {
        String nameI18nText = accessor.getMessage(commonkey + "name");
        yukonApiValidationUtils.validateName(terminalBase.getName(), errors, nameI18nText);
    }
}