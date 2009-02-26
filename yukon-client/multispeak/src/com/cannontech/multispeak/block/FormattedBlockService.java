package com.cannontech.multispeak.block;

import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.multispeak.deploy.service.FormattedBlock;

public interface FormattedBlockService <T extends Block> {

    /**
     * Returns a FormattedBlock object for the list of meters.
     * The value list data is populated for the meters.
     * Use this method if you want to load data for the meter objects
     * based on the getBlock method.  
     */
    public abstract FormattedBlock getFormattedBlock(List<Meter> meters);

    /**
     * Helper method to call getFormattedBlock for singleton.
     */
    public abstract FormattedBlock getFormattedBlock(Meter meter);
    
    /**
     * Returns a FormattedBlock for blocks that already exist.
     * Use this method if you already have the block data loaded.
     */
    public abstract FormattedBlock createFormattedBlock(List<T> blocks);

    /**
     * Helper method to call createFormattedBlcok for singleton.
     */
    public abstract FormattedBlock createFormattedBlock(T block);

    /** 
     * Method to return a new instance of T
     * @return T extends Block
     */
    public T getNewBlock();
    
    /**
     * Helper method to change a meter into a Block.
     */
    public abstract T getBlock(Meter meter);
}
