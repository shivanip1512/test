package com.cannontech.database.dbchange;

import static com.cannontech.database.dbchange.ChangeSequenceStrategyEnum.ERROR;
import static com.cannontech.database.dbchange.ChangeSequenceStrategyEnum.KEEP_BOTH;
import static com.cannontech.database.dbchange.ChangeSequenceStrategyEnum.KEEP_FIRST;
import static com.cannontech.database.dbchange.ChangeSequenceStrategyEnum.KEEP_LAST;
import static com.cannontech.message.dispatch.message.DbChangeType.ADD;
import static com.cannontech.message.dispatch.message.DbChangeType.DELETE;
import static com.cannontech.message.dispatch.message.DbChangeType.UPDATE;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.message.dispatch.message.DbChangeType;

public class ChangeSequenceDecoder {
    private static final Map<ChangeSequence, ChangeSequenceStrategyEnum> strategyLookup = new HashMap<ChangeSequence, ChangeSequenceStrategyEnum>();
    
    static {
        initData(ADD, UPDATE, KEEP_FIRST);
        initData(UPDATE, UPDATE, KEEP_FIRST);
        initData(ADD, ADD, KEEP_FIRST);
        initData(DELETE, DELETE, KEEP_FIRST);
        
        initData(DELETE, ADD, KEEP_BOTH);
        initData(ADD, DELETE, KEEP_BOTH);
        
        initData(UPDATE, DELETE, KEEP_LAST);
        
        initData(UPDATE, ADD, ERROR);
        initData(DELETE, UPDATE, ERROR);
    }
    
    private static void initData(DbChangeType first, DbChangeType second, ChangeSequenceStrategyEnum strategy) {
        ChangeSequenceStrategyEnum last = strategyLookup.put(new ChangeSequence(first, second), strategy);
        if (last != null) {
            throw new BadConfigurationException("Bad setup, can't repeat a change sequence combination");
        }
    }

    public static ChangeSequenceStrategyEnum getStrategy(DbChangeType first, DbChangeType second) {
        return strategyLookup.get(new ChangeSequence(first, second));
    }
    
    private static class ChangeSequence {
        private DbChangeType first;
        private DbChangeType second;
        
        public ChangeSequence(DbChangeType first, DbChangeType second) {
            super();
            this.first = first;
            this.second = second;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((first == null) ? 0 : first.hashCode());
            result = prime * result + ((second == null) ? 0 : second.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ChangeSequence other = (ChangeSequence) obj;
            if (first == null) {
                if (other.first != null)
                    return false;
            } else if (!first.equals(other.first))
                return false;
            if (second == null) {
                if (other.second != null)
                    return false;
            } else if (!second.equals(other.second))
                return false;
            return true;
        }
        
        
    }
}
