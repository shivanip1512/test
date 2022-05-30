package com.cannontech.web.stars.signalTransmitter;

import org.apache.logging.log4j.Logger;
import org.springframework.core.convert.converter.Converter;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.terminal.model.PagingTapTerminal;
import com.cannontech.common.device.terminal.model.SNPPTerminal;
import com.cannontech.common.device.terminal.model.TNPPTerminal;
import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.device.terminal.model.WCTPTerminal;
import com.cannontech.common.pao.PaoType;

/**
 * Converter class for terminal base.
 * This will take signal transmitter type as string and return the appropriate object.
 */
public class TerminalBaseConverter implements Converter<String, TerminalBase> {
    private static final Logger log = YukonLogManager.getLogger(TerminalBaseConverter.class);

    @Override
    public TerminalBase convert(String signalTransmitterType) {
        TerminalBase terminalBase = null;
        PaoType paoType = null;
        try {
            paoType = PaoType.valueOf(signalTransmitterType);
        } catch (IllegalArgumentException e) {
            log.error(signalTransmitterType + " pao type doesn't match with existing pao types", e);
        }
        switch (paoType) {
        case WCTP_TERMINAL:
            terminalBase = new WCTPTerminal();
            break;
        case SNPP_TERMINAL:
            terminalBase = new SNPPTerminal();
            break;
        case TNPP_TERMINAL:
            terminalBase = new TNPPTerminal();
        case TAPTERMINAL:
            terminalBase = new PagingTapTerminal();
            break;
        }
        return terminalBase;
    }
}
