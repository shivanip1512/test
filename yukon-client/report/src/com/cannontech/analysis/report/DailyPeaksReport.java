package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
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
import com.cannontech.analysis.tablemodel.DailyPeaksModel;

/**
 * Created on Dec 15, 2003
 * Creates a DatabaseReport using the com.cannontech.analysis.data.DatabaseModel tableModel
 * No Group by used, only by column headings.
 * 
 * @author snebben
 */
public class DailyPeaksReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf DatabaseModel.
     */
    public DailyPeaksReport() {
        this(new DailyPeaksModel());
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf DatabaseModel.
     * 
     * @param data_ - DatabaseModel TableModel data
     */
    public DailyPeaksReport(DailyPeaksModel model_) {
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

        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        Date start = cal.getTime();

        cal.add(java.util.Calendar.DATE, 1);
        Date stop = cal.getTime();

        DailyPeaksModel reportModel = new DailyPeaksModel(start, stop);
        YukonReportBase dbReport = new DailyPeaksReport(reportModel);
        dbReport.getModel().collectData();

        // Create the report
        MasterReport report = dbReport.createReport();
        report.setDataFactory(new TableDataFactory("default", dbReport.getModel()));

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
    private RelationalGroup createControlAreaGroup() {
        final RelationalGroup collHdgGroup = new RelationalGroup();
        collHdgGroup.setName(DailyPeaksModel.CONTROL_AREA_STRING + ReportFactory.NAME_GROUP);
        collHdgGroup.addField(DailyPeaksModel.CONTROL_AREA_STRING);

        GroupHeader header = ReportFactory.createGroupHeaderDefault();

        LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(),
                DailyPeaksModel.CONTROL_ARAEA_COLUMN);
        factory.setText(factory.getText() + ":");
        header.addElement(factory.createElement());

        TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(),
                DailyPeaksModel.CONTROL_ARAEA_COLUMN);
        tfactory.setAbsolutePosition(new Point2D.Float(80, 1)); // override the default posX
        header.addElement(tfactory.createElement());

        tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), DailyPeaksModel.PEAK_TITLE_COLUMN);
        tfactory.setAbsolutePosition(
                new Point2D.Float(getModel().getColumnProperties(DailyPeaksModel.PEAK_TITLE_COLUMN).getPositionX(), 18)); // override
                                                                                                                          // the
                                                                                                                          // default
                                                                                                                          // posX
        tfactory.setHorizontalAlignment(ElementAlignment.CENTER);
        header.addElement(tfactory.createElement());

        tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), DailyPeaksModel.OFF_PEAK_TITLE_COLUMN);
        tfactory.setAbsolutePosition(
                new Point2D.Float(getModel().getColumnProperties(DailyPeaksModel.OFF_PEAK_TITLE_COLUMN).getPositionX(), 18)); // override
                                                                                                                              // the
                                                                                                                              // default
                                                                                                                              // posX
        tfactory.setHorizontalAlignment(ElementAlignment.CENTER);
        header.addElement(tfactory.createElement());

        for (int i = DailyPeaksModel.RANK_COLUMN; i <= DailyPeaksModel.OFF_PEAK_TIME_COLUMN; i++) {
            factory = ReportFactory.createGroupLabelElementDefault(model, i);
            factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 36)); // lower this
                                                                                                                  // row of
                                                                                                                  // "headings"
            header.addElement(factory.createElement());
        }

        header.addElement(ReportFactory.createBasicLine(0.5f, 36));
        collHdgGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();

        tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), DailyPeaksModel.THRESHOLD_COLUMN);
        tfactory.setAbsolutePosition(new Point2D.Float(0, 1)); // overrider the default posX
        footer.addElement(tfactory.createElement());

        tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), DailyPeaksModel.CURRENT_PEAK_VALUE_COLUMN);
        tfactory.setAbsolutePosition(new Point2D.Float(0, 24)); // overrider the default posX
        footer.addElement(tfactory.createElement());

        tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), DailyPeaksModel.CURRENT_PEAK_TIMESTAMP_COLUMN);
        tfactory.setAbsolutePosition(new Point2D.Float(125, 24)); // overrider the default posX
        footer.addElement(tfactory.createElement());

        collHdgGroup.setFooter(footer);

        return collHdgGroup;
    }

    /**
     * Create a GroupList
     * 
     * @return the grouplist
     */
    protected List<RelationalGroup> createGroups() {
        return List.of(createControlAreaGroup());
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

        TextFieldElementFactory factory;
        for (int i = DailyPeaksModel.RANK_COLUMN; i <= DailyPeaksModel.OFF_PEAK_TIME_COLUMN; i++) {
            factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            items.addElement(factory.createElement());
        }

        return items;
    }
}
