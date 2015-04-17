package com.cannontech.web.search.searcher;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightLoginGroup;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;

public class LoginGroupLuceneSearcher extends AbstractLuceneSearcher<UltraLightLoginGroup> implements LoginGroupSearcher {
    
    @Override
    public UltraLightLoginGroup buildResults(final Document doc) {
        
        int groupId = Integer.parseInt(doc.get("groupid"));
        
        final UltraLightLoginGroup ultra = new UltraLightLoginGroup() {
            
            @Override
            public String getGroupName() {
                return doc.get("group");
            }
            
            @Override
            public int getGroupId() {
                return groupId;
            }
            
            @Override
            public String getDescription() {
                YukonGroupDao yukonGroupDao = YukonSpringHook.getBean(YukonGroupDao.class);
                return yukonGroupDao.getLiteYukonGroup(groupId).getGroupDescription();
            }
        };
        
        return ultra;
    }
    
    public SearchResults<UltraLightLoginGroup> all(YukonObjectCriteria criteria, int start, int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}