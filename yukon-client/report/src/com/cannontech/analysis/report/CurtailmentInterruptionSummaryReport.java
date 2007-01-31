package com.cannontech.analysis.report;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;

import org.jfree.report.ElementAlignment;
import org.jfree.report.JFreeReportBoot;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CurtailmentInterruptionSummaryModel;

/**
 * Created on Mar 26, 2004
 * Creates a DatabaseReport using the com.cannontech.analysis.data.DatabaseModel tableModel
 * No Group by used, only by column headings.
 *  
 * @author snebben
 */
public class CurtailmentInterruptionSummaryReport extends SimpleYukonReportBase {
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData(0, 160),
        new ColumnLayoutData(1, 60, "#0.0#"), 
        new ColumnLayoutData(2, 60, "#0.0#"),
        new ColumnLayoutData(3, 60, "#0.0#"),
        new ColumnLayoutData(4, 60, "#,###", ElementAlignment.RIGHT),
        new ColumnLayoutData(5, 60, "#,###", ElementAlignment.RIGHT),
        new ColumnLayoutData(6, 60, "#0.00#"),
        new ColumnLayoutData(7, 60, "#,###", ElementAlignment.RIGHT),
        new ColumnLayoutData(8, 60, "#,###", ElementAlignment.RIGHT),
    };
    
    public CurtailmentInterruptionSummaryReport() {
        this(new CurtailmentInterruptionSummaryModel());
    }
    
    public CurtailmentInterruptionSummaryReport(BareReportModel model) {
        super(model);
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
