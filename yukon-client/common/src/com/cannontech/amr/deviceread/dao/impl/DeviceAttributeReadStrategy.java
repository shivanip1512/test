package com.cannontech.amr.deviceread.dao.impl;

import com.cannontech.amr.device.StrategyType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DeviceAttributeReadStrategy {
    
    /**
     * Each strategy implementation should have a type in the {@link StrategyType} enum.
     * This returns that type for this strategy implementation.
     */
    public StrategyType getType();

    /**
     * This associates the strategy with a subset of PaoTypes. It is up to the author of the
     * strategies to ensure that only one strategy "canRead" for each PaoType. It is recommended
     * that this be implemented with PaoTags. The caller of this method is allowed to cache the
     * result, therefore implementations should return always return the same answer.
     * @return true if it is appropriate to call use this strategy for the given PaoType
     */
    public boolean canRead(PaoType paoType);
    
    /**
     * This method provides a quick check of the readability of some PaoMultiPointIdentifiers.
     * Implementations may assume that this method will only be invoked for PaoTypes that have
     * already been tested with the {@link #canRead} method.
     * It is expected to execute much faster than the initateRead call. See {@link DeviceAttributeReadService#isReadable(Iterable, java.util.Set, LiteYukonUser) the main service}
     * for more information on the contract of this method.
     * @param points an Iterable of PaoMultiPointIdentifiers
     * @param user passed in for historical reasons, no authorization checks are expected
     * @return true if calling {@link #initiateRead} would do something
     */
    public boolean isReadable(Iterable<PaoMultiPointIdentifier> points, LiteYukonUser user);
    
    /**
     * This method initiates a read of some PaoMultiPointIdentifiers using whatever mechanism is 
     * appropriate for the strategy.
     * See {@linkplain DeviceAttributeReadService#initiateRead(Iterable, java.util.Set, com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback, DeviceRequestType, LiteYukonUser) the main service}
     * for more information on the contract of this method.
     * @param points an Iterable of PaoMultiPointIdentifiers
     * @param callback
     * @param type
     * @param user passed in logging purposes, no authorization checks are expected
     */
    public void initiateRead(Iterable<PaoMultiPointIdentifier> points, DeviceAttributeReadStrategyCallback callback, DeviceRequestType type, LiteYukonUser user);

}
