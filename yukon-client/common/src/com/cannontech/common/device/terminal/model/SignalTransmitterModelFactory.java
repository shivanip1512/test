package com.cannontech.common.device.terminal.model;

import com.cannontech.common.pao.PaoType;

public class SignalTransmitterModelFactory {
    
    public static final TerminalBase createSignalTransmitter(PaoType type) {
        TerminalBase terminalBase = null;
        switch (type) {
        case WCTP_TERMINAL:
            terminalBase = new WCTPTerminal();
            break;
        case SNPP_TERMINAL:
            terminalBase = new SNPPTerminal();
            break;
        case TNPP_TERMINAL:
            terminalBase = new TNPPTerminal();
            break;
        case TAPTERMINAL:
            terminalBase = new PagingTapTerminal();
            break;
        }
        return terminalBase;
    }

}
