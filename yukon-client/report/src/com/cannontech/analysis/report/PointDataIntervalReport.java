package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.util.Calendar;
import java.util.Date;
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
import com.cannontech.analysis.tablemodel.PointDataIntervalModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;

/**
 * Created on Dec 15, 2003
 * Creates a DatabaseReport using the com.cannontech.analysis.data.DatabaseModel tableModel
 * No Group by used, only by column headings.
 * 
 * @author snebben
 */
public class PointDataIntervalReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf DatabaseModel.
     */
    public PointDataIntervalReport() {
        this(new PointDataIntervalModel());
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf DatabaseModel.
     * 
     * @param data_ - DatabaseModel TableModel data
     */
    public PointDataIntervalReport(PointDataIntervalModel model_) {
        super();
        setPageOrientation(PageFormat.PORTRAIT);
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
        cal.set(java.util.Calendar.DATE, 28);
        cal.set(Calendar.MONTH, Calendar.APRIL);
        Date stop = cal.getTime();
        cal.add(java.util.Calendar.DATE, -1);
        Date start = cal.getTime();

        PointDataIntervalModel reportModel = new PointDataIntervalModel(start, stop);
        YukonReportBase dbReport = new PointDataIntervalReport(reportModel);

        reportModel.setOrderBy(PointDataIntervalModel.ORDER_BY_VALUE);
        reportModel.setPointType(PointDataIntervalModel.LOAD_PROFILE_POINT_TYPE);
        reportModel.setSortOrder(ReportModelBase.DESCENDING);
        reportModel.setBillingGroups(new String[] { "Default" });
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
     * Create a Group for Point Name.
     * 
     * @return Group
     */
    private RelationalGroup createPointGroup() {
        final RelationalGroup pointGroup = new RelationalGroup();
        pointGroup.setName(PointDataIntervalModel.POINT_NAME_STRING + ReportFactory.NAME_GROUP);
        pointGroup.addField(PointDataIntervalModel.PAO_NAME_STRING);
        pointGroup.addField(PointDataIntervalModel.POINT_NAME_STRING);

        GroupHeader header = ReportFactory.createGroupHeaderDefault();
        header.setRepeat(true);

        LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(),
                PointDataIntervalModel.PAO_NAME_COLUMN);
        factory.setText(factory.getText() + ":");
        header.addElement(factory.createElement());

        TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(),
                PointDataIntervalModel.PAO_NAME_COLUMN);
        tfactory.setAbsolutePosition(new Point2D.Float(75, 1)); // override the posX location
        header.addElement(tfactory.createElement());

        factory = ReportFactory.createGroupLabelElementDefault(getModel(), PointDataIntervalModel.POINT_NAME_COLUMN);
        factory.setText(factory.getText() + ":");
        header.addElement(factory.createElement());

        tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), PointDataIntervalModel.POINT_NAME_COLUMN);
        tfactory.setAbsolutePosition(new Point2D.Float(360, 1));
        header.addElement(tfactory.createElement());

        for (int i = PointDataIntervalModel.DATE_COLUMN; i <= PointDataIntervalModel.QUALITY_COLUMN; i++) {
            factory = ReportFactory.createGroupLabelElementDefault(model, i);
            factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 18));
            header.addElement(factory.createElement());
        }

        header.addElement(ReportFactory.createBasicLine(0.5f, 20));
        pointGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        pointGroup.setFooter(footer);

        return pointGroup;
    }

    /**
     * Create a GroupList and all Group(s) to it.
     * 
     * @return the groupList.
     */
    protected List<RelationalGroup> createGroups() {
        return List.of(createPointGroup());
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
        for (int i = PointDataIntervalModel.DATE_COLUMN; i <= PointDataIntervalModel.QUALITY_COLUMN; i++) {
            factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            items.addElement(factory.createElement());
        }

        return items;
    }
}
