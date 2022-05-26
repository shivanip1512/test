package com.cannontech.web.stars.signalTransmitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.terminal.model.PagingTapTerminal;
import com.cannontech.common.device.terminal.model.SNPPTerminal;
import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.device.terminal.model.WCTPTerminal;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;

public class SignalTransmitterValidator<T extends TerminalBase<?>> extends SimpleValidator<T> {
    @Autowired private YukonValidationHelper yukonValidationHelper;

    private static final String key = "yukon.web.modules.operator.signalTransmitter";

    @SuppressWarnings("unchecked")
    public SignalTransmitterValidator() {
        super((Class<T>) TerminalBase.class);
    }

    public SignalTransmitterValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T terminalBase, Errors errors) {
        String paoId = terminalBase.getId() != null ? terminalBase.getId().toString() : null;

        // Validate Name
        yukonValidationHelper.validatePaoName(terminalBase.getName(), terminalBase.getType(), errors,
                yukonValidationHelper.getMessage("yukon.common.name"), paoId, "name");

        // Validate Type
        yukonValidationHelper.checkIfFieldRequired("type", errors, terminalBase.getType(),
                yukonValidationHelper.getMessage(key + ".type"));

        // validate commChannel
        yukonValidationHelper.checkIfFieldRequired("commChannel.id", errors,
                terminalBase.getCommChannel() == null ? terminalBase.getCommChannel() : terminalBase.getCommChannel().getId(),
                yukonValidationHelper.getMessage(key + ".commChannel"));

        if (terminalBase instanceof PagingTapTerminal) {
            validatePageNumberField(terminalBase, errors);
        }

        if (terminalBase instanceof SNPPTerminal) {
            validatePageNumberField(terminalBase, errors);
            SNPPTerminal terminal = (SNPPTerminal) terminalBase;

            String loginTxt = yukonValidationHelper.getMessage(key + ".login");
            yukonValidationHelper.checkIfFieldRequired("login", errors, terminal.getLogin(),
                    loginTxt);
            if (!errors.hasFieldErrors("login")) {
                yukonValidationHelper.checkExceedsMaxLength("login", errors, terminal.getLogin(),
                        loginTxt, 20);
            }
        }

        if (terminalBase instanceof WCTPTerminal) {
            validatePageNumberField(terminalBase, errors);
            WCTPTerminal terminal = (WCTPTerminal) terminalBase;
            
            String sender = terminal.getSender();
            String senderTxt = yukonValidationHelper.getMessage(key + ".sender");
            yukonValidationHelper.checkIfFieldRequired("sender", errors, sender, senderTxt);
            if (!errors.hasFieldErrors("sender")) {
                yukonValidationHelper.checkExceedsMaxLength("sender", errors, sender, senderTxt, 64);
            }
            
            String securityCodeTxt = yukonValidationHelper.getMessage(key + ".securityCode");
            String securityCode = terminal.getSecurityCode();
            yukonValidationHelper.checkIfFieldRequired("securityCode", errors, securityCode, securityCodeTxt);
            if (!errors.hasFieldErrors("securityCode")) {
                yukonValidationHelper.checkExceedsMaxLength("securityCode", errors, securityCode, securityCodeTxt, 64);
            }
        }

    }

    private void validatePageNumberField(TerminalBase terminal, Errors errors) {
        String pageNumber = null;
        if (terminal instanceof SNPPTerminal) {
            pageNumber = ((SNPPTerminal) terminal).getPagerNumber();
        } else if (terminal instanceof PagingTapTerminal) {
            pageNumber = ((PagingTapTerminal) terminal).getPagerNumber();
        } else if (terminal instanceof WCTPTerminal) {
            pageNumber = ((WCTPTerminal) terminal).getPagerNumber();
        }
        String pageNumberFieldTxt = yukonValidationHelper.getMessage(key + ".pageNumber");
        yukonValidationHelper.checkIfFieldRequired("pagerNumber", errors, pageNumber,
                pageNumberFieldTxt);
        if (!errors.hasFieldErrors("pagerNumber")) {
            yukonValidationHelper.checkExceedsMaxLength("pagerNumber", errors, pageNumber,
                    pageNumberFieldTxt, 20);
        }
    }
}