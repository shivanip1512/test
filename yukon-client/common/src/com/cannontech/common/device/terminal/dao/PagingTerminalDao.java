package com.cannontech.common.device.terminal.dao;

import java.util.List;

import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.model.Direction;

public interface PagingTerminalDao {

    List<TerminalBase> getAllTerminals(SortBy sortBy, Direction direction, String terminalName);
    
    
    public enum SortBy {
        NAME("PAOName"),
        TYPE("Type"),
        STATUS("DisableFlag");

        private final String dbString;

        private SortBy(String dbString) {
            this.dbString = dbString;
        }

        public String getDbString() {
            return dbString;
        }
    }
}
