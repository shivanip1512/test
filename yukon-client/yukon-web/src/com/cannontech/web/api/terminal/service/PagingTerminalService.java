package com.cannontech.web.api.terminal.service;

import java.util.List;

import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.device.terminal.model.TerminalCopy;

public interface PagingTerminalService {

    /**
     * Retrieve a paging terminal using id
     */
    TerminalBase<?> retrieve(int id);
    
    /**
     * Create a paging terminal
     */
    public TerminalBase<?> create(TerminalBase<?> terminalBase);
    
    /**
     * Delete a paging terminal
     */
    int delete(int id);
    
    /**
     * Retrieve all paging terminals
     */
    List<TerminalBase> retrieveAll();

    /**
     * Copy a paging terminal
     */
    TerminalBase<?> copy(int id, TerminalCopy terminalCopy);

}
