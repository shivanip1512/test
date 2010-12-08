package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.UltraLightAssignedProgramApplianceCategoryFilter;
import com.cannontech.stars.dr.appliance.dao.UltraLightAssignedProgramRowMapper;
import com.cannontech.stars.dr.appliance.model.UltraLightAssignedProgram;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class AssignedProgramPicker
    extends DatabasePicker<UltraLightAssignedProgram> {
    private ApplianceCategoryDao applianceCategoryDao;

    private final static String[] searchColumnNames = new String[] {
        "pao.paoName", "ac.description", "wc.alternateDisplayName"
        };
    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.assignedProgram.";

        columns.add(new OutputColumn("programName", titleKeyPrefix + "paoName"));
        columns.add(new OutputColumn("displayName", titleKeyPrefix + "displayName"));
        columns.add(new OutputColumn("applianceCategoryName", titleKeyPrefix + "applianceCategory"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    public AssignedProgramPicker() {
        super(new UltraLightAssignedProgramRowMapper(), searchColumnNames);
    }

    @Override
    protected void updateFilters(
            List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightAssignedProgram>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        int energyCompanyId = NumberUtils.toInt(extraArgs, 0);
        List<Integer> applianceCategoryIds =
            applianceCategoryDao.getApplianceCategoryIdsByEC(energyCompanyId);
        sqlFilters.add(new UltraLightAssignedProgramApplianceCategoryFilter(applianceCategoryIds));
    }

    @Override
    public String getIdFieldName() {
        return "assignedProgramId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    @Override
    protected String getDatabaseIdFieldName() {
        return "p.programId";
    }

    @Autowired
    public void setApplianceCategoryDao(ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }
}
