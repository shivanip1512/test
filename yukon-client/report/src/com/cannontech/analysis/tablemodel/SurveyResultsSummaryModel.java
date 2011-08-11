package com.cannontech.analysis.tablemodel;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.stars.dr.optout.model.SurveyResult;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;


public class SurveyResultsSummaryModel extends SurveyResultsModelBase<SurveyResultsSummaryModel.ModelRow> {
    // inputs
    // all inputs are covered by base class

    // member variables
    private static String title = "Survey Results Summary Report";

    public static class ModelRow {
        ModelRow(ProgramControlHistory hist) {
            controlBegin = hist.getStartDateTime();
            controlEnd = hist.getStopDateTime();
            loadProgram = hist.getProgramName();
            gear = hist.getGearName();
        }
        public Date controlBegin = null;
        public Date controlEnd = null;
        public Object reason = null;
        public int numDevicesOverridden = 0;
        public String loadProgram = null;
        public String gear = null;
    }

    private static class ProgramHistorySummary {
        ProgramControlHistory hist;
        Map<Integer, ModelRow> dropDownAnswerRows = Maps.newTreeMap();
        ModelRow unansweredRow = null;
        Map<String, ModelRow> otherAnswerRows = Maps.newTreeMap();

        ProgramHistorySummary(ProgramControlHistory hist) {
            this.hist = hist;
        }

        // The program control history here is used only if a new row needs to be created.
        ModelRow rowForResult(SurveyResult result, Object reason) {
            Integer answerId = result.getSurveyQuestionAnswerId();
            if (answerId != null) {
                ModelRow retVal = dropDownAnswerRows.get(answerId);
                if (retVal == null) {
                    retVal = new ModelRow(hist);
                    retVal.reason = reason;
                    dropDownAnswerRows.put(answerId, retVal);
                }
                return retVal;
            }
            if (!StringUtils.isEmpty(result.getTextAnswer())) {
                ModelRow retVal = otherAnswerRows.get(result.getTextAnswer());
                if (retVal == null) {
                    retVal = new ModelRow(hist);
                    retVal.reason = reason;
                    otherAnswerRows.put(result.getTextAnswer(), retVal);
                }
                return retVal;
            }
            if (unansweredRow == null) {
                unansweredRow = new ModelRow(hist);
                unansweredRow.reason = reason;
            }
            return unansweredRow;
        }
    }

    public void doLoadData() {
        ReadableInstant startDate = getStartDateAsInstant();
        if (startDate == null) {
            startDate = new Instant(0);
        }

        Multimap<SurveyResult, Integer> assignedProgramIdsBySurveyResult = ArrayListMultimap.create();
        Map<Integer, List<ProgramEnrollment>> enrollmentsBySurveyResultId = Maps.newHashMap();
        List<SurveyResult> surveyResults =
            optOutSurveyDao.findSurveyResults(surveyId, questionId, answerIds,
                                              includeOtherAnswers, includeUnanswered, false,
                                              startDate, getStopDateAsInstant(),
                                              null, null);
        for (SurveyResult result : surveyResults) {
            List<ProgramEnrollment> enrollments =
                enrollmentDao.getHistoricalEnrollmentsByInventoryId(result.getInventoryId(),
                                                                    result.getWhenTaken());
            enrollmentsBySurveyResultId.put(result.getSurveyResultId(), enrollments);
            for (ProgramEnrollment enrollment : enrollments) {
                assignedProgramIdsBySurveyResult.put(result, enrollment.getAssignedProgramId());
            }
        }

        Map<Integer, ProgramHistorySummary> resultsByProgramHistoryId = Maps.newHashMap();

        Survey survey = surveyDao.getSurveyById(surveyId);
        Question question = surveyDao.getQuestionById(questionId);
        String baseKey = survey.getBaseKey(question);
        Map<Integer, ProgramControlHistory> programControlHistoriesById = Maps.newHashMap();
        Map<Integer, Integer> programIdsByAssignedProgramId =
            assignedProgramDao.getProgramIdsByAssignedProgramIds(assignedProgramIdsBySurveyResult.values());
        Set<Integer> programIdsToUse = null;
        if (programIds != null && !programIds.isEmpty()) {
            programIdsToUse = Sets.newHashSet(programIds);
        }
        for (SurveyResult result : surveyResults) {
            List<ProgramEnrollment> enrollments = enrollmentsBySurveyResultId.get(result.getSurveyResultId());
            for (ProgramEnrollment enrollment : enrollments) {
                int programId = programIdsByAssignedProgramId.get(enrollment.getAssignedProgramId());
                if (programIdsToUse == null || programIdsToUse.contains(programId)) {
                    ProgramControlHistory hist =
                        loadControlProgramDao.findHistoryForProgramAtTime(programId, result.getWhenTaken());
                    if (hist == null) {
                        // The opt out happened during a time for which there was no
                        // control.  (In the future we may want to count these too.)
                    } else {
                        int programHistoryId = hist.getProgramHistoryId();
                        ProgramHistorySummary histSummary = resultsByProgramHistoryId.get(programHistoryId);
                        if (histSummary == null) {
                            histSummary = new ProgramHistorySummary(hist);
                            resultsByProgramHistoryId.put(programHistoryId, histSummary);
                        }

                        ModelRow summaryRow = histSummary.rowForResult(result, getReason(result, baseKey));
                        summaryRow.numDevicesOverridden++;

                        programControlHistoriesById.put(programHistoryId, hist);
                    }
                }
            }
        }

        List<ProgramHistorySummary> sortedProgramHistorySummaries =
            Lists.newArrayList(resultsByProgramHistoryId.values());
        Collections.sort(sortedProgramHistorySummaries, new Comparator<ProgramHistorySummary>() {
            @Override
            public int compare(ProgramHistorySummary hist1,
                    ProgramHistorySummary hist2) {
                return hist1.hist.getStartDateTime().compareTo(hist2.hist.getStartDateTime());
            }});
        for (ProgramHistorySummary histSummary : sortedProgramHistorySummaries) {
            for (ModelRow row : histSummary.dropDownAnswerRows.values()) {
                data.add(row);
            }
            for (ModelRow row : histSummary.otherAnswerRows.values()) {
                data.add(row);
            }
            if (includeUnanswered && histSummary.unansweredRow != null) {
                data.add(histSummary.unansweredRow);
            }
        }
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
