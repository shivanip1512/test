package com.cannontech.analysis.tablemodel;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.optout.model.SurveyResult;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class SurveyResultsDetailModel extends SurveyResultsModelBase<SurveyResultsDetailModel.ModelRow> {

    // inputs
    private String accountNumber;
    private String deviceSerialNumber;

    // member variables
    private static String title = "Survey Results Detail Report";

    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private InventoryDao inventoryDao;

    private final static MessageSourceResolvable noControlDuringOptOut =
        new YukonMessageSourceResolvable("yukon.web.modules.survey.report.noControlDuringOptOut");
    private final static MessageSourceResolvable hardwareNotFound =
        new YukonMessageSourceResolvable("yukon.web.modules.survey.report.hardwareNotFound");

    public static class ModelRow {
        public String accountNumber;
        public MessageSourceResolvable serialNumber;
        public String altTrackingNumber;
        public Object reason;
        public Date scheduledDate;
        public Date startDate;
        public Date endDate;
        public Object loadProgram;
        public String gear;

        private ModelRow() {}
        private ModelRow(ModelRow toCopy) {
            accountNumber = toCopy.accountNumber;
            serialNumber = toCopy.serialNumber;
            altTrackingNumber = toCopy.altTrackingNumber;
            reason = toCopy.reason;
            scheduledDate = toCopy.scheduledDate;
            startDate = toCopy.startDate;
            endDate = toCopy.endDate;
        }
    }

    public void doLoadData() {
        ReadableInstant startDate = getStartDateAsInstant();
        if (startDate == null) {
            startDate = new Instant(0);
        }

        boolean reportIsFiltered = programIds != null && !programIds.isEmpty();
        Set<Integer> programIdsToUse = Sets.newHashSet(getAuthorizedPrograms());

        List<SurveyResult> surveyResults =
            optOutSurveyDao.findSurveyResults(surveyId, questionId, answerIds,
                                              includeOtherAnswers, includeUnanswered, true,
                                              startDate, getStopDateAsInstant(),
                                              accountNumber, deviceSerialNumber);

        Multimap<SurveyResult, Integer> assignedProgramIdsBySurveyResult = ArrayListMultimap.create();
        Map<Integer, List<ProgramEnrollment>> enrollmentsBySurveyResultId = Maps.newHashMap();
        Set<Integer> inventoryIds = Sets.newHashSet();
        List<Integer> accountIds = Lists.newArrayList();
        for (SurveyResult result : surveyResults) {
            List<ProgramEnrollment> enrollments =
                enrollmentDao.getHistoricalEnrollmentsByInventoryId(result.getInventoryId(),
                                                                    result.getWhenTaken());
            enrollmentsBySurveyResultId.put(result.getSurveyResultId(), enrollments);
            for (ProgramEnrollment enrollment : enrollments) {
                assignedProgramIdsBySurveyResult.put(result, enrollment.getAssignedProgramId());
            }

            Integer accountId = result.getAccountId();
            if (accountId != null && accountId != 0) {
                accountIds.add(result.getAccountId());
            }
            
            inventoryIds.add(result.getInventoryId());
        }

        Survey survey = surveyDao.getSurveyById(surveyId);
        Question question = surveyDao.getQuestionById(questionId);
        String baseKey = survey.getBaseKey(question);

        Map<Integer, Integer> programIdsByAssignedProgramId =
            assignedProgramDao.getProgramIdsByAssignedProgramIds(assignedProgramIdsBySurveyResult.values());
        Map<Integer, HardwareSummary> hardwareSummariesById =
            inventoryDao.findHardwareSummariesById(inventoryIds);
        Map<Integer, CustomerAccountWithNames> accountsByAccountId =
            customerAccountDao.getAccountsWithNamesByAccountId(accountIds);

        for (SurveyResult result : surveyResults) {
            List<ProgramEnrollment> enrollments = enrollmentsBySurveyResultId.get(result.getSurveyResultId());
            List<ProgramControlHistory> programsControlledDuringOptOut = Lists.newArrayList();
            
            boolean programControlHistoryExists = false;
            
            for (ProgramEnrollment enrollment : enrollments) {
                int programId = programIdsByAssignedProgramId.get(enrollment.getAssignedProgramId());
                // This is going to skip putting anything in programsControlledDuringOptOut if:
                //    a) there was no control at all,
                //    b) there was control but it was not by a program we care about/is visible to us.
                ProgramControlHistory hist =
                        loadControlProgramDao.findHistoryForProgramAtTime(programId, result.getWhenTaken());
                if (hist != null ) {
                    // There was control history for this enrollment.
                    programControlHistoryExists = true;
                    if (programIdsToUse.contains(programId)) {
                        // The LM program is visible to this user.
                        programsControlledDuringOptOut.add(hist);
                    }
                } else {
                    // There was no history or this LM program is not visible to this user.
                }
            }
            
            if (!programsControlledDuringOptOut.isEmpty()) {
                ModelRow row = buildRowBasis(baseKey, result, hardwareSummariesById,
                        accountsByAccountId);
                for (ProgramControlHistory hist : programsControlledDuringOptOut) {
                    ModelRow programRow = new ModelRow(row);
                    programRow.loadProgram = hist.getProgramName();
                    programRow.gear = hist.getGearName();
                    data.add(programRow);
                }
            } else {
                // The programsControlledDuringOptOut list can be empty for two reasons:
                //   a) There was no relevant control.
                //   b) There was control but not for any programs we care about/are allowed to see.  

                if (reportIsFiltered) {
                    // Don't show, there is user-selected filtering and no control history for this result.
                    continue;
                } else {
                    if (!programControlHistoryExists) {
                        // a) Add 'No control', there is no relevant control for this result & no user-selected filtering.
                        ModelRow programRow = buildRowBasis(baseKey, result, hardwareSummariesById,
                                accountsByAccountId);
                        programRow.loadProgram =  noControlDuringOptOut;
                        programRow.gear = "";
                        data.add(programRow);
                    } else {
                        // b) Skip this row, user does not have permission to view this result.
                        continue;
                    }
                }
            }
        }
    }

    private ModelRow buildRowBasis(String baseKey, SurveyResult result,
            Map<Integer, HardwareSummary> hardwareSummariesById,
            Map<Integer, CustomerAccountWithNames> accountsByAccountId) {
        ModelRow row = new ModelRow(); 

        row.accountNumber = result.getAccountNumber();
        HardwareSummary hardwareSummary = hardwareSummariesById.get(result.getInventoryId());
        if (hardwareSummary != null) {
            row.serialNumber = YukonMessageSourceResolvable.createDefaultWithoutCode(hardwareSummary.getSerialNumber());
        } else {
            row.serialNumber = hardwareNotFound;
        }
        CustomerAccountWithNames account = accountsByAccountId.get(result.getAccountId());
        if (account != null) {
            row.altTrackingNumber = account.getAlternateTrackingNumber();
        } else {
            row.altTrackingNumber = "";
        }
        row.reason = getReason(result, baseKey);
        row.scheduledDate = result.getScheduledDate().toDate();
        row.startDate = result.getStartDate().toDate();
        row.endDate = result.getStopDate().toDate();
        
        return row;
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

}
