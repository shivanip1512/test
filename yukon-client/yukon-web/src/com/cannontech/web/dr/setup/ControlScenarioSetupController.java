package com.cannontech.web.dr.setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.ControlScenario;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.ProgramDetails;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@RequestMapping("/setup/controlScenario")
public class ControlScenarioSetupController {

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private IDatabaseCache dbCache;

    private static final String baseKey = "yukon.web.modules.dr.setup.";
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String setupRedirectLink = "/dr/setup/filter?filterByType=CONTROL_SCENARIO";
    private static final Logger log = YukonLogManager.getLogger(ControlScenarioSetupController.class);

    @GetMapping("/create")
    public String create(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);
        ControlScenario controlScenario = null;
        if (model.containsAttribute("controlScenario")) {
            controlScenario = (ControlScenario) model.get("controlScenario");
        } else {
            controlScenario = new ControlScenario();
        }
        populateGears(controlScenario.getAllPrograms(), userContext, request);
        model.addAttribute("controlScenario", controlScenario);
        model.addAttribute("selectedProgramIds", getSelectedProgramIds(controlScenario.getAllPrograms()));
        return "dr/setup/controlScenario/view.jsp";
    }

    private List<Integer> getSelectedProgramIds(List<ProgramDetails> allPrograms) {
        List<Integer> selectedProgramIds = Lists.newArrayList();
        CollectionUtils.emptyIfNull(allPrograms).stream().forEach(program -> {
            selectedProgramIds.add(program.getProgramId());
        });
        return selectedProgramIds;
    }

    @GetMapping("/{id}")
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drControlScenarioRetrieveUrl + id);
            model.addAttribute("mode", PageEditMode.VIEW);
            ControlScenario controlScenario = retrieveScenario(userContext, request, id, url);
            if (controlScenario == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "controlScenario.retrieve.error"));
                return "redirect:" + setupRedirectLink;
            }
            controlScenario.setId(id);
            model.addAttribute("controlScenario", controlScenario);
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        }
        return "dr/setup/controlScenario/view.jsp";
    }

    @GetMapping("/{id}/edit")
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.EDIT);
        try {
            ControlScenario controlScenario = null;
            if (model.containsAttribute("controlScenario")) {
                controlScenario = (ControlScenario) model.get("controlScenario");
            } else {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.drControlScenarioRetrieveUrl + id);
                controlScenario = retrieveScenario(userContext, request, id, url);
                if (controlScenario == null) {
                    flash.setError(new YukonMessageSourceResolvable(baseKey + "controlScenario.retrieve.error"));
                    return "redirect:" + setupRedirectLink;
                }
            }
            populateGears(controlScenario.getAllPrograms(), userContext, request);
            model.addAttribute("selectedProgramIds", getSelectedProgramIds(controlScenario.getAllPrograms()));
            model.addAttribute("controlScenario", controlScenario);
            return "dr/setup/controlScenario/view.jsp";

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        }
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable int id, @ModelAttribute LMDelete lmDelete, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request) {
        try {
            // Api call to delete control scenario
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drControlScenarioDeleteUrl + id);
            ResponseEntity<? extends Object> response =
                apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.DELETE, Object.class, lmDelete);

            if (response.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success", lmDelete.getName()));
                return "redirect:" + setupRedirectLink;
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        } catch (RestClientException ex) {
            log.error("Error deleting Scenario : ", ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.error", lmDelete.getName()));
            return "redirect:" + setupRedirectLink;
        }
        return "redirect:" + setupRedirectLink;
    }

    @PostMapping(value = "/save")
    public String save(@ModelAttribute ControlScenario controlScenario, BindingResult result, FlashScope flash,
            YukonUserContext userContext, RedirectAttributes redirectAttributes, ModelMap model,
            HttpServletRequest request) {
        try {
            ResponseEntity<? extends Object> response = null;
            if (controlScenario.getId() == null) {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.drControlScenarioCreateUrl);
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, Object.class,
                    controlScenario);
            } else {
                String url = helper.findWebServerUrl(request, userContext,
                    ApiURL.drControlScenarioUpdateUrl + controlScenario.getId());
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, Object.class,
                    controlScenario);
            }
            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(controlScenario, "controlScenario");
                result = helper.populateBindingError(result, error, response);
                if (result.hasFieldErrors("allPrograms")) {
                    flash.setError(result.getFieldError("allPrograms"));
                }
                return bindAndForward(controlScenario, result, redirectAttributes);
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                HashMap<String, Integer> paoIdMap = (HashMap<String, Integer>) response.getBody();
                int controlScenarioId = paoIdMap.get("paoId");
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "save.success", controlScenario.getName()));
                return "redirect:/dr/setup/controlScenario/" + controlScenarioId;
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        } catch (RestClientException ex) {
            log.error("Error creating Scenario: ", ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "save.error", controlScenario.getName()));
            return "redirect:" + setupRedirectLink;
        }
        return null;
    }

    @PostMapping("/renderAssignedPrograms")
    public String renderAssignedPrograms(ControlScenario controlScenario, @RequestParam List<Integer> programIds,
            ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        if (CollectionUtils.isEmpty(controlScenario.getAllPrograms())) {
            controlScenario.setAllPrograms(Lists.newArrayList());
        }
        controlScenario.getAllPrograms().addAll(
            programIds.stream().map(programId -> getProgramDetails(programId)).collect(Collectors.toList()));
        populateGears(controlScenario.getAllPrograms(), userContext, request);
        model.addAttribute("controlScenario", controlScenario);
        return "dr/setup/controlScenario/assignedPrograms.jsp";
    }

    private ProgramDetails getProgramDetails(Integer programId) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(programId);
        ProgramDetails programDetails = new ProgramDetails();
        programDetails.setProgramId(pao.getLiteID());
        programDetails.setStartOffsetInMinutes(0);
        programDetails.setStopOffsetInMinutes(0);
        return programDetails;
    }

    /**
     * Make rest call for retrieving control scenario
     */
    private ControlScenario retrieveScenario(YukonUserContext userContext, HttpServletRequest request, int id,
            String url) {
        ControlScenario controlScenario = null;
        try {
            ResponseEntity<? extends Object> response =
                apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.GET, ControlScenario.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                controlScenario = (ControlScenario) response.getBody();
            }
        } catch (RestClientException ex) {
            log.error("Error retrieving Scenario: ", ex);
        }

        return controlScenario;
    }

    private String bindAndForward(ControlScenario controlScenario, BindingResult result,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("controlScenario", controlScenario);
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.controlScenario", result);
        if (controlScenario.getId() == null) {
            return "redirect:/dr/setup/controlScenario/create";
        }
        return "redirect:/dr/setup/controlScenario/" + controlScenario.getId() + "/edit";
    }

    private void populateGears(List<ProgramDetails> allPrograms, YukonUserContext userContext, HttpServletRequest request) {
        CollectionUtils.emptyIfNull(allPrograms).stream().forEach(assignedProgram -> {
            List<LiteGear> allGears = retrieveGears(assignedProgram.getProgramId(), userContext, request);
            if (CollectionUtils.isNotEmpty(assignedProgram.getGears())) {
                LiteGear selectGear = new LiteGear();
                if (assignedProgram.getGears().get(0).getId() != null) {
                    selectGear.setGearID(assignedProgram.getGears().get(0).getId());
                    selectGear.setOwnerID(assignedProgram.getProgramId());
                    int index = allGears.indexOf(selectGear);
                    if (index != -1) {
                        LiteGear gear = allGears.get(index);
                        allGears.remove(selectGear);
                        allGears.add(0, gear);
                    }
                }
            }
            assignedProgram.setGears(allGears.stream()
                                             .map(liteGear -> buildGear(liteGear))
                                             .collect(Collectors.toList()));
        });
    }

    private List<LiteGear> retrieveGears(Integer programId, YukonUserContext userContext, HttpServletRequest request) {
        List<LiteGear> liteGears = new ArrayList<>();
        String url = helper.findWebServerUrl(request, userContext, ApiURL.drGetGearsForLoadProgram + programId);
        try {
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForList(userContext, request, url,
                LiteGear.class, HttpMethod.GET, LiteGear.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                liteGears = (List<LiteGear>) response.getBody();
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
        }
        return liteGears;
    }
    private LMDto buildGear(LiteGear liteGear) {
        return new LMDto(liteGear.getGearID(), liteGear.getGearName());
    }
}
