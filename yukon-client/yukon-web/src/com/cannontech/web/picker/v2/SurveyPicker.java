package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.survey.dao.impl.EnergyCompanyFilter;
import com.cannontech.common.survey.dao.impl.SurveyRowMapper;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class SurveyPicker extends DatabasePicker<Survey> {
    private EnergyCompanyDao energyCompanyDao;

    private final static String[] searchColumnNames = new String[] {
            "surveyName", "surveyKey" };
    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.survey.";

        columns.add(new OutputColumn("surveyName", titleKeyPrefix + "name"));
        columns.add(new OutputColumn("surveyKey", titleKeyPrefix + "key"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    public SurveyPicker() {
        super(new SurveyRowMapper(), searchColumnNames);
    }

    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<Survey>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        LiteEnergyCompany energyCompany =
            energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
        sqlFilters.add(new EnergyCompanyFilter(energyCompany.getEnergyCompanyID()));
    }

    @Override
    public String getIdFieldName() {
        return "surveyId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    @Autowired
    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }
}
