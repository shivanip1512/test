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

import com.cannontech.analysis.report.ColumnLayoutData;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.survey.dao.SurveyDao;
import com.cannontech.common.survey.model.Answer;
import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.QuestionType;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.simplereport.ColumnInfo;
import com.cannontech.simplereport.SimpleReportService;
import com.cannontech.simplereport.YukonReportDefinition;
import com.cannontech.simplereport.YukonReportDefinitionFactory;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.InputUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Maps;

@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_SURVEY_EDIT)
@RequestMapping("/surveyReport/*")
public class SurveyReportController {
    private SurveyDao surveyDao;
    private EnergyCompanyDao energyCompanyDao;
    private DatePropertyEditorFactory datePropertyEditorFactory;
    private YukonReportDefinitionFactory<BareReportModel> reportDefinitionFactory;
    private SimpleReportService simpleReportService;

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
            Date start = reportConfig.getStartDate();
            Date end = reportConfig.getStopDate();
            
            if (start == null) {
                errors.rejectValue("startDate", "startDateRequired");
            }

            if (end == null) {
                errors.rejectValue("stopDate", "stopDateRequired");
            }

            if (start != null && end != null && start.after(end)) {
                YukonValidationUtils.rejectValues(errors,
                                                  "startTimeNotBeforeStopTime",
                                                  "startDate",
                                                  "stopDate");
            }
            
            if (question.getQuestionType() == QuestionType.DROP_DOWN) {
                List<Integer> answerIds = reportConfig.getAnswerIds();
                if ((answerIds == null || answerIds.size() == 0)
                        && !reportConfig.isIncludeOtherAnswers()
                        && !reportConfig.isIncludeUnanswered()) {
                    errors.rejectValue("answerId", "atLeasteOneAnswerRequired");
                }
            }
        }
    };

    @RequestMapping
    public String config(ModelMap model, int surveyId,
            @ModelAttribute ReportConfig reportConfig,
            YukonUserContext userContext) {
        Survey survey = verifyEnergyCompany(surveyId, userContext);
        model.addAttribute("survey", survey);
        if(reportConfig == null) {
            reportConfig = new ReportConfig();
        }
        reportConfig.setSurveyId(surveyId);
        return config(model, survey, userContext);
    }

    private String config(ModelMap model, Survey survey, YukonUserContext userContext) {
        LiteEnergyCompany energyCompany =
            energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
        model.addAttribute("energyCompanyId", energyCompany.getEnergyCompanyID());
        List<Question> questions = surveyDao.getQuestionsBySurveyId(survey.getSurveyId());
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
            YukonUserContext userContext) throws Exception {
        int surveyId = reportConfig.getSurveyId();
        Survey survey = verifyEnergyCompany(surveyId, userContext);
        model.addAttribute("survey", survey);
        Question question = surveyDao.getQuestionById(reportConfig.getQuestionId());
        model.addAttribute("question", question);

        ReportConfigValidator validator = new ReportConfigValidator(question);
        validator.validate(reportConfig, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return config(model, survey, userContext);
        }

        String definitionName = "summary".equals(reportConfig.getReportType())
            ?  "surveyResultsSummaryDefinition" : "surveyResultsDetailDefinition";
        model.addAttribute("definitionName", definitionName);

        YukonReportDefinition<BareReportModel> reportDefinition =
            reportDefinitionFactory.getReportDefinition(definitionName);
        Map<String, String> inputMap = InputUtil.extractProperties(reportDefinition.getInputs(), reportConfig);
        model.addAttribute("inputMap", inputMap);

        BareReportModel reportModel =
            simpleReportService.getReportModel(reportDefinition, inputMap, true);

        ColumnLayoutData[] bodyColumns = reportDefinition.getReportLayoutData().getBodyColumns();
        List<ColumnInfo> columnInfo = simpleReportService.buildColumnInfoListFromColumnLayoutData(bodyColumns);
        model.addAttribute("columnInfo", columnInfo);
        List<List<String>> data = simpleReportService.getFormattedData(reportDefinition, reportModel, userContext);
        model.addAttribute("data", data);

        return "surveyReport/report.jsp";
    }

    private Survey verifyEnergyCompany(int surveyId, YukonUserContext userContext) {
        Survey survey = surveyDao.getSurveyById(surveyId);
        LiteEnergyCompany energyCompany =
            energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
        if (energyCompany.getEnergyCompanyID() != survey.getEnergyCompanyId()) {
            throw new NotAuthorizedException("energy company mismatch");
        }
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
        binder.registerCustomEditor(Date.class, "startDate", dayStartDateEditor);
        binder.registerCustomEditor(Date.class, "stopDate", dayEndDateEditor);
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
    public void setDatePropertyEditorFactory(
            DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }

    @Autowired
    public void setReportDefinitionFactory(
            YukonReportDefinitionFactory<BareReportModel> reportDefinitionFactory) {
        this.reportDefinitionFactory = reportDefinitionFactory;
    }

    @Autowired
    public void setSimpleReportService(SimpleReportService simpleReportService) {
        this.simpleReportService = simpleReportService;
    }
}
