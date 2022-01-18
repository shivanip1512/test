package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.util.Calendar;
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
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.LoadControlVerificationModel;

/**
 * Created on Oct 21, 2005
 * No Group by used, only by column headings.
 * 
 * @author snebben
 */
public class LoadControlVerificationReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf LoadControlVerificationModel.
     */
    public LoadControlVerificationReport() {
        this(new LoadControlVerificationModel());
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf LoadControlVerificationModel.
     * 
     * @param data_ - LoadControlVerificationModel TableModel data
     */
    public LoadControlVerificationReport(LoadControlVerificationModel model_) {
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

        LoadControlVerificationModel reportModel = new LoadControlVerificationModel();
        // Define default start and stop parameters for a default year to date report.
        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        cal.set(java.util.Calendar.DATE, 23); // default stop date is tomorrow
        cal.set(java.util.Calendar.MONTH, Calendar.SEPTEMBER);
        Date start = cal.getTime();

        cal.add(java.util.Calendar.DATE, 1);
        Date stop = cal.getTime(); // default start date is begining of year
        reportModel.setStartDate(start);
        reportModel.setStopDate(stop);
        reportModel.setCode("3914");
        YukonReportBase dbReport = new LoadControlVerificationReport(reportModel);
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
     * Create a Group for SystemLog.date column.
     * 
     * @return
     */
    private RelationalGroup createDateGroup() {
        final RelationalGroup dateGroup = new RelationalGroup();
        dateGroup.setName(LoadControlVerificationModel.DATE_STRING + ReportFactory.NAME_GROUP);
        dateGroup.addField(LoadControlVerificationModel.DATE_STRING);

        GroupHeader header = ReportFactory.createGroupHeaderDefault();

        TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(),
                LoadControlVerificationModel.DATE_COLUMN);
        header.addElement(tfactory.createElement());

        header.addElement(ReportFactory.createBasicLine(0.5f, 20));

        LabelElementFactory factory;
        // Add all columns (excluding Date) to the table model.
        for (int i = 1; i < LoadControlVerificationModel.COMMAND_COLUMN; i++) {
            factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
            factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 18));
            header.addElement(factory.createElement());
        }
        dateGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
//		dateGroup.setFooter(footer);

        return dateGroup;
    }

    /**
     * Create a GroupList and all Group(s) to it.
     * 
     * @return the groupList.
     */
    protected List<RelationalGroup> createGroups() {
        return List.of(createDateGroup());
    }

    /**
     * Creates the itemBand, the rows of data.
     * 
     * @return the item band.
     */
    protected ItemBand createItemBand() {
        ItemBand items = ReportFactory.createItemBandDefault();

        items.addElement(
                HorizontalLineElementFactory.createHorizontalLine(0, java.awt.Color.decode("#DFDFDF"), new BasicStroke(0.1f)));

        TextFieldElementFactory factory = null;

        for (int i = 1; i < getModel().getColumnNames().length; i++) {
            factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            if (i == LoadControlVerificationModel.COMMAND_COLUMN) {
                LabelElementFactory lFactory = ReportFactory.createLabelElementDefault(getModel(), i);
                lFactory.setText(lFactory.getText() + ": ");
                lFactory.setBold(Boolean.TRUE);
                lFactory.setVerticalAlignment(ElementAlignment.MIDDLE);
                items.addElement(lFactory.createElement());
                factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX() + 50,
                        getModel().getColumnProperties(i).getPositionY()));
            }
            items.addElement(factory.createElement());
        }

        return items;
    }
}
