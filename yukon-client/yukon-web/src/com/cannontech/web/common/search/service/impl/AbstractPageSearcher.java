package com.cannontech.web.common.search.service.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.search.result.Page;
import com.cannontech.web.common.search.service.PageSearcher;
import com.cannontech.web.search.lucene.TopDocsCallbackHandler;
import com.cannontech.web.search.lucene.YukonObjectSearchAnalyzer;
import com.cannontech.web.search.lucene.index.IndexManager;
import com.cannontech.web.search.lucene.index.SearchTemplate;
import com.google.common.collect.Lists;

public abstract class AbstractPageSearcher implements PageSearcher {
    private final Logger log = YukonLogManager.getLogger(AbstractPageSearcher.class);

    protected final static Analyzer analyzer = new YukonObjectSearchAnalyzer();

    protected abstract IndexManager getIndexManager();

    /**
     * If a concrete class has a query that all objects must match, it should override this and return it.  For
     * example, the "required query" for {@link DrPageSearcher}.
     */
    protected Query getRequiredQuery() throws ParseException {
        return null;
    }

    /**
     * Get a list of fields to search.  This is used for both searching and auto-complete.
     */
    protected abstract String[] getSearchFields();

    private Query getSearchQuery(String searchString) throws ParseException {
        BooleanQuery searchQuery = new BooleanQuery(false);

        for (String searchField : getSearchFields()) {
            QueryParser parser = new QueryParser(Version.LUCENE_34, searchField, analyzer);
            searchQuery.add(parser.parse(searchString), Occur.SHOULD);
        }

        return searchQuery;
    }

    /**
     * Build a page given the Lucene document.  This method can return null if the user is not allowed to view
     * the page represented by the Lucene document.
     */
    protected abstract Page buildPage(Document document, LiteYukonUser user);

    @Override
    public SearchResults<Page> search(String searchString, final int count, YukonUserContext userContext) {
        Query query;
        try {
            Query requiredQuery = getRequiredQuery();
            Query searchQuery = getSearchQuery(searchString);
            if (requiredQuery == null) {
                query = searchQuery;
            } else {
                BooleanQuery booleanQuery = new BooleanQuery();
                booleanQuery.add(requiredQuery, Occur.MUST);
                booleanQuery.add(searchQuery, Occur.MUST);
                query = booleanQuery;
            }
        } catch (ParseException parseException) {
            throw new IllegalArgumentException("could not parse query for searchString [" + searchString + "]",
                parseException);
        }

        final LiteYukonUser user = userContext.getYukonUser();
        SearchTemplate searchTemplate = getIndexManager().getSearchTemplate();
        TopDocsCallbackHandler<SearchResults<Page>> handler = new TopDocsCallbackHandler<SearchResults<Page>>() {
            @Override
            public SearchResults<Page> processHits(TopDocs topDocs, IndexSearcher indexSearcher) throws IOException {
                int stop = Math.min(count, topDocs.totalHits);
                List<Page> list = Lists.newArrayListWithCapacity(stop);

                for (int index = 0; index < stop; ++index) {
                    int docId = topDocs.scoreDocs[index].doc;
                    Document document = indexSearcher.doc(docId);
                    Page page = buildPage(document, user);
                    if (page != null) {
                        list.add(page);
                    }
                }

                SearchResults<Page> results = new SearchResults<>();
                results.setResultList(list);
                results.setBounds(0, count, topDocs.totalHits);

                return results;
            }
        };

        try {
            SearchResults<Page> results = searchTemplate.doCallBackSearch(query, handler);
            return results;
        } catch (IOException ioe) {
            log.error("IOException searching Lucene index", ioe);
            throw new RuntimeException(ioe);
        }
    }

    private Set<String> autocomplete(String searchString, final int count, final String columnToMatch)
            throws IOException {
        Query query;
        try {
            Query requiredQuery = getRequiredQuery();
            QueryParser parser = new QueryParser(Version.LUCENE_34, columnToMatch, analyzer);
            parser.setDefaultOperator(Operator.AND);
            Query searchQuery = parser.parse(searchString);
            if (requiredQuery == null) {
                query = searchQuery;
            } else {
                BooleanQuery booleanQuery = new BooleanQuery();
                booleanQuery.add(requiredQuery, Occur.MUST);
                booleanQuery.add(searchQuery, Occur.MUST);
                query = booleanQuery;
            }
        } catch (ParseException parseException) {
            throw new IllegalArgumentException("could not parse query for searchString [" + searchString + "]",
                parseException);
        }

        SearchTemplate searchTemplate = getIndexManager().getSearchTemplate();
        TopDocsCallbackHandler<Set<String>> handler = new TopDocsCallbackHandler<Set<String>>() {
            @Override
            public Set<String> processHits(TopDocs topDocs, IndexSearcher indexSearcher) throws IOException {
                int stop = Math.min(count, topDocs.totalHits);
                Set<String> matches = new HashSet<>();

                for (int index = 0; index < stop; ++index) {
                    int docId = topDocs.scoreDocs[index].doc;
                    Document document = indexSearcher.doc(docId);
                    String match = document.get(columnToMatch);
                    matches.add(match);
                }

                return matches;
            }
        };
        return searchTemplate.doCallBackSearch(query, handler);
    }

    @Override
    public Set<String> autocomplete(String searchString, int count, YukonUserContext userContext) {
        try {
            Set<String> matches = new HashSet<>();
            for (String autocompleteField : getSearchFields()) {
                matches.addAll(autocomplete(searchString, count, autocompleteField));
            }
            return matches;
        } catch (IOException ioe) {
            log.error("IOException searching Lucene index", ioe);
            throw new RuntimeException(ioe);
        }
    }
}
