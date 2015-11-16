package com.cannontech.web.stars.dr.operator.model;

import java.util.Map;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.QuestionType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.stars.dr.operator.model.OptOutBackingBean.SurveyResult;
import com.cannontech.web.stars.dr.operator.model.OptOutBackingBean.SurveyResultAnswer;

public class SurveyResultValidator extends SimpleValidator<OptOutBackingBean> {
    private final static String errorPrefix =
        "yukon.web.modules.operator.optOutBackingBean.";
    private int surveyIndex;
    private Map<Integer, Question> questionsById;

    public SurveyResultValidator(int surveyIndex,
            Map<Integer, Question> questionsById) {
        super(OptOutBackingBean.class);
        this.surveyIndex = surveyIndex;
        this.questionsById = questionsById;
    }

    @Override
    protected void doValidation(OptOutBackingBean optOutBackingBean, Errors errors) {
        SurveyResult surveyResult = optOutBackingBean.getSurveyResults().get(surveyIndex);
        int index = 0;
        for (SurveyResultAnswer answer : surveyResult.getAnswers()) {
            Question question = questionsById.get(answer.getQuestionId());
            String pathPrefix = "surveyResults[" + surveyIndex + "].answers[" +
                index + "].";
            if (question.isAnswerRequired()) {
                if (question.getQuestionType() == QuestionType.DROP_DOWN
                        && "other".equals(answer.getDropDownAnswer())
                        || question.getQuestionType() == QuestionType.TEXT) {
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                        pathPrefix + "textAnswer",
                        errorPrefix + "required");
                } else if (question.getQuestionType() == QuestionType.DROP_DOWN
                        && "pleaseChoose".equals(answer.getDropDownAnswer())) {
                    errors.rejectValue(pathPrefix + "dropDownAnswer",
                                       errorPrefix + "required");
                }
            }
            YukonValidationUtils.checkExceedsMaxLength(errors,
                                                       pathPrefix + "textAnswer",
                                                       answer.getTextAnswer(), 255);
            index++;
        }
    }
}
