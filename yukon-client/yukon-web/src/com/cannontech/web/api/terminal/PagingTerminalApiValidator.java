package com.cannontech.web.api.terminal;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.device.terminal.model.IdentifierFormat;
import com.cannontech.common.device.terminal.model.PagingTapTerminal;
import com.cannontech.common.device.terminal.model.SNPPTerminal;
import com.cannontech.common.device.terminal.model.TNPPTerminal;
import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.device.terminal.model.WCTPTerminal;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.PagingTerminalValidatorHelper;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

public class PagingTerminalApiValidator<T extends TerminalBase<?>> extends SimpleValidator<T> {

    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private MessageSourceAccessor accessor;

    private final static String commonKey = "yukon.common.";
    
    @Autowired private PagingTerminalValidatorHelper pagingTerminalValidatorHelper;

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    public PagingTerminalApiValidator(Class<T> objectType) {
        super(objectType);
    }

    @SuppressWarnings("unchecked")
    public PagingTerminalApiValidator() {
        super((Class<T>) TerminalBase.class);
    }

    @Override
    protected void doValidation(T terminalBase, Errors errors) {
        if (!errors.hasFieldErrors("name")) {
            // validate name
            String nameI18nText = accessor.getMessage(commonKey + "name");
            yukonApiValidationUtils.validateNewPaoName(terminalBase.getName(), terminalBase.getType(), errors, nameI18nText);
        }
        // validate paoType
        pagingTerminalValidatorHelper.validateType(terminalBase, errors);

        // validate password
        if (terminalBase.getPassword() == null) {
            yukonApiValidationUtils.checkIfFieldRequired("password", errors, terminalBase.getPassword(),
                    "Password");
        }
        if (StringUtils.isNotBlank(terminalBase.getPassword())) {
            int maxLength = terminalBase instanceof SNPPTerminal ? 64 : 20;
            yukonApiValidationUtils.checkExceedsMaxLength(errors, "password", terminalBase.getPassword(), maxLength);
        }

        // validate commChannel
        pagingTerminalValidatorHelper.vaildateCommChannel(terminalBase, errors);

        // validate PagingTapTerminal fields
        if (terminalBase instanceof PagingTapTerminal) {
            PagingTapTerminal terminal = (PagingTapTerminal) terminalBase;
            pagingTerminalValidatorHelper.validatePagingTapTerminalFields(terminal, errors);
        }
        
        // validate SNPPTerminal fields
        if (terminalBase instanceof SNPPTerminal) {
            SNPPTerminal terminal = (SNPPTerminal) terminalBase;
            pagingTerminalValidatorHelper.validateSNPPTerminalFields(terminal, errors);
        }

        // validate TNPPTerminal fields
        if (terminalBase instanceof TNPPTerminal) {
            TNPPTerminal terminal = (TNPPTerminal) terminalBase;
            pagingTerminalValidatorHelper.validateTNPPTerminalFields(terminal, errors);
        }

        // validate WCTPTerminal fields
        if (terminalBase instanceof WCTPTerminal) {
            WCTPTerminal terminal = (WCTPTerminal) terminalBase;
            pagingTerminalValidatorHelper.validateWCTPTerminalFields(terminal, errors);
        }
    }
}