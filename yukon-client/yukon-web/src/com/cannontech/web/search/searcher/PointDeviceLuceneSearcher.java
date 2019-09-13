package com.cannontech.web.search.searcher;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightPoint;
import com.cannontech.web.search.lucene.TopDocsCallbackHandler;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;

public class PointDeviceLuceneSearcher extends AbstractLuceneSearcher<UltraLightPoint> implements PointDeviceSearcher {
    
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

            public String getPointType() {
                return doc.get("pointtype");
            }
            public String getCategory() {
                return doc.get("category");
            }
        };
        return ultra;
    }

    public SearchResults<UltraLightPoint> sameDevicePoints(final int currentPointId, final YukonObjectCriteria criteria, final int start, final int count) {
        final Query query = new TermQuery(new Term("pointid", Integer.toString(currentPointId)));

        try {
            return this.getIndexManager().getSearchTemplate().doCallBackSearch(query, new TopDocsCallbackHandler<SearchResults<UltraLightPoint>>() {
                public SearchResults<UltraLightPoint> processHits(TopDocs hits, IndexSearcher indexSearcher) throws IOException {
                    if (hits.totalHits != 1) {
                        return SearchResults.emptyResult();
                    }
                    
                    int docId = hits.scoreDocs[0].doc;
                    Document document = indexSearcher.doc(docId);
                    String deviceId = document.get("deviceid");
                    Query aQuery = new TermQuery(new Term("deviceid", deviceId));
                    aQuery = compileAndCombine(aQuery, criteria);
                    return doSearch(aQuery, start, count);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public SearchResults<UltraLightPoint> all(YukonObjectCriteria criteria,
            int start, int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
