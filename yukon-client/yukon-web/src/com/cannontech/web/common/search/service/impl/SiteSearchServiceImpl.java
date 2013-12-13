package com.cannontech.web.common.search.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.EnergyCompanyNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.search.result.Page;
import com.cannontech.web.common.search.service.SiteSearchService;
import com.cannontech.web.search.lucene.TopDocsCallbackHandler;
import com.cannontech.web.search.lucene.YukonObjectSearchAnalyzer;
import com.cannontech.web.search.lucene.index.SearchTemplate;
import com.cannontech.web.search.lucene.index.SiteSearchIndexManager;
import com.cannontech.web.search.lucene.index.site.DocumentBuilder;

@Component
public class SiteSearchServiceImpl implements SiteSearchService {
    private final Logger log = YukonLogManager.getLogger(SiteSearchServiceImpl.class);

    @Autowired private YukonEnergyCompanyService ecService;
    @Autowired private SiteSearchIndexManager siteSearchIndexManager;
    @Autowired private RolePropertyDao rolePropertyDao;;

    private final static Analyzer analyzer = new YukonObjectSearchAnalyzer();

    @Override
    public String sanitizeSearchStr(String searchStr) {
        return searchStr == null ? "" : searchStr.replaceAll("[^\\p{Alnum}]+", " ").trim();
    }

    /**
     * This class is made to be called multiple times, until it returns true (which means that we either found
     * the number of documents requested (number of items per page) or we found all possible matching documents.
     * 
     * It works this way because we aren't able to filter some documents out based on permissions until after
     * we match them.  (We need to use complicated service calls to check permissions.) 
     */
    private class DocumentHandler implements TopDocsCallbackHandler<Boolean> {
        final int startIndex;
        final int numWanted;
        final LiteYukonUser user;

        // How many times this instance has been used.
        int iteration = 0;
        int numToQuery;
        List<Page> matches = new ArrayList<>();

        DocumentHandler(int startIndex, int numWanted, LiteYukonUser user) {
            this.startIndex = startIndex;
            this.numWanted = numWanted;
            this.user = user;
            // The first iteration, we'll query a few extra to avoid a second iteration if possible.  300 should
            // be large enough for any site map pages and DR pages for _most_ customers that would be weeded out.
            numToQuery = startIndex + numWanted + 300;
        }

        // Index into results...only reset this the first time we are used so we don't re-check.
        int index = 0; // index into topDocs
        int numFoundAllPages = 0; // number of documents matched, including those on previous pages
        int numFoundCurrentPage = 0; // number of documents matched on the desired page (after startIndex)
        boolean searchExhausted = false; // true when we've exhausted the entire search
        int numDisallowed = 0;
        int totalHits;

        @Override
        public Boolean processHits(TopDocs topDocs, IndexSearcher indexSearcher) throws IOException {
            int stop = Math.min(numToQuery, topDocs.totalHits);
            for (; numFoundCurrentPage < numWanted && index < stop; ++index) {
                int docId = topDocs.scoreDocs[index].doc;
                Document document = indexSearcher.doc(docId);
                Page page = siteSearchIndexManager.buildPageFromDocument(document, user);
                if (page != null) {
                    numFoundAllPages++;
                    if (numFoundAllPages > startIndex) {
                        matches.add(page);
                        numFoundCurrentPage++;
                    }
                    if (numFoundCurrentPage == numWanted) {
                        break;
                    }
                } else {
                    numDisallowed++;
                }
            }

            totalHits = topDocs.totalHits - numDisallowed;

            // We know we've exhausted the search if we got fewer hits than we asked for.  We're assuming here
            // that we will never get 2,147,483,647 or more results.
            searchExhausted = topDocs.totalHits < numToQuery;
            iteration++;
            // The first time we tried startIndex + numWanted + 300
            // second we will try (startIndex + numWanted) * 3 + 300
            // third try we will just use Integer.MAX_VALUE
            if (iteration == 1) {
                numToQuery = (startIndex + numWanted) * 3 + 300;
            } else {
                numToQuery = Integer.MAX_VALUE;
            }
            return searchExhausted || numFoundCurrentPage == numWanted;
        }

        SearchResults<Page> getSearchResults() {
            SearchResults<Page> results = new SearchResults<>();
            results.setResultList(matches);
            results.setBounds(0, numWanted, totalHits - numDisallowed);
            return results;
        }
    }

    @Override
    public SearchResults<Page> search(String searchStr, int startIndex, int numWanted, YukonUserContext userContext) {
        if (log.isDebugEnabled()) {
            log.debug("searching for [" + searchStr + "], starting at " + startIndex + ", count = " + numWanted);
        }

        if (startIndex + numWanted > 1000) {
            // The caller should limit this to avoid this exception.
            throw new UnsupportedOperationException("search does not support more than 1000 results");
        }

        LiteYukonUser user = userContext.getYukonUser();
        BooleanQuery query = buildBaseQuery(user);

        BooleanQuery searchStrQuery = new BooleanQuery();
        QueryParser parser = new QueryParser(Version.LUCENE_34, "primarySearch", analyzer);
        try {
            searchStrQuery.add(parser.parse(searchStr), Occur.MUST);
        } catch (ParseException parseException) {
            throw new RuntimeException("could not parse search string " + searchStr, parseException);
        }
        query.add(searchStrQuery, Occur.MUST);

        DocumentHandler handler = new DocumentHandler(startIndex, numWanted, user);

        SearchTemplate searchTemplate = siteSearchIndexManager.getSearchTemplate(userContext);
        if (log.isTraceEnabled()) {
            log.trace("search - Searching with Lucene query: " + query);
        }
        try {
            do {
                if (log.isDebugEnabled()) {
                    log.debug("searching, for up to " + handler.numToQuery + " documents");
                }
            } while (!searchTemplate.doCallBackSearch(query, handler, handler.numToQuery));
             SearchResults<Page> results = handler.getSearchResults();
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
        BooleanQuery query = new BooleanQuery();
        Query noEcQuery = new TermQuery(new Term("energyCompanyId", "none"));
        try {
            int ecId = ecService.getEnergyCompanyIdByOperator(user);
            BooleanQuery ecQuery = new BooleanQuery();
            ecQuery.add(noEcQuery, Occur.SHOULD);
            ecQuery.add(new TermQuery(new Term("energyCompanyId", Integer.toString(ecId))), Occur.SHOULD);
            boolean searchChildEcs = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, user);
            if (searchChildEcs) {
                List<Integer> childEcIds = ecService.getChildEnergyCompanies(ecId);
                for (Integer childEcId : childEcIds) {
                    ecQuery.add(new TermQuery(new Term("energyCompanyId", Integer.toString(childEcId))), Occur.SHOULD);
                }
            }
            query.add(ecQuery, Occur.MUST);
        } catch (EnergyCompanyNotFoundException ecnfe) {
            // Operator is not part of an energy company.
            query.add(noEcQuery, Occur.MUST);
        }

        List<Query> permissionQueries = siteSearchIndexManager.getPermissionQueries(user);
        for (Query permissionQuery : permissionQueries) {
            query.add(permissionQuery, Occur.MUST_NOT);
        }
        return query;
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
        BooleanQuery baseQuery = buildBaseQuery(user);
        QueryParser parser = new QueryParser(Version.LUCENE_34, fieldName, analyzer);
        parser.setDefaultOperator(Operator.AND);
        try {
            baseQuery.add(parser.parse(searchStr), Occur.MUST);
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

                int stop = Math.min(maxResults, topDocs.totalHits);
                for (int index = 0; index < stop && intoResults.size() < maxResults; ++index) {
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
            log.trace("autocomplete - Searching with Lucene query: " + baseQuery);
        }
        try {
            boolean foundOne = searchTemplate.doCallBackSearch(baseQuery, handler, maxResults);
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
}
