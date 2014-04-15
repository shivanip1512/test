package com.cannontech.web.stars.survey;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.survey.dao.SurveyDao;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.optout.dao.OptOutSurveyDao;
import com.cannontech.stars.dr.optout.model.OptOutSurvey;
import com.cannontech.stars.dr.optout.service.OptOutSurveyService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonUtils;
import com.cannontech.web.util.ListBackingBean;
import com.google.common.collect.Sets;

@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_OPT_OUT_SURVEY_EDIT)
@RequestMapping("/optOutSurvey/*")
public class OptOutSurveyController {
    private final static String baseKey = "yukon.web.modules.operator.surveyList";

    @Autowired private OptOutSurveyDao optOutSurveyDao;
    @Autowired private OptOutSurveyService optOutSurveyService;
    @Autowired private SurveyDao surveyDao;
    @Autowired private PaoDao paoDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private EnergyCompanyDao ecService;
    
    private Validator validator = new SimpleValidator<OptOutSurveyDto>(OptOutSurveyDto.class) {
        @Override
        protected void doValidation(OptOutSurveyDto target, Errors errors) {
            if (target.getSurveyId() == 0) {
                errors.rejectValue("surveyId", "yukon.web.error.required");
            }
            if (target.getProgramIds() == null || target.getProgramIds().length == 0) {
                errors.rejectValue("programIds", "yukon.web.error.required");
            }
            if (target.getStartDate() == null) {
                errors.rejectValue("startDate", "yukon.web.error.required");
            } else if (target.getStopDate() != null && !target.getStartDate().before(target.getStopDate())) {
                errors.rejectValue("stopDate", "yukon.web.modules.operator.surveyEdit.stopDateNotBeforeStart");
            }
        }};

    @RequestMapping("list")
    public String list(ModelMap model,
            @ModelAttribute("backingBean") ListBackingBean backingBean,
            YukonUserContext userContext) {
        EnergyCompany energyCompany = ecService.getEnergyCompany(userContext.getYukonUser());
        
        SearchResults<OptOutSurvey> optOutSurveys =
            optOutSurveyService.findSurveys(energyCompany.getId(),
                                 backingBean.getStartIndex(),
                                 backingBean.getItemsPerPage());
        model.addAttribute("optOutSurveys", optOutSurveys);

        Set<Integer> programIds = Sets.newHashSet();
        for (OptOutSurvey optOutSurvey : optOutSurveys.getResultList()) {
            programIds.addAll(optOutSurvey.getProgramIds());
        }

        Map<Integer, String> programNamesById = paoDao.getYukonPAONames(programIds);
        model.addAttribute("programNamesById", programNamesById);

        model.addAttribute("energyCompanyId", energyCompany.getId());

        return "optOutSurvey/list.jsp";
    }

    @RequestMapping("programList")
    public String programList(ModelMap model, int optOutSurveyId,
            YukonUserContext userContext) {
        OptOutSurvey optOutSurvey =
            optOutSurveyDao.getOptOutSurveyById(optOutSurveyId);
        verifyEditable(optOutSurvey.getEnergyCompanyId(), userContext);
        model.addAttribute("optOutSurvey", optOutSurvey);

        Map<Integer, String> programNamesById =
            paoDao.getYukonPAONames(optOutSurvey.getProgramIds());
        model.addAttribute("programNamesById", programNamesById);

        Survey survey = surveyDao.getSurveyById(optOutSurvey.getSurveyId());
        model.addAttribute("survey", survey);

        return "optOutSurvey/programList.jsp";
    }

    @RequestMapping("confirmDelete")
    public String confirmDelete(ModelMap model, int optOutSurveyId,
            YukonUserContext userContext) {
        OptOutSurvey optOutSurvey =
            optOutSurveyDao.getOptOutSurveyById(optOutSurveyId);
        verifyEditable(optOutSurvey.getEnergyCompanyId(), userContext);
        model.addAttribute("optOutSurvey", optOutSurvey);
        return "optOutSurvey/confirmDelete.jsp";
    }

    @RequestMapping("delete")
    public @ResponseBody Map<String, String> delete(int optOutSurveyId, FlashScope flashScope, YukonUserContext userContext) {
        OptOutSurvey optOutSurvey = optOutSurveyDao.getOptOutSurveyById(optOutSurveyId);
        verifyEditable(optOutSurvey.getEnergyCompanyId(), userContext);
        optOutSurveyDao.deleteOptOutSurvey(optOutSurveyId);
        Survey survey = surveyDao.getSurveyById(optOutSurvey.getSurveyId());
        MessageSourceResolvable confirmMsg =
            new YukonMessageSourceResolvable(baseKey + ".optOutSurveyDeleted", survey.getSurveyName());
        flashScope.setConfirm(confirmMsg);

        return Collections.singletonMap("action", "reload");
    }

    @RequestMapping("edit")
    public String edit(ModelMap model, Integer optOutSurveyId,
            Integer surveyId, Integer[] programIds,
            YukonUserContext userContext) {
        OptOutSurveyDto optOutSurveyDto = null;
        if (optOutSurveyId == null || optOutSurveyId == 0) {
            optOutSurveyDto = new OptOutSurveyDto();
            optOutSurveyDto.setStartDate(new Date());
            EnergyCompany energyCompany = ecService.getEnergyCompany(userContext.getYukonUser());
            optOutSurveyDto.setEnergyCompanyId(energyCompany.getId());
        } else {
            OptOutSurvey optOutSurvey =
                optOutSurveyDao.getOptOutSurveyById(optOutSurveyId);
            optOutSurveyDto = new OptOutSurveyDto(optOutSurvey);
            verifyEditable(optOutSurvey.getEnergyCompanyId(), userContext);
        }
        return edit(model, optOutSurveyDto);
    }

    private String edit(ModelMap model, OptOutSurveyDto optOutSurveyDto) {
        model.addAttribute("optOutSurveyDto", optOutSurveyDto);
        return "optOutSurvey/edit.jsp";
    }

    @RequestMapping("save")
    public String save(HttpServletResponse resp, ModelMap model,
            @ModelAttribute OptOutSurveyDto optOutSurveyDto,
            BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope) throws IOException {
        verifyEditable(optOutSurveyDto.getEnergyCompanyId(), userContext);

        validator.validate(optOutSurveyDto, bindingResult);
        if (!bindingResult.hasErrors()) {
            optOutSurveyDao.saveOptOutSurvey(optOutSurveyDto.getOptOutSurvey());
        }

        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return edit(model, optOutSurveyDto);
        }

        Survey survey = surveyDao.getSurveyById(optOutSurveyDto.getSurveyId());
        MessageSourceResolvable confirmMsg =
            new YukonMessageSourceResolvable(baseKey + ".optOutSurveySaved", survey.getSurveyName());
        flashScope.setConfirm(confirmMsg);

        resp.setContentType("application/json");
        resp.getWriter().print(JsonUtils.toJson(Collections.singletonMap("action", "reload")));
        resp.getWriter().close();
        return null;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        PropertyEditor fullDateTimeEditor =
            datePropertyEditorFactory.getPropertyEditor(DateFormatEnum.DATEHM, userContext);
        binder.registerCustomEditor(Date.class, fullDateTimeEditor);
    }

    private void verifyEditable(int energyCompanyId,
            YukonUserContext userContext) {
        EnergyCompany energyCompany = ecService.getEnergyCompany(userContext.getYukonUser());
        if (energyCompany.getId() != energyCompanyId) {
            throw new NotAuthorizedException("energy company mismatch");
        }
    }
}
