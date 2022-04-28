package com.cannontech.multispeak.dao.v4;

import java.util.EnumSet;
import java.util.List;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.msp.beans.v4.FormattedBlock;
import com.cannontech.multispeak.block.v4.Block;

/**
 * This is basically a copy of MeterReadProcessingService. 
 * At some point, there may be some commonalities that could be pulled from these
 */

public interface FormattedBlockProcessingService <T extends Block> {

    /**
     * Return an immutable object that can be used at the callers discretion
     * to update a FormattedBlock object with the given value for the given attribute.
     * The primary purpose of this is to allow the caller to asynchronously process
     * the point values into an object that can easily be stored (and can even be
     * aggregated or chained) and then processed later in a synchronous manner.
     */
    public FormattedBlockUpdater<T> buildFormattedBlockUpdater(BuiltInAttribute attribute,
                                           PointValueHolder pointValueHolder);

    /**
     * Simple helper to create a blank Block for a given Meter.
     */
    public T createBlock(YukonMeter meter);

    /**
     * This is an immediate version of the buildFormattedBlockUpdater that updates a FormattedBlock
     * object directly with the given value for the given attribute. This method
     * is not appropriate in a multi-threaded environment when the FormattedBlock may
     * be shared across threads.
     */
    public void updateFormattedBlock(T reading, BuiltInAttribute attribute,
                         PointValueHolder pointValueHolder);
    
    /**
     * Returns a copy of the set of BuiltInAttributes from attributesToLoad
     * @return
     */
    public EnumSet<BuiltInAttribute> getAttributeSet();
    
    /**
     * Returns a FormattedBlock for blocks that already exist.
     * Use this method if you already have the block data loaded.
     */
    public abstract FormattedBlock createMspFormattedBlock(List<T> blocks);

    /**
     * Helper method to call createFormattedBlcok for singleton.
     */
    public abstract FormattedBlock createMspFormattedBlock(T block);
}
