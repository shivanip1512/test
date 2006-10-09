package com.cannontech.common.search;

import java.io.File;
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

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.search.index.IndexManager;

public class PointDeviceLuceneSearcher implements PointDeviceSearcher {

    private static final String INDEX_BUILDING_ERROR = "This index is currently being created. Please try again later.";

    private File indexLocation;
    private Analyzer analyzer = new PointDeviceSearchAnalyzer();
    private final ReentrantReadWriteLock indexLock = new ReentrantReadWriteLock();
    private IndexSearcher indexSearcher = null;
    private IndexManager manager = null;

    public PointDeviceLuceneSearcher() {
        }
    
    public SearchResult<UltraLightPoint> search(String queryString, PointDeviceCriteria criteria, final int start, final int count) {
        try {
            if (isIndexBuilding()) {
                throw new RuntimeException(INDEX_BUILDING_ERROR);
            }
            // fix the query a little bit...
            String[] terms = queryString.split("\\s+");
            String newQueryString = StringUtils.join(terms, " ");
            QueryParser parser = new QueryParser("all", analyzer);
            parser.setDefaultOperator(QueryParser.AND_OPERATOR);
            Query query = parser.parse(newQueryString);
            Query queryWithCriteria = compileAndCombine(query, criteria);
            return doQuery(queryWithCriteria, start, count);
        } catch (Exception e) {
            CTILogger.error(e);
            throw new RuntimeException("Couldn't complete search");
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
        indexSearcher = new IndexSearcher(indexLocation.getAbsolutePath());
        indexLock.readLock().lock();
        try {
            hits = indexSearcher.search(query);
            
            int stop; // 0-based, exclusive bound
            stop = Math.min(start + count, hits.length());
            
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
                CTILogger.error(e);
                throw new RuntimeException("Can't process results");
            }
            SearchResult<UltraLightPoint> result = new SearchResult<UltraLightPoint>();
            result.setBounds(start, count, hits.length());
            result.setResultList(disconnectedCollection);
            return result;
        } finally {
            indexLock.readLock().unlock();
            indexSearcher.close();
        }
    }
    
    public SearchResult<UltraLightPoint> search(String queryString, PointDeviceCriteria criteria) {
        return search(queryString, criteria, 0, -1);
    }
    
    public SearchResult<UltraLightPoint> sameDevicePoints(int currentPointId, PointDeviceCriteria criteria, int start, int count) {
        try {
            if (isIndexBuilding()) {
                throw new RuntimeException(INDEX_BUILDING_ERROR);
            }
            Hits hits;
            Query queryWithCriteria;
            TermQuery termQuery = new TermQuery(new Term("pointid", Integer.toString(currentPointId)));
            indexSearcher = new IndexSearcher(indexLocation.getAbsolutePath());
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
            CTILogger.error(e);
            throw new RuntimeException("Couldn't complete search");
        }
    }
    
    public SearchResult<UltraLightPoint> allPoints(PointDeviceCriteria criteria, int i, int j) {
        Query query = new MatchAllDocsQuery();
        try {
            if (isIndexBuilding()) {
                throw new RuntimeException(INDEX_BUILDING_ERROR);
            }
            Query queryWithCriteria = compileAndCombine(query, criteria);
            return doQuery(queryWithCriteria, i, j);
        } catch (IOException e) {
            CTILogger.error(e);
            throw new RuntimeException("Couldn't complete search");
        }
    }
    
    public File getIndexLocation() {
        return indexLocation;
    }
    
    public void setIndexLocation(File indexLocation) {
        this.indexLocation = indexLocation;
    }

    public void setIndexManager(IndexManager manager) {
        this.manager = manager;
        this.setIndexLocation(manager.getIndexLocation());
    }

    public boolean isIndexBuilding() {
        return this.manager.isBuilding();
    }

}
