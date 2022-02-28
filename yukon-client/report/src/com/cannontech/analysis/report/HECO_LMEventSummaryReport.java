package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.print.PageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.function.FunctionProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.pentahoreport.BoldFormatFunction;
import com.cannontech.analysis.tablemodel.HECO_LMEventSummaryModel;

/**
 * Created on Feb 06, 2003
 * Creates a DisconnectReport using the com.cannontech.analysis.data.DisconnectData tableModel
 * Groups data by Collection Group and then by Device.
 * 
 * @author snebben
 * @author bjonasson
 */
public class HECO_LMEventSummaryReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf LoadControlEventSummaryModel.
     */
    public HECO_LMEventSummaryReport() {
        this(new HECO_LMEventSummaryModel());
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf LoadControlEventSummaryModel.
     * 
     * @param data_ - DatabaseModel TableModel data
     */
    public HECO_LMEventSummaryReport(HECO_LMEventSummaryModel model_) {
        super();
        setModel(model_);
    }

    /**
     * Runs this report and shows a preview dialog.
     * 
     * @param args the arguments (ignored).
     * @throws Exception if an error occurs (default: print a stack trace)
     */
    public static void main(final String[] args) throws Exception {
        // initialize Report
        ClassicEngineBoot.getInstance().start();
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());

        HECO_LMEventSummaryModel model = new HECO_LMEventSummaryModel();

        // start and stop time are only valid when model.showHist is false
        GregorianCalendar cal = new GregorianCalendar();
        model.setStopDate(cal.getTime());

        cal.set(Calendar.MONTH, Calendar.NOVEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 14);
        model.setStartDate(cal.getTime());

        cal.set(Calendar.DAY_OF_MONTH, 15);
        model.setStopDate(cal.getTime());
        YukonReportBase lcSummaryReport = new HECO_LMEventSummaryReport(model);
        lcSummaryReport.getModel().collectData();
        // Create the report
        MasterReport report = lcSummaryReport.createReport();
        report.setDataFactory(new TableDataFactory("default", lcSummaryReport.getModel()));

        final PreviewDialog dialog = new PreviewDialog(report);
        // Add a window closeing event, even though I think it's already handled by setDefaultCloseOperation(..)
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
                System.exit(0);
            };
        });

        dialog.setModal(true);
        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * Create a Group for Column Headings only.
     * 
     * @return Group
     */
    private RelationalGroup createColumnHeadingGroup() {
        final RelationalGroup collHdgGroup = new RelationalGroup();
        collHdgGroup.setName("Column Heading");

        GroupHeader header = ReportFactory.createGroupHeaderDefault();

        LabelElementFactory factory;
        for (int i = 0; i < getModel().getColumnNames().length; i++) {
            factory = ReportFactory.createGroupLabelElementDefault(model, i);
            factory.setWrapText(Boolean.TRUE);
            if (i > HECO_LMEventSummaryModel.DURATION_COLUMN)
                factory.setHorizontalAlignment(ElementAlignment.RIGHT);
            header.addElement(factory.createElement());
        }

        header.addElement(HorizontalLineElementFactory.createHorizontalLine(22, null, new BasicStroke(0.5f)));
        //"line1", null, new BasicStroke(0.5f), 22)
        collHdgGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        collHdgGroup.setFooter(footer);

        return collHdgGroup;
    }

    /**
     * Create a GroupList and all Group(s) to it.
     * 
     * @return the groupList.
     */
    protected List<RelationalGroup> createGroups() {
        return List.of(createColumnHeadingGroup());
    }

    /**
     * Creates the itemBand, the rows of data.
     * 
     * @return the item band.
     */
    protected ItemBand createItemBand() {
        ItemBand items = ReportFactory.createItemBandDefault();
        items.addElement(HorizontalLineElementFactory.createHorizontalLine(0, java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f)));

        for (int i = 0; i < getModel().getColumnNames().length; i++) {
            TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            if (i > HECO_LMEventSummaryModel.DURATION_COLUMN)
                factory.setHorizontalAlignment(ElementAlignment.RIGHT);
            else
                factory.setNullString("");
            items.addElement(factory.createElement());
        }
        return items;
    }

    /**
     * Creates the function collection. The xml definition for this construct:
     * 
     * @return the functions.
     * @throws FunctionProcessingException if there is a problem processing the functions.
     */
    protected ExpressionCollection getExpressions() throws FunctionProcessingException {
        super.getExpressions();

        BoldFormatFunction boldItem = new BoldFormatFunction(HECO_LMEventSummaryModel.START_TIME_COLUMN);
        boldItem.setName("boldItem");
        boldItem.setElement(getModel().getColumnName(HECO_LMEventSummaryModel.START_TIME_COLUMN) + ReportFactory.NAME_ELEMENT);
        expressions.add(boldItem);
        return expressions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cannontech.analysis.report.YukonReportBase#getPageDefinition()
     */
    public SimplePageDefinition getPageDefinition() {
        if (pageDefinition == null) {
            java.awt.print.Paper reportPaper = new java.awt.print.Paper();
            // Adjust the imagable width in the case of the columns being too large for one page.
            ColumnProperties prop = getModel().getColumnProperties(getModel().getColumnProperties().length - 1);
            int totalWidth = (int) (prop.getPositionX() + prop.getWidth());

            int numPagesWide = (totalWidth / 732) + 1;

            reportPaper.setImageableArea(30, 30, 552, 732); // 8.5 x 11 -> 612w 792h
            PageFormat pageFormat = new java.awt.print.PageFormat();
            pageFormat.setOrientation(getPageOrientation());
            pageFormat.setPaper(reportPaper);
            pageDefinition = new SimplePageDefinition(pageFormat, numPagesWide, 1);
        }
        return pageDefinition;
    }
}
