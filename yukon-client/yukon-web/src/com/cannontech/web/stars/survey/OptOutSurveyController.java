package com.cannontech.web.stars.survey;

import java.beans.PropertyEditor;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.survey.dao.SurveyDao;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.optout.dao.OptOutSurveyDao;
import com.cannontech.stars.dr.optout.model.OptOutSurvey;
import com.cannontech.stars.dr.optout.service.OptOutSurveyService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.ListBackingBean;
import com.google.common.collect.Sets;

@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_OPT_OUT_SURVEY_EDIT)
@RequestMapping("/optOutSurvey/*")
public class OptOutSurveyController {
    private final static String baseKey = "yukon.web.modules.dr.survey";

    private OptOutSurveyDao optOutSurveyDao;
    private OptOutSurveyService optOutSurveyService;
    private SurveyDao surveyDao;
    private EnergyCompanyDao energyCompanyDao;
    private PaoDao paoDao;
    private DatePropertyEditorFactory datePropertyEditorFactory;
    
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
                errors.rejectValue("stopDate", "yukon.web.modules.dr.surveyEdit.stopDateNotBeforeStart");
            }
        }};

    @RequestMapping
    public String list(ModelMap model,
            @ModelAttribute("backingBean") ListBackingBean backingBean,
            YukonUserContext userContext) {
        LiteEnergyCompany energyCompany =
            energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
        SearchResult<OptOutSurvey> optOutSurveys =
            optOutSurveyService.findSurveys(energyCompany.getEnergyCompanyID(),
                                 backingBean.getStartIndex(),
                                 backingBean.getItemsPerPage());
        model.addAttribute("optOutSurveys", optOutSurveys);

        Set<Integer> programIds = Sets.newHashSet();
        for (OptOutSurvey optOutSurvey : optOutSurveys.getResultList()) {
            programIds.addAll(optOutSurvey.getProgramIds());
        }

        Map<Integer, String> programNamesById = paoDao.getYukonPAONames(programIds);
        model.addAttribute("programNamesById", programNamesById);

        model.addAttribute("energyCompanyId", energyCompany.getEnergyCompanyID());

        return "optOutSurvey/list.jsp";
    }

    @RequestMapping
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

    @RequestMapping
    public String confirmDelete(ModelMap model, int optOutSurveyId,
            YukonUserContext userContext) {
        OptOutSurvey optOutSurvey =
            optOutSurveyDao.getOptOutSurveyById(optOutSurveyId);
        verifyEditable(optOutSurvey.getEnergyCompanyId(), userContext);
        model.addAttribute("optOutSurvey", optOutSurvey);
        return "optOutSurvey/confirmDelete.jsp";
    }

    @RequestMapping
    public String delete(ModelMap model, int optOutSurveyId, FlashScope flashScope,
            YukonUserContext userContext) {
        OptOutSurvey optOutSurvey =
            optOutSurveyDao.getOptOutSurveyById(optOutSurveyId);
        verifyEditable(optOutSurvey.getEnergyCompanyId(), userContext);
        optOutSurveyDao.deleteOptOutSurvey(optOutSurveyId);
        Survey survey = surveyDao.getSurveyById(optOutSurvey.getSurveyId());
        MessageSourceResolvable confirmMsg =
            new YukonMessageSourceResolvable(baseKey +
                                             "List.optOutSurveyDeleted",
                                             survey.getSurveyName());
        flashScope.setConfirm(confirmMsg);
        return closeDialog(model);
    }

    @RequestMapping
    public String edit(ModelMap model, Integer optOutSurveyId,
            Integer surveyId, Integer[] programIds,
            YukonUserContext userContext) {
        OptOutSurveyDto optOutSurveyDto = null;
        if (optOutSurveyId == null || optOutSurveyId == 0) {
            optOutSurveyDto = new OptOutSurveyDto();
            optOutSurveyDto.setStartDate(new Date());
            LiteEnergyCompany energyCompany =
                energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
            optOutSurveyDto.setEnergyCompanyId(energyCompany.getEnergyCompanyID());
        } else {
            OptOutSurvey optOutSurvey =
                optOutSurveyDao.getOptOutSurveyById(optOutSurveyId);
            optOutSurveyDto = new OptOutSurveyDto(optOutSurvey);
            verifyEditable(optOutSurvey.getEnergyCompanyId(), userContext);
        }
        return edit(model, optOutSurveyDto, userContext);
    }

    private String edit(ModelMap model, OptOutSurveyDto optOutSurveyDto,
            YukonUserContext userContext) {
        model.addAttribute("optOutSurveyDto", optOutSurveyDto);

        return "optOutSurvey/edit.jsp";
    }

    @RequestMapping
    public String save(ModelMap model,
            @ModelAttribute OptOutSurveyDto optOutSurveyDto,
            BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope) {
        verifyEditable(optOutSurveyDto.getEnergyCompanyId(), userContext);

        validator.validate(optOutSurveyDto, bindingResult);
        if (!bindingResult.hasErrors()) {
            optOutSurveyDao.saveOptOutSurvey(optOutSurveyDto.getOptOutSurvey());
        }

        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return edit(model, optOutSurveyDto, userContext);
        }

        Survey survey = surveyDao.getSurveyById(optOutSurveyDto.getSurveyId());
        MessageSourceResolvable confirmMsg =
            new YukonMessageSourceResolvable(baseKey +
                                             "List.optOutSurveySaved",
                                             survey.getSurveyName());
        flashScope.setConfirm(confirmMsg);

        return closeDialog(model);
    }

    private String closeDialog(ModelMap model) {
        return closeDialog(model, null);
    }

    private String closeDialog(ModelMap model, String newLocation) {
        model.addAttribute("popupId", "ajaxDialog");
        if (newLocation != null) {
            model.addAttribute("newLocation", newLocation);
        }
        return "closePopup.jsp";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        PropertyEditor fullDateTimeEditor =
            datePropertyEditorFactory.getPropertyEditor(DateFormatEnum.DATEHM, userContext);
        binder.registerCustomEditor(Date.class, fullDateTimeEditor);
    }

    private void verifyEditable(int energyCompanyId,
            YukonUserContext userContext) {
        LiteEnergyCompany energyCompany =
            energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
        if (energyCompany.getEnergyCompanyID() != energyCompanyId) {
            throw new NotAuthorizedException("energy company mismatch");
        }
    }

    @Autowired
    public void setOptOutSurveyDao(OptOutSurveyDao optOutSurveyDao) {
        this.optOutSurveyDao = optOutSurveyDao;
    }

    @Autowired
    public void setOptOutSurveyService(OptOutSurveyService optOutSurveyService) {
        this.optOutSurveyService = optOutSurveyService;
    }

    @Autowired
    public void setSurveyDao(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }

    @Autowired
    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Autowired
    public void setDatePropertyEditorFactory(
            DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }
}
