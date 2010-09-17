package com.cannontech.web.stars.survey;

import java.beans.PropertyEditor;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.survey.dao.SurveyDao;
import com.cannontech.common.survey.model.Answer;
import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.QuestionType;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_SURVEY_EDIT)
@RequestMapping("/surveyReport/*")
public class SurveyReportController {
    private SurveyDao surveyDao;
    private EnergyCompanyDao energyCompanyDao;
    private PaoDao paoDao;
    private DatePropertyEditorFactory datePropertyEditorFactory;

    public static class JsonSafeQuestion {
        private Question question;

        private JsonSafeQuestion(Question question) {
            this.question = question;
        }

        public String getQuestionType() {
            return question.getQuestionType().name();
        }

        public List<Answer> getAnswers() {
            return question.getAnswers();
        }

        public boolean isTextAnswerAllowed() {
            return question.isTextAnswerAllowed();
        }

        public boolean isAnswerRequired() {
            return question.isAnswerRequired();
        }
    }

    private static class ReportConfigValidator extends SimpleValidator<ReportConfig> {
        private Question question;

        private ReportConfigValidator(Question question) {
            super(ReportConfig.class);
            this.question = question;
        }

        @Override
        protected void doValidation(ReportConfig reportConfig, Errors errors) {
            Date start = reportConfig.getStart();
            Date end = reportConfig.getEnd();
            if (start != null && end != null && start.after(end)) {
                errors.reject("startTimeNotBeforeStopTime");
            }
            if (question.getQuestionType() == QuestionType.DROP_DOWN) {
                Integer[] answerIds = reportConfig.getAnswerIds();
                if ((answerIds == null || answerIds.length == 0)
                        && !reportConfig.isIncludeOtherAnswers()) {
                    errors.rejectValue("answerIds", "atLeasteOneAnswerRequired");
                }
            }
        }
    };

    @RequestMapping
    public String config(ModelMap model, int surveyId,
            YukonUserContext userContext) {
        Survey survey = verifyEditable(surveyId, userContext);
        model.addAttribute("survey", survey);
        ReportConfig reportConfig = new ReportConfig();
        reportConfig.setSurveyId(surveyId);
        model.addAttribute("reportConfig", reportConfig);
        LiteEnergyCompany energyCompany =
            energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
        model.addAttribute("energyCompanyId", energyCompany.getEnergyCompanyID());
        return config(model, surveyId);
    }

    private String config(ModelMap model, int surveyId) {
        List<Question> questions = surveyDao.getQuestionsBySurveyId(surveyId);
        model.addAttribute("questions", questions);
        Map<Integer, Object> questionsById = Maps.newHashMap();
        for (final Question question : questions) {
            questionsById.put(question.getSurveyQuestionId(),
                              new JsonSafeQuestion(question));
        }
        model.addAttribute("questionsById", questionsById);
        return "surveyReport/config.jsp";
    }

    @RequestMapping
    public String report(ModelMap model,
            @ModelAttribute ReportConfig reportConfig,
            BindingResult bindingResult, FlashScope flashScope,
            YukonUserContext userContext) {
        int surveyId = reportConfig.getSurveyId();
        Survey survey = verifyEditable(surveyId, userContext);
        model.addAttribute("survey", survey);
        Question question = surveyDao.getQuestionById(reportConfig.getQuestionId());
        ReportConfigValidator validator = new ReportConfigValidator(question);
        validator.validate(reportConfig, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            Map<Integer, String> programNamesById =
                paoDao.getYukonPAONames(Lists.newArrayList(reportConfig.getProgramIds()));
            List<UltraLightPao> initialPrograms = Lists.newArrayList();
            LiteEnergyCompany energyCompany =
                energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
            model.addAttribute("energyCompanyId", energyCompany.getEnergyCompanyID());
            for (final Integer programId : reportConfig.getProgramIds()) {
                final String programName = programNamesById.get(programId);
                initialPrograms.add(new UltraLightPao() {
                    @Override
                    public String getType() {
                        return PaoType.LM_DIRECT_PROGRAM.getDbString();
                    }
                    
                    @Override
                    public String getPaoName() {
                        return programName;
                    }
                    
                    @Override
                    public int getPaoId() {
                        return programId;
                    }
                });
            }
            model.addAttribute("initialPrograms", initialPrograms);
            return config(model, surveyId);
        }
        return "surveyReport/report.jsp";
    }

    private Survey verifyEditable(Survey survey, YukonUserContext userContext) {
        LiteEnergyCompany energyCompany =
            energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
        if (energyCompany.getEnergyCompanyID() != survey.getEnergyCompanyId()) {
            throw new NotAuthorizedException("energy company mismatch");
        }
        return survey;
    }

    private Survey verifyEditable(int surveyId, YukonUserContext userContext) {
        Survey survey = surveyDao.getSurveyById(surveyId);
        verifyEditable(survey, userContext);
        return survey;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver =
                new YukonMessageCodeResolver("yukon.web.modules.survey.reportConfig.");
            binder.setMessageCodesResolver(msgCodesResolver);
        }

        PropertyEditor dayStartDateEditor =
            datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.START_OF_DAY, userContext);
        PropertyEditor dayEndDateEditor =
            datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.END_OF_DAY, userContext);
        binder.registerCustomEditor(Date.class, "start", dayStartDateEditor);
        binder.registerCustomEditor(Date.class, "end", dayEndDateEditor);
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
