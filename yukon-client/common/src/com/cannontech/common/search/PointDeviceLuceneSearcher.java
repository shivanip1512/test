package com.cannontech.common.search;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

public class PointDeviceLuceneSearcher extends AbstractLuceneSearcher<UltraLightPoint> implements PointDeviceSearcher {
    private SortField sortList[] = 
        {new SortField("pointName", SortField.STRING),
         new SortField("pointid", SortField.INT)};
    private final Sort sort = new Sort(sortList);
    
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
        };
        return ultra;
    }

    public SearchResult<UltraLightPoint> sameDevicePoints(final int currentPointId, final YukonObjectCriteria criteria, final int start, final int count) {
        final Query query = new TermQuery(new Term("pointid", Integer.toString(currentPointId)));

        try {
            return this.getIndexManager().getSearchTemplate().doCallBackSearch(query, sort, new TopDocsCallbackHandler<SearchResult<UltraLightPoint>>() {
                public SearchResult<UltraLightPoint> processHits(TopDocs hits, IndexSearcher indexSearcher) throws IOException {
                    if (hits.totalHits != 1) {
                        return SearchResult.emptyResult();
                    }
                    
                    int docId = hits.scoreDocs[0].doc;
                    Document document = indexSearcher.doc(docId);
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
    
    public SearchResult<UltraLightPoint> all(YukonObjectCriteria criteria,
            int start, int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, sort, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
