/**
 * 
 */
package com.cannontech.common.search;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import com.cannontech.common.search.index.IndexManager;
import com.google.common.collect.Lists;

/**
 * @author nmeverden
 *
 */
public abstract class AbstractLuceneSearcher<E> {
    private IndexManager indexManager;
    private final Analyzer analyzer = new YukonObjectSearchAnalyzer();
    
    public abstract E buildResults(Document doc);
    
    public final SearchResult<E> search(String queryString, YukonObjectCriteria criteria) {
        return search(queryString, criteria, 0, -1);
    }
    public final SearchResult<E> search(String queryString, YukonObjectCriteria criteria, int start, int count) {
        try { 
            Query query = createQuery(queryString, criteria);
            return doSearch(query, null, start, count);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected final SearchResult<E> doSearch(final Query query, final Sort sort, final int start, final int count) throws IOException {
        final Sort aSort = (sort == null) ? new Sort() : sort;
        final SearchResult<E> result = getIndexManager().getSearchTemplate().doCallBackSearch(query, aSort, new TopDocsCallbackHandler<SearchResult<E>>() {
            
            @Override
            public SearchResult<E> processHits(TopDocs topDocs, IndexSearcher indexSearcher) throws IOException {
                final int stop = Math.min(start + count, topDocs.totalHits);
                final List<E> list = Lists.newArrayListWithCapacity(stop - start);

                for (int i = start; i < stop; ++i) {
                    int docId = topDocs.scoreDocs[i].doc;
                    Document document = indexSearcher.doc(docId);
                    list.add(buildResults(document));
                }

                SearchResult<E> result = new SearchResult<E>();
                result.setBounds(start, count, topDocs.totalHits);
                result.setResultList(list);
                return result;
            }
        });

        return result;
    }

    private Query createQuery(final String queryString, final YukonObjectCriteria criteria) 
        throws ParseException {

        String[] terms = queryString.split("\\s+");
        List<String> cleanList = new java.util.ArrayList<String>();
        for (String s : terms) {
            //clean out lucene wild chars
            s = s.replaceAll("[^a-zA-Z_0-9]", " ");
            if (java.util.regex.Pattern.matches("^\\s*$", s)) continue;
            cleanList.add(s);
        }

        Query query;
        if (cleanList.isEmpty()) {
            query = new MatchAllDocsQuery();
        } else {
            String newQueryString = StringUtils.join(cleanList.toArray(), " ");
            QueryParser parser = new QueryParser(Version.LUCENE_34, "all", analyzer);
            parser.setDefaultOperator(QueryParser.AND_OPERATOR);
            query = parser.parse(newQueryString);
        }
        
        return compileAndCombine(query, criteria);
    }

    protected final Query compileAndCombine(Query originalQuery, YukonObjectCriteria criteria) {
        if (criteria == null) return originalQuery;
            
        Query criteriaQuery = criteria.getCriteria();
        BooleanQuery finalQuery = new BooleanQuery(false);
        finalQuery.add(originalQuery, BooleanClause.Occur.MUST);
        finalQuery.add(criteriaQuery, BooleanClause.Occur.MUST);
        return finalQuery;
    }
    
    public final IndexManager getIndexManager() {
        return indexManager;
    }

    public final void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }
}
