package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
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
import com.cannontech.analysis.tablemodel.StarsLMDetailModel;

/**
 * Created on May 22, 2005
 * Creates a STARS LM Summary Report using com.cannontech.analysis.data.StarsLMSummaryModel tableModel
 * 
 * @author snebben
 */
public class StarsLMDetailReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf StarsLMDetailModel.
     */
    public StarsLMDetailReport() {
        this(new StarsLMDetailModel());
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf StarsLMDetailModel.
     * 
     * @param data_ - StarsLMDetailModel TableModel data
     */
    public StarsLMDetailReport(StarsLMDetailModel model_) {
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

        StarsLMDetailModel model = new StarsLMDetailModel();

        // start and stop time are only valid when model.showHist is false
        GregorianCalendar cal = new GregorianCalendar();
        model.setStopDate(cal.getTime());

        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        model.setStartDate(cal.getTime());
        model.setShowCapacity(true);
        model.setOrderBy(StarsLMDetailModel.ORDER_BY_LAST_NAME);
        YukonReportBase yukonReport = new StarsLMDetailReport(model);
        yukonReport.getModel().collectData();
        // Create the report
        MasterReport report = yukonReport.createReport();
        report.setDataFactory(new TableDataFactory("default", yukonReport.getModel()));

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
    private RelationalGroup createLMGroupGroup() {
        final RelationalGroup lmGroupGroup = new RelationalGroup();
        lmGroupGroup.setName(StarsLMDetailModel.GROUP_NAME_STRING + ReportFactory.NAME_GROUP);
        lmGroupGroup.addField(StarsLMDetailModel.GROUP_NAME_STRING);
        lmGroupGroup.addField(StarsLMDetailModel.GROUP_CAPACITY_STRING);

        GroupHeader header = ReportFactory.createGroupHeaderDefault();

        LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(),
                StarsLMDetailModel.GROUP_NAME_COLUMN);
        factory.setAbsolutePosition(new Point2D.Float(0, 1)); // override posX
        factory.setText("LM Group: ");
        header.addElement(factory.createElement());

        TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(),
                StarsLMDetailModel.GROUP_NAME_COLUMN);
        header.addElement(tfactory.createElement());

        factory = ReportFactory.createGroupLabelElementDefault(getModel(), StarsLMDetailModel.GROUP_CAPACITY_COLUMN);
        factory.setText("Group Capacity: ");
        factory.setAbsolutePosition(new Point2D.Float(0, 18)); // override posX
        header.addElement(factory.createElement());

        tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), StarsLMDetailModel.GROUP_CAPACITY_COLUMN);
        tfactory.setAbsolutePosition(
                new Point2D.Float(getModel().getColumnProperties(StarsLMDetailModel.GROUP_CAPACITY_COLUMN).getPositionX(), 18)); // override
                                                                                                                                 // location,
                                                                                                                                 // need
                                                                                                                                 // to
                                                                                                                                 // be
                                                                                                                                 // lower
                                                                                                                                 // than
                                                                                                                                 // macroroute
                                                                                                                                 // text
        header.addElement(tfactory.createElement());

        header.addElement(ReportFactory.createBasicLine(0.5f, 38));

        for (int i = StarsLMDetailModel.ACCOUNT_NUMBER_COLUMN; i < getModel().getColumnNames().length; i++) {
            boolean show = true;
            if (!((StarsLMDetailModel) getModel()).isShowCapacity() &&
                    (StarsLMDetailModel.GROUP_CAPACITY_COLUMN == i ||
                            StarsLMDetailModel.APPLIANCE_KW_CAPACITY_COLUMN == i ||
                            StarsLMDetailModel.APPLIANCE_TYPE_COLUMN == i))
                show = false;

            if (show) {
                factory = ReportFactory.createGroupLabelElementDefault(model, i);
                factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 36)); // override
                                                                                                                      // location,
                                                                                                                      // need to
                                                                                                                      // be lower
                                                                                                                      // than
                                                                                                                      // macroroute
                                                                                                                      // text
                header.addElement(factory.createElement());
            }
        }
        lmGroupGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        lmGroupGroup.setFooter(footer);

        return lmGroupGroup;
    }

    /**
     * Create a GroupList and all Group(s) to it.
     * 
     * @return the groupList.
     */
    protected List<RelationalGroup> createGroups() {
        return List.of(createLMGroupGroup());
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

        for (int i = StarsLMDetailModel.ACCOUNT_NUMBER_COLUMN; i < getModel().getColumnNames().length; i++) {
            boolean show = true;
            if (!((StarsLMDetailModel) getModel()).isShowCapacity() &&
                    (StarsLMDetailModel.GROUP_CAPACITY_COLUMN == i ||
                            StarsLMDetailModel.APPLIANCE_KW_CAPACITY_COLUMN == i ||
                            StarsLMDetailModel.APPLIANCE_TYPE_COLUMN == i))
                show = false;

            if (show) {

                TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
                items.addElement(factory.createElement());
            }
        }
        return items;
    }
}
