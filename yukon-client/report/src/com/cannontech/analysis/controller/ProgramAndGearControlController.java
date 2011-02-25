package com.cannontech.analysis.controller;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.ProgramAndGearControlReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ProgramAndGearControlModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.common.util.StringUtils;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.util.ServletRequestEnumUtils;
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
        
        ReportFilter filter = ServletRequestEnumUtils.getEnumParameter(request, ReportFilter.class, ReportModelBase.ATT_FILTER_MODEL_TYPE, ReportFilter.NONE);
        
        String filterValuesStr = ServletRequestUtils.getStringParameter(request, ReportModelBase.ATT_FILTER_MODEL_VALUES, "");
        Set<Integer> paoIdsSet = Sets.newHashSet(StringUtils.parseIntStringForList(filterValuesStr));
        Set<Integer> programIdSet = Sets.newHashSet();
        
        if (filter == ReportFilter.PROGRAM) {
            programIdSet = paoIdsSet;
        } else if (filter == ReportFilter.LMSCENARIO) {
            ScenarioDao scenarioDao = YukonSpringHook.getBean("drScenarioDao", ScenarioDao.class);
            
            for (int scenarioId : paoIdsSet) {
                Map<Integer, ScenarioProgram> programs = 
                    scenarioDao.findScenarioProgramsForScenario(scenarioId);
                programIdSet.addAll(programs.keySet());
            }
        } else if (filter == ReportFilter.LMCONTROLAREA) {
            
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
