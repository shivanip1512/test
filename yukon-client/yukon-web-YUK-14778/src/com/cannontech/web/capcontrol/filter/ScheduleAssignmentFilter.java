package com.cannontech.web.capcontrol.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.db.pao.PaoScheduleAssignment;

public class ScheduleAssignmentFilter implements UiFilter<PaoScheduleAssignment> {
    public String schedule;
    
    public ScheduleAssignmentFilter(String schedule){
        this.schedule = schedule;
    }
    
    @Override
    public Iterable<PostProcessingFilter<PaoScheduleAssignment>> getPostProcessingFilters() {
        return null;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        List<SqlFilter> retVal = new ArrayList<SqlFilter>(1);
        retVal.add(new SqlFilter(){
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder retVal = new SqlStatementBuilder("s.ScheduleName");
                retVal.eq(schedule);
                return retVal;
            }
        });
        return retVal;
    }
}
