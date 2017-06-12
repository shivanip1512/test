package com.cannontech.web.search.searcher;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightYukonUser;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;

public class UserLuceneSearcher extends AbstractLuceneSearcher<UltraLightYukonUser> implements UserSearcher {
    
    @Override
    public UltraLightYukonUser buildResults(final Document doc) {
        
        int userId = Integer.parseInt(doc.get("userid"));
        
        final UltraLightYukonUser user = new UltraLightYukonUser() {
            
            public String getUserName() {
                return doc.get("user");
            }
            
            public String getUserGroupName() {
                return doc.get("group");
            }
            
            public int getUserId() {
                return userId;
            }
        };
        
        return user;
    }
    
    public SearchResults<UltraLightYukonUser> all(YukonObjectCriteria criteria, int start, int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}