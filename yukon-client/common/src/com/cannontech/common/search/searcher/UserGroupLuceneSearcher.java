package com.cannontech.common.search.searcher;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import com.cannontech.common.search.criteria.YukonObjectCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightUserGroup;

public class UserGroupLuceneSearcher extends AbstractLuceneSearcher<UltraLightUserGroup> implements UserGroupSearcher {
    
    @Override
    public UltraLightUserGroup buildResults(final Document doc) {
        final UltraLightUserGroup ultra = new UltraLightUserGroup() {
            public String getUserGroupName() {
                return doc.get("userGroup");
            }
            public int getUserGroupId() {
                return Integer.parseInt(doc.get("userGroupId"));
            }
        };
        return ultra;
    }

    public SearchResults<UltraLightUserGroup> all(YukonObjectCriteria criteria,
            int start, int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
