package com.cannontech.web.reports;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.model.FeederPhaseData;
import com.cannontech.capcontrol.model.SubstationBus;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.web.capcontrol.CBCWebUtils;
import com.cannontech.web.security.annotation.CheckFalseRoleProperty;

@Controller
@RequestMapping("/cbcAnalysisChart/*")
@CheckFalseRoleProperty(YukonRoleProperty.HIDE_GRAPHS)
public class CBCAnalysisChartController {
    
    @Autowired private PointDao pointDao;
    @Autowired private CapControlCache capControlCache;
    @Autowired private SubstationBusDao substationBusDao;
    @Autowired private FeederDao feederDao;

    /**
     * @param request
     * @param response
     */
    @RequestMapping(value = "cbcChart", method = RequestMethod.GET)
    public ModelAndView cbcChart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // PARAMETERS
        // ------------------------------------------------------------------------------------------------------
        String analysisType = ServletRequestUtils.getRequiredStringParameter(request, "analysisType");
        ChartPeriod chartPeriod = ChartPeriod.valueOf(ServletRequestUtils.getRequiredStringParameter(request, "period"));
        String targetsStr = ServletRequestUtils.getRequiredStringParameter(request, "targets");
        
        // parse target ids
        String[] targetStrIds = StringUtils.split(targetsStr, ",");
        List<Integer> targetIds = new ArrayList<Integer>();
        for(String targetStrId : targetStrIds) {
            targetIds.add(Integer.parseInt(targetStrId));
        }
        
        // DATES
        // ------------------------------------------------------------------------------------------------------
        Date endDate = new Date();
        Date startDate = endDate;
        if(chartPeriod == ChartPeriod.MONTH) {
            startDate = DateUtils.addDays(startDate, -30);
            startDate = DateUtils.truncate(startDate, Calendar.DATE);
        }
        else if(chartPeriod == ChartPeriod.DAY) {
            startDate = DateUtils.truncate(startDate, Calendar.DATE);
        }
        
        ChartInterval chartInterval = chartPeriod.getChartUnit(Range.inclusive(startDate, endDate), ConverterType.RAW);
        
        
        Long startDateMillis = startDate.getTime();
        Long endDateMillis = endDate.getTime();
        
        // INIT MAPS
        // ------------------------------------------------------------------------------------------------------
        Map<Integer, String> targetNames = new LinkedHashMap<Integer, String>();
        Map<Integer, Map<String, Object>> targetReportInfo = new LinkedHashMap<Integer, Map<String, Object>>();
        Map<Integer, Map<String, List<Integer>>> groupedPointIdsByTarget = new LinkedHashMap<Integer, Map<String, List<Integer>>>();
        
        // GATHER POINTS PER TARGET
        // ------------------------------------------------------------------------------------------------------
        for(int targetId : targetIds) {
            
            // START REPORT INFO
            Map<String, Object> reportInfo = new HashMap<String, Object>();
            String definitionName = "";
            reportInfo.put("targetId", targetId);
            reportInfo.put("startDate", startDateMillis);
            reportInfo.put("stopDate", endDateMillis);
            
            // TARGET NAME
            targetNames.put(targetId, capControlCache.getCapControlPao(targetId).toString());
            
            // ALL POINTS FOR TARGET
            Map<String, List<Integer>> labledPointIds = new LinkedHashMap<String, List<Integer>>();
            
            // VAR-WATTS POINT IDS
            if(analysisType.equalsIgnoreCase(CBCWebUtils.TYPE_VARWATTS)){
                
                if(capControlCache.isSubBus(targetId)) {
                    
                    SubBus subBus_cache = capControlCache.getSubBus(targetId);
                    SubstationBus subBus_dao = substationBusDao.findSubBusById(targetId);
                    
                    if ("Y".equalsIgnoreCase(subBus_dao.getUsephasedata())) {
                        
                        definitionName = "kVarPhaseWattRPHDefinition";
                        
                        List<Integer> phaseIds = new ArrayList<Integer>();
                        phaseIds.add(subBus_dao.getCurrentVarLoadPointId());
                        phaseIds.add(subBus_dao.getPhaseb());
                        phaseIds.add(subBus_dao.getPhasec());
                        labledPointIds.put("phaseIds", phaseIds);
                        
                        labledPointIds.put("estimatedVarLoadPointId", Collections.singletonList(subBus_cache.getEstimatedVarLoadPointID()));
                        labledPointIds.put("currentWattLoadPointId", Collections.singletonList(subBus_cache.getCurrentWattLoadPointID()));
                    }
                    else {
                        
                        definitionName = "kVarWattRPHDefinition";
                        
                        labledPointIds.put("currentVarLoadPointId", Collections.singletonList(subBus_cache.getCurrentVarLoadPointID()));      
                        labledPointIds.put("estimatedVarLoadPointId", Collections.singletonList(subBus_cache.getEstimatedVarLoadPointID()));
                        labledPointIds.put("currentWattLoadPointId", Collections.singletonList(subBus_cache.getCurrentWattLoadPointID()));
                    }
                    
                }
                else if(capControlCache.isFeeder(targetId)) {
                    com.cannontech.message.capcontrol.streamable.Feeder feeder_cache = capControlCache.getFeeder(targetId);
                    FeederPhaseData phaseData = feederDao.getFeederPhaseData(targetId);
                    
                    if (phaseData.getUsePhaseData() == true) {
                        
                        definitionName = "kVarPhaseWattRPHDefinition";
                        
                        List<Integer> phaseIds = new ArrayList<Integer>();
                        phaseIds.add(phaseData.getCurrentVarLoadPointId());
                        phaseIds.add(phaseData.getPhaseB());
                        phaseIds.add(phaseData.getPhaseC());
                        labledPointIds.put("phaseIds", phaseIds);
                        
                        labledPointIds.put("estimatedVarLoadPointId", Collections.singletonList(feeder_cache.getEstimatedVarLoadPointID()));
                        labledPointIds.put("currentWattLoadPointId", Collections.singletonList(feeder_cache.getCurrentWattLoadPointID()));
                    }
                    else {
                        
                        definitionName = "kVarWattRPHDefinition";
                        
                        labledPointIds.put("currentVarLoadPointId", Collections.singletonList(feeder_cache.getCurrentVarLoadPointID()));      
                        labledPointIds.put("estimatedVarLoadPointId", Collections.singletonList(feeder_cache.getEstimatedVarLoadPointID()));
                        labledPointIds.put("currentWattLoadPointId", Collections.singletonList(feeder_cache.getCurrentWattLoadPointID()));
                    }
                }
            }
            
            // POWER FACTOR POINT IDS
            else if(analysisType.equalsIgnoreCase(CBCWebUtils.TYPE_PF)){
                
                definitionName = "powerFactorRPHDefinition";
                
                if(capControlCache.isSubBus(targetId)) {
                    SubBus subBus = capControlCache.getSubBus(targetId);
                    labledPointIds.put("powerFactorPointId", Collections.singletonList(subBus.getPowerFactorPointId()));
                    labledPointIds.put("estimatedPowerFactorPointId", Collections.singletonList(subBus.getEstimatedPowerFactorPointId()));
                }
                else if(capControlCache.isFeeder(targetId)) {
                    Feeder feeder = capControlCache.getFeeder(targetId);
                    labledPointIds.put("powerFactorPointId", Collections.singletonList(feeder.getPowerFactorPointID()));        
                    labledPointIds.put("estimatedPowerFactorPointId", Collections.singletonList(feeder.getEstimatedPowerFactorPointID()));
                }
            }
            
            // weed out zeros
            for (String labelKey : labledPointIds.keySet()) {
                List<Integer> okPointIds = new ArrayList<Integer>();
                for (Integer pointId : labledPointIds.get(labelKey)) {
                    if (pointId > 0) {
                        okPointIds.add(pointId);
                    }
                }
                labledPointIds.put(labelKey, okPointIds);
            }
            
            // groupedPointIdsByTarget
            groupedPointIdsByTarget.put(targetId, labledPointIds);
            
            // FINISH REPORT INFO
            reportInfo.put("definitionName", definitionName);
            targetReportInfo.put(targetId, reportInfo);
        }
        
        // GRAPHS
        // list of graphs per target name
        // ------------------------------------------------------------------------------------------------------
        Map<Integer, List<Map<String, Object>>> targetGraphs = new LinkedHashMap<Integer, List<Map<String, Object>>>();
        
        for(Integer targetId : groupedPointIdsByTarget.keySet()) {
        
            List<Map<String, Object>> graphMapsForTarget = new ArrayList<Map<String, Object>>(); 
            
            Map<String, List<Integer>> pointIdsListForTarget = groupedPointIdsByTarget.get(targetId);
            for (String groupLabel : pointIdsListForTarget.keySet()) {
                
                if (pointIdsListForTarget.get(groupLabel).size() > 0) {

                    Map<String, Object> graph = new HashMap<String, Object>();
                    graph.put("targetName", targetNames.get(targetId));
                    if (pointIdsListForTarget.get(groupLabel).size() > 1) {
                        graph.put("pointName", "Phase Var Data");
                    }
                    else {
                        graph.put("pointName", pointDao.getPointName(pointIdsListForTarget.get(groupLabel).get(0)));
                    }
                    graph.put("pointIds", StringUtils.join(pointIdsListForTarget.get(groupLabel), ","));
                    graph.put("interval", chartInterval);
                    
                    if ("PF".equals(analysisType)) {
                        //if Power Factor, use a different converter
                        graph.put("converterType", "POWERFACTOR");
                    } else {
                        //else use the default
                        graph.put("converterType", "RAW");
                    }
                    
                    graph.put("graphType", "LINE");
                    graph.put("startDateMillis", startDateMillis);
                    graph.put("endDateMillis", endDateMillis);
                    
                    graphMapsForTarget.add(graph);
                }
                
            }
            
            if (graphMapsForTarget.size() > 0) {
                targetGraphs.put(targetId, graphMapsForTarget);
            }
            else {
                targetGraphs.put(targetId, null);
            }
        }
        
        
        // MAV
        // - list of targetIds to use as key for maps
        // - map of graph data(s) if target has non-zero points, null otherwise
        // - map of target names
        // - report definition name for creating report links
        // ------------------------------------------------------------------------------------------------------
        ModelAndView mav = new ModelAndView("cbcAnalysisChart/cbcChart.jsp");
        
        mav.addObject("showMenu", "false");
        mav.addObject("targetIds", targetIds);
        mav.addObject("targetNames", targetNames);
        mav.addObject("targetGraphs", targetGraphs);
        mav.addObject("targetReportInfo", targetReportInfo);
        
        return mav;
    }
}
