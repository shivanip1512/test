package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
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
import com.cannontech.analysis.tablemodel.LoadGroupModel;

/**
 * Created on Dec 15, 2003
 * Creates a LGAccountingReport using the com.cannontech.analysis.data.loadgroup.LoadGroupReportData tableModel
 * Groups data by Load Group.
 * 
 * @author snebben
 */
public class LGAccountingReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf LoadGroupReportData.
     */
    public LGAccountingReport() {
        this(new LoadGroupModel());
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf LoadGroupReportData.
     * 
     * @param data_ - LoadGroupReportData TableModel data
     */
    public LGAccountingReport(LoadGroupModel model_) {
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
        cal.add(java.util.Calendar.DATE, 1);
        Date stop = cal.getTime();
        cal.add(java.util.Calendar.DATE, -30);
        Date start = cal.getTime();

        // Initialize the report data and populate the TableModel (collectData).
        LoadGroupModel model = new LoadGroupModel(start, stop);

        // Create the report
        YukonReportBase lgaReport = new LGAccountingReport(model);
        lgaReport.getModel().collectData();
        MasterReport report = lgaReport.createReport();
        report.setDataFactory(new TableDataFactory("default", lgaReport.getModel()));

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
     * Create a Group for Load Group column.
     * 
     * @return
     */
    private RelationalGroup createLoadGrpGroup() {
        final RelationalGroup collGrpGroup = new RelationalGroup();
        collGrpGroup.setName("Load Group");
        collGrpGroup.addField(LoadGroupModel.PAO_NAME_STRING);

        GroupHeader header = ReportFactory.createGroupHeaderDefault();

        LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), LoadGroupModel.PAO_NAME_COLUMN);
        factory.setText("Accounting Summary:"); // override default
        header.addElement(factory.createElement());

        TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(),
                LoadGroupModel.PAO_NAME_COLUMN);
        tfactory.setAbsolutePosition(new Point2D.Float(140, 1)); // override
        header.addElement(tfactory.createElement());
        header.addElement(HorizontalLineElementFactory.createHorizontalLine(20, null, new BasicStroke(0.5f)));

        for (int i = 1; i < getModel().getColumnCount(); i++) {
            if (!(i == LoadGroupModel.DAILY_CONTROL_COLUMN && !((LoadGroupModel) getModel()).isShowDailyTotal()) &&
                    !(i == LoadGroupModel.MONTHLY_CONTROL_COLUMN && !((LoadGroupModel) getModel()).isShowMonthlyTotal()) &&
                    !(i == LoadGroupModel.SEASONAL_CONTROL_COLUMN && !((LoadGroupModel) getModel()).isShowSeasonalTotal()) &&
                    !(i == LoadGroupModel.ANNUAL_CONTROL_COLUMN && !((LoadGroupModel) getModel()).isShowAnnualTotal())) {
                factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
                factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 30)); // lower
                                                                                                                      // this row
                                                                                                                      // of
                                                                                                                      // "headings"
                header.addElement(factory.createElement());
            }
        }
        collGrpGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        // Moved into the header and item band
//		factory = ReportFactory.createGroupLabelElementDefault(getModel(), LoadGroupModel.SEASONAL_CONTROL_COLUMN);
//		factory.setText(factory.getText() + ":");
//		footer.addElement(factory.createElement());
//
//		tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), LoadGroupModel.SEASONAL_CONTROL_COLUMN);
//		tfactory.setAbsolutePosition(new Point2D.Float(100, 1));	//override
//		footer.addElement(tfactory.createElement());

//		factory = ReportFactory.createGroupLabelElementDefault(getModel(), LoadGroupModel.ANNUAL_CONTROL_COLUMN);
//		factory.setText(factory.getText() + ":");
//		factory.setAbsolutePosition(new Point2D.Float(0, 12));	//override
//		footer.addElement(factory.createElement());
//
//		tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), LoadGroupModel.ANNUAL_CONTROL_COLUMN);
//		tfactory.setAbsolutePosition(new Point2D.Float(100, 12));	//override
//		footer.addElement(tfactory.createElement());

        collGrpGroup.setFooter(footer);

        return collGrpGroup;
    }

    /**
     * Create a GroupList and all Group(s) to it.
     * 
     * @return the groupList.
     */
    protected List<RelationalGroup> createGroups() {
        return List.of(createLoadGrpGroup());
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
        // Start at 1, we don't want to include the Load Group column, our group by column.
        for (int i = 1; i < getModel().getColumnCount(); i++) {
            if (!(i == LoadGroupModel.DAILY_CONTROL_COLUMN && !((LoadGroupModel) getModel()).isShowDailyTotal()) &&
                    !(i == LoadGroupModel.MONTHLY_CONTROL_COLUMN && !((LoadGroupModel) getModel()).isShowMonthlyTotal()) &&
                    !(i == LoadGroupModel.SEASONAL_CONTROL_COLUMN && !((LoadGroupModel) getModel()).isShowSeasonalTotal()) &&
                    !(i == LoadGroupModel.ANNUAL_CONTROL_COLUMN && !((LoadGroupModel) getModel()).isShowAnnualTotal())) {
                factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
                items.addElement(factory.createElement());
            }
        }
        return items;
    }
}
