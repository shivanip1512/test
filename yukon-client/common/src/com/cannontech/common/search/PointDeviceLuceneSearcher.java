package com.cannontech.common.search;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;

public class PointDeviceLuceneSearcher extends AbstractLuceneSearcher<UltraLightPoint> implements PointDeviceSearcher {
    private static final Sort sort = new Sort(new String[]{"pointName", "pointid"});
    
    public PointDeviceLuceneSearcher() {
    }

    @Override
    public UltraLightPoint buildResults(final Document doc) {
        final UltraLightPoint ultra = new UltraLightPoint() {
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
        return ultra;
    }

    public SearchResult<UltraLightPoint> sameDevicePoints(final int currentPointId, final YukonObjectCriteria criteria, final int start, final int count) {
        final Query query = new TermQuery(new Term("pointid", Integer.toString(currentPointId)));

        try {
            return doCallBackSearch(query, sort, new HitsCallbackHandler<SearchResult<UltraLightPoint>>() {
                public SearchResult<UltraLightPoint> processHits(Hits hits) throws IOException {
                    if (hits.length() != 1) return SearchResult.emptyResult();
                    Document document = hits.doc(0);
                    String deviceId = document.get("deviceid");
                    Query aQuery = new TermQuery(new Term("deviceid", deviceId));
                    aQuery = compileAndCombine(aQuery, criteria);
                    return doSearch(aQuery, sort, start, count);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public SearchResult<UltraLightPoint> allPoints(YukonObjectCriteria criteria, int start, int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, sort, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
