package com.cannontech.web.dr.setup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.validation.FieldError;
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
import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LMModelFactory;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
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
    private static final String setupRedirectLink = "/dr/setup/filter?filterByType=LOAD_PROGRAM";
    private static final Logger log = YukonLogManager.getLogger(LoadProgramSetupController.class);


    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private LoadProgramSetupControllerHelper controllerHelper;
    @Autowired private ServerDatabaseCache dbCache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private Cache<String, ProgramGear> gearCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build();
    private Cache<String, BindingResult> gearErrorCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build();

    @GetMapping("/create")
    public String create(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);
        LoadProgram loadProgram = new LoadProgram();
        if (model.containsAttribute("loadProgram")) {
            loadProgram = (LoadProgram) model.get("loadProgram");
        }
        controllerHelper.buildProgramModelMap(model, userContext, request, loadProgram);

        if (model.containsAttribute("gearInfos")) {
            List<GearInfo> gearInfos = (List<GearInfo>) model.get("gearInfos");
            model.addAttribute("gearInfos", gearInfos);
        }

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
                return "redirect:" + setupRedirectLink;
            }
            model.addAttribute("selectedSwitchType", loadProgram.getType());
            model.addAttribute("loadProgram", loadProgram);

            buildGearInfo(model, loadProgram);
            controllerHelper.buildNotificationModel(model, loadProgram);

            return "dr/setup/loadProgram/loadProgramView.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
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
                return "redirect:" + setupRedirectLink;
            } else if (model.containsAttribute("loadProgram")) {
                loadProgram = (LoadProgram) model.get("loadProgram");
                loadProgram.setProgramId(id);
            }

            controllerHelper.buildProgramModelMap(model, userContext, request, loadProgram);

            if (model.containsAttribute("gearInfos")) {
                List<GearInfo> gearInfos = (List<GearInfo>) model.get("gearInfos");
                model.addAttribute("gearInfos", gearInfos);
            } else {
                buildGearInfo(model, loadProgram);
            }

            return "dr/setup/loadProgram/loadProgramView.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
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
                    setValidationErrorsForGears(result, flash, loadProgram, baseKey , selectedGearsIds, userContext);
                }
                return bindAndForward(loadProgram, selectedGearsIds, result, redirectAttributes);
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                HashMap<String, Integer> programIdMap = (HashMap<String, Integer>) response.getBody();
                int programId = programIdMap.get("programId");
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "save.success", loadProgram.getName()));
                gearCache.invalidateAll(selectedGearsIds);
                gearErrorCache.invalidateAll(selectedGearsIds);
                return "redirect:/dr/setup/loadProgram/" + programId;
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        } catch (RestClientException ex) {
            log.error("Error creating load program: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "save.error", loadProgram.getName()));
            return "redirect:" + setupRedirectLink;
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
                return "redirect:" + setupRedirectLink;
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        } catch (RestClientException ex) {
            log.error("Error deleting load program: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.error.exception", ex.getMessage()));
            return "redirect:" + setupRedirectLink;
        }
        return "redirect:" + setupRedirectLink;
    }

    @PostMapping("/{id}/copy")
    public String copy(@ModelAttribute("programCopy") LoadProgramCopy programCopy, @PathVariable int id, BindingResult result,
            YukonUserContext userContext, FlashScope flash, ModelMap model, HttpServletRequest request,
            HttpServletResponse servletResponse) throws IOException {
        Map<String, String> json = new HashMap<>();
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadProgramCopyUrl + id);
            ResponseEntity<? extends Object> response = copyProgram(userContext, request, url, programCopy, HttpMethod.POST);

            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(programCopy, "programCopy");
                result = helper.populateBindingError(result, error, response);
               
                return bindAndForwardForCopy(programCopy, userContext, request, result, model, servletResponse, id);
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                HashMap<String, Integer> paoIdMap = (HashMap<String, Integer>) response.getBody();
                int programId = paoIdMap.get("programId");
                json.put("loadProgramId", Integer.toString(programId));
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            json.put("redirectUrl", setupRedirectLink);
            servletResponse.setContentType("application/json");
            JsonUtils.getWriter().writeValue(servletResponse.getOutputStream(), json);
            return null;
        } catch (RestClientException ex) {
            log.error("Error while copying load program: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "copy.error", programCopy.getName()));
            json.put("redirectUrl", setupRedirectLink);
        }

        servletResponse.setContentType("application/json");
        JsonUtils.getWriter().writeValue(servletResponse.getOutputStream(), json);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "copy.success", programCopy.getName()));
        return null;
    }

    /**
     * Load Program - Copy Popup functionality.
     */
    @GetMapping("/{id}/rendercopyloadProgram")
    public String renderCopyLoadProgram(@PathVariable int id, ModelMap model, YukonUserContext userContext,
            HttpServletRequest request) {

        LoadProgramCopy programCopy = null;
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
        LiteYukonPAObject lmProgram = getProgramFromCache(id);
        if (model.containsAttribute("programCopy")) {
            programCopy = (LoadProgramCopy) model.get("programCopy");
        } else {
            programCopy = new LoadProgramCopy();
            programCopy.setName(messageSourceAccessor.getMessage("yukon.common.copyof", lmProgram.getPaoName()));
        }
        controllerHelper.buildProgramCopyModelMap(model, userContext, request, programCopy, lmProgram);

        return "dr/setup/loadProgram/copyLoadProgram.jsp";
    }

    public void setValidationErrorsForGears(BindingResult result, FlashScope flash, LoadProgram loadProgram, String baseKey, List<String> selectedGearsIds, YukonUserContext userContext){
        List<FieldError> errorList = result.getFieldErrors();
        List <Integer> gearPositionIndexes = errorList.stream()
                                                    .filter(fieldError -> fieldError.getField().contains("gears") 
                                                        && !(fieldError.getField().contains("controlMethod")))
                                                    .map(fieldError -> Integer.parseInt(fieldError.getField().replaceAll("[\\D]", "")))
                                                    .distinct()
                                                    .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(loadProgram.getGears())) {
            List<String> filteredList =
                IntStream.range(0, loadProgram.getGears().size())
                         .filter(i -> gearPositionIndexes.contains(i))
                         .mapToObj(loadProgram.getGears()::get)
                         .map(gear -> gear.getGearName())
                         .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(filteredList)) {
                flash.setError(
                    new YukonMessageSourceResolvable(baseKey + "gear.error", String.join(", ", filteredList)));
            }
        }

        CollectionUtils.emptyIfNull(gearPositionIndexes).forEach(index -> {
            String gearId = selectedGearsIds.get(index);
            ProgramGear programGear = gearCache.asMap().get(gearId);
            BindException bindException = new BindException(programGear, "programGear");
            BindingResult bindingResult = new BindException(bindException.getTarget(), bindException.getObjectName());

            List<FieldError> filteredErrors =
                errorList.stream()
                         .filter(fieldError -> fieldError.getField().contains("gears[" + index + "]"))
                         .collect(Collectors.toList());

            filteredErrors.stream().forEach(fieldError -> {
                String fieldName = StringUtils.substringAfter(fieldError.getField(), "gears[" + index + "].");
                String errorMessage =
                    getFieldErrorMessage(fieldName, programGear.getControlMethod(), userContext, fieldError);
                bindingResult.rejectValue(fieldName, "", errorMessage);
            });

            gearErrorCache.put(gearId, bindingResult);
        });

    }

    private String getFieldErrorMessage(String fieldName, GearControlMethod controlMethod, YukonUserContext userContext,
            FieldError fieldError) {

        if (controlMethod == GearControlMethod.SimpleThermostatRamping
            && fieldName.equals("fields.maxRuntimeInMinutes")) {
            MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);
            return messageAccessor.getMessage(baseKey + "error.timeRange", "4:00", "23:59");
        } else
            return fieldError.getDefaultMessage();
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

    public void buildGearInfo(ModelMap model, LoadProgram loadProgram) {

        if (CollectionUtils.isNotEmpty(loadProgram.getGears())) {
            List<GearInfo> gearInfos = new ArrayList<>();
            loadProgram.getGears().forEach(gear -> {
                gearCache.put(String.valueOf(gear.getGearId()), gear);
                GearInfo info = new GearInfo();
                info.setId(gear.getGearId().toString());
                info.setName(gear.getGearName());
                info.setControlMethod(gear.getControlMethod());
                gearInfos.add(info);
            });
            model.addAttribute("gearInfos", gearInfos);
        }

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
    
    /**
     * Returns the LiteYukonPAObject based upon the Load Program Id
     */
    LiteYukonPAObject getProgramFromCache(int programId) {
        LiteYukonPAObject lmProgram = dbCache.getAllLMPrograms().stream()
                                                                .filter(program -> program.getLiteID() == programId)
                                                                .findFirst()
                                                                .get();
        return lmProgram;
    }
    
    /**
     * Get the response for copy
     */
    private ResponseEntity<? extends Object> copyProgram(YukonUserContext userContext, HttpServletRequest request,
            String webserverUrl, LoadProgramCopy programCopy, HttpMethod methodtype) throws RestClientException {
        ResponseEntity<? extends Object> response =
            apiRequestHelper.callAPIForObject(userContext, request, webserverUrl, methodtype, Object.class, programCopy);
        return response;
    }

    private String bindAndForwardForCopy(LoadProgramCopy programCopy, YukonUserContext userContext,
            HttpServletRequest request, BindingResult result, ModelMap model, HttpServletResponse response, int id) {

        model.addAttribute("org.springframework.validation.BindingResult.loadProgramCopy", result);
        LiteYukonPAObject lmProgram = getProgramFromCache(id);
        controllerHelper.buildProgramCopyModelMap(model, userContext, request, programCopy, lmProgram);

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return "dr/setup/loadProgram/copyLoadProgram.jsp";
    }

    @GetMapping("/createGearPopup/{programType}")
    public String createGearPopup(ModelMap model, @PathVariable PaoType programType) {
        model.addAttribute("mode", PageEditMode.CREATE);
        ProgramGear programGear = new ProgramGear();
        if (model.containsAttribute("programGear")) {
            programGear = (ProgramGear) model.get("programGear");
        }
        model.addAttribute("programGear", programGear);
        model.addAttribute("showGearTypeOptions", true);
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
            model.addAttribute("showGearTypeOptions", false);
        } else {
            programGear.setGearName(gearName);
            programGear.setControlMethod(gearType);
            controllerHelper.setDefaultGearFieldValues(programGear);
            model.addAttribute("showGearTypeOptions", true);
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
        gearErrorCache.invalidate(gearId);
        return programGearMap;
    }
    
    @DeleteMapping("/gear/{id}/delete")
    public void deleteGear(@PathVariable String id) {
       gearCache.invalidate(id);
    }

    @GetMapping("/gear/{id}")
    public String gear(ModelMap model, @PathVariable String id, @RequestParam PageEditMode mode, YukonUserContext userContext, HttpServletRequest request) {
        ProgramGear programGear = gearCache.getIfPresent(id);
        model.addAttribute("mode", mode);
        model.addAttribute("selectedGearType", programGear.getControlMethod().name());
        model.addAttribute("programGear", programGear);
        model.addAttribute("showGearTypeOptions", false);
        if (mode == PageEditMode.EDIT || mode == PageEditMode.CREATE) {
            controllerHelper.buildGearModelMap(programGear.getControlMethod(), model, request, userContext);
        }
        if ((programGear.getControlMethod() == GearControlMethod.ThermostatRamping
            || programGear.getControlMethod() == GearControlMethod.SimpleThermostatRamping)) {
            controllerHelper.setDefaultGearFieldValues(programGear);
        }

        BindingResult result = gearErrorCache.asMap().get(id);
        if (result != null && CollectionUtils.isNotEmpty(result.getFieldErrors())) {
            model.put("org.springframework.validation.BindingResult.programGear", result);
        }

        return "dr/setup/programGear/view.jsp";
    }

}
