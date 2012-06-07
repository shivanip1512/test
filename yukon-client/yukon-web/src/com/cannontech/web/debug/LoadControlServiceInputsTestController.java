package com.cannontech.web.debug;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.ws.StarsControllableDeviceDTO;
import com.cannontech.stars.ws.StarsControllableDeviceHelper;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public class LoadControlServiceInputsTestController extends MultiActionController {
    
    private DateFormattingService dateformattingService;
    private LoadControlService loadControlService;
    private EnrollmentHelperService enrollmentHelperService;
    private StarsControllableDeviceHelper starsControllableDeviceHelper;
    
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        return returnMav(request, new ArrayList<String>(0));
    }
    
    //====================================================================================================================================
    // PROGRAM STATUS BY PROGRAM NAME
    //====================================================================================================================================
    public ModelAndView getProgramStatusByProgramName(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {

    	YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
    	
        List<String> results = new ArrayList<String>();
        
        String programName = ServletRequestUtils.getRequiredStringParameter(request, "programName");
        
        ProgramStatus programStatus = loadControlService.getProgramStatusByProgramName(programName, userContext.getYukonUser());
        results.add(programStatus.toString());
        
        return returnMav(request, results);
    }
    
    //====================================================================================================================================
    // ALL CURRENTLY ACTIVE PROGRAMS 
    //====================================================================================================================================
    public ModelAndView getAllCurrentlyActivePrograms(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {

    	YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
    	
        List<String> results = new ArrayList<String>();
        
        List<ProgramStatus> programStatii = loadControlService.getAllCurrentlyActivePrograms(userContext.getYukonUser());
        
        for (ProgramStatus programStatus : programStatii) {
            results.add(programStatus.toString());
        }
        
        if (results.size() < 1) {
            results.add("No Currrently Active Programs");
        }
        
        return returnMav(request, results);
    }
    
    //====================================================================================================================================
    // START CONTROL BY SCENARIO NAME
    //====================================================================================================================================
    public ModelAndView startControlByScenarioName(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        List<String> results = new ArrayList<String>();
        
        String scenarioName = ServletRequestUtils.getRequiredStringParameter(request, "scenarioName");
        Date startTime = parseDateTime(request, "start", userContext);
        Date stopTime = parseDateTime(request, "stop", userContext);
        boolean force = ServletRequestUtils.getBooleanParameter(request, "force", false);
        boolean observeConstraintsAndExecute = ServletRequestUtils.getBooleanParameter(request, "observeConstraintsAndExecute", false);
        
        ScenarioStatus scenarioStatus = loadControlService.startControlByScenarioName(scenarioName, startTime, stopTime, force, observeConstraintsAndExecute, userContext.getYukonUser());
        
        for (ProgramStatus programStatus : scenarioStatus.getProgramStatuses()) {
            
            results.add(programStatus.toString());
            
            if (programStatus.getConstraintViolations().size() > 0) {
                results.add("Contains Constraint Violations");
            }
        }
        
        return returnMav(request, results);
    }
    
    //====================================================================================================================================
    // START CONTROL BY PROGRAM NAME
    //====================================================================================================================================
    public ModelAndView startControlByProgramName(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        List<String> results = new ArrayList<String>();
        
        String programName = ServletRequestUtils.getRequiredStringParameter(request, "programName");
        Date startTime = parseDateTime(request, "start", userContext);
        Date stopTime = parseDateTime(request, "stop", userContext);
        String gearName = ServletRequestUtils.getRequiredStringParameter(request, "gearName");
        boolean force = ServletRequestUtils.getBooleanParameter(request, "force", false);
        boolean observeConstraintsAndExecute = ServletRequestUtils.getBooleanParameter(request, "observeConstraintsAndExecute", false);
        
        ProgramStatus programStatus = loadControlService.startControlByProgramName(programName, startTime, stopTime, gearName, force, observeConstraintsAndExecute, userContext.getYukonUser());
        results.add(programStatus.toString());
        
        if (programStatus.getConstraintViolations().size() > 0) {
            results.add("Contains Constraint Violations");
        }
        
        return returnMav(request, results);
    }
    
    //====================================================================================================================================
    // STOP CONTROL BY SCENARIO NAME
    //====================================================================================================================================
    public ModelAndView stopControlByScenarioName(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        List<String> results = new ArrayList<String>();
        
        String scenarioName = ServletRequestUtils.getRequiredStringParameter(request, "scenarioName");
        Date stopTime = parseDateTime(request, "stop", userContext);
        boolean force = ServletRequestUtils.getBooleanParameter(request, "force", false);
        boolean observeConstraintsAndExecute = ServletRequestUtils.getBooleanParameter(request, "observeConstraintsAndExecute", false);
        
        ScenarioStatus scenarioStatus = loadControlService.stopControlByScenarioName(scenarioName, stopTime, force, observeConstraintsAndExecute, userContext.getYukonUser());
        
        for (ProgramStatus programStatus : scenarioStatus.getProgramStatuses()) {
            
            results.add(programStatus.toString());
            
            if (programStatus.getConstraintViolations().size() > 0) {
                results.add("Contains Constraint Violations");
            }
        }
        
        return returnMav(request, results);
    }
    
    //====================================================================================================================================
    // STOP CONTROL BY PROGRAM NAME
    //====================================================================================================================================
    public ModelAndView stopControlByProgramName(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        List<String> results = new ArrayList<String>();
        
        String programName = ServletRequestUtils.getRequiredStringParameter(request, "programName");
        Date stopTime = parseDateTime(request, "stop", userContext);
        boolean force = ServletRequestUtils.getBooleanParameter(request, "force", false);
        boolean observeConstraintsAndExecute = ServletRequestUtils.getBooleanParameter(request, "observeConstraintsAndExecute", false);
        
        ProgramStatus programStatus = loadControlService.stopControlByProgramName(programName, stopTime, force, observeConstraintsAndExecute, userContext.getYukonUser());
        results.add(programStatus.toString());
        
        if (programStatus.getConstraintViolations().size() > 0) {
            results.add("Contains Constraint Violations");
        }
        
        return returnMav(request, results);
    }
    
    //====================================================================================================================================
    // SCENARIOS LIST OF PROGRAM STARTING GEARS
    //====================================================================================================================================
    public ModelAndView getScenarioProgramStartGears(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.text.ParseException {

    	YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
    	
        List<String> results = new ArrayList<String>();
        
        String scenarioName = ServletRequestUtils.getRequiredStringParameter(request, "scenarioName");
        
        ScenarioProgramStartingGears scenarioProgramStartingGears = loadControlService.getScenarioProgramStartingGearsByScenarioName(scenarioName, userContext.getYukonUser());
        results.add(scenarioProgramStartingGears.toString());
        
        return returnMav(request, results);
    }
    
    //====================================================================================================================================
    // ENROLLING A DEVICE IN A PROGRAM
    //====================================================================================================================================
    public ModelAndView enrollingADeviceInAProgram(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.text.ParseException {

        List<String> results = new ArrayList<String>();

        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);

        EnrollmentHelper enrollmentHelper = new EnrollmentHelper();
        enrollmentHelper.setAccountNumber(ServletRequestUtils.getRequiredStringParameter(request, "accountNumber"));
        enrollmentHelper.setLoadGroupName(ServletRequestUtils.getRequiredStringParameter(request, "loadGroupName"));
        enrollmentHelper.setProgramName(ServletRequestUtils.getRequiredStringParameter(request, "programName"));
        enrollmentHelper.setSerialNumber(ServletRequestUtils.getRequiredStringParameter(request, "serialNumber"));
        enrollmentHelper.setRelay(ServletRequestUtils.getStringParameter(request, "relay"));
        enrollmentHelper.setApplianceKW(ServletRequestUtils.getFloatParameter(request, "applianceKW", 0));
        enrollmentHelper.setApplianceCategoryName(ServletRequestUtils.getStringParameter(request, "applianceCategoryName"));
        enrollmentHelper.setSeasonalLoad(ServletRequestUtils.getBooleanParameter(request, "seasonalLoad", false));
        
        enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.ENROLL, yukonUserContext.getYukonUser());
        
        return returnMav(request, results);
    }

    //====================================================================================================================================
    // UNENROLLING A DEVICE IN A PROGRAM
    //====================================================================================================================================
    public ModelAndView unenrollingADeviceInAProgram(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.text.ParseException {

        List<String> results = new ArrayList<String>();

        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        
        EnrollmentHelper enrollmentHelper = new EnrollmentHelper();
        enrollmentHelper.setAccountNumber(ServletRequestUtils.getRequiredStringParameter(request, "accountNumber"));
        enrollmentHelper.setLoadGroupName(ServletRequestUtils.getStringParameter(request, "loadGroupName"));
        enrollmentHelper.setProgramName(ServletRequestUtils.getStringParameter(request, "programName"));
        enrollmentHelper.setSerialNumber(ServletRequestUtils.getRequiredStringParameter(request, "serialNumber"));
        enrollmentHelper.setRelay(ServletRequestUtils.getStringParameter(request, "relay"));
        enrollmentHelper.setApplianceKW(ServletRequestUtils.getFloatParameter(request, "applianceKW", 0));
        enrollmentHelper.setApplianceCategoryName(ServletRequestUtils.getStringParameter(request, "applianceCategoryName"));

        enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.UNENROLL, yukonUserContext.getYukonUser());
        
        return returnMav(request, results);
    }
    
    //====================================================================================================================================
    // ADD A DEVICE TO AN ACCOUNT
    //====================================================================================================================================
    public ModelAndView addDeviceToAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {

        List<String> results = new ArrayList<String>();        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        StarsControllableDeviceDTO deviceInfo = new StarsControllableDeviceDTO();
        deviceInfo.setAccountNumber(ServletRequestUtils.getRequiredStringParameter(request, "accountNumber"));
        deviceInfo.setSerialNumber(ServletRequestUtils.getRequiredStringParameter(request, "serialNumber"));        
        deviceInfo.setDeviceType(ServletRequestUtils.getRequiredStringParameter(request, "deviceType"));
        deviceInfo.setFieldInstallDate(parseDateTime(request, "fieldInstall", userContext));
        deviceInfo.setServiceCompanyName(ServletRequestUtils.getStringParameter(request, "serviceCompanyName"));
        deviceInfo.setDeviceLabel(ServletRequestUtils.getStringParameter(request, "deviceLabel"));        

        starsControllableDeviceHelper.addDeviceToAccount(deviceInfo, userContext.getYukonUser());
        results.add(deviceInfo.toString());
        
        return returnMav(request, results);
    }
    
    //====================================================================================================================================
    // UPDATE A DEVICE ON AN ACCOUNT
    //====================================================================================================================================
    public ModelAndView updateDeviceOnAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {

        List<String> results = new ArrayList<String>();        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        StarsControllableDeviceDTO deviceInfo = new StarsControllableDeviceDTO();
        deviceInfo.setAccountNumber(ServletRequestUtils.getRequiredStringParameter(request, "accountNumber"));
        deviceInfo.setSerialNumber(ServletRequestUtils.getRequiredStringParameter(request, "serialNumber"));        
        deviceInfo.setDeviceType(ServletRequestUtils.getRequiredStringParameter(request, "deviceType"));
        deviceInfo.setFieldInstallDate(parseDateTime(request, "fieldInstall", userContext));
        deviceInfo.setServiceCompanyName(ServletRequestUtils.getStringParameter(request, "serviceCompanyName"));
        deviceInfo.setDeviceLabel(ServletRequestUtils.getStringParameter(request, "deviceLabel"));        

        starsControllableDeviceHelper.updateDeviceOnAccount(deviceInfo, userContext.getYukonUser());
        results.add(deviceInfo.toString());
        
        return returnMav(request, results);
    }
    
    //====================================================================================================================================
    // REMOVE A DEVICE FROM AN ACCOUNT
    //====================================================================================================================================
    public ModelAndView removeDeviceFromAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {

        List<String> results = new ArrayList<String>();        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        StarsControllableDeviceDTO deviceInfo = new StarsControllableDeviceDTO();
        deviceInfo.setAccountNumber(ServletRequestUtils.getRequiredStringParameter(request, "accountNumber"));
        deviceInfo.setSerialNumber(ServletRequestUtils.getRequiredStringParameter(request, "serialNumber"));        
        deviceInfo.setDeviceType(ServletRequestUtils.getRequiredStringParameter(request, "deviceType"));
        deviceInfo.setFieldRemoveDate(parseDateTime(request, "fieldRemove", userContext));

        starsControllableDeviceHelper.removeDeviceFromAccount(deviceInfo, userContext.getYukonUser());
        results.add(deviceInfo.toString());
        
        return returnMav(request, results);
    }    
    
    // HELPERS
    private ModelAndView returnMav(HttpServletRequest request, List<String> results) {
        
        ModelAndView mav = new ModelAndView("loadControlService/inputs/home.jsp");
     
        // re-populate fields
        mav.addAllObjects(ServletUtil.getParameterMap(request));
        mav.addObject("results", results);
        
        return mav;
    }
    
    private Date parseDateTime(HttpServletRequest request, String paramName, YukonUserContext userContext) throws ServletException, java.text.ParseException {
        
        Date date = null;
        String dateStr = ServletRequestUtils.getStringParameter(request, paramName + "Date", null);
        String timeStr = ServletRequestUtils.getStringParameter(request, paramName + "Time", null);
        
        if (dateStr == null) {
            return null;
        }
        
        String d = dateStr;
        if (timeStr != null) {
            d = dateStr + " " + timeStr;
        }
        
        try {
            date = dateformattingService.flexibleDateParser(d, userContext);
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad date: " + d);
        }
        
        return date;
    }
    
    @Autowired
    public void setEnrollmentHelperService(
            EnrollmentHelperService enrollmentHelperService) {
        this.enrollmentHelperService = enrollmentHelperService;
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
    
    @Autowired
    public void setStarsControllableDeviceHelper(
            StarsControllableDeviceHelper starsControllableDeviceHelper) {
        this.starsControllableDeviceHelper = starsControllableDeviceHelper;
    }
}
