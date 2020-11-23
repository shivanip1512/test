package com.cannontech.web.amr.phaseDetect;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.phaseDetect.data.PhaseDetectData;
import com.cannontech.amr.phaseDetect.data.PhaseDetectResult;
import com.cannontech.amr.phaseDetect.data.PhaseDetectResultDetail;
import com.cannontech.amr.phaseDetect.data.PhaseDetectState;
import com.cannontech.amr.phaseDetect.service.PhaseDetectCancelledException;
import com.cannontech.amr.phaseDetect.service.PhaseDetectService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
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
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Phase;
import com.cannontech.common.model.Route;
import com.cannontech.common.model.Substation;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.core.substation.dao.SubstationToRouteMappingDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RequestMapping("/phaseDetect/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.PHASE_DETECT)
public class PhaseDetectController {
    
    private static final Logger log = YukonLogManager.getLogger(PhaseDetectController.class);
        
    @Autowired private SubstationToRouteMappingDao strmDao;
    @Autowired private SubstationDao substationDao;
    @Autowired private PhaseDetectService phaseDetectService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private MeterDao meterDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver = null;
    @Autowired @Qualifier("phaseDetect") private RecentResultsCache<PhaseDetectResult> phaseDetectResultsCache;
    
    private enum PhaseDisplay implements DisplayableEnum {
        PHASE_A(YukonColorPalette.BLUE),
        PHASE_B(YukonColorPalette.GREEN),
        PHASE_C(YukonColorPalette.ORANGE),
        PHASE_AB(YukonColorPalette.PURPLE),
        PHASE_AC(YukonColorPalette.YELLOW),
        PHASE_BC(YukonColorPalette.WINE),
        PHASE_ABC(YukonColorPalette.TEAL),
        UNDEFINED(YukonColorPalette.GRAY);
        
        private YukonColorPalette color;

        PhaseDisplay(YukonColorPalette color) {
            this.color = color;
        }
        
        public YukonColorPalette getColor() {
            return color;
        }

		@Override
		public String getFormatKey() {
			return "yukon.web.modules.amr.phaseDetect.results." + this.name();
		}
    }

    @RequestMapping("home")
    public String home(ModelMap model, String errorMsg) {
        try {
            phaseDetectService.getPhaseDetectResult();
            return "redirect:testPage";
        } catch (PhaseDetectCancelledException e) {
            /* we don't have a previous test to show them. no biggie - move along */
            log.debug("no previous phase detection tests to display, continuing on to test home");
        }
        
        List<Substation> substations = substationDao.getAll();
        model.addAttribute("substations", substations);
        
        if(StringUtils.isNotBlank(errorMsg)){
            model.addAttribute("errorMsg", errorMsg);
        }
        return "phaseDetect/home.jsp";
    }
    
    @RequestMapping(value = "routes", method = RequestMethod.GET)
    public String routes(ModelMap model, int substationId) {
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
    
    @RequestMapping(value="saveSubstationAndReadMethod", method=RequestMethod.POST)
    public String saveSubstationAndReadMethod(String readPhasesWhen, Integer selectedSub, HttpServletRequest request) {
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
        data.setReadRoutes(readRoutes);
        data.setReadAfterAll(readPhasesWhen.equalsIgnoreCase("after") ? true : false);
        data.setSubstationId(selectedSub);
        data.setSubstationName(substation.getName());
        phaseDetectService.getPhaseDetectResult().setTestData(data);
        phaseDetectService.setPhaseDetectState(state);
        
        return "redirect:broadcastRouteSelection";
    }
    
    @RequestMapping("broadcastRouteSelection")
    public String broadcastRouteSelection(ModelMap model){
        model.addAttribute("routes", phaseDetectService.getPhaseDetectResult().getTestData().getReadRoutes());
        return "phaseDetect/broadcastRouteSelection.jsp";
    }
    
    @RequestMapping(value="saveBroadcastRoutes", method=RequestMethod.POST)
    public String saveBroadcastRoutes(ModelMap model, HttpServletRequest request, LiteYukonUser user) throws ServletException {
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Test */
            phaseDetectService.cancelTest(user);
            return "redirect:home";
        }
        List<Route> routes = phaseDetectService.getPhaseDetectResult().getTestData().getReadRoutes();
        List<Route> broadcastRoutes = Lists.newArrayList();
        for(Route route : routes){
            String value = request.getParameter("read_route_" + route.getId());
            if(StringUtils.isNotBlank(value) && value.equalsIgnoreCase("on")){
                broadcastRoutes.add(route);
            }
        }
        phaseDetectService.getPhaseDetectResult().getTestData().setBroadcastRoutes(broadcastRoutes);
        return "redirect:clearPhaseData";
    }
    
    @RequestMapping("clearPhaseData")
    public String clearPhaseData(ModelMap model) {
        model.addAttribute("substationName", phaseDetectService.getPhaseDetectResult().getTestData().getSubstationName());
        return "phaseDetect/clearPhaseData.jsp";
    }
    
    @RequestMapping(value="clear", method=RequestMethod.POST)
    public String clear(LiteYukonUser user, ModelMap model, HttpServletRequest request) throws ServletException {
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Test */
            phaseDetectService.cancelTest(user);
            return "redirect:home";
        }
        
        phaseDetectService.clearPhaseData(user);/* Will block until done */
        
        try {
            String errorMsg = phaseDetectService.getPhaseDetectResult().getErrorMsg();
            if(StringUtils.isNotBlank(errorMsg)) {
                model.addAttribute("errorReason", StringUtils.abbreviate(errorMsg, 65));
                return "redirect:clearPhaseData";
            }
        } catch (PhaseDetectCancelledException e) {
            /* user canceled the test - no biggie */
            log.debug("could not clear phase detect test because user canceled it");
        }
        return "redirect:testSettings";
    }
    
    @RequestMapping("testSettings")
    public String testSettings(ModelMap model, HttpServletRequest request) {
        model.addAttribute("substationName", phaseDetectService.getPhaseDetectResult().getTestData().getSubstationName());
        return "phaseDetect/testSettings.jsp";
    }
    
    @RequestMapping(value="saveTestSettings", method=RequestMethod.POST)
    public String saveTestSettings(int intervalLength, int deltaVoltage, int numIntervals, ModelMap model, HttpServletRequest request, LiteYukonUser user) throws ServletException {
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Test */
            phaseDetectService.cancelTest(user);
            return "redirect:home";
        }
        if (intervalLength < 15 || intervalLength > 60) intervalLength = 30;
        if (deltaVoltage < -4 || deltaVoltage > 4) deltaVoltage = 2;
        if (numIntervals < 4 || numIntervals > 6) numIntervals = 6;
        phaseDetectService.getPhaseDetectResult().getTestData().setIntervalLength(intervalLength);
        phaseDetectService.getPhaseDetectResult().getTestData().setDeltaVoltage(deltaVoltage);
        phaseDetectService.getPhaseDetectResult().getTestData().setNumIntervals(numIntervals);
        return "redirect:testPage";
    }
    
    @RequestMapping("testPage")
    public String testPage(ModelMap model, YukonUserContext userContext) {
        List<SimpleDevice> devicesOnSub = getDevicesOnSub(phaseDetectService.getPhaseDetectResult().getTestData().getSubstationId());
        if(phaseDetectService.getPhaseDetectState() == null){
            return "redirect:home"; /* Redirect to start page if no test is in progress */
        }

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String errorDetectMessage = accessor.getMessage("yukon.web.modules.amr.phaseDetect.sendTest.errorDetect",
                                                                     phaseDetectService.getPhaseDetectResult().getErrorMsg());
        String errorReadMessage = accessor.getMessage("yukon.web.modules.amr.phaseDetect.sendTest.errorRead",
                                                                   phaseDetectService.getPhaseDetectResult().getErrorMsg());
        
        model.addAttribute("data", phaseDetectService.getPhaseDetectResult().getTestData());
        model.addAttribute("state", phaseDetectService.getPhaseDetectState());
        model.addAttribute("phases", Phase.getRealPhases());
        model.addAttribute("phaseA", Phase.A);
        model.addAttribute("phaseB", Phase.B);
        model.addAttribute("phaseC", Phase.C);
        Boolean readCanceled = phaseDetectService.getPhaseDetectState().isReadCanceled();
        CommandRequestExecutionIdentifier identifier = phaseDetectService.getPhaseDetectResult().getCommandRequestExecutionIdentifier();
        if(identifier != null){
            model.addAttribute("showReadProgress", Boolean.TRUE);
            model.addAttribute("readComplete", Boolean.valueOf(commandRequestExecutionDao.isComplete(identifier.getCommandRequestExecutionId())).toString());
            model.addAttribute("readCanceled", readCanceled);
            model.addAttribute("errorDetectMsg", errorDetectMessage );
            model.addAttribute("errorReadMsg", errorReadMessage);
            model.addAttribute("id", phaseDetectService.getPhaseDetectResult().getCommandRequestExecutionIdentifier().getCommandRequestExecutionId());
            model.addAttribute("totalCount", devicesOnSub.size());
        } else {
            model.addAttribute("showReadProgress", Boolean.FALSE);
        }
        if(phaseDetectService.getPhaseDetectResult().getTestData().isReadAfterAll()){
            return "phaseDetect/phaseTestPage.jsp";
        } else {
            if(readCanceled){
                String phaseName = phaseDetectService.getPhaseDetectState().getCurrentPhaseBeingRead().name();
                model.addAttribute("setPhase" + phaseName, Boolean.TRUE);
            }
            return "phaseDetect/readBetweenPhaseTestPage.jsp";
        }
    }
    
    @RequestMapping(value="startTest", method=RequestMethod.POST)
    public @ResponseBody Map<String, Object> startTest(String phase, LiteYukonUser user) {
        
        Map<String , Object> json = new HashMap<>();
        
        try {
            Phase phaseEnumValue = Phase.valueOf(phase);
            phaseDetectService.startPhaseDetect(user, phaseEnumValue); /* Will block until done */
            json.put("phase", phase);
            json.put("complete", phaseDetectService.getPhaseDetectState().isPhaseDetectComplete());
            String errorMsg = phaseDetectService.getPhaseDetectResult().getErrorMsg();
            if (StringUtils.isNotBlank(errorMsg)) {
                json.put("errorOccurred", true);
                json.put("errorMsg", StringUtils.abbreviate(errorMsg, 80));
                phaseDetectService.getPhaseDetectState().setTestStep("send");
            } else {
                json.put("errorOccurred", false);
                if(phaseDetectService.getPhaseDetectResult().getTestData().isReadAfterAll()){
                    if(phaseDetectService.getPhaseDetectState().isPhaseDetectComplete()){
                        phaseDetectService.getPhaseDetectState().setTestStep("read");
                    } else {
                        phaseDetectService.getPhaseDetectState().setTestStep("send");
                    }
                } else {
                    phaseDetectService.getPhaseDetectState().setTestStep("read");
                }
            }
        } catch (PhaseDetectCancelledException e) {
            /* ignore since this means the user cancelled the test right away */
            log.debug("user canceled phase detection before test could be started");
        }
        
        return json;
    }
    
    @RequestMapping(method=RequestMethod.POST, value="readPhase")
    public String readPhase(String phase, ModelMap model, LiteYukonUser user, HttpServletResponse response)
            throws JsonProcessingException {
        phaseDetectService.getPhaseDetectState().setReadCanceled(false);
        List<SimpleDevice> devicesOnSub = Lists.newArrayList();
        for(Route route : phaseDetectService.getPhaseDetectResult().getTestData().getReadRoutes()){
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
        
        Map<String, Object> json = new HashMap<>();
        boolean success = StringUtils.isBlank(phaseDetectService.getPhaseDetectResult().getErrorMsg());
        json.put("success", success);
        boolean complete = phaseDetectService.getPhaseDetectState().isPhaseReadComplete();
        
        if (success) {
            if (complete || phaseDetectService.getPhaseDetectResult().getTestData().isReadAfterAll()){
                phaseDetectService.getPhaseDetectState().setTestStep("results");
            } else {
                phaseDetectService.getPhaseDetectState().setTestStep("clear");
            }
        } else {
            phaseDetectService.getPhaseDetectState().setTestStep("read");
        }
        
        if (!phaseDetectService.getPhaseDetectResult().getTestData().isReadAfterAll()) {
            json.put("phase", phase);
            json.put("complete", complete);
        }
        response.addHeader("X-JSON", JsonUtils.toJson(json));

        model.addAttribute("errorMsg", phaseDetectService.getPhaseDetectResult().getErrorMsg());
        model.addAttribute("id", phaseDetectService.getPhaseDetectResult().getCommandRequestExecutionIdentifier().getCommandRequestExecutionId());
        model.addAttribute("totalCount", devicesOnSub.size());
        
        return "phaseDetect/readPhaseResults.jsp";
    }
    
    @RequestMapping("cancelRead")
    public String cancelRead(ModelMap model, LiteYukonUser user) {
        
        phaseDetectService.cancelReadPhaseDetect(user);
        phaseDetectService.getPhaseDetectState().setReadCanceled(true);
        phaseDetectService.getPhaseDetectState().setTestStep("read");
        
        return "redirect:testPage";
    }
    
    @RequestMapping("cancelTest")
    public String cancelTest(LiteYukonUser user) {
        
        phaseDetectService.cancelTest(user);
        
        return "redirect:home";
    }
    
    @RequestMapping("sendClearFromTestPage")
    public String sendClearFromTestPage(LiteYukonUser user, ModelMap model, HttpServletResponse response) throws JsonProcessingException {
        
        phaseDetectService.clearPhaseData(user);/* Will block until done */
        String errorReason = phaseDetectService.getPhaseDetectResult().getErrorMsg();
        Boolean success = StringUtils.isBlank(errorReason);
        if(success){
            phaseDetectService.getPhaseDetectState().setTestStep("send");
            phaseDetectService.getPhaseDetectResult().setCommandRequestExecutionIdentifier(null);
        }
        response.addHeader("X-JSON", JsonUtils.toJson(Collections.singletonMap("success", success)));
        model.addAttribute("errorReason", errorReason);
        
        return "phaseDetect/testPageClearResult.jsp";
    }
    
    @RequestMapping("phaseDetectResults")
    public String phaseDetectResults(ModelMap model, LiteYukonUser user, HttpServletRequest request) throws ServletException {
        
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Test */
            phaseDetectService.cancelTest(user);
            return "redirect:home";
        }
        
        PhaseDetectResult result = null;
        String cacheKey = null;
        try {
            result = phaseDetectService.getPhaseDetectResult();
            cacheKey = phaseDetectService.cacheResults();
            phaseDetectService.setPhaseDetectResult(null);
            phaseDetectService.setPhaseDetectState(null);
        } catch (PhaseDetectCancelledException e) {
            cacheKey = phaseDetectService.getLastCachedResultKey();
            if (cacheKey != null) {
                result = phaseDetectResultsCache.getResult(cacheKey);
            }
        }
        
        if (result == null) {
            throw new IllegalArgumentException("No Results");
        }
        
        model.addAttribute("cacheKey", cacheKey);
        
        model.addAttribute("data", result.getTestData());
        model.addAttribute("result", result);
        List<PlcMeter> phaseAMeters = getMeterListForGroup(result.getPhaseToGroupMap().get(Phase.A));
        List<PlcMeter> phaseBMeters = getMeterListForGroup(result.getPhaseToGroupMap().get(Phase.B));
        List<PlcMeter> phaseCMeters = getMeterListForGroup(result.getPhaseToGroupMap().get(Phase.C));
        List<PlcMeter> undefinedMeters = getUndefinedMeters(result);
        
        model.addAttribute("phaseAMeters", phaseAMeters);
        model.addAttribute("phaseAMetersSize", phaseAMeters.size());
        model.addAttribute("phaseBMeters", phaseBMeters);
        model.addAttribute("phaseBMetersSize", phaseBMeters.size());
        model.addAttribute("phaseCMeters", phaseCMeters);
        model.addAttribute("phaseCMetersSize", phaseCMeters.size());
        model.addAttribute("undefinedMeters", undefinedMeters);
        model.addAttribute("undefinedMetersSize", undefinedMeters.size());
        Map<PlcMeter, String> failureMetersMap = Maps.newHashMap();
        
        for (SimpleDevice device : result.getFailureGroupMap().keySet()) {
            PlcMeter meter = meterDao.getPlcMeterForId(device.getDeviceId());
            String error = result.getFailureGroupMap().get(device);
            failureMetersMap.put(meter, error);
        }
        model.addAttribute("failureMetersMap", failureMetersMap);
        model.addAttribute("failureMeters", failureMetersMap.keySet());
        model.addAttribute("failureMetersSize", failureMetersMap.keySet().size());
        
        DeviceCollection phaseACollection = deviceGroupCollectionHelper.buildDeviceCollection(result.getPhaseToGroupMap().get(Phase.A));
        model.addAttribute("phaseACollection", phaseACollection);
        
        DeviceCollection phaseBCollection = deviceGroupCollectionHelper.buildDeviceCollection(result.getPhaseToGroupMap().get(Phase.B));
        model.addAttribute("phaseBCollection", phaseBCollection);
        
        DeviceCollection phaseCCollection = deviceGroupCollectionHelper.buildDeviceCollection(result.getPhaseToGroupMap().get(Phase.C));
        model.addAttribute("phaseCCollection", phaseCCollection);
        
        StoredDeviceGroup failureGroup = temporaryDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(failureGroup, failureMetersMap.keySet());
        DeviceCollection failureCollection = deviceGroupCollectionHelper.buildDeviceCollection(failureGroup);
        model.addAttribute("failureCollection", failureCollection);
        
        StoredDeviceGroup undefinedGroup = temporaryDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(undefinedGroup, undefinedMeters);
        DeviceCollection undefinedCollection = deviceGroupCollectionHelper.buildDeviceCollection(undefinedGroup);
        model.addAttribute("undefinedCollection", undefinedCollection);
        
        try {
            StoredDeviceGroup abStoredGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.AB);
            DeviceCollection phaseABCollection = deviceGroupCollectionHelper.buildDeviceCollection(abStoredGroup);
            model.addAttribute("phaseABMeters", getMeterListForGroup(abStoredGroup));
            model.addAttribute("phaseABMetersSize", phaseABCollection.getDeviceList().size());
            model.addAttribute("phaseABCollection", phaseABCollection);
        } catch (NotFoundException e){
            model.addAttribute("phaseABMetersSize", 0);
        }
        
        try {
            StoredDeviceGroup acStoredGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.AC);
            DeviceCollection phaseACCollection = deviceGroupCollectionHelper.buildDeviceCollection(acStoredGroup);
            model.addAttribute("phaseACMeters", getMeterListForGroup(acStoredGroup));
            model.addAttribute("phaseACMetersSize", phaseACCollection.getDeviceList().size());
            model.addAttribute("phaseACCollection", phaseACCollection);
        } catch (NotFoundException e){
            model.addAttribute("phaseACMetersSize", 0);
        }
        
        try {
            DeviceGroup bcGroup =  deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.BC);
            StoredDeviceGroup bcStoredGroup = deviceGroupEditorDao.getStoredGroup(bcGroup);
            DeviceCollection phaseBCCollection = deviceGroupCollectionHelper.buildDeviceCollection(bcStoredGroup);
            model.addAttribute("phaseBCMeters", getMeterListForGroup(bcStoredGroup));
            model.addAttribute("phaseBCMetersSize", phaseBCCollection.getDeviceList().size());
            model.addAttribute("phaseBCCollection", phaseBCCollection);
        } catch (NotFoundException e){
            model.addAttribute("phaseBCMetersSize", 0);
        }
        
        try {
            DeviceGroup abcGroup =  deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.ABC);
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
    
    @RequestMapping("chart")
    public @ResponseBody Map<String, Object> chart(String key, YukonUserContext userContext) {
        
        Map<PhaseDisplay, Integer> phaseResultsMap = getChartPhaseResults(phaseDetectResultsCache.getResult(key));
        int total = phaseResultsMap.values().stream().reduce(0, Integer::sum);
        List<PhaseDetectResultDetail> phaseDetectDetails = Lists.newArrayList();
        Map<String, Object> phaseDetectResults = Maps.newHashMap();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        phaseResultsMap.forEach((mapKey, value) -> {
            PhaseDetectResultDetail details = new PhaseDetectResultDetail();
            details.setMeterCount(value);
            details.calculatePrecentage(total);
            details.setPhase(accessor.getMessage(mapKey.getFormatKey()));
            details.setColorHexValue(mapKey.getColor().getHexValue());
            phaseDetectDetails.add(details);
        });
        phaseDetectResults.put("phaseDetectDetails", phaseDetectDetails);
        return phaseDetectResults;
    }

    private Map<PhaseDisplay, Integer> getChartPhaseResults(PhaseDetectResult result) {
        
        Map<PhaseDisplay, Integer> phaseResultsMap = Maps.newHashMap();
        try {
            DeviceGroup aGroup =  deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.A);
            int groupACount = deviceGroupService.getDeviceCount(Collections.singletonList(aGroup));
            phaseResultsMap.put(PhaseDisplay.PHASE_A, groupACount);
        } catch (NotFoundException e){
            phaseResultsMap.put(PhaseDisplay.PHASE_A, 0);
        }
        
        try {
            DeviceGroup bGroup =  deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.B);
            int groupBCount = deviceGroupService.getDeviceCount(Collections.singletonList(bGroup));
            phaseResultsMap.put(PhaseDisplay.PHASE_B, groupBCount);
        } catch (NotFoundException e){
            phaseResultsMap.put(PhaseDisplay.PHASE_B, 0);
        }
        
        try {
            DeviceGroup cGroup =  deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.C);
            int groupCCount = deviceGroupService.getDeviceCount(Collections.singletonList(cGroup));
            phaseResultsMap.put(PhaseDisplay.PHASE_C, groupCCount);
        } catch (NotFoundException e){
            phaseResultsMap.put(PhaseDisplay.PHASE_C, 0);
        }
        
        List<PlcMeter> undefinedMeters = getUndefinedMeters(result);
        phaseResultsMap.put(PhaseDisplay.UNDEFINED, undefinedMeters.size());
        
        if (!result.getTestData().isReadAfterAll()) {
            try {
                DeviceGroup abGroup =  deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.AB);
                int groupABCount = deviceGroupService.getDeviceCount(Collections.singletonList(abGroup));
                phaseResultsMap.put(PhaseDisplay.PHASE_AB, groupABCount);
            } catch (NotFoundException e){
                phaseResultsMap.put(PhaseDisplay.PHASE_AB, 0);
            }
            
            try {
                DeviceGroup acGroup =  deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.AC);
                int groupACCount = deviceGroupService.getDeviceCount(Collections.singletonList(acGroup));
                phaseResultsMap.put(PhaseDisplay.PHASE_AC, groupACCount);
            } catch (NotFoundException e){
                phaseResultsMap.put(PhaseDisplay.PHASE_AC, 0);
            }
            
            try {
                DeviceGroup bcGroup =  deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.BC);
                int groupBCCount = deviceGroupService.getDeviceCount(Collections.singletonList(bcGroup));
                phaseResultsMap.put(PhaseDisplay.PHASE_BC, groupBCCount);
            } catch (NotFoundException e){
                phaseResultsMap.put(PhaseDisplay.PHASE_BC, 0);
            }
            
            try {
                DeviceGroup abcGroup =  deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.ABC);
                int groupABCCount = deviceGroupService.getDeviceCount(Collections.singletonList(abcGroup));
                phaseResultsMap.put(PhaseDisplay.PHASE_ABC, groupABCCount);
            } catch (NotFoundException e){
                phaseResultsMap.put(PhaseDisplay.PHASE_ABC, 0);
            }
        }
        return phaseResultsMap;
    }
    
    private List<PlcMeter> getUndefinedMeters(PhaseDetectResult result) {
        List<SimpleDevice> allDevices = getDevicesOnSub(result.getTestData().getSubstationId());
        List<PlcMeter> undefinedMeters = Lists.newArrayList();
        List<SimpleDevice> phaseADevices = deviceGroupMemberEditorDao.getChildDevices(result.getPhaseToGroupMap().get(Phase.A));
        List<SimpleDevice> phaseBDevices = deviceGroupMemberEditorDao.getChildDevices(result.getPhaseToGroupMap().get(Phase.B));
        List<SimpleDevice> phaseCDevices = deviceGroupMemberEditorDao.getChildDevices(result.getPhaseToGroupMap().get(Phase.C));
        List<SimpleDevice> phaseReportedDevices = Lists.newArrayList();
        phaseReportedDevices.addAll(phaseADevices);
        phaseReportedDevices.addAll(phaseBDevices);
        phaseReportedDevices.addAll(phaseCDevices);
        phaseReportedDevices.addAll(result.getFailureGroupMap().keySet());
        
        for (SimpleDevice device : phaseReportedDevices) {
            allDevices.remove(device);
        }
        for (SimpleDevice device : allDevices) {
            PlcMeter meter = meterDao.getPlcMeterForId(device.getDeviceId());
            undefinedMeters.add(meter);
        }
        return undefinedMeters;
    }
    
    private List<SimpleDevice> getDevicesOnSub(int subId) {
        List<Integer> routeIds = strmDao.getRouteIdsBySubstationId(subId);
        List<SimpleDevice> devicesOnSub = Lists.newArrayList();
        
        for(Integer routeId : routeIds){
            List<SimpleDevice> devices = deviceDao.getDevicesForRouteId(routeId);
            Iterable<SimpleDevice> phaseDetectDevices = paoDefinitionDao.filterPaosForTag(devices, PaoTag.PHASE_DETECT);
            Iterables.addAll(devicesOnSub, phaseDetectDevices);
        }
        
        return devicesOnSub;
    }
    
    private List<PlcMeter> getMeterListForGroup(StoredDeviceGroup group) {
        List<SimpleDevice> devices = deviceGroupMemberEditorDao.getChildDevices(group);
        List<PlcMeter> meters = Lists.newArrayList();
        for(SimpleDevice device : devices){
            PlcMeter meter = meterDao.getPlcMeterForId(device.getDeviceId());
            meters.add(meter);
        }
        return meters;
    }
}