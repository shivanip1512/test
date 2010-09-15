package com.cannontech.analysis.tablemodel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.google.common.collect.Multimap;


public class SurveyResultsSummaryModel extends
        SurveyResultsModelBase<SurveyResultsSummaryModel.ModelRow> {
    private LoadControlProgramDao loadControlProgramDao;

    // inputs
    // all inputs are covered by base class

    // member variables
    private static String title = "Survey Results Summary Report";

    public static class ModelRow {
        public String reason;
        public int numDevicesOverridden;
        public String loadProgram;
        public String gear;
    }

    public void doLoadData() {
        ModelRow row = new ModelRow();
        row.reason = "summary from " + startDate + " to " + endDate +
            " for question " + questionId;
        row.numDevicesOverridden = programIds == null ? 0 : programIds.size();
        row.loadProgram = "answers: " + StringUtils.join(answerIds, ",");
        row.gear = "other:" + includeOtherAnswers + ", unanswerd:" + includeUnanswered;
        data.add(row);

        if (startDate == null) {
            startDate = CtiUtilities.get1990GregCalendar().getTime();
        }
        /*
        Multimap<Integer, ProgramControlHistory> controlHistory =
            loadControlProgramDao.getHistoryByProgramIds(programIds, startDate,
                                                         endDate);

        for (Integer programId : controlHistory.keySet()) {
            for (ProgramControlHistory hist : controlHistory.get(programId)) {
                row = new ModelRow();
                row.reason = hist.getStartDateTime() + " to " + hist.getStopDateTime();
                row.numDevicesOverridden = hist.getProgramId();
                row.loadProgram = hist.getProgramName();
                row.gear = hist.getGearName();
            }
        }
        */
        if (programIds == null) {
            return;
        }
        for (Integer programId : programIds) {
            row = new ModelRow();
            row.reason = "program";
            row.numDevicesOverridden = programId;
            row.loadProgram = "";
            row.gear = "";
            data.add(row);
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

    @Autowired
    public void setLoadControlProgramDao(LoadControlProgramDao loadControlProgramDao) {
        this.loadControlProgramDao = loadControlProgramDao;
    }
}
