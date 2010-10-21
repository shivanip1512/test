package com.cannontech.analysis.tablemodel;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.survey.dao.SurveyDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.optout.dao.OptOutSurveyDao;
import com.cannontech.stars.dr.optout.model.SurveyResult;
import com.google.common.collect.Lists;

public abstract class SurveyResultsModelBase<T> extends BareReportModelBase<T> {
    protected YukonJdbcTemplate yukonJdbcTemplate;
    protected SurveyDao surveyDao;
    protected OptOutSurveyDao optOutSurveyDao;

    // inputs
    protected int surveyId;
    protected Date startDate;
    protected Date endDate;
    protected int questionId;
    protected List<Integer> answerIds;
    protected boolean includeOtherAnswers;
    protected boolean includeUnanswered;
    protected List<Integer> programIds;

    protected List<T> data = Lists.newArrayList();

    private final static MessageSourceResolvable unansweredReason =
        new YukonMessageSourceResolvable("yukon.web.modules.survey.report.unanswered");

    @Override
    protected T getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }


    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public List<Integer> getAnswerIds() {
        return answerIds;
    }

    public void setAnswerIds(List<Integer> answerIds) {
        this.answerIds = answerIds;
    }

    public boolean isIncludeOtherAnswers() {
        return includeOtherAnswers;
    }

    public void setIncludeOtherAnswers(boolean includeOtherAnswers) {
        this.includeOtherAnswers = includeOtherAnswers;
    }

    public boolean isIncludeUnanswered() {
        return includeUnanswered;
    }

    public void setIncludeUnanswered(boolean includeUnanswered) {
        this.includeUnanswered = includeUnanswered;
    }

    public List<Integer> getProgramIds() {
        return programIds;
    }

    public void setProgramIds(List<Integer> programIds) {
        this.programIds = programIds;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Object getReason(SurveyResult result, String baseKey) {
        Integer answerId = result.getSurveyQuestionAnswerId();
        if (answerId != null) {
            return new YukonMessageSourceResolvable(baseKey + "." + result.getAnswerKey());
        }
        if (result.getTextAnswer() != null) {
            return result.getTextAnswer();
        }
        return unansweredReason;
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Autowired
    public void setSurveyDao(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }

    @Autowired
    public void setOptOutSurveyDao(OptOutSurveyDao optOutSurveyDao) {
        this.optOutSurveyDao = optOutSurveyDao;
    }
}
