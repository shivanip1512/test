package com.cannontech.common.search.searcher;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import com.cannontech.common.search.criteria.YukonObjectCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightLoginGroup;

public class LoginGroupLuceneSearcher extends AbstractLuceneSearcher<UltraLightLoginGroup> implements LoginGroupSearcher {
    
    @Override
    public UltraLightLoginGroup buildResults(final Document doc) {
        final UltraLightLoginGroup ultra = new UltraLightLoginGroup() {
            public String getGroupName() {
                return doc.get("group");
            }
            public int getGroupId() {
                return Integer.parseInt(doc.get("groupid"));
            }
        };
        return ultra;
    }

    public SearchResults<UltraLightLoginGroup> all(YukonObjectCriteria criteria,
            int start, int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
