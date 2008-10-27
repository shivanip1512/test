package com.cannontech.web.loadControlServiceTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public class LoadControlServiceTestController extends MultiActionController {
    
    private DateFormattingService dateformattingService;
    private LoadControlService loadControlService;

    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        ModelAndView mav = new ModelAndView("home.jsp");
        return mav;
    }
    
    // PROGRAM STATUS BY PROGRAM NAME
    public ModelAndView getProgramStatusByProgramName(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.text.ParseException {

        ModelAndView mav = new ModelAndView("home.jsp");

        List<String> results = new ArrayList<String>();
        List<String> errorReasons = new ArrayList<String>();
        
        String programName = ServletRequestUtils.getRequiredStringParameter(request, "programName");
        
        try {
            ProgramStatus programStatus = loadControlService.getProgramStatusByProgramName(programName);
            results.add(programStatus.toString());
        } catch (IllegalArgumentException e) {
            errorReasons.add(e.getMessage());
        }
        
        // re-populate fields
        mav.addAllObjects(ServletUtil.getParameterMap(request));
        mav.addObject("results", results);
        mav.addObject("errorReasons", errorReasons);
        
        return mav;
        
    }
    
    // ALL CURRENTLY ACTIVE PROGRAMS 
    public ModelAndView getAllCurrentlyActivePrograms(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.text.ParseException {

        ModelAndView mav = new ModelAndView("home.jsp");

        List<String> results = new ArrayList<String>();
        List<String> errorReasons = new ArrayList<String>();
        
        List<ProgramStatus> programStatii = loadControlService.getAllCurrentlyActivePrograms();
        
        for (ProgramStatus programStatus : programStatii) {
            results.add(programStatus.toString());
        }
        
        if (results.size() < 1) {
            errorReasons.add("No Currrently Active Programs");
        }
        
        // re-populate fields
        mav.addAllObjects(ServletUtil.getParameterMap(request));
        mav.addObject("results", results);
        mav.addObject("errorReasons", errorReasons);
        
        return mav;
        
    }
    
    // START CONTROL BY SCENARIO NAME
    public ModelAndView startControlByScenarioName(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.text.ParseException {

        ModelAndView mav = new ModelAndView("home.jsp");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        List<String> results = new ArrayList<String>();
        List<String> errorReasons = new ArrayList<String>();
        
        String scenarioName = ServletRequestUtils.getRequiredStringParameter(request, "scenarioName");
        Date startTime = parseStartStopDateTime(request, "start", userContext);
        Date stopTime = parseStartStopDateTime(request, "stop", userContext);
        boolean force = ServletRequestUtils.getBooleanParameter(request, "force", false);
        
        ScenarioStatus scenarioStatus = null;
        try {
            
            scenarioStatus = loadControlService.startControlByScenarioName(scenarioName, startTime, stopTime, force);
            
            for (ProgramStatus programStatus : scenarioStatus.getProgramStatii()) {
                
                results.add(programStatus.toString());
                
                if (programStatus.getConstraintViolations().size() > 0) {
                    errorReasons.add("Contains Constraint Violations");
                }
            }
            
        } catch (TimeoutException e) {
            errorReasons.add("Timeout Exception");
        } catch (IllegalArgumentException e) {
            errorReasons.add(e.getMessage());
        }
        
        // re-populate fields
        mav.addAllObjects(ServletUtil.getParameterMap(request));
        mav.addObject("results", results);
        mav.addObject("errorReasons", errorReasons);
        
        return mav;
        
    }
    
    
    // START CONTROL BY PROGRAM NAME
    public ModelAndView startControlByProgramName(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.text.ParseException {

        ModelAndView mav = new ModelAndView("home.jsp");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        List<String> results = new ArrayList<String>();
        List<String> errorReasons = new ArrayList<String>();
        
        String programName = ServletRequestUtils.getRequiredStringParameter(request, "programName");
        Date startTime = parseStartStopDateTime(request, "start", userContext);
        Date stopTime = parseStartStopDateTime(request, "stop", userContext);
        boolean force = ServletRequestUtils.getBooleanParameter(request, "force", false);
        
        ProgramStatus programStatus = null;
        try {
            
            programStatus = loadControlService.startControlByProgramName(programName, startTime, stopTime, 0, force);
            results.add(programStatus.toString());
            
            if (programStatus.getConstraintViolations().size() > 0) {
                errorReasons.add("Contains Constraint Violations");
            }
            
        } catch (TimeoutException e) {
            errorReasons.add("Timeout Exception");
        } catch (IllegalArgumentException e) {
            errorReasons.add(e.getMessage());
        }
        
        // re-populate fields
        mav.addAllObjects(ServletUtil.getParameterMap(request));
        mav.addObject("results", results);
        mav.addObject("errorReasons", errorReasons);
        
        return mav;
        
    }
    
    
    // STOP CONTROL BY SCENARIO NAME
    public ModelAndView stopControlByScenarioName(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.text.ParseException {

        ModelAndView mav = new ModelAndView("home.jsp");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        List<String> results = new ArrayList<String>();
        List<String> errorReasons = new ArrayList<String>();
        
        String scenarioName = ServletRequestUtils.getRequiredStringParameter(request, "scenarioName");
        Date stopTime = parseStartStopDateTime(request, "stop", userContext);
        boolean force = ServletRequestUtils.getBooleanParameter(request, "force", false);
        
        ScenarioStatus scenarioStatus = null;
        try {
            
            scenarioStatus = loadControlService.stopControlByScenarioName(scenarioName, stopTime, force);
            
            for (ProgramStatus programStatus : scenarioStatus.getProgramStatii()) {
                
                results.add(programStatus.toString());
                
                if (programStatus.getConstraintViolations().size() > 0) {
                    errorReasons.add("Contains Constraint Violations");
                }
            }
            
        } catch (TimeoutException e) {
            errorReasons.add("Timeout Exception");
        } catch (IllegalArgumentException e) {
            errorReasons.add(e.getMessage());
        }
        
        // re-populate fields
        mav.addAllObjects(ServletUtil.getParameterMap(request));
        mav.addObject("results", results);
        mav.addObject("errorReasons", errorReasons);
        
        return mav;
        
    }
    
    
    // STOP CONTROL BY PROGRAM NAME
    public ModelAndView stopControlByProgramName(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.text.ParseException {

        ModelAndView mav = new ModelAndView("home.jsp");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        List<String> results = new ArrayList<String>();
        List<String> errorReasons = new ArrayList<String>();
        
        String programName = ServletRequestUtils.getRequiredStringParameter(request, "programName");
        Date stopTime = parseStartStopDateTime(request, "stop", userContext);
        boolean force = ServletRequestUtils.getBooleanParameter(request, "force", false);
        
        ProgramStatus programStatus = null;
        try {
            
            programStatus = loadControlService.stopControlByProgramName(programName, stopTime, 0, force);
            results.add(programStatus.toString());
            
            if (programStatus.getConstraintViolations().size() > 0) {
                errorReasons.add("Contains Constraint Violations");
            }
            
        } catch (TimeoutException e) {
            errorReasons.add("Timeout Exception");
        } catch (IllegalArgumentException e) {
            errorReasons.add(e.getMessage());
        }
        
        // re-populate fields
        mav.addAllObjects(ServletUtil.getParameterMap(request));
        mav.addObject("results", results);
        mav.addObject("errorReasons", errorReasons);
        
        return mav;
        
    }
    
    // SCENARIOS LIST OF PROGRAM STARTING GEARS
    public ModelAndView getScenarioProgramStartGears(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.text.ParseException {

        ModelAndView mav = new ModelAndView("home.jsp");

        List<String> results = new ArrayList<String>();
        List<String> errorReasons = new ArrayList<String>();
        
        String scenarioName = ServletRequestUtils.getRequiredStringParameter(request, "scenarioName");
        
        try {
            ScenarioProgramStartingGears scenarioProgramStartingGears = loadControlService.getScenarioProgramStartingGearsByScenarioName(scenarioName);
            results.add(scenarioProgramStartingGears.toString());
        } catch (IllegalArgumentException e) {
            errorReasons.add(e.getMessage());
        }
        
        // re-populate fields
        mav.addAllObjects(ServletUtil.getParameterMap(request));
        mav.addObject("results", results);
        mav.addObject("errorReasons", errorReasons);
        
        return mav;
        
    }
    
    // HELPERS
    private Date parseStartStopDateTime(HttpServletRequest request, String startOrStop, YukonUserContext userContext) throws ServletException, java.text.ParseException {
        
        String dateStr = ServletRequestUtils.getRequiredStringParameter(request, startOrStop + "Date");
        String timeStr = ServletRequestUtils.getRequiredStringParameter(request, startOrStop + "Time");
        
        Date date = dateformattingService.flexibleDateParser(dateStr, userContext);
        
        int hours = Integer.valueOf(StringUtils.split(timeStr, ":")[0]);
        int mins = Integer.valueOf(StringUtils.split(timeStr, ":")[1]);
        
        date = DateUtils.addHours(date, hours);
        date = DateUtils.addMinutes(date, mins);
        
        return date;
    }
    
    
    @Autowired
    public void setDateformattingService(
            DateFormattingService dateformattingService) {
        this.dateformattingService = dateformattingService;
    }
    
    @Autowired
    public void setLoadControlService(LoadControlService loadControlService) {
        this.loadControlService = loadControlService;
    }
}
