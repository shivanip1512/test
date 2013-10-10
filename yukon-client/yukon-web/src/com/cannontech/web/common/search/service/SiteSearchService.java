package com.cannontech.web.common.search.service;

import java.util.List;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.search.result.Page;

public interface SiteSearchService {
    /**
     * Search for up to count results across all searchable pages, starting at startIndex.  The value of
     * count cannot be greater than 1000.
     */
    SearchResults<Page> search(String searchStr, int startIndex, int count, YukonUserContext userContext);

    List<String> autocomplete(String searchStr, YukonUserContext userContext);
}
