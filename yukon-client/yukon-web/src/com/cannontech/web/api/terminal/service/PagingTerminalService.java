package com.cannontech.web.api.terminal.service;

import com.cannontech.common.device.terminal.dao.PagingTerminalDao.SortBy;
import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.device.terminal.model.TerminalCopy;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PaginatedResponse;

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
    PaginatedResponse<TerminalBase> retrieveAll(SortBy sortBy, Direction direction, int pageNumber, int itemsPerPage,
            String terminalName);

    /**
     * Copy a paging terminal
     */
    TerminalBase<?> copy(int id, TerminalCopy terminalCopy);

    /**
     * Update a terminal.
     */
    TerminalBase<?> update(int id, TerminalBase<?> terminalBase);

}
