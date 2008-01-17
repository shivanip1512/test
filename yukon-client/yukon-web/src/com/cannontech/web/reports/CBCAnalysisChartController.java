package com.cannontech.web.reports;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.web.CBCWebUtils;
import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.core.dao.PointDao;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;

public class CBCAnalysisChartController extends MultiActionController  {
    
	private PointDao pointDao = null;
	private CapControlCache capControlCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
	
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
    		startDate = DateUtils.add(startDate, Calendar.DATE, -30);
    		startDate = DateUtils.truncate(startDate, Calendar.DATE);
    	}
    	else if(chartPeriod == ChartPeriod.DAY) {
    		startDate = DateUtils.truncate(startDate, Calendar.DATE);
    	}
    	
    	
    	Long startDateMillis = startDate.getTime();
    	Long endDateMillis = endDate.getTime();
    	
    	
    	// INIT MAPS
    	// ------------------------------------------------------------------------------------------------------
    	Map<Integer, List<Integer>> targetPointIdsForGraph = new LinkedHashMap<Integer, List<Integer>>();
    	Map<Integer, String> targetNames = new LinkedHashMap<Integer, String>();
    	Map<Integer, Map<String, Object>> targetReportInfo = new LinkedHashMap<Integer, Map<String, Object>>();
    	
    	
    	// GATHER POINTS PER TARGET
    	// ------------------------------------------------------------------------------------------------------
    	for(int targetId : targetIds) {
    		
    		// TARGET NAME
    		targetNames.put(targetId, capControlCache.getCapControlPAO(targetId).toString());
    		
    		// ALL POINTS FOR TARGET
    		Map<String, Integer> allPointsForTarget = new LinkedHashMap<String, Integer>();
    		
    		// VAR-WATTS POINT IDS
    		if(analysisType.equalsIgnoreCase(CBCWebUtils.TYPE_VARWATTS)){
    			
    			if(capControlCache.isSubBus(targetId)) {
    	    		SubBus subBus = capControlCache.getSubBus(targetId);
    	    		allPointsForTarget.put("currentVarLoadPointId", subBus.getCurrentVarLoadPointID());      
    	    		allPointsForTarget.put("estimatedVarLoadPointId", subBus.getEstimatedVarLoadPointID());
    	    		allPointsForTarget.put("currentWattLoadPointId", subBus.getCurrentWattLoadPointID());
    	    	}
    	    	else if(capControlCache.isFeeder(targetId)) {
    	    		Feeder feeder = capControlCache.getFeeder(targetId);
    	    		allPointsForTarget.put("currentVarLoadPointId", feeder.getCurrentVarLoadPointID());      
    	    		allPointsForTarget.put("estimatedVarLoadPointId", feeder.getEstimatedVarLoadPointID());
    	    		allPointsForTarget.put("currentWattLoadPointId", feeder.getCurrentWattLoadPointID());
    	    	}
    		}
    		
    		// POWER FACTOR POINT IDS
    		else if(analysisType.equalsIgnoreCase(CBCWebUtils.TYPE_PF)){
    			
    			if(capControlCache.isSubBus(targetId)) {
    	    		SubBus subBus = capControlCache.getSubBus(targetId);
    	    		allPointsForTarget.put("powerFactorPointId", subBus.getPowerFactorPointId());
    	    		allPointsForTarget.put("estimatedPowerFactorPointId", subBus.getEstimatedPowerFactorPointId());
    	    	}
    	    	else if(capControlCache.isFeeder(targetId)) {
    	    		Feeder feeder = capControlCache.getFeeder(targetId);
    	    		allPointsForTarget.put("powerFactorPointId", feeder.getPowerFactorPointID());		
    	    		allPointsForTarget.put("estimatedPowerFactorPointId", feeder.getEstimatedPowerFactorPointID());
    	    	}
    		}
    		
    		// POINT IDS TO GRAPH
    		List<Integer> pointIdsForGraph = new ArrayList<Integer>();
    		for(String pointKey : allPointsForTarget.keySet()) {
    			Integer pt = allPointsForTarget.get(pointKey);
    			if(pt > 0) {
    				pointIdsForGraph.add(pt);
    			}
    		}
    		
    		// ADD KEEPERS, OTHERWISE NULL
    		targetPointIdsForGraph.put(targetId, null);
    		if(pointIdsForGraph.size() > 0) {
    			targetPointIdsForGraph.put(targetId, pointIdsForGraph);
    		}
    		
    		// REPORT INFO
    		Map<String, Object> reportInfo = new HashMap<String, Object>();
    		
    		reportInfo.put("targetId", targetId);
    		reportInfo.put("targetId", targetId);
    		reportInfo.put("startDate", startDateMillis);
    		reportInfo.put("stopDate", endDateMillis);
    		
    		for(String pointKey : allPointsForTarget.keySet()) {
    			Integer pt = allPointsForTarget.get(pointKey);
    			reportInfo.put(pointKey, pt);
    		}
    		
    		targetReportInfo.put(targetId, reportInfo);
    		
    	}
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	// GRAPHS
    	// list of graphs per target name
    	// ------------------------------------------------------------------------------------------------------
    	Map<Integer, List<Map<String, Object>>> targetGraphs = new LinkedHashMap<Integer, List<Map<String, Object>>>();
    	
    	for(Integer targetId : targetPointIdsForGraph.keySet()) {
    	
    		String targetName = targetNames.get(targetId);
    		List<Integer> pointIdsListForTarget = targetPointIdsForGraph.get(targetId);
    		
    		if(pointIdsListForTarget != null) {
    		
	    		List<Map<String, Object>> graphs = new ArrayList<Map<String, Object>>(); 
	    		
		    	for(Integer id : pointIdsListForTarget) {
		    		
		    		Map<String, Object> graph = new HashMap<String, Object>();
		    		graph.put("targetName", targetName);
		    		graph.put("pointName", pointDao.getPointName(id));
		    		graph.put("pointIds", id);
		    		graph.put("period", chartPeriod);
		    		graph.put("converterType", "RAW");
		    		graph.put("graphType", "LINE");
		    		graph.put("startDateMillis", startDateMillis);
		    		graph.put("endDateMillis", endDateMillis);
		    		
		    		graphs.add(graph);
		    	}
		    	
		    	targetGraphs.put(targetId, graphs);
		    	
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
    	
    	// title, module
    	mav.addObject("title", "Analysis");
    	mav.addObject("module", "capcontrol");
    	mav.addObject("showMenu", "false");
    	
    	if(analysisType.equalsIgnoreCase(CBCWebUtils.TYPE_VARWATTS)){
    		mav.addObject("definitionName", "kVarWattRPHDefinition");
		}
		else if(analysisType.equalsIgnoreCase(CBCWebUtils.TYPE_PF)){
			mav.addObject("definitionName", "powerFactorRPHDefinition");
		}
    	
    	mav.addObject("targetIds", targetIds);
    	mav.addObject("targetNames", targetNames);
    	mav.addObject("targetGraphs", targetGraphs);
    	mav.addObject("targetReportInfo", targetReportInfo);
    	
    	return mav;
    }

    @Required
	public void setPointDao(PointDao pointDao) {
		this.pointDao = pointDao;
	}
}
