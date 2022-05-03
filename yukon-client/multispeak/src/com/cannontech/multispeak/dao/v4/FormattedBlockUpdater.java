package com.cannontech.multispeak.dao.v4;

import com.cannontech.multispeak.block.v4.Block;

/**
 * This is basically a copy of MeterReadUpdater. 
 * At some point, there may be some commonalities that could be pulled from these
 */

/**
 * Utility callback that allows the implementer to encapsulate some manipulation
 * of a Block object. It is assumed that some implementations of this will
 * aggregate other implementations of this interface.
 */
public interface FormattedBlockUpdater <T extends Block> {
    /**
     * Applies some encapsulated manipulation to the Block object.
     */
    public void update(T block);
}
