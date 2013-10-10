package com.cannontech.web.common.search.service;

import java.util.Set;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.search.result.Page;

/**
 * A class that implements this interface knows how to perform part of the site-wide search (searching for pages).
 * Results from all instances of this class will be aggregated into the final search results.
 */
public interface PageSearcher {
    /**
     * Search for up to <tt>count</tt> items matching <tt>searchString</tt>.
     */
    SearchResults<Page> search(String searchString, int count, YukonUserContext userContext);

    /**
     * Find up to <tt>count</tt> items for auto-complete matching <tt>searchString</tt>.  The strings
     * returned should be localized (mainly this would apply to page names).
     */
    Set<String> autocomplete(String searchString, int count, YukonUserContext userContext);
}
