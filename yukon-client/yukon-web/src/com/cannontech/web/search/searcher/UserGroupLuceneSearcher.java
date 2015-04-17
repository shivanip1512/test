package com.cannontech.web.search.searcher;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightUserGroup;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;

public class UserGroupLuceneSearcher extends AbstractLuceneSearcher<UltraLightUserGroup> implements UserGroupSearcher {
    
    @Override
    public UltraLightUserGroup buildResults(final Document doc) {
        
        int userGroupId = Integer.parseInt(doc.get("userGroupId"));
        
        final UltraLightUserGroup ultra = new UltraLightUserGroup() {
            
            @Override
            public String getUserGroupName() {
                return doc.get("userGroup");
            }
            
            @Override
            public int getUserGroupId() {
                return userGroupId;
            }
            
            @Override
            public int getUsers() {
                UserGroupDao userGroupDao = YukonSpringHook.getBean(UserGroupDao.class);
                return userGroupDao.getNumberOfUsers(userGroupId);
            }
            
            @Override
            public String getDescription() {
                UserGroupDao userGroupDao = YukonSpringHook.getBean(UserGroupDao.class);
                return userGroupDao.getLiteUserGroup(userGroupId).getUserGroupDescription();
            }
            
        };
        
        return ultra;
    }
    
    public SearchResults<UltraLightUserGroup> all(YukonObjectCriteria criteria, int start, int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}