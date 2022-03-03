package com.cannontech.web.picker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;

public class DeviceActionsRoleUserPicker extends UserPicker{
    @Autowired private YukonUserDao userDao;

    @Override
    public YukonObjectCriteria combineCriteria(YukonObjectCriteria criteria, YukonUserContext userContext,
            String extraArgs) {
        final BooleanQuery.Builder query = new BooleanQuery.Builder();

        if (criteria != null) {
            query.add(criteria.getCriteria(), Occur.MUST);
        }
        List<String> userGroupNames = new ArrayList<>();
        userGroupNames = userDao.getDeviceActionsRoleUserGroups();

        LuceneQueryHelper.buildQueryWithUserGroups(query, new HashSet<String>(userGroupNames));

        return new YukonObjectCriteria() {
            @Override
            public Query getCriteria() {
                return query.build();
            }
        };
    }
    
}
