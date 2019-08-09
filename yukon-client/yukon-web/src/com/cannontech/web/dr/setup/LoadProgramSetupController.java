package com.cannontech.web.dr.setup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LMModelFactory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/setup/loadProgram")
public class LoadProgramSetupController {

    private static final String baseKey = "yukon.web.modules.dr.setup.";
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final Logger log = YukonLogManager.getLogger(LoadProgramSetupController.class);


    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private LoadProgramSetupControllerHelper controllerHelper;
    private Cache<String, ProgramGear> gearCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build();

    @GetMapping("/create")
    public String create(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);
        LoadProgram loadProgram = new LoadProgram();
        if (model.containsAttribute("loadProgram")) {
            loadProgram = (LoadProgram) model.get("loadProgram");
        }
        controllerHelper.buildProgramModelMap(model, userContext, request, loadProgram);

        return "dr/setup/loadProgram/loadProgramView.jsp";
    }

    @GetMapping("/create/{type}")
    public String create(ModelMap model, @PathVariable PaoType type, @RequestParam String name,
            YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);
        LoadProgram loadProgram = new LoadProgram();
        if (model.containsAttribute("loadProgram")) {
            loadProgram = (LoadProgram) model.get("loadProgram");
        } else {
            loadProgram.setName(name);
            loadProgram.setType(type);
            controllerHelper.setDefaultProgramControlWindow(loadProgram);
        }

        controllerHelper.buildProgramModelMap(model, userContext, request, loadProgram);
        return "dr/setup/loadProgram/view.jsp";
    }

    @GetMapping("/{id}")
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadProgramRetrieveUrl + id);
            model.addAttribute("mode", PageEditMode.VIEW);
            LoadProgram loadProgram = retrieveProgram(userContext, request, id, url);
            if (loadProgram == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "loadProgram.retrieve.error"));
                return "redirect:/dr/setup/list";
            }
            model.addAttribute("selectedSwitchType", loadProgram.getType());
            model.addAttribute("loadProgram", loadProgram);

            controllerHelper.buildGearInfo(model, loadProgram);
            controllerHelper.buildNotificationModel(model, loadProgram);
            return "dr/setup/loadProgram/loadProgramView.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        }

    }

    @GetMapping("/{id}/edit")
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadProgramRetrieveUrl + id);
            model.addAttribute("mode", PageEditMode.EDIT);
            LoadProgram loadProgram = retrieveProgram(userContext, request, id, url);

            if (loadProgram == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "loadProgram.retrieve.error"));
                return "redirect:/dr/setup/list";
            } else if (model.containsAttribute("loadProgram")) {
                loadProgram = (LoadProgram) model.get("loadProgram");
                loadProgram.setProgramId(id);
            }

            controllerHelper.buildProgramModelMap(model, userContext, request, loadProgram);
            return "dr/setup/loadProgram/loadProgramView.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        }

    }

    @PostMapping("/save")
    public String save(@ModelAttribute("loadProgram") LoadProgram loadProgram, BindingResult result,
            YukonUserContext userContext, FlashScope flash, RedirectAttributes redirectAttributes,
            HttpServletRequest request, @RequestParam("selectedGroupIds") List<Integer> selectedGroupIds,
            @RequestParam("selectedMemberIds") List<Integer> selectedMemberIds,
            @RequestParam("selectedNotificationGroupIds") List<Integer> selectedNotificationGroupIds,
            @RequestParam("selectedGearsIds") List<String> selectedGearsIds) {

        controllerHelper.buildProgramGroup(loadProgram, selectedGroupIds);
        controllerHelper.buildProgramDirectMemberControl(loadProgram, selectedMemberIds);
        controllerHelper.buildNotificationGroup(loadProgram, selectedNotificationGroupIds);

        if (CollectionUtils.isNotEmpty(selectedGearsIds)) {
            List<ProgramGear> gears = Lists.newArrayList();
            Integer gearOrder = 1;
            for (String gearId : selectedGearsIds) {
                ProgramGear programGear = gearCache.getIfPresent(gearId);
                programGear.setGearNumber(gearOrder);
                gears.add(programGear);
                gearOrder++;
            }
            loadProgram.setGears(gears);
        }

        try {
            String url;
            if (loadProgram.getProgramId() == null) {
                url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadProgramSaveUrl);
            } else {
                url = helper.findWebServerUrl(request, userContext,
                    ApiURL.drLoadProgramUpdateUrl + loadProgram.getProgramId());
            }
            ResponseEntity<? extends Object> response = saveProgram(userContext, request, url, loadProgram, HttpMethod.POST);
            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(loadProgram, "loadProgram");
                result = helper.populateBindingError(result, error, response);
                if (result.hasGlobalErrors()) {

                    List<ObjectError> objectErrorList = result.getGlobalErrors();
                    List<String> errors = objectErrorList.stream()
                                                         .map(obj -> obj.getCode())
                                                         .collect(Collectors.toList());
                    flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(String.join(", ", errors)));

                }

                if (result.hasFieldErrors()) {
                    controllerHelper.setValidationMessageInFlash(result, flash, loadProgram, baseKey);
                }
                return bindAndForward(loadProgram, selectedGearsIds, result, redirectAttributes);
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                HashMap<String, Integer> programIdMap = (HashMap<String, Integer>) response.getBody();
                int programId = programIdMap.get("programId");
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "save.success", loadProgram.getName()));
                gearCache.invalidateAll(selectedGearsIds);
                return "redirect:/dr/setup/loadProgram/" + programId;
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        } catch (RestClientException ex) {
            log.error("Error creating load program: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "save.error", loadProgram.getName()));
            return "redirect:/dr/setup/list";
        }
        return null;
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable int id, @ModelAttribute LMDelete lmDelete, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request) {

        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadProgramDeleteUrl + id);
            ResponseEntity<? extends Object> response = deleteProgram(userContext, request, url, lmDelete);

            if (response.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success", lmDelete.getName()));
                return "redirect:/dr/setup/list";
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        } catch (RestClientException ex) {
            log.error("Error deleting load program: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.error", lmDelete.getName()));
            return "redirect:/dr/setup/list";
        }
        return "dr/setup/list/list.jsp";
    }

    private String bindAndForward(LoadProgram loadProgram, List<String> selectedGearsIds, BindingResult result, RedirectAttributes attrs) {
        attrs.addFlashAttribute("loadProgram", loadProgram);
        
        List<GearInfo>  gearInfos = buildGearInfo(selectedGearsIds);
        if(CollectionUtils.isNotEmpty(gearInfos)) {
            attrs.addFlashAttribute("gearInfos", gearInfos);
        }
       
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.loadProgram", result);
        if (loadProgram.getProgramId() == null) {
            return "redirect:/dr/setup/loadProgram/create";
        }
        return "redirect:/dr/setup/loadProgram/" + loadProgram.getProgramId() + "/edit";
    }

    private List<GearInfo> buildGearInfo(List<String> selectedGearsIds) {

        List<GearInfo> gearInfos = Lists.newArrayList();
        for (String gearId : selectedGearsIds) {
            GearInfo gearInfo = new GearInfo();
            ProgramGear gear = gearCache.getIfPresent(gearId);
            gearInfo.setId(gearId);
            gearInfo.setName(gear.getGearName());
            gearInfo.setControlMethod(gear.getControlMethod());
            gearInfos.add(gearInfo);
        }
        return gearInfos;
    }
    
    /**
     * Make a rest call for retrieving program
     */
    private LoadProgram retrieveProgram(YukonUserContext userContext, HttpServletRequest request, int id, String url) {
        LoadProgram loadProgram = null;
        try {
            ResponseEntity<? extends Object> response =
                apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.GET, LoadProgram.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                loadProgram = (LoadProgram) response.getBody();
                loadProgram.setProgramId(id);
            }

            if (CollectionUtils.isNotEmpty(loadProgram.getGears())) {
                for (ProgramGear gear : loadProgram.getGears()) {
                    gearCache.put(String.valueOf(gear.getGearId()), gear);
                }
            }

        } catch (RestClientException ex) {
            log.error("Error retrieving load program: " + ex.getMessage());
        }
        return loadProgram;
    }

    /**
     * Get the response for save
     */
    private ResponseEntity<? extends Object> saveProgram(YukonUserContext userContext, HttpServletRequest request,
            String webserverUrl, LoadProgram loadProgram, HttpMethod methodtype) throws RestClientException {
        ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request,
            webserverUrl, methodtype, Object.class, loadProgram);
        return response;
    }

    /**
     * Get the response for delete
     */
    private ResponseEntity<? extends Object> deleteProgram(YukonUserContext userContext, HttpServletRequest request,
            String webserverUrl, LMDelete lmDelete) throws RestClientException {
        ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request,
            webserverUrl, HttpMethod.DELETE, Object.class, lmDelete);
        return response;
    }
    
    @GetMapping("/createGearPopup/{programType}")
    public String createGearPopup(ModelMap model, @PathVariable PaoType programType) {
        model.addAttribute("mode", PageEditMode.CREATE);
        ProgramGear programGear = new ProgramGear();
        if (model.containsAttribute("programGear")) {
            programGear = (ProgramGear) model.get("programGear");
        }
        model.addAttribute("programGear", programGear);
        model.addAttribute("programType", programType);
        model.addAttribute("gearTypes", GearControlMethod.getGearTypesByProgramType(programType));

        return "dr/setup/programGear/view.jsp";
    }

    @GetMapping("/populateGearFields/{gearType}")
    public String populateGearFields(ModelMap model, @PathVariable GearControlMethod gearType,
            @RequestParam String gearName, @RequestParam PaoType programType, YukonUserContext userContext,
            HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);
        ProgramGear programGear = new ProgramGear();
        programGear.setFields(LMModelFactory.createProgramGearFields(gearType));
        if (model.containsAttribute("programGear")) {
            programGear = (ProgramGear) model.get("programGear");
        } else {
            programGear.setGearName(gearName);
            programGear.setControlMethod(gearType);
            controllerHelper.setDefaultGearFieldValues(programGear);
        }
        model.addAttribute("programGear", programGear);
        model.addAttribute("programType", programType);
        model.addAttribute("gearTypes", GearControlMethod.getGearTypesByProgramType(programType));
        model.addAttribute("selectedGearType", programGear.getControlMethod());
        controllerHelper.buildGearModelMap(programGear.getControlMethod(), model, request, userContext);
        return "dr/setup/programGear/view.jsp";
    }

    @PostMapping("/gear/save")
    public @ResponseBody Map<String, String> save(@ModelAttribute("programGear") ProgramGear programGear, @RequestParam String tempGearId,
            BindingResult result, YukonUserContext userContext, FlashScope flash, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        Map<String, String> programGearMap = new HashMap<>();
        final String gearId;
        if (StringUtils.isNotBlank(tempGearId)) {
            gearId = tempGearId;
            gearCache.put(tempGearId, programGear);
        } else {
            gearId = UUID.randomUUID().toString().replace("-", "");
            gearCache.put(gearId, programGear);
        }

        programGearMap.put("id", gearId);
        programGearMap.put("gearName", programGear.getGearName());
        return programGearMap;
    }

    @GetMapping("/gear/{id}")
    public String gear(ModelMap model, @PathVariable String id, @RequestParam PageEditMode mode,
            YukonUserContext userContext, HttpServletRequest request) {
        ProgramGear programGear = gearCache.getIfPresent(id);
        model.addAttribute("mode", mode);
        model.addAttribute("selectedGearType", programGear.getControlMethod());
        model.addAttribute("programGear", programGear);
        if (mode == PageEditMode.EDIT || mode == PageEditMode.CREATE) {
            model.addAttribute("gearTypes", GearControlMethod.getGearTypesByProgramType(PaoType.LM_DIRECT_PROGRAM));
            controllerHelper.buildGearModelMap(programGear.getControlMethod(), model, request, userContext);
        }
        if (programGear.getControlMethod() == GearControlMethod.ThermostatRamping
            || programGear.getControlMethod() == GearControlMethod.SimpleThermostatRamping) {
            controllerHelper.setDefaultGearFieldValues(programGear);
        }
        return "dr/setup/programGear/view.jsp";
    }

}
