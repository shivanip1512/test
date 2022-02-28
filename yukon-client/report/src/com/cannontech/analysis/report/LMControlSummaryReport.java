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
import com.cannontech.analysis.tablemodel.LMControlSummaryModel;
import com.cannontech.spring.YukonSpringHook;

public class LMControlSummaryReport extends SingleGroupYukonReportBase {
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("", "total", 50),
        new ColumnLayoutData("Number of Enrolled Participants", "enrolledCustomers", 120),
        new ColumnLayoutData("Number of Enrolled Inventory", "enrolledInventory", 120),
        new ColumnLayoutData("Total Customer Control Hours", "controlHours", 120),
        new ColumnLayoutData("Total Customer Opt Out Control Hours", "totalOptOutHoursDuringControl", 120),
        new ColumnLayoutData("Total Inventory Opt Out Events", "optOutEvents", 120)
    };
    
    private static final AggregateFooterFieldFactory footerColumns[] = new AggregateFooterFieldFactory[] {
    	
    	new LabelFooterFieldFactory(bodyColumns[0], "Total"),
        new SumFooterFieldFactory(bodyColumns[1]),
        new SumFooterFieldFactory(bodyColumns[2]),
        new SumFooterFieldFactory(bodyColumns[3]),
        new SumFooterFieldFactory(bodyColumns[4]),
        new SumFooterFieldFactory(bodyColumns[5]),
    };
    
    public LMControlSummaryReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    @Override
    protected List<? extends AggregateFooterFieldFactory> getFooterColumns() {
        return Arrays.asList(footerColumns);
    }

    @Override
    protected ColumnLayoutData getGroupFieldData() {
        return new ColumnLayoutData("Program: ", "program", 150);
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
        
        LMControlSummaryModel model = new LMControlSummaryModel();
        model.setEnergyCompanyId(0);
        model.setStartDate(DateFormat.getDateInstance(DateFormat.SHORT).parse("1/1/2008"));
        model.setStopDate(DateFormat.getDateInstance(DateFormat.SHORT).parse("1/31/2008"));
        YukonReportBase rmReport = new LMControlSummaryReport(model);
        ReportFuncs.generatePreview(rmReport);
    }

}
