package com.cannontech.web.stars.dr.operator.model;

import java.util.List;

import org.joda.time.LocalDate;

import com.cannontech.common.util.LazyList;
import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;
import com.google.common.collect.Lists;

public class OptOutBackingBean {
    private LocalDate startDate;
    private int durationInDays;
    private Integer[] inventoryIds;
    private Integer currentSurveyIndex;
    private List<SurveyResult> surveyResults = LazyList.ofInstance(SurveyResult.class);
    private List<ScheduledOptOutQuestion> legacyQuestions = LazyList.ofInstance(ScheduledOptOutQuestion.class);

    public static class SurveyResultAnswer {
        private int questionId;
        private String dropDownAnswer;
        private String textAnswer;

        public int getQuestionId() {
            return questionId;
        }

        public void setQuestionId(int questionId) {
            this.questionId = questionId;
        }

        public String getDropDownAnswer() {
            return dropDownAnswer;
        }

        public void setDropDownAnswer(String dropDownAnswer) {
            this.dropDownAnswer = dropDownAnswer;
        }

        public String getTextAnswer() {
            return textAnswer;
        }

        public void setTextAnswer(String textAnswer) {
            this.textAnswer = textAnswer;
        }

        @Override
        public String toString() {
            return "questionId=" + questionId + "; dropDownAnswer=[" + dropDownAnswer + "]; textAnswer=[" + textAnswer
                + "]";
        }
    }

    public static class SurveyResult {
        private int surveyId;
        private boolean answered;
        private List<SurveyResultAnswer> answers = LazyList.ofInstance(SurveyResultAnswer.class);

        public SurveyResult() {
        }

        public SurveyResult(int surveyId) {
            this.surveyId = surveyId;
            answered = false;
        }

        public int getSurveyId() {
            return surveyId;
        }

        public void setSurveyId(int surveyId) {
            this.surveyId = surveyId;
        }

        public boolean isAnswered() {
            return answered;
        }

        public void setAnswered(boolean answered) {
            this.answered = answered;
        }

        public List<SurveyResultAnswer> getAnswers() {
            return answers;
        }

        public void setAnswers(List<SurveyResultAnswer> answers) {
            this.answers = answers;
        }

        @Override
        public String toString() {
            return "surveyId=" + surveyId + "; answered=" + answered;
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }

    public Integer[] getInventoryIds() {
        return inventoryIds;
    }

    public void setInventoryIds(Integer[] inventoryIds) {
        this.inventoryIds = inventoryIds;
    }

    public Integer getCurrentSurveyIndex() {
        return currentSurveyIndex;
    }

    public void setCurrentSurveyIndex(Integer currentSurveyIndex) {
        this.currentSurveyIndex = currentSurveyIndex;
    }

    public List<SurveyResult> getSurveyResults() {
        return surveyResults;
    }

    public void setSurveyResults(List<SurveyResult> surveyResults) {
        this.surveyResults = surveyResults;
    }

    public void createSurveyResults(Iterable<Integer> surveyIds) {
        surveyResults = Lists.newArrayList();
        for (int surveyId : surveyIds) {
            surveyResults.add(new SurveyResult(surveyId));
        }
    }

    public List<ScheduledOptOutQuestion> getLegacyQuestions() {
        return legacyQuestions;
    }

    public void setLegacyQuestions(List<ScheduledOptOutQuestion> legacyQuestions) {
        this.legacyQuestions = legacyQuestions;
    }
}
