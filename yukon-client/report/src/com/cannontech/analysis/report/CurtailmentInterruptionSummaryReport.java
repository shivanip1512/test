package com.cannontech.analysis.report;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;

import org.jfree.report.ElementAlignment;
import org.jfree.report.JFreeReportBoot;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.function.AggregateFooterFieldFactory;
import com.cannontech.analysis.function.LabelFooterFieldFactory;
import com.cannontech.analysis.function.SumFooterFieldFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CurtailmentInterruptionSummaryModel;
import com.cannontech.spring.YukonSpringHook;

/**
 * Created on Mar 26, 2004
 * Creates a DatabaseReport using the com.cannontech.analysis.data.DatabaseModel tableModel
 * No Group by used, only by column headings.
 *  
 * @author snebben
 */
public class CurtailmentInterruptionSummaryReport extends SimpleYukonReportBase {
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Customer Name", "customername", 140),
        new ColumnLayoutData("Contract Hours", "interruptHoursContract", 50).setFormat("#0.0#").setHorizontalAlignment(ElementAlignment.RIGHT), 
        new ColumnLayoutData("Remaining Hours", "interruptHoursRemaining", 50).setFormat("#0.0#").setHorizontalAlignment(ElementAlignment.RIGHT),
        new ColumnLayoutData("Used Hours", "interruptHoursUsed", 40).setFormat("#0.0#").setHorizontalAlignment(ElementAlignment.RIGHT),
        new ColumnLayoutData("CIL", "cil", 40).setFormat("#,###").setHorizontalAlignment(ElementAlignment.RIGHT),
        new ColumnLayoutData("Notice (mins)", "noticeMinutes", 40).setFormat("#,###").setHorizontalAlignment(ElementAlignment.RIGHT),
        new ColumnLayoutData("Min Duration", "minEventDuration", 40).setFormat("#,###").setHorizontalAlignment(ElementAlignment.RIGHT),
        new ColumnLayoutData("Max 24 Hours", "interruptHrs24Hr", 40).setFormat("#,###").setHorizontalAlignment(ElementAlignment.RIGHT),
        new ColumnLayoutData("Adv. Election $/kW", "advancedElectionPricePerkW", 60).setFormat("#0.00#").setHorizontalAlignment(ElementAlignment.RIGHT),
        new ColumnLayoutData("Adv. Election kW", "advancedElectionkW", 60).setFormat("#,###").setHorizontalAlignment(ElementAlignment.RIGHT),
        new ColumnLayoutData("CFD", "cfd", 55).setFormat("#,###").setHorizontalAlignment(ElementAlignment.RIGHT),
    };
    private static final AggregateFooterFieldFactory footerColumns[] = new AggregateFooterFieldFactory[] {
        new LabelFooterFieldFactory(bodyColumns[0], "Total"),
        new SumFooterFieldFactory(bodyColumns[4]),
        new SumFooterFieldFactory(bodyColumns[8]),
    };
    
    public CurtailmentInterruptionSummaryReport() {
        this(new CurtailmentInterruptionSummaryModel());
    }
    
    public CurtailmentInterruptionSummaryReport(BareReportModel model) {
        super(model);
    }
    
    @Override
    protected List<? extends AggregateFooterFieldFactory> getFooterColumns() {
        return Arrays.asList(footerColumns);
    }
    
    @Override
    public int getExtraFieldSpacing() {
        return 15;
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
        
        CurtailmentInterruptionSummaryModel model = new CurtailmentInterruptionSummaryModel();
        YukonReportBase rmReport = new CurtailmentInterruptionSummaryReport(model);
        rmReport.getModel().setEnergyCompanyID(0);
        rmReport.getModel().setStartDate(DateFormat.getDateInstance(DateFormat.SHORT).parse("1/1/2006"));
        rmReport.getModel().setStopDate(DateFormat.getDateInstance(DateFormat.SHORT).parse("12/31/2006"));
        ReportFuncs.generatePreview(rmReport);
    }
    
    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
    
}
