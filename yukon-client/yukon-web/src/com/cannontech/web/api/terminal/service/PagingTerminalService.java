package com.cannontech.web.api.terminal.service;

import com.cannontech.common.device.terminal.model.TerminalBase;

public interface PagingTerminalService {

    /**
     * Create a paging terminal
     */
    public TerminalBase<?> create(TerminalBase<?> terminalBase);

}
