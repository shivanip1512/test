package com.cannontech.common.search;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.user.UserUtils;

public class UserLuceneSearcher extends AbstractLuceneSearcher<UltraLightYukonUser> implements UserSearcher {

    @Override
    public UltraLightYukonUser buildResults(final Document doc) {
        final UltraLightYukonUser ultra = new UltraLightYukonUser() {
            public String getUserName() {
                return doc.get("user");
            }
            /*groupName is not searchable; it is only for display purposes*/
            public String getGroupName() {
                return getGroupNamesFromUser((Integer.parseInt(doc.get("userid"))));
            }
            public int getUserId() {
                return Integer.parseInt(doc.get("userid"));
            }
        };
        return ultra;
    }

    public SearchResult<UltraLightYukonUser> all(YukonObjectCriteria criteria,
            int start, int count) {
        try {
            final Query query = compileAndCombine(new MatchAllDocsQuery(), criteria);
            return doSearch(query, start, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getGroupNamesFromUser(int userid) {
        List<LiteYukonGroup> groups = DaoFactory.getYukonGroupDao().getGroupsForUser(userid);
        if (groups.size() == 0) { 
            return "";
        }
        StringBuilder groupNames = new StringBuilder(groups.get(0).getGroupName());
        
        for (int j = 1; j < groups.size(); j++) {
            if (groups.get(j).getGroupID() == UserUtils.USER_ADMIN_ID) continue;  
            groupNames.append(", " + groups.get(j).getGroupName());
        }
        return groupNames.toString();
    }
}
