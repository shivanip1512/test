package com.cannontech.web.stars.survey;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.survey.dao.SurveyDao;
import com.cannontech.common.survey.model.Answer;
import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.QuestionType;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.common.survey.service.SurveyService;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;
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
        SearchResult<Survey> surveys =
            surveyService.findSurveys(energyCompany.getEnergyCompanyID(),
                                 backingBean.getStartIndex(),
                                 backingBean.getItemsPerPage());
        model.addAttribute("surveys", surveys);

        return "survey/list.jsp";
    }

    @RequestMapping
    public String sampleXml(ModelMap model, Integer surveyId,
            YukonUserContext userContext) {
        List<Survey> surveys = Lists.newArrayList();
        if (surveyId != null && surveyId > 0) {
            Survey survey = verifyEditable(surveyId, userContext);
            surveys.add(survey);
        } else {
            LiteEnergyCompany energyCompany =
                energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
            SearchResult<Survey> surveyResults =
                surveyService.findSurveys(energyCompany.getEnergyCompanyID(),
                                          0, Integer.MAX_VALUE);
            surveys = surveyResults.getResultList();
        }
        model.addAttribute("surveys", surveys);
        Map<Integer, List<Question>> questions = Maps.newHashMap();
        for (Survey survey : surveys) {
            questions.put(survey.getSurveyId(),
                          surveyDao.getQuestionsBySurveyId(survey.getSurveyId()));
        }
        model.addAttribute("questions", questions);

        return "survey/sampleXml.jsp";
    }

    @RequestMapping
    public String confirmDelete(ModelMap model, int surveyId,
            FlashScope flashScope, YukonUserContext userContext) {
        Survey survey = verifyEditable(surveyId, userContext);
        model.addAttribute("survey", survey);
        if (surveyDao.usedByOptOutSurvey(surveyId)
                || surveyDao.hasBeenTaken(surveyId)) {
            return "survey/inUse.jsp";
        }
        return "survey/confirmDelete.jsp";
    }

    @RequestMapping
    public String delete(ModelMap model, int surveyId, FlashScope flashScope,
            YukonUserContext userContext) {
        Survey survey = verifyEditable(surveyId, userContext);
        if (surveyDao.usedByOptOutSurvey(surveyId)
                || surveyDao.hasBeenTaken(surveyId)) {
            model.addAttribute("survey", survey);
            return "survey/inUse.jsp";
        }
        surveyDao.deleteSurvey(surveyId);
        MessageSourceResolvable confirmMsg =
            new YukonMessageSourceResolvable(baseKey + "list.surveyDeleted",
                                             survey.getSurveyName());
        flashScope.setConfirm(confirmMsg);
        return closeDialog(model);
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
    public String saveDetails(ModelMap model, @ModelAttribute Survey survey,
            BindingResult bindingResult, YukonUserContext userContext,
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
                    newLocation = "/spring/stars/survey/edit?surveyId=" +
                        survey.getSurveyId();
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
        }

        return closeDialog(model, newLocation);
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
    public String saveQuestion(ModelMap model,
            @ModelAttribute Question question, BindingResult bindingResult,
            String[] answerKeys, YukonUserContext userContext,
            FlashScope flashScope) {
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
            new YukonMessageSourceResolvable(baseKey +
                                             "edit.surveyQuestionSaved",
                                             question.getQuestionKey());
        flashScope.setConfirm(confirmMsg);

        return closeDialog(model);
    }

    @RequestMapping
    public View moveQuestion(HttpServletResponse response, ModelMap model,
            int surveyQuestionId, String direction, YukonUserContext userContext) {
        Question question = surveyDao.getQuestionById(surveyQuestionId);
        if ("up".equals(direction)) {
            surveyDao.moveQuestionUp(question);
        } else if ("down".equals(direction)) {
            surveyDao.moveQuestionDown(question);
        } else {
            throw new RuntimeException("invalid diirection [" + direction + "]");
        }
        model.addAttribute("action", "reload");
        return new JsonView();
    }

    @RequestMapping
    public String confirmDeleteQuestion(ModelMap model, int surveyQuestionId,
            YukonUserContext userContext) {
        Question question = surveyDao.getQuestionById(surveyQuestionId);
        model.addAttribute("question", question);
        Survey survey = verifyEditable(question.getSurveyId(), userContext);
        model.addAttribute("survey", survey);
        return "survey/confirmDeleteQuestion.jsp";
    }

    @RequestMapping
    public String deleteQuestion(ModelMap model, int surveyQuestionId,
            FlashScope flashScope, YukonUserContext userContext) {
        Question question = surveyDao.getQuestionById(surveyQuestionId);
        model.addAttribute("question", question);
        verifyEditable(question.getSurveyId(), userContext);
        surveyDao.deleteQuestion(surveyQuestionId);
        MessageSourceResolvable confirmMsg =
            new YukonMessageSourceResolvable(baseKey +
                                             "edit.surveyQuestionDeleted",
                                             question.getQuestionKey());
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
