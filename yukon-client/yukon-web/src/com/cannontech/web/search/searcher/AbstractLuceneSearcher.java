package com.cannontech.web.search.searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.web.search.lucene.TopDocsCallbackHandler;
import com.cannontech.web.search.lucene.YukonObjectSearchAnalyzer;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;
import com.cannontech.web.search.lucene.index.IndexManager;
import com.google.common.collect.Lists;

public abstract class AbstractLuceneSearcher<E> {
    
    private IndexManager indexManager;
    private final Analyzer analyzer = new YukonObjectSearchAnalyzer();
    
    public abstract E buildResults(Document doc);
    
    public final SearchResults<E> search(String queryString, YukonObjectCriteria criteria) {
        return search(queryString, criteria, 0, -1);
    }
    
    public final SearchResults<E> search(String queryString, YukonObjectCriteria criteria, int start, int count) {
        try {
            Query query = createQuery(queryString, criteria);
            return doSearch(query, start, count);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected final SearchResults<E> doSearch(final Query query, final int start, final int count) throws IOException {
        // We can't use count here because Lucene will just give us the first number of items. Here we are
        // paging the results so we need enough items to populate the current 
        // page. Hence (count + start) instead of just count
        int maxResults = CtiUtilities.addNoOverflow(count, start);
        final SearchResults<E> result =
            getIndexManager().getSearchTemplate().doCallBackSearch(query,
                new TopDocsCallbackHandler<SearchResults<E>>() {
                    
                    @Override
                    public SearchResults<E> processHits(TopDocs topDocs, IndexSearcher indexSearcher)
                            throws IOException {
                                // In version 8.3 totalHits become TotalHits object. The total hit count is now stored in value
                                // field of type long. We're assuming here that we will never get 2,147,483,647 or more hit counts
                                // so converted it to int.
                                int totalHitsInInt = Math.toIntExact(topDocs.totalHits.value);
                        final int stop = Math.min(start + count, totalHitsInInt);
                        final List<E> list = Lists.newArrayListWithCapacity(stop - start);
                        
                        for (int i = start; i < stop; ++i) {
                            int docId = topDocs.scoreDocs[i].doc;
                            Document document = indexSearcher.doc(docId);
                            list.add(buildResults(document));
                        }
                        
                        SearchResults<E> result = new SearchResults<>();
                        result.setBounds(start, count, totalHitsInInt);
                        result.setResultList(list);
                        return result;
                    }
                }, maxResults);
        
        return result;
    }
    
    private Query createQuery(final String queryString, final YukonObjectCriteria criteria) throws ParseException {
        
        String[] terms = queryString.split("\\s+");
        List<String> cleanList = new ArrayList<>();
        for (String s : terms) {
            // clean out lucene wild chars
            s = s.replaceAll("[^a-zA-Z_0-9]", " ");
            if (Pattern.matches("^\\s*$", s)) {
                continue;
            }
            cleanList.add(s);
        }
        
        Query query;
        if (cleanList.isEmpty()) {
            query = new MatchAllDocsQuery();
        } else {
            String newQueryString = StringUtils.join(cleanList.toArray(), " ");
            QueryParser parser = new QueryParser("all", analyzer);
            parser.setDefaultOperator(QueryParser.AND_OPERATOR);
            query = parser.parse(newQueryString);
        }
        
        return compileAndCombine(query, criteria);
    }
    
    protected final Query compileAndCombine(Query originalQuery, YukonObjectCriteria criteria) {
        if (criteria == null) {
            return originalQuery;
        }
        
        Query criteriaQuery = criteria.getCriteria();
        BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
        finalQuery.add(originalQuery, BooleanClause.Occur.MUST);
        finalQuery.add(criteriaQuery, BooleanClause.Occur.MUST);
        return finalQuery.build();
    }
    
    public final IndexManager getIndexManager() {
        return indexManager;
    }
    
    public final void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }
    
}