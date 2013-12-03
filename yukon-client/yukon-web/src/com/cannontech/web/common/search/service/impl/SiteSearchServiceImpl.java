package com.cannontech.web.common.search.service.impl;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.search.result.Page;
import com.cannontech.web.common.search.service.PageSearcher;
import com.cannontech.web.common.search.service.SiteSearchService;
import com.cannontech.web.common.userpage.service.UserPageService;
import com.cannontech.web.search.lucene.TopDocsCallbackHandler;
import com.cannontech.web.search.lucene.YukonObjectSearchAnalyzer;
import com.cannontech.web.search.lucene.index.SearchTemplate;
import com.cannontech.web.search.lucene.index.SiteSearchIndexManager;
import com.cannontech.web.search.lucene.index.site.DocumentBuilder;
import com.cannontech.web.support.SiteMapPage;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Component
public class SiteSearchServiceImpl implements SiteSearchService {
    private final Logger log = YukonLogManager.getLogger(SiteSearchServiceImpl.class);

    @Autowired private UserPageService userPageService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private YukonEnergyCompanyService ecService;

    // old
    @Autowired private List<PageSearcher> pageSearchers;
    // new
    @Autowired private SiteSearchIndexManager siteSearchIndexManager;

    private final static Analyzer analyzer = new YukonObjectSearchAnalyzer();

    @Override
    public String sanitizeSearchStr(String searchStr) {
        return searchStr == null ? "" : searchStr.replaceAll("[^\\p{Alnum}]+", " ").trim();
    }

    private static List<String> getListFromDocument(Document document, String prefix) {
        List<String> list = new ArrayList<>();
        int index = 0;
        String fieldValue;
        while ((fieldValue = document.get(prefix + index)) != null) {
            list.add(fieldValue);
            index++;
        }
        return list;
    }

    private static Page buildPageFromDocument(Document document) {
        String pageKey = document.get("pageKey");

        if (pageKey.startsWith("up")) {
            String path = document.get("path");
            String pageName = document.get("pageName");
            List<String> pageArgs = getListFromDocument(document, "pageArg");
            List<String> summaryArgs = new ArrayList<>(pageArgs);
            summaryArgs.addAll(getListFromDocument(document, "summaryArg"));
            String module = document.get("module");
            UserPage userPage = new UserPage(0, path, false, SiteModule.getByName(module), pageName,
                pageArgs, null, null);
            Object[] summaryArgsArray = summaryArgs.toArray(new String[summaryArgs.size()]);
            Page page = new Page(userPage, summaryArgsArray);
            return page;
        }

        Page page = new Page(SiteMapPage.valueOf(pageKey.substring(3)));
        return page;
    }

    @Override
    public SearchResults<Page> search(String searchStr, int startIndex, final int count, YukonUserContext userContext) {
        if (log.isDebugEnabled()) {
            log.debug("searching for [" + searchStr + "], starting at " + startIndex + ", count = " + count);
        }

        if (startIndex + count > 1000) {
            // The caller should limit this to avoid this exception.
            throw new UnsupportedOperationException("search does not support more than 1000 results");
        }

        final LiteYukonUser user = userContext.getYukonUser();
        BooleanQuery query = buildBaseQuery(user);

        BooleanQuery searchStrQuery = new BooleanQuery();
        QueryParser parser = new QueryParser(Version.LUCENE_34, "primarySearch", analyzer);
        try {
            searchStrQuery.add(parser.parse(searchStr), Occur.MUST);
        } catch (ParseException parseException) {
            throw new RuntimeException("could not parse search string " + searchStr, parseException);
        }
        query.add(searchStrQuery, Occur.MUST);

        TopDocsCallbackHandler<SearchResults<Page>> handler = new TopDocsCallbackHandler<SearchResults<Page>>() {
            @Override
            public SearchResults<Page> processHits(TopDocs topDocs, IndexSearcher indexSearcher) throws IOException {
                int stop = Math.min(count, topDocs.totalHits);
                List<Page> list = Lists.newArrayListWithCapacity(stop);

                for (int index = 0; index < stop; ++index) {
                    int docId = topDocs.scoreDocs[index].doc;
                    Document document = indexSearcher.doc(docId);
                    if (siteSearchIndexManager.isAllowedToView(document, user)) {
                        Page page = buildPageFromDocument(document);
                        list.add(page);
                    }
                }

                SearchResults<Page> results = new SearchResults<>();
                results.setResultList(list);
                results.setBounds(0, count, topDocs.totalHits);

                return results;
            }
        };

        SearchTemplate searchTemplate = siteSearchIndexManager.getSearchTemplate(userContext);
        if (log.isTraceEnabled()) {
            log.trace("search - Searching with Lucene query: " + query);
        }
        try {
            SearchResults<Page> results = searchTemplate.doCallBackSearch(query, handler, startIndex + count);
            // TODO:  re-query with bigger count if results were filtered
            return results;
        } catch (IOException e) {
            log.error("error querying for [" + searchStr + "]", e);
            throw new RuntimeException("error querying for [" + searchStr + "]", e);
        }
    }

    /**
     * Start out a Lucene {@link BooleanQuery} for both search and auto-complete that includes sub-queries to limit
     * the results based on user permissions.
     */
    private BooleanQuery buildBaseQuery(LiteYukonUser user) {
        int ecId = ecService.getEnergyCompanyIdByOperator(user);

        BooleanQuery query = new BooleanQuery();
        BooleanQuery ecQuery = new BooleanQuery();
        ecQuery.add(new TermQuery(new Term("energyCompanyId", "none")), Occur.SHOULD);
        // TODO:  should at least in some cases include child energy companies
        ecQuery.add(new TermQuery(new Term("energyCompanyId", Integer.toString(ecId))), Occur.SHOULD);
        query.add(ecQuery, Occur.MUST);

        List<Query> permissionQueries = siteSearchIndexManager.getPermissionQueries(user);
        for (Query permissionQuery : permissionQueries) {
            query.add(permissionQuery, Occur.MUST_NOT);
        }
        return query;
    }

    @Override
    public SearchResults<Page> oldSearch(String searchStr, int startIndex, int count, final YukonUserContext userContext) {
        if (log.isDebugEnabled()) {
            log.debug("searching for [" + searchStr + "], starting at " + startIndex + ", count = " + count);
        }

        if (startIndex + count > 1000) {
            // The caller should limit this to avoid this exception.
            throw new UnsupportedOperationException("search does not support more than 1000 results");
        }

        // Because we can't sort until after all the search results have been compiled, we need to get all results
        // up to and including the page we are on to properly page.  Of course, this will get less efficient as the
        // on the second page than the first, and so on.  (This is why we limit the search to 1000 items.)
        int searchCount = startIndex + count;

        List<Page> combined = new ArrayList<>();
        int combinedCount = 0;
        for (PageSearcher searchService : pageSearchers) {
            SearchResults<Page> results = searchService.search(searchStr, searchCount, userContext);
            combined.addAll(results.getResultList());
            combinedCount += results.getResultCount();
        }

        // Sort on the localized name.
        Function<Page, String> translator = new Function<Page, String>() {
            @Override
            public String apply(Page page) {
                if (page.isBackedBySiteMapPage()) {
                    MessageSourceAccessor messageSourceAccessor =
                            messageSourceResolver.getMessageSourceAccessor(userContext);
                    return messageSourceAccessor.getMessage(page.getSiteMapPage().getFormatKey());
                }
                return userPageService.getLocalizePageTitle(page.getUserPage(),
                    userContext).toLowerCase(userContext.getLocale());
            }
        };
        combined = CtiUtilities.smartTranslatedSort(combined, translator);

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

    /**
     * Find auto-complete results using the given field.  Return true if results were found and stuff those
     * results into the intoResults parameter.
     */
    private boolean autocompleteOn(final String fieldName, String searchStr,
            final LinkedHashSet<String> intoResults, final int maxResults, YukonUserContext userContext) {
        if (log.isTraceEnabled()) {
            log.trace("autocompleteOn(" + fieldName + ", " + searchStr + ", " + intoResults + ", "
                    + maxResults + ", " + userContext + ");");
        }
        final LiteYukonUser user = userContext.getYukonUser();
        BooleanQuery query = buildBaseQuery(user);
        // TODO:  can we make these parsers ahead of time and reuse?
        QueryParser parser = new QueryParser(Version.LUCENE_34, fieldName, analyzer);
        parser.setDefaultOperator(Operator.AND);
        try {
            query.add(parser.parse(searchStr), Occur.MUST);
        } catch (ParseException parseException) {
            throw new IllegalStateException("Error parsing search string " + searchStr, parseException);
        }

        final AtomicInteger numResultsFiltered = new AtomicInteger();
        TopDocsCallbackHandler<Boolean> handler = new TopDocsCallbackHandler<Boolean>() {
            @Override
            public Boolean processHits(TopDocs topDocs, IndexSearcher indexSearcher) throws IOException {
                boolean foundOne = false;
                if (log.isTraceEnabled()) {
                    log.trace("found " + topDocs.totalHits);
                }

                for (int index = 0; index < topDocs.totalHits && intoResults.size() < maxResults; ++index) {
                    int docId = topDocs.scoreDocs[index].doc;
                    Document document = indexSearcher.doc(docId);
                    if (siteSearchIndexManager.isAllowedToView(document, user)) {
                        intoResults.add(document.get(fieldName));
                        foundOne = true;
                    } else {
                        numResultsFiltered.incrementAndGet();
                    }
                }

                return foundOne;
            }
        };
        // TODO:  if there were filtered results (numResultsFiltered), we should do the query again (probably
        // gradually increasing maxResults) until there are enough results or we know there are no matches. 

        SearchTemplate searchTemplate = siteSearchIndexManager.getSearchTemplate(userContext);
        if (log.isTraceEnabled()) {
            log.trace("autocomplete - Searching with Lucene query: " + query);
        }
        try {
            boolean foundOne = searchTemplate.doCallBackSearch(query, handler, maxResults);
            return foundOne;
        } catch (IOException e) {
            log.error("error querying for [" + searchStr + "]", e);
            throw new RuntimeException("error querying for [" + searchStr + "]", e);
        }
    }

    @Override
    public List<String> autocomplete(String searchStr, YukonUserContext userContext) {
        searchStr = sanitizeSearchStr(searchStr);
        if (log.isDebugEnabled()) {
            log.debug("autocompleting [" + searchStr + "]");
        }

        int maxResults = 10;
        LinkedHashSet<String> results = new LinkedHashSet<>();

        // TODO:  Consider combine all the autocompleteOn calls into a single Lucene query with weighting.
        for (int index = 0; index < DocumentBuilder.MAX_PAGE_ARGS && results.size() < maxResults; index++) {
            autocompleteOn("pageArg" + index, searchStr, results, maxResults, userContext);
        }
        for (int index = 0; index < DocumentBuilder.MAX_SUMMARY_ARGS && results.size() < maxResults; index++) {
            autocompleteOn("summaryArg" + index, searchStr, results, maxResults, userContext);
        }

        if (log.isDebugEnabled()) {
            log.debug("found " + results);
        }
        return new ArrayList<String>(results);
    }

    @Override
    public List<String> oldAutocomplete(String searchStr, YukonUserContext userContext) {
        searchStr = sanitizeSearchStr(searchStr);
        if (log.isDebugEnabled()) {
            log.debug("autocompleting [" + searchStr + "]");
        }

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
