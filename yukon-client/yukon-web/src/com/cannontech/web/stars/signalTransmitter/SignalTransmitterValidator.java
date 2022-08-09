package com.cannontech.web.stars.signalTransmitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.terminal.model.IdentifierFormat;
import com.cannontech.common.device.terminal.model.PagingTapTerminal;
import com.cannontech.common.device.terminal.model.SNPPTerminal;
import com.cannontech.common.device.terminal.model.TNPPTerminal;
import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.device.terminal.model.WCTPTerminal;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.common.validator.YukonValidationUtils;

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
        yukonValidationHelper.checkIfFieldRequired("name", errors, terminalBase.getName(),
                yukonValidationHelper.getMessage(key + ".name"));
        if (!PaoUtils.isValidPaoName(terminalBase.getName())) {
            errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
        }

        // Validate Type
        yukonValidationHelper.checkIfFieldRequired("type", errors, terminalBase.getType(),
                yukonValidationHelper.getMessage(key + ".type"));

        // Validate Pao Name
        if (!errors.hasFieldErrors("type") && !errors.hasFieldErrors("name")) {
            yukonValidationHelper.validatePaoName(terminalBase.getName(), terminalBase.getType(), errors,
                    yukonValidationHelper.getMessage("yukon.common.name"), paoId, "name");
        }

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
                        loginTxt, 64);
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
        
        if (terminalBase instanceof TNPPTerminal) {
            TNPPTerminal terminal = (TNPPTerminal) terminalBase;
            String originAddressTxt = yukonValidationHelper.getMessage(key + ".originAddress");
            yukonValidationHelper.checkIfFieldRequired("originAddress", errors, terminal.getOriginAddress(), originAddressTxt);
            if (!errors.hasFieldErrors("originAddress")) {
                YukonValidationUtils.checkRange(errors, "originAddress", terminal.getOriginAddress(), 0, 65535, true);
            }
            String destinationAddressTxt = yukonValidationHelper.getMessage(key + ".destinationAddress");
            yukonValidationHelper.checkIfFieldRequired("destinationAddress", errors, terminal.getDestinationAddress(),
                    destinationAddressTxt);
            if (!errors.hasFieldErrors("destinationAddress")) {
                YukonValidationUtils.checkRange(errors, "destinationAddress", terminal.getDestinationAddress(), 0, 65535, true);
            }
            String inertiaTxt = yukonValidationHelper.getMessage(key + ".inertia");
            yukonValidationHelper.checkIfFieldRequired("inertia", errors, terminal.getInertia(), inertiaTxt);
            if (!errors.hasFieldErrors("inertia")) {
                YukonValidationUtils.checkRange(errors, "inertia", terminal.getInertia(), -999999999, 999999999, true);
            }
            String pagerIdTxt = yukonValidationHelper.getMessage(key + ".pagerId");
            yukonValidationHelper.checkIfFieldRequired("pagerId", errors, terminal.getPagerId(), pagerIdTxt);
            String protocolTxt = yukonValidationHelper.getMessage(key + ".protocol");
            yukonValidationHelper.checkIfFieldRequired("protocol", errors, terminal.getProtocol(), protocolTxt);
            String dataFormatTxt = yukonValidationHelper.getMessage(key + ".dataFormat");
            yukonValidationHelper.checkIfFieldRequired("dataFormat", errors, terminal.getDataFormat(), dataFormatTxt);
            String indentifierFormatTxt = yukonValidationHelper.getMessage(key + ".indentifierFormat");
            yukonValidationHelper.checkIfFieldRequired("identifierFormat", errors, terminal.getIdentifierFormat(),
                    indentifierFormatTxt);
            
            if (!errors.hasFieldErrors("pagerId")) {
                if (terminal.getIdentifierFormat() == IdentifierFormat.CAP_PAGE) {
                    try {
                        Integer pagerId = Integer.valueOf(terminal.getPagerId());
                        YukonValidationUtils.checkRange(errors, "pagerId", pagerId, -9999999, 99999999, true);
                    } catch (Exception e) {
                        errors.rejectValue("pagerId", key + ".pagerId.invalidValue");
                    }
                } else {
                    yukonValidationHelper.checkExceedsMaxLength("pagerId", errors, String.valueOf(terminal.getPagerId()),
                            pagerIdTxt, 10);
                }
            }
            String channelTxt = yukonValidationHelper.getMessage(key + ".channel");
            yukonValidationHelper.checkIfFieldRequired("channel", errors, terminal.getChannel(), channelTxt);
            if (!errors.hasFieldErrors("channel")) {
                YukonValidationUtils.checkRange(errors, "channel", terminal.getChannel(), 0, 63, true);
            }
            String functionCodeTxt = yukonValidationHelper.getMessage(key + ".functionCode");
            yukonValidationHelper.checkIfFieldRequired("functionCode", errors, terminal.getFunctionCode(), functionCodeTxt);
            if (!errors.hasFieldErrors("functionCode")) {
                YukonValidationUtils.checkRange(errors, "functionCode", terminal.getFunctionCode(), 0, 63, true);
            }
            String zoneTxt = yukonValidationHelper.getMessage(key + ".zone");
            yukonValidationHelper.checkIfFieldRequired("zone", errors, terminal.getZone(), zoneTxt);
            if (!errors.hasFieldErrors("zone")) {
                YukonValidationUtils.checkRange(errors, "zone", terminal.getZone(), 0, 63, true);
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
        String pageNumberFieldTxt = yukonValidationHelper.getMessage(key + ".pagerNumber");
        yukonValidationHelper.checkIfFieldRequired("pagerNumber", errors, pageNumber,
                pageNumberFieldTxt);
        if (!errors.hasFieldErrors("pagerNumber")) {
            yukonValidationHelper.checkExceedsMaxLength("pagerNumber", errors, pageNumber,
                    pageNumberFieldTxt, 20);
        }
    }
}