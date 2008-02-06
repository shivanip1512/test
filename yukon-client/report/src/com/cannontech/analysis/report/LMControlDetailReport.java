package com.cannontech.analysis.report;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;

import org.jfree.report.JFreeReportBoot;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.function.AggregateFooterFieldFactory;
import com.cannontech.analysis.function.LabelFooterFieldFactory;
import com.cannontech.analysis.function.SumFooterFieldFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.LMControlDetailModel;
import com.cannontech.spring.YukonSpringHook;

public class LMControlDetailReport extends SingleGroupYukonReportBase {
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Program Name", "program", 100),
        new ColumnLayoutData("Enrollment Start", "enrollmentStart", 120),
        new ColumnLayoutData("Enrollment Stop", "enrollmentStop", 120),
        new ColumnLayoutData("Control Hours", "controlHours", 90),
        new ColumnLayoutData("Opt Out Control Hours", "optOutHours", 90),
        new ColumnLayoutData("Opt Out Events", "optOutEvents", 90)
    };
    
    private static final AggregateFooterFieldFactory footerColumns[] = new AggregateFooterFieldFactory[] {
        new LabelFooterFieldFactory(bodyColumns[0], "Total"),
        new SumFooterFieldFactory(bodyColumns[3]),
        new SumFooterFieldFactory(bodyColumns[4]),
        new SumFooterFieldFactory(bodyColumns[5]),
    };
    
    public LMControlDetailReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    @Override
    protected List<? extends AggregateFooterFieldFactory> getFooterColumns() {
        return Arrays.asList(footerColumns);
    }

    @Override
    protected ColumnLayoutData getGroupFieldData() {
        return new ColumnLayoutData("Account: ", "accountNumberAndName", 220);
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
        YukonSpringHook.setDefaultContext(YukonSpringHook.WEB_BEAN_FACTORY_KEY);
        
        LMControlDetailModel model = new LMControlDetailModel();
        model.setEnergyCompanyId(0);
        model.setStartDate(DateFormat.getDateInstance(DateFormat.SHORT).parse("1/1/2008"));
        model.setStopDate(DateFormat.getDateInstance(DateFormat.SHORT).parse("1/31/2008"));
        YukonReportBase rmReport = new LMControlDetailReport(model);
        ReportFuncs.generatePreview(rmReport);
    }

}
