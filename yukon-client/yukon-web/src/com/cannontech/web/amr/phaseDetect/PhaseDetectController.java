package com.cannontech.web.amr.phaseDetect;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.phaseDetect.data.Phase;
import com.cannontech.amr.phaseDetect.data.PhaseDetectData;
import com.cannontech.amr.phaseDetect.data.PhaseDetectResult;
import com.cannontech.amr.phaseDetect.data.PhaseDetectState;
import com.cannontech.amr.phaseDetect.service.PhaseDetectService;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Route;
import com.cannontech.common.model.Substation;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.core.substation.dao.SubstationToRouteMappingDao;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RequestMapping("/phaseDetect/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.PHASE_DETECT)
public class PhaseDetectController {
    
    private SubstationToRouteMappingDao strmDao;
    private SubstationDao substationDao;
    private PhaseDetectService phaseDetectService;
    private DeviceDao deviceDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private MeterDao meterDao;
    private PhaseDetectResult resultCopy = null;
    private PhaseDetectData dataCopy = null;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private DeviceGroupService deviceGroupService;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private CommandRequestExecutionDao commandRequestExecutionDao;
    private PaoDefinitionDao paoDefinitionDao;

    @RequestMapping
    public String home(ModelMap model, String errorMsg) throws ServletException {
        if(phaseDetectService.getPhaseDetectData() != null){
            return "redirect:testPage";
        }
        
        List<Substation> substations = substationDao.getAll();
        model.addAttribute("substations", substations);
        
        if(StringUtils.isNotBlank(errorMsg)){
            model.addAttribute("errorMsg", errorMsg);
        }
        return "phaseDetect/home.jsp";
    }
    
    @RequestMapping
    public String routes(ModelMap model, int substationId, HttpServletResponse response) throws ServletException {
        try {
            Substation currentSubstation = substationDao.getById(substationId);
            List<Route> routes = strmDao.getRoutesBySubstationId(substationId);
            Map<Integer, Integer> routeSizeMap = Maps.newHashMap();
            
            int deviceCount = 0;
            
            for(Route route : routes ){
                int routeDeviceCount = deviceDao.getRouteDeviceCount(route.getId());
                routeSizeMap.put(route.getId(), routeDeviceCount);
                deviceCount += routeDeviceCount;
            }
            model.addAttribute("routeSizeMap", routeSizeMap);
            model.addAttribute("deviceCount", deviceCount);
            model.addAttribute("currentSubstation", currentSubstation.getName());
            model.addAttribute("routes", routes);
            model.addAttribute("show", true);
        }catch(NotFoundException e) {
            model.addAttribute("show", false);
        }
        
        return "phaseDetect/routes.jsp";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String saveSubstationAndReadMethod(String readPhasesWhen, Integer selectedSub, ModelMap model, HttpServletRequest request) throws ServletException {
        List<Route> routes = strmDao.getRoutesBySubstationId(selectedSub);
        List<Route> readRoutes = Lists.newArrayList();
        for(Route route : routes){
            int routeId = route.getId();
            String value = request.getParameter("read_route_" + routeId);
            if(StringUtils.isNotBlank(value) && value.equalsIgnoreCase("on")){
                readRoutes.add(route);
            }
        }
        if(readRoutes.isEmpty()){
            return "redirect:home";
        }
        Substation substation = substationDao.getById(selectedSub);
        PhaseDetectData data = new PhaseDetectData();
        PhaseDetectState state = new PhaseDetectState();
        phaseDetectService.initializeResult();
        dataCopy = null;
        resultCopy = null;
        data.setReadRoutes(readRoutes);
        data.setReadAfterAll(readPhasesWhen.equalsIgnoreCase("after") ? true : false);
        data.setSubstationId(selectedSub);
        data.setSubstationName(substation.getName());
        phaseDetectService.setPhaseDetectData(data);
        phaseDetectService.setPhaseDetectState(state);
        
        return "redirect:broadcastRouteSelection";
    }
    
    @RequestMapping
    public String broadcastRouteSelection(ModelMap model){
        model.addAttribute("routes", phaseDetectService.getPhaseDetectData().getReadRoutes());
        return "phaseDetect/broadcastRouteSelection.jsp";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String saveBroadcastRoutes(ModelMap model, HttpServletRequest request, LiteYukonUser user) throws ServletException {
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Test */
            phaseDetectService.cancelTest(user);
            return "redirect:home";
        }
        List<Route> routes = phaseDetectService.getPhaseDetectData().getReadRoutes();
        List<Route> broadcastRoutes = Lists.newArrayList();
        for(Route route : routes){
            String value = request.getParameter("read_route_" + route.getId());
            if(StringUtils.isNotBlank(value) && value.equalsIgnoreCase("on")){
                broadcastRoutes.add(route);
            }
        }
        phaseDetectService.getPhaseDetectData().setBroadcastRoutes(broadcastRoutes);
        return "redirect:clearPhaseData";
    }
    
    @RequestMapping
    public String clearPhaseData(ModelMap model) {
        model.addAttribute("substationName", phaseDetectService.getPhaseDetectData().getSubstationName());
        return "phaseDetect/clearPhaseData.jsp";
    }
    
    
    @RequestMapping(method=RequestMethod.POST)
    public String clear(LiteYukonUser user, ModelMap model, HttpServletRequest request) throws ServletException {
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Test */
            phaseDetectService.cancelTest(user);
            return "redirect:home";
        }
        
        phaseDetectService.clearPhaseData(user);/* Will block until done */
        
        String errorMsg = phaseDetectService.getPhaseDetectResult().getErrorMsg();
        if(StringUtils.isNotBlank(errorMsg)) {
            model.addAttribute("errorReason", StringUtils.abbreviate(errorMsg, 65));
            return "redirect:clearPhaseData";
        }
        return "redirect:testSettings";
    }
    
    @RequestMapping
    public String testSettings(ModelMap model, HttpServletRequest request) {
        model.addAttribute("substationName", phaseDetectService.getPhaseDetectData().getSubstationName());
        return "phaseDetect/testSettings.jsp";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String saveTestSettings(int intervalLength, int deltaVoltage, int numIntervals, ModelMap model, HttpServletRequest request, LiteYukonUser user) throws ServletException {
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Test */
            phaseDetectService.cancelTest(user);
            return "redirect:home";
        }
        if(intervalLength < 15 || intervalLength > 60) intervalLength = 30;
        if(deltaVoltage < -4 || deltaVoltage > 4) deltaVoltage = 2;
        if(numIntervals < 4 || numIntervals > 6) numIntervals = 6;
        phaseDetectService.getPhaseDetectData().setIntervalLength(intervalLength);
        phaseDetectService.getPhaseDetectData().setDeltaVoltage(deltaVoltage);
        phaseDetectService.getPhaseDetectData().setNumIntervals(numIntervals);
        return "redirect:testPage";
    }
    
    @RequestMapping
    public String testPage(ModelMap model) {
        List<SimpleDevice> devicesOnSub = getDevicesOnSub(phaseDetectService.getPhaseDetectData().getSubstationId());
        if(phaseDetectService.getPhaseDetectState() == null){
            return "redirect:home"; /* Redirect to start page if no test is in progress */
        }
        model.addAttribute("data", phaseDetectService.getPhaseDetectData());
        model.addAttribute("state", phaseDetectService.getPhaseDetectState());
        Boolean readCanceled = phaseDetectService.getPhaseDetectState().isReadCanceled();
        CommandRequestExecutionIdentifier identifier = phaseDetectService.getPhaseDetectResult().getCommandRequestExecutionIdentifier();
        if(identifier != null){
            model.addAttribute("showReadProgress", Boolean.TRUE);
            model.addAttribute("readComplete", Boolean.valueOf(commandRequestExecutionDao.isComplete(identifier.getCommandRequestExecutionId())).toString());
            model.addAttribute("readCanceled", readCanceled);
            model.addAttribute("errorMsg", phaseDetectService.getPhaseDetectResult().getErrorMsg());
            model.addAttribute("id", phaseDetectService.getPhaseDetectResult().getCommandRequestExecutionIdentifier().getCommandRequestExecutionId());
            model.addAttribute("totalCount", devicesOnSub.size());
        } else {
            model.addAttribute("showReadProgress", Boolean.FALSE);
        }
        if(phaseDetectService.getPhaseDetectData().isReadAfterAll()){
            return "phaseDetect/phaseTestPage.jsp";
        } else {
            if(readCanceled){
                String phaseName = phaseDetectService.getPhaseDetectState().getCurrentPhaseBeingRead().name();
                model.addAttribute("setPhase" + phaseName, Boolean.TRUE);
            }
            return "phaseDetect/readBetweenPhaseTestPage.jsp";
        }
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public View startTest(String phase, ModelMap model, LiteYukonUser user) {
        Phase phaseEnumValue = Phase.valueOf(phase);
        phaseDetectService.startPhaseDetect(user, phaseEnumValue); /* Will block until done */
        model.addAttribute("phase", phase);
        model.addAttribute("complete", phaseDetectService.getPhaseDetectState().isPhaseDetectComplete());
        String errorMsg = phaseDetectService.getPhaseDetectResult().getErrorMsg();
        if(StringUtils.isNotBlank(errorMsg)) {
            model.addAttribute("errorOccurred", Boolean.TRUE);
            model.addAttribute("errorMsg", StringUtils.abbreviate(errorMsg, 80));
            phaseDetectService.getPhaseDetectState().setTestStep("send");
        } else {
            model.addAttribute("errorOccurred", Boolean.FALSE);
            if(phaseDetectService.getPhaseDetectData().isReadAfterAll()){
                if(phaseDetectService.getPhaseDetectState().isPhaseDetectComplete()){
                    phaseDetectService.getPhaseDetectState().setTestStep("read");
                } else {
                    phaseDetectService.getPhaseDetectState().setTestStep("send");
                }
            } else {
                phaseDetectService.getPhaseDetectState().setTestStep("read");
            }
        }
        
        return new JsonView();
    }
    
    @RequestMapping(method=RequestMethod.POST, value="readPhase")
    public String readPhase(String phase, ModelMap model, LiteYukonUser user, HttpServletResponse response) {
        phaseDetectService.getPhaseDetectState().setReadCanceled(false);
        List<SimpleDevice> devicesOnSub = Lists.newArrayList();
        for(Route route : phaseDetectService.getPhaseDetectData().getReadRoutes()){
            List<SimpleDevice> devicesOnRoute = deviceDao.getDevicesForRouteId(route.getId());
            Iterable<SimpleDevice> phaseDetectDevices = paoDefinitionDao.filterPaosForTag(devicesOnRoute, PaoTag.PHASE_DETECT);
            Iterables.addAll(devicesOnSub, phaseDetectDevices);
        }
        
        Phase phaseValue = null;
        /* send a null when phase is not specified */
        if(StringUtils.isNotBlank(phase)){
            phaseValue = Phase.valueOf(phase);
        }
        try{
            phaseDetectService.readPhaseDetect(devicesOnSub, phaseValue, user);
            phaseDetectService.getPhaseDetectState().setPhaseDetectRead(phaseValue);
            
        } catch(DispatchNotConnectedException e){
            phaseDetectService.getPhaseDetectResult().setErrorMsg(e.getMessage());
        }
        
        JSONObject jsonObject = new JSONObject();
        Boolean success = StringUtils.isBlank(phaseDetectService.getPhaseDetectResult().getErrorMsg());
        jsonObject.put("success", success);
        Boolean complete = phaseDetectService.getPhaseDetectState().isPhaseReadComplete();
        
        if(success){
            if(complete || phaseDetectService.getPhaseDetectData().isReadAfterAll()){
                phaseDetectService.getPhaseDetectState().setTestStep("results");
            } else {
                phaseDetectService.getPhaseDetectState().setTestStep("clear");
            }
        } else {
            phaseDetectService.getPhaseDetectState().setTestStep("read");
        }
        
        if(!phaseDetectService.getPhaseDetectData().isReadAfterAll()){
            jsonObject.put("phase", phase);
            jsonObject.put("complete", complete);
        }
        String jsonStr = jsonObject.toString();
        response.addHeader("X-JSON", jsonStr);

        model.addAttribute("errorMsg", phaseDetectService.getPhaseDetectResult().getErrorMsg());
        model.addAttribute("id", phaseDetectService.getPhaseDetectResult().getCommandRequestExecutionIdentifier().getCommandRequestExecutionId());
        model.addAttribute("totalCount", devicesOnSub.size());
        return "phaseDetect/readPhaseResults.jsp";
    }
    
    @RequestMapping
    public String cancelRead(ModelMap model, LiteYukonUser user){
        phaseDetectService.cancelReadPhaseDetect(user);
        phaseDetectService.getPhaseDetectState().setReadCanceled(true);
        phaseDetectService.getPhaseDetectState().setTestStep("read");
        return "redirect:testPage";
    }
    
    @RequestMapping
    public String cancelTest(ModelMap model, LiteYukonUser user){
        phaseDetectService.cancelTest(user);
        return "redirect:home";
    }
    
    @RequestMapping
    public String sendClearFromTestPage(LiteYukonUser user, ModelMap model, HttpServletResponse response) throws ServletException {
        JSONObject jsonObject = new JSONObject();
        phaseDetectService.clearPhaseData(user);/* Will block until done */
        String errorReason = phaseDetectService.getPhaseDetectResult().getErrorMsg();
        Boolean success = StringUtils.isBlank(errorReason);
        if(success){
            phaseDetectService.getPhaseDetectState().setTestStep("send");
            phaseDetectService.getPhaseDetectResult().setCommandRequestExecutionIdentifier(null);
        }
        jsonObject.put("success", success);
        String jsonStr = jsonObject.toString();
        response.addHeader("X-JSON", jsonStr);
        model.addAttribute("errorReason", errorReason);
        return "phaseDetect/testPageClearResult.jsp";
    }
    
    @RequestMapping
    public String phaseDetectResults(ModelMap model, LiteYukonUser user, HttpServletRequest request) throws ServletException {
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Test */
            phaseDetectService.cancelTest(user);
            return "redirect:home";
        }
        if(phaseDetectService.getPhaseDetectResult() != null){
            phaseDetectService.getPhaseDetectResult().setTestData(phaseDetectService.getPhaseDetectData());
            String cacheKey = phaseDetectService.cacheResults();
            model.addAttribute("cacheKey", cacheKey);
            resultCopy = phaseDetectService.getPhaseDetectResult();
            dataCopy = phaseDetectService.getPhaseDetectData();
            phaseDetectService.setPhaseDetectResult(null);
            phaseDetectService.setPhaseDetectData(null);
            phaseDetectService.setPhaseDetectState(null);
        } else {
            String cacheKey = phaseDetectService.getLastCachedResultKey();
            if(cacheKey != null){
                model.addAttribute("cacheKey", cacheKey);
            }
        }
        model.addAttribute("data", dataCopy);
        model.addAttribute("result", resultCopy);
        List<Meter> phaseAMeters = getMeterListForGroup(resultCopy.getPhaseToGroupMap().get(Phase.A));
        List<Meter> phaseBMeters = getMeterListForGroup(resultCopy.getPhaseToGroupMap().get(Phase.B));
        List<Meter> phaseCMeters = getMeterListForGroup(resultCopy.getPhaseToGroupMap().get(Phase.C));
        List<Meter> undefinedMeters = getUndefinedMeters();
        
        model.addAttribute("phaseAMeters", phaseAMeters);
        model.addAttribute("phaseAMetersSize", phaseAMeters.size());
        model.addAttribute("phaseBMeters", phaseBMeters);
        model.addAttribute("phaseBMetersSize", phaseBMeters.size());
        model.addAttribute("phaseCMeters", phaseCMeters);
        model.addAttribute("phaseCMetersSize", phaseCMeters.size());
        model.addAttribute("undefinedMeters", undefinedMeters);
        model.addAttribute("undefinedMetersSize", undefinedMeters.size());
        Map<Meter, String> failureMetersMap = Maps.newHashMap();
        for(SimpleDevice device : resultCopy.getFailureGroupMap().keySet()){
            Meter meter = meterDao.getForYukonDevice(device);
            String error = resultCopy.getFailureGroupMap().get(device);
            failureMetersMap.put(meter, error);
        }
        model.addAttribute("failureMetersMap", failureMetersMap);
        model.addAttribute("failureMeters", failureMetersMap.keySet());
        model.addAttribute("failureMetersSize", failureMetersMap.keySet().size());
        
        DeviceCollection phaseACollection = deviceGroupCollectionHelper.buildDeviceCollection(resultCopy.getPhaseToGroupMap().get(Phase.A));
        model.addAttribute("phaseACollection", phaseACollection);
        
        DeviceCollection phaseBCollection = deviceGroupCollectionHelper.buildDeviceCollection(resultCopy.getPhaseToGroupMap().get(Phase.B));
        model.addAttribute("phaseBCollection", phaseBCollection);
        
        DeviceCollection phaseCCollection = deviceGroupCollectionHelper.buildDeviceCollection(resultCopy.getPhaseToGroupMap().get(Phase.C));
        model.addAttribute("phaseCCollection", phaseCCollection);
        
        StoredDeviceGroup failureGroup = temporaryDeviceGroupService.createTempGroup(null);
        deviceGroupMemberEditorDao.addDevices(failureGroup, failureMetersMap.keySet());
        DeviceCollection failureCollection = deviceGroupCollectionHelper.buildDeviceCollection(failureGroup);
        model.addAttribute("failureCollection", failureCollection);
        
        StoredDeviceGroup undefinedGroup = temporaryDeviceGroupService.createTempGroup(null);
        deviceGroupMemberEditorDao.addDevices(undefinedGroup, undefinedMeters);
        DeviceCollection undefinedCollection = deviceGroupCollectionHelper.buildDeviceCollection(undefinedGroup);
        model.addAttribute("undefinedCollection", undefinedCollection);
        
        try {
            DeviceGroup abGroup = deviceGroupService.resolveGroupName(SystemGroupEnum.PHASE_DETECT_LAST_RESULTS_AB.getFullPath());
            StoredDeviceGroup abStoredGroup = deviceGroupEditorDao.getStoredGroup(abGroup);
            DeviceCollection phaseABCollection = deviceGroupCollectionHelper.buildDeviceCollection(abStoredGroup);
            model.addAttribute("phaseABMeters", getMeterListForGroup(abStoredGroup));
            model.addAttribute("phaseABMetersSize", phaseABCollection.getDeviceList().size());
            model.addAttribute("phaseABCollection", phaseABCollection);
        } catch (NotFoundException e){
            model.addAttribute("phaseABMetersSize", 0);
        }
        
        try {
            DeviceGroup acGroup = deviceGroupService.resolveGroupName(SystemGroupEnum.PHASE_DETECT_LAST_RESULTS_AC.getFullPath());
            StoredDeviceGroup acStoredGroup = deviceGroupEditorDao.getStoredGroup(acGroup);
            DeviceCollection phaseACCollection = deviceGroupCollectionHelper.buildDeviceCollection(acStoredGroup);
            model.addAttribute("phaseACMeters", getMeterListForGroup(acStoredGroup));
            model.addAttribute("phaseACMetersSize", phaseACCollection.getDeviceList().size());
            model.addAttribute("phaseACCollection", phaseACCollection);
        } catch (NotFoundException e){
            model.addAttribute("phaseACMetersSize", 0);
        }
        
        try {
            DeviceGroup bcGroup = deviceGroupService.resolveGroupName(SystemGroupEnum.PHASE_DETECT_LAST_RESULTS_BC.getFullPath());
            StoredDeviceGroup bcStoredGroup = deviceGroupEditorDao.getStoredGroup(bcGroup);
            DeviceCollection phaseBCCollection = deviceGroupCollectionHelper.buildDeviceCollection(bcStoredGroup);
            model.addAttribute("phaseBCMeters", getMeterListForGroup(bcStoredGroup));
            model.addAttribute("phaseBCMetersSize", phaseBCCollection.getDeviceList().size());
            model.addAttribute("phaseBCCollection", phaseBCCollection);
        } catch (NotFoundException e){
            model.addAttribute("phaseBCMetersSize", 0);
        }
        
        try {
            DeviceGroup abcGroup = deviceGroupService.resolveGroupName(SystemGroupEnum.PHASE_DETECT_LAST_RESULTS_ABC.getFullPath());
            StoredDeviceGroup abcStoredGroup = deviceGroupEditorDao.getStoredGroup(abcGroup);
            DeviceCollection phaseABCCollection = deviceGroupCollectionHelper.buildDeviceCollection(abcStoredGroup);
            model.addAttribute("phaseABCMeters", getMeterListForGroup(abcStoredGroup));
            model.addAttribute("phaseABCMetersSize", phaseABCCollection.getDeviceList().size());
            model.addAttribute("phaseABCCollection", phaseABCCollection);
        } catch (NotFoundException e){
            model.addAttribute("phaseABCMetersSize", 0);
        }
        
        return "phaseDetect/phaseDetectResults.jsp";
    }
    
    @RequestMapping
    public String chartData(ModelMap model) {
        try {
            DeviceGroup aGroup = deviceGroupService.resolveGroupName(SystemGroupEnum.PHASE_DETECT_LAST_RESULTS_A.getFullPath());
            int groupACount = deviceGroupService.getDeviceCount(Collections.singletonList(aGroup));
            model.addAttribute("phaseA", groupACount);
        } catch (NotFoundException e){
            model.addAttribute("phaseA", 0);
        }
        
        try {
            DeviceGroup bGroup = deviceGroupService.resolveGroupName(SystemGroupEnum.PHASE_DETECT_LAST_RESULTS_B.getFullPath());
            int groupBCount = deviceGroupService.getDeviceCount(Collections.singletonList(bGroup));
            model.addAttribute("phaseB", groupBCount);
        } catch (NotFoundException e){
            model.addAttribute("phaseB", 0);
        }
        
        try {
            DeviceGroup cGroup = deviceGroupService.resolveGroupName(SystemGroupEnum.PHASE_DETECT_LAST_RESULTS_C.getFullPath());
            int groupCCount = deviceGroupService.getDeviceCount(Collections.singletonList(cGroup));
            model.addAttribute("phaseC", groupCCount);
        } catch (NotFoundException e){
            model.addAttribute("phaseC", 0);
        }
        
        List<Meter> undefinedMeters = getUndefinedMeters();
        model.addAttribute("undefined", undefinedMeters.size());
        
        if(!dataCopy.isReadAfterAll()){
            try {
                DeviceGroup abGroup = deviceGroupService.resolveGroupName(SystemGroupEnum.PHASE_DETECT_LAST_RESULTS_AB.getFullPath());
                int groupABCount = deviceGroupService.getDeviceCount(Collections.singletonList(abGroup));
                model.addAttribute("phaseAB", groupABCount);
            } catch (NotFoundException e){
                model.addAttribute("phaseAB", 0);
            }
            
            try {
                DeviceGroup acGroup = deviceGroupService.resolveGroupName(SystemGroupEnum.PHASE_DETECT_LAST_RESULTS_AC.getFullPath());
                int groupACCount = deviceGroupService.getDeviceCount(Collections.singletonList(acGroup));
                model.addAttribute("phaseAC", groupACCount);
            } catch (NotFoundException e){
                model.addAttribute("phaseAC", 0);
            }
            
            try {
                DeviceGroup bcGroup = deviceGroupService.resolveGroupName(SystemGroupEnum.PHASE_DETECT_LAST_RESULTS_BC.getFullPath());
                int groupBCCount = deviceGroupService.getDeviceCount(Collections.singletonList(bcGroup));
                model.addAttribute("phaseBC", groupBCCount);
            } catch (NotFoundException e){
                model.addAttribute("phaseBC", 0);
            }
            
            try {
                DeviceGroup abcGroup = deviceGroupService.resolveGroupName(SystemGroupEnum.PHASE_DETECT_LAST_RESULTS_ABC.getFullPath());
                int groupABCCount = deviceGroupService.getDeviceCount(Collections.singletonList(abcGroup));
                model.addAttribute("phaseABC", groupABCCount);
            } catch (NotFoundException e){
                model.addAttribute("phaseABC", 0);
            }
            
            return "phaseDetect/readBetweenPieChartDataFile.jsp";
        }
        
        
        return "phaseDetect/pieChartDataFile.jsp";
    }
    
    @RequestMapping
    public String chartSettings(ModelMap model) {
        return "phaseDetect/pieChartSettingsFile.jsp";
    }
    
    private List<Meter> getUndefinedMeters(){
        List<SimpleDevice> allDevices = getDevicesOnSub(dataCopy.getSubstationId());
        List<Meter> undefinedMeters = Lists.newArrayList();
        List<SimpleDevice> phaseADevices = deviceGroupMemberEditorDao.getChildDevices(resultCopy.getPhaseToGroupMap().get(Phase.A));
        List<SimpleDevice> phaseBDevices = deviceGroupMemberEditorDao.getChildDevices(resultCopy.getPhaseToGroupMap().get(Phase.B));
        List<SimpleDevice> phaseCDevices = deviceGroupMemberEditorDao.getChildDevices(resultCopy.getPhaseToGroupMap().get(Phase.C));
        List<SimpleDevice> phaseReportedDevices = Lists.newArrayList();
        phaseReportedDevices.addAll(phaseADevices);
        phaseReportedDevices.addAll(phaseBDevices);
        phaseReportedDevices.addAll(phaseCDevices);
        phaseReportedDevices.addAll(resultCopy.getFailureGroupMap().keySet());
        
        for(SimpleDevice device : phaseReportedDevices){
            allDevices.remove(device);
        }
        for(SimpleDevice device : allDevices){
            Meter meter = meterDao.getForYukonDevice(device);
            undefinedMeters.add(meter);
        }
        return undefinedMeters;
    }
    
    private List<SimpleDevice> getDevicesOnSub(int subId){
        List<Integer> routeIds = strmDao.getRouteIdsBySubstationId(subId);
        List<SimpleDevice> devicesOnSub = Lists.newArrayList();
        
        for(Integer routeId : routeIds){
            List<SimpleDevice> devices = deviceDao.getDevicesForRouteId(routeId);
            Iterable<SimpleDevice> phaseDetectDevices = paoDefinitionDao.filterPaosForTag(devices, PaoTag.PHASE_DETECT);
            Iterables.addAll(devicesOnSub, phaseDetectDevices);
        }
        
        return devicesOnSub;
    }
    
    private List<Meter> getMeterListForGroup(StoredDeviceGroup group){
        List<SimpleDevice> devices = deviceGroupMemberEditorDao.getChildDevices(group);
        List<Meter> meters = Lists.newArrayList();
        for(SimpleDevice device : devices){
            Meter meter = meterDao.getForYukonDevice(device);
            meters.add(meter);
        }
        return meters;
    }

    @Autowired
    public void setSubstationToRouteMappingDao(final SubstationToRouteMappingDao strmDao) {
        this.strmDao = strmDao;
    }
    
    @Autowired
    public void setSubstationDao(SubstationDao substationDao) {
        this.substationDao = substationDao;
    }
    
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Autowired
    public void setPhaseDetectService(PhaseDetectService phaseDetectService) {
        this.phaseDetectService = phaseDetectService;
    }
    
    @Autowired
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Autowired
    public void setMeterDao(MeterDao meterDao){
        this.meterDao = meterDao;
    }
    
    @Autowired
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }
    
    @Autowired
    public void setTemporaryDeviceGroupService(TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    @Autowired
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    
    @Autowired
    public void setCommandRequestExecutionDao(CommandRequestExecutionDao commandRequestExecutionDao) {
        this.commandRequestExecutionDao = commandRequestExecutionDao;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
}