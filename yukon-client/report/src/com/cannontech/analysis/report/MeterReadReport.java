package com.cannontech.analysis.report;

import java.awt.BasicStroke;
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
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.function.FunctionProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ItemHideFunction;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.tablemodel.MeterReadModel;

/**
 * Created on Dec 15, 2003
 * Creates a MissedMeterReport using the com.cannontech.analysis.data.MissedMeterData tableModel
 * Groups data by Collection Group and then by Device.
 * 
 * @author snebben
 */
public class MeterReadReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf MissedMeterModel.
     * 
     * @param data_ - PowerFailModel TableModel data
     */
    public MeterReadReport(MeterReadModel model_) {
        super();
        setModel(model_);
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf MissedMeterModel.
     * 
     * @param data_ - MissedMeterModel TableModel data
     */
    public MeterReadReport() {
        this(new MeterReadModel());
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

        // Get a default start date of 90 days previous.
        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        cal.add(java.util.Calendar.DATE, 1);
        Date stop = cal.getTime();
        cal.add(java.util.Calendar.DATE, -390);
        Date start = cal.getTime();

        MeterReadModel model = new MeterReadModel(start);
        model.setFilterModelType(ReportFilter.GROUPS);
        model.setMeterReadType(model.SUCCESS_METER_READ_TYPE);
        YukonReportBase meterReadReport = new MeterReadReport(model);
        meterReadReport.getModel().collectData();

        // Create the report
        MasterReport report = meterReadReport.createReport();
        report.setDataFactory(new TableDataFactory("default", meterReadReport.getModel()));

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
     * Create a Group for Device
     * 
     * @return Group
     */
    private RelationalGroup createDeviceGroup() {
        final RelationalGroup devGrpGroup = new RelationalGroup();
        devGrpGroup.setName("Column Heading");

        GroupHeader header = ReportFactory.createGroupHeaderDefault();

        LabelElementFactory factory;
        for (int i = 0; i < getModel().getColumnCount(); i++) {
            factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
            header.addElement(factory.createElement());
        }

        header.addElement(ReportFactory.createBasicLine(0.5f, 20));
        devGrpGroup.setHeader(header);

        final GroupFooter footer = new GroupFooter();
        footer.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 12f);
        footer.getStyle().getStyleProperty(TextStyleKeys.FONT, "Serif");
        footer.getStyle().getIntStyleProperty(TextStyleKeys.FONTSIZE, 12);

        footer.addElement(ReportFactory.createBasicLine(0.5f, 4));
//		devGrpGroup.setFooter(footer);

        return devGrpGroup;
    }

    /**
     * Creates the function collection. The xml definition for this construct:
     * 
     * @return the functions.
     * @throws FunctionInitializeException if there is a problem initialising the functions.
     */
    protected ExpressionCollection getExpressions() throws FunctionProcessingException {
        super.getExpressions();

        ItemHideFunction hideItem = new ItemHideFunction();
        hideItem.setName("hideItem");
        hideItem.setField(MeterReadModel.DEVICE_NAME_STRING);
        hideItem.setElement(MeterReadModel.DEVICE_NAME_STRING + ReportFactory.NAME_GROUP);
        expressions.add(hideItem);

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

        TextFieldElementFactory factory;
        for (int i = MeterReadModel.DEVICE_NAME_COLUMN; i < getModel().getColumnCount(); i++) {
            factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            items.addElement(factory.createElement());
        }

        return items;
    }

}
