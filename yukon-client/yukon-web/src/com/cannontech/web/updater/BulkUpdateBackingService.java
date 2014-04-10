package com.cannontech.web.updater;

import java.util.List;
import java.util.Map;

import com.cannontech.user.YukonUserContext;

public interface BulkUpdateBackingService {
    
    /**
     * Returns latest values.
     */
    Map<UpdateIdentifier, String> getLatestValues(List<UpdateIdentifier> identifiers,
                                                  long afterDate, YukonUserContext userContext,
                                                  boolean registerForPointData);

}
