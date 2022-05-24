package com.cannontech.web.stars.signalTransmitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.terminal.model.PagingTapTerminal;
import com.cannontech.common.device.terminal.model.SNPPTerminal;
import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.device.terminal.model.WCTPTerminal;
import com.cannontech.common.validator.PagingTerminalValidatorHelper;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;

public class SignalTransmitterValidator<T extends TerminalBase<?>> extends SimpleValidator<T> {
    @Autowired private YukonValidationHelper yukonValidationHelper;

    @Autowired private PagingTerminalValidatorHelper pagingTerminalValidatorHelper;

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
        pagingTerminalValidatorHelper.validateType(terminalBase, errors);

        // validate commChannel
        pagingTerminalValidatorHelper.vaildateCommChannel(terminalBase, errors);

        if (terminalBase instanceof PagingTapTerminal) {
            PagingTapTerminal terminal = (PagingTapTerminal) terminalBase;
            pagingTerminalValidatorHelper.validatePagingTapTerminalFields(terminal, errors);
        }

        if (terminalBase instanceof SNPPTerminal) {
            SNPPTerminal terminal = (SNPPTerminal) terminalBase;
            pagingTerminalValidatorHelper.validateSNPPTerminalFields(terminal, errors);
        }

        if (terminalBase instanceof WCTPTerminal) {
            WCTPTerminal terminal = (WCTPTerminal) terminalBase;
            pagingTerminalValidatorHelper.validateWCTPTerminalFields(terminal, errors);
        }

    }
}