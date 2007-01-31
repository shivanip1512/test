package com.cannontech.analysis.report;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;

import org.jfree.report.JFreeReportBoot;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CurtailmentEventSummaryModel;

public class CurtailmentEventSummaryReport extends SingleGroupYukonReportBase {
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData(1, 50),
        new ColumnLayoutData(3, 140),
        new ColumnLayoutData(2, 140),
        new ColumnLayoutData(4, 140),
        new ColumnLayoutData(5, 90),
        new ColumnLayoutData(6, 150),
    };
    
    public CurtailmentEventSummaryReport(BareReportModel bareModel) {
        super(bareModel);
    }

    @Override
    protected ColumnLayoutData getGroupFieldData() {
        return new ColumnLayoutData(0, 140);
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

    /**
     * Runs this report and shows a preview dialog.
     * @param args the arguments (ignored).
     * @throws Exception if an error occurs (default: print a stack trace)
     */
    public static void main(final String[] args) throws Exception {
        // initialize JFreeReport
        JFreeReportBoot.getInstance().start();
        javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());
        
        CurtailmentEventSummaryModel model = new CurtailmentEventSummaryModel();
        YukonReportBase rmReport = new CurtailmentEventSummaryReport(model);
        rmReport.getModel().setEnergyCompanyID(0);
        rmReport.getModel().setStartDate(DateFormat.getDateInstance(DateFormat.SHORT).parse("1/1/2006"));
        rmReport.getModel().setStopDate(DateFormat.getDateInstance(DateFormat.SHORT).parse("12/31/2006"));
        ReportFuncs.generatePreview(rmReport);
    }

}
