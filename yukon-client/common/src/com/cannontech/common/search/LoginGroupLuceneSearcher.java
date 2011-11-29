package com.cannontech.common.search;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

public class LoginGroupLuceneSearcher extends AbstractLuceneSearcher<UltraLightLoginGroup> implements LoginGroupSearcher {
    private SortField sortList[] = 
        {new SortField("groupName", SortField.STRING),
         new SortField("groupid", SortField.INT)};
    private final Sort sort = new Sort(sortList);
    
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

    public SearchResult<UltraLightLoginGroup> all(YukonObjectCriteria criteria,
            int start, int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, sort, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
