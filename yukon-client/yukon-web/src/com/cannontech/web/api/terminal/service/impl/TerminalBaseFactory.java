package com.cannontech.web.api.terminal.service.impl;

import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.IEDBase;
import com.cannontech.database.data.device.PagingTapTerminal;
import com.cannontech.database.data.device.SNPPTerminal;
import com.cannontech.database.data.device.TNPPTerminal;
import com.cannontech.database.data.device.WCTPTerminal;

public class TerminalBaseFactory {

    public static IEDBase getIEDBase(PaoType type) {
        switch (type) {
        case TAPTERMINAL:
            return new PagingTapTerminal();
        case SNPP_TERMINAL:
            return new SNPPTerminal();
        case TNPP_TERMINAL:
            return new TNPPTerminal();
        case WCTP_TERMINAL:
            return new WCTPTerminal();
        default:
            throw new IllegalArgumentException("Invalid PaoType for terminal base");
        }
    }
    
    public static TerminalBase<?> getTerminalBase(PaoType type)
    {
        switch (type) {
        case TAPTERMINAL:
            return new com.cannontech.common.device.terminal.model.PagingTapTerminal();
        case SNPP_TERMINAL:
            return new com.cannontech.common.device.terminal.model.SNPPTerminal();
        case TNPP_TERMINAL:
            return new com.cannontech.common.device.terminal.model.TNPPTerminal();
        case WCTP_TERMINAL:
            return new com.cannontech.common.device.terminal.model.WCTPTerminal();
        default:
            throw new IllegalArgumentException("Invalid PaoType for terminal base");
        }
    }
}