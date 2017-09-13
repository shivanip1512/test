package com.cannontech.web.picker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.dashboard.dao.DashboardDao;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;

public class DashboardUsersPicker extends UserPicker {

    @Autowired private DashboardDao dashboardDao;
    
    @Override
    public YukonObjectCriteria combineCriteria(YukonObjectCriteria criteria, YukonUserContext userContext, String extraArgs) {
        final BooleanQuery.Builder query = new BooleanQuery.Builder().setDisableCoord(false);
        
        if (criteria != null) {
            query.add(criteria.getCriteria(), Occur.MUST);
        }
        List<Integer> userIds = new ArrayList<>();
        userIds = dashboardDao.getAllUsersForDashboard(Integer.parseInt(extraArgs));
        LuceneQueryHelper.buildQueryWithDashboardIDs(query, new HashSet<Integer>(userIds));
        
        return new YukonObjectCriteria() {
            @Override
            public Query getCriteria() {
                return query.build();
            }
        };
    }
    
}
