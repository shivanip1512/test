package com.cannontech.web.picker;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class AvailableLoadProgramPicker extends DatabasePicker<LMDto> {

    private final static String[] searchColumnNames = new String[] { "pao.PAOName" };
    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.availableProgram.";
        columns.add(new OutputColumn("name", titleKeyPrefix + "programName"));
        outputColumns = Collections.unmodifiableList(columns);
    }

    protected AvailableLoadProgramPicker() {
        super(new AvailableProgramRowMapper(), searchColumnNames);
    }

    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters, List<PostProcessingFilter<LMDto>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        List<Integer> programIds = LMControlAreaProgram.getAllProgramsInControlAreas();
        sqlFilters.add(new AvailableProgramFilter(programIds));
    }

    @Override
    public String getIdFieldName() {
        return "id";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    @Override
    protected String getDatabaseIdFieldName() {
        return "pao.PAObjectID";
    }
}
