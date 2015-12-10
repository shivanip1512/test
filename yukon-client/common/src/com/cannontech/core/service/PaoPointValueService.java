package com.cannontech.core.service;

import java.util.List;
import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.amr.paoPointValue.model.MeterPointValue;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.util.ReadableRange;
import com.cannontech.user.YukonUserContext;

public interface PaoPointValueService {
    
    /**
     * Method to get a list of MeterPointValue objects for a given list of YukonPaos & attributes within a give time range. 
     * StartDate and StopDate are inclusive.
     * Ordering is timestamp asc if maxRows is NOT passed in, and timestamp desc if it IS passed in
     * @param list The Iterable of YukonPao objects
     * @param attributes The Set of Attribute objects
     * @param from - Start time of period (this is always the first argument in SQL, either > or >=)
     * @param to - End time of period (this is always the second argument in SQL, either < or <=)
     * @param maxRows - the max number of results per device to return (so, total results will never be more than maxRows * devices.size())
     * @param includeDisabledPaos - whether or not to include disabled paos in the returned results
     * @param discludedPointStateValues - the String representation of any PointState value's that should not be included in the results
     * @return List of values for the point
     */
    <P extends YukonPao> List<MeterPointValue> getMeterPointValues(Iterable<P> list,
                                                                   Set<? extends Attribute> attributes,
                                                                   ReadableRange<Instant> range,
                                                                   Integer maxRows,
                                                                   boolean includeDisabledPaos,
                                                                   Set<Integer> discludedPointStateValues,
                                                                   YukonUserContext userContext);
}
