package com.cannontech.web.dr.setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.DayOfWeek;
import com.cannontech.common.dr.setup.HolidayUsage;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;

@Controller
@RequestMapping("/setup/constraint")
public class ProgramConstraintController {

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private ProgramConstraintControllerHelper programConstraintControllerHelper;

    private static final String baseKey = "yukon.web.modules.dr.setup.";
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final Logger log = YukonLogManager.getLogger(ProgramConstraintController.class);

    @GetMapping("/create")
    public String create(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);
        ProgramConstraint programConstraint = new ProgramConstraint();
        if (model.containsAttribute("programConstraint")) {
            programConstraint = (ProgramConstraint) model.get("programConstraint");
        }
        programConstraintControllerHelper.setDefaultValues(programConstraint);
        return setupModel(programConstraint, model, userContext, request);
    }

    @GetMapping("/{id}")
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        ProgramConstraint programConstraint = null;
        try {
            model.addAttribute("mode", PageEditMode.VIEW);
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drProgramConstraintRetrieveUrl + id);
            programConstraint = retrieveConstraint(userContext, request, id, url);
            if (programConstraint == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "constraint.retrieve.error"));
                return "redirect:/dr/setup/list";
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        }
        programConstraintControllerHelper.setDefaultValues(programConstraint);
        return setupModel(programConstraint, model, userContext, request);
    }

    @GetMapping("/{id}/edit")
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.EDIT);
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drProgramConstraintRetrieveUrl + id);
            ProgramConstraint programConstraint = retrieveConstraint(userContext, request, id, url);
            if (programConstraint == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "constraint.retrieve.error"));
                return "redirect:/dr/setup/list";
            } else if (model.containsAttribute("programConstraint")) {
                programConstraint = (ProgramConstraint) model.get("programConstraint");
            }
            return setupModel(programConstraint, model, userContext, request);

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        }
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("programConstraint") ProgramConstraint programConstraint, BindingResult result,
            YukonUserContext userContext, FlashScope flash, RedirectAttributes redirectAttributes, ModelMap model,
            HttpServletRequest request) {
        try {
            String url = StringUtils.EMPTY;
            ResponseEntity<? extends Object> response = null;
            if (programConstraint.getHolidaySchedule().getId() == 0) {
                programConstraint.setHolidayUsage(HolidayUsage.NONE);
            }
            if (programConstraint.getId() == null) {
                url = helper.findWebServerUrl(request, userContext, ApiURL.drProgramConstraintCreateUrl);
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, Object.class,
                    programConstraint);
            } else {
                url = helper.findWebServerUrl(request, userContext,
                    ApiURL.drProgramConstraintUpdateUrl + programConstraint.getId());
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, Object.class,
                    programConstraint);
            }
            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(programConstraint, "programConstraint");
                result = helper.populateBindingError(result, error, response);
                return bindAndForward(programConstraint, result, redirectAttributes);
            }
            if (response.getStatusCode() == HttpStatus.OK) {
                HashMap<String, Integer> constraintIdMap = (HashMap<String, Integer>) response.getBody();
                int constraintId = constraintIdMap.get("id");
                flash.setConfirm(
                    new YukonMessageSourceResolvable(baseKey + "save.success", programConstraint.getName()));
                return "redirect:/dr/setup/constraint/" + constraintId;
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        } catch (RestClientException ex) {
            log.error("Error creating program constraint: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "save.error", programConstraint.getName()));
            return "redirect:/dr/setup/list";
        }
        return null;
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable int id, @ModelAttribute LMDelete lmDelete, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drProgramConstraintDeleteUrl + id);
            ResponseEntity<? extends Object> response =
                apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.DELETE, Object.class, lmDelete);
            if (response.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success", lmDelete.getName()));
                return "redirect:/dr/setup/list";
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        } catch (RestClientException ex) {
            log.error("Error deleting program constraint : " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.error", lmDelete.getName()));
            return "redirect:/dr/setup/list";
        }
        return "dr/setup/list.jsp";
    }

    private String bindAndForward(ProgramConstraint programConstraint, BindingResult result,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("programConstraint", programConstraint);
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.programConstraint", result);
        if (programConstraint.getId() == null) {
            return "redirect:/dr/setup/constraint/create";
        }
        return "redirect:/dr/setup/constraint/" + programConstraint.getId() + "/edit";
    }

    private ProgramConstraint retrieveConstraint(YukonUserContext userContext, HttpServletRequest request, int id,
            String url) {
        ProgramConstraint programConstraint = null;
        try {
            ResponseEntity<? extends Object> response =
                apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.GET, ProgramConstraint.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                programConstraint = (ProgramConstraint) response.getBody();
            }
        } catch (RestClientException ex) {
            log.error("Error retrieving program constraint : ", ex);
        }
        return programConstraint;
    }

    private String setupModel(ProgramConstraint programConstraint, ModelMap model, YukonUserContext userContext,
            HttpServletRequest request) {

        String seasonScheduleUrl = helper.findWebServerUrl(request, userContext, ApiURL.drSeasonScheduleUrl);
        List<LMDto> seasonSchedules = getSchedules(userContext, request, seasonScheduleUrl);
        programConstraintControllerHelper.setDefaultSeasonSchedule(seasonSchedules, userContext);
        model.addAttribute("seasonSchedules", seasonSchedules);

        String holidayScheduleUrl = helper.findWebServerUrl(request, userContext, ApiURL.drHolidayScheduleUrl);
        List<LMDto> holidaySchedules = getSchedules(userContext, request, holidayScheduleUrl);
        programConstraintControllerHelper.setDefaultHolidaySchedule(holidaySchedules, userContext);
        model.addAttribute("holidaySchedules", holidaySchedules);

        if (PageEditMode.VIEW == model.get("mode")) {
            model.addAttribute("daySelections", programConstraint.getDaySelection());
        } else {
            model.addAttribute("daySelections", DayOfWeek.values());
        }

        model.addAttribute("programConstraint", programConstraint);

        return "dr/setup/constraint/view.jsp";
    }

    private List<LMDto> getSchedules(YukonUserContext userContext, HttpServletRequest request, String url) {
        List<LMDto> schedules = new ArrayList<>();
        try {
            ResponseEntity<? extends Object> response =
                apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.GET, List.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                schedules = (List<LMDto>) response.getBody();
            }
        } catch (RestClientException ex) {
            log.error("Error retrieving schedules : ", ex);
        }
        return schedules;
    }

}