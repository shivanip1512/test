package com.cannontech.common.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.cannontech.common.search.index.IndexManager;

public class PointDeviceLuceneSearcher implements PointDeviceSearcher {

    private Analyzer analyzer = new PointDeviceSearchAnalyzer();
    private final ReentrantReadWriteLock indexLock = new ReentrantReadWriteLock();
    private IndexManager manager = null;

    public PointDeviceLuceneSearcher() {
        }
    
    public SearchResult<UltraLightPoint> search(String queryString, PointDeviceCriteria criteria, final int start, final int count) {
        try {
            // fix the query a little bit...
            String[] terms = queryString.split("\\s+");
            String newQueryString = StringUtils.join(terms, " ");
            QueryParser parser = new QueryParser("all", analyzer);
            parser.setDefaultOperator(QueryParser.AND_OPERATOR);
            Query query = parser.parse(newQueryString);
            Query queryWithCriteria = compileAndCombine(query, criteria);
            return doQuery(queryWithCriteria, start, count);
        } catch (RuntimeException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public Query compileAndCombine(Query originalQuery, PointDeviceCriteria criteria) {
        if (criteria == null) {
            return originalQuery;
        }
        Query criteriaQuery = criteria.getCriteria();
        
        BooleanQuery finalQuery = new BooleanQuery(false);
        finalQuery.add(originalQuery, BooleanClause.Occur.MUST);
        finalQuery.add(criteriaQuery, BooleanClause.Occur.MUST);
        return finalQuery;
    }
    
    private SearchResult<UltraLightPoint> doQuery(Query query, final int start, final int count) throws IOException {
        Hits hits;
        IndexSearcher indexSearcher = this.manager.getIndexSearcher();
        indexLock.readLock().lock();
        try {
            hits = indexSearcher.search(query);
            
            int stop; // 0-based, exclusive bound
            stop = Math.min(start + count, hits.length());
            
            List<UltraLightPoint> disconnectedCollection = new ArrayList<UltraLightPoint>(count);
            for (int i = start; i < stop; ++i) {
                final Document doc = hits.doc(i);
                // a Document does not hold a referrence to an IndexReader so it is okay
                // to hold a reference to one here
                UltraLightPoint ultra = new UltraLightPoint() {
                    public String getPointName() {
                        return doc.get("point");
                    }
                    public String getDeviceName() {
                        return doc.get("device");
                    }
                    public int getPointId() {
                        return Integer.parseInt(doc.get("pointid"));
                    }
                    public int getDeviceId() {
                        return Integer.parseInt(doc.get("deviceid"));
                    }
                    
                };
                disconnectedCollection.add(ultra);
            }
            SearchResult<UltraLightPoint> result = new SearchResult<UltraLightPoint>();
            result.setBounds(start, count, hits.length());
            result.setResultList(disconnectedCollection);
            return result;
        } finally {
            indexLock.readLock().unlock();
            try {
                indexSearcher.close();
            } catch (IOException e) {
                // do nothing - tried to close
            }
        }
    }
    
    public SearchResult<UltraLightPoint> search(String queryString, PointDeviceCriteria criteria) {
        return search(queryString, criteria, 0, -1);
    }
    
    public SearchResult<UltraLightPoint> sameDevicePoints(int currentPointId, PointDeviceCriteria criteria, int start, int count) {
        try {
            Hits hits;
            Query queryWithCriteria;
            TermQuery termQuery = new TermQuery(new Term("pointid", Integer.toString(currentPointId)));
            IndexSearcher indexSearcher = this.manager.getIndexSearcher();
            indexLock.readLock().lock();
            try {
                hits = indexSearcher.search(termQuery);
                if (hits.length() != 1) {
                    return SearchResult.emptyResult();
                }
                Document document = hits.doc(0);
                String deviceId = document.get("deviceid");
                TermQuery query = new TermQuery(new Term("deviceid", deviceId));
                queryWithCriteria = compileAndCombine(query, criteria);
            } finally {
                indexLock.readLock().unlock();
                indexSearcher.close();
            }
            return doQuery(queryWithCriteria, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public SearchResult<UltraLightPoint> allPoints(PointDeviceCriteria criteria, int i, int j) {
        Query query = new MatchAllDocsQuery();
        try {
            Query queryWithCriteria = compileAndCombine(query, criteria);
            return doQuery(queryWithCriteria, i, j);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setIndexManager(IndexManager manager) {
        this.manager = manager;
    }

}
