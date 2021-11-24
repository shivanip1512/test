package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.RectangleElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.StarsLMSummaryModel;

/**
 * Created on May 22, 2005
 * Creates a STARS LM Summary Report using com.cannontech.analysis.data.StarsLMSummaryModel tableModel
 * 
 * @author snebben
 */
public class StarsLMSummaryReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf StarsLMSummaryModel.
     */
    public StarsLMSummaryReport() {
        this(new StarsLMSummaryModel());
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf StarsLMSummaryModel.
     * 
     * @param data_ - StarsLMSummaryModel TableModel data
     */
    public StarsLMSummaryReport(StarsLMSummaryModel model_) {
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
        ClassicEngineBoot.getInstance().start();
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());

        StarsLMSummaryModel model = new StarsLMSummaryModel();

        // start and stop time are only valid when model.showHist is false
        GregorianCalendar cal = new GregorianCalendar();
        model.setStopDate(cal.getTime());

        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        model.setStartDate(cal.getTime());

        YukonReportBase disconnectReport = new StarsLMSummaryReport(model);
        disconnectReport.getModel().collectData();
        // Create the report
        MasterReport report = disconnectReport.createReport();
        report.setDataFactory(new TableDataFactory("default", disconnectReport.getModel()));

        final PreviewDialog dialog = new PreviewDialog(report);
        // Add a window closing event, even though I think it's already handled by setDefaultCloseOperation(..)
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
            header.addElement(factory.createElement());
        }

        header.addElement(HorizontalLineElementFactory.createHorizontalLine(22, null, new BasicStroke(0.5f)));
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

        if (showBackgroundColor) {
            items.addElement(RectangleElementFactory.createFilledRectangle(0, 0, -100, -100, java.awt.Color.decode("#DFDFDF")));
            items.addElement(HorizontalLineElementFactory.createHorizontalLine(0, java.awt.Color.decode("#DFDFDF"),
                    new BasicStroke(0.1f)));
            items.addElement(HorizontalLineElementFactory.createHorizontalLine(10, java.awt.Color.decode("#DFDFDF"),
                    new BasicStroke(0.1f)));
        }

        for (int i = 0; i < getModel().getColumnNames().length; i++) {
            TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            items.addElement(factory.createElement());
        }
        return items;
    }
}
