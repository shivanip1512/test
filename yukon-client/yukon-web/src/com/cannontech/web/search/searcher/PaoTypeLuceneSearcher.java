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
import com.cannontech.common.search.result.UltraLightPao;
import com.cannontech.web.search.lucene.TopDocsCallbackHandler;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;


public class PaoTypeLuceneSearcher extends AbstractLuceneSearcher<UltraLightPao> implements PaoTypeSearcher {

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

    public SearchResults<UltraLightPao> sameTypePaos(final int currentPaoId, final YukonObjectCriteria criteria, final int start, final int count) {
        final Query query = new TermQuery(new Term("paoid", Integer.toString(currentPaoId)));
        
        try {
            return this.getIndexManager().getSearchTemplate().doCallBackSearch(query, new TopDocsCallbackHandler<SearchResults<UltraLightPao>>() {
                public SearchResults<UltraLightPao> processHits(TopDocs topDocs, IndexSearcher indexSearcher) throws IOException {
                    if (topDocs.totalHits != 1) {
                        return SearchResults.emptyResult();
                    }
                    
                    int docId = topDocs.scoreDocs[0].doc;
                    Document document = indexSearcher.doc(docId);
                    String type = document.get("type");
                    Query aQuery = new TermQuery(new Term("type", type));
                    aQuery = compileAndCombine(aQuery, criteria);
                    return doSearch(aQuery, start, count);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SearchResults<UltraLightPao> all(final YukonObjectCriteria criteria,
            final int start, final int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
