package com.cannontech.web.common.search.service;

import java.util.List;
import java.util.Map;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.search.result.Page;

public interface SiteSearchService {
    
    /**
     * For performance reasons, searches are limited to the first MAX_SEARCH_ITEMS results. Querying
     * for more than this will result in an error. (In
     * {@link #search(String, int, int, YukonUserContext)}, startIndex + count must be no greater
     * than this value.)
     */
    int MAX_SEARCH_ITEMS = 1000;

    /**
     * This should be called on any search string before using it in a search to clean the string of any special
     * Lucene characters.  This is available as a separate method (instead of just happening inside search) so the
     * user can report the actual string searched on.
     */
    String sanitizeQuery(String query);

    /**
     * Search for up to count results across all searchable pages, starting at startIndex.  The value of
     * count cannot be greater than 1000.  It is expected that {@link #sanitizeQuery(String)} will have
     * been called on searchStr before calling this method.<p>
     * 
     * startIndex + count must be no greater than {@link #MAX_SEARCH_ITEMS}
     */
    SearchResults<Page> search(String query, int startIndex, int count, YukonUserContext userContext);

    /**
     * Search for a list of search strings.  The incoming query will be sanitized using
     * {@link #sanitizeQuery(String)} so it is not necessary to call it as with
     * {@link #search(String, int, int, YukonUserContext)}.
     */
    List<Map<String, Object>> autocomplete(String query, YukonUserContext userContext);
    
}