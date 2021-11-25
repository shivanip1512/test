package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.util.Calendar;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.RectangleElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.function.FunctionProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ItemHideFunction;
import org.pentaho.reporting.engine.classic.core.function.ItemSumFunction;
import org.pentaho.reporting.engine.classic.core.function.TotalGroupSumFunction;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.MeterOutageCountModel;

/**
 * Created on Feb 17, 2004
 * Creates a MissedMeterReport using the com.cannontech.analysis.data.PowerFailData tableModel
 * Groups data by Device.
 * 
 * @author bjonasson
 */
public class MeterOutageCountReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf PowerFailModel.
     * 
     * @param data_ - PowerFailModel TableModel data
     */
    public MeterOutageCountReport(MeterOutageCountModel model_) {
        super();
        setPageOrientation(PageFormat.PORTRAIT);
        setModel(model_);
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf PowerFailModel.
     * 
     * @param data_ - PowerFailModel TableModel data
     */
    public MeterOutageCountReport() {
        this(new MeterOutageCountModel());
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

        MeterOutageCountModel model = new MeterOutageCountModel();
//        model.setMinimumOutageCount(1);
        model.setPaoIDs(new int[] { 3023 });
        model.setIncompleteDataReport(true);

        // Get a default start date of 90 days previous.
        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        model.setStopDate(cal.getTime());

        cal.set(Calendar.MONTH, 4);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        model.setStartDate(cal.getTime());

        YukonReportBase powerFailReport = new MeterOutageCountReport(model);
        powerFailReport.getModel().collectData();
        // Create the report
        MasterReport report = powerFailReport.createReport();
        report.setDataFactory(new TableDataFactory("default", powerFailReport.getModel()));

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
     * Create a Group for DeviceName
     * 
     * @return Group
     */
    private RelationalGroup createDeviceGroup() {
        final RelationalGroup devGroup = new RelationalGroup();
        devGroup.setName(MeterOutageCountModel.DEVICE_NAME_STRING + ReportFactory.NAME_GROUP);
        if (!((MeterOutageCountModel) getModel()).isIncompleteDataReport()) {
            devGroup.addField(MeterOutageCountModel.DEVICE_NAME_STRING);
        }

        GroupHeader header = ReportFactory.createGroupHeaderDefault();
        header.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 2f);

        header.addElement(HorizontalLineElementFactory.createHorizontalLine(20, null, new BasicStroke(0.5f)));
        for (int i = MeterOutageCountModel.DEVICE_NAME_COLUMN; i < getModel().getColumnCount(); i++) {
            LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
            factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(),
                    getModel().getColumnProperties(i).getPositionY()));
            if (i >= MeterOutageCountModel.VALUE_COLUMN)
                factory.setHorizontalAlignment(ElementAlignment.RIGHT);
            header.addElement(factory.createElement());
        }

        devGroup.setHeader(header);

        if (!((MeterOutageCountModel) getModel()).isIncompleteDataReport()) {
            GroupFooter footer = ReportFactory.createGroupFooterDefault();
            footer.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 20f);

            LabelElementFactory lfactory = ReportFactory.createLabelElementDefault(getModel(),
                    MeterOutageCountModel.VALUE_COLUMN);
            lfactory.setText("Total:");
            lfactory.setHorizontalAlignment(ElementAlignment.RIGHT);
            footer.addElement(lfactory.createElement());

            TextFieldElementFactory tfactory = ReportFactory.createTextFieldElementDefault(getModel(),
                    MeterOutageCountModel.OUTAGE_COUNT_CALC_COLUMN);
            tfactory.setFieldname("outageCountTotal");
            tfactory.setHorizontalAlignment(ElementAlignment.RIGHT);
            footer.addElement(tfactory.createElement());
            devGroup.setFooter(footer);
        }

        return devGroup;
    }

    /**
     * Creates the expression collection. The xml definition for this construct:
     * 
     * @return the functions.
     * @throws FunctionInitializeException if there is a problem initialising the functions.
     */
    protected ExpressionCollection getExpressions() throws FunctionProcessingException {
        super.getExpressions();

        ItemHideFunction hideItem = new ItemHideFunction();
        hideItem.setName("hideDevice");
        hideItem.setField(MeterOutageCountModel.DEVICE_NAME_STRING);
        hideItem.setElement(MeterOutageCountModel.DEVICE_NAME_STRING + ReportFactory.NAME_ELEMENT);
        expressions.add(hideItem);

        hideItem = new ItemHideFunction();
        hideItem.setName("hidePoint");
        hideItem.setField(MeterOutageCountModel.POINT_NAME_STRING);
        hideItem.setElement(MeterOutageCountModel.POINT_NAME_STRING + ReportFactory.NAME_ELEMENT);
        expressions.add(hideItem);

        ItemSumFunction sumFunction = new ItemSumFunction();
        sumFunction.setName("outageCountTotal");
        sumFunction.setField(MeterOutageCountModel.OUTAGE_COUNT_CALC_STRING);
        sumFunction.setGroup(MeterOutageCountModel.DEVICE_NAME_STRING + ReportFactory.NAME_GROUP);
        expressions.add(sumFunction);

        TotalGroupSumFunction groupSumFunction = new TotalGroupSumFunction();
        groupSumFunction.setName("groupOutageCountTotal");
        groupSumFunction.setField(MeterOutageCountModel.OUTAGE_COUNT_CALC_STRING);
//        groupSumFunction.setGroup(PowerFailModel.DEVICE_NAME_STRING + ReportFactory.NAME_GROUP);
        expressions.add(groupSumFunction);

        return expressions;
    }

    /**
     * Create a GroupList and all Group(s) to it.
     * 
     * @return the groupList.
     */
    protected List<RelationalGroup> createGroups() {
        return List.of(createDeviceGroup());
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

        for (int i = MeterOutageCountModel.DEVICE_NAME_COLUMN; i <= MeterOutageCountModel.OUTAGE_COUNT_CALC_COLUMN; i++) {
            TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            if (i >= MeterOutageCountModel.VALUE_COLUMN)
                factory.setHorizontalAlignment(ElementAlignment.RIGHT);
            items.addElement(factory.createElement());
        }

        return items;
    }

    @Override
    protected ReportFooter createReportFooter() {
        ReportFooter reportFooter = super.createReportFooter();

        if (!((MeterOutageCountModel) getModel()).isIncompleteDataReport()) {
            LabelElementFactory lfactory = ReportFactory.createLabelElementDefault(getModel(),
                    MeterOutageCountModel.VALUE_COLUMN);
            lfactory.setText("TOTAL:");
            lfactory.setHorizontalAlignment(ElementAlignment.RIGHT);
            reportFooter.addElement(lfactory.createElement());

            TextFieldElementFactory tfactory = ReportFactory.createTextFieldElementDefault(getModel(),
                    MeterOutageCountModel.OUTAGE_COUNT_CALC_COLUMN);
            tfactory.setFieldname("groupOutageCountTotal");
            tfactory.setHorizontalAlignment(ElementAlignment.RIGHT);
            reportFooter.addElement(tfactory.createElement());
        }

        return reportFooter;
    }
}