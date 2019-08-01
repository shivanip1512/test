package com.cannontech.web.picker;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.service.CbcHelperService;
import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class CapControlCBCOrphanPicker extends DatabasePicker<Map<String, Object>> {
    private String selectedCBCId;
    private final static String[] searchColumnNames = new String[] { "paoName", "type", "pointName" };

    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        columns.add(new OutputColumn("paoName", "yukon.web.picker.pao.name"));
        columns.add(new OutputColumn("type", "yukon.web.picker.pao.type"));
        columns.add(new OutputColumn("pointName", "yukon.web.picker.point.pointName"));

        outputColumns = Collections.unmodifiableList(columns);
    }
    
    protected CapControlCBCOrphanPicker() {
        super(new CapControlCBCOrphanRowMapper(), searchColumnNames);
    }

    @Autowired
    public CapControlCBCOrphanPicker(CbcHelperService cbcHelperService) {
        super(new CapControlCBCOrphanRowMapper(cbcHelperService), searchColumnNames);
    }
    
    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    @Override
    public String getIdFieldName() {
        return "paoId";
    }

    @Override
    protected String getDatabaseIdFieldName() {
        return "PAObjectId";
    }

    @Override
    protected void updateFilters(List<SqlFilter> filters, List<PostProcessingFilter<Map<String, Object>>> postFilters,
            String extraArgs, YukonUserContext userContext) {
        selectedCBCId = extraArgs;
        SqlFilter filter = new SqlFilter() {
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder orphanClause = new SqlStatementBuilder();
                SqlStatementBuilder controlDeviceInClause = new SqlStatementBuilder();
                controlDeviceInClause.append("SELECT DISTINCT ControlDeviceId FROM CapBank");
                orphanClause.append("(PAObjectId ").notIn(controlDeviceInClause);
                orphanClause.append("OR PAObjectId").eq(selectedCBCId);
                orphanClause.append(")");
                return orphanClause;
            }
        };

        filters.add(filter);
    }

}