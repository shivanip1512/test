package com.cannontech.web.capcontrol.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.capcontrol.ScheduleCommand;
import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.db.pao.PaoScheduleAssignment;

public class ScheduleAssignmentCommandFilter implements
        UiFilter<PaoScheduleAssignment> {
    
    private ScheduleCommand command;
    
    public ScheduleAssignmentCommandFilter(ScheduleCommand command){
        this.command = command;
    }
    
    @Override
    public List<PostProcessingFilter<PaoScheduleAssignment>> getPostProcessingFilters() {
        return null;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        List<SqlFilter> retVal = new ArrayList<SqlFilter>(1);
        retVal.add(new SqlFilter(){
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                //must compare the first part of the command, because the tail end 
                //of VerifyNotOperatedIn command is variable, depending on what 
                //min/hr/day/wk values are
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append("sa.Command").startsWith(command.getCommandName());
                return retVal;
            }
        });
        
        return retVal;
    }

}
