package com.cannontech.analysis.controller;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.ProgramAndGearControlReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ProgramAndGearControlModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.common.util.StringUtils;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;
import com.google.common.collect.Sets;

public class ProgramAndGearControlController extends ReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.PROGRAM,
            ReportFilter.LMCONTROLAREA,
            ReportFilter.LMSCENARIO
            };
    
    public ProgramAndGearControlController() {
        super();
        model = new ProgramAndGearControlModel();
        report = new ProgramAndGearControlReport(model);
    }

    public String getHTMLOptionsTable() {
        return "* Select at least one program, control area or scenario below.";
    }

    public YukonReportBase getReport() {
        return report;
    }

    public ReportModelBase getModel() {
        return report.getModel();
    }

    public ReportFilter[] getFilterModelTypes() {
        return filterModelTypes;
    }

    public void setFilterModelTypes(ReportFilter[] filterModelTypes) {
        this.filterModelTypes = filterModelTypes;
    }
    
    public void setRequestParameters(HttpServletRequest request) {

        super.setRequestParameters(request);
        ProgramAndGearControlModel programAndGearControlModel = (ProgramAndGearControlModel) model;
        
        programAndGearControlModel.setLiteUser(ServletUtil.getYukonUser(request));
        
        int filterModelType = ServletRequestUtils.getIntParameter(request, ReportModelBase.ATT_FILTER_MODEL_TYPE, -1);
        ReportFilter reportFilter = ReportFilter.values()[filterModelType];
        
        String filterValuesStr = ServletRequestUtils.getStringParameter(request, ReportModelBase.ATT_FILTER_MODEL_VALUES, "");
        Set<Integer> paoIdsSet = Sets.newHashSet(StringUtils.parseIntStringForList(filterValuesStr));
        Set<Integer> programIdSet = Sets.newHashSet();
        
        if (ReportFilter.PROGRAM.equals(reportFilter)) {
            programIdSet = paoIdsSet;
        } else if (ReportFilter.LMSCENARIO.equals(reportFilter)) {
            ScenarioDao scenarioDao = YukonSpringHook.getBean("drScenarioDao", ScenarioDao.class);
            
            for (int scenarioId : paoIdsSet) {
                Map<Integer, ScenarioProgram> programs = 
                    scenarioDao.findScenarioProgramsForScenario(scenarioId);
                programIdSet.addAll(programs.keySet());
            }
        } else if (ReportFilter.LMCONTROLAREA.equals(reportFilter)) {
            
            ControlAreaDao controlAreaDao = 
                YukonSpringHook.getBean("drControlAreaDao", ControlAreaDao.class);
            
            for (int controlAreaId : paoIdsSet) {
                Set<Integer> programIds = controlAreaDao.getProgramIdsForControlArea(controlAreaId);
                programIdSet.addAll(programIds);
            }
        }
        
        programAndGearControlModel.setProgramIds(programIdSet);
    }
}
