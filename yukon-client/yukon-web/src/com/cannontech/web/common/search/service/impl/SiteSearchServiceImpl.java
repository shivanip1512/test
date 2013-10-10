package com.cannontech.web.common.search.service.impl;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.search.result.Page;
import com.cannontech.web.common.search.service.PageSearcher;
import com.cannontech.web.common.search.service.SiteSearchService;
import com.cannontech.web.common.userpage.service.UserPageService;
import com.google.common.base.Function;

public class SiteSearchServiceImpl implements SiteSearchService {
    private final Logger log = YukonLogManager.getLogger(SiteSearchServiceImpl.class);

    @Autowired private UserPageService userPageService;

    @Autowired private List<PageSearcher> pageSearchers;

    @Override
    public SearchResults<Page> search(String searchStr, int startIndex, int count, final YukonUserContext userContext) {
        if (log.isDebugEnabled()) {
            log.debug("searching for [" + searchStr + "], starting at " + startIndex + ", count = " + count);
        }

        if (count > 1000) {
            // The caller should limit this to avoid this exception.
            throw new UnsupportedOperationException("search does not support more than 1000 results");
        }

        // Because we can't sort until after all the search results have been compiled, we need to get all results
        // up to and including the page we are on to properly page.  Of course, this will get less efficient as the
        // on the second page than the first, and so on.  (This is why we limit the search to 1000 items.)
        int searchCount = startIndex + count;

        List<Page> combined = new ArrayList<>();
        for (PageSearcher searchService : pageSearchers) {
            SearchResults<Page> results = searchService.search(searchStr, searchCount, userContext);
            combined.addAll(results.getResultList());
        }

        // Sort on the localized name.
        Function<Page, String> translator = new Function<Page, String>() {
            @Override
            public String apply(Page page) {
                return userPageService.getLocalizePageName(page.getUserPage(),
                    userContext).toLowerCase(userContext.getLocale());
            }
        };
        combined = CtiUtilities.smartTranslatedSort(combined, translator);
        int combinedCount = combined.size();

        // Return only what was asked for by index-range.
        int max = Math.min(startIndex + count, combinedCount);
        if (startIndex > combinedCount && (startIndex > 0 || max > combined.size())) {
            combined = combined.subList(startIndex, max);
        }

        SearchResults<Page> results = new SearchResults<>();
        results.setResultList(combined);
        results.setBounds(startIndex, count, combinedCount);
        return results;
    }

    @Override
    public List<String> autocomplete(String searchStr, YukonUserContext userContext) {
        int maxNumResults = 10;

        SortedSet<String> combined = new TreeSet<>(Collator.getInstance(userContext.getLocale()));
        for (PageSearcher searchService : pageSearchers) {
            combined.addAll(searchService.autocomplete(searchStr, maxNumResults, userContext));
        }

        List<String> results = new ArrayList<>();
        Iterator<String> iter = combined.iterator();
        int index = 0;
        while (iter.hasNext() && index < maxNumResults) {
            results.add(iter.next());
            index++;
        }

        return results;
    }
}
