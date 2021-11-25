package com.cannontech.analysis.report;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.function.AggregateFooterFieldFactory;
import com.cannontech.analysis.function.LabelFooterFieldFactory;
import com.cannontech.analysis.function.SumFooterFieldFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CurtailmentEventSummaryModel;
import com.cannontech.spring.YukonSpringHook;

public class CurtailmentEventSummaryReport extends SingleGroupYukonReportBase {
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Event #", "eventNumber", 50),
        new ColumnLayoutData("State", "state", 80),
        new ColumnLayoutData("Notification Time", "notificationDate", 120),
        new ColumnLayoutData("Start Date", "startDate", 120),
        new ColumnLayoutData("Stop Time", "stopDate", 120),
        new ColumnLayoutData("Duration (hours)", "durationHours", 90),
        new ColumnLayoutData("Type", "type", 150),
    };
    private static final AggregateFooterFieldFactory footerColumns[] = new AggregateFooterFieldFactory[] {
        new LabelFooterFieldFactory(bodyColumns[0], "Total"),
        new SumFooterFieldFactory(bodyColumns[5]),
    };
    
    public CurtailmentEventSummaryReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    @Override
    protected List<? extends AggregateFooterFieldFactory> getFooterColumns() {
        return Arrays.asList(footerColumns);
    }

    @Override
    protected ColumnLayoutData getGroupFieldData() {
        return new ColumnLayoutData("Customer Name", "customerName", 140);
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
        // initialize Report
        ClassicEngineBoot.getInstance().start();
        javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());
        YukonSpringHook.setDefaultContext(YukonSpringHook.WEB_BEAN_FACTORY_KEY);
        
        CurtailmentEventSummaryModel model = new CurtailmentEventSummaryModel();
        YukonReportBase rmReport = new CurtailmentEventSummaryReport(model);
        rmReport.getModel().setEnergyCompanyID(0);
        rmReport.getModel().setStartDate(DateFormat.getDateInstance(DateFormat.SHORT).parse("1/1/2007"));
        rmReport.getModel().setStopDate(DateFormat.getDateInstance(DateFormat.SHORT).parse("12/31/2007"));
        ReportFuncs.generatePreview(rmReport);
    }

}
