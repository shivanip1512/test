package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.tablemodel.ActivityModel;
import com.cannontech.util.ServletUtil;

/**
 * Created on Dec 15, 2003
 * Creates a ECActivityLogReport using the com.cannontech.analysis.tablemodel.ActivityModel tableModel
 * Groups data by Date. Orders asc/desc based on tableModel definition.
 * 
 * @author snebben
 */
public class ECActivityLogReport extends YukonReportBase {
    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf SystemLogModel.
     */
    public ECActivityLogReport() {
        this(new ActivityModel());
    }

    /**
     * Constructor for Report.
     * Data Base for this report type is instanceOf SystemLogModel.
     * 
     * @param data_ - SystemLogModel TableModel data
     */
    public ECActivityLogReport(ActivityModel model_) {
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

        // Define default start and stop parameters for a default year to date report.
        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        cal.add(java.util.Calendar.DATE, 1); // default stop date is tomorrow
        Date stop = cal.getTime();

        cal.set(java.util.Calendar.DATE, 1);
        cal.set(java.util.Calendar.MONTH, 0);
        Date start = cal.getTime(); // default start date is begining of year

        ActivityModel model = new ActivityModel(start, stop);

        for (int i = 0; i < args.length; i++) {
            String arg = (String) args[i].toLowerCase();

            int startIndex = arg.indexOf('=');
            startIndex += 1;
            String subString = arg.substring(startIndex);

            if (arg.startsWith("ec"))
                model.setEnergyCompanyID(Integer.valueOf(subString));
            else if (arg.startsWith("start")) {
                Date startDate = ServletUtil.parseDateStringLiberally(subString);
                model.setStartDate(startDate);
            } else if (arg.startsWith("stop")) {
                Date stopDate = ServletUtil.parseDateStringLiberally(subString);
                model.setStartDate(stopDate);
            }
        }

        // Define start and stop parameters for a default 90 day report.
        YukonReportBase report = new ECActivityLogReport(model);
        report.getModel().collectData();

        // Create the report
        MasterReport freeReport = report.createReport();
        freeReport.setDataFactory(new TableDataFactory("default", report.getModel()));

        final PreviewDialog dialog = new PreviewDialog(freeReport);
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

    /*
     * (non-Javadoc)
     * 
     * @see com.cannontech.analysis.report.YukonReportBase#getExpressions()
     */
    protected ExpressionCollection getExpressions() throws FunctionProcessingException {
        super.getExpressions();

        ItemHideFunction hideItem = new ItemHideFunction();
        hideItem.setName(ActivityModel.CONTACT_STRING + ReportFactory.NAME_HIDDEN);
        hideItem.setField(ActivityModel.CONTACT_STRING);
        hideItem.setElement(ActivityModel.CONTACT_STRING + ReportFactory.NAME_ELEMENT);
        expressions.add(hideItem);

        hideItem = new ItemHideFunction();
        hideItem.setName(ActivityModel.ACCOUNT_NUMBER_STRING + ReportFactory.NAME_HIDDEN);
        hideItem.setField(ActivityModel.ACCOUNT_NUMBER_STRING);
        hideItem.setElement(ActivityModel.ACCOUNT_NUMBER_STRING + ReportFactory.NAME_ELEMENT);
        expressions.add(hideItem);

        hideItem = new ItemHideFunction();
        hideItem.setName(ActivityModel.USERNAME_STRING + ReportFactory.NAME_HIDDEN);
        hideItem.setField(ActivityModel.USERNAME_STRING);
        hideItem.setElement(ActivityModel.USERNAME_STRING + ReportFactory.NAME_ELEMENT);
        expressions.add(hideItem);

        return expressions;
    }

    /**
     * Create a Group for EnergyCompany column.
     * 
     * @return
     */
    private RelationalGroup createECGroup() {
        final RelationalGroup ecGroup = new RelationalGroup();
        ecGroup.setName(ActivityModel.ENERGY_COMPANY_STRING + ReportFactory.NAME_GROUP);
        ecGroup.addField(getModel().getColumnName(ActivityModel.ENERGY_COMPANY_COLUMN));

        GroupHeader header = ReportFactory.createGroupHeaderDefault();

        TextFieldElementFactory tfactory = ReportFactory.createGroupTextFieldElementDefault(getModel(),
                ActivityModel.ENERGY_COMPANY_COLUMN);
        header.addElement(tfactory.createElement());

        LabelElementFactory lFactory = null;
        // Add all columns (excluding Date) to the table model.
        for (int i = 1; i < getModel().getColumnNames().length; i++) {
            lFactory = ReportFactory.createGroupLabelElementDefault(getModel(), i);
            lFactory.setAbsolutePosition(new Point2D.Float(getModel().getColumnProperties(i).getPositionX(), 18)); // override the
                                                                                                                   // position
            header.addElement(lFactory.createElement());
        }
        header.addElement(ReportFactory.createBasicLine(0.5f, 36)); // 36 position, group pos(18) + 18 from labelElements
        ecGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
//		ecGroup.setFooter(footer);

        return ecGroup;
    }

    /**
     * Create a Group for Contact column.
     * 
     * @return
     */
    private RelationalGroup createContactGroup() {
        final RelationalGroup contGroup = new RelationalGroup();
        contGroup.setName(ActivityModel.CONTACT_STRING + ReportFactory.NAME_GROUP);
        contGroup.addField(getModel().getColumnName(ActivityModel.ENERGY_COMPANY_COLUMN));
        contGroup.addField(getModel().getColumnName(ActivityModel.CONTACT_COLUMN));

        GroupHeader header = ReportFactory.createGroupHeaderDefault();
        header.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 5f); // override the size
        contGroup.setHeader(header);

        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        footer.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 5f); // override the size
        contGroup.setFooter(footer);

        return contGroup;
    }

    /**
     * Create a GroupList and all Group(s) to it.
     * 
     * @return the groupList.
     */

    protected List<RelationalGroup> createGroups() {
        return List.of(createECGroup(), createContactGroup());
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
        // Start at 1, we don't want to include the Date column, Date is our group by column.
        for (int i = 1; i < getModel().getColumnNames().length; i++) {
            TextFieldElementFactory factory = ReportFactory.createTextFieldElementDefault(getModel(), i);
            if (i == ActivityModel.ACTION_COUNT_COLUMN)
                factory.setHorizontalAlignment(ElementAlignment.CENTER);
            else
                factory.setHorizontalAlignment(ElementAlignment.LEFT);
            items.addElement(factory.createElement());
        }

        return items;
    }

    /**
     * Creates the report footer.
     * 
     * @return the report footer.
     */
    protected ReportFooter createReportFooter() {
        ReportFooter footer = ReportFactory.createReportFooterDefault();

        LabelElementFactory factory = ReportFactory.createGroupLabelElementDefault(ActivityModel.TOTALS_HEADER_STRING, 0, 1, 100);
        footer.addElement(factory.createElement());

        factory = ReportFactory.createGroupLabelElementDefault(getModel(), ActivityModel.ACTION_COLUMN);
        footer.addElement(factory.createElement());

        factory = ReportFactory.createGroupLabelElementDefault(getModel(), ActivityModel.ACTION_COUNT_COLUMN);
        footer.addElement(factory.createElement());

        Iterator iter = ((ActivityModel) getModel()).getTotals().entrySet().iterator();
        int offset = 8;

        while (iter.hasNext()) {
            offset += 10;
            Map.Entry entry = ((Map.Entry) iter.next());

            factory = ReportFactory.createGroupLabelElementDefault(entry.getKey().toString(),
                    0, offset, 480);
            factory.setHorizontalAlignment(ElementAlignment.RIGHT);
            footer.addElement(factory.createElement());

            factory = ReportFactory.createGroupLabelElementDefault(entry.getValue().toString(),
                    getModel().getColumnProperties(ActivityModel.ACTION_COUNT_COLUMN).getPositionX(),
                    offset, getModel().getColumnProperties(ActivityModel.ACTION_COUNT_COLUMN).getWidth());
            factory.setHorizontalAlignment(ElementAlignment.CENTER);
            footer.addElement(factory.createElement());
        }
        offset += 20;

        footer.addElement(ReportFactory.createBasicLine(0.5f, 20));

        factory = new LabelElementFactory();
        factory.setAbsolutePosition(new Point2D.Float(0, offset));
        factory.setMinimumSize(new FloatDimension(-100, 16));
        factory.setHorizontalAlignment(ElementAlignment.CENTER);
        factory.setVerticalAlignment(ElementAlignment.MIDDLE);
        factory.setText("*** END OF REPORT ***");
        footer.addElement(factory.createElement());
        return footer;

    }
}