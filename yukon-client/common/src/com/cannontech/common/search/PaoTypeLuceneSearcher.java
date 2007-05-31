package com.cannontech.common.search;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Sort;


public class PaoTypeLuceneSearcher extends AbstractLuceneSearcher<UltraLightPao> implements PaoTypeSearcher {
    private static final Sort sort = new Sort(new String[]{"category", "type", "paoid"});
    
    public PaoTypeLuceneSearcher() {
    }
    
    @Override
    public UltraLightPao buildResults(final Document doc) {
        final UltraLightPao ultra = new UltraLightPao() {
            public String getPaoName() {
                return doc.get("pao");
            }
            public String getType() {
                return doc.get("type");
            }
            public int getPaoId() {
                return Integer.parseInt(doc.get("paoid"));
            }
        };
        return ultra;
    }

    public SearchResult<UltraLightPao> sameTypePaos(final int currentPaoId, final YukonObjectCriteria criteria, final int start, final int count) {
        final Query query = new TermQuery(new Term("paoid", Integer.toString(currentPaoId)));
        
        try {
            return this.getIndexManager().getSearchTemplate().doCallBackSearch(query, sort, new HitsCallbackHandler<SearchResult<UltraLightPao>>() {
                public SearchResult<UltraLightPao> processHits(Hits hits) throws IOException {
                    if (hits.length() != 1) return SearchResult.emptyResult();
                    Document document = hits.doc(0);
                    String type = document.get("type");
                    Query aQuery = new TermQuery(new Term("type", type));
                    aQuery = compileAndCombine(aQuery, criteria);
                    return doSearch(aQuery, sort, start, count);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public SearchResult<UltraLightPao> allPaos(final YukonObjectCriteria criteria, final int start, final int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, sort, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
