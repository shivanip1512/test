package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.function.FunctionProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ItemAvgFunction;
import org.pentaho.reporting.engine.classic.core.function.ItemCountFunction;
import org.pentaho.reporting.engine.classic.core.function.ItemMaxFunction;
import org.pentaho.reporting.engine.classic.core.function.ItemMinFunction;
import org.pentaho.reporting.engine.classic.core.function.ItemSumFunction;
import org.pentaho.reporting.engine.classic.core.function.TotalGroupSumFunction;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.pentahoreport.ItemColumnQuotientExpression;
import com.cannontech.analysis.pentahoreport.ItemMaxValueFunction;
import com.cannontech.analysis.pentahoreport.ItemMinValueFunction;
import com.cannontech.analysis.pentahoreport.ItemValueComparatorFunction;
import com.cannontech.analysis.tablemodel.PointDataSummaryModel;

/**
 * Created on Dec 15, 2003
 * @author snebben
 */
public class PointDataSummaryReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf LPDataSummaryModel.
     */
    public PointDataSummaryReport() {
        this(new PointDataSummaryModel());
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf PointDataSummaryModel.
     * 
     * @param data_ - DatabaseModel TableModel data
     */
    public PointDataSummaryReport(PointDataSummaryModel model_) {
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

        // Define start and stop parameters for a default 90 day report.
        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, -100);
        Date stop = cal.getTime();
        cal.add(Calendar.DATE, -30);
        Date start = cal.getTime();

        PointDataSummaryModel reportModel = new PointDataSummaryModel(start, stop, PointDataSummaryModel.LOAD_PROFILE_POINT_TYPE);
        reportModel.setShowDetails(true);
        YukonReportBase dbReport = new PointDataSummaryReport(reportModel);
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
    private RelationalGroup createDeviceGroup() {
        final RelationalGroup devGroup = new RelationalGroup();
        devGroup.setName(PointDataSummaryModel.PAO_NAME_STRING + ReportFactory.NAME_GROUP);
        devGroup.addField(PointDataSummaryModel.PAO_NAME_STRING);

        GroupHeader header = ReportFactory.createGroupHeaderDefault();
        header.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 18f);

        for (int i = PointDataSummaryModel.PAO_NAME_COLUMN; i <= PointDataSummaryModel.PAO_TYPE_COLUMN; i++) {
            LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
            factory.setText(factory.getText() + ":");
            header.addElement(factory.createElement());

            TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), i);
            tfactory.setBold(Boolean.FALSE);
            header.addElement(tfactory.createElement());
        }
        header.addElement(ReportFactory.createBasicLine(0.5f, 20));
        devGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        devGroup.setFooter(footer);

        return devGroup;
    }

    /**
     * Create a Group for Device
     * 
     * @return Group
     */
    private RelationalGroup createPointGroup() {
        final RelationalGroup devGrpGroup = new RelationalGroup();
        devGrpGroup.setName(PointDataSummaryModel.POINT_NAME_STRING + ReportFactory.NAME_GROUP);
        devGrpGroup.addField(PointDataSummaryModel.PAO_NAME_STRING);
        devGrpGroup.addField(PointDataSummaryModel.POINT_NAME_STRING);

        GroupHeader header = ReportFactory.createGroupHeaderDefault();
        header.setRepeat(true);

        LabelElementFactory factory;
        TextFieldElementFactory tfactory;

        int index = PointDataSummaryModel.POINT_NAME_COLUMN;
        factory = ReportFactory.createGroupLabelElementDefault(getModel(), index);
        factory.setText(factory.getText() + ":");
        header.addElement(factory.createElement());

        tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), index);
        tfactory.setBold(Boolean.FALSE);
        header.addElement(tfactory.createElement());

        if (((PointDataSummaryModel) getModel()).getPointType() == PointDataSummaryModel.LOAD_PROFILE_POINT_TYPE ||
                ((PointDataSummaryModel) getModel()).getPointType() == PointDataSummaryModel.DEMAND_ACC_POINT_TYPE) {
            index = PointDataSummaryModel.CHANNEL_NUMBER_COLUMN;
            factory = ReportFactory.createGroupLabelElementDefault(getModel(), index);
            factory.setText(factory.getText() + ":");
            header.addElement(factory.createElement());

            tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), index);
            tfactory.setBold(Boolean.FALSE);
            header.addElement(tfactory.createElement());

            index = PointDataSummaryModel.CHANNEL_INTERVAL_COLUMN;
            factory = ReportFactory.createGroupLabelElementDefault(getModel(), index);
            factory.setText(factory.getText() + ":");
            header.addElement(factory.createElement());

            tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), index);
            tfactory.setBold(Boolean.FALSE);
            header.addElement(tfactory.createElement());

            index = PointDataSummaryModel.MISSING_DATA_FLAG_COLUMN;
            factory = ReportFactory.createGroupLabelElementDefault(getModel(), index);
            factory.setText(factory.getText() + ":");
            header.addElement(factory.createElement());
            tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(), index);
            tfactory.setBold(Boolean.FALSE);
            header.addElement(tfactory.createElement());
        }

        if (((PointDataSummaryModel) getModel()).isShowDetails()) {
            for (int i = PointDataSummaryModel.START_DATE_COLUMN; i <= PointDataSummaryModel.DAILY_LOW_COLUMN; i++) {
                factory = ReportFactory.createGroupLabelElementDefault(model, i);
                factory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 18));
                header.addElement(factory.createElement());
            }
            if (((PointDataSummaryModel) getModel()).getPointType() == PointDataSummaryModel.LOAD_PROFILE_POINT_TYPE ||
                    ((PointDataSummaryModel) getModel()).getPointType() == PointDataSummaryModel.DEMAND_ACC_POINT_TYPE) {
                factory = ReportFactory.createGroupLabelElementDefault(model, PointDataSummaryModel.TOTAL_KWH_COLUMN);
                factory.setAbsolutePosition(new Point2D.Float(
                        getModel().getColumnProperties(PointDataSummaryModel.TOTAL_KWH_COLUMN).getPositionX(), 18));
                header.addElement(factory.createElement());
            }

        }

        header.addElement(ReportFactory.createBasicLine(0.5f, 20));
        devGrpGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();

        if (((PointDataSummaryModel) getModel()).isShowDetails() &&
                (((PointDataSummaryModel) getModel()).getPointType() == PointDataSummaryModel.LOAD_PROFILE_POINT_TYPE ||
                        ((PointDataSummaryModel) getModel()).getPointType() == PointDataSummaryModel.DEMAND_ACC_POINT_TYPE)) {
            footer.addElement(HorizontalLineElementFactory.createHorizontalLine(100, null, new BasicStroke(0.3f)));
            tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.POINT_TOTAL_KWH_COLUMN);
            tfactory.setFieldname(PointDataSummaryModel.POINT_TOTAL_KWH_STRING + ReportFactory.NAME_ELEMENT);
            footer.addElement(tfactory.createElement());
        }

        factory = ReportFactory.createGroupLabelElementDefault(getModel(), PointDataSummaryModel.PEAKS_TITLE_COLUMN);
        factory.setAbsolutePosition(
                new Point2D.Float(getModel().getColumnProperties(PointDataSummaryModel.PEAKS_TITLE_COLUMN).getPositionX(), 12));
        footer.addElement(factory.createElement());

        factory = ReportFactory.createGroupLabelElementDefault(getModel(), PointDataSummaryModel.LOWS_TITLE_COLUMN);
        factory.setAbsolutePosition(
                new Point2D.Float(getModel().getColumnProperties(PointDataSummaryModel.LOWS_TITLE_COLUMN).getPositionX(), 12));
        footer.addElement(factory.createElement());

//		footer.addElement(StaticShapeElementFactory.createLineShapeElement("peakSumLine", null, new BasicStroke(0.3f), new Line2D.Float(50, 30, 225, 30)));
//		footer.addElement(StaticShapeElementFactory.createLineShapeElement("lowSumLine", null, new BasicStroke(0.3f), new Line2D.Float(325, 30, 500, 30)));

        for (int i = PointDataSummaryModel.PEAK_TIMESTAMP_0_COLUMN; i <= PointDataSummaryModel.LOW_VALUE_4_COLUMN; i++) {
            tfactory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            tfactory.setBold(Boolean.FALSE);
            footer.addElement(tfactory.createElement());
        }

        devGrpGroup.setFooter(footer);

        return devGrpGroup;
    }

    private RelationalGroup createStartDateGroup() {
        if (((PointDataSummaryModel) getModel()).isShowDetails()) {
            final RelationalGroup startDateGroup = new RelationalGroup();
            startDateGroup.setName(PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
            startDateGroup.addField(PointDataSummaryModel.PAO_NAME_STRING);
            startDateGroup.addField(PointDataSummaryModel.POINT_NAME_STRING);
            startDateGroup.addField(PointDataSummaryModel.START_DATE_STRING);
            GroupHeader header = ReportFactory.createGroupHeaderDefault();
            GroupFooter footer = ReportFactory.createGroupFooterDefault();
            footer.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 10f);
            footer.getStyle().getStyleProperty(TextStyleKeys.FONT, ReportFactory.DEFAULT_FONT);
            footer.getStyle().getIntStyleProperty(TextStyleKeys.FONTSIZE, 10);

            TextFieldElementFactory tfactory = ReportFactory.createTextFieldElementDefault(getModel(),
                    PointDataSummaryModel.START_DATE_COLUMN);
            footer.addElement(tfactory.createElement());

            tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.DAILY_HIGH_TIME_COLUMN);
            tfactory.setFieldname(PointDataSummaryModel.DAILY_HIGH_TIME_STRING + ReportFactory.NAME_ELEMENT);
            footer.addElement(tfactory.createElement());

            tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.DAILY_HIGH_COLUMN);
            tfactory.setFieldname(PointDataSummaryModel.DAILY_HIGH_STRING + ReportFactory.NAME_ELEMENT);
            footer.addElement(tfactory.createElement());

            tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.DAILY_AVERAGE_COLUMN);
            tfactory.setFieldname(PointDataSummaryModel.DAILY_AVERAGE_STRING + ReportFactory.NAME_ELEMENT);
            footer.addElement(tfactory.createElement());

            tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.DAILY_LOW_TIME_COLUMN);
            tfactory.setFieldname(PointDataSummaryModel.DAILY_LOW_TIME_STRING + ReportFactory.NAME_ELEMENT);
            footer.addElement(tfactory.createElement());

            tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.DAILY_LOW_COLUMN);
            tfactory.setFieldname(PointDataSummaryModel.DAILY_LOW_STRING + ReportFactory.NAME_ELEMENT);
            footer.addElement(tfactory.createElement());

            if (((PointDataSummaryModel) getModel()).getPointType() == PointDataSummaryModel.LOAD_PROFILE_POINT_TYPE ||
                    ((PointDataSummaryModel) getModel()).getPointType() == PointDataSummaryModel.DEMAND_ACC_POINT_TYPE) {
                tfactory = ReportFactory.createTextFieldElementDefault(getModel(), PointDataSummaryModel.TOTAL_KWH_COLUMN);
                tfactory.setFieldname(PointDataSummaryModel.TOTAL_KWH_STRING + ReportFactory.NAME_ELEMENT);
                footer.addElement(tfactory.createElement());

                tfactory = ReportFactory.createTextFieldElementDefault(getModel(),
                        PointDataSummaryModel.DAILY_MISSING_DATA_FLAG_COLUMN);
                tfactory.setFieldname(PointDataSummaryModel.DAILY_MISSING_DATA_FLAG_COLUMN + ReportFactory.NAME_ELEMENT);
                tfactory.setNullString("*");
                footer.addElement(tfactory.createElement());
            }

            startDateGroup.setFooter(footer);

            return startDateGroup;
        }
        return null;
    }

    /**
     * Create a GroupList and all Group(s) to it.
     * 
     * @return the groupList.
     */

    protected List<RelationalGroup> createGroups() {
        List<RelationalGroup> groups = new ArrayList<>();

        groups.add(createDeviceGroup());
        groups.add(createPointGroup());
        RelationalGroup g = createStartDateGroup();
        if (g != null) {
            groups.add(g);
        }

        return groups;
    }

    /**
     * Creates the itemBand, the rows of data.
     * 
     * @return the item band.
     */
    protected ItemBand createItemBand() {
        // No item band, only summary data!
        ItemBand items = ReportFactory.createItemBandDefault();
        items.setVisible(false);
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

        // No need to define them if we aren't using them!
        if (((PointDataSummaryModel) getModel()).isShowDetails()) {
            ItemCountFunction countItem = new ItemCountFunction();
            countItem.setName(PointDataSummaryModel.DAILY_COUNT_STRING + "Temp");
            countItem.setGroup(PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
            expressions.add(countItem);

            ItemMaxFunction maxItem = new ItemMaxFunction();
            maxItem.setName(PointDataSummaryModel.DAILY_HIGH_STRING + ReportFactory.NAME_ELEMENT);
            maxItem.setGroup(PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
            maxItem.setField(PointDataSummaryModel.VALUE_STRING);
            expressions.add(maxItem);

            ItemMaxValueFunction maxValueItem = new ItemMaxValueFunction();
            maxValueItem.setName(PointDataSummaryModel.DAILY_HIGH_TIME_STRING + ReportFactory.NAME_ELEMENT);
            maxValueItem.setDataField(PointDataSummaryModel.TIME_STRING);
            maxValueItem.setGroup(PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
            maxValueItem.setField(PointDataSummaryModel.VALUE_STRING);
            expressions.add(maxValueItem);

            ItemMinFunction minItem = new ItemMinFunction();
            minItem.setName(PointDataSummaryModel.DAILY_LOW_STRING + ReportFactory.NAME_ELEMENT);
            minItem.setGroup(PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
            minItem.setField(PointDataSummaryModel.VALUE_STRING);
            expressions.add(minItem);

            ItemMinValueFunction minValueItem = new ItemMinValueFunction();
            minValueItem.setName(PointDataSummaryModel.DAILY_LOW_TIME_STRING + ReportFactory.NAME_ELEMENT);
            minValueItem.setDataField(PointDataSummaryModel.TIME_STRING);
            minValueItem.setGroup(PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
            minValueItem.setField(PointDataSummaryModel.VALUE_STRING);
            expressions.add(minValueItem);

            ItemAvgFunction avgItem = new ItemAvgFunction();
            avgItem.setName(PointDataSummaryModel.DAILY_AVERAGE_STRING + ReportFactory.NAME_ELEMENT);
            avgItem.setGroup(PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
            avgItem.setField(PointDataSummaryModel.VALUE_STRING);
            expressions.add(avgItem);

            ItemSumFunction sumItem = new ItemSumFunction();
            sumItem.setName(PointDataSummaryModel.TOTAL_KWH_STRING + "Temp");
            sumItem.setGroup(PointDataSummaryModel.START_DATE_STRING + ReportFactory.NAME_GROUP);
            sumItem.setField(PointDataSummaryModel.VALUE_STRING);
            expressions.add(sumItem);

            TotalGroupSumFunction groupSumItem = new TotalGroupSumFunction();
            groupSumItem.setName(PointDataSummaryModel.POINT_TOTAL_KWH_STRING + "Temp");
            groupSumItem.setGroup(PointDataSummaryModel.POINT_NAME_STRING + ReportFactory.NAME_GROUP);
            groupSumItem.setField(PointDataSummaryModel.VALUE_STRING);
            expressions.add(groupSumItem);

            ItemColumnQuotientExpression quotientItem = new ItemColumnQuotientExpression();
            quotientItem.setName(PointDataSummaryModel.TOTAL_KWH_STRING + ReportFactory.NAME_ELEMENT);
            quotientItem.setDividend(PointDataSummaryModel.TOTAL_KWH_STRING + "Temp");
            quotientItem.setDivisor(PointDataSummaryModel.INTERVALS_PER_HOUR_STRING);
            expressions.add(quotientItem);

            quotientItem = new ItemColumnQuotientExpression();
            quotientItem.setName(PointDataSummaryModel.POINT_TOTAL_KWH_STRING + ReportFactory.NAME_ELEMENT);
            quotientItem.setDividend(PointDataSummaryModel.POINT_TOTAL_KWH_STRING + "Temp");
            quotientItem.setDivisor(PointDataSummaryModel.INTERVALS_PER_HOUR_STRING);
            expressions.add(quotientItem);

            ItemValueComparatorFunction compItem = new ItemValueComparatorFunction();
            compItem.setName(PointDataSummaryModel.DAILY_MISSING_DATA_FLAG_COLUMN + ReportFactory.NAME_ELEMENT);
            compItem.setXValue(PointDataSummaryModel.INTERVALS_PER_DAY_STRING);
            compItem.setYValue(PointDataSummaryModel.DAILY_COUNT_STRING + "Temp");
            expressions.add(compItem);
        }
        return expressions;
    }

    /**
     * Creates a page footer.
     * 
     * @return The page footer.
     */
    protected PageFooter createPageFooter() {
        final PageFooter pageFooter = super.createPageFooter();

        if (((PointDataSummaryModel) getModel()).getPointType() == PointDataSummaryModel.LOAD_PROFILE_POINT_TYPE ||
                ((PointDataSummaryModel) getModel()).getPointType() == PointDataSummaryModel.DEMAND_ACC_POINT_TYPE) {
            final LabelElementFactory factory = new LabelElementFactory();
            factory.setAbsolutePosition(new Point2D.Float(0, 8));
            factory.setMinimumSize(new FloatDimension(-100, 0));
            factory.setHorizontalAlignment(ElementAlignment.LEFT);
            factory.setVerticalAlignment(ElementAlignment.BOTTOM);
            factory.setText(" * Missing Interval Data");
            factory.setDynamicHeight(Boolean.TRUE);
            pageFooter.addElement(factory.createElement());
        }
        return pageFooter;
    }
}