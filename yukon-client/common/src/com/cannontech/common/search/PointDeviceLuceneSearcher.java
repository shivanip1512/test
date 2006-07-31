package com.cannontech.common.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

import com.cannontech.clientutils.CTILogger;

public class PointDeviceLuceneSearcher implements PointDeviceSearcher {
    
    private File indexLocation;
    private Analyzer analyzer = new PointDeviceSearchAnalyzer();
    private final ReentrantReadWriteLock indexLock = new ReentrantReadWriteLock();
    private IndexSearcher indexSearcher = null;
    
    public PointDeviceLuceneSearcher(File indexLocation) {
        try {
            indexSearcher = new IndexSearcher(indexLocation.getAbsolutePath());
        } catch (IOException e) {
            CTILogger.info("Unable to create lucene searcher, index must not be built yet.");
        }
    }
    
    
    public synchronized void resetIndexSearcher() {
        try {
            if (indexSearcher == null) {
                indexSearcher = new IndexSearcher(indexLocation.getAbsolutePath());
            }
            if (indexSearcher.getIndexReader().isCurrent()) {
                return;
            }
            indexLock.writeLock().lock();
            try {
                indexSearcher.close();
                indexSearcher = new IndexSearcher(indexLocation.getAbsolutePath());
            } finally {
                indexLock.writeLock().unlock();
            }
        } catch (IOException e) {
            CTILogger.error(e);
        }
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
        } catch (Exception e) {
            throw new RuntimeException("Couldn't complete search",e);
        }
    }
    
    public Query compileAndCombine(Query originalQuery, PointDeviceCriteria criteria) {
        if (criteria == null) {
            return originalQuery;
        }
        BooleanQuery unitOfMeasureQuery = new BooleanQuery(false);
        unitOfMeasureQuery.setMinimumNumberShouldMatch(1);
        for (int unitOfMeansure : criteria.getUnitOfMeasureIds()) {
            Query q = new TermQuery(new Term("uomid", Integer.toString(unitOfMeansure)));
            unitOfMeasureQuery.add(q, BooleanClause.Occur.SHOULD);
        }
        BooleanQuery finalQuery = new BooleanQuery(false);
        finalQuery.add(originalQuery, BooleanClause.Occur.MUST);
        finalQuery.add(unitOfMeasureQuery, BooleanClause.Occur.MUST);
        return finalQuery;
    }
    
    private SearchResult<UltraLightPoint> doQuery(Query query, final int start, final int count) throws IOException {
        System.out.println("Query: " + query);
        Hits hits;
        resetIndexSearcher();
        indexLock.readLock().lock();
        try {
            hits = indexSearcher.search(query);
            
            int stop; // 0-based, exclusive bound
            stop = Math.min(start + count, hits.length());
            
            final Iterator hitIter = hits.iterator();
            List<UltraLightPoint> disconnectedCollection = new ArrayList<UltraLightPoint>(count);
            try {
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
            } catch (IOException e) {
                throw new RuntimeException("Can't process iterator", e);
            }
            SearchResult<UltraLightPoint> result = new SearchResult<UltraLightPoint>();
            result.setHitCount(hits.length());
            result.setBounds(start, count);
            result.setResultList(disconnectedCollection);
            return result;
        } finally {
            indexLock.readLock().unlock();
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
            resetIndexSearcher();
            indexLock.readLock().lock();
            try {
                hits = indexSearcher.search(termQuery);
                if (hits.length() != 1) {
                    List<UltraLightPoint> nothing = Collections.emptyList();
                    return SearchResult.emptyResult();
                }
                Document document = hits.doc(0);
                String deviceId = document.get("deviceid");
                TermQuery query = new TermQuery(new Term("deviceid", deviceId));
                queryWithCriteria = compileAndCombine(query, criteria);
            } finally {
                indexLock.readLock().unlock();
            }
            return doQuery(queryWithCriteria, start, count);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't complete search", e);
        }
    }
    
    public SearchResult<UltraLightPoint> allPoints(PointDeviceCriteria criteria, int i, int j) {
        Query query = new MatchAllDocsQuery();
        try {
            Query queryWithCriteria = compileAndCombine(query, criteria);
            return doQuery(queryWithCriteria, 0, -1);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't complete search", e);
        }
    }
    
    public File getIndexLocation() {
        return indexLocation;
    }
    
    
    
}
