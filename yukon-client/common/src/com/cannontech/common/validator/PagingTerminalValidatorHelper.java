package com.cannontech.common.validator;

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
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

public class PagingTerminalValidatorHelper {

    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private IDatabaseCache databaseCache;
    private MessageSourceAccessor accessor;

    private final static String commonKey = "yukon.common.";
    private final static String pagingTerminalKey = "yukon.web.modules.operator.pagingTerminal.";

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    public void vaildateCommChannel(TerminalBase terminalBase, Errors errors) {
        String commChannelI18nText = accessor.getMessage(pagingTerminalKey + "commChannel");
        yukonApiValidationUtils.checkIfFieldRequired("commChannel", errors, terminalBase.getCommChannel(),
                commChannelI18nText);
        if (!errors.hasFieldErrors("commChannel")) {
            yukonApiValidationUtils.checkIfFieldRequired("commChannel.id", errors, terminalBase.getCommChannel().getId(),
                    commChannelI18nText);
            if (!errors.hasFieldErrors("commChannel.id")) {
                LiteYukonPAObject liteYukonPAObject = databaseCache.getAllPaosMap().get(terminalBase.getCommChannel().getId());
                if (liteYukonPAObject == null || !(liteYukonPAObject.getPaoType() == PaoType.RFN_1200
                        || liteYukonPAObject.getPaoType().getPaoCategory() == PaoCategory.PORT)) {
                    errors.rejectValue("commChannel.id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { "PAO type RFN_1200 or PAO category PORT device Id" }, null);
                }
            }
        }
    }

    public void validatePagingTapTerminalFields(PagingTapTerminal terminal, Errors errors) {
        String pagerNumberI18nText = accessor.getMessage(pagingTerminalKey + "pagerNumber");
        yukonApiValidationUtils.checkIfFieldRequired("pagerNumber", errors, terminal.getPagerNumber(), pagerNumberI18nText);
        if (!errors.hasFieldErrors("pagerNumber")) {
            yukonApiValidationUtils.checkExceedsMaxLength(errors, "pagerNumber", terminal.getPagerNumber(), 20);
        }
    }

    public void validateSNPPTerminalFields(SNPPTerminal terminal, Errors errors) {
        String pagerNumberI18nText = accessor.getMessage(pagingTerminalKey + "pagerNumber");
        yukonApiValidationUtils.checkIfFieldRequired("pagerNumber", errors, terminal.getPagerNumber(), pagerNumberI18nText);
        if (!errors.hasFieldErrors("pagerNumber")) {
            yukonApiValidationUtils.checkExceedsMaxLength(errors, "pagerNumber", terminal.getPagerNumber(), 20);
        }
        if (terminal.getLogin() == null) {
            yukonApiValidationUtils.checkIfFieldRequired("login", errors, terminal.getLogin(),
                    "Login");
        }
        if (StringUtils.isNotBlank(terminal.getLogin())) {
            yukonApiValidationUtils.checkExceedsMaxLength(errors, "login", terminal.getLogin(), 64);
        }
    }

    public void validateTNPPTerminalFields(TNPPTerminal terminal, Errors errors) {
        yukonApiValidationUtils.checkRange(errors, "originAddress", terminal.getOriginAddress(), 0, 65535, true);
        yukonApiValidationUtils.checkRange(errors, "destinationAddress", terminal.getDestinationAddress(), 0, 65535,
                true);
        yukonApiValidationUtils.checkRange(errors, "inertia", terminal.getInertia(), -999999999, 999999999, true);

        String pagerIdI18nText = accessor.getMessage(pagingTerminalKey + "pagerId");
        yukonApiValidationUtils.checkIfFieldRequired("pagerId", errors, terminal.getPagerId(), pagerIdI18nText);

        String protocolI18nText = accessor.getMessage(pagingTerminalKey + "protocol");
        yukonApiValidationUtils.checkIfFieldRequired("protocol", errors, terminal.getProtocol(), protocolI18nText);

        String dataFormatI18nText = accessor.getMessage(pagingTerminalKey + "dataFormat");
        yukonApiValidationUtils.checkIfFieldRequired("dataFormat", errors, terminal.getDataFormat(), dataFormatI18nText);

        String identifierFormatI18nText = accessor.getMessage(pagingTerminalKey + "identifierFormat");
        yukonApiValidationUtils.checkIfFieldRequired("identifierFormat", errors, terminal.getIdentifierFormat(),
                identifierFormatI18nText);

        if (!errors.hasFieldErrors("pagerId")) {
            if (terminal.getIdentifierFormat() == IdentifierFormat.CAP_PAGE) {
                try {
                    Integer pagerId = Integer.valueOf(terminal.getPagerId());
                    yukonApiValidationUtils.checkRange(errors, "pagerId", pagerId, -9999999, 99999999, true);
                } catch (Exception e) {
                    errors.rejectValue("pagerId", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { "Integer between -9999999 & 99999999" }, pagerIdI18nText);
                }
            } else {
                yukonApiValidationUtils.checkExceedsMaxLength(errors, "pagerId", terminal.getPagerId(), 10);
            }
        }
        yukonApiValidationUtils.checkRange(errors, "channel", terminal.getChannel(), 0, 63, true);
        yukonApiValidationUtils.checkRange(errors, "functionCode", terminal.getFunctionCode(), 0, 63, true);
        yukonApiValidationUtils.checkRange(errors, "zone", terminal.getZone(), 0, 63, true);
    }

    public void validateWCTPTerminalFields(WCTPTerminal terminal, Errors errors) {
        String pagerNumberI18nText = accessor.getMessage(pagingTerminalKey + "pagerNumber");
        yukonApiValidationUtils.checkIfFieldRequired("pagerNumber", errors, terminal.getPagerNumber(), pagerNumberI18nText);
        if (!errors.hasFieldErrors("pagerNumber")) {
            yukonApiValidationUtils.checkExceedsMaxLength(errors, "pagerNumber", terminal.getPagerNumber(), 20);
        }
        String senderI18nText = accessor.getMessage(pagingTerminalKey + "sender");
        yukonApiValidationUtils.checkIfFieldRequired("sender", errors, terminal.getSender(), senderI18nText);
        if (!errors.hasFieldErrors("sender")) {
            yukonApiValidationUtils.checkExceedsMaxLength(errors, "sender", terminal.getSender(), 64);
        }
        String securityCodeI18nText = accessor.getMessage(pagingTerminalKey + "securityCode");
        yukonApiValidationUtils.checkIfFieldRequired("securityCode", errors, terminal.getSecurityCode(),
                securityCodeI18nText);
        if (!errors.hasFieldErrors("securityCode")) {
            yukonApiValidationUtils.checkExceedsMaxLength(errors, "securityCode", terminal.getSecurityCode(), 64);
        }
    }

    public void validateType(TerminalBase terminalBase, Errors errors) {
        String typeI18nText = accessor.getMessage(commonKey + "type");
        yukonApiValidationUtils.checkIfFieldRequired("type", errors, terminalBase.getType(), typeI18nText);
        if (!errors.hasFieldErrors("type") && !(terminalBase.getType() == PaoType.TAPTERMINAL
                || terminalBase.getType() == PaoType.SNPP_TERMINAL || terminalBase.getType() == PaoType.TNPP_TERMINAL
                || terminalBase.getType() == PaoType.WCTP_TERMINAL)) {
            errors.rejectValue("type", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { "TAPTERMINAL, SNPP_TERMINAL, TNPP_TERMINAL & WCTP_TERMINAL" },
                    typeI18nText);
        }
    }
}
