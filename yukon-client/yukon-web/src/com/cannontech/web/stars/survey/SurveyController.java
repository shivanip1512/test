package com.cannontech.web.stars.survey;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.survey.dao.SurveyDao;
import com.cannontech.common.survey.model.Answer;
import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.QuestionType;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.common.survey.service.SurveyService;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.ListBackingBean;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_SURVEY_EDIT)
@RequestMapping("/survey/*")
public class SurveyController {
    private final static String baseKey = "yukon.web.modules.survey.";
    private final static Pattern validKeyPattern = Pattern.compile("^\\w*$");
    private final static Function<Answer, String> answerKeyTransformer =
        new Function<Answer, String>() {
            @Override
            public String apply(Answer from) {
                return from.getAnswerKey();
            }
        };

    private SurveyDao surveyDao;
    private SurveyService surveyService;
    private EnergyCompanyDao energyCompanyDao;

    private Validator detailsValidator = new SimpleValidator<Survey>(Survey.class) {
        @Override
        protected void doValidation(Survey target, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "surveyName",
                                                      baseKey + "edit.valueRequired");
            YukonValidationUtils.checkExceedsMaxLength(errors, "surveyName",
                                                       target.getSurveyName(), 64);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "surveyKey",
                                                      baseKey + "edit.valueRequired");
            YukonValidationUtils.checkExceedsMaxLength(errors, "surveyKey",
                                                       target.getSurveyKey(), 64);
            YukonValidationUtils.regexCheck(errors, "surveyKey",
                                            target.getSurveyKey(),
                                            validKeyPattern,
                                            baseKey + "edit.invalidChars");
        }
    };

    private Validator questionValidator = new SimpleValidator<Question>(Question.class) {
        @Override
        protected void doValidation(Question target, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "questionKey",
                                                      baseKey + "edit.valueRequired");
            YukonValidationUtils.checkExceedsMaxLength(errors, "questionKey",
                                                       target.getQuestionKey(), 64);
            YukonValidationUtils.regexCheck(errors, "questionKey",
                                            target.getQuestionKey(),
                                            validKeyPattern,
                                            baseKey + "edit.invalidChars");
            if (target.getQuestionType() == QuestionType.DROP_DOWN) {
                boolean foundNotUnique = false;
                boolean foundValueRequired = false;
                boolean foundTooLarge = false;
                boolean foundInvalidChars = false;
                Set<String> answerKeysUsed = Sets.newHashSet();
                List<Answer> answers = target.getAnswers();
                for (Answer answer : answers) {
                    String answerKey = answer.getAnswerKey();
                    if (!foundNotUnique && answerKeysUsed.contains(answerKey)) {
                        errors.reject(baseKey + "edit.answerKeysNotUnique");
                        foundNotUnique = true;
                    }
                    answerKeysUsed.add(answerKey);
                    if (!foundValueRequired && StringUtils.isBlank(answerKey)) {
                        errors.reject(baseKey + "edit.answerValueRequired");
                        foundValueRequired = true;
                    }
                    if (!foundTooLarge && answerKey != null && answerKey.length() > 64) {
                        errors.reject(baseKey + "edit.answerTooLarge");
                        foundTooLarge = true;
                    }
                    Matcher matcher = validKeyPattern.matcher(answerKey);
                    if (!foundInvalidChars && !matcher.matches()) {
                        errors.reject(baseKey + "edit.answerHasInvalidChars");
                        foundInvalidChars = true;
                    }
                    if ("other".equals(answerKey) || "pleaseChoose".equals(answerKey)) {
                        errors.reject(baseKey + "edit.answerReserved");
                    }
                }
                if (answers.size() < 1 || !target.isTextAnswerAllowed() && answers.size() < 2) {
                    errors.reject(baseKey + "edit.notEnoughAnswers");
                }
            }
        }
    };

    @RequestMapping
    public String list(ModelMap model,
            @ModelAttribute("backingBean") ListBackingBean backingBean,
            YukonUserContext userContext) {
        LiteEnergyCompany energyCompany =
            energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
        SearchResults<Survey> surveys =
            surveyService.findSurveys(energyCompany.getEnergyCompanyID(),
                                 backingBean.getStartIndex(),
                                 backingBean.getItemsPerPage());
        model.addAttribute("surveys", surveys);

        return "survey/list.jsp";
    }
    
    @RequestMapping
    public String listTable(ModelMap model,
            @ModelAttribute("backingBean") ListBackingBean backingBean,
            YukonUserContext userContext) {
        LiteEnergyCompany energyCompany =
            energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
        SearchResults<Survey> surveys =
            surveyService.findSurveys(energyCompany.getEnergyCompanyID(),
                                 backingBean.getStartIndex(),
                                 backingBean.getItemsPerPage());
        model.addAttribute("surveys", surveys);

        return "survey/listTable.jsp";
    }

    @RequestMapping
    public void  sampleXml(HttpServletResponse response, Integer surveyId,
            YukonUserContext userContext) throws IOException {
        List<Survey> surveys = Lists.newArrayList();
        if (surveyId != null && surveyId > 0) {
            Survey survey = verifyEditable(surveyId, userContext);
            surveys.add(survey);
        } else {
            LiteEnergyCompany energyCompany =
                energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
            SearchResults<Survey> surveyResults =
                surveyService.findSurveys(energyCompany.getEnergyCompanyID(),
                                          0, Integer.MAX_VALUE);
            surveys = surveyResults.getResultList();
        }
        String sampleFile = sampleXmlFile(surveys);
        InputStream is = IOUtils.toInputStream(sampleFile);
        
        
        response.setContentType("text/xml");
        response.setHeader("Content-Disposition", "attachment; filename=\"SurveySample.xml\"");
        FileCopyUtils.copy(is, response.getOutputStream());

    }

    private String sampleXmlFile (List<Survey> surveys) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        xmlBuilder.append("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n");
        xmlBuilder.append("<!-- The \".title\" key is optional although highly recommended. -->\n");
        xmlBuilder.append("<!-- The \".description\" is also optional. -->\n");
        xmlBuilder.append("<!-- The \".pleaseChoose\" keys are optional.  Excluding this key will make the first answer the default. -->\n");
        xmlBuilder.append("<properties>\n");

        for (Survey survey : surveys) {
            String surveyKey = "yukon.web.surveys." + survey.getSurveyKey();
            xmlBuilder.append("    <entry key=\"").append(surveyKey).append(".title\">TITLE HERE</entry>\n");
            xmlBuilder.append("    <entry key=\"").append(surveyKey).append(".description\">DESCRIPTION HERE</entry>\n");
            for (Question question : surveyDao.getQuestionsBySurveyId(survey.getSurveyId())) {
                String questionKey = surveyKey + "." + question.getQuestionKey();
                xmlBuilder.append("    <entry key=\"").append(questionKey).append("\">QUESTION HERE</entry>\n");
                if (question.getQuestionType().equals(QuestionType.DROP_DOWN)) {
                    xmlBuilder.append("    <entry key=\"").append(questionKey).append(".pleaseChoose\">Please Choose</entry>\n");
                    for (Answer answer : question.getAnswers()){
                        xmlBuilder.append("    <entry key=\"").append(questionKey).append(".")
                        .append(answer.getAnswerKey()).append("\">ANSWER_HERE</entry>\n");
                    }
                    if (question.isTextAnswerAllowed()) {
                        xmlBuilder.append("    <entry key=\"").append(questionKey).append(".other\">ANSWER_HERE</entry>\n");
                    }
                }
            }
        }
        xmlBuilder.append("</properties>");
        return xmlBuilder.toString();
    }

    @RequestMapping
    public String delete(ModelMap model, int surveyId, FlashScope flashScope,
            YukonUserContext userContext) {
        Survey survey = verifyEditable(surveyId, userContext);
        if (surveyDao.usedByOptOutSurvey(surveyId) || surveyDao.hasBeenTaken(surveyId)) {
            MessageSourceResolvable errorMsg =
                    new YukonMessageSourceResolvable(baseKey + "list.surveyInUse",
                                                     survey.getSurveyName());
            flashScope.setError(errorMsg);
        } else {
            surveyDao.deleteSurvey(surveyId);
            MessageSourceResolvable confirmMsg =
                    new YukonMessageSourceResolvable(baseKey + "list.surveyDeleted",
                                                     survey.getSurveyName());
            flashScope.setConfirm(confirmMsg);
        }
        return "redirect:list";
    }

    @RequestMapping
    public String edit(ModelMap model, int surveyId,
            YukonUserContext userContext, FlashScope flashScope) {
        Survey survey = verifyEditable(surveyId, userContext);
        List<Question> questions = surveyDao.getQuestionsBySurveyId(survey.getSurveyId());
        model.addAttribute("survey", survey);
        model.addAttribute("questions", questions);
        model.addAttribute("hasBeenTaken", surveyDao.hasBeenTaken(surveyId));
        
        List<MessageSourceResolvable> messages = surveyService.getKeyErrorsForQuestions(surveyId, userContext);
        flashScope.setMessage(messages, FlashScopeMessageType.WARNING);
        
        return "survey/edit.jsp";
    }

    @RequestMapping
    public String editDetails(ModelMap model, Integer surveyId,
            YukonUserContext userContext) {
        Survey survey;

        LiteEnergyCompany energyCompany =
            energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
        if (surveyId == null || surveyId == 0) {
            survey = new Survey();
            survey.setEnergyCompanyId(energyCompany.getEnergyCompanyID());
            survey.setSurveyKey(surveyDao.getNextSurveyKey());
        } else {
            survey = verifyEditable(surveyId, userContext);
        }

        return editDetails(model, survey, userContext);
    }

    private String editDetails(ModelMap model, Survey survey,
            YukonUserContext userContext) {
        model.addAttribute("survey", survey);

        return "survey/editDetails.jsp";
    }

    @RequestMapping
    public String saveDetails(HttpServletResponse response, ModelMap model,
            @ModelAttribute Survey survey, BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope) {
        verifyEditable(survey, userContext);
        boolean isNew = survey.getSurveyId() == 0;
        detailsValidator.validate(survey, bindingResult);
        String newLocation = null;
        if (!bindingResult.hasErrors()) {
            try {
                boolean wasNew = survey.getSurveyId() == 0;
                surveyDao.saveSurvey(survey);
                if (wasNew) {
                    newLocation = "'/stars/survey/edit?surveyId=" +
                        survey.getSurveyId() + "'";
                } else {
                    newLocation = "edit?surveyId=" + survey.getSurveyId();
                }
            } catch (DuplicateException duplicateException) {
                bindingResult.rejectValue(duplicateException.getMessage(),
                                          baseKey + "edit.duplicate");
            }
        }
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return editDetails(model, survey, userContext);
        }

        if (!isNew) {
            // Creating a new survey redirects to the survey edit page so
            // a confirmation message isn't desirable.
            MessageSourceResolvable confirmMsg =
                new YukonMessageSourceResolvable(baseKey + "list.surveySaved",
                                                 survey.getSurveyName());
            flashScope.setConfirm(confirmMsg);
            return "redirect:" + newLocation;
        }

        ServletUtils.dialogFormSuccess(response, "yukonDetailsUpdated", newLocation);
        return null;
    }

    @RequestMapping
    public String addQuestion(ModelMap model, int surveyId,
            YukonUserContext userContext) {
        Question question = new Question();
        question.setSurveyId(surveyId);
        question.setQuestionKey(surveyDao.getNextQuestionKey(surveyId));
        return editQuestion(model, question, userContext);
    }

    @RequestMapping
    public String editQuestion(ModelMap model,
            int surveyQuestionId, YukonUserContext userContext) {
        Question question = surveyDao.getQuestionById(surveyQuestionId);
        return editQuestion(model, question, userContext);
    }

    private String editQuestion(ModelMap model, Question question,
            YukonUserContext userContext) {
        Survey survey = verifyEditable(question.getSurveyId(), userContext);
        boolean hasBeenTaken = surveyDao.hasBeenTaken(survey.getSurveyId());
        model.addAttribute("hasBeenTaken", hasBeenTaken);
        model.addAttribute("mode", hasBeenTaken ? PageEditMode.VIEW : PageEditMode.EDIT);
        model.addAttribute("question", question);
        List<String> answerKeys = Lists.transform(question.getAnswers(),
                                                  answerKeyTransformer);
        model.addAttribute("answerKeys", answerKeys);
        model.addAttribute("survey", survey);

        QuestionType[] questionTypes = QuestionType.values();
        model.addAttribute("questionTypes", questionTypes);

        return "survey/editQuestion.jsp";
    }

    @RequestMapping
    public String saveQuestion(HttpServletResponse response, ModelMap model,
            @ModelAttribute Question question, BindingResult bindingResult,
            String[] answerKeys, YukonUserContext userContext, FlashScope flashScope) {
        verifyEditable(question.getSurveyId(), userContext);
        if (question.getQuestionType() == QuestionType.DROP_DOWN) {
            int questionId = question.getSurveyQuestionId();
            int displayOrder = 1;
            List<Answer> answers = Lists.newArrayList();
            if (answerKeys != null) {
                for (String answerKey : answerKeys) {
                    Answer answer = new Answer(questionId, answerKey, displayOrder++);
                    answers.add(answer);
                }
            }
            question.setAnswers(answers);
        }
        else {
            // in case it was set before the type was change to "TEXT"
            question.setTextAnswerAllowed(false);
        }
        questionValidator.validate(question, bindingResult);
        if (!bindingResult.hasErrors()) {
            try {
                surveyDao.saveQuestion(question);
            } catch (DuplicateException duplicateException) {
                bindingResult.rejectValue(duplicateException.getMessage(),
                                          baseKey + "edit.duplicate");
            }
        }
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return editQuestion(model, question, userContext);
        }

        MessageSourceResolvable confirmMsg =
            new YukonMessageSourceResolvable(baseKey + "edit.surveyQuestionSaved",
                                             question.getQuestionKey());
        flashScope.setConfirm(confirmMsg);

        ServletUtils.dialogFormSuccess(response, "yukonQuestionSaved");
        return null;
    }

    @RequestMapping
    public String moveQuestion(HttpServletResponse response, ModelMap model,
            int surveyQuestionId, String direction, YukonUserContext userContext) {
        Question question = surveyDao.getQuestionById(surveyQuestionId);
        if ("up".equals(direction)) {
            surveyDao.moveQuestionUp(question);
        } else if ("down".equals(direction)) {
            surveyDao.moveQuestionDown(question);
        } else {
            throw new RuntimeException("invalid diirection [" + direction + "]");
        }
        return "redirect:edit?surveyId=" + question.getSurveyId();
    }

    @RequestMapping
    public String deleteQuestion(ModelMap model, int surveyQuestionId,
            FlashScope flashScope, YukonUserContext userContext) {
        Question question = surveyDao.getQuestionById(surveyQuestionId);
        verifyEditable(question.getSurveyId(), userContext);
        surveyDao.deleteQuestion(surveyQuestionId);
        MessageSourceResolvable confirmMsg =
            new YukonMessageSourceResolvable(baseKey +
                                             "edit.surveyQuestionDeleted",
                                             question.getQuestionKey());
        flashScope.setConfirm(confirmMsg);
        return "redirect:edit?surveyId=" + question.getSurveyId();
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

    @Autowired
    public void setSurveyDao(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }

    @Autowired
    public void setSurveyService(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @Autowired
    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }
}
