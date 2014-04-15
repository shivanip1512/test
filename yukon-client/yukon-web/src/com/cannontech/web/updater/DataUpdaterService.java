package com.cannontech.web.updater;

import java.util.Set;

import com.cannontech.user.YukonUserContext;

public interface DataUpdaterService {
    /**
     * Process data from a single request. The request contains all of the updating values on a single
     * page collected in the tokens parameter. This method is used by the controller which accepts the
     * periodic requests for updates.
     * 
     * @param afterDate the if-modified-since date, or null if the latest value is desired
     */
    UpdateResponse getUpdates(Set<String> tokens, long afterDate, YukonUserContext userContext);

    /**
     * Process the single value request. This method is used by the server when building the page
     * for the first time.
     */
    UpdateValue getFirstValue(String fullIdentifier, YukonUserContext userContext);
}
